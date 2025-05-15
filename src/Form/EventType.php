<?php

namespace App\Form;

use App\Entity\Event;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\DateTimeType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Validator\Constraints as Assert;

class EventType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', TextType::class, [
                'label' => 'Event Name',
                'attr' => [
                    'placeholder' => 'Enter the event name',
                    'class' => 'form-control',
                ],
                'constraints' => [
                    new Assert\NotBlank([
                        'message' => 'The event name cannot be empty.',
                    ]),
                    new Assert\Length([
                        'min' => 3,
                        'minMessage' => 'The event name must be at least {{ limit }} characters long.',
                    ]),
                ],
            ])
            ->add('description', TextareaType::class, [
                'label' => 'Description',
                'attr' => [
                    'placeholder' => 'Describe the event',
                    'class' => 'form-control',
                    'rows' => 5,
                ],
                'constraints' => [
                    new Assert\NotBlank([
                        'message' => 'The description cannot be empty.',
                    ]),
                    new Assert\Length([
                        'max' => 500,
                        'maxMessage' => 'The description cannot exceed {{ limit }} characters.',
                    ]),
                ],
            ])
            ->add('nbrplacetottale', IntegerType::class, [
                'label' => 'Nombre de places',
                'attr' => [
                    'placeholder' => 'Entrer le nombre total de places',
                    'class' => 'form-control',
                ],
                'constraints' => [
                    new Assert\NotBlank([
                        'message' => 'Le nombre de places est requis.',
                    ]),
                    new Assert\Positive([
                        'message' => 'Le nombre de places doit être supérieur à 0.',
                    ]),
                ],
            ])
            ->add('heur_debut', DateTimeType::class, [
                'label' => 'Start Date and Time',
                'widget' => 'single_text',
                'attr' => [
                    'class' => 'form-control',
                ],
                'constraints' => [
                    new Assert\NotNull([
                        'message' => 'The start date and time cannot be empty.',
                    ]),
                    new Assert\GreaterThanOrEqual([
                        'value' => 'today',
                        'message' => 'The start date and time must be in the future.',
                    ]),
                ],
            ])
            ->add('heur_fin', DateTimeType::class, [
                'label' => 'End Date and Time',
                'widget' => 'single_text',
                'attr' => [
                    'class' => 'form-control',
                ],
                'constraints' => [
                    new Assert\NotNull([
                        'message' => 'The end date and time cannot be empty.',
                    ]),
                    new Assert\GreaterThan([
                        'propertyPath' => 'parent.all[heur_debut].data',
                        'message' => 'The end date and time must be after the start date and time.',
                    ]),
                ],
            ])
            ->add('image', FileType::class, [
                'label' => 'Event Image',
                'required' => false,
                'mapped' => false,
                'attr' => [
                    'class' => 'form-control',
                ],
                'constraints' => [
                    new Assert\Image([
                        'maxSize' => '5M',
                        'mimeTypesMessage' => 'Please upload a valid image file.',
                    ]),
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Event::class,
        ]);
    }
}
