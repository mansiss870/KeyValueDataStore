import com.freshworks.keyValueDataStore.*;
import com.freshworks.exceptions.*;
import com.freshworks.utility.*;

class DeleteTestCaseForValidKey
{
public static void main(String args[]) 
{
try
{
KeyValueDataStore kvds = KeyValueDataStore.getKeyValueDataStoreInstance();
Key key = new Key("0704CS171073");
kvds.delete(key);
}catch(Exception e)
{
e.printStackTrace();
}
}
}