public class Books extends Item {

    private String ISBN;
    private String genre;
    private String publisher;

    //productID, type, title, author, numInStock, price


    public Books(String name, String author, int inventory, double price) {
        super(name, author, inventory, price);
    }

    public Books(int id, String name, String author, int inventory, double price) {
        super(id, name, author, inventory, price);
    }

    public Books(String name, String author) {
        super(name, author, 1, 9.99);
    }

    // constructor made to deal with duplicate read in files
    public Books(int id, String name, String author, double price) {
        super(id, name, author, 1, price);
    }

    @Override
    public int compareTo(Item item) {
        if (item.getInventory() > this.getInventory())
            return 1;
        if (item.getInventory() < this.getInventory())
            return -1;
        return 0;
    }

    @Override
    public String getType() {
        return "Book";
    }
}
