/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*     */ 
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*     */ public class DownItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellToolTipListener
/*     */ {
/*     */   public static final String COLUMN_ID = "download";
/*     */   protected static boolean separate_prot_data_stats;
/*     */   protected static boolean data_stats_only;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  55 */     info.addCategories(new String[] { "bytes" });
/*     */   }
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*  61 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "config.style.dataStatsOnly", "config.style.separateProtDataStats" }, new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String x)
/*     */       {
/*  65 */         DownItem.separate_prot_data_stats = COConfigurationManager.getBooleanParameter("config.style.separateProtDataStats");
/*  66 */         DownItem.data_stats_only = COConfigurationManager.getBooleanParameter("config.style.dataStatsOnly");
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public DownItem(String table_id)
/*     */   {
/*  73 */     super("download", 2, -1, 70, table_id);
/*  74 */     setRefreshInterval(-2);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  78 */     PEPeer peer = (PEPeer)cell.getDataSource();
/*  79 */     long data_value = 0L;
/*  80 */     long prot_value = 0L;
/*     */     
/*  82 */     if (peer != null) {
/*  83 */       data_value = peer.getStats().getTotalDataBytesReceived();
/*  84 */       prot_value = peer.getStats().getTotalProtocolBytesReceived(); }
/*     */     long sort_value;
/*     */     long sort_value;
/*  87 */     if (separate_prot_data_stats) {
/*  88 */       sort_value = (data_value << 24) + prot_value; } else { long sort_value;
/*  89 */       if (data_stats_only) {
/*  90 */         sort_value = data_value;
/*     */       } else {
/*  92 */         sort_value = data_value + prot_value;
/*     */       }
/*     */     }
/*  95 */     if ((!cell.setSortValue(sort_value)) && (cell.isValid())) {
/*  96 */       return;
/*     */     }
/*  98 */     cell.setText(DisplayFormatters.formatDataProtByteCountToKiBEtc(data_value, prot_value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cellHover(TableCell cell)
/*     */   {
/* 106 */     Object ds = cell.getDataSource();
/* 107 */     if ((ds instanceof PEPeer)) {
/* 108 */       PEPeer peer = (PEPeer)ds;
/*     */       
/* 110 */       long data_value = peer.getStats().getTotalDataBytesReceived();
/* 111 */       long prot_value = peer.getStats().getTotalProtocolBytesReceived();
/*     */       
/* 113 */       StringBuilder sb = new StringBuilder();
/* 114 */       sb.append(DisplayFormatters.formatByteCountToKiBEtc(data_value));
/* 115 */       sb.append(' ');
/* 116 */       sb.append(MessageText.getString("label.transfered.data"));
/* 117 */       sb.append('\n');
/* 118 */       sb.append(DisplayFormatters.formatByteCountToKiBEtc(prot_value));
/* 119 */       sb.append(' ');
/* 120 */       sb.append(MessageText.getString("label.transfered.protocol"));
/* 121 */       sb.append('\n');
/* 122 */       sb.append(DisplayFormatters.formatByteCountToKiBEtc(prot_value + data_value));
/* 123 */       sb.append(' ');
/* 124 */       sb.append(MessageText.getString("label.transfered.total"));
/* 125 */       sb.append('\n');
/*     */       
/* 127 */       cell.setToolTip(sb.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellHoverComplete(TableCell cell)
/*     */   {
/* 135 */     cell.setToolTip(null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/DownItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */