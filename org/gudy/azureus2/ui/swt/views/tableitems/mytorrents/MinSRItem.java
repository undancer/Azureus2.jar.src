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
/*     */ public class MinSRItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  48 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   public static final String COLUMN_ID = "min_sr";
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  53 */     info.addCategories(new String[] { "sharing" });
/*  54 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public MinSRItem(String sTableID)
/*     */   {
/*  59 */     super(DATASOURCE_TYPE, "min_sr", 2, 70, sTableID);
/*  60 */     setRefreshInterval(-2);
/*     */     
/*  62 */     TableContextMenuItem menuItem = addContextMenuItem("menu.min.share.ratio2");
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
/*  75 */             object = ((TableRowCore)object).getDataSource(true);
/*     */           }
/*  77 */           if ((object instanceof DownloadManager)) {
/*  78 */             int x = ((DownloadManager)object).getDownloadState().getIntParameter("sr.min");
/*     */             
/*  80 */             if (existing == -1) {
/*  81 */               existing = x;
/*  82 */             } else if (existing != x) {
/*  83 */               existing = -1;
/*  84 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         String existing_text;
/*     */         String existing_text;
/*  91 */         if (existing == -1) {
/*  92 */           existing_text = "";
/*     */         } else {
/*  94 */           existing_text = String.valueOf(existing / 1000.0F);
/*     */         }
/*     */         
/*  97 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("min.sr.window.title", "min.sr.window.message");
/*     */         
/*     */ 
/* 100 */         entryWindow.setPreenteredText(existing_text, false);
/* 101 */         entryWindow.selectPreenteredText(true);
/*     */         
/* 103 */         entryWindow.prompt();
/*     */         
/* 105 */         if (entryWindow.hasSubmittedInput()) {
/*     */           try
/*     */           {
/* 108 */             String text = entryWindow.getSubmittedInput().trim();
/*     */             
/* 110 */             int sr = 0;
/*     */             
/* 112 */             if (text.length() > 0)
/*     */             {
/*     */               try {
/* 115 */                 float f = Float.parseFloat(text);
/*     */                 
/* 117 */                 sr = (int)(f * 1000.0F);
/*     */                 
/* 119 */                 if (sr < 0)
/*     */                 {
/* 121 */                   sr = 0;
/*     */                 }
/* 123 */                 else if ((sr == 0) && (f > 0.0F))
/*     */                 {
/* 125 */                   sr = 1;
/*     */                 }
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 130 */                 Debug.out(e);
/*     */               }
/*     */               
/* 133 */               for (Object object : o) {
/* 134 */                 if ((object instanceof TableRowCore)) {
/* 135 */                   object = ((TableRowCore)object).getDataSource(true);
/*     */                 }
/* 137 */                 if ((object instanceof DownloadManager)) {
/* 138 */                   ((DownloadManager)object).getDownloadState().setIntParameter("sr.min", sr);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 144 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 152 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 153 */     int value = 0;
/* 154 */     if (dm != null) {
/* 155 */       value = dm.getDownloadState().getIntParameter("sr.min");
/*     */     }
/*     */     
/* 158 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 159 */       return;
/*     */     }
/*     */     
/* 162 */     cell.setText(value == 0 ? "" : String.valueOf(value / 1000.0F));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/MinSRItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */