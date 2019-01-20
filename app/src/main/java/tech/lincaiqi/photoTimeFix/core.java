package tech.lincaiqi.photoTimeFix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
/*
public class core {

    private SharedPreferences.Editor editor;
    private boolean support = true;
    private TextView locateTv;
    private EditText start;
    private EditText end;
    private RadioGroup radioGroup;
    private EditText editFormat;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getBoolean("ifFirst", true)) {
            showAbout();
            editor.putBoolean("ifFirst", false);
            editor.apply();
        }

        Button startBtn = findViewById(R.id.startbutton);
        Button chooseBtn = findViewById(R.id.chooseButton);
        locateTv = findViewById(R.id.locateText);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        radioGroup = findViewById(R.id.radioGroup);
        editFormat = findViewById(R.id.editFormat);

        locateTv.setText(preferences.getString("locate", Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera"));
        radioGroup.check(preferences.getInt("mode", R.id.radioButton));

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            editor.putInt("mode", i);
            editor.apply();
        });

        chooseBtn.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "由于系统限制(其实是我懒)，请选择文件夹内任意一张图片", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 0);
        });

        startBtn.setOnClickListener(view -> {
            final int startnum = Integer.valueOf(start.getText().toString());
            final int endnum = Integer.valueOf(end.getText().toString());
            File file = new File(locateTv.getText().toString());
            //Log.d("EditText", locateTv.getText().toString());
            if (!file.exists()) {
                Toast.makeText(MainActivity.this, "路径不存在", Toast.LENGTH_LONG).show();
                return;
            }
            editor.putString("locate", String.valueOf(locateTv.getText()));
            editor.apply();
            final File[] files = file.listFiles();
            final ProgressDialog pd = new ProgressDialog(MainActivity.this);

            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setMax(files.length);
            pd.setTitle("进度");
            pd.show();

            new Thread(() -> {

                int i = 0;

                Log.d("button", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                Log.d("button", String.valueOf(R.id.radioButton2));
                DataOutputStream os = null;
                Process suProcess;

                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton2) {

                    boolean retValue;

                    try {
                        suProcess = Runtime.getRuntime().exec("su");

                        os = new DataOutputStream(suProcess.getOutputStream());
                        DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

                        // Getting the id of the current user to check if this is root
                        os.writeBytes("id\n");
                        os.flush();

                        String currUid = osRes.readLine();
                        boolean exitSu;
                        if (null == currUid) {
                            retValue = false;
                            //exitSu = false;
                            Log.d("ROOT", "Can't get root access or denied by user");
                        } else if (currUid.contains("uid=0")) {
                            retValue = true;
                            //exitSu = true;
                            Log.d("ROOT", "Root access granted");
                        } else {
                            retValue = false;
                            //exitSu = true;
                            Log.d("ROOT", "Root access rejected: " + currUid);
                        }

                    } catch (Exception e) {

                        retValue = false;
                        Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
                        e.printStackTrace();
                    }

                    if (!retValue) {
                        MainActivity.this.runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "请检查root权限", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        });
                        /*try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                }

                String format = editFormat.getText().toString().equals("") ? "yyyyMMddHHmm" : editFormat.getText().toString();
                long targetTimeLongType;

                for (File f : files) {
                    Log.d("File:", f.getName());
                    i++;

                    if (i >= startnum && (endnum == 0 || i <= endnum)) {
                        String time = f.getName();
                        time = Pattern.compile("[^0-9]").matcher(time).replaceAll("").trim();
                        if (time.contains("20") && time.substring(time.indexOf("20")).length() >= 12) {
                            String targetTime = time.substring(time.indexOf("20"), time.indexOf("20") + 12);
                            if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton) {
                                try {
                                    targetTimeLongType = format.equals("yyyyMMddHHmm") ? new SimpleDateFormat(format, Locale.getDefault()).parse(targetTime).getTime() : new SimpleDateFormat(format, Locale.getDefault()).parse(f.getName()).getTime();
                                    if (f.setLastModified(targetTimeLongType)) {
                                        Log.d("succese", new Date(f.lastModified()).toString());
                                    } else {
                                        Log.d("fail", String.valueOf(targetTimeLongType));
                                        support = false;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    String command = "touch -t " + targetTime + " " + f.getAbsolutePath();
                                    Log.d("command",command);
                                    assert os != null;
                                    os.writeBytes(command + "\n");
                                    os.flush();
                                    //suProcess.waitFor();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            /*try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        MainActivity.this.runOnUiThread(() -> pd.incrementProgressBy(1));
                    } else if (i > endnum) break;

                }

                if (os!=null) {
                    try {
                        os.writeBytes("exit\n");
                        //os.flush();
                        //os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                MainActivity.this.runOnUiThread(() -> {
                    pd.dismiss();
                    if (!support) {
                        Toast.makeText(MainActivity.this, "您的系统极有可能不支持此操作，请更换操作模式", Toast.LENGTH_LONG).show();
                    }
                }
                );

                Looper.prepare();
                Toast.makeText(MainActivity.this, "完成！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }).start();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showAbout();
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.about, null);
        WebView webview = view.findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("file:///android_asset/about.html");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(this, "openGit");
        builder.setView(view);
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    @JavascriptInterface
    public  void openUrl(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark))
                .setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this,Uri.parse(url));
    }

    //作者：花生酱啊
    //来源：CSDN
    //原文：https://blog.csdn.net/u011286957/article/details/80824235
    //版权声明：本文为博主原创文章，转载请附上博文链接！
    @JavascriptInterface
    public void openCoolapk(String user) {
        Intent intent = new Intent();
        try {
            //intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse("coolmarket://u/"+user));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "未安装酷安", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 0) {
                Uri originalUri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                if (originalUri != null) {
                    cursor = getContentResolver().query(originalUri, proj, null, null, null);
                }
                if (cursor != null) {
                    int column_index;
                    column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    path = path.substring(0, path.lastIndexOf("/"));
                    locateTv.setText(path);
                    cursor.close();
                    editor.putString("locate", path);
                    editor.apply();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "选择出错，请手动填写路径并联系开发者", Toast.LENGTH_LONG).show();
        }
    }

}*/
