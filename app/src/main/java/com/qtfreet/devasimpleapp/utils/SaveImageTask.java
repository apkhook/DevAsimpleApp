package com.qtfreet.devasimpleapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.qtfreet.devasimpleapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hehe on 2016/3/7.
 */
public class SaveImageTask extends AsyncTask<String, Void, File> {

    private Context context;
    private String mtime;

    public SaveImageTask(Context context, String time) {
        this.context = context;
        this.mtime = time;
    }

    @Override
    protected File doInBackground(String... params) {
        String url = params[0]; // should be easy to extend to share multiple images at once
        try {
            return Glide
                    .with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                    ;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(File pic) {
        if (!Utils.isSdCardAviable()) {
            Toast.makeText(context, R.string.sd_error, Toast.LENGTH_SHORT).show();
            return;
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            int bytesum = 0;
            int byteread = 0;

            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/PicSave");
            if (!path.exists() && !path.isDirectory()) {
                path.mkdir();
            }
            File newfile = new File(path.getPath(), mtime + ".jpg");

            if (pic.exists()) { //文件存在时
                is = new FileInputStream(pic); //读入原文件
                fos = new FileOutputStream(newfile);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fos.write(buffer, 0, byteread);
                }
                fos.close();
                is.close();
                Toast.makeText(context, R.string.save_succ, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.save_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
