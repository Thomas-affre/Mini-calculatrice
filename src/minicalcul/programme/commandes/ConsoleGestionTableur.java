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
 * Objet contenant des commandes permettant d'int�ragir avec le tableur.
 * Toutes les explications sur les commandes sont contenu dans le champ de
 * classe AIDE.
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 0.6
 */
public class ConsoleGestionTableur extends ConsoleGestionMemoire {
    
    /** R�sum� des commandes de la gestion de la m�moire */
    public static final String AIDE = 
            "--------------- AIDE DU TABLEUR --------------- \n"
            + " - RAZ [Cellule ou plage de cellules]\n"
            + "\tRemet � 0 les cellules sp�cifi�es ou toutes s'il n'y a pas"
            + "\n\td'argument.\n\n"
            + " - CONT [Cellule ou plage de cellules]\n"
            + "\tAffiche le contenu de la ou les cellule(s) sp�cifi�e(s) ou "
            + "\n\ttoutes s'il n'y a pas d'arguments. Le contenu d�une cellule "
            + "\n\tcomporte la valeur de la cellule et �ventuellement la "
            + "\n\tformule qu�elle contient.\n\n"
            + " - INIT [Cellule ou plage de cellules] {valeur}\n" 
            + "\tInitialise le contenu de la ou les cellule(s) sp�cifi�e(s) "
            + "\n\tou de toutes si aucune plage n'est pr�cis�e, avec la valeur "
            + "\n\tmise en deuxi�me argument.\n\n"
            + " - FORM {Ligne ou colonne de cellules}\n"
            + "\tDemande � l'utilisateur via une bo�te de dialogue de saisir "
            + "\n\tla formule � appliquer sur la plage de cellule. Cette "
            + "\n\tcommande prend en compte l'utilisation des blocages ($)"
            + "\n\tde cellules.\n\n"
            + " - COPIER {Cellule} {Cellule}\n" 
            + "\tCopie le contenu de la premi�re cellule dans la deuxi�me.\n\n"
            + " - COPIER {Cellule} {Plage de cellules}\n"
            + "\tCopie le contenu de la cellule dans toutes les cellules de la "
            + "\n\tplage s�lectionn�e.\n\n"
            + " - COPIER {Plage de cellules} {Plage de cellules}\n"
            + "\tCopie le contenu des cellules de la plage s�lectionn�e dans "
            + "\n\tles cellules de la deuxi�me. La plage d�arriv�e doit �tre "
            + "\n\tsup�rieure ou �gale � la plage de d�part.\n\n"
            + " - COPVAL {Cellule} {Cellule}\n" 
            + "\tCopie la valeur de la premi�re cellule dans la deuxi�me.\n\n"
            + " - COPVAL {Cellule} {Plage de cellules}\n"
            + "\tCopie la valeur de la premi�re cellule dans toutes les "
            + "\n\tcellules de la plage du deuxi�me argument.\n\n"
            + " - COPVAL {Plage de cellules} {Plage de cellules}\n"
            + "\tCopie les valeurs des cellules de la plage s�lectionn�e dans "
            + "\n\tles cellules de la deuxi�me. La plage d�arriv�e doit �tre "
            + "\n\tsup�rieure ou �gale � la plage de d�part.\n\n"
            + " - AIDE sans arguments\n"
            + "\tAffiche des informations sur les commandes du tableur.\n\n"
            + "[ ] : OPTIONNEL / { } : OBLIGATOIRE\n";

    /** Message affich� lorsque une plage de cellules est incorrecte */
    protected static final String MSG_PLAGE_MEMOIRE_MAUVAISE = "Erreur de "
            + "syntaxe : cet argument n'est pas une cellule ou une plage "
            + "de cellules.";
    
    /** Message affich� lorsque une plage de cellules n'est pas rang�e */
    protected static final String MSG_PLAGE_MEMOIRE_NON_ORDONNEE = "Erreur "
            + "de syntaxe : les bornes de la plage de cellules "
            + "doivent �tre rang�es dans l'ordre.";
    
    /** Message affich� � cause d'un probl�me de dimensions lors d'une copie */
    protected static final Object MSG_MAUVAISE_DIMENSION = "Erreur de "
            + "dimensions : la dimension de la plage source doit �tre plus "
            + "petite que la plage destination.";

    /** Message affich� lorsqu'un cellule n'est pas initialis�e (copie) */
    protected static final Object MSG_CELLULE_NON_INITIALISEE = "Erreur "
            + "d'initialisation : la cellule � copier n'est pas initialis�e.";

    /** Message affich� lorsqu'il manque une m�moire dans une commande */
    protected static final String MSG_MEMOIRE_ABSENTE = "Erreur de format : "
            + "une cellule ou une plage de cellules correcte est attendue pour "
            + "cette commande.";

    /** Message affich� lorsque la plage doit �tre une ligne ou une colonne */
    protected static final String MSG_PLAGE_LIGNE_OU_COLONNE = "Erreur de "
            + "format : la plage de cellules attendue doit �tre une ligne ou "
            + "une colone.";

    /** Message affich� lorsqu'une affectation n'est pas demand�e */
    protected static final String MSG_AFFECTATION_INNATENDUE = "Erreur "
            + "d'affectation : aucune affectation n'est demand�e.";

    /** Commandes disponibles pour le tableur */
    public static final String[] COMMANDES =
        {"CONT", "INIT", "FORM", "COPIER", "COPVAL", "RAZ", "AIDE"};

    /** R�f�rence du tableur afin de pouvoir int�ragir avec */
    private Tableur leTableur;
    
    /** 
     * R�f�rence au groupe de consoles afin de pouvoir s'en servir pour 
     * des calculs sur le tableur
     */
    private Commandes lesConsoles;
        
    /** 
     * Plage de cellules consern�es par l'op�ration envoy�e sous forme
     * d'indices (A1=[0,0]..Z20[19,25]) 
     */
    private int[][] plageCellules;
    
    /** Plage de destination lors d'une copie */
    private int[][] plageDestination;
    
    /**
     * Constructeur de la console de gestionnaire des commandes du tableur
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     * @param lesConsoles R�f�rence au groupe de consoles afin de pouvoir s'en
     *                          servir pour des calculs sur le tableur
     * @param leTableur R�f�rence permettant d'acc�der au tableur
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
        // On commence par r�initialiser l'�tat de cette console
        this.reinitialisation();

        // On d�coupe la cha�ne avec les espaces
        this.commande = commande;
        this.instructions = commande.split(" ");
        
        // On regarde quelle instruction est demand�e
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
        case "RAZ" : // Remise � z�ro
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
        this.erreurTrouvee = true; // Erreur d�clench�e
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
     * contenu des cellules sp�cifi�es. C'est-�-dire valeur + �ventuellement la
     * formule.
     */
    private void contenu() {
        // On contr�le les arguments
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
        // On contr�le les arguments
        if (!this.controleValiditeArgument("INIT")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon
        
        /*
         * On r�cup�re le deuxi�me argument qui sera le contenu de la zone
         * s'il s'agit bien d'un double
         */
        if (estUnDouble(this.aAffecter)) {
            // On contr�le si on peut enlev� la virgule s'il y en a une
            this.aAffecter = estUnEntier(this.aAffecter) 
                    ? Integer.toString(
                            (int) Double.parseDouble(this.aAffecter)) 
                    : this.getARRONDIR().format(
                            Double.parseDouble(this.aAffecter));
                    
        } else {
            // La valeur est mauvaise (2�me argument)
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
                    
                    // On r�cup�re les coordonn�es en cha�nes
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});
                    
                    // On r�initialise la cellule
                    this.leTableur.reinitialisationCellule(tmpZone);

                    // Et on y affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, this.aAffecter);

                    aRetourner.append(tmpZone + " a �t� inintialis�e � "
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
        // On contr�le les arguments
        if (!this.controleValiditeArgument("FORM")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon    
                
        // On demande la formule dans une bo�te de dialogue
        this.aAffecter = (String) JOptionPane.showInputDialog(
                this.getLaFenetre(),
                "Saisissez la formule � appliquer � la plage",
                "Affectation d'une formule", JOptionPane.QUESTION_MESSAGE);

        if (this.aAffecter == null || this.aAffecter.equals("")) { 
            // Zone de saisie vide ou op�ration annul�e
            this.aRetourner = "Initialisation annul�e.";
            return;
        }

        // On v�rifie qu'aucune affectation n'est faite dans la formule
        for (int i = 0; i < this.aAffecter.length(); i++) {
            if (this.aAffecter.charAt(0) == '=') {
                this.lieuMauvaisArgument = i;
                this.rechercheErreur(ERREUR_AFFECTATION_INNATENDUE);
                return;
            }
        }
        
        StringBuilder aRetourner = new StringBuilder("");
        /*
         * On effectue la premi�re op�ration sur la premi�re cellule de la plage
         * � part. Si elle se passe bien, on pourra continuer sur le reste de 
         * la plage. 
         */
        this.lesConsoles.getTableur().traitementCommande(this.aAffecter + " = "
                + this.leTableur.conversionCoordonneesEnChaine(new int[]{
                        this.plageCellules[0][0],this.plageCellules[0][1]}));
        
        /*
         * On v�rifie l'�tat de la console de calcul du tableur. Si aucune
         * erreur n'a �t� d�clench�e, on continue.
         */
        if (this.lesConsoles.getTableur().erreurTrouvee) {
            // On retourne l'�tat de la console avec l'erreur
            this.aRetourner = "? " + this.lesConsoles.getTableur().commande 
                    + "\n" + this.lesConsoles.getTableur().aRetourner;
            return; // Et on s'arr�te la
        }            
        
        // Premi�re cellule de la plage
        String premiere = this.leTableur.conversionCoordonneesEnChaine(
                new int[]{this.plageCellules[0][0], this.plageCellules[0][1]});

        /*
         * On r�cup�re le contenu que l'on a affect� � la premi�re cellule de
         * la plage concern�e.
         */
        this.aAffecter = this.leTableur.restaurationContenuCellule(premiere);
        
        // Cha�ne qui contiendra la formule modifi�e � affecter
        String nouvelleFormule;
        
        // On parcourt chaque ligne
        for (int i = this.plageCellules[0][0];
                i <= this.plageCellules[1][0]; i++) {

            // Puis chaque colonne
            for (int j = this.plageCellules[0][1];
                    j <= this.plageCellules[1][1]; j++) {

                // On r�cup�re les coordonn�es d'insertions en cha�nes
                String tmpZone = this.leTableur.
                        conversionCoordonneesEnChaine(new int[]{i, j});
                
                // On remplace les cellules non bloqu�es de la formule
                nouvelleFormule = this.modificationCellulesNonBloquee(premiere, 
                        tmpZone, this.aAffecter.split(" "));
                                
                // On effectue l'op�ration
                this.lesConsoles.getTableur().traitementCommande(
                        nouvelleFormule + " = " + tmpZone);
                
                // On garde une trace de l'op�ration
                aRetourner.append(tmpZone + " modifi�e.\n");
            }
        }
        this.aRetourner = aRetourner.toString();
    }

    /**
     * Copie la cellule ou la plage du premier argument dans la
     * cellule ou la plage du deuxi�me � condition que les dimensions des deux
     * arguments soient bonnes. C'est-�-dire :
     *  - Si la source est une cellule, la cellule sera copier dans toutes les
     *    cellules de la destination en faisant attention aux '$' de blocage de
     *    cellules.
     *  - Si la source est une plage, la plage source sera copier dans une plage
     *    destination qui devra �tre au moins de la m�me taille en faisant 
     *    attention aux '$' de blocage de cellules.     
     */
    private void copier() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("COPIER")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon   
        
        /*
         * On v�rifie les dimensions des plages afin d'�tre s�r que la source
         * puisse rentrer dans la destination.
         */
        int[] dimSource = dimensionPlage(this.plageCellules);
        int[] dimDestination = dimensionPlage(this.plageDestination);
        
        if (dimSource[0] > dimDestination[0] 
                || dimSource[1] > dimDestination[1]) {
            // Probl�me de dimension
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
                // On r�cup�re le contenu de la cellule source
                this.aAffecter = this.leTableur.restaurationContenuCellule(
                        this.instructions[1]);
                System.out.println("contenu : " + aAffecter);
                
                // On l'affecte � toutes les cellules de la plage
                for (int i = this.plageDestination[0][0];
                        i <= this.plageDestination[1][0]; i++) {

                    for (int j = this.plageDestination[0][1];
                            j <= this.plageDestination[1][1]; j++) {
                        
                        // On r�cup�re les coordonn�es d'insertions en cha�nes
                        String tmpZone = this.leTableur.
                                conversionCoordonneesEnChaine(new int[]{i, j});
                        
                        // On modifie les cellules non bloqu�es de la formule
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
             * Si la source est une plage, on la repporte dans les premi�res
             * cellules de la plage destination.
             */
            
            // On r�cup�re les coordonn�es des plages
            this.plageCellules = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[1]);
            this.plageDestination = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[2]);
            
            // On r�cup�re les contenus dans un tableau
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
                    // On r�cup�re la cellule en cha�ne
                    String cellule = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{
                                    i + this.plageDestination[0][0],
                                    j + this.plageDestination[0][1]});

                    // On modifie les cellules non bloqu�es de la formule
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
        
        // On garde une trace � afficher sur la console
        this.aRetourner = "Les contenus de la plage " + this.instructions[1]
                + " ont �t� copi�es dans la plage " + this.instructions[2] 
                + ".";  
    }

    /**
     * Copie la VALEUR de la cellule ou de la plage du premier argument dans la
     * cellule ou la plage du deuxi�me � condition que les dimensions des deux
     * arguments soient bonnes. C'est-�-dire :
     *  - Si la source est une cellule, la valeur sera copier dans toutes les
     *    cellules de la destination.
     *  - Si la source est une plage, la valeur sera copier dans une plage
     *    destination qui devra �tre au moins de la m�me taille.
     */
    private void copierValeur() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("COPVAL")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon   
        
        /*
         * On v�rifie les dimensions des plages afin d'�tre s�r que la source
         * puisse rentrer dans la destination
         */
        int[] dimSource = dimensionPlage(this.plageCellules);
        int[] dimDestination = dimensionPlage(this.plageDestination);
        
        if (dimSource[0] > dimDestination[0] 
                || dimSource[1] > dimDestination[1]) {
            // Probl�me de dimension
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
                // On r�cup�re la valeur de la cellule source
                this.aAffecter = this.leTableur.restaurationValeurCellule(
                        this.instructions[1]);
                
                // On l'affecte � toutes les cellules de la plage
                for (int i = this.plageDestination[0][0];
                        i <= this.plageDestination[1][0]; i++) {

                    for (int j = this.plageDestination[0][1];
                            j <= this.plageDestination[1][1]; j++) {
                        
                        // On r�cup�re la cellule
                        String cellule = this.leTableur.
                                conversionCoordonneesEnChaine(new int[]{i,j});
                        
                        // On r�initialise la cellule
                        this.leTableur.reinitialisationCellule(cellule);
                        
                        // Puis on affecte
                        this.lesConsoles.getTableur().traitementCommande(
                                this.aAffecter + " = " + cellule);
                    }
                }
                
                // On garde une trace � afficher sur la console
                this.aRetourner = "La valeur de " + this.instructions[1]
                        + " a �t� copi�e dans "
                        + ((dimDestination[0] == 1 && dimDestination[1] == 1) ?
                               (this.instructions[2] + ".") :
                               ("dans la plage " + this.instructions[2]) + ".");    
                
            } else {
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_INTIALISATION);
            }
        } else { // Le premier argument (� copier) est une plage
            // On r�cup�re les coordonn�es des plages
            this.plageCellules = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[1]);
            this.plageDestination = this.leTableur.coordonneesPlageCorrecte(
                    this.instructions[2]);
            
            // On r�cup�re les valeurs dans un tableau
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
                    // On r�cup�re la cellule en cha�ne
                    String cellule = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{
                                    i + this.plageDestination[0][0],
                                    j + this.plageDestination[0][1]});
                    
                    if (!aCopier[i][j].equals("")) {
                        // Cellule � copier initialis�e
                        this.lesConsoles.getTableur().traitementCommande(
                                aCopier[i][j] + " = " + cellule);
                    } else {
                        // On r�initialise la cellule destination
                        this.leTableur.reinitialisationCellule(cellule);
                    }
                }
            }
            
            // On garde une trace � afficher sur la console
            this.aRetourner = "Les valeurs de la plage " + this.instructions[1]
                    + " ont �t� copi�es dans la plage " + this.instructions[2] 
                    + ".";  
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire#raz()
     */
    @Override
    protected void raz() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("RAZ")) {
            return;     // Inutile de continuer
        } // else le nombre d'argument est bon
                
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la remise � 0 si la plage est correcte
        if (this.plageCellules != null) {
                        
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On r�cup�re les coordonn�es en cha�nes
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});
                    
                    // On r�initialise la cellule
                    this.leTableur.reinitialisationCellule(tmpZone);

                    // Puis on affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, "0");

                    aRetourner.append(tmpZone + " a �t� remis � 0.\n");
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
                 * La plage n'est pas sp�cifi�e donc on l'applique sur toutes 
                 * les cellules
                 */
                this.plageCellules = new int[][]{ {0,0}, {19,25} };
            } else {
                // On cherche � r�cup�rer la ou les m�moires concern�es
                this.plageCellules = 
                        this.memoiresAAffecter(this.instructions[1]);
            }
            break;
            
        case "1" : // Commande FORM
            // On cherche � r�cup�rer la ou les m�moires concern�es
            this.plageCellules = this.memoiresAAffecter(this.instructions[1]);
            
            // Si on a r�ussi � r�cup�rer la plage
            if (this.plageCellules == null) {
                /* 
                 * Il n'y pas de m�moires ou de plages dans une commande qui en 
                 * demande une, donc on d�clenche une erreur
                 */
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_MEMOIRE_ABSENTE);
                return false;   
            }
            
            if (!estUnePlageLigneOuColone(this.plageCellules)) {
                /*
                 * On ne pourra propager une formule que sur une m�me ligne ou 
                 * sur une m�me colone.
                 */
                this.lieuMauvaisArgument = 1;
                this.rechercheErreur(ERREUR_PLAGE_LIGNE_OU_COLONNE);
                return false; 
            }
            
            break;
            
        case "1 ou 2" : // Commande INIT
            // On cherche � r�cup�rer la ou les m�moires concern�es
            this.plageCellules = this.memoiresAAffecter(this.instructions[1]);
            
            // Si on a r�ussi � r�cup�rer la plage
            if (this.plageCellules != null) {
                // Si on a r�ussi, on cherche la valeur qui suit
                if (this.instructions.length == 3) {
                    // Il y a un 3�me argument
                    if (estUnDouble(this.instructions[2])) {
                        this.aAffecter = this.instructions[2];
                    } else {
                        // Probl�me avec la valeur attendue
                        this.lieuMauvaisArgument = 2;
                        this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
                        return false;
                    }
                    
                } else {
                    /* 
                     * Il n'y pas de valeur dans une commande qui en demande 
                     * une, donc on d�clenche une erreur
                     */
                    this.lieuMauvaisArgument = 2;
                    this.rechercheErreur(ERREUR_VALEUR_ABSENTE);
                    return false;
                }
            } else if (estUnDouble(this.instructions[1])) {
                // Pas de m�moires sp�cifi�es
                this.plageCellules = new int[][]{ {0,0}, {19,25} };
                this.aAffecter = this.instructions[1];
            }
            break;
            
        case "2" : // Commandes de copies
            // On tente de r�cup�rer les plages
            this.plageCellules = this.memoiresAAffecter(this.instructions[1]);
            this.plageDestination = this.memoiresAAffecter(
                    this.instructions[2].replace("$", ""));
            
            // On v�rifie que la destination est bonne
            if (this.plageDestination == null) {
                this.lieuMauvaisArgument = 2;
                if (estUnePlageDeCellules(this.instructions[2])) {
                    // La plage n'est pas ordonn�e
                    this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRE);
                } else {
                    // Ce n'est pas une plage ou une m�moire
                    this.rechercheErreur(ERREUR_PLAGE_MEMOIRE);
                }
                return false;
            }
            break;
        }
        
        if (this.plageCellules == null) {
            this.lieuMauvaisArgument = 1;
            if (estUnePlageDeCellules(this.instructions[1])) {
                // La plage n'est pas ordonn�e
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRE);
            } else {
                // Ce n'est pas une plage ou une m�moire
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRE);
            }
            return false;
        }

        return true; // Pas d'erreurs trouv�es
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
            // La commande, la plage m�moire (facultatif) et la valeur
            return this.instructions.length == 2
                    || this.instructions.length == 3;
        case "FORM" :
            // La commande et la plage m�moire
            return this.instructions.length == 2;
        case "COPIER" :
        case "COPVAL" :
            // La commande, la plage m�moire et la valeur
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
     * Teste si une cha�ne est une cellule ou une plage de celules
     * @param memoire Cha�ne � convertir
     * @return plage de cellule en coordonn�es s'il s'agit bien d'une cellule 
     *          ou d'une plage de celules, null sinon
     */
    protected int[][] memoiresAAffecter(String memoire) {
        
        if (estUneMemoire(memoire)) {
            // Une cellule donc la plage commence et finie au m�me endroit
            return new int[][]{
                    this.leTableur.conversionChaineEnCoordonnees(memoire),
                    this.leTableur.conversionChaineEnCoordonnees(memoire)};
        } 
        
        if (estUnePlageDeCellulesOrdonnees(memoire)) {
            // On r�cup�re les coordonn�es de la plage
            return this.leTableur.coordonneesPlageCorrecte(memoire);
        }
        return null; // s'il ne s'agit pas d'une cellule ou d'une plage
    }
    
    /**
     * Parcourt une formule afin de modifier les cellules non bloqu�es
     * @param origine Cellule d'origine de la formule
     * @param destination Cellule destination de la formule
     * @param formule Formule � modifier
     * @return la formule modifi�e en fonction des '$' rencontr�s ou non
     */
    private String modificationCellulesNonBloquee(String origine,
            String destination, String[] formule) {

        // On r�cup�re les coordonn�es des cellules sources et destination
        int[] coordonneesOrig = this.leTableur.conversionChaineEnCoordonnees(
                origine.replace("$", ""));
        int[] coordonneesDest = this.leTableur.conversionChaineEnCoordonnees(
                destination.replace("$", ""));

        
        int[] tmpNouvelle;  // Cellule temporaire lorsque l'une d'elle sera 
        					// modifi�e

        // On parcourt la formule
        for (int i = 0; i < formule.length; i++) {
            // On tente une modification si on rencontre une cellule
            if (this.estUneMemoire(formule[i])) {
                // On r�cup�re les coordonn�es de la cellule rencontr�e
                tmpNouvelle = this.leTableur.conversionChaineEnCoordonnees(
                        formule[i].replace("$", ""));

                /*
                 * On est susceptible de rencontrer quatre cas de figure :
                 *   - cellule pas bloqu�e du tout
                 *   - cellule bloqu�e uniquement sur la ligne
                 *   - cellule bloqu�e uniquement sur la colonne
                 *   - cellule bloqu�e sur la ligne et sur la colonne
                 */
                if (!estUneCellulebloqueeSurLaColone(formule[i])
                        && !estUneCellulebloqueeSurLaLigne(formule[i])) {
                    // Cellule pas bloqu�e du tout
                    tmpNouvelle[0] = tmpNouvelle[0] +
                            coordonneesDest[0] - coordonneesOrig[0];
                    tmpNouvelle[1] = tmpNouvelle[1] + 
                            coordonneesDest[1] - coordonneesOrig[1];

                    // On r�cup�re la nouvelle cellule
                    formule[i] = this.leTableur.conversionCoordonneesEnChaine(
                            tmpNouvelle);

                } else if (!estUneCellulebloqueeSurLaColone(formule[i])
                        && estUneCellulebloqueeSurLaLigne(formule[i])) {
                    // Cellule bloqu� seulement sur la ligne (chiffre)
                    tmpNouvelle[1] = tmpNouvelle[1] + 
                            coordonneesDest[1] - coordonneesOrig[1];

                    // On r�cup�re la nouvelle cellule
                    formule[i] = this.leTableur.conversionCoordonneesEnChaine(
                            tmpNouvelle);
                    
                    // On remet le blocage en place
                    formule[i] = formule[i].charAt(0) + 
                            "$" + formule[i].substring(1,formule[i].length());
                    
                } else if (!estUneCellulebloqueeSurLaLigne(formule[i])
                        && estUneCellulebloqueeSurLaColone(formule[i])) {
                    // Cellule bloqu� seulement sur la colonne (lettre)
                    tmpNouvelle[0] = tmpNouvelle[0]
                            + coordonneesDest[0] - coordonneesOrig[0];

                    // On r�cup�re la nouvelle cellule
                    formule[i] = this.leTableur.conversionCoordonneesEnChaine(
                            tmpNouvelle);

                    // On remet le blocage en place
                    formule[i] = '$' + formule[i];
                } // else 
                /*
                 * La cellule est bloqu�e � la fois sur la ligne et sur la
                 * colonne. On effectue donc aucun changement.
                 */
            }
        }
        return tableauEnChaine(formule);
    }

    /**
     * Calcule la dimension d'une plage de cellule CORRECTE
     * @param plageCellules Plage � tester
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
     * Contr�le si une cha�ne de caract�res est une plage de cellules
     * @param aTester Chaine � tester
     * @return true si la cha�ne est une plage de cellules, false sinon
     */
    private static boolean estUnePlageDeCellules(String aTester) {
        return Pattern.compile(REGEX_PLAGE_CELLULES).matcher(aTester).matches();
    }
    
    /**
     * Contr�le si une cha�ne de caract�res est une cellule incluant un blocage
     * au niveau de la colonne (lettre)
     * @param aTester Cha�ne � tester
     * @return true si la cha�ne est une cellule bloqu�e sur la colonne,
     *         false sinon
     */
    private static boolean estUneCellulebloqueeSurLaColone(String aTester) {
        return Pattern.compile(REGEX_CELLULE_BLOCAGE_COLONE).
                matcher(aTester).matches();
    }
    
    /**
     * Contr�le si une cha�ne de caract�res est une cellule incluant un blocage
     * au niveau de la ligne (chiffre)
     * @param aTester Cha�ne � tester
     * @return true si la cha�ne est une cellule bloqu�e sur la ligne,
     *         false sinon
     */
    private static boolean estUneCellulebloqueeSurLaLigne(String aTester) {
        return Pattern.compile(REGEX_CELLULE_BLOCAGE_LIGNE).
                matcher(aTester).matches();
    }
    
    /**
     * Controle si une plage de cellules est une ligne ou une colonne
     * @param plageCellules Plage � tester
     * @return true s'il s'agit d'une 
     */
    private static boolean estUnePlageLigneOuColone(int[][] plageCellules) {
        return plageCellules[0][0] == plageCellules[1][0]
                || plageCellules[0][1] == plageCellules[1][1];
    }

    /**
     * Contr�le si une cha�ne de caract�res est une plage de cellules ordonn�es
     * @param aTester Cha�ne � tester
     * @return true si la cha�ne est une plage de cellules, false sinon
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
     * Convertit un tableau de cha�ne de caract�res en cha�ne de caract�res o�
     * chaque �l�ment est s�par� par un espace
     * @param formule Tableau contenant la formule
     * @return formule en cha�ne de caract�re
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