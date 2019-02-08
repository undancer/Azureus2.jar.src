/*    */ package com.aelitis.azureus.plugins.dht;
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
/*    */ public class DHTPluginOperationAdapter
/*    */   implements DHTPluginOperationListener
/*    */ {
/*    */   public void starts(byte[] key) {}
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
/*    */   public boolean diversified()
/*    */   {
/* 38 */     return true;
/*    */   }
/*    */   
/*    */   public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*    */   
/*    */   public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*    */   
/*    */   public void complete(byte[] key, boolean timeout_occurred) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPluginOperationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */