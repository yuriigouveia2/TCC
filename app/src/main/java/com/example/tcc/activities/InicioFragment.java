package com.example.tcc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;


public class InicioFragment extends Fragment {

    private TextView bemVindo;
    private CircleImageView safeBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    public InicioFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_inicio, container, false);

            bemVindo = view.findViewById(R.id.nome_usuario);
            safeBtn = view.findViewById(R.id.safe_btn);

            final String idUsuario = firebaseAuth.getCurrentUser().getUid();

            // Verifica estado da flag SAFE
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean SAFE = (boolean) dataSnapshot.child(idUsuario).child("SAFE").getValue();

                    Activity activity = getActivity();
                    if(activity != null && isAdded()) {
                        if(!SAFE) {
                            safeBtn.setCircleBackgroundColor(activity.getResources().getColor(R.color.orange_400));
                        }
                        else {
                            safeBtn.setCircleBackgroundColor(activity.getResources().getColor(R.color.ciano_100));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Mensagem de bem vindo com nome de usuário
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String mensagemInicio  = "Seja bem vindo, " +
                            dataSnapshot.child(idUsuario).child("nome").getValue(String.class) +
                            "!";

                    bemVindo.setText(mensagemInicio);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Evento de clique para retornar ao estado de segurança
            safeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.confirma_seguranca)
                            .setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reference.child(idUsuario).child("SAFE").setValue(true);
                                    Toast.makeText(getContext(), "Você está em segurança!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.perigo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reference.child(idUsuario).child("SAFE").setValue(false);
                                    Toast.makeText(getContext(), "Você está em perigo!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });

            return view;
        }
        catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
