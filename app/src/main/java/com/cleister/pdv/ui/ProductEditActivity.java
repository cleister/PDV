package com.cleister.pdv.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cleister.pdv.R;
import com.cleister.pdv.domain.model.Product;
import com.cleister.pdv.domain.network.APIClient;
import com.cleister.pdv.domain.util.Base64Util;
import com.cleister.pdv.domain.util.ImageInputHelper;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.sprinkles.Query;

public class ProductEditActivity extends BasicActivity implements ImageInputHelper.ImageActionListener {

    @Bind(R.id.spinnerProduct)
    Spinner spinnerProduct;

    @Bind(R.id.editTextDescription)
    EditText editTextDescription;

    @Bind(R.id.editTextUnit)
    EditText editTextUnit;

    @Bind(R.id.editTextPrice)
    EditText editTextPrice;

    @Bind(R.id.editTextBarcode)
    EditText editTextBarcode;

    @Bind(R.id.imageViewProduct)
    ImageView imageViewProduct;

    @Bind(R.id.imageButtonGalley)
    ImageButton imageButtonGalley;

    @Bind(R.id.imageButtonCamera)
    ImageButton imageButtonCamera;

    private ImageInputHelper imageInputHelper;
    private Product product;
    private double latitude = 0.0d;
    private double longitude = 0.0d;

    Callback<String> callbackEditProduct;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configureNewProductCallback();

        dialog = new SpotsDialog(this, getString(R.string.send_to_server));

        LostApiClient lostApiClient = new LostApiClient.Builder(this).build();
        lostApiClient.connect();

        Location location = LocationServices.FusedLocationApi.getLastLocation();
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            Log.d("LOCATION","Latitude -> " + latitude);
            Log.d("LOCATION","Longitude -> " + longitude);
        }

        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);

        List<Product> lstProduct = Query.many(Product.class, Product.OrderBy("barCode")).get().asList();

        //---------------------
        product = new Product();

        final List<String> barcodeList = new ArrayList<>();
        final List<Product> productList;

        for(Product product: lstProduct){
            barcodeList.add(product.getBarcode());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,barcodeList);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerProduct.setAdapter(dataAdapter);
        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String barCode = parent.getItemAtPosition(position).toString();

                product = Product.GetSingleFromBarcodeProduct(barCode);

                if (product != null){
                    editTextDescription.setText(product.getDescription());
                    editTextUnit.setText(product.getUnit());
                    editTextBarcode.setText(product.getBarcode());
                    editTextPrice.setText(String.valueOf(product.getPrice()));

                    Bitmap image = Base64Util.decodeBase64(product.getPhoto());
                    imageViewProduct.setImageBitmap(image);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //---------------------

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                product.setDescription(editTextDescription.getText().toString());
                product.setUnit(editTextUnit.getText().toString());

                if(!editTextPrice.getText().toString().equals("")){
                    product.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
                }

                product.setBarcode(editTextBarcode.getText().toString());

                Bitmap image = ((BitmapDrawable)imageViewProduct.getDrawable()).getBitmap();

                product.setPhoto(Base64Util.encodeTobase64(image));

                product.setLatitude(latitude);
                product.setLongitude(longitude);

                product.save();

                dialog.show();

                new APIClient().getRestService().updateProduto(product.getBarcode(), product.getDescription(), product.getUnit(), product.getPrice(), product.getPhoto(), product.getStatus(), product.getLatitude(), product.getLongitude(), callbackEditProduct);

                Snackbar.make(view, getResources().getString(R.string.snackbar_success_edit_product), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.imageButtonGalley)
    public void onClickGalley()
    {
        imageInputHelper.selectImageFromGallery();
    }

    @OnClick(R.id.imageButtonCamera)
    public void onCamera()
    {
        imageInputHelper.takePhotoWithCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageInputHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);

                if (scanResult == null) {
                       return;
                }

                String result = scanResult.getContents();
                if (result != null) {
                    editTextBarcode.setText(result);
                }
                break;
        }
    }

    @Override
    public void onImageSelectedFromGallery(Uri uri, File imageFile) {
        // cropping the selected image. crop intent will have aspect ratio 16/9 and result image
        // will have size 800x450
        imageInputHelper.requestCropImage(uri, 100, 100, 0, 0);
    }

    @Override
    public void onImageTakenFromCamera(Uri uri, File imageFile) {
        // cropping the taken photo. crop intent will have aspect ratio 16/9 and result image
        // will have size 800x450
        imageInputHelper.requestCropImage(uri, 100, 100, 0, 0);
    }

    @Override
    public void onImageCropped(Uri uri, File imageFile) {
        try {
            // getting bitmap from uri
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            imageViewProduct.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureNewProductCallback() {

        callbackEditProduct = new Callback<String>() {

            @Override public void success(String resultado, Response response) {

                dialog.dismiss();
                finish();
            }

            @Override public void failure(RetrofitError error) {

                dialog.dismiss();

                Snackbar.make(findViewById(android.R.id.content).getRootView(), "Houve um problema de conexão! Por favor, verifique e tente novamente.", Snackbar.LENGTH_SHORT).show();

                Log.e("RETROFIT", "Error:" + error.getMessage());
            }
        };
    }
}
