<?php
// src/Form/CommandeType.php
namespace App\Form;

use App\Entity\Commande;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class CommandeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('adresse', TextType::class, [
                'label' => 'Adresse de livraison',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('typePaiement', ChoiceType::class, [
                'label' => 'Type de paiement',
                'choices' => [
                    'Carte Bancaire' => 'Carte Bancaire',
                    'PayPal' => 'PayPal',
                    'Paiement à la livraison' => 'Paiement à la livraison',
                ],
                'expanded' => true, // Radio buttons
                'multiple' => false,
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Commande::class,
        ]);
    }
}
