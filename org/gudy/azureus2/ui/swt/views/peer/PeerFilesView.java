/*     */ package org.gudy.azureus2.ui.swt.views.peer;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRefreshListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
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
/*     */ public class PeerFilesView
/*     */   extends TableViewTab<PeersFilesViewRow>
/*     */   implements TableDataSourceChangedListener, TableLifeCycleListener, TableRefreshListener
/*     */ {
/*     */   public static final String TABLEID_PEER_FILES = "PeerFiles";
/*  45 */   boolean refreshing = false;
/*     */   
/*  47 */   private static final TableColumnCore[] basicItems = { new NameItem(null), new PercentItem(null) };
/*     */   
/*     */   private TableViewSWT<PeersFilesViewRow> tv;
/*     */   private PEPeer current_peer;
/*     */   
/*     */   static
/*     */   {
/*  54 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/*     */     
/*  56 */     tcManager.setDefaultColumnNames("PeerFiles", basicItems);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeerFilesView()
/*     */   {
/*  66 */     super("PeerFilesView");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TableViewSWT<PeersFilesViewRow> initYourTableView()
/*     */   {
/*  74 */     this.tv = TableViewFactory.createTableViewSWT(PeersFilesViewRow.class, "PeerFiles", getPropertiesPrefix(), basicItems, "firstpiece", 268500994);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  79 */     this.tv.addTableDataSourceChangedListener(this, true);
/*  80 */     this.tv.addRefreshListener(this, true);
/*  81 */     this.tv.addLifeCycleListener(this);
/*     */     
/*  83 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tableDataSourceChanged(Object newDataSource)
/*     */   {
/*  91 */     if ((newDataSource instanceof PEPeer))
/*     */     {
/*  93 */       this.current_peer = ((PEPeer)newDataSource);
/*     */     }
/*  95 */     if ((newDataSource instanceof Object[]))
/*     */     {
/*  97 */       Object[] temp = (Object[])newDataSource;
/*     */       
/*  99 */       if ((temp.length > 0) && ((temp[0] instanceof PEPeer)))
/*     */       {
/* 101 */         this.current_peer = ((PEPeer)temp[0]);
/*     */       }
/*     */       else
/*     */       {
/* 105 */         this.current_peer = null;
/*     */       }
/*     */     }
/*     */     else {
/* 109 */       this.current_peer = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableRefresh()
/*     */   {
/* 116 */     synchronized (this)
/*     */     {
/* 118 */       if (this.refreshing)
/*     */       {
/* 120 */         return;
/*     */       }
/*     */       
/* 123 */       this.refreshing = true;
/*     */     }
/*     */     try
/*     */     {
/* 127 */       PEPeer peer = this.current_peer;
/*     */       
/* 129 */       if (peer == null)
/*     */       {
/* 131 */         this.tv.removeAllTableRows();
/*     */ 
/*     */ 
/*     */       }
/* 135 */       else if (this.tv.getRowCount() == 0)
/*     */       {
/* 137 */         DiskManagerFileInfo[] files = peer.getManager().getDiskManager().getFiles();
/*     */         
/* 139 */         PeersFilesViewRow[] rows = new PeersFilesViewRow[files.length];
/*     */         
/* 141 */         for (int i = 0; i < files.length; i++)
/*     */         {
/* 143 */           rows[i] = new PeersFilesViewRow(files[i], peer, null);
/*     */         }
/*     */         
/* 146 */         this.tv.addDataSources(rows);
/*     */         
/* 148 */         this.tv.processDataSourceQueueSync();
/*     */       }
/*     */       else
/*     */       {
/* 152 */         TableRowCore[] rows = this.tv.getRows();
/*     */         
/* 154 */         for (TableRowCore row : rows)
/*     */         {
/* 156 */           ((PeersFilesViewRow)row.getDataSource()).setPeer(peer);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 162 */       synchronized (this)
/*     */       {
/* 164 */         this.refreshing = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tableViewTabInitComplete()
/*     */   {
/* 175 */     super.tableViewTabInitComplete();
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewInitialized() {}
/*     */   
/*     */ 
/*     */   public void tableViewDestroyed() {}
/*     */   
/*     */ 
/*     */   protected static class PeersFilesViewRow
/*     */   {
/*     */     private DiskManagerFileInfo file;
/*     */     
/*     */     private PEPeer peer;
/*     */     
/*     */     private PeersFilesViewRow(DiskManagerFileInfo _file, PEPeer _peer)
/*     */     {
/* 193 */       this.file = _file;
/* 194 */       this.peer = _peer;
/*     */     }
/*     */     
/*     */ 
/*     */     private DiskManagerFileInfo getFile()
/*     */     {
/* 200 */       return this.file;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setPeer(PEPeer _peer)
/*     */     {
/* 207 */       this.peer = _peer;
/*     */     }
/*     */     
/*     */ 
/*     */     private PEPeer getPeer()
/*     */     {
/* 213 */       return this.peer;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class NameItem
/*     */     extends CoreTableColumnSWT
/*     */     implements TableCellRefreshListener
/*     */   {
/*     */     private NameItem()
/*     */     {
/* 225 */       super(1, -2, 300, "PeerFiles");
/*     */       
/* 227 */       setType(1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void refresh(TableCell cell)
/*     */     {
/* 235 */       PeerFilesView.PeersFilesViewRow row = (PeerFilesView.PeersFilesViewRow)cell.getDataSource();
/* 236 */       String name = row == null ? "" : PeerFilesView.PeersFilesViewRow.access$400(row).getFile(true).getName();
/* 237 */       if (name == null) {
/* 238 */         name = "";
/*     */       }
/* 240 */       cell.setText(name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class PercentItem
/*     */     extends CoreTableColumnSWT
/*     */     implements TableCellRefreshListener
/*     */   {
/*     */     private PercentItem()
/*     */     {
/* 252 */       super(2, -2, 60, "PeerFiles");
/* 253 */       setRefreshInterval(-2);
/* 254 */       setMinWidthAuto(true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void refresh(TableCell cell)
/*     */     {
/* 261 */       PeerFilesView.PeersFilesViewRow row = (PeerFilesView.PeersFilesViewRow)cell.getDataSource();
/*     */       
/* 263 */       if (row == null)
/*     */       {
/* 265 */         return;
/*     */       }
/*     */       
/* 268 */       DiskManagerFileInfo file = row.getFile();
/*     */       
/* 270 */       PEPeer peer = row.getPeer();
/*     */       
/* 272 */       BitFlags pieces = peer.getAvailable();
/*     */       
/* 274 */       if (pieces == null)
/*     */       {
/* 276 */         cell.setText("");
/*     */         
/* 278 */         return;
/*     */       }
/*     */       
/* 281 */       boolean[] flags = pieces.flags;
/*     */       
/* 283 */       int first_piece = file.getFirstPieceNumber();
/*     */       
/* 285 */       int last_piece = file.getLastPieceNumber();
/*     */       
/* 287 */       int done = 0;
/*     */       
/* 289 */       for (int i = first_piece; i <= last_piece; i++)
/*     */       {
/* 291 */         if (flags[i] != 0)
/*     */         {
/* 293 */           done++;
/*     */         }
/*     */       }
/*     */       
/* 297 */       int percent = done * 1000 / (last_piece - first_piece + 1);
/*     */       
/* 299 */       if ((!cell.setSortValue(percent)) && (cell.isValid()))
/*     */       {
/* 301 */         return;
/*     */       }
/*     */       
/* 304 */       cell.setText(percent < 0 ? "" : DisplayFormatters.formatPercentFromThousands(percent));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/peer/PeerFilesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */