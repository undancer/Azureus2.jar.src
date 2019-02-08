/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.util.AERunnable;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
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
/*    */ public class InfoParameter
/*    */   extends Parameter
/*    */ {
/*    */   private String name;
/*    */   private BufferedLabel label;
/*    */   
/*    */   public InfoParameter(Composite composite, String name)
/*    */   {
/* 40 */     this(composite, name, COConfigurationManager.getStringParameter(name));
/*    */   }
/*    */   
/*    */   public InfoParameter(Composite composite, String name, String defaultValue) {
/* 44 */     super(name);
/* 45 */     this.name = name;
/* 46 */     this.label = new BufferedLabel(composite, 0);
/* 47 */     String value = COConfigurationManager.getStringParameter(name, defaultValue);
/* 48 */     this.label.setText(value);
/*    */   }
/*    */   
/*    */   public void setValue(final String value)
/*    */   {
/* 53 */     Utils.execSWTThread(new AERunnable() {
/*    */       public void runSupport() {
/* 55 */         if ((InfoParameter.this.label == null) || (InfoParameter.this.label.isDisposed()) || (InfoParameter.this.label.getText().equals(value)))
/*    */         {
/* 57 */           return;
/*    */         }
/* 59 */         InfoParameter.this.label.setText(value);
/*    */       }
/*    */     });
/*    */     
/* 63 */     if (!COConfigurationManager.getStringParameter(this.name).equals(value)) {
/* 64 */       COConfigurationManager.setParameter(this.name, value);
/*    */     }
/*    */   }
/*    */   
/*    */   public void setLayoutData(Object layoutData) {
/* 69 */     Utils.adjustPXForDPI(layoutData);
/* 70 */     this.label.setLayoutData(layoutData);
/*    */   }
/*    */   
/* 73 */   public String getValue() { return this.label.getText(); }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Control getControl()
/*    */   {
/* 80 */     return this.label.getControl();
/*    */   }
/*    */   
/*    */   public void setValue(Object value) {
/* 84 */     if ((value instanceof String)) {
/* 85 */       setValue((String)value);
/*    */     }
/*    */   }
/*    */   
/*    */   public Object getValueObject() {
/* 90 */     return COConfigurationManager.getStringParameter(this.name);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/InfoParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */