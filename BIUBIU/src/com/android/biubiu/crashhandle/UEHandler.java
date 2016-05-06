package com.android.biubiu.crashhandle;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class UEHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = UEHandler.class.getSimpleName();

    private File fileErrorMLog;
    public static final String fileName = "errorlog";

    public UEHandler(Context ctx) {
//        fileErrorMLog = new File(ctx.getFilesDir(), fileName);
        fileErrorMLog = new File(ctx.getExternalCacheDir(), fileName);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Log.e(TAG, "uncaught exception happened!");
        // fetch Excpetion Info
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // print
        long threadId = thread.getId();
        Log.e(TAG, "Thread.getName()=" + thread.getName() + " id=" + threadId + " state=" + thread.getState());
        Log.e(TAG, "Error[" + info + "]");

        // if (!BuildConfig.DEBUG) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        write2ErrorMLog(fileErrorMLog, info);
        // kill App Progress
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        // }
    }

    private void write2ErrorMLog(File file, String content) {
        FileOutputStream fos = null;
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
