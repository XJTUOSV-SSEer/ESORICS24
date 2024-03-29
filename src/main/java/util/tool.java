package util;

import Client.entity.KV;
import Client.entity.KV2;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class tool {

   /*
    函数: byte数组转long
    输入: param 1 - 字节数组
    输出: long型变量
     */
    public static long bytesToLong(byte[] bytes) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (bytes[ix] & 0xff);
        }
        return num;
    }

    public static byte[] longToBytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static byte[] intToBytes(int num) {
        byte[] byteNum = new byte[4];
        for (int ix = 0; ix < 4; ++ix) {
            int offset = 32 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static boolean Xor_Empty(byte[] xor) {
        for (int i = 0; i < xor.length; i++) {
            if (xor[i] == 0) {
                continue;
            } else
                return false;
        }
        return true;
    }


    public static byte[] Xor(byte[] x, byte[] y) {
        int min =0;
        if(x.length>y.length){
            min = y.length;
        }else{
            min = x.length;
        }
        byte[] temp = new byte[min];
        for (int i = 0; i < min; i++) {
            temp[i] = (byte) (x[i] ^ y[i]);
        }
        return temp;
    }

    public static void WriteDataToFile(KV[] kv_list, String pliantext) throws IOException {
        BufferedWriter bw = null;
        try {
            File file = new File("./" + pliantext);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            String current = kv_list[0].key;
            for (int i = 0; i < kv_list.length; ) {
                bw.write("key:  " + kv_list[i].key + "   value:  ");
                while (i < kv_list.length && current.equals(kv_list[i].key)) {
                    bw.write(kv_list[i].value + "  ");
                    i++;
                }
                if (i < kv_list.length)
                    current = kv_list[i].key;
                bw.newLine();
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bw) {
                bw.close();
            }
        }
    }

    public static void WriteDataToFile(KV2[] kv_list, String pliantext) throws IOException {
        BufferedWriter bw = null;
        try {
            File file = new File("./" + pliantext);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            for (int i = 0; i < kv_list.length; i++) {
                bw.write("key:  " + kv_list[i].key + "   value:  " + kv_list[i].value);
                bw.newLine();
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bw) {
                bw.close();
            }
        }
    }

    public static void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    //
    public static int[] TtS(int inNum, int index, int level) {
        int[] result = new int[level];
        int i = 0;
        while (i < level) {
            result[i] = (inNum % index);
            inNum = inNum / index;
            i++;
        }
        reverseArray(result);
        return result;
    }



    //BRC
    public static String int2bit(int a, int bitLength) {
        StringBuilder result = new StringBuilder();
        for (int i = bitLength - 1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((a & mask) != 0 ? "1" : "0");
        }
        return "#" + result.toString();
    }

    public static int bitString2Ten(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                result += Math.pow(2, s.length() - 1 - i);
            }
        }
        return result;
    }

    public static String stringAdd(String am, int num, int lenRes) {
        am = am.substring(1); // Remove the '#' character
        int lenAm = am.length();
        int a = bitString2Ten(am);
        int x = (int) Math.pow(2.0, lenAm);
        a = (a + num) % x;
        return int2bit(a, lenRes - 1);
    }

    public static boolean stringIsSmaller(String am, String bm) {
        am = am.substring(1); // Remove the '#' character
        bm = bm.substring(1); // Remove the '#' character
        return am.compareTo(bm) < 0;
    }

    public static ArrayList<String> getBRCm(int a, int b, int length) {
        ArrayList<String> res = new ArrayList<>();
        String am = int2bit(a, length);
        String bm = int2bit(b, length);
        while (stringIsSmaller(am, bm)) {
            int lenAm = am.length();
            int lenBm = bm.length();
            if (am.charAt(lenAm - 1) == '1') {
                res.add(am);
            }
            if (bm.charAt(lenBm - 1) == '0') {
                res.add(bm);
            }
            am = stringAdd(am, 1, lenAm);
            bm = stringAdd(bm, -1, lenBm);
            am = am.substring(0, lenAm - 1);
            bm = bm.substring(0, lenBm - 1);
        }
        if (am.equals(bm)) {
            res.add(am);
        }
        return res;
    }
}