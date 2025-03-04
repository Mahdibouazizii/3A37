<?php

namespace App\Security;

use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Security;
use Symfony\Component\Security\Http\Authenticator\AbstractLoginFormAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\CsrfTokenBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\RememberMeBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Credentials\PasswordCredentials;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Util\TargetPathTrait;
use Symfony\Component\Routing\RouterInterface;

class LoginAuthenticator extends AbstractLoginFormAuthenticator
{
    use TargetPathTrait;

    public const LOGIN_ROUTE = 'app_login';

    private RouterInterface $router;

    public function __construct(
        private UrlGeneratorInterface $urlGenerator,
        RouterInterface $router
    ) {
        $this->router = $router;
    }

    public function authenticate(Request $request): Passport
    {
        $email = $request->request->get('email', '');
        $request->getSession()->set(Security::LAST_USERNAME, $email);

        return new Passport(
            new UserBadge($email),
            new PasswordCredentials($request->request->get('password', '')),
            [
                // ✅ Check CSRF token
                new CsrfTokenBadge('authenticate', $request->request->get('_csrf_token')),
                // ✅ Allow Remember Me
                new RememberMeBadge(),
            ]
        );
    }

    /**
     * This method is called immediately after a successful authentication.
     * We will redirect based on the user's role or a previously requested URL.
     */
    public function onAuthenticationSuccess(
        Request $request,
        TokenInterface $token,
        string $firewallName
    ): ?Response {
        // 1️⃣ If the user was trying to access a protected page before logging in
        //    Symfony stores that URL, so let's redirect them there first.
        $targetUrl = $this->getTargetPath($request->getSession(), $firewallName);
        if ($targetUrl) {
            return new RedirectResponse($targetUrl);
        }

        // 2️⃣ Otherwise, redirect based on the user's role
        $user = $token->getUser();
        if (in_array('ROLE_ADMIN', $user->getRoles(), true)) {
            // Redirect Admins to Dashboard
            return new RedirectResponse($this->router->generate('admin_dashboard'));
        }

        // 3️⃣ Default: Redirect to Home for regular users
        return new RedirectResponse($this->router->generate('homepage'));
    }

    /**
     * Returns the login URL when authentication is needed.
     */
    protected function getLoginUrl(Request $request): string
    {
        return $this->urlGenerator->generate(self::LOGIN_ROUTE);
    }
}
