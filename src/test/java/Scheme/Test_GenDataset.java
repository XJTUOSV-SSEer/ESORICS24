package Scheme;

import Client.entity.KV;
import Client.entity.KV2;
import util.tool;
import util.Constants;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test_GenDataset {
    public static void main(String[] args) throws Exception{
        if(args.length > 0){
            Constants.initialize(args[0]);
        } else {
            System.out.println("请提供文件路径作为程序的第一个参数。");
        }

        int k = Constants.k;
        //data size 需要修改时更改这两个参数
        int ELEMENT_SIZE = Constants.ELEMENT_SIZE; //k-v个数
        System.out.println("ELEMENT_SIZE:" + ELEMENT_SIZE);
        int KEY_SIZE = Constants.KEY_SIZE; //k的个数
        System.out.println("KEY_SIZE:" + KEY_SIZE);
        System.out.println("MAX_VOLUME_LENGTH:" + Constants.MAX_VOLUME_LENGTH);
        //initialize an database
        KV[] kv_list1 = new KV[ELEMENT_SIZE];
        KV2[] kv_list2 = new KV2[KEY_SIZE];

        //读取文件，k=3
        String filePath = Constants.filepath;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int c_line = 0;
            int c_element = 0;
            int MAX_VOLUME_LENGTH = 0;
            br.readLine();
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String node = parts[0];
                int neighborsCount = Integer.parseInt(parts[1]);
                // System.out.println(neighborsCount);
                List<String> neighbors = new ArrayList<>();
                
                if(c_line % k == 0){
                    //maximum volume length
                    MAX_VOLUME_LENGTH = neighborsCount;
                }
                KV2 tmp2 = new KV2();
                tmp2.key = "key_s_" + node;
                tmp2.value = MAX_VOLUME_LENGTH;
                kv_list2[c_line] = tmp2;

                for (int i = 0; i < neighborsCount; i++) {
                    neighbors.add(parts[i + 2]);
                }
                
                for (int i = 0; i < neighbors.size(); i++) {
                    KV tmp =  new KV();
                    tmp.key = "key_s_"+node;
                    tmp.value = "value_s_"+ neighbors.get(i);
                    tmp.counter = i;
                    kv_list1[c_element] = tmp;
                    c_element++;
                    // System.err.println(tmp.key);
                    // System.out.println(tmp.value);
                }
                
                // System.out.println("Node: " + node + ", Neighbors Count: " + neighborsCount + ", Neighbors: " + neighbors);
                c_line++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("KV1.dat")); //这里面数据存储的方式和plaintext中是不一样的，明文是自定义的
            out.writeObject(kv_list1);
            out.close();

            out = new ObjectOutputStream(new FileOutputStream("KV2.dat"));
            out.writeObject(kv_list2);
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tool.WriteDataToFile(kv_list1,"Plaintext_KV1.txt");
        tool.WriteDataToFile(kv_list2,"Plaintext_KV2.txt");
    }
}
