/*     */ package com.aelitis.azureus.ui.swt.player;
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
/*     */ public class PlayerInstallWindow
/*     */   implements PlayerInstallerListener
/*     */ {
/*  44 */   private static final boolean FAKE_DELAY = Constants.IS_CVS_VERSION;
/*     */   
/*     */   private VuzeMessageBox box;
/*     */   
/*     */   private ProgressBar progressBar;
/*     */   
/*     */   private SWTSkinObjectText soProgressText;
/*     */   
/*     */   private String progressText;
/*     */   
/*     */   private SWTSkinObjectText soInstallPct;
/*     */   
/*     */   private PlayerInstaller installer;
/*     */   
/*     */   public PlayerInstallWindow(PlayerInstaller installer)
/*     */   {
/*  60 */     this.installer = installer;
/*  61 */     installer.setListener(this);
/*     */   }
/*     */   
/*     */   public void open()
/*     */   {
/*  66 */     this.box = new VuzeMessageBox("", "", null, 0);
/*  67 */     this.box.setSubTitle(MessageText.getString("dlg.player.install.subtitle"));
/*  68 */     this.box.addResourceBundle(PlayerInstallWindow.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/*  70 */     this.box.setIconResource("image.player.dlg.header");
/*     */     
/*  72 */     this.progressText = MessageText.getString("dlg.player.install.description");
/*     */     
/*  74 */     this.box.setListener(new VuzeMessageBoxListener()
/*     */     {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/*  77 */         SWTSkin skin = soExtra.getSkin();
/*  78 */         skin.createSkinObject("dlg.register.install", "dlg.register.install", soExtra);
/*     */         
/*     */ 
/*  81 */         SWTSkinObjectContainer soProgressBar = (SWTSkinObjectContainer)skin.getSkinObject("progress-bar");
/*  82 */         if (soProgressBar != null) {
/*  83 */           PlayerInstallWindow.this.progressBar = new ProgressBar(soProgressBar.getComposite(), 256);
/*     */           
/*  85 */           PlayerInstallWindow.this.progressBar.setMinimum(0);
/*  86 */           PlayerInstallWindow.this.progressBar.setMaximum(100);
/*  87 */           PlayerInstallWindow.this.progressBar.setLayoutData(Utils.getFilledFormData());
/*     */         }
/*     */         
/*  90 */         PlayerInstallWindow.this.soInstallPct = ((SWTSkinObjectText)skin.getSkinObject("install-pct"));
/*     */         
/*  92 */         PlayerInstallWindow.this.soProgressText = ((SWTSkinObjectText)skin.getSkinObject("progress-text"));
/*  93 */         if ((PlayerInstallWindow.this.soProgressText != null) && (PlayerInstallWindow.this.progressText != null)) {
/*  94 */           PlayerInstallWindow.this.soProgressText.setText(PlayerInstallWindow.this.progressText);
/*     */         }
/*     */         
/*     */       }
/*  98 */     });
/*  99 */     this.box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 101 */         PlayerInstallWindow.this.installer.setListener(null);
/* 102 */         PlayerInstallWindow.this.installer.cancel();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void failed()
/*     */   {
/* 109 */     if (this.box != null) {
/* 110 */       this.box.close(0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void finished() {
/* 115 */     if (this.box != null) {
/* 116 */       this.box.close(0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void progress(final int percent) {
/* 121 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 123 */         int pct = percent == 100 ? 99 : percent;
/* 124 */         if (PlayerInstallWindow.this.soInstallPct != null) {
/* 125 */           PlayerInstallWindow.this.soInstallPct.setText(MessageText.getString("dlg.auth.install.pct", new String[] { "" + pct }));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 130 */         if ((PlayerInstallWindow.this.progressBar != null) && (!PlayerInstallWindow.this.progressBar.isDisposed()))
/*     */         {
/* 132 */           PlayerInstallWindow.this.progressBar.setSelection(pct);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/player/PlayerInstallWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */