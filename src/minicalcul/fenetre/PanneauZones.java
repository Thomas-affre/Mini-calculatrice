/* 
 * PanneauZones.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panneau des zones m�moire
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class PanneauZones extends JPanel {
    
    /** Panneau d'affichage des zones de m�moire */
    private JPanel zonesMemoire;
    
    /** Paire label/textfield pour le contenu de la zone m�moire */
    private JPanel[] paireNomZone;
    
    /** Nom des zones m�moire */
    private JLabel[] nomsZones;
    
    /** Contenu des zones m�moire */
    private JTextField[] contenuZones;
        
    /**
     * Constructeur du panneau des zones m�moire
     */
    public PanneauZones() {
        this.setLayout(null);
                        
        // Cr�ation des zones m�moires
        this.creationZonesMemoire();
    }

    /**
     * Constuit le tableau des zones m�moire
     */
    private void creationZonesMemoire() {
        // Construction du tableau dans une grille
        this.zonesMemoire = new JPanel(new GridLayout(13, 2, 5, 0));
        this.zonesMemoire.setBounds(5, 5, 420, 540);    // Position et taille
        
        // Cr�ation des tableaux
        this.paireNomZone = new JPanel[26];
        this.nomsZones = new JLabel[26];
        this.contenuZones = new JTextField[26];
        
        // Initialisation et ajout
        for (int i = 0; i < this.nomsZones.length; i++) {
            
            // Panel
            this.paireNomZone[i] = new JPanel(new BorderLayout());
            
            // Nom de zone A=65..Z=90
            Character nom = new Character((char) (i + 65));
            this.nomsZones[i] = new JLabel(" " + nom.toString() + " : ");
            this.nomsZones[i].setFont(new Font("Courier", 0, 18));              
            this.paireNomZone[i].add(this.nomsZones[i], BorderLayout.WEST);
            
            // Zone
            this.contenuZones[i] = new JTextField();
            this.contenuZones[i].setFont(new Font("Courier", 0, 18));
            this.contenuZones[i].setEditable(false);
            this.contenuZones[i].setBackground(Color.WHITE);
            this.contenuZones[i].setHorizontalAlignment(JTextField.CENTER);
            this.contenuZones[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.paireNomZone[i].add(this.contenuZones[i], BorderLayout.CENTER); 
            
            // Ajout de la paire au panel
            this.zonesMemoire.add(this.paireNomZone[i]);
        }
        this.add(this.zonesMemoire);
    }
    
    /**
     * Contr�le si une zone m�moire est initialis�e ou non
     * @param indiceZone Indice de la zone m�moire � v�rifier
     * @return true si la memoire est vide, false sinon
     */
    public boolean memoireVide(int indiceZone) {
        return this.contenuZones[indiceZone].getText().equals("");
    }
    
    /**
     * Affecte valeur � la zone m�moire indic�e � indiceZone
     * @param indiceZone indice de la zone m�moire � modifier
     * @param valeur Valeur � affecter dans la zone
     */
    public void affectationMemoire(int indiceZone, String valeur) {
        this.contenuZones[indiceZone].setText(valeur);
        
        // On indique que la zone m�moire n'est plus vide
        this.nomsZones[indiceZone].setForeground(Color.RED);
    }

    /**
     * Retourne le contenu de la zone m�moire du nom pass� en argument
     * @param nomZone Nom de la zone m�moire o� l'on va chercher la valeur 
     * @return Contenu de cette zone m�moire
     */
    public String valeurMemoire(String nomZone) {
        return this.contenuZones[nomZone.charAt(0) - 65].getText();
    }

    /**
     * Accesseur � contenuZones
     * @return contenuZones 
     */
    public JTextField[] getContenuZones() {
        return contenuZones;
    }
}