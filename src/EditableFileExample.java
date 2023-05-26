import javax.swing.*;
import java.awt.*;
import java.io.*;

public class EditableFileExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Editable File");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JButton openButton = new JButton("Open File");
        openButton.addActionListener(e -> openFile());

        panel.add(openButton, BorderLayout.CENTER);
        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
    }

    private static void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                Desktop.getDesktop().edit(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Không thể mở tệp tin.");
            }
        }
    }
}
