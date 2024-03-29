package Scheme;

import Client.entity.KV;
import Client.entity.KV2;
import Server.server;
import util.AESUtil;
import util.Constants;
import util.GGM;
import util.Hash;
import util.tool;
import util.Constants;
import Client.Xor_Hash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Test_kVXorMM {

    public static KV[] kv_list; //可以被序列化
    public static HashMap<String, SimpleEntry<Integer, byte[]>> vol_map = new HashMap<String, SimpleEntry<Integer, byte[]>>(); // 分别是len和hash
    // public static HashMap<String, Integer> true_vol_map = new HashMap<String, Integer>(); // 分别是len和hash

    public static void KV2Map(KV2[] kv_list2, long K_d){
        int index = 0;
        for(int i=0;i<kv_list2.length;i++){
            byte[] tk_key = Hash.Get_SHA_256((kv_list2[i].key+K_d).getBytes(StandardCharsets.UTF_8));
            byte[] hash = new byte[32];
            // int count = 0;
            while(kv_list[index].key == kv_list2[i].key){
                byte[] hash_value = util.Hash.Get_SHA_256(kv_list[index].value.getBytes(StandardCharsets.UTF_8));
                hash = util.tool.Xor(hash,hash_value);
                index ++;
                // count ++;
            }
            SimpleEntry<Integer, byte[]> Pair = new SimpleEntry<>(kv_list2[i].value, hash);
            vol_map.put(Arrays.toString(tk_key), Pair);
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length > 0){
            Constants.initialize(args[0]);
        } else {
            System.out.println("请提供文件路径作为程序的第一个参数。");
        }

        //maximum volume length
        int MAX_VOLUME_LENGTH = Constants.MAX_VOLUME_LENGTH; //设置最大卷长度
        // System.out.println(MAX_VOLUME_LENGTH);
        // int XOR_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(3.0));//GGM Tree level for xor hash 这个之间有什么关系吗
        int XOR_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(2.0));//GGM Tree level for xor hash 这个之间有什么关系吗

        //data size
        int ELEMENT_SIZE = Constants.ELEMENT_SIZE; //MM中的元素个数

        //storage size
        int beta = 0;//parameter for xor hash
        int STORAGE_XOR = (int) Math.floor(((ELEMENT_SIZE * 1.23) + beta) / 3);

        //Search key
        // String[] search_key = {"key_s_87", "key_s_95"};
        // String[] search_key = {"key_s_1044", "key_s_477", "key_s_367", "key_s_1824", "key_s_370"};
        
        // String[] search_key = {"key_s_143", "key_s_106"};
        String[] search_key = {"key_s_111264", "key_s_11835", "key_s_4222", "key_s_1984", "key_s_929"};

        // String[] search_key = {"key_s_18556", "key_s_37138", "key_s_16611", "key_s_988", "key_s_32769", "key_s_8123"};

        //initialize an database
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("KV1.dat"));
            kv_list = (KV[]) in.readObject(); //kv_list中存储的是kv对
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) { 
            e.printStackTrace();
        }

        System.out.println("---------------------kVXorMM---------------------");
        //setup phase
        long startTime = System.currentTimeMillis();
        Xor_Hash xor = new Xor_Hash(beta);
        xor.XorMM_setup(kv_list, XOR_LEVEL);

        long K_d = xor.Get_K_d();
        int K_e = xor.Get_K_e();

        byte[][] xor_EMM = xor.Get_EMM();

        //生成vol_map
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("KV2.dat"));
            KV2[] kv_list2 = (KV2[]) in.readObject();
            KV2Map(kv_list2, K_d);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) { 
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Setup time: " + executionTime + " ms");

        for(int k=0;k<search_key.length;k++){
            // query phase
            startTime = System.currentTimeMillis();
            server xor_server = new server(xor_EMM,MAX_VOLUME_LENGTH, XOR_LEVEL, STORAGE_XOR, vol_map);//server receives ciphertext

            System.out.println("\nClient is generating token ... keywords:" + (search_key[k]));
            byte[] tk_key = Hash.Get_SHA_256((search_key[k]+K_d).getBytes(StandardCharsets.UTF_8));//search token

            System.out.println("\nServer is searching and then Client decrypts ... ");
            xor_server.Query_kVXorMM(tk_key);//search
            ArrayList<byte[]> C_key = xor_server.Get_C_key();//client receives results
            byte[] K = Hash.Get_Sha_128((K_e+search_key[k]).getBytes(StandardCharsets.UTF_8));

            byte[] v_hash = xor_server.Get_v_hash();
            byte[] hash = new byte[32];
            int count = 0;

            ArrayList<byte[]> res = new ArrayList<byte[]>();
            for (int i = 0; i < C_key.size(); i++)//decryption
            {
                byte[] str_0 = AESUtil.decrypt(K,C_key.get(i));
                if(str_0!=null){
                    String s = new String(str_0);
                    count++;
                    res.add(str_0);
                    // System.out.println("Result:" + s);
                }
            }

            endTime = System.currentTimeMillis();
            System.out.println("True Result size:" + count);
            executionTime = endTime - startTime;
            System.out.println("Query time: " + executionTime + " ms");


            //verify phase
            startTime = System.currentTimeMillis();
            for(int i=0;i<count;i++){
                hash = tool.Xor(hash, res.get(i));
                // System.err.println(hash);
            }
            if(v_hash == hash){
                System.out.println("Verify Right");
            }   
            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            System.out.println("Verify time: " + executionTime + " ms");
            xor_server.Store_Server("kVXorMM");
        }


    }
}
