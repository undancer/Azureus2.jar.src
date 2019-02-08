/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ 
/*     */ public class SkinnedDialog
/*     */ {
/*     */   private final String shellSkinObjectID;
/*     */   private Shell shell;
/*     */   private SWTSkin skin;
/*  54 */   private List<SkinnedDialogClosedListener> closeListeners = new CopyOnWriteArrayList();
/*     */   
/*     */   private Shell mainShell;
/*     */   protected boolean disposed;
/*     */   
/*     */   public SkinnedDialog(String skinFile, String shellSkinObjectID)
/*     */   {
/*  61 */     this(skinFile, shellSkinObjectID, 2160);
/*     */   }
/*     */   
/*     */   public SkinnedDialog(String skinFile, String shellSkinObjectID, int style) {
/*  65 */     this(SkinnedDialog.class.getClassLoader(), "com/aelitis/azureus/ui/skin/", skinFile, shellSkinObjectID, style);
/*     */   }
/*     */   
/*     */   public SkinnedDialog(String skinFile, String shellSkinObjectID, Shell parent, int style)
/*     */   {
/*  70 */     this(SkinnedDialog.class.getClassLoader(), "com/aelitis/azureus/ui/skin/", skinFile, shellSkinObjectID, parent, style);
/*     */   }
/*     */   
/*     */ 
/*     */   public SkinnedDialog(ClassLoader cla, String skinPath, String skinFile, String shellSkinObjectID, int style)
/*     */   {
/*  76 */     this(cla, skinPath, skinFile, shellSkinObjectID, UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell(), style);
/*     */   }
/*     */   
/*     */ 
/*     */   public SkinnedDialog(ClassLoader cla, String skinPath, String skinFile, String shellSkinObjectID, Shell parent, int style)
/*     */   {
/*  82 */     this.shellSkinObjectID = shellSkinObjectID;
/*     */     
/*  84 */     this.mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/*  85 */     this.shell = ShellFactory.createShell(parent, style);
/*     */     
/*  87 */     Utils.setShellIcon(this.shell);
/*     */     
/*  89 */     SWTSkin skin = SWTSkinFactory.getNonPersistentInstance(cla, skinPath, skinFile + ".properties");
/*     */     
/*     */ 
/*  92 */     setSkin(skin);
/*     */     
/*  94 */     skin.initialize(this.shell, shellSkinObjectID);
/*     */     
/*  96 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/*  98 */         if (e.detail == 2) {
/*  99 */           SkinnedDialog.this.shell.close();
/*     */         }
/*     */         
/*     */       }
/* 103 */     });
/* 104 */     this.shell.addDisposeListener(new DisposeListener()
/*     */     {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 107 */         SkinnedDialog.this.disposed = true;
/* 108 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*     */           public void runSupport() {
/* 110 */             for (SkinnedDialog.SkinnedDialogClosedListener l : SkinnedDialog.this.closeListeners) {
/*     */               try {
/* 112 */                 l.skinDialogClosed(SkinnedDialog.this);
/*     */               } catch (Exception e2) {
/* 114 */                 Debug.out(e2);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 121 */     });
/* 122 */     this.disposed = false;
/*     */   }
/*     */   
/*     */   protected void setSkin(SWTSkin _skin) {
/* 126 */     this.skin = _skin;
/*     */   }
/*     */   
/*     */   public void open() {
/* 130 */     open(null, true);
/*     */   }
/*     */   
/*     */   public void open(String idShellMetrics, boolean bringToFront) {
/* 134 */     if (this.disposed) {
/* 135 */       Debug.out("can't opened disposed skinnedialog");
/* 136 */       return;
/*     */     }
/* 138 */     this.skin.layout();
/*     */     
/* 140 */     if (idShellMetrics != null) {
/* 141 */       Utils.linkShellMetricsToConfig(this.shell, idShellMetrics);
/*     */     } else {
/* 143 */       Utils.centerWindowRelativeTo(this.shell, this.mainShell);
/* 144 */       Utils.verifyShellRect(this.shell, true);
/*     */     }
/*     */     
/* 147 */     this.shell.setData("bringToFront", Boolean.valueOf(bringToFront));
/* 148 */     this.shell.open();
/*     */   }
/*     */   
/*     */   public SWTSkin getSkin() {
/* 152 */     return this.skin;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 161 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 163 */         if (SkinnedDialog.this.disposed) {
/* 164 */           return;
/*     */         }
/* 166 */         if ((SkinnedDialog.this.shell != null) && (!SkinnedDialog.this.shell.isDisposed())) {
/* 167 */           SkinnedDialog.this.shell.close();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void addCloseListener(SkinnedDialogClosedListener l) {
/* 174 */     this.closeListeners.add(l);
/*     */   }
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
/*     */   public void setTitle(String string)
/*     */   {
/* 188 */     if ((!this.disposed) && (this.shell != null) && (!this.shell.isDisposed())) {
/* 189 */       this.shell.setText(string);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Shell getShell()
/*     */   {
/* 197 */     return this.shell;
/*     */   }
/*     */   
/*     */   public boolean isDisposed() {
/* 201 */     return (this.disposed) || (this.shell == null) || (this.shell.isDisposed());
/*     */   }
/*     */   
/*     */   public static abstract interface SkinnedDialogClosedListener
/*     */   {
/*     */     public abstract void skinDialogClosed(SkinnedDialog paramSkinnedDialog);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SkinnedDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */