/**
 * @author SAIFIDINE Dayar 
 * @version 1.2
 */
package org.leanGrid.model.util;
import java.util.ArrayList;
import java.util.List;
public class Algorithme{
    int lambda;
    Reseau reseau;


    /**
     * Constructeur de la classe Algorithme.
     * @param reseau le réseau sur lequel l'algorithe va être exécuté.
     * @param lambda la pénalisation des surcharges affectée au réseau.
     */
    public Algorithme(Reseau reseau, int lambda) {
        this.reseau = reseau;
        this.lambda = lambda;
        this.reseau.setPenalite(this.lambda);
    }

    /**
     * Programme principal chargé de chercher et retourner la meilleure configuration du réseau sur lequel s'applique l'algorithme.
     * @return reseau amélioré.
     */
    public Reseau launch(){  
        int maxIterations = 1000;
        int nb_iterations = 0;
        double cout_precedent = Double.MAX_VALUE;
        double delta = 0.01; 
        boolean amelioration,modification;
        while(nb_iterations < maxIterations){
            amelioration = false;
            for(Generateur g : reseau.getGenerateurs()){
                if(!isTauxCorrect(g)){
                    //Si le taux du générateur est incorrect(supérieur à 1 ou inférieur à 0.)
                    modification = false;
                    if(reseau.getTauxFromGen(g) == 0){
                        // Si le taux d'utilisation est égal à 0 (générateur inutilisé)
                        modification = fillNotUsedGenerateur(g);
                    } else { 
                        // Si le taux d'utilisation est supérieur à 1 (générateur surutilisé)
                        modification = unfillOverUsedGenerateur(g);
                    }
                    if(modification){
                        // S'il y a eu une modification du réseau on met l'amélioration à true
                        amelioration = true;
                    }
                }
            }
            // Calculer le coût actuel (somme des écarts au taux moyen)
            double cout_actuel = calculateTotalCost();
            
            // Si aucune amélioration a été constatée et la différence des couts a atteints delta, terminer la couble
            if(!amelioration || Math.abs(cout_precedent - cout_actuel) < delta){
                break;
            }
            cout_precedent= cout_actuel;
            nb_iterations++;
        }
        return reseau;
    }


    /**
     * Calcule le coût total du réseau (somme des carrés des écarts au taux moyen)
     * @return coût du réseau sur lequel l'algorithme est appliqué.
     */
    private double calculateTotalCost(){
        double cost = 0;
        double tauxMoyen = reseau.getTauxMoyen();
        for(Generateur g : reseau.getGenerateurs()){
            double taux = reseau.getTauxFromGen(g);
            double ecart = taux - tauxMoyen;
            cost += ecart * ecart;
        }
        return cost;
    }


    /**
     * Cherche une maison à connecter avec un générateur inutilisé.
     * @param generateur avec un taux d'utilisation nul.
     * @return {@code true} si la charge actuelle du générateur a été augmentée, {@code false} sinon.
     */
    private boolean fillNotUsedGenerateur(Generateur generateur){
        Maison maison = null;
        Connection c1 = null;
        for(Generateur generateur1 : overUsedGenerateurs(generateur)){
            for(Connection connexion : new ArrayList<>(reseau.getCoByGenerateur(generateur1))){
                maison = connexion.getMaison();
                if(isThisAGoodConnection(generateur, maison)){
                    reseau.supprimerConnexion(connexion);
                    c1 = new Connection(maison, generateur);
                    reseau.ajouterConnexion(c1);
                    return true;                
                }
            }
        }
        return false;
    }



    /**
     * Cherche un générateur pour connecter une ou plusieurs des maisons d'un générateur surutilisé
     * @param generateur dui appartient au réseau et qui est surchargé.
     * @return {@code true} si la charge du générateur surutilisé est abaissé, {@code false} sinon.
     */
        private boolean unfillOverUsedGenerateur(Generateur generateur){
        boolean amelioration = false;
        List<Connection> connectionsToMove = new ArrayList<>(reseau.getCoByGenerateur(generateur));
        Maison maison;
        int maisonConsommation;
        int nouvelle_charge;
        for(Connection connexion : connectionsToMove){
            maison = connexion.getMaison();
            maisonConsommation = maison.getConsommation().getValue();
            
            //Générateur sous-utilisés
            for(Generateur g : underUsedGenerateurs(generateur)){
                nouvelle_charge = g.getChargeActuelle() + maisonConsommation;
                if(nouvelle_charge <= g.getCapacite()){
                    reseau.supprimerConnexion(connexion);
                    reseau.ajouterConnexion(new Connection(maison, g));
                    amelioration = true;
                    break; // Si on a eu une amélioration dans ces générateurs sous-utilisés on sort de la boucle
                    
                }
            }
            if(amelioration==true){
                //Si il y a eu une amélioration, on sort de la boucle principale. On n'a pas besoin de chercher d'autre amélioration simple (1 échange entre générateurs)
                break;
            }
            
            //Générateur disponibles(non surchargés.)
            for(Generateur g : usableGenerateurs(generateur)){
                nouvelle_charge = g.getChargeActuelle() + maisonConsommation;
                if(nouvelle_charge <= g.getCapacite()){
                    //Même concept que la boucle précédente
                    reseau.supprimerConnexion(connexion);
                    reseau.ajouterConnexion(new Connection(maison, g));
                    amelioration = true;
                    break;
                }
            }
             if(amelioration==true){
                //Si il y a eu une amélioration, on sort de la boucle principale. On n'a pas besoin de chercher d'autre amélioration simple (1 échange entre générateurs)
                break;
            }
            
            // Si aucun générateur ne peut accueillir, échanger avec un autre excepté le générateur en question
            for(Generateur g : getGenerateursExcepteG(generateur)){
                List<Connection> connectionsG = new ArrayList<>(reseau.getCoByGenerateur(g));
                for(Connection c1 : connectionsG){
                    Maison maisonG = c1.getMaison();

                    // Si en simulant les nouvelles charges sur chaque générateur les taux sont corrects, l'échange est faisable
                    if(canSwap(generateur, maison, g, maisonG)){
                        reseau.supprimerConnexion(connexion);
                        reseau.supprimerConnexion(c1);
                        reseau.ajouterConnexion(new Connection(maisonG, generateur));
                        reseau.ajouterConnexion(new Connection(maison, g));
                        amelioration = true;
                        break;
                    }
                }
                
            }
             if(amelioration==true){
                break;
             }
              //Si aucune amélioration n'a été effectuée on va essayer d'effectuer des échanges en chaine en forçant le déplacement de la maison concernée à un générateur qui peut l'accuellir. 
             amelioration = ChaineMove(generateur, maison);
             if(amelioration==true){
                break;
             }   
        }
       
        return amelioration;
    }



    /**
     * Effectue un mouvement en chaîne pour affecter une maison d'un générateur surutilisé en libérant de l'espace dans un autre générateur du réseau.
     * @param sourceGenerateur le générateur surchargé
     * @param maisonLourde la maison à déplacer
     * @return {@code true}si le déplacement a été réussi, {@code false} sinon.
     */
     private boolean ChaineMove(Generateur sourceGenerateur, Maison maisonLourde) {
        int consoLourde = maisonLourde.getConsommation().getValue();
        for (Generateur cible : getGenerateursExcepteG(sourceGenerateur)) {
           
            int espaceDisponible = cible.getCapacite() - cible.getChargeActuelle();
            int manque = consoLourde - espaceDisponible;
            
            if (manque > 0) {
               
                List<Connection> connectionsCible = new ArrayList<>(reseau.getCoByGenerateur(cible));
                
               
                connectionsCible.sort((c1, c2) -> 
                    Integer.compare(c1.getMaison().getConsommation().getValue(),
                                   c2.getMaison().getConsommation().getValue()));
                
                
                List<Maison> maisonsADeplacer = new ArrayList<>();
                List<Generateur> destinations = new ArrayList<>();
                int espaceLibere = 0;
                
                for (Connection c : connectionsCible) {
                    Maison m = c.getMaison();
                    int conso = m.getConsommation().getValue();
                    
                    
                    for (Generateur dest : getGenerateursExcepteG(cible)) {
                        if (dest.equals(sourceGenerateur)) continue;
                        
                        if (dest.getChargeActuelle() + conso <= dest.getCapacite()) {
                            maisonsADeplacer.add(m);
                            destinations.add(dest);
                            espaceLibere += conso;
                            break;
                        }
                    }
                    
                    
                    if (espaceDisponible + espaceLibere >= consoLourde) {
                        break;
                    }
                }
                
               
                if (espaceDisponible + espaceLibere >= consoLourde && !maisonsADeplacer.isEmpty()) {
                   
                    Connection connexionLourde = null;
                    for (Connection c : reseau.getCoByGenerateur(sourceGenerateur)) {
                        if (c.getMaison().equals(maisonLourde)) {
                            connexionLourde = c;
                            break;
                        }
                    }
                    
                    if (connexionLourde == null) continue;
                    
                   
                    for (int i = 0; i < maisonsADeplacer.size(); i++) {
                        Maison m = maisonsADeplacer.get(i);
                        Generateur dest = destinations.get(i);
                       
                        for (Connection c : new ArrayList<>(reseau.getCoByGenerateur(cible))) {
                            if (c.getMaison().equals(m)) {
                                reseau.supprimerConnexion(c);
                                reseau.ajouterConnexion(new Connection(m, dest));
                                break;
                            }
                        }
                    }
                    
                  
                    reseau.supprimerConnexion(connexionLourde);
                    reseau.ajouterConnexion(new Connection(maisonLourde, cible));
                    
                    return true;
                }
            } else if (manque <= 0) {
               
                Connection connexionLourde = null;
                for (Connection c : reseau.getCoByGenerateur(sourceGenerateur)) {
                    if (c.getMaison().equals(maisonLourde)) {
                        connexionLourde = c;
                        break;
                    }
                }
                
                if (connexionLourde != null) {
                    reseau.supprimerConnexion(connexionLourde);
                    reseau.ajouterConnexion(new Connection(maisonLourde, cible));
                    return true;
                }
            }
        }
        
        return false;
    }



    /**
     * Verifie si la connexion entre un générateur et une maison donnée est possible sans incidence sur le taux d'utilisation du générateur.
     * @param generateur un générateur aui appartient au réseau sur lequel travaille l'algorithme.
     * @param maison une maison qui appartient au réseau surlequel l'agorithme est appliqué.
     * @return {@code true} si la connexion entre la maison et le générateur est possible, {@code false} sinon.
     */
    private boolean isThisAGoodConnection(Generateur generateur, Maison maison){
        return generateur.getChargeActuelle()+maison.getConsommation().getValue()<=generateur.getCapacite();
    }


    /**
     * Verifie si le taux d'utilisation d'un générateur est inférieur ou égal à 1 et supérieur à 0.
     * @param generateur un générateur qui appartient au réseau.
     * @return {@code true} si le taux est compris entre O et 1 inclus, {@code false} sinon.
     */
    private boolean isTauxCorrect(Generateur generateur){
        double taux = reseau.getTauxFromGen(generateur);
        return (taux<=1 && taux>0)?true:false;
    }




    /**
     * Vérifie si le taux d'utilisation d'un générateur est très supérieur(différence d'au moins 0.5) au taux d'utilisation moyen du réseau.
     * @param generateur un générateur qui appartient au réseau sur lequel l'algorithme travaille.
     * @return {@code true} si le taux d'utilisation du générateur est très supérieur(différence plus ou égale à 0.5) au taux d'utilisation moyen du réseau, {@code false} sinon.
     */
    private boolean isSupTauxMoyen(Generateur generateur){
        return reseau.getTauxFromGen(generateur)-reseau.getTauxMoyen()>=0.5;
    }


    /**
     * Vérifie si le taux d'utilisation d'un générateur est très inférieur (différence d'au moins 0.5) au taux d'utilisation moyen du réseau.
     * @param generateur un générateur qui appartient au réseau sur lequel l'algorithme est appliqué.
     * @return {@code true} si le taux d'utilisation du générateur est très inférieur (différence plus ou égal à 0.5) au taux d'utilisation moyen du réseau, {@code false} sinon.
     */
    private boolean isInfTauxMoyen(Generateur generateur){
        return reseau.getTauxMoyen() - reseau.getTauxFromGen(generateur)>=0.5;
    }


    /**
     * Retourne une liste des générateurs du réseau qui sont surutilisés ( taux(g) > 1 ou ont un taux d'utilisation très supérieur au taux d'utilisation moyen du réseau sur lequel est appliqué.
     * @param generateur qui est exlcu dans cette liste.
     * @return overUsedGenerateurs une liste des générateurs du réseau qui sont surutilisés.
     */
    private List<Generateur> overUsedGenerateurs(Generateur generateur){
        List<Generateur> generateurs1 = getGenerateursExcepteG(generateur);
        List<Generateur> overusedGenerateurs = new ArrayList<>();
        for(Generateur g : generateurs1){
            if(reseau.getTauxFromGen(g)>=1 || isSupTauxMoyen(g) == true ){
                overusedGenerateurs.add(g);
            }
        }
        return overusedGenerateurs;
    }


    /**
     * Retourne les générateurs sous-utilisés (taux d'utilisation d'un générateur inférieur au taux d'utilisation moyen du réseau).
     * @param generateur qui est exclu dans cette liste.
     * @return generateurs qui sont sous-utilisés.
     */
    private List<Generateur> underUsedGenerateurs(Generateur generateur){
        List<Generateur> generateurs1 = getGenerateursExcepteG(generateur);
        List<Generateur> underUsedGenerateurs = new ArrayList<>();
        for(Generateur g : generateurs1){
            if(isInfTauxMoyen(g)==true){
                //Si son taux d'utilisation est inférieur au taux d'utilisation moyen
                underUsedGenerateurs.add(g);
            }
        }
        return underUsedGenerateurs;
    }


    /**
     * Retourne une liste des générateurs du réseau qui sont disponibles. C'est à dire, leur taux d'utilisation respectif est compris entre 0 et 1 exclu.
     * @param generateur qui est exclu dans cette liste.
     * @return generateurs qui sont disponibles (utilisables).
     */
    private List<Generateur> usableGenerateurs(Generateur generateur){
        List<Generateur> generateurs1 = getGenerateursExcepteG(generateur);
        List<Generateur> underUsedGenerateurs = new ArrayList<>();
        for(Generateur g : generateurs1){
            if(reseau.getTauxFromGen(g)<1){
                underUsedGenerateurs.add(g);
            }
        }
        return underUsedGenerateurs;
    }


    /**
     * Retourne une liste des générateurs qui appartiennent au réseau excepté celui qui est indiqué en paramètre.
     * @param g qui appartient au réseau sur lequel l'algorithm est appliqué.
     * @return generateurs qui appartiennent au réseau excepté celui indiqué en paramètre.
     */
    private List<Generateur> getGenerateursExcepteG(Generateur g){
        List<Generateur> generateurs1 = new ArrayList<>();
        for(Generateur gen : reseau.getGenerateurs()){
            if(gen.equals(g)==false){
                generateurs1.add(gen);
            }
        }
        return generateurs1;
    }

    /**
     * Simule l'échange de maisons entre deux générateurs.
     * @param a        le premier générateur impliqué dans l'échange
     * @param aMaison  la maison actuellement alimentée par le générateur A
     * @param b        le second générateur impliqué dans l'échange
     * @param bMaison  la maison actuellement alimentée par le générateur B
     * @return {@code true} si l'échange est réalisable sans dépasser la capacité de chacun des générateurs, {@code false} sinon
     */
    private boolean canSwap(Generateur a, Maison aMaison, Generateur b, Maison bMaison){
        int aNew = a.getChargeActuelle() - aMaison.getConsommation().getValue()
                   + bMaison.getConsommation().getValue();
        int bNew = b.getChargeActuelle() - bMaison.getConsommation().getValue()
                   + aMaison.getConsommation().getValue();
        return aNew <= a.getCapacite() && bNew <= b.getCapacite();
    }

}