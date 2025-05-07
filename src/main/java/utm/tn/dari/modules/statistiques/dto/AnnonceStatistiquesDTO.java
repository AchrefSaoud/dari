package utm.tn.dari.modules.statistiques.dto;
public class AnnonceStatistiquesDTO {

    private long totalAnnonces;
    private long annoncesAujourdHui;
    private long annoncesCetteSemaine;
    private long annoncesActives;
    private long annoncesInactives;

    public long getTotalAnnonces() {
        return totalAnnonces;
    }

    public void setTotalAnnonces(long totalAnnonces) {
        this.totalAnnonces = totalAnnonces;
    }

    public long getAnnoncesAujourdHui() {
        return annoncesAujourdHui;
    }

    public void setAnnoncesAujourdHui(long annoncesAujourdHui) {
        this.annoncesAujourdHui = annoncesAujourdHui;
    }

    public long getAnnoncesCetteSemaine() {
        return annoncesCetteSemaine;
    }

    public void setAnnoncesCetteSemaine(long annoncesCetteSemaine) {
        this.annoncesCetteSemaine = annoncesCetteSemaine;
    }

    public long getAnnoncesActives() {
        return annoncesActives;
    }

    public void setAnnoncesActives(long annoncesActives) {
        this.annoncesActives = annoncesActives;
    }

    public long getAnnoncesInactives() {
        return annoncesInactives;
    }

    public void setAnnoncesInactives(long annoncesInactives) {
        this.annoncesInactives = annoncesInactives;
    }
}
