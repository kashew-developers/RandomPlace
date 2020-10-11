package in.kashewdevelopers.randomplace;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapViewModel extends ViewModel {

    LatLng coordinates;
    String placeName;
    int lastTaskId = 0;

}
