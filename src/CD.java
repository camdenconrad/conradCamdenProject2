public class CD extends Item {

    private final String publisher = null;
    private final double runTime = 0.0;
    private final String genre = null;
    private final String type = null; // single, ep, album

    public CD(int id, String name, String author, int inventory, double price) {
        super(id, name, author, inventory, price);
    }

    public CD(String name, String author, int inventory, double price) {
        super(name, author, inventory, price);
    }

    public CD(String name, String author) {
        super(name, author, 1, 9.99);
    }

    // constructor made to deal with duplicate read in files
    public CD(int id, String name, String author, double price) {
        super(id, name, author, 1, price);
    }


    @Override
    public int compareTo(Item item) {
        return Integer.compare(item.getInventory(), this.getInventory());
    }

    @Override
    public String getType() {
        return "CD";
    }
}
