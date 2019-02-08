/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryDropListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public class WelcomeView
/*     */   extends SkinView
/*     */ {
/*  44 */   private static boolean waitLoadingURL = true;
/*     */   
/*     */   private static WelcomeView instance;
/*     */   
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   private SWTSkinObject skinObject;
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/*  53 */     instance = null;
/*  54 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object skinObjectInitialShow(final SWTSkinObject skinObject, Object params)
/*     */   {
/*  60 */     this.skinObject = skinObject;
/*  61 */     instance = this;
/*  62 */     this.browserSkinObject = ((SWTSkinObjectBrowser)this.skin.getSkinObject("welcome", this.soMain));
/*     */     
/*     */ 
/*  65 */     this.browserSkinObject.addListener(new BrowserContext.loadingListener()
/*     */     {
/*     */       public void browserLoadingChanged(boolean loading, String url) {
/*  68 */         if (!loading) {
/*  69 */           skinObject.getControl().getParent().layout(true, true);
/*     */         }
/*     */         
/*     */       }
/*  73 */     });
/*  74 */     COConfigurationManager.setParameter("v3.Show Welcome", false);
/*     */     
/*  76 */     openURL();
/*     */     
/*  78 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*     */     
/*  80 */     if (mdi != null)
/*     */     {
/*  82 */       MdiEntry entry = mdi.getEntry("Welcome");
/*     */       
/*  84 */       if (entry != null)
/*     */       {
/*  86 */         entry.addListener(new MdiCloseListener() {
/*     */           public void mdiEntryClosed(MdiEntry entry, boolean userClosed) {
/*  88 */             MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  89 */             if (mdi != null) {
/*  90 */               mdi.showEntryByID("Library");
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*  97 */     return null;
/*     */   }
/*     */   
/*     */   private void openURL() {
/* 101 */     if (waitLoadingURL) {
/* 102 */       return;
/*     */     }
/*     */     
/* 105 */     Object o = this.skinObject.getData("CreationParams");
/* 106 */     String sURL; String sURL; if ((o instanceof String)) {
/* 107 */       sURL = (String)o;
/*     */     } else {
/* 109 */       sURL = ContentNetworkUtils.getUrl(ConstantsVuze.getDefaultContentNetwork(), 8);
/*     */     }
/*     */     
/* 112 */     this.browserSkinObject.enablePluginProxy("welcome");
/*     */     
/* 114 */     this.browserSkinObject.setURL(sURL);
/*     */   }
/*     */   
/*     */   public static void setWaitLoadingURL(boolean waitLoadingURL) {
/* 118 */     waitLoadingURL = waitLoadingURL;
/* 119 */     if ((!waitLoadingURL) && (instance != null)) {
/* 120 */       instance.openURL();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setupSidebarEntry(MultipleDocumentInterface mdi) {
/* 125 */     mdi.registerEntry("Welcome", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 128 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.vuze", "Welcome", "main.area.welcome", MessageText.getString("v3.MainWindow.menu.getting_started").replaceAll("&", ""), null, null, true, "");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */         entry.setImageLeftID("image.sidebar.welcome");
/* 136 */         WelcomeView.addDropTest(entry);
/* 137 */         return entry;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static void addDropTest(MdiEntry entry) {
/* 143 */     if (!Constants.isCVSVersion()) {
/* 144 */       return;
/*     */     }
/* 146 */     entry.addListener(new MdiEntryDropListener() {
/*     */       public boolean mdiEntryDrop(MdiEntry entry, Object droppedObject) {
/* 148 */         String s = "You just dropped " + droppedObject.getClass() + "\n" + droppedObject + "\n\n";
/*     */         
/* 150 */         if (droppedObject.getClass().isArray()) {
/* 151 */           Object[] o = (Object[])droppedObject;
/* 152 */           for (int i = 0; i < o.length; i++) {
/* 153 */             s = s + "" + i + ":  ";
/* 154 */             Object object = o[i];
/* 155 */             if (object == null) {
/* 156 */               s = s + "null";
/*     */             } else {
/* 158 */               s = s + object.getClass() + ";" + object;
/*     */             }
/* 160 */             s = s + "\n";
/*     */           }
/*     */         }
/* 163 */         UIFunctionsManager.getUIFunctions().promptUser("test", s, null, 0, null, null, false, 0, null);
/*     */         
/* 165 */         return true;
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/WelcomeView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */