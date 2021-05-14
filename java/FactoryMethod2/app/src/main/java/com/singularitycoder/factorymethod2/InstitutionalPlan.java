package com.singularitycoder.factorymethod2;

// concrete class
class InstitutionalPlan extends Plan {

    @Override
    public void getRate() {
        rate = 5.50;
    }
}