/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.DefaultRankCalculator;
/*     */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin.UIAdapter;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
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
/*     */ public class StartStopRulesDefaultPluginSWTUI
/*     */   implements StartStopRulesDefaultPlugin.UIAdapter
/*     */ {
/*     */   public StartStopRulesDefaultPluginSWTUI(PluginInterface plugin_interface)
/*     */   {
/*  45 */     plugin_interface.addConfigSection(new ConfigSectionQueue());
/*  46 */     plugin_interface.addConfigSection(new ConfigSectionDownloading());
/*  47 */     plugin_interface.addConfigSection(new ConfigSectionSeeding());
/*  48 */     plugin_interface.addConfigSection(new ConfigSectionSeedingAutoStarting());
/*  49 */     plugin_interface.addConfigSection(new ConfigSectionSeedingFirstPriority());
/*  50 */     plugin_interface.addConfigSection(new ConfigSectionSeedingIgnore());
/*     */   }
/*     */   
/*     */   public void openDebugWindow(final DefaultRankCalculator dlData) {
/*  54 */     final Shell shell = new Shell(Display.getCurrent(), 17652);
/*     */     
/*     */ 
/*  57 */     GridLayout layout = new GridLayout();
/*  58 */     layout.numColumns = 4;
/*     */     
/*  60 */     shell.setLayout(layout);
/*     */     
/*  62 */     shell.setText("Debug for " + dlData.getDownloadObject().getName());
/*     */     
/*  64 */     final Text txtFP = new Text(shell, 2306);
/*  65 */     GridData gd = new GridData(1808);
/*  66 */     gd.horizontalSpan = 4;
/*  67 */     txtFP.setLayoutData(gd);
/*     */     
/*  69 */     final Button btnAutoRefresh = new Button(shell, 32);
/*  70 */     btnAutoRefresh.setText("Auto-Refresh");
/*  71 */     btnAutoRefresh.setLayoutData(new GridData());
/*     */     
/*  73 */     final Button btnRefresh = new Button(shell, 0);
/*  74 */     btnRefresh.setLayoutData(new GridData());
/*  75 */     btnRefresh.setText("Refresh");
/*     */     
/*  77 */     Button btnToClip = new Button(shell, 0);
/*  78 */     btnToClip.setLayoutData(new GridData());
/*  79 */     btnToClip.setText("To Clipboard");
/*  80 */     btnToClip.addListener(4, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  82 */         new Clipboard(Display.getCurrent()).setContents(new Object[] { txtFP.getText() }, new Transfer[] { TextTransfer.getInstance() });
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  87 */     });
/*  88 */     final Label lbl = new Label(shell, 0);
/*  89 */     gd = new GridData(768);
/*  90 */     lbl.setLayoutData(gd);
/*     */     
/*  92 */     final TimerTask task = new TimerTask() {
/*  93 */       String lastText = "";
/*     */       
/*     */       public String formatString() {
/*  96 */         return "FP:\n" + dlData.sExplainFP + "\n" + "SR:" + dlData.sExplainSR + "\n" + "TRACE:\n" + dlData.sTrace;
/*     */       }
/*     */       
/*     */       public void setText(String s)
/*     */       {
/* 101 */         this.lastText = s;
/*     */         
/* 103 */         txtFP.setText(s);
/*     */       }
/*     */       
/*     */       public void run() {
/* 107 */         if (shell.isDisposed()) {
/* 108 */           return;
/*     */         }
/* 110 */         shell.getDisplay().syncExec(new Runnable() {
/*     */           public void run() {
/* 112 */             if (StartStopRulesDefaultPluginSWTUI.2.this.val$shell.isDisposed()) {
/* 113 */               return;
/*     */             }
/* 115 */             String s = StartStopRulesDefaultPluginSWTUI.2.this.formatString();
/* 116 */             if (s.compareTo(StartStopRulesDefaultPluginSWTUI.2.this.lastText) != 0) {
/* 117 */               if ((StartStopRulesDefaultPluginSWTUI.2.this.lastText.length() == 0) || (StartStopRulesDefaultPluginSWTUI.2.this.val$btnAutoRefresh.getSelection()) || (StartStopRulesDefaultPluginSWTUI.2.this.val$btnRefresh.getData("Pressing") != null))
/*     */               {
/* 119 */                 StartStopRulesDefaultPluginSWTUI.2.this.setText(s);
/*     */               } else
/* 121 */                 StartStopRulesDefaultPluginSWTUI.2.this.val$lbl.setText("Information is outdated.  Press refresh.");
/*     */             } else {
/* 123 */               StartStopRulesDefaultPluginSWTUI.2.this.val$lbl.setText("");
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 128 */     };
/* 129 */     btnAutoRefresh.addListener(4, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 131 */         if (btnAutoRefresh.getSelection())
/* 132 */           lbl.setText("");
/* 133 */         task.run();
/*     */       }
/*     */       
/* 136 */     });
/* 137 */     btnRefresh.addListener(4, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 139 */         btnRefresh.setData("Pressing", "1");
/* 140 */         task.run();
/* 141 */         btnRefresh.setData("Pressing", null);
/*     */       }
/*     */       
/* 144 */     });
/* 145 */     shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 147 */         if (e.detail == 2) {
/* 148 */           shell.dispose();
/*     */         }
/*     */         
/*     */       }
/* 152 */     });
/* 153 */     shell.setSize(550, 350);
/* 154 */     shell.open();
/*     */     
/* 156 */     Timer timer = new Timer(true);
/* 157 */     timer.schedule(task, 0L, 2000L);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/StartStopRulesDefaultPluginSWTUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */