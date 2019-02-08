/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*     */ public class TagColorsItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellSWTPaintListener
/*     */ {
/*  54 */   private static TagManager tag_manager = ;
/*     */   
/*  56 */   public static final Class DATASOURCE_TYPE = Download.class;
/*     */   public static final String COLUMN_ID = "tag_colors";
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  61 */     info.addCategories(new String[] { "content" });
/*     */   }
/*     */   
/*     */   public TagColorsItem(String sTableID)
/*     */   {
/*  66 */     super(DATASOURCE_TYPE, "tag_colors", 1, 70, sTableID);
/*  67 */     setRefreshInterval(-2);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  71 */     String sTags = null;
/*  72 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*  73 */     if (dm != null) {
/*  74 */       List<Tag> tags = tag_manager.getTagsForTaggable(3, dm);
/*     */       
/*  76 */       if (tags.size() > 0)
/*     */       {
/*  78 */         for (Tag t : tags)
/*     */         {
/*  80 */           String str = t.getTagName(true);
/*     */           
/*  82 */           if (sTags == null) {
/*  83 */             sTags = str;
/*     */           } else {
/*  85 */             sTags = sTags + ", " + str;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  90 */     cell.setSortValue(sTags);
/*  91 */     cell.setToolTip(sTags == null ? "" : sTags);
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  96 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*     */     
/*  98 */     List<Color> colors = new ArrayList();
/*     */     
/*     */ 
/* 101 */     if (dm != null)
/*     */     {
/* 103 */       List<Tag> tags = tag_manager.getTagsForTaggable(3, dm);
/*     */       
/* 105 */       for (Tag tag : tags)
/*     */       {
/* 107 */         int[] rgb = tag.getColor();
/*     */         
/* 109 */         if ((rgb != null) && (rgb.length == 3))
/*     */         {
/* 111 */           colors.add(ColorCache.getColor(gc.getDevice(), rgb));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 116 */     int num_colors = colors.size();
/*     */     
/* 118 */     if (num_colors > 0)
/*     */     {
/* 120 */       Rectangle bounds = cell.getBounds();
/*     */       
/* 122 */       bounds.x += 1;
/* 123 */       bounds.y += 1;
/* 124 */       bounds.width -= 1;
/* 125 */       bounds.height -= 1;
/*     */       
/* 127 */       if (num_colors == 1)
/*     */       {
/* 129 */         gc.setBackground((Color)colors.get(0));
/*     */         
/* 131 */         gc.fillRectangle(bounds);
/*     */       }
/*     */       else
/*     */       {
/* 135 */         int width = bounds.width;
/* 136 */         int chunk = width / num_colors;
/*     */         
/* 138 */         if (chunk == 0) {
/* 139 */           chunk = 1;
/*     */         }
/*     */         
/* 142 */         bounds.width = chunk;
/*     */         
/* 144 */         for (int i = 0; i < num_colors; i++)
/*     */         {
/* 146 */           if (i == num_colors - 1)
/*     */           {
/* 148 */             int rem = width - chunk * (num_colors - 1);
/*     */             
/* 150 */             if (rem > 0)
/*     */             {
/* 152 */               bounds.width = rem;
/*     */             }
/*     */           }
/*     */           
/* 156 */           gc.setBackground((Color)colors.get(i));
/*     */           
/* 158 */           gc.fillRectangle(bounds);
/*     */           
/* 160 */           bounds.x += chunk;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TagColorsItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */