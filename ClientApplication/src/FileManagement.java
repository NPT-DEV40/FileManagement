import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class FileManagement {
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;

    static String checkAction;

    public FileManagement(Path dir) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();

        walkAndRegisterDirectories(dir);
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }

    private void walkAndRegisterDirectories(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void processEvent() throws IOException {
        for(;;) {
            WatchKey key;
            try{
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            Path dir = keys.get(key);
            if(dir == null) {
                System.err.println("WatchKey not recognized!");
                continue;
            }

            for(WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                Path name = ((WatchEvent<Path>) event).context();
                Path child = dir.resolve(name);

                checkAction = event.kind().name();
                Main.socketController.sender.write(checkAction);
                Main.socketController.sender.newLine();
                Main.socketController.sender.flush();

                System.out.format("%s: %s\n", event.kind().name(), child);

                if(kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        if(Files.isDirectory(child)) {
                            walkAndRegisterDirectories(child);
                        }
                    } catch (IOException x) {

                    }
                }
            }

            boolean valid = key.reset();
            if(!valid) {
                keys.remove(key);

                if(keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("D:/Desktop/Study");
        new FileManagement(dir).processEvent();
    }
}
