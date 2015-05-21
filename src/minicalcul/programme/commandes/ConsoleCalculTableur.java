/* 
 * ConsoleTableur.java                            16 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet permettant d'intéragir avec le tableur
 * @author Clément Zeghmati
 * @version 0.1
 */
public class ConsoleCalculTableur extends ConsoleCalculSimple {

    /** Référence au tableur afin de pouvoir intéragir avec */
    private Tableur leTableur;
    
    /**
     * Constructeur de la console du tableur
     * @param laFenetre Référence permettant d'accéder à la console et aux
     *                           zones mémoires
     * @param leTableur Référence permettant d'accéder au tableur
     */
    public ConsoleCalculTableur(FenetrePrincipale laFenetre, 
            Tableur leTableur) {
        super(laFenetre);
        this.leTableur = leTableur;
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par réinitialiser l'état de cet objet
        this.reinitialisation();

        // On découpe la chaine avec les espaces en conservant l'originale
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));
        
        // On vérifie la syntaxe des sous-chaines 
        if (!this.verificationSyntaxe()) {
            /*
             * On recherche le lieux de l'erreur et on retourne la localisation
             * avec un message
             */
            this.rechercheErreur(ERREUR_SYNTAXE);
        }
                        
        // On vérifie le format de la chaine s'il n'y a pas eu d'erreur avant.
        if (!this.isErreurTrouvee() && !this.verificationFormat()) {
            this.rechercheErreur(ERREUR_FORMAT);
        }
        
        // Le nombre d'= doit être égal à 1 car il doit y avoir une affectation.
        if (!this.isErreurTrouvee() && !this.verificationAffectation()) {
            this.rechercheErreur(ERREUR_AFFECTATION);
        }
                
        /*
         * S'il n'y a pas eu d'erreur avant et que la restauration s'est mal
         * passé, on déclenche une erreur. Si la restauration s'est bien passé,
         * on peut continuer
         */
        if (!this.isErreurTrouvee() && !this.restaurationSauvegarde()) {
            this.rechercheErreur(ERREUR_INTIALISATION);
        }   
        
        // On peut faire le calcul s'il n'y a pas eu d'erreur avant
        if (!this.isErreurTrouvee()) {

            if (this.getNbParentheses() != 0) {
                this.calculExpressionParenthese();

                /*
                 * Tous les calculs entre parenthèse ont été fait et les 
                 * parenthèses ont été enlevées. Il reste donc une opération 
                 * simple que l'on effectue.
                 */
                this.calculExpressionSimple(0, this.longueurInstruction() - 1);

            } else {
                this.calculExpressionSimple(0, this.getInstructions().length-1);           
            }

            // On teste si le résultat est un entier pour eviter la virgule
            if (estUnEntier(this.getInstructions()[0])) {
                this.getInstructions()[0] = Integer.toString(
                        (int) Double.parseDouble(this.getInstructions()[0]));
            }

            // On affecte le résultat à la cellule
            this.leTableur.affectationValeur(
                    this.getZoneAffecte(), this.getInstructions()[0]);
                     
        }
        
        // On retourne le résultat s'il n'y a pas d'erreur
        return !this.isErreurTrouvee() ? (this.getZoneAffecte() + " modifiée.")
                        : this.getaRetourner();        
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

        // On rajoute le type d'erreur
        if (typeErreur == ERREUR_SYNTAXE) {       
            this.setaRetourner(tmpARetourner.append(
             "^\nErreur de syntaxe : symbole \"" 
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" inconnu.");
        } else if (typeErreur == ERREUR_AFFECTATION) {
            this.setaRetourner(tmpARetourner.append(
              "^\nErreur d'affectation : une affectation doit être faite.")
              .toString());
        
        } else if (typeErreur == ERREUR_FORMAT) {
            if (this.getNbParentheses() == -1) {
                // Problème au niveau des parenthèses
                this.setaRetourner(tmpARetourner.append("^\nErreur de format :"
                        + " le nombre de parenthèses ouvrantes et fermantes"
                        + " doit être équivalent.").toString());
            } else {
                // Problème avec le tableau de vérité
                this.setaRetourner(tmpARetourner.append("^\nErreur de format :"
                        + " symbole innatendu.").toString());
            }
        
        } else if (typeErreur == ERREUR_INTIALISATION) {
            this.setaRetourner(tmpARetourner.append(
             "^\nCalcul impossible : \""
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" n'a pas été initialisée.");
        }         
    }
         
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #verificationAffectation()
     */
    @Override
    public boolean verificationAffectation() {
        // On contrôle qu'il y ait un égale et que l'affectation soit correcte
        if (!(this.nombreEgaleExpression() == 1 && affectationCorrecte())) {
            this.setLieuMauvaisArgument(this.getInstructions().length - 1);
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #restaurationSauvegarde()
     */
    @Override
    public boolean restaurationSauvegarde() {
        
        /*
         * Passe à true s'il y a eu une restauration à faire. Si c'est le cas,
         * on doit le spécifier dans la cellule comme quoi il s'agit d'une
         * formule.
         */
        boolean restauration = false;
                
        // On peut s'arrêter de rechercher lorsqu'on rencontre un =
        for (int i = 0; i < this.getInstructions().length
                && !this.getInstructions()[i].equals("="); i++) {
            
            if (estUneMemoire(this.getInstructions()[i])) {
                
                // S'il s'agit d'une cellule, on vérifie si elle est initialisée
                if (this.leTableur.celluleInitialisee(
                        this.getInstructions()[i])) {
                    // On récupère la valeur
                    this.getInstructions()[i] = this.leTableur.
                            restaurationCellule(this.getInstructions()[i]);
                    restauration = true;
                } else { // Erreur : zone non initialisée
                    this.setLieuMauvaisArgument(i);
                    /*
                     * On remplit la cellule avec un ? afin de le spécifier que
                     * l'affectation est impossible. On le signale également à
                     * l'utilisateur  
                     */
                    this.leTableur.affectationFormule(
                            this.getZoneAffecte(), this.getCommande());
                    this.leTableur.affectationValeur(
                            this.getZoneAffecte(), "?");
                    return false;  // On sort ici car opération impossible
                }
            }
        }
        
        if (restauration) {
            // On spécifie que la cellule va contenir une formule
            this.leTableur.affectationFormule(
                    // Cellule
                    this.getInstructions()[this.getInstructions().length - 1],
                    // Formule que l'on copie pour la garder
                    new String(this.getCommande()));
        } else {
            // On spécifie que la cellule ne contiendra pas de formule
            this.leTableur.reinitialisationCellule(
                    // Cellule
                    this.getInstructions()[this.getInstructions().length - 1]);
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_CELLULE).matcher(aTester).matches();
    }
    
}