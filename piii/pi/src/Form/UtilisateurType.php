<?php

namespace App\Form;

use App\Entity\Utilisateur;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class UtilisateurType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom')
            ->add('email')
            ->add('mot_de_passe')
            ->add('role')
            ->add('date_inscription', null, [
                'widget' => 'single_text'
            ])
            ->add('telephone')
            ->add('date_naissance', null, [
                'widget' => 'single_text'
            ])
            ->add('genre')
            ->add('photo_profil')
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}
