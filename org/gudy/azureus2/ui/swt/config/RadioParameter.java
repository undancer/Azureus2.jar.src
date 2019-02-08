/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.eclipse.swt.widgets.Button;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
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
/*    */ public class RadioParameter
/*    */   extends Parameter
/*    */ {
/*    */   Button radioButton;
/* 39 */   List performers = new ArrayList();
/*    */   
/*    */   public RadioParameter(Composite composite, String sConfigName, int iButtonValue) {
/* 42 */     this(composite, sConfigName, iButtonValue, null);
/*    */   }
/*    */   
/*    */   public RadioParameter(Composite composite, final String sConfigName, final int iButtonValue, IAdditionalActionPerformer actionPerformer)
/*    */   {
/* 47 */     super(sConfigName);
/* 48 */     if (actionPerformer != null) {
/* 49 */       this.performers.add(actionPerformer);
/*    */     }
/* 51 */     int iDefaultValue = COConfigurationManager.getIntParameter(sConfigName);
/*    */     
/* 53 */     this.radioButton = new Button(composite, 16);
/* 54 */     this.radioButton.setSelection(iDefaultValue == iButtonValue);
/* 55 */     this.radioButton.addListener(13, new Listener() {
/*    */       public void handleEvent(Event event) {
/* 57 */         boolean selected = RadioParameter.this.radioButton.getSelection();
/* 58 */         if (selected) {
/* 59 */           COConfigurationManager.setParameter(sConfigName, iButtonValue);
/*    */         }
/* 61 */         if (RadioParameter.this.performers.size() > 0) {
/* 62 */           for (int i = 0; i < RadioParameter.this.performers.size(); i++) {
/* 63 */             IAdditionalActionPerformer performer = (IAdditionalActionPerformer)RadioParameter.this.performers.get(i);
/*    */             
/* 65 */             performer.setSelected(selected);
/* 66 */             performer.performAction();
/*    */           }
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public void setLayoutData(Object layoutData) {
/* 74 */     Utils.adjustPXForDPI(layoutData);
/* 75 */     this.radioButton.setLayoutData(layoutData);
/*    */   }
/*    */   
/*    */   public void setAdditionalActionPerformer(IAdditionalActionPerformer actionPerformer) {
/* 79 */     this.performers.add(actionPerformer);
/* 80 */     boolean selected = this.radioButton.getSelection();
/* 81 */     actionPerformer.setSelected(selected);
/* 82 */     actionPerformer.performAction();
/*    */   }
/*    */   
/*    */ 
/*    */   public Control getControl()
/*    */   {
/* 88 */     return this.radioButton;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSelected()
/*    */   {
/* 94 */     return this.radioButton.getSelection();
/*    */   }
/*    */   
/*    */   public void setValue(Object value) {
/* 98 */     System.err.println("NOT IMPLEMENTED");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/RadioParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */