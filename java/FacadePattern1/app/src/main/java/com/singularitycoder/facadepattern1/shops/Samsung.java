package com.singularitycoder.facadepattern1.shops;

import com.singularitycoder.facadepattern1.MobileShop;

public class Samsung implements MobileShop {
    @Override
    public String modelNo() {
        System.out.println(" Samsung galaxy tab 3 ");
        return "Samsung galaxy tab 3";
    }

    @Override
    public String price() {
        System.out.println(" Rs 45000.00 ");
        return "Rs 45000.00";
    }
}
