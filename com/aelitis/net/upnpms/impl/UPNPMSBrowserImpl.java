/*     */ package com.aelitis.net.upnpms.impl;
/*     */ 
/*     */ import com.aelitis.net.upnpms.UPNPMSBrowser;
/*     */ import com.aelitis.net.upnpms.UPNPMSBrowserListener;
/*     */ import com.aelitis.net.upnpms.UPNPMSContainer;
/*     */ import com.aelitis.net.upnpms.UPnPMSException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.xml.simpleparser.SimpleXMLParserDocumentFactory;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ 
/*     */ public class UPNPMSBrowserImpl
/*     */   implements UPNPMSBrowser
/*     */ {
/*     */   private List<URL> endpoints;
/*     */   private String client_name;
/*     */   private UPNPMSBrowserListener listener;
/*     */   private UPNPMSContainerImpl root;
/*     */   private URL preferred_endpoint;
/*     */   
/*     */   public UPNPMSBrowserImpl(String _client_name, List<URL> _urls, UPNPMSBrowserListener _listener)
/*     */     throws UPnPMSException
/*     */   {
/*  55 */     this.client_name = _client_name;
/*  56 */     this.endpoints = _urls;
/*  57 */     this.listener = _listener;
/*     */     
/*  59 */     this.client_name = this.client_name.replaceAll("\"", "'");
/*  60 */     this.client_name = this.client_name.replaceAll(";", ",");
/*  61 */     this.client_name = this.client_name.replaceAll("=", "-");
/*     */     
/*  63 */     this.root = new UPNPMSContainerImpl(this, "0", "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UPNPMSContainer getRoot()
/*     */     throws UPnPMSException
/*     */   {
/*  71 */     return this.root;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setPreferredEndpoint(URL url)
/*     */   {
/*  78 */     if (this.endpoints.size() > 1)
/*     */     {
/*  80 */       if (url != this.preferred_endpoint)
/*     */       {
/*  82 */         this.preferred_endpoint = url;
/*     */         
/*  84 */         this.listener.setPreferredURL(this.preferred_endpoint);
/*     */         
/*  86 */         this.endpoints.remove(this.preferred_endpoint);
/*  87 */         this.endpoints.add(0, this.preferred_endpoint);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected List<SimpleXMLParserDocumentNode> getContainerContents(String id)
/*     */     throws UPnPMSException
/*     */   {
/*     */     try
/*     */     {
/*  99 */       List<SimpleXMLParserDocumentNode> results = new ArrayList();
/*     */       
/* 101 */       int starting_index = 0;
/*     */       
/*     */       for (;;)
/*     */       {
/* 105 */         String NL = "\r\n";
/*     */         
/* 107 */         String soap_action = "urn:schemas-upnp-org:service:ContentDirectory:1#Browse";
/*     */         
/* 109 */         String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + NL + "<s:Body>" + NL + "<u:Browse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\">" + NL + "<ObjectID>" + id + "</ObjectID>" + NL + "<BrowseFlag>BrowseDirectChildren</BrowseFlag>" + NL + "<Filter>dc:date,res@protocolInfo,res@size</Filter>" + NL + "<StartingIndex>" + starting_index + "</StartingIndex>" + NL + "<RequestedCount>256</RequestedCount>" + NL + "<SortCriteria></SortCriteria>" + NL + "</u:Browse>" + NL + "</s:Body>" + NL + "</s:Envelope>";
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
/* 124 */         SimpleXMLParserDocument doc = null;
/*     */         
/* 126 */         UPnPMSException last_error = null;
/*     */         
/* 128 */         for (URL endpoint : new ArrayList(this.endpoints)) {
/*     */           try
/*     */           {
/* 131 */             doc = getXML(endpoint, soap_action, request);
/*     */             
/* 133 */             setPreferredEndpoint(endpoint);
/*     */ 
/*     */           }
/*     */           catch (UPnPMSException e)
/*     */           {
/*     */ 
/* 139 */             last_error = e;
/*     */           }
/*     */         }
/*     */         
/* 143 */         if (doc == null)
/*     */         {
/* 145 */           throw last_error;
/*     */         }
/*     */         
/* 148 */         SimpleXMLParserDocumentNode body = doc.getChild("Body");
/*     */         
/* 150 */         SimpleXMLParserDocumentNode response = body.getChild("BrowseResponse");
/*     */         
/* 152 */         SimpleXMLParserDocumentNode didl_result = response.getChild("Result");
/*     */         
/* 154 */         String didl_str = didl_result.getValue();
/*     */         
/* 156 */         SimpleXMLParserDocument didle_doc = SimpleXMLParserDocumentFactory.create(didl_str);
/*     */         
/* 158 */         results.add(didle_doc);
/*     */         
/* 160 */         int num_returned = Integer.parseInt(response.getChild("NumberReturned").getValue());
/*     */         
/* 162 */         if (num_returned <= 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 167 */         starting_index += num_returned;
/*     */         
/* 169 */         int total_matches = Integer.parseInt(response.getChild("TotalMatches").getValue());
/*     */         
/* 171 */         if (starting_index >= total_matches) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 177 */       return results;
/*     */     }
/*     */     catch (UPnPMSException e)
/*     */     {
/* 181 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 185 */       throw new UPnPMSException("Failed to read container", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SimpleXMLParserDocument getXML(URL url, String soap_action, String post_data)
/*     */     throws UPnPMSException
/*     */   {
/* 197 */     ResourceDownloader rd = new ResourceDownloaderFactoryImpl().create(url, post_data);
/*     */     try
/*     */     {
/* 200 */       rd.setProperty("URL_Connection", "Keep-Alive");
/* 201 */       rd.setProperty("URL_Read_Timeout", Integer.valueOf(600000));
/* 202 */       rd.setProperty("URL_Connect_Timeout", Integer.valueOf(300000));
/* 203 */       rd.setProperty("URL_SOAPAction", "\"" + soap_action + "\"");
/* 204 */       rd.setProperty("URL_X-AV-Client-Info", "av=1.0; cn=\"Azureus Software, Inc.\"; mn=\"" + this.client_name + "\"; mv=\"" + "5.7.6.0" + "\"");
/* 205 */       rd.setProperty("URL_Content-Type", "text/xml; charset=\"utf-8\"");
/*     */       
/* 207 */       return SimpleXMLParserDocumentFactory.create(url, rd.download());
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/*     */ 
/* 215 */       throw new UPnPMSException("XML RPC failed", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/impl/UPNPMSBrowserImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */