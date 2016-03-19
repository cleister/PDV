package com.cleister.pdv;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;

/**
 * Created by Cleister on 08/03/2016.
 */
public class PdvApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());

        sprinkles.addMigration(new Migration() {
            @Override
            protected void onPreMigrate() {
                // do nothing
            }

            @Override
            protected void doMigration(SQLiteDatabase db) {
                db.execSQL(
                        "CREATE TABLE product (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                "description TEXT,"+
                                "unit TEXT,"+
                                "price REAL,"+
                                "barcode TEXT,"+
                                "photo TEXT,"+
                                "latitude REAL,"+
                                "longitude REAL,"+
                                "status INTEGER"+
                                ")"
                );

                db.execSQL(
                        "CREATE TABLE item (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                "id_buy TEXT,"+
                                "id_product TEXT,"+
                                "quantity INTEGER"+
                                ")"
                );
            }

            @Override
            protected void onPostMigrate() {
                // do nothing
            }
        });
    }
}
