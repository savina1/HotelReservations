package model;

public class Guest {
    private String name;

    public Guest(String name) { this.name = name; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }

}

