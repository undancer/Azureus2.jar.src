package com.aelitis.azureus.core.dht.netcoords;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract interface DHTNetworkPositionProvider
{
  public abstract byte getPositionType();
  
  public abstract DHTNetworkPosition create(byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract DHTNetworkPosition getLocalPosition();
  
  public abstract DHTNetworkPosition deserialisePosition(DataInputStream paramDataInputStream)
    throws IOException;
  
  public abstract void serialiseStats(DataOutputStream paramDataOutputStream)
    throws IOException;
  
  public abstract void startUp(DataInputStream paramDataInputStream);
  
  public abstract void shutDown(DataOutputStream paramDataOutputStream);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/DHTNetworkPositionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */