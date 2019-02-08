/*     */ package org.gudy.azureus2.plugins.ui.tables;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnImpl;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*     */ public class TableCellRefresher
/*     */ {
/*  44 */   private static TableCellRefresher instance = null;
/*     */   
/*     */   private AEThread2 refresher;
/*     */   
/*  48 */   private Map<TableCell, TableColumn> mapCellsToColumn = new HashMap();
/*     */   
/*     */   private long iterationNumber;
/*     */   
/*  52 */   private volatile boolean inProgress = false;
/*     */   private AERunnable runnable;
/*     */   
/*     */   private TableCellRefresher()
/*     */   {
/*  57 */     this.runnable = new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/*     */         try {
/*  61 */           synchronized (TableCellRefresher.this.mapCellsToColumn) {
/*  62 */             cellsCopy = new HashMap(TableCellRefresher.this.mapCellsToColumn);
/*  63 */             TableCellRefresher.this.mapCellsToColumn.clear();
/*     */           }
/*     */           
/*  66 */           for (TableCell cell : cellsCopy.keySet()) {
/*  67 */             TableColumn column = (TableColumn)cellsCopy.get(cell);
/*     */             
/*     */             try
/*     */             {
/*  71 */               if ((column instanceof TableCellRefreshListener)) {
/*  72 */                 ((TableCellRefreshListener)column).refresh(cell);
/*  73 */               } else if ((column instanceof TableColumnImpl)) {
/*  74 */                 List<TableCellRefreshListener> listeners = ((TableColumnImpl)column).getCellRefreshListeners();
/*  75 */                 for (TableCellRefreshListener listener : listeners) {
/*  76 */                   listener.refresh(cell);
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable t) {
/*  81 */               t.printStackTrace();
/*     */             }
/*     */           }
/*     */         } finally { Map<TableCell, TableColumn> cellsCopy;
/*  85 */           TableCellRefresher.this.inProgress = false;
/*     */         }
/*     */         
/*     */       }
/*  89 */     };
/*  90 */     this.refresher = new AEThread2("Cell Refresher", true)
/*     */     {
/*     */       public void run() {
/*     */         try {
/*  94 */           TableCellRefresher.this.iterationNumber = 0L;
/*     */           
/*  96 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */           
/*     */           for (;;)
/*     */           {
/* 100 */             if (uif != null)
/*     */             {
/*     */               int size;
/*     */               
/* 104 */               synchronized (TableCellRefresher.this.mapCellsToColumn)
/*     */               {
/* 106 */                 size = TableCellRefresher.this.mapCellsToColumn.size();
/*     */               }
/*     */               
/* 109 */               if ((size > 0) && (!TableCellRefresher.this.inProgress))
/*     */               {
/* 111 */                 TableCellRefresher.this.inProgress = true;
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 116 */                 uif.runOnUIThread(1, TableCellRefresher.this.runnable);
/*     */               }
/*     */             }
/*     */             
/* 120 */             Thread.sleep(100L);
/*     */             
/* 122 */             TableCellRefresher.access$208(TableCellRefresher.this);
/*     */           }
/*     */         } catch (Exception e) {
/* 125 */           e.printStackTrace();
/*     */         }
/*     */         
/*     */       }
/* 129 */     };
/* 130 */     this.refresher.start();
/*     */   }
/*     */   
/*     */   private void _addColumnCell(TableColumn column, TableCell cell)
/*     */   {
/* 135 */     synchronized (this.mapCellsToColumn) {
/* 136 */       if (this.mapCellsToColumn.containsKey(cell)) {
/* 137 */         return;
/*     */       }
/* 139 */       this.mapCellsToColumn.put(cell, column);
/*     */     }
/*     */   }
/*     */   
/*     */   private int _getRefreshIndex(int refreshEvery100ms, int nbIndices) {
/* 144 */     if (refreshEvery100ms <= 0) return 1;
/* 145 */     if (nbIndices <= 0) { return 1;
/*     */     }
/* 147 */     return (int)(this.iterationNumber / refreshEvery100ms % nbIndices);
/*     */   }
/*     */   
/*     */   private static synchronized TableCellRefresher getInstance() {
/* 151 */     if (instance == null) {
/* 152 */       instance = new TableCellRefresher();
/*     */     }
/* 154 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addCell(TableColumn column, TableCell cell)
/*     */   {
/* 161 */     getInstance()._addColumnCell(column, cell);
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getRefreshIndex(int refreshEvery100ms, int nbIndices)
/*     */   {
/* 167 */     return getInstance()._getRefreshIndex(refreshEvery100ms, nbIndices);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableCellRefresher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */