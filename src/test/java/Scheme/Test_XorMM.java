package Scheme;

import Client.entity.KV;
import Server.server;
import util.AESUtil;
import util.Constants;
import util.GGM;
import util.Hash;
import Client.Xor_Hash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class Test_XorMM {

    public static KV[] kv_list; //可以被序列化

    public static void main(String[] args) throws Exception {
        if(args.length > 0){
            Constants.initialize(args[0]);
        } else {
            System.out.println("请提供文件路径作为程序的第一个参数。");
        }

        //maximum volume length
        // int MAX_VOLUME_LENGTH = (int) Math.pow(2, 14); //设置最大卷长度
        int MAX_VOLUME_LENGTH = Constants.MAX_VOLUME_LENGTH; //设置最大卷长度
        // int XOR_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(3.0));//GGM Tree level for xor hash 这个之间有什么关系吗
        int XOR_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(2.0));//GGM Tree level for xor hash 这个之间有什么关系吗

        //data size
        // int power_size = 23;
        // int ELEMENT_SIZE = (int) Math.pow(2, power_size); //MM中的元素个数
        int ELEMENT_SIZE = Constants.ELEMENT_SIZE; //MM中的元素个数

        //storage size
        int beta = 0;//parameter for xor hash
        int STORAGE_XOR = (int) Math.floor(((ELEMENT_SIZE * 1.23) + beta) / 3);

        //Search key
        // String[] search_key = {"key_s_87", "key_s_95"};
        // String[] search_key = {"key_s_1044", "key_s_477", "key_s_367", "key_s_1824", "key_s_370"};

        // String[] search_key = {"key_s_143", "key_s_106"};
        // String[] search_key = {"key_s_111264", "key_s_11835", "key_s_4222", "key_s_1984", "key_s_929"};

        // String[] search_key = {"key_s_18556", "key_s_40612", "key_s_16611", "key_s_988", "key_s_9290", "key_s_21043"};
        // String[] search_key = {"key_s_18556", "key_s_40006", "key_s_55794", "key_s_44016", "key_s_42343", "key_s_43608"};
        String[] search_key = {"key_s_18556", "key_s_37138", "key_s_16611", "key_s_988", "key_s_32769", "key_s_8123"};


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

        System.out.println("---------------------XorMM scheme---------------------");
        //setup phase
        long startTime = System.currentTimeMillis();
        Xor_Hash xor = new Xor_Hash(beta);
        xor.XorMM_setup(kv_list, XOR_LEVEL);

        long K_d = xor.Get_K_d();
        int K_e = xor.Get_K_e();

        byte[][] xor_EMM = xor.Get_EMM();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Setup time: " + executionTime + " ms");

        //query phase
        for(int k=0;k<search_key.length;k++){
            startTime = System.currentTimeMillis();
            server xor_server = new server(xor_EMM,MAX_VOLUME_LENGTH, XOR_LEVEL, STORAGE_XOR);//server receives ciphertext

            System.out.println("\nClient is generating token ... keywords:" + (search_key[k]));
            byte[] tk_key = Hash.Get_SHA_256((search_key[k]+K_d).getBytes(StandardCharsets.UTF_8));//search token

            System.out.println("\nServer is searching and then Client decrypts ... ");
            xor_server.Query_Xor(tk_key);//search
            ArrayList<byte[]> C_key = xor_server.Get_C_key();//client receives results
            byte[] K = Hash.Get_Sha_128((K_e+search_key[k]).getBytes(StandardCharsets.UTF_8));

            for (int i = 0; i < C_key.size(); i++)//decryption
            {
                byte[] str_0 = AESUtil.decrypt(K,C_key.get(i));
                if(str_0!=null){
                    String s = new String(str_0);
                    // System.out.println("Result:" + s);
                }
            }
            System.out.println("Result size:" + C_key.size());


            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            System.out.println("Query time: " + executionTime + " ms");
            xor_server.Store_Server("XorMM");
        }
    }
}
