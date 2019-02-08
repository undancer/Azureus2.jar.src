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
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import com.aelitis.azureus.util.FeatureUtils.licenceDetails;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class SBC_PlusFTUX
/*     */   extends SkinView
/*     */ {
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   private String url;
/*  46 */   private static String sRef = "user";
/*     */   
/*  48 */   private static boolean DEBUG = Constants.IS_CVS_VERSION;
/*     */   
/*     */   private MdiEntry entry;
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(final SWTSkinObject skinObject, Object params)
/*     */   {
/*  55 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  56 */     if (mdi != null) {
/*  57 */       this.entry = mdi.getEntryFromSkinObject(skinObject);
/*     */     }
/*     */     
/*  60 */     this.browserSkinObject = ((SWTSkinObjectBrowser)this.skin.getSkinObject("plus-ftux", this.soMain));
/*     */     
/*     */ 
/*  63 */     this.browserSkinObject.addListener(new BrowserContext.loadingListener()
/*     */     {
/*     */       public void browserLoadingChanged(boolean loading, String url) {
/*  66 */         if (!loading) {
/*  67 */           skinObject.getControl().getParent().layout(true, true);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*  72 */     if (DEBUG) {
/*  73 */       System.out.println("PlusFTUX sourceRef is now " + sRef);
/*     */     }
/*     */     
/*  76 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params) {
/*  80 */     super.skinObjectShown(skinObject, params);
/*  81 */     buildURL(true);
/*  82 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params) {
/*  86 */     if (this.browserSkinObject != null) {
/*  87 */       this.browserSkinObject.setURL("about:blank");
/*     */     }
/*  89 */     sRef = "user";
/*  90 */     if (DEBUG) {
/*  91 */       System.out.println("PlusFTUX sourceRef is now " + sRef);
/*     */     }
/*  93 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateLicenceInfo()
/*     */   {
/* 100 */     buildURL(false);
/*     */   }
/*     */   
/*     */   private void buildURL(boolean forceSet)
/*     */   {
/* 105 */     FeatureUtils.licenceDetails plusDetails = FeatureUtils.getPlusFeatureDetails();
/* 106 */     long plusExpiryTimeStamp = plusDetails == null ? 0L : plusDetails.getExpiryDisplayTimeStamp();
/* 107 */     String sRef2; String sRef2; if ((plusExpiryTimeStamp > 0L) && (plusExpiryTimeStamp < SystemTime.getCurrentTime())) {
/* 108 */       sRef2 = "-/plus/renew";
/*     */     } else {
/* 110 */       sRef2 = "-/plus/ftux";
/*     */     }
/* 112 */     String suffix = "?sourceRef=" + UrlUtils.encode(new StringBuilder().append(sRef).append(sRef2).toString());
/* 113 */     String newUrl = ConstantsVuze.getDefaultContentNetwork().getSiteRelativeURL("plus-ftux.start" + suffix, false);
/*     */     
/* 115 */     newUrl = FeatureManagerUI.appendFeatureManagerURLParams(newUrl);
/* 116 */     if ((!forceSet) && (newUrl.equals(this.url))) {
/* 117 */       return;
/*     */     }
/*     */     
/* 120 */     this.url = newUrl;
/*     */     
/* 122 */     if (DEBUG) {
/* 123 */       System.out.println("URL is now " + this.url + " via " + Debug.getCompressedStackTrace());
/*     */     }
/*     */     
/*     */ 
/* 127 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 128 */     MdiEntry currentEntry = mdi.getCurrentEntry();
/*     */     
/* 130 */     if ((this.browserSkinObject != null) && ((forceSet) || (this.entry == currentEntry))) {
/* 131 */       this.browserSkinObject.setURL(this.url);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setSourceRef(String _sRef) {
/* 136 */     sRef = _sRef;
/*     */     
/* 138 */     if (DEBUG) {
/* 139 */       System.out.println("PlusFTUX sourceRef is now " + sRef);
/*     */     }
/*     */     
/* 142 */     SBC_PlusFTUX sv = (SBC_PlusFTUX)SkinViewManager.getByClass(SBC_PlusFTUX.class);
/* 143 */     if (sv != null) {
/* 144 */       sv.buildURL(false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_PlusFTUX.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */