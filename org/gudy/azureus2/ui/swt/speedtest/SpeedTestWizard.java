/*    */ package org.gudy.azureus2.ui.swt.speedtest;
/*    */ 
/*    */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
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
/*    */ public class SpeedTestWizard
/*    */   extends Wizard
/*    */ {
/*    */   protected static final String CFG_PREFIX = "speedtest.wizard.";
/*    */   
/*    */   public SpeedTestWizard()
/*    */   {
/* 38 */     super("speedtest.wizard.title");
/* 39 */     SpeedTestSelector panel = new SpeedTestSelector(this, null);
/* 40 */     setFirstPanel(panel);
/*    */   }
/*    */   
/*    */   public void onClose()
/*    */   {
/* 45 */     super.onClose();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/speedtest/SpeedTestWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */