/*    */ package org.gudy.azureus2.ui.swt.views.configsections;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.layout.GridLayout;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigSectionTracker
/*    */   implements UISWTConfigSection
/*    */ {
/*    */   public String configSectionGetParentSection()
/*    */   {
/* 42 */     return "root";
/*    */   }
/*    */   
/*    */   public String configSectionGetName() {
/* 46 */     return "tracker";
/*    */   }
/*    */   
/*    */ 
/*    */   public void configSectionSave() {}
/*    */   
/*    */   public void configSectionDelete() {}
/*    */   
/*    */   public int maxUserMode()
/*    */   {
/* 56 */     return 0;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Composite configSectionCreate(Composite parent)
/*    */   {
/* 66 */     Composite gMainTab = new Composite(parent, 0);
/*    */     
/* 68 */     GridData gridData = new GridData(272);
/* 69 */     gMainTab.setLayoutData(gridData);
/* 70 */     GridLayout layout = new GridLayout();
/* 71 */     layout.numColumns = 3;
/* 72 */     gMainTab.setLayout(layout);
/*    */     
/*    */ 
/*    */ 
/* 76 */     return gMainTab;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */