LeanGrid – Projet de gestion et d’optimisation de réseau électrique

Ce projet est structuré comme un projet Maven standard, avec une arborescence conforme
(src/main/java, src/test/java, target/).

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
ARCHITECTURE DU PROJET

Le projet suit une architecture MVP (Model – View – Presenter).

Model : entièrement implémenté (logique métier, validation, algorithme)
View : structure présente mais vide
Presenter : structure présente mais vide

Cette architecture a été mise en place dans l’optique d’une extension future vers une interface graphique (JavaFX), mais seule la partie modèle a été finalisée.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

CLASSE PRINCIPALE

La classe contenant la méthode main est :

"org.leanGrid.LeanGridApp"

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

LANCEMENT ET EXÉCUTION

Le projet peut être exécuté de plusieurs façons selon le système d’exploitation et les outils disponibles.

1. AVEC MAVEN (WINDOWS, LINUX ET MAC)

Compilation :

--------------------
mvn clean compile

--------------------

Exécution – création manuelle du réseau :

-------------------------------------------------
java -cp target/classes org.leanGrid.LeanGridApp
-------------------------------------------------

Exécution – importation du réseau (avec fichier + λ) :

-------------------------------------------------------------------------------------------------------------
java -cp target/classes org.leanGrid.LeanGridApp ImportedFiles/nomFichier.txt ou chemin/vers/fichier.txt λ

-------------------------------------------------------------------------------------------------------------
2. AVEC L’UN DES SCRIPTS FOURNIS À LA RACINE DU PROJET

Les scripts permettent :

- de compiler automatiquement tous les fichiers Java

- d’exécuter directement la classe principale

- d’éviter d’écrire les commandes à la main

------------------------------------------------------------------------------------------------------------------------------------------
2.1 SCRIPT POWERSHELL (WINDOWS UNIQUEMENT)

Commande :

--------------------
.\build.ps1
--------------------

Le script utilise le dossier out/ pour stocker les fichiers .class.

Les paramètres (fichier d’entrée, valeur de λ) peuvent être modifiés directement dans le script ou via les commandes ci-dessous.

Mode manuel (sans arguments) :

---------------------------------------
java -cp out org.leanGrid.LeanGridApp

----------------------------------------

Mode automatique (avec fichier + λ) :

-----------------------------------------------------------------
java -cp out org.leanGrid.LeanGridApp chemin/vers/fichier.txt λ

-----------------------------------------------------------------

2.2 SCRIPT BASH (LINUX / MAC UNIQUEMENT)

Commande :

-------------------
./build.sh

-------------------

Ce script crée également un dossier out pour stocker les fichiers .class.

Les paramètres (fichier d’entrée, valeur de λ) peuvent être modifiés directement dans le script ou via les commandes ci-dessous.

Mode manuel (sans arguments) :

-------------------------------------------
java -cp out org.leanGrid.LeanGridApp

--------------------------------------------

Mode automatique (avec fichier + λ) :

-----------------------------------------------------------------------------------------------------
java -cp out org.leanGrid.LeanGridApp ImportedFiles/fichier.txt ou chemin/vers/fichier.txt λ
-----------------------------------------------------------------------------------------------------

FONCTIONNALITÉS IMPLÉMENTÉES

1. CONSTRUCTION MANUELLE DU RÉSEAU

Implémentée dans la classe NetworkOperator :

- ajout / mise à jour de générateurs

- ajout / mise à jour de maisons

- ajout / suppression de connexions

- gestion robuste des erreurs de saisie


2. GESTION D’UN RÉSEAU EXISTANT

- calcul du coût global du réseau :
Coût = Dispersion + λ × Surcharge

- affichage clair et structuré du réseau

- modification dynamique des connexions

- gestion des maisons non connectées

-----------------------------------------------------------------------------------------------------------------------------------------
3. EXPORT DU RÉSEAU

Export d’un réseau valide vers un fichier texte via :

NetworkSerializer.export(...)

DOCUMENTATION :

- toutes les classes sont documentées en Javadoc

- les méthodes clés sont commentées

-------------------------------------------------------------------------------------------------------------------------------------

4. ALGORITHME D’OPTIMISATION AUTOMATIQUE

Implémenté dans la classe :

---------------------------------------
org.leanGrid.model.util.Algorithme

--------------------------------------
OBJECTIF

L’objectif de l’algorithme est de réduire le coût global du réseau en améliorant la répartition des maisons entre les générateurs, en tenant compte :

- de l’équilibrage des taux d’utilisation des générateurs (dispersion)

- de la pénalisation des surcharges via le paramètre λ

- L’algorithme est approximatif : il ne garantit pas l’optimalité globale, mais fournit des solutions robustes et proches ou égales aux valeurs optimales théoriques fournies.

PRINCIPE GÉNÉRAL

L’algorithme repose principalement sur une amélioration locale itérative, puis résout les limites de cette approche en élargissant le domaine d’amélioration grâce à des mouvements en chaîne.

À partir d’un réseau valide initial

Tant qu’une amélioration est possible (ou jusqu’à un maximum de 1000 itérations)

Identification des générateurs dont le taux d’utilisation est :

- nul (générateur inutilisé)

- supérieur à 1 (surchargé)

- trop éloigné du taux moyen du réseau (différence minimale de 0.5)

- Des opérations locales sont alors appliquées pour réduire la surcharge et la dispersion.

TYPES DE TRANSFORMATIONS APPLIQUÉES

Déplacement simple
Une maison est déplacée d’un générateur surutilisé vers un générateur sous-utilisé ou disponible, en respectant strictement les capacités.

Échange entre générateurs
Deux maisons appartenant à deux générateurs distincts sont échangées.
L’échange est validé uniquement si les nouvelles charges respectent les capacités (méthode canSwap()).

Mouvement en chaîne
Lorsqu’aucun déplacement direct n’est possible (par exemple pour certaines instances), l’algorithme libère de l’espace sur un générateur cible en déplaçant plusieurs maisons vers d’autres générateurs. Une fois l’espace libéré, la maison problématique est déplacée.

Cette stratégie permet de résoudre des situations bloquées localement.

CONDITIONS D’ARRÊT

L’algorithme s’arrête lorsque :

- aucune modification du réseau n’apporte d’amélioration

- la variation du coût devient inférieure au seuil delta (0.01)

- le nombre maximal d’itérations (1000) est atteint

QUALITÉ DES RÉSULTATS

- l’algorithme converge vers des coûts très proches ou égaux aux valeurs optimales fournies dans le fichier Excel de référence

- il est stable, cohérent et performant sur les instances testées

------------------------------------------------------------------------------------------------------------------------------------------------------------
5. IMPORT ET EXPORT DES RÉSEAUX

L’import et l’export des réseaux sont gérés par la classe NetworkSerializer.

- IMPORT :

Un dossier ImportedFiles est fourni pour faciliter les tests.
Il contient l’ensemble des instances mises à disposition sur Moodle.

- EXPORT :

- l’utilisateur peut fournir un nom de fichier ou un chemin complet

- si seul un nom est fourni, le fichier est sauvegardé dans le dossier ExportedFiles

- le dossier est créé automatiquement s’il n’existe pas

- l’extension .txt est ajoutée automatiquement si absente

- si le fichier existe déjà, une confirmation d’écrasement est demandée

- si le chemin de sauvegarde correspond au fichier d’import initial, la sauvegarde est annulée

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
CONCLUSION

Le projet LeanGrid est fonctionnel, robuste et extensible.
Toutes les fonctionnalités demandées ont été implémentées avec soin, et l’architecture choisie permet une évolution future vers une interface graphique ou des algorithmes plus avancés.