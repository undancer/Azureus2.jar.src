/*     */ package com.aelitis.azureus.ui.swt.feature;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBoxListener;
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence.LicenceInstallationListener;
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
/*     */ public class FeatureManagerInstallWindow
/*     */   implements FeatureManager.Licence.LicenceInstallationListener
/*     */ {
/*  52 */   private static final boolean FAKE_DELAY = Constants.IS_CVS_VERSION;
/*     */   
/*     */   private VuzeMessageBox box;
/*     */   
/*     */   private ProgressBar progressBar;
/*     */   
/*     */   private FeatureManager.Licence licence;
/*     */   
/*     */   private SWTSkinObjectText soProgressText;
/*     */   
/*     */   private String progressText;
/*     */   private SWTSkinObjectText soInstallPct;
/*     */   
/*     */   public FeatureManagerInstallWindow(FeatureManager.Licence licence)
/*     */   {
/*  67 */     if (!FeatureManagerUI.enabled) {
/*  68 */       return;
/*     */     }
/*  70 */     this.licence = licence;
/*  71 */     licence.addInstallationListener(this);
/*     */   }
/*     */   
/*     */   public void open() {
/*  75 */     if (!FeatureManagerUI.enabled) {
/*  76 */       return;
/*     */     }
/*     */     
/*  79 */     boolean isTrial = FeatureUtils.isTrialLicence(this.licence);
/*  80 */     this.box = new VuzeMessageBox(MessageText.getString("dlg.auth.title"), "", null, 0);
/*     */     
/*  82 */     this.box.setSubTitle(MessageText.getString(isTrial ? "dlg.auth.install.subtitle.trial" : "dlg.auth.install.subtitle.plus"));
/*     */     
/*  84 */     this.box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/*  86 */     this.box.setIconResource(isTrial ? "image.burn.dlg.header" : "image.vp");
/*     */     
/*  88 */     this.box.setListener(new VuzeMessageBoxListener()
/*     */     {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/*  91 */         SWTSkin skin = soExtra.getSkin();
/*  92 */         skin.createSkinObject("dlg.register.install", "dlg.register.install", soExtra);
/*     */         
/*     */ 
/*  95 */         SWTSkinObjectContainer soProgressBar = (SWTSkinObjectContainer)skin.getSkinObject("progress-bar");
/*  96 */         if (soProgressBar != null) {
/*  97 */           FeatureManagerInstallWindow.this.progressBar = new ProgressBar(soProgressBar.getComposite(), 256);
/*     */           
/*  99 */           FeatureManagerInstallWindow.this.progressBar.setMinimum(0);
/* 100 */           FeatureManagerInstallWindow.this.progressBar.setMaximum(100);
/* 101 */           FeatureManagerInstallWindow.this.progressBar.setLayoutData(Utils.getFilledFormData());
/*     */         }
/*     */         
/* 104 */         FeatureManagerInstallWindow.this.soInstallPct = ((SWTSkinObjectText)skin.getSkinObject("install-pct"));
/*     */         
/* 106 */         FeatureManagerInstallWindow.this.soProgressText = ((SWTSkinObjectText)skin.getSkinObject("progress-text"));
/* 107 */         if ((FeatureManagerInstallWindow.this.soProgressText != null) && (FeatureManagerInstallWindow.this.progressText != null)) {
/* 108 */           FeatureManagerInstallWindow.this.soProgressText.setText(FeatureManagerInstallWindow.this.progressText);
/*     */         }
/*     */         
/*     */       }
/* 112 */     });
/* 113 */     this.box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 115 */         FeatureManagerInstallWindow.this.licence.removeInstallationListener(FeatureManagerInstallWindow.this);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void reportActivity(String licence_key, String install, String activity) {
/* 121 */     if (FAKE_DELAY) {
/*     */       try {
/* 123 */         Thread.sleep(80L);
/*     */       }
/*     */       catch (InterruptedException e) {}
/*     */     }
/*     */     
/* 128 */     if (this.soProgressText != null) {
/* 129 */       String[] split = install.split("/", 2);
/* 130 */       this.progressText = MessageText.getString("dlg.auth.install.progress", new String[] { split.length == 2 ? split[1] : split[0] });
/*     */       
/* 132 */       this.soProgressText.setText(this.progressText);
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportProgress(String licence_key, String install, final int percent)
/*     */   {
/* 138 */     if (FAKE_DELAY) {
/*     */       try {
/* 140 */         Thread.sleep(80L);
/*     */       }
/*     */       catch (InterruptedException e) {}
/*     */     }
/*     */     
/* 145 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 147 */         int pct = percent == 100 ? 99 : percent;
/* 148 */         if (FeatureManagerInstallWindow.this.soInstallPct != null) {
/* 149 */           FeatureManagerInstallWindow.this.soInstallPct.setText(MessageText.getString("dlg.auth.install.pct", new String[] { "" + pct }));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 154 */         if ((FeatureManagerInstallWindow.this.progressBar != null) && (!FeatureManagerInstallWindow.this.progressBar.isDisposed()))
/*     */         {
/* 156 */           FeatureManagerInstallWindow.this.progressBar.setSelection(pct);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void complete(String licence_key) {
/* 163 */     if (this.box != null) {
/* 164 */       this.box.close(0);
/*     */     }
/* 166 */     this.licence.removeInstallationListener(this);
/*     */   }
/*     */   
/* 169 */   public static boolean alreadyFailing = false;
/*     */   
/* 171 */   public void failed(String licence_key, PluginException error) { if (alreadyFailing) {
/* 172 */       return;
/*     */     }
/* 174 */     alreadyFailing = true;
/* 175 */     UIFunctionsManager.getUIFunctions().promptUser(MessageText.getString("dlg.auth.install.failed.title"), MessageText.getString("dlg.auth.install.failed.text", new String[] { licence_key, Debug.getNestedExceptionMessage(error) }), new String[] { MessageText.getString("Button.ok") }, 0, null, null, false, 0, new UserPrompterResultListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void prompterClosed(int result)
/*     */       {
/*     */ 
/* 182 */         FeatureManagerInstallWindow.alreadyFailing = false;
/*     */       }
/*     */       
/*     */ 
/* 186 */     });
/* 187 */     Logger.log(new LogAlert(true, "Error while installing " + licence_key, error));
/*     */     
/* 189 */     this.box.close(0);
/* 190 */     this.licence.removeInstallationListener(this);
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/* 195 */     this.box.close(0);
/* 196 */     this.licence.removeInstallationListener(this);
/*     */   }
/*     */   
/*     */   public void start(String licence_key) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/feature/FeatureManagerInstallWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */