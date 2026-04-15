#  Mini-Compilateur Python (Java)

 Présentation
Ce projet consiste en l'implémentation d'un mini-compilateur pédagogique en Java, conçu pour analyser un sous-ensemble de langage inspiré de Python. Il couvre les deux premières phases fondamentales de la compilation :
- **Analyse Lexicale** : Transformation du code source brut en une suite structurée de tokens (jetons).
- **Analyse Syntaxique** : Vérification de la conformité structurelle du programme via une descente récursive.

Le système inclut une **interface graphique Swing** intuitive pour la visualisation des résultats .

 Fonctionnalités Supportées
- Variables & Affectations (`=`, `+=`, `-=`)
- Opérateurs arithmétiques, de comparaison et logiques
- Incrémentation / Décrémentation (`++`, `--`)
- Structures conditionnelles : `if / elif / else`
- Boucles : `while`, `do / while`, `for in range()`
- Aiguillage multi-branches : `switch / case / default`
- Gestion des commentaires (`#`) et des chaînes de caractères
- Détection et localisation précise des erreurs lexicales et syntaxiques


##  Prérequis
- **Java Development Kit (JDK)** : version 8 ou supérieure
- Un terminal ou un IDE (VS Code, IntelliJ, Eclipse, NetBeans, etc.)

## ▶ Compilation & Exécution
Avec votre IDE (VS Code, IntelliJ, Eclipse, NetBeans...) :
1. Ouvrez le dossier du projet.
2. Faites un clic droit sur le fichier **`CompilerGUI.java`**.
3. Sélectionnez **"Run Java"**.

 L'interface graphique se lancera directement. La compilation et l'exécution sont gérées automatiquement par l'IDE.



                Notes Importantes & Workflow de Travail:

 Modification de fichiers sources chargés

Lorsque vous utilisez le bouton "Charger Fichier" dans l'interface :
Le contenu du fichier .py est copié en mémoire dans la zone d'édition de l'interface.
L'interface ne surveille pas les modifications externes du fichier.
** Procédure obligatoire pour prendre en compte vos changements :
1.Modifiez et sauvegardez votre fichier .py dans votre éditeur externe.
2.Retournez sur l'interface et cliquez sur "Charger Fichier" pour actualiser le contenu en mémoire.
3.Cliquez enfin sur "Compiler".

      Sans cette étape de rechargement, le compilateur analysera l'ancienne version copiée en mémoire et ignorera vos modifications.


