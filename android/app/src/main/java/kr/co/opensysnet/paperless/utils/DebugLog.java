package kr.co.opensysnet.paperless.utils;

import android.util.Log;

import kr.co.opensysnet.paperless.BuildConfig;

public class DebugLog {
    public static void d(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Exception e = new Exception();
            StackTraceElement[] ste = e.getStackTrace();
            Log.d(tag, String.format("Function : %s, Line : %s, Log : %s", ste[1].getMethodName(), ste[1].getLineNumber(), log));
        }
    }

    public static void i(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Exception e = new Exception();
            StackTraceElement[] ste = e.getStackTrace();
            Log.i(tag, String.format("Function : %s, Line : %s, Log : %s", ste[1].getMethodName(), ste[1].getLineNumber(), log));
        }
    }

    public static void e(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Exception e = new Exception();
            StackTraceElement[] ste = e.getStackTrace();
            Log.e(tag, String.format("Function : %s, Line : %s, Log : %s", ste[1].getMethodName(), ste[1].getLineNumber(), log));
        }
    }

    public static void w(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Exception e = new Exception();
            StackTraceElement[] ste = e.getStackTrace();
            Log.w(tag, String.format("Function : %s, Line : %s, Log : %s", ste[1].getMethodName(), ste[1].getLineNumber(), log));
        }
    }

    public static void exception(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
