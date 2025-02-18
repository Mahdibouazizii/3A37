<?php

namespace App\Form;

use App\Entity\Event;
use App\Entity\Participation;
use App\Entity\User;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ParticipationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('age')
            ->add('nbrplace')
            // Update the statut field to use ChoiceType
            ->add('statut', ChoiceType::class, [
                'choices' => [
                    'Standard' => 'standard',
                    'Premium' => 'premium',
                ],
                'expanded' => false,  // Set to true if you prefer radio buttons
                'multiple' => false, // Only one choice can be selected
                'label' => 'Statut', // Optional: change the label if needed
                'required' => false, // Make it optional
            ])
            ->add('idevenement', EntityType::class, [
                'class' => Event::class,
                'choice_label' => 'id',
            ])
            ->add('iduser', EntityType::class, [
                'class' => User::class, // Make sure to use the correct namespace for User
                'choice_label' => 'id',
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Participation::class,
        ]);
    }
}
