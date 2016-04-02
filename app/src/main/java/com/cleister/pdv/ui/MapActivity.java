package com.cleister.pdv.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.cleister.pdv.R;
import com.cleister.pdv.domain.model.Product;
import com.cleister.pdv.domain.util.Util;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;


import java.util.List;


import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

public class MapActivity extends BasicActivity {

    @Bind(R.id.mapview)
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        mapView.setStyleUrl(Style.DARK);
        mapView.setCenterCoordinate(new LatLng(-23.5586729,-46.6612236));
        mapView.setZoomLevel(12);

        CursorList cursorList = Query.all(Product.class).get();

        List<Product> listaProdutos = cursorList.asList();

        for(Product produto : listaProdutos) {
            Log.d("PRODUTO", produto.toString());

            if(produto.getLatitude()+produto.getLongitude() != 0.0)
            {
                mapView.addMarker(new MarkerOptions()
                        .position(new LatLng(produto.getLatitude(), produto.getLongitude()))
                        .title(produto.getDescription())
                        .snippet(Util.getCurrencyValue(produto.getPrice()) + " " + produto.getUnit()));
            }
        }

        mapView.onCreate(savedInstanceState);
    }

    @OnClick(R.id.btnRuas)
    public void onClickRuas()
    {
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
    }

    @OnClick(R.id.btnSatelite)
    public void onClickSatelite()
    {
        mapView.setStyleUrl(Style.SATELLITE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}