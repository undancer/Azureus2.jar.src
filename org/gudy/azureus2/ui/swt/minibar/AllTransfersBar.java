/*     */ package org.gudy.azureus2.ui.swt.minibar;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.DoubleBufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SelectableSpeedMenu;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
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
/*     */ public class AllTransfersBar
/*     */   extends MiniBar
/*     */ {
/*  54 */   private static MiniBarManager manager = new MiniBarManager("AllTransfersBar");
/*     */   private GlobalManager g_manager;
/*     */   private DoubleBufferedLabel down_speed;
/*     */   
/*  58 */   public static MiniBarManager getManager() { return manager; }
/*     */   
/*     */   public static AllTransfersBar getBarIfOpen(GlobalManager g_manager)
/*     */   {
/*  62 */     return (AllTransfersBar)manager.getMiniBarForObject(g_manager);
/*     */   }
/*     */   
/*     */   public static void open(Shell main) {
/*  66 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  68 */         GlobalManager g_manager = core.getGlobalManager();
/*  69 */         AllTransfersBar result = AllTransfersBar.getBarIfOpen(g_manager);
/*  70 */         if (result == null) {
/*  71 */           result = new AllTransfersBar(g_manager, this.val$main, null);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void closeAllTransfersBar() {
/*  78 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  80 */         GlobalManager g_manager = core.getGlobalManager();
/*  81 */         AllTransfersBar result = AllTransfersBar.getBarIfOpen(g_manager);
/*  82 */         if (result != null) {
/*  83 */           result.close();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private DoubleBufferedLabel up_speed;
/*     */   
/*     */   private DoubleBufferedLabel next_eta;
/*     */   private Label icon_label;
/*     */   private AllTransfersBar(GlobalManager gmanager, Shell main)
/*     */   {
/*  96 */     super(manager);
/*  97 */     this.g_manager = gmanager;
/*  98 */     construct(main);
/*     */   }
/*     */   
/* 101 */   public Object getContextObject() { return this.g_manager; }
/*     */   
/*     */   public void beginConstruction() {
/* 104 */     createFixedTextLabel("MinimizedWindow.all_transfers", false, true);
/* 105 */     createGap(40);
/*     */     
/*     */ 
/*     */ 
/* 109 */     Label dlab = createFixedTextLabel("ConfigView.download.abbreviated", false, false);
/* 110 */     this.down_speed = createSpeedLabel();
/*     */     
/* 112 */     final Menu downloadSpeedMenu = new Menu(getShell(), 8);
/*     */     
/* 114 */     downloadSpeedMenu.addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 116 */         if (AzureusCoreFactory.isCoreRunning()) {
/* 117 */           SelectableSpeedMenu.generateMenuItems(downloadSpeedMenu, AzureusCoreFactory.getSingleton(), AllTransfersBar.this.g_manager, false);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 123 */     });
/* 124 */     dlab.setMenu(downloadSpeedMenu);
/* 125 */     this.down_speed.setMenu(downloadSpeedMenu);
/*     */     
/*     */ 
/*     */ 
/* 129 */     Label ulab = createFixedTextLabel("ConfigView.upload.abbreviated", false, false);
/* 130 */     this.up_speed = createSpeedLabel();
/*     */     
/* 132 */     final Menu uploadSpeedMenu = new Menu(getShell(), 8);
/*     */     
/* 134 */     uploadSpeedMenu.addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 136 */         if (AzureusCoreFactory.isCoreRunning()) {
/* 137 */           SelectableSpeedMenu.generateMenuItems(uploadSpeedMenu, AzureusCoreFactory.getSingleton(), AllTransfersBar.this.g_manager, true);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 143 */     });
/* 144 */     ulab.setMenu(uploadSpeedMenu);
/* 145 */     this.up_speed.setMenu(uploadSpeedMenu);
/*     */     
/*     */ 
/*     */ 
/* 149 */     createFixedTextLabel("TableColumn.header.eta_next", true, false);
/* 150 */     this.next_eta = createDataLabel(65);
/*     */     
/*     */ 
/*     */ 
/* 154 */     if (COConfigurationManager.getBooleanParameter("Transfer Bar Show Icon Area"))
/*     */     {
/* 156 */       this.icon_label = createFixedLabel(16);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setIconImage(Image image)
/*     */   {
/* 164 */     if ((this.icon_label != null) && (image != this.icon_label.getImage()))
/*     */     {
/*     */ 
/* 167 */       this.icon_label.setImage(image);
/* 168 */       this.icon_label.pack();
/* 169 */       this.icon_label.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void doubleClick()
/*     */   {
/* 177 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 178 */     if (functionsSWT != null) {
/* 179 */       functionsSWT.bringToFront();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void buildMenu(Menu menu, MenuEvent menuEvent)
/*     */   {
/* 186 */     MenuItem start_all = new MenuItem(menu, 8);
/* 187 */     Messages.setLanguageText(start_all, "MainWindow.menu.transfers.startalltransfers");
/* 188 */     Utils.setMenuItemImage(start_all, "start");
/* 189 */     start_all.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {}
/*     */ 
/* 193 */     });
/* 194 */     start_all.setEnabled(true);
/*     */     
/*     */ 
/* 197 */     MenuItem stop_all = new MenuItem(menu, 8);
/* 198 */     Messages.setLanguageText(stop_all, "MainWindow.menu.transfers.stopalltransfers");
/* 199 */     Utils.setMenuItemImage(stop_all, "stop");
/* 200 */     stop_all.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {}
/*     */ 
/* 204 */     });
/* 205 */     stop_all.setEnabled(true);
/*     */     
/*     */ 
/* 208 */     MenuItem pause_all = new MenuItem(menu, 8);
/* 209 */     Messages.setLanguageText(pause_all, "MainWindow.menu.transfers.pausetransfers");
/* 210 */     Utils.setMenuItemImage(pause_all, "pause");
/* 211 */     pause_all.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {}
/*     */ 
/* 215 */     });
/* 216 */     pause_all.setEnabled(this.g_manager.canPauseDownloads());
/*     */     
/*     */ 
/* 219 */     MenuItem resume_all = new MenuItem(menu, 8);
/* 220 */     Messages.setLanguageText(resume_all, "MainWindow.menu.transfers.resumetransfers");
/* 221 */     Utils.setMenuItemImage(resume_all, "resume");
/* 222 */     resume_all.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {}
/*     */ 
/* 226 */     });
/* 227 */     resume_all.setEnabled(this.g_manager.canResumeDownloads());
/*     */     
/* 229 */     new MenuItem(menu, 2);
/* 230 */     super.buildMenu(menu);
/*     */   }
/*     */   
/*     */   protected void refresh0() {
/* 234 */     GlobalManagerStats stats = this.g_manager.getStats();
/* 235 */     updateSpeedLabel(this.down_speed, stats.getDataReceiveRate(), stats.getProtocolReceiveRate());
/* 236 */     updateSpeedLabel(this.up_speed, stats.getDataSendRate(), stats.getProtocolSendRate());
/*     */     
/* 238 */     long min_eta = Long.MAX_VALUE;
/* 239 */     int num_downloading = 0;
/*     */     
/* 241 */     List<DownloadManager> dms = this.g_manager.getDownloadManagers();
/* 242 */     for (DownloadManager dm : dms) {
/* 243 */       if (dm.getState() == 50)
/*     */       {
/* 245 */         num_downloading++;
/*     */         
/* 247 */         long eta = dm.getStats().getSmoothedETA();
/*     */         
/* 249 */         if (eta < min_eta)
/*     */         {
/* 251 */           min_eta = eta;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 256 */     if (min_eta == Long.MAX_VALUE)
/*     */     {
/* 258 */       min_eta = 1827387392L;
/*     */     }
/* 260 */     this.next_eta.setText(num_downloading == 0 ? "" : DisplayFormatters.formatETA(min_eta));
/*     */   }
/*     */   
/*     */   public String[] getPluginMenuIdentifiers(Object[] context)
/*     */   {
/* 265 */     if (context == null) {
/* 266 */       return null;
/*     */     }
/* 268 */     return new String[] { "transfersbar" };
/*     */   }
/*     */   
/*     */ 
/*     */   protected void storeLastLocation(Point location)
/*     */   {
/* 274 */     COConfigurationManager.setParameter("transferbar.x", location.x);
/* 275 */     COConfigurationManager.setParameter("transferbar.y", location.y);
/*     */   }
/*     */   
/*     */   protected Point getInitialLocation() {
/* 279 */     if (!COConfigurationManager.getBooleanParameter("Remember transfer bar location")) {
/* 280 */       return null;
/*     */     }
/* 282 */     if (!COConfigurationManager.hasParameter("transferbar.x", false)) {
/* 283 */       return null;
/*     */     }
/* 285 */     int x = COConfigurationManager.getIntParameter("transferbar.x");
/* 286 */     int y = COConfigurationManager.getIntParameter("transferbar.y");
/* 287 */     return new Point(x, y);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/minibar/AllTransfersBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */