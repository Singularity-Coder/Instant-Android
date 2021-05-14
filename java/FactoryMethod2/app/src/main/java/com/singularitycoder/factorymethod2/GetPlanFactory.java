package com.singularitycoder.factorymethod2;

import static com.singularitycoder.factorymethod2.MainActivity.planTypeArray;

class GetPlanFactory {

    //use getPlan method to get object of type Plan
    public Plan getPlan(String planType) {
        if (planType == null) return null;
        if (planType.equalsIgnoreCase(planTypeArray[0])) return new DomesticPlan();
        if (planType.equalsIgnoreCase(planTypeArray[1])) return new CommercialPlan();
        if (planType.equalsIgnoreCase(planTypeArray[2])) return new InstitutionalPlan();
        return null;
    }
}