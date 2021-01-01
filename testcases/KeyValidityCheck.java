import com.freshworks.keyValueDataStore.*;
import com.freshworks.exceptions.*;
import com.freshworks.utility.*;
import org.json.simple.*;

class KeyValidityCheck
{
public static void main(String args[])
{

try 
{
Key key = new Key("");
}catch(Exception e)
{
System.out.println(e);
}

try 
{
Key key = new Key(null);
}catch(Exception e)
{
System.out.println(e);
}

try 
{
String s = new String(new char[33]).replace('\0',' ');
Key key = new Key(s);
}catch(Exception e)
{
System.out.println(e);
}

try 
{
Key key = new Key("7860",0);
}catch(Exception e)
{
System.out.println(e);
}

}
}
