<?php
namespace App\Controller;

use App\Entity\Badge;
use App\Form\BadgeType;
use App\Repository\BadgeRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\File\Exception\FileException;

#[Route('/badge')]
final class BadgeController extends AbstractController
{
    // Route to view all badges
    #[Route('/', name: 'app_badge_index', methods: ['GET'])]
    public function index(BadgeRepository $badgeRepository): Response
    {
        return $this->render('admin/badge/index.html.twig', [
            'badges' => $badgeRepository->findAll(),
        ]);
    }

    // Route to create a new badge
    #[Route('/new', name: 'app_badge_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $badge = new Badge();
        $form = $this->createForm(BadgeType::class, $badge);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            /** @var File $file */
            $file = $badge->getImageFile();

            if ($file) {
                $targetDirectory = $this->getParameter('images_directory');
                $filename = uniqid() . '.' . $file->guessExtension();

                try {
                    $file->move($targetDirectory, $filename);
                    $badge->setImagePath($filename); // Save the filename in the entity
                } catch (FileException $e) {
                    $this->addFlash('error', 'Could not upload file: ' . $e->getMessage());
                }
            }

            $entityManager->persist($badge);
            $entityManager->flush();

            return $this->redirectToRoute('app_badge_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('admin/badge/new.html.twig', [
            'badge' => $badge,
            'form' => $form->createView(),  // Explicitly create the form view
        ]);
        
    }

    // Route to view a specific badge
    #[Route('/{id}', name: 'app_badge_show', methods: ['GET'])]
    public function show(Badge $badge): Response
    {
        return $this->render('admin/badge/show.html.twig', [
            'badge' => $badge,
        ]);
    }

    // Route to edit a badge
    #[Route('/edit/{id}', name: 'app_badge_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Badge $badge, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(BadgeType::class, $badge);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            /** @var File $file */
            $file = $badge->getImageFile();

            if ($file) {
                $targetDirectory = $this->getParameter('images_directory');
                $filename = uniqid() . '.' . $file->guessExtension();

                try {
                    $file->move($targetDirectory, $filename);
                    $badge->setImagePath($filename); // Save the filename in the entity
                } catch (FileException $e) {
                    $this->addFlash('error', 'Could not upload file: ' . $e->getMessage());
                }
            }

            $entityManager->flush();

            return $this->redirectToRoute('app_badge_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('admin/badge/edit.html.twig', [
            'badge' => $badge,
            'form' => $form,
        ]);
    }

    // Route to delete a badge
    #[Route('/{id}', name: 'app_badge_delete', methods: ['POST'])]
    public function delete(Request $request, Badge $badge, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$badge->getId(), $request->request->get('_token'))) {
            $entityManager->remove($badge);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_badge_index', [], Response::HTTP_SEE_OTHER);
    }
}
