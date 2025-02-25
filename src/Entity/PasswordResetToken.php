<?php
// src/Entity/PasswordResetToken.php
namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity]
class PasswordResetToken
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: 'integer')]
    private ?int $id = null;

    #[ORM\Column(type: 'string', length: 180)]
    #[Assert\Email]
    private string $email;

    #[ORM\Column(type: 'string', length: 6)]
    private string $resetCode;

    #[ORM\Column(type: 'datetime')]
    private \DateTimeInterface $expiresAt;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getEmail(): string
    {
        return $this->email;
    }

    public function setEmail(string $email): self
    {
        $this->email = $email;
        return $this;
    }

    public function getResetCode(): string
    {
        return $this->resetCode;
    }

    public function setResetCode(string $resetCode): self
    {
        $this->resetCode = $resetCode;
        return $this;
    }

    public function getExpiresAt(): \DateTimeInterface
    {
        return $this->expiresAt;
    }

    public function setExpiresAt(\DateTimeInterface $expiresAt): self
    {
        $this->expiresAt = $expiresAt;
        return $this;
    }
}
