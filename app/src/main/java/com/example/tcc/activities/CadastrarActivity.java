package com.example.tcc.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastrarActivity extends AppCompatActivity {

    private TextView voltarLoginBtn;
    private Button cadastrarBtn;
    private Intent intent;
    private EditText nomeCadastro, emailCadastro, senhaCadastro;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        cadastrarBtn = findViewById(R.id.cadastrar);
        voltarLoginBtn = findViewById(R.id.voltar_login);
        nomeCadastro = findViewById(R.id.nome_cadastro);
        emailCadastro = findViewById(R.id.email_cadastro);
        senhaCadastro = findViewById(R.id.senha_cadastro);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        voltarLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastrarActivity.this.finish();
            }
        });

        /*****************************************************************************************/
        /******************************** CADASTRO NA APLICAÇÃO **********************************/
        /*****************************************************************************************/

        cadastrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(CadastrarActivity.this, "Aguarde...", "Cadastrando", true);
                final String nome = nomeCadastro.getText().toString();
                final String email = emailCadastro.getText().toString();
                final String senha = senhaCadastro.getText().toString();

                if(!VerificarEmailESenha(nome, email, senha)) {
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            reference.child(firebaseAuth.getCurrentUser().getUid()).child("nome").setValue(nome);
                            reference.child(firebaseAuth.getCurrentUser().getUid()).child("SAFE").setValue(true);
                            reference.child(firebaseAuth.getCurrentUser().getUid()).child("email").setValue(email);
                            Toast.makeText(CadastrarActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();

                            CadastrarActivity.this.finish();
                        }
                        else {
                            Toast.makeText(CadastrarActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });

                intent = new Intent(CadastrarActivity.this, LoginActivity.class);
            }
        });
    }

    private boolean VerificarEmailESenha(String nome, String email, String senha) {
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(email) && TextUtils.isEmpty(nome)) {
            Toast.makeText(getApplicationContext(), "Por favor, preencha os campos!", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(nome)) {
            Toast.makeText(getApplicationContext(), "Por favor, digite o nome!", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Por favor, digite o e-mail!", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(senha)) {
            Toast.makeText(getApplicationContext(), "Por favor, digite a senha!", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), "Por favor, digite um e-mail válido!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
