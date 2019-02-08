/*    */ package com.aelitis.azureus.ui.swt.skin;
/*    */ 
/*    */ import java.util.regex.Pattern;
/*    */ import org.eclipse.swt.custom.CTabFolder;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.gudy.azureus2.core3.util.Constants;
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
/*    */ public class SWTSkinObjectTabFolder
/*    */   extends SWTSkinObjectContainer
/*    */ {
/*    */   private CTabFolder tabFolder;
/*    */   
/*    */   public SWTSkinObjectTabFolder(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parent)
/*    */   {
/* 35 */     super(skin, properties, null, sID, sConfigID, "tabfolder", parent);
/* 36 */     createTabFolder(null);
/*    */   }
/*    */   
/*    */   public SWTSkinObjectTabFolder(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, Composite createOn)
/*    */   {
/* 41 */     super(skin, properties, null, sID, sConfigID, "tabfolder", null);
/* 42 */     createTabFolder(createOn);
/*    */   }
/*    */   
/*    */   private void createTabFolder(Composite createOn) {
/* 46 */     if (createOn == null) {
/* 47 */       if (this.parent == null) {
/* 48 */         createOn = this.skin.getShell();
/*    */       } else {
/* 50 */         createOn = (Composite)this.parent.getControl();
/*    */       }
/*    */     }
/*    */     
/* 54 */     int style = 0;
/* 55 */     if (this.properties.getIntValue(this.sConfigID + ".border", 0) == 1) {
/* 56 */       style = 2048;
/*    */     }
/*    */     
/* 59 */     String sStyle = this.properties.getStringValue("style");
/* 60 */     if ((sStyle != null) && (sStyle.length() > 0)) {
/* 61 */       String[] styles = Constants.PAT_SPLIT_COMMA.split(sStyle);
/* 62 */       for (String aStyle : styles) {
/* 63 */         if (aStyle.equalsIgnoreCase("close")) {
/* 64 */           style |= 0x40;
/*    */         }
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 70 */     this.tabFolder = new CTabFolder(createOn, style);
/*    */     
/* 72 */     triggerListeners(4);
/* 73 */     setControl(this.tabFolder);
/*    */   }
/*    */   
/*    */   protected boolean setIsVisible(boolean visible, boolean walkup) {
/* 77 */     boolean isVisible = superSetIsVisible(visible, walkup);
/*    */     
/* 79 */     return isVisible;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void childAdded(SWTSkinObject soChild) {}
/*    */   
/*    */ 
/*    */ 
/*    */   public CTabFolder getTabFolder()
/*    */   {
/* 90 */     return this.tabFolder;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectTabFolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */