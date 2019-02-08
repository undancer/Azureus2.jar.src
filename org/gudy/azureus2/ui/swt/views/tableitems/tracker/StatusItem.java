/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.tracker;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatusItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*  41 */   private static final String[] js_resource_keys = { "SpeedView.stats.unknown", "pairing.status.disabled", "ManagerItem.stopped", "ManagerItem.queued", "GeneralView.label.updatein.querying", "azbuddy.ui.table.online", "ManagerItem.error", "tps.status.available", "tps.status.unavailable", "ManagerItem.initializing" };
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
/*  54 */   private static String[] js_resources = new String[js_resource_keys.length];
/*     */   
/*     */ 
/*     */ 
/*     */   public StatusItem(String tableID)
/*     */   {
/*  60 */     super("status", 1, -2, 75, tableID);
/*     */     
/*  62 */     setRefreshInterval(-1);
/*     */     
/*  64 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*     */       public void localeChanged(Locale old_locale, Locale new_locale) {
/*  66 */         for (int i = 0; i < StatusItem.js_resources.length; i++) {
/*  67 */           StatusItem.js_resources[i] = MessageText.getString(StatusItem.js_resource_keys[i]);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  77 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  80 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  87 */     TrackerPeerSource ps = (TrackerPeerSource)cell.getDataSource();
/*     */     
/*     */     int status;
/*     */     int status;
/*  91 */     if (ps == null)
/*     */     {
/*  93 */       status = 0;
/*     */     }
/*     */     else {
/*     */       int status;
/*  97 */       if (ps.isUpdating())
/*     */       {
/*  99 */         status = 4;
/*     */       }
/*     */       else
/*     */       {
/* 103 */         status = ps.getStatus();
/*     */       }
/*     */     }
/*     */     
/* 107 */     String str = js_resources[status];
/*     */     
/* 109 */     String extra = ps == null ? "" : ps.getStatusString();
/*     */     
/* 111 */     if (status == 5)
/*     */     {
/* 113 */       if (extra != null)
/*     */       {
/* 115 */         int pos = extra.indexOf(" (");
/*     */         
/* 117 */         if (pos != -1)
/*     */         {
/* 119 */           str = str + extra.substring(pos);
/*     */         }
/*     */       }
/* 122 */     } else if ((status == 6) || (status == 2) || (status == 3))
/*     */     {
/* 124 */       if (extra != null)
/*     */       {
/* 126 */         str = str + ": " + extra;
/*     */       }
/*     */     }
/*     */     
/* 130 */     if ((!cell.setSortValue(str)) && (cell.isValid()))
/*     */     {
/* 132 */       return;
/*     */     }
/*     */     
/* 135 */     cell.setText(str);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/tracker/StatusItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */