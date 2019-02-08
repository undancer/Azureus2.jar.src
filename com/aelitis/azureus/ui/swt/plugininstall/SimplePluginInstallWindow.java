/*     */ package com.aelitis.azureus.ui.swt.plugininstall;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBoxListener;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SimplePluginInstallWindow
/*     */   implements SimplePluginInstallerListener
/*     */ {
/*  45 */   private static final boolean FAKE_DELAY = Constants.IS_CVS_VERSION;
/*     */   
/*     */ 
/*     */   private VuzeMessageBox box;
/*     */   
/*     */ 
/*     */   private ProgressBar progressBar;
/*     */   
/*     */   private SWTSkinObjectText soProgressText;
/*     */   
/*     */   private String progressText;
/*     */   
/*     */   private SWTSkinObjectText soInstallPct;
/*     */   
/*     */   private SimplePluginInstaller installer;
/*     */   
/*     */   private String resource_prefix;
/*     */   
/*     */ 
/*     */   public SimplePluginInstallWindow(SimplePluginInstaller _installer, String _resource_prefix)
/*     */   {
/*  66 */     this.installer = _installer;
/*  67 */     this.resource_prefix = _resource_prefix;
/*     */     
/*  69 */     this.installer.setListener(this);
/*     */   }
/*     */   
/*     */   public void open()
/*     */   {
/*  74 */     this.box = new VuzeMessageBox("", "", null, 0);
/*  75 */     this.box.setSubTitle(MessageText.getString(this.resource_prefix + ".subtitle"));
/*  76 */     this.box.addResourceBundle(SimplePluginInstallWindow.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/*  78 */     this.box.setIconResource(this.resource_prefix + ".image");
/*     */     
/*  80 */     this.progressText = MessageText.getString(this.resource_prefix + ".description");
/*     */     
/*  82 */     this.box.setListener(new VuzeMessageBoxListener()
/*     */     {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/*  85 */         SWTSkin skin = soExtra.getSkin();
/*  86 */         skin.createSkinObject("dlg.register.install", "dlg.register.install", soExtra);
/*     */         
/*     */ 
/*  89 */         SWTSkinObjectContainer soProgressBar = (SWTSkinObjectContainer)skin.getSkinObject("progress-bar");
/*  90 */         if (soProgressBar != null) {
/*  91 */           SimplePluginInstallWindow.this.progressBar = new ProgressBar(soProgressBar.getComposite(), 256);
/*     */           
/*  93 */           SimplePluginInstallWindow.this.progressBar.setMinimum(0);
/*  94 */           SimplePluginInstallWindow.this.progressBar.setMaximum(100);
/*  95 */           SimplePluginInstallWindow.this.progressBar.setLayoutData(Utils.getFilledFormData());
/*     */         }
/*     */         
/*  98 */         SimplePluginInstallWindow.this.soInstallPct = ((SWTSkinObjectText)skin.getSkinObject("install-pct"));
/*     */         
/* 100 */         SimplePluginInstallWindow.this.soProgressText = ((SWTSkinObjectText)skin.getSkinObject("progress-text"));
/* 101 */         if ((SimplePluginInstallWindow.this.soProgressText != null) && (SimplePluginInstallWindow.this.progressText != null)) {
/* 102 */           SimplePluginInstallWindow.this.soProgressText.setText(SimplePluginInstallWindow.this.progressText);
/*     */         }
/*     */         
/*     */       }
/* 106 */     });
/* 107 */     this.box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 109 */         SimplePluginInstallWindow.this.installer.setListener(null);
/*     */         try {
/* 111 */           SimplePluginInstallWindow.this.installer.cancel();
/*     */         } catch (Exception e) {
/* 113 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void failed(Throwable e)
/*     */   {
/* 121 */     if (this.box != null) {
/* 122 */       this.box.close(0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void finished() {
/* 127 */     if (this.box != null) {
/* 128 */       this.box.close(0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void progress(final int percent) {
/* 133 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 135 */         int pct = percent == 100 ? 99 : percent;
/* 136 */         if (SimplePluginInstallWindow.this.soInstallPct != null) {
/* 137 */           SimplePluginInstallWindow.this.soInstallPct.setText(MessageText.getString("dlg.auth.install.pct", new String[] { "" + pct }));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 142 */         if ((SimplePluginInstallWindow.this.progressBar != null) && (!SimplePluginInstallWindow.this.progressBar.isDisposed()))
/*     */         {
/* 144 */           SimplePluginInstallWindow.this.progressBar.setSelection(pct);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/plugininstall/SimplePluginInstallWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */