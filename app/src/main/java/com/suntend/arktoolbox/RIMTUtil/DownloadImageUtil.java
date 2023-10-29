package com.suntend.arktoolbox.RIMTUtil;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Random;

/**
 * 图片下载的工具类
 */
public class DownloadImageUtil {
    public static void downPic(String url,DownFinishListener downFinishListener){
        //获取目录
        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath + File.separator + "Pictures" +
                File.separator + "RIMT");
        if(!file.exists()){
            file.mkdir();
        }
        loadPic(file.getPath(),url,downFinishListener);
    }

    private static void loadPic(final String filePath, final String url, final DownFinishListener downFinishListener) {
        new AsyncTask<Void,Void,String>(){
            String fileName;
            InputStream is;
            OutputStream out;

            @Override
            protected String doInBackground(Void... voids) {
                // 下载文件的名称
                String[] split = url.split("/");
                String newString = split[split.length - 1];
                try{
                    fileName = URLDecoder.decode(newString);
                } catch (Exception e) {
                    fileName = newString;
                }

                // 创建目标文件,不是文件夹
                File picFile = new File(filePath + File.separator + fileName);
                if(picFile.exists()){
                    return picFile.getPath();
                }

                try {
                    URL picUrl = new URL(url);
                    //通过图片的链接打开输入流
                    is = picUrl.openStream();
                    if(is==null){
                        return null;
                    }
                    out = new FileOutputStream(picFile);
                    byte[] b=new byte[1024];
                    int end ;
                    while ((end=is.read(b))!=-1){
                        out.write(b,0,end);
                    }

                    if(is!=null){
                        is.close();
                    }
                    if(out!=null){
                        out.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return picFile.getPath();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s!=null){
                    downFinishListener.getDownPath(s);
                }
            }
        }.execute();
    }

    //下载完成回调的接口
    public interface DownFinishListener{
        void getDownPath(String s);
    }
}