<?php

namespace App\Controller;

use App\Entity\Produit;
use App\Entity\Feedback;
use App\Form\ProduitType;
use App\Form\FeedbackType;
use App\Controller\FeedbackRepository;
use App\Repository\ProduitRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\SecurityBundle\Attribute\IsGranted;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use App\Entity\Commande;
use App\Entity\CommandeProduit;
use App\Controller\TextareaType;

use App\Form\CommandeType;


#[Route('/produits')]
class ProduitController extends AbstractController
{



    
    #[Route('/', name: 'produit_index', methods: ['GET'])]
    public function index(ProduitRepository $produitRepository): Response
    {
        // Generate random ads dynamically
        $ads = [
            ['image' => 'https://source.unsplash.com/800x200/?technology'],
            ['image' => 'https://source.unsplash.com/800x200/?business'],
            ['image' => 'https://source.unsplash.com/800x200/?marketing'],
            ['image' => 'https://source.unsplash.com/800x200/?advertising']
        ];
        return $this->render('produit/index.html.twig', [
            'produits' => $produitRepository->findAll(),
            'ads'=>$ads,
        ]);
    }

    

    

    
    #[Route('/new', name: 'produit_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        // Check if the user has ROLE_ADMIN
        $this->denyAccessUnlessGranted('ROLE_ADMIN');
    
        $produit = new Produit();
        $form = $this->createForm(ProduitType::class, $produit);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
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
                    // Handle upload error
                }
            }
    
            $entityManager->persist($produit);
            $entityManager->flush();
    
            return $this->redirectToRoute('admin_dashboard');
        }
    
        return $this->render('admin/produits/new.html.twig', [
            'form' => $form->createView(),
            'produit' => $produit,     
        ]);
    }
    

    #[Route('/produits/{id}', name: 'produit_show', methods: ['GET','POST'])]
    public function show(Produit $produit, Request $request, EntityManagerInterface $entityManager): Response
    {
        // ✅ Create a new feedback instance
    
    $feedback = new Feedback();
    $feedback->setProduit($produit);
    
    // Create Feedback form
    $form = $this->createForm(FeedbackType::class, $feedback);
    $form->handleRequest($request);

    // Handle form submission
    if ($form->isSubmitted() && $form->isValid()) {
        $feedback->setUser($this->getUser()); // Associate the feedback with the logged-in user
        $entityManager->persist($feedback);
        $entityManager->flush();

        return $this->redirectToRoute('produit_show', ['id' => $produit->getId()]);
    }

    return $this->render('produit/show.html.twig', [
        'produit' => $produit,
        'feedback_form' => $form->createView(),
    ]);
    }

    #[Route('/{id}/edit', name: 'produit_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Produit $produit, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ProduitType::class, $produit);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            return $this->redirectToRoute('produit_index');
        }

        return $this->render('produit/edit.html.twig', [
            'form' => $form->createView(),
            'produit' => $produit,
        ]);
    }

    #[Route('/{id}', name: 'produit_delete', methods: ['POST'])]
    public function delete(Request $request, Produit $produit, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$produit->getId(), $request->request->get('_token'))) {
            $entityManager->remove($produit);
            $entityManager->flush();
        }

        return $this->redirectToRoute('produit_index');
    }

    #[Route('/produits/{id}/feedback', name: 'produit_feedback', methods: ['POST'])]
    public function addFeedback(Request $request, Produit $produit, EntityManagerInterface $entityManager): Response
    {
        if (!$this->getUser()) {
            return $this->redirectToRoute('app_login');
        }
    
        $feedback = new Feedback();
        $form = $this->createForm(FeedbackType::class, $feedback);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $feedback->setProduit($produit);
            $feedback->setUser($this->getUser());
            $entityManager->persist($feedback);
            $entityManager->flush();
        }
    
        return $this->redirectToRoute('produit_show', ['id' => $produit->getId()]);
    }

  






    
}
