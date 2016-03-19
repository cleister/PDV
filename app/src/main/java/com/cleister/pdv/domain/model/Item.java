package com.cleister.pdv.domain.model;

import java.util.List;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * Created by Cleister on 12/03/2016.
 */
@Table("item")
public class Item extends Model {

    @Key
    @AutoIncrement
    @Column("id")
    private long id;

    @Column("id_buy")
    private String idBuy;

    @Column("id_product")
    private String idProduct;

    @Column("quantity")
    private int quantity;

    public static Item GetSingleItemFromId(long id){
        Item item;// = new Product();
        item = Query.one(Item.class,"select * from item where id = ?", id).get();
        return item;
    }

    public static void ClearAll(){
        List<Item> items = Query.all(Item.class).get().asList();

        for (Item item:items){
            item.delete();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdBuy() {
        return idBuy;
    }

    public void setIdBuy(String idBuy) {
        this.idBuy = idBuy;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
