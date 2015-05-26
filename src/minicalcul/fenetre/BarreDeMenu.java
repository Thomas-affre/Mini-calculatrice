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
 * Barre de menu que l'on affichera sur la fen�tre qui permettra d'ouvrir un
 * nouveau tableur ou un tableur sauvegard�. Il permettra �galement de 
 * sauvegarder l'�tat actuel du tableur dans un fichier binaire et quitter 
 * l'application.
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class BarreDeMenu extends JMenuBar implements ActionListener {
    
    /** R�f�rence � la fen�tre � laquelle la barre est attach�e */
    private FenetrePrincipale laFenetre;
    
    /** R�f�rence au tableur */
    private Tableur leTableur;
    
    /** Bouton "Fichier" dans la barre de menu */
    private JMenu fichier = new JMenu("Fichier");
    
    /** R�initialise l'�tat du tableur */
    private JMenuItem nouveau = new JMenuItem("Nouveau tableur");
    
    /** Ouvrir un tableur existant */
    private JMenuItem ouvrir = new JMenuItem("Ouvrir...");
    
    /** Enregistre l'�tat de la machine */
    private JMenuItem enregistrer = new JMenuItem("Enregistrer");

    /** Enregistre l'�tat du tableur dans un nouveau fichier binaire */
    private JMenuItem enregistrerSous = new JMenuItem("Enregistrer sous...");
    
    /** Bouton du menu permettant de se d�connecter */
    private JMenuItem quitter = new JMenuItem("Quitter");
    
    /** Bouton "?" dans la barre de menu */
    private JMenu aide = new JMenu("Aide");
    
    /** Affiche une aide pour effectuer un calcul simple */
    private JMenuItem aideCalcul = new JMenuItem("Effectuer un calcul");
    
    /** Affiche une aide pour effectuer un sur le tableur */
    private JMenuItem aideTableur = new JMenuItem("Utiliser le tableur");
    
    /** Affiche une aide sur les diff�rents modes du programme */
    private JMenuItem aideModes = new JMenuItem("Modes du logiciel");
    
    /** Fichier actuellement ouvert (= null si aucun fichier ouvert) */
    private String fichierOuvert;
    
    /** Aide du mode calculatrice simple */
    private static final String AIDE_CALCUL = 
            "--------------- FONCTIONALITES CALCULATRICE --------------- \n"
            + " - Utiliser des r�els : \n"
            + "\t57    6.4    -4    -0.500    567.0\n\n"
            + " - Utiliser des op�rateurs :\n"
            + "\t+    -    *    /\n\n"
            + " - Effectuer des calculs simples :\n"
            + "\t3 + 5.2\n"
            + "\t7 * -2\n"
            + "\t25 / 4\n"
            + "\t-5.98 * 9 - 46\n\n"
            + " - Effectuer des calculs avec des parenth�ses :\n"
            + "\t3 + ( 70 * 10 )\n"
            + "\t( 25 / 2 ) - 2\n"
            + "\t( 50 + ( 2 * 14 ) )\n\n"
            + " - Affecter des valeurs aux zones m�moires :\n"
            + "\t-3 + 4 = A\n"
            + "\t2 * A\n"
            + "\tA + A = B";
            

    /**
     * Construction de la barre de menu
     * @param laFenetre R�f�rence � la fen�tre � laquelle la barre est attach�e
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
         * Au d�marrage, on se situe sur la calculatrice, donc on d�sactive les 
         * fonctions d'enregistrement ou d'ouverture de tableur
         */
        this.desactivationFonctionsTableur();
    }

    /**
     * Restaure un fichier de sauvegarde de tableur s�lectionn�e dans une boite
     * de dialogue
     */
    public void ouvirFichier() {
        
        // Objet tampon dans lequel est plac� l'objet lu dans le fichier  
        String[][] tampon = null;
        
        // Boite de dialogue d'ouverture de fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Binary Files (*.bin)", "bin"));
        fileChooser.setDialogTitle("Ouvrir"); 
        
        int selection = fileChooser.showOpenDialog(this.laFenetre);
        
        // Si l'utilisateur a valid� un fichier
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
                
                // On le sp�cifie sur la console
                this.laFenetre.ajoutLigneConsole(this.fichierOuvert 
                        + " : restaur�.");
                
                // On met � jour le titre de la fen�tre pour le savoir
                this.laFenetre.setTitle(this.fichierOuvert
                         + " - Mini-Calcultrice");
                
            } catch (Exception erreur) { // Restauration impossible
                this.laFenetre.ajoutLigneConsole("Erreur de restauration : "
                        + "le fichier sp�cifi� ne contient pas des donn�es "
                        + "issues d'un tableur.");
            }
        }
    }

    /**
     * Sauvegarde le contenu du tableur dans le fichier sp�cifi� dans
     * this.fichierOuvert (chemin absolu)
     */
    private void sauvegarderFichier() {
        
        // Sauvegarde l'�tat du tableur dans un tableau � 2 dimensions
        String[][] aSauvegarder = this.leTableur.sauvegardeEtatTableur();

        // Cr�ation et ouverture du fichier o� l'on effectuera la sauvegarde
        try (ObjectOutputStream fichier = new ObjectOutputStream(
             new FileOutputStream(this.fichierOuvert))) {
            
            // On �crit le tableau dans le fichier
            fichier.writeObject(aSauvegarder);  
                        
            // On met � jour le titre de la fen�tre pour le savoir
            this.laFenetre.setTitle(this.fichierOuvert
                     + " - Mini-Calcultrice");
            
            // On le sp�cifie sur la console
            this.laFenetre.ajoutLigneConsole("Tableur sauvegard� sur : \"" 
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
                    
            // On v�rifie que l'extension soit pr�sente
            this.fichierOuvert = this.fichierOuvert.substring(
                    this.fichierOuvert.length() - 4, 
                    this.fichierOuvert.length()).equals(".bin") ?
                            this.fichierOuvert : this.fichierOuvert + ".bin";
            
            // On proc�de � la sauvegarde
            this.sauvegarderFichier();            
        }
    }
    
    /**
     * Appel� lorsque le raccourci de sauvegarde est utilis� Ctrl + S.
     * Si l'utilisateur a d�j� sp�cifi� un chemin de sauvegarde, la vers
     * sauvegarde s'effectue sur ce fichier, sinon il est r�orient�
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
     * Est appel�e lorsque l'utilisateur passe en mode tableur. Active les
     * boutons du menu permettant d'enregistrer ou d'ouvrir un tableur.
     */
    public void activationFonctionsTableur() {
        this.nouveau.setEnabled(true);
        this.ouvrir.setEnabled(true);
        this.enregistrer.setEnabled(true);
        this.enregistrerSous.setEnabled(true);
    }
    
    /**
     * Est appel�e lorsque l'utilisateur quitte le mode tableur. D�sactive les
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
             * s'il n'y a pas eu d'enregistrement au pr�alable, on r�oriente
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
                    + "Souhaitez-vous r�initialiser le tableur ?<br/>Les "
                    + "donn�es non sauvegard�es seront d�finitivement perdus."
                    + "</center></html>",
                    "Nouveau tableur", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                
                // On enl�ve le titre du fichier
                this.laFenetre.setTitle("Mini-Calculatrice");
                this.fichierOuvert = null; // Aucun fichier n'est ouvert
                
                // On r�initialise l'�tat du tableur
                this.leTableur.reinitialisationTableur();
                this.laFenetre.getLeTableur().getGrilleTableur()
                                    .reinitialisationInterfaceTableur();
                
                // On le signale � l'utilisateur
                this.laFenetre.ajoutLigneConsole(">> Nouveau Tableur ouvert.");
            }
            
        } else if (source == this.quitter) {
            this.laFenetre.fermetureApplication();
        } else if (source == this.aideCalcul) {
            this.laFenetre.ajoutLigneConsole(AIDE_CALCUL);
        }
    }
}