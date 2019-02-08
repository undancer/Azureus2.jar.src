/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytracker;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateCellText;
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
/*     */ public class NameItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, ObfusticateCellText
/*     */ {
/*     */   private static boolean bShowIcon;
/*     */   
/*     */   static
/*     */   {
/*  48 */     COConfigurationManager.addAndFireParameterListener("NameColumn.showProgramIcon", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*  51 */         NameItem.access$002(COConfigurationManager.getBooleanParameter("NameColumn.showProgramIcon"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public NameItem()
/*     */   {
/*  58 */     super("name", -2, 250, "MyTracker");
/*  59 */     setType(1);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  63 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/*  64 */     String name = item == null ? "" : TorrentUtils.getLocalisedName(item.getTorrent());
/*     */     
/*     */ 
/*     */ 
/*  68 */     if (((cell.setText(name)) || (!cell.isValid())) && 
/*  69 */       (item != null) && (item.getTorrent() != null) && (bShowIcon) && ((cell instanceof TableCellSWT))) {
/*     */       try
/*     */       {
/*  72 */         final TOTorrent torrent = item.getTorrent();
/*  73 */         final String path = torrent.getFiles()[0].getRelativePath();
/*     */         
/*  75 */         if (path != null)
/*     */         {
/*  77 */           Image icon = null;
/*     */           
/*  79 */           final TableCellSWT _cell = (TableCellSWT)cell;
/*     */           
/*     */ 
/*     */ 
/*  83 */           if (Utils.isSWTThread())
/*     */           {
/*  85 */             icon = ImageRepository.getPathIcon(path, false, !torrent.isSimpleTorrent());
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*  90 */             Utils.execSWTThread(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/*  96 */                 Image icon = ImageRepository.getPathIcon(path, false, !torrent.isSimpleTorrent());
/*     */                 
/*  98 */                 _cell.setIcon(icon);
/*     */                 
/* 100 */                 _cell.redraw();
/*     */               }
/*     */             });
/*     */           }
/*     */           
/*     */ 
/* 106 */           if (icon != null)
/*     */           {
/* 108 */             _cell.setIcon(icon);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public String getObfusticatedText(TableCell cell)
/*     */   {
/* 118 */     TRHostTorrent item = (TRHostTorrent)cell.getDataSource();
/* 119 */     String name = null;
/*     */     try
/*     */     {
/* 122 */       name = ByteFormatter.nicePrint(item.getTorrent().getHash(), true);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 126 */     if (name == null)
/* 127 */       name = "";
/* 128 */     return name;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytracker/NameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */