/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.android.bluetoothchat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.android.Utils.ToastUtil;
import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.BoomButtonBuilder;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.leakcanary.RefWatcher;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    public BluetoothChatFragment fragment;
    private  Button mToastBtn  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_constrainted);
        // ??????Toast
        mToastBtn = findViewById(R.id.Toast_button) ;
        mToastBtn.setOnClickListener(this);

        getActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (ContextCompat.checkSelfPermission
                (MyApplication.getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //????????????,????????????
            //????????????
            // ??????????????????
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                    },
                    1);
        } else {
            //?????????
            Toast.makeText(MyApplication.getContext(), "????????????", Toast.LENGTH_SHORT).show();
        }
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }
    public void hideToastButton(){
        mToastBtn.setVisibility(View.INVISIBLE);
    }
    public void showToastButton(){
        mToastBtn.setVisibility(View.VISIBLE);
    }
    // ??????????????????????????????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ????????????????????????
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);//1
        refWatcher.watch(this);
    }

    // ??????????????????????????????????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // ?????????????????????????????????????????????create??????????????????????????????????????????????????????????????????????????????
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    // ??????????????????menu?????????????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a chain of targets that will receive log data
     */
    // ?????????????????????
    @Override
    public void initializeLogging() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Toast_button:{
                if (ToastUtil.getState()==ToastUtil.state_close)
                    ToastUtil.setState(ToastUtil.state_on);
                else
                    ToastUtil.setState(ToastUtil.state_close);
                Toast.makeText(MainActivity.this,ToastUtil.getState()==ToastUtil.state_on?"Toast???????????????":"Toast???????????????",Toast.LENGTH_SHORT).show();
                mToastBtn.setText("Toast_"+((ToastUtil.getState()==ToastUtil.state_on)?"on":"closed"));
                break ;
            }
        }
    }
}
