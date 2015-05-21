/* 
 * ConsoleCalcul.java                            17 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

/**
 * Interface réunissant les méthodes de calcul qui pourront être utilisées pour
 * la calculatrice ou le tableur.
 * @author Clément Zeghmati
 * @version 0.1
 */
public interface ConsoleCalcul {
           
    /**
     * Vérifie que la syntaxe des sous-chaines est correcte
     * @return true si la syntaxe est correcte, false sinon
     */
    public boolean verificationSyntaxe();

    /**
     * Vérifie le format de la chaine selon le tableau de vérité après avoir
     * vérifié s'il y a autant de parenthèses ouvrante que de fermante
     * @return true si le format est bon, false sinon
     */
    public boolean verificationFormat();

    /**
     * Vérifie si une opération à une affectation correcte
     * @return true si l'affectation est correcte
     */
    public boolean verificationAffectation();
    
    /**
     * Restaure et remplace les noms de zones mémoire (ou cellules) dans
     * l'expression par la valeur qu'elle contient
     * @return true si la restauration s'est bien passé, false si on tente de
     *          restaurer une sauvegarde qui ne contient aucune valeur
     */
    public boolean restaurationSauvegarde();

    /**
     * Vérifie si l'affectation d'un nombre dans une zone mémoire ou cellule
     * est correcte. On suppose qu'il n'y a qu'un seul = dans l'expression. 
     * Ensuite on garde la zone affecté.
     * @return true si l'affectation est possible, false sinon
     */
    public boolean affectationCorrecte();

    /**
     * Calcule l'expression de cette instruction lorsqu'elle possède des
     * parenthèses
     */
    public void calculExpressionParenthese();

    /**
     * Calcule l'expression simple (sans parenthèse) délimitée par le chiffre du
     * début, et le chiffre de la fin et retourne le résultat dans une chaine.
     * @param debut Début de l'expression
     * @param fin Fin de l'expression
     */
    public void calculExpressionSimple(int debut, int fin);

    /**
     * Effectue l'opération passé en argument aux opérantes situées avant
     * et après la position de l'opérateur
     * @param operation Opération à effectuer
     *          - 0 : Division
     *          - 1 : Multiplication
     *          - 2 : Addition
     *          - 3 : Soustraction
     * @param position Position de l'opérateur
     */
    public void operateurOperationOperateur(int operation, int position);
    
    /**
     * Décallage de deux cases après avoir effectué une opération
     * @param position Position à partir de laquelle on commence le décallage
     */
    public void decallage(int position);
   
    /**
     * Recherche le nombre de = dans une expression
     * @return le nombre de = dans une expression
     */
    public int nombreEgaleExpression();

    /**
     * Recherche la zone mémoire (ou cellule) à laquelle le résultat devra être 
     * affecté
     * @return Indice de la zone mémoire à laquelle le résultat 
     *         devra être affecté
     */
    public int caseAAffecter();
    
    /**
     * Longueur actuelle de l'instruction
     * @return longueur de l'instruction
     */
    public int longueurInstruction();
}
