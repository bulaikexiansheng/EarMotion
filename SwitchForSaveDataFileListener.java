package com.example.android.PublicUIListeners;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.android.bluetoothchat.BluetoothChatFragment;

import java.io.File;
import java.util.Date;

import static com.example.android.bluetoothchat.BluetoothChatFragment.savaFileToSD;

public class SwitchForSaveDataFileListener implements CompoundButton.OnCheckedChangeListener {
    public Context context ;

    public SwitchForSaveDataFileListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean state) {
        // state 为true 开 ，开始写数据
        // state 为false 闭 ，保存数据
        if (state) {
            File dirFiles = new File(Environment.getExternalStorageDirectory(), BluetoothChatFragment.DATA_SAVED_DIR_NAME);
            System.out.println(dirFiles.getAbsolutePath());
            dirFiles = new File(dirFiles.getAbsolutePath());
            if (!dirFiles.exists()) {
                dirFiles.mkdirs();
                System.out.println("NO");
            }
            BluetoothChatFragment.filename = "" + dirFiles.list().length + ".txt";
            BluetoothChatFragment.savingflag = true;
            try {
                savaFileToSD(BluetoothChatFragment.filename, "\n" + new Date().toLocaleString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (context != null) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            BluetoothChatFragment.savingflag = false;
            rename();
        }
    }
    // 更改文件的名字
    public void rename() {
        File dirFiles = new File(Environment.getExternalStorageDirectory(), BluetoothChatFragment.DATA_SAVED_DIR_NAME);
        System.out.println(dirFiles.getAbsolutePath());
        File newfile = new File( dirFiles.getAbsolutePath() + "/" + BluetoothChatFragment.filename + ".txt");

        final EditText editText = new EditText(context);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(context);
        inputDialog.setTitle("请输入文件名").setView(editText);
        if (dirFiles.list() != null)
            editText.setText("" + (dirFiles.list().length - 1));
        else
            editText.setText("" + 0);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothChatFragment.filename = editText.getText().toString();
                        newfile.renameTo(new File(dirFiles.getAbsolutePath() + "/" + BluetoothChatFragment.filename + ".txt"));
                        try {
                            savaFileToSD(BluetoothChatFragment.filename, "\n" + new Date().toLocaleString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }
    public void getName() {
        final EditText editText = new EditText(context);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(context);
        inputDialog.setTitle("请输入文件名").setView(editText);
        File dirFiles = new File(Environment.getExternalStorageDirectory(), BluetoothChatFragment.DATA_SAVED_DIR_NAME       );
        System.out.println(dirFiles.getAbsolutePath());
        dirFiles = new File(dirFiles.getAbsolutePath());
        if (!dirFiles.exists()) {
            dirFiles.mkdirs();
        }
        if (dirFiles.list() != null)
            editText.setText("" + (dirFiles.list().length - 1));
        else
            editText.setText("" + 0);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothChatFragment.savingflag = false;
                        BluetoothChatFragment.filename = editText.getText().toString();
                        try {
                            savaFileToSD(BluetoothChatFragment.filename, "\n" + new Date().toLocaleString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }
}
