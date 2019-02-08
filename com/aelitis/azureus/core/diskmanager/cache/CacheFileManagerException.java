/*    */ package com.aelitis.azureus.core.diskmanager.cache;
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
/*    */ public class CacheFileManagerException
/*    */   extends Exception
/*    */ {
/*    */   private final CacheFile file;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int fail_index;
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
/*    */   public CacheFileManagerException(CacheFile _file, String _str)
/*    */   {
/* 38 */     super(_str);
/*    */     
/* 40 */     this.file = _file;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CacheFileManagerException(CacheFile _file, String _str, Throwable _cause)
/*    */   {
/* 49 */     super(_str, _cause);
/*    */     
/* 51 */     this.file = _file;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CacheFileManagerException(CacheFile _file, String _str, Throwable _cause, int _fail_index)
/*    */   {
/* 61 */     super(_str, _cause);
/*    */     
/* 63 */     this.file = _file;
/* 64 */     this.fail_index = _fail_index;
/*    */   }
/*    */   
/*    */ 
/*    */   public CacheFile getFile()
/*    */   {
/* 70 */     return this.file;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getFailIndex()
/*    */   {
/* 76 */     return this.fail_index;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/CacheFileManagerException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */