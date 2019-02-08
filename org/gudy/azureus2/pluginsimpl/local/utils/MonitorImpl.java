/*    */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.AEMonitor;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.utils.Monitor;
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
/*    */ public class MonitorImpl
/*    */   implements Monitor
/*    */ {
/*    */   private static long next_mon_id;
/*    */   private AEMonitor mon;
/*    */   
/*    */   protected MonitorImpl(PluginInterface pi)
/*    */   {
/* 37 */     synchronized (MonitorImpl.class)
/*    */     {
/* 39 */       this.mon = new AEMonitor("Plugin " + pi.getPluginID() + ":" + next_mon_id++);
/*    */     }
/*    */   }
/*    */   
/*    */   public void enter()
/*    */   {
/* 45 */     this.mon.enter();
/*    */   }
/*    */   
/*    */   public void exit() {
/* 49 */     this.mon.exit();
/*    */   }
/*    */   
/*    */   public boolean isOwned()
/*    */   {
/* 54 */     return this.mon.isHeld();
/*    */   }
/*    */   
/*    */   public boolean hasWaiters()
/*    */   {
/* 59 */     return this.mon.hasWaiters();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/MonitorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */