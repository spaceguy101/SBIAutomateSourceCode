package in.busflix.sbiautomatemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String CurrentToken = FirebaseInstanceId.getInstance().getToken();
        if (CurrentToken != null) {
            Intent intent = new Intent("device_id");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            Log.d("token", "Refreshed token: " + CurrentToken);
            editor.putString("device_id", CurrentToken);
            editor.apply();
        } else {
            onTokenRefresh();
        }
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Intent intent = new Intent("device_id");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("token", "Refreshed token: " + refreshedToken);
        editor.putString("device_id", refreshedToken);
        editor.apply();

    }
}
