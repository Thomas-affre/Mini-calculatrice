/* 
 * PanneauTableur.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Panneau de l'onglet du tableur
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class PanneauTableur extends JPanel {
    
    /** Panneau permettant de rajouter un scroll au tableur */
    private JScrollPane scrollPane;
    
    /** Premi�re colonne du tableau qui reste fig�e */
    private JPanel premiereColonne;
    
    /** Tableau d'affichage du tableur */
    private JPanel tableau;

    /** Labels d'affichage des chiffres dans la premi�re colonne */
    private JLabel[] lignes;

    /** Labels d'affichage des lettres dans la premi�re ligne */
    private JLabel[] colonnes;
    
    /** Grille correspondant aux cellules du tableur */
    private GrilleCellules grilleTableur;

    /**
     * Construction du panneau
     */
    public PanneauTableur() {
        this.setLayout(null);
        
        // Cr�ation des headers du tableur (on ajoute 1 pour les headers)
        this.lignes = new JLabel[21]; 
        this.colonnes = new JLabel[26];
                
        this.creationPremiereColonne();

        // On va cr�er le tableur ligne par ligne en commancant par le header
        this.creationTableur();
        this.add(this.scrollPane);
    }

    /**
     * Cr�� la premi�re colonne du tableur contenant les chiffres de 1 � 20
     */
    private void creationPremiereColonne() {

        // Construction du panneau dispos� dans une grille
        this.premiereColonne = new JPanel(new GridLayout(20,1));
        this.premiereColonne.setBounds(20, 34, 35, 491); // Taille et position
        
        for (int i = 0; i < this.lignes.length - 1; i++) {
            this.lignes[i] = new JLabel(Integer.toString(i + 1), JLabel.CENTER);
            this.lignes[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.premiereColonne.add(this.lignes[i]);
        }

        this.add(this.premiereColonne);
    }

    /**
     * Cr�� et affiche le tableur
     */
    private void creationTableur() {
        
        // Cr�ation des cellules
        this.grilleTableur = new GrilleCellules();

        // Cr�ation du tableur
        this.tableau = new JPanel(new GridLayout(21,26));
        this.tableau.setPreferredSize(new Dimension(2200,0));

        // On place le tableur dans un scroll
        this.scrollPane = new JScrollPane(this.tableau);
        this.scrollPane.setBounds(55, 10, 370, 530);      // Taille
        
        char tmp = 65; // A = 65 .. Z = 90

        // Premi�re ligne contenant toutes les lettres de l'alphabet
        for (int i = 0; i < this.colonnes.length; i++) {
            Character nom = new Character(tmp);
            this.colonnes[i] = new JLabel(nom.toString(), JLabel.CENTER);
            this.colonnes[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.tableau.add(this.colonnes[i]);
            tmp++;
        }     
        
        // Vitesses de d�filement du scroll avec la fl�che et la molette
        this.scrollPane.getHorizontalScrollBar().setBlockIncrement(50);
        this.scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
                 
        // On continue pour les autres lignes
        for (int i = 0; i < this.lignes.length - 1; i++) {
            
            // On cr�� chaque cellule de la ligne
            for (int j = 0; j < this.colonnes.length; j++) {
                this.grilleTableur.constructionCellule(i,j);
                this.tableau.add(this.grilleTableur.getTableau()[i][j]);   
            }
        }        
    }

    /**
     * Accesseur � grilleTableur
     * @return grilleTableur 
     */
    public GrilleCellules getGrilleTableur() {
        return grilleTableur;
    }
}