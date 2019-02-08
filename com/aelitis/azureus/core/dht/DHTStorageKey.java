package com.aelitis.azureus.core.dht;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract interface DHTStorageKey
{
  public abstract byte getDiversificationType();
  
  public abstract void serialiseStats(DataOutputStream paramDataOutputStream)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTStorageKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */