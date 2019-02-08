/*    */ package com.aelitis.azureus.core.diskmanager.access;
/*    */ 
/*    */ import com.aelitis.azureus.core.diskmanager.access.impl.DiskAccessControllerImpl;
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
/*    */ public class DiskAccessControllerFactory
/*    */ {
/*    */   public static DiskAccessController create(String name, int max_read_threads, int max_read_mb, int max_write_threads, int max_write_mb)
/*    */   {
/* 35 */     return new DiskAccessControllerImpl(name, max_read_threads, max_read_mb, max_write_threads, max_write_mb);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/DiskAccessControllerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */