/*    */ package org.gudy.azureus2.pluginsimpl.local.utils.resourceuploader;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploader;
/*    */ import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderFactory;
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
/*    */ public class ResourceUploaderFactoryImpl
/*    */   implements ResourceUploaderFactory
/*    */ {
/* 36 */   private static ResourceUploaderFactory singleton = new ResourceUploaderFactoryImpl();
/*    */   
/*    */ 
/*    */   public static ResourceUploaderFactory getSingleton()
/*    */   {
/* 41 */     return singleton;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ResourceUploader create(URL url, InputStream data)
/*    */   {
/* 49 */     return create(url, data, null, null);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ResourceUploader create(URL url, InputStream data, String user_name, String password)
/*    */   {
/* 59 */     return new ResourceUploaderURLImpl(url, data, user_name, password);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourceuploader/ResourceUploaderFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */