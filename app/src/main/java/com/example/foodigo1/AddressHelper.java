package com.example.foodigo1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressHelper {

    @SuppressLint("SuspiciousIndentation")
    public static List<AddressWithDistance> getAddresses(Context context, double latitude, double longitude, int maxDistance) throws IOException {
        // Récupérez l'adresse en utilisant l'API de géocodage
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<AddressWithDistance> result = new ArrayList<>();
        int maxResult=100;
        //On cherche les 6 adresses Disponibles à moins de 30 m
        while (result.size()<7) {


            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, maxResult);
            // Créez une liste pour stocker les adresses qui correspondent à des routes


            // Parcourez la liste des adresses
            for (Address address : addresses) {
                // Vérifiez si l'adresse est une route en utilisant la méthode getMaxAddressLineIndex
                //L'emplacement n'est pas un batiment
                if (address.getMaxAddressLineIndex() == 0) {
                    // Calculez la distance entre l'adresse et la position en utilisant la méthode distanceTo
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    LatLng addressLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    float distance = location.distanceTo(toLocation(addressLatLng));
                    //On retiens juste les adresses
                    if (distance <= maxDistance && result.size()<7)
                        // Ajoutez l'adresse à la liste de résultats avec sa distance
                        result.add(new AddressWithDistance(address, distance));
                }
            }
            maxResult=maxResult+100;
        }
        return result;
    }

    private static Location toLocation(LatLng latLng) {
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    public static class AddressWithDistance {
        public final Address address;
        public final float distance;

        public AddressWithDistance(Address address, float distance) {
            this.address = address;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "AddressWithDistance{" +
                    "address=" + address +
                    ", distance=" + distance +
                    '}';
        }
    }
}
