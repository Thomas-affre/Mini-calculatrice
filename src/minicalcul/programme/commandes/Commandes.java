/* 
 * Commandes.java                            12 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet r�unissant toutes les consoles de l'application
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public class Commandes {
    
    /** R�f�rence � la fen�tre principale */
    private FenetrePrincipale laFenetre;
    
    /** Objet effectuant les calculs */
    private ConsoleCalculSimple calculs;
    
    /** Objet de gestion de la m�moire */
    private ConsoleGestionMemoire memoires;
    
    /** Objet du tableur */
    private ConsoleCalculTableur tableur;
    
    /** Objet ex�cutant les commandes du tableur */
    private ConsoleGestionTableur gestionTableur;
    
    /** Tableau des cellules (partie programme) */
    private Tableur tableauCellules;

    /**
     * Constructeur des commandes
     * @param fenetre Fen�tre � laquelle la console envoie des messages
     */
    public Commandes(FenetrePrincipale fenetre) {
        this.laFenetre = fenetre;
        
        // Construction de la partie programme du tableur
        this.tableauCellules = new Tableur(
                this.laFenetre.getLeTableur().getGrilleTableur());
        
        // On envoie au menu la r�f�rence du tableur pour les sauvegardes
        this.laFenetre.getLeMenu().setLeTableur(this.tableauCellules);
        
        /*
         * Les consoles de calculs et de gestion de la m�moire connaissent le
         * panneau contenant le retour console et le gestionnaire de m�moire
         * afin de pouvoir leurs envoyer des messages de mise � jour
         */
        this.calculs = new ConsoleCalculSimple(this.laFenetre);
        this.memoires = new ConsoleGestionMemoire(this.laFenetre);
        this.tableur = new ConsoleCalculTableur(
                this.laFenetre, this.tableauCellules);
        this.gestionTableur = new ConsoleGestionTableur(
                this.laFenetre, this, this.tableauCellules);
        
        // On envoie la r�f�rence de la console de calcul tableur au tableur
        this.tableauCellules.setCalculTableur(this.tableur);
    
        // On permet � la console d'envoyer des messages aux trois consoles
        this.laFenetre.getLaConsole().setCommandes(this);
    }
    
    /**
     * Accesseur � calculs
     * @return calculs 
     */
    public ConsoleCalculSimple getCalculs() {
        return calculs;
    }

    /**
     * Accesseur � memoires
     * @return memoires 
     */
    public ConsoleGestionMemoire getMemoires() {
        return memoires;
    }

    /**
     * Accesseur � tableur
     * @return tableur 
     */
    public ConsoleCalculTableur getTableur() {
        return tableur;
    }

    /**
     * Accesseur � gestionTableur
     * @return gestionTableur 
     */
    public ConsoleGestionTableur getGestionTableur() {
        return gestionTableur;
    }

    /**
     * Accesseur � tableauCellules
     * @return tableauCellules 
     */
    public Tableur getTableauCellules() {
        return tableauCellules;
    }

    /**
     * Mutateur de tableauCellules
     * @param tableauCellules nouveau tableauCellules
     */
    public void setTableauCellules(Tableur tableauCellules) {
        this.tableauCellules = tableauCellules;
    }
}