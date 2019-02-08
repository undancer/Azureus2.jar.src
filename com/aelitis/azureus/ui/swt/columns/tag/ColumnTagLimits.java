/*     */ package com.aelitis.azureus.ui.swt.columns.tag;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureLimits;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class ColumnTagLimits
/*     */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*     */ {
/*  29 */   public static String COLUMN_ID = "tag.limit";
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  32 */     info.addCategories(new String[] { "settings" });
/*     */     
/*     */ 
/*  35 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public ColumnTagLimits(TableColumn column)
/*     */   {
/*  40 */     column.setWidth(200);
/*  41 */     column.addListeners(this);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  45 */     Tag tag = (Tag)cell.getDataSource();
/*  46 */     String tag_limits = "";
/*  47 */     if ((tag instanceof TagFeatureLimits))
/*     */     {
/*  49 */       TagFeatureLimits tfl = (TagFeatureLimits)tag;
/*     */       
/*  51 */       int max = tfl.getMaximumTaggables();
/*     */       
/*  53 */       if (max > 0) {
/*  54 */         tag_limits = String.valueOf(max);
/*     */         
/*  56 */         String policy = null;
/*     */         
/*  58 */         switch (tfl.getRemovalStrategy()) {
/*     */         case 0: 
/*  60 */           policy = "label.none.assigned";
/*  61 */           break;
/*     */         case 1: 
/*  63 */           policy = "MyTorrentsView.menu.archive";
/*  64 */           break;
/*     */         case 2: 
/*  66 */           policy = "Button.deleteContent.fromLibrary";
/*  67 */           break;
/*     */         case 3: 
/*  69 */           policy = "Button.deleteContent.fromComputer";
/*  70 */           break;
/*     */         case 4: 
/*  72 */           policy = "label.move.to.old.tag";
/*     */         }
/*     */         
/*     */         
/*  76 */         if (policy != null) {
/*  77 */           tag_limits = tag_limits + "; " + MessageText.getString(policy);
/*     */         }
/*     */         
/*  80 */         String order = null;
/*  81 */         switch (tfl.getOrdering()) {
/*     */         case 0: 
/*  83 */           order = "label.time.added.to.vuze";
/*  84 */           break;
/*     */         case 1: 
/*  86 */           order = "label.time.added.to.tag";
/*     */         }
/*     */         
/*     */         
/*  90 */         if (order != null) {
/*  91 */           tag_limits = tag_limits + "; " + MessageText.getString(order);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  96 */     if ((!cell.setSortValue(tag_limits)) && (cell.isValid())) {
/*  97 */       return;
/*     */     }
/*     */     
/* 100 */     if (!cell.isShown()) {
/* 101 */       return;
/*     */     }
/*     */     
/* 104 */     cell.setText(tag_limits);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagLimits.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */