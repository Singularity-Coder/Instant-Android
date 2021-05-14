package com.singularitycoder.abstractfactory1;

import com.singularitycoder.abstractfactory1.bankTypes.Bank;
import com.singularitycoder.abstractfactory1.loanTypes.Loan;

// Create an abstract class (i.e AbstractFactory) to get the factories for Bank and Loan Objects.
public abstract class AbstractFactory {
    public abstract Bank getBank(String bank);

    public abstract Loan getLoan(String loan);
}
