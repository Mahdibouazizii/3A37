<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;

#[ORM\Entity]
#[ORM\Table(name: "user")]
#[UniqueEntity(fields: ['email'], message: 'There is already an account with this email')]
class User implements UserInterface, PasswordAuthenticatedUserInterface
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: "integer")]
    private ?int $id = null;

    #[ORM\Column(type: "string", length: 255)]
    private ?string $nom = null;

    #[ORM\Column(type: "string", length: 255)]
    private ?string $prenom = null;

    #[ORM\Column(type: "string", length: 180, unique: true)]
    private ?string $email = null;

    #[ORM\Column(type: "string", length: 255)]
    private ?string $password = null;

    #[ORM\Column(type: "string", length: 255)]
    private ?string $adresse = null;

    #[ORM\Column(type: "string", length: 255, nullable: true)]
    private ?string $profilePicture = null;

<<<<<<< HEAD
    #[ORM\Column(type: "json")]
private array $roles = ['ROLE_USER'];

#[ORM\Column(type: "boolean", options: ["default" => false])]
private bool $isBanned = false;

public function isBanned(): bool
{
    return $this->isBanned;
}

public function setIsBanned(bool $isBanned): self
{
    $this->isBanned = $isBanned;
    return $this;
}


public function __construct()
{
    $this->roles = ['ROLE_USER']; // ✅ Default role is always set
}


=======
    // ⚠️ le rôle est maintenant une string simple, plus un json
    #[ORM\Column(type: "string", length: 20)]
    private string $roles = 'user';

    #[ORM\Column(type: "boolean", options: ["default" => false])]
    private bool $isBanned = false;

    public function __construct()
    {
        $this->roles = 'user'; // valeur par défaut
    }
>>>>>>> 7ef3b12 (Initial commit with README.md)

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

    public function getPrenom(): ?string
    {
        return $this->prenom;
    }

    public function setPrenom(string $prenom): self
    {
        $this->prenom = $prenom;
        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): self
    {
        $this->email = $email;
        return $this;
    }

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(string $password): self
    {
        $this->password = $password;
        return $this;
    }

    public function getAdresse(): ?string
    {
        return $this->adresse;
    }

    public function setAdresse(string $adresse): self
    {
        $this->adresse = $adresse;
        return $this;
    }

    public function getProfilePicture(): ?string
    {
        return $this->profilePicture;
    }

    public function setProfilePicture(?string $profilePicture): self
    {
        $this->profilePicture = $profilePicture;
        return $this;
    }

<<<<<<< HEAD
    public function getRoles(): array
    {
        return !empty($this->roles) ? array_unique($this->roles) : ['ROLE_USER'];
    }
    
    public function setRoles(?array $roles): self
    {
        $this->roles = $roles ?? ['ROLE_USER']; // ✅ Default to ['ROLE_USER'] if null
        return $this;
    }
    


 

=======
    // Symfony attend un tableau ['ROLE_USER'] ou ['ROLE_ADMIN']
    public function getRoles(): array
    {
        return match ($this->roles) {
            'admin' => ['ROLE_ADMIN'],
            default => ['ROLE_USER'],
        };
    }

    public function setRoles(array $roles): self
    {
        if (in_array('ROLE_ADMIN', $roles)) {
            $this->roles = 'admin';
        } else {
            $this->roles = 'user';
        }
        return $this;
    }

    public function getRawRole(): string
    {
        return $this->roles;
    }

    public function setRawRole(string $role): self
    {
        $this->roles = $role;
        return $this;
    }

    public function getDisplayRole(): string
    {
        return $this->roles;
    }

    public function isBanned(): bool
    {
        return $this->isBanned;
    }

    public function setIsBanned(bool $isBanned): self
    {
        $this->isBanned = $isBanned;
        return $this;
    }
>>>>>>> 7ef3b12 (Initial commit with README.md)

    public function eraseCredentials(): void {}

    public function getUserIdentifier(): string
    {
        return $this->email;
    }

    public function getSalt(): ?string
    {
<<<<<<< HEAD
        return null; // ✅ Not needed for modern password encoders
=======
        return null;
>>>>>>> 7ef3b12 (Initial commit with README.md)
    }

    public function getUsername(): string
    {
        return $this->email;
    }
}
