import com.freshworks.keyValueDataStore.*;
import com.freshworks.exceptions.*;
import com.freshworks.utility.*;
import org.json.simple.*;

class ThreadSafeTestCase{
public KeyValueDataStore kvds;
ThreadSafeTestCase()
{
try{
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
try{
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
try{
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
