package spnetworking.sbiautomate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements OnCompleteListener<Void> {
    TextView date;
    TextView time;
    TextView branchName;
    TextView branchAddress;
    WebView attributionText;
    Button getPlaceButton;
    EditText query_text;
    Button scheduleButton;
    TextView cardTextDate;
    TextView cardTextTime;
    TextView cardTextBranchName;
    TextView cardTextBranchAddress;
    TextView cardTextQuery;
    String dayofweekText = null;
    String monthText = null;
    String fullDate = null;
    String selectedDate = null;
    String selectedTime = null;
    String selectedBankName = null;
    String selectedBankAddress = null;
    String bankName1 = "sbi";
    String bankName2 = "state bank of india";
    String place_picker_bank_address = null;
    LinearLayout linearlayout_horizontalScroll;
    HorizontalScrollView horizontalScrollview;
    Dialog mConfirm;

    private final static int PLACE_PICKER_REQUEST = 1;

    static final HashMap<String, LatLng> GeoFenceMap = new HashMap<>();

    private String[] timeLabels = {"10:00 AM-10:30 AM", "10:30 AM-11:00 AM", "11:30 AM-12:00 PM", "12:30 PM-01:00 PM", "01:00 PM-01:30 PM", "01:30 PM-02:00 PM", "02:00 PM-02:30 PM", "02:30 PM-03:00 PM", "03:00 PM-03:30 PM", "03:30 PM-04:00 PM", "04:00 PM-04:30 PM"};

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private GeofencingClient mGeofencingClient;

    private ArrayList<Geofence> mGeofenceList;

    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private PendingIntent mGeofencePendingIntent;


    enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private MainActivity.PendingGeofenceTask mPendingGeofenceTask = MainActivity.PendingGeofenceTask.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        date = (TextView) findViewById(R.id.dateLabel);
        time = (TextView) findViewById(R.id.timeLabel);
        branchName = (TextView) findViewById(R.id.branch_name_label);
        branchAddress = (TextView) findViewById(R.id.branch_address_label);
        attributionText = (WebView) findViewById(R.id.wvAttribution);
        query_text = (EditText) findViewById(R.id.query_editText);
        getPlaceButton = (Button) findViewById(R.id.choose_location_button);
        scheduleButton = (Button) findViewById(R.id.btnSchedule);
        mConfirm = new Dialog(this);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((date.getText().length() > 0) && (time.getText().length() > 0) && (branchAddress.getText().length() > 0) && (query_text.getText().length() > 0)) {
                    scheduleButton.setEnabled(true);
                } else {
                    scheduleButton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        /*
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGeofences();
                setAppointment();
            }
        });
        */

        date.addTextChangedListener(textWatcher);
        time.addTextChangedListener(textWatcher);
        branchAddress.addTextChangedListener(textWatcher);
        query_text.addTextChangedListener(textWatcher);
        final DatePickerTimeline timeline = (DatePickerTimeline) findViewById(R.id.timeline);
        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.YEAR) % 2000);
            }
        });

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {

                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); //

                if (dayOfWeek == 1) {
                    dayofweekText = "Sun";
                } else if (dayOfWeek == 2) {
                    dayofweekText = "Mon";
                } else if (dayOfWeek == 3) {
                    dayofweekText = "Tue";
                } else if (dayOfWeek == 4) {
                    dayofweekText = "Wed";
                } else if (dayOfWeek == 5) {
                    dayofweekText = "Thu";
                } else if (dayOfWeek == 6) {
                    dayofweekText = "Fri";
                } else if (dayOfWeek == 7) {
                    dayofweekText = "Sat";
                }

                if (month == 0) {
                    monthText = "January";
                } else if (month == 1) {
                    monthText = "February";
                } else if (month == 2) {
                    monthText = "March";
                } else if (month == 3) {
                    monthText = "April";
                } else if (month == 4) {
                    monthText = "May";
                } else if (month == 5) {
                    monthText = "June";
                } else if (month == 6) {
                    monthText = "July";
                } else if (month == 7) {
                    monthText = "August";
                } else if (month == 8) {
                    monthText = "September";
                } else if (month == 9) {
                    monthText = "October";
                } else if (month == 10) {
                    monthText = "November";
                } else if (month == 11) {
                    monthText = "December";
                }

                fullDate = dayofweekText + ", " + day + " " + monthText + " " + year;
                date.setText(fullDate);
                selectedDate = date.getText().toString();

            }

        });
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        timeline.setFirstVisibleDate(year, month, day);

        timeline.setLastVisibleDate(year, month + 1, day);

        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.mandatory)
                .checkedChipColor(ResourcesCompat.getColor(getResources(), R.color.selected_color, null))
                .checkedTextColor(ResourcesCompat.getColor(getResources(), R.color.selected_font_color, null))
                .uncheckedChipColor(ResourcesCompat.getColor(getResources(), R.color.deselected_color, null))
                .uncheckedTextColor(ResourcesCompat.getColor(getResources(), R.color.deselected_font_color, null));

        linearlayout_horizontalScroll = (LinearLayout) findViewById(R.id.horizontal_layout);
        horizontalScrollview = (HorizontalScrollView) findViewById(R.id.horizontal_scroll);
        config.useInsetPadding = true;
        config.selectMode = ChipCloud.SelectMode.mandatory;
        ChipCloud horizontalChipCloud = new ChipCloud(this, linearlayout_horizontalScroll, config);
        horizontalChipCloud.addChips(timeLabels);

        horizontalChipCloud.setListener(new ChipListener() {
            @Override
            public void chipCheckedChange(int index, boolean checked, boolean userClick) {
                if (userClick) {
                    time.setText(timeLabels[index]);
                    selectedTime = time.getText().toString();
                }
            }
        });

        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                branchName.setText("");
                branchAddress.setText("");
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(MainActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void showPopup(View v) {
        ImageView ivClose;
        Button btnConfirm;


        mConfirm.setContentView(R.layout.custom_popup);

        ivClose = (ImageView) mConfirm.findViewById(R.id.ivClose);
        btnConfirm = (Button) mConfirm.findViewById(R.id.btnConfirm);
        cardTextDate = (TextView) mConfirm.findViewById(R.id.cardText_date);
        cardTextTime = (TextView) mConfirm.findViewById(R.id.cardText_time);
        cardTextBranchName = (TextView) mConfirm.findViewById(R.id.cardText_branchName);
        cardTextBranchAddress = (TextView) mConfirm.findViewById(R.id.cardText_branchAddress);
        cardTextQuery = (TextView) mConfirm.findViewById(R.id.cardText_query);


        cardTextDate.setText(selectedDate);
        cardTextTime.setText(selectedTime);
        cardTextBranchName.setText(selectedBankName);
        cardTextBranchAddress.setText(selectedBankAddress);
        cardTextQuery.setText(query_text.getText().toString());

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConfirm.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGeofences();
                setAppointment();
                date.setText("");
                time.setText("");
                branchName.setText("");
                branchAddress.setText("");
                query_text.setText("");
                mConfirm.dismiss();
            }
        });
        mConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mConfirm.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        performPendingGeofenceTask();
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    @SuppressLint("MissingPermission")
    public void addGeofences() {
        mPendingGeofenceTask = MainActivity.PendingGeofenceTask.ADD;
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }


    public void removeGeofencesButtonHandler() {
        mPendingGeofenceTask = MainActivity.PendingGeofenceTask.REMOVE;
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = MainActivity.PendingGeofenceTask.NONE;
        /*
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
        */
    }


    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : GeoFenceMap.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER )
                    .build());
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @SuppressLint("MissingPermission")
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == MainActivity.PendingGeofenceTask.ADD) {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnCompleteListener(this);
        } else if (mPendingGeofenceTask == MainActivity.PendingGeofenceTask.REMOVE) {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = MainActivity.PendingGeofenceTask.NONE;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedplace = PlacePicker.getPlace(MainActivity.this, data);
                place_picker_bank_address = selectedplace.getName().toString();

                boolean isBank = false;
                selectedplace = PlacePicker.getPlace(this, data);
                for (int i : selectedplace.getPlaceTypes()) {
                    if (i == Place.TYPE_BANK && (place_picker_bank_address.toLowerCase().contains(bankName1.toLowerCase()) || place_picker_bank_address.toLowerCase().contains(bankName2.toLowerCase()))) {
                        isBank = true;
                        break;
                    }
                }

                if (isBank) {
                    //Right type of place
                    branchName.setText(selectedplace.getName());
                    selectedBankName = branchName.getText().toString();
                    branchAddress.setText(selectedplace.getAddress());
                    selectedBankAddress = branchAddress.getText().toString();
                    GeoFenceMap.put("SBI_Branch", selectedplace.getLatLng());
                    populateGeofenceList();

                    if (selectedplace.getAttributions() == null) {
                        attributionText.loadData("", "text/html:charset=utf-8", "UFT-8");
                    } else {
                        attributionText.loadData(selectedplace.getAttributions().toString(), "text/html:charset=utf-8", "UFT-8");
                    }

                } else {
                    //Tell to the user to select an appropriate place
                    SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("Please select State Bank of India.");

                    pDialog.show();
                }

            }
        }
    }

    void setAppointment(){

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("date",date.getText());
            jsonObject.put("time",time.getText());
            jsonObject.put("branch",branchAddress.getText());
            jsonObject.put("query",query_text.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .url(MyApplication.domain + "/sbiautomate/setAppointment")
                .header("Connection", "keep-alive")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFailureDialog();
                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()){
                            Toasty.success(MainActivity.this, "Appointment Confirmed!", Toast.LENGTH_SHORT, true).show();
                            showSuccessDialog();
                        }else{
                            showFailureDialog();
                        }
                    }
                });

            }

        });

    }

    void showFailureDialog(){
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText("Failed Please Try Again..");
        pDialog.show();
    }

    void showSuccessDialog(){
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("Thank You .. :)");
        pDialog.show();
    }



}
