<?php

namespace App\Entity;

use App\Repository\ChallengeRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ChallengeRepository::class)]
class Challenge
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank]
    private ?string $name = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank]
    private ?string $description = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    #[Assert\NotNull]
    private ?\DateTimeInterface $dateStart = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank]
    private ?string $type = null;

    #[ORM\Column(name: "image", length: 255, nullable: true)]
    private ?string $image = null;

    #[Assert\Image(
        maxSize: "2M",
        mimeTypes: ["image/jpeg", "image/png"],
        mimeTypesMessage: "Veuillez télécharger une image valide (JPG ou PNG)"
    )]
    private ?UploadedFile $imageFile = null;

    #[ORM\OneToMany(targetEntity: Badge::class, mappedBy: 'challenge')]
    private Collection $badges;

    public function __construct()
    {
        $this->badges = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getName(): ?string
    {
        return $this->name;
    }

    public function setName(string $name): static
    {
        $this->name = $name;
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

    public function getDateStart(): ?\DateTimeInterface
    {
        return $this->dateStart;
    }

    public function setDateStart(\DateTimeInterface $dateStart): static
    {
        $this->dateStart = $dateStart;
        return $this;
    }

    public function getType(): ?string
    {
        return $this->type;
    }

    public function setType(string $type): static
    {
        $this->type = $type;
        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): static
    {
        $this->image = $image;
        return $this;
    }

    public function getImageFile(): ?UploadedFile
    {
        return $this->imageFile;
    }

    public function setImageFile(?UploadedFile $imageFile): static
    {
        $this->imageFile = $imageFile;

        // Réinitialiser le nom de l'image si une nouvelle est ajoutée
        if ($imageFile) {
            $this->image = null;
        }

        return $this;
    }

    public function uploadImage(string $uploadDir): void
    {
        if ($this->getImageFile() === null) {
            return;
        }

        $filename = uniqid() . '.' . $this->getImageFile()->guessExtension();
        $this->getImageFile()->move($uploadDir, $filename);

        $this->setImage($filename);
    }

    public function getBadges(): Collection
    {
        return $this->badges;
    }

    public function addBadge(Badge $badge): static
    {
        if (!$this->badges->contains($badge)) {
            $this->badges->add($badge);
            $badge->setChallenge($this);
        }

        return $this;
    }

    public function removeBadge(Badge $badge): static
    {
        if ($this->badges->removeElement($badge)) {
            if ($badge->getChallenge() === $this) {
                $badge->setChallenge(null);
            }
        }

        return $this;
    }
}
