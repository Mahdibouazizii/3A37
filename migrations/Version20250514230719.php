<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250514230719 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE commande_produit (id INT AUTO_INCREMENT NOT NULL, commande_id INT NOT NULL, produit_id INT NOT NULL, quantity INT NOT NULL, INDEX IDX_DF1E9E8782EA2E54 (commande_id), INDEX IDX_DF1E9E87F347EFB (produit_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('CREATE TABLE password_reset_token (id INT AUTO_INCREMENT NOT NULL, email VARCHAR(180) NOT NULL, reset_code VARCHAR(6) NOT NULL, expires_at DATETIME NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE commande_produit ADD CONSTRAINT FK_DF1E9E8782EA2E54 FOREIGN KEY (commande_id) REFERENCES commande (id)');
        $this->addSql('ALTER TABLE commande_produit ADD CONSTRAINT FK_DF1E9E87F347EFB FOREIGN KEY (produit_id) REFERENCES produit (id)');
        $this->addSql('ALTER TABLE facture DROP FOREIGN KEY facture_ibfk_1');
        $this->addSql('ALTER TABLE participations DROP FOREIGN KEY participations_ibfk_1');
        $this->addSql('ALTER TABLE reclamation DROP FOREIGN KEY reclamation_ibfk_1');
        $this->addSql('DROP TABLE comments');
        $this->addSql('DROP TABLE facture');
        $this->addSql('DROP TABLE participations');
        $this->addSql('DROP TABLE posts');
        $this->addSql('DROP TABLE reclamation');
        $this->addSql('ALTER TABLE badge ADD challenge_id INT DEFAULT NULL');
        $this->addSql('ALTER TABLE badge ADD CONSTRAINT FK_FEF0481D98A21AC6 FOREIGN KEY (challenge_id) REFERENCES challenge (id)');
        $this->addSql('CREATE INDEX IDX_FEF0481D98A21AC6 ON badge (challenge_id)');
        $this->addSql('ALTER TABLE challenge ADD type VARCHAR(255) NOT NULL, ADD image_path VARCHAR(255) DEFAULT NULL, DROP date_end, DROP location, DROP image, CHANGE date_start date_start DATETIME NOT NULL');
        $this->addSql('ALTER TABLE commande DROP FOREIGN KEY fk_user');
        $this->addSql('DROP INDEX fk_user ON commande');
        $this->addSql('ALTER TABLE commande ADD user_id INT NOT NULL, DROP id_user, DROP total, DROP details, CHANGE created_at created_at DATETIME NOT NULL, CHANGE adresse adresse VARCHAR(255) NOT NULL, CHANGE type_paiement type_paiement VARCHAR(50) NOT NULL, CHANGE status status VARCHAR(50) NOT NULL');
        $this->addSql('ALTER TABLE commande ADD CONSTRAINT FK_6EEAA67DA76ED395 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('CREATE INDEX IDX_6EEAA67DA76ED395 ON commande (user_id)');
        $this->addSql('ALTER TABLE event DROP nbrplacetottale');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY feedback_ibfk_1');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY feedback_ibfk_2');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY feedback_ibfk_1');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY feedback_ibfk_2');
        $this->addSql('ALTER TABLE feedback ADD created_at DATETIME NOT NULL, CHANGE user_id user_id INT DEFAULT NULL, CHANGE commentaire commentaire LONGTEXT NOT NULL');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT FK_D2294458F347EFB FOREIGN KEY (produit_id) REFERENCES produit (id)');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT FK_D2294458A76ED395 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('DROP INDEX produit_id ON feedback');
        $this->addSql('CREATE INDEX IDX_D2294458F347EFB ON feedback (produit_id)');
        $this->addSql('DROP INDEX user_id ON feedback');
        $this->addSql('CREATE INDEX IDX_D2294458A76ED395 ON feedback (user_id)');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT feedback_ibfk_1 FOREIGN KEY (produit_id) REFERENCES produit (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT feedback_ibfk_2 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY fk_participation_user');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY fk_participation_event');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY fk_participation_user');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY fk_participation_event');
        $this->addSql('ALTER TABLE participation CHANGE idevenement_id idevenement_id INT NOT NULL, CHANGE iduser_id iduser_id INT NOT NULL, CHANGE age age INT NOT NULL, CHANGE nbrplace nbrplace INT NOT NULL, CHANGE statut statut VARCHAR(20) NOT NULL');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT FK_AB55E24F497F501A FOREIGN KEY (idevenement_id) REFERENCES event (id)');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT FK_AB55E24F786A81FB FOREIGN KEY (iduser_id) REFERENCES user (id)');
        $this->addSql('DROP INDEX idevenement_id ON participation');
        $this->addSql('CREATE INDEX IDX_AB55E24F497F501A ON participation (idevenement_id)');
        $this->addSql('DROP INDEX iduser_id ON participation');
        $this->addSql('CREATE INDEX IDX_AB55E24F786A81FB ON participation (iduser_id)');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT fk_participation_user FOREIGN KEY (iduser_id) REFERENCES user (id) ON UPDATE CASCADE ON DELETE SET NULL');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT fk_participation_event FOREIGN KEY (idevenement_id) REFERENCES event (id) ON UPDATE CASCADE ON DELETE SET NULL');
        $this->addSql('ALTER TABLE produit CHANGE description description LONGTEXT NOT NULL, CHANGE prix prix NUMERIC(10, 2) NOT NULL, CHANGE poids poids NUMERIC(10, 2) NOT NULL, CHANGE promo_percentage promo_percentage INT DEFAULT NULL');
        $this->addSql('ALTER TABLE user CHANGE email email VARCHAR(180) NOT NULL, CHANGE adresse adresse VARCHAR(255) NOT NULL, CHANGE roles roles VARCHAR(20) NOT NULL, CHANGE is_banned is_banned TINYINT(1) DEFAULT 0 NOT NULL');
        $this->addSql('DROP INDEX email ON user');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_8D93D649E7927C74 ON user (email)');
        $this->addSql('ALTER TABLE messenger_messages CHANGE created_at created_at DATETIME NOT NULL, CHANGE available_at available_at DATETIME NOT NULL, CHANGE delivered_at delivered_at DATETIME DEFAULT NULL');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE comments (comment_id INT AUTO_INCREMENT NOT NULL, author VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, content TEXT CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, date VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, likes INT NOT NULL, dislikes INT NOT NULL, PRIMARY KEY(comment_id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE facture (id INT AUTO_INCREMENT NOT NULL, id_commande INT NOT NULL, total DOUBLE PRECISION NOT NULL, details TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, id_user INT DEFAULT NULL, INDEX id_commande (id_commande), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE participations (id INT AUTO_INCREMENT NOT NULL, challenge_id INT NOT NULL, participation_date_time DATETIME NOT NULL, score DOUBLE PRECISION NOT NULL, submission_details TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, INDEX challenge_id (challenge_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE posts (post_id INT AUTO_INCREMENT NOT NULL, caption TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, image_url TEXT CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, location TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, post_type TEXT CHARACTER SET utf8mb4 DEFAULT \'image\' COLLATE `utf8mb4_general_ci`, aspect_ratio DOUBLE PRECISION DEFAULT NULL, post_like_count INT DEFAULT 0, PRIMARY KEY(post_id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE reclamation (id INT AUTO_INCREMENT NOT NULL, user_id INT NOT NULL, sujet VARCHAR(255) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, description TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, INDEX user_id (user_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('ALTER TABLE facture ADD CONSTRAINT facture_ibfk_1 FOREIGN KEY (id_commande) REFERENCES commande (id)');
        $this->addSql('ALTER TABLE participations ADD CONSTRAINT participations_ibfk_1 FOREIGN KEY (challenge_id) REFERENCES challenge (id)');
        $this->addSql('ALTER TABLE reclamation ADD CONSTRAINT reclamation_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE commande_produit DROP FOREIGN KEY FK_DF1E9E8782EA2E54');
        $this->addSql('ALTER TABLE commande_produit DROP FOREIGN KEY FK_DF1E9E87F347EFB');
        $this->addSql('DROP TABLE commande_produit');
        $this->addSql('DROP TABLE password_reset_token');
        $this->addSql('ALTER TABLE badge DROP FOREIGN KEY FK_FEF0481D98A21AC6');
        $this->addSql('DROP INDEX IDX_FEF0481D98A21AC6 ON badge');
        $this->addSql('ALTER TABLE badge DROP challenge_id');
        $this->addSql('ALTER TABLE challenge ADD date_end DATE NOT NULL, ADD image VARCHAR(255) NOT NULL, DROP image_path, CHANGE date_start date_start DATE NOT NULL, CHANGE type location VARCHAR(255) NOT NULL');
        $this->addSql('ALTER TABLE commande DROP FOREIGN KEY FK_6EEAA67DA76ED395');
        $this->addSql('DROP INDEX IDX_6EEAA67DA76ED395 ON commande');
        $this->addSql('ALTER TABLE commande ADD id_user INT DEFAULT NULL, ADD total DOUBLE PRECISION NOT NULL, ADD details TEXT DEFAULT NULL, DROP user_id, CHANGE created_at created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, CHANGE adresse adresse VARCHAR(255) DEFAULT NULL, CHANGE type_paiement type_paiement VARCHAR(100) DEFAULT NULL, CHANGE status status VARCHAR(100) DEFAULT NULL');
        $this->addSql('ALTER TABLE commande ADD CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES user (id)');
        $this->addSql('CREATE INDEX fk_user ON commande (id_user)');
        $this->addSql('ALTER TABLE event ADD nbrplacetottale INT DEFAULT 0 NOT NULL');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY FK_D2294458F347EFB');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY FK_D2294458A76ED395');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY FK_D2294458F347EFB');
        $this->addSql('ALTER TABLE feedback DROP FOREIGN KEY FK_D2294458A76ED395');
        $this->addSql('ALTER TABLE feedback DROP created_at, CHANGE user_id user_id INT NOT NULL, CHANGE commentaire commentaire TEXT NOT NULL');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT feedback_ibfk_1 FOREIGN KEY (produit_id) REFERENCES produit (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT feedback_ibfk_2 FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE');
        $this->addSql('DROP INDEX idx_d2294458f347efb ON feedback');
        $this->addSql('CREATE INDEX produit_id ON feedback (produit_id)');
        $this->addSql('DROP INDEX idx_d2294458a76ed395 ON feedback');
        $this->addSql('CREATE INDEX user_id ON feedback (user_id)');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT FK_D2294458F347EFB FOREIGN KEY (produit_id) REFERENCES produit (id)');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT FK_D2294458A76ED395 FOREIGN KEY (user_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE messenger_messages CHANGE created_at created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', CHANGE available_at available_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', CHANGE delivered_at delivered_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\'');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY FK_AB55E24F497F501A');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY FK_AB55E24F786A81FB');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY FK_AB55E24F497F501A');
        $this->addSql('ALTER TABLE participation DROP FOREIGN KEY FK_AB55E24F786A81FB');
        $this->addSql('ALTER TABLE participation CHANGE idevenement_id idevenement_id INT DEFAULT NULL, CHANGE iduser_id iduser_id INT DEFAULT NULL, CHANGE age age INT DEFAULT NULL, CHANGE nbrplace nbrplace INT DEFAULT NULL, CHANGE statut statut VARCHAR(20) DEFAULT NULL');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT fk_participation_user FOREIGN KEY (iduser_id) REFERENCES user (id) ON UPDATE CASCADE ON DELETE SET NULL');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT fk_participation_event FOREIGN KEY (idevenement_id) REFERENCES event (id) ON UPDATE CASCADE ON DELETE SET NULL');
        $this->addSql('DROP INDEX idx_ab55e24f786a81fb ON participation');
        $this->addSql('CREATE INDEX iduser_id ON participation (iduser_id)');
        $this->addSql('DROP INDEX idx_ab55e24f497f501a ON participation');
        $this->addSql('CREATE INDEX idevenement_id ON participation (idevenement_id)');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT FK_AB55E24F497F501A FOREIGN KEY (idevenement_id) REFERENCES event (id)');
        $this->addSql('ALTER TABLE participation ADD CONSTRAINT FK_AB55E24F786A81FB FOREIGN KEY (iduser_id) REFERENCES user (id)');
        $this->addSql('ALTER TABLE produit CHANGE description description TEXT DEFAULT NULL, CHANGE prix prix DOUBLE PRECISION NOT NULL, CHANGE poids poids DOUBLE PRECISION DEFAULT NULL, CHANGE promo_percentage promo_percentage DOUBLE PRECISION DEFAULT NULL');
        $this->addSql('ALTER TABLE user CHANGE email email VARCHAR(255) NOT NULL, CHANGE adresse adresse VARCHAR(255) DEFAULT NULL, CHANGE roles roles VARCHAR(255) NOT NULL, CHANGE is_banned is_banned TINYINT(1) DEFAULT 0');
        $this->addSql('DROP INDEX uniq_8d93d649e7927c74 ON user');
        $this->addSql('CREATE UNIQUE INDEX email ON user (email)');
    }
}
