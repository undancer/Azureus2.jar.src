/*    */ package org.gudy.azureus2.core3.disk.impl.access;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.impl.DiskManagerHelper;
/*    */ import org.gudy.azureus2.core3.disk.impl.access.impl.DMCheckerImpl;
/*    */ import org.gudy.azureus2.core3.disk.impl.access.impl.DMReaderImpl;
/*    */ import org.gudy.azureus2.core3.disk.impl.access.impl.DMWriterImpl;
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
/*    */ public class DMAccessFactory
/*    */ {
/*    */   public static DMReader createReader(DiskManagerHelper adapter)
/*    */   {
/* 37 */     return new DMReaderImpl(adapter);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static DMWriter createWriter(DiskManagerHelper disk_manager)
/*    */   {
/* 44 */     return new DMWriterImpl(disk_manager);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static DMChecker createChecker(DiskManagerHelper disk_manager)
/*    */   {
/* 51 */     return new DMCheckerImpl(disk_manager);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/DMAccessFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */