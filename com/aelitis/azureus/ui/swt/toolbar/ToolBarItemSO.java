/*     */ package com.aelitis.azureus.ui.swt.toolbar;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarItemImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ToolBarItemSO
/*     */ {
/*     */   private static final boolean DEBUG_TUX = false;
/*     */   private SWTSkinButtonUtility skinButton;
/*     */   private SWTSkinObjectText skinTitle;
/*     */   private boolean isDown;
/*     */   private UIToolBarItemImpl base;
/*     */   private SWTSkinObject so;
/*     */   
/*     */   public ToolBarItemSO(UIToolBarItemImpl base, SWTSkinObject so)
/*     */   {
/*  53 */     this.base = base;
/*  54 */     this.so = so;
/*     */   }
/*     */   
/*     */   public SWTSkinObject getSO() {
/*  58 */     return this.so;
/*     */   }
/*     */   
/*     */   public void setSkinButton(SWTSkinButtonUtility btn) {
/*  62 */     this.skinButton = btn;
/*  63 */     updateUI();
/*     */   }
/*     */   
/*     */   public SWTSkinButtonUtility getSkinButton() {
/*  67 */     return this.skinButton;
/*     */   }
/*     */   
/*     */   public void setSkinTitle(SWTSkinObjectText s) {
/*  71 */     this.skinTitle = s;
/*  72 */     this.skinTitle.setTextID(this.base.getTextID());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setEnabled(boolean enabled)
/*     */   {
/*  83 */     if ((this.base.isAlwaysAvailable()) && (!enabled)) {
/*  84 */       return;
/*     */     }
/*  86 */     if (this.skinButton != null) {
/*  87 */       this.skinButton.setDisabled(!enabled);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose()
/*     */   {
/*  93 */     this.skinButton = null;
/*  94 */     this.skinTitle = null;
/*     */   }
/*     */   
/*     */   public ToolBarItem getBase() {
/*  98 */     return this.base;
/*     */   }
/*     */   
/*     */   public void updateUI() {
/* 102 */     if (this.skinButton != null) {
/* 103 */       this.skinButton.setImage(this.base.getImageID());
/* 104 */       String tt = this.base.getTooltipID();
/* 105 */       if (tt == null) {
/* 106 */         String temp = this.base.getTextID();
/*     */         
/* 108 */         if (temp != null)
/*     */         {
/* 110 */           String test = temp + ".tooltip";
/*     */           
/* 112 */           if (MessageText.keyExists(test))
/*     */           {
/* 114 */             temp = test;
/*     */           }
/*     */         }
/*     */         
/* 118 */         tt = temp;
/*     */       }
/* 120 */       this.skinButton.setTooltipID(tt);
/*     */     }
/* 122 */     if (this.skinTitle != null) {
/* 123 */       this.skinTitle.setTextID(this.base.getTextID());
/*     */     }
/* 125 */     if (this.base.isAlwaysAvailable()) {
/* 126 */       setEnabled(true);
/*     */     } else {
/* 128 */       long state = this.base.getState();
/* 129 */       setEnabled((state & 1L) > 0L);
/* 130 */       this.isDown = ((state & 0x2) > 0L);
/* 131 */       if (this.skinButton != null) {
/* 132 */         this.skinButton.getSkinObject().switchSuffix(this.isDown ? "-selected" : "", 4, false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSO(SWTSkinObject so)
/*     */   {
/* 139 */     this.so = so;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/toolbar/ToolBarItemSO.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */