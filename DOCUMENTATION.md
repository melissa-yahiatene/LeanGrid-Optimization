# LeanGrid – Projet de gestion et d’optimisation de réseau électrique

Ce projet est structuré comme un projet Maven standard, avec une arborescence conforme
(`src/main/java`, `src/test/java`, `target/`) 

## Architecture du projet

Le projet suit une architecture **MVP (Model – View – Presenter)**.

* **Model** : entièrement implémenté (logique métier, validation, algorithme)
* **View** : structure présente mais vide
* **Presenter** : structure présente mais vide

Cette architecture a été mise en place dans l’optique d’une extension future vers une interface graphique (JavaFX), mais seule la partie modèle a été finalisée.

---
### Classe principale

La classe contenant la méthode `main` est :

```
org.leanGrid.LeanGridApp
```

---

## Lancement et exécution
Le projet peut être exécuté de plusieurs façons selon le système d’exploitation et les outils disponibles.
Voici un récapitulatif complet et clair.

### 1. Avec Maven (Windows, Linux et Mac) 

Compilation :

```
mvn clean compile

```

Exécution : 

(Création manuelle du réseau) :

```
java -cp target/classes org.leanGrid.LeanGridApp

```

Importation du réseau (avec fichier + λ) :

```
java -cp target/classes org.leanGrid.LeanGridApp ImportedFiles/nomFichier.txt ou chemin/vers/fichier.txt λ

```
---

### 2. Avec l'un des scripts fournis à la racine du projet :

Ils permettent :

* de compiler automatiquement tous les fichiers Java
* d’exécuter directement la classe principale
* d’éviter d’écrire les commandes à la main


## 2.1. Script PowerShell (ne fonctionne que sur Windows)

Commande :

```
.\build.ps1
```
Le script utilise le dossier out/ pour stocker les .class.

Les paramètres (fichier d’entrée, valeur de λ) peuvent être modifiés directement dans le script ou avec les commandes ci-dessous.

# Mode manuel (sans arguments)

```
java -cp out org.leanGrid.LeanGridApp

```
# Mode automatique (avec fichier + λ)

```
java -cp out org.leanGrid.LeanGridApp chemin/vers/fichier.txt λ

```

## 2.2. Script Bash (fonctionne uniquement sur Linux/Mac)

Commande : 

```
./build.sh
```
Ce script créé également un dossier out pour stocker les .class.
Les paramètres (fichier d’entrée, valeur de λ) peuvent être modifiés directement dans le script ou avec les commandes ci-dessous.

# Mode manuel (sans arguments) : 

Construction du réseau via des menus interactifs :

```
java -cp out org.leanGrid.LeanGridApp

```
# Mode automatique (avec fichier + λ)

Chargement d’un fichier texte décrivant un réseau, puis lancement de l’algorithme d’optimisation :

```
java -cp out org.leanGrid.LeanGridApp ImportedFiles/fichier.txt ou chemin/vers/fichier.txt λ

```

---

### Fonctionnalités implémentées

## Construction manuelle du réseau

Implémentée dans `NetworkOperator` :

* ajout / mise à jour de générateurs
* ajout / mise à jour de maisons
* ajout / suppression de connexions
* gestion robuste des erreurs de saisie

---

## Gestion d’un réseau existant

* calcul du coût global :

```
Coût = Dispersion + λ × Surcharge
```

* affichage clair et structuré du réseau
* modification dynamique des connexions
* gestion des maisons non connectées

---

## Export du réseau

Export d’un réseau valide vers un fichier texte :

```
NetworkSerializer.export(...)
```

---

###  Documentation

* toutes les classes sont documentées en **Javadoc**
* les méthodes clés sont commentées.

---

### Algorithme d’optimisation automatique

Implémenté dans la classe :

```
org.leanGrid.model.util.Algorithme
```

### Objectif

L’objectif de l’algorithme est de **réduire le coût global du réseau** en améliorant la répartition des maisons entre les générateurs, en tenant compte :

* de l’équilibrage des taux d’utilisation des générateurs (dispersion),
* de la pénalisation des surcharges via le paramètre λ.

L’algorithme est **approximatif** : il ne garantit pas l’optimalité globale, mais fournit des solutions **robustes et proches ou égales des valeurs optimales théoriques ** fournies.

---

### Principe général

L’algorithme repose principalement sur une **amélioration locale itérative** puis résoud les problèmes que peuvent impliqué l'amélioration locale en élargissant le domaine d'amélioration avec grâce à des **mouvements en chaines** :

1. À partir d’un réseau valide initial
2. Tant qu’une amélioration est possible (ou jusqu’à un nombre maximal d’itérations fixé à **1000**)
3. On identifie les générateurs dont le taux d’utilisation est :

    * nul (générateur inutilisé),
    * supérieur à 1 (surchargé),
    * ou trop éloigné du taux moyen du réseau (avec une différence fixée à **0.5** au minimum).

Des opérations locales sont alors appliquées afin de réduire la surcharge et la dispersion.

---

### Types de transformations appliquées

L’algorithme applique successivement plusieurs stratégies, de la plus simple à la plus complexe :

#### Déplacement simple

* Une maison est déplacée d’un générateur surutilisé vers un générateur sous-utilisé ou disponible
* La capacité maximale des générateurs est strictement respectée

#### Échange entre générateurs

* Deux maisons appartenant à deux générateurs distincts sont échangées
* L’échange est validé uniquement si les nouvelles charges respectent les capacités(**méthode <i>canSwap()</i>**)

#### Mouvement en chaîne

* Lorsqu'aucun déplacement direct n’est possible (**exemple l'instance 3**)
* L’algorithme libère de l’espace sur un générateur cible en déplaçant plusieurs maisons vers d’autres générateurs
* Une fois l’espace libéré, la maison problématique est déplacée

Cette stratégie permet de résoudre des situations bloquées localement.

---

### Conditions d’arrêt

L’algorithme s’arrête lorsque :

* aucune modification du réseau n’apporte d’amélioration,
* ou que la variation du coût devient inférieure à un seuil `delta` (0.01),
* ou que le nombre maximal d’itérations est atteint (1000).

---

### Qualité des résultats

* L'algorithme permet d'obtenir un cout global qui converge vers des solutions **très proches ou égales aux coûts optimaux** fournis dans le fichier Excel de référence
* Il est stable, cohérent et performant sur les instances testées.

---

## Import et export des réseaux

L’import et l’export des réseaux sont gérés par `NetworkSerializer`.

### Import

Pour faciliter les tests, un dossier `ImportedFiles` est fourni.

Il contient l’ensemble des instances mises à disposition sur Moodle et peut être utilisé directement lors de l’exécution automatique du programme.

---

### Export

* l’utilisateur peut fournir soit un **nom de fichier**, soit un **chemin complet**
* si seul un nom est fourni, le fichier est automatiquement sauvegardé dans un dossier `ExportedFiles`
* ce dossier est créé automatiquement s’il n’existe pas
* l’extension `.txt` est ajoutée automatiquement si elle est absente
* si le fichier de sauvegarde existe déjà, l’utilisateur est invité à confirmer l’écrasement
* si le chemin de sauvegarde correspond au fichier d’import initial, la sauvegarde est annulée

---

## Conclusion

Le projet **LeanGrid** est fonctionnel, robuste et extensible. Toutes les fonctionnalités demandées ont été implémentées avec soin, et l’architecture choisie permet une évolution future vers une interface graphique ou des algorithmes plus avancés.

