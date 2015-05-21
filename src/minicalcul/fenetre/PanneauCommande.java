/* 
 * PanneauCommande.java                            10 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import minicalcul.programme.commandes.Commandes;
import minicalcul.programme.commandes.ConsoleGestionTableur;

/**
 * Panneau de l'interpréteur de commandes
 * @author Clément Zeghmati
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PanneauCommande extends JPanel 
implements ActionListener, KeyListener {

    /** Différents modes sur lesquels on peut se situer */
    private final String[] MODES = 
        {"Calculatrice", "Gestionnaire de la mémoire", "Tableur"};
    
    /** Référence à la fenêtre pour intéragir avec la mémoire et le tableur */
    private FenetrePrincipale laFenetre;

    /** Référence à la console permettant d'envoyer les commandes passées */
    private Commandes commandes;
    
    /** Label du mode */
    private JLabel mode;
    
    /** Label de la flêche indiquant l'invite de commandes */ 
    private JLabel fleche;
    
    /** Label du mode sur lequel on se situe */
    private JLabel modeActuel;

    /** Champs de saisie des commandes d'intéraction avec l'application */
    private JTextField ligneDeCommande;

    /** Bouton de validation d'une commande */
    private JButton validerCommande;
    
    /** Sauvegarde les dernières commandes saisies par l'utilisateur */
    private ArrayList<String> sauvegardeCommandes;
    
    /** Position dans la liste de sauvegarde */
    private int positionSauvegarde;

    /**
     * Constructeur du panneau d'interpréteur de commandes
     * @param laFenetre Fenetre à laquelle le panneau est appliqué
     */
    public PanneauCommande(FenetrePrincipale laFenetre) {
        this.laFenetre = laFenetre;
        this.sauvegardeCommandes = new ArrayList<String>();
        this.setLayout(null);
        this.setBorder(BorderFactory      // Bordure
                .createTitledBorder(" Interpréteur de commandes "));
        this.setBounds(18, 605, 977, 100);
        
        // Label du mode
        this.mode = new JLabel("Mode : ");
        this.mode.setBounds(20, 60, 55, 30);
        this.mode.setFont(new Font("Arial", 0, 16));
        this.add(this.mode);
        
        // Fleche
        this.fleche = new JLabel(">");
        this.fleche.setBounds(35, 25, 20, 30);
        this.fleche.setFont(new Font("Arial", Font.BOLD, 30));
        this.add(this.fleche);
        
        // Type du mode
        this.modeActuel = new JLabel(MODES[0]); // Calculatrice au lancement
        this.modeActuel.setBounds(70, 60, 220, 30);
        this.modeActuel.setFont(new Font("Arial", Font.BOLD, 16));
        this.add(this.modeActuel);

        // Ligne de commande
        this.ligneDeCommande = new JTextField();
        this.ligneDeCommande.setBounds(65, 25, 780, 30);
        this.ligneDeCommande.setFont(new Font("Courier", 0, 16));
        this.ligneDeCommande.setForeground(Color.BLACK);
        this.ligneDeCommande.addKeyListener(this);
        this.add(this.ligneDeCommande);

        // Bouton valider
        this.validerCommande = new JButton("Valider");
        this.validerCommande.setBounds(855, 25, 80, 30);
        this.validerCommande.setFont(new Font("Arial", 0, 16));
        this.validerCommande.setMargin(new Insets(0,0,0,0));
        this.validerCommande.addActionListener(this);
        this.add(this.validerCommande);

    }

    /**
     * Applelée lorsque l'utilisateur clique sur le bouton valider ou appuie
     * sur la touche ENTRER
     */
    public void commandeValidee() {
        
        // On contrôle d'abord si le champs n'est pas vide
        if (this.ligneDeCommande.getText().equals("")) {
            return; // Inutile de continuer
        }

        // On ajoute la commande à la sauvegarde et on met le cureseur à la fin
        this.sauvegardeCommandes.add(this.ligneDeCommande.getText());
        this.positionSauvegarde = this.sauvegardeCommandes.size();
        
        // On traite en fonction du mode sur lequel on se trouve
        if (this.modeActuel.getText() == MODES[0]) {
            // Mode calculatrice
            this.traitementCalculatrice();
        } else if (this.modeActuel.getText() == MODES[1]) {
            // Mode gestion de la memoire
            this.traitementGestionMemoire();
        } else if (this.modeActuel.getText() == MODES[2]) {
            // Mode tableur
            this.traitementTableur();
        }
        
        this.ligneDeCommande.setText(""); // On vide la ligne de commande
    }

    /**
     * Appelée lorsqu'une commande est validée et qu'on est dans le mode
     * calculatrice
     */
    private void traitementCalculatrice() {
        
        // On affiche sur la console la saisie
        this.laFenetre.ajoutLigneConsole("? " + this.ligneDeCommande.getText());
        
        // On traite la demande
        if (this.ligneDeCommande.getText().equals("MEM")) {
            this.passageModeGestionMemoire();
        } else if (this.ligneDeCommande.getText().equals("TAB")) {
            this.passageModeTableur();
        } else {
            // Envoie la requête de calcul
            this.laFenetre.ajoutLigneConsole(this.commandes.
                    getCalculs().
                    traitementCommande(this.ligneDeCommande.getText()));
        }
    }

    /**
     * Appelée lorsqu'une commande est validée et qu'on est dans le mode
     * gestionnaire de la mémoire
     */
    private void traitementGestionMemoire() {
        
        // On affiche sur la console la saisie
        this.laFenetre.ajoutLigneConsole("$ " + this.ligneDeCommande.getText());  
        
        // On traite la demande
        if (this.ligneDeCommande.getText().equals("QUIT")) {
            this.passageModeCalculatrice();
        } else {
            // Envoie la requête au gestionnaire de la mémoire
            this.laFenetre.ajoutLigneConsole(this.commandes.getMemoires().
                    traitementCommande(this.ligneDeCommande.getText()));
        }
    }

    /**
     * Appelée lorsqu'une commande est validée et qu'on est dans le mode
     * tableur
     */
    private void traitementTableur() {
        
        // On affiche sur la console la saisie
        this.laFenetre.ajoutLigneConsole("$ " + this.ligneDeCommande.getText()); 
        
        // On traite la demande
        if (this.ligneDeCommande.getText().equals("QUIT")) {
            this.passageModeCalculatrice();
        } else if (estUneCommandeTableur(
                this.ligneDeCommande.getText().split(" ")[0])) {
            // Si la première sous-chaine est une commande de gestion du tableur
            this.laFenetre.ajoutLigneConsole(this.commandes.getGestionTableur()
                    .traitementCommande(this.ligneDeCommande.getText()));
  
            // On met à jour toutes les cellules s'il y a eu du changement
            this.commandes.getTableauCellules().miseAJourTableur();
        } else {
            // Envoie la requête au tableur
            this.laFenetre.ajoutLigneConsole(this.commandes.getTableur().
                    traitementCommande(this.ligneDeCommande.getText()));
            
            // On met à jour toutes les cellules s'il y a eu du changement
            this.commandes.getTableauCellules().miseAJourTableur();            
        }
    }
    
    /**
     * Appelée lorsque l'utilisateur passe au mode Calculatrice
     */
    private void passageModeCalculatrice() {
        this.modeActuel.setText(MODES[0]); // Mode gestionnaire de mémoire
        this.laFenetre.ajoutLigneConsole("? MODE CALCULATRICE");
        
        this.laFenetre.getLesOnglets().setEnabledAt(0, true); // 0 : Zones
        this.laFenetre.getLesOnglets().setEnabledAt(1, false);  // 1 : Tableur
        
        // On change d'onglet
        this.laFenetre.getLesOnglets().setSelectedIndex(0);
    }

    /**
     * Appelée lorsque l'utilisateur passe au mode Gestionnaire de la mémoire
     */
    private void passageModeGestionMemoire() {
        this.modeActuel.setText(MODES[1]); // Mode gestionnaire de mémoire
        this.laFenetre.ajoutLigneConsole("$ MODE GESTIONNAIRE DE LA MEMOIRE");        
    }
    
    /**
     * Appelée lorsque l'utilisateur passe au mode Tableur
     */
    private void passageModeTableur() {
        this.modeActuel.setText(MODES[2]); // Mode gestionnaire de mémoire
        
        this.laFenetre.getLesOnglets().setEnabledAt(0, false); // 0 : Zones
        this.laFenetre.getLesOnglets().setEnabledAt(1, true);  // 1 : Tableur
        
        // On change d'onglet
        this.laFenetre.getLesOnglets().setSelectedIndex(1); 
        
        this.laFenetre.ajoutLigneConsole("$ MODE TABLEUR");        
    }

    /**
     * Remonte dans la liste des sauvegardes. S'arète au début.
     */
    private void affichageSauvegardeAvant() {
        // On le fait seulement si on n'est pas au début

        if (this.positionSauvegarde > 0) {
            this.positionSauvegarde--;
            this.ligneDeCommande.setText(
                    this.sauvegardeCommandes.get(this.positionSauvegarde));
        }
    }

    /**
     * Redescend dans la liste des sauvegardes. S'arète à la fin.
     */
    private void affichageSauvegardeApres() {

        // On le fait seulement si on n'est pas à la fin de la liste
        if (this.positionSauvegarde < this.sauvegardeCommandes.size() - 1) {
            this.positionSauvegarde++;

            this.ligneDeCommande.setText(
                    this.sauvegardeCommandes.get(this.positionSauvegarde)); 
        }
    }
    
    /**
     * Vérifie si la première sous-chaine saisie est une commande du tableur
     * @param aTester Chaine correspondant à la première sous chaine de la
     *          commande passée
     * @return true s'il s'agit d'une commande, false sinon
     */
    public static boolean estUneCommandeTableur(String aTester) {
        for (int i = 0; i < ConsoleGestionTableur.COMMANDES.length; i++) {
            if (ConsoleGestionTableur.COMMANDES[i].equals(aTester)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mutateur de commandes
     * @param commandes nouveau commandes
     */
    public void setCommandes(Commandes commandes) {
        this.commandes = commandes;
    }
    
    /**
     * Acceseur à ligneDeCommande
     * @return ligneDeCommande 
     */
    public JTextField getLigneDeCommande() {
        return ligneDeCommande;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed
     *          (java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
    
        if (source == this.validerCommande) {
            this.commandeValidee();
        }
    
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.commandeValidee();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.affichageSauvegardeAvant();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.affichageSauvegardeApres();
        }
        
        if ((e.getKeyCode() == KeyEvent.VK_S) 
                && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            this.laFenetre.getLeMenu().raccourciSauvegarde();
        } else if ((e.getKeyCode() == KeyEvent.VK_O) 
                && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            this.laFenetre.getLeMenu().ouvirFichier();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // Non utilisé
    }

    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Non utilisé
    }

}