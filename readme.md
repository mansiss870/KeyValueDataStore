# Key Value Data Store

It is file-based key-value data store that supports the basic CRD (create, read, and delete) operations, This data store is meant to be used as a local storage for one single process on one laptop. The data store is  exposed as a library to clients that can instantiate a class and work with the data store.

### Prerequsite
- ##### Java ( version 1.8 )
- ##### SimpleJson ( version 1.1 )

### How to use KeyValueDataStore ?
##### From libs folder download KeyValueDataStore.jar and json-simple-1.1.1.jar 


-  ####  Packages To Import
     - ##### com.freshworks.keyValueDataStore
        This package contains KeyValueDataStore class, in which code related to methods create, read and delete is written. All these methods throws ValidationException or ProcessException.
     KeyValueDataStore can be initialized using an optional file path. If one is not provided, it will reliably create itself in a reasonable location on machine

        ``` 
        public static KeyValueDataStore getKeyValueDataStoreInstance() throws ProcessException
    
        public static KeyValueDataStore getKeyValueDataStoreInstance(String filePath) throws ProcessException,IOException 
        ```
    - ##### com.freshworks.utility
  
      This package contains Key class. This class contains two properties key and timeToLive. Every key supports setting a Time-To-Live property when it is created. This property is optional. If provided, it will be evaluated as an integer defining the number of seconds the key must be retained in the data store. Once the Time-To-Live for a key has expired the key will no longer be available for Read or Delete operations.
        
        ```
        Key(String key);
          
        Key(String key,Integer timeToLive);
        ```
    - ##### com.freshworks.exceptions
    
       This package contains two classes ValidationException and ProcessException.
    
    - ##### org.json.simple
        This package contains JSONObject.

 - #### How to add record in Key Value Data Store ?
 
   A new key-value pair can be added to the data store using the Create operation. The key is always a string - capped at 32chars. The value is always a JSON obiect capped at 16KB.
   If create is invoked for an existing key, ProcessException will be thrown. If key exceed 32 chars then a ValidationException will be thrown and same with JSONObject if it's size exceed 16KB, ValidationException will be thrown.
    ```
    import com.freshworks.keyValueDataStore.*; 
    import com.freshworks.exceptions.*;
    import com.freshworks.utility.*;
    import org.json.simple.*;
    class CreateTestCase
    {
    public static void main(String args[]) 
    {
    try
    {
    KeyValueDataStore kvds = KeyValueDataStore.getKeyValueDataStoreInstance();
    Key key = new Key("0704CS171015");
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name","mansi sharma");
    jsonObject.put("city","ujjain");
    kvds.create(key,jsonObject);
    System.out.println("Record Inserted.");
    key = new Key("0704CS171015",5);
    jsonObject = new JSONObject();
    jsonObject.put("name","akshatjane");
    jsonObject.put("city","ujjain");
    kvds.create(key,jsonObject);
    System.out.println("Record Inserted.");
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    }
    }
    ```
- #### How to read record from KeyValueDataStore

    A Read operation on a key can be performed by providing the key, and receiving
    the value in response, as a JSON object. If key is does not exists in the Key
    Value Data Store, ProcessException will be thrown
    
    ```
    import com.freshworks.keyValueDataStore.*;
    import com.freshworks.exceptions.*;
    import com.freshworks.utility.*;
    import org.json.simple.*;
    class ReadTestCase
    {
    public static void main(String args[]) 
    {
    try
    {
    KeyValueDataStore kvds = KeyValueDataStore.getKeyValueDataStoreInstance("MyDataStore");
    Key key = new Key("0704CS171015");
    System.out.println(kvds.read(key).toJSONString());
   }catch(Exception e)
   {
   e.printStackTrace();
   }
   }
   }
    ```
- #### How to delete record from Key Value Data Store ?  
    A Delete operation can be performed by providing the key. If key is does notexists in the Key Value Data Store, ProcessException will be thrown.
    
    ```
    import com.freshworks.keyValueDataStore.*;
    import com.freshworks.exceptions.*;
    import com.freshworks.utility.*;
    import org.json.simple.*;
    class DeleteTestCaseForInvalidKey
    {
    public static void main(String args[]) 
    {
    try
    {
    KeyValueDataStore kvds = KeyValueDataStore.getKeyValueDataStoreInstance();
    Key key = new Key("0704CS171078");
    kvds.delete(key);
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    }
    }
    ```
- #### Key Value Data Store is Thread-safe.    
    A client process is allowed access the data store using multiple threads i.e The data store is  thread-safe.
    ```
    import com.freshworks.keyValueDataStore.*;
    import com.freshworks.exceptions.*;
    import com.freshworks.utility.*;
    import org.json.simple.*;
    class ThreadSafeTestCase
    {
    public KeyValueDataStore kvds;
    ThreadSafeTestCase()
    {
    try
    {
    this.kvds = KeyValueDataStore.getKeyValueDataStoreInstance();
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    testCase();
    }
    public void testCase() 
    {
    try
    {
    Thread t1=new Thread(){
    public void run()
    {
    try
    {
    Key key = new Key("0704IT171078",10);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name","Vansh Malviya");
    jsonObject.put("city","Bhopal");
    kvds.create(key,jsonObject);
    System.out.println("Recorded with key "+key.getKey()+" added.");
    this.sleep(1000);
    key = new Key("0704IT171080",10);
    jsonObject = new JSONObject();
    jsonObject.put("name","Mahak Mishra");
    jsonObject.put("city","Sujalpur");
    kvds.create(key,jsonObject);
    System.out.println("Recorded with key "+key.getKey()+" added.");
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    }
    };
    Thread t2=new Thread(){
    public void run()
    {
    try
    {
    Key key = new Key("0704CS171015");
    System.out.println("Record with key: "+key.getKey()+",readed :"+kvds.read(key).toJSONString());
    this.sleep(5000);
    key = new Key("0704CS171073");
    System.out.println("Record with key: "+key.getKey()+",readed :"+kvds.read(key).toJSONString());
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    }
    };
    Thread t3=new Thread(){
    public void run()
    {
    try{
    Key key = new Key("0704CS171015");
    kvds.delete(key);
    System.out.println("Record with key: "+key.getKey()+" deleted");
    this.sleep(8000);
    key = new Key("0704CS171073");
    kvds.delete(key);
    System.out.println("Record with key: "+key.getKey()+" deleted");
    }catch(Exception e)
    {
    e.printStackTrace();
    }   
    }
    };
    t1.start();
    t2.start();
    t3.start();
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    }
    public static void main(String gg[])
    {
    ThreadSafeTestCase tdtc=new ThreadSafeTestCase();
    }
    }
    ```
    
- #### How to compile and run the above programs?
    Uset can compile and run the above programs by including  both the jar files KeyValueDataStore.jar and json-simple-1.1.1.jar in classpath
    
    ``` javac -classpath libs/KeyValueDataStore.jar;libs/json-simple-1.1.1.jar;. FileName.java```

    ``` java -classpath libs/KeyValueDataStore.jar;libs/json-simple-1.1.1.jar;. MainClassName```

 ### Non Functional Requriments
-  The size of the file storing data can never exceed 1GB.
-  More than one client process cannot be allowed to use the same file as a data store at any given time.

