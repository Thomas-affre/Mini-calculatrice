/* 
 * BarreDeMenu.java                            4 mai 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import minicalcul.programme.tableur.Tableur;

/**
 * Barre de menu que l'on affichera sur la fenêtre qui permettra d'ouvrir un
 * nouveau tableur ou un tableur sauvegardé. Il permettra également de 
 * sauvegarder l'état actuel du tableur dans un fichier binaire et quitter 
 * l'application.
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class BarreDeMenu extends JMenuBar implements ActionListener {
    
    /** Référence à la fenêtre à laquelle la barre est attachée */
    private FenetrePrincipale laFenetre;
    
    /** Référence au tableur */
    private Tableur leTableur;
    
    /** Bouton "Fichier" dans la barre de menu */
    private JMenu fichier = new JMenu("Fichier");
    
    /** Réinitialise l'état du tableur */
    private JMenuItem nouveau = new JMenuItem("Nouveau tableur");
    
    /** Ouvrir un tableur existant */
    private JMenuItem ouvrir = new JMenuItem("Ouvrir...");
    
    /** Enregistre l'état de la machine */
    private JMenuItem enregistrer = new JMenuItem("Enregistrer");

    /** Enregistre l'état du tableur dans un nouveau fichier binaire */
    private JMenuItem enregistrerSous = new JMenuItem("Enregistrer sous...");
    
    /** Bouton du menu permettant de se déconnecter */
    private JMenuItem quitter = new JMenuItem("Quitter");
    
    /** Bouton "?" dans la barre de menu */
    private JMenu aide = new JMenu("Aide");
    
    /** Affiche une aide pour effectuer un calcul simple */
    private JMenuItem aideCalcul = new JMenuItem("Effectuer un calcul");
    
    /** Affiche une aide pour effectuer un sur le tableur */
    private JMenuItem aideTableur = new JMenuItem("Utiliser le tableur");
    
    /** Affiche une aide sur les différents modes du programme */
    private JMenuItem aideModes = new JMenuItem("Modes du logiciel");
    
    /** Fichier actuellement ouvert (= null si aucun fichier ouvert) */
    private String fichierOuvert;
    
    /** Aide du mode calculatrice simple */
    private static final String AIDE_CALCUL = 
            "--------------- FONCTIONALITES CALCULATRICE --------------- \n"
            + " - Utiliser des réels : \n"
            + "\t57    6.4    -4    -0.500    567.0\n\n"
            + " - Utiliser des opérateurs :\n"
            + "\t+    -    *    /\n\n"
            + " - Effectuer des calculs simples :\n"
            + "\t3 + 5.2\n"
            + "\t7 * -2\n"
            + "\t25 / 4\n"
            + "\t-5.98 * 9 - 46\n\n"
            + " - Effectuer des calculs avec des parenthèses :\n"
            + "\t3 + ( 70 * 10 )\n"
            + "\t( 25 / 2 ) - 2\n"
            + "\t( 50 + ( 2 * 14 ) )\n\n"
            + " - Affecter des valeurs aux zones mémoires :\n"
            + "\t-3 + 4 = A\n"
            + "\t2 * A\n"
            + "\tA + A = B";
            

    /**
     * Construction de la barre de menu
     * @param laFenetre Référence à la fenêtre à laquelle la barre est attachée
     */
    public BarreDeMenu(FenetrePrincipale laFenetre) {
        this.laFenetre = laFenetre;
        
        // Menu fichier
        this.add(this.fichier);
        this.fichier.add(this.nouveau);
        this.fichier.add(this.ouvrir);
        this.fichier.addSeparator();
        this.fichier.add(this.enregistrer);
        this.fichier.add(this.enregistrerSous);
        this.fichier.addSeparator();
        this.fichier.add(this.quitter);
        
        // Menu Aide
        this.add(this.aide);
        this.aide.add(this.aideCalcul);
        this.aide.add(this.aideTableur);
        this.aide.addSeparator();
        this.aide.add(this.aideModes);
        
        // Ecouteurs clics souris
        this.nouveau.addActionListener(this);
        this.ouvrir.addActionListener(this);
        this.enregistrer.addActionListener(this);
        this.enregistrerSous.addActionListener(this);
        this.quitter.addActionListener(this);
        this.aideCalcul.addActionListener(this);
        this.aideTableur.addActionListener(this);
        this.aideModes.addActionListener(this);
        
        /*
         * Au démarrage, on se situe sur la calculatrice, donc on désactive les 
         * fonctions d'enregistrement ou d'ouverture de tableur
         */
        this.desactivationFonctionsTableur();
    }

    /**
     * Restaure un fichier de sauvegarde de tableur sélectionnée dans une boite
     * de dialogue
     */
    public void ouvirFichier() {
        
        // Objet tampon dans lequel est placé l'objet lu dans le fichier  
        String[][] tampon = null;
        
        // Boite de dialogue d'ouverture de fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Binary Files (*.bin)", "bin"));
        fileChooser.setDialogTitle("Ouvrir"); 
        
        int selection = fileChooser.showOpenDialog(this.laFenetre);
        
        // Si l'utilisateur a validé un fichier
        if (selection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
                        
            // Ouverture du fichier et lecture de l'objet qu'il contient
            try (ObjectInputStream fichier = new ObjectInputStream(
                    new FileInputStream(fileToSave.getAbsolutePath()))) {           

                // lecture de l'objet contenu dans le fichier
                tampon = (String[][]) fichier.readObject();
                
                // On l'applique au tableur
                this.leTableur.restaurationTableurFichier(tampon);
                                
                // On garde le chemin de sauvegarde
                this.fichierOuvert = fileToSave.getAbsolutePath();
                
                // On le spécifie sur la console
                this.laFenetre.ajoutLigneConsole(this.fichierOuvert 
                        + " : restauré.");
                
                // On met à jour le titre de la fenêtre pour le savoir
                this.laFenetre.setTitle(this.fichierOuvert
                         + " - Mini-Calcultrice");
                
            } catch (Exception erreur) { // Restauration impossible
                this.laFenetre.ajoutLigneConsole("Erreur de restauration : "
                        + "le fichier spécifié ne contient pas des données "
                        + "issues d'un tableur.");
            }
        }
    }

    /**
     * Sauvegarde le contenu du tableur dans le fichier spécifié dans
     * this.fichierOuvert (chemin absolu)
     */
    private void sauvegarderFichier() {
        
        // Sauvegarde l'état du tableur dans un tableau à 2 dimensions
        String[][] aSauvegarder = this.leTableur.sauvegardeEtatTableur();

        // Création et ouverture du fichier où l'on effectuera la sauvegarde
        try (ObjectOutputStream fichier = new ObjectOutputStream(
             new FileOutputStream(this.fichierOuvert))) {
            
            // On écrit le tableau dans le fichier
            fichier.writeObject(aSauvegarder);  
                        
            // On met à jour le titre de la fenêtre pour le savoir
            this.laFenetre.setTitle(this.fichierOuvert
                     + " - Mini-Calcultrice");
            
            // On le spécifie sur la console
            this.laFenetre.ajoutLigneConsole("Tableur sauvegardé sur : \"" 
                    + this.fichierOuvert + "\"");
            
        } catch (IOException e) { // Sauvegarde impossible
            this.laFenetre.ajoutLigneConsole("Erreur de sauvegarde : "
                    + "la sauvegarde n'a pu avoir lieu");
        }        
    }

    /**
     * Demande via une boite dialoque le chemin vers lequel l'utilisateur
     * souhaite sauvegarder dans un fichier le contenu actuel du tableur
     */
    private void sauvegarderFichierSous() {
        
        // Boite de dialogue de sauvegarde
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Binary Files (*.bin)", "bin"));
        fileChooser.setDialogTitle("Enregistrer sous");  
        
        int selection = fileChooser.showSaveDialog(this.laFenetre);
        
        // Si l'utilisateur valide le chemin de sauvegarde
        if (selection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // On garde le chemin de sauvegarde
            this.fichierOuvert = fileToSave.getAbsolutePath();
                    
            // On vérifie que l'extension soit présente
            this.fichierOuvert = this.fichierOuvert.substring(
                    this.fichierOuvert.length() - 4, 
                    this.fichierOuvert.length()).equals(".bin") ?
                            this.fichierOuvert : this.fichierOuvert + ".bin";
            
            // On procède à la sauvegarde
            this.sauvegarderFichier();            
        }
    }
    
    /**
     * Appelé lorsque le raccourci de sauvegarde est utilisé Ctrl + S.
     * Si l'utilisateur a déjà spécifié un chemin de sauvegarde, la vers
     * sauvegarde s'effectue sur ce fichier, sinon il est réorienté
     * "Enregistrer sous..." sur une boite de dialogue
     */
    public void raccourciSauvegarde() {
        if (this.fichierOuvert == null) {
            this.sauvegarderFichierSous();
        } else {            
            this.sauvegarderFichier();
        }
    }

    /**
     * Est appelée lorsque l'utilisateur passe en mode tableur. Active les
     * boutons du menu permettant d'enregistrer ou d'ouvrir un tableur.
     */
    public void activationFonctionsTableur() {
        this.nouveau.setEnabled(true);
        this.ouvrir.setEnabled(true);
        this.enregistrer.setEnabled(true);
        this.enregistrerSous.setEnabled(true);
    }
    
    /**
     * Est appelée lorsque l'utilisateur quitte le mode tableur. Désactive les
     * boutons du menu permettant d'enregistrer ou d'ouvrir un tableur.
     */
    public void desactivationFonctionsTableur() {
        this.nouveau.setEnabled(false);
        this.ouvrir.setEnabled(false);
        this.enregistrer.setEnabled(false);
        this.enregistrerSous.setEnabled(false);
    }

    /**
     * Mutateur de leTableur
     * @param leTableur nouveau leTableur
     */
    public void setLeTableur(Tableur leTableur) {
        this.leTableur = leTableur;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener
     *                  #actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == this.enregistrerSous || (source == this.enregistrer 
                && this.fichierOuvert == null)) {
            /*
             * Si l'utilisateur souhaite choisir le chemin de sauvegarde ou
             * s'il n'y a pas eu d'enregistrement au préalable, on réoriente
             * vers "Enregistrer sous..."
             */
            this.sauvegarderFichierSous();
            
        } else if (source == this.enregistrer) {
            // && this.fichierEnregistrement != null
            
            this.sauvegarderFichier();
            
        } else if (source == this.ouvrir) {
            // Ouverture fichier
            this.ouvirFichier();
            
        } else if (source == this.nouveau) {
            /*
             * On demande confirmation si l'utilisateur souhaite ouvrir un
             * nouveau tableur
             */
            if (JOptionPane.showConfirmDialog(this.laFenetre, "<html><center>"
                    + "Souhaitez-vous réinitialiser le tableur ?<br/>Les "
                    + "données non sauvegardées seront définitivement perdus."
                    + "</center></html>",
                    "Nouveau tableur", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                
                // On enlève le titre du fichier
                this.laFenetre.setTitle("Mini-Calculatrice");
                this.fichierOuvert = null; // Aucun fichier n'est ouvert
                
                // On réinitialise l'état du tableur
                this.leTableur.reinitialisationTableur();
                this.laFenetre.getLeTableur().getGrilleTableur()
                                    .reinitialisationInterfaceTableur();
                
                // On le signale à l'utilisateur
                this.laFenetre.ajoutLigneConsole(">> Nouveau Tableur ouvert.");
            }
            
        } else if (source == this.quitter) {
            this.laFenetre.fermetureApplication();
        } else if (source == this.aideCalcul) {
            this.laFenetre.ajoutLigneConsole(AIDE_CALCUL);
        }
    }
}