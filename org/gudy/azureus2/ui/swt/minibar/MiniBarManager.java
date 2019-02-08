/*     */ package org.gudy.azureus2.ui.swt.minibar;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.ListIterator;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellManager;
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
/*     */ public class MiniBarManager
/*     */   implements UIUpdatable
/*     */ {
/*     */   private boolean global;
/*     */   private String type;
/*  43 */   private ArrayList minibars = new ArrayList();
/*     */   
/*  45 */   private static final AEMonitor minibars_mon = new AEMonitor("MiniBarManager");
/*     */   
/*  47 */   private final ShellManager shellManager = new ShellManager();
/*     */   
/*     */ 
/*     */ 
/*  51 */   private static MiniBarManager global_instance = new MiniBarManager();
/*     */   
/*     */   public static MiniBarManager getManager()
/*     */   {
/*  55 */     return global_instance;
/*     */   }
/*     */   
/*     */   MiniBarManager(String type)
/*     */   {
/*  60 */     this.global = false;
/*  61 */     this.type = type;
/*     */   }
/*     */   
/*     */   private MiniBarManager() {
/*  65 */     this.global = true;
/*  66 */     this.type = null;
/*     */   }
/*     */   
/*     */   public void register(MiniBar bar) {
/*  70 */     this.shellManager.addWindow(bar.getShell());
/*     */     try {
/*  72 */       minibars_mon.enter();
/*  73 */       this.minibars.add(bar);
/*  74 */       if (!this.global) global_instance.register(bar);
/*     */     } finally {
/*  76 */       minibars_mon.exit();
/*     */     }
/*  78 */     if (this.minibars.size() == 1) {
/*     */       try {
/*  80 */         UIFunctionsManager.getUIFunctions().getUIUpdater().addUpdater(this);
/*     */       } catch (Exception e) {
/*  82 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void unregister(MiniBar bar) {
/*     */     try {
/*  89 */       minibars_mon.enter();
/*  90 */       this.minibars.remove(bar);
/*  91 */       if (!this.global) global_instance.unregister(bar);
/*     */     } finally {
/*  93 */       minibars_mon.exit();
/*     */     }
/*  95 */     if (this.minibars.isEmpty()) {
/*     */       try {
/*  97 */         UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(this);
/*     */       } catch (Exception e) {
/*  99 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public ShellManager getShellManager() {
/* 105 */     return this.shellManager;
/*     */   }
/*     */   
/*     */   public AEMonitor getMiniBarMonitor() {
/* 109 */     return minibars_mon;
/*     */   }
/*     */   
/*     */   public ListIterator getMiniBarIterator() {
/* 113 */     return this.minibars.listIterator();
/*     */   }
/*     */   
/*     */   public int countMiniBars() {
/* 117 */     return this.minibars.size();
/*     */   }
/*     */   
/*     */   public void setAllVisible(boolean visible)
/*     */   {
/*     */     try {
/* 123 */       minibars_mon.enter();
/*     */       
/* 125 */       for (iter = this.minibars.iterator(); iter.hasNext();) {
/* 126 */         MiniBar bar = (MiniBar)iter.next();
/* 127 */         bar.setVisible(visible);
/*     */       }
/*     */     } finally {
/*     */       Iterator iter;
/* 131 */       minibars_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void close(MiniBar mini_bar) {
/* 136 */     if (mini_bar != null) {
/* 137 */       mini_bar.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public MiniBar getMiniBarForObject(Object context) {
/*     */     try {
/* 143 */       minibars_mon.enter();
/* 144 */       for (Iterator iter = this.minibars.iterator(); iter.hasNext();) {
/* 145 */         MiniBar bar = (MiniBar)iter.next();
/* 146 */         if (bar.hasContext(context)) return bar;
/*     */       }
/* 148 */       return null;
/*     */     }
/*     */     finally {
/* 151 */       minibars_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void close(Object context) {
/* 156 */     MiniBar bar = getMiniBarForObject(context);
/* 157 */     if (bar != null) bar.close();
/*     */   }
/*     */   
/*     */   public void closeAll() {
/*     */     try {
/* 162 */       minibars_mon.enter();
/* 163 */       for (iter = new ArrayList(this.minibars).iterator(); iter.hasNext();) {
/* 164 */         MiniBar bar = (MiniBar)iter.next();
/* 165 */         bar.close();
/*     */       }
/*     */     } finally {
/*     */       Iterator iter;
/* 169 */       minibars_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isOpen(Object context) {
/* 174 */     return getMiniBarForObject(context) != null;
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 179 */     return "MiniBar-" + this.type;
/*     */   }
/*     */   
/*     */   public void updateUI()
/*     */   {
/*     */     try {
/* 185 */       minibars_mon.enter();
/*     */       
/* 187 */       for (iter = this.minibars.iterator(); iter.hasNext();) {
/* 188 */         MiniBar bar = (MiniBar)iter.next();
/*     */         try {
/* 190 */           bar.refresh();
/*     */         } catch (Exception e) {
/* 192 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     } finally { Iterator iter;
/* 196 */       minibars_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/minibar/MiniBarManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */