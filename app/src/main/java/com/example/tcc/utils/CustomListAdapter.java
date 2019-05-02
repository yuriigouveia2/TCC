package com.example.tcc.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList itemName;
    private int resource;

    public CustomListAdapter(Context context, int resource, ArrayList<String> itemName) {
        super(context, com.example.tcc.activities.R.layout.list, itemName);

        this.context = context;
        this.itemName = itemName;
        this.resource = resource;
    }

    public View getView(int posicao, View view, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(resource, parent, false);

        TextView txtTitle = rowView.findViewById(com.example.tcc.activities.R.id.item);
        ImageView imageView = rowView.findViewById(com.example.tcc.activities.R.id.imagem);

        txtTitle.setText(itemName.indexOf(posicao));
        imageView.setImageResource(com.example.tcc.activities.R.drawable.ic_security_black_40dp);

        return rowView;
    }
}
