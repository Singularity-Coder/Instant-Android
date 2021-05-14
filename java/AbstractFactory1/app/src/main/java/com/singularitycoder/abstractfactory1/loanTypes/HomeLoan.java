package com.singularitycoder.abstractfactory1.loanTypes;

public class HomeLoan extends Loan {
    public void getInterestRate(double r) {
        rate = r;
    }
}