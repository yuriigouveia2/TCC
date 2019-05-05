package com.example.tcc.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MapaFragment extends Fragment implements OnMapReadyCallback {


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private String idUsuario;
    private MapView mapView;
    private GoogleMap googleMap;
    private View mView;
    private Context mContext;


    public MapaFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_mapa, container, false);

        this.idUsuario = getArguments().getString("id");

        return mView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = mView.findViewById(R.id.mapa);

        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapsInitializer.initialize(mContext);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Handler handler = new Handler();
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                reference.child(idUsuario).child("coordenadas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GetLocalizacao(googleMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        handler.post(thread);
    }

    public void GetLocalizacao(final GoogleMap googleMap) {
        final List<LatLng> loc = new ArrayList<>();
        reference.child(this.idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //Adicionar caminho percorrido no mapa
                final PolylineOptions polyOpt = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

                if(dataSnapshot.child("coordenadas").child("0").child("lat").getValue() != null) {

                double lat = (double) dataSnapshot.child("coordenadas").child("0").child("lat").getValue();
                double lon = (double) dataSnapshot.child("coordenadas").child("0").child("lon").getValue();
                final LatLng ponto = new LatLng(lat, lon);

                Handler handler = new Handler();
                Runnable thread = new Runnable() {
                    @Override
                    public void run() {
                        if (ponto != null && !loc.contains(ponto)) {
                            googleMap.clear();
                            loc.add(ponto);

                            if (loc.size() > 0) {

                                String nome = dataSnapshot.child("nome").getValue(String.class);
                                googleMap.addMarker(new MarkerOptions().position(ponto)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                                        .setTitle("Localização de " + nome);

                                //Da zoom de fator 16.0 na camera do mapa (googleMap.getCameraPosition().zoom)
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ponto, 16.0f));
                            }
                        }
                    }
                };
                handler.post(thread);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
