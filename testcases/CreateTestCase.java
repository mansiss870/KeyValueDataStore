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
jsonObject.put("name","akshat jain");
jsonObject.put("city","ujjain");
kvds.create(key,jsonObject);
System.out.println("Record Inserted.");

key = new Key("0704CS171073");
jsonObject = new JSONObject();
jsonObject.put("name","mansi sharma");
jsonObject.put("city","ujjain");
kvds.create(key,jsonObject);
System.out.println("Record Inserted.");


key = new Key("0704CS171031");
jsonObject = new JSONObject();
jsonObject.put("name","ashutosh gautam");
jsonObject.put("city","ujjain");
kvds.create(key,jsonObject);
System.out.println("Record Inserted.");

}catch(Exception e)
{
e.printStackTrace();
}
}
}