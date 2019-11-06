package api.objects.utils.service;

import api.objects.DeezerEntity;

import java.net.URL;

public class Offer extends DeezerEntity {
    private String name;
    private float amount;
    private String currency;
    private String displayed_amount;
    private URL tc;
    private URL tc_html;
    private URL tc_txt;
    private int try_and_buy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDisplayed_amount() {
        return displayed_amount;
    }

    public void setDisplayed_amount(String displayed_amount) {
        this.displayed_amount = displayed_amount;
    }

    public URL getTc() {
        return tc;
    }

    public void setTc(URL tc) {
        this.tc = tc;
    }

    public URL getTc_html() {
        return tc_html;
    }

    public void setTc_html(URL tc_html) {
        this.tc_html = tc_html;
    }

    public URL getTc_txt() {
        return tc_txt;
    }

    public void setTc_txt(URL tc_txt) {
        this.tc_txt = tc_txt;
    }

    public int getTry_and_buy() {
        return try_and_buy;
    }

    public void setTry_and_buy(int try_and_buy) {
        this.try_and_buy = try_and_buy;
    }
}
