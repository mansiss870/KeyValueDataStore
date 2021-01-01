import com.freshworks.keyValueDataStore.*;
import com.freshworks.exceptions.*;
import com.freshworks.utility.*;
import org.json.simple.*;

class ReadTestCaseForInvalidKey
{
public static void main(String args[]) 
{
try
{
KeyValueDataStore kvds = KeyValueDataStore.getKeyValueDataStoreInstance();
Key key = new Key("0704MS171015");
System.out.println(kvds.read(key).toJSONString());
}catch(Exception e)
{
e.printStackTrace();
}
}
}