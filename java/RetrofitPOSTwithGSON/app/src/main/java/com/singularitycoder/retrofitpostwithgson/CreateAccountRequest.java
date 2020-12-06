package com.singularitycoder.retrofitpostwithgson;

public class CreateAccountRequest {

    // Retrofit uses GSON by default so CreateAccountRequest instances will be serialized as JSON as the entire body of the request. So these fields
    private String user_profile_image;
    private String user_name;
    private String user_email;
    private String user_phone;
    private String user_password;

    public CreateAccountRequest(String userProfileImage, String userName, String userEmail, String userPhone, String userPassword) {
        this.user_profile_image = userProfileImage;
        this.user_name = userName;
        this.user_email = userEmail;
        this.user_phone = userPhone;
        this.user_password = userPassword;
    }
}
