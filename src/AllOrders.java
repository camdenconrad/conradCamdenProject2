import java.util.ArrayList;

public class AllOrders {
    public static ArrayList<Item> orderList = new ArrayList<>();

    public static void addOrder(ArrayList<Item> order) {
        orderList.addAll(order);
    }
}
