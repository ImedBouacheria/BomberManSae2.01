package com.example.bombermansae201;

/**
 * Classe de test pour le système de profils
 * Utilisez cette classe pour tester les fonctionnalités des profils
 */
public class ProfileTest {

    public static void main(String[] args) {
        System.out.println("=== TEST DU SYSTÈME DE PROFILS ===");

        ProfileManager manager = ProfileManager.getInstance();

        // Test 1: Création de profils
        System.out.println("\n1. Test de création de profils:");

        Profile profile1 = new Profile("Alice", "Dupont", "Rouge");
        Profile profile2 = new Profile("Bob", "Martin", "Bleu");
        Profile profile3 = new Profile("Claire", "Durand", "Vert");

        manager.addProfile(profile1);
        manager.addProfile(profile2);
        manager.addProfile(profile3);

        // Test 2: Affichage des profils
        System.out.println("\n2. Liste des profils:");
        manager.printAllProfiles();

        // Test 3: Simulation de parties
        System.out.println("\n3. Simulation de parties:");

        // Alice joue plusieurs parties
        profile1.updateStats(true, 1500);  // Victoire
        profile1.updateStats(false, 800);  // Défaite
        profile1.updateStats(true, 2200);  // Victoire

        // Bob joue quelques parties
        profile2.updateStats(false, 600);  // Défaite
        profile2.updateStats(true, 1800);  // Victoire

        // Claire joue une partie
        profile3.updateStats(true, 1200);  // Victoire

        // Sauvegarder les changements
        manager.saveProfiles();

        // Test 4: Affichage des statistiques
        System.out.println("\n4. Statistiques après simulation:");
        manager.printAllProfiles();

        // Test 5: Recherche de profil
        System.out.println("\n5. Test de recherche:");
        Profile found = manager.findProfile("Alice", "Dupont");
        if (found != null) {
            System.out.println("Profil trouvé: " + found);
            System.out.println("Taux de victoire: " + String.format("%.1f%%", found.getWinRate()));
        }

        // Test 6: Couleurs disponibles
        System.out.println("\n6. Couleurs disponibles:");
        String[] colors = ProfileManager.getAvailableColors();
        for (String color : colors) {
            System.out.println("- " + color);
        }

        // Test 7: Test de profil existant
        System.out.println("\n7. Test de profil existant:");
        boolean exists = manager.profileExists("Alice", "Dupont");
        System.out.println("Alice Dupont existe: " + exists);

        boolean notExists = manager.profileExists("Jean", "Inconnu");
        System.out.println("Jean Inconnu existe: " + notExists);

        System.out.println("\n=== FIN DES TESTS ===");
        System.out.println("Total profils: " + manager.getProfileCount());

        // Optionnel: Nettoyer pour les tests
        // manager.clearAllProfiles();
    }

    /**
     * Test de création de profils avec données réalistes
     */
    public static void createSampleProfiles() {
        ProfileManager manager = ProfileManager.getInstance();

        // Créer quelques profils d'exemple avec des statistiques
        Profile[] sampleProfiles = {
                new Profile("Emma", "Leroy", "Rouge", 15, 8, 12500),
                new Profile("Lucas", "Bernard", "Bleu", 22, 12, 18700),
                new Profile("Sophie", "Dubois", "Vert", 8, 6, 9200),
                new Profile("Thomas", "Roux", "Jaune", 31, 18, 25600),
                new Profile("Camille", "Moreau", "Orange", 5, 2, 4800)
        };

        System.out.println("Création de profils d'exemple...");
    }
}