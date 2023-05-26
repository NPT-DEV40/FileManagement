import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class FolderTracker extends JFrame {
    private JTextArea outputArea;

    public FolderTracker() {
        setTitle("Folder Tracker");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JButton selectFolderButton = new JButton("Select Folder");
        selectFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(FolderTracker.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File folder = fileChooser.getSelectedFile();
                    trackFolder(folder);
                }
            }
        });

        outputArea = new JTextArea(20, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(selectFolderButton);
        add(panel, "North");
        add(scrollPane, "Center");
    }

    private void trackFolder(File folder) {
        outputArea.setText("");
        traverseFolder(folder);
    }

    private void traverseFolder(File folder) {
        outputArea.append("Folder: " + folder.getAbsolutePath() + "\n");

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseFolder(file); // Recursively traverse subfolders
                } else {
                    outputArea.append("File: " + file.getAbsolutePath() + "\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FolderTracker().setVisible(true);
            }
        });
    }
}
