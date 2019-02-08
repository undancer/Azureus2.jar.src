/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*     */ 
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ 
/*     */ public class PriorityItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*     */   public PriorityItem()
/*     */   {
/*  38 */     super("priority", 1, -2, 70, "Files");
/*  39 */     setRefreshInterval(-2);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  43 */     info.addCategories(new String[] { "content" });
/*     */     
/*     */ 
/*  46 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  50 */     DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/*     */     
/*  52 */     int sortval = 0;
/*     */     String tmp;
/*  54 */     String tmp; if (fileInfo == null)
/*     */     {
/*  56 */       tmp = "";
/*     */     }
/*     */     else
/*     */     {
/*  60 */       int st = fileInfo.getStorageType();
/*     */       
/*  62 */       if (((st == 2) || (st == 4)) && (fileInfo.isSkipped()))
/*     */       {
/*     */ 
/*     */ 
/*  66 */         String tmp = MessageText.getString("FileItem.delete");
/*     */         
/*  68 */         sortval = Integer.MIN_VALUE;
/*     */       }
/*  70 */       else if (fileInfo.isSkipped())
/*     */       {
/*  72 */         String tmp = MessageText.getString("FileItem.donotdownload");
/*     */         
/*  74 */         sortval = -2147483647;
/*     */       }
/*     */       else
/*     */       {
/*  78 */         int pri = fileInfo.getPriority();
/*     */         
/*  80 */         sortval = pri;
/*     */         
/*  82 */         if (pri > 0)
/*     */         {
/*  84 */           String tmp = MessageText.getString("FileItem.high");
/*     */           
/*  86 */           if (pri > 1)
/*     */           {
/*  88 */             tmp = tmp + " (" + pri + ")";
/*     */           }
/*  90 */         } else if (pri < 0)
/*     */         {
/*  92 */           String tmp = MessageText.getString("FileItem.low");
/*     */           
/*  94 */           if (pri < -1)
/*     */           {
/*  96 */             tmp = tmp + " (" + pri + ")";
/*     */           }
/*     */         }
/*     */         else {
/* 100 */           tmp = MessageText.getString("FileItem.normal");
/*     */         }
/*     */       }
/*     */     }
/* 104 */     cell.setText(tmp);
/* 105 */     cell.setSortValue(sortval);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/PriorityItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */