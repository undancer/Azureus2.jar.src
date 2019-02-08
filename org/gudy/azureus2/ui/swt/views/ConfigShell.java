/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
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
/*     */ public class ConfigShell
/*     */ {
/*     */   private static ConfigShell instance;
/*     */   private Shell shell;
/*     */   private ConfigView configView;
/*     */   private UISWTViewImpl swtView;
/*     */   
/*     */   public static ConfigShell getInstance()
/*     */   {
/*  62 */     if (null == instance) {
/*  63 */       instance = new ConfigShell();
/*     */     }
/*  65 */     return instance;
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
/*     */   public void open(final String section)
/*     */   {
/*  78 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/*  81 */         ConfigShell.this.swt_open(section);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void swt_open(String section) {
/*  87 */     if ((null != this.shell) && (!this.shell.isDisposed())) {
/*  88 */       this.configView.selectSection(section, true);
/*  89 */       if (this.shell.getMinimized()) {
/*  90 */         this.shell.setMinimized(false);
/*     */       }
/*  92 */       this.shell.forceActive();
/*  93 */       this.shell.forceFocus();
/*     */     } else {
/*  95 */       this.shell = ShellFactory.createMainShell(1136);
/*  96 */       this.shell.setLayout(new GridLayout());
/*  97 */       this.shell.setText(MessageText.getString(MessageText.resolveLocalizationKey("ConfigView.title.full")));
/*  98 */       Utils.setShellIcon(this.shell);
/*  99 */       this.configView = new ConfigView();
/*     */       try {
/* 101 */         this.swtView = new UISWTViewImpl("ConfigView", null, false);
/* 102 */         this.swtView.setDatasource(section);
/* 103 */         this.swtView.setEventListener(this.configView, true);
/*     */       } catch (Exception e1) {
/* 105 */         Debug.out(e1);
/*     */       }
/* 107 */       this.swtView.initialize(this.shell);
/* 108 */       this.configView.selectSection(section, true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 113 */       if (null == COConfigurationManager.getStringParameter("options.rectangle", null))
/*     */       {
/* 115 */         Rectangle shellBounds = this.shell.getMonitor().getBounds();
/* 116 */         Point size = new Point(shellBounds.width * 10 / 11, shellBounds.height * 10 / 11);
/*     */         
/* 118 */         if (size.x > 1400) {
/* 119 */           size.x = 1400;
/*     */         }
/* 121 */         if (size.y > 700) {
/* 122 */           size.y = 700;
/*     */         }
/* 124 */         this.shell.setSize(size);
/* 125 */         Utils.centerWindowRelativeTo(this.shell, getMainShell());
/*     */       }
/*     */       
/* 128 */       Utils.linkShellMetricsToConfig(this.shell, "options");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 133 */       this.shell.addListener(21, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 135 */           ConfigShell.this.configView.save();
/* 136 */           event.doit = true;
/*     */         }
/*     */         
/* 139 */       });
/* 140 */       this.shell.addTraverseListener(new TraverseListener() {
/*     */         public void keyTraversed(TraverseEvent e) {
/* 142 */           if (e.detail == 2) {
/* 143 */             ConfigShell.this.shell.dispose();
/*     */           }
/*     */           
/*     */         }
/* 147 */       });
/* 148 */       this.shell.addDisposeListener(new DisposeListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void widgetDisposed(DisposeEvent arg0)
/*     */         {
/*     */ 
/* 155 */           ConfigShell.this.close();
/*     */         }
/*     */         
/* 158 */       });
/* 159 */       this.shell.open();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void close()
/*     */   {
/* 171 */     if (this.swtView != null) {
/* 172 */       this.swtView.triggerEvent(7, null);
/* 173 */       this.swtView = null;
/*     */     }
/*     */     
/* 176 */     this.shell = null;
/* 177 */     this.configView = null;
/*     */   }
/*     */   
/*     */   private Shell getMainShell() {
/* 181 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 182 */     if (null != uiFunctions) {
/* 183 */       return uiFunctions.getMainShell();
/*     */     }
/*     */     
/* 186 */     throw new IllegalStateException("No instance of UIFunctionsSWT found; the UIFunctionsManager might not have been initialized properly");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/ConfigShell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */