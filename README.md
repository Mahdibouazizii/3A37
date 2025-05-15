# GreenTech – Symfony Application (Backend)

This is the backend component of the GreenTech platform. It handles business logic, database operations and admin features for managing ecological electronic product sales.

## 📦 Tech Stack

- PHP 8.1+
- Symfony 6.x
- Doctrine ORM
- MySQL (Database: `projetpi2`)
- Twig (for any admin frontend)


## 🗄️ Key Entities
- `user` – Authenticated users with roles (admin, user)
- `produit` – Electronic products for sale
- `commande`, `commande_produit` – Orders and order details
- `facture` – Billing records
- `feedback`, `comments`, `posts` – Community interactions
- `event`, `participation`, `challenge`, `participations` – Campaigns and eco-events
- `badge` – Reward system
- `reclamation` – Customer support tickets

## ⚙️ Setup Instructions

### 1. Clone the repo
```bash
git clone https://github.com/Mahdibouazizii/3A37.git
cd greentech-symfony
```

### 2. Install dependencies
```bash
composer install
```

### 3. Set up environment
Copy `.env` and update DB credentials:
```env
DATABASE_URL="mysql://root:password@127.0.0.1:3306/projetpi2"
```

### 4. Run migrations (optional if DB already created)
```bash
php bin/console doctrine:migrations:migrate
```

### 5. Serve the app
```bash
symfony server:start
```


## 🔐 Admin Access
Admins can:
- Add/edit/delete products
- View/manage users, commandes, factures, feedback
- Moderate posts and challenges

---

## 🧩 Notes
- Symfony does not serve frontend for JavaFX
- JavaFX app connects directly to the same DB for client features

