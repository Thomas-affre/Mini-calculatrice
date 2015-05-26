/* 
 * FenetrePrincipale.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import minicalcul.programme.commandes.Commandes;

/**
 * Fen�tre principale de l'application contenant tous les objets graphiques
 * principaux.
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class FenetrePrincipale extends JFrame {
    
    /** Onglets des zones m�moires et du tableur */
    private JTabbedPane lesOnglets;
    
    /** Barre de menu de l'application */
    private BarreDeMenu leMenu;
    
    /** Panneau de l'onglet zone m�moire */
    private PanneauZones laMemoire;
    
    /** Panneau de l'onglet du tableur */
    private PanneauTableur leTableur;
    
    /** Panneau du retour console */
    private PanneauCommande laConsole;
    
    /** Panneau permettant de rajouter un scroll � la console */
    private JScrollPane scrollPane;
    
    /** Zone de texte correpondant au retour console */
    private JTextArea retourConsole;

    /**
     * Constructeur de la fen�tre
     */
    public FenetrePrincipale() {
        
        // Propri�t�s de la fen�tre
        this.setTitle("Mini-Calculatrice");      // Titre
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setIconImage(new ImageIcon("icone.png").getImage()); // Logo
        this.setSize(1024, 768);                // Taille
        this.getContentPane().setLayout(null);  // Disposition par coordonn�es
        this.setResizable(false);               // Non redimensionnable
        this.setLocationRelativeTo(null);       // Centrer � l'ouverture
        this.setVisible(true);                  // Affichage de la fen�tre
        
        // Message de confirmation lorsque l'utilisateur ferme la fenetre
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                fermetureApplication();
            }
        });
        
        // Cr�ation du menu principal
        this.leMenu = new BarreDeMenu(this);
        this.setJMenuBar(this.leMenu);
               
        // Initialisation des onglets
        this.lesOnglets = new JTabbedPane(JTabbedPane.TOP); // onglets en hauts
        this.lesOnglets.setBounds(20, 10, 450, 580);       // position et taille
        
        // On cr�� la console
        this.creationConsole();
        
        // Construction des panneaux des onglets...
        this.laMemoire = new PanneauZones();
        
        // Disposition et taille du panneau de la m�moire
        this.laMemoire.setBounds(0, 0, this.getLesOnglets().getWidth(),
                this.getLesOnglets().getHeight());
        
        // Cr�ation du tableur
        this.leTableur = new PanneauTableur();
        
        // Disposition et taille du panneau du tableur
        this.leTableur.setBounds(0, 0, this.getLesOnglets().getWidth(),
                this.getLesOnglets().getHeight());
        
        // ... qu'on ajoute ensuite
        this.lesOnglets.addTab("Zones m�moires", this.laMemoire);
        this.lesOnglets.addTab("Tableur", this.leTableur);
        
        // On d�sactive le tableur au lancement
        this.lesOnglets.setEnabledAt(1, false);
        this.lesOnglets.setSelectedIndex(0); 
        
        // Panneau console
        this.laConsole = new PanneauCommande(this);
        this.add(this.laConsole);
        
        // Ajout des onglets au panneau de la fen�tre
        this.getContentPane().add(this.lesOnglets);
               
        // On positionne le curseur sur la ligne de commande
        this.laConsole.getLigneDeCommande().requestFocus();
        
        // On valide tous les ajouts
        this.getContentPane().repaint();    
        this.validate();
    }
    
    /**
     * Construit la console qui affichera un historique des op�rations effectu�s
     * avec l'incidence qu'elles auront eu sur l'application (ex : r�sultat
     * op�ration, affectation, erreur...)
     */
    private void creationConsole() {
        
        // Construction
        this.retourConsole = new JTextArea(new String(), 10, 10);

        // Propri�t�s du scroll
        this.scrollPane = new JScrollPane(this.retourConsole);        
        this.scrollPane.setBounds(485, 30, 510, 560);    // Position et taille
        
        // Propri�t�s de la console
        this.retourConsole.setEditable(false);           // D�sactive l'�criture
        this.retourConsole.setBackground(Color.BLACK);   // Arri�re plan
        this.retourConsole.setForeground(Color.GREEN);   // Couleur police
        this.retourConsole.setFont(new Font("Courier", 0, 12)); // Police

        this.retourConsole.setLineWrap(true); // Empeche le scroll horizontal...
        this.retourConsole.setWrapStyleWord(true); // ...sans d�couper les mots
        
        // Premier texte affich� � l'ouverture de la calculatrice
        this.ajoutLigneConsole("Mini-calculatrice [version 1.1]"
                + "\nIUT INFO1 2014-2015\n");
        
        // Ajout au panneau de la fen�tre
        this.getContentPane().add(this.scrollPane);
    }
    
    /**
     * Ajoute la chaine de caract�res pass�e en argument � la suite de la 
     * console et envoie le scroll au bas de la console
     * @param aAjouter Chaine � ajouter
     */
    public void ajoutLigneConsole(String aAjouter) {
        // Ajoute le texte � la suite de la console
        this.retourConsole.append(aAjouter + "\n");

        // Envoie le scroll, s'il est pr�sent, au plus bas
        this.retourConsole.setCaretPosition(
                this.retourConsole.getDocument().getLength());        
    }
    
    /**
     * Appel�e lorsque l'utilisateur souhaite fermer l'application. Un message
     * de confirmation lui sera affich�.
     */
    public void fermetureApplication() {
        if (JOptionPane.showConfirmDialog(this, "<html><center>"
                + "Souhaitez-vous vraiment quitter ?<br/>Les "
                + "donn�es non sauvegard�es seront d�finitivement perdus."
                + "</center</html>",
                "Quitter", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Acceseur � lesOnglets
     * @return lesOnglets 
     */
    public JTabbedPane getLesOnglets() {
        return lesOnglets;
    }

    /**
     * Accesseur � laConsole
     * @return laConsole 
     */
    public PanneauCommande getLaConsole() {
        return laConsole;
    }

    /**
     * Mutateur de laMemoire
     * @param laMemoire nouveau laMemoire
     */
    public void setLaMemoire(PanneauZones laMemoire) {
        this.laMemoire = laMemoire;
    }

    /**
     * Acceseur � laMemoire
     * @return laMemoire 
     */
    public PanneauZones getLaMemoire() {
        return laMemoire;
    }

    /**
     * Acceseur � leTableur
     * @return leTableur 
     */
    public PanneauTableur getLeTableur() {
        return leTableur;
    }

    /**
     * Acceseur � leMenu
     * @return leMenu 
     */
    public BarreDeMenu getLeMenu() {
        return leMenu;
    }  
    
    /**
     * Lancement de l'application
     * @param args unused
     */
    public static void main(String[] args) {
        new Commandes(new FenetrePrincipale());
    }
}