package api.objects.utils.service;

public class Infos {
    private String country_iso;
    private String country;
    private boolean open;
    private Offer[] offers;

    public String getCountry_iso() {
        return country_iso;
    }

    public void setCountry_iso(String country_iso) {
        this.country_iso = country_iso;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Offer[] getOffers() {
        return offers;
    }

    public void setOffers(Offer[] offers) {
        this.offers = offers;
    }
}
