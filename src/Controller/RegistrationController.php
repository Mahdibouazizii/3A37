<?php

namespace App\Controller;

use App\Entity\User;
use App\Form\RegistrationFormType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\Form\Extension\Core\Type\RepeatedType;
use Doctrine\DBAL\Exception\UniqueConstraintViolationException;
use Symfony\Component\Security\Http\Authentication\UserAuthenticatorInterface;
use App\Security\LoginAuthenticator;


class RegistrationController extends AbstractController
{
    #[Route('/register', name: 'app_register')]
    public function register(
        Request $request, 
        UserPasswordHasherInterface $passwordHasher, 
        EntityManagerInterface $entityManager,
        UserAuthenticatorInterface $userAuthenticator,
        LoginAuthenticator $loginAuthenticator
    ): Response 
    {
        $user = new User();
        $form = $this->createForm(RegistrationFormType::class, $user);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            $plainPassword = $form->get('plainPassword')->getData();
        
            if (!$plainPassword) {
                $this->addFlash('error', 'Password cannot be empty.');
                return $this->redirectToRoute('app_register');
            }
        
            // Hash password
            $user->setPassword(
                $passwordHasher->hashPassword($user, $plainPassword)
            );
        
            // Ensure the role is set
            $user->setRoles(['ROLE_USER']);
        
            // Handle Profile Picture Upload
            $profilePictureFile = $form->get('profilePicture')->getData();
            if ($profilePictureFile) {
                $newFilename = uniqid() . '.' . $profilePictureFile->guessExtension();
                try {
                    $profilePictureFile->move(
                        $this->getParameter('profile_pictures_directory'),
                        $newFilename
                    );
                    $user->setProfilePicture($newFilename);
                } catch (FileException $e) {
                    $this->addFlash('error', 'Error uploading the profile picture.');
                    return $this->redirectToRoute('app_register');
                }
            }
        
            try {
                $entityManager->persist($user);
                $entityManager->flush();
            } catch (UniqueConstraintViolationException $e) {
                $this->addFlash('error', 'This email is already in use.');
                return $this->redirectToRoute('app_register');
            }
        
            // ✅ Automatically log in the user after registration
            $response = $userAuthenticator->authenticateUser(
                $user,
                $loginAuthenticator,
                $request
            );
    
            return $response; // Return the authentication response
        }
    
        return $this->render('registration/register.html.twig', [
            'registrationForm' => $form->createView(),
        ]);
    }
}    
    


