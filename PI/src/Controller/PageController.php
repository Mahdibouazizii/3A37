<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class PageController extends AbstractController
{
    #[Route('/', name: 'homepage')]
    public function home(): Response
    {
        return $this->render('page/index.html.twig');
    }

    #[Route('/about', name: 'about_page')]
    public function about(): Response
    {
        return $this->render('page/about.html.twig');
    }

    #[Route('/company', name: 'company_page')]
    public function company(): Response
    {
        return $this->render('page/company.html.twig');
    }

    #[Route('/furnitures', name: 'furnitures_page')]
    public function furnitures(): Response
    {
        return $this->render('page/furnitures.html.twig');
    }

    #[Route('/contact', name: 'contact_page')]
    public function contact(): Response
    {
        return $this->render('page/contact.html.twig');
    }
}
