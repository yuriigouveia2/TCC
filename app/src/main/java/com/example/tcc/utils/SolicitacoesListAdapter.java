package com.example.tcc.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tcc.activities.AmigosFragment;
import com.example.tcc.activities.R;
import com.example.tcc.activities.SolicitacoesAmizadeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SolicitacoesListAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList itemName;
    private int resource;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    public SolicitacoesListAdapter(Context context, int resource, ArrayList<String> itemName) {
        super(context, resource, itemName);

        this.context = context;
        this.itemName = itemName;
        this.resource = resource;
    }

    public View getView(final int posicao, View view, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(resource, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        final TextView txtTitle = rowView.findViewById(com.example.tcc.activities.R.id.item_solicitacao);
        ImageView aceitar = rowView.findViewById(com.example.tcc.activities.R.id.aceitar);
        ImageView negar = rowView.findViewById(com.example.tcc.activities.R.id.negar);

        reference.child(getItem(posicao)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtTitle.setText(dataSnapshot.child("nome").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        AceitaNegaAmizade(aceitar, negar, posicao);

        return rowView;
    }

    private void AceitaNegaAmizade(ImageView aceitar, ImageView negar, final int posicao) {
        aceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getItem(posicao);
                itemName.remove(posicao);
                reference.child(firebaseAuth.getCurrentUser().getUid()).child("solicitacoes").child(id).setValue(null);
                reference.child(firebaseAuth.getCurrentUser().getUid()).child("amigos").child(id).setValue(true);
                reference.child(id).child("amigos").child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                reference.child(id).child("solicitacoes").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                Toast.makeText(v.getContext(), "Solicitação aceita", Toast.LENGTH_LONG).show();
            }
        });

        negar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = getItem(posicao);
                itemName.remove(posicao);
                reference.child(firebaseAuth.getCurrentUser().getUid()).child("solicitacoes").child(id).setValue(null);
                reference.child(id).child("solicitacoes").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                Toast.makeText(v.getContext(), "Solicitação negada", Toast.LENGTH_LONG).show();
            }
        });
    }

}
