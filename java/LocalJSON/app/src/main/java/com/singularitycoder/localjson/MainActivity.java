package com.singularitycoder.localjson;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.JsonWriter;
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
}