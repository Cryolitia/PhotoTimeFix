package tech.lincaiqi.PhotoTimeFix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private TextView locateTv;
    private EditText start;
    private EditText end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBtn = (Button) findViewById(R.id.startbutton);
        Button chooseBtn = (Button) findViewById(R.id.chooseButton);
        locateTv = (TextView) findViewById(R.id.locateText);
        start = (EditText) findViewById(R.id.start);
        end = (EditText) findViewById(R.id.end);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "由于系统限制(其实是我懒)，请选择文件夹内任意一张图片", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 0);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int startnum = Integer.valueOf(start.getText().toString());
                final int endnum = Integer.valueOf(end.getText().toString());
                File file = new File(locateTv.getText().toString());
                //Log.d("EditText", locateTv.getText().toString());
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, "路径不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                final File[] files = file.listFiles();
                final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                pd.setMax(files.length);
                pd.setTitle("进度");
                pd.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int i = 0;


                        for (File f : files) {
                            Log.d("File:", f.getName());
                            i++;

                            if (i >= startnum && (endnum == 0 || i <= endnum)) {
                                String time = f.getName();
                                String regEx = "[^0-9]";
                                Pattern pa = Pattern.compile(regEx);
                                Matcher m = pa.matcher(time);
                                time = m.replaceAll("").trim();
                                if (time.contains("20") && time.substring(time.indexOf("20")).length() >= 12) {
                                    try {
                                        String targetTime =  time.substring(time.indexOf("20"), time.indexOf("20") + 12);
                                        long targetTimeLongType = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).parse(targetTime).getTime();
                                        if (f.setLastModified(targetTimeLongType)) {
                                            Log.d("succese", new Date(f.lastModified()).toString());
                                        }else {
                                            Log.d("fail", String.valueOf(targetTimeLongType));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    /*try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                }

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.incrementProgressBy(1);
                                    }
                                });
                            }

                        }


                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                            }
                        });

                        Looper.prepare();
                        Toast.makeText(MainActivity.this, "完成！", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }

                }).start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("关于")
                .setMessage("这是一个简单的小程序用来修复手机中的照片时间错误\n使用时请尽量不要进行其他操作以免机器卡死\n在Gayhub上开源：https://github.com/singleNeuron/PhotoTimeFixforAndroid\n酷安ID：@神经元")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        dialog.show();
        return true;
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
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "选择出错，请手动填写路径并联系开发者", Toast.LENGTH_LONG).show();
        }
    }

}
