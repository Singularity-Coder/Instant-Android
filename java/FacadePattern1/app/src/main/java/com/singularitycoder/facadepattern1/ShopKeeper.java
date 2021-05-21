package com.singularitycoder.facadepattern1;

import com.singularitycoder.facadepattern1.shops.Blackberry;
import com.singularitycoder.facadepattern1.shops.Iphone;
import com.singularitycoder.facadepattern1.shops.Samsung;

public class ShopKeeper {
    private MobileShop iphone;
    private MobileShop samsung;
    private MobileShop blackberry;

    public ShopKeeper() {
        iphone = new Iphone();
        samsung = new Samsung();
        blackberry = new Blackberry();
    }

    public String iphoneSale() {
        return "You purchased " + iphone.modelNo() + " for " + iphone.price() + " Rs.";
    }

    public String samsungSale() {
        return "You purchased " + samsung.modelNo() + " for " + samsung.price() + " Rs.";
    }

    public String blackberrySale() {
        return "You purchased " + blackberry.modelNo() + " for " + blackberry.price() + " Rs.";
    }
}
