/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*     */ public class BadAvailTimeItem
/*     */   extends ColumnDateSizer
/*     */ {
/*  37 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "bad_avail_time";
/*     */   private static String now_string;
/*     */   
/*     */   static
/*     */   {
/*  44 */     MessageText.addAndFireListener(new MessageText.MessageTextListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void localeChanged(Locale old_locale, Locale new_locale)
/*     */       {
/*     */ 
/*     */ 
/*  52 */         BadAvailTimeItem.access$002(MessageText.getString("SpeedView.stats.now"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public BadAvailTimeItem(String sTableID) {
/*  58 */     super(DATASOURCE_TYPE, "bad_avail_time", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*  59 */     setRefreshInterval(-2);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  63 */     info.addCategories(new String[] { "swarm", "time" });
/*     */     
/*     */ 
/*     */ 
/*  67 */     info.setProficiency((byte)2);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, long timestamp) {
/*  71 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  72 */     long value = dm == null ? -1L : dm.getStats().getAvailWentBadTime();
/*     */     
/*  74 */     if (value == 0L)
/*     */     {
/*     */ 
/*     */ 
/*  78 */       PEPeerManager pm = dm.getPeerManager();
/*     */       
/*  80 */       if ((pm == null) || (pm.getMinAvailability() < 1.0D))
/*     */       {
/*  82 */         long stopped = dm.getDownloadState().getLongAttribute("timestopped");
/*     */         
/*  84 */         if (stopped > 0L)
/*     */         {
/*  86 */           value = stopped;
/*     */         }
/*     */         else
/*     */         {
/*  90 */           value = -1L;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  95 */         value = -2L;
/*     */       }
/*     */     }
/*     */     
/*  99 */     if (value > 0L) {
/* 100 */       super.refresh(cell, value);
/*     */     } else {
/*     */       String text;
/*     */       String text;
/* 104 */       if (value == -2L) {
/* 105 */         text = now_string;
/*     */       } else {
/* 107 */         text = "";
/*     */       }
/*     */       
/* 110 */       if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 111 */         return;
/*     */       }
/*     */       
/* 114 */       cell.setText(text);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/BadAvailTimeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */