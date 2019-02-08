/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectAdapter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
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
/*     */ public abstract class SkinView
/*     */   extends SWTSkinObjectAdapter
/*     */ {
/*     */   private boolean shownOnce;
/*     */   private boolean visible;
/*     */   protected SWTSkinObject soMain;
/*     */   protected SWTSkin skin;
/*  51 */   private boolean disposed = false;
/*     */   
/*     */ 
/*     */ 
/*     */   public SkinView()
/*     */   {
/*  57 */     this.shownOnce = false;
/*     */     
/*  59 */     if ((this instanceof UIUpdatable)) {
/*  60 */       UIUpdatable updateable = (UIUpdatable)this;
/*     */       try {
/*  62 */         UIFunctionsManager.getUIFunctions().getUIUpdater().addUpdater(updateable);
/*     */       }
/*     */       catch (Throwable e) {
/*  65 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isVisible()
/*     */   {
/*  74 */     return this.visible;
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/*  79 */     setMainSkinObject(skinObject);
/*     */     
/*  81 */     this.visible = true;
/*     */     
/*  83 */     if (this.shownOnce) {
/*  84 */       return null;
/*     */     }
/*     */     
/*  87 */     this.shownOnce = true;
/*     */     try {
/*  89 */       return skinObjectInitialShow(skinObject, params);
/*     */     } catch (Exception e) {
/*  91 */       Debug.out(e);
/*     */     }
/*  93 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/*  98 */     this.visible = false;
/*  99 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 104 */     this.disposed = true;
/*     */     
/* 106 */     SkinViewManager.remove(this);
/* 107 */     if ((this instanceof UIUpdatable)) {
/* 108 */       UIUpdatable updateable = (UIUpdatable)this;
/*     */       try {
/* 110 */         UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(updateable);
/*     */       }
/*     */       catch (Exception e) {
/* 113 */         Debug.out(e);
/*     */       }
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isDisposed() {
/* 120 */     return this.disposed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract Object skinObjectInitialShow(SWTSkinObject paramSWTSkinObject, Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */   public SWTSkinObject getMainSkinObject()
/*     */   {
/* 131 */     return this.soMain;
/*     */   }
/*     */   
/*     */   public Object skinObjectCreated(SWTSkinObject skinObject, Object params) {
/* 135 */     SkinViewManager.add(this);
/*     */     
/* 137 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/* 138 */     if (mdi != null) {
/* 139 */       MdiEntry entry = mdi.getEntryFromSkinObject(skinObject);
/* 140 */       if ((entry != null) && ((this instanceof UIPluginViewToolBarListener))) {
/* 141 */         entry.addToolbarEnabler((UIPluginViewToolBarListener)this);
/*     */       }
/*     */     }
/* 144 */     return super.skinObjectCreated(skinObject, params);
/*     */   }
/*     */   
/*     */   public final void setMainSkinObject(SWTSkinObject main) {
/* 148 */     if (this.soMain != null) {
/* 149 */       return;
/*     */     }
/* 151 */     this.soMain = main;
/* 152 */     if (this.soMain != null) {
/* 153 */       this.skin = this.soMain.getSkin();
/* 154 */       this.soMain.setSkinView(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public final SWTSkin getSkin() {
/* 159 */     return this.skin;
/*     */   }
/*     */   
/*     */   public final SWTSkinObject getSkinObject(String viewID) {
/* 163 */     return this.skin.getSkinObject(viewID, this.soMain);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SkinView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */