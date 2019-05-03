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
import android.support.v4.app.FragmentActivity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_amigos, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        listaAmigos = view.findViewById(R.id.lista_amigos);
        adicionarBtn = view.findViewById(R.id.adicionar_amigos_btn);
        solicitacoesBtn = view.findViewById(R.id.solicitacoes_amigos_btn);

        CarregaListaAmigos(inflater);
        GerenciaBotoes();

        return view;
    }


    private void CarregaListaAmigos(final LayoutInflater inflater) {
        final ArrayList<String> listaNomes = new ArrayList<>();
        final ArrayList<Integer> listaImage = new ArrayList<>();
        final ArrayList<String> listaIds = new ArrayList<>();

        reference.child(firebaseAuth.getCurrentUser().getUid()).child("amigos").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snap : dataSnapshot.getChildren()) {
                    String idAmigo = snap.getKey();

                    reference.child(idAmigo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String nomeAmigo = dataSnapshot.child("nome").getValue(String.class);
                            boolean safe = dataSnapshot.child("SAFE").getValue(Boolean.class);
                            int image = R.drawable.ic_security_black_40dp;

                            if(!safe) {
                                image = R.drawable.ic_danger_40dp;
                            }

                            if(!listaNomes.contains(nomeAmigo)) {
                                listaNomes.add(nomeAmigo);
                                listaImage.add(image);
                                listaIds.add(dataSnapshot.getKey());
                            }
                            else{
                                int pos = listaNomes.indexOf(nomeAmigo);
                                listaImage.set(pos, image);
                            }
                            arrayAdapter = new CustomListAdapter(inflater.getContext(), R.layout.list, listaNomes, listaImage);
                            listaAmigos.setAdapter(arrayAdapter);

                            if(!safe) {
                                listaAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        if(listaImage.get(i) == R.drawable.ic_danger_40dp) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", listaIds.get(i));
                                            Fragment fragment = new MapaFragment();
                                            fragment.setArguments(bundle);

                                            CarregaFragment(fragment);
                                            Toast.makeText(getContext(), "Localização de " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getContext(), adapterView.getItemAtPosition(i) + " não está em perigo", Toast.LENGTH_SHORT).show();
                                        }
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

                                        String myId = firebaseAuth.getCurrentUser().getUid();
                                        if(dataSnapshot.getValue() != null) {
                                            for(final DataSnapshot snap : dataSnapshot.getChildren()) {
                                                if(snap.getKey() != null && !snap.getKey().equals(myId)) {
                                                    reference.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.child("amigos").hasChild(snap.getKey())) {
                                                                Toast.makeText(getContext(), "Este usuário já é seu amigo!", Toast.LENGTH_LONG).show();
                                                            }
                                                            else if(dataSnapshot.child("solicitacoes").hasChild(snap.getKey())){
                                                                Toast.makeText(getContext(), "Você já enviou uma solicitação para esse usuário!", Toast.LENGTH_LONG).show();
                                                            }
                                                            else{
                                                                Toast.makeText(getContext(), "Solicitação enviada!", Toast.LENGTH_LONG).show();
                                                                reference.child(snap.getKey()).child("solicitacoes").child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                                    });
                                                }
                                                else if(snap.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                                    Toast.makeText(getContext(), "Você não pode enviar uma solicitação para você mesmo!", Toast.LENGTH_LONG).show();
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

        solicitacoesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SolicitacoesAmizadeFragment();
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