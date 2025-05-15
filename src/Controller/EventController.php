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
use Symfony\Component\HttpFoundation\JsonResponse;
use Knp\Snappy\Pdf;

use Endroid\QrCode\QrCode;
use Endroid\QrCode\Writer\PngWriter;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

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
        return $this->render('admin/event/indexback.html.twig', [
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

    return $this->render('admin/event/new.html.twig', [
        'form' => $form->createView(), // Corrected this line to pass form view
    ]);
}

    
   
    #[Route('/{id}', name: 'app_event_show', methods: ['GET'])]
    public function show(Event $event, UrlGeneratorInterface $urlGenerator): Response
    {
        $qrCode = $this->generateQrCode($event, $urlGenerator);
    
        return $this->render('event/show.html.twig', [
            'event' => $event,
            'qrCode' => $qrCode, // QR Code en base64
        ]);
    }
    


    // Affichage de l'événement côté back
    #[Route('/{id}/back', name: 'app_event_show_back', methods: ['GET'])]
    public function showBack(Event $event): Response
    {
         return $this->render('admin/event/showback.html.twig', [  // Utilisez la vue showback.html.twig pour le back
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
            // Générer un nom unique pour l'image
            $imageFilename = uniqid() . '.' . $imageFile->guessExtension();
            
            // Déplacer l'image vers le répertoire configuré
            $imageFile->move(
                $this->getParameter('images_directory'), // Le paramètre 'images_directory' doit être configuré
                $imageFilename
            );

            // Mettre à jour l'entité avec le nom de l'image
            $event->setImage($imageFilename);
        }

        // Flusher les modifications dans la base de données
        $entityManager->flush();

        // Rediriger vers la liste des événements
        return $this->redirectToRoute('app_event_index_back');
    }

    // Passer la vue du formulaire à la vue Twig
    return $this->render('admin/event/edit.html.twig', [
        'event' => $event,
        'form' => $form->createView(),  // Passage du FormView
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

#[Route('/event/statistics', name: 'app_event_statistics', methods: ['GET'])]
public function statistics(EventRepository $eventRepository): JsonResponse
{
    // Get all events
    $events = $eventRepository->findAll();
    $stats = array_fill(1, 12, 0); // Initialize an array for months 1-12

    // Loop through events to count the number of events per month
    foreach ($events as $event) {
        // Ensure that we are only processing events that have a start date
        if ($event->getHeurDebut()) {
            $month = (int) $event->getHeurDebut()->format('m');
            $stats[$month]++; // Increment the count for the respective month
        }
    }

    // Debugging output to check the stats
    dump($stats); // Remove this after debugging

    // Return the stats as JSON
    return $this->json($stats);
}

#[Route('/event/{id}/pdf', name: 'event_pdf')]
public function generatePdf(Event $event, Pdf $snappy): Response
{
    $html = $this->renderView('admin/event/pdf.html.twig', [
        'event' => $event
    ]);

    return new Response(
        $snappy->getOutputFromHtml($html),
        200,
        [
            'Content-Type' => 'application/pdf',
            'Content-Disposition' => 'attachment; filename="event_'.$event->getId().'.pdf"'
        ]
    );
}
#[Route('/event/{id}/qrcode', name: 'event_qrcode')]
public function generateQrCode(Event $event, UrlGeneratorInterface $urlGenerator): string
{
    $serverIp = '192.168.1.18'; // Ton adresse IP locale

    $eventUrl = "http://{$serverIp}:8000" . $urlGenerator->generate('app_event_show', [
        'id' => $event->getId()
    ]);


    $qrCode = new QrCode($eventUrl);
    $qrCode->setSize(300);
    $qrCode->setMargin(10);

    $writer = new PngWriter();
    $qrCodeResult = $writer->write($qrCode);

    return 'data:image/png;base64,' . base64_encode($qrCodeResult->getString());
}


}
