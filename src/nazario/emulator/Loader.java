package nazario.emulator;

import java.io.*;

public class Loader {
    public static void loadIntoMemory(String filePath, int startIndex, Memory memory) {
        try(FileInputStream fileInputStream = new FileInputStream(filePath); DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {
            byte[] bytes = dataInputStream.readAllBytes();
            for(int i = 0;i<bytes.length;i++) {
                memory.writeValue(i + startIndex, bytes[i]);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dumpMemory(String filePath, Memory memory) {

        File file = new File(filePath);
        file.getParentFile().mkdirs(); // create directories if missing
        try(FileOutputStream fileOutputStream = new FileOutputStream(file); DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            byte[] bytes = memory.getMemory();
            dataOutputStream.write(bytes, 0, bytes.length);
            dataOutputStream.flush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
