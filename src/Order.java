import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Order {

    public ArrayList<Item> getArray() {
        return order;
    }

    private final ArrayList<Item> order = new ArrayList<>();
    public double orderCost = 0.0;
    Receipts currentReceipts = null;
    private Membership member = new Membership(); // guest member

    public Order() {
    }

    /*
    Needs to be added:
    addToOrder limit to in-stock inventory
     */

    public Order(Membership member) {
        this.member = member;
        member.setOrder(this);
    }

    public Order(AtomicReference<Membership> membership) {
    }

    public Receipts getCurrentReceipts() {
        return currentReceipts;
    }

    public boolean addToOrder(Item item) {
        // check if item is in inventory
        if (Inventory.inventory.contains(item)) {
            this.order.add(item);

            // add price
            orderCost += item.getPrice();

            return true;
        }
        return false;
    }

    public boolean addToOrder(Item item, int amount) {
        if (Inventory.inventory.contains(item)) {
            if (item.getInventory() >= amount) {
                for (int i = 0; i < amount; i++) {
                    this.order.add(item);
                    orderCost += item.getPrice();
                    item.sellInventory(1);
                }
                return true;
            }
        }
        return false;
    }

    public boolean subtractFromOrder(Item item) {
        while (this.order.contains(item)) {
            this.order.remove(item);
        }
        return true;
    }

    public boolean subtractFromOrder(Item item, int amount) {
        for (int i = 0; i < amount; i++) {
            this.order.remove(item);
        }
        return true;
    }

    public void purchaseOrder(Inventory inventory) {
        if (member.isPremium()) {
            if (!member.hasPayed()) {
                orderCost = member.premiumPrice();
            } else
                orderCost = 0;
        } else
            orderCost = 0;

        for (Item item : order) {
            orderCost += item.getPrice();
            inventory.sell(item, 0);
        }

        Receipts receipt = new Receipts();
        this.currentReceipts = receipt;

        receipt.generateReceipt(order);
        member.addRecipient((receipt));
        member.addNewTransaction(getOrderCost());

        if (member.isPremium() && !member.hasPayed()) {
            member.setHasPayed(true);
        }

        //System.out.println(receipt.getReceipt(order));

    }

    public String toString() {
        StringBuilder list = new StringBuilder();
        list.append("[ORDER]\n");
        for (Item item : order) {
            list.append(item.toString()).append("\n");
        }

        return list.toString();
    }

    public void setMember(Membership member) {
        this.member = member;
    }

    public double getOrderCost() {
        return orderCost;
    }

}
