import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class renderServer {
    public final static String File_Name = "D:\\Desktop\\JAVA\\FileManagement\\FileManagement\\ClientApplication\\server.txt";

    public static List<ServerData> getServerList() throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(File_Name));
        List<ServerData> serverDataList = new ArrayList<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                serverDataList.add(new ServerData(data[0], data[1], Integer.parseInt(data[2])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reader.close();
        return serverDataList;
    }

    public static Object[][] getServerObject(List<ServerData> serverDataList) {
        if(serverDataList.isEmpty()) {
            return new Object[][] {};
        }
        Object[][] serverObject = new Object[serverDataList.size()][6];
        for (int i = 0; i < serverDataList.size(); i++) {
            serverObject[i][0] = serverDataList.get(i).nickName;
            serverObject[i][1] = serverDataList.get(i).realName;
            serverObject[i][2] = serverDataList.get(i).ip;
            serverObject[i][3] = serverDataList.get(i).port;
            serverObject[i][4] = serverDataList.get(i).isOpen ? "Active" : "Inactive";
            serverObject[i][5] = serverDataList.get(i).connectAccountCount;
        }
        return serverObject;
    }
}
