/* 
 * Tableur.java                            17 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.tableur;

import minicalcul.fenetre.GrilleCellules;
import minicalcul.programme.commandes.ConsoleCalculTableur;

/**
 * Regroupement de cellules formant un tableur
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public class Tableur {
    
    /** R�f�rence � l'objet de calcul du tableur */
    private ConsoleCalculTableur calculTableur;
    
    /** 
     * Cellules du tableur 
     *      - 1�re dimension : lignes du tableur
     *      - 2�me dimension : cellules de la ligne 
     */
    private Cellule[][] cellules;
    
    /** R�f�rence � l'interface du tableur sur la fen�tre */
    private GrilleCellules grilleTableur;
    
    /**
     * Constructeur du tableur
     * @param laGrille R�f�rence � la grille de l'interface
     */
    public Tableur(GrilleCellules laGrille) {
        this.grilleTableur = laGrille;
 
        this.creationCellules();
    }
    
    /**
     * Cr�� toutes les cellules du tableur
     */
    private void creationCellules() {
        this.cellules = new Cellule[20][26];
        
        // On cr�� toutes les cellules
        for (int i = 0; i < this.cellules.length; i++) {

            for (int j = 0; j < this.cellules[i].length; j++) {
                String lettre = new Character((char) (j + 65)).toString();
                // identification de la cellule au format : 
                // lettre + (num�ro de la cellule+1)    
                this.cellules[i][j] = new Cellule(
                        lettre + Integer.toString(i+1), this);
            }
        }  
    }

    /**
     * Regarde si une cellule de ce tableur est initialis�e
     * @param aTester Coordonn�es de la cellule � tester
     * @return true si la cellule est initialis�e, false sinon
     */
    public boolean celluleInitialisee(String aTester) {
        int[] coordonnees = conversionChaineEnCoordonnees(aTester);

        return this.grilleTableur.estInitialisee(coordonnees);
    }
    
    /**
     * Apr�s chaque commande, on met � jour toutes les cellules contenant 
     * une formule
     */
    public void miseAJourTableur() {

        for (int i = 0; i < this.getCellules().length; i++) {
            for (int j = 0; j < this.getCellules()[i].length; j++) {
                if (this.getCellules()[i][j].isFormule()) {
                    // On lance une op�ration
                    this.calculTableur.traitementCommande( 
                            this.getCellules()[i][j].getContenu());
                }
            }
        }
    }

    /**
     * Restaure le contenu brut d'une cellule � partir de ses coordonn�es
     * @param cellule Coordonn�es de la cellule
     * @return Valeur de la cellule en chaine de caract�re
     */
    public String restaurationValeurCellule(String cellule) {
        int[] coordonnees = conversionChaineEnCoordonnees(cellule);
        
        return this.grilleTableur.valeurCellule(coordonnees);
    }
    
    /**
     * Restaure le contenu d'une cellule � partir de ses coordonn�es
     * @param cellule Coordonn�es de la cellule
     * @return Contenu de la cellule en chaine de caract�re
     */
    public String restaurationContenuCellule(String cellule) {
        int[] coordonnees = conversionChaineEnCoordonnees(cellule);
        
        String aRetourner =
                this.cellules[coordonnees[0]][coordonnees[1]].isFormule() ?
                // S'il y'a une formule
                this.cellules[coordonnees[0]][coordonnees[1]].getContenu()
                // Sinon on restaure la valeur brute
                : this.cellules[coordonnees[0]][coordonnees[1]].getValeur();

        /*
         * Si le contenu est une formule, il contient � la fin " = [cellule] "
         * donc on l'enl�ve
         */
        if (this.cellules[coordonnees[0]][coordonnees[1]].isFormule()) {
            for (int i = 0; i < aRetourner.length(); i++) {
                if (aRetourner.charAt(i) == '=') {
                    aRetourner = aRetourner.substring(0, i-1);
                    break;
                }
            }
        }
        return aRetourner;
    }
    
    
    /**
     * Affecte � la cellule pass�e en argument, la valeur pass�e en argument
     * @param cellule Cellule qui va changer
     * @param valeur Valeur qui va lui �tre affect�e
     */
    public void affectationValeur(String cellule, String valeur) {
        int[] coordonnees = conversionChaineEnCoordonnees(cellule);
        
        // Comme il s'agit d'une valeur, on l'affecte directement dans la grille
        this.grilleTableur.miseAJourValeur(coordonnees, valeur);
        
        // Puis dans le contenu si cette cellule ne contient pas de formule
        if (!this.cellules[coordonnees[0]][coordonnees[1]].isFormule()) {
            this.cellules[coordonnees[0]][coordonnees[1]].setContenu(valeur);
        }
    }

    /**
     * Affecte la formule � une cellule et sp�cifie que cette cellule contiendra
     * d�sormais une formule afin de pouvoir les mettre � jour lorsqu'une autre
     * cellule sera modifi�e
     * @param cellule Cellule � laquelle la formule est affect�e
     * @param formule Formule que l'on va affecter � la cellule
     */
    public void affectationFormule(String cellule, String formule) {
        int[] coordonnees = conversionChaineEnCoordonnees(cellule);

        // La cellule contient d�sormais une formule
        this.cellules[coordonnees[0]][coordonnees[1]].setFormule(true);
        
        // On lui donne la formule
        this.cellules[coordonnees[0]][coordonnees[1]].setContenu(formule);
    }

    /**
     * R�initialise une cellule lors d'une affectation sans formule
     * @param cellule Cellule � r�initialiser 
     */
    public void reinitialisationCellule(String cellule) {
        int[] coordonnees = conversionChaineEnCoordonnees(cellule);
        
        // La cellule ne contient pas de formule (programme)
        this.cellules[coordonnees[0]][coordonnees[1]].setFormule(false);
        
        // On enl�ve la formule si elle en contenait une
        this.cellules[coordonnees[0]][coordonnees[1]].setContenu(null);
        
        // On vide la cellule (interface)
        this.grilleTableur.miseAJourValeur(coordonnees, null);
    }

    /**
     * Convertit une cellule sous la forme de chaine de caract�res (ex : A1) en
     * coordonn�es du tableur (ex : A1 : [0,0] et Z20 : [19,25]) 
     * @param aConvertir Chaine � convertir
     * @return Tableau de coordonn�es de la cellule
     */
    @SuppressWarnings("static-method")
    public int[] conversionChaineEnCoordonnees(String aConvertir) {
        
        int[] aRetourner = new int[2];
        // On r�cup�re l'indice du chiffre et on enl�ve 1
        aRetourner[0] = (aConvertir.length() == 2 
                ? Integer.parseInt(aConvertir.substring(1, 2))
                : Integer.parseInt(aConvertir.substring(1, 3))) - 1;  
        
        // On r�cup�re l'indice correspondant � la lettre A=65..Z=90
        aRetourner[1] = new Character(aConvertir.charAt(0)).hashCode() - 65;
        
        return aRetourner;
    }

    /**
     * Convertit les coordonn�es d'une cellule en chaine de caract�res
     * @param coordonnees Coordonn�es de la cellule � convertir
     * @return Chaine de caract�res repr�sentant la cellule (ex : A1)
     */
    @SuppressWarnings("static-method")
    public String conversionCoordonneesEnChaine(int[] coordonnees) {
        StringBuilder aRetourner = new StringBuilder("");

        // La lettre
        aRetourner.append(new Character((char) (coordonnees[1] + 65)).
                toString());
        
        // Le chiffre
        aRetourner.append(Integer.toString(coordonnees[0] + 1));
        
        return aRetourner.toString();
    }

    /**
     * R�cup�re les coordonn�es d'une plage de cellules correcte
     * @param plage de cellule � convertir
     * @return tableau repr�sentant les coordon�es (0 : d�but, 1 : fin)
     */
    public int[][] coordonneesPlageCorrecte(String plage) {
        int[][] aRetourner = new int[2][];
        
        // On d�coupe la plage
        String[] coordonnees = plage.split("\\."); // resultat : [debut, , fin]
        
        aRetourner[0] = conversionChaineEnCoordonnees(coordonnees[0]); // debut
        aRetourner[1] = conversionChaineEnCoordonnees(coordonnees[2]); // fin
        
        return aRetourner;
    }

    /**
     * Va chercher la repr�sentation en chaine de caract�res de la cellule dont
     * les coordonn�es ont �t� pass�es en argument
     * @param coordonnees Coordonn�es de la cellule � rechercher
     * @return Repr�sentation en chaine de caract�re de la cellule
     */
    public String contenuCellule(int[] coordonnees) {
                
        return this.cellules[coordonnees[0]][coordonnees[1]].toString();
    }
    
    /**
     * Sauvegarde les cellules dans un tableau � deux dimensions de String et le
     * renvois. La premi�re dimension correspond � une cellule. Dans la deuxi�me
     * dimension, on trouve : 
     *          0 : les coordonn�es sous forme de chaine
     *          1 : contenu brut de la cellule
     *          2 : formule. null s'il n'y en a pas
     * @return tableau de sauvegarde des cellules
     */
    public String[][] sauvegardeEtatTableur() {
        String[][] aRetourner = new String[520][];
        
        for (int i = 0; i < this.cellules.length; i++) {
            for (int j = 0; j < this.cellules[i].length; j++) {
                aRetourner[i * this.cellules.length + j]
                        = this.cellules[i][j].sauvegardeCellule(); 
            }
        }
                
        return aRetourner;
    }

    /**
     * Restaure dans le tableur le contenu du tableau pass� en argument qui
     * contient tous les �l�ments du tableur
     * @param contenuFichier Contenu du fichier � restaurer 
     */
    public void restaurationTableurFichier(String[][] contenuFichier) {
        
        for (int i = 0; i < this.cellules.length; i++) {
            for (int j = 0; j < this.cellules[i].length; j++) {      
                this.cellules[i][j] = new Cellule(
                        contenuFichier[i *this.cellules.length + j][0],
                        contenuFichier[i * this.cellules.length + j][1],
                        contenuFichier[i * this.cellules.length + j][2], this);
            }
        }
        
        // Puis on le met � jour sur l'interface
        this.miseAJourTableur();
    }

    /**
     * R�initialise l'�tat des cellules de ce tableur
     */
    public void reinitialisationTableur() {
        this.creationCellules();        
    }

    /**
     * Accesseur � cellules
     * @return cellules 
     */
    public Cellule[][] getCellules() {
        return cellules;
    }

    /**
     * Accesseur � grilleTableur
     * @return grilleTableur 
     */
    public GrilleCellules getGrilleTableur() {
        return grilleTableur;
    }

    /**
     * Mutateur de calculTableur
     * @param calculTableur nouveau calculTableur
     */
    public void setCalculTableur(ConsoleCalculTableur calculTableur) {
        this.calculTableur = calculTableur;
    }
}