package com.singularitycoder.abstractfactory1;

import com.singularitycoder.abstractfactory1.factoryTypes.BankFactory;
import com.singularitycoder.abstractfactory1.factoryTypes.LoanFactory;

import static com.singularitycoder.abstractfactory1.MainActivity.factoryTypes;

class FactoryCreator {
    public static AbstractFactory getFactory(String choice) {
        if (choice.equalsIgnoreCase(factoryTypes[0])) return new BankFactory();
        if (choice.equalsIgnoreCase(factoryTypes[1])) return new LoanFactory();
        return null;
    }
}