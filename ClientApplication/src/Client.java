import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

public class Client extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    public ServerData serverData;
    JTable serverTable;
    List<ServerData> serverDataList;

    public Client() throws IOException {
        GBCBuilder gbc = new GBCBuilder(1, 1);
        JPanel connectClient = new JPanel(new GridBagLayout());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setActionCommand("Refresh");
        refreshButton.addActionListener(this);

        serverTable = new JTable();
        serverTable.setRowHeight(25);
        serverTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4) {
                    c.setForeground(value.toString().equals("Active") ? Color.GREEN : Color.RED);
                    c.setFont(new Font("Dialog", Font.BOLD, 13));
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        // Update Server list
        serverDataList = renderServer.getServerList();

        updateServerList();

        JScrollPane serverScrollPane = new JScrollPane(serverTable);
        serverScrollPane.setBorder(BorderFactory.createTitledBorder("Server list to connect"));

        JButton connectServer = new JButton("Connect Server");
        connectServer.setActionCommand("Connect");
        connectServer.addActionListener(this);

        JButton createServer = new JButton("Create Server");
        createServer.setActionCommand("Create");
        createServer.addActionListener(this);

        JButton deleteServer = new JButton("Delete Server");
        deleteServer.setActionCommand("Delete");
        deleteServer.addActionListener(this);

        JButton EditServer = new JButton("Edit Server");
        EditServer.setActionCommand("Edit");
        EditServer.addActionListener(this);


        serverTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    connectServer.doClick();
                }
            }
        });

        connectClient.add(serverScrollPane,
                gbc.setSpan(3, 1).setGrid(1, 1).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setInsets(5));

        JPanel refreshConnectPanel = new JPanel(new FlowLayout());
        refreshConnectPanel.add(refreshButton);
        refreshConnectPanel.add(connectServer);

        connectClient.add(refreshConnectPanel,
                gbc.setSpan(3, 1).setGrid(1, 2).setWeight(1, 0).setFill(GridBagConstraints.NONE));
        connectClient.add(createServer,
                gbc.setSpan(1, 1).setGrid(1, 3).setFill(GridBagConstraints.BOTH));
        connectClient.add(deleteServer, gbc.setGrid(2, 3));
        connectClient.add(EditServer, gbc.setGrid(3, 3));


        this.setTitle("File Management");
        this.setContentPane(connectClient);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    JTextField nameText;

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Connect":
                if (serverTable.getSelectedRow() == -1) {
                    break;
                }
                String serverState = serverTable.getValueAt(serverTable.getSelectedRow(), 4).toString();
                if (!serverState.equals("Active")) {
                    JOptionPane.showMessageDialog(this, "Server is not active", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                JDialog dialog = new JDialog();
                nameText = new JTextField();
                nameText.setPreferredSize(new Dimension(250, 30));
                JButton joinServerButton = new JButton("Join");
                joinServerButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (nameText.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Please enter your name", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        String ip = serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString();
                        String port = serverTable.getValueAt(serverTable.getSelectedRow(), 3).toString();
                        ServerData selectedServer = serverDataList.stream().filter(x -> x.ip.equals(ip) && x.port == Integer.parseInt(port)).findAny().orElse(null);
                        try {
                            Main.socketController = new SocketController(nameText.getText(), selectedServer);
                            Main.socketController.login();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                });
                JPanel askNameContent = new JPanel(new GridBagLayout());
                askNameContent.add(nameText, new GBCBuilder(1, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
                askNameContent.add(joinServerButton, new GBCBuilder(2, 1).setFill(GridBagConstraints.BOTH));

                dialog.setContentPane(askNameContent);
                dialog.setTitle("Please enter your name to join server:  "
                        + serverTable.getValueAt(serverTable.getSelectedRow(), 0).toString());
                dialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
                dialog.pack();
                dialog.getRootPane().setDefaultButton(joinServerButton);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                break;
            case "Create":
                break;
            case "Delete":
                if (serverTable.getSelectedRow() == -1) {
                    break;
                }
                serverDataList.remove(serverDataList.get(serverTable.getSelectedRow()));
                serverTable.updateUI();
                break;
            case "Edit":
                if (serverTable.getSelectedRow() == -1) {
                    break;
                }
                break;
            case "Refresh":
                updateServerList();
                break;
        }
    }

    public void connectResultAction(String connected) {
        if (connected.equals("connected")) {
            String ip = serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString();
            String port = serverTable.getValueAt(serverTable.getSelectedRow(), 3).toString();
            serverData = serverDataList.stream().filter(x -> x.ip.equals(ip) && x.port == Integer.parseInt(port)).findAny().orElse(null);

        } else {
            JOptionPane.showMessageDialog(this, "Server is full", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateServerList() {
        if (serverDataList == null) {
            return;
        }
        for (ServerData serverData : serverDataList) {
            serverData.isOpen = SocketController.serverOnline(serverData.ip, serverData.port);
            if (serverData.isOpen) {
                serverData.realName = SocketController.getServerName(serverData.ip, serverData.port);
                serverData.connectAccountCount = SocketController.getConnectedClientCount(serverData.ip, serverData.port);
            }
        }

        serverTable.setModel(new DefaultTableModel(renderServer.getServerObject(serverDataList),
                new String[]{"Server Name", "Real Name", "IP", "Port", "Server State", "Connected Client Count"}) {
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

    public static void main(String[] args) throws IOException {
        new Client();
    }
}
