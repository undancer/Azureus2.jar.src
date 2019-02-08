/*    */ package com.aelitis.azureus.plugins.dht.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*    */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
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
/*    */ public class DHTPluginValueImpl
/*    */   implements DHTPluginValue
/*    */ {
/*    */   private DHTTransportValue value;
/*    */   
/*    */   protected DHTPluginValueImpl(DHTTransportValue _value)
/*    */   {
/* 36 */     this.value = _value;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getValue()
/*    */   {
/* 42 */     return this.value.getValue();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getCreationTime()
/*    */   {
/* 48 */     return this.value.getCreationTime();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getVersion()
/*    */   {
/* 54 */     return this.value.getVersion();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isLocal()
/*    */   {
/* 60 */     return this.value.isLocal();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getFlags()
/*    */   {
/* 66 */     return this.value.getFlags() & 0xFF;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/impl/DHTPluginValueImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */