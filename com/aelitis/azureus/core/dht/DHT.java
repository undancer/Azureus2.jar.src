/*    */ package com.aelitis.azureus.core.dht;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*    */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*    */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*    */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface DHT
/*    */ {
/*    */   public static final String PR_CONTACTS_PER_NODE = "EntriesPerNode";
/*    */   public static final String PR_NODE_SPLIT_FACTOR = "NodeSplitFactor";
/*    */   public static final String PR_SEARCH_CONCURRENCY = "SearchConcurrency";
/*    */   public static final String PR_LOOKUP_CONCURRENCY = "LookupConcurrency";
/*    */   public static final String PR_MAX_REPLACEMENTS_PER_NODE = "ReplacementsPerNode";
/*    */   public static final String PR_CACHE_AT_CLOSEST_N = "CacheClosestN";
/*    */   public static final String PR_ORIGINAL_REPUBLISH_INTERVAL = "OriginalRepublishInterval";
/*    */   public static final String PR_CACHE_REPUBLISH_INTERVAL = "CacheRepublishInterval";
/*    */   public static final String PR_ENCODE_KEYS = "EncodeKeys";
/*    */   public static final String PR_ENABLE_RANDOM_LOOKUP = "EnableRandomLookup";
/*    */   public static final short FLAG_NONE = 0;
/*    */   public static final short FLAG_SINGLE_VALUE = 0;
/*    */   public static final short FLAG_DOWNLOADING = 1;
/*    */   public static final short FLAG_SEEDING = 2;
/*    */   public static final short FLAG_MULTI_VALUE = 4;
/*    */   public static final short FLAG_STATS = 8;
/*    */   public static final short FLAG_ANON = 16;
/*    */   public static final short FLAG_PRECIOUS = 32;
/*    */   public static final short FLAG_BRIDGED = 64;
/*    */   public static final short FLAG_PUT_AND_FORGET = 256;
/*    */   public static final short FLAG_OBFUSCATE_LOOKUP = 512;
/*    */   public static final short FLAG_LOOKUP_FOR_STORE = 1024;
/*    */   public static final short FLAG_HIGH_PRIORITY = 2048;
/*    */   public static final int MAX_VALUE_SIZE = 512;
/*    */   public static final byte REP_FACT_NONE = 0;
/*    */   public static final byte REP_FACT_DEFAULT = -1;
/*    */   public static final byte DT_NONE = 1;
/*    */   public static final byte DT_FREQUENCY = 2;
/*    */   public static final byte DT_SIZE = 3;
/* 82 */   public static final String[] DT_STRINGS = { "", "None", "Freq", "Size" };
/*    */   public static final int NW_MAIN = 0;
/*    */   public static final int NW_CVS = 1;
/*    */   public static final int NW_MAIN_V6 = 3;
/*    */   
/*    */   public abstract void put(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, short paramShort, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract void put(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, short paramShort, boolean paramBoolean, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract void put(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, short paramShort, byte paramByte, boolean paramBoolean, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract void put(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, short paramShort, byte paramByte1, byte paramByte2, boolean paramBoolean, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract DHTTransportValue getLocalValue(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract List<DHTTransportValue> getStoredValues(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract void get(byte[] paramArrayOfByte, String paramString, short paramShort, int paramInt, long paramLong, boolean paramBoolean1, boolean paramBoolean2, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract byte[] remove(byte[] paramArrayOfByte, String paramString, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract byte[] remove(DHTTransportContact[] paramArrayOfDHTTransportContact, byte[] paramArrayOfByte, String paramString, DHTOperationListener paramDHTOperationListener);
/*    */   
/*    */   public abstract boolean isDiversified(byte[] paramArrayOfByte);
/*    */   
/*    */   public abstract int getIntProperty(String paramString);
/*    */   
/*    */   public abstract DHTTransport getTransport();
/*    */   
/*    */   public abstract DHTRouter getRouter();
/*    */   
/*    */   public abstract DHTControl getControl();
/*    */   
/*    */   public abstract DHTDB getDataBase();
/*    */   
/*    */   public abstract DHTNATPuncher getNATPuncher();
/*    */   
/*    */   public abstract DHTStorageAdapter getStorageAdapter();
/*    */   
/*    */   public abstract void exportState(DataOutputStream paramDataOutputStream, int paramInt)
/*    */     throws IOException;
/*    */   
/*    */   public abstract void importState(DataInputStream paramDataInputStream)
/*    */     throws IOException;
/*    */   
/*    */   public abstract void integrate(boolean paramBoolean);
/*    */   
/*    */   public abstract void setSuspended(boolean paramBoolean);
/*    */   
/*    */   public abstract void destroy();
/*    */   
/*    */   public abstract boolean isSleeping();
/*    */   
/*    */   public abstract void setLogging(boolean paramBoolean);
/*    */   
/*    */   public abstract DHTLogger getLogger();
/*    */   
/*    */   public abstract void print(boolean paramBoolean);
/*    */   
/*    */   public abstract void addListener(DHTListener paramDHTListener);
/*    */   
/*    */   public abstract void removeListener(DHTListener paramDHTListener);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */