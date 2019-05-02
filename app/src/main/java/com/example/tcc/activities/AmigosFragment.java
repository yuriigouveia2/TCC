package com.example.tcc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tcc.utils.CustomListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AmigosFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listaAmigos;
    private Button adicionarBtn, solicitacoesBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_amigos, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        listaAmigos = view.findViewById(R.id.lista_amigos);
        adicionarBtn = view.findViewById(R.id.adicionar_amigos_btn);
        solicitacoesBtn = view.findViewById(R.id.solicitacoes_amigos_btn);

        CarregaListaAmigos();
        GerenciaBotoes();

        return view;
    }


    private void CarregaListaAmigos() {
        final List<String> listaIds = new ArrayList<>();

        reference.child(firebaseAuth.getCurrentUser().getUid()).child("amigos").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String idAmigo = snap.getKey();

                    reference.child(idAmigo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String nomeAmigo = dataSnapshot.child("nome").getValue(String.class);

                            listaIds.add(nomeAmigo);
                            arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list, R.id.item, listaIds);
                            listaAmigos.setAdapter(arrayAdapter);

                            if(!dataSnapshot.child("SAFE").getValue(Boolean.class)) {
                                listaAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        // TODO: Iniciar MapFragment
                                        Toast.makeText(getContext(), "MAP", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GerenciaBotoes() {
        adicionarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText emailAmigoET = new EditText(getContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.digite_email)
                        .setPositiveButton(R.string.enviar_solicitacao, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                final String emailAmigo = emailAmigoET.getText().toString();

                                Query query = reference.orderByChild("email").equalTo(emailAmigo);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.getValue() != null) {
                                            for(DataSnapshot snap : dataSnapshot.getChildren()) {
                                                if(snap.getKey() != null) {
                                                    Toast.makeText(getContext(), "Solicitação enviada!", Toast.LENGTH_LONG).show();
                                                    reference.child(snap.getKey()).child("solicitacoes").child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "O e-mail fornecido não existe!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                        else {
                                            Toast.makeText(getContext(), "O e-mail fornecido não existe!", Toast.LENGTH_LONG).show();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Não fazer nada
                                dialog.cancel();
                            }
                        });
                builder.setView(emailAmigoET);
                builder.show();
            }
        });
    }
}