/* 
 * ConsoleCalculTableur.java                            16 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet effectuant des instructions de calculs simple (+, -, *, /) en acceptant
 * les parenth�ses. Le r�sultat d'une op�ration devra �tre affect�e dans une
 * cellule du tabeur auquel cet objet sera associ� (A1..Z20). Le contenu de ces
 * cellules pourra �tre r�cup�r� dans une op�ration gr�ce � son identifiant.
 * Si l'op�ration est correcte, l'objet retourne le r�sultat de l'op�ration,
 * sinon il renvoie une erreur. 
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public class ConsoleCalculTableur extends ConsoleCalculSimple {
    
    /** Message affich� lorsqu'il y a un probl�me d'affectation */
    protected static final String MSG_MAUVAISE_AFFECTATION = "Erreur "
            + "d'affectation : une et une seule affectation doit �tre faite.";
    
    /** Message affich� lorsqu'une cellule non initialis�e est impliqu�e */
    protected static final String MSG_CELLULE_NON_INITIALISEE = "Calcul "
            + "impossible : une cellule non initialis�e est impliqu�e dans "
            + "ce calcul.";

    /** R�f�rence au tableur afin de pouvoir int�ragir avec */
    private Tableur leTableur;
    
    /**
     * Constructeur de la console du tableur
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     * @param leTableur R�f�rence permettant d'acc�der au tableur
     */
    public ConsoleCalculTableur(FenetrePrincipale laFenetre, 
            Tableur leTableur) {
        super(laFenetre);
        this.leTableur = leTableur;
        this.reinitialisation();
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par r�initialiser l'�tat de cet objet
        super.reinitialisation();

        /*
         * On d�coupe la cha�ne avec les espaces en conservant l'originale et
         * en enlevant les espaces multiples.
         */
        this.commande = commande.replace("  ", "");
        this.instructions = this.commande.split(" ");
        
        // On effectue tous les contr�les sur la commande
        this.controleCommande();
        
        /*
         * On tente de restaurer le contenu des cellules qu'il pourrait y avoir
         * dans la commande. Si la restauration s'est bien pass�, on peut
         * continuer. Sinon, on ne d�clenche pas d'errerurs mais on signale que
         * le calcul est impossible.
         */
        if (!this.erreurTrouvee && !this.restaurationSauvegarde()) {
            this.aRetourner = MSG_CELLULE_NON_INITIALISEE;
            // On ne fait pas le calcul
            return "> " + this.commande + "\n" + this.aRetourner ;
        }  
                        
        // On peut faire le calcul s'il n'y a pas eu d'erreur trouv�e
        if (!this.erreurTrouvee) {
            this.calculExpression();
        }
        
        // On retourne le r�sultat ou l'erreur
        return "> " + this.commande + "\n"
                + (!this.erreurTrouvee ? (this.zoneAffecte + " modifi�e.")
                        : this.aRetourner);        
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #controleCommande()
     */
    @Override
    public void controleCommande() {
        // On v�rifie la syntaxe des sous-cha�nes 
        if (!super.verificationSyntaxe()) {
            
            /*
             * On recherche le lieux de l'erreur et on retourne la localisation
             * avec un message.
             */
            this.rechercheErreur(ERREUR_SYNTAXE);
        }
        
        // Pour traiter la commande on enl�ve les �ventuels '$'
        for (int i = 0; i < this.instructions.length; i++) {
            if (this.estUneMemoire(this.instructions[i])) {
                this.instructions[i] = this.instructions[i].replace("$", "");
            }
        }
                                
        // On v�rifie le format de la cha�ne s'il n'y a pas eu d'erreur avant
        if (!this.erreurTrouvee) {
            super.verificationFormat();
        }
        
        // Le nombre d''=' doit �tre �gal � 1 car il doit y avoir une affectation.
        if (!this.erreurTrouvee) {
            this.verificationAffectation();
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #calculExpression()
     */
    @Override
    public void calculExpression() {
        if (this.nbParentheses != 0) {
            /*
             * On effectue tous les calculs entre parenth�ses et on enl�ve
             * toutes les parenth�ses.
             */
            super.calculExpressionParenthese();

            /*
             * Tous les calculs entre parenth�ses ont �t� fait et les 
             * parenth�ses ont �t� enlev�es. Il reste donc une op�ration 
             * simple que l'on effectue.
             */
            super.calculExpressionSimple(0, this.longueurInstruction() - 1);

        } else {
            // Pas de parenth�ses
            super.calculExpressionSimple(0, this.instructions.length - 1);           
        }

        // On arrondit le r�sultat
        super.arrondissementResultat();
        
        
        try {
            // On v�rifie les erreurs math�matiques (division par 0)
            Double.isInfinite(Double.parseDouble(this.instructions[0]));
            
            // On affecte le r�sultat � la cellule
            this.leTableur.affectationValeur(
                    this.zoneAffecte, this.instructions[0]);

        } catch (NumberFormatException e) {
            // Conversion impossible
            this.lieuMauvaisArgument = this.instructions.length - 3;
            this.rechercheErreur(ERREUR_MATHEMATIQUE);
        }

    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#rechercheErreur(int)
     */
    @Override
    protected void rechercheErreur(int typeErreur) {
        int posErreur = 0;
        this.erreurTrouvee = true; // Erreur d�clench�e
        StringBuilder tmpARetourner = new StringBuilder("  ");

        // On recherche la position de l'erreur dans la cha�ne originale
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
                    [this.lieuMauvaisArgument]).toString()+ "\" inconnu.";
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
            
        case ERREUR_MAUVAIS_SUCCESSEUR:
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
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #verificationAffectation()
     */
    @Override
    public boolean verificationAffectation() {
        // On r�cup�re le nombre d''=' dans l'expression
        int compteur = super.nombreEgalExpression();
        
        // On contr�le qu'il y ait un �gal et que l'affectation soit correcte
        if (compteur == 1 && super.affectationCorrecte()) {
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
                return false;
            }
        }
        
        // On v�rifie qu'il n'y ait pas d'op�ration apr�s le �gal
        if (compteur == 1 && !this.instructions[this.instructions.length - 2]
                .equals("=")) {
            // Le �gal est mal plac�
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
        /*
         * Passe � true s'il y a eu une restauration � faire. Si c'est le cas,
         * on doit le sp�cifier dans la cellule comme quoi il s'agit d'une
         * formule.
         */
        boolean restauration = false;
                
        // On peut s'arr�ter de rechercher lorsqu'on rencontre un '='
        for (int i = 0; i < this.instructions.length
                && !this.instructions[i].equals("="); i++) {
            
            if (this.estUneMemoire(this.instructions[i])) {
                // S'il s'agit d'une cellule, on v�rifie si elle est initialis�e
                if (this.estInitialisee(this.instructions[i])) {
                    // On r�cup�re la valeur
                    this.instructions[i] = this.leTableur.
                            restaurationValeurCellule(this.instructions[i]);
                    restauration = true;
                } else { // Erreur : zone non initialis�e

                    this.lieuMauvaisArgument = i;
                    /*
                     * On remplit la cellule avec un '?' afin de sp�cifier que 
                     * l'affectation est impossible. On le signale �galement
                     * � l'utilisateur.
                     */
                    this.leTableur.affectationFormule(
                            this.zoneAffecte, this.commande);
                    this.leTableur.affectationValeur(this.zoneAffecte, "?");
                    return false;  // On sort ici car op�ration impossible
                }
            }
        }
        
        if (restauration) {
            // On sp�cifie que la cellule va contenir une formule
            this.leTableur.affectationFormule(
                    // Cellule
                    this.instructions[this.instructions.length - 1],
                    // Formule que l'on copie pour la garder
                    new String(this.commande));
        } else {
            // On sp�cifie que la cellule ne contiendra pas de formule
            this.leTableur.reinitialisationCellule(
                    // Cellule
                    this.instructions[this.instructions.length - 1]);
        }
        return true; // Les restaurations �ventuelles se sont bien d�roul�es
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    protected boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_CELLULE).matcher(aTester).matches()
                || Pattern.compile(REGEX_CELLULE_BLOCAGE).
                   matcher(aTester).matches();
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #estInitialisee(java.lang.String)
     */
    @Override
    protected boolean estInitialisee(String aTester) {
        return this.leTableur.celluleInitialisee(aTester);
    }
}