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
    private JButton editButton;
    private JButton refreshButton;
    private String folderPath;
    private String filePath;
    private File rootPath;
    private static DefaultTreeModel defaultTreeModel;
    private DefaultMutableTreeNode rootNode;

    public CustomFolderChooser(File rootDirectory) {
        rootPath = rootDirectory;
        setTitle("Custom Folder Chooser");
        setSize(400, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        createButton = new JButton("Create");
        openButton = new JButton("Open");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        refreshButton = new JButton("Refresh");
        createButton.addActionListener(e -> {
            try {
                createAction();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        openButton.addActionListener(e -> openAction());
        deleteButton.addActionListener(e -> {
            try {
                deleteAction();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        editButton.addActionListener(e -> {
            try {
                editAction();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        refreshButton.addActionListener(e -> refreshAction());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(openButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
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

        folderTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    if (selectedFile.isDirectory()) {
                        folderPath = selectedFile.getAbsolutePath();
                        System.out.println("Selected folder: " + selectedFile.getAbsolutePath());
                    } else {
                        filePath = selectedFile.getAbsolutePath();
                        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    }
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

    private void editAction() throws IOException {
        Main.socketController.editFile();
    }

    private void refreshAction() {
        rootNode.removeAllChildren();
        buildFolderTree(rootPath, rootNode);
        defaultTreeModel.reload();
    }

    private void deleteAction() throws IOException {
        Main.socketController.deleteFile();
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

    public void createFile(File rootDirectory) {
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

    public void createFolder(File rootDirectory) {
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

    public void deleteFile() {
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

    private void openFile(File openFileSelected) {
        try {
            openFileSelected.setReadOnly();
            Desktop.getDesktop().open(openFileSelected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editFile() throws IOException {
        if(filePath == null) {
            JOptionPane.showMessageDialog(null, "Please select a file to edit.");
            return;
        }
        File editFileSelected = new File(filePath);
        editFileSelected.setWritable(true);
        Desktop.getDesktop().edit(editFileSelected);
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

    public static void main(String[] args) {
        File file = new File("D:\\Desktop\\Study");
        new CustomFolderChooser(file);
    }
}
