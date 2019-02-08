/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagListener;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
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
/*     */ public class PeersGeneralView
/*     */   extends TableViewTab<PEPeer>
/*     */   implements TagListener, TableLifeCycleListener, TableViewSWTMenuFillListener, UISWTViewCoreEventListenerEx
/*     */ {
/*     */   private TableViewSWT<PEPeer> tv;
/*     */   private Shell shell;
/*     */   private Tag tag;
/*     */   
/*     */   public PeersGeneralView(Tag _tag)
/*     */   {
/*  66 */     super("AllPeersView");
/*     */     
/*  68 */     this.tag = _tag;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/*  74 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/*  80 */     return new PeersGeneralView(this.tag);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFullTitle()
/*     */   {
/*  86 */     return this.tag.getTagName(true);
/*     */   }
/*     */   
/*     */ 
/*     */   public TableViewSWT<PEPeer> initYourTableView()
/*     */   {
/*  92 */     TableColumnCore[] items = PeersView.getBasicColumnItems("AllPeers");
/*  93 */     TableColumnCore[] basicItems = new TableColumnCore[items.length + 1];
/*  94 */     System.arraycopy(items, 0, basicItems, 0, items.length);
/*  95 */     basicItems[items.length] = new DownloadNameItem("AllPeers");
/*     */     
/*  97 */     this.tv = TableViewFactory.createTableViewSWT(Peer.class, "AllPeers", getPropertiesPrefix(), basicItems, "connected_time", 268500994);
/*     */     
/*     */ 
/*     */ 
/* 101 */     this.tv.setRowDefaultHeightEM(1.0F);
/* 102 */     this.tv.setEnableTabViews(true, true, null);
/*     */     
/* 104 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */     
/* 106 */     if (uiFunctions != null)
/*     */     {
/* 108 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 110 */       if ((pluginUI != null) && (!PeersSuperView.registeredCoreSubViews))
/*     */       {
/* 112 */         pluginUI.addView("AllPeers", "PeerInfoView", PeerInfoView.class, null);
/*     */         
/* 114 */         pluginUI.addView("AllPeers", "RemotePieceDistributionView", RemotePieceDistributionView.class, null);
/*     */         
/*     */ 
/* 117 */         pluginUI.addView("AllPeers", "LoggerView", LoggerView.class, Boolean.valueOf(true));
/*     */         
/*     */ 
/* 120 */         PeersSuperView.registeredCoreSubViews = true;
/*     */       }
/*     */     }
/*     */     
/* 124 */     this.tv.addLifeCycleListener(this);
/* 125 */     this.tv.addMenuFillListener(this);
/*     */     
/* 127 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void taggableAdded(Tag tag, Taggable tagged)
/*     */   {
/* 135 */     this.tv.addDataSource((PEPeer)tagged);
/*     */   }
/*     */   
/*     */ 
/*     */   public void taggableSync(Tag tag)
/*     */   {
/*     */     Set<PEPeer> peers_in_table;
/* 142 */     if (this.tv.getRowCount() != tag.getTaggedCount())
/*     */     {
/* 144 */       peers_in_table = new HashSet(this.tv.getDataSources());
/*     */       
/* 146 */       Set<PEPeer> peers_in_tag = new HashSet(tag.getTagged());
/*     */       
/* 148 */       for (PEPeer peer : peers_in_table)
/*     */       {
/* 150 */         if (!peers_in_tag.contains(peer))
/*     */         {
/* 152 */           this.tv.removeDataSource(peer);
/*     */         }
/*     */       }
/*     */       
/* 156 */       for (PEPeer peer : peers_in_tag)
/*     */       {
/* 158 */         if (!peers_in_table.contains(peer))
/*     */         {
/* 160 */           this.tv.addDataSource(peer);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void taggableRemoved(Tag tag, Taggable tagged)
/*     */   {
/* 171 */     this.tv.removeDataSource((PEPeer)tagged);
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewInitialized()
/*     */   {
/* 177 */     this.shell = this.tv.getComposite().getShell();
/*     */     
/* 179 */     this.tag.addTagListener(this, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewDestroyed()
/*     */   {
/* 185 */     this.tag.removeTagListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 193 */     PeersView.fillMenu(menu, this.tv, this.shell, null);
/*     */   }
/*     */   
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/PeersGeneralView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */