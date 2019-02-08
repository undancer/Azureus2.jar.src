/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatusItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellMouseListener
/*     */ {
/*  48 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "status";
/*     */   
/*  52 */   private static final Object CLICK_KEY = new Object();
/*  53 */   private static final int[] BLUE = Utils.colorToIntArray(Colors.blue);
/*     */   
/*     */   private boolean changeRowFG;
/*  56 */   private boolean changeCellFG = true;
/*     */   private boolean showTrackerErrors;
/*     */   
/*     */   public StatusItem(String sTableID, boolean changeRowFG)
/*     */   {
/*  61 */     super(DATASOURCE_TYPE, "status", 1, 80, sTableID);
/*  62 */     this.changeRowFG = changeRowFG;
/*  63 */     setRefreshInterval(-2);
/*     */   }
/*     */   
/*     */   public StatusItem(String sTableID) {
/*  67 */     this(sTableID, true);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  71 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  74 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  81 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     
/*  83 */     if (dm == null)
/*     */     {
/*  85 */       return;
/*     */     }
/*     */     
/*  88 */     int state = dm.getState();
/*     */     
/*     */     String text;
/*     */     String text;
/*  92 */     if ((this.showTrackerErrors) && (dm.isUnauthorisedOnTracker()) && (state != 100))
/*     */     {
/*  94 */       text = dm.getTrackerStatus();
/*     */     }
/*     */     else
/*     */     {
/*  98 */       text = DisplayFormatters.formatDownloadStatus(dm);
/*     */     }
/*     */     
/* 101 */     if ((cell.setText(text)) || (!cell.isValid()))
/*     */     {
/* 103 */       boolean clickable = false;
/*     */       
/* 105 */       if ((cell instanceof TableCellSWT))
/*     */       {
/*     */         int cursor_id;
/*     */         int cursor_id;
/* 109 */         if (!text.contains("http://"))
/*     */         {
/* 111 */           dm.setUserData(CLICK_KEY, null);
/*     */           
/* 113 */           cursor_id = 0;
/*     */         }
/*     */         else
/*     */         {
/* 117 */           dm.setUserData(CLICK_KEY, text);
/*     */           
/* 119 */           cursor_id = 21;
/*     */           
/* 121 */           clickable = true;
/*     */         }
/*     */         
/* 124 */         ((TableCellSWT)cell).setCursorID(cursor_id);
/*     */       }
/*     */       
/* 127 */       if ((!this.changeCellFG) && (!this.changeRowFG))
/*     */       {
/*     */ 
/*     */ 
/* 131 */         cell.setForeground(clickable ? BLUE : null);
/*     */         
/* 133 */         return;
/*     */       }
/*     */       
/* 136 */       TableRow row = cell.getTableRow();
/*     */       
/* 138 */       if (row != null)
/*     */       {
/* 140 */         Color color = null;
/* 141 */         if (state == 60) {
/* 142 */           color = Colors.blues[7];
/* 143 */         } else if (state == 100) {
/* 144 */           color = Colors.colorError;
/*     */         } else {
/* 146 */           color = null;
/*     */         }
/* 148 */         if (this.changeRowFG) {
/* 149 */           row.setForeground(Utils.colorToIntArray(color));
/* 150 */         } else if (this.changeCellFG) {
/* 151 */           cell.setForeground(Utils.colorToIntArray(color));
/*     */         }
/* 153 */         if (clickable) {
/* 154 */           cell.setForeground(Utils.colorToIntArray(Colors.blue));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isChangeRowFG()
/*     */   {
/* 162 */     return this.changeRowFG;
/*     */   }
/*     */   
/*     */   public void setChangeRowFG(boolean changeRowFG) {
/* 166 */     this.changeRowFG = changeRowFG;
/*     */   }
/*     */   
/*     */   public boolean isChangeCellFG() {
/* 170 */     return this.changeCellFG;
/*     */   }
/*     */   
/*     */   public void setChangeCellFG(boolean changeCellFG) {
/* 174 */     this.changeCellFG = changeCellFG;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setShowTrackerErrors(boolean s)
/*     */   {
/* 181 */     this.showTrackerErrors = s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 189 */     DownloadManager dm = (DownloadManager)event.cell.getDataSource();
/* 190 */     if (dm == null) { return;
/*     */     }
/* 192 */     String clickable = (String)dm.getUserData(CLICK_KEY);
/*     */     
/* 194 */     if (clickable == null)
/*     */     {
/* 196 */       return;
/*     */     }
/*     */     
/* 199 */     event.skipCoreFunctionality = true;
/*     */     
/* 201 */     if (event.eventType == 1)
/*     */     {
/* 203 */       String url = UrlUtils.getURL(clickable);
/*     */       
/* 205 */       if (url != null)
/*     */       {
/* 207 */         Utils.launch(url);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/StatusItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */