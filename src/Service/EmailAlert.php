<?php
namespace App\Service;

use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;

class EmailAlert
{
    private $mailer;

    public function __construct(MailerInterface $mailer)
    {
        $this->mailer = $mailer;
    }

    public function sendEventAlert($userEmail, $eventName)
    {
        // Créez l'e-mail d'alerte
        $email = (new Email())
            ->from('your_email@gmail.com')  // Votre adresse Gmail
            ->to($userEmail)
            ->subject('Nouvel Événement ajouté : ' . $eventName)
            ->text("Bonjour, un nouvel événement intitulé '$eventName' a été ajouté à notre plateforme. Ne manquez pas l'occasion!");

        // Envoyez l'e-mail
        $this->mailer->send($email);
    }
}

    