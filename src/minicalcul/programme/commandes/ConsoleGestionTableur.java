/* 
 * ConsoleGestionTableur.java                            19 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet contenant des commandes permettant d'intéragir avec le tableur.
 * Toutes les explications sur les commandes sont contenu dans le champ de
 * classe AIDE.
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 0.6
 */
public class ConsoleGestionTableur extends ConsoleGestionMemoire {
    
    /** Résumé des commandes de la gestion de la mémoire */
    public static final String AIDE = 
            "--------------- AIDE DU TABLEUR --------------- \n"
            + " - RAZ [Cellule ou plage de cellules]\n"
            + "\tRemet à 0 les cellules spécifiées ou toutes s'il n'y a pas"
            + "\n\td'argument.\n\n"
            + " - CONT [Cellule ou plage de cellules]\n"
            + "\tAffiche le contenu de la ou les cellule(s) spécifiée(s) ou "
            + "\n\ttoutes s'il n'y a pas d'arguments. Le contenu d’une cellule "
            + "\n\tcomporte la valeur de la cellule et éventuellement la "
            + "\n\tformule qu’elle contient.\n\n"
            + " - INIT [Cellule ou plage de cellules] {valeur}\n" 
            + "\tInitialise le contenu de la ou les cellule(s) spécifiée(s) "
            + "\n\tou de toutes si aucune plage n'est précisée, avec la valeur "
            + "\n\tmise en deuxième argument.\n\n"
            + " - FORM {Ligne ou colonne de cellules}\n"
            + "\tDemande à l'utilisateur via une boîte de dialogue de saisir "
            + "\n\tla formule à appliquer sur la plage de cellule. Cette "
            + "\n\tcommande prend en compte l'utilisation des blocages ($)"
            + "\n\tde cellules.\n\n"
            + " - COPIER {Cellule} {Cellule}\n" 
            + "\tCopie le contenu de la première cellule dans la deuxième.\n\n"
            + " - COPIER {Cellule} {Plage de cellules}\n"
            + "\tCopie le contenu de la cellule dans toutes les cellules de la "
            + "\n\tplage sélectionnée.\n\n"
            + " - COPIER {Plage de cellules} {Plage de cellules}\n"
            + "\tCopie le contenu des cellules de la plage sélectionnée dans "
            + "\n\tles cellules de la deuxième. La plage d’arrivée doit être "
            + "\n\tsupérieure ou égale à la plage de départ.\n\n"
            + " - COPVAL {Cellule} {Cellule}\n" 
            + "\tCopie la valeur de la première cellule dans la deuxième.\n\n"
            + " - COPVAL {Cellule} {Plage de cellules}\n"
            + "\tCopie la valeur de la première cellule dans toutes les "
            + "\n\tcellules de la plage du deuxième argument.\n\n"
            + " - COPVAL {Plage de cellules} {Plage de cellules}\n"
            + "\tCopie les valeurs des cellules de la plage sélectionnée dans "
            + "\n\tles cellules de la deuxième. La plage d’arrivée doit être "
            + "\n\tsupérieure ou égale à la plage de départ.\n\n"
            + " - AIDE sans arguments\n"
            + "\tAffiche des informations sur les commandes du tableur.\n\n"
            + "[ ] : OPTIONNEL / { } : OBLIGATOIRE\n";

    /** Message affiché lorsque une plage de cellules est incorrecte */
    protected static final String MSG_PLAGE_MEMOIRE_MAUVAISE = "Erreur de "
            + "syntaxe : cet argument n'est pas une cellule ou une plage "
            + "de cellules.";
    
    /** Message affiché lorsque une plage de cellules n'est pas rangée */
    protected static final String MSG_PLAGE_MEMOIRE_NON_ORDONNEE = "Erreur "
            + "de syntaxe : les bornes de la plage de cellules "
            + "doivent être rangées dans l'ordre.";
    
    /** Message affiché à cause d'un problème de dimensions lors d'une copie */
    protected static final Object MSG_MAUVAISE_DIMENSION = "Erreur de "
            + "dimensions : la dimension de la plage source doit être plus "
            + "petite que la plage destination.";

    /** Message affiché lorsqu'un cellule n'est pas initialisée (copie) */
    protected static final Object MSG_CELLULE_NON_INITIALISEE = "Erreur "
            + "d'initialisation : la cellule à copier n'est pas initialisée.";

    /** Message affiché lorsqu'il manque une mémoire dans une commande */
    protected static final String MSG_MEMOIRE_ABSENTE = "Erreur de format : "
            + "une cellule ou une plage de cellules correcte est attendue pour "
            + "cette commande.";

    /** Message affiché lorsque la plage doit être une ligne ou une colonne */
    protected static final String MSG_PLAGE_LIGNE_OU_COLONNE = "Erreur de "
            + "format : la plage de cellules attendue doit être une ligne ou "
            + "une colone.";

    /** Message affiché lorsqu'une affectation n'est pas demandée */
    protected static final String MSG_AFFECTATION_INNATENDUE = "Erreur "
            + "d'affectation : aucune affectation n'est demandée.";

    /** Commandes disponibles pour le tableur */
    public static final String[] COMMANDES =
        {"CONT", "INIT", "FORM", "COPIER", "COPVAL", "RAZ", "AIDE"};

    /** Référence du tableur afin de pouvoir intéragir avec */
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
    
    /** Plage de destination lors d'une copie */
    private int[][] plageDestination;
    
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
        this.reinitialisation();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par réinitialiser l'état de cette console
        this.reinitialisation();

        // On découpe la chaîne avec les espaces
        this.commande = commande;
        this.instructions = commande.split(" ");
        
        // On regarde quelle instruction est demandée
        switch (this.instructions[0].toString()) {
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
        case "COPVAL" : // Copie la valeur de la cellule
            this.copierValeur();
            break;
        case "RAZ" : // Remise à zéro
            this.raz();
            break;
        case "AIDE" : // Aide
            this.aRetourner = AIDE;
            break;
        }
        return "> " + this.commande + "\n" + this.aRetourner;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #reinitialisation()
     */
    @Override
    protected void reinitialisation() {
        super.reinitialisation();
        this.plageCellules = null;
        this.plageDestination = null;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#rechercheErreur(int)
     */
    @Override
    protected void rechercheErreur(int typeErreur) {
        int posErreur = 0;
        this.erreurTrouvee = true; // Erreur déclenchée
        StringBuilder tmpARetourner = new StringBuilder("  ");

        // On recherche la position de l'erreur dans la chaine originale
        for (int i = 0; i < this.commande.length()
                && posErreur < this.lieuMauvaisArgument; i++) {
            if (this.commande.charAt(i) == ' ') {
                posErreur++;
            }
            tmpARetourner.append(" ");
        }
        // On rajoute un accent pour montrer le lieu de l'erreur
        tmpARetourner.append("^\n");

        switch (typeErreur) {
        case ERREUR_NB_ARGUMENT :
            this.aRetourner = tmpARetourner.append("Erreur d'argument : "
                    + "la commande " + this.instructions[0] + " prend "                  
                    + this.nbArgumentsCommande() + " arguments.").toString();
            break;
        case ERREUR_PLAGE_MEMOIRE :
            this.aRetourner = tmpARetourner.append(
                    MSG_PLAGE_MEMOIRE_MAUVAISE).toString();
            break;
        case ERREUR_ORDRE_PLAGE_MEMOIRE :
            this.aRetourner = tmpARetourner.append(
                    MSG_PLAGE_MEMOIRE_NON_ORDONNEE).toString();
            break;
        case ERREUR_VALEUR_ARGUMENT :
            this.aRetourner = tmpARetourner.append(
                    MSG_VALEUR_MAUVAISE).toString();
            break;
        case ERREUR_VALEUR_ABSENTE :
            this.aRetourner = tmpARetourner.append(
                    MSG_VALEUR_ABSENTE).toString();
            break;
        case ERREUR_DIMENSION_COPIE :
            this.aRetourner = tmpARetourner.append(
                    MSG_MAUVAISE_DIMENSION).toString();
            break;
        case ERREUR_INTIALISATION :
            this.aRetourner = tmpARetourner.append(
                    MSG_CELLULE_NON_INITIALISEE).toString();
            break;
        case ERREUR_MEMOIRE_ABSENTE :
            this.aRetourner = tmpARetourner.append(
                    MSG_MEMOIRE_ABSENTE).toString();
            break;
        case ERREUR_PLAGE_LIGNE_OU_COLONNE :
            this.aRetourner = tmpARetourner.append(
                    MSG_PLAGE_LIGNE_OU_COLONNE).toString();
            break;
        case ERREUR_AFFECTATION_INNATENDUE : 
            this.aRetourner = tmpARetourner.append(
                    MSG_AFFECTATION_INNATENDUE).toString();
            break;
        }        
    }
    
    /**
     * Affiche soit le contenu de toutes les cellules (sans arguments), soit le
     * contenu des cellules spécifiées. C'est-à-dire valeur + éventuellement la
     * formule.
     */
    private void contenu() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("RAZ")) {
            return;     // Inutile de continuer
        }
        
        // On va rechercher les contenus si la plage est bonne
        if (this.plageCellules != null) {
            StringBuilder aRetourner = new StringBuilder("");
            
            // Pour chaque ligne
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                // Et chaque colonne
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    aRetourner.append("\n"
                            + this.leTableur.contenuCellule(new int[]{i, j}));
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #initialisation()
     */
    @Override
    protected void initialisation() {        
        // On contrôle les arguments
        if (!this.controleValiditeArgument("INIT")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon
        
        /*
         * On récupère le deuxième argument qui sera le contenu de la zone
         * s'il s'agit bien d'un double
         */
        if (estUnDouble(this.aAffecter)) {
            // On contrôle si on peut enlevé la virgule s'il y en a une
            this.aAffecter = estUnEntier(this.aAffecter) 
                    ? Integer.toString(
                            (int) Double.parseDouble(this.aAffecter)) 
                    : this.getARRONDIR().format(
                            Double.parseDouble(this.aAffecter));
                    
        } else {
            // La valeur est mauvaise (2ème argument)
            this.lieuMauvaisArgument = 2;
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
                    
                    // On récupère les coordonnées en chaînes
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});
                    
                    // On réinitialise la cellule
                    this.leTableur.reinitialisationCellule(tmpZone);

                    // Et on y affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, this.aAffecter);

                    aRetourner.append(tmpZone + " a été inintialisée à "
                            + this.aAffecter + ".\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Initialise une cellule ou une plage de cellules avec une formule
     */
    private void initFormule() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("FORM")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon    
                
        // On demande la formule dans une boîte de dialogue
        this.aAffecter = (String) JOptionPane.showInputDialog(
                this.getLaFenetre(),
                "Saisissez la formule à appliquer à la plage",
                "Affectation d'une formule", JOptionPane.QUESTION_MESSAGE);

        if (this.aAffecter == null || this.aAffecter.equals("")) { 
            // Zone de saisie vide ou opération annulée
            this.aRetourner = "Initialisation annulée.";
            return;
        }

        // On vérifie qu'aucune affectation n'est faite dans la formule
        for (int i = 0; i < this.aAffecter.length(); i++) {
            if (this.aAffecter.charAt(0) == '=') {
                this.lieuMauvaisArgument = i;
                this.rechercheErreur(ERREUR_AFFECTATION_INNATENDUE);
                return;
            }
        }
        
        StringBuilder aRetourner = new StringBuilder("");
        /*
         * On effectue la première opération sur la première cellule de la plage
         * à part. Si elle se passe bien, on pourra continuer sur le reste de 
         * la plage. 
         */
        this.lesConsoles.getTableur().traitementCommande(this.aAffecter + " = "
                + this.leTableur.conversionCoordonneesEnChaine(new int[]{
                        this.plageCellules[0][0],this.plageCellules[0][1]}));
        
        /*
         * On vérifie l'état de la console de calcul du tableur. Si aucune
         * erreur n'a été déclenchée, on continue.
         */
        if (this.lesConsoles.getTableur().erreurTrouvee) {
            // On retourne l'état de la console avec l'erreur
            this.aRetourner = "? " + this.lesConsoles.getTableur().commande 
                    + "\n" + this.lesConsoles.getTableur().aRetourner;
            return; // Et on s'arrète la
        }            
        
        // Première cellule de la plage
        String premiere = this.leTableur.conversionCoordonneesEnChaine(
                new int[]{this.plageCellules[0][0], this.plageCellules[0][1]});

        /*
         * On récupère le contenu que l'on a affecté à la première cellule de
         * la plage concernée.
         */
        this.aAffecter = this.leTableur.restaurationContenuCellule(premiere);
        
        // Chaîne qui contiendra la formule modifiée à affecter
        String nouvelleFormule;
        
        // On parcourt chaque ligne
        for (int i = this.plageCellules[0][0];
                i <= this.plageCellules[1][0]; i++) {

            // Puis chaque colonne
            for (int j = this.plageCellules[0][1];
                    j <= this.plageCellules[1][1]; j++) {

                // On récupère les coordonnées d'insertions en chaînes
                String tmpZone = this.leTableur.
                        conversionCoordonneesEnChaine(new int[]{i, j});
                
                // On remplace les cellules non bloquées de la formule
                nouvelleFormule = this.modificationCellulesNonBloquee(premiere, 
                        tmpZone, this.aAffecter.split(" "));
                                
                // On effectue l'opération
                this.lesConsoles.getTableur().traitementCommande(
                        nouvelleFormule + " = " + tmpZone);
                
                // On garde une trace de l'opération
                aRetourner.append(tmpZone + " modifiée.\n");
            }
        }
        this.aRetourner = aRetourner.toString();
    }

    /**
     * Copie la cellule ou la plage du premier argument dans la
     * cellule ou la plage du deuxième à condition que les dimensions des deux
     * arguments soient bonnes. C'est-à-dire :
     *  - Si la source est une cellule, la cellule sera copier dans toutes les
     *    cellules de la destination en faisant attention aux '$' de blocage de
     *    cellules.
     *  - Si la source est une plage, la plage source sera copier dans une plage
     *    destination qui devra être au moins de la même taille en faisant 
     *    attention aux '$' de blocage de cellules.     
     */
    private void copier() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("COPIER")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon   
        
        /*
         * On vérifie les dimensions des plages afin d'être sûr que la source
         * puisse rentrer dans la destination.
         */
        int[] dimSource = dimensionPlage(this.plageCellules);
        int[] dimDestination = dimensionPlage(this.plageDestination);
        
        if (dimSource[0] > dimDestination[0] 
                || dimSource[1] > dimDestination[1]) {
            // Problème de dimension
            this.lieuMauvaisArgument = 1;
            this.rechercheErreur(ERREUR_DIMENSION_COPIE);
            return; // On ne fait pas la copie
        }
        
        /*
         * Si la source n'est qu'une cellule, on la repporte dans toutes les
         * cellules de la plage destination.
         */
        if (dimSource[0] == 1 && dimSource[1] == 1) {
            
            if (estInitialisee(this.instructions[1])) {
                // On récupère le contenu de la cellule source
                this.aAffecter = this.leTableur.restaurationContenuCellule(
                        this.instructions[1]);
                System.out.println("contenu : " + aAffecter);
                
                // On l'affecte à toutes les cellules de la plage
                for (int i = this.plageDestination[0][0];
                        i <= this.plageDestination[1][0]; i++) {

                    for (int j = this.plageDestination[0][1];
                            j <= this.plageDestination[1][1]; j++) {
                        
                        // On récupère les coordonnées d'insertions en chaînes
                        String tmpZone = this.leTableur.
                                conversionCoordonneesEnChaine(new int[]{i, j});
                        
                        // On modifie les cellules non bloquées de la formule
                        String tmpFormule = modificationCellulesNonBloquee(
                                this.instructions[1], tmpZone, 
                                this.aAffecter.split(" "));
                        
                        // On effectue la commande
                        this.lesConsoles.getTableur().traitementCommande(
                                tmpFormule + " = " + this.leTableur.
                                conversionCoordonneesEnChaine(new int[]{i,j}));
                    }
                }
            } else {
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_INTIALISATION);
            }
        } else {
            /*
             * Si la source est une plage, on la repporte dans les premières
             * cellules de la plage destination.
             */
            
            // On récupère les coordonnées des plages
            this.plageCellules = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[1]);
            this.plageDestination = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[2]);
            
            // On récupère les contenus dans un tableau
            String[][] aCopier = new String[dimSource[1]][dimSource[0]];
            
            for (int i = 0; i < aCopier.length; i++) {
                for (int j = 0; j < aCopier[i].length; j++) {
                    aCopier[i][j] = this.leTableur.restaurationContenuCellule(
                                this.leTableur.conversionCoordonneesEnChaine(
                                    new int[]{i + this.plageCellules[0][0],
                                            j + this.plageCellules[0][1]}));
                }
            }
            
            // On les affectes dans les nouvelles cellules
            for (int i = 0; i < aCopier.length; i++) {
                for (int j = 0; j < aCopier[i].length; j++) {        
                    // On récupère la cellule en chaîne
                    String cellule = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{
                                    i + this.plageDestination[0][0],
                                    j + this.plageDestination[0][1]});

                    // On modifie les cellules non bloquées de la formule
                    this.aAffecter = this.modificationCellulesNonBloquee(
                            this.leTableur.conversionCoordonneesEnChaine(
                                    new int[]{i + this.plageCellules[0][0],
                                            j + this.plageCellules[0][1]}),
                                            cellule, aCopier[i][j].split(" ")); 
                    
                    // On effectue la commande
                    this.lesConsoles.getTableur().traitementCommande(
                            this.aAffecter + " = " + cellule);
                }
            }
        }
        
        // On garde une trace à afficher sur la console
        this.aRetourner = "Les contenus de la plage " + this.instructions[1]
                + " ont été copiées dans la plage " + this.instructions[2] 
                + ".";  
    }

    /**
     * Copie la VALEUR de la cellule ou de la plage du premier argument dans la
     * cellule ou la plage du deuxième à condition que les dimensions des deux
     * arguments soient bonnes. C'est-à-dire :
     *  - Si la source est une cellule, la valeur sera copier dans toutes les
     *    cellules de la destination.
     *  - Si la source est une plage, la valeur sera copier dans une plage
     *    destination qui devra être au moins de la même taille.
     */
    private void copierValeur() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("COPVAL")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon   
        
        /*
         * On vérifie les dimensions des plages afin d'être sûr que la source
         * puisse rentrer dans la destination
         */
        int[] dimSource = dimensionPlage(this.plageCellules);
        int[] dimDestination = dimensionPlage(this.plageDestination);
        
        if (dimSource[0] > dimDestination[0] 
                || dimSource[1] > dimDestination[1]) {
            // Problème de dimension
            this.lieuMauvaisArgument = 1;
            this.rechercheErreur(ERREUR_DIMENSION_COPIE);
            return; // On ne fait pas la copie
        }
        
        /*
         * Si la source n'est qu'une cellule, on la repporte dans toutes les
         * cellules de la plage destination
         */
        if (dimSource[0] == 1 && dimSource[1] == 1) {

            if (estInitialisee(this.instructions[1])) {
                // On récupère la valeur de la cellule source
                this.aAffecter = this.leTableur.restaurationValeurCellule(
                        this.instructions[1]);
                
                // On l'affecte à toutes les cellules de la plage
                for (int i = this.plageDestination[0][0];
                        i <= this.plageDestination[1][0]; i++) {

                    for (int j = this.plageDestination[0][1];
                            j <= this.plageDestination[1][1]; j++) {
                        
                        // On récupère la cellule
                        String cellule = this.leTableur.
                                conversionCoordonneesEnChaine(new int[]{i,j});
                        
                        // On réinitialise la cellule
                        this.leTableur.reinitialisationCellule(cellule);
                        
                        // Puis on affecte
                        this.lesConsoles.getTableur().traitementCommande(
                                this.aAffecter + " = " + cellule);
                    }
                }
                
                // On garde une trace à afficher sur la console
                this.aRetourner = "La valeur de " + this.instructions[1]
                        + " a été copiée dans "
                        + ((dimDestination[0] == 1 && dimDestination[1] == 1) ?
                               (this.instructions[2] + ".") :
                               ("dans la plage " + this.instructions[2]) + ".");    
                
            } else {
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_INTIALISATION);
            }
        } else { // Le premier argument (à copier) est une plage
            // On récupère les coordonnées des plages
            this.plageCellules = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[1]);
            this.plageDestination = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[2]);
            
            // On récupère les valeurs dans un tableau
            String[][] aCopier = new String[dimSource[1]][dimSource[0]];
            
            for (int i = 0; i < aCopier.length; i++) {
                for (int j = 0; j < aCopier[i].length; j++) {
                    aCopier[i][j] = this.leTableur.restaurationValeurCellule(
                                this.leTableur.conversionCoordonneesEnChaine(
                                    new int[]{i + this.plageCellules[0][0],
                                            j + this.plageCellules[0][1]}));
                }
            }
            
            // On les affecte dans les nouvelles cellules
            for (int i = 0; i < aCopier.length; i++) {
                for (int j = 0; j < aCopier[i].length; j++) {        
                    // On récupère la cellule en chaîne
                    String cellule = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{
                                    i + this.plageDestination[0][0],
                                    j + this.plageDestination[0][1]});
                    
                    if (!aCopier[i][j].equals("")) {
                        // Cellule à copier initialisée
                        this.lesConsoles.getTableur().traitementCommande(
                                aCopier[i][j] + " = " + cellule);
                    } else {
                        // On réinitialise la cellule destination
                        this.leTableur.reinitialisationCellule(cellule);
                    }
                }
            }
            
            // On garde une trace à afficher sur la console
            this.aRetourner = "Les valeurs de la plage " + this.instructions[1]
                    + " ont été copiées dans la plage " + this.instructions[2] 
                    + ".";  
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire#raz()
     */
    @Override
    protected void raz() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("RAZ")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon
                
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la remise à 0 si la plage est correcte
        if (this.plageCellules != null) {
                        
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On récupère les coordonnées en chaînes
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});
                    
                    // On réinitialise la cellule
                    this.leTableur.reinitialisationCellule(tmpZone);

                    // Puis on affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, "0");

                    aRetourner.append(tmpZone + " a été remis à 0.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #controlePlageMemoire()
     */
    @Override
    protected boolean controlePlageMemoire() {
        
        switch (this.nbArgumentsCommande()) {
        case "0 ou 1" : // Commandes RAZ, CONT
            if (this.instructions.length == 1) {
                /*
                 * La plage n'est pas spécifiée donc on l'applique sur toutes 
                 * les cellules
                 */
                this.plageCellules = new int[][]{ {0,0}, {19,25} };
            } else {
                // On cherche à récupérer la ou les mémoires concernées
                this.plageCellules = 
                        this.memoiresAAffecter(this.instructions[1]);
            }
            break;
            
        case "1" : // Commande FORM
            // On cherche à récupérer la ou les mémoires concernées
            this.plageCellules = this.memoiresAAffecter(this.instructions[1]);
            
            // Si on a réussi à récupérer la plage
            if (this.plageCellules == null) {
                /* 
                 * Il n'y pas de mémoires ou de plages dans une commande qui en 
                 * demande une, donc on déclenche une erreur
                 */
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_MEMOIRE_ABSENTE);
                return false;   
            }
            
            if (!estUnePlageLigneOuColone(this.plageCellules)) {
                /*
                 * On ne pourra propager une formule que sur une même ligne ou 
                 * sur une même colone.
                 */
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_PLAGE_LIGNE_OU_COLONNE);
                return false; 
            }
            
            break;
            
        case "1 ou 2" : // Commande INIT
            // On cherche à récupérer la ou les mémoires concernées
            this.plageCellules = this.memoiresAAffecter(this.instructions[1]);
            
            // Si on a réussi à récupérer la plage
            if (this.plageCellules != null) {
                // Si on a réussi, on cherche la valeur qui suit
                if (this.instructions.length == 3) {
                    // Il y a un 3ème argument
                    if (estUnDouble(this.instructions[2])) {
                        this.aAffecter = this.instructions[2];
                    } else {
                        // Problème avec la valeur attendue
                        this.lieuMauvaisArgument = 2;
                        this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
                        return false;
                    }
                    
                } else {
                    /* 
                     * Il n'y pas de valeur dans une commande qui en demande 
                     * une, donc on déclenche une erreur
                     */
                    this.lieuMauvaisArgument = 2;
                    this.rechercheErreur(ERREUR_VALEUR_ABSENTE);
                    return false;
                }
            } else if (estUnDouble(this.instructions[1])) {
                // Pas de mémoires spécifiées
                this.plageCellules = new int[][]{ {0,0}, {19,25} };
                this.aAffecter = this.instructions[1];
            }
            break;
            
        case "2" : // Commandes de copies
            // On tente de récupérer les plages
            this.plageCellules = this.memoiresAAffecter(this.instructions[1]);
            this.plageDestination = this.memoiresAAffecter(
                    this.instructions[2].replace("$", ""));
            
            // On vérifie que la destination est bonne
            if (this.plageDestination == null) {
                this.lieuMauvaisArgument = 2;
                if (estUnePlageDeCellules(this.instructions[2])) {
                    // La plage n'est pas ordonnée
                    this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRE);
                } else {
                    // Ce n'est pas une plage ou une mémoire
                    this.rechercheErreur(ERREUR_PLAGE_MEMOIRE);
                }
                return false;
            }
            break;
        }
        
        if (this.plageCellules == null) {
            this.lieuMauvaisArgument = 1;
            if (estUnePlageDeCellules(this.instructions[1])) {
                // La plage n'est pas ordonnée
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRE);
            } else {
                // Ce n'est pas une plage ou une mémoire
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRE);
            }
            return false;
        }

        return true; // Pas d'erreurs trouvées
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #argumentsCommandeAttendus(java.lang.String)
     */
    @Override
    protected boolean argumentsCommandeAttendus(String commande) {
        
        switch (commande.toString()) {
        case "CONT" :
        case "RAZ" :
            // La commande et la plage (facultatif)
            return this.instructions.length <= 2;
        case "INIT" :
            // La commande, la plage mémoire (facultatif) et la valeur
            return this.instructions.length == 2
                    || this.instructions.length == 3;
        case "FORM" :
            // La commande et la plage mémoire
            return this.instructions.length == 2;
        case "COPIER" :
        case "COPVAL" :
            // La commande, la plage mémoire et la valeur
            return this.instructions.length == 3;
        
        }
        throw new IllegalArgumentException("Passage impossible");
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #nbArgumentsCommande()
     */
    @Override
    protected String nbArgumentsCommande() {

        switch (this.instructions[0].toString()) {
        case "CONT" :
        case "RAZ" :
            // La commande et la plage (facultatif) 
            return "0 ou 1";
        case "INIT" :       
            // La commande, la plage (facultatif) et la valeur
            return "1 ou 2";
        case "FORM" :
            // La commande et la plage
            return "1";
        case "COPIER" :
        case "COPVAL" :
            // La commande et deux plages
            return "2";
        }
        throw new IllegalArgumentException("Passage impossible");
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    protected boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_CELLULE).matcher(aTester).matches()
                || Pattern.compile(REGEX_CELLULE_BLOCAGE).
                       matcher(aTester).matches();
    }
    
    /**
     * Teste si une chaîne est une cellule ou une plage de celules
     * @param memoire Chaîne à convertir
     * @return plage de cellule en coordonnées s'il s'agit bien d'une cellule 
     *          ou d'une plage de celules, null sinon
     */
    protected int[][] memoiresAAffecter(String memoire) {
        
        if (estUneMemoire(memoire)) {
            // Une cellule donc la plage commence et finie au même endroit
            return new int[][]{
                    this.leTableur.conversionChaineEnCoordonnees(memoire),
                    this.leTableur.conversionChaineEnCoordonnees(memoire)};
        } 
        
        if (estUnePlageDeCellulesOrdonnees(memoire)) {
            // On récupère les coordonnées de la plage
            return this.leTableur.coordonneesPlageCorrecte(memoire);
        }
        return null; // s'il ne s'agit pas d'une cellule ou d'une plage
    }
    
    /**
     * Parcourt une formule afin de modifier les cellules non bloquées
     * @param origine Cellule d'origine de la formule
     * @param destination Cellule destination de la formule
     * @param formule Formule à modifier
     * @return la formule modifiée en fonction des '$' rencontrés ou non
     */
    private String modificationCellulesNonBloquee(String origine,
            String destination, String[] formule) {

        // On récupère les coordonnées des cellules sources et destination
        int[] coordonneesOrig = this.leTableur.conversionChaineEnCoordonnees(
                origine.replace("$", ""));
        int[] coordonneesDest = this.leTableur.conversionChaineEnCoordonnees(
                destination.replace("$", ""));

        
        int[] tmpNouvelle;  // Cellule temporaire lorsque l'une d'elle sera 
        					// modifiée

        // On parcourt la formule
        for (int i = 0; i < formule.length; i++) {
            // On tente une modification si on rencontre une cellule
            if (this.estUneMemoire(formule[i])) {
                // On récupère les coordonnées de la cellule rencontrée
                tmpNouvelle = this.leTableur.conversionChaineEnCoordonnees(
                        formule[i].replace("$", ""));

                /*
                 * On est susceptible de rencontrer quatre cas de figure :
                 *   - cellule pas bloquée du tout
                 *   - cellule bloquée uniquement sur la ligne
                 *   - cellule bloquée uniquement sur la colonne
                 *   - cellule bloquée sur la ligne et sur la colonne
                 */
                if (!estUneCellulebloqueeSurLaColone(formule[i])
                        && !estUneCellulebloqueeSurLaLigne(formule[i])) {
                    // Cellule pas bloquée du tout
                    tmpNouvelle[0] = tmpNouvelle[0] +
                            coordonneesDest[0] - coordonneesOrig[0];
                    tmpNouvelle[1] = tmpNouvelle[1] + 
                            coordonneesDest[1] - coordonneesOrig[1];

                    // On récupère la nouvelle cellule
                    formule[i] = this.leTableur.conversionCoordonneesEnChaine(
                            tmpNouvelle);

                } else if (!estUneCellulebloqueeSurLaColone(formule[i])
                        && estUneCellulebloqueeSurLaLigne(formule[i])) {
                    // Cellule bloqué seulement sur la ligne (chiffre)
                    tmpNouvelle[1] = tmpNouvelle[1] + 
                            coordonneesDest[1] - coordonneesOrig[1];

                    // On récupère la nouvelle cellule
                    formule[i] = this.leTableur.conversionCoordonneesEnChaine(
                            tmpNouvelle);
                    
                    // On remet le blocage en place
                    formule[i] = formule[i].charAt(0) + 
                            "$" + formule[i].substring(1,formule[i].length());
                    
                } else if (!estUneCellulebloqueeSurLaLigne(formule[i])
                        && estUneCellulebloqueeSurLaColone(formule[i])) {
                    // Cellule bloqué seulement sur la colonne (lettre)
                    tmpNouvelle[0] = tmpNouvelle[0]
                            + coordonneesDest[0] - coordonneesOrig[0];

                    // On récupère la nouvelle cellule
                    formule[i] = this.leTableur.conversionCoordonneesEnChaine(
                            tmpNouvelle);

                    // On remet le blocage en place
                    formule[i] = '$' + formule[i];
                } // else 
                /*
                 * La cellule est bloquée à la fois sur la ligne et sur la
                 * colonne. On effectue donc aucun changement.
                 */
            }
        }
        return tableauEnChaine(formule);
    }

    /**
     * Calcule la dimension d'une plage de cellule CORRECTE
     * @param plageCellules Plage à tester
     * @return dimension dans un tableau
     *          0 : longueur
     *          1 : hauteur
     */
    private static int[] dimensionPlage(int[][] plageCellules) {
        return new int[]{
                plageCellules[1][1] - plageCellules[0][1] + 1,
                plageCellules[1][0] - plageCellules[0][0] + 1
        };
    }

    
    
    /**
     * Contrôle si une chaîne de caractères est une plage de cellules
     * @param aTester Chaine à tester
     * @return true si la chaîne est une plage de cellules, false sinon
     */
    private static boolean estUnePlageDeCellules(String aTester) {
        return Pattern.compile(REGEX_PLAGE_CELLULES).matcher(aTester).matches();
    }
    
    /**
     * Contrôle si une chaîne de caractères est une cellule incluant un blocage
     * au niveau de la colonne (lettre)
     * @param aTester Chaîne à tester
     * @return true si la chaîne est une cellule bloquée sur la colonne,
     *         false sinon
     */
    private static boolean estUneCellulebloqueeSurLaColone(String aTester) {
        return Pattern.compile(REGEX_CELLULE_BLOCAGE_COLONE).
                matcher(aTester).matches();
    }
    
    /**
     * Contrôle si une chaîne de caractères est une cellule incluant un blocage
     * au niveau de la ligne (chiffre)
     * @param aTester Chaîne à tester
     * @return true si la chaîne est une cellule bloquée sur la ligne,
     *         false sinon
     */
    private static boolean estUneCellulebloqueeSurLaLigne(String aTester) {
        return Pattern.compile(REGEX_CELLULE_BLOCAGE_LIGNE).
                matcher(aTester).matches();
    }
    
    /**
     * Controle si une plage de cellules est une ligne ou une colonne
     * @param plageCellules Plage à tester
     * @return true s'il s'agit d'une 
     */
    private static boolean estUnePlageLigneOuColone(int[][] plageCellules) {
        return plageCellules[0][0] == plageCellules[1][0]
                || plageCellules[0][1] == plageCellules[1][1];
    }

    /**
     * Contrôle si une chaîne de caractères est une plage de cellules ordonnées
     * @param aTester Chaîne à tester
     * @return true si la chaîne est une plage de cellules, false sinon
     */
    public boolean estUnePlageDeCellulesOrdonnees(String aTester) {
        
        if (estUnePlageDeCellules(aTester)) {
            int[][] coordonnees = 
                    this.leTableur.coordonneesPlageCorrecte(aTester);
            
            return (coordonnees[0][0] <= coordonnees[1][0]
                    || coordonnees[0][0] == coordonnees[1][0])
                            && coordonnees[0][1] <= coordonnees[1][1];
        }
        return false;
    }

    /**
     * Convertit un tableau de chaîne de caractères en chaîne de caractères où
     * chaque élément est séparé par un espace
     * @param formule Tableau contenant la formule
     * @return formule en chaîne de caractère
     */
    private static String tableauEnChaine(String[] formule) {
        StringBuilder aRetourner = new StringBuilder();
        
        for (int i = 0; i < formule.length; i++) {
            aRetourner.append(formule[i] + " ");
        }
        return aRetourner.toString();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #estInitialisee(java.lang.String)
     */
    @Override
    protected boolean estInitialisee(String aTester) {
        return !this.leTableur.restaurationValeurCellule(aTester).equals("");
    }
}