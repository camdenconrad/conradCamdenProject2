import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GUI {

    public static final Form form;
    private static final JFileChooser finder = new JFileChooser();
    public static JFrame frame = new JFrame("Project 3");

    static {
        form = new Form();
    }

    public GUI() throws IOException {


        // getClass().getResource("/Icons/home.png")
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icons/book-stack.png")));
        frame.setIconImage(icon.getImage());

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height - 100);
        frame.setResizable(true);

        finder.setFileFilter(new FileNameExtensionFilter(".csv", "csv")); // filters out files that aren't ideal
        frame.add(form.getPanel1());
    }

    public Form getForm() {
        return form;
    }

    public File findFile() {

        frame.add(finder);
        frame.setLayout(new CardLayout());

        if (finder.showOpenDialog(null) == JFileChooser.OPEN_DIALOG)
            return finder.getSelectedFile();
        return null;
    }

    public File createFile() throws IOException {

        frame.add(finder);
        frame.setLayout(new CardLayout());
        finder.setDialogTitle("Open save directory");
        finder.setSelectedFile(new File(finder.getCurrentDirectory() + "/newInventory.csv"));

        if (finder.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return new File(finder.getSelectedFile().getAbsolutePath());
        }
        return null;
    }
}
