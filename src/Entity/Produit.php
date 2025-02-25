<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;

#[ORM\Entity]
class Produit
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: "integer")]
    private ?int $id = null;

    #[ORM\Column(type: "string", length: 255)]
    private ?string $nom = null;

    #[ORM\Column(type: "text")]
    private ?string $description = null;

    #[ORM\Column(type: "string", length: 255, nullable: true)]
    private ?string $image = null;

    #[ORM\Column(type: "decimal", precision: 10, scale: 2)]
    private ?float $prix = null;

    #[ORM\Column(type: "integer")]
    private ?int $stock = null;

    #[ORM\Column(type: "decimal", precision: 10, scale: 2)]
    private ?float $poids = null;

    #[ORM\Column(type: "integer", nullable: true)]
    private ?int $promoPercentage = null;

    #[ORM\OneToMany(mappedBy: "produit", targetEntity: Feedback::class, cascade: ["remove"], orphanRemoval: true)]
    private Collection|ArrayCollection $feedbacks;

    // Getters and Setters
    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): self
    {
        $this->nom = $nom;
        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): self
    {
        $this->description = $description;
        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): self
    {
        $this->image = $image;
        return $this;
    }

    public function getPrix(): ?float
    {
        return $this->prix;
    }

    public function setPrix(float $prix): self
    {
        $this->prix = $prix;
        return $this;
    }

    public function getStock(): ?int
    {
        return $this->stock;
    }

    public function setStock(int $stock): self
    {
        $this->stock = $stock;
        return $this;
    }

    public function getPoids(): ?float
    {
        return $this->poids;
    }

    public function setPoids(float $poids): self
    {
        $this->poids = $poids;
        return $this;
    }

    public function __construct()
    {
        $this->feedbacks = new ArrayCollection();
    }

    public function getFeedbacks(): Collection
    {
        return $this->feedbacks;
    }

    public function getPromoPercentage(): ?int
{
    return $this->promoPercentage;
}

public function setPromoPercentage(?int $promoPercentage): self
{
    $this->promoPercentage = $promoPercentage;
    return $this;
}

public function getDiscountedPrice(): ?float
{
    if ($this->promoPercentage && $this->promoPercentage > 0) {
        return $this->prix - ($this->prix * ($this->promoPercentage / 100));
    }
    return $this->prix;
}

}
