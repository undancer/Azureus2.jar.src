/*    */ package com.aelitis.net.udp.mc;
/*    */ 
/*    */ import com.aelitis.net.udp.mc.impl.MCGroupImpl;
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
/*    */ public class MCGroupFactory
/*    */ {
/*    */   public static MCGroup getSingleton(MCGroupAdapter adapter, String group_address, int group_port, int control_port, String[] selected_interfaces)
/*    */     throws MCGroupException
/*    */   {
/* 37 */     return MCGroupImpl.getSingleton(adapter, group_address, group_port, control_port, selected_interfaces);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void setSuspended(boolean suspended)
/*    */   {
/* 44 */     MCGroupImpl.setSuspended(suspended);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/mc/MCGroupFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */