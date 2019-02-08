/*     */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryManager;
/*     */ import org.gudy.azureus2.core3.category.CategoryManagerListener;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttributeEvent;
/*     */ import org.gudy.azureus2.plugins.utils.Formatters;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
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
/*     */ public class TorrentAttributeCategoryImpl
/*     */   extends BaseTorrentAttributeImpl
/*     */ {
/*     */   protected TorrentAttributeCategoryImpl()
/*     */   {
/*  42 */     CategoryManager.addCategoryManagerListener(new CategoryManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void categoryAdded(final Category category)
/*     */       {
/*     */ 
/*  49 */         TorrentAttributeEvent ev = new TorrentAttributeEvent()
/*     */         {
/*     */ 
/*     */           public int getType()
/*     */           {
/*     */ 
/*  55 */             return 1;
/*     */           }
/*     */           
/*     */ 
/*     */           public TorrentAttribute getAttribute()
/*     */           {
/*  61 */             return TorrentAttributeCategoryImpl.this;
/*     */           }
/*     */           
/*     */ 
/*     */           public Object getData()
/*     */           {
/*  67 */             return category.getName();
/*     */           }
/*     */           
/*  70 */         };
/*  71 */         TorrentAttributeCategoryImpl.this.notifyListeners(ev);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void categoryChanged(Category category) {}
/*     */       
/*     */ 
/*     */       public void categoryRemoved(final Category category)
/*     */       {
/*  81 */         TorrentAttributeEvent ev = new TorrentAttributeEvent()
/*     */         {
/*     */ 
/*     */           public int getType()
/*     */           {
/*     */ 
/*  87 */             return 2;
/*     */           }
/*     */           
/*     */ 
/*     */           public TorrentAttribute getAttribute()
/*     */           {
/*  93 */             return TorrentAttributeCategoryImpl.this;
/*     */           }
/*     */           
/*     */ 
/*     */           public Object getData()
/*     */           {
/*  99 */             return category.getName();
/*     */           }
/*     */           
/* 102 */         };
/* 103 */         TorrentAttributeCategoryImpl.this.notifyListeners(ev);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 111 */     return "Category";
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getDefinedValues()
/*     */   {
/* 117 */     Category[] categories = CategoryManager.getCategories();
/*     */     
/* 119 */     List v = new ArrayList();
/*     */     
/* 121 */     for (int i = 0; i < categories.length; i++)
/*     */     {
/* 123 */       Category cat = categories[i];
/*     */       
/* 125 */       if (cat.getType() == 0)
/*     */       {
/* 127 */         v.add(cat.getName());
/*     */       }
/*     */     }
/*     */     
/* 131 */     String[] res = new String[v.size()];
/*     */     
/* 133 */     v.toArray(res);
/*     */     
/*     */ 
/*     */ 
/* 137 */     Arrays.sort(res, StaticUtilities.getFormatters().getAlphanumericComparator(true));
/*     */     
/* 139 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDefinedValue(String name)
/*     */   {
/* 146 */     CategoryManager.createCategory(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeDefinedValue(String name)
/*     */   {
/* 154 */     Category cat = CategoryManager.getCategory(name);
/*     */     
/* 156 */     if (cat != null)
/*     */     {
/* 158 */       CategoryManager.removeCategory(cat);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/TorrentAttributeCategoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */