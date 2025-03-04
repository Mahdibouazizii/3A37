<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Commande;
use App\Entity\CommandeProduit;
use Doctrine\ORM\EntityManagerInterface;

use App\Entity\Produit;
use App\Entity\Feedback;
use App\Form\ProduitType;
use App\Form\FeedbackType;
use App\Controller\FeedbackRepository;
use App\Repository\ProduitRepository;
use Stripe\Stripe;
use Stripe\Checkout\Session;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

use Symfony\Bundle\SecurityBundle\Attribute\IsGranted;
use Symfony\Component\HttpFoundation\Session\SessionInterface;



use App\Form\CommandeType;

class CommandeController extends AbstractController
{


    #[Route('/place-order', name: 'place_order', methods: ['GET', 'POST'])]
public function placeOrder(Request $request, SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    $cart = $session->get('cart', []);
    $stockWarnings = [];

    if (empty($cart)) {
        $this->addFlash('warning', 'Votre panier est vide.');
        return $this->redirectToRoute('cart');
    }

    foreach ($cart as $item) {
        $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

        if (!$produit) {
            $this->addFlash('error', 'Un produit dans le panier est introuvable.');
            return $this->redirectToRoute('cart');
        }

        // ✅ Check stock availability
        if ($produit->getStock() < $item['quantity']) {
            $stockWarnings[$produit->getId()] = [
                'name' => $produit->getNom(),
                'available_stock' => $produit->getStock(),
                'requested_quantity' => $item['quantity'],
            ];
        }
    }

    // ✅ If there are stock warnings, display them in the Twig template
    if (!empty($stockWarnings)) {
        return $this->render('commande/place_order.html.twig', [
            'form' => $this->createForm(CommandeType::class)->createView(),
            'cart' => $cart,
            'stockWarnings' => $stockWarnings, // ✅ Pass the warnings to Twig
        ]);
    }

    // ✅ If no stock issues, proceed with order creation
    $commande = new Commande();
    $commande->setUser($this->getUser());

    $form = $this->createForm(CommandeType::class, $commande, [
        'allow_extra_fields' => true,
    ]);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $adresse = $commande->getAdresse();
        $session->set('adresse', $adresse);

        // ✅ Retrieve payment type
        $typePaiement = $request->request->get('commande')['typePaiement'] ?? null;
        if (!$typePaiement) {
            $this->addFlash('error', 'Type de paiement non défini.');
            return $this->redirectToRoute('cart');
        }
        $commande->setTypePaiement($typePaiement);

        foreach ($cart as $item) {
            $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

            // ✅ Decrement stock
            $produit->setStock($produit->getStock() - $item['quantity']);

            $commandeProduit = new CommandeProduit();
            $commandeProduit->setCommande($commande);
            $commandeProduit->setProduit($produit);
            $commandeProduit->setQuantity($item['quantity']);

            $entityManager->persist($commandeProduit);
        }

        $entityManager->persist($commande);
        $entityManager->flush();

        // ✅ Clear cart after successful order
        $session->remove('cart');
        $this->addFlash('success', 'Commande passée avec succès.');
        return $this->redirectToRoute('produit_index');
    }

    return $this->render('commande/place_order.html.twig', [
        'form' => $form->createView(),
        'cart' => $cart,
        'stockWarnings' => [],
    ]);
}






#[Route('/create-checkout-session', name: 'create_checkout_session', methods: ['POST'])]
public function createCheckoutSession(
    Request $request,
    SessionInterface $session,
    EntityManagerInterface $entityManager,
    UrlGeneratorInterface $urlGenerator
): Response {
    $cart = $session->get('cart', []);

    if (empty($cart)) {
        return $this->json(['error' => 'Votre panier est vide.'], 400);
    }

    $data = json_decode($request->getContent(), true);

    if (!isset($data['adresse']) || empty(trim($data['adresse']))) {
        return $this->json(['error' => 'Adresse non fournie.'], 400);
    }

    $adresse = trim($data['adresse']);
    $session->set('adresse', $adresse);

    // Set Stripe API key
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

    // Create Stripe Checkout Session
    $checkoutSession = Session::create([
        'payment_method_types' => ['card'],
        'line_items' => $lineItems,
        'mode' => 'payment',
        'metadata' => [
            'adresse' => $adresse,
            'user_id' => $this->getUser()->getId(), // Store user ID
            'cart' => json_encode($cart), // Store cart in metadata
        ],
        'success_url' => $urlGenerator->generate('payment_success', [], UrlGeneratorInterface::ABSOLUTE_URL),
        'cancel_url' => $urlGenerator->generate('cart', [], UrlGeneratorInterface::ABSOLUTE_URL),
    ]);

    return $this->json(['checkoutUrl' => $checkoutSession->url]);
}






#[Route('/payment-success', name: 'payment_success')]
public function paymentSuccess(SessionInterface $session, EntityManagerInterface $entityManager): Response
{
    $cart = $session->get('cart', []);

    if (empty($cart)) {
        $this->addFlash('warning', 'Votre panier est vide.');
        return $this->redirectToRoute('cart');
    }

    $adresse = $session->get('adresse');

    if (!$adresse) {
        $this->addFlash('error', 'Adresse non définie. Veuillez repasser la commande.');
        return $this->redirectToRoute('cart');
    }

    // Create new Commande
    $commande = new Commande();
    $commande->setUser($this->getUser());
    $commande->setAdresse($adresse);
    $commande->setTypePaiement('Carte Bancaire');

    foreach ($cart as $item) {
        $produit = $entityManager->getRepository(Produit::class)->find($item['id']);

        if (!$produit) {
            continue;
        }

        $commandeProduit = new CommandeProduit();
        $commandeProduit->setCommande($commande);
        $commandeProduit->setProduit($produit);
        $commandeProduit->setQuantity($item['quantity']);

        $entityManager->persist($commandeProduit);
    }

    $entityManager->persist($commande);
    $entityManager->flush();

    // ✅ Clear cart AFTER successful payment
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
                'quantity' => $item['quantity']
            ];
        }
    }

    return $this->render('cart/index.html.twig', [
        'cart' => $cartItems
    ]);
}







}
