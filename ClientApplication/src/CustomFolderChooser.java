import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class CustomFolderChooser extends JFrame {
    private JTree folderTree;
    private JButton createButton;
    private JButton openButton;
    private JButton deleteButton;
    private static String folderPath = null;
    private String filePath;
    private static DefaultTreeModel defaultTreeModel;
    private DefaultMutableTreeNode rootNode;

    public CustomFolderChooser(File rootDirectory) {
        setTitle("Custom Folder Chooser");
        setSize(400, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        createButton = new JButton("Create");
        openButton = new JButton("Open");
        deleteButton = new JButton("Delete");
        createButton.addActionListener(e -> {
            try {
                createAction();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        openButton.addActionListener(e -> openAction());
        deleteButton.addActionListener(e -> deleteAction());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(openButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        System.out.println("Root directory: " + rootDirectory.getAbsolutePath());
        rootNode = new DefaultMutableTreeNode(rootDirectory.getName());
        buildFolderTree(rootDirectory, rootNode);
        folderTree = new JTree(rootNode);
        folderTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        folderTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
                if (e.getClickCount() == 2) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    filePath = selectedFile.getAbsolutePath();
                    File openFileSelected = new File(selectedFile.getAbsolutePath());
                    openFile(openFileSelected);
                }
            }
        });
        defaultTreeModel = (DefaultTreeModel) folderTree.getModel();

        JScrollPane scrollPane = new JScrollPane(folderTree);
        add(scrollPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void deleteAction() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            File selectedFile = (File) selectedNode.getUserObject();
            if (selectedFile.isDirectory()) {
                if (selectedFile.delete()) {
                    System.out.println("Deleted folder: " + selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Could not delete folder: " + selectedFile.getAbsolutePath());
                }
            } else {
                System.out.println("Could not delete file: " + selectedFile.getAbsolutePath());
            }
        }
        defaultTreeModel.reload();
    }

    private void openAction() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            File selectedFile = (File) selectedNode.getUserObject();
            if (selectedFile.isDirectory()) {
                folderPath = selectedFile.getAbsolutePath();
                System.out.println("Selected folder: " + selectedFile.getAbsolutePath());
            } else {
                filePath = selectedFile.getAbsolutePath();
                File openFileSelected = new File(selectedFile.getAbsolutePath());
                openFile(openFileSelected);
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            }
        }
    }

    private void createAction() throws IOException {
        Main.socketController.createFile();
    }

    public static void createFile(File rootDirectory) {
        if(folderPath == null) {
            String fileName = JOptionPane.showInputDialog("Enter file name: ");
            File newFile = new File(rootDirectory.getAbsolutePath() + File.separator + fileName);
            try {
                if (newFile.createNewFile()) {
                    System.out.println("Created file: " + newFile.getAbsolutePath());
                } else {
                    System.out.println("Could not create file: " + newFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String fileName = JOptionPane.showInputDialog("Enter file name: ");
            File newFile = new File(folderPath + File.separator + fileName);
            try {
                if (newFile.createNewFile()) {
                    System.out.println("Created file: " + newFile.getAbsolutePath());
                } else {
                    System.out.println("Could not create file: " + newFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        defaultTreeModel.reload();
    }

    public static void createFolder(File rootDirectory) {
        if(folderPath == null) {
            String folderName = JOptionPane.showInputDialog("Enter folder name: ");
            File newFolder = new File(rootDirectory.getAbsolutePath() + File.separator + folderName);
            if (newFolder.mkdir()) {
                System.out.println("Created folder: " + newFolder.getAbsolutePath());
            } else {
                System.out.println("Could not create folder: " + newFolder.getAbsolutePath());
            }
        } else {
            String folderName = JOptionPane.showInputDialog("Enter folder name: ");
            File newFolder = new File(folderPath + File.separator + folderName);
            if (newFolder.mkdir()) {
                System.out.println("Created folder: " + newFolder.getAbsolutePath());
            } else {
                System.out.println("Could not create folder: " + newFolder.getAbsolutePath());
            }
        }
        defaultTreeModel.reload();
    }

    private void openFile(File openFileSelected) {
        try {
            Desktop.getDesktop().open(openFileSelected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildFolderTree(File directory, DefaultMutableTreeNode parentNode) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                parentNode.add(node);
                if (file.isDirectory()) {
                    buildFolderTree(file, node);
                }
            }
        }
    }
}
