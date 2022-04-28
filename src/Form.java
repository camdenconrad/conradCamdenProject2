import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Scanner;

public class Form {
    private static final Inventory inventory = new Inventory();
    private static final Color unfocusedColor = new Color(155, 155, 155);
    private static final Color focusedColor = new Color(0, 0, 0);
    DefaultListModel<Item> inventoryModel = new DefaultListModel<>();
    DefaultListModel<Membership> memberModel = new DefaultListModel<>();
    private JProgressBar progressBar1;
    private JPanel mainContainer;
    private JComboBox itemChoice;
    private JTextField titleTextField;
    private JTextField authorTextField;
    private JTextField priceTextField;
    private JButton addInvButton;
    private JList<Item> inventoryList;
    private JList<Membership> membersList;
    private JPanel middlePane;
    private JFormattedTextField typeTextField;
    private JComboBox sorterField;
    private JButton saveButton;
    private JComboBox comboBox1;
    private JPanel sideBarRight;
    private JTextField displayTotal;
    private JButton openButton;
    private JButton addInventoryButton;
    private JScrollPane scrollPane;
    private JTextField debugField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTextField firstNameField;
    private JButton addMember;
    private JCheckBox isPremium;
    private JTextField searchBar;
    private JPanel inventoryTab;
    private JPanel membersTab;

    public Thread updateInventoryList;


    @SuppressWarnings({"BoundFieldAssignment", "BusyWait"})
    public Form() {

        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // increase scroll speed
        inventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // supposed to limit selection to only one line... doesn't seem to work

        // set fields to unfocused color palette
        titleTextField.setForeground(unfocusedColor);
        authorTextField.setForeground(unfocusedColor);
        priceTextField.setForeground(unfocusedColor);

        firstNameField.setForeground(unfocusedColor);
        lastNameField.setForeground(unfocusedColor);
        emailField.setForeground(unfocusedColor);
        phoneNumberField.setForeground(unfocusedColor);


        inventoryList.setDoubleBuffered(true); //lessen CPU/GPU load

        typeTextField.setText("%10s | %4s | %-40s | %-25s | %-12s  | Price: $".formatted("ID", "Type", "Title", "Author", "In stock:"));

        //typeTextField.setFocusable(false);

        JPopupMenu edit = new JPopupMenu("Edit");
        inventoryList.add(edit);
        JMenuItem restock = new JMenuItem("Restock");
        JMenuItem remove = new JMenuItem("Remove");

        JPopupMenu add = new JPopupMenu("Add");
        JMenuItem restockLabel = new JMenuItem("Restock");
        restockLabel.setEnabled(false);

        JTextField restockAmount = new JTextField(null);
        edit.add(restockLabel);
        edit.add(restockAmount);
        edit.add(remove);


        inventoryList = new JList<>(inventoryModel);
        inventoryList.setFont(new Font("Courier New", Font.PLAIN, 20));

        inventoryList.setBackground(new Color(255, 255, 255));
        inventoryList.setForeground(new Color(0, 0, 0));

        membersList = new JList<>(memberModel);
        membersList.setFont(new Font("Courier New", Font.PLAIN, 20));

        membersList.setBackground(new Color(255, 255, 255));
        membersList.setForeground(new Color(0, 0, 0));

        // does additional things to - updateUI, update inventory total display
        updateInventoryList = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(0, 1); // limits thread
                } catch (InterruptedException ignored) {
                }

                mainContainer.updateUI(); // updates main UI

                // calls sorter based on sort selection
                //ID
                //Title
                //Author
                //Stock
                try {
                    if (getSorter() == 0) {
                        Inventory.sortById();
                    }
                    if (getSorter() == 1) {
                        Inventory.sortByTitle();
                    }
                    if (getSorter() == 2) {
                        Inventory.sortByAuthor();
                    }
                    if (getSorter() == 3) {
                        Inventory.sortByInventory();
                    }
                    if (getSorter() == 4) {
                        Inventory.sortByType();
                    }
                    if (getSorter() == 5) {
                        Inventory.sortByPrice();
                    }
                    // null pointer exception thrown when loading in NEW inventory through open button
                } catch (ConcurrentModificationException | NullPointerException ignored) {
                    inventoryModel.clear(); // exceptions only thrown when inventory is changing, so we'll just clear it and skip further exceptions
                }

                try {
                    for (Item item : Inventory.inventory) {
                        if (!inventoryModel.contains(item)) {
                            inventoryModel.addElement(item);
                        }
                    }
                    for (int i = 0; i < inventoryModel.getSize(); i++) {
                        if (!Inventory.inventory.contains(inventoryModel.elementAt(i))) {
                            inventoryModel.remove(i);
                        }

                    }
                    for (int i = 0; i < inventoryModel.getSize(); i++) {
                        // if list is not in the same order as the inventory, clear list and redo
                        if (inventoryModel.elementAt(i) != Inventory.inventory.get(i)) {
                            inventoryModel.clear();
                        }

                    }

                    displayTotal.setText("Total: $%.2f".formatted(inventory.getInventoryTotal()));

                } catch (ConcurrentModificationException ignored) {
                    inventoryModel.clear();
                }

                while (!Objects.equals(searchBar.getText(), "")) {
                    try {

                        String localHolder = searchBar.getText();

                        for (int i = 0; i < inventoryModel.getSize(); i++) {
                            if (!inventoryModel.elementAt(i).toString().toLowerCase().replaceAll("'", "").replaceAll(" ", "").contains(localHolder.toLowerCase().replaceAll("'", "").replaceAll(" ", ""))) {
                                inventoryModel.remove(i);
                            }
                        }

                        // if search changes
                        if (!Objects.equals(localHolder, searchBar.getText())) {
                            inventoryModel.clear();
                            for (Item item : Inventory.inventory) {
                                if (!inventoryModel.contains(item)) {
                                    inventoryModel.addElement(item);
                                }
                            }
                            // add all items back
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }

                }

            }

        });
        updateInventoryList.start();

        inventoryList.setAutoscrolls(true);
        middlePane.add(membersList);
        membersList.setVisible(false);

        middlePane.add(inventoryList);

        // this code lets the screen load upon creation
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainContainer.setSize(screenSize.width, screenSize.height);


        // AKA add button
        addInvButton.addActionListener(e -> {

            createInventory();

        });
        titleTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(titleTextField, "title");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(titleTextField, "title");
            }
        });
        authorTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(authorTextField, "author");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(authorTextField, "author");
            }
        });
        priceTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(priceTextField, "price");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(priceTextField, "price");
            }

        });

        inventoryList.addMouseListener(new MouseAdapter() {
            int x; // mouse x location on screen
            int y;// mouse y location on screen

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // only provides menu when mouse is right click
                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {

                    // Plays sound!
                    Clip clip;
                    try {
                        clip = AudioSystem.getClip();

                        clip.open(AudioSystem.getAudioInputStream(getClass().getResource("Sounds/click.wav")));
                        clip.start();
                    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ignored) {
                    }
                    /// /// //

                    x = e.getX();
                    y = e.getY();
                    edit.show(inventoryList, x, y); // shows popup
                    restockAmount.requestFocus(); // gives focus to text area

                    restockAmount.addKeyListener(new KeyListener() {
                        @Override
                        public void keyTyped(KeyEvent e) {

                        }

                        @Override
                        // when user hits enter, inventory added
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                try {
                                    inventory.restockProduct(inventoryList.getSelectedValue().getId(), Integer.parseInt(restockAmount.getText()));
                                } catch (NumberFormatException ignored) {
                                    restockAmount.setText(null);
                                }
                                restockAmount.setText(null);
                                edit.setVisible(false);
                                progressBar1.setValue((progressBar1.getValue() + 1)); // for fun lets increase the progress lol
                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {

                        }
                    });

                }
            }


        });

        // remove item from inventory
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int c = JOptionPane.showConfirmDialog(remove, "Are you sure you want to remove " + "\"" + inventoryList.getSelectedValue().getName() + "\"?");
                if (c == JOptionPane.YES_OPTION) { // check if user is sure about removing
                    inventoryList.getSelectedValue().setInventory(0); // removes inventory - sets inventory to 0, thread automatically removes it
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    doSave();
                } catch (IOException ignored) {
                }
            }

        });


        // opens NEW inventory
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    readInNewInventory();

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // same thing as above, but doesn't clear inventory
        addInventoryButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.readInInventory();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        firstNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(firstNameField, "First name");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(firstNameField, "First name");
            }
        });
        lastNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(lastNameField, "Last name");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(lastNameField, "Last name");
            }
        });
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(emailField, "Email");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(emailField, "Email");
            }
        });
        phoneNumberField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                focused(phoneNumberField, "###-###-####");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                unfocused(phoneNumberField, "###-###-####");
            }
        });
        addMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean stillContinue = true;

                // check if we already have member
                for(int i = 0; i < memberModel.size(); i++) {
                    if(Objects.equals(memberModel.get(i).getMemberPhone(), phoneNumberField.getText().replaceAll("-", ""))) {
                        stillContinue = false;
                        debugField.setText("Phone number is already in use."); // show debug
                    }
                    if(Objects.equals(memberModel.get(i).getMemberEmail(), emailField.getText())) {
                        stillContinue = false;
                        debugField.setText("Email is already in use."); // show debug
                    }
                }

                // only adds member if we don't already have a member registered by phone number or email
                if(stillContinue) {
                    Members.members.add(new Membership(
                            isPremium.isSelected(),
                            firstNameField.getText(),
                            lastNameField.getText(), emailField.getText(),
                            phoneNumberField.getText().replaceAll("-", "")));
                }
                updateMembers();
            }
        });
        inventoryTab.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                switchToInventory();
            }
        });
        membersTab.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                switchToMembers();
            }
        });
    }

    // updates members pane
    private void updateMembers() {
        memberModel.clear();

        for (Membership members : Members.members) {
            memberModel.addElement(members);
        }

        membersList.updateUI();
    }

    private void switchToMembers() {
        middlePane.add(membersList);
        membersList.setVisible(true);
        inventoryList.setVisible(false);
        membersList.setBackground(new Color(255, 255, 255));
        membersList.setForeground(new Color(0, 0, 0));
        typeTextField.setText("%-12s | %-12s | %-30s | %10s | Is premium:".formatted("First:", "Last:", "Email:", "###-###-####"));
        updateMembers();
    }

    private void switchToInventory() {
        membersList.setVisible(false);
        typeTextField.setText("%10s | %4s | %-40s | %-25s | %-12s  | Price: $".formatted("ID", "Type", "Title", "Author", "In stock:"));
        inventoryList.setVisible(true);

    }

    private void unfocused(JTextField field, String text) {
        if (Objects.equals(field.getText(), "")) {
            field.setText(text);
            field.setForeground(unfocusedColor);
        }
    }

    private void focused(JTextField field, String text) {
        field.setForeground(focusedColor);
        if (field.getText().equals(text)) {
            field.setText("");
        }
    }

    private void createInventory() {
        String title = titleTextField.getText();
        String author = authorTextField.getText();


        // book
        if (itemChoice.getSelectedIndex() == 0) {
            inventory.add(new Books(title, author));
            try {
                inventory.getItem(title, author).setPrice(Double.parseDouble(priceTextField.getText()));
            } catch (Exception ignored) {
            }
        }

        // cd
        if (itemChoice.getSelectedIndex() == 1) {
            inventory.add(new CD(title, author));
            try {
                inventory.getItem(title, author).setPrice(Double.parseDouble(priceTextField.getText()));
            } catch (Exception ignored) {
            }
        }

        // dvd
        if (itemChoice.getSelectedIndex() == 2) {
            inventory.add(new DVD(title, author));
            try {
                inventory.getItem(title, author).setPrice(Double.parseDouble(priceTextField.getText().replaceAll("\\$", "").strip()));
            } catch (Exception ignored) {
            }
        }

        // return fields to default
        titleTextField.setText("title");
        authorTextField.setText("author");
        priceTextField.setText("price");

        // set back to greyed out colors
        titleTextField.setForeground(unfocusedColor);
        authorTextField.setForeground(unfocusedColor);
        priceTextField.setForeground(unfocusedColor);
    }

    private void doSave() throws IOException {
        // deactivate button while doing stuff
        saveButton.setEnabled(false);

        try {

            Main.createInventoryFile();

            // play sound
            Clip clip;
            try {
                clip = AudioSystem.getClip();

                clip.open(AudioSystem.getAudioInputStream(getClass().getResource("Sounds/save.wav")));
                clip.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ignored) {
            }

            // show that save was successful
            // show last save time in menu bar

            // gets current date and time
            DateTimeFormatter dateAndTime = DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            debugField.setText("Last saved: " + dateAndTime.format(now));


        } catch (NullPointerException ex) {
            debugField.setText("Last saved: save failed");

            // play sound
            Clip clip;
            try {
                clip = AudioSystem.getClip();

                clip.open(AudioSystem.getAudioInputStream(getClass().getResource("Sounds/saveFailed.wav")));
                clip.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ignored) {
            }

            Main.createNewSave();
        }

        // reactivate button
        saveButton.setEnabled(true);
    }

    public JPanel getPanel1() {
        return mainContainer;
    }

    public void setProgress(int value) {
        progressBar1.setValue(value);
    }

    public int getSorter() {
        return sorterField.getSelectedIndex();
    }

    public void readInNewInventory() throws FileNotFoundException {

        System.out.print("Enter file path for inventory: ");
        //Scanner reader = new Scanner(new File(new Scanner(System.in).nextLine()));

        try {
            File localHolder = new File(Main.gui.findFile().getCanonicalPath());

            Scanner reader = new Scanner(localHolder);

            Main.inventoryFile = localHolder; // set mains file so that saving is possible when loading in new inventory
            //Scanner reader = new Scanner(new File("inventory.csv"));

            Inventory.inventory.clear();
            inventoryModel.clear();

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
        } catch (IOException ignored) {
        }

    }
}
