import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;

public class Main {

    public static Client client;
    public static CustomFolderChooser customFolderChooser;
    public static SocketController socketController;

    public static void main(String arg[]) throws IOException {
        client = new Client();
    }
}