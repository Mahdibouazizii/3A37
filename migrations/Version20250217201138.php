<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250217201138 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE participation (id INT AUTO_INCREMENT NOT NULL, idevenement_id INT NOT NULL, iduser_id INT NOT NULL, age INT NOT NULL, nbrplace INT NOT NULL, statut VARCHAR(20) NOT NULL, INDEX IDX_AB55E24F497F501A (idevenement_id), INDEX IDX_AB55E24F786A81FB (iduser_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT FK_AB55E24F497F501A FOREIGN KEY (idevenement_id) REFERENCES event (id)');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT FK_AB55E24F786A81FB FOREIGN KEY (iduser_id) REFERENCES user (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY FK_AB55E24F497F501A');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY FK_AB55E24F786A81FB');
        $this->addSql('DROP TABLE participation');
    }
}
