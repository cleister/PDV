package com.cleister.pdv.domain.model;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * Created by Cleister on 08/03/2016.
 */
@Table("product")
public class Product extends Model {

    @Key
    @Column("id")
    @AutoIncrement
    private long id;

    @Column("description")
    private String description;

    @Column("unit")
    private String unit;

    @Column("price")
    private double price;

    @Column("barcode")
    private String barcode;

    @Column("photo")
    private String photo;

    @Column("latitude")
    private double latitude;

    @Column("longitude")
    private double longitude;

    public static String OrderBy(String column){
        return "select * from product order by " + column;
    }

    public static Product GetSingleFromBarcodeProduct(String value){
        Product product;// = new Product();
        product = Query.one(Product.class, "select * from product where barcode = ?", value).get();
        return product;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
