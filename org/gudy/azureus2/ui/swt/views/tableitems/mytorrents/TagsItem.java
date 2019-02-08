/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.core.tag.TagManager;
/*    */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*    */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TagsItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 48 */   private static TagManager tag_manager = ;
/*    */   
/* 50 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "tags";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 55 */     info.addCategories(new String[] { "content" });
/*    */   }
/*    */   
/*    */   public TagsItem(String sTableID)
/*    */   {
/* 60 */     super(DATASOURCE_TYPE, "tags", 1, 70, sTableID);
/* 61 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 65 */     String sTags = null;
/* 66 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 67 */     if (dm != null) {
/* 68 */       List<Tag> tags = tag_manager.getTagsForTaggable(3, dm);
/*    */       
/* 70 */       if (tags.size() > 0)
/*    */       {
/* 72 */         tags = TagUIUtils.sortTags(tags);
/*    */         
/* 74 */         for (Tag t : tags)
/*    */         {
/* 76 */           String str = t.getTagName(true);
/*    */           
/* 78 */           if (sTags == null) {
/* 79 */             sTags = str;
/*    */           } else {
/* 81 */             sTags = sTags + ", " + str;
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/* 86 */     cell.setText(sTags == null ? "" : sTags);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TagsItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */