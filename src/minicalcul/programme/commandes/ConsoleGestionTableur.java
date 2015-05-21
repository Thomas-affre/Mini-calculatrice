/* 
 * ConsoleGestionTableur.java                            19 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet de gestion des commandes du tableur
 * @author Clément Zeghmati
 * @version 0.1
 */
public class ConsoleGestionTableur extends ConsoleGestionMemoire {
    
    /** Commandes disponibles pour le tableur */
    public static final String[] COMMANDES =
        {"CONT", "INIT", "FORM", "COPIER", "COPVAL", "RAZ", "AIDE"};

    /** Référence au tableur afin de pouvoir intéragir avec */
    private Tableur leTableur;
    
    /** 
     * Référence au groupe de consoles afin de pouvoir s'en servir pour 
     * des calculs sur le tableur
     */
    private Commandes lesConsoles;
    
    /** 
     * Plage de cellules consernées par l'opération envoyée sous forme
     * d'indices (A1=[0,0]..Z20[19,25]) 
     */
    private int[][] plageCellules;
    
    /** Formule à affecter conserné par l'opération */
    private String formule;

    /**
     * Constructeur de la console de gestionnaire des commandes du tableur
     * @param laFenetre Référence permettant d'accéder à la console et aux
     *                           zones mémoires
     * @param lesConsoles Référence au groupe de consoles afin de pouvoir s'en
     *                          servir pour des calculs sur le tableur
     * @param leTableur Référence permettant d'accéder au tableur
     */
    public ConsoleGestionTableur(FenetrePrincipale laFenetre,
            Commandes lesConsoles, Tableur leTableur) {
        super(laFenetre);
        this.leTableur = leTableur;
        this.lesConsoles = lesConsoles;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par réinitialiser l'état de cette console
        this.reinitialisation();

        // On découpe la chaine avec les espaces
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));
        
        // On regarde quelle instruction est demandée
        switch (this.getInstructions()[0].toString()) {
        case "CONT" : // Contenu d'une cellule ou d'une plage
            this.contenu();
            break;
        case "INIT" : // Initialisation d'une cellule
            this.initialisation();
            break;
        case "FORM" : // Initialisation avec une formule
            this.initFormule();
            break;
        case "COPIER" : // Copie des cellules
            this.copier();
            break;
        case "COPVAL" : // Copie la la valeur de la cellule
            this.copierValeur();
            break;
        case "RAZ" : // Remise à zéro
            this.raz();
            break;
        case "AIDE" : // Aide
            // TODO
            break;
        }
        
        return this.getaRetourner();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #reinitialisation()
     */
    @Override
    public void reinitialisation() {
        this.setCommande(null);
        this.setInstructions(null);
        this.setErreurTrouvee(false);
        this.setaRetourner(null);
        this.setLieuMauvaisArgument(-1);
        this.plageCellules = null;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #rechercheErreur(int)
     */
    @Override
    public void rechercheErreur(int typeErreur) {
        int posErreur = 0;
        this.setErreurTrouvee(true); // Erreur déclenchée
        StringBuilder tmpARetourner = new StringBuilder("  ");

        // On recherche la position de l'erreur dans la chaine originale
        for (int i = 0; i < this.getCommande().length()
                && posErreur < this.getLieuMauvaisArgument(); i++) {
            if (this.getCommande().charAt(i) == ' ') {
                posErreur++;
            }
            tmpARetourner.append(" ");
        }

        switch (typeErreur) {
        case ERREUR_NB_ARGUMENTS :
            this.setaRetourner(tmpARetourner.append("^\nErreur d'arguments : "
                    + "la commande " + this.getInstructions()[0] + " prend "                  
                    + nbArgumentsCommande() + " argument(s)").toString());
            break;
        case ERREUR_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : \"" 
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" n'est pas une cellule ou une plage de cellules.");
            break;
        case ERREUR_ORDRE_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : les bornes de la plage de cellules "
                            + "doivent être rangées dans l'ordre.").toString());
            break;
        case ERREUR_VALEUR_ARGUMENT :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur d'arguments : le deuxième argument "
                            + "doit être un réel.").toString());
            break;
        }        
    }

    /**
     * Affiche soit le contenu de toutes les cellules (sans arguments), soit le
     * contenu des cellules spécifiées. C'est-à-dire valeur + éventuellement
     * formule.
     */
    private void contenu() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("CONT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageCellules != null) {
            StringBuilder aRetourner = new StringBuilder("");
            
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    aRetourner.append("\n"
                            + this.leTableur.contenuCellule(new int[]{i, j}));
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #initialisation()
     */
    @Override
    public void initialisation() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("INIT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On récupère le deuxième argument qui sera le contenu de la zone
         * s'il s'agit bien d'un double
         */
        String valeur = null; // Initialisation bidon
        if (estUnDouble(this.getInstructions()[2])) {
            // On contrôle si on peut enlevé la virgule s'il y en a une
            valeur = estUnEntier(this.getInstructions()[2]) ? Integer.toString(
                    (int) Double.parseDouble(this.getInstructions()[2])) 
                    : this.getInstructions()[2];
                    
        } else {
            // La valeur est mauvaise (2ème argument)
            this.setLieuMauvaisArgument(2);
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageCellules != null) {
                        
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On récupère les coordonnées en chaines
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});

                    // On affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, valeur);

                    aRetourner.append(tmpZone + " a été inintialisée à "
                            + valeur + ".\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Initialise une cellule ou une plage de cellules avec une formule
     */
    private void initFormule() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("FORM")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();      
        
        // On récupère la formule et on la découpe pour la contrôler
        this.formule = this.getCommande().replaceFirst(
                "FORM " + this.getCommande().split(" ")[1] + " " , "");
        System.out.println(this.formule);    
        
        StringBuilder aRetourner = new StringBuilder("");
        
        // Si la plage mémoire est correcte, on peut continuer
        if (this.plageCellules != null) {
            
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On récupère les coordonnées en chaines
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});
                    
                    // On effectue l'opération
                    aRetourner.append("\n" + this.lesConsoles.getTableur()
                           .traitementCommande(this.formule + " = " + tmpZone));
                    
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * TODO comment method role
     */
    private void copier() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Copie la VALEUR de la cellule ou de la plage du premier argument dans la
     * cellule ou la plage du deuxième à condition que les dimensions des deux
     * arguments soient les mêmes
     */
    private void copierValeur() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("COPVAL")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();        
    }

    /**
     * Remet à 0 la cellule ou la plage cellule spécifiée en argument
     */
    private void raz() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("RAZ")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();  
        
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la remise à 0 si la plage est correcte
        if (this.plageCellules != null) {
                        
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On récupère les coordonnées en chaines
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});

                    // On affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, "0");

                    aRetourner.append(tmpZone + " a été remis à 0.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
        
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #controlePlageMemoire()
     */
    @Override
    public void controlePlageMemoire() {
        
        if (this.getInstructions().length == 1) {
            // On l'applique sur toutes les plages mémoires
            this.plageCellules = new int[][]{ {0,0}, {19,25} };

        } else if (estUneMemoire(this.getInstructions()[1])) {
                        
            // La plage commence et fini au même endroit
            this.plageCellules = new int[][]{
                    this.leTableur.conversionChaineEnCoordonnees(
                            this.getInstructions()[1]),
                    this.leTableur.conversionChaineEnCoordonnees(
                            this.getInstructions()[1])};

        } else if (estUnePlageDeCellulesOrdonnees(this.getInstructions()[1])) {
            // On récupère les coordonnées de la plage
            this.plageCellules = this.leTableur.coordonneesPlageCorrecte(
                    this.getInstructions()[1]);

        } else { // Erreur dans la plage de cellules
            this.setLieuMauvaisArgument(1);
            if (estUnePlageDeCellules(this.getInstructions()[1])) {
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRES);
            } else {
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRES);
            }
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #argumentsCommandeAttendus(java.lang.String)
     */
    @Override
    public boolean argumentsCommandeAttendus(String commande) {
        
        switch (commande.toString()) {
        case "CONT" :
        case "RAZ" :
            // La commande et la plage (facultatif
            return this.getInstructions().length <= 2;
        case "INIT" :       
        case "COPIER" :
        case "COPVAL" :
            // La commande, la plage mémoire et la valeur
            return this.getInstructions().length == 3;
        case "FORM" :
            // La commande, la plage mémoire et la formule
            return this.getInstructions().length >= 3;
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #nbArgumentsCommande()
     */
    @Override
    public String nbArgumentsCommande() {

        switch (this.getInstructions()[0].toString()) {
        case "CONT" :
        case "RAZ" :
            // La commande et la plage (facultatif) 
            return "0 ou 1";
        case "INIT" :       
        case "FORM" :
        case "COPIER" :
        case "COPVAL" :
            // La commande, la plage plage et la valeur
            return "2";
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_CELLULE).matcher(aTester).matches();
    }
    
    /**
     * Contrôle si une chaine de caractères est une plage de cellules
     * @param aTester Chaine à tester
     * @return true si la chaine est une plage de cellules, false sinon
     */
    private static boolean estUnePlageDeCellules(String aTester) {
        return Pattern.compile(REGEX_PLAGE_CELLULES).matcher(aTester).matches();
    }
    
    /**
     * Contrôle si une chaine de caractères est une plage de cellules ordonnées
     * @param aTester Chaine à tester
     * @return true si la chaine est une plage de cellules, false sinon
     */
    public boolean estUnePlageDeCellulesOrdonnees(String aTester) {
        
        if (Pattern.compile(REGEX_PLAGE_CELLULES).matcher(aTester).matches()) {
            int[][] coordonnees = leTableur.coordonneesPlageCorrecte(aTester);
            
            return (coordonnees[0][0] < coordonnees[1][0]
                    || (coordonnees[0][0] == coordonnees[1][0]
                            && coordonnees[0][1] < coordonnees[1][1]));
        }
        return false;
    }
}