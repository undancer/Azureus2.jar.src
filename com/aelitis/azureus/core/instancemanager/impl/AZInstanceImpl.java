/*    */ package com.aelitis.azureus.core.instancemanager.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*    */ import java.net.InetAddress;
/*    */ import java.util.Map;
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
/*    */ public abstract class AZInstanceImpl
/*    */   implements AZInstance
/*    */ {
/*    */   protected void encode(Map<String, Object> map)
/*    */   {
/* 40 */     map.put("id", getID().getBytes());
/*    */     
/* 42 */     map.put("ai", getApplicationID().getBytes());
/*    */     
/* 44 */     map.put("iip", getInternalAddress().getHostAddress().getBytes());
/*    */     
/* 46 */     map.put("eip", getExternalAddress().getHostAddress().getBytes());
/*    */     
/* 48 */     map.put("tp", new Long(getTCPListenPort()));
/*    */     
/* 50 */     map.put("dp", new Long(getUDPListenPort()));
/*    */     
/* 52 */     map.put("dp2", new Long(getUDPNonDataListenPort()));
/*    */     
/* 54 */     Map<String, Object> props = getProperties();
/*    */     
/* 56 */     if (props != null)
/*    */     {
/* 58 */       map.put("pr", props);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 65 */     String id = getID();
/*    */     
/* 67 */     if (id.length() > 8)
/*    */     {
/* 69 */       id = id.substring(0, 8) + "...";
/*    */     }
/*    */     
/* 72 */     return "id=" + id + ",ap=" + getApplicationID() + ",int=" + getInternalAddress().getHostAddress() + ",ext=" + getExternalAddress().getHostAddress() + ",tcp=" + getTCPListenPort() + ",udp=" + getUDPListenPort() + ",udp2=" + getUDPNonDataListenPort() + ",props=" + getProperties();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/impl/AZInstanceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */