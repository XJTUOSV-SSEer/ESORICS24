package util;

// import java.io.RandomAccessFile;

public class Constants {

    public static String filepath;
    public static int k = 100;
    public static int ELEMENT_SIZE;
    public static int KEY_SIZE;
    public static int MAX_VOLUME_LENGTH;
    public static int HASH;

    public static void initialize(String path) {
        filepath = path;
        int tempelementSize = 0;
        int tempkeySize = 0;
        int tempmaxvolumelength = 0;
        try{
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filepath)); 
            String line = reader.readLine(); // 假设我们的数据在文件的第一行
            tempelementSize = Integer.parseInt(line.trim());
            
            line = reader.readLine();
            tempkeySize = Integer.parseInt(line.trim());

            line = reader.readLine();
            String[] parts = line.split(" ");
            tempmaxvolumelength = Integer.parseInt(parts[1]);

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            // 可以选择给tempSize一个默认值，或者根据需要处理异常
        }
        ELEMENT_SIZE = tempelementSize; // 最终将读取的值赋给ELEMENT_SIZE
        KEY_SIZE = tempkeySize;
        MAX_VOLUME_LENGTH = tempmaxvolumelength;
    }
}
