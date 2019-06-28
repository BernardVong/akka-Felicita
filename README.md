# Scala project
Introduction to Scala with AKKA Framework 

# Team
<table>
    <tr>
      <td>Bernard VONG</td>
      <td><a href="https://github.com/BernardVong">Github</a></td>
    </tr>
    <tr>
      <td>Manitra RANAIVOHARISON</td>
      <td><a href="https://github.com/Harisonm">Gitlab</a></td>
    </tr>
</table>


# Todo
- [x] Scala case class, trait, future, etc...
- [x] AKKA Actors
- [x] AKKA Marshalling / Unmarshalling  
- [x] AKKA Routing 
- [x] Database management

# Quickstart

### Setup sbt
Pour pouvoir utiliser le projet, il faut integrer la librairie scalatest.
Ajoutez dans votre config sbt (~/.sbt/1.X/global.sbt) la ligne suivante : 
```
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
```

### Create database
```
bash utils/sh/db_create.sh
```

## All Routes helper - Postman

Import Postman's collection
```
PROJECT_PATH/src/main/scala/com/felicita/_utils/routes.postman_collection.json
```

And setup ENV variables
```
url as "http://localhost:8080"
```

## Project Routes helper 

Use the Postman Collection above and refer to the list of routes below

###OR

Use those routes below :

####Tips
○ Récupérer la liste de tous les donateurs (liste de users) :
```
GET http://localhost:8080/tips
```
○ Réaliser un don :
```
POST http://localhost:8080/tips
```

○ Annuler un don :
```
DELETE http://localhost:8080/tips/{{id_tip}}
```

○ Faire la somme de tous les dons : 
```
GET http://localhost:8080/tips/total
```

○ Faire la somme de tous les dons par utilisateur :
```
GET http://localhost:8080/tips/users/distinct
```


○ Faire la somme de tous les dons d’un utilisateur :
```
GET http://localhost:8080/tips/users/{{id_user}}/total
```

####Subscribers
○ Récupérer la liste de tous les abonnées (liste de users) :
```
GET http://localhost:8080/users/subscribers
```


####Giveaways
○ Créer un giveaway
```
POST http://localhost:8080/giveaways
```

○ S'inscrire à un giveaway pour un utilisateur
```
POST http://localhost:8080/giveaways/{{id_giveaway}}/participate
```

○ Tirage au sort du gagnant (aléatoirement pondéré par le montant donné par
l’utilisateur..., s’assurer que l’utilisateur n’est pas ban) :
```
POST http://localhost:8080/giveaways/{{id_giveaway}}/close
```

####Blacklist
○ Pouvoir blacklister un utilisateur (ne peut plus participer aux giveaway) :
```
PATCH http://localhost:8080/users/{{pseudo_user}}/blacklist
```
 
####Surveys
○ Créer un sondage (2 choix possibles à chaque fois) :
```
POST http://localhost:8080/surveys
```

○ Participer au sondage pour un utilisateur :
```
POST http://localhost:8080/surveys/{{id_survey}}
```
○ Avoir le résultat final du sondage :
```
GET http://localhost:8080/surveys/{{id_survey}}/result
```


