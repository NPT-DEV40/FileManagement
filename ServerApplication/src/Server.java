import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Server extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    JLabel portLabel;
    JTextField portText;
    JLabel serverNameLabel;
    JTextField serverNameText;

    static JTable clientTable;
    JButton openCloseButton;
    JButton testButton;
    boolean isSocketOpened = false;

    public Server() {
        JPanel mainContent = new JPanel(new GridBagLayout());
        GBCBuilder gbc = new GBCBuilder(1, 1).setInsets(5);

        JLabel ipLabel = new JLabel("IP: " + SocketController.getThisIP());

        portLabel = new JLabel("Port: ");
        portText = new JTextField();
        serverNameLabel = new JLabel("Server Name: ");
        serverNameText = new JTextField();

        openCloseButton = new JButton("Open Server");
        openCloseButton.addActionListener(this);
        openCloseButton.setActionCommand("OpenClose");

        testButton = new JButton("Connect");
        testButton.addActionListener(this);
        testButton.setActionCommand("Connect");


        clientTable = new JTable(new Object[][]{}, new String[]{"Server Client", "Port client"});
        clientTable.setRowHeight(25);
        clientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    testButton.doClick();
                }
            }
        });
        JScrollPane clientScrollPane = new JScrollPane(clientTable);
        clientScrollPane.setBorder(BorderFactory.createTitledBorder("List Client Connected"));

        mainContent.add(ipLabel, gbc.setFill(GridBagConstraints.BOTH).setWeight(0, 0).setSpan(1, 1));
        mainContent.add(portLabel, gbc.setGrid(2, 1).setWeight(0, 0).setSpan(1, 1));
        mainContent.add(portText, gbc.setGrid(3, 1).setWeight(1, 0));
        mainContent.add(serverNameLabel, gbc.setGrid(1, 2).setWeight(0, 0).setSpan(1, 1));
        mainContent.add(serverNameText, gbc.setGrid(2, 2).setWeight(1, 0).setSpan(2, 1));
        mainContent.add(clientScrollPane,
                gbc.setGrid(1, 3).setFill(GridBagConstraints.BOTH).setWeight(1, 1).setSpan(4, 1));
        mainContent.add(openCloseButton, gbc.setGrid(1, 4).setWeight(1, 0).setSpan(4, 1));
        mainContent.add(testButton, gbc.setGrid(1, 5).setWeight(1, 0).setSpan(4, 1));
        mainContent.setPreferredSize(new Dimension(250, 300));

        this.setTitle("File Server");
        this.setContentPane(mainContent);
        this.getRootPane().setDefaultButton(openCloseButton);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        Main.socketController = new SocketController();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Connect":
                if(clientTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a client", "Error", JOptionPane.WARNING_MESSAGE);
                    break;
                }
                try {
                    ClientCommunicateThread.clientModel.sender.write("Connect");
                    ClientCommunicateThread.clientModel.sender.newLine();
                    ClientCommunicateThread.clientModel.sender.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 400);
                frame.setTitle("File Chooser");
                JList<String> fileList = new JList<String>();
                DefaultListModel<String> listModel = new DefaultListModel<String>();
                String[] folders = ClientCommunicateThread.listFile.toArray(new String[ClientCommunicateThread.listFile.size()]);
                for (String folder : folders) {
                    listModel.addElement(folder);
                }
                fileList.setModel(listModel);
                JScrollPane scrollPane = new JScrollPane(fileList);
                fileList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getClickCount() == 2) {
                            String selectedFile = fileList.getSelectedValue();
                            try {
                                ClientCommunicateThread.clientModel.sender.write("File Selected");
                                ClientCommunicateThread.clientModel.sender.newLine();
                                ClientCommunicateThread.clientModel.sender.flush();

                                ClientCommunicateThread.clientModel.sender.write(selectedFile);
                                ClientCommunicateThread.clientModel.sender.newLine();
                                ClientCommunicateThread.clientModel.sender.flush();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            frame.dispose();
                        }
                    }
                });
                frame.setLayout(new BorderLayout());
                frame.add(scrollPane);
                frame.setVisible(true);
                break;
            case "OpenClose":
                if (!isSocketOpened) {
                    try {
                        if (serverNameText.getText().isEmpty())
                            JOptionPane.showMessageDialog(this, "Please fill out server name field", "Error",
                                    JOptionPane.WARNING_MESSAGE);
                        else if (portText.getText().isEmpty())
                            JOptionPane.showMessageDialog(this, "Please fill out port field", "Error", JOptionPane.WARNING_MESSAGE);
                        else {

                            Main.socketController.serverName = serverNameText.getText();
                            Main.socketController.serverPort = Integer.parseInt(portText.getText());

                            Main.socketController.OpenSocket(Main.socketController.serverPort);
                            isSocketOpened = true;
                            openCloseButton.setText("Close Server");
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Port must be a integer type", "Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    Main.socketController.CloseSocket();
                    isSocketOpened = false;
                    openCloseButton.setText("Open Server");
                }
                break;
        }
    }

    public void updateClientTable() {

        Object[][] tableContent = new Object[Main.socketController.connectedClient.size()][2];
        for (int i = 0; i < Main.socketController.connectedClient.size(); i++) {
            tableContent[i][0] = Main.socketController.connectedClient.get(i).name;
            tableContent[i][1] = Main.socketController.connectedClient.get(i).port;
        }

        clientTable.setModel(new DefaultTableModel(tableContent, new String[]{"Client Name", "Port client"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

        });
    }
}