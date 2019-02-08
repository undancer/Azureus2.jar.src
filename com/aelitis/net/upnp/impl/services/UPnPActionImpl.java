/*    */ package com.aelitis.net.upnp.impl.services;
/*    */ 
/*    */ import com.aelitis.net.upnp.UPnPAction;
/*    */ import com.aelitis.net.upnp.UPnPActionInvocation;
/*    */ import com.aelitis.net.upnp.UPnPService;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
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
/*    */ public class UPnPActionImpl
/*    */   implements UPnPAction
/*    */ {
/*    */   protected UPnPServiceImpl service;
/*    */   protected String name;
/*    */   
/*    */   protected UPnPActionImpl(UPnPServiceImpl _service, SimpleXMLParserDocumentNode node)
/*    */   {
/* 43 */     this.service = _service;
/*    */     
/* 45 */     this.name = node.getChild("name").getValue().trim();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 51 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/*    */   public UPnPService getService()
/*    */   {
/* 57 */     return this.service;
/*    */   }
/*    */   
/*    */ 
/*    */   public UPnPActionInvocation getInvocation()
/*    */   {
/* 63 */     return new UPnPActionInvocationImpl(this);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPActionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */