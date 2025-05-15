<?php
namespace App\Repository;

use App\Entity\Badge;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Badge>
 */
class BadgeRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Badge::class);
    }

    /**
     * @return Badge[] Returns an array of Badge objects
     */
    public function findByExampleField($value): array
    {
        return $this->createQueryBuilder('b')
            ->andWhere('b.exampleField = :val')
            ->setParameter('val', $value)
            ->orderBy('b.id', 'ASC')
            ->setMaxResults(10)
            ->getQuery()
            ->getResult();
    }

    public function findOneBySomeField($value): ?Badge
    {
        return $this->createQueryBuilder('b')
            ->andWhere('b.exampleField = :val')
            ->setParameter('val', $value)
            ->getQuery()
            ->getOneOrNullResult();
    }

    // Example method to find badges with a specific image path
    public function findByImagePath(string $imagePath): array
    {
        return $this->createQueryBuilder('b')
            ->andWhere('b.imagePath = :imagePath')
            ->setParameter('imagePath', $imagePath)
            ->getQuery()
            ->getResult();
    }
}