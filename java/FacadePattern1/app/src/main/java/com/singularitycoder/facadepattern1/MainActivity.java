package com.singularitycoder.facadepattern1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.singularitycoder.facadepattern1.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// https://www.javatpoint.com/facade-pattern
// just provide a unified and simplified interface to a set of interfaces in a subsystem, therefore it hides the complexities of the subsystem from the client
// In other words, Facade Pattern describes a higher-level interface that makes the sub-system easier to use.
// Practically, every Abstract Factory is a type of Facade.

// Advantages
// It shields the clients from the complexities of the sub-system components.
// It promotes loose coupling between subsystems and its clients.

// Uses
// When you want to provide simple interface to a complex sub-system.
// When several dependencies exist between clients and the implementation classes of an abstraction.

public class MainActivity extends AppCompatActivity {

    @NonNull
    private static final String[] shopArray = new String[]{"APPLE", "SAMSUNG", "BLACKBERRY"};

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ArrayAdapter<String> shopAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shopArray);
        ((AutoCompleteTextView) binding.etSelectShop.getEditText()).setAdapter(shopAdapter);

        binding.btnPurchase.setOnClickListener(v -> {
            // Creating a client that can purchase the mobiles from MobileShop through ShopKeeper.
            final String selectedShop = String.valueOf(((AutoCompleteTextView) binding.etSelectShop.getEditText()).getText());
            final ShopKeeper shopKeeper = new ShopKeeper();

            switch (selectedShop) {
                case "APPLE":
                    binding.tvPurchaseDetails.setText(shopKeeper.iphoneSale());
                    break;
                case "SAMSUNG":
                    binding.tvPurchaseDetails.setText(shopKeeper.samsungSale());
                    break;
                case "BLACKBERRY":
                    binding.tvPurchaseDetails.setText(shopKeeper.blackberrySale());
                    break;
                default:
                    binding.tvPurchaseDetails.setText("You didnt purchase anything");
                    return;
            }
        });
    }
}