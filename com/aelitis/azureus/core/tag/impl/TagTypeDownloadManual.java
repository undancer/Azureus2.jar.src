/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagException;
/*     */ import com.aelitis.azureus.core.tag.TaggableResolver;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class TagTypeDownloadManual
/*     */   extends TagTypeWithState
/*     */ {
/*  37 */   private static final int[] color_default = { 0, 140, 66 };
/*     */   
/*  39 */   private final AtomicInteger next_tag_id = new AtomicInteger(0);
/*     */   
/*     */ 
/*     */ 
/*     */   protected TagTypeDownloadManual(TaggableResolver resolver)
/*     */   {
/*  45 */     super(3, resolver, 511, "tag.type.man");
/*     */     
/*  47 */     addTagType();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTagTypePersistent()
/*     */   {
/*  53 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTagTypeAuto()
/*     */   {
/*  59 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int[] getColorDefault()
/*     */   {
/*  66 */     return color_default;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Tag createTag(String name, boolean auto_add)
/*     */     throws TagException
/*     */   {
/*  77 */     TagDownloadWithState new_tag = new TagDownloadWithState(this, this.next_tag_id.incrementAndGet(), name, true, true, true, true, 11);
/*     */     
/*  79 */     new_tag.setSupportsTagTranscode(true);
/*  80 */     new_tag.setSupportsFileLocation(true);
/*     */     
/*  82 */     if (auto_add)
/*     */     {
/*  84 */       addTag(new_tag);
/*     */     }
/*     */     
/*  87 */     return new_tag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Tag createTag(int tag_id, Map details)
/*     */   {
/*  95 */     TagDownloadWithState new_tag = new TagDownloadWithState(this, tag_id, details, true, true, true, true, 11);
/*     */     
/*  97 */     new_tag.setSupportsTagTranscode(true);
/*  98 */     new_tag.setSupportsFileLocation(true);
/*     */     
/* 100 */     this.next_tag_id.set(Math.max(this.next_tag_id.get(), tag_id + 1));
/*     */     
/* 102 */     return new_tag;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagTypeDownloadManual.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */