/*     */ package org.gudy.azureus2.ui.swt.minibar;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.DoubleBufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
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
/*     */ public class DownloadBar
/*     */   extends MiniBar
/*     */ {
/*  52 */   private static MiniBarManager manager = new MiniBarManager("AllTransfersBar");
/*     */   private DownloadManager download;
/*     */   
/*     */   public static MiniBarManager getManager() {
/*  56 */     return manager;
/*     */   }
/*     */   
/*     */   public static DownloadBar open(DownloadManager download, Shell main) {
/*  60 */     DownloadBar result = (DownloadBar)manager.getMiniBarForObject(download);
/*  61 */     if (result == null) {
/*  62 */       result = new DownloadBar(download, main);
/*     */     }
/*  64 */     return result;
/*     */   }
/*     */   
/*     */   public static void close(DownloadManager download) {
/*  68 */     DownloadBar result = (DownloadBar)manager.getMiniBarForObject(download);
/*  69 */     if (result != null) { result.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private DoubleBufferedLabel download_name;
/*     */   private ProgressBar progress_bar;
/*     */   private DoubleBufferedLabel down_speed;
/*     */   private DoubleBufferedLabel up_speed;
/*     */   private DoubleBufferedLabel eta;
/*     */   private DownloadBar(DownloadManager download, Shell main)
/*     */   {
/*  80 */     super(manager);
/*  81 */     this.download = download;
/*  82 */     construct(main);
/*     */   }
/*     */   
/*  85 */   public Object getContextObject() { return this.download; }
/*     */   
/*     */ 
/*     */   public void beginConstruction()
/*     */   {
/*  90 */     createFixedTextLabel("MinimizedWindow.name", false, false);
/*  91 */     this.download_name = createDataLabel(200);
/*     */     
/*     */ 
/*  94 */     this.progress_bar = createPercentProgressBar(100);
/*     */     
/*     */ 
/*  97 */     createFixedTextLabel("ConfigView.download.abbreviated", false, false);
/*  98 */     this.down_speed = createSpeedLabel();
/*     */     
/*     */ 
/* 101 */     createFixedTextLabel("ConfigView.upload.abbreviated", false, false);
/* 102 */     this.up_speed = createSpeedLabel();
/*     */     
/*     */ 
/* 105 */     createFixedTextLabel("TableColumn.header.eta", true, false);
/* 106 */     this.eta = createDataLabel(65);
/*     */   }
/*     */   
/*     */ 
/*     */   public void buildMenu(Menu menu, MenuEvent menuEvent)
/*     */   {
/* 112 */     MenuItem itemQueue = new MenuItem(menu, 8);
/* 113 */     Messages.setLanguageText(itemQueue, "MyTorrentsView.menu.queue");
/* 114 */     Utils.setMenuItemImage(itemQueue, "start");
/* 115 */     itemQueue.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 117 */         ManagerUtils.queue(DownloadBar.this.download, DownloadBar.this.splash);
/*     */       }
/* 119 */     });
/* 120 */     itemQueue.setEnabled(ManagerUtils.isStartable(this.download));
/*     */     
/*     */ 
/*     */ 
/* 124 */     MenuItem itemStop = new MenuItem(menu, 8);
/* 125 */     Messages.setLanguageText(itemStop, "MyTorrentsView.menu.stop");
/* 126 */     Utils.setMenuItemImage(itemStop, "stop");
/* 127 */     itemStop.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 129 */         ManagerUtils.stop(DownloadBar.this.download, DownloadBar.this.splash);
/*     */       }
/* 131 */     });
/* 132 */     itemStop.setEnabled(ManagerUtils.isStopable(this.download));
/*     */     
/* 134 */     new MenuItem(menu, 2);
/* 135 */     super.buildMenu(menu);
/*     */   }
/*     */   
/*     */   protected void refresh0() {
/* 139 */     DownloadManagerStats stats = this.download.getStats();
/*     */     
/* 141 */     this.download_name.setText(this.download.getDisplayName());
/* 142 */     int percent = stats.getPercentDoneExcludingDND();
/*     */     
/* 144 */     updateSpeedLabel(this.down_speed, stats.getDataReceiveRate(), stats.getProtocolReceiveRate());
/* 145 */     updateSpeedLabel(this.up_speed, stats.getDataSendRate(), stats.getProtocolSendRate());
/*     */     
/* 147 */     this.eta.setText(DisplayFormatters.formatETA(stats.getSmoothedETA()));
/* 148 */     if (this.progress_bar.getSelection() != percent) {
/* 149 */       this.progress_bar.setSelection(percent);
/* 150 */       this.progress_bar.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void doubleClick()
/*     */   {
/* 156 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 157 */     if (functionsSWT != null) {
/* 158 */       functionsSWT.bringToFront();
/*     */     }
/* 160 */     this.download.fireGlobalManagerEvent(1);
/*     */   }
/*     */   
/* 163 */   public String[] getPluginMenuIdentifiers(Object[] context) { if (context == null) return null;
/* 164 */     return new String[] { "downloadbar", "download_context" };
/*     */   }
/*     */   
/*     */   public Object[] getPluginMenuContextObjects() {
/* 168 */     try { return new Download[] { DownloadManagerImpl.getDownloadStatic(this.download) }; } catch (DownloadException de) {}
/* 169 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/minibar/DownloadBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */