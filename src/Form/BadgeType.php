<?php
namespace App\Form;

use App\Entity\Badge;
use App\Entity\Challenge;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\File;

class BadgeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('name')
            ->add('description')
            ->add('challenge', EntityType::class, [
                'class' => Challenge::class,
                'choice_label' => 'id', // Assurez-vous que vous avez bien un champ "id" dans votre entité Challenge.
            ])
            ->add('imageFile', FileType::class, [
                'label' => 'Badge Image (JPEG, PNG, GIF files)',
                'mapped' => false, // Le champ imageFile ne sera pas mappé directement à l'entité Badge
                'required' => false, // Le champ est optionnel
                'constraints' => [
                    new File([
                        'maxSize' => '1024k', // Limite de taille à 1 Mo
                        'mimeTypes' => [
                            'image/jpeg',
                            'image/png',
                            'image/gif',
                        ],
                        'mimeTypesMessage' => 'Please upload a valid image (JPEG, PNG, or GIF).', // Message d'erreur
                    ])
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Badge::class, // La classe associée est Badge
        ]);
    }
}

