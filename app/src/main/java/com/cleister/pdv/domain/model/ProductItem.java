package com.cleister.pdv.domain.model;

/**
 * Created by Cleister on 12/03/2016.
 */
public class ProductItem {

    private String idBuy;
    private long idItem;
    private String photo;
    private String description;
    private String unit;
    private int quantity;
    private double price;

    public String getIdBuy() {
        return idBuy;
    }

    public void setIdBuy(String idBuy) {
        this.idBuy = idBuy;
    }

    public long getIdItem() {
        return idItem;
    }

    public void setIdItem(long idItem) {
        this.idItem = idItem;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
