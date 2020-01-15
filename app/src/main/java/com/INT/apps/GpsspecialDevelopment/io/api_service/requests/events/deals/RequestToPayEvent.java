package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

/**
 * @author Michael Soyma (Created on 9/13/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class RequestToPayEvent {

    private String cardId;
    private int dealId;
    private double amount;
    private String currency;
    private String currencySymbol;
    private int quantity;
    private double tax;
    private int points;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getDealId() {
        return dealId;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
