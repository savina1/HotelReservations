package manager;

import model.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomTree {
    private class Node {
        Room room;
        Node left, right;
        Node(Room room) { this.room = room; }
    }

    private Node root;

    public void insert(Room room) { root = insertRec(root, room); }

    private Node insertRec(Node root, Room room) {
        if (root == null) return new Node(room);
        if (room.getPrice() < root.room.getPrice())
            root.left = insertRec(root.left, room);
        else
            root.right = insertRec(root.right, room);
        return root;
    }

    public List<Room> inOrder() {
        List<Room> list = new ArrayList<>();
        inOrderRec(root, list);
        return list;
    }

    private void inOrderRec(Node node, List<Room> list) {
        if (node != null) {
            inOrderRec(node.left, list);
            list.add(node.room);
            inOrderRec(node.right, list);
        }
    }


}


