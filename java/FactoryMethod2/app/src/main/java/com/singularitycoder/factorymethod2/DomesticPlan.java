package com.singularitycoder.factorymethod2;

// concrete class
class DomesticPlan extends Plan {

    @Override
    public void getRate() {
        rate = 3.50;
    }
}