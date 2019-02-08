/*     */ package com.aelitis.net.upnp.impl.services;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.UPnPStateVariable;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import com.aelitis.net.upnp.impl.device.UPnPDeviceImpl;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UPnPStateVariableImpl
/*     */   implements UPnPStateVariable
/*     */ {
/*     */   protected UPnPServiceImpl service;
/*     */   protected String name;
/*     */   
/*     */   protected UPnPStateVariableImpl(UPnPServiceImpl _service, SimpleXMLParserDocumentNode node)
/*     */   {
/*  45 */     this.service = _service;
/*     */     
/*  47 */     this.name = node.getChild("name").getValue().trim();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  53 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPService getService()
/*     */   {
/*  59 */     return this.service;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getValue()
/*     */     throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/*  68 */       String soap_action = "urn:schemas-upnp-org:control-1-0#QueryStateVariable";
/*     */       
/*  70 */       String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body>";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  75 */       request = request + "<u:QueryStateVariable xmlns:u=\"urn:schemas-upnp-org:control-1-0\"><u:varName>" + this.name + "</u:varName>" + "</u:QueryStateVariable>";
/*     */       
/*     */ 
/*     */ 
/*  79 */       request = request + "</s:Body></s:Envelope>";
/*     */       
/*     */ 
/*  82 */       SimpleXMLParserDocument resp_doc = ((UPnPDeviceImpl)this.service.getDevice()).getUPnP().performSOAPRequest(this.service, soap_action, request);
/*     */       
/*  84 */       SimpleXMLParserDocumentNode body = resp_doc.getChild("Body");
/*     */       
/*  86 */       SimpleXMLParserDocumentNode fault = body.getChild("Fault");
/*     */       
/*  88 */       if (fault != null)
/*     */       {
/*  90 */         throw new UPnPException("Invoke fails - fault reported: " + fault.getValue());
/*     */       }
/*     */       
/*  93 */       SimpleXMLParserDocumentNode resp_node = body.getChild("QueryStateVariableResponse");
/*     */       
/*  95 */       if (resp_node == null)
/*     */       {
/*  97 */         throw new UPnPException("Invoke fails - response missing: " + body.getValue());
/*     */       }
/*     */       
/* 100 */       SimpleXMLParserDocumentNode value_node = resp_node.getChild("return");
/*     */       
/* 102 */       return value_node.getValue();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 106 */       if ((e instanceof UPnPException))
/*     */       {
/* 108 */         throw ((UPnPException)e);
/*     */       }
/*     */       
/* 111 */       throw new UPnPException("Invoke fails", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPStateVariableImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */