package com.android.notes.util.Password;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.android.notes.R;
import com.android.notes.util.ChooseFile.ChooseFileActivity;
import com.android.notes.util.EditFile.EditFileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends AppCompatActivity {

    @BindView(R.id.cir1)
    TextView cir1;
    @BindView(R.id.cir2)
    TextView cir2;
    @BindView(R.id.cir3)
    TextView cir3;
    @BindView(R.id.cir4)
    TextView cir4;
    @BindView(R.id.one)
    TextView one;
    @BindView(R.id.two)
    TextView two;
    @BindView(R.id.three)
    TextView three;
    @BindView(R.id.four)
    TextView four;
    @BindView(R.id.five)
    TextView five;
    @BindView(R.id.six)
    TextView six;
    @BindView(R.id.seven)
    TextView seven;
    @BindView(R.id.eight)
    TextView eight;
    @BindView(R.id.nine)
    TextView nine;
    @BindView(R.id.zero)
    TextView zero;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.real)
    TextView real;

    int count = 0;
    int realCount = 0;
    Vibrator vibrator;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                    finish();
//                }
//                return;
//            }
//        }
        //權限
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        while (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                break;
            } else {
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        //權限
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        while(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PasswordActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                break;
            }else{break;}
        }

        vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @OnClick({R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero})
    public void numClicked() {
        count();
    }

    public void count() {
        count++;
        if (count == 1) {
            vibrator.vibrate(20);
            cir1.setBackgroundResource(R.drawable.password_cir2);
        } else if (count == 2) {
            vibrator.vibrate(20);
            cir2.setBackgroundResource(R.drawable.password_cir2);
        } else if (count == 3) {
            vibrator.vibrate(20);
            cir3.setBackgroundResource(R.drawable.password_cir2);
        } else if (count == 4) {
            count = 0;
            cir4.setBackgroundResource(R.drawable.password_cir2);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView1.setText("密碼錯誤");
                    cir1.setBackgroundResource(R.drawable.password_cir);
                    cir2.setBackgroundResource(R.drawable.password_cir);
                    cir3.setBackgroundResource(R.drawable.password_cir);
                    cir4.setBackgroundResource(R.drawable.password_cir);
                    vibrator.vibrate(90);
                }
            }, 100);
        }
    }

    @OnClick(R.id.real)
    public void realClicked() {
        realCount ++;
        if(realCount == 2){
            Intent it = new Intent(PasswordActivity.this, ChooseFileActivity.class);
            startActivity(it);
            finish();
        }

    }
}
