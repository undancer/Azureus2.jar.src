/*    */ package org.gudy.azureus2.ui.common.util;
/*    */ 
/*    */ import org.gudy.azureus2.core3.logging.ILogEventListener;
/*    */ import org.gudy.azureus2.core3.logging.LogEvent;
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
/*    */ public class LGLogger2Log4j
/*    */   implements ILogEventListener
/*    */ {
/* 34 */   public static org.apache.log4j.Logger core = org.apache.log4j.Logger.getLogger("azureus2.core");
/*    */   
/* 36 */   private static LGLogger2Log4j inst = null;
/*    */   
/*    */   public static LGLogger2Log4j getInstance() {
/* 39 */     if (inst == null)
/* 40 */       inst = new LGLogger2Log4j();
/* 41 */     return inst;
/*    */   }
/*    */   
/*    */   public static void set() {
/* 45 */     org.gudy.azureus2.core3.logging.Logger.addListener(getInstance());
/*    */   }
/*    */   
/*    */   public void log(LogEvent event) {
/* 49 */     if (event.entryType == 3) {
/* 50 */       core.error(event.text);
/* 51 */     } else if (event.entryType == 1) {
/* 52 */       core.log(SLevel.CORE_WARNING, event.text);
/*    */     } else {
/* 54 */       core.log(SLevel.CORE_INFO, event.text);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/util/LGLogger2Log4j.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */