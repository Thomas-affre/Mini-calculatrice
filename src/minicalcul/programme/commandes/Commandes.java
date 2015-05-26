/* 
 * Commandes.java                            12 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.commandes;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet réunissant toutes les consoles de l'application
 * @author Clément Zeghmati
 * @version 0.1
 */
public class Commandes {
    
    /** Référence à la fenêtre principale */
    private FenetrePrincipale laFenetre;
    
    /** Objet effectuant les calculs */
    private ConsoleCalculSimple calculs;
    
    /** Objet de gestion de la mémoire */
    private ConsoleGestionMemoire memoires;
    
    /** Objet du tableur */
    private ConsoleCalculTableur tableur;
    
    /** Objet exécutant les commandes du tableur */
    private ConsoleGestionTableur gestionTableur;
    
    /** Tableau des cellules (partie programme) */
    private Tableur tableauCellules;

    /**
     * Constructeur des commandes
     * @param fenetre Fenêtre à laquelle la console envoie des messages
     */
    public Commandes(FenetrePrincipale fenetre) {
        this.laFenetre = fenetre;
        
        // Construction de la partie programme du tableur
        this.tableauCellules = new Tableur(
                this.laFenetre.getLeTableur().getGrilleTableur());
        
        // On envoie au menu la référence du tableur pour les sauvegardes
        this.laFenetre.getLeMenu().setLeTableur(this.tableauCellules);
        
        /*
         * Les consoles de calculs et de gestion de la mémoire connaissent le
         * panneau contenant le retour console et le gestionnaire de mémoire
         * afin de pouvoir leurs envoyer des messages de mise à jour
         */
        this.calculs = new ConsoleCalculSimple(this.laFenetre);
        this.memoires = new ConsoleGestionMemoire(this.laFenetre);
        this.tableur = new ConsoleCalculTableur(
                this.laFenetre, this.tableauCellules);
        this.gestionTableur = new ConsoleGestionTableur(
                this.laFenetre, this, this.tableauCellules);
        
        // On envoie la référence de la console de calcul tableur au tableur
        this.tableauCellules.setCalculTableur(this.tableur);
    
        // On permet à la console d'envoyer des messages aux trois consoles
        this.laFenetre.getLaConsole().setCommandes(this);
    }
    
    /**
     * Accesseur à calculs
     * @return calculs 
     */
    public ConsoleCalculSimple getCalculs() {
        return calculs;
    }

    /**
     * Accesseur à memoires
     * @return memoires 
     */
    public ConsoleGestionMemoire getMemoires() {
        return memoires;
    }

    /**
     * Accesseur à tableur
     * @return tableur 
     */
    public ConsoleCalculTableur getTableur() {
        return tableur;
    }

    /**
     * Accesseur à gestionTableur
     * @return gestionTableur 
     */
    public ConsoleGestionTableur getGestionTableur() {
        return gestionTableur;
    }

    /**
     * Accesseur à tableauCellules
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