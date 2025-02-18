<?php

namespace App\Form;

use App\Entity\Panier;
use App\Entity\produit;
use App\Entity\utilisateur;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType; // Added SubmitType
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class PanierType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('total')
            ->add('datecreation', null, [
                'widget' => 'single_text'
            ])
            ->add('statut')
            ->add('utilisateur', EntityType::class, [
                'class' => utilisateur::class,
                'choice_label' => 'id', // Or a more descriptive field like 'username' or 'email'
            ])
            ->add('produits', EntityType::class, [
                'class' => produit::class,
                'choice_label' => 'nom', // Changed to 'nom' (or appropriate field) for product display
                'multiple' => true,
                'expanded' => true, // Added for checkboxes
            ])
            ->add('save', SubmitType::class, ['label' => 'Save']); // Added Save button
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Panier::class,
        ]);
    }
}