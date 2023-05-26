import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class ClientModel {
    public int port;
    public String name;
    public Socket socket;
    public BufferedReader receiver;
    public BufferedWriter sender;
    public Boolean is_connect;
    public ClientModel() {
    }

    public ClientModel(int port, String name, Socket socket, BufferedReader receiver, BufferedWriter sender, Boolean is_connect) {
        this.port = port;
        this.name = name;
        this.socket = socket;
        this.receiver = receiver;
        this.sender = sender;
        this.is_connect = is_connect;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getReceiver() {
        return receiver;
    }

    public void setReceiver(BufferedReader receiver) {
        this.receiver = receiver;
    }

    public BufferedWriter getSender() {
        return sender;
    }

    public void setSender(BufferedWriter sender) {
        this.sender = sender;
    }

    public Boolean getIs_connect() {
        return is_connect;
    }

    public void setIs_connect(Boolean is_connect) {
        this.is_connect = is_connect;
    }
}
