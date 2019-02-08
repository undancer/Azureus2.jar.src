/*    */ package com.aelitis.net.upnp.impl.device;
/*    */ 
/*    */ import com.aelitis.net.upnp.UPnPDeviceImage;
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
/*    */ public class UPnPDeviceImageImpl
/*    */   implements UPnPDeviceImage
/*    */ {
/*    */   public int width;
/*    */   public int height;
/*    */   public String location;
/*    */   public String mime;
/*    */   
/*    */   public UPnPDeviceImageImpl(int width, int height, String location, String mime)
/*    */   {
/* 35 */     this.width = width;
/* 36 */     this.height = height;
/* 37 */     this.location = location;
/* 38 */     this.mime = mime;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 42 */     return this.width;
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 46 */     return this.height;
/*    */   }
/*    */   
/*    */   public String getLocation() {
/* 50 */     return this.location;
/*    */   }
/*    */   
/*    */   public String getMime() {
/* 54 */     return this.mime;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/device/UPnPDeviceImageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */