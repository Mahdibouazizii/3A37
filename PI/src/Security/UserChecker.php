<?php

namespace App\Security;

use Symfony\Component\Security\Core\User\UserCheckerInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\Exception\CustomUserMessageAccountStatusException;
use App\Entity\User;

class UserChecker implements UserCheckerInterface
{
    public function checkPreAuth(UserInterface $user)
    {
        // Only proceed if $user is our App\Entity\User
        if (!$user instanceof User) {
            return;
        }

        // If user is banned, throw an exception to block authentication
        if ($user->isBanned()) {
            throw new CustomUserMessageAccountStatusException(
                'Your account has been banned. Please contact support.'
            );
        }
    }

    public function checkPostAuth(UserInterface $user)
    {
        // Usually empty unless you need to do checks after login success
    }
}
