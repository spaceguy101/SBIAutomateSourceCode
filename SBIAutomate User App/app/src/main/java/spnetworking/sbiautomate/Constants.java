package spnetworking.sbiautomate;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;

final class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "spnetworking.sbiautomate";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 720;

    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 100;


    static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();


    static {
        BAY_AREA_LANDMARKS.put("Office", new LatLng(18.4873981, 73.85407899999996));
        BAY_AREA_LANDMARKS.put("Kunal Icon", new LatLng(18.5933972,73.79588839999997));
    }

}
