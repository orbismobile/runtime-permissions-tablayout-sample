package pe.com.orbis.runtimepermissions_sample.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos Vargas on 13/05/16.
 * Alias: CarlitosDroid
 */
public class FileSystemUtils {

    public static List<String> getAllImage(Activity activity) {
        List<String> allImageFile = new ArrayList<>();
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //Get relevant columns for use later.
        String[] projection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.TITLE
        };

        CursorLoader cursorLoader = new CursorLoader(
                activity,
                queryUri,
                projection,
                null,
                null, // Selection args (none).
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                //Log.e("CURSOR ", "CURSOR " + cursor.getString(1));
                allImageFile.add(cursor.getString(1));
            }
            cursor.close();
        }
        return allImageFile;
    }
}