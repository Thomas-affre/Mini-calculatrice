/* 
 * LancementProgramme.java                            9 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.lancement;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.commandes.Commandes;

/**
 * Lanceur de l'application
 * @author Clément Zeghmati
 * @version 0.1
 */
public class LancementProgramme {

    /**
     * Lancement de l'application
     * @param args
     */
    public static void main(String[] args) {
        
        FenetrePrincipale fenetre = new FenetrePrincipale();
        new Commandes(fenetre);

    }

}
