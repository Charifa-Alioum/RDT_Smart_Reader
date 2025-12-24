# RDT\_Smart\_Reader

\## Table des Matières

1\. \[Présentation du Projet](#présentation-du-projet)

2\. \[Architecture Technique](#architecture-technique)

3\. \[Guide d'Installation](#guide-dinstallation)

4\. \[Manuel d'Utilisation](#manuel-dutilisation)

5\. \[Développement](#développement)

6\. \[Dépannage](#dépannage)

7\. \[Sécurité](#sécurité)

8\. \[Maintenance](#maintenance)

9\. \[Annexes](#annexes)


\## Présentation du Projet

\### Objectif

RDT Smart Reader est une application mobile pour diagnostiquer le paludisme en analysant des images de tests de diagnostic rapide (TDR) avec l'IA.

\### Fonctionnalités Principales

\- Capture d'images de tests TDR

\- Analyse automatique par IA

\- Classification des résultats (positif/négatif/invalide)

\- Stockage local des résultats

\- Synchronisation avec serveur DHIS2


\### Public Cible

\- Professionnels de santé

\- Agents de terrain

\- Centres de santé

\- Organismes de santé publique


\## Architecture Technique

\### Stack Technique

\- \*\*Langage\*\* : Kotlin

\- \*\*Architecture\*\* : MVVM (Model-View-ViewModel)

\- \*\*Base de Données\*\* : Room

\- \*\*Traitement d'Images\*\* : YOLOv5, Tesseract OCR, OpenCV

\- \*\*Réseau\*\* : Retrofit

\- \*\*Injection de Dépendances\*\* : Hilt



\### Structure des Fichiers

```

app/

├── src/

│   ├── main/

│   │   ├── assets/           # Modèles IA

│   │   ├── java/cm/seeds/rdtsmartreader/

│   │   │   ├── adapters/     # Adaptateurs RecyclerView

│   │   │   ├── data/         # Couche données

│   │   │   ├── helper/       # Utilitaires

│   │   │   ├── modeles/      # Modèles

│   │   │   ├── ui/           # Fragments/Activités

│   │   │   └── viewmodel/    # ViewModels

│   │   └── res/              # Ressources

│   └── test/                 # Tests

```


\## Guide d'Installation

\### Prérequis

\- Android Studio Arctic Fox+

\- JDK 11

\- SDK Android 31+

\- Appareil Android 6.0+ ou émulateur


\### Étapes d'Installation

1\. Cloner le dépôt

2\. Ouvrir avec Android Studio

3\. Synchroniser avec Gradle

4\. Configurer les variables d'environnement

5\. Exécuter sur appareil/émulateur


\## Manuel d'Utilisation

1\. \*\*Capture d'Image\*\*
  - Lancer l'application
  - Appuyer sur "Nouveau Test"
  - Prendre une photo du test TDR
  - Valider la capture

2\. \*\*Analyse du Test\*\*
  - Traitement automatique
  - Identification des zones d'intérêt
  - Affichage du résultat

3\. \*\*Gestion des Résultats\*\*
  - Historique des tests
  - Export des résultats
  - Synchronisation serveur


\## Développement

\### Structure du Code

\#### PythonMod.kt

```kotlin

class PythonMod private constructor(application: Application) {
   init {
       if (!Python.isStarted()) {
           Python.start(AndroidPlatform(application))
       }
       preprocessingMod = Python.getInstance().getModule("preprocessing")
   }

   suspend fun preproccessImage(imagePath: String): List<Bitmap?> {
       // Implémentation...
   }
}

\#### ScanFragment.kt

```kotlin

class ScanFragment : Fragment(), ZXingScannerView.ResultHandler {
   private fun checkCameraPermission() { /\* ... \*/ }

   override fun handleResult(result: Result?) {

       // Traitement du résultat

   }
}


\### Tests

```kotlin

@RunWith(AndroidJUnit4::class)

class PythonModTest {

   @Test
   fun testImageProcessing() { /\* ... \*/ }

}

\### Problèmes Courants

1\. \*\*Échec de Capture\*\*
  - Vérifier les permissions caméra
  - Redémarrer l'application
  - Vérifier l'utilisation de la caméra par d'autres apps

2\. \*\*Erreur de Traitement\*\*
  - Vérifier la qualité de l'image
  - S'assurer d'un bon éclairage
  - Redémarrer l'application

3\. \*\*Synchronisation\*\*
  - Vérifier la connexion Internet
  - Vérifier les identifiants API
  - Vérifier le statut du serveur

\## Sécurité

\### Mesures Implémentées

1\. Chiffrement des données

2\. Authentification utilisateur

3\. Gestion sécurisée des jetons

4\. Validation des entrées

5\. Journalisation des accès



\### Bonnes Pratiques

\- Pas de stockage en clair

\- Utilisation de HTTPS

\- Mises à jour régulières

\- Validation des entrées




\### Journalisation

```kotlin

private const val TAG = "RDT\_APP"

Log.d(TAG, "Message de débogage")

```



\### Sauvegarde

\- Sauvegarde automatique

\- Export CSV/JSON

\- Synchronisation cloud


\### Codes d'Erreur

| Code | Description | Solution |

|------|-------------|----------|

| 401  | Non autorisé | Vérifier identifiants |

| 404  | Non trouvé | Vérifier URL |

| 500  | Erreur serveur | Contacter admin |



