<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class MainController extends AbstractController
{
    #[Route('/', name: 'home')]
    public function index(): Response
    {
      //return $this->render('base.html.twig'); // S'assurer que ce fichier existe
         return $this->render('back.html.twig'); // S'assurer que ce fichier existe
    } 
}
