package com.hashtag.generator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private Button buttonGenerate;
    private Button buttonCopy;
    private TextView textViewHashtags;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        editTextTitle = findViewById(R.id.editTextTitle);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        buttonCopy = findViewById(R.id.buttonCopy);
        textViewHashtags = findViewById(R.id.textViewHashtags);
        progressBar = findViewById(R.id.progressBar);
        
        buttonGenerate.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!isNetworkAvailable()) {
                textViewHashtags.setText("No internet connection");
                return;
            }
            
            progressBar.setVisibility(View.VISIBLE);
            textViewHashtags.setText("Generating...");
            buttonCopy.setEnabled(false);
            buttonGenerate.setEnabled(false);
            
            executor.execute(() -> {
                String hashtags = HashtagGenerator.generateHashtags(title);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    textViewHashtags.setText(hashtags);
                    buttonCopy.setEnabled(!hashtags.startsWith("No"));
                    buttonGenerate.setEnabled(true);
                });
            });
        });
        
        buttonCopy.setOnClickListener(v -> {
            String text = textViewHashtags.getText().toString();
            if (!text.isEmpty() && !text.startsWith("Your") && !text.startsWith("No") && !text.startsWith("Generating")) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("hashtags", text));
                Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
