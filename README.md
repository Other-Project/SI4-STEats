# SopiaTech Eats-Team-C-24-25

## Team members and roles

* FALCOZ Alban : SA (Software Architect)
* GALLI Evan : QA (Quality Assurance)
* GRIPARI Alexandre : Ops (Operations)
* LASSAUNIERE Théo : PO (Product Owner)

## User stories

**[US] Make an order inside a group order #26** :

* **As a** Client
* **I want** to be able to make a order while being in a group order
* **So that** i can enjoy meals with my friends

## How to install and run the project

Requirements :

* Java 21
* Maven

```sh
git clone https://github.com/PNS-Conception/STE-24-25--teamc.git
cd STE-24-25--teamc
mvn clean test
```

## Project structure

The project is divided into 5 different packages :

* **order :** The classes related to the order feature. The SingleOrder class represents a single order made by
  a user while the GroupOrder class represents a group order, which contains multiple SingleOrder. They both inherit
  from the Order and Saleable interfaces, and every order has an Address and a Status.
* **restaurant :** The classes related to the restaurant feature. It contains the class MenuItem, which represents a
  single item on the menu of a Restaurant. Schedules represents the time when users can take order from a restaurant.
* **user :** The classes that help to represent all the users. The main classes are Role, which represents the
  different users of our platform, and User, which has a name, an userId and a role.
* **discounts :** The classes related to the discounts feature. The Discount class represents a discount that can be
  applied to an order. It has options, criteria and discounts, and it's build with a DiscountBuilder.
* **payment :** The classes related to the payment feature. The PaymentSystem should call an external payment system to validate the payment.

## .github

1. Contient sous workflows/maven.yml, une version d'un fichier d'actions qui est déclenché dès que vous poussez du code.
   Sur cette version initiale seule un test Junit5 est déclenché pour vérifier que tout fonctionne.

2. Contient sous ISSUE_TEMPLATE, les modèles pour les issues user_story et bug. Vous pouvez le compléter à votre guise.

## src

- pom.xml :
    - Cucumber 7 et JUnit 5
    - JDK 21
    - Etc.  
      Ce pom.xml sera mis à jour avec la démonstration qui vous sera donnée ultérieurement.

Lorsque vous passerez en développement, les codes donnés ici peuvent être éliminés.

Nous les laissons cependant pour votre permettre de vérifier que vous n'avez pas de problème d'intégration continue.


<!-- ## Ce que fait votre projet


### Principales User stories
Vous mettez en évidence les principales user stories de votre projet.
Chaque user story doit être décrite par 
   - son identifiant en tant que issue github (#), 
   - sa forme classique (As a… I want to… In order to…) (pour faciliter la lecture)
   - Le nom du fichier feature Cucumber et le nom des scénarios qui servent de tests d’acceptation pour la story.
   Les contenus détaillés sont dans l'issue elle-même. -->
   

   
