<?php

namespace App\Entity;

use App\Repository\ParticipationRepository;
use Doctrine\ORM\Mapping as ORM;

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
    private ?int $age = null;

    #[ORM\Column]
    private ?int $nbrplace = null;

    #[Assert\Choice(choices: ['premium', 'standard'], message: "Le statut doit être 'premium' ou 'standard'.")]
    #[ORM\Column(length: 20)]
    private ?string $statut = 'standard';

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

    public function getIduser(): ?user
    {
        return $this->iduser;
    }

    public function setIduser(?user $iduser): static
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
