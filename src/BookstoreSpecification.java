public interface BookstoreSpecification {

    void restockProduct(int productId, int amount);

    double getInventoryTotal();


}
