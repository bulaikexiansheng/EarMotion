/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.bluetoothchat;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chaquo.python.PyException;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.android.PublicUIListeners.SwitchForSaveDataFileListener;
import com.example.android.PythonExecutor.PythonExecutor;
import com.example.android.Utils.ToastUtil;
import com.example.android.bluetoothchat.sampledata.AutoScrollTextView;
import com.example.android.bluetoothchat.sampledata.RoundCornersImageView;
import com.example.android.common.logger.Log;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.android.bluetoothchat.Constants.MESSAGE_desperate;
import static com.example.android.bluetoothchat.Constants.MESSAGE_runmodel;
import static com.example.android.bluetoothchat.Constants.musicname;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment  {
    /* 蓝牙数据保存目录 */
    public static final String DATA_SAVED_DIR_NAME = "EarMotion" ; // 数据保存在手机中的目录
    /* 播放器 */
    private static MediaPlayer mediaPlayer;
    private static boolean isPlayer_show = false;
    private AutoScrollTextView autoScrollTextView;
    private boolean isplaying = false;          // 音乐是否正在播放
    private int current_music = 1;              // 当前音乐编号
    private View player;
    private RoundCornersImageView media_image;
    private static View media_play;
    private static View media_pause;
    private static View media_next;
    private static View media_previous;
    private static OnClickListeners onClickListener ;
    public boolean imuisShowing = false;
    public boolean imuisShowing2 = false;
    public boolean imuisShowing3 = false;
    private static ThreadPoolExecutor threadPoolExecutor;
    private BaiduMap mbaidumap;
    private static final String TAG = "BluetoothChatFragment";
    public static boolean savingflag = false;
    private static boolean message_show = false;
    private static boolean map_show = false;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private static boolean isFirstLocation = false;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static Queue<String> queue_gx = new LinkedList<String>();
    private static Queue<String> queue_gy = new LinkedList<String>();
    private static Queue<String> queue_gz = new LinkedList<String>();
    private static Queue<String> queue_bx = new LinkedList<String>();
    private static Queue<String> queue_by = new LinkedList<String>();
    private static Queue<String> queue_bz = new LinkedList<String>();
//    private static Queue<String> queue_ax = new LinkedList<String>();
//    private static Queue<String> queue_ay = new LinkedList<String>();
//    private static Queue<String> queue_az = new LinkedList<String>();
//    private static Queue<String> queue_nx = new LinkedList<String>();
//    private static Queue<String> queue_ny = new LinkedList<String>();
//    private static Queue<String> queue_nz = new LinkedList<String>();
//    private static Queue<String> queue_qx = new LinkedList<String>();
//    private static Queue<String> queue_qy = new LinkedList<String>();
//    private static Queue<String> queue_qz = new LinkedList<String>();
//    private static Queue<String> queue_qw = new LinkedList<String>();
    private static Queue<Integer> queue_result = new LinkedList<Integer>();
    private static final int DATASIZE = 20;
    private static final int CHANNALS = 3;
    private static int gx_scan = 0;
    private static int past = 4;
    private static int tick = 0;
    // Layout Views
    private boolean player_show = false;
    private ListView mConversationView;
    private View button_imu;
    private View button_imu2;
    private View button_imu3;
    private BoomMenuButton bmb;
    private Button backbutton ;
    private static ExecutorService singleThreadExecutor;
    private static ExecutorService singleThreadExecutor1;
    private static ExecutorService singleThreadExecutor3;
    private imuChartFragment imuchartfragment;
    private imu2ChartFragment imu2chartfragment;
    private imu3ChartFragment imu3chartfragment;
    public Fragment thisfragment;
    public Switch switchForFileSaved ;
    public static SwitchForSaveDataFileListener switchListener ;
    private static int flag = 0;
    private static int chart_update = 0;
    private static int send = 0;
    public static String filename;
    private MyHandler mhandler;
    private static MyHandler2 mhandler2, mhandler3;
    private final WeakReference<Activity> weakReference = new WeakReference<Activity>(getActivity());
    /**
     * Name of the connected device
     */
    private static String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private static ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    // 初始化python模块
    public void initPython() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getContext()));
        }
    }

    // 初始化播放器模块
    public void initMedia() {
        File mfile = null;
        mediaPlayer = new MediaPlayer();
        isPlayer_show = true;
        try {
            String f = Environment.getExternalStorageDirectory().getCanonicalPath() + "/yourmusic/music_" + String.valueOf(current_music) + ".mp3";
            autoScrollTextView.initScrollTextView(getActivity().getWindowManager(), musicname[current_music - 1]);
            mfile = new File(f);
            mediaPlayer.setDataSource(mfile.getPath());
            media_image.setBackgroundResource(Constants.mdrawable[current_music - 1]);
//            System.out.println("播放器装载");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 销毁播放器
    public void DestroyMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlayer_show = false;
            isplaying = false;
            current_music = 1;
            if (media_play.getVisibility() == View.INVISIBLE) {
                media_play.setVisibility(View.VISIBLE);
                media_pause.setVisibility(View.INVISIBLE);
            }
//            System.out.println("播放器解除");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initPython();
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        threadPoolExecutor = new ThreadPoolExecutor(3, 10, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100));
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor1 = Executors.newSingleThreadExecutor();
        singleThreadExecutor3 = Executors.newSingleThreadExecutor();
        ExecutorService singleThreadExecutor2 = Executors.newSingleThreadExecutor();
        singleThreadExecutor2.execute(new modelRunnable(getActivity()));//问题在这！！！！！内存泄漏！！！
        mhandler = new MyHandler(getActivity());
        mhandler2 = new MyHandler2(getActivity());

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            ToastUtil.showShortMessage(weakReference.get(), "Bluetooth is not available");
            weakReference.get().finish();
        }
        try {
            File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/yourmusic");
            if (!file.exists())
                file.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mhandler.removeCallbacksAndMessages(null);
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        imuchartfragment = new imuChartFragment();
        imu2chartfragment = new imu2ChartFragment();
        imu3chartfragment = new imu3ChartFragment();
        thisfragment = this;
        onClickListener = new OnClickListeners() ;
        switchListener = new SwitchForSaveDataFileListener(getContext()) ;
        getFragmentManager().beginTransaction().add(R.id.sample_content_fragment, imuchartfragment, "chart").commit();
        getFragmentManager().beginTransaction().hide(imuchartfragment).commit();
        getFragmentManager().beginTransaction().add(R.id.sample_content_fragment, imu2chartfragment, "chart").commit();
        getFragmentManager().beginTransaction().hide(imu2chartfragment).commit();
        getFragmentManager().beginTransaction().add(R.id.sample_content_fragment, imu3chartfragment, "chart").commit();
        getFragmentManager().beginTransaction().hide(imu3chartfragment).commit();
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        player = view.findViewById(R.id.outer);
        autoScrollTextView = view.findViewById(R.id.textView);
        autoScrollTextView.initScrollTextView(getActivity().getWindowManager(),
                musicname[current_music - 1]);
        autoScrollTextView.starScroll();
        // 播放器
        media_image = view.findViewById(R.id.music_image);
        media_previous = view.findViewById(R.id.previous);
        media_next = view.findViewById(R.id.next);
        media_play = view.findViewById(R.id.play);
        media_pause = view.findViewById(R.id.pause);
        media_next = view.findViewById(R.id.next);
        // 地图
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMapView.setVisibility(View.INVISIBLE);
        mbaidumap = mMapView.getMap();

        // 播放器的点击事件
        media_previous.setOnClickListener(onClickListener);
        media_next.setOnClickListener(onClickListener);
        media_play.setOnClickListener(onClickListener);
        media_pause.setOnClickListener(onClickListener);
        // 设置个性化地图样式文件的路径和加载方式
        mMapView.setMapCustomStylePath("C:\\Users\\twx\\Desktop\\BTC\\android-BluetoothChat-master - 4\\Application\\src\\main\\assets\\customConfigdir\\map_style.sty");
        mbaidumap.setMyLocationEnabled(true);
        initLocation();
        mConversationView = (ListView) view.findViewById(R.id.in);
        mConversationView.setVisibility(View.INVISIBLE);

        LottieAnimationView lottieAnimationView = new LottieAnimationView(MyApplication.getContext());
//        System.out.println("lottie" + lottieAnimationView);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("3.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();


        backbutton = getActivity().findViewById(R.id.back_button);
        backbutton.setVisibility(View.INVISIBLE);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateAnimation translateAniHide = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                        0,//fromXValue表示开始的X轴位置
                        Animation.RELATIVE_TO_SELF,
                        0,//fromXValue表示结束的X轴位置
                        Animation.RELATIVE_TO_SELF,
                        0,//fromXValue表示开始的Y轴位置
                        Animation.RELATIVE_TO_SELF,
                        1);//fromXValue表示结束的Y轴位置
                translateAniHide.setRepeatMode(Animation.REVERSE);
                translateAniHide.setDuration(500);

                translateAniHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        backbutton.setVisibility(View.INVISIBLE);
                        ((MainActivity)getActivity()).showToastButton();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        map_show = false;
                        mMapView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        mMapView.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mMapView.startAnimation(translateAniHide);

            }
        });
        // 用于保存蓝牙数据
        switchForFileSaved = (Switch) view.findViewById(R.id.switch1);
        switchForFileSaved.setVisibility(View.INVISIBLE);
        // 按钮组
        bmb = (BoomMenuButton) getActivity().findViewById(R.id.bmb);
        bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_7_4);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_7_4);
        bmb.setBoomEnum(BoomEnum.HORIZONTAL_THROW_2);
        bmb.setNormalColor(Color.WHITE);
        bmb.setLongClickable(true);
        // 扫描蓝牙设备列表
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                    }
                });
        // 角速度图像
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        getFragmentManager().beginTransaction().hide(imu2chartfragment).commit();
                        getFragmentManager().beginTransaction().hide(imu3chartfragment).commit();
                        getFragmentManager().beginTransaction().hide(thisfragment);
                        getFragmentManager().beginTransaction().hide(thisfragment).addToBackStack(null).setCustomAnimations(
                                R.anim.slide_left_in, R.anim.slide_right_out).commit();
                        getFragmentManager().beginTransaction().show(imuchartfragment).addToBackStack(null).setCustomAnimations(R.anim.slide_right_in,
                                R.anim.slide_left_out).commit();
                        imuisShowing = true;
                    }
                });
        // 角度图像
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        getFragmentManager().beginTransaction().hide(imuchartfragment).commit();
                        getFragmentManager().beginTransaction().hide(imu3chartfragment).commit();
                        getFragmentManager().beginTransaction().hide(thisfragment).addToBackStack(null).commit();
                        getFragmentManager().beginTransaction().show(imu2chartfragment).addToBackStack(null).commit();
                    }
                });
        // 加速度图像
        TextOutsideCircleButton.Builder builder4 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        getFragmentManager().beginTransaction().hide(imuchartfragment).commit();
                        getFragmentManager().beginTransaction().hide(imu2chartfragment).commit();
                        getFragmentManager().beginTransaction().hide(thisfragment).addToBackStack(null).commit();
                        getFragmentManager().beginTransaction().show(imu3chartfragment).addToBackStack(null).commit();
                    }
                });
        // data button
        TextOutsideCircleButton.Builder builder5 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        if (message_show) {
                            message_show = false;
                            mConversationView.setVisibility(View.INVISIBLE);
                            switchForFileSaved.setVisibility(View.INVISIBLE);
                        } else {
                            message_show = true;
                            mConversationView.setVisibility(View.VISIBLE);
                            switchForFileSaved.setVisibility(View.VISIBLE);
                        }
                    }
                });
        // 地图
        TextOutsideCircleButton.Builder builder6 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ((MainActivity)getActivity()).hideToastButton();
                        if (map_show) {

                            TranslateAnimation translateAniHide = new TranslateAnimation(
                                    Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                                    0,//fromXValue表示开始的X轴位置
                                    Animation.RELATIVE_TO_SELF,
                                    0,//fromXValue表示结束的X轴位置
                                    Animation.RELATIVE_TO_SELF,
                                    0,//fromXValue表示开始的Y轴位置
                                    Animation.RELATIVE_TO_SELF,
                                    1);//fromXValue表示结束的Y轴位置
                            translateAniHide.setRepeatMode(Animation.REVERSE);
                            translateAniHide.setDuration(500);

                            translateAniHide.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    backbutton.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    map_show = false;
                                    mMapView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT, 1));
                                    mMapView.setVisibility(View.INVISIBLE);

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            mMapView.startAnimation(translateAniHide);

                        } else {
                            TranslateAnimation translateAniShow = new TranslateAnimation(
                                    Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                                    0,//fromXValue表示开始的X轴位置
                                    Animation.RELATIVE_TO_SELF,
                                    0,//fromXValue表示结束的X轴位置
                                    Animation.RELATIVE_TO_SELF,
                                    1,//fromXValue表示开始的Y轴位置
                                    Animation.RELATIVE_TO_SELF,
                                    0);//fromXValue表示结束的Y轴位置
                            translateAniShow.setRepeatMode(Animation.REVERSE);
                            translateAniShow.setDuration(500);
                            mMapView.startAnimation(translateAniShow);
                            map_show = true;
                            mMapView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT, 0));
                            mMapView.setVisibility(View.VISIBLE);
                            backbutton.setVisibility(View.VISIBLE);
                        }
                    }
                });
        // 音乐player
        TextOutsideCircleButton.Builder builder7 = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        if (player_show) {
                            player_show = false;
                            Animation animation = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_fade_out);
                            player.startAnimation(animation);
                            player.setVisibility(View.INVISIBLE);
                            DestroyMedia();
                        } else {
                            player_show = true;
                            player.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_fade_in);
                            player.startAnimation(animation);
                            initMedia();

                        }
                    }
                });
        // 赋予按钮颜色
        builder1.normalColorRes(R.color.violet);    // 蓝牙列表
        builder2.normalColorRes(R.color.orange);    // 角速度
        builder3.normalColorRes(R.color.lightcyan); // 加速度
        builder4.normalColorRes(R.color.indianred); //角度
        builder5.normalColorRes(R.color.ivory); // data
        builder6.normalColorRes(R.color.ivory); // map
        builder7.normalColorRes(R.color.ivory); // music
        bmb.addBuilder(builder1
                .normalImageRes(R.drawable.bluetooth).normalText("SCAN DEVICE"));
        bmb.addBuilder(builder2
                .normalImageRes(R.drawable.chart).normalText("角速度"));
        bmb.addBuilder(builder3
                .normalImageRes(R.drawable.chart).normalText("加速度"));
        bmb.addBuilder(builder4
                .normalImageRes(R.drawable.chart).normalText("角度"));
        bmb.addBuilder(builder5
                .normalImageRes(R.drawable.message).normalText("DATA"));
        bmb.addBuilder(builder6
                .normalImageRes(R.drawable.map).normalText("MAP"));
        bmb.addBuilder(builder7
                .normalImageRes(R.drawable.ic_baseline_play_circle_outline_24).normalText("MUSIC"));

    }

    private void initLocation() {
        //定位初始化
        mLocationClient = new LocationClient(MyApplication.getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isFirstLocation = true;

            }
        }).start();
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setOpenGps(true);
        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key

        // Initialize the send button with a listener that for click events

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mhandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        switchForFileSaved.setOnCheckedChangeListener(switchListener) ;
    }


    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            if (weakReference.get() != null)
                ToastUtil.showShortMessage(weakReference.get(), R.string.not_connected);
            return;

        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */


    private static void setStatus(int resId, Activity activity) {


        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private static void setStatus(CharSequence subTitle, Activity activity) {

        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private static ArrayList<String> analyze(String str) {
        byte[] readbuf = str.getBytes();
        int p1 = 0;
        int p2 = p1;
        flag = 0;
        ByteBuffer b1 = ByteBuffer.allocate(2000);
        ArrayList<String> arrayList = new ArrayList<>();
        if (readbuf != null && b1.remaining() > readbuf.length && readbuf.length > 0)
            b1.put(readbuf);
        b1.clear();
        while (b1.remaining() > 0 && flag < 1998 && b1.position() < str.length() - 1) {
            while (b1.remaining() > 0 && b1.position() < str.length() - 1) {
                b1.get();
                flag += 1;
                if (b1.position() >= str.length() - 1 || b1.remaining() <= 0)
                    break;
                else if (str.charAt(b1.position()) == 'D')
                    break;
            }
            p2 = b1.position();
            if (p2 >= p1)
                arrayList.add(str.substring(p1, p2 + 1));
            p1 = p2 + 1;
        }
        if (arrayList.isEmpty())
            return null;
        else
            return arrayList;
    }

    private static class modelRunnable implements Runnable {
        private Activity activity;
        modelRunnable(Activity activity) {
            this.activity = activity;
        }
        @Override
        public void run() {
            Looper.prepare();
            mhandler3 = new MyHandler2(this.activity);
            Looper.loop();
        }
    }

    private static class Mythread1 implements Runnable {
        private String receivestr;
        private final WeakReference<Activity> reference;

        public Mythread1(String s, Activity activity) {
            receivestr = s;
            reference = new WeakReference<Activity>(activity);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            ArrayList<String> mArrayList = analyze(receivestr);
            Mythread2 mythread2 = new Mythread2(receivestr, mArrayList);
            if (reference.get() != null)
                mythread2.run();
        }
    }

    private static class Mythread2 implements Runnable {
        private String receivestr;
        private ArrayList<String> mArrayList;
        private long startTime;
        private long endTime;

        public Mythread2(String s, ArrayList<String> ss) {
            receivestr = s;
            mArrayList = ss;
            startTime = System.currentTimeMillis();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            chart_update = (chart_update + 1) % 5;
            if (mArrayList != null && mArrayList.size() > 0) {
//                System.out.println(mArrayList.size());
                for (int i = 0; i < mArrayList.size(); i++) {
                    if (mArrayList.get(i) != null) {
                        // 正则表达式提取蓝牙数据
                        String regEx = "[a-zA-Z:\\s]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(mArrayList.get(i));
                        String s = m.replaceAll("").trim();
                        // TODO: 2021/8/2 gx,gy,gz 三个队列，当三个队列全满20的时候才发送数据给python，发送过后立刻清空 start
                        if (mArrayList.get(i).contains("GX")) {
                            queue_gx.offer(s);
                        } else if (mArrayList.get(i).contains("GY")) {
                            queue_gy.offer(s);
                        } else if (mArrayList.get(i).contains("GZ")) {
                            queue_gz.offer(s);
                        } else if (mArrayList.get(i).contains("BX")) {
                            queue_bx.offer(s);
                        } else if (mArrayList.get(i).contains("BY")) {
                            queue_by.offer(s);
                        } else if (mArrayList.get(i).contains("BZ")) {
                            queue_bz.offer(s);
                        }
//                        else if (mArrayList.get(i).contains("AX")) {
//                            queue_ax.offer(s);
//                        } else if (mArrayList.get(i).contains("AY")) {
//                            queue_ay.offer(s);
//                        } else if (mArrayList.get(i).contains("AZ")) {
//                            queue_az.offer(s);
//                        } else if (mArrayList.get(i).contains("NX")) {
//                            queue_nx.offer(s);
//                        } else if (mArrayList.get(i).contains("NY")) {
//                            queue_ny.offer(s);
//                        }
//                        else if (mArrayList.get(i).contains("NZ")) {
//                            queue_nz.offer(s);
//                        } else if (mArrayList.get(i).contains("QX")) {
//                            queue_qx.offer(s);
//                        }
//                        else if (mArrayList.get(i).contains("QY")) {
//                            queue_qy.offer(s);
//                        }
//                        else if (mArrayList.get(i).contains("QZ")) {
//                            queue_qz.offer(s);
//                        }else if (mArrayList.get(i).contains("QW")) {
//                            queue_qw.offer(s);
//                        }

                        // 更新图表
                        if (chart_update == 0) {
                            Message msg = new Message();
                            msg.what = MESSAGE_desperate;
                            Bundle bundle = new Bundle();
                            bundle.putString("output", mArrayList.get(i));
                            msg.setData(bundle);
                            mhandler2.sendMessage(msg);
                            EventBus.getDefault().post(mArrayList.get(i));
                        }
                    // 三个全满20，发送数据
                        if (queue_gx.size() == DATASIZE && queue_gy.size() == DATASIZE && queue_gz.size() == DATASIZE) {
                            endTime = System.currentTimeMillis();
//                            System.out.println(("data collected consumed Time :" + (endTime - startTime) + "ms"));
                            // 十六个通道
                            ArrayList<String> list_bx = new ArrayList<String>(queue_bx);
                            ArrayList<String> list_by = new ArrayList<String>(queue_by);
                            ArrayList<String> list_bz = new ArrayList<String>(queue_bz);
                            ArrayList<String> list_gx = new ArrayList<String>(queue_gx);
                            ArrayList<String> list_gy = new ArrayList<String>(queue_gy);
                            ArrayList<String> list_gz = new ArrayList<String>(queue_gz);
//
//                            ArrayList<String> list_az = new ArrayList<String>(queue_az);
//                            ArrayList<String> list_ax = new ArrayList<String>(queue_ax);
//                            ArrayList<String> list_ay = new ArrayList<String>(queue_ay);
//
//                            ArrayList<String> list_qz = new ArrayList<String>(queue_qz);
//                            ArrayList<String> list_qx = new ArrayList<String>(queue_qx);
//                            ArrayList<String> list_qy = new ArrayList<String>(queue_qy);
//                            ArrayList<String> list_qw = new ArrayList<String>(queue_qw);
//
//                            ArrayList<String> list_nx = new ArrayList<String>(queue_nx);
//                            ArrayList<String> list_ny = new ArrayList<String>(queue_ny);
//                            ArrayList<String> list_nz = new ArrayList<String>(queue_nz);

                            String[] array_bx = list_bx.toArray(new String[DATASIZE]);
                            String[] array_by = list_by.toArray(new String[DATASIZE]);
                            String[] array_bz = list_bz.toArray(new String[DATASIZE]);
                            String[] array_gx = list_gx.toArray(new String[DATASIZE]);
                            String[] array_gy = list_gy.toArray(new String[DATASIZE]);
                            String[] array_gz = list_gz.toArray(new String[DATASIZE]);
//
//                            String[] array_ax = list_ax.toArray(new String[DATASIZE]);
//                            String[] array_ay = list_ay.toArray(new String[DATASIZE]);
//                            String[] array_az = list_az.toArray(new String[DATASIZE]);
//                            String[] array_nx = list_nx.toArray(new String[DATASIZE]);
//                            String[] array_ny = list_ny.toArray(new String[DATASIZE]);
//                            String[] array_nz = list_nz.toArray(new String[DATASIZE]);
//
//                            String[] array_qz = list_qz.toArray(new String[DATASIZE]);
//                            String[] array_qx = list_qx.toArray(new String[DATASIZE]);
//                            String[] array_qy = list_qy.toArray(new String[DATASIZE]);
//                            String[] array_qw = list_qw.toArray(new String[DATASIZE]);

//                            System.out.println("god" + Thread.currentThread() + array_gy[0] + "-" + queue_gy.peek() + "-" + i);
                            Message mm = new Message();
                            mm.what = MESSAGE_runmodel;
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("bx", array_bx);
                            bundle.putStringArray("by", array_by);
                            bundle.putStringArray("bz", array_bz);
                            bundle.putStringArray("gx", array_gx);
                            bundle.putStringArray("gy", array_gy);
                            bundle.putStringArray("gz", array_gz);
//
//                            bundle.putStringArray("ax", array_ax);
//                            bundle.putStringArray("ay", array_ay);
//                            bundle.putStringArray("az", array_az);
//
//                            bundle.putStringArray("nx", array_nx);
//                            bundle.putStringArray("ny", array_ny);
//                            bundle.putStringArray("nz", array_nz);
//
//                            bundle.putStringArray("qx", array_qx);
//                            bundle.putStringArray("qy", array_qy);
//                            bundle.putStringArray("qz", array_qz);
//                            bundle.putStringArray("qw", array_qw);

                            bundle.putInt("int", i);
                            bundle.putLong("time", System.currentTimeMillis());
                            mm.setData(bundle);
                            if (mhandler3.getLooper().getQueue().isIdle()) {
//                                System.out.println("郭fasong" + Thread.currentThread() + i);
                                mhandler3.sendMessage(mm);
                            }
                            queue_gx.clear();
                            queue_gy.clear();
                            queue_gz.clear();
                            queue_bx.clear();
                            queue_by.clear();
                            queue_bz.clear();
//
//                            queue_ax.clear();
//                            queue_ay.clear();
//                            queue_az.clear();
//                            queue_nx.clear();
//                            queue_ny.clear();
//                            queue_nz.clear();
//                            queue_qz.clear();
//                            queue_qx.clear();
//                            queue_qy.clear();
//                            queue_qw.clear();
                            // TODO: 2021/8/4 end
                        }
                    }
                }
            }
        }
    }

    private static class MyHandler2 extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler2(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_desperate:
                        mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + msg.getData().getString("output"));
                        break;
                    case MESSAGE_runmodel:
                        // 提取对应的数据
//                        String[] array_bx = msg.getData().getStringArray("bx");
//                        String[] array_by = msg.getData().getStringArray("by");
//                        String[] array_bz = msg.getData().getStringArray("bz");
                        String[] array_gx = msg.getData().getStringArray("gx");
                        String[] array_gy = msg.getData().getStringArray("gy");
                        String[] array_gz = msg.getData().getStringArray("gz");
//
//                        String[] array_ax = msg.getData().getStringArray("ax");
//                        String[] array_ay = msg.getData().getStringArray("ay");
//                        String[] array_az = msg.getData().getStringArray("az");
//
//                        String[] array_nx = msg.getData().getStringArray("nx");
//                        String[] array_ny = msg.getData().getStringArray("ny");
//                        String[] array_nz = msg.getData().getStringArray("nz");
//
//                        String[] array_qz = msg.getData().getStringArray("qz");
//                        String[] array_qx = msg.getData().getStringArray("qx");
//                        String[] array_qy = msg.getData().getStringArray("qy");
//                        String[] array_qw = msg.getData().getStringArray("qw");
                        int number = msg.getData().getInt("int");
                        long time = msg.getData().getLong("time");
                        singleThreadExecutor3.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // TODO: 2021/8/2 对数据发送给Python端进行处理 start
                                    long start = System.currentTimeMillis() ;
                                    PythonExecutor pyExecutor = PythonExecutor.getExecutor() ;
                                        // 返回result结果
                                    int result = pyExecutor.execute(array_gx,array_gy,array_gz) ;
                                    if (result!=4){
                                        Log.d( "result :"+result," consumed Time: "+(""+(System.currentTimeMillis()-start))) ;
                                    }
                                    // TODO: 2021/8/2 end
                                    // 根据result判断行为
                                    if (!(queue_result.contains(result) || result == 4)) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (result) {
                                                    case 0:
                                                        if (isPlayer_show) {
                                                            if (media_play.getVisibility() == View.VISIBLE)
                                                                media_play.callOnClick();
                                                            else
                                                                media_pause.callOnClick();
                                                        }
                                                        if (!map_show)
                                                             ToastUtil.showShortMessage(activity,"head_down");
                                                        else {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Gesture.down_moving();
                                                                }
                                                            }).start();
                                                        }

                                                        break;
                                                    case 1:
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"head_up");
                                                        else {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Gesture.up_moving();
                                                                }
                                                            }).start();
                                                        }
                                                        break;
                                                    case 2:
                                                        if (isPlayer_show) {
                                                            try {
                                                                String keyCommand = "input keyevent " + KeyEvent.KEYCODE_VOLUME_DOWN;
                                                                Runtime runtime = Runtime.getRuntime();
                                                                Process proc = runtime.exec(keyCommand);

                                                                Log.i(TAG, "OnVolumeAddKey: 音量减按下");
                                                            } catch (IOException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"head_turn_left");
                                                        else {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Gesture.right_moving();
                                                                }
                                                            }).start();
                                                        }
                                                        break;
                                                    case 3:
                                                        if (isPlayer_show) {
                                                            String keyCommand = "input keyevent " + KeyEvent.KEYCODE_VOLUME_UP;
                                                            Runtime runtime = Runtime.getRuntime();
                                                            try {
                                                                Process proc = runtime.exec(keyCommand);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }

                                                            Log.i(TAG, "OnVolumeAddKey: 音量加按下");
                                                        }
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"head_turn_right");
                                                        else {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Gesture.left_moving();
                                                                }
                                                            }).start();
                                                        }
                                                        break;
                                                    case 4:
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"still");

                                                        break;
                                                    case 5:
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"body_turn_left");

                                                        break;
                                                    case 6:
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"body_turn_right");

                                                        break;
                                                    case 7:
                                                        if (isPlayer_show) {
                                                            media_previous.callOnClick();
                                                        }
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"head_lean_left");

                                                        else {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Gesture.bigger();
                                                                }
                                                            }).start();
                                                        }
                                                        break;
                                                    case 8:
                                                        if (isPlayer_show) {
                                                            media_next.callOnClick();
                                                        }
                                                        if (!map_show)
                                                            ToastUtil.showShortMessage(activity,"head_lean_right");

                                                        else {
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Gesture.smaller();
                                                                }
                                                            }).start();
                                                        }
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                    if (queue_result.size() < 5)
                                        queue_result.offer(result);
                                    else {
                                        queue_result.poll();
                                        queue_result.offer(result);
                                    }
                                } catch (PyException e) {
                                    System.out.println("郭异常");
                                }
                            }
                        });
                        break;
                }

            }
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        public MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mWeakReference.get();
            if (activity != null) {
                // TODO: 2021/8/2 test
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothChatService.STATE_CONNECTED:
                                //setStatus(R.string.title_connected_to+mConnectedDeviceName,mWeakReference.get());
                                setStatus("Successfully connected to " + mConnectedDeviceName, mWeakReference.get());
                                mConversationArrayAdapter.clear();
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                setStatus(R.string.title_connecting, mWeakReference.get());

                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                setStatus(R.string.title_not_connected, mWeakReference.get());
                                break;
                        }
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        mConversationArrayAdapter.add("Me:  " + writeMessage);
                        break;


                    case Constants.MESSAGE_READ:
                        Bundle data = msg.getData();
                        send++;
                        String recieveStr = new String(data.getString("BTdata"));
                        if (send >= 0 && recieveStr != null) {
                            Mythread1 mythread1 = new Mythread1(recieveStr, activity);
                            singleThreadExecutor1.execute(mythread1);
                        }
                        if (send >= 5)
                            send = 0;
                        if (savingflag) {
                            singleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // notice1
                                        savaFileToSD(filename, recieveStr);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                        if (mConversationArrayAdapter.getCount() > 1000)
                            mConversationArrayAdapter.clear();

                        break;
                    case Constants.MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                        if (activity != null) {
                            ToastUtil.showShortMessage(activity,"Connected to"+ mConnectedDeviceName);
                        }
                        break;
                    case Constants.MESSAGE_TOAST:
                        if (null != activity) {
                            ToastUtil.showShortMessage(activity,msg.getData().getString(Constants.TOAST));
                        }
                        break;
                }

            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    ToastUtil.showShortMessage(weakReference.get(), R.string.bt_not_enabled_leaving);
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //MenuItemCompat.setShowAsAction(menu.findItem(R.id.secure_connect_scan), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }
    public static void writeFile(Context context, String fileName, String data) {
        FileOutputStream outputStream;
        BufferedWriter bufferedWriter = null;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(data + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null)
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mbaidumap.setMyLocationData(locData);
            if (isFirstLocation) {
                //获取经纬度
//                System.out.println("郭" + location.getLatitude() + " " + location.getLongitude());
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                mbaidumap.animateMapStatus(status);//动画的方式到中间
                isFirstLocation = false;
                WindowManager wm = getActivity().getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();
//                System.out.println("郭" + width + " " + height);
            }
        }
    }

    public static void savaFileToSD(String filename, String filecontent) throws Exception {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + DATA_SAVED_DIR_NAME + "/"+ filename + ".txt";
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename, true);
            filecontent += "\n";
            output.write(filecontent.getBytes());
            //将String字符串以字节流的形式写入到输出流中
            output.close();
            //关闭输出流
        } else
            Toast.makeText(MyApplication.getContext(), "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
    }
    // 接口回调实现注册点击事件
    class OnClickListeners implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.previous: {
                    if (current_music > 1) {
                        current_music--;
                        autoScrollTextView.initScrollTextView(getActivity().getWindowManager(),
                                musicname[current_music - 1]);
                        autoScrollTextView.starScroll();
                        mediaPlayer.reset();
                        String f = null;
                        try {
                            f = Environment.getExternalStorageDirectory().getCanonicalPath() + "/yourmusic/music_" + String.valueOf(current_music) + ".mp3";
                            File mfile = new File(f);
                            mediaPlayer.setDataSource(mfile.getPath());
                            mediaPlayer.prepare();
                            if (!mediaPlayer.isPlaying()) {
                                media_play.setVisibility(View.INVISIBLE);
                                media_pause.setVisibility(View.VISIBLE);
                            }
                            mediaPlayer.setVolume(1f, 1f);
                            mediaPlayer.start();
                            media_image.setBackgroundResource(Constants.mdrawable[current_music - 1]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case R.id.next: {
                    if (current_music < 15) {
                        current_music++;
                        autoScrollTextView.initScrollTextView(getActivity().getWindowManager(),
                                musicname[current_music - 1]);
                        autoScrollTextView.starScroll();
                        mediaPlayer.reset();
                        String f = null;
                        try {
                            f = Environment.getExternalStorageDirectory().getCanonicalPath() + "/yourmusic/music_" + String.valueOf(current_music) + ".mp3";
                            File mfile = new File(f);
                            mediaPlayer.setDataSource(mfile.getPath());
                            mediaPlayer.prepare();
                            if (!mediaPlayer.isPlaying()) {
                                media_play.setVisibility(View.INVISIBLE);
                                media_pause.setVisibility(View.VISIBLE);
                            }
                            mediaPlayer.setVolume(1f, 1f);
                            mediaPlayer.start();
                            media_image.setBackgroundResource(Constants.mdrawable[current_music - 1]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case R.id.play:{
                    if (media_play != null && !isplaying) {
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                        isplaying = true;
                        media_play.setVisibility(View.INVISIBLE);
                        media_pause.setVisibility(View.VISIBLE);
                        if (mediaPlayer.isPlaying())
                            Toast.makeText(MyApplication.getContext(), "开始播放", Toast.LENGTH_SHORT).show();
                    } else if (media_play != null) {
                        mediaPlayer.start();
                        inandout.volumeGradient(mediaPlayer, 0f, 1f, new inandout.DoneCallBack() {
                            @Override
                            public void onComplete() {
                                return;
                            }
                        });

                        media_play.setVisibility(View.INVISIBLE);
                        media_pause.setVisibility(View.VISIBLE);
                    }
                    break ;
                }
                case R.id.pause:{
                    inandout.volumeGradient(mediaPlayer, 1f, 0f, new inandout.DoneCallBack() {
                        @Override
                        public void onComplete() {
                            mediaPlayer.pause();
                        }
                    });
                    media_play.setVisibility(View.VISIBLE);
                    media_pause.setVisibility(View.INVISIBLE);
                    break ;
                }
                default:
                    break ;
            }
        }
    }
}
