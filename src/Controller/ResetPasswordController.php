<?php

namespace App\Controller;

use App\Entity\PasswordResetToken;
use App\Form\ResetPasswordRequestFormType;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Csrf\TokenGenerator\TokenGeneratorInterface;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mailer\Transport;
use Symfony\Component\Mime\Email;
use App\Form\VerifyResetCodeType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;

class ResetPasswordController extends AbstractController
{

    #[Route('/reset-password/{email}/form', name: 'app_reset_password_form')]
public function resetPasswordForm(Request $request, string $email, EntityManagerInterface $entityManager, UserRepository $userRepository): Response
{
    $user = $userRepository->findOneBy(['email' => $email]);

    if (!$user) {
        $this->addFlash('error', 'Invalid password reset request.');
        return $this->redirectToRoute('app_reset_password');
    }

    if ($request->isMethod('POST')) {
        $newPassword = $request->request->get('new_password');
        $confirmPassword = $request->request->get('confirm_password');

        if ($newPassword !== $confirmPassword) {
            $this->addFlash('error', 'Passwords do not match.');
        } else {
            // Hash the password
            $user->setPassword(password_hash($newPassword, PASSWORD_BCRYPT));

            $entityManager->flush();

            $this->addFlash('success', 'Password successfully updated.');
            return $this->redirectToRoute('app_login');
        }
    }

    return $this->render('reset_password/change_password.html.twig');
}


    #[Route('/test-email', name: 'app_test_email')]
    public function sendTestEmail(): Response
    {
        // ✅ Manually configure SMTP transport for Gmail
        $dsn = 'smtp://mailpisymfony@gmail.com:yyatxiaanrqasuag@smtp.gmail.com:587?encryption=tls&auth_mode=login';
        
        // Create transport manually
        $transport = Transport::fromDsn($dsn);
        $mailer = new \Symfony\Component\Mailer\Mailer($transport);

        // Create email
        $email = (new Email())
            ->from('mailpisymfony@gmail.com') // Sender email
            ->to('your-email@example.com')  // Change to your test email
            ->subject('Test Email from Symfony')
            ->text('This is a test email sent using Symfony Mailer with Gmail SMTP.');

        // Send the email
        $mailer->send($email);

        return new Response('✅ Test email sent successfully.');
    }

    #[Route('/reset-password', name: 'app_reset_password')]
    public function request(Request $request, UserRepository $userRepository, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ResetPasswordRequestFormType::class);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $email = $form->get('email')->getData();
            $user = $userRepository->findOneBy(['email' => $email]);

            if ($user) {
                // ✅ Generate a 6-digit reset code
                $resetCode = random_int(100000, 999999);

                // ✅ Store the reset token
                $token = new PasswordResetToken();
                $token->setEmail($email);
                $token->setResetCode($resetCode);
                $token->setExpiresAt(new \DateTime('+10 minutes'));

                $entityManager->persist($token);
                $entityManager->flush();

                // ✅ Manually configure SMTP transport for Gmail
                $dsn = 'smtp://mailpisymfony@gmail.com:yyatxiaanrqasuag@smtp.gmail.com:587?encryption=tls&auth_mode=login';
                
                // Create transport manually
                $transport = Transport::fromDsn($dsn);
                $mailer = new \Symfony\Component\Mailer\Mailer($transport);

                // ✅ Create email content
                $emailMessage = (new Email())
    ->from('mailpisymfony@gmail.com') // Sender email
    ->to($user->getEmail()) // Recipient email
    ->subject('Password Reset Code')
    ->html($this->renderView('reset_password/email_reset_code.html.twig', [
        'reset_code' => $resetCode, // ✅ Fix: Passing the reset code correctly
    ]));

// ✅ Send the email
$mailer->send($emailMessage);


                // ✅ Redirect to verify code page
                return $this->redirectToRoute('app_verify_reset_code', ['email' => $email]);
            }

            $this->addFlash('error', 'No account found for this email.');
        }

        return $this->render('reset_password/request_password_reset.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/verify-reset-code/{email}', name: 'app_verify_reset_code')]
public function verifyCode(Request $request, string $email, EntityManagerInterface $entityManager): Response
{
    $form = $this->createForm(VerifyResetCodeType::class);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $code = $form->get('reset_code')->getData();
        $token = $entityManager->getRepository(PasswordResetToken::class)->findOneBy(['email' => $email, 'resetCode' => $code]);

        if ($token && $token->getExpiresAt() > new \DateTime()) {
            return $this->redirectToRoute('app_reset_password_form', ['email' => $email]);
        }

        $this->addFlash('error', 'Invalid or expired code.');
    }

    return $this->render('reset_password/verify_reset_code.html.twig', [
        'form' => $form->createView(),
    ]);
}

}
