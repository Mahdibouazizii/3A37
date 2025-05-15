<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;


class SecurityController extends AbstractController
{
    // SecurityController.php
#[Route('/login', name: 'app_login')]
public function login(AuthenticationUtils $authenticationUtils): Response
{
    if ($this->getUser()) {
        // The user has already passed the user checker
        // because they're recognized as authenticated here
        // so they're definitely NOT banned
<<<<<<< HEAD
        return in_array('ROLE_ADMIN', $this->getUser()->getRoles())
=======
        return in_array('admin', $this->getUser()->getRoles())
>>>>>>> 7ef3b12 (Initial commit with README.md)
            ? $this->redirectToRoute('admin_dashboard')
            : $this->redirectToRoute('homepage');
    }

    // Get any login error
    $error = $authenticationUtils->getLastAuthenticationError();
    $lastUsername = $authenticationUtils->getLastUsername();

    return $this->render('security/login.html.twig', [
        'last_username' => $lastUsername,
        'error' => $error,
    ]);
}







    #[Route(path: '/logout', name: 'app_logout')]
    public function logout(): void
    {
        throw new \LogicException('This method can be blank - it will be intercepted by the logout key on your firewall.');
    }
}
