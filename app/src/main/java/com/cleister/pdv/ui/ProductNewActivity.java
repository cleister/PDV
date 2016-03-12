package com.cleister.pdv.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.BinderThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cleister.pdv.R;
import com.cleister.pdv.domain.model.Product;
import com.cleister.pdv.domain.util.Base64Util;
import com.cleister.pdv.domain.util.ImageInputHelper;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

public class ProductNewActivity extends BasicActivity implements ImageInputHelper.ImageActionListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Cadastro",editTextDescription.getText().toString());
                Log.d("Unidade",editTextUnit.getText().toString());
                Log.d("Preco",editTextPrice.getText().toString());
                Log.d("Codigo",editTextBarcode.getText().toString());

                product = new Product();
                product.setId(0L);
                product.setDescription(editTextDescription.getText().toString());
                product.setUnit(editTextUnit.getText().toString());

                if(!editTextPrice.getText().toString().equals("")){
                    product.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
                }else {
                    product.setPrice(0.0);
                }

                product.setBarcode(editTextBarcode.getText().toString());

                Bitmap image = ((BitmapDrawable)imageViewProduct.getDrawable()).getBitmap();

                product.setPhoto(Base64Util.encodeTobase64(image));

                product.setLatitude(latitude);
                product.setLongitude(longitude);

                product.save();
                finish();
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
}
