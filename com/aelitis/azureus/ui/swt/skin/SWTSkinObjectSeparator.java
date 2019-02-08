/*    */ package com.aelitis.azureus.ui.swt.skin;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;
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
/*    */ public class SWTSkinObjectSeparator
/*    */   extends SWTSkinObjectBasic
/*    */ {
/*    */   private Label separator;
/*    */   
/*    */   public SWTSkinObjectSeparator(SWTSkin skin, SWTSkinProperties properties, String sid, String configID, SWTSkinObject parent)
/*    */   {
/* 33 */     super(skin, properties, sid, configID, "separator", parent);
/*    */     Composite createOn;
/*    */     Composite createOn;
/* 36 */     if (parent == null) {
/* 37 */       createOn = skin.getShell();
/*    */     } else {
/* 39 */       createOn = (Composite)parent.getControl();
/*    */     }
/*    */     
/* 42 */     this.separator = new Label(createOn, 258);
/*    */     
/* 44 */     setControl(this.separator);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectSeparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */