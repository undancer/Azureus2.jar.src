/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*     */ public class WritersItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*     */   public WritersItem()
/*     */   {
/*  38 */     super("writers", 1, -1, 80, "Pieces");
/*  39 */     setObfustication(true);
/*  40 */     setRefreshInterval(4);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  44 */     info.addCategories(new String[] { "swarm" });
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  50 */     PEPiece piece = (PEPiece)cell.getDataSource();
/*  51 */     String[] core_writers = piece.getWriters();
/*  52 */     String[] my_writers = new String[core_writers.length];
/*  53 */     int writer_count = 0;
/*  54 */     Map map = new HashMap();
/*     */     
/*  56 */     int i = 0;
/*  57 */     for (;;) { String this_writer = null;
/*     */       
/*     */ 
/*  60 */       for (int start = i; start < core_writers.length; start++) {
/*  61 */         this_writer = core_writers[start];
/*  62 */         if (this_writer != null)
/*     */           break;
/*     */       }
/*  65 */       if (this_writer == null) {
/*     */         break;
/*     */       }
/*     */       
/*  69 */       for (int end = start + 1; end < core_writers.length; end++) {
/*  70 */         if (!this_writer.equals(core_writers[end])) {
/*     */           break;
/*     */         }
/*     */       }
/*  74 */       StringBuffer pieces = (StringBuffer)map.get(this_writer);
/*  75 */       if (pieces == null) {
/*  76 */         pieces = new StringBuffer();
/*  77 */         map.put(this_writer, pieces);
/*  78 */         my_writers[(writer_count++)] = this_writer;
/*     */       } else {
/*  80 */         pieces.append(',');
/*     */       }
/*     */       
/*  83 */       pieces.append(start);
/*  84 */       if (end - 1 > start) {
/*  85 */         pieces.append('-').append(end - 1);
/*     */       }
/*  87 */       i = end;
/*     */     }
/*     */     
/*  90 */     StringBuilder sb = new StringBuilder();
/*  91 */     for (int i = 0; i < writer_count; i++) {
/*  92 */       String writer = my_writers[i];
/*  93 */       StringBuffer pieces = (StringBuffer)map.get(writer);
/*  94 */       if (i > 0)
/*  95 */         sb.append(';');
/*  96 */       sb.append(writer).append('[').append(pieces).append(']');
/*     */     }
/*     */     
/*  99 */     String value = sb.toString();
/* 100 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 101 */       return;
/*     */     }
/*     */     
/* 104 */     cell.setText(value);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/WritersItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */