package com.singularitycoder.offlinejson2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AutoCompleteTextView autoCompleteTvCity;
    private final ArrayList<String> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCityFromJsonData();
        setAutocompleteSpinnerList(cityList);   // Add City

        File JSONfile = new File(getExternalFilesDir(null).getPath(), "cityList.json");

        // Update or write json data to the local json file
        OutputStream out = null;
        try {
            out = new FileOutputStream(JSONfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String getJsonDataFromLocalFile() {
        String jsonString = null;
        try {
            InputStream inputStream = getAssets().open("cityList.json");
            int fileSize = inputStream.available();
            byte[] buffer = new byte[fileSize];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

    private void getCityFromJsonData() {
        try {
            JSONObject jsonObject = new JSONObject(getJsonDataFromLocalFile());
            JSONArray array = jsonObject.getJSONArray("array");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String city = object.getString("city");
                cityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAutocompleteSpinnerList(ArrayList arrayList) {
        autoCompleteTvCity = findViewById(R.id.autocomplete_tv_city);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayList);
        autoCompleteTvCity.setAdapter(adapter);
        hideKeyBoard();
    }

    private void hideKeyBoard() {
        autoCompleteTvCity.setOnItemClickListener((adapterView, view, i, l) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] filesList = null;
        try {
            filesList = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (filesList != null) for (String filename : filesList) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void writeJsonStream(OutputStream out, List<Message> messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeMessagesArray(writer, messages);
        writer.close();
    }

    public void writeMessagesArray(JsonWriter writer, List<Message> messages) throws IOException {
        writer.beginArray();
        for (Message message : messages) {
            writeMessage(writer, message);
        }
        writer.endArray();
    }

    public void writeMessage(JsonWriter writer, Message message) throws IOException {
        writer.beginObject();
        writer.name("id").value(message.getId());
        writer.name("text").value(message.getText());
        if (message.getGeo() != null) {
            writer.name("geo");
            writeDoublesArray(writer, message.getGeo());
        } else {
            writer.name("geo").nullValue();
        }
        writer.name("user");
        writeUser(writer, message.getUser());
        writer.endObject();
    }

    public void writeUser(JsonWriter writer, User user) throws IOException {
        writer.beginObject();
        writer.name("name").value(user.getName());
        writer.name("followers_count").value(user.getFollowersCount());
        writer.endObject();
    }

    public void writeDoublesArray(JsonWriter writer, List<Double> doubles) throws IOException {
        writer.beginArray();
        for (Double value : doubles) {
            writer.value(value);
        }
        writer.endArray();
    }
}