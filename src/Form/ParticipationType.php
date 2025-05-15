<?php

namespace App\Form;

use App\Entity\Event;
use App\Entity\Participation;
use App\Entity\User;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\TextType;



class ParticipationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('age', IntegerType::class, [
                'constraints' => [
                    new Assert\NotNull(['message' => "L'âge est requis."]),
                    new Assert\GreaterThanOrEqual([
                        'value' => 18,
                        'message' => "L'âge doit être supérieur ou égal à 18 ans."
                    ]),
                ],
            ])
            ->add('nbrplace', IntegerType::class, [
                'constraints' => [
                    new Assert\NotNull(['message' => "Le nombre de places est requis."]),
                    new Assert\GreaterThanOrEqual([
                        'value' => 1,
                        'message' => "Le nombre de places doit être supérieur ou égal à 1."
                    ]),
                ],
            ])
            ->add('statut', ChoiceType::class, [
                'choices' => [
                    'Standard' => 'standard',
                    'Premium' => 'premium',
                ],
                'expanded' => false,
                'multiple' => false,
                'label' => 'Statut',
                'required' => true, 
                'placeholder' => 'Sélectionnez un statut', 
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
