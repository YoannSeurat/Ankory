# Ankory

Ankory est une application de commande et de suivi de repas, inspiree d'un mini service de livraison. 

Elle permet de consulter le menu d'un restaurant, de composer un panier, de passer une commande et de suivre son avancement. 

Une vue restaurateur permet ensuite de faire progresser le statut de la commande.

## Architecture

Le depot contient deux parties :

```text
Ankory/
├── backend-service/       # API REST Spring Boot et persistance H2/JPA
│   ├── src/main/java/     # controleurs, services, entites, depots et DTO
│   ├── src/main/resources/
│   │   ├── config/data.csv # restaurants et plats initiaux
│   │   └── application.properties
│   └── src/test/java/     # tests de contexte et du cycle de commande
└── frontend-client/       # interface web statique HTML/CSS/JavaScript
```

## Lancer le projet

### Prerequis

- un JDK 26 installe et disponible dans `JAVA_HOME` ou dans le `PATH` ;
- Windows : utiliser `gradlew.bat` ; macOS/Linux : utiliser `./gradlew`.

### Demarrer l'application

Depuis la racine du depot :

```powershell
cd backend-service
.\gradlew.bat bootRun
```

Sur macOS/Linux :

```bash
cd backend-service
./gradlew bootRun
```

Une fois le demarrage termine, ouvrir [http://localhost:8080](http://localhost:8080) dans un navigateur. L'interface est servie par Spring Boot.

Les donnees de demonstration sont chargees automatiquement depuis `backend-service/src/main/resources/config/data.csv`. 

Si ce fichier est absent ou illisible, le backend utilise un petit jeu de donnees de secours.

### Console H2

La console H2 est disponible a [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

Avec la configuration actuelle, utiliser :

```text
JDBC URL : jdbc:h2:mem:ankorydb
User     : ankoryadmin
Password : laisser vide
```


## API REST

L'API est disponible sur `http://localhost:8080`.

| Methode | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/restaurants` | Liste les restaurants disponibles. |
| `GET` | `/restaurants/{id}/menu` | Retourne le menu d'un restaurant. |
| `POST` | `/orders` | Cree une commande. |
| `GET` | `/orders?restaurantId={id}` | Liste les commandes, eventuellement filtrees par restaurant. |
| `GET` | `/orders/{id}` | Retourne le detail et le statut d'une commande. |
| `PATCH` | `/orders/{id}/status` | Met a jour le statut d'une commande. |

### Creer une commande

```http
POST /orders
Content-Type: application/json

{
	"restaurantId": 1,
	"lines": [
		{ "menuItemId": 1, "quantity": 2 }
	]
}
```

Le total est recalcule cote backend a partir des prix enregistres dans le menu.

Une commande commence toujours avec le statut `IN_PREPARATION`.

### Mettre a jour un statut

```http
PATCH /orders/1/status
Content-Type: application/json

{ "status": "EN_LIVRAISON" }
```

Les statuts acceptes sont `IN_PREPARATION`, `EN_LIVRAISON` et `LIVRE`. Une
transition qui saute une etape est rejetee.
