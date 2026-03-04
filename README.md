# ⚡ LeanGrid - Smart Grid Optimization Engine

[![Java](https://img.shields.io/badge/Java-L3_Projet-ED8B00?style=for-the-badge&logo=openjdk)](https://github.com/melissa-yahiatene/LeanGrid-Optimization)
[![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://maven.apache.org/)
[![Security](https://img.shields.io/badge/Focus-Cybersecurity-red?style=for-the-badge)](./DOCUMENTATION.md)

## 📖 Présentation
Ce projet modélise et optimise un réseau de distribution électrique intelligent (Smart Grid). L'objectif est d'assurer une répartition optimale de la charge entre des **générateurs** et des **consommateurs** tout en minimisant les coûts de surcharge et de dispersion.

> 🔍 **Analyse Détaillée :** Pour comprendre le fonctionnement de l'algorithme et des mouvements en chaîne, consultez la [**Documentation Technique Complète ICI**](./DOCUMENTATION.md).<br>
> **Note :** Ce projet a été réalisé dans le cadre de la L3 Informatique à l'Université Paris Cité.

## 🏗️ Architecture & Design Patterns
Le projet repose sur une architecture robuste pour garantir la maintenabilité et l'évolution du code :
* **Pattern MVP (Model-View-Presenter) :** Séparation stricte de la logique métier et de l'interface.
* **Factory Pattern :** Centralisation de la création des entités du réseau (`NetworkFactory`).
* **Algorithmique :** Implémentation d'un algorithme d'optimisation itératif pour la réduction des coûts de réseau.

## 🛡️ Focus Cybersécurité & Résilience
Bien que ce soit un projet de programmation, il intègre des concepts clés de la sécurité des systèmes :
- **Robustesse du Parsing :** Validation stricte des entrées via `NetworkSerializer` pour prévenir les données malformées.
- **Gestion des Infrastructures Critiques :** Simulation de scénarios de saturation et calcul de pénalités de surcharge (SICS).
- **Fiabilité :** Gestion complète des exceptions (`NumberFormatException`, `SecurityException`) pour éviter les dénis de service applicatifs.

## 📊 Modélisation Technique
Une instance est définie par le triplet $S = \langle M, G, C \rangle$ :
- **M (Maisons) :** 10, 20 ou 40 kW.
- **G (Générateurs) :** Capacité maximale variable.
- **C (Connexions) :** Mapping unique Maison ↔ Générateur.

**Fonction de Coût :** $$Cout(S) = Disp(S) + \lambda \times Surcharge(S)$$
*(Où $\lambda$ représente le facteur de sévérité de la pénalité).*

## 🚀 Installation & Lancement
Le projet inclut des scripts d'automatisation pour compiler et lancer l'application instantanément.

### 🐧 Linux / macOS
Ouvrez un terminal dans le dossier du projet :
```bash
chmod +x build.sh
./build.sh
```

### 🪟 Windows (PowerShell)
Ouvrez PowerShell dans le dossier du projet :
```PowerShell
# Si nécessaire, autorisez l'exécution : Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\build.ps1
```
