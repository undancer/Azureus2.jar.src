/*    */ package com.aelitis.net.upnp.impl.services;
/*    */ 
/*    */ import com.aelitis.net.upnp.UPnPActionArgument;
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
/*    */ public class UPnPActionArgumentImpl
/*    */   implements UPnPActionArgument
/*    */ {
/*    */   protected String name;
/*    */   protected String value;
/*    */   
/*    */   protected UPnPActionArgumentImpl(String _name, String _value)
/*    */   {
/* 41 */     this.name = _name;
/* 42 */     this.value = _value;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 48 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 54 */     return this.value;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPActionArgumentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */