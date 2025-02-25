<?php
// src/Entity/Commande.php
namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use App\Repository\CommandeRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;

#[ORM\Entity(repositoryClass: CommandeRepository::class)]
class Commande
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private ?User $user = null;

    #[ORM\Column(type: 'datetime')]
    private \DateTime $date;

    #[ORM\OneToMany(mappedBy: 'commande', targetEntity: CommandeProduit::class, cascade: ['persist', 'remove'])]
    private Collection $commandeProduits;

    // src/Entity/Commande.php
#[ORM\Column(type: 'string', length: 255)]
private ?string $adresse = null;

#[ORM\Column(type: 'string', length: 50)]
private ?string $typePaiement = null;

public function getAdresse(): ?string
{
    return $this->adresse;
}

public function setAdresse(string $adresse): self
{
    $this->adresse = $adresse;
    return $this;
}

public function getTypePaiement(): ?string
{
    return $this->typePaiement;
}

public function setTypePaiement(string $typePaiement): self
{
    $this->typePaiement = $typePaiement;
    return $this;
}


    public function __construct()
    {
        $this->date = new \DateTime();
        $this->commandeProduits = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getUser(): ?User
    {
        return $this->user;
    }

    public function setUser(User $user): self
    {
        $this->user = $user;
        return $this;
    }

    public function getDate(): \DateTime
    {
        return $this->date;
    }

    public function getCommandeProduits(): Collection
    {
        return $this->commandeProduits;
    }
}
