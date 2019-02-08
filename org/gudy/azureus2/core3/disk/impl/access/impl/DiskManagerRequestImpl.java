/*    */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.config.ParameterListener;
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerRequest;
/*    */ import org.gudy.azureus2.core3.logging.LogEvent;
/*    */ import org.gudy.azureus2.core3.logging.LogIDs;
/*    */ import org.gudy.azureus2.core3.logging.Logger;
/*    */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*    */ public abstract class DiskManagerRequestImpl
/*    */   implements DiskManagerRequest
/*    */ {
/* 34 */   private static final LogIDs LOGID = LogIDs.DISK;
/*    */   private static boolean DEBUG;
/*    */   private static int next_id;
/*    */   private long start_time;
/*    */   private String name;
/*    */   
/* 40 */   static { COConfigurationManager.addAndFireParameterListener("diskmanager.request.debug.enable", new ParameterListener()
/*    */     {
/*    */ 
/*    */ 
/*    */       public void parameterChanged(String name)
/*    */       {
/*    */ 
/*    */ 
/* 48 */         DiskManagerRequestImpl.access$002(COConfigurationManager.getBooleanParameter(name, false));
/*    */       }
/*    */     }); }
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
/*    */   public void requestStarts()
/*    */   {
/* 63 */     if (DEBUG) {
/*    */       try
/*    */       {
/*    */         int id;
/*    */         
/* 68 */         synchronized (DiskManagerRequestImpl.class)
/*    */         {
/* 70 */           id = next_id++;
/*    */         }
/*    */         
/* 73 */         this.name = (getName() + " [" + id + "]");
/*    */         
/* 75 */         this.start_time = SystemTime.getCurrentTime();
/*    */         
/* 77 */         Logger.log(new LogEvent(LOGID, "DMRequest start: " + this.name));
/*    */       }
/*    */       catch (Throwable e) {}
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void requestEnds(boolean ok)
/*    */   {
/* 88 */     if (DEBUG) {
/*    */       try
/*    */       {
/* 91 */         Logger.log(new LogEvent(LOGID, "DMRequest end: " + this.name + ",ok=" + ok + ", time=" + (SystemTime.getCurrentTime() - this.start_time)));
/*    */       }
/*    */       catch (Throwable e) {}
/*    */     }
/*    */   }
/*    */   
/*    */   protected abstract String getName();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DiskManagerRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */