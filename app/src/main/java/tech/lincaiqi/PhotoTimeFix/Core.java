package tech.lincaiqi.PhotoTimeFix;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Core {

    private boolean support = true;

    public void process(Context context, int startnum, int endnum, String fileString, boolean radio, Activity activity, String format, String selectDate, int delay) {
        File file = new File(fileString);
        //Log.d("EditText", locateTv.getText().toString());
        if (!file.exists()) {
            Toast.makeText(context, "路径或文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        final File[] files;
        if (file.isDirectory()) {
            files = file.listFiles();
        } else files = new File[]{file};
        final ProgressDialog pd = new ProgressDialog(context);

        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMax(files.length);
        pd.setTitle("进度");
        pd.show();

        new Thread(() -> {

            int i = 0;

            Log.d("button", String.valueOf(radio));
            DataOutputStream os = null;
            Process suProcess;

            if (!radio) {

                boolean retValue;

                Looper.prepare();
                Toast.makeText(context, "正在获取ROOT权限……", Toast.LENGTH_LONG).show();
                Looper.loop();

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
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, "请检查root权限", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    });
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

            }

            long targetTimeLongType;

            for (File f : files) {
                Log.d("File:", f.getName());
                i++;

                if (i >= startnum && (endnum == 0 || i <= endnum)) {
                    String time = selectDate.equals("") ? f.getName() : selectDate;
                    time = Pattern.compile("[^0-9]").matcher(time).replaceAll("").trim();
                    if (time.contains("20") && time.substring(time.indexOf("20")).length() >= 12) {
                        String targetTime = time.substring(time.indexOf("20"), time.indexOf("20") + 12);
                        if (radio) {
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
                                Log.d("command", command);
                                os.writeBytes(command + "\n");
                                os.flush();
                                //suProcess.waitFor();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    activity.runOnUiThread(() -> pd.incrementProgressBy(1));
                } else if (i > endnum) break;

            }

            if (os != null) {
                try {
                    os.writeBytes("exit\n");
                    //os.flush();
                    //os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            activity.runOnUiThread(() -> {
                        pd.dismiss();
                        if (!support) {
                            Toast.makeText(context, "您的系统极有可能不支持此操作，请更换操作模式", Toast.LENGTH_LONG).show();
                        }
                    }
            );

            Looper.prepare();
            Toast.makeText(context, "完成！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }).start();
    }
}