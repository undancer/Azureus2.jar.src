/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class SBC_BurnFTUX
/*     */   extends SkinView
/*     */ {
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   private String url;
/*  45 */   private static String sRef = "user";
/*     */   
/*     */   private String entryID;
/*     */   
/*     */   private MdiEntry entry;
/*     */   
/*  51 */   private static boolean DEBUG = Constants.IS_CVS_VERSION;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(final SWTSkinObject skinObject, Object params)
/*     */   {
/*  56 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  57 */     if (mdi != null) {
/*  58 */       this.entry = mdi.getEntryFromSkinObject(skinObject);
/*  59 */       if (this.entry != null) {
/*  60 */         this.entryID = this.entry.getId();
/*     */       }
/*     */     }
/*     */     
/*  64 */     this.browserSkinObject = ((SWTSkinObjectBrowser)this.skin.getSkinObject("browser", this.soMain));
/*     */     
/*     */ 
/*  67 */     this.browserSkinObject.addListener(new BrowserContext.loadingListener()
/*     */     {
/*     */       public void browserLoadingChanged(boolean loading, String url) {
/*  70 */         if (!loading) {
/*  71 */           skinObject.getControl().getParent().layout(true, true);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*  76 */     if (DEBUG) {
/*  77 */       System.out.println("BurnFTUX sourceRef is now " + sRef);
/*     */     }
/*     */     
/*  80 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params) {
/*  84 */     super.skinObjectShown(skinObject, params);
/*  85 */     buildURL(true);
/*  86 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params) {
/*  90 */     if (this.browserSkinObject != null) {
/*  91 */       this.browserSkinObject.setURL("about:blank");
/*     */     }
/*  93 */     sRef = "user";
/*  94 */     if (DEBUG) {
/*  95 */       System.out.println("BurnFTUX sourceRef is now " + sRef);
/*     */     }
/*  97 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateLicenceInfo()
/*     */   {
/* 104 */     buildURL(false);
/*     */   }
/*     */   
/*     */   private void buildURL(boolean forceSet) {
/* 108 */     String suffix = "?view=" + this.entryID + "&sourceRef=" + UrlUtils.encode(new StringBuilder().append(sRef).append("-/plus/ftux/dvd").toString());
/*     */     
/* 110 */     String newUrl = ConstantsVuze.getDefaultContentNetwork().getSiteRelativeURL("burn_ftux.start" + suffix, false);
/*     */     
/* 112 */     newUrl = FeatureManagerUI.appendFeatureManagerURLParams(newUrl);
/* 113 */     if ((!forceSet) && (newUrl.equals(this.url))) {
/* 114 */       return;
/*     */     }
/*     */     
/* 117 */     this.url = newUrl;
/*     */     
/* 119 */     if (DEBUG) {
/* 120 */       System.out.println("URL is now " + this.url + " via " + Debug.getCompressedStackTrace());
/*     */     }
/*     */     
/* 123 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 124 */     MdiEntry currentEntry = mdi.getCurrentEntry();
/*     */     
/* 126 */     if ((this.browserSkinObject != null) && ((forceSet) || (this.entry == currentEntry))) {
/* 127 */       this.browserSkinObject.setURL(this.url);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setSourceRef(String _sRef) {
/* 132 */     sRef = _sRef;
/* 133 */     if (DEBUG) {
/* 134 */       System.out.println("BurnFTUX sourceRef is now " + sRef);
/*     */     }
/*     */     
/* 137 */     SkinView[] views = SkinViewManager.getMultiByClass(SBC_BurnFTUX.class);
/* 138 */     if (views != null) {
/* 139 */       for (SkinView bview : views) {
/* 140 */         ((SBC_BurnFTUX)bview).buildURL(false);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_BurnFTUX.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */