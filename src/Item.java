import java.util.Random;

public abstract class Item implements Comparable<Item>, TypeItem {
    public String name;
    public String author;
    public int id;
    public int inventory;
    public double price;

    public Item(String name, String author, int inventory, double price) {
        this.name = name;
        this.author = author;
        this.id = Math.abs(new Random().nextInt()); // just for fun, so that we don't have to specify a new id number each time
        this.inventory = inventory;
        this.price = price;
    }

    public Item(int id, String name, String author, int inventory, double price) {
        this.name = name;
        this.author = author;
        this.id = id;
        this.inventory = inventory;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Inventory

    public void sellInventory(int sold) {
        this.inventory -= sold;
    }

    public void addInventory(int added) {
        this.inventory += added;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String toString() {
        return ("%10d | %4s | %-40s | %-25s | In stock: %3d | Price: $%6.2f".formatted(this.getId(), this.getType(), getSubstring(this.getName(), 39), getSubstring(this.getAuthor(), 24), this.getInventory(), this.getPrice()));
    }

    // cleans up names that are too long to display cleanly in inventory
    private String getSubstring(String toModify, int endIndex) {
        try {
            return toModify.substring(0, endIndex);
        } catch (StringIndexOutOfBoundsException ignored) {
            return toModify;
        }
    }

}
