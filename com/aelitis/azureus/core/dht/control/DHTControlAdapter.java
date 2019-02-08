package com.aelitis.azureus.core.dht.control;

import com.aelitis.azureus.core.dht.DHTStorageAdapter;
import com.aelitis.azureus.core.dht.transport.DHTTransportContact;

public abstract interface DHTControlAdapter
{
  public abstract DHTStorageAdapter getStorageAdapter();
  
  public abstract byte[][] diversify(String paramString, DHTTransportContact paramDHTTransportContact, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfByte, byte paramByte, boolean paramBoolean3, int paramInt);
  
  public abstract boolean isDiversified(byte[] paramArrayOfByte);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControlAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */