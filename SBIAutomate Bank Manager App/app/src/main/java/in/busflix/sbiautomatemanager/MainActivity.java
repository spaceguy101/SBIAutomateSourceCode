package in.busflix.sbiautomatemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client = MyApplication.getClient();
    SpotsDialog dialog;
    TextView cardText_date;
    TextView cardText_time;
    TextView cardText_branchAddress;
    TextView cardText_query;
    ConstraintLayout appointmentConstraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new SpotsDialog(this);
        cardText_branchAddress = findViewById(R.id.cardText_branchAddress);
        cardText_date = findViewById(R.id.cardText_date);
        cardText_time = findViewById(R.id.cardText_time);
        cardText_query = findViewById(R.id.cardText_query);

        appointmentConstraintLayout = findViewById(R.id.constraintLayout);

        getAppointments();
    }

    void getAppointments(){
        appointmentConstraintLayout.setVisibility(View.GONE);
        showProgress(true);
        final Request request = new Request.Builder()
                .url(MyApplication.domain+"/sbiautomate/getAppointments")
                .header("Connection", "keep-alive")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        onNetworkError();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);

                        if (response.isSuccessful()) {
                            appointmentConstraintLayout.setVisibility(View.VISIBLE);
                            Log.i("res",res);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                String date = jsonObject.getString("date");
                                String time = jsonObject.getString("time");
                                String branch = jsonObject.getString("branch");
                                String query = jsonObject.getString("query");

                                cardText_date.setText(date);
                                cardText_time.setText(time);
                                cardText_branchAddress.setText(branch);
                                cardText_query.setText(query);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onNetworkError();
                            }


                        } else {
                            onNoAppointmentError();
                        }
                    }
                });

            }

        });

    }

    public void logout(View view) {

        showProgress(true);
        final Request request = new Request.Builder()
                .url(MyApplication.domain+"/sbiautomate/logout")
                .header("Connection", "keep-alive")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()){
                            showProgress(false);
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            showProgress(false);
                        }

                    }
                });
            }

        });

    }

    void showProgress(boolean show){
        if(show){
            dialog.show();
            dialog.setMessage("Please Wait ...");
        }else{
            if(dialog.isShowing())
                dialog.dismiss();
        }
    }

    void onNetworkError(){
        appointmentConstraintLayout.setVisibility(View.GONE);
            View container = findViewById(android.R.id.content);
            if (container != null) {
                Snackbar.make(container, "Please Check Internet ", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAppointments();
                    }
                }).show();
            }

    }

    void onNoAppointmentError(){
        appointmentConstraintLayout.setVisibility(View.GONE);
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, "No Appointments Found !", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAppointments();
                }
            }).show();
        }

    }

}
