/*     */ package org.gudy.azureus2.ui.swt.sharing;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
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
/*     */ public class ShareUtils
/*     */ {
/*     */   public static void shareFile(final Shell shell)
/*     */   {
/*  49 */     new AEThread("shareFile")
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*  54 */         Display display = shell.getDisplay();
/*  55 */         final String[] path = { null };
/*  56 */         final AESemaphore sem = new AESemaphore("ShareUtils:file");
/*     */         
/*     */ 
/*  59 */         display.asyncExec(new AERunnable()
/*     */         {
/*     */           public void runSupport() {
/*     */             try {
/*  63 */               FileDialog dialog = new FileDialog(ShareUtils.1.this.val$shell, 135168);
/*     */               
/*  65 */               dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*     */               
/*  67 */               dialog.setText(MessageText.getString("MainWindow.dialog.share.sharefile"));
/*     */               
/*  69 */               path[0] = TorrentOpener.setFilterPathData(dialog.open());
/*     */             }
/*     */             finally
/*     */             {
/*  73 */               sem.release();
/*     */             }
/*     */             
/*     */           }
/*  77 */         });
/*  78 */         sem.reserve();
/*     */         
/*  80 */         if (path[0] != null)
/*     */         {
/*  82 */           ShareUtils.shareFile(path[0]);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void shareDir(Shell shell)
/*     */   {
/*  92 */     shareDirSupport(shell, false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void shareDirContents(Shell shell, boolean recursive)
/*     */   {
/* 100 */     shareDirSupport(shell, true, recursive);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void shareDirSupport(final Shell shell, final boolean contents, final boolean recursive)
/*     */   {
/* 109 */     new AEThread("shareDirSupport")
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/* 114 */         Display display = shell.getDisplay();
/* 115 */         final String[] path = { null };
/* 116 */         final AESemaphore sem = new AESemaphore("ShareUtils:dir");
/*     */         
/* 118 */         display.asyncExec(new AERunnable()
/*     */         {
/*     */           public void runSupport() {
/*     */             try {
/* 122 */               DirectoryDialog dialog = new DirectoryDialog(ShareUtils.2.this.val$shell, 131072);
/*     */               
/* 124 */               dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*     */               
/* 126 */               dialog.setText(ShareUtils.2.this.val$contents ? MessageText.getString("MainWindow.dialog.share.sharedircontents") + (ShareUtils.2.this.val$recursive ? "(" + MessageText.getString("MainWindow.dialog.share.sharedircontents.recursive") + ")" : "") : MessageText.getString("MainWindow.dialog.share.sharedir"));
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */               path[0] = TorrentOpener.setFilterPathData(dialog.open());
/*     */             }
/*     */             finally
/*     */             {
/* 136 */               sem.release();
/*     */             }
/*     */             
/*     */           }
/* 140 */         });
/* 141 */         sem.reserve();
/*     */         
/* 143 */         if (path[0] != null)
/*     */         {
/* 145 */           if (contents)
/*     */           {
/* 147 */             ShareUtils.shareDirContents(path[0], recursive);
/*     */           }
/*     */           else
/*     */           {
/* 151 */             ShareUtils.shareDir(path[0]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void shareFile(String file_name)
/*     */   {
/* 162 */     shareFile(file_name, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void shareFile(final String file_name, final Map<String, String> properties)
/*     */   {
/* 170 */     new AEThread("shareFile")
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 176 */           PluginInitializer.getDefaultInterface().getShareManager().addFile(new File(file_name), properties);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 180 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void shareDir(String file_name)
/*     */   {
/* 190 */     shareDir(file_name, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void shareDir(final String file_name, final Map<String, String> properties)
/*     */   {
/* 198 */     new AEThread("shareDir")
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 204 */           PluginInitializer.getDefaultInterface().getShareManager().addDir(new File(file_name), properties);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 208 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void shareDirContents(final String file_name, final boolean recursive)
/*     */   {
/* 219 */     new AEThread("shareDirCntents")
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 225 */           PluginInitializer.getDefaultInterface().getShareManager().addDirContents(new File(file_name), recursive);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 229 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/sharing/ShareUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */