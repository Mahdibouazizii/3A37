<?php

namespace App\Controller;

use App\Entity\Participation;
use App\Entity\Event;
use App\Form\ParticipationType;
use App\Repository\ParticipationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Validation;
use Symfony\Component\HttpClient\HttpClient;

    #[Route('/participation')]
    final class ParticipationController extends AbstractController
    {
        #[Route(name: 'app_participation_index', methods: ['GET'])]
        public function index(ParticipationRepository $participationRepository, Request $request): Response
        {
            // Récupération du paramètre de tri 'sort' (par défaut 'age_asc')
            $sort = $request->query->get('sort', 'age_asc');
        
            // Création du QueryBuilder pour les participations
            $queryBuilder = $participationRepository->createQueryBuilder('p');
        
            // Application du tri en fonction du paramètre 'sort'
            switch ($sort) {
                case 'age_asc':  // Tri croissant par âge
                    $queryBuilder->orderBy('p.age', 'ASC');
                    break;
                case 'age_desc':  // Tri décroissant par âge
                    $queryBuilder->orderBy('p.age', 'DESC');
                    break;
                case 'statut_asc':  // Tri croissant par enplacement (statut)
                    $queryBuilder->orderBy('p.statut', 'ASC');
                    break;
                case 'statut_desc':  // Tri décroissant par enplacement (statut)
                    $queryBuilder->orderBy('p.statut', 'DESC');
                    break;
                default:  // Tri croissant par défaut (âge)
                    $queryBuilder->orderBy('p.age', 'ASC');
                    break;
            }
        
            // Exécution de la requête pour récupérer les participations triées
            $participations = $queryBuilder->getQuery()->getResult();
        
            // Retourne la vue avec les participations et le paramètre de tri
            return $this->render('participation/index.html.twig', [
                'participations' => $participations,
                'sort' => $sort,  // Passer le paramètre de tri à la vue
            ]);
        }
        
        #[Route('/back', name: 'app_participation_index_back', methods: ['GET'])]

        public function backindex(ParticipationRepository $participationRepository): Response
        {
            return $this->render('participation/indexback.html.twig', [
                'participations' => $participationRepository->findAll(),
                
            ]);
        }

        #[Route('/new-front/{id}', name: 'app_participation_new_front', methods: ['GET', 'POST'])]
        public function frontnew(Request $request, EntityManagerInterface $entityManager, int $id): Response
        {
            // Récupérer l'événement par ID
            $event = $entityManager->getRepository(Event::class)->find($id);
            
            if (!$event) {
                throw $this->createNotFoundException('Événement non trouvé.');
            }
        
            // Créer un objet Participation
            $participation = new Participation();
            $participation->setIdevenement($event);
        
            // Créer le formulaire
            $form = $this->createForm(ParticipationType::class, $participation);
            $form->handleRequest($request);
        
            // Gérer la soumission du formulaire et la validation
            if ($form->isSubmitted() && $form->isValid()) {
                $captchaResponse = $request->get('g-recaptcha-response');
        
                // Vérification de la réponse reCAPTCHA
                $client = HttpClient::create();
                $response = $client->request('POST', 'https://www.google.com/recaptcha/api/siteverify', [
                    'query' => [
                    'secret' => '6LdCeuIqAAAAANpsJsZqC3nO59aSgnSYZ2YqpQBL',
                        'response' => $captchaResponse
                    ]
                ]);
        
                $data = $response->toArray();
        
                // Vérification si la validation reCAPTCHA est réussie
                if ($data['success']) {
                    // Persist la participation si reCAPTCHA valide
                    $entityManager->persist($participation);
                    $entityManager->flush();
        
                    // Redirection vers la page d'index des participations
                    return $this->redirectToRoute('app_participation_index', [], Response::HTTP_SEE_OTHER);
                } else {
                    // Ajouter un message d'erreur si reCAPTCHA échoue
                    $this->addFlash('error', 'reCAPTCHA invalide. Veuillez réessayer.');
                }
            } else {
                // Ajouter un message si le formulaire est soumis sans passer la validation reCAPTCHA
                if ($form->isSubmitted()) {
                    $this->addFlash('error', 'Veuillez compléter le reCAPTCHA pour continuer.');
                }
            }
        
            // Retourner le formulaire si non soumis ou invalide
            return $this->render('participation/newfront.html.twig', [
                'participation' => $participation,
                'form' => $form->createView(),
            ]);
        }
        
       


        #[Route('/{id}/back', name: 'app_participation_show_back', methods: ['GET'])]
        public function backshow(Participation $participation): Response
        {
            return $this->render('participation/showback.html.twig', [
                'participation' => $participation,
            ]);
        }

        #[Route('/{id}', name: 'app_participation_show', methods: ['GET'])]
        public function show(Participation $participation): Response
        {
            return $this->render('participation/show.html.twig', [
                'participation' => $participation,
            ]);
        }


        #[Route('/{id}/fedit', name: 'app_participation_edit', methods: ['GET', 'POST'])]
        public function edit(Request $request, Participation $participation, EntityManagerInterface $entityManager): Response
        {
            $form = $this->createForm(ParticipationType::class, $participation);
            $form->handleRequest($request);

            if ($form->isSubmitted() && $form->isValid()) {
                // Save the updated participation
                $entityManager->flush();

                // Redirect to the participation index page after editing
                return $this->redirectToRoute('app_participation_index_back', [], Response::HTTP_SEE_OTHER);
            }

            return $this->render('participation/edit.html.twig', [
                'participation' => $participation,
                'form' => $form->createView(), // Ensure form is rendered correctly
            ]);
        }

        #[Route('/{id}/edit', name: 'app_participation_fedit', methods: ['GET', 'POST'])]
        public function fedit(Request $request, Participation $participation, EntityManagerInterface $entityManager): Response
        {
            $form = $this->createForm(ParticipationType::class, $participation);
            $form->handleRequest($request);

            if ($form->isSubmitted() && $form->isValid()) {
                // Save the updated participation
                $entityManager->flush();

                // Redirect to the participation index page after editing
                return $this->redirectToRoute('app_participation_index', [], Response::HTTP_SEE_OTHER);
            }

            return $this->render('participation/fedit.html.twig', [
                'participation' => $participation,
                'form' => $form->createView(), // Ensure form is rendered correctly
            ]);
        }

        

        #[Route('/{id}', name: 'app_participation_delete', methods: ['POST'])]
        public function delete(Request $request, Participation $participation, EntityManagerInterface $entityManager): Response
        {
            if ($this->isCsrfTokenValid('delete' . $participation->getId(), $request->request->get('_token'))) {
                $entityManager->remove($participation);
                $entityManager->flush();
            }
            
            // Rediriger vers la page 'back.html.twig' après la suppression
            return $this->redirectToRoute('app_participation_index');
        }
        
        

    }
