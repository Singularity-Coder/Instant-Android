package com.singularitycoder.facadepattern1.shops;

import com.singularitycoder.facadepattern1.MobileShop;

public class Iphone implements MobileShop {
    @Override
    public String modelNo() {
        System.out.println(" Iphone 6 ");
        return "Iphone 6";
    }

    @Override
    public String price() {
        System.out.println(" Rs 65000.00 ");
        return "Rs 65000.00";
    }
}