package com.example.tcc.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;

public class MenuPrincipalActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private NotificationCompat.Builder notificacao;
    private NotificationManager notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.acao_inicio);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(firebaseAuth.getCurrentUser().getUid());

        Handler handler = new Handler();
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                Localizacao();
            }
        };

        Runnable thread2 = new Runnable() {
            @Override
            public void run() {
                Notifica();
            }
        };

        handler.post(thread2);
        handler.postDelayed(thread, 500);



    }

    private void Notifica() {
        final DatabaseReference reference2 = firebaseDatabase.getReference();
        reference.child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String idAmigo = snap.getKey();

                    reference2.child(idAmigo).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            final String nomeAmigo = dataSnapshot.child("nome").getValue(String.class);



                            if(!(boolean)dataSnapshot.child("SAFE").getValue()) {
                                Handler handler = new Handler();
                                Runnable thread = new Runnable() {
                                    @Override
                                    public void run() {
                                        final int numero = StringParaNumero(nomeAmigo);

                                        notificacao = new NotificationCompat.Builder(getApplicationContext(), "1")
                                                .setSmallIcon(R.drawable.ic_danger_40dp)
                                                .setColor(getResources().getColor(R.color.ciano_100))
                                                .setContentTitle("SafeBand")
                                                .setContentText(nomeAmigo + " está em perigo.")
                                                .setPriority(NotificationManager.IMPORTANCE_HIGH);

                                        notification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        notification.notify(numero, notificacao.build());
                                    }
                                };
                                handler.post(thread);
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

    @TargetApi(Build.VERSION_CODES.M)
    private void Localizacao() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.child("SAFE").getValue(Boolean.class)) {
                            int tamanho = (int) dataSnapshot.child("coordenadas").getChildrenCount();
                            reference.child("coordenadas").child(String.valueOf(tamanho + 1)).child("lat").setValue(location.getLatitude());
                            reference.child("coordenadas").child(String.valueOf(tamanho + 1)).child("lon").setValue(location.getLongitude());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(MenuPrincipalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MenuPrincipalActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }
        else {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                return;
        }
    }


    private boolean CarregaFragment(Fragment fragment) {
        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.acao_inicio:
                fragment = new InicioFragment();
                break;

            case R.id.acao_amigos:
                fragment = new AmigosFragment();
                break;

            case R.id.acao_configuracoes:
                fragment = new ConfiguracoesFragment();
                break;
        }
        return CarregaFragment(fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        notification.cancelAll();
        this.finish();
    }

    private int StringParaNumero(String nome) {
        char[] nomeArray = nome.replaceAll("\\s","").toCharArray();
        String numeroFinal = "";

        for(char letra : nomeArray) {
            numeroFinal = numeroFinal.concat(String.valueOf(Character.getNumericValue(letra)));
        }

        if(numeroFinal.length() > 8) {
            numeroFinal = numeroFinal.substring(0,8);
        }

        return Integer.parseInt(numeroFinal);
    }
}
