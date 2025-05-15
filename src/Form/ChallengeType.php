<?php

namespace App\Form;

use App\Entity\Challenge;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Validator\Constraints as Assert;

class ChallengeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('name', TextType::class, [
                'label' => 'Nom du challenge',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('description', TextType::class, [
                'label' => 'Description du challenge',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('date_start', DateType::class, [
                'widget' => 'single_text',
                'label' => 'Date de début',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('type', TextType::class, [
                'label' => 'Type de challenge',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('imageFile', FileType::class, [
                'label' => 'Image (JPEG, PNG)',
                'required' => false,
                'mapped' => false, // Since it's not mapped directly to an entity property
                'attr' => ['class' => 'form-control'],
                'constraints' => [
                    new Assert\Image([
                        'maxSize' => '5M',
                        'mimeTypes' => ['image/jpeg', 'image/png'],
                        'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPEG ou PNG).',
                    ]),
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Challenge::class,
        ]);
    }
}
