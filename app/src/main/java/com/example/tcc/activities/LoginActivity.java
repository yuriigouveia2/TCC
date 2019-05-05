package com.example.tcc.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button logarBtn;
    private TextView recuperarSenhaBtn, cadastrarBtn;
    private EditText emailLogin, senhaLogin;
    private Intent intent;

    private FirebaseAuth firebaseAuth;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Definição de botões e campos
        logarBtn = findViewById(R.id.entrar);
        recuperarSenhaBtn = findViewById(R.id.esqueceu_senha);
        cadastrarBtn = findViewById(R.id.nao_possui_conta);
        emailLogin = findViewById(R.id.email_login);
        senhaLogin = findViewById(R.id.senha_login);

        //Intancia de autenticação Firebase
        firebaseAuth = FirebaseAuth.getInstance();



        recuperarSenhaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, RecuperarSenhaActivity.class);
                startActivity(intent);
            }
        });

        cadastrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, CadastrarActivity.class);
                startActivity(intent);
            }
        });




        /*****************************************************************************************/
        /********************************* LOGIN DA APLICAÇÃO ************************************/
        /*****************************************************************************************/

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if(preferences.getString("email", "default").equals("default")) {
            logarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String email = emailLogin.getText().toString();
                    final String senha = senhaLogin.getText().toString();

                    if(!VerificarEmailESenha(email, senha)) {
                        return;
                    }
 
                    final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Aguarde...", "Entrando", true);

                    firebaseAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                // Pega login e senha default do dispositivo
                                editor.putString("email", email);
                                editor.putString("senha", senha);
                                editor.apply();

                                progressDialog.show();
                                Toast.makeText(LoginActivity.this, "Logado", Toast.LENGTH_LONG).show();
                                intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                                progressDialog.dismiss();

                            } else {
                                // Erro ao logar
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } // final onComplete
                    }); // fbAuth
                } // final onClick
            }); // logarBtn
        } // final if
        else {
            final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Aguarde...", "Entrando", true);
            // Salva login e senha como default do dispositivo
            emailLogin.setText(preferences.getString("email", "default"));
            senhaLogin.setText(preferences.getString("senha", "default"));

            String email = emailLogin.getText().toString();
            String senha = senhaLogin.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();

                    if(task.isSuccessful()) {
                        progressDialog.show();
                        Toast.makeText(LoginActivity.this, "Logado", Toast.LENGTH_LONG).show();
                        intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        progressDialog.dismiss();
                    } // final if
                    else {
                        editor.remove("email");
                        editor.remove("senha");
                        editor.apply();
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    } // final else
                } // final onComplete
            }); // fbAuth
        } // final else
    }

    private boolean VerificarEmailESenha(String email, String senha) {
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Por favor, digite o e-mail e a senha!", Toast.LENGTH_LONG).show();
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
