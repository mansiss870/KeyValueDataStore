package com.freshworks.keyValueDataStore;
import java.io.*;
import java.util.*;
import java.time.*;
import org.json.simple.*;
import java.util.concurrent.*;
import com.freshworks.exceptions.*;
import com.freshworks.utility.*;

public class KeyValueDataStore
{
private String filePath;
private String timerDataFilePath;
private File file; //file to store key value pair
private File timerDataFile;// file to store key , its time to live and time stamps
private Timer timer;
private static KeyValueDataStore keyValueDataStore;

private KeyValueDataStore() throws ProcessException
{
this.filePath = "keyValueDataStore.data";
this.timerDataFilePath = "timerDataFile.data";
this.timerDataFile = new File(this.timerDataFilePath);
this.file = new File(this.filePath);
if(!this.file.renameTo(this.file)) throw new ProcessException("Key Value Data Store named : "+this.filePath+" ,already in use by some other process.");
}

private KeyValueDataStore(String filePath) throws ProcessException
{
this.filePath=filePath;
this.file = new File(filePath); 
try{
this.file.getCanonicalPath(); //checking if given path is valid or not
}catch(IOException ioe)
{
throw new ProcessException("Invalid Key-Value DataStore file path: "+this.filePath);
}
if(!this.file.renameTo(this.file)) throw new ProcessException("Key Value Data Store named : "+this.filePath+" ,already in use by some other process.");
int i = filePath.lastIndexOf('.');
this.timerDataFilePath = filePath.substring(0,i) + "TimerDataFile.data";
this.timerDataFile = new File(this.timerDataFilePath);
}

public static KeyValueDataStore getKeyValueDataStoreInstance() throws ProcessException
{
if(keyValueDataStore!=null) return keyValueDataStore;
keyValueDataStore=new KeyValueDataStore();
return keyValueDataStore;
}

public static KeyValueDataStore getKeyValueDataStoreInstance(String filePath) throws ProcessException,IOException
{
if(keyValueDataStore != null) return keyValueDataStore;
keyValueDataStore=new KeyValueDataStore(filePath);
return keyValueDataStore;
}

public synchronized void create(Key key,JSONObject jsonObject) throws ValidationException,ProcessException,IOException,FileNotFoundException
{

if( jsonObject.toJSONString().length()  > 16384 ) throw new ValidationException("Size of JSONObject cannot exceed 16KB.");

// removing expired data
RandomAccessFile tdraf = new RandomAccessFile(this.timerDataFile,"rw");
while(tdraf.getFilePointer() < tdraf.length())
{
String tempKey = tdraf.readLine();
Key k = new Key(tempKey);
Instant old = Instant.parse(tdraf.readLine());
Integer timeToLive =Integer.parseInt(tdraf.readLine());
old = old.plusSeconds(timeToLive);
Instant now = Instant.now();
if(old.compareTo(now) < 0)
{
deleteFromTimerData(k);
delete(k);
}
}
tdraf.close();

if(getFileSizeInMegaBytes(this.file) > 1024) throw new ProcessException("Size cannot exceed 1 GB.");

//checking if key already exist or not .
RandomAccessFile raf = new RandomAccessFile(this.file,"rw");
while(raf.getFilePointer() < raf.length())
{
if(key.getKey().compareTo(raf.readLine()) == 0)
{
raf.close(); 
throw new ProcessException("Key already exists.");
}
raf.readLine();
}

//writing the data in the database
raf.writeBytes(key.getKey()+"\n");
raf.writeBytes(jsonObject.toJSONString()+"\n");
raf.close();

//setting timer to the key
//if timer is present program will not end until timer ends 
//if program is terminated forcefully it will clear the expired later 

if(key.isTimeToLivePresent())
{
tdraf = new RandomAccessFile(this.timerDataFile,"rw");
tdraf.seek(tdraf.length());
tdraf.writeBytes(key.getKey() + "\n");
tdraf.writeBytes(Instant.now().toString() + "\n");
tdraf.writeBytes(key.getTimeToLive() + "\n");
tdraf.close();
timer =  new Timer();
timer.schedule(new TimerTask(){
public void run() // deleting the key when it is expired
{
try
{
deleteFromTimerData(key);
delete(key);
timer.cancel();
}catch(Exception e)
{
e.printStackTrace();
}
}
},key.getTimeToLive()*1000);
}

}

public synchronized void delete(Key key) throws ValidationException,ProcessException,IOException,FileNotFoundException
{
if( key == null ) throw new ValidationException("Invalid key.");

RandomAccessFile tdraf = new RandomAccessFile(this.timerDataFile,"rw");

//checking if given key is expried
//if it is expired than delete it
while(tdraf.getFilePointer() < tdraf.length())
{
if(tdraf.readLine().compareTo(key.getKey()) == 0)
{
Instant old = Instant.parse(tdraf.readLine());
Integer timeToLive =Integer.parseInt(tdraf.readLine());
old = old.plusSeconds(timeToLive);
Instant now = Instant.now(); //current time
if(old.compareTo(now) < 0) //comparing current time and expiry time of data
{
deleteFromTimerData(key);
delete(key);
tdraf.close();
throw new  ProcessException("Key "+ key.getKey() +" not found or it is expired.");
}
else
{
tdraf.close();
break;
}
}
tdraf.readLine();
tdraf.readLine();
}

//deleting data
RandomAccessFile raf = new RandomAccessFile(this.file,"rw");
File tempFile = new File("temp.data");
if(tempFile.exists())
{
tempFile.delete();
}
RandomAccessFile tempRaf = new RandomAccessFile(tempFile,"rw");
String tempKey="";
String tempJSON="";
boolean found = false;
while(raf.getFilePointer()<raf.length())
{
tempKey=raf.readLine();
tempJSON=raf.readLine();
if(tempKey.compareTo(key.getKey()) != 0) 
{
tempRaf.writeBytes(tempKey+"\n");
tempRaf.writeBytes(tempJSON+"\n");
}
else
{
found = true;
}
}

// if key is not present in data
if(found == false)
{
raf.close();
tempRaf.close();
tempFile.delete();
throw new  ProcessException("Key "+ key.getKey() +" not found or it is expired.");
}

//copying data form temprory file to orignal file
raf.seek(0);
tempRaf.seek(0);
while(tempRaf.getFilePointer()<tempRaf.length())
{
raf.writeBytes(tempRaf.readLine()+"\n");
}
raf.setLength(tempRaf.length());
raf.close();
tempRaf.close();
tempFile.delete();
}

public synchronized JSONObject read(Key key) throws ProcessException,ValidationException,IOException,FileNotFoundException
{
if(key == null) throw new ValidationException("Invalid key.");

//checking if key is in expired data or not
//if it is in expired record remove it and return null
RandomAccessFile tdraf = new RandomAccessFile(this.timerDataFile,"rw");
while(tdraf.getFilePointer() < tdraf.length())
{
if(tdraf.readLine().compareTo(key.getKey()) == 0)
{
Instant old = Instant.parse(tdraf.readLine());
Integer timeToLive =Integer.parseInt(tdraf.readLine());
old = old.plusSeconds(timeToLive);
Instant now = Instant.now(); //current time
if(old.compareTo(now) < 0) //comparing current time and expiry time of key
{

//key is expired
deleteFromTimerData(key);
delete(key);
tdraf.close();
throw new  ProcessException("Key "+ key.getKey() +" not found or it is expired.");
}
else
{
//key is valid

tdraf.close();
break;
}
}
tdraf.readLine();
tdraf.readLine();
}

//searching for the data
RandomAccessFile raf = new RandomAccessFile(this.file,"rw");
String jsonString="";

while(raf.getFilePointer() < raf.length())
{
if((raf.readLine()).compareTo(key.getKey())==0) 
{
jsonString=raf.readLine();
raf.close();
return (JSONObject)JSONValue.parse(jsonString); //converting string to jsonObject
}
raf.readLine();
}
throw new  ProcessException("Key "+ key.getKey() +" not found or it is expired.");

}

// for deleting data from TimerDataFile similar to delete
// only used by this class
// timerDataFile consist of three value
// key , timeToLive , Instant i.e the time at which key was inserted in database
private synchronized void deleteFromTimerData(Key key) 
{
try
{
RandomAccessFile raf = new RandomAccessFile(this.timerDataFile,"rw");
File tempFile = new File("temp.data");
if(tempFile.exists())
{
tempFile.delete();
}
RandomAccessFile tempRaf = new RandomAccessFile(tempFile,"rw");
String tempKey="";
String tempInstant="";
String tempTimeToLive="";
while(raf.getFilePointer() < raf.length())
{
tempKey=raf.readLine();
tempInstant=raf.readLine();
tempTimeToLive=raf.readLine();
if(tempKey.compareTo(key.getKey()) != 0) 
{
tempRaf.writeBytes(tempKey+"\n");
tempRaf.writeBytes(tempInstant+"\n");
tempRaf.writeBytes(tempTimeToLive+"\n");
}
}
raf.seek(0);
tempRaf.seek(0);
while(tempRaf.getFilePointer()<tempRaf.length())
{
raf.writeBytes(tempRaf.readLine()+"\n");
}
raf.setLength(tempRaf.length());
raf.close();
tempRaf.close();
tempFile.delete();
}catch(Exception e)
{
}
}

private long getFileSizeInMegaBytes(File file) 
{
return file.length() / ((1024)*(1024));
}
}

