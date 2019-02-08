/*     */ package com.aelitis.net.upnpms.impl;
/*     */ 
/*     */ import com.aelitis.net.upnpms.UPNPMSContainer;
/*     */ import com.aelitis.net.upnpms.UPNPMSNode;
/*     */ import com.aelitis.net.upnpms.UPnPMSException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UPNPMSContainerImpl
/*     */   implements UPNPMSContainer
/*     */ {
/*     */   private UPNPMSBrowserImpl browser;
/*     */   private String id;
/*     */   private String title;
/*     */   private List<UPNPMSNode> children;
/*     */   
/*     */   protected UPNPMSContainerImpl(UPNPMSBrowserImpl _browser, String _id, String _title)
/*     */   {
/*  51 */     this.browser = _browser;
/*  52 */     this.id = _id;
/*  53 */     this.title = _title;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  59 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  65 */     return this.title;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void populate()
/*     */     throws UPnPMSException
/*     */   {
/*  73 */     synchronized (this)
/*     */     {
/*  75 */       if (this.children == null)
/*     */       {
/*  77 */         this.children = new ArrayList();
/*     */         
/*  79 */         List<SimpleXMLParserDocumentNode> results = this.browser.getContainerContents(this.id);
/*     */         
/*  81 */         for (SimpleXMLParserDocumentNode result : results)
/*     */         {
/*     */ 
/*     */ 
/*  85 */           SimpleXMLParserDocumentNode[] kids = result.getChildren();
/*     */           
/*  87 */           for (SimpleXMLParserDocumentNode kid : kids)
/*     */           {
/*  89 */             String name = kid.getName();
/*     */             
/*  91 */             if (name.equalsIgnoreCase("container"))
/*     */             {
/*  93 */               String id = kid.getAttribute("id").getValue();
/*  94 */               String title = kid.getChild("title").getValue();
/*     */               
/*  96 */               this.children.add(new UPNPMSContainerImpl(this.browser, id, title));
/*     */             }
/*  98 */             else if (name.equalsIgnoreCase("item"))
/*     */             {
/* 100 */               String id = kid.getAttribute("id").getValue();
/* 101 */               String title = kid.getChild("title").getValue();
/* 102 */               String cla = kid.getChild("class").getValue();
/*     */               
/*     */               String item_class;
/*     */               String item_class;
/* 106 */               if (cla.contains(".imageItem"))
/*     */               {
/* 108 */                 item_class = "image";
/*     */               } else { String item_class;
/* 110 */                 if (cla.contains(".audioItem"))
/*     */                 {
/* 112 */                   item_class = "audio";
/*     */                 } else { String item_class;
/* 114 */                   if (cla.contains(".videoItem"))
/*     */                   {
/* 116 */                     item_class = "video";
/*     */                   }
/*     */                   else
/*     */                   {
/* 120 */                     item_class = "other"; }
/*     */                 }
/*     */               }
/* 123 */               URL url = null;
/* 124 */               long size = 0L;
/*     */               
/* 126 */               SimpleXMLParserDocumentNode[] sub = kid.getChildren();
/*     */               
/* 128 */               for (SimpleXMLParserDocumentNode x : sub)
/*     */               {
/* 130 */                 if (x.getName().equalsIgnoreCase("res"))
/*     */                 {
/* 132 */                   SimpleXMLParserDocumentAttribute a_size = x.getAttribute("size");
/*     */                   
/* 134 */                   long this_size = 0L;
/*     */                   
/* 136 */                   if (a_size != null) {
/*     */                     try
/*     */                     {
/* 139 */                       this_size = Long.parseLong(a_size.getValue().trim());
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/*     */                   
/*     */ 
/* 145 */                   SimpleXMLParserDocumentAttribute pi = x.getAttribute("protocolInfo");
/*     */                   
/* 147 */                   if (pi != null)
/*     */                   {
/* 149 */                     String pi_str = pi.getValue().trim();
/*     */                     
/* 151 */                     if (pi_str.toLowerCase().startsWith("http-get")) {
/*     */                       try
/*     */                       {
/* 154 */                         if ((size == 0L) || (this_size > size))
/*     */                         {
/* 156 */                           url = new URL(x.getValue().trim());
/*     */                           
/* 158 */                           size = this_size;
/*     */                         }
/*     */                       }
/*     */                       catch (Throwable e) {}
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/* 168 */               if (url != null)
/*     */               {
/* 170 */                 this.children.add(new UPNPMSItemImpl(id, title, item_class, size, url));
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<UPNPMSNode> getChildren()
/*     */     throws UPnPMSException
/*     */   {
/* 184 */     populate();
/*     */     
/* 186 */     return this.children;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/impl/UPNPMSContainerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */