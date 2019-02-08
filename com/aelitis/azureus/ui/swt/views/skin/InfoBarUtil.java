/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public abstract class InfoBarUtil
/*     */ {
/*     */   private final SWTSkinObject forSO;
/*     */   private final boolean top;
/*     */   private SWTSkin skin;
/*     */   private SWTSkinObject soInfoBar;
/*     */   private final String stateConfigID;
/*     */   private final String textPrefix;
/*     */   private final String skintemplateid;
/*  57 */   private static int uniqueNo = 0;
/*     */   
/*     */   public InfoBarUtil(SWTSkinObject forSO, boolean top, String stateConfigID, String textPrefix)
/*     */   {
/*  61 */     this(forSO, "library.top.info", top, stateConfigID, textPrefix);
/*     */   }
/*     */   
/*     */   public InfoBarUtil(final SWTSkinObject forSO, String skintemplateid, boolean top, final String stateConfigID, String textPrefix)
/*     */   {
/*  66 */     this.forSO = forSO;
/*  67 */     this.skintemplateid = skintemplateid;
/*  68 */     this.stateConfigID = stateConfigID;
/*  69 */     this.textPrefix = textPrefix;
/*  70 */     this.skin = forSO.getSkin();
/*  71 */     this.top = top;
/*     */     
/*     */ 
/*     */ 
/*  75 */     if (COConfigurationManager.hasParameter(stateConfigID, true))
/*     */     {
/*  77 */       RememberedDecisionsManager.setRemembered(stateConfigID, COConfigurationManager.getBooleanParameter(stateConfigID) ? 1 : 0);
/*     */       
/*  79 */       COConfigurationManager.removeParameter(stateConfigID);
/*     */     }
/*     */     
/*  82 */     forSO.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/*  85 */         if (eventType == 0) {
/*  86 */           forSO.removeListener(this);
/*  87 */           boolean show = RememberedDecisionsManager.getRememberedDecision(stateConfigID) != 0;
/*  88 */           if ((show) && (InfoBarUtil.this.allowShow()) && (InfoBarUtil.this.soInfoBar == null)) {
/*  89 */             InfoBarUtil.this.createInfoBar();
/*     */           }
/*     */         }
/*  92 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void createInfoBar() {
/*  98 */     Control control = this.forSO.getControl();
/*  99 */     if ((control == null) || (control.isDisposed())) {
/* 100 */       return;
/*     */     }
/* 102 */     Object ldForSO = control.getLayoutData();
/* 103 */     if (!(ldForSO instanceof FormData)) {
/* 104 */       return;
/*     */     }
/* 106 */     FormData fdForSO = (FormData)ldForSO;
/* 107 */     SWTSkinObject parent = this.forSO.getParent();
/* 108 */     this.soInfoBar = this.skin.createSkinObject(this.skintemplateid + uniqueNo++, this.skintemplateid, parent);
/*     */     
/* 110 */     FormData fdInfoBar = (FormData)this.soInfoBar.getControl().getLayoutData();
/* 111 */     if (fdInfoBar == null) {
/* 112 */       fdInfoBar = Utils.getFilledFormData();
/*     */     }
/* 114 */     if (this.top) {
/* 115 */       if (fdForSO.top.control == null) {
/* 116 */         fdInfoBar.top = new FormAttachment(fdForSO.top.numerator, fdForSO.top.denominator, fdForSO.top.offset);
/*     */       }
/*     */       else {
/* 119 */         fdInfoBar.top = new FormAttachment(fdForSO.top.control, fdForSO.top.offset, fdForSO.top.alignment);
/*     */       }
/*     */       
/* 122 */       fdInfoBar.bottom = null;
/* 123 */       this.soInfoBar.getControl().setLayoutData(fdInfoBar);
/* 124 */       fdForSO.top = new FormAttachment(this.soInfoBar.getControl(), 0, 1024);
/* 125 */       this.forSO.getControl().setLayoutData(fdForSO);
/*     */     } else {
/* 127 */       if (fdForSO.bottom.control == null) {
/* 128 */         fdInfoBar.bottom = new FormAttachment(fdForSO.bottom.numerator, fdForSO.bottom.denominator, fdForSO.bottom.offset);
/*     */       }
/*     */       else {
/* 131 */         fdInfoBar.bottom = new FormAttachment(fdForSO.bottom.control, fdForSO.bottom.offset, fdForSO.bottom.alignment);
/*     */       }
/*     */       
/* 134 */       fdInfoBar.top = null;
/* 135 */       this.soInfoBar.getControl().setLayoutData(fdInfoBar);
/* 136 */       fdForSO.bottom = new FormAttachment(this.soInfoBar.getControl(), 0, 128);
/* 137 */       this.forSO.getControl().setLayoutData(fdForSO);
/*     */     }
/*     */     
/* 140 */     ((SWTSkinObjectContainer)parent).getComposite().layout(true);
/*     */     
/* 142 */     SWTSkinObject soClose = this.skin.getSkinObject("close", parent);
/* 143 */     if (soClose != null) {
/* 144 */       SWTSkinButtonUtility btnClose = new SWTSkinButtonUtility(soClose);
/* 145 */       btnClose.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */       {
/*     */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask)
/*     */         {
/* 149 */           InfoBarUtil.this.soInfoBar.setVisible(false);
/* 150 */           RememberedDecisionsManager.setRemembered(InfoBarUtil.this.stateConfigID, 0);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 155 */     this.soInfoBar.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params) {
/* 158 */         if (eventType == 0) {
/* 159 */           RememberedDecisionsManager.setRemembered(InfoBarUtil.this.stateConfigID, 1);
/*     */         }
/* 161 */         return null;
/*     */       }
/*     */       
/* 164 */     });
/* 165 */     SWTSkinObject soText1 = this.skin.getSkinObject("infobar-title-1", parent);
/* 166 */     if ((soText1 instanceof SWTSkinObjectText)) {
/* 167 */       SWTSkinObjectText soText = (SWTSkinObjectText)soText1;
/* 168 */       String id = this.textPrefix + ".text1";
/* 169 */       if (MessageText.keyExists(id)) {
/* 170 */         soText.setTextID(id);
/*     */       }
/*     */     }
/* 173 */     SWTSkinObject soText2 = this.skin.getSkinObject("infobar-title-2", parent);
/* 174 */     if ((soText2 instanceof SWTSkinObjectText)) {
/* 175 */       SWTSkinObjectText soText = (SWTSkinObjectText)soText2;
/* 176 */       String id = this.textPrefix + ".text2";
/* 177 */       if (MessageText.keyExists(id)) {
/* 178 */         soText.setTextID(id);
/*     */       }
/*     */     }
/*     */     
/* 182 */     created(parent);
/*     */     
/* 184 */     this.soInfoBar.setVisible(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void created(SWTSkinObject parent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void hide(boolean permanently)
/*     */   {
/* 197 */     if ((this.soInfoBar != null) && (!this.soInfoBar.isDisposed())) {
/* 198 */       this.soInfoBar.setVisible(false);
/*     */     }
/* 200 */     if (permanently) {
/* 201 */       RememberedDecisionsManager.setRemembered(this.stateConfigID, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void show() {
/* 206 */     RememberedDecisionsManager.setRemembered(this.stateConfigID, 1);
/* 207 */     if (this.soInfoBar == null) {
/* 208 */       createInfoBar();
/*     */     } else {
/* 210 */       this.soInfoBar.setVisible(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public abstract boolean allowShow();
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/InfoBarUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */