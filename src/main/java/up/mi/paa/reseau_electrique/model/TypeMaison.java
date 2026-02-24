package up.mi.paa.reseau_electrique.model;
/**
 * L'énumération TypeMaison définit les types de maisons
 * selon leur consommation électrique : basse, moyenne ou forte.
 * Chaque type est associé à une consommation en kilowatts (kW).
 * Elle fournit également des méthodes utilitaires pour convertir
 * depuis et vers une représentation textuelle.
 */

public enum TypeMaison {
    BASSE_CONSOMMATION(10),
    MOYENNE_CONSOMMATION(20),
    FORTE_CONSOMMATION(40);

    private final double consommationKW;

    TypeMaison(double consommationKW) {
        this.consommationKW = consommationKW;
    }

    public double getConsommationKW() {
        return consommationKW;
    }

    // Conversion depuis une chaîne provenant de l'utilisateur / fichier texte
    public static TypeMaison stringToTypeMaison(String s) {
        if (s == null) return null;

        switch (s.toUpperCase()) {
            case "BASSE":
                return TypeMaison.BASSE_CONSOMMATION;
            case "MOYENNE":
            case "NORMAL":     // pour être compatible avec l'énoncé
                return TypeMaison.MOYENNE_CONSOMMATION;
            case "FORTE":
                return TypeMaison.FORTE_CONSOMMATION;
            default:
                return null;
        }
    }
    public static String typeToString(TypeMaison type) {
        switch(type) {
            case BASSE_CONSOMMATION: return "BASSE";
            case MOYENNE_CONSOMMATION: return "NORMAL";
            case FORTE_CONSOMMATION: return "FORTE";
        }
        return "INCONNU";
    }

}
