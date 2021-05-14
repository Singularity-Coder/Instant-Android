package com.singularitycoder.factorymethod2;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.singularitycoder.factorymethod2.databinding.ActivityMainBinding;

// https://www.javatpoint.com/factory-method-design-pattern
// define an interface or abstract class for creating an object but let the subclasses decide which class to instantiate.
// subclasses are responsible to create the instance of the class.
// The Factory Method Pattern is also known as Virtual Constructor.

// Advantages
// Factory Method Pattern allows the sub-classes to choose the type of objects to create.
// promotes the loose-coupling by eliminating the need to bind application-specific classes into the code.

// Usage
// When a class doesn't know what sub-classes will be required to create
// When a class wants that its sub-classes specify the objects to be created.
// When the parent classes choose the creation of objects to its sub-classes.

public class MainActivity extends AppCompatActivity {

    static String[] planTypeArray = {"DOMESTIC", "COMMERCIAL", "INSTITUTIONAL"};
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ArrayAdapter<String> planTypeAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, planTypeArray);
        binding.etPlanType.setThreshold(1);
        binding.etPlanType.setAdapter(planTypeAdapter);

        binding.btnCalculateBill.setOnClickListener(v -> {
            final String planName = String.valueOf(binding.etPlanType.getText());
            final int units = Integer.parseInt(String.valueOf(binding.etUnits.getText()));
            final GetPlanFactory planFactory = new GetPlanFactory();
            final Plan plan = planFactory.getPlan(planName);
            plan.getRate();
            // call getRate() method and calculateBill() method of Domestic Plan. Rate is an implementation detail that is hidden nicely.
            binding.tvResult.setText("Bill amount for " + planName.toLowerCase() + " plan with  " + units + " units is " + plan.calculateBill(units) + "Rs. The rate applied is " + plan.rate + ".");
        });
    }
}
