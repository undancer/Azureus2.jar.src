package com.aelitis.azureus.core.dht;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
import java.io.DataInputStream;
import java.io.IOException;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface DHTStorageAdapter
{
  public abstract int getNetwork();
  
  public abstract DHTStorageKey keyCreated(HashWrapper paramHashWrapper, boolean paramBoolean);
  
  public abstract void keyDeleted(DHTStorageKey paramDHTStorageKey);
  
  public abstract void keyRead(DHTStorageKey paramDHTStorageKey, DHTTransportContact paramDHTTransportContact);
  
  public abstract DHTStorageKeyStats deserialiseStats(DataInputStream paramDataInputStream)
    throws IOException;
  
  public abstract void valueAdded(DHTStorageKey paramDHTStorageKey, DHTTransportValue paramDHTTransportValue);
  
  public abstract void valueUpdated(DHTStorageKey paramDHTStorageKey, DHTTransportValue paramDHTTransportValue1, DHTTransportValue paramDHTTransportValue2);
  
  public abstract void valueDeleted(DHTStorageKey paramDHTStorageKey, DHTTransportValue paramDHTTransportValue);
  
  public abstract boolean isDiversified(byte[] paramArrayOfByte);
  
  public abstract byte[][] getExistingDiversification(byte[] paramArrayOfByte, boolean paramBoolean1, boolean paramBoolean2, int paramInt);
  
  public abstract byte[][] createNewDiversification(String paramString, DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte, boolean paramBoolean1, byte paramByte, boolean paramBoolean2, int paramInt);
  
  public abstract int getNextValueVersions(int paramInt);
  
  public abstract DHTStorageBlock keyBlockRequest(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract DHTStorageBlock getKeyBlockDetails(byte[] paramArrayOfByte);
  
  public abstract DHTStorageBlock[] getDirectKeyBlocks();
  
  public abstract byte[] getKeyForKeyBlock(byte[] paramArrayOfByte);
  
  public abstract void setStorageForKey(String paramString, byte[] paramArrayOfByte);
  
  public abstract byte[] getStorageForKey(String paramString);
  
  public abstract int getRemoteFreqDivCount();
  
  public abstract int getRemoteSizeDivCount();
  
  public abstract int getKeyCount();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTStorageAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */