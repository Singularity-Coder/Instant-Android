package com.singularitycoder.androidbackgroundprocessing;


// 0. Some scenarios to use Threads/background processing - complex calculations, conversions like - to byteArray, compression of images, downloads, network calls, wait for some process to finish before executing some other process or update some view,
// 0.1 Problems with background processing - updating UI
// 1. Async Task long format
// 2. Async Task short format
// 3. Handlers
// 4. Threads
// 4.1 Thread Pool
// 5. Service
// 5.1 Service + UI result
// 5.2 Types of Services
// 6. Executor framework + Runnable/Callable+Future

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    ImageView imageView = null;
    Bitmap bitmapImage = null;
    InputStream inputStream = null;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.download_button);
        imageView = findViewById(R.id.image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LongAsyncTask asyncTask = new LongAsyncTask();
                asyncTask.execute("https://cdn.pixabay.com/photo/2020/01/07/06/22/peacock-4746848_960_720.jpg");
            }
        });
    }

    private class LongAsyncTask extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Please wait...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL imageUrl = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                inputStream = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmapImage = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmapImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView != null) {
                progress.hide();
                imageView.setImageBitmap(bitmap);
            } else {
                progress.hide();
                Toast.makeText(MainActivity.this, "Something is wrong. Try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void crreateThread () {
        Thread customThread = new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                imageView.post(new Runnable() {
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            Log.d(TAG, "New Thread is ");
                        }
                    }
                });
            }
        });
        customThread.currentThread();
        customThread.getThreadGroup();
        customThread.setName("Thread number 1");
        customThread.setPriority(10);
        customThread.start();
    }
 }