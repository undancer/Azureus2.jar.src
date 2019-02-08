/*    */ package com.aelitis.net.upnpms.impl;
/*    */ 
/*    */ import com.aelitis.net.upnpms.UPNPMSItem;
/*    */ import java.net.URL;
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
/*    */ public class UPNPMSItemImpl
/*    */   implements UPNPMSItem
/*    */ {
/*    */   private String id;
/*    */   private String title;
/*    */   private String item_class;
/*    */   private long size;
/*    */   private URL url;
/*    */   
/*    */   protected UPNPMSItemImpl(String _id, String _title, String _class, long _size, URL _url)
/*    */   {
/* 45 */     this.id = _id;
/* 46 */     this.title = _title;
/* 47 */     this.item_class = _class;
/* 48 */     this.size = _size;
/* 49 */     this.url = _url;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getID()
/*    */   {
/* 55 */     return this.id;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getTitle()
/*    */   {
/* 61 */     return this.title;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getItemClass()
/*    */   {
/* 67 */     return this.item_class;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getSize()
/*    */   {
/* 73 */     return this.size;
/*    */   }
/*    */   
/*    */ 
/*    */   public URL getURL()
/*    */   {
/* 79 */     return this.url;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/impl/UPNPMSItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */