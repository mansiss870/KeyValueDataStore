import com.freshworks.keyValueDataStore.*;
import com.freshworks.exceptions.*;
import com.freshworks.utility.*;
import org.json.simple.*;

class TimeToLiveTestcase
{
public static void main(String args[]) 
{
try
{
KeyValueDataStore kvds = KeyValueDataStore.getKeyValueDataStoreInstance();
Key key = new Key("0704EC171078",10);
JSONObject jsonObject = new JSONObject();
jsonObject.put("name","gaurav joshi");
jsonObject.put("city","ujjain");
kvds.create(key,jsonObject);


key = new Key("0704EC171073",10);
jsonObject = new JSONObject();
jsonObject.put("name","prishita takshale");
jsonObject.put("city","dhar");
kvds.create(key,jsonObject);

Thread.sleep(5000);
System.out.println("After 5 seconds. I am trying to read record with key: "+key.getKey()+" and Time-to-live: "+key.getTimeToLive()+" seconds");
System.out.println(kvds.read(key).toString());
Thread.sleep(15000);
System.out.println("After 20 seconds. I am trying to read record with key: "+key.getKey()+" and Time-to-live: "+key.getTimeToLive()+" seconds");
System.out.println(kvds.read(key));

}catch(Exception e)
{
e.printStackTrace();
}
}
}
