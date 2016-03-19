package com.cleister.pdv.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cleister.pdv.R;
import com.cleister.pdv.domain.adapter.CustomArrayAdapter;
import com.cleister.pdv.domain.model.Carrinho;
import com.cleister.pdv.domain.model.Compra;
import com.cleister.pdv.domain.model.Item;
import com.cleister.pdv.domain.model.Product;
import com.cleister.pdv.domain.model.ProductItem;
import com.cleister.pdv.domain.network.APIClient;
import com.cleister.pdv.domain.util.Util;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import dmax.dialog.SpotsDialog;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.typeserializers.StringSerializer;

public class MainActivity extends BasicActivity {

    private ZXingLibConfig zxingLibConfig;

    private List<ProductItem> list;
    private int quantityItems;
    private double totalValue;

    private CustomArrayAdapter adapter;

    private Callback<List<Product>> callbackProdutos;

    private AlertDialog dialog;

    private String idBuy;

    private Carrinho carrinho;

    private Compra compra;

    private Callback<String> callbackCompra;

    @Bind(R.id.listView)
    SwipeMenuListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;

        configureProdutoCallback();
        configureCompraCallback();

        dialog = new SpotsDialog(this, "Carregando...");

        //Deleta todos os itens de compras anteriores que não foram finalizadas.
        Item.ClearAll();

        idBuy = Util.getUniquePsuedoID();
        carrinho = new Carrinho();

        //carrinho.setId(0);
        carrinho.setIdCompra(idBuy);
        carrinho.setEncerrada(0);
        carrinho.setEnviada(0);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator.initiateScan(MainActivity.this, zxingLibConfig);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(Util.convertPixelsToDp(190.0f, MainActivity.this));

                openItem.setIcon(R.drawable.ic_exposure_plus_1_black_36dp);

                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(Util.convertPixelsToDp(190.0f, MainActivity.this));

                // set a icon
                deleteItem.setIcon(R.drawable.ic_remove_shopping_cart_white_36dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ProductItem productItem = adapter.getItem(position);
                Item item = Item.GetSingleItemFromId(productItem.getIdItem());

                switch (index) {
                    case 0:
                        //Toast.makeText(getApplicationContext(), "Action 1 for " + itemProduto.getDescricao(), Toast.LENGTH_SHORT).show();
                        item.setQuantity(item.getQuantity() + 1);
                        item.save();
                        list.clear();
                        pupulateList();
                        break;
                    case 1:
                        //Toast.makeText(getApplicationContext(), "Action 2 for " + itemProduto.getDescricao(), Toast.LENGTH_SHORT).show();
                        item.delete();
                        list.clear();
                        pupulateList();

                        break;
                }
                return false;
            }
        });

        pupulateList();
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
        else if(id == R.id.action_sycronize_product)
        {
            dialog.show();
            new APIClient().getRestService().getAllProdutos(callbackProdutos);
        }
        else if(id == R.id.action_close_compra)
        {
            List<Item> items = Query.all(Item.class).get().asList();

            int quantityItems = 0;
            double priceTotal = 0.0d;
            Product product;

            for (Item it:items)
            {
                quantityItems += it.getQuantity();

                product = Product.GetSingleFromBarcodeProduct(it.getIdProduct());

                priceTotal += (it.getQuantity() * product.getPrice());
            }

            compra = new Compra();
            compra.setCarrinho(carrinho);
            compra.setItens(items);

            MaterialStyledDialog dialog = new MaterialStyledDialog(this)
                    .setTitle("Fechar Compra?")
                    .setDescription("Quantidade de volumes: "+quantityItems+" - Total: R$ "+priceTotal)
                    .setPositive("Sim", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

                            dialog.dismiss();
                            MainActivity.this.dialog.show();
                            new APIClient().getRestService().enviarCompra(compra, callbackCompra);
                            Log.d("MaterialStyledDialogs", "Do something!");
                        }
                    })
                    .setNegative("Não", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

                            dialog.dismiss();
                            // NADA!!!!!
                            Log.d("MaterialStyledDialogs", "Cancelado pelo usuário!");
                        }
                    })
                    .build();
            //...
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:

                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                String result = scanResult.getContents();
                if (result != null) {

                    Log.d("SCANBARCODE", "barcode: " + result);

                    Product product = Product.GetSingleFromBarcodeProduct(result);

                    if (product != null) {

                        Item item = new Item();

                        item.setId(0L);
                        item.setIdBuy(idBuy);
                        item.setIdProduct(product.getBarcode());
                        item.setQuantity(1);

                        item.save();

                        pupulateList();

                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.toast_main_activity_prodcut_null), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
        }
    }

    public void pupulateList(){
        List<Item> listaItem = Query.many(Item.class, "select * from item where id_buy = ? order by id", idBuy).get().asList();

        Log.d("TAMANHOLISTA",""+ listaItem.size());

        ProductItem productItem;
        Product product;
        list = new ArrayList<>();
        totalValue = 0.0d;
        quantityItems = 0;

        for(Item item:listaItem){

            product = Product.GetSingleFromBarcodeProduct(item.getIdProduct());

            productItem = new ProductItem();
            productItem.setIdBuy(idBuy);
            productItem.setIdItem(item.getId());
            productItem.setPhoto(product.getPhoto());
            productItem.setDescription(product.getDescription());
            productItem.setUnit(product.getUnit());
            productItem.setQuantity(item.getQuantity());
            productItem.setPrice(product.getPrice());

            list.add(productItem);

            totalValue += item.getQuantity() * product.getPrice();
            quantityItems += item.getQuantity();
        }
        getSupportActionBar().setTitle("PDV " + Util.getFormatedCurrency(String.valueOf(totalValue)));
        adapter = new CustomArrayAdapter(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
    }

    private void configureProdutoCallback() {

        callbackProdutos = new Callback<List<Product>>() {

            @Override public void success(List<Product> resultado, Response response) {

                List<Product> lp = Query.all(Product.class).get().asList();

                for(Product p:lp){
                    p.delete();
                }

                for(Product product:resultado){
                    product.setId(0L);
                    product.save();
                }

                dialog.dismiss();
            }

            @Override public void failure(RetrofitError error) {

                dialog.dismiss();

                Log.e("RETROFIT", "Error:"+error.getMessage());
            }
        };
    }

    private void configureCompraCallback() {

        callbackCompra = new Callback<String>() {

            @Override public void success(String resultado, Response response) {

                Item.ClearAll();

                carrinho = new Carrinho();
                //carrinho.setId(0);
                idBuy = Util.getUniquePsuedoID();
                carrinho.setIdCompra(idBuy);
                carrinho.setEnviada(0);
                carrinho.setEncerrada(0);

                pupulateList();

                dialog.dismiss();
            }

            @Override public void failure(RetrofitError error) {

                dialog.dismiss();

                Log.e("RETROFIT", "Error:"+error.getMessage());
            }
        };
    }
}
