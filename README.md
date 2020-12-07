# Todoc - Projet 5
 
 
 Ce projet avait pour but de nous apprendre à implémenter, utiliser, et tester une base de données SQLite en utilisant ROOM dans une application déjà existante.
 Il fallait aussi produire une APK signée et la publier sur le Play Store en Open Beta. L'APK (ou le bundle) devait être obfusqué, afin d'être le plus léger possible, et d'être protégé contre le reverse engineering.
 
 L'application de base est une application de gestion de tâches et de projets.
 L'utilisation d'une base de données permet la persistance des données de l'app.
 
 Pour la mise en place de cette bdd et de Room, j'ai utilisé le modèle MVVM afin d'avoir une architecture propre et facile à maintenir si je voulais par la suite implémenter de nouvelles fonctionnalitées dans l'application.
 
 J'ai utilisé RXJava et RXAndroid pour contourner l'obsolescence des AsyncTask
 L'obfuscation à été géré par R8, l'outil de base d'Android Studio.
