/*     */ package org.gudy.azureus2.ui.swt.components.shell;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
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
/*     */ 
/*     */ public class ShellManager
/*     */ {
/*  46 */   private final Collection<Shell> shells = new ArrayList();
/*  47 */   private final List addHandlers = new LinkedList();
/*  48 */   private final List removeHandlers = new LinkedList();
/*     */   
/*     */ 
/*     */ 
/*  52 */   private static ShellManager instance = new ShellManager();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final ShellManager sharedManager()
/*     */   {
/*  63 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void addWindow(final Shell shell)
/*     */   {
/*  74 */     if (this.shells.contains(shell)) { return;
/*     */     }
/*  76 */     this.shells.add(shell);
/*  77 */     notifyAddListeners(shell);
/*  78 */     shell.addDisposeListener(new DisposeListener()
/*     */     {
/*     */       public void widgetDisposed(DisposeEvent event)
/*     */       {
/*     */         try {
/*  83 */           ShellManager.this.removeWindow(shell);
/*     */         } catch (Exception e) {
/*  85 */           Logger.log(new LogEvent(LogIDs.GUI, "removeWindow", e));
/*     */         }
/*     */       }
/*  88 */     });
/*  89 */     shell.addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  91 */         Utils.verifyShellRect(shell, false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void removeWindow(Shell shell)
/*     */   {
/* 103 */     this.shells.remove(shell);
/* 104 */     notifyRemoveListeners(shell);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Iterator<Shell> getWindows()
/*     */   {
/* 115 */     return this.shells.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isEmpty()
/*     */   {
/* 124 */     return this.shells.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int getSize()
/*     */   {
/* 133 */     return this.shells.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void performForShells(Listener command)
/*     */   {
/* 143 */     Iterator iter = this.shells.iterator();
/* 144 */     for (int i = 0; i < this.shells.size(); i++)
/*     */     {
/* 146 */       Shell aShell = (Shell)iter.next();
/* 147 */       Event evt = new Event();
/* 148 */       evt.widget = aShell;
/* 149 */       evt.data = this;
/* 150 */       command.handleEvent(evt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Collection getManagedShellSet()
/*     */   {
/* 160 */     return this.shells;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void addWindowAddedListener(Listener listener)
/*     */   {
/* 172 */     this.addHandlers.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void removeWindowAddedListener(Listener listener)
/*     */   {
/* 181 */     this.addHandlers.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void addWindowRemovedListener(Listener listener)
/*     */   {
/* 190 */     this.removeHandlers.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void removeWindowRemovedListener(Listener listener)
/*     */   {
/* 199 */     this.removeHandlers.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void notifyAddListeners(Shell sender)
/*     */   {
/* 208 */     Iterator iter = this.addHandlers.iterator();
/* 209 */     for (int i = 0; i < this.addHandlers.size(); i++)
/*     */     {
/* 211 */       ((Listener)iter.next()).handleEvent(getSWTEvent(sender));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void notifyRemoveListeners(Shell sender)
/*     */   {
/* 221 */     Iterator iter = this.removeHandlers.iterator();
/* 222 */     for (int i = 0; i < this.removeHandlers.size(); i++)
/*     */     {
/* 224 */       ((Listener)iter.next()).handleEvent(getSWTEvent(sender));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Event getSWTEvent(Shell shell)
/*     */   {
/* 236 */     Event e = new Event();
/* 237 */     e.widget = shell;
/* 238 */     e.item = shell;
/* 239 */     return e;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/shell/ShellManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */