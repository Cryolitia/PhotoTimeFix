package tech.lincaiqi.phototimefix;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.topjohnwu.superuser.Shell;
import tech.lincaiqi.phototimefix.utils.ExifUtilKt;
import tech.lincaiqi.phototimefix.utils.ProcesserKt;

import java.io.File;
import java.util.Date;

public class Core {

    private final boolean support = true;

    public void process(Context context, int startnum, int endnum, String fileString, boolean radio, Activity activity, String format, String selectDate, int delay, Boolean exif) {

        File file = new File(fileString);
        if (!file.exists()) {
            Toast.makeText(context, R.string.fileNotExistence, Toast.LENGTH_LONG).show();
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
        pd.setTitle(context.getString(R.string.progress));
        pd.show();

        new Thread(() -> {

            int i = 0;

            Log.d("button", String.valueOf(radio));

            if (!radio) {
                Shell.Config.setFlags(Shell.FLAG_USE_MAGISK_BUSYBOX);
                //Shell.Config.addInitializers(BusyBoxInstaller.class);
                if (!Shell.rootAccess()) {
                    activity.runOnUiThread(() -> Toast.makeText(context, R.string.checkRoot, Toast.LENGTH_LONG).show());
                    pd.dismiss();
                    return;
                }
            }
            for (File f : files) {
                Log.d("File:", f.getName());
                i++;

                if (i >= startnum && (endnum == 0 || i <= endnum)) {
                    Date date;
                    if (exif) {
                        date = ExifUtilKt.getExitData(f);
                    } else {
                        date = ProcesserKt.getTimeByFileName(f.getName());
                    }
                    if (date != null) {
                        if (radio) {
                            ProcesserKt.setTimeStampByJava(date, f);
                        } else {
                            ProcesserKt.setTimeStampByTouch(date, f);
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

            activity.runOnUiThread(() -> {
                        pd.dismiss();
                        if (!support) {
                            Toast.makeText(context, R.string.systemNotSupport, Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(context, R.string.finish, Toast.LENGTH_SHORT).show();
                    }
            );

        }).start();
    }
}