package com.singularitycoder.facadepattern1.shops;

import com.singularitycoder.facadepattern1.MobileShop;

public class Blackberry implements MobileShop {
    @Override
    public String modelNo() {
        System.out.println(" Blackberry Z10 ");
        return "Blackberry Z10";
    }

    @Override
    public String price() {
        System.out.println(" Rs 55000.00 ");
        return "Rs 55000.00";
    }
}
