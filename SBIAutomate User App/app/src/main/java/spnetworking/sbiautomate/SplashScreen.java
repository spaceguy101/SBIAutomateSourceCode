package spnetworking.sbiautomate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreen extends AppCompatActivity {

    ImageView sbi_logo;
    ImageView automate_tag;
    ImageView sbi_top_image;
    ImageView sbi_bottom_image;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        checkPermission(false);
    }

    public void checkPermission(boolean firstTime) {
        if (!hasPermissions(this, PERMISSIONS)) {
            if (!firstTime) {
                showPermissionDialog();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        } else {
            setContentView(R.layout.activity_splash_screen);

            sbi_logo = (ImageView) findViewById(R.id.ivSBILogo);
            automate_tag = (ImageView) findViewById(R.id.ivAutomate);
            sbi_top_image = (ImageView) findViewById(R.id.outline_image_top);
            sbi_bottom_image = (ImageView) findViewById(R.id.outline_image_bottom);
            Animation fade_animation = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
            Animation pop=AnimationUtils.loadAnimation(this,R.anim.pop);
            Animation slide_up=AnimationUtils.loadAnimation(this,R.anim.slide_up);
            sbi_top_image.startAnimation(fade_animation);
            sbi_logo.startAnimation(pop);
            automate_tag.startAnimation(fade_animation);
            sbi_bottom_image.startAnimation(slide_up);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, Dashboard.class));
                    finish();
                }
            }, 3000);

        }
    }

    void showPermissionDialog() {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Please Allow Permissions");
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                ActivityCompat.requestPermissions(SplashScreen.this, PERMISSIONS, PERMISSION_ALL);
            }
        });

        pDialog.show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
