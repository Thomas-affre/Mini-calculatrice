/* 
 * ConsoleCalcul.java                            17 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

/**
 * Interface r�unissant les m�thodes de calcul qui pourront �tre utilis�es pour
 * la calculatrice ou le tableur.
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public interface ConsoleCalcul {
    
    /**
     * Effectue tous les contr�les sur la commande saisie par l'utilisateur.
     * Si elle ne trouve aucune erreur, le calcul pourra �tre �ffectu�.
     */
    public void controleCommande();
    
    /**
     * Effectue le calcul d'une expression correcte qui a �t� contr�l�e au 
     * pr�alable.
     */
    public void calculExpression();
           
    /**
     * V�rifie que la syntaxe des sous-chaines est correcte. S'il y a un 
     * probl�me, une erreur est d�clench�e.
     * @return true si la syntaxe est correcte, false sinon
     */
    public boolean verificationSyntaxe();

    /**
     * Chaque �l�ment est cens� �tre s�par� d'un espace. Mais si cela n'est pas
     * le cas, plut�t que de d�clencher une erreur, on cherche � s�parer les
     * �l�ments qui peuvent l'�tre par un espace.
     * @param position Indice de l'�l�ment � modifier
     * @return nombre de s�parations effectu�es dans la chaine,
     *         0 s'il n'y a pas eu de s�parations
     */
    public int separationElementsExpression(int position);

    /**
     * V�rifie le format de la cha�ne selon le tableau de v�rit� ainsi que le
     * nombre de parenth�ses ouvrantes et fermantes qui doit �tre �quivalent.
     * S'il y a un probl�me, une erreur est d�clench�e.
     * @return true si le format est bon, false sinon
     */
    public boolean verificationFormat();

    /**
     * V�rifie si une op�ration � une affectation et si elle est correcte.
     * S'il y a un probl�me, une erreur est d�clench�e.
     * @return true si l'affectation est correcte, false sinon
     */
    public boolean verificationAffectation();
    
    /**
     * Restaure et remplace les noms de zones m�moire (ou cellules) dans
     * l'expression par la valeur qu'elle contient
     * @return true si la restauration s'est bien pass�, false si on tente de
     *          restaurer une sauvegarde qui ne contient aucune valeur
     */
    public boolean restaurationSauvegarde();

    /**
     * V�rifie si l'affectation d'un nombre dans une zone m�moire ou cellule
     * est correcte. On suppose qu'il n'y a qu'un seul '=' dans l'expression. 
     * Ensuite on garde la zone affect�e (ou la cellule).
     * @return true si l'affectation est possible, false sinon
     */
    public boolean affectationCorrecte();
    
    /**
     * Teste si une cha�ne est un �l�ment d'une op�ration (r�el, op�rateur,
     * m�moire ou parenth�se)
     * @param aTester Cha�ne � tester
     * @return true s'il s'agit d'un �l�ment, false sinon
     */
    public boolean estUneSousChaine(String aTester);

    /**
     * Calcule l'expression d'une instruction lorsqu'elle poss�de des
     * parenth�ses
     */
    public void calculExpressionParenthese();

    /**
     * Calcule l'expression simple (sans parenth�ses) d�limit�e par le chiffre 
     * du d�but, et le chiffre de la fin et retourne le r�sultat dans une 
     * cha�ne.
     * @param debut Indice de d�but de la sous-cha�ne � calculer
     * @param fin Indice de fin de la sous-cha�ne � calculer
     */
    public void calculExpressionSimple(int debut, int fin);

    /**
     * Effectue l'op�ration pass�e en argument aux op�randes situ�es avant
     * et apr�s la position de l'op�rateur. Lorsque cela est fait, on met le
     * r�sultat dans l'op�rande situ�e � gauche de l'op�rateur et on d�cale 
     * l'expression de 2 cases.
     * @param operation Op�ration � effectuer
     *          - 0 : Division
     *          - 1 : Multiplication
     *          - 2 : Addition
     *          - 3 : Soustraction
     * @param position Position de l'op�rateur
     */
    public void operateurOperationOperateur(int operation, int position);
    
    /**
     * D�calage de deux cases apr�s avoir effectu� une op�ration
     * @param position Position � partir de laquelle on commence le d�calage
     */
    public void decalage(int position);
   
    /**
     * Recherche le nombre de '=' dans une expression
     * @return le nombre de '=' dans une expression
     */
    public int nombreEgalExpression();

    /**
     * Recherche la zone m�moire (ou cellule) � laquelle le r�sultat devra �tre 
     * affect�
     * @return Indice de la zone m�moire � laquelle le r�sultat 
     *         devra �tre affect�
     */
    public int caseAAffecter();
    
    /**
     * Tente d'arrondir un r�sultat apr�s un calcul. Si le r�sultat est un
     * entier, la virgule est retir�e, sinon le r�sultat est arrondi � deux
     * chiffres apr�s la virgule.
     */
    public void arrondissementResultat();
    
    /**
     * Longueur actuelle de l'instruction
     * @return longueur de l'instruction
     */
    public int longueurInstruction();
}