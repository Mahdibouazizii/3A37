<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Commande;
use App\Entity\CommandeProduit;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

use Stripe\Stripe;
use Stripe\Checkout\Session as StripeSession;
use App\Entity\Produit;
use App\Entity\Feedback;
use App\Form\ProduitType;
use App\Form\FeedbackType;
use App\Controller\FeedbackRepository;
use App\Repository\ProduitRepository;

use Stripe\Checkout\Session;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

use Symfony\Bundle\SecurityBundle\Attribute\IsGranted;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Security\Csrf\CsrfTokenManagerInterface;
use Symfony\Component\Security\Csrf\CsrfToken;



use App\Form\CommandeType;

class CommandeController extends AbstractController
{

    #[Route('/admin/commande/edit/{id}', name: 'admin_commande_edit')]
public function editCommande(Request $request, EntityManagerInterface $entityManager, int $id): Response
{
    $commande = $entityManager->getRepository(Commande::class)->find($id);

    if (!$commande) {
        throw $this->createNotFoundException('Commande non trouvée');
    }

    // Create a form to edit the status
    $form = $this->createFormBuilder($commande)
        ->add('status', ChoiceType::class, [
            'choices' => [
                'Pending' => 'Pending',
                'Validated' => 'Validated',
                'Shipped' => 'Shipped',
                'Delivered' => 'Delivered',
                'Canceled' => 'Canceled'
            ],
            'label' => 'Statut de la commande',
            'attr' => ['class' => 'form-control'],
        ])
        ->getForm();

    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager->flush();
        $this->addFlash('success', 'Commande mise à jour avec succès.');

        return $this->redirectToRoute('admin_commande_details', ['id' => $commande->getId()]);
    }

    return $this->render('admin/commandes/edit.html.twig', [
        'commande' => $commande,
        'form' => $form->createView(),
    ]);
}


    #[Route('/mes-commandes', name: 'user_orders')]
public function userOrders(EntityManagerInterface $entityManager): Response
{
    $user = $this->getUser();

    if (!$user) {
        $this->addFlash('error', 'Vous devez être connecté pour voir vos commandes.');
        return $this->redirectToRoute('app_login');
    }

$commandes = $entityManager->getRepository(Commande::class)->findBy(['user' => $user], ['createdAt' => 'DESC']);

    return $this->render('commande/user_orders.html.twig', [
        'commandes' => $commandes,
    ]);
}



#[Route('/place-order', name: 'place_order', methods: ['GET', 'POST'])]
public function placeOrder(Request $request, SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    $cart = $session->get('cart', []);
    $stockWarnings = [];

    if (empty($cart)) {
        $this->addFlash('warning', 'Votre panier est vide.');
        return $this->redirectToRoute('cart');
    }

    $commande = new Commande();
    $commande->setUser($this->getUser());

    $form = $this->createForm(CommandeType::class, $commande, [
        'allow_extra_fields' => true,
    ]);
    $form->handleRequest($request);

    // ✅ Check stock before processing the order
    foreach ($cart as $item) {
        $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

        if (!$produit) {
            continue;
        }

        if ($produit->getStock() < $item['quantity']) {
            $stockWarnings[] = [
                'name' => $produit->getNom(),
                'available_stock' => $produit->getStock(),
                'requested_quantity' => $item['quantity']
            ];
        }
    }

    // ✅ If stockWarnings is not empty, show warning and stop order
    if (!empty($stockWarnings)) {
        return $this->render('commande/place_order.html.twig', [
            'form' => $form->createView(),
            'cart' => $cart,
            'stockWarnings' => $stockWarnings, // Pass warnings to the template
        ]);
    }

    if ($form->isSubmitted() && $form->isValid()) {
        $adresse = $commande->getAdresse();
        $session->set('adresse', $adresse);

        $typePaiement = $request->request->get('commande')['typePaiement'] ?? null;
        if (!$typePaiement) {
            $this->addFlash('error', 'Type de paiement non défini.');
            return $this->redirectToRoute('cart');
        }
        $commande->setTypePaiement($typePaiement);

        foreach ($cart as $item) {
            $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

            $produit->setStock($produit->getStock() - $item['quantity']);

            $commandeProduit = new CommandeProduit();
            $commandeProduit->setCommande($commande);
            $commandeProduit->setProduit($produit);
            $commandeProduit->setQuantity($item['quantity']);

            $entityManager->persist($commandeProduit);
        }

        $entityManager->persist($commande);
        $entityManager->flush();

        $session->remove('cart');
        $this->addFlash('success', 'Commande passée avec succès.');
        return $this->redirectToRoute('produit_index');
    }

    return $this->render('commande/place_order.html.twig', [
        'form' => $form->createView(),
        'cart' => $cart,
        'stockWarnings' => $stockWarnings, // Ensure warnings are passed even when form is not submitted
    ]);
}









#[Route('/create-checkout-session', name: 'create_checkout_session', methods: ['POST'])]
public function createCheckoutSession(Request $request, SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    $cart = $session->get('cart', []);

    if (empty($cart)) {
        return $this->json(['error' => 'Votre panier est vide.'], 400);
    }

    $data = json_decode($request->getContent(), true);

    if (!isset($data['adresse']) || empty(trim($data['adresse']))) {
        return $this->json(['error' => 'Adresse non fournie.'], 400);
    }

    // ✅ Store address in session to ensure it persists
    $session->set('adresse', $data['adresse']);

    Stripe::setApiKey($_ENV['STRIPE_SECRET_KEY']);

    $lineItems = [];
    foreach ($cart as $item) {
        $lineItems[] = [
            'price_data' => [
                'currency' => 'eur',
                'product_data' => ['name' => $item['name']],
                'unit_amount' => $item['price'] * 100,
            ],
            'quantity' => $item['quantity'],
        ];
    }

    $checkoutSession = StripeSession::create([
        'payment_method_types' => ['card'],
        'line_items' => $lineItems,
        'mode' => 'payment',
        'metadata' => [
            'adresse' => $data['adresse'],
            'id_user' => $this->getUser()->getId(),
            'cart' => json_encode($cart),
        ],
        'success_url' => $this->generateUrl('payment_success', [], UrlGeneratorInterface::ABSOLUTE_URL) . "?session_id={CHECKOUT_SESSION_ID}",
        'cancel_url' => $this->generateUrl('cart', [], UrlGeneratorInterface::ABSOLUTE_URL),
    ]);

    return $this->json(['checkoutUrl' => $checkoutSession->url]);
}









#[Route('/payment-success', name: 'payment_success')]
public function paymentSuccess(Request $request, SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    Stripe::setApiKey($_ENV['STRIPE_SECRET_KEY']);

    // ✅ Get session ID from URL
    $sessionId = $request->query->get('session_id');
    
    if (!$sessionId) {
        $this->addFlash('error', 'Erreur de paiement : Session ID manquant.');
        return $this->redirectToRoute('cart');
    }

    // ✅ Retrieve the Stripe session
    try {
        $stripeSession = StripeSession::retrieve($sessionId);
    } catch (\Exception $e) {
        $this->addFlash('error', 'Erreur lors de la récupération de la session Stripe.');
        return $this->redirectToRoute('cart');
    }

    // ✅ Extract metadata
    $metadata = $stripeSession->metadata;
    $cart = json_decode($metadata->cart, true);
    $adresse = $metadata->adresse ?? $session->get('adresse');

    if (empty($cart)) {
        $this->addFlash('warning', 'Votre panier est vide.');
        return $this->redirectToRoute('cart');
    }

    // ✅ Create the order
    $commande = new Commande();
    $commande->setUser($this->getUser());
    $commande->setAdresse($adresse);
    $commande->setTypePaiement('Carte Bancaire');
    $commande->setStatus('Validated');

    $entityManager->persist($commande);

    foreach ($cart as $item) {
        $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

        if (!$produit) {
            continue;
        }

        if ($produit->getStock() < $item['quantity']) {
            $this->addFlash('error', "Stock insuffisant pour: {$produit->getNom()}.");
            return $this->redirectToRoute('cart');
        }

        // ✅ Deduct stock
        $produit->setStock($produit->getStock() - $item['quantity']);

        // ✅ Save order-product relation
        $commandeProduit = new CommandeProduit();
        $commandeProduit->setCommande($commande);
        $commandeProduit->setProduit($produit);
        $commandeProduit->setQuantity($item['quantity']);

        $entityManager->persist($commandeProduit);
    }

    $entityManager->flush();

    // ✅ Clear session cart
    $session->remove('cart');
    $session->remove('adresse');

    $this->addFlash('success', 'Paiement réussi ! Votre commande a été enregistrée.');

    return $this->redirectToRoute('produit_index');
}








    


    #[Route('/admin/commandes', name: 'admin_commandes')]
public function listCommandes(EntityManagerInterface $entityManager): Response
{
    $commandes = $entityManager->getRepository(Commande::class)->findAll();

    return $this->render('admin/commandes/index.html.twig', [
        'commandes' => $commandes
    ]);
}

#[Route('/admin/commande/{id}', name: 'admin_commande_details')]
public function showCommandeDetails(int $id, EntityManagerInterface $entityManager): Response
{
    $commande = $entityManager->getRepository(Commande::class)->find($id);

    if (!$commande) {
        throw $this->createNotFoundException('Commande non trouvée');
    }

    return $this->render('admin/commandes/details.html.twig', [
        'commande' => $commande
    ]);
}



#[Route('/add-to-cart/{id}', name: 'add_to_cart')]
public function addToCart(Produit $produit, SessionInterface $session): Response
{
    if (!$produit) {
        $this->addFlash('error', 'Produit introuvable.');
        return $this->redirectToRoute('cart');
    }

    $cart = $session->get('cart', []);

    if (!isset($cart[$produit->getId()])) {
        $cart[$produit->getId()] = [
            'id' => $produit->getId(),
            'name' => $produit->getNom(),
            'price' => $produit->getPrix(),
            'quantity' => 1
        ];
    } else {
        $cart[$produit->getId()]['quantity']++;
    }

    $session->set('cart', $cart);

    $this->addFlash('success', 'Produit ajouté au panier.');
    return $this->redirectToRoute('cart');
}




    #[Route('/remove-from-cart/{id}', name: 'remove_from_cart')]
    public function removeFromCart(int $id, SessionInterface $session): Response
    {
        $cart = $session->get('cart', []);

        if (isset($cart[$id])) {
            unset($cart[$id]);
            $session->set('cart', $cart);
            $this->addFlash('success', 'Produit retiré du panier.');
        } else {
            $this->addFlash('warning', 'Produit non trouvé dans le panier.');
        }

        return $this->redirectToRoute('cart');
    }

    #[Route('/cart', name: 'cart')]
public function cart(SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    $cart = $session->get('cart', []);

    $cartItems = [];
    foreach ($cart as $item) {
        $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

        if ($produit) {
            $cartItems[$item['id']] = [
                'id' => $produit->getId(),
                'name' => $produit->getNom(),
                'price' => $produit->getPrix(),
                'quantity' => $item['quantity'],
                'image' => $produit->getImage()
            ];
        }
    }

    return $this->render('cart/index.html.twig', [
        'cart' => $cartItems
    ]);
}

#[Route('/update-cart/{id}', name: 'update_cart', methods: ['POST'])]
public function updateCart(int $id, Request $request, SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    $cart = $session->get('cart', []);

    if (!isset($cart[$id])) {
        return $this->json(['error' => 'Produit introuvable dans le panier.'], 400);
    }

    $produit = $entityManager->getRepository(Produit::class)->find($id);

    if (!$produit) {
        return $this->json(['error' => 'Produit introuvable.'], 400);
    }

    $newQuantity = (int) $request->request->get('quantity');

    if ($newQuantity < 1) {
        unset($cart[$id]); // Remove product if quantity is set to 0
    } elseif ($newQuantity > $produit->getStock()) {
        return $this->json(['error' => 'Stock insuffisant pour ce produit.'], 400);
    } else {
        $cart[$id]['quantity'] = $newQuantity;
    }

    // ✅ Recalculate cart total
    $total = array_reduce($cart, function ($carry, $item) {
        return $carry + ($item['quantity'] * $item['price']);
    }, 0);

    // ✅ Store updated cart in session
    $session->set('cart', $cart);
    $session->set('cart_total', $total); // Store total price

    return $this->json([
        'success' => true,
        'newTotal' => number_format($total, 2, '.', ',')
    ]);
}










}
