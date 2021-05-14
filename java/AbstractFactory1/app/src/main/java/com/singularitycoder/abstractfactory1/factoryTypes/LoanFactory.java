package com.singularitycoder.abstractfactory1.factoryTypes;

import com.singularitycoder.abstractfactory1.AbstractFactory;
import com.singularitycoder.abstractfactory1.bankTypes.Bank;
import com.singularitycoder.abstractfactory1.loanTypes.BussinessLoan;
import com.singularitycoder.abstractfactory1.loanTypes.EducationLoan;
import com.singularitycoder.abstractfactory1.loanTypes.HomeLoan;
import com.singularitycoder.abstractfactory1.loanTypes.Loan;

import static com.singularitycoder.abstractfactory1.MainActivity.loanTypes;

public class LoanFactory extends AbstractFactory {
    public Bank getBank(String bank) {
        return null;
    }

    public Loan getLoan(String loan) {
        if (loan == null) return null;
        if (loan.equalsIgnoreCase(loanTypes[0])) return new HomeLoan();
        if (loan.equalsIgnoreCase(loanTypes[1])) return new BussinessLoan();
        if (loan.equalsIgnoreCase(loanTypes[2])) return new EducationLoan();
        return null;
    }
}
