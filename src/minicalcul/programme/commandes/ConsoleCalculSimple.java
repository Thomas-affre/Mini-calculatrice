/* 
 * ConsoleCalculSimple.java                            15 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet effectuant des instructions de calculs simple (+, -, *, /) en acceptant
 * les parenthèses. Le résultat d'une opération pourra être affectée dans une
 * zone mémoire (A..Z) et son contenu pourra être récupéré dans une opération
 * grâce à son identifiant. Si l'opération est correcte, l'objet retourne le
 * résultat de l'opération, sinon il renvoie une erreur. 
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
public class ConsoleCalculSimple extends Console implements ConsoleCalcul {
    
    /** Message affiché lorsqu'il y a un problème avec les parenthèses */
    protected static final String MSG_MAUVAISES_PARENTHESES = "Erreur "
            + "de format : le nombre de parenthèses ouvrantes et fermantes "
            + "doit être équivalent.";

    /** Message affiché lorsqu'il y a un problème avec le dernier élément */
    protected static final String MSG_MAUVAISE_FIN =  "Erreur "
            + "de format : une opération ne peut pas se finir par un = ou "
            + "par un opérateur.";

    /** Message affiché lorsqu'il y a un problème avec le premier élément */
    protected static final String MSG_MAUVAIS_DEBUT = "Erreur de format : "
            + "une opération ne peut pas commencer par un =, par un opérateur "
            + "ou par une parenthèse fermante.";
    
    /** Message affiché lorsqu'il y a un problème d'affectation */
    protected static final String MSG_MAUVAISE_AFFECTATION = "Erreur "
            + "d'affectation : il ne peut y avoir au maximum qu'un seul égal.";
    
    /** Message affiché lorsqu'il y a des parenthèses après un égal */
    protected static final String MSG_PARENTHESE_APRES_EGAL = "Erreur de "
            + "format : il ne peut y avoir des parenthèses après une "
            + "affectation.";

    /** Message affiché lorsqu'il y a une opération apeès une affectation */
    protected static final String MSG_OPERATION_APRES_EGAL = "Erreur de "
            + "format : il ne doit pas y avoir d'opérations après une "
            + "affectation.";

    /** Liste des éléments disponible (en chaine) utilisé pour les messages */
    protected static final String[] ELEMENTS_EN_CHAINE =
        {"un réel", "un opérteur", "une parenthèse ouvrante",
        "une parenthèse fermante", "un égal", "une mémoire"};

    /**
     * Tableau de vérité des successeurs des caractères possibles de :
     *  - 0 : entier ou réel
     *  - 1 : operateur (+ - * /)
     *  - 2 : parenthèse ouvrante
     *  - 3 : parenthèse fermante
     *  - 4 : égal
     *  - 5 : mémoire
     */
    public static final boolean[][] VERITE_SUCCESSEURS = {
        {false, true , false, true , true , false},
        {true , false, true , false, false, true },
        {true , false, true , false, false, true },
        {false, true , false, true , true , false},
        {false, false, false, false, false, true },
        {false, true , false, true , true , false} };
        
    /**
     * Ordre de priorité des opérateurs allant de la priorité la plus forte à la
     * priorité la plus faible.
     */
    public static final String[] PRIORITE_OPERATEUR = {"/", "*", "+", "-"};

    /** 
     * Types des sous-chaines de la commande envoyée 
     *  - 0 : entier ou réel
     *  - 1 : operateur (+ - * /)
     *  - 2 : parenthèse ouvrante
     *  - 3 : parenthèse fermante
     *  - 4 : égal
     *  - 5 : mémoire
     */
    protected int[] typesSousChaines;
    
    /** Mémoire à laquelle le résultat sera affectée */
    protected String zoneAffecte;
        
    /** Nombre de paires de parenthèses dans l'expression envoyée */
    protected int nbParentheses;

    /**
     * Constructeur de la console de calculs
     * @param laFenetre Référence permettant d'accéder à la console et aux
     *                           zones mémoires
     */
    public ConsoleCalculSimple(FenetrePrincipale laFenetre) {
        this.setLaFenetre(laFenetre);
        
        /* 
         * On définit le format des réels qui seront arrondits à 2 chiffres 
         * après la virgule.
         */
        this.getARRONDIR().setRoundingMode(RoundingMode.HALF_UP);
    }
 
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par réinitialiser l'état de l'objet
        this.reinitialisation();
        
        /*
         * On découpe la chaine avec les espaces en conservant l'originale et
         * en enlevant les espaces multiples.
         */
        this.commande = commande.replace("  ", "");
        this.instructions = this.commande.split(" ");
        
        // On effectue tous les contrôles sur la commande
        this.controleCommande();

        // On peut faire le calcul s'il n'y a pas eu d'erreur trouvée
        if (!this.erreurTrouvee) {
            this.calculExpression();
        }
        
        // On retourne le résultat ou l'erreur
        return "> " + this.commande + "\n"
               + (!this.erreurTrouvee ? ("= " + this.instructions[0])
                       : this.aRetourner);
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
        this.typesSousChaines = null;
        this.zoneAffecte = null;
        this.nbParentheses = 0;           
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#controleCommande()
     */
    @Override
    public void controleCommande() {        
        // On vérifie la syntaxe des sous-chaines 
        if (!this.verificationSyntaxe()) {
            /*
             * On recherche le lieux de l'erreur et on retourne la localisation
             * avec un message.
             */
            this.rechercheErreur(ERREUR_SYNTAXE);
        }

        // On vérifie le format de la chaine s'il n'y a pas eu d'erreur avant
        if (!this.erreurTrouvee) {
            this.verificationFormat();
        }
        
        /*
         * Le nombre d'égal dans l'expression doit être égal à 0 ou 1 car il 
         * doit y avoir au maximum une seule affectation.
         */
        if (!this.erreurTrouvee) {
            this.verificationAffectation();            
        }

        /*
         * S'il n'y a pas eu d'erreur avant, on tente de resteurer le contenu
         * des zones mémoires qu'il pourrait y avoir dans la commande. Si la
         * restauration s'est bien passé, on peut continuer.
         */
        if (!this.erreurTrouvee && !this.restaurationSauvegarde()) {
            this.rechercheErreur(ERREUR_INTIALISATION);
        }    
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#calculExpression()
     */
    @Override
    public void calculExpression() {
        // Si le calcul comporte des parenthèses
        if (this.nbParentheses != 0) {
            /*
             * On effectue tous les calculs entre parenthèses et on enlève
             * toutes les parenthèses.
             */
            this.calculExpressionParenthese();

            /*
             * Tous les calculs entre parenthèses ont été fait et les 
             * parenthèses ont été enlevées. Il reste donc une opération 
             * simple que l'on effectue.
             */
            this.calculExpressionSimple(0, this.longueurInstruction() - 1);

        } else {
            // Pas de parenthèses
            this.calculExpressionSimple(0, this.instructions.length-1);           
        }

        // On arrondit le résultat
        this.arrondissementResultat();

        /*
         * Si l'on doit faire une affectation, on la fait en mettant à jour
         * les zones mémoire. 
         */
        if (this.zoneAffecte != null) {
            this.getLaFenetre().getLaMemoire().affectationMemoire(
                    this.caseAAffecter(), this.instructions[0]);
        }        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#rechercheErreur(int)
     */
    @Override
    protected void rechercheErreur(int typeErreur) {
        int posErreur = 0;
        this.erreurTrouvee = true; // Erreur déclenchée
        
        // Cette chaine correspond à l'erreur qui sera affichée sur la console
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

        // On rajoute le type d'erreur
        switch (typeErreur) {
        case ERREUR_SYNTAXE:
            this.aRetourner = tmpARetourner.append(
                    "Erreur de syntaxe : symbole \"" + this.instructions
                    [this.lieuMauvaisArgument]).toString() + "\" inconnu.";
            break;
            
        case ERREUR_MAUVAISE_FIN :
            this.aRetourner = tmpARetourner.append(MSG_MAUVAISE_FIN).toString();
            break;
            
        case ERREUR_MAUVAIS_DEBUT :
            this.aRetourner = tmpARetourner.append(
                    MSG_MAUVAIS_DEBUT).toString();
            break;
            
        case ERREUR_EQUIVALENCE_PARENTHESES :
            this.aRetourner = tmpARetourner.append(
                    MSG_MAUVAISES_PARENTHESES).toString();
            break;
            
        case ERREUR_MAUVAIS_SUCCESSEUR :
            this.aRetourner = tmpARetourner.append("Erreur de format : "
                    + ELEMENTS_EN_CHAINE
                    [this.typesSousChaines[this.lieuMauvaisArgument - 1]] 
                    +" ne peut être suivi d'" + ELEMENTS_EN_CHAINE
                    [this.typesSousChaines[this.lieuMauvaisArgument]] + ".")
                    .toString();
            break;
            
        case ERREUR_PARENTHESES_APRES_EGAL :
            this.aRetourner = tmpARetourner.append(
                    MSG_PARENTHESE_APRES_EGAL).toString();
            break;
            
        case ERREUR_AFFECTATION :
            this.aRetourner = tmpARetourner.append(
                    MSG_MAUVAISE_AFFECTATION).toString();
            break;
            
        case ERREUR_INTIALISATION:
            this.aRetourner = tmpARetourner.append(
                    "Erreur d'initialisation : \"" + this.instructions
                   [this.lieuMauvaisArgument]).toString()
                    + "\" n'a pas été initialisé.";
            break;
        case ERREUR_OPERATION_APRES_EGAL :
            this.aRetourner = tmpARetourner.append(
                    MSG_OPERATION_APRES_EGAL).toString();
            break;
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#verificationSyntaxe()
     */
    @Override
    public boolean verificationSyntaxe() {
        // On vérifie la syntaxe de chaque sous-chaine
        for (int i = 0; i < this.instructions.length
                && this.instructions[i] != null; i++) {
            /*
             * On doit traité le signe - de différentes manières en fonction de
             * comment l'utilisateur l'a placé et des espaces qui l'a pu laisser
             * avant ou pas. En effet, il peut être utilisé comme opérateur
             * unaire.
             */
            if (estUnDouble(this.instructions[i]) 
                    && !estPositif(this.instructions[i])) {
                // Réel négatif
                if (i != 0 && !(estUnOperateur(this.instructions[i-1])
                        || this.instructions[i-1].equals("("))) {
                    
                    this.separationElementsExpression(i);
                    i++;
                } 
            }
            
            // Syntaxe correcte : réel ou opérateur ou égal ou mémoire 
            if (!estUneSousChaine(this.instructions[i])) {
                int separation = this.separationElementsExpression(i);
    
                for (int j = i; j < i + separation; j++) {
                    if (!estUneSousChaine(this.instructions[j])) {
                        /*
                         * On garde l'indice du mauvais argument pour traiter l'
                         * erreur et afficher la partie où à eu lieu de l'erreur
                         */
                        this.lieuMauvaisArgument = j;
                        return false;
                    }
                }
    
                // On tente une séparation
                if (separation != 0) {
                    i += separation - 1;
                } else {
                    /*
                     * On garde l'indice du mauvais argument pour traiter 
                     * l'erreur et afficher la partie où à eu lieu de l'erreur
                     */
                    this.lieuMauvaisArgument = i;
                    return false;
                }
            }
        }
        return true; // Syntaxe correcte
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #separationElementsExpression(int)
     */
    @Override
    public int separationElementsExpression(int position) {
        // On récupère la chaine que l'on veut tenter de séparer
        int partieTraitee = 0;
        String aTester = new String(this.instructions[position]);
        ArrayList<String> liste = new ArrayList<String>();
        
        int i = 0; // Indice de passage
        
        // On traite le premier caractère à part s'il s'agit d'un -
        if (aTester.charAt(0) == '-' && (position == 0
                || (position != 0
                        && estUnOperateur(this.instructions[position-1])
                        || this.instructions[position-1].equals("(")))) {
            i = 1;
        }
                
        while (aTester.length() != 0) {
            
            if ((estUnCaractereValide(aTester.substring(i, i + 1))
                    || estUneMemoire(aTester.substring(i, i + 1)))) {
                
                if (i == 0) {
                    /*
                     * Si le premier caractère est un signe opératoire ou une 
                     * parenthèse
                     */
                    liste.add(aTester.substring(0, 1));
                    aTester = new String(
                            aTester.substring(1, aTester.length()));
                    partieTraitee++;
                } else {
                    /*
                     * Si on a atteint un signe opératoire ou une parenthèse et
                     * qu'il y avait avant un réel juste avant
                     */
                    liste.add(aTester.substring(0, i));    // Réel
                    liste.add(aTester.substring(i, i+1));  // Caractère
                    
                    aTester = aTester.length() == i ? "" : // Fin de chaine
                           new String(aTester.substring(i+1, aTester.length()));
                    
                    partieTraitee += 2;
                    i = 0;
                }
            } else {
                /*
                 * S'il s'agit d'un chiffre, on continue le parcourt sans rien
                 * faire jusqu'au prochain signe opératoire...
                 */
                i++;
                
                // ...sauf dans le cas où l'on a atteint la fin de la chaine.
                if (i == aTester.length()) {
                    liste.add(aTester);
                    aTester = "";
                    partieTraitee++;
                }
            }
        }
                
        // On créé la nouvelle commande avec les bons espaces pour le traitement
        StringBuilder nouvelle = new StringBuilder();
        
        // On prend ce qu'il y avait avant
        for (i = 0; i < position; i++) {
            nouvelle.append(this.instructions[i] + " ");
        }
        
        // On ajoute ce qu'on a obtenu dans ce traitement
        for (i = 0; i < liste.size(); i++) {
            nouvelle.append(liste.get(i) + " ");
        }
        
        // Puis ce qu'il y a après
        for (i = position + 1; i < this.instructions.length; i++) {
            nouvelle.append(this.instructions[i] + " ");
        }
        
        this.commande = nouvelle.toString();
        this.instructions = this.commande.split(" ");
        
        return partieTraitee;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#verificationFormat()
     */
    @Override
    public boolean verificationFormat() {
        /*
         * Le tableau des successeurs ne peut pas tout contrôler. Il faut
         * d'abord vérifier le dernier élément qui ne peut être un opérateur
         * ou un égale. S'il y a une erreur, on retourne la position de la
         * dernière sous-chaine.
         */
        if (this.instructions[this.instructions.length - 1].equals("=")
                || estUnOperateur(this.instructions
                                [this.instructions.length - 1])) {
            this.lieuMauvaisArgument = this.instructions.length - 1;
            this.rechercheErreur(ERREUR_MAUVAISE_FIN);
            this.erreurTrouvee = true;
        }
        
        /*
         * On vérifie également le premier élément qui doit être un réel ou une
         * zone mémoire ou une parenthèse ouvrante
         */
        if (!this.erreurTrouvee && !(estUnDouble(this.instructions[0]) 
                || this.instructions[0].equals("(")
                || estUneMemoire(this.instructions[0]))) {
            this.lieuMauvaisArgument = 0;
            this.rechercheErreur(ERREUR_MAUVAIS_DEBUT);
            this.erreurTrouvee = true;
        }
        
        // Le reste des vérifications se fera grâce au tableau des successeurs.
        
        /*
         * Types rencontrés tel que : 
         *      - 0 = réel
         *      - 1 = operateur (+ - * /)
         *      - 2 = parenthèse ouvrante
         *      - 3 = parenthèse fermante  
         *      - 4 = égale
         *      - 5 = zone mémoire ou cellule
         */
        this.typesSousChaines = new int[this.instructions.length];

        int ouvrante = 0, // Compteur des parenthèses ouvrantes
            fermante = 0; // Compteur des parenthèses fermantes

        /*
         * On récupère les types de chaque sous chaine pour vérifier chaque
         * successeur.
         */
        for (int i = 0; i < this.instructions.length; i++) {

            if (this.instructions[i].charAt(0) == '(') {
                ouvrante++;
                this.typesSousChaines[i] = 2;
            } else if (this.instructions[i].charAt(0) == ')') {
                fermante++;
                this.typesSousChaines[i] = 3;
            } else if (estUnOperateur(this.instructions[i])) {
                // Opérateur
                this.typesSousChaines[i] = 1;                
            } else if (estUnDouble(this.instructions[i])) {
                // Double
                this.typesSousChaines[i] = 0;
            } else if (this.instructions[i].charAt(0) == '=') {
                // Egale
                this.typesSousChaines[i] = 4;
            } else if (estUneMemoire(this.instructions[i])) {
                // Zone mémoire
                this.typesSousChaines[i] = 5;
            }
        }
        
        // Si il n'y a pas autant d'ouvrantes que de fermantes, erreur de format
        if (!this.erreurTrouvee && ouvrante != fermante) {
            this.lieuMauvaisArgument = this.instructions.length;
            this.rechercheErreur(ERREUR_EQUIVALENCE_PARENTHESES);
            this.erreurTrouvee = true;
        }
        
        // On effectue les contrôles grâce au tableau des successeurs
        if (!this.erreurTrouvee) {
            this.nbParentheses = ouvrante; // On garde le nombre de paires
            
            for (int i = 0; i < this.instructions.length - 1; i++) {
                if (!VERITE_SUCCESSEURS[this.typesSousChaines[i]]
                        [this.typesSousChaines[i+1]]) {
                    // Mauvais successeur
                    this.lieuMauvaisArgument = i + 1;
                    this.rechercheErreur(ERREUR_MAUVAIS_SUCCESSEUR);
                    this.erreurTrouvee = true;
                }
            }
        }
        return !this.erreurTrouvee;  // Le format est bon, pas d'erreur trouvée
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #verificationAffectation()
     */
    @Override
    public boolean verificationAffectation() {
        /*
         * On contrôle qu'il n'y ait pas d'égal ou s'il y en a 1, qu'il soit 
         * suivit d'une lettre. S'il y a une affectation, on conserve la zone 
         * où l'on effectuera la sauvegarde
         */
        int compteur = this.nombreEgalExpression();
        
        if (compteur == 0 || (compteur == 1 && this.affectationCorrecte())) {
            return true;
        } // else mauvaise affectation

        /*
         * Il est possible que l'utilisateur ait utilisé des parenthèses après
         * un égal donc on vérifie.
         */
        for (int i = this.typesSousChaines.length - 1; i >= 0; i--) {
            
            if (this.typesSousChaines[i] == 2 
                    || this.typesSousChaines[i] == 3) {
                this.lieuMauvaisArgument = i;
                this.rechercheErreur(ERREUR_PARENTHESES_APRES_EGAL);
                
                /*
                 * L'erreur n'étant pas dans l'affectation, on renvoie true 
                 * afin qu'on ne traite pas une mauvaise erreur.
                 */
                return true;
            }
        }
        
        // On vérifie qu'il n'y ait pas d'opération après le égal
        if (compteur == 1) { // Le égal est mal placé
            this.lieuMauvaisArgument = this.instructions.length - 1;
            this.rechercheErreur(ERREUR_OPERATION_APRES_EGAL);
            return false;
        }
        
        this.lieuMauvaisArgument = this.instructions.length;
        this.rechercheErreur(ERREUR_AFFECTATION);
        return false;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #restaurationSauvegarde()
     */
    @Override
    public boolean restaurationSauvegarde() {
        // On peut s'arrêter de rechercher lorsqu'on rencontre un égale
        for (int i = 0; i < this.instructions.length
                && !this.instructions[i].equals("="); i++) {
            
            if (estUneMemoire(this.instructions[i])) {
                /*
                 * S'il s'agit d'une zone mémoire, on vérifie si elle est bien
                 * initialisée.
                 */
                if (this.estInitialisee(this.instructions[i])) {
                    this.instructions[i] = this.getLaFenetre()
                       .getLaMemoire().valeurMemoire(this.instructions[i]);
                    
                } else { // Erreur : zone non initialisée
                    this.lieuMauvaisArgument = i;
                    return false;  // On sort ici car l'opération est impossible
                }
            }
        }
        return true; // Les restaurations éventuelles se sont bien déroulées
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#affectationCorrecte()
     */
    @Override
    public boolean affectationCorrecte() {
        /*
         * S'il n'y a qu'un égale, il se situe en avant dernière position. Et
         * le dernier caractère est une mémoire. Sinon il y a une erreur. 
         */
        if (this.typesSousChaines[this.typesSousChaines.length - 2] == 4 &&
               estUneMemoire(this.instructions[this.instructions.length - 1])) {
            // On garde la mémoire
            this.zoneAffecte = this.instructions[this.instructions.length - 1];
            return true;
        }
        
        this.lieuMauvaisArgument = this.typesSousChaines.length - 2;
        return false;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #calculExpressionParenthese()
     */
    @Override
    public void calculExpressionParenthese() {

        // On le fait autant de fois qu'il y a de parenthèses + 1        
        for (int i = this.nbParentheses; i > 0; i--) {
            
            // On utilise des variables pour localiser ce que l'on cherche
            int compteur = 0, // Parenthèses ouvrantes passées 
                   debut = 0, // Indice de la position du début 
                     fin = 0; // Indice de la position de la fin
            
            // On cherche la dernière parenthèse ouvrante
            for (int j = 0; j < this.instructions.length; j++) {
                
                if (this.instructions[j].equals("(")) {
                    compteur++;
                    if (compteur == i) {
                        debut = j;
                        break;
                    }
                }
            }
            
            // Après l'avoir trouvée, on cherche la première fermante qui suit
            for (int j = debut; j < this.instructions.length
                    && this.instructions[j] != null; j++) {
                if (this.instructions[j].equals(")")) {
                    fin = j;
                    break;
                }
            }
                      
            // On en tire une expression sans parenthèse que l'on calcule
            this.calculExpressionSimple(debut + 1, fin - 1);
            
            // On enlève les parenthèses
            this.instructions[debut] = this.instructions[debut + 1];
            this.decalage(debut + 1);
        }        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *          #calculExpressionSimple(int, int)
     */
    @Override
    public void calculExpressionSimple(int debut, int fin) {
        
        // 4 comme le nombre d'opérateurs
        for (int i = 0; i < 4; i++) {
            
            int compteur = debut;
            /*
             * On parcourt la sous chaine à la recherche des opérateurs dans
             * l'ordre des priorités définies dans PRIORITE_OPERATEUR
             */
            while (compteur < fin &&
                    !this.instructions[debut + 1].equals(")")) {
                
                // Si on trouve l'opérateur que l'on recherche
                if (this.instructions[compteur].
                        equals(PRIORITE_OPERATEUR[i])) {
                    this.operateurOperationOperateur(i, compteur);
                    
                    fin -= 2;           // On enlève 2
                    compteur = debut;   // On repart du début
                }
                compteur++;
            }
        }        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #operateurOperationOperateur(int, int)
     */
    @Override
    public void operateurOperationOperateur(int operation, int position) {
        String resultat = null;
        
        switch (operation) {
        case 0 : // Division
            resultat = Double.toString(
                    Double.parseDouble(this.instructions[position - 1])
                    /
                    Double.parseDouble(this.instructions[position + 1]));
            break;
        case 1 : // Multiplication
            resultat = Double.toString(
                    Double.parseDouble(this.instructions[position - 1])
                    *
                    Double.parseDouble(this.instructions[position + 1]));
            break;
        case 2 : // Addition
            resultat = Double.toString(
                    Double.parseDouble(this.instructions[position - 1])
                    +
                    Double.parseDouble(this.instructions[position + 1]));
            break;
        case 3 : // Soustraction
            resultat = Double.toString(
                    Double.parseDouble(this.instructions[position - 1])
                    -
                    Double.parseDouble(this.instructions[position + 1]));
            break;
        }
        
        // On remplace l'opérande de gauche par le résultat
        this.instructions[position - 1] = resultat;
        
        // Puis on décale
        this.decalage(position);        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#decalage(int)
     */
    @Override
    public void decalage(int position) {
        // On décale de deux cases
        int i;      
        for (i = position; i < this.instructions.length - 2
                && this.instructions[i + 2] != null; i++) {
            this.instructions[i] = this.instructions[i+2];
        }
        
        // Et on efface les deux dernières cases
        this.instructions[i] = this.instructions[i+1] = null;        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#nombreEgalExpression()
     */
    @Override
    public int nombreEgalExpression() {
        int aRetourner = 0;
        
        for (int i = 0; i < this.typesSousChaines.length; i++) {
            if (this.typesSousChaines[i] == 4) {
                aRetourner++;
            }
        }
        return aRetourner;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#caseAAffecter()
     */
    @Override
    public int caseAAffecter() {
        // A=65 --> 0 .. Z=90 --> 25
        return this.zoneAffecte.charAt(0) - 65;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#longueurInstruction()
     */
    @Override
    public int longueurInstruction() {
        int i;
        for (i = 0; i < this.instructions.length
                && this.instructions[i] != null; i++) {
            if (this.instructions[i] == null) {
                return i;
            }
        }
        return i;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #arrondissementResultat()
     */
    @Override
    public void arrondissementResultat() {
        // On teste si le résultat est un entier pour éviter la virgule
        if (estUnEntier(this.instructions[0])) {
            this.instructions[0] = Integer.toString(
                    (int) Double.parseDouble(this.instructions[0]));
        } else {
            /*
             * Comme il s'agit d'un réel, on tente d'arrondir à 2 chiffres
             * après la virgule
             */
            this.instructions[0] = this.getARRONDIR().format(
                    Double.parseDouble(this.instructions[0]));
        }
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
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #estUneSousChaine(java.lang.String)
     */
    @Override
    public boolean estUneSousChaine(String aTester) {
        return (estUnDouble(aTester)
                || estUnCaractereValide(aTester)
                || estUneMemoire(aTester));
    }

    /**
     * Teste si une chaine de caractère est un caractère valide, c'est-à-dire
     * ( ) + - * /
     * @param aTester Chaine à tester
     * @return true si la chaine correspond à un caractère spécial d'une 
     *         opération, false sinon          
     */
    public static boolean estUnCaractereValide(String aTester) {
        return aTester.length() == 1 &&
                (aTester.charAt(0) == '('
                || aTester.charAt(0) == ')'
                || aTester.charAt(0) == '='
                || estUnOperateur(aTester));
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