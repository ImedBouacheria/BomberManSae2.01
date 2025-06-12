package Joueur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de profils utilisateurs.
 * <p>
 * Cette classe implémente le patron de conception Singleton pour gérer la sauvegarde et le chargement des profils
 * utilisateurs. Elle permet de stocker, récupérer, modifier et supprimer des profils dans un fichier texte.
 * Les profils contiennent des informations comme le nom, prénom, couleur préférée et statistiques de jeu.
 * </p>
 * <p>
 * Le format de stockage dans le fichier utilise un séparateur ";" entre les champs suivants :
 * prénom, nom, couleur, parties jouées, parties gagnées, score total.
 * </p>
 * 
 * @author Non spécifié
 * @version Non spécifiée
 * @see Profile
 */
public class ProfileManager {

    /** Chemin du fichier où sont stockés les profils */
    private static final String PROFILES_FILE = "profiles.txt";
    
    /** Séparateur utilisé dans le fichier pour délimiter les champs */
    private static final String SEPARATOR = ";";
    
    /** Liste des profils chargés en mémoire */
    private List<Profile> profiles;
    
    /** Instance unique du gestionnaire de profils (Singleton) */
    private static ProfileManager instance;

    /**
     * Constructeur privé qui initialise la liste des profils et charge les profils existants.
     * <p>
     * Ce constructeur est privé pour empêcher l'instanciation directe (patron Singleton).
     * </p>
     */
    private ProfileManager() {
        profiles = new ArrayList<>();
        loadProfiles();
    }

    /**
     * Retourne l'instance unique du gestionnaire de profils.
     * <p>
     * Si l'instance n'existe pas, elle est créée.
     * </p>
     *
     * @return L'instance unique du ProfileManager
     */
    public static ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }

    /**
     * Charge les profils depuis le fichier de sauvegarde.
     * <p>
     * Cette méthode efface d'abord la liste des profils en mémoire, puis lit le fichier
     * ligne par ligne pour recréer les objets Profile. Si le fichier n'existe pas, une nouvelle
     * liste vide est créée.
     * </p>
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
     * Sauvegarde tous les profils dans le fichier.
     * <p>
     * Cette méthode écrit chaque profil en mémoire dans le fichier de sauvegarde,
     * en convertissant chaque profil en une ligne de texte.
     * </p>
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
     * Analyse une ligne du fichier pour créer un objet Profile.
     * <p>
     * Cette méthode divise la ligne en utilisant le séparateur et extrait les informations
     * nécessaires pour créer un nouveau profil.
     * </p>
     *
     * @param line La ligne de texte à analyser
     * @return Un nouvel objet Profile créé à partir de la ligne, ou null en cas d'erreur
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
     * Formate un profil en ligne de texte pour le fichier.
     * <p>
     * Cette méthode convertit un objet Profile en une chaîne de caractères
     * avec les champs séparés par le séparateur défini.
     * </p>
     *
     * @param profile Le profil à formater
     * @return Une chaîne de caractères représentant le profil
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
     * Ajoute un nouveau profil à la liste et sauvegarde les changements.
     * <p>
     * Cette méthode vérifie d'abord si le profil existe déjà avant de l'ajouter.
     * </p>
     *
     * @param profile Le profil à ajouter
     * @return true si le profil a été ajouté avec succès, false sinon
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
     * Supprime un profil de la liste et sauvegarde les changements.
     *
     * @param profile Le profil à supprimer
     * @return true si le profil a été supprimé avec succès, false s'il n'a pas été trouvé
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
     * Met à jour un profil existant dans la liste et sauvegarde les changements.
     * <p>
     * Cette méthode recherche le profil correspondant et le remplace par la nouvelle version.
     * </p>
     *
     * @param profile Le profil mis à jour
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
     * Recherche un profil par son prénom et nom.
     *
     * @param firstName Le prénom à rechercher
     * @param lastName Le nom à rechercher
     * @return Le profil correspondant ou null s'il n'existe pas
     */
    public Profile findProfile(String firstName, String lastName) {
        return profiles.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName) &&
                        p.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retourne une copie de la liste de tous les profils.
     *
     * @return Une nouvelle liste contenant tous les profils
     */
    public List<Profile> getAllProfiles() {
        return new ArrayList<>(profiles);
    }

    /**
     * Retourne le nombre total de profils.
     *
     * @return Le nombre de profils dans la liste
     */
    public int getProfileCount() {
        return profiles.size();
    }

    /**
     * Vérifie si un profil avec le prénom et nom spécifiés existe déjà.
     *
     * @param firstName Le prénom à vérifier
     * @param lastName Le nom à vérifier
     * @return true si un profil correspondant existe, false sinon
     */
    public boolean profileExists(String firstName, String lastName) {
        return findProfile(firstName, lastName) != null;
    }

    /**
     * Retourne un tableau des couleurs disponibles pour les profils.
     *
     * @return Un tableau de chaînes contenant les noms des couleurs disponibles
     */
    public static String[] getAvailableColors() {
        return new String[]{"Rouge", "Bleu", "Vert", "Jaune", "Orange", "Violet", "Rose", "Cyan"};
    }

    /**
     * Supprime tous les profils de la liste et sauvegarde les changements.
     * <p>
     * Cette méthode est principalement utilisée à des fins de débogage.
     * </p>
     */
    public void clearAllProfiles() {
        profiles.clear();
        saveProfiles();
        System.out.println("🗑️ Tous les profils ont été supprimés.");
    }

    /**
     * Affiche tous les profils dans la console.
     * <p>
     * Cette méthode est principalement utilisée à des fins de débogage.
     * </p>
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