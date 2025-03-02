<?php
// src/Form/VerifyResetCodeType.php
namespace App\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
use App\Form\VerifyResetCodeType;

class VerifyResetCodeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('reset_code', TextType::class, [
                'constraints' => [
                    new NotBlank(),
                    new Length(['min' => 6, 'max' => 6])
                ]
            ])
            ;
    }
}
