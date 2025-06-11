package Joueur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire pour la sauvegarde et le chargement des profils
 */
public class ProfileManager {

    private static final String PROFILES_FILE = "profiles.txt";
    private static final String SEPARATOR = ";";
    private List<Profile> profiles;
    private static ProfileManager instance;

    private ProfileManager() {
        profiles = new ArrayList<>();
        loadProfiles();
    }

    /**
     * Singleton pour avoir une seule instance du gestionnaire
     */
    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }

    /**
     * Charge les profils depuis le fichier
     */
    public void loadProfiles() {
        profiles.clear();
        File file = new File(PROFILES_FILE);

        if (!file.exists()) {
            System.out.println("Aucun fichier de profils trouvé. Création d'une nouvelle liste.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Profile profile = parseProfileFromLine(line);
                if (profile != null) {
                    profiles.add(profile);
                }
            }
            System.out.println("✅ " + profiles.size() + " profils chargés avec succès.");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement des profils: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde tous les profils dans le fichier
     */
    public void saveProfiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILES_FILE))) {
            for (Profile profile : profiles) {
                String line = formatProfileToLine(profile);
                writer.write(line);
                writer.newLine();
            }
            System.out.println("✅ " + profiles.size() + " profils sauvegardés avec succès.");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la sauvegarde des profils: " + e.getMessage());
        }
    }

    /**
     * Analyse une ligne du fichier pour créer un profil
     */
    private Profile parseProfileFromLine(String line) {
        try {
            String[] parts = line.split(SEPARATOR);
            if (parts.length >= 3) {
                String firstName = parts[0].trim();
                String lastName = parts[1].trim();
                String colorName = parts[2].trim();

                // Statistiques optionnelles
                int gamesPlayed = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 0;
                int gamesWon = parts.length > 4 ? Integer.parseInt(parts[4].trim()) : 0;
                int totalScore = parts.length > 5 ? Integer.parseInt(parts[5].trim()) : 0;

                return new Profile(firstName, lastName, colorName, gamesPlayed, gamesWon, totalScore);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'analyse de la ligne: " + line);
        }
        return null;
    }

    /**
     * Formate un profil en ligne pour le fichier
     */
    private String formatProfileToLine(Profile profile) {
        return profile.getFirstName() + SEPARATOR +
                profile.getLastName() + SEPARATOR +
                profile.getColorName() + SEPARATOR +
                profile.getGamesPlayed() + SEPARATOR +
                profile.getGamesWon() + SEPARATOR +
                profile.getTotalScore();
    }

    /**
     * Ajoute un nouveau profil
     */
    public boolean addProfile(Profile profile) {
        // Vérifier si le profil existe déjà
        if (profiles.contains(profile)) {
            System.out.println("⚠️ Un profil avec ce nom existe déjà: " + profile.getFullName());
            return false;
        }

        profiles.add(profile);
        saveProfiles();
        System.out.println("✅ Nouveau profil ajouté: " + profile.getFullName());
        return true;
    }

    /**
     * Supprime un profil
     */
    public boolean removeProfile(Profile profile) {
        if (profiles.remove(profile)) {
            saveProfiles();
            System.out.println("✅ Profil supprimé: " + profile.getFullName());
            return true;
        }
        return false;
    }

    /**
     * Met à jour un profil existant
     */
    public void updateProfile(Profile profile) {
        // Rechercher et remplacer le profil
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).equals(profile)) {
                profiles.set(i, profile);
                saveProfiles();
                System.out.println("✅ Profil mis à jour: " + profile.getFullName());
                return;
            }
        }
        System.out.println("⚠️ Profil non trouvé pour mise à jour: " + profile.getFullName());
    }

    /**
     * Recherche un profil par nom
     */
    public Profile findProfile(String firstName, String lastName) {
        return profiles.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName) &&
                        p.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retourne tous les profils
     */
    public List<Profile> getAllProfiles() {
        return new ArrayList<>(profiles);
    }

    /**
     * Retourne le nombre de profils
     */
    public int getProfileCount() {
        return profiles.size();
    }

    /**
     * Vérifie si un profil existe déjà
     */
    public boolean profileExists(String firstName, String lastName) {
        return findProfile(firstName, lastName) != null;
    }

    /**
     * Retourne les couleurs disponibles
     */
    public static String[] getAvailableColors() {
        return new String[]{"Rouge", "Bleu", "Vert", "Jaune", "Orange", "Violet", "Rose", "Cyan"};
    }

    /**
     * Nettoie tous les profils (pour debug)
     */
    public void clearAllProfiles() {
        profiles.clear();
        saveProfiles();
        System.out.println("🗑️ Tous les profils ont été supprimés.");
    }

    /**
     * Affiche tous les profils (pour debug)
     */
    public void printAllProfiles() {
        System.out.println("=== LISTE DES PROFILS ===");
        if (profiles.isEmpty()) {
            System.out.println("Aucun profil enregistré.");
        } else {
            for (int i = 0; i < profiles.size(); i++) {
                System.out.println((i + 1) + ". " + profiles.get(i));
            }
        }
        System.out.println("========================");
    }
}
