package com.cleister.pdv.ui;

import android.bluetooth.BluetoothAssignedNumbers;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.cleister.pdv.domain.util.Base64Util;
import com.cleister.pdv.domain.util.ImageInputHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.typeserializers.BitmapSerializer;
import se.emilsjolander.sprinkles.typeserializers.StringSerializer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                Log.d("BARCODE", "Selecionado --- > " + barCode);

                //product = Query.one(Product.class, Product.GetSingleFromBarcodeProduct(barCode)).get();
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

                product.save();

                Snackbar.make(view, "Produto alterado com sucesso!", Snackbar.LENGTH_SHORT).show();

                //finish();
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
