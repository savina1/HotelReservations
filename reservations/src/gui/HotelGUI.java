import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HotelGUI extends JFrame {
    private JComboBox<String> roomComboBox;
    private JComboBox<String> statusFilter;
    private JTextField guestField;
    private JDateChooser checkInChooser;
    private JDateChooser checkOutChooser;
    private JTextArea occupiedDatesArea;
    private DefaultTableModel tableModel;
    private JTable reservationTable;
    private List<String[]> roomList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private int editingRow = -1;

    public HotelGUI() {
        setTitle("Hotel Reservation System");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadRooms();

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        guestField = new JTextField();
        roomComboBox = new JComboBox<>();
        updateRoomComboBox("Всички");
        checkInChooser = new JDateChooser();
        checkOutChooser = new JDateChooser();
        JButton addButton = new JButton("Запази / Добави");

        statusFilter = new JComboBox<>(new String[]{"Всички", "Свободна", "Заета"});
        statusFilter.addActionListener(e -> updateRoomComboBox((String) statusFilter.getSelectedItem()));

        occupiedDatesArea = new JTextArea(3, 20);
        occupiedDatesArea.setEditable(false);
        roomComboBox.addActionListener(e -> showOccupiedDates((String) roomComboBox.getSelectedItem()));

        inputPanel.add(new JLabel("Име на гост:"));
        inputPanel.add(guestField);
        inputPanel.add(new JLabel("Филтър по статус:"));
        inputPanel.add(statusFilter);
        inputPanel.add(new JLabel("Стая:"));
        inputPanel.add(roomComboBox);
        inputPanel.add(new JLabel("Настаняване:"));
        inputPanel.add(checkInChooser);
        inputPanel.add(new JLabel("Напускане:"));
        inputPanel.add(checkOutChooser);
        inputPanel.add(new JLabel("Заети дати:"));
        inputPanel.add(new JScrollPane(occupiedDatesArea));
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Гост", "Стая", "Настаняване", "Напускане", "Редактирай", "Изтрий"}, 0);
        reservationTable = new JTable(tableModel);
        reservationTable.getColumn("Редактирай").setCellRenderer(new ButtonRenderer());
        reservationTable.getColumn("Редактирай").setCellEditor(new ButtonEditor(new JCheckBox(), true, this));
        reservationTable.getColumn("Изтрий").setCellRenderer(new ButtonRenderer());
        reservationTable.getColumn("Изтрий").setCellEditor(new ButtonEditor(new JCheckBox(), false, this));

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> saveReservation());

        loadReservations();
        setVisible(true);
    }

    public void editReservation(int row) {
        editingRow = row;
        guestField.setText((String) tableModel.getValueAt(row, 0));
        roomComboBox.setSelectedItem(tableModel.getValueAt(row, 1));
        try {
            checkInChooser.setDate(dateFormat.parse((String) tableModel.getValueAt(row, 2)));
            checkOutChooser.setDate(dateFormat.parse((String) tableModel.getValueAt(row, 3)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveReservation() {
        String guest = guestField.getText();
        String room = (String) roomComboBox.getSelectedItem();
        Date checkIn = checkInChooser.getDate();
        Date checkOut = checkOutChooser.getDate();

        if (guest.isEmpty() || room == null || checkIn == null || checkOut == null) {
            JOptionPane.showMessageDialog(this, "Моля, попълнете всички полета.");
            return;
        }

        if (hasDateConflict(room, checkIn, checkOut, editingRow)) {
            JOptionPane.showMessageDialog(this, "Стаята вече е заета в избрания период.");
            return;
        }

        if (editingRow >= 0) {
            tableModel.setValueAt(guest, editingRow, 0);
            tableModel.setValueAt(room, editingRow, 1);
            tableModel.setValueAt(dateFormat.format(checkIn), editingRow, 2);
            tableModel.setValueAt(dateFormat.format(checkOut), editingRow, 3);
            editingRow = -1;
        } else {
            tableModel.addRow(new Object[]{guest, room, dateFormat.format(checkIn), dateFormat.format(checkOut), "Редактирай", "Изтрий"});
        }

        updateRoomStatus(room, "Заета");
        saveReservations();
    }

    private boolean hasDateConflict(String room, Date newStart, Date newEnd, int skipRow) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i == skipRow) continue;
            String rowRoom = (String) tableModel.getValueAt(i, 1);
            if (!room.equals(rowRoom)) continue;
            try {
                Date start = dateFormat.parse((String) tableModel.getValueAt(i, 2));
                Date end = dateFormat.parse((String) tableModel.getValueAt(i, 3));
                if (newStart.before(end) && newEnd.after(start)) {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void showOccupiedDates(String room) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(room)) {
                builder.append(tableModel.getValueAt(i, 2)).append(" → ").append(tableModel.getValueAt(i, 3)).append("\n");
            }
        }
        occupiedDatesArea.setText(builder.toString());
    }

    private void updateRoomComboBox(String status) {
        roomComboBox.removeAllItems();
        for (String[] room : roomList) {
            if (status.equals("Всички") || room[1].equals(status)) {
                roomComboBox.addItem(room[0]);
            }
        }
    }

    private void loadRooms() {
        roomList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("reservations/data/rooms.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                roomList.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRooms() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reservations/data/rooms.txt"))) {
            for (String[] room : roomList) {
                writer.write(room[0] + "," + room[1]);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRoomStatus(String roomNumber, String newStatus) {
        for (String[] room : roomList) {
            if (room[0].equals(roomNumber)) {
                room[1] = newStatus;
                break;
            }
        }
        saveRooms();
    }

    private void saveReservations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reservations/data/reservations.txt"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(tableModel.getValueAt(i, 0) + "," +
                             tableModel.getValueAt(i, 1) + "," +
                             tableModel.getValueAt(i, 2) + "," +
                             tableModel.getValueAt(i, 3));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        try (BufferedReader reader = new BufferedReader(new FileReader("reservations/data/reservations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    tableModel.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], "Редактирай", "Изтрий"});
                    updateRoomStatus(parts[1], "Заета");
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelGUI::new);
    }
}

class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public ButtonRenderer() { setOpaque(true); }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private boolean isEdit;
    private JTable table;
    private HotelGUI gui;

    public ButtonEditor(JCheckBox checkBox, boolean isEdit, HotelGUI gui) {
        super(checkBox);
        this.isEdit = isEdit;
        this.gui = gui;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (isEdit) {
                gui.editReservation(row);
            } else {
                ((DefaultTableModel) table.getModel()).removeRow(row);
                gui.saveReservations();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        button.setText((value == null) ? "" : value.toString());
        return button;
    }

    public Object getCellEditorValue() {
        return button.getText();
    }
}
