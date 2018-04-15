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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private Button startBtn;
    private Button chooseBtn;
    private TextView locateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.startbutton);
        chooseBtn = (Button) findViewById(R.id.chooseButton);
        locateTv = (TextView) findViewById(R.id.locateText);

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
                File file = new File(locateTv.getText().toString());
                Log.d("EditText", locateTv.getText().toString());
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, "路径不存在", Toast.LENGTH_LONG).show();
                    return;
                }
                final ArrayList<String> list = new ArrayList<String>();
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

                        for (File f : files) {
                            Log.d("File:", f.getName());
                            list.add(f.getName());

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.incrementProgressBy(1);
                                }
                            });

                        }

                        Collections.sort(list);

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                            }
                        });

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
                .setMessage("测试")
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
