/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerAvailability;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.CompletedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.LeechersItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.NameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.PeersItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.SeedsItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.StatusItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.TypeItem;
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
/*     */ public class TrackerAvailView
/*     */   extends TableViewTab<TrackerPeerSource>
/*     */   implements TableLifeCycleListener, TableDataSourceChangedListener, TableViewSWTMenuFillListener
/*     */ {
/*     */   private static final String TABLE_ID = "TrackerAvail";
/*  46 */   private static final TableColumnCore[] basicItems = { new TypeItem("TrackerAvail"), new NameItem("TrackerAvail"), new StatusItem("TrackerAvail"), new SeedsItem("TrackerAvail"), new LeechersItem("TrackerAvail"), new PeersItem("TrackerAvail"), new CompletedItem("TrackerAvail") };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String MSGID_PREFIX = "TrackerView";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private DownloadManagerAvailability availability;
/*     */   
/*     */ 
/*     */ 
/*     */   private TableViewSWT<TrackerPeerSource> tv;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerAvailView()
/*     */   {
/*  67 */     super("TrackerView");
/*     */   }
/*     */   
/*     */ 
/*     */   public TableViewSWT<TrackerPeerSource> initYourTableView()
/*     */   {
/*  73 */     this.tv = TableViewFactory.createTableViewSWT(TrackerPeerSource.class, "TrackerAvail", getPropertiesPrefix(), basicItems, basicItems[0].getName(), 268500994);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  81 */     this.tv.addLifeCycleListener(this);
/*  82 */     this.tv.addMenuFillListener(this);
/*  83 */     this.tv.addTableDataSourceChangedListener(this, true);
/*     */     
/*  85 */     this.tv.setEnableTabViews(false, true, null);
/*     */     
/*  87 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUpdating()
/*     */   {
/*  93 */     List<TrackerPeerSource> peer_sources = this.tv.getDataSources();
/*     */     
/*  95 */     for (TrackerPeerSource p : peer_sources)
/*     */     {
/*  97 */       int status = p.getStatus();
/*     */       
/*  99 */       if ((status == 9) || (status == 3) || (status == 4))
/*     */       {
/*     */ 
/*     */ 
/* 103 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 107 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tableDataSourceChanged(Object newDataSource)
/*     */   {
/* 129 */     DownloadManagerAvailability old_avail = this.availability;
/* 130 */     if (newDataSource == null) {
/* 131 */       this.availability = null;
/* 132 */     } else if ((newDataSource instanceof Object[])) {
/* 133 */       Object temp = ((Object[])(Object[])newDataSource)[0];
/* 134 */       if ((temp instanceof DownloadManagerAvailability)) {
/* 135 */         this.availability = ((DownloadManagerAvailability)temp);
/*     */       } else {
/* 137 */         return;
/*     */       }
/*     */     }
/* 140 */     else if ((newDataSource instanceof DownloadManagerAvailability)) {
/* 141 */       this.availability = ((DownloadManagerAvailability)newDataSource);
/*     */     } else {
/* 143 */       return;
/*     */     }
/*     */     
/* 146 */     if (old_avail == this.availability) {
/* 147 */       return;
/*     */     }
/*     */     
/* 150 */     if (!this.tv.isDisposed())
/*     */     {
/* 152 */       this.tv.removeAllTableRows();
/*     */       
/* 154 */       if (this.availability != null)
/*     */       {
/* 156 */         addExistingDatasources();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewInitialized()
/*     */   {
/* 164 */     if (this.availability != null)
/*     */     {
/* 166 */       addExistingDatasources();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tableViewDestroyed() {}
/*     */   
/*     */ 
/*     */ 
/*     */   private void addExistingDatasources()
/*     */   {
/* 178 */     if ((this.availability == null) || (this.tv.isDisposed()))
/*     */     {
/* 180 */       return;
/*     */     }
/*     */     
/* 183 */     List<TrackerPeerSource> tps = this.availability.getTrackerPeerSources();
/*     */     
/* 185 */     this.tv.addDataSources(tps.toArray(new TrackerPeerSource[tps.size()]));
/*     */     
/* 187 */     this.tv.processDataSourceQueueSync();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/TrackerAvailView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */