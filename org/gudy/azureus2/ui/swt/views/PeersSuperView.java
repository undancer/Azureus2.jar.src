/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*     */ import org.gudy.azureus2.ui.swt.views.peer.PeerInfoView;
/*     */ import org.gudy.azureus2.ui.swt.views.peer.RemotePieceDistributionView;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.peers.DownloadNameItem;
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
/*     */ public class PeersSuperView
/*     */   extends TableViewTab<PEPeer>
/*     */   implements GlobalManagerListener, DownloadManagerPeerListener, TableLifeCycleListener, TableViewSWTMenuFillListener, UISWTViewCoreEventListenerEx
/*     */ {
/*     */   public static final String VIEW_ID = "AllPeersView";
/*     */   private TableViewSWT<PEPeer> tv;
/*     */   private Shell shell;
/*  78 */   private boolean active_listener = true;
/*     */   
/*  80 */   protected static boolean registeredCoreSubViews = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeersSuperView()
/*     */   {
/*  88 */     super("AllPeersView");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/*  94 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/* 100 */     return new PeersSuperView();
/*     */   }
/*     */   
/*     */ 
/*     */   public TableViewSWT<PEPeer> initYourTableView()
/*     */   {
/* 106 */     TableColumnCore[] items = PeersView.getBasicColumnItems("AllPeers");
/* 107 */     TableColumnCore[] basicItems = new TableColumnCore[items.length + 1];
/* 108 */     System.arraycopy(items, 0, basicItems, 0, items.length);
/* 109 */     basicItems[items.length] = new DownloadNameItem("AllPeers");
/*     */     
/* 111 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*     */     
/* 113 */     tcManager.setDefaultColumnNames("AllPeers", basicItems);
/*     */     
/*     */ 
/* 116 */     this.tv = TableViewFactory.createTableViewSWT(Peer.class, "AllPeers", getPropertiesPrefix(), basicItems, "connected_time", 268500994);
/*     */     
/*     */ 
/* 119 */     this.tv.setRowDefaultHeightEM(1.0F);
/* 120 */     this.tv.setEnableTabViews(true, true, null);
/*     */     
/* 122 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 123 */     if (uiFunctions != null) {
/* 124 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 126 */       if ((pluginUI != null) && (!registeredCoreSubViews))
/*     */       {
/* 128 */         pluginUI.addView("AllPeers", "PeerInfoView", PeerInfoView.class, null);
/*     */         
/* 130 */         pluginUI.addView("AllPeers", "RemotePieceDistributionView", RemotePieceDistributionView.class, null);
/*     */         
/*     */ 
/* 133 */         pluginUI.addView("AllPeers", "LoggerView", LoggerView.class, Boolean.valueOf(true));
/*     */         
/*     */ 
/* 136 */         registeredCoreSubViews = true;
/*     */       }
/*     */     }
/*     */     
/* 140 */     this.tv.addLifeCycleListener(this);
/* 141 */     this.tv.addMenuFillListener(this);
/*     */     
/* 143 */     return this.tv;
/*     */   }
/*     */   
/*     */   public void tableViewInitialized()
/*     */   {
/* 148 */     this.shell = this.tv.getComposite().getShell();
/* 149 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 152 */         PeersSuperView.this.registerGlobalManagerListener(core);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void tableViewDestroyed() {
/* 158 */     unregisterListeners();
/*     */   }
/*     */   
/*     */   public void fillMenu(String sColumnName, Menu menu) {
/* 162 */     PeersView.fillMenu(menu, this.tv, this.shell, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public void peerAdded(PEPeer created)
/*     */   {
/* 168 */     this.tv.addDataSource(created);
/*     */   }
/*     */   
/*     */   public void peerRemoved(PEPeer removed) {
/* 172 */     this.tv.removeDataSource(removed);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addExistingDatasources(AzureusCore core)
/*     */   {
/* 181 */     if (this.tv.isDisposed()) {
/* 182 */       return;
/*     */     }
/*     */     
/* 185 */     ArrayList<PEPeer> sources = new ArrayList();
/* 186 */     Iterator<?> itr = core.getGlobalManager().getDownloadManagers().iterator();
/* 187 */     while (itr.hasNext()) {
/* 188 */       PEPeer[] peers = ((DownloadManager)itr.next()).getCurrentPeers();
/* 189 */       if (peers != null) {
/* 190 */         sources.addAll(Arrays.asList(peers));
/*     */       }
/*     */     }
/* 193 */     if (sources.isEmpty()) {
/* 194 */       return;
/*     */     }
/*     */     
/* 197 */     this.tv.addDataSources(sources.toArray(new PEPeer[sources.size()]));
/* 198 */     this.tv.processDataSourceQueue();
/*     */   }
/*     */   
/*     */   private void registerGlobalManagerListener(AzureusCore core) {
/* 202 */     this.active_listener = false;
/*     */     try {
/* 204 */       core.getGlobalManager().addListener(this);
/*     */     } finally {
/* 206 */       this.active_listener = true;
/*     */     }
/* 208 */     addExistingDatasources(core);
/*     */   }
/*     */   
/*     */   private void unregisterListeners() {
/*     */     try {
/* 213 */       GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 214 */       gm.removeListener(this);
/* 215 */       Iterator<?> itr = gm.getDownloadManagers().iterator();
/* 216 */       while (itr.hasNext()) {
/* 217 */         DownloadManager dm = (DownloadManager)itr.next();
/* 218 */         downloadManagerRemoved(dm);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */   public void downloadManagerAdded(DownloadManager dm) {
/* 225 */     dm.addPeerListener(this, !this.active_listener);
/*     */   }
/*     */   
/* 228 */   public void downloadManagerRemoved(DownloadManager dm) { dm.removePeerListener(this); }
/*     */   
/*     */   public void destroyInitiated() {}
/*     */   
/*     */   public void destroyed() {}
/*     */   
/*     */   public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*     */   
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */   
/*     */   public void peerManagerAdded(PEPeerManager manager) {}
/*     */   
/*     */   public void peerManagerRemoved(PEPeerManager manager) {}
/*     */   
/*     */   public void peerManagerWillBeAdded(PEPeerManager manager) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/PeersSuperView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */