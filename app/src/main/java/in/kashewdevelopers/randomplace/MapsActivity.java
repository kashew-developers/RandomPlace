package in.kashewdevelopers.randomplace;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Random;

import in.kashewdevelopers.randomplace.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ActivityMapsBinding binding;

    Marker marker;
    LatLngToPlaceTask.OnPlaceTaskListener placeTaskListener;
    int lastTaskId = 0;

    Toast copiedToast;

    // constants
    double MIN_LATITUDE = -85.0, MAX_LATITUDE = 85;
    double MIN_LONGITUDE = -180.0, MAX_LONGITUDE = 180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        else
            return;

        initialize();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        marker.setVisible(false);
        marker.setDraggable(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker.setPosition(latLng);
                setCoordinateView(latLng.latitude, latLng.longitude);
                setPlaceName(latLng.latitude, latLng.longitude);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng coordinates = marker.getPosition();
                binding.placeProgressBar.setVisibility(View.VISIBLE);
                setPlaceName(coordinates.latitude, coordinates.longitude);
            }
        });
    }


    // initialization
    @SuppressLint("ShowToast")
    public void initialize() {
        copiedToast = Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT);

        setWidgetInitialVisibility();
        setPlaceTaskListener();
    }

    public void setWidgetInitialVisibility() {
        binding.coordinateLabel.setVisibility(View.GONE);
        binding.coordinateValue.setVisibility(View.GONE);

        binding.placeLabel.setVisibility(View.GONE);
        binding.placeProgressBar.setVisibility(View.GONE);
        binding.placeValue.setVisibility(View.GONE);

        binding.copyButton.setVisibility(View.GONE);
        binding.shareButton.setVisibility(View.GONE);

        binding.zoomInButton.setVisibility(View.GONE);
        binding.zoomOutButton.setVisibility(View.GONE);
    }

    public void setPlaceTaskListener() {
        placeTaskListener = new LatLngToPlaceTask.OnPlaceTaskListener() {
            @Override
            public void onPlaceTaskListener(String placeName, int taskId) {
                if (lastTaskId == taskId) {
                    binding.placeProgressBar.setVisibility(View.GONE);

                    if (placeName == null) {
                        binding.placeValue.setText(getString(R.string.place_name_not_found));
                    } else {
                        binding.placeValue.setText(placeName);
                    }
                } else {
                    if (placeName != null) {
                        binding.placeValue.setText(placeName);
                    }
                }
            }
        };
    }


    // UI functionality
    public void setCoordinateView(double latitude, double longitude) {
        String coordinate = latitude + ", " + longitude;
        binding.coordinateValue.setText(coordinate);

        binding.coordinateLabel.setVisibility(View.VISIBLE);
        binding.coordinateValue.setVisibility(View.VISIBLE);

        binding.placeLabel.setVisibility(View.VISIBLE);
        binding.placeProgressBar.setVisibility(View.VISIBLE);
        binding.placeValue.setVisibility(View.VISIBLE);

        binding.copyButton.setVisibility(View.VISIBLE);
        binding.shareButton.setVisibility(View.VISIBLE);

        binding.zoomOutButton.setVisibility(View.VISIBLE);
        binding.zoomInButton.setVisibility(View.VISIBLE);
    }


    // functionality
    public void setRandomMarker() {
        Random r = new Random();
        DecimalFormat decimalFormat = new DecimalFormat("#.000000");

        double latitude = MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * r.nextDouble();
        latitude = Double.parseDouble(decimalFormat.format(latitude));

        double longitude = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * r.nextDouble();
        longitude = Double.parseDouble(decimalFormat.format(longitude));

        LatLng coordinate = new LatLng(latitude, longitude);
        marker.setPosition(coordinate);
        marker.setVisible(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));

        setCoordinateView(latitude, longitude);

        setPlaceName(latitude, longitude);
    }

    @SuppressWarnings("deprecation")
    public void setPlaceName(double latitude, double longitude) {
        new LatLngToPlaceTask(this, ++lastTaskId)
                .setOLatLngToPlaceTask(placeTaskListener)
                .execute(latitude, longitude);
    }

    public String getPlaceDetails() {
        String details = getString(R.string.coordinates) + ": " + binding.coordinateValue.getText();
        String placeName = binding.placeValue.getText().toString();
        if ((!placeName.equals(getString(R.string.loading_place_name))) &&
                (!placeName.equals(getString(R.string.place_name_not_found)))) {
            details += "\n\n" + getString(R.string.place_name) + ": " + placeName;
        }

        return details;
    }


    // handle widget clicks
    public void onRandomClicked(View v) {
        setRandomMarker();
    }

    public void onCopyClicked(View v) {
        String text = getPlaceDetails();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), text);
        clipboard.setPrimaryClip(clip);
        copiedToast.show();
    }

    public void onShareClicked(View v) {
        String text = getPlaceDetails();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share));
        startActivity(shareIntent);
    }

    public void onZoomInClicked(View v) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10f));
    }

    public void onZoomOutClicked(View v) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 0f));
    }

}