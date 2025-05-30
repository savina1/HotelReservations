package model;

public class Room {

    private int number;
    private String type;
    private double price;
    private boolean occupied;

    public Room(int number, String type, double price) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.occupied = false;
    }

    public int getNumber() { return number; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    @Override
    public String toString() {
        return number + ", " + type + ", " + price + ", " + (occupied ? "Заета" : "Свободна");
    }

}




