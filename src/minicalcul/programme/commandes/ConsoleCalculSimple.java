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
 * les parenth�ses. Le r�sultat d'une op�ration pourra �tre affect�e dans une
 * zone m�moire (A..Z) et son contenu pourra �tre r�cup�r� dans une op�ration
 * gr�ce � son identifiant. Si l'op�ration est correcte, l'objet retourne le
 * r�sultat de l'op�ration, sinon il renvoie une erreur. 
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public class ConsoleCalculSimple extends Console implements ConsoleCalcul {
    
    /** Message affich� lorsqu'il y a un probl�me avec les parenth�ses */
    protected static final String MSG_MAUVAISES_PARENTHESES = "Erreur "
            + "de format : le nombre de parenth�ses ouvrantes et fermantes "
            + "doit �tre �quivalent.";

    /** Message affich� lorsqu'il y a un probl�me avec le dernier �l�ment */
    protected static final String MSG_MAUVAISE_FIN =  "Erreur "
            + "de format : une op�ration ne peut pas se finir par un = ou "
            + "par un op�rateur.";

    /** Message affich� lorsqu'il y a un probl�me avec le premier �l�ment */
    protected static final String MSG_MAUVAIS_DEBUT = "Erreur de format : "
            + "une op�ration ne peut pas commencer par un =, par un op�rateur "
            + "ou par une parenth�se fermante.";
    
    /** Message affich� lorsqu'il y a un probl�me d'affectation */
    protected static final String MSG_MAUVAISE_AFFECTATION = "Erreur "
            + "d'affectation : il ne peut y avoir au maximum qu'un seul �gal.";
    
    /** Message affich� lorsqu'il y a des parenth�ses apr�s un �gal */
    protected static final String MSG_PARENTHESE_APRES_EGAL = "Erreur de "
            + "format : il ne peut y avoir des parenth�ses apr�s une "
            + "affectation.";

    /** Message affich� lorsqu'il y a une op�ration apr�s une affectation */
    protected static final String MSG_OPERATION_APRES_EGAL = "Erreur de "
            + "format : il ne doit pas y avoir d'op�rations apr�s une "
            + "affectation.";

    /** Message affich� lorsqu'il y a une erreur de calcul */
    protected static final String MSG_PROBLEME_MATHEMATIQUE = "Erreur "
            + "math�matique : calcul impossible.";

    /** Liste des �l�ments disponible (en chaine) utilis� pour les messages */
    protected static final String[] ELEMENTS_EN_CHAINE =
        {"un r�el", "un op�rateur", "une parenth�se ouvrante",
        "une parenth�se fermante", "un �gal", "une m�moire"};

    /**
     * Tableau de v�rit� des successeurs des caract�res possibles de :
     *  - 0 : entier ou r�el
     *  - 1 : operateur (+ - * /)
     *  - 2 : parenth�se ouvrante
     *  - 3 : parenth�se fermante
     *  - 4 : �gal
     *  - 5 : m�moire
     */
    public static final boolean[][] VERITE_SUCCESSEURS = {
        {false, true , false, true , true , false},
        {true , false, true , false, false, true },
        {true , false, true , false, false, true },
        {false, true , false, true , true , false},
        {false, false, false, false, false, true },
        {false, true , false, true , true , false} };
        
    /**
     * Ordre de priorit� des op�rateurs allant de la priorit� la plus forte � la
     * priorit� la plus faible.
     */
    public static final String[] PRIORITE_OPERATEUR = {"/", "*", "+", "-"};

    /** 
     * Types des sous-chaines de la commande envoy�e 
     *  - 0 : entier ou r�el
     *  - 1 : operateur (+ - * /)
     *  - 2 : parenth�se ouvrante
     *  - 3 : parenth�se fermante
     *  - 4 : �gal
     *  - 5 : m�moire
     */
    protected int[] typesSousChaines;
    
    /** M�moire � laquelle le r�sultat sera affect�e */
    protected String zoneAffecte;
        
    /** Nombre de paires de parenth�ses dans l'expression envoy�e */
    protected int nbParentheses;

    /**
     * Constructeur de la console de calculs
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     */
    public ConsoleCalculSimple(FenetrePrincipale laFenetre) {
        this.setLaFenetre(laFenetre);
        
        /* 
         * On d�finit le format des r�els qui seront arrondits � 2 chiffres 
         * apr�s la virgule.
         */
        this.getARRONDIR().setRoundingMode(RoundingMode.HALF_UP);
    }
 
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par r�initialiser l'�tat de l'objet
        this.reinitialisation();
        
        /*
         * On d�coupe la chaine avec les espaces en conservant l'originale et
         * en enlevant les espaces multiples.
         */
        this.commande = commande.replace("  ", "");
        this.instructions = this.commande.split(" ");
        
        // On effectue tous les contr�les sur la commande
        this.controleCommande();

        // On peut faire le calcul s'il n'y a pas eu d'erreur trouv�e
        if (!this.erreurTrouvee) {
            this.calculExpression();
        }
        
        // On retourne le r�sultat ou l'erreur
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
        // On v�rifie la syntaxe des sous-chaines 
        if (!this.verificationSyntaxe()) {
            /*
             * On recherche le lieux de l'erreur et on retourne la localisation
             * avec un message.
             */
            this.rechercheErreur(ERREUR_SYNTAXE);
        }

        // On v�rifie le format de la chaine s'il n'y a pas eu d'erreur avant
        if (!this.erreurTrouvee) {
            this.verificationFormat();
        }
        
        /*
         * Le nombre d'�gal dans l'expression doit �tre �gal � 0 ou 1 car il 
         * doit y avoir au maximum une seule affectation.
         */
        if (!this.erreurTrouvee) {
            this.verificationAffectation();            
        }

        /*
         * S'il n'y a pas eu d'erreur avant, on tente de resteurer le contenu
         * des zones m�moires qu'il pourrait y avoir dans la commande. Si la
         * restauration s'est bien pass�, on peut continuer.
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
        // Si le calcul comporte des parenth�ses
        if (this.nbParentheses != 0) {
            /*
             * On effectue tous les calculs entre parenth�ses et on enl�ve
             * toutes les parenth�ses.
             */
            this.calculExpressionParenthese();

            /*
             * Tous les calculs entre parenth�ses ont �t� fait et les 
             * parenth�ses ont �t� enlev�es. Il reste donc une op�ration 
             * simple que l'on effectue.
             */
            this.calculExpressionSimple(0, this.longueurInstruction() - 1);

        } else {
            // Pas de parenth�ses
            this.calculExpressionSimple(0, this.instructions.length-1);           
        }

        // On arrondit le r�sultat
        this.arrondissementResultat();
        
        try {
            // On v�rifie les erreurs math�matiques (division par 0)
            Double.isInfinite(Double.parseDouble(this.instructions[0]));

        } catch (NumberFormatException e) {
            // Conversion impossible
            this.lieuMauvaisArgument = this.instructions.length - 1;
            this.rechercheErreur(ERREUR_MATHEMATIQUE);
            return;  // On ne fait pas d'affectations
        }

        /*
         * Si l'on doit faire une affectation, on la fait en mettant � jour
         * les zones m�moire. 
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
        this.erreurTrouvee = true; // Erreur d�clench�e
        
        // Cette chaine correspond � l'erreur qui sera affich�e sur la console
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
                    +" ne peut �tre suivi d'" + ELEMENTS_EN_CHAINE
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
                    + "\" n'a pas �t� initialis�.";
            break;
        case ERREUR_OPERATION_APRES_EGAL :
            this.aRetourner = tmpARetourner.append(
                    MSG_OPERATION_APRES_EGAL).toString();
            break;
        case ERREUR_MATHEMATIQUE : 
            this.aRetourner = tmpARetourner.append(
                    MSG_PROBLEME_MATHEMATIQUE).toString();
            break;
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#verificationSyntaxe()
     */
    @Override
    public boolean verificationSyntaxe() {
        // On v�rifie la syntaxe de chaque sous-chaine
        for (int i = 0; i < this.instructions.length
                && this.instructions[i] != null; i++) {
            /*
             * On doit trait� le signe - de diff�rentes mani�res en fonction de
             * comment l'utilisateur l'a plac� et des espaces qui l'a pu laisser
             * avant ou pas. En effet, il peut �tre utilis� comme op�rateur
             * unaire.
             */
            if (estUnDouble(this.instructions[i]) 
                    && !estPositif(this.instructions[i])) {
                // R�el n�gatif
                if (i != 0 && !(estUnOperateur(this.instructions[i-1])
                        || this.instructions[i-1].equals("("))) {
                    
                    this.separationElementsExpression(i);
                    i++;
                } 
            }
            
            // Syntaxe correcte : r�el ou op�rateur ou �gal ou m�moire 
            if (!estUneSousChaine(this.instructions[i])) {
                int separation = this.separationElementsExpression(i);
    
                for (int j = i; j < i + separation; j++) {
                    if (!estUneSousChaine(this.instructions[j])) {
                        /*
                         * On garde l'indice du mauvais argument pour traiter l'
                         * erreur et afficher la partie o� � eu lieu de l'erreur
                         */
                        this.lieuMauvaisArgument = j;
                        return false;
                    }
                }
    
                // On tente une s�paration
                if (separation != 0) {
                    i += separation - 1;
                } else {
                    /*
                     * On garde l'indice du mauvais argument pour traiter 
                     * l'erreur et afficher la partie o� � eu lieu de l'erreur
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
        // On r�cup�re la chaine que l'on veut tenter de s�parer
        int partieTraitee = 0;
        String aTester = new String(this.instructions[position]);
        ArrayList<String> liste = new ArrayList<String>();
        
        int i = 0; // Indice de passage
        
        // On traite le premier caract�re � part s'il s'agit d'un -
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
                     * Si le premier caract�re est un signe op�ratoire ou une 
                     * parenth�se
                     */
                    liste.add(aTester.substring(0, 1));
                    aTester = new String(
                            aTester.substring(1, aTester.length()));
                    partieTraitee++;
                } else {
                    /*
                     * Si on a atteint un signe op�ratoire ou une parenth�se et
                     * qu'il y avait avant un r�el juste avant
                     */
                    liste.add(aTester.substring(0, i));    // R�el
                    liste.add(aTester.substring(i, i+1));  // Caract�re
                    
                    aTester = aTester.length() == i ? "" : // Fin de chaine
                           new String(aTester.substring(i+1, aTester.length()));
                    
                    partieTraitee += 2;
                    i = 0;
                }
            } else {
                /*
                 * S'il s'agit d'un chiffre, on continue le parcourt sans rien
                 * faire jusqu'au prochain signe op�ratoire...
                 */
                i++;
                
                // ...sauf dans le cas o� l'on a atteint la fin de la chaine.
                if (i == aTester.length()) {
                    liste.add(aTester);
                    aTester = "";
                    partieTraitee++;
                }
            }
        }
                
        // On cr�� la nouvelle commande avec les bons espaces pour le traitement
        StringBuilder nouvelle = new StringBuilder();
        
        // On prend ce qu'il y avait avant
        for (i = 0; i < position; i++) {
            nouvelle.append(this.instructions[i] + " ");
        }
        
        // On ajoute ce qu'on a obtenu dans ce traitement
        for (i = 0; i < liste.size(); i++) {
            nouvelle.append(liste.get(i) + " ");
        }
        
        // Puis ce qu'il y a apr�s
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
         * Le tableau des successeurs ne peut pas tout contr�ler. Il faut
         * d'abord v�rifier le dernier �l�ment qui ne peut �tre un op�rateur
         * ou un �gale. S'il y a une erreur, on retourne la position de la
         * derni�re sous-chaine.
         */
        if (this.instructions[this.instructions.length - 1].equals("=")
                || estUnOperateur(this.instructions
                                [this.instructions.length - 1])) {
            this.lieuMauvaisArgument = this.instructions.length - 1;
            this.rechercheErreur(ERREUR_MAUVAISE_FIN);
            this.erreurTrouvee = true;
        }
        
        /*
         * On v�rifie �galement le premier �l�ment qui doit �tre un r�el ou une
         * zone m�moire ou une parenth�se ouvrante
         */
        if (!this.erreurTrouvee && !(estUnDouble(this.instructions[0]) 
                || this.instructions[0].equals("(")
                || estUneMemoire(this.instructions[0]))) {
            this.lieuMauvaisArgument = 0;
            this.rechercheErreur(ERREUR_MAUVAIS_DEBUT);
            this.erreurTrouvee = true;
        }
        
        // Le reste des v�rifications se fera gr�ce au tableau des successeurs.
        
        /*
         * Types rencontr�s tel que : 
         *      - 0 = r�el
         *      - 1 = operateur (+ - * /)
         *      - 2 = parenth�se ouvrante
         *      - 3 = parenth�se fermante  
         *      - 4 = �gale
         *      - 5 = zone m�moire ou cellule
         */
        this.typesSousChaines = new int[this.instructions.length];

        int ouvrante = 0, // Compteur des parenth�ses ouvrantes
            fermante = 0; // Compteur des parenth�ses fermantes

        /*
         * On r�cup�re les types de chaque sous chaine pour v�rifier chaque
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
                // Op�rateur
                this.typesSousChaines[i] = 1;                
            } else if (estUnDouble(this.instructions[i])) {
                // Double
                this.typesSousChaines[i] = 0;
            } else if (this.instructions[i].charAt(0) == '=') {
                // Egale
                this.typesSousChaines[i] = 4;
            } else if (estUneMemoire(this.instructions[i])) {
                // Zone m�moire
                this.typesSousChaines[i] = 5;
            }
        }
        
        // Si il n'y a pas autant d'ouvrantes que de fermantes, erreur de format
        if (!this.erreurTrouvee && ouvrante != fermante) {
            this.lieuMauvaisArgument = this.instructions.length;
            this.rechercheErreur(ERREUR_EQUIVALENCE_PARENTHESES);
            this.erreurTrouvee = true;
        }
        
        // On effectue les contr�les gr�ce au tableau des successeurs
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
        return !this.erreurTrouvee;  // Le format est bon, pas d'erreur trouv�e
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #verificationAffectation()
     */
    @Override
    public boolean verificationAffectation() {
        /*
         * On contr�le qu'il n'y ait pas d'�gal ou s'il y en a 1, qu'il soit 
         * suivit d'une lettre. S'il y a une affectation, on conserve la zone 
         * o� l'on effectuera la sauvegarde
         */
        int compteur = this.nombreEgalExpression();
        
        if (compteur == 0 || (compteur == 1 && this.affectationCorrecte())) {
            return true;
        } // else mauvaise affectation

        /*
         * Il est possible que l'utilisateur ait utilis� des parenth�ses apr�s
         * un �gal donc on v�rifie.
         */
        for (int i = this.typesSousChaines.length - 1; i >= 0; i--) {
            
            if (this.typesSousChaines[i] == 2 
                    || this.typesSousChaines[i] == 3) {
                this.lieuMauvaisArgument = i;
                this.rechercheErreur(ERREUR_PARENTHESES_APRES_EGAL);
                
                /*
                 * L'erreur n'�tant pas dans l'affectation, on renvoie true 
                 * afin qu'on ne traite pas une mauvaise erreur.
                 */
                return true;
            }
        }
        
        // On v�rifie qu'il n'y ait pas d'op�ration apr�s le �gal
        if (compteur == 1) { // Le �gal est mal plac�
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
        // On peut s'arr�ter de rechercher lorsqu'on rencontre un �gale
        for (int i = 0; i < this.instructions.length
                && !this.instructions[i].equals("="); i++) {
            
            if (estUneMemoire(this.instructions[i])) {
                /*
                 * S'il s'agit d'une zone m�moire, on v�rifie si elle est bien
                 * initialis�e.
                 */
                if (this.estInitialisee(this.instructions[i])) {
                    this.instructions[i] = this.getLaFenetre()
                       .getLaMemoire().valeurMemoire(this.instructions[i]);
                    
                } else { // Erreur : zone non initialis�e
                    this.lieuMauvaisArgument = i;
                    return false;  // On sort ici car l'op�ration est impossible
                }
            }
        }
        return true; // Les restaurations �ventuelles se sont bien d�roul�es
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#affectationCorrecte()
     */
    @Override
    public boolean affectationCorrecte() {
        /*
         * S'il n'y a qu'un �gale, il se situe en avant derni�re position. Et
         * le dernier caract�re est une m�moire. Sinon il y a une erreur. 
         */
        if (this.typesSousChaines[this.typesSousChaines.length - 2] == 4 &&
               estUneMemoire(this.instructions[this.instructions.length - 1])) {
            // On garde la m�moire
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

        // On le fait autant de fois qu'il y a de parenth�ses + 1        
        for (int i = this.nbParentheses; i > 0; i--) {
            
            // On utilise des variables pour localiser ce que l'on cherche
            int compteur = 0, // Parenth�ses ouvrantes pass�es 
                   debut = 0, // Indice de la position du d�but 
                     fin = 0; // Indice de la position de la fin
            
            // On cherche la derni�re parenth�se ouvrante
            for (int j = 0; j < this.instructions.length; j++) {
                
                if (this.instructions[j].equals("(")) {
                    compteur++;
                    if (compteur == i) {
                        debut = j;
                        break;
                    }
                }
            }
            
            // Apr�s l'avoir trouv�e, on cherche la premi�re fermante qui suit
            for (int j = debut; j < this.instructions.length
                    && this.instructions[j] != null; j++) {
                if (this.instructions[j].equals(")")) {
                    fin = j;
                    break;
                }
            }
                      
            // On en tire une expression sans parenth�se que l'on calcule
            this.calculExpressionSimple(debut + 1, fin - 1);
            
            // On enl�ve les parenth�ses
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
        
        // 4 comme le nombre d'op�rateurs
        for (int i = 0; i < 4; i++) {
            
            int compteur = debut;
            /*
             * On parcourt la sous chaine � la recherche des op�rateurs dans
             * l'ordre des priorit�s d�finies dans PRIORITE_OPERATEUR
             */
            while (compteur < fin &&
                    !this.instructions[debut + 1].equals(")")) {
                
                // Si on trouve l'op�rateur que l'on recherche
                if (this.instructions[compteur].
                        equals(PRIORITE_OPERATEUR[i])) {
                    this.operateurOperationOperateur(i, compteur);
                    
                    fin -= 2;           // On enl�ve 2
                    compteur = debut;   // On repart du d�but
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
        
        // On remplace l'op�rande de gauche par le r�sultat
        this.instructions[position - 1] = resultat;
        
        // Puis on d�cale
        this.decalage(position);        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#decalage(int)
     */
    @Override
    public void decalage(int position) {
        // On d�cale de deux cases
        int i;      
        for (i = position; i < this.instructions.length - 2
                && this.instructions[i + 2] != null; i++) {
            this.instructions[i] = this.instructions[i+2];
        }
        
        // Et on efface les deux derni�res cases
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
        // On teste si le r�sultat est un entier pour �viter la virgule
        if (estUnEntier(this.instructions[0])) {
            this.instructions[0] = Long.toString(
                    (long) Double.parseDouble(this.instructions[0]));
        } else {
            /*
             * Comme il s'agit d'un r�el, on tente d'arrondir � 2 chiffres
             * apr�s la virgule
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
     * Teste si une chaine de caract�re est un caract�re valide, c'est-�-dire
     * ( ) + - * /
     * @param aTester Chaine � tester
     * @return true si la chaine correspond � un caract�re sp�cial d'une 
     *         op�ration, false sinon          
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