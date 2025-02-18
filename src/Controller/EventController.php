<?php

namespace App\Controller;

use App\Entity\Event;
use App\Form\EventType;
use App\Repository\EventRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\File\UploadedFile;

#[Route('/event')]
final class EventController extends AbstractController
{
    #[Route(name: 'app_event_index_front', methods: ['GET'])]
    public function frontIndex(EventRepository $eventRepository): Response
    {
        return $this->render('event/index.html.twig', [
            'events' => $eventRepository->findAll(),
        ]);
    }

    #[Route('/back', name: 'app_event_index_back', methods: ['GET'])]
    public function backIndex(EventRepository $eventRepository): Response
    {
        return $this->render('event/indexback.html.twig', [
            'events' => $eventRepository->findAll(),
        ]);
    }

    

    #[Route('/new', name: 'app_event_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $event = new Event();
        $form = $this->createForm(EventType::class, $event);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            // Handle file upload
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $imageFilename = uniqid() . '.' . $imageFile->guessExtension();
                $imageFile->move(
                    $this->getParameter('images_directory'), // Ensure this parameter is set in your config
                    $imageFilename
                );
    
                // Set the image filename in the entity
                $event->setImage($imageFilename);
            }
    
            // Persist and flush the entity
            $entityManager->persist($event);
            $entityManager->flush();
    
            return $this->redirectToRoute('app_event_index_back');
        }
    
        return $this->render('event/new.html.twig', [
            'event' => $event,
            'form' => $form,
        ]);
    }
    
    #[Route('/{id}', name: 'app_event_show', methods: ['GET'])]
    public function show(Event $event): Response
    {
        return $this->render('event/show.html.twig', [
            'event' => $event,
        ]);
    }

    // Affichage de l'événement côté back
    #[Route('/{id}/back', name: 'app_event_show_back', methods: ['GET'])]
    public function showBack(Event $event): Response
    {
        return $this->render('event/showback.html.twig', [  // Utilisez la vue showback.html.twig pour le back
            'event' => $event,
        ]);
    }
    

    #[Route('/{id}/edit', name: 'app_event_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Event $event, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(EventType::class, $event);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            /** @var UploadedFile $imageFile */
            $imageFile = $form->get('image')->getData();

            if ($imageFile) {
                $imageFilename = uniqid() . '.' . $imageFile->guessExtension();
                $imageFile->move(
                    $this->getParameter('images_directory'),
                    $imageFilename
                );

                $event->setImage($imageFilename);
            }

            $entityManager->flush();

            return $this->redirectToRoute('home');
        }

        return $this->render('event/edit.html.twig', [
            'event' => $event,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_event_delete', methods: ['POST'])]
public function delete(Request $request, Event $event, EntityManagerInterface $entityManager): Response
{
    if ($this->isCsrfTokenValid('delete' . $event->getId(), $request->request->get('_token'))) {
        $entityManager->remove($event);
        $entityManager->flush();
    }

    // Rediriger vers la page 'back.html.twig' après la suppression
    return $this->redirectToRoute('app_event_index_back');
}

}
