/* 
 * ConsoleCalcul.java                            17 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

/**
 * Interface réunissant les méthodes de calcul qui pourront être utilisées pour
 * la calculatrice ou le tableur.
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
public interface ConsoleCalcul {
    
    /**
     * Effectue tous les contrôles sur la commande saisie par l'utilisateur.
     * Si elle ne trouve aucune erreur, le calcul pourra être éffectué.
     */
    public void controleCommande();
    
    /**
     * Effectue le calcul d'une expression correcte qui a été contrôlée au 
     * préalable.
     */
    public void calculExpression();
           
    /**
     * Vérifie que la syntaxe des sous-chaines est correcte. S'il y a un 
     * problème, une erreur est déclenchée.
     * @return true si la syntaxe est correcte, false sinon
     */
    public boolean verificationSyntaxe();

    /**
     * Chaque élément est censé être séparé d'un espace. Mais si cela n'est pas
     * le cas, plutôt que de déclencher une erreur, on cherche à séparer les
     * éléments qui peuvent l'être par un espace.
     * @param position Indice de l'élément à modifier
     * @return nombre de séparations effectuées dans la chaine,
     *         0 s'il n'y a pas eu de séparations
     */
    public int separationElementsExpression(int position);

    /**
     * Vérifie le format de la chaîne selon le tableau de vérité ainsi que le
     * nombre de parenthèses ouvrantes et fermantes qui doit être équivalent.
     * S'il y a un problème, une erreur est déclenchée.
     * @return true si le format est bon, false sinon
     */
    public boolean verificationFormat();

    /**
     * Vérifie si une opération à une affectation et si elle est correcte.
     * S'il y a un problème, une erreur est déclenchée.
     * @return true si l'affectation est correcte, false sinon
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
     * est correcte. On suppose qu'il n'y a qu'un seul '=' dans l'expression. 
     * Ensuite on garde la zone affectée (ou la cellule).
     * @return true si l'affectation est possible, false sinon
     */
    public boolean affectationCorrecte();
    
    /**
     * Teste si une chaîne est un élément d'une opération (réel, opérateur,
     * mémoire ou parenthèse)
     * @param aTester Chaîne à tester
     * @return true s'il s'agit d'un élément, false sinon
     */
    public boolean estUneSousChaine(String aTester);

    /**
     * Calcule l'expression d'une instruction lorsqu'elle possède des
     * parenthèses
     */
    public void calculExpressionParenthese();

    /**
     * Calcule l'expression simple (sans parenthèses) délimitée par le chiffre 
     * du début, et le chiffre de la fin et retourne le résultat dans une 
     * chaîne.
     * @param debut Indice de début de la sous-chaîne à calculer
     * @param fin Indice de fin de la sous-chaîne à calculer
     */
    public void calculExpressionSimple(int debut, int fin);

    /**
     * Effectue l'opération passée en argument aux opérandes situées avant
     * et après la position de l'opérateur. Lorsque cela est fait, on met le
     * résultat dans l'opérande située à gauche de l'opérateur et on décale 
     * l'expression de 2 cases.
     * @param operation Opération à effectuer
     *          - 0 : Division
     *          - 1 : Multiplication
     *          - 2 : Addition
     *          - 3 : Soustraction
     * @param position Position de l'opérateur
     */
    public void operateurOperationOperateur(int operation, int position);
    
    /**
     * Décalage de deux cases après avoir effectué une opération
     * @param position Position à partir de laquelle on commence le décalage
     */
    public void decalage(int position);
   
    /**
     * Recherche le nombre de '=' dans une expression
     * @return le nombre de '=' dans une expression
     */
    public int nombreEgalExpression();

    /**
     * Recherche la zone mémoire (ou cellule) à laquelle le résultat devra être 
     * affecté
     * @return Indice de la zone mémoire à laquelle le résultat 
     *         devra être affecté
     */
    public int caseAAffecter();
    
    /**
     * Tente d'arrondir un résultat après un calcul. Si le résultat est un
     * entier, la virgule est retirée, sinon le résultat est arrondi à deux
     * chiffres après la virgule.
     */
    public void arrondissementResultat();
    
    /**
     * Longueur actuelle de l'instruction
     * @return longueur de l'instruction
     */
    public int longueurInstruction();
}