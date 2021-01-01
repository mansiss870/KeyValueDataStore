package com.freshworks.utility;
import com.freshworks.exceptions.*;

public class Key
{

private String key;
private Integer timeToLive;
private boolean isTimeToLive;

public Key(String key) throws ValidationException
{
if(key == null || key.trim().length()==0 ) throw new ValidationException("Invalid key.");
if(key.trim().length() > 32) throw new ValidationException("Key cannot exceed 32 characters.");
this.key = key.trim();
this.timeToLive = Integer.MAX_VALUE;
this.isTimeToLive = false;
}

public Key(String key , Integer timeToLive) throws ValidationException
{
if(key == null ||  key.trim().length()==0 ) throw new ValidationException("Invalid key.");
if(key.trim().length() > 32) throw new ValidationException("Key cannot exceed 32 characters.");
if(timeToLive <= 0) throw new ValidationException("Time-to-live cannot be negative or zero");
this.key = key.trim();
this.timeToLive = timeToLive;
this.isTimeToLive = true;
}

public String getKey()
{
return this.key;
}

public Integer getTimeToLive()
{
return this.timeToLive;
}

public boolean isTimeToLivePresent()
{
return this.isTimeToLive;
}

}