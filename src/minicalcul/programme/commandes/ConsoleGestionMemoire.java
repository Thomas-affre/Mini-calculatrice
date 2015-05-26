/* 
 * ConsoleGestionMemoire.java                            15 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet contenant des commandes permettant d'int�ragir avec des zones m�moires
 * allant de A � Z.
 * Toutes les explications sur les commandes sont contenu dans le champ de
 * classe AIDE.
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public class ConsoleGestionMemoire extends Console {
    
    /** R�sum� des commandes de la gestion de la m�moire */
    public static final String AIDE = 
            "--------------- AIDE DU GESTIONNARE DE MEMOIRES --------------- \n"
            + " - RAZ [Zone ou plage m�moire]\n"
            + "\tRemet � 0 les zones m�moires sp�cifi�es ou toutes s'il n'y a "
            + "\n\tpas d'argument.\n\n"
            + " - INCR [Zone ou plage m�moire]\n"
            + "\tAjoute 1 aux zones m�moires sp�cifi�es ou � toutes s'il n'y a "
            + "\n\tpas d'argument.\n\n"
            + " - SOM [Zone ou plage m�moire]\n"
            + "\tAffiche la somme des zones m�moires sp�cifi�es ou de toutes "
            + "\n\ts'il n'y a pas d'argument.\n\n"
            + " - PROD [Zone ou plage m�moire]\n"
            + "\tAffiche le produit des zones m�moires sp�cifi�es ou de "
            + "toutes \n\ts'il n'y a pas d'argument.\n\n"
            + " - MOY [Zone ou plage m�moire]\n"
            + "\tAffiche la moyenne arithm�tique des zones m�moires sp�cifi�es "
            + "\n\tou de toutes s'il n'y a pas d'argument.\n\n"
            + " - SQRT [Zone ou plage m�moire]\n"
            + "\tRemplace la valeur des zones m�moires sp�cifi�es ou de toutes "
            + "\n\ts'il n'y a pas d'argument, par leurs racines carr�es "
            + "\n\trespectives.\n\n"
            + " - CAR [Zone ou plage m�moire]\n"
            + "\tRemplace la valeur des zones m�moires sp�cifi�es ou de toutes "
            + "\n\ts'il n'y a pas d'argument, par leurs carr�es "
            + "respectifs.\n\n"
            + " - INIT [Zone ou plage m�moire] {valeur}\n"
            + "\tInitialise les zones m�moires sp�cifi�es ou toutes s'il n'y a "
            + "\n\tpas d'argument, � la valeur sp�cifi�e.\n\n"
            + " - ADD [Zone ou plage m�moire] {valeur}\n"
            + "\tAjoute � la valeur des zones m�moires sp�cifi�es ou toutes "
            + "\n\ts'il n'y a pas d'argument, la valeur sp�cifi�e.\n\n"    
            + " - MUL [Zone ou plage m�moire] {valeur}\n"
            + "\tMultiplie la valeur des zones m�moires sp�cifi�es ou toutes "
            + "\n\ts'il n'y a pas d'argument, par la valeur sp�cifi�e.\n\n"
            + " - EXP [Zone ou plage m�moire] {valeur}\n"
            + "\tEl�ve la valeur des zones m�moires sp�cifi�es, ou � toutes "
            + "\n\ts'il n'y a pas d'argument, � la puissance sp�cifi�e.\n\n"
            + "[ ] : OPTIONNEL / { } : OBLIGATOIRE\n";
    
    /** Message affich� lorsqu'une plage m�moire est incorrecte */
    protected static final String MSG_PLAGE_MEMOIRE_MAUVAISE = "Erreur de "
            + "syntaxe : cet argument n'est pas une zone m�moire ou une plage "
            + "de zones m�moire.";
    
    /** Message affich� lorsqu'une plage m�moire n'est pas rang�e */
    protected static final String MSG_PLAGE_MEMOIRE_NON_ORDONNEE = "Erreur "
            + "de syntaxe : les bornes de la plage m�moire "
            + "doivent �tre rang�es dans l'ordre.";
    
    /** Message affich� lorsqu'une valeur est incorrecte */
    protected static final String MSG_VALEUR_MAUVAISE = "Erreur "
            + "d'argument : le deuxi�me argument doit �tre un r�el.";
    
    /** Message affich� lorsqu'une valeur est attendue mais absente */
    protected static final String MSG_VALEUR_ABSENTE = "Erreur d'argument : "
            + "cette commande doit avoir une valeur en dernier argument.";
    
    /** Message affich� lorsqu'une plage enti�re n'est pas initialis�e */
    protected static final String MSG_PLAGE_NON_INITIALISEE = "Erreur "
            + "d'initialisation : aucune zone de la plage n'est initialis�e.";
    
    /** 
     * Plage des zones m�moires consern�es par l'op�ration envoy�e sous 
     * forme d'indices (A=0..Z=25)
     */
    private int[] plageMemoire;
    
    /** Valeur � affecter dans les commandes demandant une valeur (ex : INIT) */
    protected String aAffecter;

    /**
     * Constructeur de la console de gestionnaire de la m�moire
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     */
    public ConsoleGestionMemoire(FenetrePrincipale laFenetre) {
        this.setLaFenetre(laFenetre);
        this.instructions = new String[1];
        this.reinitialisation();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par r�initialiser l'�tat de cette console
        this.reinitialisation();

        // On d�coupe la chaine avec les espaces
        this.commande = commande;
        this.instructions = commande.split(" ");

        // On regarde quelle instruction est demand�e
        switch (this.instructions[0].toString()) {
        case "RAZ" : // Remise � z�ro
            this.raz();
            break;
        case "INCR" : // Incr�mentation
            this.incremente();
            break;
        case "SOM" : // Somme des cases et retour r�sultat
            this.somme();
            break;
        case "PROD" : // Produit des cases et retour r�sultat
            this.produit();
            break;
        case "MOY" : // Moyenne des cases et retour r�sultat
            this.moyenne();
            break;
        case "SQRT" : // Racine carr�
            this.racineCarre();
            break;
        case "CAR" : // Carr�
            this.carre();
            break;
        case "INIT" : // Initialisation zone m�moire
            this.initialisation();
            break;
        case "ADD" : // Ajoute valeur � la zone
            this.addition();
            break;
        case "MUL" : // Multiplie la valeur par la zone
            this.multiplication();
            break;
        case "EXP" : // Eleve la valeur de la zone par l'exposant
            this.exposant();
            break;
        case "AIDE" : // Aide
            this.aRetourner = AIDE;
            break;
        default :
            // Commande inconnue
            this.aRetourner = "  ^\nErreur de syntaxe : la commande \""
                    +  this.instructions[0] + "\" n'existe pas.";
        }
        return this.aRetourner;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#reinitialisation()
     */
    @Override
    protected void reinitialisation() {
        this.commande = null;
        this.instructions = null;
        this.erreurTrouvee = false;
        this.aRetourner = null;
        this.lieuMauvaisArgument = 0;
        this.plageMemoire = null;     
        this.aAffecter = null;
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
        }        
    }

    /**
     * Remet � 0 les cases sp�cifi�es en argument ou toutes si aucun argument
     * n'est sp�cifi�
     */
    protected void raz() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("RAZ")) {
            return;     // Inutile de continuer
        }
        
        // On applique la remise � z�ro si l'argument de RAZ est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                this.getLaFenetre().getLaMemoire().affectationMemoire(i, "0");
            }
            
            if (this.instructions.length == 1) {
                // Toutes les zones m�moires
                this.aRetourner = "Toutes les zones m�moires ont �t� remises" 
                        + " � z�ro.";
                
            } else if (this.plageMemoire[0] != this.plageMemoire[1]) {
                // Plage de zones m�moire
                this.aRetourner = "Les zones m�moires de " 
                        + this.instructions[1].charAt(0) + " � "
                        + this.instructions[1].charAt(3)
                        + " ont �t� remises � z�ro.";
            } else {
                // Une seule zone m�moire
                this.aRetourner = this.instructions[1].charAt(0)
                        + " a �t� remise � z�ro.";
            }
        }
    }

    /**
     * Incr�mente de 1 les cases sp�cifi�es en argument ou toutes si la commande
     * est saisie sans argument
     */
    private void incremente() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("INCR")) {
            return;     // Inutile de continuer
        } 
        
        /*
         * On incr�mente seulement les cases initialis�es. Donc on garde une
         * trace des actions effectu�es que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'incr�mentation si l'argument de INCR est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    
                    // On prend le nouveau r�sultat
                    String resultat = Double.toString(Double.parseDouble(
                            this.contenuZoneMemoire(i)) + 1);
                    
                    // Comme c'est un double, on regarde si on peut enlev� la','
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append(tmpZone + " a �t� incr�ment�e.\n");
                } else {
                    aRetourner.append(tmpZone + " n'a pu �tre incr�ment�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Affiche la somme des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument.
     */
    private void somme() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("SOM")) {
            return;     // Inutile de continuer
        }
        
        double somme = 0; // Somme a retourner
        
        for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
            // On garde la lettre de la zone (A = 65)
            String tmpZone = new Character((char) (65 + i)).toString();

            // On v�rifie que la zone m�moire est initialis�e
            if (estInitialisee(tmpZone)) {
                somme += Double.parseDouble(this.contenuZoneMemoire(i));
            }
        }

        // Comme c'est un double, on regarde si on peut enlev� la virgule
        this.aRetourner = "= " + (estUnEntier(Double.toString(somme)) ? 
              Integer.toString((int) Double.parseDouble(Double.toString(somme)))
                : Double.toString(somme));
    }

    /**
     * Affiche le produit des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument.
     */
    private void produit() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("PROD")) {
            return;     // Inutile de continuer
        }
        
        double produit = 1; // Produit � retourner
        boolean initialise = false; // = true si une zone est intialis�e
        
        // On fait le produit si l'argument de PROD est bon ou inexistant
        for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
            // On garde la lettre de la zone (A = 65)
            String tmpZone = new Character((char) (65 + i)).toString();

            // On v�rifie que la zone m�moire est initialis�e
            if (estInitialisee(tmpZone)) {
                produit *= Double.parseDouble(this.contenuZoneMemoire(i));
                initialise = true;
            }
        }

        if (initialise) {
            // Comme c'est un double, on regarde si on peut enlev� la ','
            this.aRetourner = "= " + (estUnEntier(Double.toString(produit)) 
                    ? Integer.toString((int) Double.parseDouble(
                            Double.toString(produit))) 
                            : Double.toString(produit));
        } else {
            // Aucune Zone n'est initialis�e
            this.aRetourner = MSG_PLAGE_NON_INITIALISEE;
        }
    }

    /**
     * Affiche la moyenne des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument.
     */
    private void moyenne() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("MOY")) {
            return;     // Inutile de continuer
        }
        
        double moyenne = 0; // Moyenne a retourner
        int nbNombre = 0;   // Compteur de nombres
        
        for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
            // On garde la lettre de la zone (A = 65)
            String tmpZone = new Character((char) (65 + i)).toString();

            // On v�rifie que la zone m�moire est initialis�e
            if (estInitialisee(tmpZone)) {
                moyenne += Double.parseDouble(this.contenuZoneMemoire(i));
                nbNombre++;
            }
        }

        if (nbNombre != 0) {
            moyenne /= nbNombre;
            // Comme c'est un double, on regarde si on peut enlev� la ','
            this.aRetourner = "= " + (estUnEntier(Double.toString(moyenne)) 
                    ? Integer.toString((int) Double.parseDouble(
                            Double.toString(moyenne))) 
                            // On tente d'arrondir le r�el
                            : this.getARRONDIR().format(
                                 Double.parseDouble(Double.toString(moyenne))));
        } else {
            // Aucune Zone n'est initialis�e
            this.aRetourner = MSG_PLAGE_NON_INITIALISEE;
        }
    }

    /**
     * Calcule la racine carr�e des contenus des zones m�moires sp�cifi�es en 
     * argument ou toutes les zones m�moires si la commande est saisie sans 
     * argument. Le r�sultat �crase le contenu de la zone m�moire si le calcul
     * est possible. Sinon il ne fait rien.
     */
    private void racineCarre() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("SQRT")) {
            return;     // Inutile de continuer
        }  
        
        /*
         * On remplace le contenu par la racine carr� seulement si les cases
         * sont initialis�es et si le contenu est positif. Donc on garde une
         * trace des actions effectu�es que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la racine carr� si l'argument de SQRT est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e et positive
                if (estInitialisee(tmpZone) && contenuMemoirePositif(tmpZone)) {
                    // On prend le contenu
                    double contenu = Double.parseDouble(
                            this.contenuZoneMemoire(i));
                    
                    // On prend le resultat qu'on arrondi
                    String resultat = Double.toString((double) Math.round(
                            Math.sqrt(contenu) * 10000) / 10000);
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append("Racine de " + tmpZone + " = "
                            + resultat + "\n");
                } else {
                    aRetourner.append(tmpZone + " n'est pas initialis�e"
                            + " ou son contenu est n�gatif.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Calcule le carr� des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument.
     * Le r�sultat �crase le contenu de la zone m�moire. 
     */
    private void carre() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("CAR")) {
            return;     // Inutile de continuer
        }
        
        /*
         * On remplace le contenu par la racine carr� seulement si les cases
         * sont initialis�es. Donc on garde une trace des actions effectu�es
         * que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique le carr� si l'argument de CAR est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {                    
                    // On prend le nouveau r�sultat
                    String resultat = Double.toString(Double.parseDouble(
                            this.contenuZoneMemoire(i))
                            * Double.parseDouble(this.contenuZoneMemoire(i)));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append("Carr� de " + tmpZone + " = "
                            + resultat + "\n");
                } else {
                    aRetourner.append(tmpZone + " n'est pas initialis�e.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Initialise les zones m�moires sp�cifi�es en premier argument avec la
     * valeur sp�cifi�e en second argument.
     */
    protected void initialisation() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("INIT")) {
            return;     // Inutile de continuer
        }
        
        // On contr�le la valeur en l'arrondissant s'il s'agit d'un r�el
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();

                // On affecte la valeur
                this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                        this.aAffecter);

                aRetourner.append(tmpZone + " a �t� inintialis�e � "
                        + this.aAffecter + ".\n");
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Ajoute aux zones m�moires sp�cifi�es en premier argument la valeur
     * sp�cifi�e en second argument.    
     */
    private void addition() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("ADD")) {
            return;     // Inutile de continuer
        }
        
        // On contr�le la valeur en l'arrondissant s'il s'agit d'un r�el
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    // On garde le r�sultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            + Double.parseDouble(this.aAffecter));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(this.aAffecter + " a �t� ajout� � "
                            + tmpZone + ".\n");
                    
                } else {
                    // La zone m�moire n'est pas initialis�e
                    aRetourner.append(tmpZone + " n'a pu �tre modifi�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Multiplie les zones m�moires sp�cifi�es en premier argument la valeur
     * sp�cifi�e en second argument.    
     */
    private void multiplication() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("MUL")) {
            return;     // Inutile de continuer
        }
        
        // On contr�le la valeur en l'arrondissant s'il s'agit d'un r�el
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    // On garde le r�sultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            * Double.parseDouble(this.aAffecter));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a �t� multipli�e par "
                            + this.aAffecter + ".\n");
                    
                } else {
                    // La zone m�moire n'est pas initialis�e
                    aRetourner.append(tmpZone + " n'a pu �tre modifi�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * El�ve le contenu des zones m�moires sp�cifi�es en premier argument � la
     * puissance sp�cifi�e en second argument. 
     */
    private void exposant() {
        // On contr�le les arguments
        if (!this.controleValiditeArgument("EXP")) {
            return;     // Inutile de continuer
        }
        
        // On contr�le la valeur en l'arrondissant s'il s'agit d'un r�el
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    // On garde le r�sultat
                    String resultat = Double.toString(Math.pow(
                            Double.parseDouble(this.contenuZoneMemoire(i)),
                            Double.parseDouble(this.aAffecter)));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a �t� �lev�e � la puissance  "
                            + this.aAffecter + ".\n");
                    
                } else {
                    // La zone m�moire n'est pas initialis�e
                    aRetourner.append(tmpZone + " n'a pu �tre modifi�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }
    
    /**
     * Contr�le si une commande a un bon nombre d'arguments et si ces �ventuels
     * arguments sont corrects.
     * @param aControler Commande valide � contr�ler
     * @return true si le contr�le s'est bien pass�, false sinon
     */
    protected boolean controleValiditeArgument(String aControler) {
        return this.verificationNombreArgument(aControler)
                && this.controlePlageMemoire();
    }
    
    /**
     * V�rifie si une commande a le bon nombre d'arguments
     * @param commande Commande � v�rifier
     * @return true si le nombre d'arguments de la commande est bon, false sinon
     */
    protected boolean verificationNombreArgument(String commande) {
        if (!this.argumentsCommandeAttendus(commande)) {
            this.lieuMauvaisArgument = this.instructions.length;
            this.rechercheErreur(ERREUR_NB_ARGUMENT);
            return false;
        }    
        return true;
    }
    
    /** 
     * V�rifie la validit� d'une valeur � affecter et l'arrondi si la valeur
     * est juste.
     */
    protected void controleValeur() {
        
        if (estUnDouble(this.aAffecter)) {
            // On contr�le si on peut enlever la virgule s'il y en a une
            this.aAffecter = estUnEntier(this.aAffecter) ? Integer.toString(
                    (int) Double.parseDouble(this.aAffecter))
                    // On tente d'arrondir le r�el
                    : this.getARRONDIR().format(
                            Double.parseDouble(this.aAffecter));
                    
        } else {
            // La valeur est mauvaise (dernier argument)
            this.lieuMauvaisArgument = this.instructions.length - 1;
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
    }

    /**
     * Contr�le si le deuxi�me argument d'une commande est valide. C'est-�-dire
     * s'il contient une zone m�moire (lettre majuscule) ou une plage m�moire
     * (ex : A..Z). Si c'est le cas, sauvegarde dans le tableau plageMemoire 
     * sous la forme des indices le d�but et la fin de la plage, sinon d�clenche
     * une erreur
     * @return true si le contr�le s'est bien pass�, false sinon
     */
    protected boolean controlePlageMemoire() {
        
        if (this.instructions.length == 1) {
            /*
             * Il s'agit d'une commande prenant 0 ou 1 argument. Donc s'il n'y
             * a pas d'argument, la commande s'applique � toutes les zones
             * m�moires.
             */
            this.plageMemoire = new int[]{0,25};
            
        } else if (estUneMemoire(this.instructions[1])
                || estUnePlageCorrecte(this.instructions[1]) == 0) {
            
            if (estUneMemoire(this.instructions[1])) {
                // La plage commence et fini au m�me endroit
                this.plageMemoire = new int[]{
                        this.instructions[1].charAt(0) - 65,
                        this.instructions[1].charAt(0) - 65};
            } else {
                // On marque le d�but et la fin de la plage
                this.plageMemoire = new int[]{
                        this.instructions[1].charAt(0) - 65,
                        this.instructions[1].charAt(3) - 65};
            }
            
            // Si la commande demande une valeur en plus de la plage
            if (this.nbArgumentsCommande().equals("1 ou 2")) {
                
                // Si la valeur est pr�sente
                if (this.instructions.length == 3) {
                    this.aAffecter = this.instructions[2];
                } else {
                    /* 
                     * Il n'y pas de valeur dans une commande qui en demande 
                     * une, donc on d�clenche une erreur
                     */
                    this.lieuMauvaisArgument = 2;
                    this.rechercheErreur(ERREUR_VALEUR_ABSENTE);
                    return false;
                }
            }
            
        } else if (this.nbArgumentsCommande().equals("1 ou 2")
                && estUnDouble(this.instructions[1])) {
            /*
             * Il n'y a pas de plages m�moires sp�cifi�es dans une commande
             * prenant une plage et un r�el en argument. Donc, on l'applique �
             * toutes les cellules et on r�cup�re le contenu du premier argument
             */
            this.plageMemoire = new int[]{0,25};
            this.aAffecter = this.instructions[1]; 

        } else { // Erreur dans la plage m�moire
            this.erreurTrouvee = true;
            this.lieuMauvaisArgument = 1;
            if (estUnePlageCorrecte(this.instructions[1]) == 2) {
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRE);
            } else {
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRE);
            }
            return false;
        }
        
        return true; // Le contr�le s'est bien pass�
    }

    /**
     * Regarde si le nombre d'arguments qui est attendu pour la commande pass�e
     * est juste
     * @param commande Commande dont on veut connaitre le nombre d'argument
     * @return true si le nombre d'argument est juste, false sinon
     */
    protected boolean argumentsCommandeAttendus(String commande) {
        
        switch (commande.toString()) {
        case "RAZ" :
        case "INCR" :
        case "SOM" :
        case "PROD" :
        case "MOY" :
        case "SQRT" :
        case "CAR" :
            // La commande et la zone
            return this.instructions.length <= 2;
        case "INIT" :       
        case "ADD" :
        case "MUL" :
        case "EXP" :
            // La commande, la plage m�moire (optionnel) et la valeur
            return this.instructions.length == 2 
                    || this.instructions.length == 3;
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }
    
    /**
     * @return Chaine de caract�res repr�sentant le nombre d'arguments
     *          attendus pour la commande saisie par l'utilisateur
     */
    protected String nbArgumentsCommande() {
        
        switch (this.instructions[0].toString()) {
        case "RAZ" :
        case "INCR" :
        case "SOM" :
        case "PROD" :
        case "MOY" :
        case "SQRT" :
        case "CAR" :
            // La commande et la zone (facultatif)
            return "0 ou 1";
        case "INIT" :       
        case "ADD" :
        case "MUL" :
        case "EXP" :
            // La commande, la plage m�moire (optionel) et la valeur
            return "1 ou 2";
        }
        throw new IllegalArgumentException("Passage impossible");
    }

    /**
     * Contr�le si une chaine de 4 caract�res est une plage m�moire avec des
     * bornes dans le bon ordre
     * @param aTester Chaine � tester
     * @return 0 si la chaine est bien une plage m�moire
     *         1 si le format n'est pas bon
     *         2 si le format est bon mais les bornes ne sont pas bonnes
     */
    public static int estUnePlageCorrecte(String aTester) {
        
        if (Pattern.compile(REGEX_PLAGE_MEMOIRES).matcher(aTester).matches()) {          
            // Premi�re lettre avant la derni�re
            if (aTester.charAt(0) <= aTester.charAt(3)) {
                return 0;
            } else {
                return 2;
            }
        }
        return 1;
    }
    
    /**
     * Cherche le contenu d'une zone m�moire
     * @param indice Indice de la zone m�moire a rechercher (A = 0.. Z = 25)
     * @return Contenu de la zone m�moire
     */
    private String contenuZoneMemoire(int indice) {
        return this.getLaFenetre().getLaMemoire()
                .getContenuZones()[indice].getText();
    }
    
    /**
     * Teste si le contenu d'une m�moire est positif
     * @param memoireATester Chaine repr�sentant une m�moire
     * @return true si son contenu est positif, false sinon
     */
    private boolean contenuMemoirePositif(String memoireATester) {
        return estPositif(this.getLaFenetre().getLaMemoire().getContenuZones()
                [memoireATester.charAt(0) - 65].getText());
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    protected boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_ZONE_MEMOIRE).matcher(aTester).matches();
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #estInitialisee(java.lang.String)
     */
    @Override
    protected boolean estInitialisee(String aTester) {
        return !this.getLaFenetre().getLaMemoire()
                .getContenuZones()[aTester.charAt(0) - 65].getText().equals("");
    }
}