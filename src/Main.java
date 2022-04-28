import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final Inventory inventory = new Inventory();
    private static final Scanner doesInput = new Scanner(System.in);
    public static GUI gui;

    public static File inventoryFile;

    public static FlatLightLaf laf = new FlatLightLaf();


    // MAIN

    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException {

        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#F37A10")); // changes accent color

        UIManager.setLookAndFeel(laf); //new FlatDarkLaf()

        gui = new GUI();

        Membership membership = null;

        // ADD INVENTORY
        boolean continues = true; // controls flow

        // first thing read in data
        try {
            readInInventory();
        } catch (FileNotFoundException ignored) {
            System.out.println("Inventory file not found.\n");
        }

        while (continues) {
            System.out.println("Enter type of inventory to add: ");
            System.out.println("""
                    1. Book
                    2. CD
                    3. DVD
                    4. Add Default Inventory
                    5. Continue""");

            switch (doesInput.nextInt()) {
                case 1 -> {
                    doesInput.nextLine();
                    createBook(doesInput, inventory);
                }
                case 2 -> {
                    doesInput.nextLine();
                    createCD(doesInput, inventory);
                }
                case 3 -> {
                    doesInput.nextLine();
                    createDVD(doesInput, inventory);
                }
                case 4 -> {
                    createDefaultBook();
                    createDefaultCD();
                    createDefaultDVD();
                }
                case 5 -> continues = false;
            }
        }

        continues = true; // controls flow
        gui.getForm().setProgress(30);

        // Manage inventory
        System.out.println(inventory.getInventory() + "\n");

        while (continues) {
            System.out.println("""
                    1. Restock Additional Inventory
                    2. Display Inventory Total
                    3. Compare Inventory
                    4. Finish""");
            switch (doesInput.nextInt()) {
                case 1 -> restockInventory();
                case 2 -> System.out.printf("Total: $%.2f\n", inventory.getInventoryTotal());
                case 3 -> smartDisplay(compareInventory());
                case 4 -> continues = false;
            }
        }
        gui.getForm().setProgress(50);

        continues = true; // controls flow

        // SIGN UP MEMBER
        System.out.println("Sign In or continue as guest: ");
        System.out.println("""
                1. Sign Up
                2. Continue as Guest""");

        while (continues) {
            switch (doesInput.nextInt()) {
                case 1 -> {
                    doesInput.nextLine();
                    membership = createNewMember();
                    System.out.println("Would you like to be premium member? (true, or false): ");
                    membership.setPremium(isPremium(doesInput));

                    continues = false;

                }
                case 2 -> {
                    membership = new Membership();
                    continues = false;
                }
            }
        }
        gui.getForm().setProgress(60);

        Order order = new Order(membership); // order declared here because it needs membership information

        continues = true; // controls flow

        // PURCHASE ORDER
        while (continues) {
            System.out.println(inventory.getInventory()); // INVENTORY CHANGES WHEN ITEMS ADDED TO CART
            System.out.println("Enter item number to add to order, or 0 to purchase");
            int itemToAdd = (doesInput.nextInt() - 1);
            if (itemToAdd == -1) {
                continues = false;
            } else {
                try {
                    order.addToOrder(inventory.getItemAt(itemToAdd), 1);
                } catch (Exception ignored) {
                }
            }

            System.out.printf("Current total: $%.2f\n\n", order.getOrderCost());
        }
        gui.getForm().setProgress(100);

        order.purchaseOrder(inventory);
        System.out.println(membership.getLastReceipt());
        System.out.println("Thank you for your purchase " + membership.getMemberFirstName() + " " + membership.getMemberLastName() + "!");

        System.out.printf("Total: $%.2f\n", order.getOrderCost());

        System.out.println("\nUpdated inventory:");
        System.out.println(inventory.getInventory());

        createInventoryFile();

        System.exit(0); // although improper, this closes the thread that would otherwise keep the program alive

    }


    // METHODS
    //
    //

    // write new inventory
    public static void createInventoryFile() throws IOException {
        try {
            FileWriter writer = new FileWriter(inventoryFile.getParent() + "/inventoryUpdated.csv"); // this saves the updated inventory file at the same place that we opened the initial file from

            writeFile(writer);
        }
        // I have no idea why this gets thrown
        catch (ConcurrentModificationException ignored) {
            createInventoryFile(); // try until saving works
        }
    }

    // reads inventory off a file
    public static void readInInventory() throws FileNotFoundException {

        System.out.print("Enter file path for inventory: ");
        //Scanner reader = new Scanner(new File(new Scanner(System.in).nextLine()));

        inventoryFile = gui.findFile();

        try {
            Scanner reader = new Scanner(inventoryFile);
            //Scanner reader = new Scanner(new File("inventory.csv"));

            while (reader.hasNextLine()) {
                //productID, type, title, author, numInStock, price
                String[] thisLine = reader.nextLine().strip().split(",", 6);

                int quickId = Integer.parseInt(thisLine[0]);
                String quickType = thisLine[1].strip();

                if (quickType.equalsIgnoreCase(("book"))) {
                    inventory.add(new Books(
                                    quickId,
                                    thisLine[2].strip(),
                                    thisLine[3].strip(),
                                    Double.parseDouble(thisLine[5].strip())
                            ), Integer.parseInt(thisLine[4].strip()) // deals with duplicates
                    );
                } else if (quickType.equalsIgnoreCase(("dvd"))) {
                    inventory.add(new DVD(
                                    quickId,
                                    thisLine[2].strip(),
                                    thisLine[3].strip(),
                                    Double.parseDouble(thisLine[5].strip())
                            ), Integer.parseInt(thisLine[4].strip()) // deals with duplicates
                    );
                } else if (quickType.equalsIgnoreCase(("cd"))) {
                    inventory.add(new CD(
                                    quickId,
                                    thisLine[2].strip(),
                                    thisLine[3].strip(),
                                    Double.parseDouble(thisLine[5].strip())
                            ), Integer.parseInt(thisLine[4].strip()) // deals with duplicates
                    );
                }


            }

        } catch (NullPointerException ignored) {
            System.out.println("File manager closed.");
        }

    }


    private static void createDefaultBook() {
        String name = "How to get better at coding!";
        String author = "Someone smarter than me";
        Random random = new Random();

        inventory.add(new Books(name, author), random.nextInt(10) + 1);
        try {
            inventory.getItem(name, author).setPrice(19.99);
        } catch (Exception ignored) {
        }
    }

    private static void createDefaultCD() {
        String name = "Try to Catch me";
        String author = "The Exceptions";
        Random random = new Random();

        inventory.add(new CD(name, author), random.nextInt(10) + 1);
        try {
            inventory.getItem(name, author).setPrice(4.99);
        } catch (Exception ignored) {
        }
    }

    private static void createDefaultDVD() {
        String name = "New Movie!";
        String author = "Big Movie Company";
        Random random = new Random();

        inventory.add(new DVD(name, author), random.nextInt(10) + 1);
        try {
            inventory.getItem(name, author).setPrice(9.99);
        } catch (Exception ignored) {
        }
    }

    private static void smartDisplay(int compareInventory) {
        if (compareInventory > 0) {
            System.out.println("Item 1 has greater inventory.");
        } else if (compareInventory < 0) {
            System.out.println("Item 1 has less inventory.");
        } else
            System.out.println("Items have equal inventory.");
    }

    private static int compareInventory() {
        if (!(Inventory.inventory.size() < 2)) {

            System.out.println("\nCurrent inventory:");
            System.out.println(inventory.getInventory() + "\n\n");
            System.out.println("Enter inventory to compare by #");

            System.out.print("Item 1: ");
            int item1 = getInt(doesInput);

            System.out.print("Item 2: ");
            int item2 = getInt(doesInput);
            try {
                return inventory.getItemAt(item1 - 1).compareTo(inventory.getItemAt(item2 - 1));
            } catch (Exception ignored) {
                System.out.println("Invalid inventory selection. ");
                compareInventory();
            }
        }
        return -2; // inventory isn't large enough to compare items
    }

    private static void restockInventory() {
        System.out.println("\nCurrent inventory:");
        System.out.println(inventory.getInventory() + "\n\n");
        System.out.println("Enter inventory # to restock");

        System.out.print("Item: ");
        int item1 = getInt(doesInput);

        System.out.print("Restock amount: ");
        int restockAmount = getInt(doesInput);
        while (restockAmount < 1) {
            System.out.println("Invalid restock amount.");
            restockAmount = getInt(doesInput);
        }

        try {
            inventory.restockProduct(inventory.getItemAt(item1 - 1).getId(), restockAmount);
        } catch (Exception ignored) {
            System.out.println("Invalid inventory selection #");
            restockInventory();
        }
        System.out.println("ITEM RESTOCKED.");
        System.out.println("\nCurrent inventory:");
        System.out.println(inventory.getInventory() + "\n\n");

    }

    private static boolean isPremium(Scanner doesInput) {
        try {
            doesInput = new Scanner(System.in);
            return doesInput.nextBoolean();
        } catch (InputMismatchException ignored) {
            return isPremium(doesInput);
        }
    }

    private static Membership createNewMember() {

        System.out.println("Enter first name: ");
        String firstName = doesInput.nextLine();

        System.out.println("Enter last name: ");
        String lastName = doesInput.nextLine();

        System.out.println("Enter email: ");
        String email = doesInput.nextLine();

        System.out.println("Enter phone #: ");
        String phone = doesInput.nextLine();

        return new Membership(firstName, lastName, email, phone);


    }

    public static void createBook(Scanner doesInput, Inventory inventory) {
        double price = -1;

        System.out.println("Enter name: ");
        String name = doesInput.nextLine();

        System.out.println("Enter author: ");
        String author = doesInput.nextLine();

        System.out.println("Enter price: ");
        while (price < 0) {
            price = getPrice(doesInput);
        }


        inventory.add(new Books(name, author));
        try {
            inventory.getItem(name, author).setPrice(price);
        } catch (Exception ignored) {
        }

    }

    // this method uses recursion as a way to force try-catch to do input validation
    // validates double input
    private static double getPrice(Scanner doesInput) {
        try {
            doesInput = new Scanner(System.in);
            return doesInput.nextDouble();
        } catch (InputMismatchException ignored) {
            return getPrice(doesInput);
        }
    }

    // input validation for integers
    private static int getInt(Scanner doesInput) {
        try {
            doesInput = new Scanner(System.in);
            return doesInput.nextInt();
        } catch (InputMismatchException ignored) {
            return getInt(doesInput);
        }
    }

    public static void createDVD(Scanner doesInput, Inventory inventory) {
        double price = -1;

        System.out.println("Enter name: ");
        String name = doesInput.nextLine();

        System.out.println("Enter producer: ");
        String author = doesInput.nextLine();

        System.out.println("Enter price: ");
        while (price < 0) {
            price = getPrice(doesInput);
        }

        inventory.add(new DVD(name, author));
        try {
            inventory.getItem(name, author).setPrice(price);
        } catch (Exception ignored) {
        }

    }

    public static void createCD(Scanner doesInput, Inventory inventory) {
        double price = -1;

        System.out.println("Enter name: ");
        String name = doesInput.nextLine();

        System.out.println("Enter artist: ");
        String author = doesInput.nextLine();

        System.out.println("Enter price: ");
        while (price < 0) {
            price = getPrice(doesInput);
        }

        inventory.add(new CD(name, author));
        try {
            inventory.getItem(name, author).setPrice(price);
        } catch (Exception ignored) {
        }

    }

    public static boolean createNewSave() {
        try {
            FileWriter writer = new FileWriter(gui.createFile()); // this saves the updated inventory file at the same place that we opened the initial file from

            writeFile(writer);
        } catch (IOException ignored) {
            return false; // an error occured
        }
        return true;
    }

    protected static void writeFile(FileWriter writer) throws IOException {
        if (Inventory.inventory.size() != 0) {
            for (Item item : Inventory.inventory) {
                //productID, type, title, author, numInStock, price
                writer.append(String.valueOf(item.getId())).append(", ");
                writer.append(item.getType()).append(", ");
                writer.append(item.getName()).append(", ");
                writer.append(item.getAuthor()).append(", ");
                writer.append(String.valueOf(item.getInventory())).append(", ");
                writer.append(String.valueOf(item.getPrice())).append("\n");
            }

            writer.close();
        }
    }

    public static void changeAccentColor(String accent) {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", accent)); // changes accent color
    }

    public static void writeEndOfDayReport() {
        //This file should include what products were purchased,
        // how many new members were registered,
        // total sales and revenue, and any other information you think is relevant.
    }

}
