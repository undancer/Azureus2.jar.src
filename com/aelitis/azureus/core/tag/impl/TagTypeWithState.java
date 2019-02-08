/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.TaggableResolver;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.List;
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
/*     */ public class TagTypeWithState
/*     */   extends TagTypeBase
/*     */ {
/*  34 */   private final CopyOnWriteList<Tag> tags = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   private TaggableResolver resolver;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TagTypeWithState(int tag_type, int tag_features, String tag_name)
/*     */   {
/*  44 */     super(tag_type, tag_features, tag_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TagTypeWithState(int tag_type, TaggableResolver _resolver, int tag_features, String tag_name)
/*     */   {
/*  54 */     super(tag_type, tag_features, tag_name);
/*     */     
/*  56 */     this.resolver = _resolver;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Taggable resolveTaggable(String id)
/*     */   {
/*  63 */     if (this.resolver == null)
/*     */     {
/*  65 */       return super.resolveTaggable(id);
/*     */     }
/*     */     
/*  68 */     return this.resolver.resolveTaggable(id);
/*     */   }
/*     */   
/*     */ 
/*     */   protected TaggableResolver getResolver()
/*     */   {
/*  74 */     return this.resolver;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeTaggable(TaggableResolver _resolver, Taggable taggable)
/*     */   {
/*  82 */     if (this.resolver == _resolver)
/*     */     {
/*  84 */       for (Tag t : this.tags)
/*     */       {
/*  86 */         t.removeTaggable(taggable);
/*     */       }
/*     */     }
/*     */     
/*  90 */     super.removeTaggable(_resolver, taggable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addTag(Tag t)
/*     */   {
/*  97 */     this.tags.add(t);
/*     */     
/*  99 */     if ((t instanceof TagWithState))
/*     */     {
/* 101 */       getTagManager().tagCreated((TagWithState)t);
/*     */     }
/*     */     
/* 104 */     super.addTag(t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeTag(Tag t)
/*     */   {
/* 111 */     this.tags.remove(t);
/*     */     
/* 113 */     if ((t instanceof TagWithState))
/*     */     {
/* 115 */       getTagManager().tagRemoved((TagWithState)t);
/*     */     }
/*     */     
/* 118 */     super.removeTag(t);
/*     */   }
/*     */   
/*     */ 
/*     */   public List<Tag> getTags()
/*     */   {
/* 124 */     return this.tags.getList();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagTypeWithState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */