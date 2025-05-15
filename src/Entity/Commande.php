<?php
<<<<<<< HEAD
// src/Entity/Commande.php
=======

>>>>>>> 7ef3b12 (Initial commit with README.md)
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
<<<<<<< HEAD
    #[ORM\JoinColumn(nullable: false)]
    private ?User $user = null;

    #[ORM\Column(type: 'datetime')]
    private \DateTime $date;
=======
    #[ORM\JoinColumn(name: "id_user", referencedColumnName: "id", nullable: false)]
    private ?User $user = null;

    #[ORM\Column(name: "created_at", type: 'datetime')]
    private \DateTime $createdAt;
>>>>>>> 7ef3b12 (Initial commit with README.md)

    #[ORM\OneToMany(mappedBy: 'commande', targetEntity: CommandeProduit::class, cascade: ['persist', 'remove'])]
    private Collection $commandeProduits;

<<<<<<< HEAD
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
=======
    #[ORM\Column(type: 'string', length: 255)]
    private ?string $adresse = null;

    #[ORM\Column(type: 'string', length: 50)]
    private ?string $typePaiement = null;

    #[ORM\Column(type: 'string', length: 50)]
    private ?string $status = null;

    public function __construct()
    {
        $this->createdAt = new \DateTime();
>>>>>>> 7ef3b12 (Initial commit with README.md)
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

<<<<<<< HEAD
    public function getDate(): \DateTime
    {
        return $this->date;
=======
    public function getCreatedAt(): \DateTime
    {
        return $this->createdAt;
    }

    public function setCreatedAt(\DateTime $createdAt): self
    {
        $this->createdAt = $createdAt;
        return $this;
>>>>>>> 7ef3b12 (Initial commit with README.md)
    }

    public function getCommandeProduits(): Collection
    {
        return $this->commandeProduits;
    }
<<<<<<< HEAD
=======

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

        if ($typePaiement === 'Paiement à la livraison') {
            $this->status = 'Pending';
        } elseif ($typePaiement === 'Carte Bancaire') {
            $this->status = 'Validated';
        }

        return $this;
    }

    public function getStatus(): ?string
    {
        return $this->status;
    }

    public function setStatus(string $status): self
    {
        $this->status = $status;
        return $this;
    }
>>>>>>> 7ef3b12 (Initial commit with README.md)
}
