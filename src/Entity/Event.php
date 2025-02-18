<?php

namespace App\Entity;

use App\Repository\EventRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: EventRepository::class)]
class Event
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[Assert\NotBlank(message: "Le nom de l'événement est obligatoire")]
    #[Assert\Length(min: 3, minMessage: "Le nom doit contenir au moins 3 caractères")]
    #[ORM\Column(length: 255)]
    private ?string $nom = null;

    #[Assert\NotBlank(message: "La description est obligatoire")]
    #[Assert\Length(min: 10, minMessage: "La description doit contenir au moins 10 caractères")]
    #[ORM\Column(length: 255)]
    private ?string $description = null;

    #[Assert\NotBlank(message: "L'heure de début est obligatoire")]
    #[Assert\Type(type: "\DateTimeInterface", message: "Format de date invalide")]
    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $heur_debut = null;

    #[Assert\NotBlank(message: "L'heure de fin est obligatoire")]
    #[Assert\Type(type: "\DateTimeInterface", message: "Format de date invalide")]
    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $heur_fin = null;

    #[ORM\Column(length: 255)]
    private ?string $image = null;
   
    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;
        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): static
    {
        $this->description = $description;
        return $this;
    }

    public function getHeurDebut(): ?\DateTimeInterface
    {
        return $this->heur_debut;
    }

    public function setHeurDebut(\DateTimeInterface $heur_debut): static
    {
        $this->heur_debut = $heur_debut;
        return $this;
    }

    public function getHeurFin(): ?\DateTimeInterface
    {
        return $this->heur_fin;
    }

    public function setHeurFin(\DateTimeInterface $heur_fin): static
    {
        $this->heur_fin = $heur_fin;
        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(string $image): static
    {
        $this->image = $image;
        return $this;
    }
}
