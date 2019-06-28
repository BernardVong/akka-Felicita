# Scala project
Introduction to Scala with AKKA Framework 

# team
<table>
    <tr>
      <td>Bernard VONG</td>
      <td><a href="https://github.com/BernardVong">Github</a></td>
    </tr>
    <tr>
      <td>Manitra RANAIVOHARISON</td>
      <td><a href="https://github.com/Harisonm">Github</a></td>
    </tr>
</table>


# todo
- [x] Scala case class, trait, future, etc...
- [x] AKKA Actors
- [x] AKKA Marshalling / Unmarshalling  
- [x] AKKA Routing 
- [x] Database management

# Initiazing project

## Initiazing sbt
Pour pouvoir utiliser le projet, il faut integrer une librarie pour le scala-test.
ajouter dans votre config sbt -> ~/.sbt/1.X/global.sbt    X : la version de votre SBT
la ligne suivant : 
```
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
```

# Create database from bash
```
bash utils/sh/db_create.sh
```

## ROUTES
###Postman file
PROJECT_PATH/src/main/scala/com/felicita/_utils/routes.postman_collection.json

## PROJECT 

Chaque requêtes GET/POST/PUT/DELETE sont explicitées ci-dessous pour tout les cas demandées :

####Tips
○ Récupérer la liste de tous les donateurs (liste de users)
GET :
```
http://localhost:8080/tips
```
○ Réaliser un don
GET :
```

```

○ Annuler un don
GET
```

```

○ Faire la somme de tous les dons
GET 
```

```

○ Faire la somme de tous les dons par utilisateur
GET
```

```


○ Faire la somme de tous les dons d’un utilisateur
GET
```

```

####Subscribers
○ Récupérer la liste de tous les abonnées (liste de users)
GET
```
http://localhost:8080/users/subscribers
```


####Giveaways
○ Créer un giveaway
POST
```

```

○ S'inscrire à un giveaway pour un utilisateur
PUT
```

```

○ Tirage au sort du gagnant (aléatoirement pondéré par le montant donné par
l’utilisateur..., s’assurer que l’utilisateur n’est pas ban)
GET
```

```

####Blacklist
○ Pouvoir blacklister un utilisateur (ne peut plus participer aux giveaway)
PUT
```

```
 
####Surveys
○ Créer un sondage (2 choix possibles à chaque fois)
POST
```

```

○ Participer au sondage pour un utilisateur
POST
```

```
○ Avoir le résultat final du sondage
GET
```
http://localhost:8080/surveys
```
