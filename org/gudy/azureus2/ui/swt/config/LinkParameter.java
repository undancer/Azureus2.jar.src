/*    */ package org.gudy.azureus2.ui.swt.config;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.eclipse.swt.events.MouseAdapter;
/*    */ import org.eclipse.swt.events.MouseEvent;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Control;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*    */ public class LinkParameter
/*    */   extends Parameter
/*    */ {
/*    */   Label link_label;
/*    */   
/*    */   public LinkParameter(Composite composite, String name_resource)
/*    */   {
/* 48 */     super(name_resource);
/* 49 */     this.link_label = new Label(composite, 0);
/* 50 */     Messages.setLanguageText(this.link_label, name_resource);
/* 51 */     this.link_label.setCursor(this.link_label.getDisplay().getSystemCursor(21));
/* 52 */     this.link_label.setForeground(Colors.blue);
/* 53 */     this.link_label.addMouseListener(new MouseAdapter() {
/*    */       public void mouseDoubleClick(MouseEvent arg0) {
/* 55 */         LinkParameter.this.fire();
/*    */       }
/*    */       
/* 58 */       public void mouseUp(MouseEvent arg0) { LinkParameter.this.fire(); }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void fire()
/*    */   {
/* 67 */     for (int i = 0; i < this.change_listeners.size(); i++)
/*    */     {
/* 69 */       ((ParameterChangeListener)this.change_listeners.get(i)).parameterChanged(this, false);
/*    */     }
/*    */   }
/*    */   
/*    */   public void setLayoutData(Object layoutData) {
/* 74 */     Utils.adjustPXForDPI(layoutData);
/* 75 */     this.link_label.setLayoutData(layoutData);
/*    */   }
/*    */   
/*    */   public Control getControl()
/*    */   {
/* 80 */     return this.link_label;
/*    */   }
/*    */   
/*    */   public void setValue(Object value) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/LinkParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */