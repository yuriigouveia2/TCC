package com.example.tcc.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private Button recuperarSenhaBtn;
    private Intent intent;
    private EditText recuperarSenhaET;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        recuperarSenhaET = findViewById(R.id.senha_recuperar_senha);
        recuperarSenhaBtn = findViewById(R.id.recuperar_senha_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        recuperarSenhaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recuperarSenhaET.getText().toString().equals("")) {
                    Toast.makeText(RecuperarSenhaActivity.this, "Informe o e-mail da conta a ser recuperada!", Toast.LENGTH_LONG).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(recuperarSenhaET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RecuperarSenhaActivity.this, "Foi enviado um email para redefinir a senha!", Toast.LENGTH_LONG).show();
                                RecuperarSenhaActivity.this.finish();
                            } else {
                                Toast.makeText(RecuperarSenhaActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
