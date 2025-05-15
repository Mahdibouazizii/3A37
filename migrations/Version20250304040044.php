<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250304040044 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE participation_challenge (id INT AUTO_INCREMENT NOT NULL, iduser_id INT NOT NULL, idbadge_id INT NOT NULL, INDEX IDX_B98EA73A786A81FB (iduser_id), INDEX IDX_B98EA73A5D778AF1 (idbadge_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE participation_challenge ADD CONSTRAINT FK_B98EA73A786A81FB FOREIGN KEY (iduser_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE participation_challenge ADD CONSTRAINT FK_B98EA73A5D778AF1 FOREIGN KEY (idbadge_id) REFERENCES badge (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE participation_challenge DROP FOREIGN KEY FK_B98EA73A786A81FB');
        $this->addSql('ALTER TABLE participation_challenge DROP FOREIGN KEY FK_B98EA73A5D778AF1');
        $this->addSql('DROP TABLE participation_challenge');
    }
}
