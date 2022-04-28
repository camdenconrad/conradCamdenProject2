import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

public class Main {

    private static final Inventory inventory = new Inventory();
    public static GUI gui;

    public static File inventoryFile;

    public static FlatLightLaf laf = new FlatLightLaf();


    // MAIN

    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException {

        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#F37A10")); // changes accent color

        UIManager.setLookAndFeel(laf); //new FlatDarkLaf()

        gui = new GUI();

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


    public static boolean createNewSave() {
        try {
            inventoryFile = gui.createFile();
            FileWriter writer = new FileWriter(inventoryFile); // this saves the updated inventory file at the same place that we opened the initial file from

            writeFile(writer);
            return true;
        } catch (IOException | NullPointerException ignored) {
            return false;
        }
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

    public static void writeEndOfDayReport() throws IOException {
        //This file should include what products were purchased,
        // how many new members were registered,
        // total sales and revenue, and any other information you think is relevant.

        FileWriter writer = new FileWriter(inventoryFile.getParent() + "/EndOfDayReport.txt");

        DateTimeFormatter dateAndTime = DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        writer.write("|End Of Day Report|");
        writer.write("\nTime of report: " + dateAndTime.format(now));
        writer.write("\nTotal revenue: $%.2f".formatted(gui.getForm().getTotalRevenue()));
        writer.write("\nTotal sales: %s".formatted(gui.getForm().getTotalSales()));
        writer.write("\nTotal members registered: %s".formatted(gui.getForm().getMembersSignedUp()));
        writer.write("\n\nItems purchased:");
        writer.write(("\n%10s | %4s | %-20s | %-25s ".formatted("ID", "Type", "Title", "Author")));

        for (Item item : AllOrders.orderList) {
            writer.write("\n" + "%10d | %4s | %-20s | %-25s".formatted(item.getId(), item.getType(), item.getName(), item.getAuthor()));
        }
        writer.write("\nEnd.");

        writer.close();


    }

}
