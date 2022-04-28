public class DVD extends Item {

    private final double runTime = 0.0;
    private String type; // movie, tv show
    private String genre;
    private String director;

    public DVD(int id, String name, String author, int inventory, double price) {
        super(id, name, author, inventory, price);
    }

    public DVD(String name, String author, int inventory, double price) {
        super(name, author, inventory, price);
    }

    public DVD(String name, String author) {
        super(name, author, 1, 9.99);
    }

    // constructor made to deal with duplicate read in files
    public DVD(int id, String name, String author, double price) {
        super(id, name, author, 1, price);
    }


    @Override
    public int compareTo(Item item) {
        return Integer.compare(item.getInventory(), this.getInventory());
    }

    @Override
    public String getType() {
        return "DVD";
    }
}
