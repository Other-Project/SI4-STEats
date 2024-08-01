#language: fr
Fonctionnalité: gérer les livres


  Contexte:
    Etant donné une bibliothèque

  Scénario: Ajout d'un livre
    Quand le bibliothécaire  ajoute le livre "UML pour les nuls"
    Alors la bibliothèque contient le livre "UML pour les nuls" en un exemplaire

  Scénario: Ajout d'un second exemplaire
    Quand le bibliothécaire  ajoute deux exemplaires du livre "Design Patterns for dummies"
    Alors la bibliothèque contient deux exemplaires du livre "Design Patterns for dummies"

    Scénario: Ajout d'un étudiant
    Quand le bibliothécaire ajoute un étudiant "Marcel" avec le numéro d'étudiant 123456
    Alors la bibliothèque contient un étudiant "Marcel" avec le numéro d'étudiant 123456