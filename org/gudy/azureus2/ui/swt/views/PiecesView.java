/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerPieceListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.piece.MyPieceDistributionView;
/*     */ import org.gudy.azureus2.ui.swt.views.piece.PieceInfoView;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.AvailabilityItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.BlockCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.BlocksItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.CompletedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.PieceNumberItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.PriorityItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.RequestedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.ReservedByItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.SizeItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.SpeedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.TypeItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.pieces.WritersItem;
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
/*     */ public class PiecesView
/*     */   extends TableViewTab<PEPiece>
/*     */   implements DownloadManagerPeerListener, DownloadManagerPieceListener, TableDataSourceChangedListener, TableLifeCycleListener, TableViewSWTMenuFillListener, UISWTViewCoreEventListenerEx
/*     */ {
/*  81 */   private static boolean registeredCoreSubViews = false;
/*     */   
/*  83 */   private static final TableColumnCore[] basicItems = { new PieceNumberItem(), new SizeItem(), new BlockCountItem(), new BlocksItem(), new CompletedItem(), new AvailabilityItem(), new TypeItem(), new ReservedByItem(), new WritersItem(), new PriorityItem(), new SpeedItem(), new RequestedItem() };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String MSGID_PREFIX = "PiecesView";
/*     */   
/*     */ 
/*     */ 
/*     */   private DownloadManager manager;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  99 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*     */     
/* 101 */     tcManager.setDefaultColumnNames("Pieces", basicItems);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 107 */   private boolean enable_tabs = true;
/*     */   
/*     */   private TableViewSWT<PEPiece> tv;
/*     */   
/*     */   private Composite legendComposite;
/*     */   
/*     */   private boolean comp_focused;
/*     */   private Object focus_pending_ds;
/*     */   
/*     */   public PiecesView()
/*     */   {
/* 118 */     super("PiecesView");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/* 124 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/* 130 */     return new PiecesView();
/*     */   }
/*     */   
/*     */   public TableViewSWT<PEPiece> initYourTableView()
/*     */   {
/* 135 */     this.tv = TableViewFactory.createTableViewSWT(PEPiece.class, "Pieces", getPropertiesPrefix(), basicItems, basicItems[0].getName(), 268500996);
/*     */     
/*     */ 
/* 138 */     this.tv.setEnableTabViews(this.enable_tabs, true, null);
/*     */     
/* 140 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 141 */     if (uiFunctions != null) {
/* 142 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 144 */       if ((pluginUI != null) && (!registeredCoreSubViews))
/*     */       {
/* 146 */         pluginUI.addView("Pieces", "PieceInfoView", PieceInfoView.class, this.manager);
/*     */         
/*     */ 
/* 149 */         pluginUI.addView("Pieces", "MyPieceDistributionView", MyPieceDistributionView.class, this.manager);
/*     */         
/*     */ 
/* 152 */         registeredCoreSubViews = true;
/*     */       }
/*     */     }
/*     */     
/* 156 */     this.tv.addTableDataSourceChangedListener(this, true);
/* 157 */     this.tv.addMenuFillListener(this);
/* 158 */     this.tv.addLifeCycleListener(this);
/*     */     
/* 160 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 168 */     final List<Object> selected = this.tv.getSelectedDataSources();
/*     */     
/* 170 */     if (selected.size() == 0)
/*     */     {
/* 172 */       return;
/*     */     }
/*     */     
/* 175 */     if (this.manager == null)
/*     */     {
/* 177 */       return;
/*     */     }
/*     */     
/* 180 */     PEPeerManager pm = this.manager.getPeerManager();
/*     */     
/* 182 */     if (pm == null)
/*     */     {
/* 184 */       return;
/*     */     }
/*     */     
/* 187 */     final PiecePicker picker = pm.getPiecePicker();
/*     */     
/* 189 */     boolean has_undone = false;
/* 190 */     boolean has_unforced = false;
/*     */     
/* 192 */     for (Object obj : selected)
/*     */     {
/* 194 */       PEPiece piece = (PEPiece)obj;
/*     */       
/* 196 */       if (!piece.getDMPiece().isDone())
/*     */       {
/* 198 */         has_undone = true;
/*     */         
/* 200 */         if (picker.isForcePiece(piece.getPieceNumber()))
/*     */         {
/* 202 */           has_unforced = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 207 */     final MenuItem force_piece = new MenuItem(menu, 32);
/*     */     
/* 209 */     Messages.setLanguageText(force_piece, "label.force.piece");
/*     */     
/* 211 */     force_piece.setEnabled(has_undone);
/*     */     
/* 213 */     if (has_undone)
/*     */     {
/* 215 */       force_piece.setSelection(has_unforced);
/*     */       
/* 217 */       force_piece.addSelectionListener(new SelectionAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void widgetSelected(SelectionEvent e)
/*     */         {
/*     */ 
/* 224 */           boolean forced = force_piece.getSelection();
/*     */           
/* 226 */           for (Object obj : selected)
/*     */           {
/* 228 */             PEPiece piece = (PEPiece)obj;
/*     */             
/* 230 */             if (!piece.getDMPiece().isDone())
/*     */             {
/* 232 */               picker.setForcePiece(piece.getPieceNumber(), forced);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 239 */     MenuItem cancel_reqs_piece = new MenuItem(menu, 8);
/*     */     
/* 241 */     Messages.setLanguageText(cancel_reqs_piece, "label.rerequest.blocks");
/*     */     
/* 243 */     cancel_reqs_piece.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 250 */         for (Object obj : selected)
/*     */         {
/* 252 */           PEPiece piece = (PEPiece)obj;
/*     */           
/* 254 */           for (int i = 0; i < piece.getNbBlocks(); i++)
/*     */           {
/* 256 */             if (piece.isRequested(i))
/*     */             {
/* 258 */               piece.clearRequested(i);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 264 */     });
/* 265 */     MenuItem reset_piece = new MenuItem(menu, 8);
/*     */     
/* 267 */     Messages.setLanguageText(reset_piece, "label.reset.piece");
/*     */     
/* 269 */     reset_piece.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/*     */ 
/* 276 */         for (Object obj : selected)
/*     */         {
/* 278 */           PEPiece piece = (PEPiece)obj;
/*     */           
/* 280 */           piece.reset();
/*     */         }
/*     */         
/*     */       }
/* 284 */     });
/* 285 */     new MenuItem(menu, 2);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setFocused(boolean foc)
/*     */   {
/* 302 */     if (foc)
/*     */     {
/* 304 */       this.comp_focused = true;
/*     */       
/* 306 */       dataSourceChanged(this.focus_pending_ds);
/*     */     }
/*     */     else
/*     */     {
/* 310 */       this.focus_pending_ds = this.manager;
/*     */       
/* 312 */       dataSourceChanged(null);
/*     */       
/* 314 */       this.comp_focused = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public void tableDataSourceChanged(Object newDataSource)
/*     */   {
/* 320 */     if (!this.comp_focused) {
/* 321 */       this.focus_pending_ds = newDataSource;
/* 322 */       return;
/*     */     }
/*     */     
/* 325 */     DownloadManager newManager = ViewUtils.getDownloadManagerFromDataSource(newDataSource);
/*     */     
/* 327 */     if (newManager == this.manager) {
/* 328 */       this.tv.setEnabled(this.manager != null);
/* 329 */       return;
/*     */     }
/*     */     
/* 332 */     if (this.manager != null) {
/* 333 */       this.manager.removePeerListener(this);
/* 334 */       this.manager.removePieceListener(this);
/*     */     }
/*     */     
/* 337 */     this.manager = newManager;
/*     */     
/* 339 */     if (this.tv.isDisposed()) {
/* 340 */       return;
/*     */     }
/*     */     
/* 343 */     this.tv.removeAllTableRows();
/* 344 */     this.tv.setEnabled(this.manager != null);
/*     */     
/* 346 */     if (this.manager != null) {
/* 347 */       this.manager.addPeerListener(this, false);
/* 348 */       this.manager.addPieceListener(this, false);
/* 349 */       addExistingDatasources();
/*     */     }
/*     */   }
/*     */   
/*     */   public void tableViewInitialized()
/*     */   {
/* 355 */     if ((this.legendComposite != null) && (this.tv != null)) {
/* 356 */       Composite composite = this.tv.getTableComposite();
/*     */       
/* 358 */       this.legendComposite = Legend.createLegendComposite(composite, BlocksItem.colors, new String[] { "PiecesView.legend.requested", "PiecesView.legend.written", "PiecesView.legend.downloaded", "PiecesView.legend.incache" });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 367 */     if (this.manager != null) {
/* 368 */       this.manager.removePeerListener(this);
/* 369 */       this.manager.removePieceListener(this);
/* 370 */       this.manager.addPeerListener(this, false);
/* 371 */       this.manager.addPieceListener(this, false);
/* 372 */       addExistingDatasources();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewDestroyed()
/*     */   {
/* 379 */     if ((this.legendComposite != null) && (this.legendComposite.isDisposed())) {
/* 380 */       this.legendComposite.dispose();
/*     */     }
/*     */     
/* 383 */     if (this.manager != null) {
/* 384 */       this.manager.removePeerListener(this);
/* 385 */       this.manager.removePieceListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public void pieceAdded(PEPiece created)
/*     */   {
/* 391 */     this.tv.addDataSource(created);
/*     */   }
/*     */   
/*     */   public void pieceRemoved(PEPiece removed) {
/* 395 */     this.tv.removeDataSource(removed);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void peerManagerRemoved(PEPeerManager manager)
/*     */   {
/* 403 */     this.tv.removeAllTableRows();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addExistingDatasources()
/*     */   {
/* 411 */     if ((this.manager == null) || (this.tv.isDisposed())) {
/* 412 */       return;
/*     */     }
/*     */     
/* 415 */     PEPiece[] dataSources = this.manager.getCurrentPieces();
/* 416 */     if ((dataSources != null) && (dataSources.length >= 0)) {
/* 417 */       this.tv.addDataSources(dataSources);
/* 418 */       this.tv.processDataSourceQueue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 427 */     TableViewSWT_TabsCommon tabs = this.tv.getTabsCommon();
/*     */     
/* 429 */     if (tabs != null)
/*     */     {
/* 431 */       tabs.triggerTabViewsDataSourceChanged(this.tv);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DownloadManager getManager()
/*     */   {
/* 440 */     return this.manager;
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 444 */     switch (event.getType())
/*     */     {
/*     */     case 0: 
/* 447 */       if ((event instanceof UISWTViewEventImpl))
/*     */       {
/* 449 */         String parent = ((UISWTViewEventImpl)event).getParentID();
/*     */         
/* 451 */         this.enable_tabs = ((parent != null) && (parent.equals("TorrentDetailsView"))); }
/* 452 */       break;
/*     */     
/*     */ 
/*     */     case 3: 
/* 456 */       String id = "DMDetails_Pieces";
/*     */       
/* 458 */       setFocused(true);
/*     */       
/* 460 */       if (this.manager != null) {
/* 461 */         if (this.manager.getTorrent() != null) {
/* 462 */           id = id + "." + this.manager.getInternalName();
/*     */         } else {
/* 464 */           id = id + ":" + this.manager.getSize();
/*     */         }
/* 466 */         SelectedContentManager.changeCurrentlySelectedContent(id, new SelectedContent[] { new SelectedContent(this.manager) });
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 471 */         SelectedContentManager.changeCurrentlySelectedContent(id, null);
/*     */       }
/*     */       
/* 474 */       break;
/*     */     case 4: 
/* 476 */       setFocused(false);
/* 477 */       SelectedContentManager.clearCurrentlySelectedContent();
/*     */     }
/*     */     
/*     */     
/* 481 */     return super.eventOccurred(event);
/*     */   }
/*     */   
/*     */   public void addThisColumnSubMenu(String sColumnName, Menu menuThisColumn) {}
/*     */   
/*     */   public void peerAdded(PEPeer peer) {}
/*     */   
/*     */   public void peerRemoved(PEPeer peer) {}
/*     */   
/*     */   public void peerManagerWillBeAdded(PEPeerManager peer_manager) {}
/*     */   
/*     */   public void peerManagerAdded(PEPeerManager manager) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/PiecesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */