/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*     */ public class CommentIconItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellMouseListener, TableCellAddedListener, TableCellToolTipListener
/*     */ {
/*  46 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  55 */     info.addCategories(new String[] { "content" });
/*     */   }
/*     */   
/*     */ 
/*  59 */   static final UISWTGraphic graphicComment = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("comment"));
/*     */   
/*  61 */   static final UISWTGraphic noGraphicComment = new UISWTGraphicImpl(ImageLoader.getInstance().getImage("no_comment"));
/*     */   
/*     */   public static final String COLUMN_ID = "commenticon";
/*     */   
/*     */   public CommentIconItem(String sTableID)
/*     */   {
/*  67 */     super(DATASOURCE_TYPE, "commenticon", -2, 20, sTableID);
/*  68 */     setRefreshInterval(-2);
/*  69 */     initializeAsGraphic(-2, 20);
/*  70 */     setMinWidth(20);
/*  71 */     setIconReference("comment", true);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/*  75 */     if ((cell instanceof TableCellSWT)) {
/*  76 */       ((TableCellSWT)cell).setCursorID(21);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event) {
/*  81 */     DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/*  82 */     if (dm == null) { return;
/*     */     }
/*  84 */     if (event.eventType != 1) { return;
/*     */     }
/*     */     
/*  87 */     if (event.button != 1) return;
/*  88 */     event.skipCoreFunctionality = true;
/*     */     
/*  90 */     TorrentUtil.promptUserForComment(new DownloadManager[] { dm });
/*  91 */     refresh(event.cell);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  95 */     if (cell.isDisposed()) { return;
/*     */     }
/*     */     
/*  98 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  99 */     String comment = null;
/* 100 */     if (dm != null) {
/* 101 */       comment = dm.getDownloadState().getUserComment();
/* 102 */       if ((comment != null) && (comment.length() == 0)) { comment = null;
/*     */       }
/*     */     }
/* 105 */     Graphic oldGraphic = cell.getGraphic();
/* 106 */     if ((comment == null) && (oldGraphic != noGraphicComment)) {
/* 107 */       cell.setGraphic(noGraphicComment);
/* 108 */       cell.setSortValue(null);
/*     */     }
/* 110 */     else if ((comment != null) && (oldGraphic != graphicComment)) {
/* 111 */       cell.setGraphic(graphicComment);
/* 112 */       cell.setSortValue(comment);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell)
/*     */   {
/* 118 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 119 */     String comment = null;
/* 120 */     if (dm != null) {
/* 121 */       comment = dm.getDownloadState().getUserComment();
/* 122 */       if ((comment != null) && (comment.length() == 0)) comment = null;
/*     */     }
/* 124 */     cell.setToolTip(comment);
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell) {
/* 128 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/CommentIconItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */