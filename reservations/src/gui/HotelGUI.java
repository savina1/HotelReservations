package gui;
import manager.ReservationManager;
import model.Guest;
import model.Reservations;
import model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class HotelGUI extends JFrame {
	 private static final long serialVersionUID = 1L; // Добавен serialVersionUID

	    private ReservationManager manager = new ReservationManager();
	    private DefaultTableModel tableModel;

	    public HotelGUI() {
	        setTitle("Hotel Reservation System");
	        setSize(700, 400);
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        initComponents();
	    }

	    private void initComponents() {
	        tableModel = new DefaultTableModel(new Object[]{"Гост", "Стая", "Тип", "Цена", "Настаняване", "Напускане"}, 0);
	        JTable table = new JTable(tableModel);

	        JButton addBtn = new JButton("Добави резервация");
	        JButton searchBtn = new JButton("Търси гост");
	        JButton saveBtn = new JButton("Запази");

	        addBtn.addActionListener(e -> addReservation());
	        searchBtn.addActionListener(e -> searchReservation());
	        saveBtn.addActionListener(e -> saveData());

	        JPanel panel = new JPanel();
	        panel.add(addBtn);
	        panel.add(searchBtn);
	        panel.add(saveBtn);

	        add(new JScrollPane(table), BorderLayout.CENTER);
	        add(panel, BorderLayout.SOUTH);
	    }

	    private void addReservation() {
	        try {
	            String guestName = JOptionPane.showInputDialog(this, "Име на гост:");
	            int roomNumber = Integer.parseInt(JOptionPane.showInputDialog(this, "Номер на стая:"));
	            String type = JOptionPane.showInputDialog(this, "Тип стая:");
	            double price = Double.parseDouble(JOptionPane.showInputDialog(this, "Цена:"));
	            LocalDate checkIn = LocalDate.parse(JOptionPane.showInputDialog(this, "Дата на настаняване (YYYY-MM-DD):"));
	            LocalDate checkOut = LocalDate.parse(JOptionPane.showInputDialog(this, "Дата на напускане (YYYY-MM-DD):"));

	            Room room = new Room(roomNumber, type, price);
	            Guest guest = new Guest(guestName);
	            Reservations res = new Reservations(guest, room, checkIn, checkOut);

	            if (manager.addReservation(res)) {
	                tableModel.addRow(new Object[]{guestName, roomNumber, type, price, checkIn, checkOut});
	            } else {
	                JOptionPane.showMessageDialog(this, "Стаята е заета за тези дати!", "Грешка", JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this, "Невалидни данни!", "Грешка", JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    private void searchReservation() {
	        String name = JOptionPane.showInputDialog(this, "Име на гост:");
	        Reservations res = manager.searchByGuest(name);
	        if (res != null) {
	            JOptionPane.showMessageDialog(this, "Намерена резервация: " + res);
	        } else {
	            JOptionPane.showMessageDialog(this, "Гостът не е намерен.");
	        }
	    }

	    private void saveData() {
	        try {
	            manager.saveToFile("reservations.txt");
	            JOptionPane.showMessageDialog(this, "Запазено успешно!");
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(this, "Грешка при запис!", "Грешка", JOptionPane.ERROR_MESSAGE);
	        }
	    }

}
