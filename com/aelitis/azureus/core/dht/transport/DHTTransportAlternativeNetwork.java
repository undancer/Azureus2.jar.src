/*    */ package com.aelitis.azureus.core.dht.transport;
/*    */ 
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
/*    */ public abstract interface DHTTransportAlternativeNetwork
/*    */ {
/*    */   public static final int AT_MLDHT_IPV4 = 1;
/*    */   public static final int AT_MLDHT_IPV6 = 2;
/*    */   public static final int AT_I2P = 3;
/* 32 */   public static final int[] AT_ALL = { 1, 2, 3 };
/*    */   
/*    */   public abstract int getNetworkType();
/*    */   
/*    */   public abstract List<DHTTransportAlternativeContact> getContacts(int paramInt);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportAlternativeNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */