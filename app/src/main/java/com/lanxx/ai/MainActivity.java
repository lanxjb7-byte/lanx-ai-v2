
package com.lanxx.ai;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout root, messages;
    EditText input;
    Switch hardWork;
    String lastAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildUI();

        if (android.provider.Settings.canDrawOverlays(this)) {
            startService(new Intent(this, FloatingService.class));
        }
    }

    void buildUI() {

        root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(18, 18, 18, 18);
        root.setBackgroundColor(Color.rgb(9, 6, 18));

        TextView title = new TextView(this);
        title.setText("LANXX AI V2");
        title.setTextColor(Color.rgb(190, 140, 255));
        title.setTextSize(26);
        title.setPadding(0, 10, 0, 20);
        root.addView(title);

        hardWork = new Switch(this);
        hardWork.setText("🔥 HARD WORK MODE");
        hardWork.setTextColor(Color.WHITE);
        root.addView(hardWork);

        ScrollView scroll = new ScrollView(this);
        messages = new LinearLayout(this);
        messages.setOrientation(1);
        scroll.addView(messages);

        root.addView(scroll, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));

        input = new EditText(this);
        input.setHint("Tanyakan sesuatu...");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        root.addView(input);

        Button send = new Button(this);
        send.setText("KIRIM");
        root.addView(send);

        Button photo = new Button(this);
        photo.setText("UPLOAD FOTO");
        root.addView(photo);

        Button save = new Button(this);
        save.setText("SIMPAN JAWABAN");
        root.addView(save);

        setContentView(root);

        send.setOnClickListener(v -> sendMessage());

        photo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 77);
        });

        save.setOnClickListener(v -> {
            if (!lastAnswer.isEmpty()) {
                saveFile();
            } else {
                Toast.makeText(this, "Belum ada jawaban", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addMessage(String who, String text) {
        TextView view = new TextView(this);
        view.setText(who + "\n" + text);
        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        view.setPadding(14, 14, 14, 14);
        messages.addView(view);
    }

    void sendMessage() {
        String question = input.getText().toString().trim();

        if (question.isEmpty()) return;

        input.setText("");
        addMessage("LU", question);
        addMessage("LANXX AI", "Sedang berpikir...");

        new AIClient().send(question, hardWork.isChecked(),
            new AIClient.Callback() {

                @Override
                public void onSuccess(String answer) {
                    runOnUiThread(() -> {
                        lastAnswer = answer;
                        messages.removeViewAt(messages.getChildCount() - 1);
                        addMessage("LANXX AI", answer);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        messages.removeViewAt(messages.getChildCount() - 1);
                        addMessage("ERROR", error);
                    });
                }
            });
    }

    void saveFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "LANXX_AI_RESULT.txt");
        startActivityForResult(intent, 900);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == 900) {
            FileCreator.write(data.getData(), this, lastAnswer);
            Toast.makeText(this, "File disimpan", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 77) {
            Uri uri = data.getData();
            Toast.makeText(this,
                "Foto dipilih: " + uri.getLastPathSegment(),
                Toast.LENGTH_SHORT).show();
        }
    }
}
