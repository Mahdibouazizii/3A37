<?php

namespace App\Entity;

use App\Repository\ParticipationRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ParticipationRepository::class)]
class Participation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private ?Event $idevenement = null;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private ?User $iduser = null;

    #[ORM\Column]
    #[Assert\NotNull(message: "L'âge est requis.")]
    #[Assert\GreaterThanOrEqual(value: 18, message: "L'âge doit être supérieur ou égal à 18 ans.")]
    private ?int $age = null;

    #[ORM\Column]
    #[Assert\NotNull(message: "Le nombre de places est requis.")]
    #[Assert\GreaterThanOrEqual(value: 1, message: "Le nombre de places doit être supérieur ou égal à 1.")]
    private ?int $nbrplace = null;

    #[ORM\Column(length: 20)]
#[Assert\NotBlank(message: "Le statut est requis.")]
#[Assert\Choice(choices: ['premium', 'standard'], message: "Le statut doit être 'premium' ou 'standard'.")]
private ?string $statut = null; // Initialiser à null pour forcer le choix


    public function getId(): ?int
    {
        return $this->id;
    }

    public function getIdevenement(): ?Event
    {
        return $this->idevenement;
    }

    public function setIdevenement(?Event $idevenement): static
    {
        $this->idevenement = $idevenement;
        return $this;
    }

    public function getIduser(): ?User
    {
        return $this->iduser;
    }

    public function setIduser(?User $iduser): static
    {
        $this->iduser = $iduser;
        return $this;
    }

    public function getAge(): ?int
    {
        return $this->age;
    }

    public function setAge(int $age): static
    {
        $this->age = $age;
        return $this;
    }

    public function getNbrplace(): ?int
    {
        return $this->nbrplace;
    }

    public function setNbrplace(int $nbrplace): static
    {
        $this->nbrplace = $nbrplace;
        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;
        return $this;
    }
}
