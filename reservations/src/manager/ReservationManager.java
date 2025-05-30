package manager;
import model.Guest;
import model.Reservations;
import model.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ReservationManager {
    private List<Reservations> reservations = new ArrayList<>();
    private RoomTree roomTree = new RoomTree();

    public boolean addReservation(Reservations res) {
        for (Reservations r : reservations) {
            if (r.getRoom().getNumber() == res.getRoom().getNumber() &&
                !(res.getCheckOut().isBefore(r.getCheckIn()) || res.getCheckIn().isAfter(r.getCheckOut()))) {
                return false;
            }
        }
        reservations.add(res);
        res.getRoom().setOccupied(true);
        roomTree.insert(res.getRoom());
        return true;
    }

    public void sortReservations() { InsertionSort.sort(reservations); }

    public Reservations searchByGuest(String name) {
        for (Reservations r : reservations) {
            if (r.getGuest().getName().equalsIgnoreCase(name)) return r;
        }
        return null;
    }

    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Reservations r : reservations) {
                writer.write(r.toString());
                writer.newLine();
            }
        }
    }

    public List<Reservations> getReservations() { return reservations; }

}
