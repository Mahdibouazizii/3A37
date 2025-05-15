<?php
// src/Controller/AdminDashboardController.php
namespace App\Controller;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Doctrine\ORM\EntityManagerInterface;
use App\Entity\Produit;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Bundle\SecurityBundle\Attribute\IsGranted;
use App\Form\ProduitType;
<<<<<<< HEAD
=======
use Symfony\Component\Validator\Validator\ValidatorInterface;
use Symfony\Component\Form\FormError;
>>>>>>> 7ef3b12 (Initial commit with README.md)
class AdminDashboardController extends AbstractController
{
    #[Route('/admin/dashboard', name: 'admin_dashboard')]
    #[IsGranted('ROLE_ADMIN')]
    public function index(): Response
    {
        return $this->render('admin/dashboard.html.twig');
    }

    #[Route('/admin/promotions', name: 'admin_produit_index')]
    public function promotions(EntityManagerInterface $entityManager): Response
    {
        // ✅ Fetch all products from the database
        $produits = $entityManager->getRepository(Produit::class)->findAll();

        return $this->render('admin/produits/index.html.twig', [
            'produits' => $produits,
        ]);
    }

    #[Route('/admin/promotions/edit/{id}', name: 'admin_produit_promo', methods: ['GET', 'POST'])]
    public function setPromotion(Request $request, Produit $produit, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createFormBuilder($produit)
        ->add('promoPercentage', IntegerType::class, [
            'required' => false,
            'label' => 'Pourcentage de promotion',
            'attr' => ['class' => 'form-control']
        ])
        ->getForm();

    $form->handleRequest($request);
    
    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager->flush();
        return $this->redirectToRoute('admin_produit_index');
    }

    return $this->render('admin/produits/edit.html.twig', [
        'form' => $form->createView(),
        'produit' => $produit,
    ]);
    }

//add new product 
#[Route('/admin/produits/new', name: 'admin_produit_new', methods: ['GET', 'POST'])]
<<<<<<< HEAD
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        // Check if the user has ROLE_ADMIN
        $this->denyAccessUnlessGranted('ROLE_ADMIN');
    
        $produit = new Produit();
        $form = $this->createForm(ProduitType::class, $produit);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
=======
public function new(Request $request, EntityManagerInterface $entityManager, ValidatorInterface $validator): Response
{
    // Check if the user has ROLE_ADMIN
    $this->denyAccessUnlessGranted('ROLE_ADMIN');

    $produit = new Produit();
    $form = $this->createForm(ProduitType::class, $produit);
    $form->handleRequest($request);

    if ($form->isSubmitted()) {
        // Perform validation
        $errors = $validator->validate($produit);

        if (count($errors) > 0) {
            foreach ($errors as $error) {
                $form->addError(new FormError($error->getMessage()));
            }
        } elseif ($form->isValid()) {
>>>>>>> 7ef3b12 (Initial commit with README.md)
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $newFilename = uniqid().'.'.$imageFile->guessExtension();
                try {
                    $imageFile->move(
                        $this->getParameter('produit_pictures_directory'),
                        $newFilename
                    );
                    $produit->setImage($newFilename);
                } catch (FileException $e) {
<<<<<<< HEAD
                    // Handle upload error
                }
            }
    
            $entityManager->persist($produit);
            $entityManager->flush();
    
            return $this->redirectToRoute('admin_produits');
        }
    
        return $this->render('admin/produits/new.html.twig', [
            'form' => $form->createView(),
            'produit' => $produit,     
        ]);
    }
=======
                    $form->addError(new FormError("Erreur lors du téléchargement de l'image."));
                }
            }

            $entityManager->persist($produit);
            $entityManager->flush();

            return $this->redirectToRoute('admin_produits');
        }
    }

    return $this->render('admin/produits/new.html.twig', [
        'form' => $form->createView(),
        'produit' => $produit,
    ]);
}

>>>>>>> 7ef3b12 (Initial commit with README.md)
// ✅ Show all products in Admin Panel
#[Route('/admin/produits', name: 'admin_produits')]
public function produits(EntityManagerInterface $entityManager): Response
{
    $produits = $entityManager->getRepository(Produit::class)->findAll();
    return $this->render('admin/produits/produits.html.twig', [
        'produits' => $produits
    ]);
}
// ✅ Edit Product
#[Route('/admin/produits/edit/{id}', name: 'admin_produit_edit', methods: ['GET', 'POST'])]
public function edit(Request $request, Produit $produit, EntityManagerInterface $entityManager): Response
{
    $form = $this->createForm(ProduitType::class, $produit);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        // Handle Image Upload
        $imageFile = $form->get('image')->getData();

        if ($imageFile) {
            $newFilename = uniqid() . '.' . $imageFile->guessExtension();

            try {
                // Move new image to the upload directory
                $imageFile->move(
                    $this->getParameter('produit_pictures_directory'),
                    $newFilename
                );

                // Delete old image if exists
                if ($produit->getImage()) {
                    $oldImagePath = $this->getParameter('produit_pictures_directory') . '/' . $produit->getImage();
                    if (file_exists($oldImagePath)) {
                        unlink($oldImagePath);
                    }
                }

                // Set new image name
                $produit->setImage($newFilename);
            } catch (FileException $e) {
                $this->addFlash('error', 'Erreur lors du téléchargement de l\'image.');
            }
        }

        $entityManager->flush();
        $this->addFlash('success', 'Produit mis à jour avec succès !');

        return $this->redirectToRoute('admin_produits');
    }

    return $this->render('admin/produits/new.html.twig', [ // ✅ Correct template file
        'form' => $form->createView(),
        'produit' => $produit
    ]);
}


// ✅ Delete Product
#[Route('/admin/produits/delete/{id}', name: 'admin_produit_delete', methods: ['POST'])]
public function delete(Produit $produit, EntityManagerInterface $entityManager): Response
{
    $entityManager->remove($produit);
    $entityManager->flush();

    return $this->redirectToRoute('admin_produits');
}



}
