import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public class Inventory implements BookstoreSpecification {

    public static ArrayList<Item> inventory = new ArrayList<>(); // master array list - contains ALL inventory
    public static Thread updateInventory;

    public Inventory() {
        // default constructor takes no args

        // thread is created to run in background and check if inventory stock is ever zero,
        // if we no longer have an item we want to remove it entirely from the inventory, rather than have 0 in stock
        // cuz why not? - can cause issues with adding items to order when not careful
        updateInventory = new Thread(() -> {
            while (true) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(0, 1); // limits thread from eating cpu alive - can be increased if program is running sluggish
                } catch (InterruptedException ignored) {
                }
                if (inventory.size() != 0) {
                    try {
                        inventory.removeIf(item -> item.getInventory() == 0); // essentially a for each statement
                    }
                    // ignore exceptions, because thread will re-loop and fix itself
                    //ArrayIndexOutOfBoundsException will be thrown when loading in a new inventory
                    catch (ConcurrentModificationException | NullPointerException |
                           ArrayIndexOutOfBoundsException ignored) {
                        System.err.println("Inventory empty or being updated.");
                    }
                }

            }
        });
        updateInventory.start(); // starts the thread

    }

    // SORTERS

    public static void sortByInventory() {
        inventory.sort((o1, o2) -> (o2.getInventory() - o1.getInventory()));
    }

    public static void sortById() {
        inventory.sort(Comparator.comparingInt(Item::getId));
    }

    public static void sortByTitle() {
        inventory.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    }

    public static void sortByAuthor() {
        inventory.sort((o1, o2) -> o1.getAuthor().compareToIgnoreCase(o2.getAuthor()));
    }

    public static void sortByType() {
        inventory.sort(Comparator.comparing(TypeItem::getType));
    }

    public static void sortByPrice() {
        inventory.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
    }

    // ADD ITEMS
    public void add(Item item) {

        boolean isNewItem = true;

        // check if inventory exists
        if (inventory.size() == 0) {
            inventory.add(item);
        } else {

            // check if item already exists
            // if so increase inventory by 1
            try {
                for (Item itemID : inventory) {
                    if ((itemID.getType().matches(item.getType())) && (Objects.equals(itemID.getAuthor(), item.getAuthor()) && (Objects.equals(itemID.getName(), item.getName())))) {
                        (itemID).addInventory(1);
                        isNewItem = false;
                        break;
                    }
                }
                if (isNewItem) {
                    inventory.add(item);
                }

            } catch (Exception ignored) {
                System.err.println("Missing parameter.");
            }
        }
    }

    public void add(Item item, int amount) { // inventory add - special amount

        boolean isNewItem = true;

        // check if inventory exists
        if (inventory.size() == 0) {
            item.addInventory(amount - 1);
            inventory.add(item);
        } else {

            // check if item already exists
            // if so increase inventory by 1
            try {
                for (Item itemID : inventory) {
                    if ((itemID.getType().matches(item.getType())) && (Objects.equals(itemID.getAuthor(), item.getAuthor()) && (Objects.equals(itemID.getName(), item.getName())))) {
                        (itemID).addInventory(amount);
                        isNewItem = false;
                        break;
                    }
                }
                if (isNewItem) {
                    item.addInventory(amount - 1);
                    inventory.add(item);
                }

            } catch (Exception ignored) {
                System.err.println("Missing parameter.");
            }
        }
    }

    // REMOVE ITEMS

    // GET INVENTORY

    // Check status

    // sell inventory

    public void sell(Item item, int amount) {
        // locate item
        for (Item itemID : inventory) {
            if (itemID.equals(item)) {
                itemID.sellInventory(amount);
            }

        }
    }

    // accessors

    public Item getItemAt(int position) {
        if (position < 0) {
            // itemDoesNotExist
            return null;
        }

        return inventory.get(position);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public Item getItem(String title, String author) {
        for (Item itemID : inventory) {
            if ((itemID.getAuthor().equals(author)) && (itemID.getName().equals(title))) {
                return getItemAt(inventory.indexOf(itemID));
            }
        }
        return getItemAt(inventory.indexOf(-1));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public Item getItemByID(int id) {
        for (Item itemID : inventory) {
            if (itemID.getId() == id) {
                return getItemAt(inventory.indexOf(itemID));
            }
        }
        return getItemAt(inventory.indexOf(-1));
    }

    // implemented methods
    @Override
    public void restockProduct(int productId, int amount) {
        getItemByID(productId).addInventory(amount);
    }


    public double getInventoryTotal() {
        double value = 0;
        for (Item item : inventory) {
            value += item.getPrice() * item.getInventory();
        }

        return value;
    }
}
