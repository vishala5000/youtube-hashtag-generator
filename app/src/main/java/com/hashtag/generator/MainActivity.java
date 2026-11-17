package com.hashtag.generator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "HashtagApp";
    
    private EditText editTextTitle;
    private Button buttonGenerate;
    private Button buttonCopy;
    private TextView textViewHashtags;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);
            
            editTextTitle = findViewById(R.id.editTextTitle);
            buttonGenerate = findViewById(R.id.buttonGenerate);
            buttonCopy = findViewById(R.id.buttonCopy);
            textViewHashtags = findViewById(R.id.textViewHashtags);
            progressBar = findViewById(R.id.progressBar);
            
            if (editTextTitle == null || buttonGenerate == null || buttonCopy == null || 
                textViewHashtags == null || progressBar == null) {
                Log.e(TAG, "UI elements not found!");
                Toast.makeText(this, "App initialization error", Toast.LENGTH_LONG).show();
                return;
            }
            
            buttonGenerate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        generateHashtags();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in button click", e);
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            
            buttonCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        copyHashtags();
                    } catch (Exception e) {
                        Log.e(TAG, "Error copying", e);
                        Toast.makeText(MainActivity.this, "Copy failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            Log.i(TAG, "App initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "App crashed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void generateHashtags() {
        String title = editTextTitle.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isNetworkAvailable()) {
            textViewHashtags.setText("No internet connection");
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        textViewHashtags.setText("Generating...");
        buttonCopy.setEnabled(false);
        buttonGenerate.setEnabled(false);
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "Generating hashtags for: " + title);
                    final String hashtags = HashtagGenerator.generateHashtags(title);
                    Log.i(TAG, "Generated: " + hashtags);
                    
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressBar.setVisibility(View.GONE);
                                textViewHashtags.setText(hashtags);
                                buttonCopy.setEnabled(true);
                                buttonGenerate.setEnabled(true);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error updating UI", e);
                            }
                        }
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error generating hashtags", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressBar.setVisibility(View.GONE);
                                textViewHashtags.setText("Error: " + e.getMessage());
                                buttonGenerate.setEnabled(true);
                            } catch (Exception ex) {
                                Log.e(TAG, "Error in error handler", ex);
                            }
                        }
                    });
                }
            }
        });
    }
    
    private void copyHashtags() {
        String text = textViewHashtags.getText().toString();
        
        if (text.isEmpty() || text.equals("Your 10 viral hashtags will appear here...") || 
            text.startsWith("No") || text.startsWith("Generating") || text.startsWith("Error")) {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("hashtags", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Copy error", e);
            Toast.makeText(this, "Copy failed", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network == null) return false;
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                android.net.NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "Network check error", e);
            return false;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            executor.shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error shutting down executor", e);
        }
    }
}
