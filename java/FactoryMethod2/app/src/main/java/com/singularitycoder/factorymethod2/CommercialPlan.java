package com.singularitycoder.factorymethod2;

// concrete class
class CommercialPlan extends Plan {

    @Override
    public void getRate() {
        rate = 7.50;
    }
}