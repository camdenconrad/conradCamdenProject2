import java.util.ArrayList;

public class Receipts {

    public String receiptText = "";

    public void generateReceipt(ArrayList<Item> order) {

        // clear receipt
        receiptText = "[RECEIPT]\n";

        // add items
        for (Item item : order) {
            receiptText += item.getName() + ", " + item.getAuthor() + "\n";

        }


    }

    public String getReceipt(ArrayList<Item> order) {
        generateReceipt(order);

        return receiptText;
    }
}
