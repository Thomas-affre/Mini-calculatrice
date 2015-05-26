/* 
 * GrilleCellules.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Grille de JTextFields correspondant aux cellules du tableur 
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public class GrilleCellules {

    /** 
     * Cellules du tableur 
     *      - 1�re dimension : lignes du tableur
     *      - 2�me dimension : cellules de la ligne 
     */
    private JTextField[][] tableau;
    
    /**
     * Constructeur du tableur
     */
    public GrilleCellules() {
        this.tableau = new JTextField[20][26];
    }
    
    /**
     * Construction des cellules du tableau une par une
     * @param numLigne Num�ro de la ligne -1 o� se trouve la cellule 
     * @param numColone Num�ro de la colone -1 o� se trouve la cellule 
     */
    public void constructionCellule(int numLigne, int numColone) {

        // Construction � vide
        this.tableau[numLigne][numColone] = new JTextField();
        
        // Propri�t�s
        
        this.tableau[numLigne][numColone].setEditable(false); // Non �ditable
        this.tableau[numLigne][numColone].setBackground(Color.WHITE);// Fond
        // Police
        this.tableau[numLigne][numColone].setFont(new Font("Courier", 0, 12));
        this.tableau[numLigne][numColone].setBorder(    // Bordure
                BorderFactory.createLineBorder(Color.GRAY));
        // Positionnement du texte
        this.tableau[numLigne][numColone]
                .setHorizontalAlignment(JTextField.CENTER);
    }

    /**
     * Teste si une cellule pass�e en argument est initialis�e
     * @param coordonnees Coordonn�es de la cellule � tester
     * @return true si la cellule est initialis�e, false sinon
     */
    public boolean estInitialisee(int[] coordonnees) {
        return !(this.tableau[coordonnees[0]][coordonnees[1]].getText()
                                .equals("")
                 || this.tableau[coordonnees[0]][coordonnees[1]].getText()
                                .equals("?"));   
    }

    /**
     * Recherche la valeur brute contenue dans la cellule pass�e en argument
     * @param coordonnees Coordonn�es de la cellule
     * @return valeur de la cellule pass�e en argument
     */
    public String valeurCellule(int[] coordonnees) {
        return this.tableau[coordonnees[0]][coordonnees[1]].getText();
    }

    /**
     * Met � jour le contenu de la cellule pass�e en argument par la valeur
     * pass�e en argument sans prendre en compte une �ventuelle formule
     * @param coordonnees Coordonn�es de la cellule
     * @param valeur Nouvelle valeur de la cellule
     */
    public void miseAJourValeur(int[] coordonnees, String valeur) {
        this.tableau[coordonnees[0]][coordonnees[1]].setText(valeur);
    }
    
    /**
     * R�initialise toutes les cellules du tableur
     */
    public void reinitialisationInterfaceTableur() {
        
        for (int i = 0; i < this.tableau.length; i++) {
            for (int j = 0; j < this.tableau[i].length; j++) {
                this.tableau[i][j].setText("");   
            }
        }   
    }

    /**
     * Accesseur � tableau
     * @return tableau 
     */
    public JTextField[][] getTableau() {
        return tableau;
    }
}