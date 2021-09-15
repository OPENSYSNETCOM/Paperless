package kr.co.opensysnet.paperless.control.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class PrefStorage {
    public static void saveStringData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey, String data) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(subKey, data);
        editor.apply();
    }

    public static String getStringData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        return pref.getString(subKey, null);
    }

    public static void saveBitmapData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey, Bitmap data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        data.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(subKey,encodedImage);
        editor.apply();
    }

    public static Bitmap getBitmapData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        String previouslyEncodedImage = pref.getString(subKey, "");

        if( previouslyEncodedImage != null && !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    public static void saveBlobData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey, byte[] data) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(subKey, json);
        editor.apply();
    }

    public static byte[] getBlobData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(subKey, null);
        Type type = new TypeToken<byte[]>() {}.getType();

        return gson.fromJson(json, type);
    }

    public static void saveIntData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey, int data) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(subKey, data);
        editor.apply();
    }

    public static int getIntData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        return pref.getInt(subKey, 0);
    }

    public static void saveBooleanData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey, boolean data) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(subKey, data);
        editor.apply();
    }

    public static boolean getBooleanData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        return pref.getBoolean(subKey, false);
    }

    public static void clearData(@NonNull Context context, @NonNull String mainKey, @NonNull String subKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(subKey);
        editor.apply();
    }

    public static void clearAll(@NonNull Context context, @NonNull String mainKey) {
        SharedPreferences pref = context.getSharedPreferences(mainKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
