package up.mi.paa.reseau_electrique.model;

import up.mi.paa.reseau_electrique.controller.Controller;

import java.io.*;

/**
 * Classe utilitaire pour sauvegarder / charger un réseau depuis un fichier texte.
 */
public class ReseauIO {

    // Sauvegarde au format de la partie 2
    public static void sauvegarder(Reseau r, String filename) {
        try (FileWriter fw = new FileWriter(filename)) {

            for (Generateur g : r.getGenerateurs()) {
                fw.write(String.format("generateur(%s,%d).\n", g.getNom(), g.getCapacite()));
            }

            for (Maison m : r.getMaisons()) {
            	fw.write(String.format("maison(%s,%s).\n", m.getNom(), TypeMaison.typeToString(m.getType())));

            }

            for (Connexion c : r.getConnexions()) {
                fw.write(String.format("connexion(%s,%s).\n",
                        c.getGenerateur().getNom(),
                        c.getMaison().getNom()));
            }

        } catch (IOException e) {
            System.out.println("Erreur écriture fichier : " + e.getMessage());
        }
    }

    // Chargement depuis un fichier texte
    public static Reseau charger(String filename) throws IOException {
        Reseau r = new Reseau();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int numeroLigne = 0;

            // 0 = generateurs, 1 = maisons, 2 = connexions
            int phase = 0;

            while ((line = br.readLine()) != null) {
                numeroLigne++;
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!line.endsWith(".")) {
                    throw new IllegalArgumentException("Ligne " + numeroLigne +
                            " : point final manquant -> " + line);
                }

                // On retire le point
                line = line.substring(0, line.length() - 1).trim();

                int idxOpen = line.indexOf('(');
                int idxClose = line.lastIndexOf(')');
                if (idxOpen == -1 || idxClose == -1 || idxOpen > idxClose) {
                    throw new IllegalArgumentException("Ligne " + numeroLigne +
                            " : format invalide -> " + line);
                }

                String keyword = line.substring(0, idxOpen).trim();
                String inside = line.substring(idxOpen + 1, idxClose).trim();
                String[] args = inside.split(",");
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].trim();
                }

                switch (keyword) {
                    case "generateur":
                        if (phase > 0) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : generateur après maisons/connexions -> " + line);
                        }
                        phase = 0;
                        if (args.length != 2) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : generateur doit avoir 2 arguments -> " + line);
                        }
                        String nomG = args[0];
                        int capacite;
                        try {
                            capacite = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : capacité non entière -> " + line);
                        }
                        if(capacite < 0) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : capacité négative -> " + line);
                        }
                        if (r.generateurExistant(nomG)) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : generateur déjà défini -> " + nomG);
                        }
                        r.ajouterGenerateur(new Generateur(nomG, capacite));
                        break;

                    case "maison":
                        if (phase == 0) phase = 1;
                        else if (phase > 1) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : maison après connexions -> " + line);
                        }
                        if (args.length != 2) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : maison doit avoir 2 arguments -> " + line);
                        }
                        String nomM = args[0];
                        String typeStr = args[1];
                        TypeMaison type = TypeMaison.stringToTypeMaison(typeStr);
                        if (type == null) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : type de maison inconnu (" + typeStr + ") -> " + line);
                        }
                        if (r.maisonExistante(nomM)) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : maison déjà définie -> " + nomM);
                        }
                        r.ajouterMaison(new Maison(nomM, type));
                        break;

                    case "connexion":
                        if (phase <= 1) phase = 2;
                        if (args.length != 2) {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : connexion doit avoir 2 arguments -> " + line);
                        }
                        String a = args[0];
                        String b = args[1];

                        Maison m;
                        Generateur g;

                        // ordre indifférent
                        if (r.maisonExistante(a) && r.generateurExistant(b)) {
                            m = r.recupererMaison(a);
                            g = r.recupererGenerateur(b);
                        } else if (r.maisonExistante(b) && r.generateurExistant(a)) {
                            m = r.recupererMaison(b);
                            g = r.recupererGenerateur(a);
                        } else {
                            throw new IllegalArgumentException("Ligne " + numeroLigne +
                                    " : connexion avec noms non définis -> " + line);
                        }

                        r.ajouterConnexion(new Connexion(m, g));
                        break;

                    default:
                        throw new IllegalArgumentException("Ligne " + numeroLigne +
                                " : mot-clé inconnu (" + keyword + ") -> " + line);
                }
            }
        }

        // Vérifs globales

        if (r.getGenerateurs().isEmpty() || r.getMaisons().isEmpty()) {
            throw new IllegalArgumentException("Erreur : il faut au moins un générateur et une maison.");
        }

        if (!r.offreSuperieureOuEgaleADemande()) {
            throw new IllegalArgumentException(
                    "Erreur : la demande totale dépasse la capacité totale des générateurs.");
        }

        if (!r.toutesMaisonsConnectees(r.getConnexions())) {
            throw new IllegalArgumentException(
                    "Erreur : certaines maisons ne sont pas connectées.");
        }

        Controller ctrl = new Controller(r);
        if (!ctrl.verifierConnexions(r.getConnexions()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Erreur : certaines maisons sont connectées à plusieurs générateurs.");
        }

        return r;
    }
}
