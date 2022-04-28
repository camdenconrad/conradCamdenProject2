import java.util.ArrayList;

public class Receipts {

    public String receiptText = "";

    @SuppressWarnings("StringConcatenationInLoop")
    public void generateReceipt(ArrayList<Item> order) {

        // clear receipt
        receiptText = "[RECEIPT]\n";

        // add items
        for (Item item : order) {
            receiptText += item.getName() + ", " + item.getAuthor() + "\n";

        }


    }

}
