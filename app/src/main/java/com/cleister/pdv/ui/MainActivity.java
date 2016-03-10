package com.cleister.pdv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.cleister.pdv.R;
import com.cleister.pdv.domain.model.Product;

import java.util.List;

import se.emilsjolander.sprinkles.Query;

public class MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        List<Product> lstProduto = Query.all(Product.class).get().asList();

        if(lstProduto != null)
        {
            for (Product p: lstProduto)
            {
                Log.d("Produto:", "id --> " + p.getId());
                Log.d("Produto:", "descricao --> " + p.getDescription());
                Log.d("Produto:", "unidade --> " + p.getUnit());
                Log.d("Produto:", "codigo de barra --> " + p.getBarcode());
                Log.d("Produto:", "preco --> " + p.getPrice());
                Log.d("Produto:", "foto --> " + p.getPhoto());
                Log.d("Produto:", "------------------------------");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_product)
        {
            Intent intentProductNew = new Intent(MainActivity.this, ProductNewActivity.class);
            startActivity(intentProductNew);
        }
        else if(id == R.id.action_edit_product)
        {
            Intent intentProductEdit = new Intent(MainActivity.this, ProductEditActivity.class);
            startActivity(intentProductEdit);
        }

        return super.onOptionsItemSelected(item);
    }
}
