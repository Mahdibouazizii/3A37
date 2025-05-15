<?php
namespace App\Controller;
use App\Entity\Event;

use App\Entity\Challenge;
use App\Form\ChallengeType;
use App\Repository\ChallengeRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Dompdf\Dompdf;

#[Route('/challenge')] // All routes in this controller will be prefixed with '/challenge'
final class ChallengeController extends AbstractController
{
    // Route for the list of challenges
    #[Route('/back', name: 'app_challenge_index', methods: ['GET'])]
    public function index(ChallengeRepository $challengeRepository): Response
    {
        return $this->render('admin/challenge/index.html.twig', [
            'challenges' => $challengeRepository->findAll(),
        ]);
    }

    #[Route(name: 'app_challenge_index_front', methods: ['GET'])]
    public function frontIndex(ChallengeRepository $challengeRepository): Response
    {
        return $this->render('challenge/index.html.twig', [
            'challenges' => $challengeRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_challenge_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $challenge = new Challenge();
        $form = $this->createForm(ChallengeType::class, $challenge);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('imageFile')->getData();
            if ($imageFile) {
                $challenge->setImageFile($imageFile);
                $challenge->uploadImage($this->getParameter('uploads_directory'));
            }
    
            $entityManager->persist($challenge);
            $entityManager->flush();
    
            return $this->redirectToRoute('app_challenge_index');
        }
    
        return $this->render('admin/challenge/new.html.twig', [
            'challenge' => $challenge,
            'form' => $form->createView(),
        ]);
    }
    


// Route to view a specific challenge
#[Route('/show/{id}/back', name: 'app_challenge_show', methods: ['GET'])]
public function show(Challenge $challenge): Response
{
    // Data for the chart (you can modify this to use dynamic data if needed)
    $chartData = [
        ['Task', 'Hours per Day'],
        ['Work', 8],
        ['Eat', 2],
        ['Commute', 2],
        ['Watch TV', 2],
        ['Sleep', 8]
    ];

    // Rendering the template with the challenge data and chart data
    return $this->render('admin/challenge/show.html.twig', [
        'challenge' => $challenge,
        'chartData' => $chartData,
    ]);
}

    #[Route('/show/{id}', name: 'app_challenge_show_front', methods: ['GET'])]
    public function showFront(Challenge $challenge): Response
    {
        return $this->render('challenge/show.html.twig', [
            'challenge' => $challenge,
        ]);
    }

   // Route to edit a specific challenge
#[Route('/edit/{id}', name: 'app_challenge_edit', methods: ['GET', 'POST'])]
public function edit(Request $request, Challenge $challenge, EntityManagerInterface $entityManager): Response
{
    // Create the form for the challenge
    $form = $this->createForm(ChallengeType::class, $challenge);
    $form->handleRequest($request);

    // If the form is submitted and valid, save the changes
    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager->flush();

        // Redirect to the challenge index page after saving
        return $this->redirectToRoute('app_challenge_index', [], Response::HTTP_SEE_OTHER);
    }

    // Render the edit template with the challenge and form
    return $this->render('admin/challenge/edit.html.twig', [
        'challenge' => $challenge,
        'form' => $form->createView(), // Pass the form view instead of the form itself
    ]);
}

   // Route to delete a challenge
#[Route('/delete/{id}', name: 'app_challenge_delete', methods: ['POST'])]
public function delete(Request $request, Challenge $challenge, EntityManagerInterface $entityManager): Response
{
    // Validate CSRF token
    if ($this->isCsrfTokenValid('delete'.$challenge->getId(), $request->request->get('_token'))) {
        $entityManager->remove($challenge);
        $entityManager->flush();
    }

    // Redirect to the challenge index page
    return $this->redirectToRoute('app_challenge_index', [], Response::HTTP_SEE_OTHER);
}


    // Route to generate PDF for challenges
    #[Route('/pdf/generate', name: 'app_challenge_generate_pdf', methods: ['GET'])]
    public function generatePdf(ChallengeRepository $challengeRepository): Response
    {
        $challenges = $challengeRepository->findAll();

        $html = $this->renderView('admin/challenge/pdf_template.html.twig', [
            'challenges' => $challenges,
        ]);

        $dompdf = new Dompdf();
        $dompdf->loadHtml($html);
        $dompdf->setPaper('A4', 'portrait');
        $dompdf->render();

        return new Response(
            $dompdf->output(),
            200,
            [
                'Content-Type' => 'application/pdf',
                'Content-Disposition' => 'attachment; filename="challenges.pdf"',
            ]
        );
    }
}
