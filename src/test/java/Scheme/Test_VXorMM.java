package Scheme;



import Client.Xor_Hash;
import Client.entity.KV;
import util.*;
import Server.server_proof;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Test_VXorMM {
    public static KV[] kv_list;

    public static void main(String[] args) throws Exception {
        if(args.length > 0){
            Constants.initialize(args[0]);
        } else {
            System.out.println("请提供文件路径作为程序的第一个参数。");
        }


        //maximum volume length
        int MAX_VOLUME_LENGTH = Constants.MAX_VOLUME_LENGTH; //设置最大卷长度
        // int xor_level = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(3.0));//GGM Tree level for xor hash
        int XOR_LEVEL = (int) Math.ceil(Math.log(MAX_VOLUME_LENGTH) / Math.log(2.0));

        //data size
        // int power_size = 10;
        // int ELEMENT_SIZE = (int) Math.pow(2, power_size);
        int ELEMENT_SIZE = Constants.ELEMENT_SIZE; //MM中的元素个数

        //storage size
        int beta = 0;//parameter for xor hash
        int STORAGE_XOR = (int) Math.floor(((ELEMENT_SIZE* 1.23) + beta) / 3);

        // String[] search_key = {"key_s_87", "key_s_95"};
        // String[] search_key = {"key_s_1044", "key_s_477", "key_s_367", "key_s_1824", "key_s_370"};
        
        // String[] search_key = {"key_s_143", "key_s_106"};
        // String[] search_key = {"key_s_111264", "key_s_11835", "key_s_4222", "key_s_1984", "key_s_929"};

        String[] search_key = {"key_s_18556", "key_s_37138", "key_s_16611", "key_s_988", "key_s_32769", "key_s_8123"};

        //initialize an database
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("KV1.dat"));
            kv_list = (KV[]) in.readObject();
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        System.out.println("---------------------VXorMM scheme---------------------");
        //setup phase
        long startTime = System.currentTimeMillis();
        Xor_Hash vxor = new Xor_Hash(beta);

        xor_hom.initial();
        vxor.VXorMM_Setup(kv_list, XOR_LEVEL);
        GGM.clear();vxor.Leave_Map_Clear();//clear cache

        long K_d = vxor.Get_K_d();
        int K_e = vxor.Get_K_e();
        int K_p = vxor.Get_K_p();
        int K_m = vxor.Get_K_m();

        byte[][] EMM = vxor.Get_EMM();
        byte[][] VMM = vxor.Get_VMM();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Setup time: " + executionTime + " ms");

        //query phase
        for(int k=0;k<search_key.length;k++){
            startTime = System.currentTimeMillis();
            server_proof vxor_server = new server_proof(EMM,VMM,MAX_VOLUME_LENGTH, XOR_LEVEL, STORAGE_XOR);

            System.out.println("\nClient is generating token ... keywords:" + search_key[k] );
            byte[] tk = Hash.Get_SHA_256((search_key[k]+K_d).getBytes(StandardCharsets.UTF_8));//search token

            System.out.println("\nServer is searching and then Client decrypts ... "); 
            vxor_server.Query(tk);//searching
            ArrayList<byte[]> C_key = vxor_server.Get_C_key();//client receives results
            byte[] proof = vxor_server.Get_P_key();
            byte[] K = Hash.Get_Sha_128((K_e+search_key[k]).getBytes(StandardCharsets.UTF_8));


            for (int i = 0; i < C_key.size(); i++){//decryption
                byte[] D_bits = AESUtil.decrypt(K,C_key.get(i));
                if(D_bits!=null) {
                    String s = new String(D_bits);
                    // System.out.println("Result:" + s);
                }
            }
            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            System.out.println("Query time: " + executionTime + " ms");

            //verify phase
            startTime = System.currentTimeMillis();
            System.out.println("\nClient is verifying ... ");
            for (int i = 0; i < MAX_VOLUME_LENGTH; i++) {
                byte[] father_Node = GGM.Tri_GGM_Path(tk, XOR_LEVEL, tool.TtS(i, 2, XOR_LEVEL));
                int t0 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 1 , 10),STORAGE_XOR,0);
                int t1 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 11, 20),STORAGE_XOR,1);
                int t2 = GGM.Map2Range(Arrays.copyOfRange(father_Node, 21, 30),STORAGE_XOR,2);
                byte[] res = C_key.get(i);
                byte[] ss = tool.Xor(xor_hom.Gen_Proof(res, K_p), Hash.Get_Sha_128((K_m+","+t0).getBytes(StandardCharsets.UTF_8)));
                ss = tool.Xor(ss, Hash.Get_Sha_128((K_m+","+t1).getBytes(StandardCharsets.UTF_8)));
                ss = tool.Xor(ss, Hash.Get_Sha_128((K_m+","+t2).getBytes(StandardCharsets.UTF_8)));
                proof = tool.Xor(Hash.Get_SHA_256(ss), proof);
            }
            if (tool.Xor_Empty(proof)) {
                System.out.println("Proof is True");
            }else {
                System.out.println("Proof is Wrong");
            }
            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            System.out.println("Verify time: " + executionTime + " ms");
            vxor_server.Store_Server_Proof("VXorMM");
        }
    }
}
