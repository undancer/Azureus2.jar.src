package com.aelitis.azureus.core.dht.netcoords;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract interface DHTNetworkPosition
{
  public static final byte POSITION_TYPE_NONE = 0;
  public static final byte POSITION_TYPE_VIVALDI_V1 = 1;
  public static final byte POSITION_TYPE_VIVALDI_V2 = 5;
  
  public abstract byte getPositionType();
  
  public abstract int getSerialisedSize();
  
  public abstract float estimateRTT(DHTNetworkPosition paramDHTNetworkPosition);
  
  public abstract void update(byte[] paramArrayOfByte, DHTNetworkPosition paramDHTNetworkPosition, float paramFloat);
  
  public abstract boolean isValid();
  
  public abstract double[] getLocation();
  
  public abstract void serialise(DataOutputStream paramDataOutputStream)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/DHTNetworkPosition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */