<?php

namespace App\Controller;

use App\Entity\Participation;
use App\Form\ParticipationType;
use App\Repository\ParticipationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/participation')]
final class ParticipationController extends AbstractController
{
    #[Route(name: 'app_participation_index', methods: ['GET'])]
    public function index(ParticipationRepository $participationRepository): Response
    {
        return $this->render('participation/index.html.twig', [
            'participations' => $participationRepository->findAll(),
        ]);
    }
    #[Route('/back', name: 'app_participation_index_back', methods: ['GET'])]

    public function backindex(ParticipationRepository $participationRepository): Response
    {
        return $this->render('participation/indexback.html.twig', [
            'participations' => $participationRepository->findAll(),
        ]);
    }

    #[Route('/new-front', name: 'app_participation_new_front', methods: ['GET', 'POST'])]
    public function frontnew(Request $request, EntityManagerInterface $entityManager): Response
    {
        $participation = new Participation();
        $form = $this->createForm(ParticipationType::class, $participation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($participation);
            $entityManager->flush();

            return $this->redirectToRoute('app_participation_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('participation/newfront.html.twig', [
            'participation' => $participation,
            'form' => $form->createView(),
        ]);
    }


    #[Route('/new', name: 'app_participation_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $participation = new Participation();
        $form = $this->createForm(ParticipationType::class, $participation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Handle the form submission and persist the new Participation
            $entityManager->persist($participation);
            $entityManager->flush();

            // Redirect to the participation index page
            return $this->redirectToRoute('app_participation_index_back', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('participation/new.html.twig', [
            'participation' => $participation,
            'form' => $form->createView(), // Ensure form is rendered correctly
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


    #[Route('/{id}/edit', name: 'app_participation_edit', methods: ['GET', 'POST'])]
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

    #[Route('/{id}', name: 'app_participation_delete', methods: ['POST'])]
    public function delete(Request $request, Participation $participation, EntityManagerInterface $entityManager): Response
    {
        // Check the CSRF token for security before deleting
        if ($this->isCsrfTokenValid('delete'.$participation->getId(), $request->request->get('_token'))) {
            $entityManager->remove($participation);
            $entityManager->flush();
        }

        // Redirect back to the participation index page after deletion
        return $this->redirectToRoute('app_participation_index_back', [], Response::HTTP_SEE_OTHER);
    }
}
