# Explication générale des tests deca
Ce document explique les différents tests automatisés mis en place pour le projet GL25.
Il y a principalement deux types de tests :
- Tests unitaires : Ils vérifient le bon fonctionnement des composants individuels du compilateur.
- Tests d'intégration : Ils vérifient que le compilateur fonctionne correctement dans son ensemble en compilant des programmes Deca et en exécutant le code généré.

# Emplacement des tests
Les tests sont situés dans le répertoire `src/test/` du projet. Voici une
vue d'ensemble de la structure des tests :

# Structure des tests
Le dossier `src/test/` est organisé comme suit :
- `deca/` : Contient les tests d'intégration pour le compilateur Deca.
    - `codegen/` : Tests d'intégration pour la génération de code.
    - `context/` : Tests d'intégration pour la vérification contextuelle.
    - `syntax/` : Tests d'intégration pour l'analyse syntaxique.
- `java/` : Contient les tests unitaires écrits en Java.
    - `context/` : Tests unitaires pour la vérification contextuelle.
    - `syntax/` : Tests unitaires pour l'analyse syntaxique.
    - `tools/` : Dossier pour les utilitaires de test.
    - `tree/` : Tests unitaires pour la structure des arbres syntaxiques.
- `script/` : Contient les scripts de test pour automatiser les tests d'intégration
    - `launchers/` : Launchers pour différents types de tests.
    - `tools`: Outils pour les scripts de test.

## Dossier deca/
Pour chaque sous-dossier dans `deca/`, la structure est la suivante :
- `valid/` : Contient des programmes Deca valides.
    - `created/` : Tests créés spécifiquement pour ce projet.
    - `provided/` : Tests fournis avec le projet.
- `invalid/` : Contient des programmes Deca invalides.
    - `created/` : Tests créés spécifiquement pour ce projet.
    - `provided/` : Tests fournis avec le projet.

Si jamais le dossier created contient d'autres sous-dossiers, c'est pour organiser les tests de manière plus spécifique.
Dans les dossiers `syntax/valid/created/` et `syntax/invalid/created/`, il y a des sous-dossiers `lexer/` et `parser/` pour séparer les tests d'analyse lexicale et syntaxique.
Dans le dossier `codegen/`, il y a un sous-dossier `perf/` pour les tests de performance, un sous-dossier `print/` pour les tests d'équivalences entre les différentes fonctions d'affichage, et un sous-dossier `interactive/` pour les tests d'intégration en mode interactif.
Il peut y avoir aussi des sous-dossiers `sans-objet` et `objet` dans les dossiers `created/` pour différencier les tests qui n'utilisent pas les objets de ceux qui les utilisent.

Voici la structure complète de `src/test/` :

```
src/test/
├── deca/
│   └── codegen/
│       └── interactive/
│           └── (tests d'intégration pour le code généré en mode interactif)
│       └── valid/
│           └── created/
│               └── (tests d'intégration pour les programmes Deca valides)
|           └── provided/
│               └── (tests d'intégration pour les programmes Deca valides fournis)
│       └── invalid/
│           └── created/
│               └── (tests d'intégration pour les programmes Deca invalides)
│       └── print/
│               └── (tests d'intégration pour les équivalences entre print et println (printx et printlnx inclus))
|       └── perf/
|           └── provided/
│               └── (tests de performance pour le code généré)
│   └── context/
│       └── valid/
|           └── created/
│               └── (tests d'intégration pour la vérification contextuelle des programmes Deca valides)
|           └── provided/
│               └── (tests d'intégration pour la vérification contextuelle des programmes Deca valides fournis)
│       └── invalid/
|           └── created/
│              └── (tests d'intégration pour la vérification contextuelle des programmes Deca invalides)
|           └── provided/
│               └── (tests d'intégration pour la vérification contextuelle des programmes Deca invalides fournis)
│   └── syntax/
│       └── valid/
|           └── created/
|               └── lexer/
│                   └── (tests d'intégration pour l'analyse lexicale des programmes Deca valides)
|               └── parser/
|                   └── sans-objet/
|                       └── (tests d'intégration pour l'analyse syntaxique des programmes Deca valides sans utilisation d'objets)
|                   └── objet/  
|                       └── (tests d'intégration pour l'analyse syntaxique des programmes Deca valides utilisant des objets)
│           └── (tests d'intégration pour l'analyse syntaxique des programmes Deca valides)
│       └── invalid/
|           └── created/
|               └── lexer/
│                   └── (tests d'intégration pour l'analyse lexicale des programmes Deca valides)
|               └── parser/
|                   └── (tests d'intégration pour l'analyse syntaxique des programmes Deca invalides)
│           └── provided/
│               └── (tests d'intégration pour l'analyse syntaxique des programmes Deca invalides fournis)
├── java/
│   └── context/
│       └── (tests unitaires en Java)
│   └── syntax/
│       └── (tests unitaires en Java)
│   └── tools/
│       └── (dossier pour les utilitaires de test)
│   └── tree/
│      └── (tests unitaires en Java)
├── script/
│   └── (scripts de test d'intégration)
└── resources/
    └── (fichiers de configuration)
```
