package Server;

import util.GGM;
import util.tool;

import java.io.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class server {
    private static byte[][] EMM;
    private static int MAX_VOLUME_LENGTH;
    private static int server_level;
    private static int server_DEFAULT_INITIAL_CAPACITY;
    private ArrayList<byte[]> C_key = new ArrayList<byte[]>();
    private byte[] v_hash = new byte[32];
    private HashMap<String, Integer> vol_map = new HashMap<String, Integer>();
    // private HashMap<String, Integer> true_vol_map = new HashMap<String, Integer>();
    private HashMap<String, SimpleEntry<Integer, byte[]>> vol_map2 = new HashMap<String, SimpleEntry<Integer, byte[]>>();
    private HashMap<String, Integer> level_map = new HashMap<String, Integer>(); //可以被序列化
    
    public server(){}


    public server(byte[][] fp,int volume_length, int level,int DEFAULT_INITIAL_CAPACITY){
        EMM = fp;
        MAX_VOLUME_LENGTH = volume_length;
        server_level = level;
        server_DEFAULT_INITIAL_CAPACITY = DEFAULT_INITIAL_CAPACITY;
    }

    public server(byte[][] fp,int volume_length, int level,int DEFAULT_INITIAL_CAPACITY, HashMap<String, SimpleEntry<Integer, byte[]>> vol_map2){
        EMM = fp;
        MAX_VOLUME_LENGTH = volume_length;
        server_level = level;
        server_DEFAULT_INITIAL_CAPACITY = DEFAULT_INITIAL_CAPACITY;
        this.vol_map2 = vol_map2;
    }

    public server(byte[][] fp,int volume_length, int level,int DEFAULT_INITIAL_CAPACITY, HashMap<String, Integer> vol_map){
        EMM = fp;
        MAX_VOLUME_LENGTH = volume_length;
        server_level = level;
        server_DEFAULT_INITIAL_CAPACITY = DEFAULT_INITIAL_CAPACITY;
        this.vol_map = vol_map;
    }

    public server(byte[][] fp, HashMap<String, Integer> level_map ,int DEFAULT_INITIAL_CAPACITY, HashMap<String, Integer> vol_map){
        EMM = fp;
        server_DEFAULT_INITIAL_CAPACITY = DEFAULT_INITIAL_CAPACITY;
        this.vol_map = vol_map;
        this.level_map = level_map;
    }

    public void Query_Xor(byte[] hash){
        for (int i = 0;i<MAX_VOLUME_LENGTH;i++ ) {
            byte[] father_Node = GGM.Doub_GGM_Path(hash, server_level, tool.TtS(i, 2, server_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
            int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
            byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
            C_key.add(res);
        }
    }

    // public void Query_Xor(byte[] hash){
    //     for (int i = 0;i<MAX_VOLUME_LENGTH;i++ ) {
    //             byte[] father_Node = GGM.Tri_GGM_Path(hash, server_level, tool.TtS(i, 3, server_level));
    //             int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);//Map2Range这个函数就是用来生成受限PRF结果的
    //             int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
    //             int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
    //             byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
    //             C_key.add(res);
    //     }
    // }

    public void Query_Scheme2(byte[] hash, String key){
        //get BRC
        // System.out.println();
        int level = level_map.get(key);
        int vol = vol_map.get(Arrays.toString(hash));
        ArrayList<String> BRC = tool.getBRCm(0,vol-1,level); //这里要减一，因为0也是一个节点
        // for(String element: BRC){
        //     System.out.println(element);
        // }
        // System.out.println("vol: "+ vol);
        System.out.println("level: "+ level);
        //计算每个BRC所包含范围的最大和最小值
        for(String element:BRC){
            element = element.substring(1);
            int lastLevel = level - element.length();
            // int[] path = tool.TtS(Integer.parseInt(element,2), 2, element.length()); //path会倒序！！！
            // 这里要判定一下，如果BRC_ROOt_node 是根节点 即:#
            byte[] BRC_Root_Node = hash;
            if(element.length() != 0){
                BRC_Root_Node = GGM.Doub_GGM_Path(hash, element.length(), tool.TtS(Integer.parseInt(element,2), 2, element.length()));
            }
            for(int i=0;i<(int)Math.pow(2, lastLevel);i++){
                byte[] father_Node = GGM.Doub_GGM_Path(BRC_Root_Node, lastLevel, tool.TtS(i, 2, lastLevel));
                int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);
                // System.out.println(t0);
                int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
                // System.out.println(t1);
                int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
                // System.out.println(t2);
                byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
                C_key.add(res);
            }
        }
    }

    public void Query_Scheme1(byte[] hash){
        for (int i = 0;i<vol_map.get(Arrays.toString(hash));i++ ) {
            byte[] father_Node = GGM.Doub_GGM_Path(hash, server_level, tool.TtS(i, 2, server_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);//Map2Range这个函数就是用来生成受限PRF结果的
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
            int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
            byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
            C_key.add(res);
        }
    }

    public void Query_kVXorMM(byte[] hash){
        for (int i = 0;i<vol_map2.get(Arrays.toString(hash)).getKey();i++ ) {
            byte[] father_Node = GGM.Doub_GGM_Path(hash, server_level, tool.TtS(i, 2, server_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);//Map2Range这个函数就是用来生成受限PRF结果的
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 19),server_DEFAULT_INITIAL_CAPACITY,1);
            int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 29),server_DEFAULT_INITIAL_CAPACITY,2);
            byte[] res = tool.Xor(tool.Xor(EMM[t0], EMM[t1]), EMM[t2]);
            C_key.add(res);
        }
        this.v_hash = vol_map2.get(Arrays.toString(hash)).getValue();
    }

    public void Query_Cuckoo(byte[] hash){
        //GGM.clear();
        for (int i = 0;i<MAX_VOLUME_LENGTH;i++ ) {
            byte[] father_Node = GGM.Doub_GGM_Path(hash, server_level, tool.TtS(i, 2, server_level));
            int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 9),server_DEFAULT_INITIAL_CAPACITY,0);
            int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 17, 26),server_DEFAULT_INITIAL_CAPACITY,1);
            C_key.add(EMM[t0]);
            C_key.add(EMM[t1]);
        }
    }
    public ArrayList<byte[]> Get_C_key(){ return C_key; }
    public byte[] Get_v_hash(){ return v_hash; }
    public void Clear(){ C_key.clear();}

    public static void Store_Server(String text) {
        try {
            FileOutputStream file = new FileOutputStream("Server_"+text+".dat");
            for (int i = 0; i < EMM.length; i++) {
                file.write(EMM[i]);
            }
            file.close();
        } catch (IOException e) {
            System.out.println("Error - " + e.toString());
        }
    }
}
