import java.io.*;
import java.nio.file.*;
import java.util.List;

public class GVT {

    private final ExitHandler exitHandler;
    private final String GVT = ".gvt";

    private Gvt(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    public boolean initialized(){
        return  Files.exists(Paths.get(GVT + "/version.txt"));
    }

    private void writeVersion(List<String> lines, String message) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(GVT + "/version.txt"));
        bw.write("Version: ");
        if (lines != null) {
            int lastVersionNumber = Integer.parseInt(lines.get(0).split(":")[1].trim());
            bw.write(String.valueOf(lastVersionNumber + 1));
            bw.newLine();
            bw.write(message);

            for (String l : lines) {
                bw.newLine();
                bw.write(l);
            }
        } else {
            bw.write("0");
            bw.newLine();
            bw.write(message);
        }
        bw.close();
    }

    private void updateCurrentVersion(int versionNumber) throws IOException{
        Path source = Paths.get(GVT + "/Version" + versionNumber);
        Path target = Paths.get(GVT + "/CurrentVersion");
        DirectoryStream<Path> stream = Files.newDirectoryStream(source);
        for (Path file : stream){
            String fileName = file.getFileName().toString();

            boolean directoryContains = Files.exists(Paths.get(GVT + "/CurrentVersion/" + fileName));
            if(directoryContains) {
                Files.copy(file,target.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }

            boolean fileExists = Files.exists(Paths.get(fileName));
            if(fileExists)
                Files.copy(file,Paths.get(fileName),StandardCopyOption.REPLACE_EXISTING);
            else
                Files.copy(file,Paths.get(fileName));
        }
    }

    private void copyDirectory(int versionNumber) throws IOException{
        Path source = Paths.get(GVT + "/CurrentVersion");
        Path target = Paths.get(GVT + "/Version" + versionNumber);
        Files.createDirectory(target);
        DirectoryStream<Path> stream = Files.newDirectoryStream(source);
        for(Path file : stream)
            Files.copy(file,target.resolve(file.getFileName()));
    }

    public void init(){
        if(initialized())
            exitHandler.exit(10,"Current directory is already initialized.");
        else {
            try {
                Files.createDirectory(Paths.get(GVT));
                Files.createDirectory(Paths.get(GVT + "/CurrentVersion"));
                writeVersion(null, "GVT initialized.");
                System.out.println("Current directory initialized successfully.");
            } catch (Exception e) {
                System.out.println("Underlying system problem. See ERR for details.");
                e.printStackTrace(System.err);
            }
        }
    }

    public void add(String name) {
        add(name, "File added successfully. File: " + name);
    }

    public void add(String name, String message) {
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        if (!Files.exists(Paths.get(name))) {
            System.out.println("File not found. File: " + name);
            return;
        }

        boolean directoryContains = Files.exists(Paths.get(GVT + "/CurrentVersion/" + name));
        if (directoryContains) {
            System.out.println("File already added. File: " + name);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(GVT + "/version.txt"));
            int versionNumber = Integer.parseInt(lines.get(0).split(":")[1].trim()) + 1;

            Path currentVersion = Paths.get(GVT + "/CurrentVersion");
            Path file = Paths.get(name);
            Files.copy(file, currentVersion.resolve(file.getFileName()));
            copyDirectory(versionNumber);

            writeVersion(lines, message);
            System.out.println("File added successfully. File: " + name);
        } catch (Exception e) {
            System.out.println("File cannot be added. See ERR for details. File: " + name);
            e.printStackTrace(System.err);
        }
    }

    public void detach(String name){
        detach(name, "File detached successfully. File: " + name);
    }

    public void detach(String name, String message){
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        boolean directoryContains = Files.exists(Paths.get(GVT + "/CurrentVersion/" + name));
        if (!directoryContains) {
            System.out.println("File is not added to gvt. File: " + name);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(GVT + "/version.txt"));
            int versionNumber = Integer.parseInt(lines.get(0).split(":")[1].trim()) + 1;
            Files.delete(Paths.get(GVT + "/CurrentVersion/" + name));
            copyDirectory(versionNumber);

            writeVersion(lines, message);
            System.out.println("File detached successfully. File: " + name);
        } catch (Exception e) {
            System.out.println("File cannot be detached, see ERR for details. File: " + name);
            e.printStackTrace(System.err);
        }
    }

    public void checkout(int versionNumber){
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(GVT + "/version.txt"));
            int currentVersionNumber = Integer.parseInt(lines.get(0).split(":")[1].trim());
            if (versionNumber > currentVersionNumber || versionNumber < 0) {
                System.out.println("Invalid version number: " + s);
                return;
            }

            updateCurrentVersion(versionNumber);
            System.out.println("Checkout successful for version: " + versionNumber);
        } catch (Exception e) {
            System.out.println("Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

    public void commit(String name){
        commit(name, "File committed successfully. File: " + name);
    }

    public void commit(String name, String message){
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        boolean fileExists = Files.exists(Paths.get(name));
        if (!fileExists) {
            System.out.println("File not found. File: " + name);
            return;
        }

        boolean directoryContains = Files.exists(Paths.get(GVT + "/CurrentVersion/" + name));
        if (!directoryContains) {
            System.out.println("File is not added to gvt. File: " + name);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(GVT + "/version.txt"));
            int versionNumber = Integer.parseInt(lines.get(0).split(":")[1].trim()) + 1;
            Path currentVersion = Paths.get(GVT + "/CurrentVersion/" + name);
            Path file = Paths.get(name);
            Files.copy(file, currentVersion, StandardCopyOption.REPLACE_EXISTING);
            copyDirectory(versionNumber);

            writeVersion(lines, message);
            System.out.println("File committed successfully. File: " + name);
        } catch (Exception e) {
            System.out.println("File cannot be committed, see ERR for details. File: " + name);
            e.printStackTrace(System.err);
        }
    }

    public void version(){
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(GVT + "/version.txt"));
            String result = br.readLine();
            String line;
            while ((line = br.readLine()) != null && !line.startsWith("Version: ")) {
                result += "\n" + line;
            }
            br.close();
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

    public void version(int versionNumber){
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(GVT + "/version.txt"));
            String result = br.readLine();
            int currentVersion = Integer.parseInt(result.split(":")[1].trim());
            result += "\n" + br.readLine();
            if (versionNumber > currentVersion || versionNumber < 0) {
                System.out.println("Invalid version number: " + s);
                return;
            }

            if (versionNumber != currentVersion) {
                String line;
                while (((line = br.readLine())) != null) {
                    if (line.equals("Version: " + versionNumber)) {
                        result = line + "\n" + br.readLine();
                        while ((line = br.readLine()) != null && !line.startsWith("Version: "))
                            result += "\n" + line;
                        break;
                    }
                }
            }
            br.close();
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

    public void history(){
        if(!initialized()) {
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(GVT + "/version.txt"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Version: "))
                    result += line.split(":")[1].trim() + ": " + br.readLine() + "\n";
            }
            br.close();
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

    public void history(int numberOfVersions){
        if(!initialized()){
            System.out.println("Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(GVT + "/version.txt"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null && numberOfVersions > 0) {
                if (line.startsWith("Version: ")) {
                    result += line.split(":")[1].trim() + ": " + br.readLine() + "\n";
                    numberOfVersions--;
                }
            }
            br.close();
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

}

