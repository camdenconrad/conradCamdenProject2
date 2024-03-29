import java.util.ArrayList;

public class Order {

    private final ArrayList<Item> order = new ArrayList<>();
    public double orderCost = 0.0;
    Receipts currentReceipts = null;
    private Membership member = new Membership(); // guest member

    public Order() {
    }

    public Order(Membership member) {
        this.member = member;
        member.setOrder(this);
    }

    /*
    Needs to be added:
    addToOrder limit to in-stock inventory
     */

    public ArrayList<Item> getArray() {
        return order;
    }

    public void addToOrder(Item item) {
        // check if item is in inventory
        if (Inventory.inventory.contains(item)) {
            this.order.add(item);

            // add price
            orderCost += item.getPrice();

        }
    }

    public boolean subtractFromOrder(Item item) {
        while (this.order.contains(item)) {
            this.order.remove(item);
        }
        return true;
    }

    public void purchaseOrder(Inventory inventory) {
        if (member.isPremium()) {
            if (member.hasPayed()) {
                orderCost = member.premiumPrice();
            } else
                orderCost = 0;
        } else
            orderCost = 0;

        for (Item item : order) {
            orderCost += item.getPrice();
            inventory.sell(item, 1);
        }

        Receipts receipt = new Receipts();
        this.currentReceipts = receipt;

        AllOrders.addOrder(order); // add to order list

        receipt.generateReceipt(order);
        member.addRecipient((receipt));
        member.addNewTransaction(getOrderCost());

        if (member.isPremium() && member.hasPayed()) {
            member.setHasPayed(true);
        }

        //clear inventory when were done with purchase
        this.order.clear();
        orderCost = 0; // reset order cost

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
