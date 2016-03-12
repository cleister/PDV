package com.cleister.pdv.domain.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cleister.pdv.R;
import com.cleister.pdv.domain.model.ProductItem;
import java.util.List;



/**
 * Created by elcio on 02/12/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<ProductItem> {
    protected LayoutInflater inflater;
    protected int layout;

    public CustomArrayAdapter(Activity activity, int resourceId, List<ProductItem> objects){
        super(activity, resourceId, objects);
        layout = resourceId;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(layout, parent, false);
        TextView tv = (TextView)v.findViewById(R.id.item_label);
        //ImageView foto = (ImageView)v.findViewById(R.id.foto);
        //foto.setImageBitmap(Base64Util.decodeBase64(getItem(position).getPhoto()));
        tv.setText(getItem(position).getDescription() + " " + getItem(position).getQuantity());
        return v;
    }
}