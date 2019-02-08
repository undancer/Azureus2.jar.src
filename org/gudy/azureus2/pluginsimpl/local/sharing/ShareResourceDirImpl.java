/*    */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*    */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
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
/*    */ public class ShareResourceDirImpl
/*    */   extends ShareResourceFileOrDirImpl
/*    */   implements ShareResourceDir
/*    */ {
/*    */   protected static ShareResourceDirImpl getResource(ShareManagerImpl _manager, File _file)
/*    */     throws ShareException
/*    */   {
/* 47 */     ShareResourceImpl res = ShareResourceFileOrDirImpl.getResourceSupport(_manager, _file);
/*    */     
/* 49 */     if ((res instanceof ShareResourceDirImpl))
/*    */     {
/* 51 */       return (ShareResourceDirImpl)res;
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
/*    */   protected ShareResourceDirImpl(ShareManagerImpl _manager, ShareResourceDirContentsImpl _parent, File _file, boolean _personal, Map<String, String> _properties)
/*    */     throws ShareException
/*    */   {
/* 67 */     super(_manager, _parent, 2, _file, _personal, _properties);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected ShareResourceDirImpl(ShareManagerImpl _manager, File _file, Map _map)
/*    */     throws ShareException
/*    */   {
/* 78 */     super(_manager, 2, _file, _map);
/*    */   }
/*    */   
/*    */ 
/*    */   protected byte[] getFingerPrint()
/*    */     throws ShareException
/*    */   {
/* 85 */     return getFingerPrint(getFile());
/*    */   }
/*    */   
/*    */ 
/*    */   public File getDir()
/*    */   {
/* 91 */     return getFile();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareResourceDirImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */