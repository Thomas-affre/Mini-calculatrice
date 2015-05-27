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
 * Objet permettant de matérialiser une console de traitements de commandes 
 * contenant :
 *      - des expressions régulières qui pourront être utilisées pour tester
 *        des chaines contenues dans des calculs 
 *      - des constantes d'erreurs prédéfinis
 *      - les instances communes à toutes les consoles
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
public abstract class Console {

    /** Regex d'un entier relatif */
    public static final String REGEX_ENTIER =
            "(\\-{0,1})([0-9]*)(\\.[0]*){0,1}";
    
    /** Regex d'un réel */
    public static final String REGEX_DOUBLE = 
            "(\\-{0,1})([0-9]{1,13})(\\.[0-9]+)?";

    /** Regex d'un opérateur */
    public static final String REGEX_OPERATEUR = "[+*/-]{1}";
    
    /** Regex représentant une zone mémoire */
    public static final String REGEX_ZONE_MEMOIRE = "[A-Z]{1}";
    
    /** Regex représentant une plage mémoire */
    public static final String REGEX_PLAGE_MEMOIRES = "[A-Z]{1}\\.\\.[A-Z]{1}";
    
    /** Regex représentant les coordonnées d'une cellule sans blocage */
    public static final String REGEX_CELLULE = 
            "[A-Z]{1}(([1-9])|(1[0-9]{1})|20)";
    
    /** Regex représentant les coordonnées d'une cellule avec blocage ($) */
    public static final String REGEX_CELLULE_BLOCAGE = 
            "[$]{0,1}[A-Z]{1}[$]{0,1}(([1-9])|(1[0-9]{1})|20)";
    
    /**
     * Regex représentant les coordonnées d'une cellule avec blocage au niveau
     * de la colone (lettre). Le dollar se trouve en première position dans la
     * chaine.
     */
    public static final String REGEX_CELLULE_BLOCAGE_COLONE = 
            "[$]{1}[A-Z]{1}[$]{0,1}(([1-9])|(1[0-9]{1})|20)";
    
    /** 
     * Regex représentant les coordonnées d'une cellule avec blocage au niveau
     * de la ligne (chiffre). Le dollar se trouve entre la lettre et le nombre.
     */
    public static final String REGEX_CELLULE_BLOCAGE_LIGNE = 
            "[$]{0,1}[A-Z]{1}[$]{1}(([1-9])|(1[0-9]{1})|20)";
    
    /** Regex représentant une plage de cellules sans blocage */
    public static final String REGEX_PLAGE_CELLULES = 
            REGEX_CELLULE +  "\\.\\." + REGEX_CELLULE;
        
    /** Erreur déclenchée si un symbole est inconnu */
    public static final int ERREUR_SYNTAXE = 1;
    
    /**
     * Erreur déclenchée si le nombre de parenthèses ouvrantes et fermantes est
     * différent lors d'un calcul
     */
    public static final int ERREUR_EQUIVALENCE_PARENTHESES = 2;
    
    /** Erreur déclenchée si on trouve une parenthèse après une affectation */
    public static final int ERREUR_PARENTHESES_APRES_EGAL = 3;

    /** Erreur déclenchée si un élément d'un calcul à un mauvais successeur */
    public static final int ERREUR_MAUVAIS_SUCCESSEUR = 4;

    /** Erreur déclenchée si le premier élément d'un calcul est incorrect */
    public static final int ERREUR_MAUVAIS_DEBUT = 5;

    /** Erreur déclenchée si le dernier élément d'un calcul est incorrect */
    public static final int ERREUR_MAUVAISE_FIN = 6;

    /** Erreur déclenchée si on tente d'utiliser une variable non intialisée */
    public static final int ERREUR_INTIALISATION = 7;
    
    /** Erreur déclenchée si une affectation est attendue mais est absent */
    public static final int ERREUR_AFFECTATION = 8;
    
    /** Erreur déclenchée s'il y a une opération après un égal */
    public static final int ERREUR_OPERATION_APRES_EGAL = 9;
    
    /** Erreur déclenchée s'il y a un calcul impossible */
    public static final int ERREUR_MATHEMATIQUE = 10;

    /** 
     * Erreur déclenchée si le nombre d'arguments d'une commande du gestionnaire
     * de mémoires ou du tableur n'est pas celui attendu
     */
    public static final int ERREUR_NB_ARGUMENT = 11;
    
    /** Erreur déclenchée si une plage mémoire est mauvaise */
    public static final int ERREUR_PLAGE_MEMOIRE = 12;
    
    /** Erreur déclenchée si une plage de mémoire n'est pas ordonnée */
    public static final int ERREUR_ORDRE_PLAGE_MEMOIRE = 13;
    
    /** Erreur déclenchée si un argument doit être un réel mais ne l'est pas */
    public static final int ERREUR_VALEUR_ARGUMENT = 14;
    
    /** Erreur déclenchée s'il manque une valeur dans une commande */
    public static final int ERREUR_VALEUR_ABSENTE = 15;
    
    /** Erreur déclenchée s'il y a un problème de dimension lors d'une copie */
    public static final int ERREUR_DIMENSION_COPIE = 16;
    
    /** Erreur déclenchée si une mémoire est attendue dans une commande */
    public static final int ERREUR_MEMOIRE_ABSENTE = 17;
    
    /** Erreur déclenchée si aucune affectation n'est attendue */
    public static final int ERREUR_AFFECTATION_INNATENDUE = 18;
    
    /** Erreur déclenchée si on attend une ligne ou une colonne en plage */
    public static final int ERREUR_PLAGE_LIGNE_OU_COLONNE = 19;
        
    /** Référence permettant d'accéder aux éléments de la console */
    private FenetrePrincipale laFenetre;
          
    /** Objet permettant de définir l'arrondissement des réels */
    private final DecimalFormat ARRONDIR = new DecimalFormat("0.00",
            new DecimalFormatSymbols(Locale.US));

    /** Commande saisie par l'utilisateur */
    protected String commande;
    
    /** Passe à true si une erreur a été trouvée lors des contrôles */
    protected boolean erreurTrouvee;
    
    /** Saisie de l'utilisateur divisée dans un tableau à chaque sous-chaine */
    protected String[] instructions;
    
    /** Message à retourner sur la console */
    protected String aRetourner;
    
    /** Indice de l'argument où il y a un problème */
    protected int lieuMauvaisArgument;
    
    /**
     * Traite la commande saisie par l'utilisateur
     * @param commande Commande saisie par l'utilisateur à traiter
     * @return résultat de la commande si elle est bonne, message d'erreur sinon
     */
    public abstract String traitementCommande(String commande);
    
    /**
     * Réinitialise l'état d'instance de la console à chaque début d'instruction
     */
    protected abstract void reinitialisation();
    
    /**
     * Recherche le lieu de l'erreur et retourne un message en fonction de cette
     * erreur qui sera affiché sur la console
     * @param typeErreur Type d'erreur à rechercher
     */
    protected abstract void rechercheErreur(int typeErreur);
    
    /**
     * Teste si une chaine de caractères correspond à une mémoire existante et 
     * acceptée par la console
     * @param aTester Chaine à tester
     * @return true s'il s'agit d'une mémoire acceptée par la console,
     *         false sinon
     */
    protected abstract boolean estUneMemoire(String aTester);
    
    /**
     * Teste si une mémoire CORRECTE que l'on souhaite exploitée est initialisée
     * @param aTester Chaine à tester qui doit être une mémoire
     * @return true si la mémoire est initialisée, false sinon
     */
    protected abstract boolean estInitialisee(String aTester);
                        
    /**
     * Vérifie si une chaine est un opérateur
     * @param aTester Chaine à tester
     * @return true si aTester est un opérateur, false sinon
     */
    public static boolean estUnOperateur(String aTester) {
        return Pattern.compile(REGEX_OPERATEUR).matcher(aTester).matches();        
    }
    
    /**
     * Teste si une chaine de caractères REPRESENTANT UN REEL est positive
     * @param aTester Chaine à tester qui doit être un réel
     * @return true si c'est positif, false sinon
     */
    public static boolean estPositif(String aTester) {
        return aTester.charAt(0) != '-';
    }

    /**
     * Teste si une chaine de caractères est un entier
     * @param aTester Chaine à tester
     * @return true s'il s'agit d'un entier, false sinon
     */
    public static boolean estUnEntier(String aTester) {
        return Pattern.compile(REGEX_ENTIER).matcher(aTester).matches();
    }

    /**
     * Teste si une chaine de caractères est un réel
     * @param aTester Chaine à tester
     * @return true s'il s'agit d'un double, false sinon
     */
    public static boolean estUnDouble(String aTester) {
        return Pattern.compile(REGEX_DOUBLE).matcher(aTester).matches();
    }

    /**
     * Accesseur à laFenetre
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
     * Accesseur à ARRONDIR
     * @return ARRONDIR 
     */
    public DecimalFormat getARRONDIR() {
        return ARRONDIR;
    }
}