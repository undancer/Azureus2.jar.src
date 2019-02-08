/*    */ package com.aelitis.azureus.core.download;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
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
/*    */ public class EnhancedDownloadManagerFile
/*    */ {
/*    */   private DiskManagerFileInfo file;
/*    */   private long offset;
/*    */   
/*    */   protected EnhancedDownloadManagerFile(DiskManagerFileInfo _file, long _offset)
/*    */   {
/* 36 */     this.file = _file;
/* 37 */     this.offset = _offset;
/*    */   }
/*    */   
/*    */ 
/*    */   public DiskManagerFileInfo getFile()
/*    */   {
/* 43 */     return this.file;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getIndex()
/*    */   {
/* 49 */     return this.file.getIndex();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getLength()
/*    */   {
/* 55 */     return this.file.getLength();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getByteOffestInTorrent()
/*    */   {
/* 61 */     return this.offset;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isComplete()
/*    */   {
/* 67 */     return this.file.getDownloaded() == this.file.getLength();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/EnhancedDownloadManagerFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */