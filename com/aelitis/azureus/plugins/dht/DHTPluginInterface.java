package com.aelitis.azureus.plugins.dht;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public abstract interface DHTPluginInterface
{
  public static final byte FLAG_SINGLE_VALUE = 0;
  public static final byte FLAG_DOWNLOADING = 1;
  public static final byte FLAG_SEEDING = 2;
  public static final byte FLAG_MULTI_VALUE = 4;
  public static final byte FLAG_STATS = 8;
  public static final byte FLAG_ANON = 16;
  public static final byte FLAG_PRECIOUS = 32;
  public static final byte FLAG_BRIDGED = 64;
  public static final int MAX_VALUE_SIZE = 512;
  
  public abstract boolean isEnabled();
  
  public abstract boolean isExtendedUseAllowed();
  
  public abstract boolean isInitialising();
  
  public abstract boolean isSleeping();
  
  public abstract DHTPluginContact getLocalAddress();
  
  public abstract String getNetwork();
  
  public abstract DHTPluginKeyStats decodeStats(DHTPluginValue paramDHTPluginValue);
  
  public abstract void registerHandler(byte[] paramArrayOfByte, DHTPluginTransferHandler paramDHTPluginTransferHandler, Map<String, Object> paramMap);
  
  public abstract void unregisterHandler(byte[] paramArrayOfByte, DHTPluginTransferHandler paramDHTPluginTransferHandler);
  
  public abstract DHTPluginContact importContact(InetSocketAddress paramInetSocketAddress);
  
  public abstract DHTPluginContact importContact(InetSocketAddress paramInetSocketAddress, byte paramByte);
  
  public abstract DHTPluginContact importContact(InetSocketAddress paramInetSocketAddress, byte paramByte, boolean paramBoolean);
  
  public abstract DHTPluginContact importContact(Map<String, Object> paramMap);
  
  public abstract void get(byte[] paramArrayOfByte, String paramString, byte paramByte, int paramInt, long paramLong, boolean paramBoolean1, boolean paramBoolean2, DHTPluginOperationListener paramDHTPluginOperationListener);
  
  public abstract void put(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, byte paramByte, DHTPluginOperationListener paramDHTPluginOperationListener);
  
  public abstract DHTInterface[] getDHTInterfaces();
  
  public abstract List<DHTPluginValue> getValues();
  
  public abstract List<DHTPluginValue> getValues(byte[] paramArrayOfByte);
  
  public abstract void remove(byte[] paramArrayOfByte, String paramString, DHTPluginOperationListener paramDHTPluginOperationListener);
  
  public abstract void remove(DHTPluginContact[] paramArrayOfDHTPluginContact, byte[] paramArrayOfByte, String paramString, DHTPluginOperationListener paramDHTPluginOperationListener);
  
  public abstract void addListener(DHTPluginListener paramDHTPluginListener);
  
  public abstract void removeListener(DHTPluginListener paramDHTPluginListener);
  
  public abstract void log(String paramString);
  
  public static abstract interface DHTInterface
  {
    public abstract byte[] getID();
    
    public abstract boolean isIPV6();
    
    public abstract int getNetwork();
    
    public abstract DHTPluginContact[] getReachableContacts();
    
    public abstract DHTPluginContact[] getRecentContacts();
    
    public abstract List<DHTPluginContact> getClosestContacts(byte[] paramArrayOfByte, boolean paramBoolean);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */