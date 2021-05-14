package com.singularitycoder.abstractfactory1.loanTypes;

public class EducationLoan extends Loan {
    public void getInterestRate(double r) {
        rate = r;
    }
}