package com.singularitycoder.qrcodescanner;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ScannedBarcode";
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1000;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private DisplayMetrics displayMetrics;
    private TextView tvScanState;

    private int DEVICE_WIDTH = 0;
    private int DEVICE_HEIGHT = 0;
    private String barcodeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBarStuff();
        setContentView(R.layout.activity_main);
        initializeData();
        getDeviceWidthHeight();
        initializeViews();
    }

    private void hideTitleBarStuff() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide Title
        getSupportActionBar().hide();   // Hide Title Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Hide Status Bar
    }

    private void initializeViews() {
        surfaceView = findViewById(R.id.surfaceView);
        tvScanState = findViewById(R.id.tv_scan_state);
    }

    private void initializeData() {
        displayMetrics = getResources().getDisplayMetrics();
    }

    private void getDeviceWidthHeight() {
        DEVICE_HEIGHT = displayMetrics.heightPixels;
        DEVICE_WIDTH = displayMetrics.widthPixels;
    }

    private void showScannedResult(Context context, String barcodeType, Object scannedResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Scanned Result");
        builder.setMessage(valueOf(scannedResult));
        builder.setPositiveButton("GO", (dialog, which) -> {
            switch (barcodeType) {
                case "EMAIL":
                    Map<String, String> emailData = (HashMap) scannedResult;
                    openEmail(emailData.get("emailAddress"), emailData.get("subject"), emailData.get("body"));
                    dialog.cancel();
                    break;
                case "URL":
                    showInChromeBrowser(valueOf(scannedResult));
                    dialog.cancel();
                    break;
                case "PHONE":
                    dialogPhoneActions(valueOf(scannedResult));
                    dialog.cancel();
                    break;
                case "SMS":
                    showMessenger(MainActivity.this, valueOf(scannedResult));
                    dialog.cancel();
                    break;
                case "TEXT":
                    openChooser(valueOf(scannedResult));
                    dialog.cancel();
                    break;
                default:
                    openChooser(valueOf(scannedResult));
                    dialog.cancel();
                    break;
            }
        });
        builder.setNegativeButton("RESCAN", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showInChromeBrowser(String url) {
        if (!("").equals(url)) {
            URLEncoder.encode(url);
            Uri uri = Uri.parse("https://www.google.com/search?q=" + url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // If Chrome not installed
                intent.setPackage(null);
                startActivity(intent);
            }
        }
    }

    private void openEmail(String emailAddress, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject.trim());
        intent.putExtra(Intent.EXTRA_TEXT, body.trim());
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void openChooser(String scannedResult) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, scannedResult);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void dialogPhoneActions(String phoneNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        String[] selectArray = {"Call", "SMS", "WhatsApp"};
        builder.setItems(selectArray, (dialog, which) -> {
            switch (which) {
                case 0:
                    showCaller(MainActivity.this, phoneNumber);
                    break;
                case 1:
                    showMessenger(MainActivity.this, phoneNumber);
                    break;
                case 2:
                    showWhatsApp(MainActivity.this, phoneNumber);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCaller(Context context, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(callIntent);
    }

    private void showMessenger(Context context, String phoneNumber) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", "Message Body");
        smsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        if (smsIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(smsIntent);
        }
    }

    private void showWhatsApp(Context context, String phoneNumber) {
        PackageManager packageManager = context.getPackageManager();
        try {
            // checks if such an app exists or not
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Uri uri = Uri.parse("smsto:" + phoneNumber);
            Intent whatsAppIntent = new Intent(Intent.ACTION_SENDTO, uri);
            whatsAppIntent.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(whatsAppIntent, "Dummy Title"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "WhatsApp not found. Install from Play Store.", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent openPlayStore = new Intent(Intent.ACTION_VIEW, uri);
            openPlayStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(openPlayStore);
        }
    }

    public void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("We need you to grant the permissions for the camera feature to work!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openDeviceSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void openDeviceSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void setBarcodeDetector() {
        barcodeDetector = new BarcodeDetector
                .Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
    }

    private void setCameraSource() {
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(DEVICE_HEIGHT, DEVICE_WIDTH)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void setSurfaceView() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        showSettingsDialog(MainActivity.this);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    private void setBarcodeProcessor() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                tvScanState.setText("STOPPED SCANNING");
                tvScanState.setBackgroundColor(Color.parseColor("#C62828"));
                tvScanState.setTextColor(Color.parseColor("#FFFFFF"));
                Toast.makeText(MainActivity.this, "Stopped scanning to avoid memory leaks!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    runOnUiThread(() -> {
                        if (null != barcodes.valueAt(0).email) {
                            barcodeType = "EMAIL";
                            Map<String, String> emailData = new HashMap<>();
                            emailData.put("emailAddress", barcodes.valueAt(0).email.address);
                            emailData.put("subject", barcodes.valueAt(0).email.subject);
                            emailData.put("body", barcodes.valueAt(0).email.body);
                            showScannedResult(MainActivity.this, barcodeType, emailData);
                        }

                        if (null != barcodes.valueAt(0).url) {
                            barcodeType = "URL";
                            String result = barcodes.valueAt(0).url.url;
                            showScannedResult(MainActivity.this, barcodeType, result);
                        }

                        if (null != barcodes.valueAt(0).phone) {
                            barcodeType = "PHONE";
                            String result = barcodes.valueAt(0).phone.number;
                            showScannedResult(MainActivity.this, barcodeType, result);
                        }

                        if (null != barcodes.valueAt(0).sms) {
                            barcodeType = "SMS";
                            String result = barcodes.valueAt(0).sms.phoneNumber;
                            showScannedResult(MainActivity.this, barcodeType, result);
                        }

                        if (null != barcodes.valueAt(0).displayValue) {
                            barcodeType = "TEXT";
                            String result = barcodes.valueAt(0).displayValue;
                            showScannedResult(MainActivity.this, barcodeType, result);
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvScanState.setText("STARTED SCANNING");
        tvScanState.setBackgroundColor(Color.parseColor("#558B2F"));
        tvScanState.setTextColor(Color.parseColor("#FFFFFF"));
        setBarcodeDetector();
        setCameraSource();
        setSurfaceView();
        setBarcodeProcessor();
    }
}
