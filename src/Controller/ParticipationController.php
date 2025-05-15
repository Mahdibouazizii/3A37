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
use Symfony\Component\Security\Core\Security;

    #[Route('/participation')]

    final class ParticipationController extends AbstractController
    {
        private $security;

        public function __construct(Security $security)
        {
            $this->security = $security;
        }

        #[Route(name: 'app_participation_index', methods: ['GET'])]
public function index(ParticipationRepository $participationRepository, Request $request): Response
{
    // Récupération du paramètre de tri 'sort' (par défaut 'age_asc')
    $sort = $request->query->get('sort', 'age_asc');

    // Récupérer l'utilisateur connecté
    $user = $this->security->getUser();
    
    if (!$user) {
        // Optionally handle the case where the user is not logged in
        return $this->redirectToRoute('app_login');
    }

    // Création du QueryBuilder pour les participations
    $queryBuilder = $participationRepository->createQueryBuilder('p');

    // Filtrer les participations en fonction de l'utilisateur connecté
    $queryBuilder->where('p.iduser = :user')
                 ->setParameter('user', $user);

    // Application du tri en fonction du paramètre 'sort'
    switch ($sort) {
        case 'age_asc':  // Tri croissant par âge
            $queryBuilder->orderBy('p.age', 'ASC');
            break;
        case 'age_desc':  // Tri décroissant par âge
            $queryBuilder->orderBy('p.age', 'DESC');
            break;
        case 'statut_asc':  // Tri croissant par statut
            $queryBuilder->orderBy('p.statut', 'ASC');
            break;
        case 'statut_desc':  // Tri décroissant par statut
            $queryBuilder->orderBy('p.statut', 'DESC');
            break;
        default:  // Tri croissant par défaut (âge)
            $queryBuilder->orderBy('p.age', 'ASC');
            break;
    }

    // Exécution de la requête pour récupérer les participations triées de l'utilisateur connecté
    $participations = $queryBuilder->getQuery()->getResult();

    // Retourne la vue avec les participations de l'utilisateur connecté et le paramètre de tri
    return $this->render('participation/index.html.twig', [
        'participations' => $participations,
        'sort' => $sort,  // Passer le paramètre de tri à la vue
    ]);
}

        
#[Route('/back', name: 'app_participation_index_back', methods: ['GET', 'POST'])]
public function backindex(ParticipationRepository $participationRepository): Response
{
    return $this->render('admin/participation/indexback.html.twig', [
        'participations' => $participationRepository->findAll(),
    ]);
}


     #[Route('/new-front/{id}', name: 'app_participation_new_front', methods: ['GET', 'POST'])]
public function frontnew(Request $request, EntityManagerInterface $entityManager, int $id): Response
{
    $event = $entityManager->getRepository(Event::class)->find($id);

    if (!$event) {
        throw $this->createNotFoundException('Événement non trouvé.');
    }

    $user = $this->security->getUser();

    if (!$user) {
        throw $this->createAccessDeniedException('Vous devez être connecté pour participer à cet événement.');
    }

    // 🔒 Vérifier si l'utilisateur a déjà participé
    $existingParticipation = $entityManager->getRepository(Participation::class)->findOneBy([
        'idevenement' => $event,
        'iduser' => $user,
    ]);

    if ($existingParticipation) {
        $this->addFlash('error', '❌ Vous avez déjà participé à cet événement.');
        return $this->redirectToRoute('app_event_index_front');
    }

    $participation = new Participation();
    $participation->setIdevenement($event);
    $participation->setIduser($user);

    $form = $this->createForm(ParticipationType::class, $participation);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $captchaResponse = $request->get('g-recaptcha-response');

        $client = HttpClient::create();
        $response = $client->request('POST', 'https://www.google.com/recaptcha/api/siteverify', [
            'query' => [
                'secret' => '6LdCeuIqAAAAANpsJsZqC3nO59aSgnSYZ2YqpQBL',
                'response' => $captchaResponse
            ]
        ]);

        $data = $response->toArray();

        if ($data['success']) {
            $nbPlacesDemandées = $participation->getNbrplace();
            $placesDisponibles = $event->getNbrplacetottale();

            if ($nbPlacesDemandées > $placesDisponibles) {
                $this->addFlash('error', '❌ Pas assez de places disponibles. Il reste seulement ' . $placesDisponibles . ' places.');
                return $this->redirectToRoute('app_event_index_front');
            }

            // 📉 Mise à jour des places disponibles
            $event->setNbrplacetottale($placesDisponibles - $nbPlacesDemandées);

            $entityManager->persist($participation);
            $entityManager->flush();

            $this->addFlash('success', '✅ Participation enregistrée avec succès !');
            return $this->redirectToRoute('app_event_index_front');
        } else {
            $this->addFlash('error', 'reCAPTCHA invalide. Veuillez réessayer.');
        }
    } elseif ($form->isSubmitted()) {
        $this->addFlash('error', 'Veuillez compléter le reCAPTCHA pour continuer.');
    }

    return $this->render('participation/newfront.html.twig', [
        'participation' => $participation,
        'form' => $form->createView(),
    ]);
}



        #[Route('/{id}/back', name: 'app_participation_show_back', methods: ['GET'])]
        public function backshow(Participation $participation): Response
        {
            return $this->render('admin/participation/showback.html.twig', [
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

            return $this->render('admin/participation/edit.html.twig', [
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
