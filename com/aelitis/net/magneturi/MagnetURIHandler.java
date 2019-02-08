/*    */ package com.aelitis.net.magneturi;
/*    */ 
/*    */ import com.aelitis.net.magneturi.impl.MagnetURIHandlerImpl;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
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
/*    */ public abstract class MagnetURIHandler
/*    */ {
/*    */   public static MagnetURIHandler getSingleton()
/*    */   {
/* 40 */     return MagnetURIHandlerImpl.getSingleton();
/*    */   }
/*    */   
/*    */   public abstract int getPort();
/*    */   
/*    */   public abstract void process(String paramString, InputStream paramInputStream, OutputStream paramOutputStream)
/*    */     throws IOException;
/*    */   
/*    */   public abstract void addListener(MagnetURIHandlerListener paramMagnetURIHandlerListener);
/*    */   
/*    */   public abstract void removeListener(MagnetURIHandlerListener paramMagnetURIHandlerListener);
/*    */   
/*    */   public abstract void addInfo(String paramString, int paramInt);
/*    */   
/*    */   public abstract URL registerResource(ResourceProvider paramResourceProvider);
/*    */   
/*    */   public static abstract interface ResourceProvider
/*    */   {
/*    */     public abstract String getUID();
/*    */     
/*    */     public abstract String getFileType();
/*    */     
/*    */     public abstract byte[] getData();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/magneturi/MagnetURIHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */