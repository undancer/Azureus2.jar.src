/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnTorrentSpeed
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 40 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "torrentspeed";
/*    */   
/*    */   private Image imgUp;
/*    */   private Image imgDown;
/*    */   
/*    */   public ColumnTorrentSpeed(String tableID)
/*    */   {
/* 49 */     super("torrentspeed", 80, tableID);
/* 50 */     setAlignment(2);
/* 51 */     setType(1);
/* 52 */     setRefreshInterval(-2);
/* 53 */     setUseCoreDataSource(false);
/*    */     
/* 55 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 56 */     this.imgUp = imageLoader.getImage("image.torrentspeed.up");
/* 57 */     this.imgDown = imageLoader.getImage("image.torrentspeed.down");
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 61 */     info.addCategories(new String[] { "essential", "bytes" });
/*    */     
/*    */ 
/*    */ 
/* 65 */     info.setProficiency((byte)0);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 69 */     Object ds = cell.getDataSource();
/* 70 */     if (!(ds instanceof Download)) {
/* 71 */       return;
/*    */     }
/* 73 */     Download dm = (Download)ds;
/*    */     
/*    */ 
/* 76 */     String prefix = "";
/*    */     
/*    */ 
/* 79 */     int iState = dm.getState();
/* 80 */     long value; if (iState == 4) {
/* 81 */       long value = dm.getStats().getDownloadAverage();
/* 82 */       ((TableCellSWT)cell).setIcon(this.imgDown);
/* 83 */     } else if (iState == 5) {
/* 84 */       long value = dm.getStats().getUploadAverage();
/* 85 */       ((TableCellSWT)cell).setIcon(this.imgUp);
/*    */     } else {
/* 87 */       ((TableCellSWT)cell).setIcon(null);
/* 88 */       value = 0L;
/*    */     }
/* 90 */     long sortValue = value << 4 | iState;
/*    */     
/*    */ 
/* 93 */     if ((cell.setSortValue(sortValue)) || (!cell.isValid())) {
/* 94 */       cell.setText(value > 0L ? prefix + DisplayFormatters.formatByteCountToKiBEtcPerSec(value) : "");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ColumnTorrentSpeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */