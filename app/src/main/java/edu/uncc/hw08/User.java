package edu.uncc.hw08;

public class User {
    String name, uid, email;
    boolean isLoggedIn;

    public User(String name, String uid, String email, boolean isLoggedIn) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.isLoggedIn = isLoggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    @Override
    public String toString() {
        return name;
    }
}
