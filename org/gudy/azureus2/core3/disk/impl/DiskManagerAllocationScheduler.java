/*    */ package org.gudy.azureus2.core3.disk.impl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*    */ public class DiskManagerAllocationScheduler
/*    */ {
/* 30 */   private final List instances = new ArrayList();
/* 31 */   private final AEMonitor instance_mon = new AEMonitor("DiskManagerAllocationScheduler");
/*    */   
/*    */ 
/*    */ 
/*    */   public void register(DiskManagerHelper helper)
/*    */   {
/*    */     try
/*    */     {
/* 39 */       this.instance_mon.enter();
/*    */       
/* 41 */       this.instances.add(helper);
/*    */     }
/*    */     finally
/*    */     {
/* 45 */       this.instance_mon.exit();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   protected boolean getPermission(DiskManagerHelper instance)
/*    */   {
/*    */     try
/*    */     {
/* 54 */       this.instance_mon.enter();
/*    */       
/* 56 */       if (this.instances.get(0) == instance)
/*    */       {
/* 58 */         return true;
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/* 63 */       this.instance_mon.exit();
/*    */     }
/*    */     try
/*    */     {
/* 67 */       Thread.sleep(250L);
/*    */     }
/*    */     catch (Throwable e) {}
/*    */     
/*    */ 
/*    */ 
/* 73 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   protected void unregister(DiskManagerHelper instance)
/*    */   {
/*    */     try
/*    */     {
/* 81 */       this.instance_mon.enter();
/*    */       
/* 83 */       this.instances.remove(instance);
/*    */     }
/*    */     finally {
/* 86 */       this.instance_mon.exit();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerAllocationScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */