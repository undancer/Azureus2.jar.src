/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.ColumnDateSizer;
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
/*     */ public class ShareRatioProgressItem
/*     */   extends ColumnDateSizer
/*     */ {
/*  45 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   
/*     */   public static final String COLUMN_ID = "sr_prog";
/*     */   private static int existing_sr;
/*     */   
/*     */   static
/*     */   {
/*  52 */     COConfigurationManager.addAndFireParameterListener("Share Ratio Progress Interval", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*  60 */         ShareRatioProgressItem.access$002(COConfigurationManager.getIntParameter(name));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  66 */     info.addCategories(new String[] { "time", "sharing", "swarm" });
/*  67 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */   public ShareRatioProgressItem(String sTableID) {
/*  71 */     super(DATASOURCE_TYPE, "sr_prog", TableColumnCreator.DATE_COLUMN_WIDTH, sTableID);
/*  72 */     setRefreshInterval(-2);
/*  73 */     setMultiline(false);
/*     */     
/*  75 */     TableContextMenuItem menuSetInterval = addContextMenuItem("TableColumn.menu.sr_prog.interval", 1);
/*     */     
/*  77 */     menuSetInterval.setStyle(1);
/*  78 */     menuSetInterval.addListener(new MenuItemListener()
/*     */     {
/*     */ 
/*     */       public void selected(MenuItem menu, Object target)
/*     */       {
/*     */ 
/*  84 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("sr_prog.window.title", "sr_prog.window.message");
/*     */         
/*     */ 
/*  87 */         String sr_str = DisplayFormatters.formatDecimal(ShareRatioProgressItem.existing_sr / 1000.0D, 3);
/*     */         
/*  89 */         entryWindow.setPreenteredText(sr_str, false);
/*  90 */         entryWindow.selectPreenteredText(true);
/*  91 */         entryWindow.setWidthHint(400);
/*     */         
/*  93 */         entryWindow.prompt();
/*     */         
/*  95 */         if (entryWindow.hasSubmittedInput()) {
/*     */           try
/*     */           {
/*  98 */             String text = entryWindow.getSubmittedInput().trim();
/*     */             
/* 100 */             if (text.length() > 0)
/*     */             {
/* 102 */               float f = Float.parseFloat(text);
/*     */               
/* 104 */               int sr = (int)(f * 1000.0F);
/*     */               
/* 106 */               COConfigurationManager.setParameter("Share Ratio Progress Interval", sr);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ShareRatioProgressItem(String tableID, boolean v)
/*     */   {
/* 119 */     this(tableID);
/* 120 */     setVisible(v);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell, long timestamp) {
/* 124 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     
/* 126 */     if ((dm == null) || (existing_sr <= 0))
/*     */     {
/* 128 */       super.refresh(cell, 0L);
/*     */       
/* 130 */       return;
/*     */     }
/*     */     
/* 133 */     int dm_state = dm.getState();
/*     */     
/* 135 */     long next_eta = -1L;
/*     */     
/* 137 */     if ((dm_state == 50) || (dm_state == 60))
/*     */     {
/* 139 */       DownloadManagerStats stats = dm.getStats();
/*     */       
/* 141 */       long downloaded = stats.getTotalGoodDataBytesReceived();
/* 142 */       long uploaded = stats.getTotalDataBytesSent();
/*     */       
/* 144 */       if (downloaded <= 0L)
/*     */       {
/* 146 */         next_eta = -2L;
/*     */       }
/*     */       else
/*     */       {
/* 150 */         int current_sr = (int)(1000L * uploaded / downloaded);
/*     */         
/* 152 */         int mult = current_sr / existing_sr;
/*     */         
/* 154 */         int next_target_sr = (mult + 1) * existing_sr;
/*     */         
/* 156 */         long up_speed = stats.getDataSendRate() == 0L ? 0L : stats.getSmoothedDataSendRate();
/*     */         
/* 158 */         if (up_speed <= 0L)
/*     */         {
/* 160 */           next_eta = -2L;
/*     */ 
/*     */ 
/*     */         }
/* 164 */         else if (dm_state == 60)
/*     */         {
/*     */ 
/*     */ 
/* 168 */           long target_upload = next_target_sr * downloaded / 1000L;
/*     */           
/* 170 */           next_eta = (target_upload - uploaded) / up_speed;
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 177 */           DiskManager disk_man = dm.getDiskManager();
/*     */           
/* 179 */           if (disk_man != null)
/*     */           {
/* 181 */             long remaining = disk_man.getRemainingExcludingDND();
/*     */             
/* 183 */             long down_speed = (dm_state == 60) || (stats.getDataReceiveRate() == 0L) ? 0L : stats.getSmoothedDataReceiveRate();
/*     */             
/* 185 */             if ((down_speed <= 0L) || (remaining <= 0L))
/*     */             {
/*     */ 
/*     */ 
/* 189 */               long target_upload = next_target_sr * downloaded / 1000L;
/*     */               
/* 191 */               next_eta = (target_upload - uploaded) / up_speed;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */               long time_to_sr = (next_target_sr * downloaded / 1000L - uploaded) / (up_speed - down_speed * next_target_sr / 1000L);
/*     */               
/* 205 */               long time_to_completion = remaining / down_speed;
/*     */               
/* 207 */               if ((time_to_sr > 0L) && (time_to_sr <= time_to_completion))
/*     */               {
/* 209 */                 next_eta = time_to_sr;
/*     */ 
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/*     */ 
/* 216 */                 long uploaded_at_completion = uploaded + up_speed * time_to_completion;
/* 217 */                 long downloaded_at_completion = downloaded + down_speed * time_to_completion;
/*     */                 
/*     */ 
/*     */ 
/* 221 */                 long target_upload = next_target_sr * downloaded_at_completion / 1000L;
/*     */                 
/* 223 */                 next_eta = time_to_completion + (target_upload - uploaded_at_completion) / up_speed;
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 228 */             next_eta = -2L;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 235 */     long data = dm.getDownloadState().getLongAttribute("sr.prog");
/*     */     
/*     */ 
/* 238 */     long sr = (int)data;
/*     */     
/* 240 */     String sr_str = DisplayFormatters.formatDecimal(sr / 1000.0D, 3);
/*     */     
/* 242 */     timestamp = (data >>> 32) * 1000L;
/*     */     
/*     */ 
/*     */ 
/* 246 */     long sort_order = timestamp;
/*     */     
/* 248 */     sort_order += ((sr & 0xFF) << 8);
/*     */     
/* 250 */     sort_order += (next_eta & 0xFF);
/*     */     
/*     */     String next_eta_str;
/*     */     String next_eta_str;
/* 254 */     if (next_eta == -1L) {
/* 255 */       next_eta_str = ""; } else { String next_eta_str;
/* 256 */       if (next_eta == -2L) {
/* 257 */         next_eta_str = "∞: ";
/*     */       } else {
/* 259 */         next_eta_str = DisplayFormatters.formatETA(next_eta) + ": ";
/*     */       }
/*     */     }
/* 262 */     String prefix = next_eta_str + sr_str + (timestamp > 0L ? ": " : "");
/*     */     
/* 264 */     super.refresh(cell, timestamp, sort_order, prefix);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/ShareRatioProgressItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */