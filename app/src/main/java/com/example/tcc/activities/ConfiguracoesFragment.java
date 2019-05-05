package com.example.tcc.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.prefs.Preferences;


public class ConfiguracoesFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private Button sairContaBtn, limparCoordBtn, alterarSenhaBtn;
    private Intent intent;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context mContext;

    public ConfiguracoesFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);

        sairContaBtn = view.findViewById(R.id.sairContaBtn);
        limparCoordBtn = view.findViewById(R.id.limparCoordBtn);
        alterarSenhaBtn = view.findViewById(R.id.alterarSenhaBtn);

        sairConta();
        limparCoordenadas();
        mudarSenha();

        return view;
    }

    private void mudarSenha() {

        alterarSenhaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Você receberá um e-mail para redefinir a senha!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void limparCoordenadas() {
        limparCoordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child(firebaseAuth.getCurrentUser().getUid()).child("SAFE").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if((boolean)dataSnapshot.getValue()) {
                            reference.child(firebaseAuth.getCurrentUser().getUid()).child("coordenadas").child("0").setValue(null);
                            Toast.makeText(getContext(), "Coordenadas apagadas", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Você não pode apagar as coordenadas enquanto está em perigo!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void sairConta() {
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = preferences.edit();
        sairContaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("email");
                editor.remove("senha");
                editor.apply();
                firebaseAuth.signOut();
                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


}
