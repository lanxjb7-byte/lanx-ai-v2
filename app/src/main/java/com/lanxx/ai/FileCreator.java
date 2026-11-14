
package com.lanxx.ai;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCreator {

    public static void save(Activity activity, String content) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "LANXX_AI_RESULT.txt");

        activity.startActivityForResult(intent, 900);
    }

    public static void write(Uri uri, Activity activity, String content) {
        try {
            ParcelFileDescriptor pfd =
                activity.getContentResolver().openFileDescriptor(uri, "w");

            if (pfd == null) return;

            try (FileOutputStream stream =
                     new FileOutputStream(pfd.getFileDescriptor())) {
                stream.write(content.getBytes("UTF-8"));
                stream.flush();
            }

            pfd.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
