package in.kashewdevelopers.randomplace;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

@SuppressWarnings("deprecation")
public class LatLngToPlaceTask extends AsyncTask<Double, Void, String> {

    public WeakReference<Context> contextWeakReference;
    int taskId;

    LatLngToPlaceTask(Context context, int taskId) {
        this.taskId = taskId;
        contextWeakReference = new WeakReference<>(context);
    }

    public interface OnPlaceTaskListener {
        void onPlaceTaskListener(String placeName, int taskId);
    }
    OnPlaceTaskListener onPlaceTaskListener;

    @Override
    protected String doInBackground(Double... latLng) {
        if (latLng.length < 2) {
            return null;
        }

        double latitude = latLng[0];
        double longitude = latLng[1];

        Geocoder geocoder = new Geocoder(contextWeakReference.get());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            return null;
        }

        if (addressList.size() >= 1) {
            return addressList.get(0).getAddressLine(0);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String placeName) {
        super.onPostExecute(placeName);
        if (onPlaceTaskListener != null) {
            onPlaceTaskListener.onPlaceTaskListener(placeName, taskId);
        }
    }

    public LatLngToPlaceTask setOLatLngToPlaceTask(OnPlaceTaskListener onPlaceTaskListener) {
        this.onPlaceTaskListener = onPlaceTaskListener;
        return this;
    }

}
