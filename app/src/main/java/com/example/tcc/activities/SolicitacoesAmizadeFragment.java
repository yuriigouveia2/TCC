package com.example.tcc.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tcc.utils.SolicitacoesListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SolicitacoesAmizadeFragment extends Fragment {

    private ArrayAdapter<String> arrayAdapter;
    private ListView listaSolicitacoes;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private Button voltarAmigosFragment;

    public SolicitacoesAmizadeFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solicitacoes_amizade, container, false);

        listaSolicitacoes = view.findViewById(R.id.lista_solicitacoes);
        voltarAmigosFragment = view.findViewById(R.id.voltar_amigos_btn);

        CarregaSolicitacoes(inflater);
        VoltaAmigosFragment();

        return view;
    }

    private void CarregaSolicitacoes(LayoutInflater inflater) {
        final ArrayList<String> listaIds = new ArrayList<>();

        reference.child(firebaseAuth.getCurrentUser().getUid()).child("solicitacoes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    listaIds.add(snap.getKey());
                }
                arrayAdapter = new SolicitacoesListAdapter(getContext(), R.layout.list_solicitacoes, listaIds);
                listaSolicitacoes.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void VoltaAmigosFragment() {
        voltarAmigosFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AmigosFragment();
                CarregaFragment(fragment);
            }
        });
    }

    private boolean CarregaFragment(Fragment fragment) {
        FragmentActivity activity = getActivity();
        if(fragment != null && activity != null) {
            activity
            .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }
}
