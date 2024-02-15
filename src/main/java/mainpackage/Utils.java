package mainpackage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utils class holding helper methods for example to read and write files and adding tabs to multiline strings
 */
public class Utils {

    public static String readFile(String path) {
        String str = "";
        try {
            File file = new File(path);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                str = str + "\n" + sc.nextLine();
            }
            sc.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        str = str.trim();
        return str;
    }

    public static String readFileWithEncoding(String path) {
        String str = "";
        try {
            FileInputStream inputStream = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1);
            int data = reader.read();
            while(data != -1) {
                str = str + (char) data;
                data = reader.read();
            }
            reader.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        str = str.trim();
        return str;
    }


    public static void writeFile(String str, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String array2Text(String[][] arr) {

        String  outStr = "";
        for(int i=0; i<arr.length; i++ ) {
            for(int j=0; j<arr[i].length; j++) {
                outStr = outStr + arr[i][j];
            }
            outStr = outStr.trim() + "\n";
        }
        return outStr;
    }

    public static String array2Text(String[][] arr, int tabs) {
        String tabStr = "";
        for(int i=0; i<tabs; i++) {
            tabStr = tabStr + "    ";
        }
        String  outStr = tabStr;
        for(int i=0; i<arr.length; i++ ) {
            for(int j=0; j<arr[i].length; j++) {
                outStr = outStr + arr[i][j];
            }
            outStr = outStr + "\n" + tabStr;
        }

        outStr = outStr.stripTrailing() + "\n";
        return outStr;
    }

    public static String addTabs(String code, int numTabs) {
        //String spaces =  new String(new char[this.level+1]).replace("\0", "    ");
        String spaces =  new String(new char[numTabs]).replace("\0", "    ");
        if(code != "") {
            String[] snpArr = code.split("\n");
            for (int i=0; i< snpArr.length; i++) {
                snpArr[i] = spaces + snpArr[i];
            }
            code = String.join("\n", snpArr) + "\n";
        }
        return code;
    }

    public static String addTab(String code, boolean add) {
        if(add) {
            String spaces = "    ";
            if (code != "") {
                String[] snpArr = code.split("\n");
                for (int i = 0; i < snpArr.length; i++) {
                    snpArr[i] = spaces + snpArr[i];
                }
                code = String.join("\n", snpArr) + "\n";
            }
        }
        return code;
    }

}
