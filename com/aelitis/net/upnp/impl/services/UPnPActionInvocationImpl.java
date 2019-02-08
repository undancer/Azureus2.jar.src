/*     */ package com.aelitis.net.upnp.impl.services;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnPActionArgument;
/*     */ import com.aelitis.net.upnp.UPnPActionInvocation;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import com.aelitis.net.upnp.impl.device.UPnPDeviceImpl;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class UPnPActionInvocationImpl
/*     */   implements UPnPActionInvocation
/*     */ {
/*     */   protected UPnPActionImpl action;
/*  41 */   protected List arg_names = new ArrayList();
/*  42 */   protected List arg_values = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   protected UPnPActionInvocationImpl(UPnPActionImpl _action)
/*     */   {
/*  48 */     this.action = _action;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addArgument(String name, String value)
/*     */   {
/*  56 */     this.arg_names.add(name);
/*     */     
/*  58 */     this.arg_values.add(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UPnPActionArgument[] invoke()
/*     */     throws UPnPException
/*     */   {
/*  66 */     UPnPService service = this.action.getService();
/*     */     
/*  68 */     String soap_action = service.getServiceType() + "#" + this.action.getName();
/*  69 */     SimpleXMLParserDocument resp_doc = null;
/*     */     try
/*     */     {
/*  72 */       String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n  <s:Body>\n";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  77 */       request = request + "    <u:" + this.action.getName() + " xmlns:u=\"" + service.getServiceType() + "\">\n";
/*     */       
/*     */ 
/*     */ 
/*  81 */       for (int i = 0; i < this.arg_names.size(); i++)
/*     */       {
/*  83 */         String name = (String)this.arg_names.get(i);
/*  84 */         String value = (String)this.arg_values.get(i);
/*     */         
/*  86 */         request = request + "      <" + name + ">" + value + "</" + name + ">\n";
/*     */       }
/*     */       
/*  89 */       request = request + "    </u:" + this.action.getName() + ">\n";
/*     */       
/*  91 */       request = request + "  </s:Body>\n</s:Envelope>";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  96 */       resp_doc = ((UPnPDeviceImpl)this.action.getService().getDevice()).getUPnP().performSOAPRequest(service, soap_action, request);
/*     */       
/*  98 */       SimpleXMLParserDocumentNode body = resp_doc.getChild("Body");
/*     */       
/* 100 */       SimpleXMLParserDocumentNode faultSection = body.getChild("Fault");
/*     */       
/* 102 */       if (faultSection != null)
/*     */       {
/* 104 */         String faultValue = faultSection.getValue();
/* 105 */         if ((faultValue != null) && (faultValue.length() > 0)) {
/* 106 */           throw new UPnPException("Invoke of '" + soap_action + "' failed - fault reported: " + faultValue, soap_action, this.action, resp_doc, faultValue, -1);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 111 */         SimpleXMLParserDocumentNode faultDetail = faultSection.getChild("detail");
/* 112 */         if (faultDetail != null) {
/* 113 */           SimpleXMLParserDocumentNode error = faultDetail.getChild("UPnPError");
/* 114 */           if (error != null) {
/* 115 */             int errCodeNumber = -1;
/* 116 */             String errDescValue = null;
/*     */             
/* 118 */             SimpleXMLParserDocumentNode errCode = error.getChild("errorCode");
/* 119 */             if (errCode != null) {
/* 120 */               String errCodeValue = errCode.getValue();
/*     */               try {
/* 122 */                 errCodeNumber = Integer.parseInt(errCodeValue);
/*     */               }
/*     */               catch (Throwable t) {}
/*     */             }
/*     */             
/* 127 */             SimpleXMLParserDocumentNode errDesc = error.getChild("errorDescription");
/* 128 */             if (errDesc != null) {
/* 129 */               errDescValue = errDesc.getValue();
/* 130 */               if ((errDescValue != null) && (errDescValue.length() == 0)) {
/* 131 */                 errDescValue = null;
/*     */               }
/*     */             }
/*     */             
/* 135 */             throw new UPnPException("Invoke of '" + soap_action + "' failed - fault reported: " + errDescValue, soap_action, this.action, resp_doc, errDescValue, errCodeNumber);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 142 */       SimpleXMLParserDocumentNode resp_node = body.getChild(this.action.getName() + "Response");
/*     */       
/* 144 */       if (resp_node == null)
/*     */       {
/* 146 */         throw new UPnPException("Invoke of '" + soap_action + "' failed - response missing: " + body.getValue(), soap_action, this.action, resp_doc, null, -1);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 151 */       SimpleXMLParserDocumentNode[] out_nodes = resp_node.getChildren();
/*     */       
/* 153 */       UPnPActionArgument[] resp = new UPnPActionArgument[out_nodes.length];
/*     */       
/* 155 */       for (int i = 0; i < out_nodes.length; i++)
/*     */       {
/* 157 */         resp[i] = new UPnPActionArgumentImpl(out_nodes[i].getName(), out_nodes[i].getValue());
/*     */       }
/*     */       
/* 160 */       return resp;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 164 */       if ((e instanceof UPnPException))
/*     */       {
/* 166 */         throw ((UPnPException)e);
/*     */       }
/*     */       
/* 169 */       throw new UPnPException("Invoke of '" + soap_action + "' on '" + this.action.getService().getControlURLs() + "' failed: " + e.getMessage(), e, soap_action, this.action, resp_doc);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map invoke2()
/*     */     throws UPnPException
/*     */   {
/* 180 */     UPnPActionArgument[] res = invoke();
/*     */     
/* 182 */     Map map = new HashMap();
/*     */     
/* 184 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 186 */       map.put(res[i].getName(), res[i].getValue());
/*     */     }
/*     */     
/* 189 */     return map;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPActionInvocationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */