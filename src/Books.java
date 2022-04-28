public class Books extends Item {

    private String ISBN;
    private String genre;
    private String publisher;

    //productID, type, title, author, numInStock, price


    public Books(String name, String author) {
        super(name, author, 1, 9.99);
    }

    // constructor made to deal with duplicate read in files
    public Books(int id, String name, String author, double price) {
        super(id, name, author, 1, price);
    }

    @Override
    public int compareTo(Item item) {
        return Integer.compare(item.getInventory(), this.getInventory());
    }

    @Override
    public String getType() {
        return "Book";
    }
}
