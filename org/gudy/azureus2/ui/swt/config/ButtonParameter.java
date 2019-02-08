/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.eclipse.swt.widgets.Button;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ButtonParameter
/*    */   extends Parameter
/*    */ {
/*    */   Button button;
/*    */   
/*    */   public ButtonParameter(Composite composite, String name_resource)
/*    */   {
/* 45 */     super(name_resource);
/* 46 */     this.button = new Button(composite, 8);
/*    */     
/* 48 */     Messages.setLanguageText(this.button, name_resource);
/*    */     
/* 50 */     this.button.addListener(13, new Listener()
/*    */     {
/*    */       public void handleEvent(Event event) {
/* 53 */         if (ButtonParameter.this.change_listeners == null) return;
/* 54 */         for (int i = 0; i < ButtonParameter.this.change_listeners.size(); i++)
/*    */         {
/* 56 */           ((ParameterChangeListener)ButtonParameter.this.change_listeners.get(i)).parameterChanged(ButtonParameter.this, false);
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public void setLayoutData(Object layoutData) {
/* 63 */     Utils.adjustPXForDPI(layoutData);
/* 64 */     this.button.setLayoutData(layoutData);
/*    */   }
/*    */   
/*    */ 
/*    */   public Button getButton()
/*    */   {
/* 70 */     return this.button;
/*    */   }
/*    */   
/*    */   public Control getControl()
/*    */   {
/* 75 */     return this.button;
/*    */   }
/*    */   
/*    */   public void setValue(Object value) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/ButtonParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */