package tech.lincaiqi.PhotoTimeFix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private Button startBtn;
    private Button chooseBtn;
    private TextView locateTv;
    private EditText start;
    private EditText end;
    private CheckBox checkBox;

    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.startbutton);
        chooseBtn = (Button) findViewById(R.id.chooseButton);
        locateTv = (TextView) findViewById(R.id.locateText);
        start = (EditText) findViewById(R.id.start);
        end = (EditText) findViewById(R.id.end);

        /*try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }*/




        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "由于系统限制(好吧是我懒)，请选择文件夹内任意一张图片", Toast.LENGTH_LONG).show();
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
                final boolean check = checkBox.isChecked();
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

                        try {
                            Process process = Runtime.getRuntime().exec("su");

                            for (File f : files) {
                                Log.d("File:", f.getName());
                                i++;

                                if (i >= startnum && (endnum == 0 || i <= endnum)) {
                                    String time = f.getName();
                                    String regEx = "[^0-9]";
                                    Pattern pa = Pattern.compile(regEx);
                                    Matcher m = pa.matcher(time);
                                    time = m.replaceAll("").trim();
                                    String command;
                                    if (time.indexOf("20") != -1 && time.substring(time.indexOf("20")).length() >= 12) {
                                        command = "touch -t " + time.substring(time.indexOf("20"), time.indexOf("20") + 12) + " " + f.getAbsolutePath();
                                        Log.d("shell", command);

                                        //Runtime.getRuntime().exec(new String[]{"su","-c", command});


                                        DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                                        dataOutputStream.writeBytes(command + "\n");
                                        dataOutputStream.flush();


                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.incrementProgressBy(1);
                                        }
                                    });
                                }

                            }




                        /*if (check == true) {
                            try {
                                Runtime.getRuntime().exec(new String[]{"su","-c", "am force-stop com.android.systemui"});
                                Runtime.getRuntime().exec(new String[]{"su","-c", "am force-stop tech.lincaiqi.PhotoTimeFix"});
                                Runtime.getRuntime().exec(new String[]{"su","-c", "am start -n tech.lincaiqi.PhotoTimeFix/tech.lincaiqi.PhotoTimeFix.MainActivity"});
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            dataOutputStream.close();
                            dataInputStream.close();
                            process.waitFor();
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/


                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                }
                            });

                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "完成！", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
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
                        return;
                    }
                }).create();
        dialog.show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver resolver = getContentResolver();
        if (requestCode == 0) {
            Uri originalUri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            path = path.substring(0, path.lastIndexOf("/"));
            locateTv.setText(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
