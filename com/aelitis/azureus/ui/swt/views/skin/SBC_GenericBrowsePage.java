/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.mdi.MdiListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class SBC_GenericBrowsePage
/*     */   extends SkinView
/*     */ {
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   private MdiEntryVitalityImage vitalityImage;
/*     */   private MdiEntry entry;
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  46 */     Object creationParams = skinObject.getData("CreationParams");
/*     */     
/*  48 */     this.browserSkinObject = SWTSkinUtils.findBrowserSO(this.soMain);
/*     */     
/*  50 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  51 */     if (mdi != null) {
/*  52 */       this.entry = mdi.getEntryBySkinView(this);
/*  53 */       if (this.entry != null) {
/*  54 */         this.vitalityImage = this.entry.addVitalityImage("image.sidebar.vitality.dots");
/*  55 */         this.vitalityImage.setVisible(false);
/*     */         
/*  57 */         mdi.addListener(new MdiListener() {
/*  58 */           long lastSelect = 0L;
/*     */           
/*     */           public void mdiEntrySelected(MdiEntry newEntry, MdiEntry oldEntry)
/*     */           {
/*  62 */             if (SBC_GenericBrowsePage.this.entry == newEntry) {
/*  63 */               if (SBC_GenericBrowsePage.this.entry == oldEntry) {
/*  64 */                 if ((this.lastSelect < SystemTime.getOffsetTime(-1000L)) && 
/*  65 */                   (SBC_GenericBrowsePage.this.browserSkinObject != null)) {
/*  66 */                   SBC_GenericBrowsePage.this.browserSkinObject.restart();
/*     */                 }
/*     */               }
/*     */               else {
/*  70 */                 this.lastSelect = SystemTime.getCurrentTime();
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*  78 */     this.browserSkinObject.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params)
/*     */       {
/*  82 */         if (eventType == 0) {
/*  83 */           SBC_GenericBrowsePage.this.browserSkinObject.removeListener(this);
/*     */           
/*  85 */           SBC_GenericBrowsePage.this.browserSkinObject.addListener(new BrowserContext.loadingListener() {
/*     */             public void browserLoadingChanged(boolean loading, String url) {
/*  87 */               if (SBC_GenericBrowsePage.this.vitalityImage != null) {
/*  88 */                 SBC_GenericBrowsePage.this.vitalityImage.setVisible(loading);
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*  93 */         return null;
/*     */       }
/*     */       
/*  96 */     });
/*  97 */     openURL();
/*     */     
/*  99 */     return null;
/*     */   }
/*     */   
/*     */   private void openURL()
/*     */   {
/* 104 */     if (this.entry != null) {
/* 105 */       Object o = this.entry.getDatasource();
/* 106 */       if ((o instanceof String)) {
/* 107 */         this.browserSkinObject.setURL((String)o);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_GenericBrowsePage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */