/*     */ package com.aelitis.azureus.ui.swt.devices.columns;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*     */ import java.io.File;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class ColumnTJ_Status
/*     */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*     */ {
/*     */   public static final String COLUMN_ID = "transcode_status";
/*  46 */   private static final String[] js_resource_keys = { "ManagerItem.queued", "devices.converting", "ManagerItem.paused", "sidebar.LibraryCD", "Progress.reporting.status.canceled", "ManagerItem.error", "ManagerItem.stopped", "devices.copy.fail", "devices.on.demand", "devices.ready", "devices.downloading", "devices.copying" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String[] js_resources;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String eta_text;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnTJ_Status(final TableColumn column)
/*     */   {
/*  66 */     column.initialize(1, -2, 160);
/*  67 */     column.addListeners(this);
/*  68 */     column.setRefreshInterval(-1);
/*  69 */     column.setType(3);
/*     */     
/*  71 */     MessageText.addAndFireListener(new MessageText.MessageTextListener() {
/*     */       public void localeChanged(Locale old_locale, Locale new_locale) {
/*  73 */         ColumnTJ_Status.access$002(new String[ColumnTJ_Status.js_resource_keys.length]);
/*     */         
/*  75 */         for (int i = 0; i < ColumnTJ_Status.js_resources.length; i++) {
/*  76 */           ColumnTJ_Status.js_resources[i] = MessageText.getString(ColumnTJ_Status.js_resource_keys[i]);
/*     */         }
/*     */         
/*  79 */         ColumnTJ_Status.access$202(MessageText.getString("TableColumn.header.eta"));
/*     */         
/*  81 */         column.invalidateCells();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  87 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  90 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  96 */     TranscodeFile tf = (TranscodeFile)cell.getDataSource();
/*  97 */     if ((tf == null) || (tf.isDeleted())) {
/*  98 */       return;
/*     */     }
/* 100 */     TranscodeJob job = tf.getJob();
/*     */     
/* 102 */     String tooltip = null;
/* 103 */     String text = null;
/* 104 */     boolean error = false;
/*     */     
/* 106 */     if (job == null)
/*     */     {
/*     */       try {
/* 109 */         if ((tf.isComplete()) && (!tf.getTargetFile().getFile(true).exists()))
/*     */         {
/* 111 */           tooltip = "File '" + tf.getTargetFile().getFile().getAbsolutePath() + "' not found";
/*     */           
/* 113 */           text = js_resources[5] + ": File not found";
/*     */           
/* 115 */           error = true;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/* 120 */       if (text == null)
/*     */       {
/* 122 */         if (tf.isCopyingToDevice())
/*     */         {
/* 124 */           text = js_resources[11];
/*     */         }
/* 126 */         else if (tf.getCopyToDeviceFails() > 0L)
/*     */         {
/* 128 */           text = js_resources[7];
/*     */           
/* 130 */           error = true;
/*     */         }
/* 132 */         else if ((tf.isTemplate()) && (!tf.isComplete()))
/*     */         {
/* 134 */           text = js_resources[8];
/*     */         }
/*     */         else
/*     */         {
/* 138 */           text = js_resources[9];
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 143 */       int state = job.getState();
/*     */       
/* 145 */       text = js_resources[state];
/*     */       
/* 147 */       if (state == 0)
/*     */       {
/* 149 */         long eta = job.getDownloadETA();
/*     */         
/* 151 */         if (eta > 0L)
/*     */         {
/* 153 */           text = js_resources[10] + ": " + eta_text + " " + (eta == Long.MAX_VALUE ? "∞" : TimeFormatter.format(eta));
/*     */         }
/*     */       }
/*     */       else {
/* 157 */         text = js_resources[state];
/*     */         
/* 159 */         if (state == 5)
/*     */         {
/* 161 */           String error_msg = job.getError();
/*     */           
/* 163 */           if (error_msg != null)
/*     */           {
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/*     */ 
/* 171 */               int pos = error_msg.indexOf('\n');
/*     */               
/* 173 */               if (pos >= 0)
/*     */               {
/* 175 */                 error_msg = error_msg.substring(0, pos);
/*     */               }
/*     */               
/* 178 */               pos = error_msg.indexOf(',');
/*     */               
/* 180 */               if (pos >= 0)
/*     */               {
/* 182 */                 pos = error_msg.indexOf(',', pos + 1);
/*     */                 
/* 184 */                 if (pos >= 0)
/*     */                 {
/* 186 */                   error_msg = error_msg.substring(0, pos);
/*     */                 }
/*     */               }
/*     */               
/* 190 */               text = text + ": " + error_msg.trim();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/* 196 */           tooltip = "See transcode log for more details";
/*     */           
/* 198 */           error = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 205 */     cell.setText(text);
/* 206 */     cell.setToolTip(tooltip);
/*     */     
/* 208 */     if (error)
/*     */     {
/* 210 */       cell.setForegroundToErrorColor();
/*     */     }
/*     */     else
/*     */     {
/* 214 */       cell.setForeground(Utils.colorToIntArray(null));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnTJ_Status.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */