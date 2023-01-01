package com.example.foodigo1;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class AddressFetcherTask extends AsyncTask<Double, Void, List<AddressHelper.AddressWithDistance>> {

    private Context context;
    private OnAddressFetchedListener listener;
    private List<AddressHelper.AddressWithDistance> listeAddressWithDistance;
    public AddressFetcherTask(Context context, OnAddressFetchedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<AddressHelper.AddressWithDistance> doInBackground(Double... coordinates) {
        double latitude = coordinates[0];
        double longitude = coordinates[1];
        List<AddressHelper.AddressWithDistance> listeAddressWithDistances;

        try {
            listeAddressWithDistances=AddressHelper.getAddresses(context, latitude, longitude,30);
            listeAddressWithDistance=listeAddressWithDistances;
            for (AddressHelper.AddressWithDistance addresWithDistance: listeAddressWithDistances) {
                System.out.println("****************Les adresses et leurs distances à proximité :"+addresWithDistance.toString() );

            }

            return listeAddressWithDistances;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    protected void onPostExecute(List<AddressHelper.AddressWithDistance> addresses) {
        listener.onAddressFetched(addresses);
    }

    public interface OnAddressFetchedListener {
        void onAddressFetched(List<AddressHelper.AddressWithDistance> addresses);
    }

    public List<AddressHelper.AddressWithDistance> getListeAddressWithDistance() {
        return listeAddressWithDistance;
    }
}
