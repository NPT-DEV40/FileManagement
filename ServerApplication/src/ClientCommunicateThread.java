import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientCommunicateThread extends Thread {
    static ClientModel clientModel;

    static List<String> listFile;
    public  ClientCommunicateThread(Socket clientSocket) throws IOException {
        clientModel = new ClientModel();
        clientModel.setSocket(clientSocket);
        OutputStream os = clientSocket.getOutputStream();
        clientModel.setSender(new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8)));
        InputStream is = clientSocket.getInputStream();
        clientModel.setReceiver(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
        clientModel.setPort(clientSocket.getPort());
        listFile = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            while(true) {
                int tmp = 0;
                String header = clientModel.receiver.readLine();
                if(header == null) {
                    throw new IOException();
                }
                System.out.println(header);
                switch (header) {
                    case "New Connect":
                        String clientName = clientModel.receiver.readLine();
                        boolean is_exist = false;
                        for(ClientModel client : Main.socketController.connectedClient) {
                            if(client.name.equals(clientName)) {
                                is_exist = true;
                                break;
                            }
                        }
                        if(!is_exist) {
                            clientModel.name = clientName;
                            Main.socketController.connectedClient.add(clientModel);
                            Main.server.updateClientTable();

                            clientModel.sender.write("connect successfully");
                            clientModel.sender.newLine();
                            clientModel.sender.flush();
                        } else {
                            clientModel.sender.write("connect failed");
                            clientModel.sender.newLine();
                            clientModel.sender.flush();
                        }
                        break;
                    case "Create File":
                        System.out.println("acceptance");
                        break;
                    case "Server Name":
                        clientModel.sender.write(Main.socketController.serverName);
                        clientModel.sender.newLine();
                        clientModel.sender.flush();
                        break;
                    case "Connected Client Count":
                        clientModel.sender.write(String.valueOf(Main.socketController.connectedClient.size()));
                        clientModel.sender.newLine();
                        clientModel.sender.flush();
                        break;
                    case "List Files":
                        String fileList = clientModel.receiver.readLine();
                        // Convert String to arraylist that String split by space
                        String[] files = fileList.split("-");
                        listFile.addAll(Arrays.asList(files));
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            if (!Main.socketController.s.isClosed() && clientModel.name != null) {
                Main.socketController.connectedClient.remove(clientModel);
                Main.server.updateClientTable();
            }
        }
    }
}
