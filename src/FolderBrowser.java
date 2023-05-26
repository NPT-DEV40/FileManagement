import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class FolderBrowser extends JFrame {
    private JButton selectButton;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;

    private File selectedFolder; // Store the selected folder
    private WatchService watchService;
    private Map<WatchKey, Path> keys;

    public FolderBrowser() {
        initializeUI();
    }

    private FolderBrowser(Path path) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        walkAndRegisterDirectories(path);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Folder Browser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);

        selectButton = new JButton("Select Folder");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(FolderBrowser.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    selectedFolder = fileChooser.getSelectedFile();
                    listFiles(selectedFolder);
                }
            }
        });

        fileList = new JList<>();
        listModel = new DefaultListModel<>();
        fileList.setModel(listModel);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = fileList.locationToIndex(evt.getPoint());
                    String fileName = listModel.getElementAt(index);
                    File selectedFile = new File(selectedFolder.getAbsolutePath(), fileName);
                    openFile(selectedFile);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(fileList);

        setLayout(new BorderLayout());
        add(selectButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void processEvents() {
        for(;;) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();

                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);

                System.out.format("%s: %s\n", event.kind().name(), child);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    if (Files.isDirectory(child)) {
                        try {
                            walkAndRegisterDirectories(child);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    if (Files.isDirectory(child)) {
                        keys.remove(key);
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void listFiles(File folder) {
        listModel.clear();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                listModel.addElement(file.getName());
            }
        }
    }

    private void openFile(File file) {
        try {
            Desktop.getDesktop().edit(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not open file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


//    private void stopWatching() {
//        if (watchService != null) {
//            try {
//                watchService.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (watchThread != null && watchThread.isAlive()) {
//            watchThread.interrupt();
//        }
//    }



    private void walkAndRegisterDirectories(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FolderBrowser().setVisible(true);
            }
        });
    }
}

