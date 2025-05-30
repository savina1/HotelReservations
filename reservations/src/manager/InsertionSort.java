package manager;
import model.Reservations;
import java.util.List;

public class InsertionSort {
	public static void sort(List<Reservations> list) {
        for (int i = 1; i < list.size(); i++) {
            Reservations key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).getCheckIn().isAfter(key.getCheckIn())) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

}

    
