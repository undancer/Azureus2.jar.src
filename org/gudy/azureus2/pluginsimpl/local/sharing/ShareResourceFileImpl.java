/*    */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*    */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShareResourceFileImpl
/*    */   extends ShareResourceFileOrDirImpl
/*    */   implements ShareResourceFile
/*    */ {
/*    */   protected static ShareResourceFileImpl getResource(ShareManagerImpl _manager, File _file)
/*    */     throws ShareException
/*    */   {
/* 47 */     ShareResourceImpl res = ShareResourceFileOrDirImpl.getResourceSupport(_manager, _file);
/*    */     
/* 49 */     if ((res instanceof ShareResourceFileImpl))
/*    */     {
/* 51 */       return (ShareResourceFileImpl)res;
/*    */     }
/*    */     
/* 54 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected ShareResourceFileImpl(ShareManagerImpl _manager, ShareResourceDirContentsImpl _parent, File _file, boolean _personal, Map<String, String> _properties)
/*    */     throws ShareException
/*    */   {
/* 67 */     super(_manager, _parent, 1, _file, _personal, _properties);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected ShareResourceFileImpl(ShareManagerImpl _manager, File _file, Map _map)
/*    */     throws ShareException
/*    */   {
/* 78 */     super(_manager, 1, _file, _map);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected byte[] getFingerPrint()
/*    */     throws ShareException
/*    */   {
/* 86 */     return getFingerPrint(getFile());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareResourceFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */