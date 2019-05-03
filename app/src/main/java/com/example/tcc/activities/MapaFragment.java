package com.example.tcc.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

        reference.child(this.idUsuario).child("coordenadas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GetLocalizacao();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void GetLocalizacao() {
        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        final List<LatLng> loc = new ArrayList<>();
        reference.child(this.idUsuario).child("coordenadas").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Adicionar caminho percorrido no mapa
                PolylineOptions polyOpt = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double lat = (double) snapshot.child("lat").getValue();
                    double lon = (double) snapshot.child("lon").getValue();

                    LatLng ponto = new LatLng(lat, lon);


                    if(ponto != null && !loc.contains(ponto)) {
                        googleMap.clear();

                        loc.add(ponto);
                        polyOpt.add(ponto);

                        if(loc.size() > 0) {
                            //Define pontos da linha a ser tracejada
                            googleMap.addPolyline(polyOpt);
                            //Marcador na posiçao original, de cor verde. Sempre sobrescreve ao pegar novos pontos.
                            googleMap.addMarker(new MarkerOptions().position(polyOpt.getPoints().get(0))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                                    .setTitle("Ponto de partida");


                            //Adiciona marcador na ultima posiçao traçada, de cor vermelha
                            if(loc.size() > 1){
                                googleMap.addMarker(new MarkerOptions().position(polyOpt.getPoints().get(loc.size()-1))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                                        .setTitle("Localização atual");
                            }

                            //Da zoom de fator 16.0 na camera do mapa
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polyOpt.getPoints().get(loc.size()-1), 16.0f));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
