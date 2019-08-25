package com.example.flohmarkt;

public class Article {
    int id;
    String name;
    double price;
    String username;
    String email;
    String phone;
    Double lat;
    Double lon;
    String created;
    String modified;

    public Article(int id, String name, double price, String username, String email, String phone, Double lat, Double lon, String created, String modified) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.lat = lat;
        this.lon = lon;
        this.created = created;
        this.modified = modified;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }
}
