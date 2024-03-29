# XorMM & VXorMM

This repo shows the constructions of dprfMM [1], XorMM and VXorMM. And this application is developed by using JDK 17.0.1.




## The Main Phases

1. Setup: the construction with different dataset;
2. Query: the key is searched on the sever and values will be return to the client;
3. Verify: This part is constructed in verifiable schemes, i.e. k-VXorMM. The client can verify the search results.

## File Structure

```javascript
/
├── README.md                                       //  introduction
├── src
│   ├── main
│   │   └── java
│   │      ├── Client
│   │      │    ├── entity                    
│   │      │    │   ├── KV2.java                    //  entity for key/vol pairs
│   │      │    │   └── KV.java                     //  entity for key/value pairs
│   │      │    ├── Cuckoo_Hash.java                //  setup for dprfMM
│   │      │    └── Xor_Hash.java                   //  setup for XorMM and VXorMM
│   │      │
│   │      ├── Server
│   │      │    ├── entity                         
│   │      │    │   ├── server_entity.java          //  entity of the server for dprfMM and XorMM
│   │      │    │   └── server_proof_entity.java    //  entity of the server for VXorMM
│   │      │    ├── server.java                     //  the server for dprfMM and XorMM
│   │      │    └── server_proof.java               //  the server for VXorMM
│   │      │
│   │      └── util
│   │           └── *                               // tools
│   │
│   └── test
│       └── java
│           └── Scheme
│               ├── Test_dprfMM                     //  dprfMM scheme
│               ├── Test_GenMM                      //  generate multi-maps
│               ├── Test_VXorMM                     //  VXorMM scheme
│               ├── Test_Xor_Success                //  test the probaility of success for XorMM
│               ├── Test_Scheme1                    //  kXorMM scheme
│               ├── Test_kXorMM                     //  kVXorMM scheme          
│               └── Test_XorMM                      //  XorMM scheme
│ 
├── KV1.dat                                         //  the obj of the multi-map of key/value pairs
├── Plaintext_KV1.txt                               //  the plaintext of the multi-map of key/value pairs
├── KV2.dat                                         //  the obj of the key count
├── Server_DprfMM.dat                               //  server storage in dprfMM
├── Server_VXorMM.dat                               //  server storage in VXorMM
├── Server_XorMM.dat                                //  server storage in XorMM
├── XorMM.iml
├── Group.py                                        //  Generate data set files that can be read, sorted from large to small according to the vol size of the key  
├── Get_Data_Distribution.py                        //  View the distribution of data
└── pom.xml                                         
```
