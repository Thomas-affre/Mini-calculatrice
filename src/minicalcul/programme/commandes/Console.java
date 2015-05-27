/* 
 * Console.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet permettant de mat�rialiser une console de traitements de commandes 
 * contenant :
 *      - des expressions r�guli�res qui pourront �tre utilis�es pour tester
 *        des chaines contenues dans des calculs 
 *      - des constantes d'erreurs pr�d�finis
 *      - les instances communes � toutes les consoles
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public abstract class Console {

    /** Regex d'un entier relatif */
    public static final String REGEX_ENTIER =
            "(\\-{0,1})([0-9]*)(\\.[0]*){0,1}";
    
    /** Regex d'un r�el */
    public static final String REGEX_DOUBLE = 
            "(\\-{0,1})([0-9]{1,13})(\\.[0-9]+)?";

    /** Regex d'un op�rateur */
    public static final String REGEX_OPERATEUR = "[+*/-]{1}";
    
    /** Regex repr�sentant une zone m�moire */
    public static final String REGEX_ZONE_MEMOIRE = "[A-Z]{1}";
    
    /** Regex repr�sentant une plage m�moire */
    public static final String REGEX_PLAGE_MEMOIRES = "[A-Z]{1}\\.\\.[A-Z]{1}";
    
    /** Regex repr�sentant les coordonn�es d'une cellule sans blocage */
    public static final String REGEX_CELLULE = 
            "[A-Z]{1}(([1-9])|(1[0-9]{1})|20)";
    
    /** Regex repr�sentant les coordonn�es d'une cellule avec blocage ($) */
    public static final String REGEX_CELLULE_BLOCAGE = 
            "[$]{0,1}[A-Z]{1}[$]{0,1}(([1-9])|(1[0-9]{1})|20)";
    
    /**
     * Regex repr�sentant les coordonn�es d'une cellule avec blocage au niveau
     * de la colone (lettre). Le dollar se trouve en premi�re position dans la
     * chaine.
     */
    public static final String REGEX_CELLULE_BLOCAGE_COLONE = 
            "[$]{1}[A-Z]{1}[$]{0,1}(([1-9])|(1[0-9]{1})|20)";
    
    /** 
     * Regex repr�sentant les coordonn�es d'une cellule avec blocage au niveau
     * de la ligne (chiffre). Le dollar se trouve entre la lettre et le nombre.
     */
    public static final String REGEX_CELLULE_BLOCAGE_LIGNE = 
            "[$]{0,1}[A-Z]{1}[$]{1}(([1-9])|(1[0-9]{1})|20)";
    
    /** Regex repr�sentant une plage de cellules sans blocage */
    public static final String REGEX_PLAGE_CELLULES = 
            REGEX_CELLULE +  "\\.\\." + REGEX_CELLULE;
        
    /** Erreur d�clench�e si un symbole est inconnu */
    public static final int ERREUR_SYNTAXE = 1;
    
    /**
     * Erreur d�clench�e si le nombre de parenth�ses ouvrantes et fermantes est
     * diff�rent lors d'un calcul
     */
    public static final int ERREUR_EQUIVALENCE_PARENTHESES = 2;
    
    /** Erreur d�clench�e si on trouve une parenth�se apr�s une affectation */
    public static final int ERREUR_PARENTHESES_APRES_EGAL = 3;

    /** Erreur d�clench�e si un �l�ment d'un calcul � un mauvais successeur */
    public static final int ERREUR_MAUVAIS_SUCCESSEUR = 4;

    /** Erreur d�clench�e si le premier �l�ment d'un calcul est incorrect */
    public static final int ERREUR_MAUVAIS_DEBUT = 5;

    /** Erreur d�clench�e si le dernier �l�ment d'un calcul est incorrect */
    public static final int ERREUR_MAUVAISE_FIN = 6;

    /** Erreur d�clench�e si on tente d'utiliser une variable non intialis�e */
    public static final int ERREUR_INTIALISATION = 7;
    
    /** Erreur d�clench�e si une affectation est attendue mais est absent */
    public static final int ERREUR_AFFECTATION = 8;
    
    /** Erreur d�clench�e s'il y a une op�ration apr�s un �gal */
    public static final int ERREUR_OPERATION_APRES_EGAL = 9;
    
    /** Erreur d�clench�e s'il y a un calcul impossible */
    public static final int ERREUR_MATHEMATIQUE = 10;

    /** 
     * Erreur d�clench�e si le nombre d'arguments d'une commande du gestionnaire
     * de m�moires ou du tableur n'est pas celui attendu
     */
    public static final int ERREUR_NB_ARGUMENT = 11;
    
    /** Erreur d�clench�e si une plage m�moire est mauvaise */
    public static final int ERREUR_PLAGE_MEMOIRE = 12;
    
    /** Erreur d�clench�e si une plage de m�moire n'est pas ordonn�e */
    public static final int ERREUR_ORDRE_PLAGE_MEMOIRE = 13;
    
    /** Erreur d�clench�e si un argument doit �tre un r�el mais ne l'est pas */
    public static final int ERREUR_VALEUR_ARGUMENT = 14;
    
    /** Erreur d�clench�e s'il manque une valeur dans une commande */
    public static final int ERREUR_VALEUR_ABSENTE = 15;
    
    /** Erreur d�clench�e s'il y a un probl�me de dimension lors d'une copie */
    public static final int ERREUR_DIMENSION_COPIE = 16;
    
    /** Erreur d�clench�e si une m�moire est attendue dans une commande */
    public static final int ERREUR_MEMOIRE_ABSENTE = 17;
    
    /** Erreur d�clench�e si aucune affectation n'est attendue */
    public static final int ERREUR_AFFECTATION_INNATENDUE = 18;
    
    /** Erreur d�clench�e si on attend une ligne ou une colonne en plage */
    public static final int ERREUR_PLAGE_LIGNE_OU_COLONNE = 19;
        
    /** R�f�rence permettant d'acc�der aux �l�ments de la console */
    private FenetrePrincipale laFenetre;
          
    /** Objet permettant de d�finir l'arrondissement des r�els */
    private final DecimalFormat ARRONDIR = new DecimalFormat("0.00",
            new DecimalFormatSymbols(Locale.US));

    /** Commande saisie par l'utilisateur */
    protected String commande;
    
    /** Passe � true si une erreur a �t� trouv�e lors des contr�les */
    protected boolean erreurTrouvee;
    
    /** Saisie de l'utilisateur divis�e dans un tableau � chaque sous-chaine */
    protected String[] instructions;
    
    /** Message � retourner sur la console */
    protected String aRetourner;
    
    /** Indice de l'argument o� il y a un probl�me */
    protected int lieuMauvaisArgument;
    
    /**
     * Traite la commande saisie par l'utilisateur
     * @param commande Commande saisie par l'utilisateur � traiter
     * @return r�sultat de la commande si elle est bonne, message d'erreur sinon
     */
    public abstract String traitementCommande(String commande);
    
    /**
     * R�initialise l'�tat d'instance de la console � chaque d�but d'instruction
     */
    protected abstract void reinitialisation();
    
    /**
     * Recherche le lieu de l'erreur et retourne un message en fonction de cette
     * erreur qui sera affich� sur la console
     * @param typeErreur Type d'erreur � rechercher
     */
    protected abstract void rechercheErreur(int typeErreur);
    
    /**
     * Teste si une chaine de caract�res correspond � une m�moire existante et 
     * accept�e par la console
     * @param aTester Chaine � tester
     * @return true s'il s'agit d'une m�moire accept�e par la console,
     *         false sinon
     */
    protected abstract boolean estUneMemoire(String aTester);
    
    /**
     * Teste si une m�moire CORRECTE que l'on souhaite exploit�e est initialis�e
     * @param aTester Chaine � tester qui doit �tre une m�moire
     * @return true si la m�moire est initialis�e, false sinon
     */
    protected abstract boolean estInitialisee(String aTester);
                        
    /**
     * V�rifie si une chaine est un op�rateur
     * @param aTester Chaine � tester
     * @return true si aTester est un op�rateur, false sinon
     */
    public static boolean estUnOperateur(String aTester) {
        return Pattern.compile(REGEX_OPERATEUR).matcher(aTester).matches();        
    }
    
    /**
     * Teste si une chaine de caract�res REPRESENTANT UN REEL est positive
     * @param aTester Chaine � tester qui doit �tre un r�el
     * @return true si c'est positif, false sinon
     */
    public static boolean estPositif(String aTester) {
        return aTester.charAt(0) != '-';
    }

    /**
     * Teste si une chaine de caract�res est un entier
     * @param aTester Chaine � tester
     * @return true s'il s'agit d'un entier, false sinon
     */
    public static boolean estUnEntier(String aTester) {
        return Pattern.compile(REGEX_ENTIER).matcher(aTester).matches();
    }

    /**
     * Teste si une chaine de caract�res est un r�el
     * @param aTester Chaine � tester
     * @return true s'il s'agit d'un double, false sinon
     */
    public static boolean estUnDouble(String aTester) {
        return Pattern.compile(REGEX_DOUBLE).matcher(aTester).matches();
    }

    /**
     * Accesseur � laFenetre
     * @return laFenetre 
     */
    public FenetrePrincipale getLaFenetre() {
        return laFenetre;
    }
    
    /**
     * Mutateur de laFenetre
     * @param laFenetre nouveau laFenetre
     */
    public void setLaFenetre(FenetrePrincipale laFenetre) {
        this.laFenetre = laFenetre;
    }

    /**
     * Accesseur � ARRONDIR
     * @return ARRONDIR 
     */
    public DecimalFormat getARRONDIR() {
        return ARRONDIR;
    }
}