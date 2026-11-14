
package com.lanxx.ai;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.*;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;

public class FloatingService extends Service {

    WindowManager manager;
    View bubble;

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        TextView icon = new TextView(this);
        icon.setText("🤖");
        icon.setTextSize(26);
        icon.setTextColor(Color.WHITE);
        icon.setGravity(Gravity.CENTER);
        icon.setBackgroundColor(Color.rgb(110, 50, 220));

        WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(
                130,
                130,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            );

        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.x = 20;

        bubble = icon;
        manager.addView(bubble, params);

        bubble.setOnClickListener(v -> {
            Intent open = new Intent(this, MainActivity.class);
            open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(open);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bubble != null) {
            manager.removeView(bubble);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    }
