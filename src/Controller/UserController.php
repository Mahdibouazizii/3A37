<?php

namespace App\Controller;

use App\Entity\User;
use App\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Request;

class UserController extends AbstractController
{
    // Afficher la liste des utilisateurs
    #[Route('/users', name: 'app_user_index')]
    public function index(UserRepository $userRepository): Response
    {
        // Récupérer tous les utilisateurs
        $users = $userRepository->findAll();

        return $this->render('user/index.html.twig', [
            'users' => $users,
        ]);
    }

    // Afficher les détails d'un utilisateur
    #[Route('/user/{id}', name: 'app_user_show')]
    public function show(int $id, UserRepository $userRepository): Response
    {
        // Récupérer l'utilisateur par ID
        $user = $userRepository->find($id);

        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }

        return $this->render('user/show.html.twig', [
            'user' => $user,
        ]);
    }

    // Créer un nouvel utilisateur
    #[Route('/user/new', name: 'app_user_new', methods: ['GET', 'POST'])]
    public function new(Request $request, UserRepository $userRepository): Response
    {
        $user = new User();
        $form = $this->createForm(UserType::class, $user);

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Enregistrer l'utilisateur en base de données
            $userRepository->save($user);

            $this->addFlash('success', 'Utilisateur créé avec succès!');
            return $this->redirectToRoute('app_user_index');
        }

        return $this->render('user/new.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    // Modifier un utilisateur existant
    #[Route('/user/{id}/edit', name: 'app_user_edit', methods: ['GET', 'POST'])]
    public function edit(int $id, Request $request, UserRepository $userRepository): Response
    {
        $user = $userRepository->find($id);

        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }

        $form = $this->createForm(UserType::class, $user);

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $userRepository->save($user);
            $this->addFlash('success', 'Utilisateur mis à jour avec succès!');
            return $this->redirectToRoute('app_user_index');
        }

        return $this->render('user/edit.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    // Supprimer un utilisateur
    #[Route('/user/{id}/delete', name: 'app_user_delete', methods: ['POST'])]
    public function delete(int $id, UserRepository $userRepository): Response
    {
        $user = $userRepository->find($id);

        if (!$user) {
            throw $this->createNotFoundException('Utilisateur non trouvé');
        }

        $userRepository->remove($user);
        $this->addFlash('success', 'Utilisateur supprimé avec succès!');

        return $this->redirectToRoute('app_user_index');
    }
}
