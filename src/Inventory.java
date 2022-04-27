import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public class Inventory implements BookstoreSpecification {

    public static ArrayList<Item> inventory = new ArrayList<>(); // master array list - contains ALL inventory
    public static Thread updateInventory;

    public static Thread sortInventory;

    public Inventory() {
        // default constructor takes no args

        // thread is created to run in background and check if inventory stock is ever zero,
        // if we no longer have an item we want to remove it entirely from the inventory, rather than have 0 in stock
        // cuz why not? - can cause issues with adding items to order when not careful
        updateInventory = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(0,1); // limits thread from eating cpu alive - can be increased if program is running sluggish
                    } catch (InterruptedException ignored) {
                    }
                    if (inventory.size() != 0) {
                        try {
                            inventory.removeIf(item -> item.getInventory() == 0); // essentially a for each statement
                        }
                        // ignore exceptions, because thread will re-loop and fix itself
                        //ArrayIndexOutOfBoundsException will be thrown when loading in a new inventory
                        catch (ConcurrentModificationException | NullPointerException | ArrayIndexOutOfBoundsException ignored) {
                        }
                    }

                }
            }
        });
        updateInventory.start(); // starts the thread

    }

    public static void updateInventoryWait() throws InterruptedException {
        updateInventory.wait();
    }

    public static void updateInventoryNotify() {
        updateInventory.notify();
    }

    // SORTERS

    public static void sortByInventory() {
        inventory.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return (o2.getInventory() - o1.getInventory());
            }
        });
    }

    public static void sortById() {
        inventory.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return (o1.getId() - o2.getId());
            }
        });
    }

    public static void sortByTitle() {
        inventory.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    public static void sortByAuthor() {
        inventory.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getAuthor().compareToIgnoreCase(o2.getAuthor());
            }
        });
    }

    public static void sortByType() {
        inventory.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
    }

    public static void sortByPrice() {
        inventory.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Double.compare(o2.getPrice(), o1.getPrice());
            }
        });
    }

    public Item getItem(Item item) {
        return inventory.get(inventory.indexOf(item));
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
            }
        }
    }

    // REMOVE ITEMS

    /**
     * Removes an item
     *
     * @param item is meant to contain item types to be sold. i.e. Books, CDs, DVDs
     */
    public void remove(Item item) {
        inventory.remove(item);
    }

    // GET INVENTORY

    public String getInventory() {
        int i = 0;
        StringBuilder localHolder = new StringBuilder();
        localHolder.append("INVENTORY:\n");

        try {
            for (Item objectID : inventory) {
                i++;
                localHolder.append("\n").append(i).append("] ").append(objectID.toString()).append("\n");
            }
        }
        // can throw when user adds an item // throws when looking for an item that has been automatically removed update thread, or user
        //ArrayIndexOutOfBoundsException will be thrown when loading in a new inventory
        catch (ConcurrentModificationException | NullPointerException ignored) {
            return getInventory(); // we can use recursive call because the value will be reset and the method will re-loop
        }

        return localHolder.toString();
    }

    // Check status

    /**
     * Checks to see if an item has inventory
     *
     * @param item //
     */
    public int checkStatus(Item item) {

        // check if item exists
        if (inventory.contains(item)) {

            // find item
            for (Item objectID : inventory) {
                if (objectID == item) {
                    return item.getInventory();
                }

            }
        }
        return -1;

    }

    // advanced checkStatus - checks for item inventory based on author / artist
    public int checkStatus(String author) {

        // check if item exists
        for (Item itemID : inventory) {
            if (itemID.getAuthor().equals(author))
                return itemID.getInventory();
        }
        return -1;

    }

    // advanced checkStatus - checks for item inventory based on AUTHOR AND TITLE
    public int checkStatus(String title, String author) {

        // check if item exists
        for (Item itemID : inventory) {
            if ((itemID.getAuthor().equals(author)) && (itemID.getName().equals(title)))
                return itemID.getInventory();
        }
        return -1;

    }

    // sell inventory

    public void sell(Item item, int amount) {
        // locate item
        for (Item itemID : inventory) {
            if (itemID.equals(item)) {
                itemID.sellInventory(amount);
            }

        }
    }

    public void sell(String title, String author, int amount) {
        // locate item
        for (Item itemID : inventory) {
            if ((itemID.getAuthor().equals(author)) && (itemID.getName().equals(title))) {
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

    public Item getItem(String title, String author) {
        for (Item itemID : inventory) {
            if ((itemID.getAuthor().equals(author)) && (itemID.getName().equals(title))) {
                return getItemAt(inventory.indexOf(itemID));
            }
        }
        return getItemAt(inventory.indexOf(-1));
    }

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
