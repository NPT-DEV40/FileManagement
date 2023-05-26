import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReadOnlyFileExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Read-Only File");
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

            // Kiểm tra xem tệp tin có tồn tại không
            if (file.exists()) {
                // Thiết lập thuộc tính chỉ đọc cho tệp tin
                file.setReadOnly();
                JOptionPane.showMessageDialog(null, "Tệp tin đã được thiết lập chỉ đọc.");
            } else {
                JOptionPane.showMessageDialog(null, "Tệp tin không tồn tại.");
            }
        }
    }
}
