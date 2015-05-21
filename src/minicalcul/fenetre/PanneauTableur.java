/* 
 * PanneauTableur.java                            9 avr. 2015
 * IUT info1 Groupe 3 2014-2015
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
 * Panneau du tableur
 * @author Clément Zeghmati
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PanneauTableur extends JPanel {
    
    /** Panneau permettant de rajouter un scroll au tableur */
    private JScrollPane scrollPane;
    
    /** Première colone du tableau qui reste figée */
    private JPanel premiereColone;
    
    /** Tableau d'affichage du tableur */
    private JPanel tableau;

    /** Labels d'affichage des chiffres dans la première colone */
    private JLabel[] lignes;

    /** Labels d'affichage des lettres dans la première ligne */
    private JLabel[] colones;
    
    /** Grille correspondant aux cellules du tableur */
    private GrilleCellules grilleTableur;

    /**
     * Construction du panneau
     */
    public PanneauTableur() {
        this.setLayout(null);
        
        // Création des headers du tableur (on ajoute 1 pour les headers)
        this.lignes = new JLabel[21]; 
        this.colones = new JLabel[26];
                
        this.creationPremiereColone();

        // On va créer le tableur ligne par ligne en commancant par le header
        this.creationTableur();
        this.add(this.scrollPane);
    }

    /**
     * Créé la première colone du tableur
     */
    private void creationPremiereColone() {

        this.premiereColone = new JPanel(new GridLayout(20,1));
        this.premiereColone.setBounds(20, 34, 35, 491);
        
        for (int i = 0; i < this.lignes.length - 1; i++) {
            this.lignes[i] = new JLabel(Integer.toString(i + 1), JLabel.CENTER);
            this.lignes[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.premiereColone.add(this.lignes[i]);
        }
        this.premiereColone.setOpaque(true);

        this.add(this.premiereColone);
    }

    /**
     * Créé et affiche le tableur
     */
    private void creationTableur() {
        
        // Création des cellules
        this.grilleTableur = new GrilleCellules();

        // Création du tableur
        this.tableau = new JPanel(new GridLayout(21,26));
        this.tableau.setPreferredSize(new Dimension(2200,0));

        this.scrollPane = new JScrollPane(this.tableau);
        this.scrollPane.setBounds(55, 10, 370, 530);      // Taille
        
        char tmp = 65; // A = 65 .. Z = 90

        for (int i = 0; i < this.colones.length; i++) {
            Character nom = new Character(tmp);
            this.colones[i] = new JLabel(nom.toString(), JLabel.CENTER);
            this.colones[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.tableau.add(this.colones[i]);
            tmp++;
        }     
        
        // Vitesses de défilement du scroll avec la flêche et la molette
        this.scrollPane.getHorizontalScrollBar().setBlockIncrement(50);
        this.scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
                 
        // On continue pour les autres lignes
        for (int i = 0; i < this.lignes.length - 1; i++) {
            
            // On créé chaque cellule de la ligne
            for (int j = 0; j < this.colones.length; j++) {
                this.grilleTableur.constructionCellule(i,j);
                this.tableau.add(this.grilleTableur.getTableau()[i][j]);   
            }
        }        
    }

    /**
     * Acceseur à grilleTableur
     * @return grilleTableur 
     */
    public GrilleCellules getGrilleTableur() {
        return grilleTableur;
    }
}