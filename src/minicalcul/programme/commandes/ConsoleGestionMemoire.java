/* 
 * ConsoleGestionMemoire.java                            14 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet de gestion de la mémoire
 * @author Clément Zeghmati
 * @version 0.1
 */
public class ConsoleGestionMemoire extends Console {
    
    /** Résumé des commandes de la gestion de la mémoire */
    private static final String AIDE = 
            "--------------- AIDE DU GESTIONNARE DE MEMOIRES --------------- \n"
            + " - RAZ [Zone ou plage mémoire]\n"
            + "\tRemet à 0 les plages mémoires spécifiées ou toutes s'il n'y a "
            + "\n\tpas d'arguments.\n\n"
            + " - INCR [Zone ou plage mémoire]\n"
            + "\tAjoute 1 aux zones mémoires spécifiées ou à toutes s'il n'y a "
            + "\n\tpas d'arguments.\n\n"
            + " - SOM [Zone ou plage mémoire]\n"
            + "\tAffiche la somme des zones mémoires spécifiées ou de toutes "
            + "\n\ts'il n'y a pas d'arguments.\n\n"
            + " - PROD [Zone ou plage mémoire]\n"
            + "\tAffiche le produit des zones mémoires spécifiées ou de "
            + "toutes \n\ts'il n'y a pas d'arguments.\n\n"
            + " - MOY [Zone ou plage mémoire]\n"
            + "\tAffiche la moyenne des zones mémoires spécifiées ou toutes "
            + "\n\ts'il n'y a pas d'arguments.\n\n"
            + " - SQRT [Zone ou plage mémoire]\n"
            + "\tRemplace la valeur des cases mémoires spécifiées ou toutes "
            + "\n\ts'il n'y a pas d'arguments, par leurs racines carrées "
            + "\n\trespectives.\n\n"
            + " - CAR [Zone ou plage mémoire]\n"
            + "\tRemplace la valeur des cases mémoires spécifiées ou toutes "
            + "\n\ts'il n'y a pas d'arguments, par leurs carrées "
            + "respectifs.\n\n"
            + " - INIT {Zone ou plage mémoire} {valeur}\n"
            + "\tInitialise les zones mémoires spécifiées à la valeur "
            + "\n\tspécifiée.\n\n"
            + " - ADD {Zone ou plage mémoire} {valeur}\n"
            + "\tAjoute à la valeur des zones mémoires spécifiées la valeur "
            + "\n\tspécifiée.\n\n"    
            + " - MUL {Zone ou plage mémoire} {valeur}\n"
            + "\tMultiplie la valeur des zones mémoires spécifiées par la "
            + "\n\tvaleur spécifiée.\n\n"
            + " - EXP {Zone ou plage mémoire} {valeur}\n"
            + "\tElève la valeur des zones mémoires spécifiées à la puissance "
            + "\n\tspécifiée.\n\n"
            + "[ ] : OPTIONNEL / { } : OBLIGATOIRE\n";
    
    /** 
     * Plage des zones mémoires consernées par l'opération envoyée sous 
     * forme d'indices (A=0..Z=25)
     */
    private int[] plageMemoire;

    /**
     * Constructeur de la console de gestionnaire de la mémoire
     * @param laFenetre Référence permettant d'accéder à la console et aux
     *                           zones mémoires
     */
    public ConsoleGestionMemoire(FenetrePrincipale laFenetre) {
        this.setLaFenetre(laFenetre);
        // Nombre de chiffre apres la virgule
        this.getDf().setMaximumFractionDigits(5); 
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
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));

        // On regarde quelle instruction est demandée
        switch (this.getInstructions()[0].toString()) {
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
            this.setaRetourner(AIDE);
            break;
        default :
            this.setaRetourner("  ^\nErreur de syntaxe : la commande \""
                    + this.getInstructions()[0] + "\" n'existe pas.");
        }
        
        return this.getaRetourner();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#reinitialisation()
     */
    @Override
    public void reinitialisation() {
        this.setCommande(null);
        this.setInstructions(null);
        this.setErreurTrouvee(false);
        this.setaRetourner(null);
        this.setLieuMauvaisArgument(-1);
        this.plageMemoire = null;        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#rechercheErreur(int)
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
             + "\" n'est pas une zone mémoire ou une plage de zones mémoire.");
            break;
        case ERREUR_ORDRE_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : les bornes de la plage mémoire "
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
     * Remet à 0 les cases spécifiées en argument ou toutes si aucun argument
     * n'est spécifié
     */
    private void raz() {
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("RAZ")) {
            return;
        } // else le nombre d'argument est bon

        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();        
        
        // On applique la remise à zéro si l'argument de RAZ est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                this.getLaFenetre().getLaMemoire().affectationMemoire(i, "0");
            }
            
            // Toutes les zones mémoires
            if (this.getInstructions().length == 1) {
                // Toutes les zones mémoires
                this.setaRetourner("Toutes les zones mémoires ont été remises" 
                        + " à zéro.");
                
            } else if (this.plageMemoire[0] != this.plageMemoire[1]) {
                // Plage de zones mémoire
                this.setaRetourner("Les zones mémoires de " 
                        + this.getInstructions()[1].charAt(0) + " à "
                        + this.getInstructions()[1].charAt(3)
                        + " ont été remises à zéro.");
            } else {
                // Une seule zone mémoire
                this.setaRetourner(this.getInstructions()[1].charAt(0)
                        + " a été remis à zéro.");
            }
        }
    }

    /**
     * Incrémente de 1 les cases spécifiées en argument ou toutes si la commande
     * est saisie sans argument
     */
    private void incremente() {
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("INCR")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();    
        
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
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Affiche la somme des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument
     */
    private void somme() {
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("SOM")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        double somme = 0; // Somme a retourner
        
        // On fait la somme si l'argument de SOM est bon ou inexistant
        if (this.plageMemoire != null) { 
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    somme += Double.parseDouble(this.contenuZoneMemoire(i));
                }
            }
            
            // Comme c'est un double, on regarde si on peut enlevé la virgule
            this.setaRetourner("= " + (estUnEntier(Double.toString(somme)) ? 
              Integer.toString((int) Double.parseDouble(Double.toString(somme)))
                               : Double.toString(somme)));
        }
        
    }

    /**
     * Affiche le produit des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument
     */
    private void produit() {
        
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("PROD")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        double produit = 1; // Produit a retourner
        boolean initialise = false; // = true si une zone est intialisée
        
        // On fait le produit si l'argument de PROD est bon ou inexistant
        if (this.plageMemoire != null) { 
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
                this.setaRetourner("= " + (estUnEntier(Double.toString(produit)) 
                        ? Integer.toString((int) Double.parseDouble(
                                Double.toString(produit))) 
                                : Double.toString(produit)));
            } else {
                // Aucune Zone n'est initialisée
                this.setaRetourner("     ^\nErreur de calcul : Aucune zone"
                        + " n'est initialisée.");
            }
        }
    }

    /**
     * Affiche la moyenne des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans argument
     */
    private void moyenne() {
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("MOY")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        double moyenne = 0; // Moyenne a retourner
        int nbNombre = 0;   // Compteur de nombres
        
        // On fait le produit si l'argument de MOY est bon ou inexistant
        if (this.plageMemoire != null) { 
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
                this.setaRetourner("= " + (estUnEntier(Double.toString(moyenne)) 
                        ? Integer.toString((int) Double.parseDouble(
                                Double.toString(moyenne))) 
                                : Double.toString(moyenne)));
            } else {
                // Aucune Zone n'est initialisée
                this.setaRetourner("     ^\nErreur de calcul : Aucune zone"
                        + " n'est initialisée.");
            }
        }
    }

    /**
     * Calcule la racine carrée des contenus des zones mémoires spécifiées en 
     * argument ou toutes les zones mémoires si la commande est saisie sans 
     * argument. Le résultat écrase le contenu de la zone mémoire si le calcul
     * est possible. Sinon il ne fait rien.
     */
    private void racineCarre() {
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("SQRT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();    
        
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
                if (estInitialisee(tmpZone) && estPositif(tmpZone)) {
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
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Calcule le carré des contenus des zones mémoires spécifiées en argument
     * ou toutes les zones mémoires si la commande est saisie sans 
     * argument. Le résultat écrase le contenu de la zone mémoire. 
     */
    private void carre() {
        // On vérifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("CAR")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
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
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Initialise les zones mémoires spécifiées en premier argument avec la
     * valeur spécifiée en second argument.
     */
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
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();

                // On affecte la valeur
                this.getLaFenetre().getLaMemoire().affectationMemoire(i,valeur);

                aRetourner.append(tmpZone + " a été inintialisée à "
                        + valeur + ".\n");
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Ajoute aux zones mémoires spécifiées en premier argument la valeur
     * spécifiée en second argument.    
     */
    private void addition() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("ADD")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On récupère le deuxième argument qui sera ajouté au contenu de la 
         * zone s'il s'agit bien d'un double
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
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    // On garde le résultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            + Double.parseDouble(valeur));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(valeur + " a été ajouté à "
                            + tmpZone + ".\n");
                    
                } else {
                    // La zone mémoire n'est pas initialisée
                    aRetourner.append(tmpZone + " n'a pu être modifiée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Multiplie les zones mémoires spécifiées en premier argument la valeur
     * spécifiée en second argument.    
     */
    private void multiplication() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("MUL")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On récupère le deuxième argument qui sera multiplierpar le contenu de
         * la zone s'il s'agit bien d'un double
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
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    // On garde le résultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            * Double.parseDouble(valeur));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a été multipliée par "
                            + valeur + ".\n");
                    
                } else {
                    // La zone mémoire n'est pas initialisée
                    aRetourner.append(tmpZone + " n'a pu être modifiée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Eleve le contenu des zones mémoires spécifiées en premier argument à la
     * puissance spécifiée en second argument. 
     */
    private void exposant() {
        // On vérifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("EXP")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contrôle s'il n'y a qu'une zone mémoire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On récupère le deuxième argument qui sera l'exposant du contenu de
         * la zone s'il s'agit bien d'un double
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
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On vérifie que la zone mémoire est initialisée
                if (estInitialisee(tmpZone)) {
                    // On garde le résultat
                    String resultat = Double.toString(Math.pow(
                            Double.parseDouble(this.contenuZoneMemoire(i)),
                            Double.parseDouble(valeur)));
                    
                    // Comme c'est un double, on regarde si on peut enlevé la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a été élevée à la puissance  "
                            + valeur + ".\n");
                    
                } else {
                    // La zone mémoire n'est pas initialisée
                    aRetourner.append(tmpZone + " n'a pu être modifiée"
                            + " car elle n'est pas initialisée.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }
    
    /**
     * Vérifie si une commande a le bon nombre d'arguments
     * @param commande Commande à vérifier
     * @return true si le nombre d'arguments de la commande est bon, false sinon
     */
    public boolean verificationNombreArgument(String commande) {
        if (!argumentsCommandeAttendus(commande)) {
            this.setLieuMauvaisArgument(this.getInstructions().length);
            this.rechercheErreur(ERREUR_NB_ARGUMENTS);
            return false;
        }    
        return true;
    }

    /**
     * Contrôle si le deuxième argument d'une commande est valide. C'est-à-dire
     * s'il contient une zone mémoire (lettre majuscule) ou une plage mémoire
     * (ex : A..Z). Si c'est le cas, sauvegarde dans le tableau plageMemoire 
     * sous la forme des indices le début et la fin de la plage, sinon déclenche
     * une erreur
     */
    public void controlePlageMemoire() {
        
        if (this.getInstructions().length == 1) {
            // On l'applique sur toutes les mémoires
            this.plageMemoire = new int[]{0,25};

        } else if (this.getInstructions()[1].length() == 1 && // Une lettre
                estUneMemoire(this.getInstructions()[1])) {
            // La plage commence et fini au même endroit
            this.plageMemoire = new int[]{
                    this.getInstructions()[1].charAt(0) - 65,
                    this.getInstructions()[1].charAt(0) - 65};

        } else if (this.getInstructions()[1].length() == 4 &&
                estUnePlageCorrecte(this.getInstructions()[1]) == 0) {
            // 4 comme le nombre de caractères d'un plage (ex : A..Z)
            this.plageMemoire = new int[]{
                    this.getInstructions()[1].charAt(0) - 65,
                    this.getInstructions()[1].charAt(3) - 65};

        } else { // Erreur dans la plage mémoire
            this.setLieuMauvaisArgument(1);
            if (estUnePlageCorrecte(this.getInstructions()[1]) == 2) {
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRES);
            } else {
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRES);
            }
        }
    }

    /**
     * Regarde si le nombre d'arguments qui est attendu pour la commande passée
     * est juste
     * @param commande Commande dont on veut connaitre le nombre d'argument
     * @return true si le nombre d'argument est juste, false sinon
     */
    public boolean argumentsCommandeAttendus(String commande) {
        
        switch (commande.toString()) {
        case "RAZ" :
        case "INCR" :
        case "SOM" :
        case "PROD" :
        case "MOY" :
        case "SQRT" :
        case "CAR" :
            // La commande et la zone
            return this.getInstructions().length <= 2;
        case "INIT" :       
        case "ADD" :
        case "MUL" :
        case "EXP" :
            // La commande, la plage mémoire et la valeur
            return this.getInstructions().length == 3;
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }
    
    /**
     * @return Chaine de caractères représenatnt le nombre d'arguments
     *          attendus pour la commande saisie par l'utilisateur
     */
    public String nbArgumentsCommande() {
        
        switch (this.getInstructions()[0].toString()) {
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
            // La commande, la plage mémoire et la valeur
            return "2";
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }

    /**
     * Controle si une chaine de 4 caractères est une plage mémoire avec des
     * bornes dans le bon ordre
     * @param aTester Chaine à tester
     * @return 0 si la chine est bien une plage mémoire
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
     * Teste si le contenu d'une zone mémoire initialisée est positif
     * @param aTester Chaine à tester qui doit être une zone mémoire
     * @return true si la zone mémoire est initialisée, false sinon
     */
    private boolean estPositif(String aTester) {
        return this.getLaFenetre().getLaMemoire().getContenuZones()
                [aTester.charAt(0) - 65].getText().charAt(0) != '-';
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_ZONE_MEMOIRE).matcher(aTester).matches();
    }
}