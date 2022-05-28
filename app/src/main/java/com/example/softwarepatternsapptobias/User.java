package com.example.softwarepatternsapptobias;

import com.example.softwarepatternsapptobias.Product;
import com.example.softwarepatternsapptobias.Strategy;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class User implements Serializable, Strategy {

        public String uid, fullname, phoneNo, email, userType;
        private ArrayList<Product> cart;
        private boolean student;

    public User() {
    }
    public User(String fullname, String phoneNo, String email, String userType) {
    }

    public User(String fullname, String phoneNo, String email, String userType, boolean student) {
        this.fullname = fullname;
        this.phoneNo = phoneNo;
        this.email = email;
        this.userType = userType;
        this.student = student;
        this.cart = new ArrayList<>();

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<Product> getCart() {
        return cart;
    }

    public void setCart(ArrayList<Product> cart) {
        this.cart = cart;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

//    @Override
//    public String toString() {
//        return "User{" +
//                "uid='" + uid + '\'' +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", address='" + address + '\'' +
//                ", admin=" + admin +
//                ", cart=" + cart +
//                '}';
//    }

    @Override
    public double calculateDiscount(double price, double rate) {
        double substraction = price * rate;
        return price - substraction;
    }
}
