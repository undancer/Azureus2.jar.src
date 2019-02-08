/*    */ package org.gudy.azureus2.ui.swt.ipchecker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
/*    */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*    */ public class IpCheckerWizard
/*    */   extends Wizard
/*    */ {
/*    */   IpSetterCallBack callBack;
/*    */   ExternalIPCheckerService selectedService;
/*    */   
/*    */   public IpCheckerWizard()
/*    */   {
/* 38 */     super("ipCheckerWizard.title");
/* 39 */     IWizardPanel panel = new ChooseServicePanel(this, null);
/* 40 */     setFirstPanel(panel);
/*    */   }
/*    */   
/*    */   public void setIpSetterCallBack(IpSetterCallBack callBack) {
/* 44 */     this.callBack = callBack;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ipchecker/IpCheckerWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */