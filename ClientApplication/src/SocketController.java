import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class SocketController {
    String name;
    Socket socket;
    ServerData serverData;
    BufferedReader receiver;
    BufferedWriter sender;

    String fileDir = "D:\\Desktop";


    Thread receiveAndProcessThread;

    public SocketController(String name, ServerData serverData) throws IOException {
        this.name = name;
        this.serverData = serverData;
        socket = new Socket(serverData.ip, serverData.port);
        InputStream is = socket.getInputStream();
        receiver = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        OutputStream os = socket.getOutputStream();
        sender = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
    }

    public void login() throws IOException {
        sender.write("New Connect");
        sender.newLine();
        sender.write(name);
        sender.newLine();
        sender.flush();

        String connectResult = receiver.readLine();
        if (connectResult.equals("connect successfully")) {
            receiveAndProcessThread = new Thread(() -> {
                try {
                    while (true) {
                        String header = receiver.readLine();
                        StringBuilder listFile = new StringBuilder();
                        System.out.println(header);
                        if (header == null) {
                            throw new Exception();
                        }
                        switch (header) {
                            case "Connect":
                                File directory = new File(fileDir);
                                String[] folders = directory.list();
                                for (String folder : folders) {
                                    listFile.append(folder).append("-");
                                }
                                sender.write("List Files");
                                sender.newLine();
                                sender.write(listFile.toString());
                                sender.newLine();
                                sender.flush();
                                break;
                            case "File Selected":
                                String fileName = receiver.readLine();
                                fileDir = fileDir + "\\" + fileName;
                                System.out.println("fileDir: " + fileDir);
                                File file = new File(fileDir);
                                Main.customFolderChooser = new CustomFolderChooser(file);
                                break;
                            case "Accept Create File":
                                File fileCreate = new File(fileDir);
                                JFrame createFrame = new JFrame("Create");
                                JButton createFileButton = new JButton("Create File");
                                JButton createFolderButton = new JButton("Create Folder");
                                createFileButton.addActionListener(e -> Main.customFolderChooser.createFile(fileCreate));
                                createFolderButton.addActionListener(e -> Main.customFolderChooser.createFolder(fileCreate));
                                JPanel buttonPanel = new JPanel(new FlowLayout());
                                buttonPanel.add(createFileButton);
                                buttonPanel.add(createFolderButton);
                                createFrame.add(buttonPanel, BorderLayout.SOUTH);
                                createFrame.setSize(400, 400);
                                createFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                                createFrame.setLocationRelativeTo(null);
                                createFrame.setVisible(true);
                                createFrame.pack();
                                break;
                            case "Accept Delete File":
                                Main.customFolderChooser.deleteFile();
                                break;
                            case "Decline":
                                JOptionPane.showMessageDialog(null, "File already exists");
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            receiveAndProcessThread.start();
        }
    }

    public static boolean serverOnline(String ip, int port) {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 1000);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getServerName(String ip, int port) {
        if (!serverOnline(ip, port)) {
            return "";
        }
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(ip, port));
            InputStream is = s.getInputStream();
            BufferedReader receiver = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            OutputStream os = s.getOutputStream();
            BufferedWriter sender = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            sender.write("Server Name");
            sender.newLine();
            sender.flush();
            String serverName = receiver.readLine();
            s.close();

            return serverName;
        } catch (IOException e) {
            return "";
        }
    }

    public static int getConnectedClientCount(String ip, int port) {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(ip, port));
            InputStream is = s.getInputStream();
            BufferedReader receiver = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            OutputStream os = s.getOutputStream();
            BufferedWriter sender = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            sender.write("Connected Client Count");
            sender.newLine();
            sender.flush();

            int connectedClientCount = Integer.parseInt(receiver.readLine());

            s.close();

            return connectedClientCount;
        } catch (IOException e) {
            return 0;
        }
    }

    public void createFile() throws IOException {
        sender.write("Create File");
        sender.newLine();
        sender.flush();
    }

    public void deleteFile() throws IOException {
        sender.write("Delete File");
        sender.newLine();
        sender.flush();
    }
}
