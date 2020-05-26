package com.singularitycoder.texttospeechtotextapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String UTTERANCE_ID = "PastedText";

    private final int REQUEST_CODE_RECOGNISE_SPEECH = 100;
    private final int REQUEST_CODE_SELECT_AUDIO = 101;

    private TextToSpeech textToSpeech;
    private AudioManager audioManager;
    private EditText etTextToConvert;
    private Button btnShowHideControls, btnTextToSpeech, btnStop;
    private Button btnSpeechToText;
    private Button btnClearText, btnSaveToAudio, btnOpenSavedAudioFolder;
    private SeekBar seekbarPitch, seekbarSpeechRate, seekbarVolume;
    private TextView tvSetLanguage;
    private TextView tvPitchValue, tvSpeechRateValue, tvVolumeValue;
    private ConstraintLayout conLayAudioControls;
    private String savedAudioFileName;
    private Button btnVolumeIncrease, btnVolumeDecrease, btnPitchIncrease, btnPitchDecrease, btnSpeechRateIncrease, btnSpeechRateDecrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeData();
        checkForPermissions();
        createFolderToSaveAudio();
        initializeTextToSpeech();
        clickListeners();
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void initializeViews() {
        etTextToConvert = findViewById(R.id.et_text_to_convert);
        btnTextToSpeech = findViewById(R.id.btn_text_to_speech);
        btnSpeechToText = findViewById(R.id.btn_speech_to_text);
        btnShowHideControls = findViewById(R.id.btn_hide_show_controls);
        conLayAudioControls = findViewById(R.id.con_lay_audio_controls);
        tvPitchValue = findViewById(R.id.tv_pitch_value);
        tvSpeechRateValue = findViewById(R.id.tv_speech_rate_value);
        tvVolumeValue = findViewById(R.id.tv_volume_value);
        seekbarPitch = findViewById(R.id.seekbar_pitch);
        seekbarSpeechRate = findViewById(R.id.seekbar_speech_rate);
        seekbarVolume = findViewById(R.id.seekbar_volume);
        tvSetLanguage = findViewById(R.id.tv_set_language);
        btnStop = findViewById(R.id.btn_stop);
        btnClearText = findViewById(R.id.btn_clear_text);
        btnSaveToAudio = findViewById(R.id.btn_save_to_audio_file);
        btnOpenSavedAudioFolder = findViewById(R.id.btn_open_saved_audio_folder);
        btnVolumeIncrease = findViewById(R.id.btn_volume_increase);
        btnVolumeDecrease = findViewById(R.id.btn_volume_decrease);
        btnPitchIncrease = findViewById(R.id.btn_pitch_increase);
        btnPitchDecrease = findViewById(R.id.btn_pitch_decrease);
        btnSpeechRateIncrease = findViewById(R.id.btn_speech_rate_increase);
        btnSpeechRateDecrease = findViewById(R.id.btn_speech_rate_decrease);
    }

    private void initializeData() {
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private void checkForPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(MainActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("You must grant permissions for the features to work properly!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openDeviceSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openDeviceSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("SimpleDateFormat")
    private void createFolderToSaveAudio() {
        // Create folder first to save speech audio
        File audioFileDirectory = new File(Environment.getExternalStorageDirectory() + "/SavedTextToSpeechAudio/");
        audioFileDirectory.mkdirs();
        savedAudioFileName = audioFileDirectory.getAbsolutePath() + "/" + UTTERANCE_ID + "_" + new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss").format(new Date()) + ".wav";
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ROOT);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getApplicationContext(), "Language not supported!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clickListeners() {
        btnTextToSpeech.setOnClickListener(v -> startTextToSpeech());
        btnShowHideControls.setOnClickListener(view -> showHideSpeechAudioControls());
        btnStop.setOnClickListener(view -> textToSpeech.stop());
        tvSetLanguage.setOnClickListener(view -> dialogSetLanguage());
        controlSpeechPitch();
        controlSpeechRate();
        controlVolume();
        btnSpeechToText.setOnClickListener(view -> startSpeechToText());
        btnClearText.setOnClickListener(view -> etTextToConvert.setText(""));
        btnSaveToAudio.setOnClickListener(view -> {
            startTextToSpeech();
            saveToAudioFile(String.valueOf(etTextToConvert.getText()).trim());
        });
        btnOpenSavedAudioFolder.setOnClickListener(view -> openSavedAudioFolder());
        btnVolumeIncrease.setOnClickListener(view -> increaseVolume());
        btnVolumeDecrease.setOnClickListener(view -> decreaseVolume());
        btnPitchIncrease.setOnClickListener(view -> increasePitch());
        btnPitchDecrease.setOnClickListener(view -> decreasePitch());
        btnSpeechRateIncrease.setOnClickListener(view -> increaseSpeechRate());
        btnSpeechRateDecrease.setOnClickListener(view -> decreaseSpeechRate());
    }

    private void increaseVolume() {
        // todo : save state in shared prefs
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        if (seekbarVolume.getProgress() <= 15) {
            seekbarVolume.setProgress(seekbarVolume.getProgress() + 1);
        }
    }

    private void decreaseVolume() {
        // todo : save state in shared prefs
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        if (seekbarVolume.getProgress() >= 0) {
            seekbarVolume.setProgress(seekbarVolume.getProgress() - 1);
        }
    }

    private void increasePitch() {
        if (seekbarPitch.getProgress() <= 100) {
            seekbarPitch.setProgress(seekbarPitch.getProgress() + 1);
        }
    }

    private void decreasePitch() {
        if (seekbarPitch.getProgress() >= 0) {
            seekbarPitch.setProgress(seekbarPitch.getProgress() - 1);
        }
    }

    private void increaseSpeechRate() {
        if (seekbarSpeechRate.getProgress() <= 100) {
            seekbarSpeechRate.setProgress(seekbarSpeechRate.getProgress() + 1);
        }
    }

    private void decreaseSpeechRate() {
        if (seekbarSpeechRate.getProgress() >= 0) {
            seekbarSpeechRate.setProgress(seekbarSpeechRate.getProgress() - 1);
        }
    }

    private void startTextToSpeech() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Started reading " + utteranceId, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals(UTTERANCE_ID)) {
                    Toast.makeText(MainActivity.this, "Saved to " + savedAudioFileName, Toast.LENGTH_LONG).show();
                }
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Finished Speaking " + utteranceId, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String utteranceId) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error with " + utteranceId, Toast.LENGTH_SHORT).show());
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UtteredWord");

        String textToSpeak = etTextToConvert.getText().toString();
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID);

        // Stay silent for 1000 ms
        textToSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, UTTERANCE_ID);
    }

    private void showHideSpeechAudioControls() {
        if (conLayAudioControls.getVisibility() == View.VISIBLE) {
            conLayAudioControls.setVisibility(View.GONE);
        } else {
            conLayAudioControls.setVisibility(View.VISIBLE);
        }
    }

    private void controlSpeechPitch() {
        if (null != seekbarPitch) {
            seekbarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvPitchValue.setText(valueOf(seekBar.getProgress()));
                    textToSpeech.setPitch(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    tvPitchValue.setText(valueOf(seekBar.getProgress()));
                    textToSpeech.setPitch(seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tvPitchValue.setText(valueOf(seekBar.getProgress()));
                    textToSpeech.setPitch(seekBar.getProgress());
                }
            });
        }
    }

    private void controlSpeechRate() {
        if (null != seekbarSpeechRate) {
            seekbarSpeechRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvSpeechRateValue.setText(valueOf(seekBar.getProgress()));
                    textToSpeech.setSpeechRate(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    tvSpeechRateValue.setText(valueOf(seekBar.getProgress()));
                    textToSpeech.setSpeechRate(seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tvSpeechRateValue.setText(valueOf(seekBar.getProgress()));
                    textToSpeech.setSpeechRate(seekBar.getProgress());
                }
            });
        }
    }

    private void controlVolume() {
        if (null != seekbarVolume) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            seekbarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

            seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int newVolume, boolean fromUser) {
                    tvVolumeValue.setText(valueOf(seekBar.getProgress()));
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    tvVolumeValue.setText(valueOf(seekBar.getProgress()));
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tvVolumeValue.setText(valueOf(seekBar.getProgress()));
                }
            });
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking Now!");
        try {
            startActivityForResult(intent, REQUEST_CODE_RECOGNISE_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device is not supported!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInChromeBrowser() {
        if (!("").equals(String.valueOf("text to browse"))) {
            String urlString = String.valueOf("text to browse");
            URLEncoder.encode(urlString);
            Uri uri = Uri.parse("https://www.google.com/search?q=" + urlString);
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

    private void saveToAudioFile(String text) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.synthesizeToFile(text, null, new File(savedAudioFileName), UTTERANCE_ID);
                            } else {
                                HashMap<String, String> hashMap = new HashMap();
                                hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
                                textToSpeech.synthesizeToFile(text, hashMap, savedAudioFileName);
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(MainActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openSavedAudioFolder() {
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/SavedTextToSpeechAudio/");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri, "audio/wav");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Open Folder with..."), REQUEST_CODE_SELECT_AUDIO);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "File Manager not found. Install from Playstore.", Toast.LENGTH_SHORT).show();
            Uri playStoreUri = Uri.parse("market://details?id=com.google.android.apps.nbu.files");
            Intent openPlayStore = new Intent(Intent.ACTION_VIEW, playStoreUri);
            openPlayStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(openPlayStore);
        }
    }

    private void dialogSetLanguage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Speech Language");
        String[] selectArray = {"Device Language", "English", "Canada French", "French", "German", "Italian", "Japanese", "Korean", "Chinese"};
        builder.setItems(selectArray, (dialog, which) -> {
            switch (which) {
                case 0:
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = textToSpeech.setLanguage(Locale.ROOT);

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Toast.makeText(getApplicationContext(), "Language not supported!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case 1:
                    tvSetLanguage.setText(selectArray[1]);
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    break;
                case 2:
                    tvSetLanguage.setText(selectArray[2]);
                    textToSpeech.setLanguage(Locale.CANADA_FRENCH);
                    break;
                case 3:
                    tvSetLanguage.setText(selectArray[3]);
                    textToSpeech.setLanguage(Locale.FRENCH);
                    break;
                case 4:
                    tvSetLanguage.setText(selectArray[4]);
                    textToSpeech.setLanguage(Locale.GERMAN);
                    break;
                case 5:
                    tvSetLanguage.setText(selectArray[5]);
                    textToSpeech.setLanguage(Locale.ITALIAN);
                    break;
                case 6:
                    tvSetLanguage.setText(selectArray[6]);
                    textToSpeech.setLanguage(Locale.JAPANESE);
                    break;
                case 7:
                    tvSetLanguage.setText(selectArray[7]);
                    textToSpeech.setLanguage(Locale.KOREAN);
                    break;
                case 8:
                    tvSetLanguage.setText(selectArray[8]);
                    textToSpeech.setLanguage(Locale.CHINESE);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeVoice() {
        // get voices
        Set<Voice> voices = textToSpeech.getVoices();
        Object[] voiceArray = voices.toArray();
        Log.d(TAG, "clickListeners: voices: " + voices);
        for (int i = 0; i < voices.size(); i++) {
            Log.d(TAG, "voice: " + voiceArray[i]);
            textToSpeech.setVoice((Voice) voiceArray[4]);
        }
    }

    private String getFilePath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};

            try {
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (null != cursor) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                    cursor.close();
                }
            } catch (Exception URISyntaxException) {
                URISyntaxException.printStackTrace();
            }
        }

        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RECOGNISE_SPEECH && resultCode == RESULT_OK && null != data) {
            ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            etTextToConvert.setText(String.valueOf(result.get(0)));
        }

        if (requestCode == REQUEST_CODE_SELECT_AUDIO && resultCode == RESULT_OK && null != data) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(getFilePath(this, data.getData()));
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}