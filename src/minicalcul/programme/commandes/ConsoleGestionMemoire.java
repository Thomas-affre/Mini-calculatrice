/* 
 * ConsoleGestionMemoire.java                            15 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet contenant des commandes permettant d'intéragir avec des zones mémoires
 * allant de A à Z.
 * Toutes les explications sur les commandes sont contenu dans le champ de
 * classe AIDE.
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
public class ConsoleGestionMemoire extends Console {
    
    /** Résumé des commandes de la gestion de la mémoire */
    public static final String AIDE = 
            "--------------- AIDE DU GESTIONNARE DE MEMOIRES --------------- \n"
            + " - RAZ [Zone ou plage mémoire]\n"
            + "\tRemet à 0 les zones mémoires spécifiées ou toutes s'il n'y a "
            + "\n\tpas d'argument.\n\n"
            + " - INCR [Zone ou plage mémoire]\n"
            + "\tAjoute 1 aux zones mémoires spécifiées ou à toutes s'il n'y a "
            + "\n\tpas d'argument.\n\n"
            + " - SOM [Zone ou plage mémoire]\n"
            + "\tAffiche la somme des zones mémoires spécifiées ou de toutes "
            + "\n\ts'il n'y a pas d'argument.\n\n"
            + " - PROD [Zone ou plage mémoire]\n"
            + "\tAffiche le produit des zones mémoires spécifiées ou de "
            + "toutes \n\ts'il n'y a pas d'argument.\n\n"
            + " - MOY [Zone ou plage mémoire]\n"
            + "\tAffiche la moyenne arithmétique des zones mémoires spécifiées "
            + "\n\tou de toutes s'il n'y a pas d'argument.\n\n"
            + " - SQRT [Zone ou plage mémoire]\n"
            + "\tRemplace la valeur des zones mémoires spécifiées ou de toutes "
            + "\n\ts'il n'y a pas d'argument, par leurs racines carrées "
            + "\n\trespectives.\n\n"
            + " - CAR [Zone ou plage mémoire]\n"
            + "\tRemplace la valeur des zones mémoires spécifiées ou de toutes "
            + "\n\ts'il n'y a pas d'argument, par leurs carrées "
            + "respectifs.\n\n"
            + " - INIT [Zone ou plage mémoire] {valeur}\n"
            + "\tInitialise les zones mémoires spécifiées ou toutes s'il n'y a "
            + "\n\tpas d'argument, à la valeur spécifiée.\n\n"
            + " - ADD [Zone ou plage mémoire] {valeur}\n"
            + "\tAjoute à la valeur des zones mémoires spécifiées ou toutes "
            + "\n\ts'il n'y a pas d'argument, la valeur spécifiée.\n\n"    
            + " - MUL [Zone ou plage mémoire] {valeur}\n"
            + "\tMultiplie la valeur des zones mémoires spécifiées ou toutes "
            + "\n\ts'il n'y a pas d'argument, par la valeur spécifiée.\n\n"
            + " - EXP [Zone ou plage mémoire] {valeur}\n"
            + "\tElève la valeur des zones mémoires spécifiées, ou à toutes "
            + "\n\ts'il n'y a pas d'argument, à la puissance spécifiée.\n\n"
            + "[ ] : OPTIONNEL / { } : OBLIGATOIRE\n";
    
    /** Message affiché lorsqu'une plage mémoire est incorrecte */
    protected static final String MSG_PLAGE_MEMOIRE_MAUVAISE = "Erreur de "
            + "syntaxe : cet argument n'est pas une zone mémoire ou une plage "
            + "de zones mémoire.";
    
    /** Message affiché lorsqu'une plage mémoire n'est pas rengée */
    protected static final String MSG_PLAGE_MEMOIRE_NON_ORDONNEE = "Erreur "
            + "de syntaxe : les bornes de la plage mémoire "
            + "doivent être rangées dans l'ordre.";
    
    /** Message affiché lorsqu'une valeur est incorrecte */
    protected static final String MSG_VALEUR_MAUVAISE = "Erreur "
            + "d'argument : le deuxième argument doit être un réel.";
    
    /** Message affiché lorsqu'une valeur est attendue mais absente */
    protected static final String MSG_VALEUR_ABSENTE = "Erreur d'argument : "
            + "cette commande doit avoir une valeur en dernier argument.";
    
    /** Message affiché lorsqu'une plage entière n'est pas initialisée */
    protected static final String MSG_PLAGE_NON_INITIALISEE = "Erreur "
            + "d'initialisation : aucune zone de la plage n'est initialisée.";
    
    /** 
     * Plage des zones mémoires consernées par l'opération envoyée sous 
     * forme d'indices (A=0..Z=25)
     */
    private int[] plageMemoire;
    
    /** Valeur à affecter dans les commandes demandant une valeur (ex : INIT) */
    protected String aAffecter;

    /**
     * Constructeur de la console de gestionnaire de la mémoire
     * @param laFenetre Référence permettant d'accéder à la console et aux
     *                           zones mémoires
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
        // On commence par réinitialiser l'état de cette console
        this.reinitialisation();

        // On découpe la chaine avec les espaces
        this.commande = commande;
        this.instructions = commande.split(" ");

        // On regarde quelle instruction est demandée
        switch (this.instructions[0].toString()) {
        case "RAZ" : // Remise à zéro
            this.raz();
            break;
        case "INCR" : // Incrémentation
            this.incremente();
            break;
        case "SOM" : // Somme des cases et retour résultat
            this.somme();
            break;
        case "PROD" : // Produit des cases et retour résultat
            this.produit();
            break;
        case "MOY" : // Moyenne des cases et retour résultat
            this.moyenne();
            break;
        case "SQRT" : // Racine carré
            this.racineCarre();
            break;
        case "CAR" : // Carré
            this.carre();
            break;
        case "INIT" : // Initialisation zone mémoire
            this.initialisation();
            break;
        case "ADD" : // Ajoute valeur à la zone
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
        }        
    }

    /**
     * Remet à 0 les cases spécifiées en argument ou toutes si aucun argument
     * n'est spécifié
     */
    protected void raz() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("RAZ")) {
            return;     // Inutile de continuer
        }
        
        // On applique la remise à zéro si l'argument de RAZ est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                this.getLaFenetre().getLaMemoire().affectationMemoire(i, "0");
            }
            
            if (this.instructions.length == 1) {
                // Toutes les zones mémoires
                this.aRetourner = "Toutes les zones mémoires ont été remises" 
                        + " à zéro.";
                
            } else if (this.plageMemoire[0] != this.plageMemoire[1]) {
                // Plage de zones mémoire
                this.aRetourner = "Les zones mémoires de " 
                        + this.instructions[1].charAt(0) + " à "
                        + this.instructions[1].charAt(3)
                        + " ont été remises à zéro.";
            } else {
                // Une seule zone mémoire
                this.aRetourner = this.instructions[1].charAt(0)
                        + " a été remise à zéro.";
            }
        }
    }

    /**
     * Incrémente de 1 les cases spécifiées en argument ou toutes si la commande
     * est saisie sans argument
     */
    private void incremente() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("INCR")) {
            return;     // Inutile de continuer
        } 
        
        /*
         * On incrémente seulement les cases initialisées. Donc on garde une
         * trace des actions effectuées que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'incrémentation si l'argument de INCR est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    
                    // On prend le nouveau résultat
                    String resultat = Double.toString(Double.parseDouble(
                            this.contenuZoneMemoire(i)) + 1);
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append(tmpZone + " a été incrémentée.\n");
                } else {
                    aRetourner.append(tmpZone + " n'a pu être incrémentée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Affiche la somme des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument.
     */
    private void somme() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("SOM")) {
            return;     // Inutile de continuer
        }
        
        double somme = 0; // Somme a retourner
        
        for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
            // On garde la lettre de la zone (A = 65)
            String tmpZone = new Character((char) (65 + i)).toString();

            // On vérifie que la zone mémoire est initialisée
            if (estInitialisee(tmpZone)) {
                somme += Double.parseDouble(this.contenuZoneMemoire(i));
            }
        }

        // Comme c'est un double, on regarde si on peut enlevé la virgule
        this.aRetourner = "= " + (estUnEntier(Double.toString(somme)) ? 
              Integer.toString((int) Double.parseDouble(Double.toString(somme)))
                : Double.toString(somme));
    }

    /**
     * Affiche le produit des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument.
     */
    private void produit() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("PROD")) {
            return;     // Inutile de continuer
        }
        
        double produit = 1; // Produit a retourner
        boolean initialise = false; // = true si une zone est intialisée
        
        // On fait le produit si l'argument de PROD est bon ou inexistant
        for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
            // On garde la lettre de la zone (A = 65)
            String tmpZone = new Character((char) (65 + i)).toString();

            // On vérifie que la zone mémoire est initialisée
            if (estInitialisee(tmpZone)) {
                produit *= Double.parseDouble(this.contenuZoneMemoire(i));
                initialise = true;
            }
        }

        if (initialise) {
            // Comme c'est un double, on regarde si on peut enlevé la ,
            this.aRetourner = "= " + (estUnEntier(Double.toString(produit)) 
                    ? Integer.toString((int) Double.parseDouble(
                            Double.toString(produit))) 
                            : Double.toString(produit));
        } else {
            // Aucune Zone n'est initialisée
            this.aRetourner = MSG_PLAGE_NON_INITIALISEE;
        }
    }

    /**
     * Affiche la moyenne des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument.
     */
    private void moyenne() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("MOY")) {
            return;     // Inutile de continuer
        }
        
        double moyenne = 0; // Moyenne a retourner
        int nbNombre = 0;   // Compteur de nombres
        
        for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
            // On garde la lettre de la zone (A = 65)
            String tmpZone = new Character((char) (65 + i)).toString();

            // On vérifie que la zone mémoire est initialisée
            if (estInitialisee(tmpZone)) {
                moyenne += Double.parseDouble(this.contenuZoneMemoire(i));
                nbNombre++;
            }
        }

        if (nbNombre != 0) {
            moyenne /= nbNombre;
            // Comme c'est un double, on regarde si on peut enlevé la ,
            this.aRetourner = "= " + (estUnEntier(Double.toString(moyenne)) 
                    ? Integer.toString((int) Double.parseDouble(
                            Double.toString(moyenne))) 
                            // On tente d'arrondir le réel
                            : this.getARRONDIR().format(
                                 Double.parseDouble(Double.toString(moyenne))));
        } else {
            // Aucune Zone n'est initialisée
            this.aRetourner = MSG_PLAGE_NON_INITIALISEE;
        }
    }

    /**
     * Calcule la racine carrée des contenus des zones mémoires spécifiées en 
     * argument ou toutes les zones mémoires si la commande est saisie sans 
     * argument. Le résultat écrase le contenu de la zone mémoire si le calcul
     * est possible. Sinon il ne fait rien.
     */
    private void racineCarre() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("SQRT")) {
            return;     // Inutile de continuer
        }  
        
        /*
         * On remplace le contenu par la racine carré seulement si les cases
         * sont initialisées et si le contenu est positif. Donc on garde une
         * trace des actions effectuées que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la racine carré si l'argument de SQRT est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée et positive
                if (estInitialisee(tmpZone) && contenuMemoirePositif(tmpZone)) {
                    // On prend le contenu
                    double contenu = Double.parseDouble(
                            this.contenuZoneMemoire(i));
                    
                    // On prend le resultat qu'on arrondi
                    String resultat = Double.toString((double) Math.round(
                            Math.sqrt(contenu) * 10000) / 10000);
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append("Racine de " + tmpZone + " = "
                            + resultat + "\n");
                } else {
                    aRetourner.append(tmpZone + " n'est pas initialisée"
                            + " ou son contenu est négatif.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Calcule le carré des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument.
     * Le résultat écrase le contenu de la zone mémoire. 
     */
    private void carre() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("CAR")) {
            return;     // Inutile de continuer
        }
        
        /*
         * On remplace le contenu par la racine carré seulement si les cases
         * sont initialisées. Donc on garde une trace des actions effectuées
         * que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique le carré si l'argument de CAR est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {                    
                    // On prend le nouveau résultat
                    String resultat = Double.toString(Double.parseDouble(
                            this.contenuZoneMemoire(i))
                            * Double.parseDouble(this.contenuZoneMemoire(i)));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append("Carré de " + tmpZone + " = "
                            + resultat + "\n");
                } else {
                    aRetourner.append(tmpZone + " n'est pas initialisée.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Initialise les zones mémoires spécifiées en premier argument avec la
     * valeur spécifiée en second argument.
     */
    protected void initialisation() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("INIT")) {
            return;     // Inutile de continuer
        }
        
        // On contrôle la valeur en l'arrondissant s'il s'agit d'un réel
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

                aRetourner.append(tmpZone + " a été inintialisée à "
                        + this.aAffecter + ".\n");
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Ajoute aux zones mémoires spécifiées en premier argument la valeur
     * spécifiée en second argument.    
     */
    private void addition() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("ADD")) {
            return;     // Inutile de continuer
        }
        
        // On contrôle la valeur en l'arrondissant s'il s'agit d'un réel
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    // On garde le résultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            + Double.parseDouble(this.aAffecter));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(this.aAffecter + " a été ajouté à "
                            + tmpZone + ".\n");
                    
                } else {
                    // La zone mémoire n'est pas initialisée
                    aRetourner.append(tmpZone + " n'a pu être modifiée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Multiplie les zones mémoires spécifiées en premier argument la valeur
     * spécifiée en second argument.    
     */
    private void multiplication() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("MUL")) {
            return;     // Inutile de continuer
        }
        
        // On contrôle la valeur en l'arrondissant s'il s'agit d'un réel
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    // On garde le résultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            * Double.parseDouble(this.aAffecter));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a été multipliée par "
                            + this.aAffecter + ".\n");
                    
                } else {
                    // La zone mémoire n'est pas initialisée
                    aRetourner.append(tmpZone + " n'a pu être modifiée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }

    /**
     * Eleve le contenu des zones mémoires spécifiées en premier argument à la
     * puissance spécifiée en second argument. 
     */
    private void exposant() {
        // On contrôle les arguments
        if (!this.controleValiditeArgument("EXP")) {
            return;     // Inutile de continuer
        }
        
        // On contrôle la valeur en l'arrondissant s'il s'agit d'un réel
        this.controleValeur();
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    // On garde le résultat
                    String resultat = Double.toString(Math.pow(
                            Double.parseDouble(this.contenuZoneMemoire(i)),
                            Double.parseDouble(this.aAffecter)));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a été élevée à la puissance  "
                            + this.aAffecter + ".\n");
                    
                } else {
                    // La zone mémoire n'est pas initialisée
                    aRetourner.append(tmpZone + " n'a pu être modifiée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.aRetourner = aRetourner.toString();
        }
    }
    
    /**
     * Contrôle si une commande a un bon nombre d'arguments et si ces éventuels
     * arguments sont corrects.
     * @param aControler Commande valide à contrôler
     * @return true si le contrôle s'est bien passé, false sinon
     */
    protected boolean controleValiditeArgument(String aControler) {
        return this.verificationNombreArgument(aControler)
                && this.controlePlageMemoire();
    }
    
    /**
     * Vérifie si une commande a le bon nombre d'arguments
     * @param commande Commande à vérifier
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
     * Vérifie la validité d'une valeur à affecter et l'arrondi si la valeur
     * est juste.
     */
    protected void controleValeur() {
        
        if (estUnDouble(this.aAffecter)) {
            // On contrôle si on peut enlever la virgule s'il y en a une
            this.aAffecter = estUnEntier(this.aAffecter) ? Integer.toString(
                    (int) Double.parseDouble(this.aAffecter))
                    // On tente d'arrondir le réel
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
     * Contrôle si le deuxième argument d'une commande est valide. C'est-à-dire
     * s'il contient une zone mémoire (lettre majuscule) ou une plage mémoire
     * (ex : A..Z). Si c'est le cas, sauvegarde dans le tableau plageMemoire 
     * sous la forme des indices le début et la fin de la plage, sinon déclenche
     * une erreur
     * @return true si le contrôle s'est bien passé, false sinon
     */
    protected boolean controlePlageMemoire() {
        
        if (this.instructions.length == 1) {
            /*
             * Il s'agit d'une commande prenant 0 ou 1 argument. Donc s'il n'y
             * a pas d'argument, la commande s'applique à toutes les zones
             * mémoires.
             */
            this.plageMemoire = new int[]{0,25};
            
        } else if (estUneMemoire(this.instructions[1])
                || estUnePlageCorrecte(this.instructions[1]) == 0) {
            
            if (estUneMemoire(this.instructions[1])) {
                // La plage commence et fini au même endroit
                this.plageMemoire = new int[]{
                        this.instructions[1].charAt(0) - 65,
                        this.instructions[1].charAt(0) - 65};
            } else {
                // On marque le début et la fin de la plage
                this.plageMemoire = new int[]{
                        this.instructions[1].charAt(0) - 65,
                        this.instructions[1].charAt(3) - 65};
            }
            
            // Si la commande demande une valeur en plus de la plage
            if (this.nbArgumentsCommande().equals("1 ou 2")) {
                
                // Si la valeur est présente
                if (this.instructions.length == 3) {
                    this.aAffecter = this.instructions[2];
                } else {
                    /* 
                     * Il n'y pas de valeur dans une commande qui en demande 
                     * une, donc on déclenche une erreur
                     */
                    this.lieuMauvaisArgument = 2;
                    this.rechercheErreur(ERREUR_VALEUR_ABSENTE);
                    return false;
                }
            }
            
        } else if (this.nbArgumentsCommande().equals("1 ou 2")
                && estUnDouble(this.instructions[1])) {
            /*
             * Il n'y a pas de plages mémoires spécifiées dans une commande
             * prenant une plage et un réel en argument. Donc, on l'applique à
             * toutes les cellules et on récupère le contenu du premier argument
             */
            this.plageMemoire = new int[]{0,25};
            this.aAffecter = this.instructions[1]; 

        } else { // Erreur dans la plage mémoire
            this.erreurTrouvee = true;
            this.lieuMauvaisArgument = 1;
            if (estUnePlageCorrecte(this.instructions[1]) == 2) {
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRE);
            } else {
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRE);
            }
            return false;
        }
        
        return true; // Le contrôle s'est bien passé
    }

    /**
     * Regarde si le nombre d'arguments qui est attendu pour la commande passée
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
            // La commande, la plage mémoire (optionnel) et la valeur
            return this.instructions.length == 2 
                    || this.instructions.length == 3;
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }
    
    /**
     * @return Chaine de caractères représenatnt le nombre d'arguments
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
            // La commande, la plage mémoire (optionel) et la valeur
            return "1 ou 2";
        }
        throw new IllegalArgumentException("Passage impossible");
    }

    /**
     * Controle si une chaine de 4 caractères est une plage mémoire avec des
     * bornes dans le bon ordre
     * @param aTester Chaine à tester
     * @return 0 si la chaine est bien une plage mémoire
     *         1 si le format n'est pas bon
     *         2 si le format est bon mais les bornes ne sont pas bonnes
     */
    public static int estUnePlageCorrecte(String aTester) {
        
        if (Pattern.compile(REGEX_PLAGE_MEMOIRES).matcher(aTester).matches()) {          
            // Première lettre avant la dernière
            if (aTester.charAt(0) <= aTester.charAt(3)) {
                return 0;
            } else {
                return 2;
            }
        }
        return 1;
    }
    
    /**
     * Cherche le contenu d'une zone mémoire
     * @param indice Indice de la zone mémoire a rechercher (A = 0.. Z = 25)
     * @return Contenu de la zone mémoire
     */
    private String contenuZoneMemoire(int indice) {
        return this.getLaFenetre().getLaMemoire()
                .getContenuZones()[indice].getText();
    }
    
    /**
     * Teste si le contenu d'une mémoire est positif
     * @param memoireATester Chaine représentant une mémoire
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