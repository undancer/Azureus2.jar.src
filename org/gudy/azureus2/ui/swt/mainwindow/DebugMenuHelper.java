/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DebugMenuHelper
/*     */ {
/*     */   public static Menu createDebugMenuItem(Menu menu)
/*     */   {
/*  55 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  56 */     if (null == uiFunctions) {
/*  57 */       throw new IllegalStateException("UIFunctionsManagerSWT.getUIFunctionsSWT() is returning null");
/*     */     }
/*     */     
/*     */ 
/*  61 */     MenuItem item = new MenuItem(menu, 64);
/*  62 */     item.setText("&Debug");
/*  63 */     Menu menuDebug = new Menu(menu.getParent(), 4);
/*  64 */     item.setMenu(menuDebug);
/*     */     
/*  66 */     item = new MenuItem(menuDebug, 64);
/*  67 */     item.setText("ScreenSize");
/*  68 */     Menu menuSS = new Menu(menu.getParent(), 4);
/*  69 */     item.setMenu(menuSS);
/*     */     
/*  71 */     item = new MenuItem(menuSS, 0);
/*  72 */     item.setText("640x400");
/*  73 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  75 */         this.val$uiFunctions.getMainShell().setSize(640, 400);
/*     */       }
/*     */       
/*  78 */     });
/*  79 */     item = new MenuItem(menuSS, 0);
/*  80 */     item.setText("800x560");
/*  81 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  83 */         this.val$uiFunctions.getMainShell().setSize(850, 560);
/*     */       }
/*     */       
/*  86 */     });
/*  87 */     item = new MenuItem(menuSS, 0);
/*  88 */     item.setText("1024x700");
/*  89 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  91 */         this.val$uiFunctions.getMainShell().setSize(1024, 700);
/*     */       }
/*     */       
/*  94 */     });
/*  95 */     item = new MenuItem(menuSS, 0);
/*  96 */     item.setText("1024x768");
/*  97 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  99 */         this.val$uiFunctions.getMainShell().setSize(1024, 768);
/*     */       }
/*     */       
/* 102 */     });
/* 103 */     item = new MenuItem(menuSS, 0);
/* 104 */     item.setText("1152x784");
/* 105 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 107 */         this.val$uiFunctions.getMainShell().setSize(1152, 784);
/*     */       }
/*     */       
/* 110 */     });
/* 111 */     item = new MenuItem(menuSS, 0);
/* 112 */     item.setText("1280x720");
/* 113 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 115 */         this.val$uiFunctions.getMainShell().setSize(1280, 720);
/*     */       }
/*     */       
/* 118 */     });
/* 119 */     item = new MenuItem(menuSS, 0);
/* 120 */     item.setText("1280x1024");
/* 121 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 123 */         this.val$uiFunctions.getMainShell().setSize(1280, 1024);
/*     */       }
/* 125 */     });
/* 126 */     item = new MenuItem(menuSS, 0);
/* 127 */     item.setText("1440x820");
/* 128 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 130 */         this.val$uiFunctions.getMainShell().setSize(1440, 820);
/*     */       }
/*     */       
/* 133 */     });
/* 134 */     item = new MenuItem(menuSS, 0);
/* 135 */     item.setText("1600x970");
/* 136 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 138 */         this.val$uiFunctions.getMainShell().setSize(1600, 970);
/*     */       }
/*     */       
/* 141 */     });
/* 142 */     item = new MenuItem(menuSS, 0);
/* 143 */     item.setText("1920x1200");
/* 144 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 146 */         this.val$uiFunctions.getMainShell().setSize(1920, 1200);
/*     */       }
/*     */       
/* 149 */     });
/* 150 */     item = new MenuItem(menuSS, 0);
/* 151 */     item.setText("2560x1520");
/* 152 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 154 */         this.val$uiFunctions.getMainShell().setSize(2560, 1520);
/*     */       }
/*     */       
/* 157 */     });
/* 158 */     item = new MenuItem(menuDebug, 0);
/* 159 */     item.setText("Reload messagebundle");
/* 160 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 162 */         MessageText.loadBundle(true);
/* 163 */         DisplayFormatters.setUnits();
/* 164 */         DisplayFormatters.loadMessages();
/* 165 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 166 */         if (uiFunctions != null) {
/* 167 */           uiFunctions.refreshLanguage();
/*     */         }
/*     */         
/*     */       }
/* 171 */     });
/* 172 */     item = new MenuItem(menuDebug, 32);
/* 173 */     item.setText("SR ChangeFlagChecker Paused");
/* 174 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 176 */         StartStopRulesDefaultPlugin.pauseChangeFlagChecker = !StartStopRulesDefaultPlugin.pauseChangeFlagChecker;
/* 177 */         ((MenuItem)e.widget).setSelection(StartStopRulesDefaultPlugin.pauseChangeFlagChecker);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 182 */     });
/* 183 */     return menuDebug;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/DebugMenuHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */