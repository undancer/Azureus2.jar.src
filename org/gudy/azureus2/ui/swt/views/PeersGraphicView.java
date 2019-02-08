/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import java.net.InetAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.custom.CTabFolder;
/*      */ import org.eclipse.swt.custom.CTabItem;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*      */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.graphics.PieUtils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventListenerHolder;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PeersGraphicView
/*      */   implements UISWTViewCoreEventListener, UIPluginViewToolBarListener, UISWTViewCoreEventListenerEx
/*      */ {
/*   89 */   public static String MSGID_PREFIX = "PeersGraphicView";
/*      */   
/*      */   private UISWTView swtView;
/*      */   
/*      */   private static final int PEER_SIZE = 18;
/*      */   private static final int OWN_SIZE_DEFAULT = 75;
/*      */   private static final int OWN_SIZE_MIN = 30;
/*      */   private static final int OWN_SIZE_MAX = 75;
/*   97 */   private static int OWN_SIZE = 75;
/*      */   
/*      */   private static final int NB_ANGLES = 1000;
/*      */   
/*      */   private double perimeter;
/*      */   private double[] rs;
/*      */   private final double[] angles;
/*      */   private final double[] deltaXXs;
/*      */   private final double[] deltaXYs;
/*      */   private final double[] deltaYXs;
/*      */   private final double[] deltaYYs;
/*      */   private PeerComparator peerComparator;
/*      */   private Image my_flag;
/*      */   private Display display;
/*      */   private Composite panel;
/*      */   
/*      */   private static class PeerComparator
/*      */     implements Comparator<PEPeer>
/*      */   {
/*      */     public int compare(PEPeer peer0, PEPeer peer1)
/*      */     {
/*  118 */       int percent0 = peer0.getPercentDoneInThousandNotation();
/*  119 */       int percent1 = peer1.getPercentDoneInThousandNotation();
/*      */       
/*  121 */       int result = percent0 - percent1;
/*      */       
/*  123 */       if (result == 0)
/*      */       {
/*  125 */         long l = peer0.getTimeSinceConnectionEstablished() - peer1.getTimeSinceConnectionEstablished();
/*      */         
/*  127 */         if (l < 0L) {
/*  128 */           result = -1;
/*  129 */         } else if (l > 0L) {
/*  130 */           result = 1;
/*      */         }
/*      */       }
/*      */       
/*  134 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class ManagerData
/*      */     implements DownloadManagerPeerListener
/*      */   {
/*  152 */     private AEMonitor peers_mon = new AEMonitor("PeersGraphicView:peers");
/*      */     
/*      */     private DownloadManager manager;
/*      */     
/*      */     private Point oldSize;
/*      */     private List<PEPeer> peers;
/*  158 */     private Map<PEPeer, int[]> peer_hit_map = new HashMap();
/*      */     
/*      */     private int me_hit_x;
/*      */     
/*      */     private int me_hit_y;
/*      */     
/*      */     private ManagerData(DownloadManager _manager)
/*      */     {
/*  166 */       this.manager = _manager;
/*      */       
/*  168 */       this.peers = new ArrayList();
/*      */       
/*  170 */       this.manager.addPeerListener(this);
/*      */     }
/*      */     
/*      */ 
/*      */     private void delete()
/*      */     {
/*  176 */       this.manager.removePeerListener(this);
/*      */       
/*  178 */       this.peer_hit_map.clear();
/*      */     }
/*      */     
/*      */     public void peerManagerWillBeAdded(PEPeerManager peer_manager) {}
/*      */     
/*      */     public void peerManagerAdded(PEPeerManager manager) {}
/*      */     
/*      */     public void peerManagerRemoved(PEPeerManager manager) {}
/*      */     
/*  187 */     public void peerAdded(PEPeer peer) { try { this.peers_mon.enter();
/*  188 */         this.peers.add(peer);
/*      */       } finally {
/*  190 */         this.peers_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */     public void peerRemoved(PEPeer peer) {
/*      */       try {
/*  196 */         this.peers_mon.enter();
/*  197 */         this.peers.remove(peer);
/*      */       } finally {
/*  199 */         this.peers_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*  204 */   private Map<DownloadManager, ManagerData> dm_map = new IdentityHashMap();
/*      */   private boolean comp_focused;
/*      */   private Object focus_pending_ds;
/*      */   
/*      */   public PeersGraphicView()
/*      */   {
/*  210 */     this.angles = new double['Ϩ'];
/*      */     
/*  212 */     this.rs = new double['Ϩ'];
/*  213 */     this.deltaXXs = new double['Ϩ'];
/*  214 */     this.deltaXYs = new double['Ϩ'];
/*  215 */     this.deltaYXs = new double['Ϩ'];
/*  216 */     this.deltaYYs = new double['Ϩ'];
/*      */     
/*  218 */     for (int i = 0; i < 1000; i++) {
/*  219 */       this.angles[i] = (2 * i * 3.141592653589793D / 1000.0D - 3.141592653589793D);
/*  220 */       this.deltaXXs[i] = Math.cos(this.angles[i]);
/*  221 */       this.deltaXYs[i] = Math.sin(this.angles[i]);
/*  222 */       this.deltaYXs[i] = Math.cos(this.angles[i] + 1.5707963267948966D);
/*  223 */       this.deltaYYs[i] = Math.sin(this.angles[i] + 1.5707963267948966D);
/*      */     }
/*      */     
/*  226 */     this.peerComparator = new PeerComparator(null);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCloneable()
/*      */   {
/*  232 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public UISWTViewCoreEventListener getClone()
/*      */   {
/*  238 */     return new PeersGraphicView();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setFocused(boolean foc)
/*      */   {
/*  247 */     if (foc)
/*      */     {
/*  249 */       this.comp_focused = true;
/*      */       
/*  251 */       dataSourceChanged(this.focus_pending_ds);
/*      */     }
/*      */     else
/*      */     {
/*  255 */       synchronized (this.dm_map)
/*      */       {
/*  257 */         this.focus_pending_ds = this.dm_map.values().toArray(new DownloadManager[this.dm_map.size()]);
/*      */       }
/*      */       
/*  260 */       dataSourceChanged(null);
/*      */       
/*  262 */       this.comp_focused = false;
/*      */     }
/*      */   }
/*      */   
/*      */   private void dataSourceChanged(Object newDataSource) {
/*  267 */     if (!this.comp_focused) {
/*  268 */       this.focus_pending_ds = newDataSource;
/*  269 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  274 */     if (this.my_flag == null)
/*      */     {
/*  276 */       InetAddress ia = NetworkAdmin.getSingleton().getDefaultPublicAddress();
/*      */       
/*  278 */       if (ia != null)
/*      */       {
/*  280 */         this.my_flag = ImageRepository.getCountryFlag(ia, false);
/*      */       }
/*      */     }
/*      */     
/*  284 */     List<DownloadManager> newManagers = ViewUtils.getDownloadManagersFromDataSource(newDataSource);
/*      */     
/*  286 */     synchronized (this.dm_map)
/*      */     {
/*  288 */       List<DownloadManager> oldManagers = new ArrayList(this.dm_map.keySet());
/*      */       
/*  290 */       boolean changed = false;
/*      */       
/*  292 */       for (DownloadManager old : oldManagers)
/*      */       {
/*  294 */         if (!newManagers.contains(old))
/*      */         {
/*  296 */           ((ManagerData)this.dm_map.remove(old)).delete();
/*      */           
/*  298 */           changed = true;
/*      */         }
/*      */       }
/*  301 */       for (DownloadManager nu : newManagers)
/*      */       {
/*  303 */         if (!oldManagers.contains(nu))
/*      */         {
/*  305 */           this.dm_map.put(nu, new ManagerData(nu, null));
/*      */           
/*  307 */           changed = true;
/*      */         }
/*      */       }
/*      */       
/*  311 */       if (!changed)
/*      */       {
/*  313 */         return;
/*      */       }
/*      */       
/*  316 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  318 */           if (!PeersGraphicView.this.dm_map.isEmpty()) {
/*  319 */             Utils.disposeComposite(PeersGraphicView.this.panel, false);
/*      */           } else {
/*  321 */             ViewUtils.setViewRequiresOneDownload(PeersGraphicView.this.panel);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void delete()
/*      */   {
/*  331 */     synchronized (this.dm_map)
/*      */     {
/*  333 */       for (Map.Entry<DownloadManager, ManagerData> dm_entry : this.dm_map.entrySet())
/*      */       {
/*  335 */         ManagerData data = (ManagerData)dm_entry.getValue();
/*      */         
/*  337 */         data.delete();
/*      */       }
/*      */       
/*  340 */       this.dm_map.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   private Composite getComposite() {
/*  345 */     return this.panel;
/*      */   }
/*      */   
/*      */   private String getData() {
/*  349 */     return "PeersGraphicView.title.full";
/*      */   }
/*      */   
/*      */   private void initialize(Composite composite) {
/*  353 */     this.display = composite.getDisplay();
/*      */     
/*  355 */     this.panel = new Canvas(composite, 262144);
/*      */     
/*  357 */     this.panel.addListener(32, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/*  360 */         int x = event.x;
/*  361 */         int y = event.y;
/*      */         
/*  363 */         String tt = "";
/*      */         
/*  365 */         synchronized (PeersGraphicView.this.dm_map)
/*      */         {
/*  367 */           for (Map.Entry<DownloadManager, PeersGraphicView.ManagerData> dm_entry : PeersGraphicView.this.dm_map.entrySet())
/*      */           {
/*  369 */             DownloadManager manager = (DownloadManager)dm_entry.getKey();
/*  370 */             PeersGraphicView.ManagerData data = (PeersGraphicView.ManagerData)dm_entry.getValue();
/*      */             
/*  372 */             if ((x >= PeersGraphicView.ManagerData.access$500(data)) && (x <= PeersGraphicView.ManagerData.access$500(data) + PeersGraphicView.OWN_SIZE) && (y >= PeersGraphicView.ManagerData.access$700(data)) && (y <= PeersGraphicView.ManagerData.access$700(data) + PeersGraphicView.OWN_SIZE))
/*      */             {
/*      */ 
/*  375 */               if (PeersGraphicView.this.dm_map.size() > 1)
/*      */               {
/*  377 */                 tt = manager.getDisplayName() + "\r\n";
/*      */               }
/*      */               
/*  380 */               tt = tt + DisplayFormatters.formatDownloadStatus(manager) + ", " + DisplayFormatters.formatPercentFromThousands(manager.getStats().getCompleted());
/*      */               
/*      */ 
/*  383 */               break;
/*      */             }
/*      */             
/*      */ 
/*  387 */             PEPeer target = null;
/*      */             
/*  389 */             for (Map.Entry<PEPeer, int[]> entry : PeersGraphicView.ManagerData.access$800(data).entrySet())
/*      */             {
/*  391 */               int[] loc = (int[])entry.getValue();
/*      */               
/*  393 */               int loc_x = loc[0];
/*  394 */               int loc_y = loc[1];
/*      */               
/*  396 */               if ((x >= loc_x) && (x <= loc_x + 18) && (y >= loc_y) && (y <= loc_y + 18))
/*      */               {
/*      */ 
/*  399 */                 target = (PEPeer)entry.getKey();
/*      */                 
/*  401 */                 break;
/*      */               }
/*      */             }
/*      */             
/*  405 */             if (target != null)
/*      */             {
/*  407 */               PEPeerStats stats = target.getStats();
/*      */               
/*  409 */               String[] details = PeerUtils.getCountryDetails(target);
/*      */               
/*  411 */               String dstr = " - " + details[0] + "/" + details[1];
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  419 */               tt = target.getIp() + dstr + ", " + DisplayFormatters.formatPercentFromThousands(target.getPercentDoneInThousandNotation()) + "\r\n" + "Up=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getDataSendRate() + stats.getProtocolSendRate()) + ", " + "Down=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getDataReceiveRate() + stats.getProtocolReceiveRate());
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  424 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  430 */         PeersGraphicView.this.panel.setToolTipText(tt);
/*      */       }
/*      */       
/*  433 */     });
/*  434 */     this.panel.addMouseListener(new MouseAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void mouseUp(MouseEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  442 */         if (event.button == 3)
/*      */         {
/*  444 */           int x = event.x;
/*  445 */           int y = event.y;
/*      */           
/*  447 */           PEPeer target = null;
/*  448 */           DownloadManager target_manager = null;
/*      */           
/*  450 */           synchronized (PeersGraphicView.this.dm_map)
/*      */           {
/*  452 */             for (Map.Entry<DownloadManager, PeersGraphicView.ManagerData> dm_entry : PeersGraphicView.this.dm_map.entrySet())
/*      */             {
/*  454 */               DownloadManager manager = (DownloadManager)dm_entry.getKey();
/*  455 */               PeersGraphicView.ManagerData data = (PeersGraphicView.ManagerData)dm_entry.getValue();
/*      */               
/*  457 */               for (Map.Entry<PEPeer, int[]> entry : PeersGraphicView.ManagerData.access$800(data).entrySet())
/*      */               {
/*  459 */                 int[] loc = (int[])entry.getValue();
/*      */                 
/*  461 */                 int loc_x = loc[0];
/*  462 */                 int loc_y = loc[1];
/*      */                 
/*  464 */                 if ((x >= loc_x) && (x <= loc_x + 18) && (y >= loc_y) && (y <= loc_y + 18))
/*      */                 {
/*      */ 
/*  467 */                   target = (PEPeer)entry.getKey();
/*      */                   
/*  469 */                   target_manager = manager;
/*      */                   
/*  471 */                   break;
/*      */                 }
/*      */               }
/*      */               
/*  475 */               if (target != null) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  482 */           if (target == null)
/*      */           {
/*  484 */             return;
/*      */           }
/*      */           
/*  487 */           Menu menu = PeersGraphicView.this.panel.getMenu();
/*      */           
/*  489 */           if ((menu != null) && (!menu.isDisposed()))
/*      */           {
/*  491 */             menu.dispose();
/*      */           }
/*      */           
/*  494 */           menu = new Menu(PeersGraphicView.this.panel);
/*      */           
/*  496 */           PeersView.fillMenu(menu, target, target_manager);
/*      */           
/*  498 */           Point cursorLocation = Display.getCurrent().getCursorLocation();
/*      */           
/*  500 */           menu.setLocation(cursorLocation.x, cursorLocation.y);
/*      */           
/*  502 */           menu.setVisible(true);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void mouseDoubleClick(MouseEvent event)
/*      */       {
/*  510 */         int x = event.x;
/*  511 */         int y = event.y;
/*      */         DownloadManager manager;
/*  513 */         synchronized (PeersGraphicView.this.dm_map)
/*      */         {
/*  515 */           for (Map.Entry<DownloadManager, PeersGraphicView.ManagerData> dm_entry : PeersGraphicView.this.dm_map.entrySet())
/*      */           {
/*  517 */             manager = (DownloadManager)dm_entry.getKey();
/*  518 */             PeersGraphicView.ManagerData data = (PeersGraphicView.ManagerData)dm_entry.getValue();
/*      */             
/*  520 */             for (Map.Entry<PEPeer, int[]> entry : PeersGraphicView.ManagerData.access$800(data).entrySet())
/*      */             {
/*  522 */               int[] loc = (int[])entry.getValue();
/*      */               
/*  524 */               int loc_x = loc[0];
/*  525 */               int loc_y = loc[1];
/*      */               
/*  527 */               if ((x >= loc_x) && (x <= loc_x + 18) && (y >= loc_y) && (y <= loc_y + 18))
/*      */               {
/*      */ 
/*  530 */                 PEPeer target = (PEPeer)entry.getKey();
/*      */                 
/*      */ 
/*      */                 try
/*      */                 {
/*  535 */                   String dm_id = "DMDetails_" + Base32.encode(manager.getTorrent().getHash());
/*      */                   
/*  537 */                   MdiEntry mdi_entry = UIFunctionsManager.getUIFunctions().getMDI().getEntry(dm_id);
/*      */                   
/*  539 */                   if (mdi_entry != null)
/*      */                   {
/*  541 */                     mdi_entry.setDatasource(new Object[] { manager, target });
/*      */                   }
/*      */                   
/*  544 */                   Composite comp = PeersGraphicView.this.panel.getParent();
/*      */                   
/*  546 */                   while (comp != null)
/*      */                   {
/*  548 */                     if ((comp instanceof CTabFolder))
/*      */                     {
/*  550 */                       CTabFolder tf = (CTabFolder)comp;
/*      */                       
/*  552 */                       CTabItem[] items = tf.getItems();
/*      */                       
/*  554 */                       for (CTabItem item : items)
/*      */                       {
/*  556 */                         UISWTViewCore view = (UISWTViewCore)item.getData("TabbedEntry");
/*      */                         
/*  558 */                         UISWTViewEventListener listener = view.getEventListener();
/*      */                         
/*  560 */                         if ((listener instanceof UISWTViewEventListenerHolder))
/*      */                         {
/*  562 */                           listener = ((UISWTViewEventListenerHolder)listener).getDelegatedEventListener(view);
/*      */                         }
/*      */                         
/*  565 */                         if ((listener instanceof PeersView))
/*      */                         {
/*  567 */                           tf.setSelection(item);
/*      */                           
/*  569 */                           Event ev = new Event();
/*      */                           
/*  571 */                           ev.item = item;
/*      */                           
/*      */ 
/*      */ 
/*  575 */                           tf.notifyListeners(13, ev);
/*      */                           
/*  577 */                           ((PeersView)listener).selectPeer(target);
/*      */                           
/*  579 */                           return;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/*  584 */                     comp = comp.getParent();
/*      */                   }
/*      */                   
/*      */ 
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*  599 */     });
/*  600 */     this.panel.addPaintListener(new PaintListener()
/*      */     {
/*      */       public void paintControl(PaintEvent e) {
/*  603 */         PeersGraphicView.this.doRefresh();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void refresh() {
/*  609 */     doRefresh();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void doRefresh()
/*      */   {
/*  616 */     synchronized (this.dm_map)
/*      */     {
/*  618 */       if ((this.panel == null) || (this.panel.isDisposed())) {
/*  619 */         return;
/*      */       }
/*      */       
/*  622 */       if (this.dm_map.size() == 0) {
/*  623 */         GC gcPanel = new GC(this.panel);
/*  624 */         gcPanel.fillRectangle(this.panel.getBounds());
/*  625 */         gcPanel.dispose();
/*  626 */         return;
/*      */       }
/*      */       
/*  629 */       int num_dms = this.dm_map.size();
/*      */       
/*  631 */       Point panelSize = this.panel.getSize();
/*      */       
/*  633 */       int pw = panelSize.x;
/*  634 */       int ph = panelSize.y;
/*      */       
/*      */       int v_cells;
/*      */       
/*      */       int h_cells;
/*  639 */       if (ph <= pw)
/*      */       {
/*  641 */         int v_cells = 1;
/*  642 */         int h_cells = pw / ph;
/*      */         
/*  644 */         double f = Math.sqrt(num_dms / (v_cells * h_cells));
/*      */         
/*  646 */         int factor = (int)Math.ceil(f);
/*      */         
/*  648 */         h_cells *= factor;
/*  649 */         v_cells = factor;
/*      */       }
/*      */       else
/*      */       {
/*  653 */         v_cells = ph / pw;
/*  654 */         h_cells = 1;
/*      */         
/*      */ 
/*  657 */         double f = Math.sqrt(num_dms / (v_cells * h_cells));
/*      */         
/*  659 */         int factor = (int)Math.ceil(f);
/*      */         
/*  661 */         v_cells *= factor;
/*  662 */         h_cells = factor;
/*      */       }
/*      */       
/*  665 */       ph = h_cells == 1 ? ph / num_dms : ph / v_cells;
/*  666 */       pw = v_cells == 1 ? pw / num_dms : pw / h_cells;
/*      */       
/*      */ 
/*      */ 
/*  670 */       Point mySize = new Point(pw, ph);
/*      */       
/*  672 */       int num = 0;
/*      */       
/*  674 */       Point lastOffset = null;
/*      */       
/*  676 */       for (Map.Entry<DownloadManager, ManagerData> dm_entry : this.dm_map.entrySet())
/*      */       {
/*  678 */         DownloadManager manager = (DownloadManager)dm_entry.getKey();
/*  679 */         ManagerData data = (ManagerData)dm_entry.getValue();
/*      */         PEPeer[] sortedPeers;
/*      */         try
/*      */         {
/*  683 */           data.peers_mon.enter();
/*  684 */           List<PEPeerTransport> connectedPeers = new ArrayList();
/*  685 */           for (PEPeer peer : data.peers) {
/*  686 */             if ((peer instanceof PEPeerTransport)) {
/*  687 */               PEPeerTransport peerTransport = (PEPeerTransport)peer;
/*  688 */               if (peerTransport.getConnectionState() == 4) {
/*  689 */                 connectedPeers.add(peerTransport);
/*      */               }
/*      */             }
/*      */           }
/*  693 */           sortedPeers = (PEPeer[])connectedPeers.toArray(new PEPeer[connectedPeers.size()]);
/*      */         } finally {
/*  695 */           data.peers_mon.exit();
/*      */         }
/*      */         
/*  698 */         if (sortedPeers == null) { return;
/*      */         }
/*  700 */         for (int i = 0; i < 3; i++) {
/*      */           try
/*      */           {
/*  703 */             Arrays.sort(sortedPeers, this.peerComparator);
/*      */           }
/*      */           catch (IllegalArgumentException e) {}
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  713 */         int h = num % h_cells;
/*  714 */         int v = num / h_cells;
/*      */         
/*  716 */         Point myOffset = new Point(h * pw, v * ph);
/*      */         
/*  718 */         render(manager, data, sortedPeers, mySize, myOffset);
/*      */         
/*  720 */         num++;
/*      */         
/*  722 */         lastOffset = myOffset;
/*      */       }
/*      */       
/*  725 */       int rem_x = panelSize.x - (lastOffset.x + mySize.x);
/*      */       
/*  727 */       if (rem_x > 0) {
/*  728 */         GC gcPanel = new GC(this.panel);
/*  729 */         gcPanel.setBackground(Colors.white);
/*  730 */         gcPanel.fillRectangle(lastOffset.x + mySize.x, lastOffset.y, rem_x, mySize.y);
/*  731 */         gcPanel.dispose();
/*      */       }
/*      */       
/*  734 */       int rem_y = panelSize.y - (lastOffset.y + mySize.y);
/*      */       
/*  736 */       if (rem_y > 0) {
/*  737 */         GC gcPanel = new GC(this.panel);
/*  738 */         gcPanel.setBackground(Colors.white);
/*  739 */         gcPanel.fillRectangle(0, lastOffset.y + mySize.y, panelSize.x, rem_y);
/*  740 */         gcPanel.dispose();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void render(DownloadManager manager, ManagerData data, PEPeer[] sortedPeers, Point panelSize, Point panelOffset)
/*      */   {
/*  753 */     data.peer_hit_map.clear();
/*      */     
/*  755 */     int min_dim = Math.min(panelSize.x, panelSize.y);
/*      */     
/*  757 */     if (min_dim <= 100) {
/*  758 */       OWN_SIZE = 30;
/*  759 */     } else if (min_dim >= 400) {
/*  760 */       OWN_SIZE = 75;
/*      */     } else {
/*  762 */       int s_diff = 45;
/*  763 */       float rat = (min_dim - 100.0F) / 300.0F;
/*      */       
/*  765 */       OWN_SIZE = 30 + (int)(s_diff * rat);
/*      */     }
/*      */     
/*      */ 
/*  769 */     int x0 = panelSize.x / 2;
/*  770 */     int y0 = panelSize.y / 2;
/*  771 */     int a = x0 - 20;
/*  772 */     int b = y0 - 20;
/*  773 */     if ((a < 10) || (b < 10)) {
/*  774 */       GC gcPanel = new GC(this.panel);
/*  775 */       gcPanel.setBackground(Colors.white);
/*  776 */       gcPanel.fillRectangle(panelOffset.x, panelOffset.y, panelSize.x, panelSize.y);
/*  777 */       gcPanel.dispose();
/*  778 */       return;
/*      */     }
/*      */     
/*  781 */     if ((data.oldSize == null) || (!data.oldSize.equals(panelSize))) {
/*  782 */       data.oldSize = panelSize;
/*  783 */       this.perimeter = 0.0D;
/*  784 */       for (int i = 0; i < 1000; i++) {
/*  785 */         this.rs[i] = Math.sqrt(1.0D / (this.deltaYXs[i] * this.deltaYXs[i] / (a * a) + this.deltaYYs[i] * this.deltaYYs[i] / (b * b)));
/*  786 */         this.perimeter += this.rs[i];
/*      */       }
/*      */     }
/*  789 */     Image buffer = new Image(this.display, panelSize.x, panelSize.y);
/*  790 */     GC gcBuffer = new GC(buffer);
/*  791 */     gcBuffer.setBackground(Colors.white);
/*  792 */     gcBuffer.setForeground(Colors.blue);
/*  793 */     gcBuffer.fillRectangle(0, 0, panelSize.x, panelSize.y);
/*      */     try
/*      */     {
/*  796 */       gcBuffer.setTextAntialias(1);
/*  797 */       gcBuffer.setAntialias(1);
/*      */     }
/*      */     catch (Exception e) {}
/*      */     
/*  801 */     gcBuffer.setBackground(Colors.blues[2]);
/*      */     
/*  803 */     int nbPeers = sortedPeers.length;
/*      */     
/*  805 */     int iAngle = 0;
/*  806 */     double currentPerimeter = 0.0D;
/*      */     
/*      */ 
/*      */ 
/*  810 */     for (int i = 0; i < nbPeers; i++) {
/*  811 */       PEPeer peer = sortedPeers[i];
/*      */       double r;
/*      */       do {
/*  814 */         r = this.rs[iAngle];
/*  815 */         currentPerimeter += r;
/*  816 */         if (iAngle + 1 < 1000) iAngle++;
/*  817 */       } while (currentPerimeter < i * this.perimeter / nbPeers);
/*      */       
/*      */ 
/*      */ 
/*  821 */       int[] triangle = new int[6];
/*      */       
/*      */ 
/*  824 */       int percent_received = peer.getPercentDoneOfCurrentIncomingRequest();
/*  825 */       int percent_sent = peer.getPercentDoneOfCurrentOutgoingRequest();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  830 */       boolean drawLine = false;
/*      */       
/*      */ 
/*      */ 
/*  834 */       if ((!peer.isChokingMe()) || (percent_received >= 0)) {
/*  835 */         gcBuffer.setForeground(Colors.blues[1]);
/*  836 */         drawLine = true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  841 */       if ((!peer.isChokedByMe()) || (percent_sent >= 0)) {
/*  842 */         gcBuffer.setForeground(Colors.blues[3]);
/*  843 */         drawLine = true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  848 */       if ((!peer.isChokingMe()) && (peer.isUnchokeOverride()) && (peer.isInteresting())) {
/*  849 */         gcBuffer.setForeground(Colors.green);
/*  850 */         drawLine = true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  855 */       if ((peer.isChokedByMe()) && (percent_sent >= 0)) {
/*  856 */         gcBuffer.setForeground(Colors.green);
/*  857 */         drawLine = true;
/*      */       }
/*      */       
/*  860 */       if (drawLine) {
/*  861 */         int x1 = x0 + (int)(r * this.deltaYXs[iAngle]);
/*  862 */         int y1 = y0 + (int)(r * this.deltaYYs[iAngle]);
/*  863 */         gcBuffer.drawLine(x0, y0, x1, y1);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  868 */       if (percent_received >= 0) {
/*  869 */         gcBuffer.setBackground(Colors.blues[7]);
/*  870 */         double r1 = r - r * percent_received / 100.0D;
/*  871 */         triangle[0] = ((int)(x0 + (r1 - 10.0D) * this.deltaYXs[iAngle] + 0.5D));
/*  872 */         triangle[1] = ((int)(y0 + (r1 - 10.0D) * this.deltaYYs[iAngle] + 0.5D));
/*      */         
/*  874 */         triangle[2] = ((int)(x0 + this.deltaXXs[iAngle] * 4.0D + r1 * this.deltaYXs[iAngle] + 0.5D));
/*  875 */         triangle[3] = ((int)(y0 + this.deltaXYs[iAngle] * 4.0D + r1 * this.deltaYYs[iAngle] + 0.5D));
/*      */         
/*      */ 
/*  878 */         triangle[4] = ((int)(x0 - this.deltaXXs[iAngle] * 4.0D + r1 * this.deltaYXs[iAngle] + 0.5D));
/*  879 */         triangle[5] = ((int)(y0 - this.deltaXYs[iAngle] * 4.0D + r1 * this.deltaYYs[iAngle] + 0.5D));
/*      */         
/*  881 */         gcBuffer.fillPolygon(triangle);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  886 */       if (percent_sent >= 0) {
/*  887 */         gcBuffer.setBackground(Colors.blues[2]);
/*  888 */         double r1 = r * percent_sent / 100.0D;
/*  889 */         triangle[0] = ((int)(x0 + r1 * this.deltaYXs[iAngle] + 0.5D));
/*  890 */         triangle[1] = ((int)(y0 + r1 * this.deltaYYs[iAngle] + 0.5D));
/*      */         
/*  892 */         triangle[2] = ((int)(x0 + this.deltaXXs[iAngle] * 4.0D + (r1 - 10.0D) * this.deltaYXs[iAngle] + 0.5D));
/*  893 */         triangle[3] = ((int)(y0 + this.deltaXYs[iAngle] * 4.0D + (r1 - 10.0D) * this.deltaYYs[iAngle] + 0.5D));
/*      */         
/*      */ 
/*  896 */         triangle[4] = ((int)(x0 - this.deltaXXs[iAngle] * 4.0D + (r1 - 10.0D) * this.deltaYXs[iAngle] + 0.5D));
/*  897 */         triangle[5] = ((int)(y0 - this.deltaXYs[iAngle] * 4.0D + (r1 - 10.0D) * this.deltaYYs[iAngle] + 0.5D));
/*  898 */         gcBuffer.fillPolygon(triangle);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  903 */       int x1 = x0 + (int)(r * this.deltaYXs[iAngle]);
/*  904 */       int y1 = y0 + (int)(r * this.deltaYYs[iAngle]);
/*  905 */       gcBuffer.setBackground(Colors.blues[7]);
/*  906 */       if (peer.isSnubbed()) {
/*  907 */         gcBuffer.setBackground(Colors.grey);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  920 */       int peer_x = x1 - 9;
/*  921 */       int peer_y = y1 - 9;
/*      */       
/*  923 */       data.peer_hit_map.put(peer, new int[] { peer_x + panelOffset.x, peer_y + panelOffset.y });
/*      */       
/*  925 */       Image flag = ImageRepository.getCountryFlag(peer, false);
/*  926 */       if (flag != null) {
/*  927 */         PieUtils.drawPie(gcBuffer, flag, peer_x, peer_y, 18, 18, peer.getPercentDoneInThousandNotation() / 10, true);
/*      */       }
/*      */       else {
/*  930 */         PieUtils.drawPie(gcBuffer, peer_x, peer_y, 18, 18, peer.getPercentDoneInThousandNotation() / 10);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  935 */     gcBuffer.setBackground(Colors.blues[7]);
/*      */     
/*  937 */     data.me_hit_x = (x0 - OWN_SIZE / 2);
/*  938 */     data.me_hit_y = (y0 - OWN_SIZE / 2);
/*      */     
/*  940 */     PieUtils.drawPie(gcBuffer, data.me_hit_x, data.me_hit_y, OWN_SIZE, OWN_SIZE, manager.getStats().getCompleted() / 10);
/*      */     
/*  942 */     if (this.my_flag != null) {
/*  943 */       PieUtils.drawPie(gcBuffer, this.my_flag, data.me_hit_x, data.me_hit_y, OWN_SIZE, OWN_SIZE, manager.getStats().getCompleted() / 10, false);
/*      */     }
/*      */     
/*  946 */     ManagerData.access$512(data, panelOffset.x);
/*  947 */     ManagerData.access$712(data, panelOffset.y);
/*      */     
/*  949 */     gcBuffer.dispose();
/*  950 */     GC gcPanel = new GC(this.panel);
/*  951 */     gcPanel.drawImage(buffer, panelOffset.x, panelOffset.y);
/*  952 */     gcPanel.dispose();
/*  953 */     buffer.dispose();
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event)
/*      */   {
/*  958 */     switch (event.getType()) {
/*      */     case 0: 
/*  960 */       this.swtView = event.getView();
/*  961 */       this.swtView.setTitle(MessageText.getString(getData()));
/*  962 */       this.swtView.setToolBarListener(this);
/*  963 */       break;
/*      */     
/*      */     case 7: 
/*  966 */       delete();
/*  967 */       break;
/*      */     
/*      */     case 2: 
/*  970 */       initialize((Composite)event.getData());
/*  971 */       break;
/*      */     
/*      */     case 6: 
/*  974 */       Messages.updateLanguageForControl(getComposite());
/*  975 */       this.swtView.setTitle(MessageText.getString(getData()));
/*  976 */       break;
/*      */     
/*      */     case 1: 
/*  979 */       dataSourceChanged(event.getData());
/*  980 */       break;
/*      */     
/*      */     case 3: 
/*  983 */       String id = "DMDetails_Swarm";
/*      */       
/*  985 */       setFocused(true);
/*  986 */       synchronized (this.dm_map)
/*      */       {
/*  988 */         if (this.dm_map.isEmpty())
/*      */         {
/*  990 */           SelectedContentManager.changeCurrentlySelectedContent(id, null);
/*      */         }
/*      */         else
/*      */         {
/*  994 */           DownloadManager manager = (DownloadManager)this.dm_map.keySet().iterator().next();
/*      */           
/*  996 */           if (manager.getTorrent() != null) {
/*  997 */             id = id + "." + manager.getInternalName();
/*      */           } else {
/*  999 */             id = id + ":" + manager.getSize();
/*      */           }
/*      */           
/* 1002 */           SelectedContentManager.changeCurrentlySelectedContent(id, new SelectedContent[] { new SelectedContent(manager) });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1008 */       break;
/*      */     case 4: 
/* 1010 */       setFocused(false);
/* 1011 */       SelectedContentManager.clearCurrentlySelectedContent();
/* 1012 */       break;
/*      */     case 5: 
/* 1014 */       refresh();
/*      */     }
/*      */     
/*      */     
/* 1018 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*      */   {
/* 1026 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void refreshToolBarItems(Map<String, Long> list)
/*      */   {
/* 1033 */     Map<String, Long> states = TorrentUtil.calculateToolbarStates(SelectedContentManager.getCurrentlySelectedContent(), null);
/*      */     
/* 1035 */     list.putAll(states);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/PeersGraphicView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */