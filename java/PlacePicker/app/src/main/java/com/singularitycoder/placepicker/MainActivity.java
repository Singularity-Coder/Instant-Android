package com.singularitycoder.placepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    int REQUEST_CODE_AUTOCOMPLETE = 0;

    String placeLatitude;
    String placeLongitude;
    String websiteUrl;
    String trimmedUrl;

    TextView tvEventLocation, tvPlaceName, tvPlaceAddress, tvPlaceLatitude, tvPlaceLongitude, tvPlaceId, tvPlacePhone, tvPLaceRating, tvPlaceWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEventLocation = findViewById(R.id.tv_select_place);
        tvPlaceName = findViewById(R.id.tv_place_name);
        tvPlaceAddress = findViewById(R.id.tv_place_address);
        tvPlaceLatitude = findViewById(R.id.tv_place_latitude);
        tvPlaceLongitude = findViewById(R.id.tv_place_longitude);
        tvPlaceId = findViewById(R.id.tv_place_id);
        tvPlacePhone = findViewById(R.id.tv_place_phone);
        tvPLaceRating = findViewById(R.id.tv_place_ratings);
        tvPlaceWebsite = findViewById(R.id.tv_place_website);
    }

    public void autoCompletePlace(View view) {

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyCapweapWrTZq_CLhkq6MKwc3zvLnYAjaw");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ID, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.WEBSITE_URI);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);

        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {

                // Get Place data from intent
                Place place = Autocomplete.getPlaceFromIntent(data);

                if (place != null) {
                    tvEventLocation.setText(place.getAddress());
                    tvPlaceName.setText("Place Name: " + place.getName());
                    tvPlaceAddress.setText("Place Address: " + place.getAddress());
                    tvPlaceAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=" + tvPlaceAddress.getText().toString().substring(15));
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);
                            } catch (ActivityNotFoundException ane) {
                                ane.getMessage();
                                Toast.makeText(getApplicationContext(), "You don't have Google Maps App", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    });
                    // Get lat long data from Place data
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        placeLatitude = String.valueOf(latLng.latitude);
                        placeLongitude = String.valueOf(latLng.longitude);
                    }
                    tvPlaceLatitude.setText("Place Latitude: " + placeLatitude);
                    tvPlaceLongitude.setText("Place Longitude: " + placeLongitude);
                    tvPlaceId.setText("Place Id: " + place.getId());
                    if (place.getPhoneNumber() != null) {
                        tvPlacePhone.setText("Place Phone: " + place.getPhoneNumber());
                    } else {
                        tvPlacePhone.setText("Place Phone: null");
                    }
                    if (place.getRating() != null) {
                        tvPLaceRating.setText("Place Rating: " + place.getRating().toString());
                    } else {
                        tvPLaceRating.setText("Place Rating: null");
                    }
                    if (place.getWebsiteUri() != null) {
                        tvPlaceWebsite.setText("Place Website: " + place.getWebsiteUri().toString());
                        tvPlaceWebsite.setTextColor(Color.parseColor("#1E88E5"));
                        websiteUrl = tvPlaceWebsite.getText().toString();
                        trimmedUrl = websiteUrl.substring(15);
                        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
                            trimmedUrl = "http://" + trimmedUrl;
                        }
                        tvPlaceWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trimmedUrl)));
                            }
                        });
                    } else {
                        tvPlaceWebsite.setText("Place Website: null");
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

                // The user canceled the operation.
                Toast.makeText(this, "Result got cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
