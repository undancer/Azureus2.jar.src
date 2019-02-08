/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
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
/*     */ public class MaxSRItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  48 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   public static final String COLUMN_ID = "max_sr";
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  53 */     info.addCategories(new String[] { "sharing" });
/*  54 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public MaxSRItem(String sTableID)
/*     */   {
/*  59 */     super(DATASOURCE_TYPE, "max_sr", 2, 70, sTableID);
/*  60 */     setRefreshInterval(-2);
/*     */     
/*  62 */     TableContextMenuItem menuItem = addContextMenuItem("menu.max.share.ratio2");
/*  63 */     menuItem.addMultiListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/*  65 */         if (target == null) {
/*  66 */           return;
/*     */         }
/*     */         
/*  69 */         Object[] o = (Object[])target;
/*     */         
/*  71 */         int existing = -1;
/*     */         
/*  73 */         for (Object object : o) {
/*  74 */           if ((object instanceof TableRowCore)) {
/*  75 */             TableRowCore rowCore = (TableRowCore)object;
/*  76 */             object = rowCore.getDataSource(true);
/*     */           }
/*  78 */           if ((object instanceof DownloadManager)) {
/*  79 */             int x = ((DownloadManager)object).getDownloadState().getIntParameter("sr.max");
/*     */             
/*  81 */             if (existing == -1) {
/*  82 */               existing = x;
/*  83 */             } else if (existing != x) {
/*  84 */               existing = -1;
/*  85 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         String existing_text;
/*     */         String existing_text;
/*  92 */         if (existing == -1) {
/*  93 */           existing_text = "";
/*     */         } else {
/*  95 */           existing_text = String.valueOf(existing / 1000.0F);
/*     */         }
/*     */         
/*  98 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("max.sr.window.title", "max.sr.window.message");
/*     */         
/*     */ 
/* 101 */         entryWindow.setPreenteredText(existing_text, false);
/* 102 */         entryWindow.selectPreenteredText(true);
/*     */         
/* 104 */         entryWindow.prompt();
/*     */         
/* 106 */         if (entryWindow.hasSubmittedInput()) {
/*     */           try
/*     */           {
/* 109 */             String text = entryWindow.getSubmittedInput().trim();
/*     */             
/* 111 */             int sr = 0;
/*     */             
/* 113 */             if (text.length() > 0)
/*     */             {
/*     */               try {
/* 116 */                 float f = Float.parseFloat(text);
/*     */                 
/* 118 */                 sr = (int)(f * 1000.0F);
/*     */                 
/* 120 */                 if (sr < 0)
/*     */                 {
/* 122 */                   sr = 0;
/*     */                 }
/* 124 */                 else if ((sr == 0) && (f > 0.0F))
/*     */                 {
/* 126 */                   sr = 1;
/*     */                 }
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 131 */                 Debug.out(e);
/*     */               }
/*     */               
/* 134 */               for (Object object : o) {
/* 135 */                 if ((object instanceof TableRowCore)) {
/* 136 */                   TableRowCore rowCore = (TableRowCore)object;
/* 137 */                   object = rowCore.getDataSource(true);
/*     */                 }
/* 139 */                 if ((object instanceof DownloadManager)) {
/* 140 */                   ((DownloadManager)object).getDownloadState().setIntParameter("sr.max", sr);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 146 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 154 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 155 */     int value = 0;
/* 156 */     if (dm != null) {
/* 157 */       value = dm.getDownloadState().getIntParameter("sr.max");
/*     */     }
/*     */     
/* 160 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 161 */       return;
/*     */     }
/*     */     
/* 164 */     cell.setText(value == 0 ? "" : String.valueOf(value / 1000.0F));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/MaxSRItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */