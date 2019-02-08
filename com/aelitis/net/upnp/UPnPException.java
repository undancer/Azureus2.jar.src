/*    */ package com.aelitis.net.upnp;
/*    */ 
/*    */ import com.aelitis.net.upnp.impl.services.UPnPActionImpl;
/*    */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
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
/*    */ public class UPnPException
/*    */   extends Exception
/*    */ {
/*    */   public String soap_action;
/*    */   public UPnPActionImpl action;
/*    */   public String fault;
/*    */   public int fault_code;
/*    */   public SimpleXMLParserDocument resp_doc;
/*    */   
/*    */   public UPnPException(String str)
/*    */   {
/* 46 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public UPnPException(String str, Throwable cause)
/*    */   {
/* 53 */     super(str, cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public UPnPException(String string, Throwable e, String soap_action, UPnPActionImpl action, SimpleXMLParserDocument resp_doc)
/*    */   {
/* 64 */     super(string, e);
/* 65 */     this.soap_action = soap_action;
/* 66 */     this.action = action;
/* 67 */     this.resp_doc = resp_doc;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public UPnPException(String message, String soap_action, UPnPActionImpl action, SimpleXMLParserDocument resp_doc, String fault, int fault_code)
/*    */   {
/* 78 */     super(message);
/* 79 */     this.soap_action = soap_action;
/* 80 */     this.action = action;
/* 81 */     this.resp_doc = resp_doc;
/* 82 */     this.fault = fault;
/* 83 */     this.fault_code = fault_code;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */