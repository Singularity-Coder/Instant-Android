package com.singularitycoder.abstractfactory1.loanTypes;

public abstract class Loan {
    protected double rate;

    public abstract void getInterestRate(double rate);

    public double calculateLoanPayment(double loanAmount, int years) {
        /**
         To calculate the monthly loan payment i.e. EMI
         rate = annualInterestRate / 12 * 100;
         n=number of monthly installments;
         1year = 12 months.
         so, n = years * 12;
         */

        rate = rate / 1200;
        final int n = years * 12;
        return ((rate * Math.pow((1 + rate), n)) / ((Math.pow((1 + rate), n)) - 1)) * loanAmount;
    }
}