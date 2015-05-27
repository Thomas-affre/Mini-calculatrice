/* 
 * Cellule.java                            17 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.programme.tableur;

/**
 * Objet mat�rialisant une cellule d'un tableur
 * @author Thomas Affre
 * @author Thibaut M�jane
 * @author Florian Louargant
 * @author Cl�ment Zeghmati
 * @version 1.1
 */
public class Cellule {
    
    /** R�f�rence au tableur � laquelle est ratach�e cette cellule */
    private Tableur sonTableur;
    
    /** 
     * Coordonn�es de la cellule correspondant � 1 lettre de A � Z et � un
     * chiffre de 1 � 20
     */
    private String coordonnees;
    
    /** Contenu de la cellule (formule ou valeur brute) */
    private String contenu;
        
    /** Passe � true si cette cellule contient une formule */
    private boolean formule;

    /**
     * Construit la cellule avec ses coordonn�es
     * @param coordonnees Coordonn�es de la cellule
     * @param sonTableur R�f�rence au tableur � laquelle est ratach�e
     *                           cette cellule
     */
    public Cellule(String coordonnees, Tableur sonTableur) {
        this.coordonnees = coordonnees;
        this.sonTableur = sonTableur;
    }
    
    /**
     * Construit une cellule � partir des informations contenues dans un fichier
     * binaire de sauvegarde
     * @param coordonnees Coordonn�es de la cellule
     * @param contenu Contenu brut de la cellule
     * @param formule Formule contenue dans la cellule ou null
     * @param sonTableur R�f�rence au tableur � laquelle est ratach�e
     *                           cette cellule
     */
    public Cellule(String coordonnees, String contenu, String formule,
            Tableur sonTableur) {
        this.sonTableur = sonTableur;
        this.coordonnees = coordonnees;
        
        if (formule == null) {
            this.formule = false;
            this.sonTableur.affectationValeur(this.coordonnees, contenu);
        } else {
            this.formule = true;
            this.contenu = formule;
        }
    }

    /**
     * Sauvegarde l'�tat de la cellule dans un tableau correspondant � :
     *          0 : coordonn�es sous forme de chaine
     *          1 : contenu brut de la cellule
     *          2 : formule. null s'il n'y en a pas
     * @return tableau de l'�tat de la cellule
     */
    public String[] sauvegardeCellule() {
        String[] aRetourner = new String[3];
        
        aRetourner[0] = this.coordonnees;
        aRetourner[1] = this.getValeur();
        aRetourner[2] = this.formule ? this.contenu : null;
        
        return aRetourner;
    }

    /**
     * Accesseur � contenu
     * @return contenu 
     */
    public String getContenu() {
        
        return contenu;
    }

    /**
     * Mutateur de contenu
     * @param contenu nouveau contenu
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /**
     * Accesseur � formule
     * @return formule 
     */
    public boolean isFormule() {
        return formule;
    }

    /**
     * Mutateur de formule
     * @param formule nouveau formule
     */
    public void setFormule(boolean formule) {
        this.formule = formule;
    }

    /**
     * Accesseur � coordonn�es
     * @return coordonnees 
     */
    public String getCoordonnees() {
        return coordonnees;
    }
    
    /**
     * Accesseur au contenu brut de la cellule
     * @return contenu brut de la cellule
     */
    public String getValeur() {
        return this.sonTableur.restaurationValeurCellule(this.coordonnees);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        /*
         * S'il y a une formule, � la fin du contenu il y a un = suivi du 
         * nom de la cellule. On l'enl�ve donc.
         */
        String laFormule = null;
        if (this.formule) {
            int i;
            for (i = 0; contenu.charAt(i) != '='; i++);
            laFormule = this.contenu.substring(0,i);
        }
        
        return "Cellule " + this.coordonnees + " :"
                + "\n\t- valeur : " + (this.getValeur().equals("") ? "vide"
                        : this.getValeur())
                + "\n\t- formule : " + (this.formule ? laFormule : "/");
    }
}