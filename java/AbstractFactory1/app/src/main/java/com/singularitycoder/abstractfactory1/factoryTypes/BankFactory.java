package com.singularitycoder.abstractfactory1.factoryTypes;

import com.singularitycoder.abstractfactory1.AbstractFactory;
import com.singularitycoder.abstractfactory1.bankTypes.Bank;
import com.singularitycoder.abstractfactory1.bankTypes.HDFC;
import com.singularitycoder.abstractfactory1.bankTypes.ICICI;
import com.singularitycoder.abstractfactory1.bankTypes.SBI;
import com.singularitycoder.abstractfactory1.loanTypes.Loan;

import static com.singularitycoder.abstractfactory1.MainActivity.bankTypes;

// Create the factory classes that inherit AbstractFactory class to generate the object of concrete class based on given information.
public class BankFactory extends AbstractFactory {
    public Bank getBank(String bank) {
        if (bank == null) return null;
        if (bank.equalsIgnoreCase(bankTypes[0])) return new HDFC();
        if (bank.equalsIgnoreCase(bankTypes[1])) return new ICICI();
        if (bank.equalsIgnoreCase(bankTypes[2])) return new SBI();
        return null;
    }

    public Loan getLoan(String loan) {
        return null;
    }
}
