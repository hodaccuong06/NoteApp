package com.example.noteapp.Domain;

public class User {
    private String Name;
    private String PhoneNumber;
    private String Password;
    private String profileImageUrl;
    private boolean isBlock;
    private String BlockPassword;


    public User() {
    }


    public User(String name, String password, String blockPassword) {
        Name = name;
        Password = password;
        profileImageUrl = "";
        BlockPassword= blockPassword;
        isBlock = false;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public String getBlockPassword() {
        return BlockPassword;
    }

    public void setBlockPassword(String blockPassword) {
        BlockPassword = blockPassword;
    }
}
