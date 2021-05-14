package com.singularitycoder.abstractfactory1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.singularitycoder.abstractfactory1.bankTypes.Bank;
import com.singularitycoder.abstractfactory1.databinding.ActivityMainBinding;
import com.singularitycoder.abstractfactory1.loanTypes.Loan;

// https://www.javatpoint.com/abstract-factory-pattern
// define an interface or abstract class for creating families of related (or dependent) objects but without specifying their concrete sub-classes.
// Abstract Factory lets a class returns a factory of classes. So, this is the reason that Abstract Factory Pattern is one level higher than the Factory Pattern.
// Abstract Factory Pattern is also known as Kit.

// Advantages
// Abstract Factory Pattern isolates the client code from concrete (implementation) classes.
// It eases the exchanging of object families.
// It promotes consistency among objects.

// Usages
// When the system needs to be independent of how its object are created, composed, and represented.
// When the family of related objects has to be used together, then this constraint needs to be enforced.
// When you want to provide a library of objects that does not show implementations and only reveals interfaces.
// When the system needs to be configured with one of a multiple family of objects.
public class MainActivity extends AppCompatActivity {

    public static String[] bankTypes = {"HDFC", "ICICI", "SBI"};
    public static String[] loanTypes = {"HOME", "BUSINESS", "EDUCATION"};
    public static String[] factoryTypes = {"BANK", "LOAN"};
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, bankTypes);
        binding.etBankName.setThreshold(1);
        binding.etBankName.setAdapter(bankAdapter);

        final ArrayAdapter<String> loanAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, loanTypes);
        binding.etLoanName.setThreshold(1);
        binding.etLoanName.setAdapter(loanAdapter);

        binding.btnCalculateLoanPayment.setOnClickListener(v -> {
            final String bankName = String.valueOf(binding.etBankName.getText());
            final String loanName = String.valueOf(binding.etLoanName.getText());
            final AbstractFactory bankFactory = FactoryCreator.getFactory(factoryTypes[0]);
            final Bank bank = bankFactory.getBank(bankName);
            final double rate = Double.parseDouble(String.valueOf(binding.etInterestRate.getText()));
            final double loanAmount = Double.parseDouble(String.valueOf(binding.etLoanAmount.getText()));
            final int loanPaymentDurationInYears = Integer.parseInt(String.valueOf(binding.etLoanPaymentDuration.getText()));
            final AbstractFactory loanFactory = FactoryCreator.getFactory(factoryTypes[1]);
            final Loan loan = loanFactory.getLoan(loanName);
            loan.getInterestRate(rate);
            binding.tvResult.setText("Your monthly EMI from " + bank.getBankName() + " bank is " + loan.calculateLoanPayment(loanAmount, loanPaymentDurationInYears) + " Rs for the amount " + loanAmount + " that you have borrowed and you must pay this loan in " + loanPaymentDurationInYears + " years.");
        });
    }
}