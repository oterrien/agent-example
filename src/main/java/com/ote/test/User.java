package com.ote.test;

import lombok.*;

@Data
public class User {

    @NotifyPropertyChange(method="notifyObservers")
    private String login;

    @NotifyPropertyChange(method="notifyObservers")
    private String password;

    public User() {
    }

    private void notifyObservers(){
        System.out.println("UPDATED");
    }
}
