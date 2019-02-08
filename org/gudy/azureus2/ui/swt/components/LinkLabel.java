/*    */ package org.gudy.azureus2.ui.swt.components;
/*    */ 
/*    */ import org.eclipse.swt.events.MouseAdapter;
/*    */ import org.eclipse.swt.events.MouseEvent;
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
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
/*    */ public class LinkLabel
/*    */ {
/*    */   private final Label linkLabel;
/*    */   
/*    */   public LinkLabel(Composite composite, String resource, String link)
/*    */   {
/* 43 */     this(composite, new GridData(), resource, link);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public LinkLabel(Composite composite, GridData gridData, String resource, String link)
/*    */   {
/* 53 */     this.linkLabel = new Label(composite, 0);
/* 54 */     Messages.setLanguageText(this.linkLabel, resource);
/* 55 */     this.linkLabel.setLayoutData(gridData);
/* 56 */     makeLinkedLabel(this.linkLabel, link);
/*    */   }
/*    */   
/*    */ 
/*    */   public Label getlabel()
/*    */   {
/* 62 */     return this.linkLabel;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void makeLinkedLabel(Label label, String hyperlink)
/*    */   {
/* 72 */     label.setData(hyperlink);
/* 73 */     String tooltip = label.getToolTipText();
/*    */     
/*    */ 
/*    */ 
/* 77 */     if ((tooltip == null) && (!hyperlink.equals(label.getText()))) {
/* 78 */       label.setToolTipText(hyperlink.replaceAll("&", "&&"));
/*    */     }
/* 80 */     label.setCursor(label.getDisplay().getSystemCursor(21));
/* 81 */     label.setForeground(Colors.blue);
/* 82 */     label.addMouseListener(new MouseAdapter() {
/*    */       public void mouseDoubleClick(MouseEvent arg0) {
/* 84 */         Utils.launch((String)((Label)arg0.widget).getData());
/*    */       }
/*    */       
/* 87 */       public void mouseUp(MouseEvent arg0) { Utils.launch((String)((Label)arg0.widget).getData());
/*    */       }
/* 89 */     });
/* 90 */     ClipboardCopy.addCopyToClipMenu(label);
/*    */   }
/*    */   
/*    */   public static void updateLinkedLabel(Label label, String hyperlink) {
/* 94 */     label.setData(hyperlink);
/* 95 */     label.setToolTipText(hyperlink);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/LinkLabel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */