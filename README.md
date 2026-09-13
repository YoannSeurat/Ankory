# Ankory

---

## Idées

### Système de Commande et Suivi de Repas (Style Mini-Deliveroo)

* **Le concept :** Gestion du catalogue de restaurants, prise de commande par le client et mise à jour du statut par le restaurateur (En préparation -> En livraison -> Livré).
* **Architecture & Endpoints REST :**
* `GET /restaurants/{id}/menu` : Afficher la carte d'un restaurant.
* `POST /orders` : Valider un panier et créer une commande.
* `PATCH /orders/{id}/status` : Changer l'état de la commande.


* **Base de données :** NoSQL (MongoDB) pour stocker les menus variés et dynamiques des restaurants, ou SQL.


* **Bonus Client :** Une petite interface web (React/Angular) ou mobile (Android) pour l'affichage du menu client.



---

### Plateforme de Revente de Billets de Concerts / Événements

* **Le concept :** Un service de bourse d'échange entre particuliers permettant de mettre en vente un billet et de l'acheter en toute sécurité.
* **Architecture & Endpoints REST/RPC :**
* `POST /tickets` : Publier un billet avec son prix et justificatif.
* `POST /tickets/{id}/buy` : Procéder à l'achat du billet.


* **Base de données :** SQL pour les transactions financières et la gestion d'inventaire.


* **Gestion des exceptions :** `400 Bad Request` si le prix de revente dépasse la valeur faciale autorisée, `422 Unprocessable Entity` si le billet est déjà vendu.

---

### Structure recommandée pour le projet (quel que soit le sujet)

Pour répondre aux consignes de structure et de qualité du projet :

```text
mon-projet-git/
├── backend-service/           # Le projet principal (Spring Boot / Express / Go / etc.)[cite: 2]
│   ├── src/
│   │   ├── controllers/      # Couche Web (API REST/gRPC)[cite: 2]
│   │   ├── services/         # Couche Métier / Inversion of Control[cite: 2]
│   │   ├── entities/         # Couche Données / Modèles DB[cite: 2]
│   │   ├── exceptions/       # Exception Handler global[cite: 2]
│   │   └── config/           # IoC / Dependency Injection setup[cite: 2]
└── frontend-client/           # (Optionnel) Application React/Angular/Android[cite: 1, 2]

```
