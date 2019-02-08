/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Display;
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
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ListenerNeedingCoreRunning;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
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
/*     */ public class TrayWindow
/*     */   implements GlobalManagerListener, UIUpdatable
/*     */ {
/*     */   private static final String ID = "DownloadBasket/TrayWindow";
/*     */   GlobalManager globalManager;
/*     */   List managers;
/*  62 */   protected AEMonitor managers_mon = new AEMonitor("DownloadBasket/TrayWindow");
/*     */   
/*     */   Display display;
/*     */   
/*     */   Shell minimized;
/*     */   
/*     */   Label label;
/*     */   private Menu menu;
/*     */   private Rectangle screen;
/*     */   private int xPressed;
/*     */   private int yPressed;
/*     */   private boolean moving;
/*     */   
/*     */   public TrayWindow()
/*     */   {
/*  77 */     this.managers = new ArrayList();
/*  78 */     UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  79 */     Shell mainShell = uif == null ? Utils.findAnyShell() : uif.getMainShell();
/*  80 */     this.display = mainShell.getDisplay();
/*  81 */     this.minimized = ShellFactory.createShell(mainShell, 16384);
/*  82 */     this.minimized.setText("Vuze");
/*  83 */     this.label = new Label(this.minimized, 0);
/*  84 */     ImageLoader.getInstance().setLabelImage(this.label, "tray");
/*  85 */     final Rectangle bounds = this.label.getImage().getBounds();
/*  86 */     this.label.setSize(bounds.width, bounds.height);
/*  87 */     this.minimized.setSize(bounds.width + 2, bounds.height + 2);
/*  88 */     this.screen = this.display.getClientArea();
/*     */     
/*  90 */     if (!Constants.isOSX) {
/*  91 */       this.minimized.setLocation(this.screen.x + this.screen.width - bounds.width - 2, this.screen.y + this.screen.height - bounds.height - 2);
/*     */     }
/*     */     else {
/*  94 */       this.minimized.setLocation(20, 20);
/*     */     }
/*  96 */     this.minimized.layout();
/*  97 */     this.minimized.setVisible(false);
/*     */     
/*     */ 
/* 100 */     MouseListener mListener = new MouseAdapter() {
/*     */       public void mouseDown(MouseEvent e) {
/* 102 */         TrayWindow.this.xPressed = e.x;
/* 103 */         TrayWindow.this.yPressed = e.y;
/* 104 */         TrayWindow.this.moving = true;
/*     */       }
/*     */       
/*     */       public void mouseUp(MouseEvent e)
/*     */       {
/* 109 */         TrayWindow.this.moving = false;
/*     */       }
/*     */       
/*     */       public void mouseDoubleClick(MouseEvent e) {
/* 113 */         TrayWindow.this.restore();
/*     */       }
/*     */       
/* 116 */     };
/* 117 */     MouseMoveListener mMoveListener = new MouseMoveListener() {
/*     */       public void mouseMove(MouseEvent e) {
/* 119 */         if (TrayWindow.this.moving) {
/* 120 */           int dX = TrayWindow.this.xPressed - e.x;
/* 121 */           int dY = TrayWindow.this.yPressed - e.y;
/* 122 */           Point currentLoc = TrayWindow.this.minimized.getLocation();
/* 123 */           int x = currentLoc.x - dX;
/* 124 */           int y = currentLoc.y - dY;
/* 125 */           if (x < 10)
/* 126 */             x = 0;
/* 127 */           if (x > TrayWindow.this.screen.width - (bounds.width + 12))
/* 128 */             x = TrayWindow.this.screen.width - (bounds.width + 2);
/* 129 */           if (y < 10)
/* 130 */             y = 0;
/* 131 */           if (y > TrayWindow.this.screen.height - (bounds.height + 12))
/* 132 */             y = TrayWindow.this.screen.height - (bounds.height + 2);
/* 133 */           TrayWindow.this.minimized.setLocation(x, y);
/*     */         }
/*     */         
/*     */       }
/* 137 */     };
/* 138 */     this.label.addMouseListener(mListener);
/* 139 */     this.label.addMouseMoveListener(mMoveListener);
/*     */     
/* 141 */     this.menu = new Menu(this.minimized, 64);
/* 142 */     this.label.setMenu(this.menu);
/*     */     
/* 144 */     MenuItem file_show = new MenuItem(this.menu, 0);
/* 145 */     Messages.setLanguageText(file_show, "TrayWindow.menu.show");
/* 146 */     this.menu.setDefaultItem(file_show);
/* 147 */     file_show.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 149 */         TrayWindow.this.restore();
/*     */       }
/*     */       
/* 152 */     });
/* 153 */     new MenuItem(this.menu, 2);
/*     */     
/* 155 */     MenuFactory.addCloseDownloadBarsToMenu(this.menu);
/*     */     
/* 157 */     new MenuItem(this.menu, 2);
/*     */     
/* 159 */     MenuItem file_startalldownloads = new MenuItem(this.menu, 0);
/* 160 */     Messages.setLanguageText(file_startalldownloads, "TrayWindow.menu.startalldownloads");
/* 161 */     file_startalldownloads.addListener(13, new ListenerNeedingCoreRunning()
/*     */     {
/*     */       public void handleEvent(AzureusCore core, Event e) {
/* 164 */         TrayWindow.this.globalManager.startAllDownloads();
/*     */       }
/*     */       
/* 167 */     });
/* 168 */     MenuItem file_stopalldownloads = new MenuItem(this.menu, 0);
/* 169 */     Messages.setLanguageText(file_stopalldownloads, "TrayWindow.menu.stopalldownloads");
/* 170 */     file_stopalldownloads.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event e) {}
/*     */ 
/* 175 */     });
/* 176 */     new MenuItem(this.menu, 2);
/*     */     
/* 178 */     MenuItem file_close = new MenuItem(this.menu, 0);
/* 179 */     Messages.setLanguageText(file_close, "TrayWindow.menu.close");
/* 180 */     file_close.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 182 */         COConfigurationManager.setParameter("Show Download Basket", false);
/*     */       }
/*     */       
/* 185 */     });
/* 186 */     MenuItem file_exit = new MenuItem(this.menu, 0);
/* 187 */     Messages.setLanguageText(file_exit, "TrayWindow.menu.exit");
/* 188 */     file_exit.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 190 */         UIFunctionsManager.getUIFunctions().dispose(false, false);
/*     */       }
/*     */       
/* 193 */     });
/* 194 */     Utils.createTorrentDropTarget(this.minimized, false);
/*     */     try {
/* 196 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */       {
/*     */         public void azureusCoreRunning(AzureusCore core) {
/* 199 */           TrayWindow.this.globalManager = core.getGlobalManager();
/* 200 */           TrayWindow.this.globalManager.addListener(TrayWindow.this);
/*     */         }
/*     */       });
/*     */     } catch (Exception e) {
/* 204 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible) {
/* 209 */     if ((visible) || (!COConfigurationManager.getBooleanParameter("Show Download Basket"))) {
/* 210 */       this.minimized.setVisible(visible);
/* 211 */       if (!visible) {
/* 212 */         this.moving = false;
/*     */       }
/*     */     }
/*     */     try {
/* 216 */       if (visible) {
/* 217 */         UIFunctionsManager.getUIFunctions().getUIUpdater().addUpdater(this);
/*     */       } else {
/* 219 */         UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(this);
/*     */       }
/*     */     } catch (Exception e) {
/* 222 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 227 */     this.minimized.dispose();
/*     */   }
/*     */   
/*     */   public void restore() {
/* 231 */     if (!COConfigurationManager.getBooleanParameter("Show Download Basket"))
/* 232 */       this.minimized.setVisible(false);
/* 233 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 234 */     if (functionsSWT != null) {
/* 235 */       functionsSWT.bringToFront();
/*     */     }
/* 237 */     this.moving = false;
/*     */   }
/*     */   
/*     */   public void updateUI()
/*     */   {
/* 242 */     if ((this.minimized.isDisposed()) || (!this.minimized.isVisible())) {
/* 243 */       return;
/*     */     }
/* 245 */     StringBuilder toolTip = new StringBuilder();
/* 246 */     String separator = "";
/*     */     try {
/* 248 */       this.managers_mon.enter();
/* 249 */       for (int i = 0; i < this.managers.size(); i++) {
/* 250 */         DownloadManager manager = (DownloadManager)this.managers.get(i);
/* 251 */         DownloadManagerStats stats = manager.getStats();
/*     */         
/* 253 */         String name = manager.getDisplayName();
/* 254 */         String completed = DisplayFormatters.formatPercentFromThousands(stats.getPercentDoneExcludingDND());
/* 255 */         toolTip.append(separator);
/* 256 */         toolTip.append(name);
/* 257 */         toolTip.append(" -- C: ");
/* 258 */         toolTip.append(completed);
/* 259 */         toolTip.append(", D : ");
/* 260 */         toolTip.append(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(stats.getDataReceiveRate(), stats.getProtocolReceiveRate()));
/*     */         
/* 262 */         toolTip.append(", U : ");
/* 263 */         toolTip.append(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(stats.getDataSendRate(), stats.getProtocolSendRate()));
/*     */         
/* 265 */         separator = "\n";
/*     */       }
/*     */     } finally {
/* 268 */       this.managers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void downloadManagerAdded(DownloadManager created)
/*     */   {
/*     */     try
/*     */     {
/* 276 */       this.managers_mon.enter();
/*     */       
/* 278 */       this.managers.add(created);
/*     */     }
/*     */     finally {
/* 281 */       this.managers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void downloadManagerRemoved(DownloadManager removed) {
/*     */     try {
/* 287 */       this.managers_mon.enter();
/*     */       
/* 289 */       this.managers.remove(removed);
/*     */     } finally {
/* 291 */       this.managers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroyed() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroyInitiated() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateLanguage()
/*     */   {
/* 311 */     MenuFactory.updateMenuText(this.menu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMoving(boolean moving)
/*     */   {
/* 318 */     this.moving = moving;
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 323 */     return "DownloadBasket/TrayWindow";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/TrayWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */