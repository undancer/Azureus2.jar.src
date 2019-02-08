/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagPropertyListener;
/*     */ import com.aelitis.azureus.core.tag.TagListener;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeAdapter;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.TaggableLifecycleAdapter;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class TagPropertyUntaggedHandler
/*     */   implements TagTypeListener
/*     */ {
/*     */   private final AzureusCore azureus_core;
/*     */   final TagManagerImpl tag_manager;
/*     */   private boolean is_initialised;
/*     */   private boolean is_enabled;
/*  58 */   final Set<Tag> untagged_tags = new HashSet();
/*     */   
/*  60 */   final Map<Taggable, int[]> taggable_counts = new IdentityHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TagPropertyUntaggedHandler(AzureusCore _core, TagManagerImpl _tm)
/*     */   {
/*  68 */     this.azureus_core = _core;
/*  69 */     this.tag_manager = _tm;
/*     */     
/*  71 */     this.tag_manager.addTaggableLifecycleListener(2L, new TaggableLifecycleAdapter()
/*     */     {
/*     */ 
/*     */       public void initialised(List<Taggable> current_taggables)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*  80 */           TagType tt = TagPropertyUntaggedHandler.this.tag_manager.getTagType(3);
/*     */           
/*  82 */           tt.addTagTypeListener(new TagTypeAdapter()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void tagAdded(Tag tag)
/*     */             {
/*     */ 
/*  89 */               TagFeatureProperties tfp = (TagFeatureProperties)tag;
/*     */               
/*  91 */               TagFeatureProperties.TagProperty[] props = tfp.getSupportedProperties();
/*     */               
/*  93 */               for (TagFeatureProperties.TagProperty prop : props)
/*     */               {
/*  95 */                 if (prop.getName(false).equals("untagged"))
/*     */                 {
/*  97 */                   prop.addListener(new TagFeatureProperties.TagPropertyListener()
/*     */                   {
/*     */ 
/*     */ 
/*     */                     public void propertyChanged(TagFeatureProperties.TagProperty property)
/*     */                     {
/*     */ 
/* 104 */                       TagPropertyUntaggedHandler.this.handleProperty(property);
/*     */                     }
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void propertySync(TagFeatureProperties.TagProperty property) {}
/* 113 */                   });
/* 114 */                   TagPropertyUntaggedHandler.this.handleProperty(prop); } } } }, true);
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/*     */ 
/* 123 */           AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void azureusCoreRunning(AzureusCore core)
/*     */             {
/*     */ 
/* 130 */               synchronized (TagPropertyUntaggedHandler.this.taggable_counts)
/*     */               {
/* 132 */                 TagPropertyUntaggedHandler.this.is_initialised = true;
/*     */                 
/* 134 */                 if (TagPropertyUntaggedHandler.this.is_enabled)
/*     */                 {
/* 136 */                   TagPropertyUntaggedHandler.this.enable();
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void taggableCreated(Taggable taggable)
/*     */       {
/* 148 */         TagPropertyUntaggedHandler.this.addDownloads(Arrays.asList(new DownloadManager[] { (DownloadManager)taggable }));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tagTypeChanged(TagType tag_type) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void tagEventOccurred(TagTypeListener.TagEvent event)
/*     */   {
/* 162 */     int type = event.getEventType();
/* 163 */     Tag tag = event.getTag();
/* 164 */     if (type == 0) {
/* 165 */       tagAdded(tag);
/* 166 */     } else if (type == 2) {
/* 167 */       tagRemoved(tag);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tagAdded(Tag tag)
/*     */   {
/* 175 */     tag.addTagListener(new TagListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void taggableAdded(Tag tag, Taggable tagged)
/*     */       {
/*     */ 
/*     */ 
/* 183 */         synchronized (TagPropertyUntaggedHandler.this.taggable_counts)
/*     */         {
/* 185 */           if (TagPropertyUntaggedHandler.this.untagged_tags.contains(tag))
/*     */           {
/* 187 */             return;
/*     */           }
/*     */           
/* 190 */           int[] num = (int[])TagPropertyUntaggedHandler.this.taggable_counts.get(tagged);
/*     */           
/* 192 */           if (num == null)
/*     */           {
/* 194 */             num = new int[1];
/*     */             
/* 196 */             TagPropertyUntaggedHandler.this.taggable_counts.put(tagged, num);
/*     */           }
/*     */           
/* 199 */           int tmp76_75 = 0; int[] tmp76_73 = num; int tmp78_77 = tmp76_73[tmp76_75];tmp76_73[tmp76_75] = (tmp78_77 + 1); if (tmp78_77 == 0)
/*     */           {
/*     */ 
/*     */ 
/* 203 */             for (Tag t : TagPropertyUntaggedHandler.this.untagged_tags)
/*     */             {
/* 205 */               t.removeTaggable(tagged);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void taggableSync(Tag tag) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void taggableRemoved(Tag tag, Taggable tagged)
/*     */       {
/* 223 */         synchronized (TagPropertyUntaggedHandler.this.taggable_counts)
/*     */         {
/* 225 */           if (TagPropertyUntaggedHandler.this.untagged_tags.contains(tag))
/*     */           {
/* 227 */             return;
/*     */           }
/*     */           
/* 230 */           int[] num = (int[])TagPropertyUntaggedHandler.this.taggable_counts.get(tagged);
/*     */           
/* 232 */           if (num != null)
/*     */           {
/* 234 */             int tmp55_54 = 0; int[] tmp55_52 = num; int tmp57_56 = tmp55_52[tmp55_54];tmp55_52[tmp55_54] = (tmp57_56 - 1); if (tmp57_56 == 1)
/*     */             {
/*     */ 
/*     */ 
/* 238 */               TagPropertyUntaggedHandler.this.taggable_counts.remove(tagged);
/*     */               
/* 240 */               DownloadManager dm = (DownloadManager)tagged;
/*     */               
/* 242 */               if (!dm.isDestroyed())
/*     */               {
/* 244 */                 for (Tag t : TagPropertyUntaggedHandler.this.untagged_tags)
/*     */                 {
/* 246 */                   t.addTaggable(tagged); } } } } } } }, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 256 */     synchronized (this.taggable_counts)
/*     */     {
/* 258 */       if (this.untagged_tags.contains(tag))
/*     */       {
/* 260 */         return;
/*     */       }
/*     */     }
/*     */     
/* 264 */     Set<Taggable> existing = tag.getTagged();
/*     */     Iterator i$;
/* 266 */     Taggable tagged; synchronized (this.taggable_counts)
/*     */     {
/* 268 */       for (i$ = existing.iterator(); i$.hasNext();) { tagged = (Taggable)i$.next();
/*     */         
/* 270 */         int[] num = (int[])this.taggable_counts.get(tagged);
/*     */         
/* 272 */         if (num == null)
/*     */         {
/* 274 */           num = new int[1];
/*     */           
/* 276 */           this.taggable_counts.put(tagged, num);
/*     */         }
/*     */         
/* 279 */         int tmp135_134 = 0; int[] tmp135_132 = num; int tmp137_136 = tmp135_132[tmp135_134];tmp135_132[tmp135_134] = (tmp137_136 + 1); if (tmp137_136 == 0)
/*     */         {
/*     */ 
/*     */ 
/* 283 */           for (Tag t : this.untagged_tags)
/*     */           {
/* 285 */             t.removeTaggable(tagged);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tagRemoved(Tag tag)
/*     */   {
/* 296 */     synchronized (this.taggable_counts)
/*     */     {
/* 298 */       boolean was_untagged = this.untagged_tags.remove(tag);
/*     */       
/* 300 */       if (was_untagged)
/*     */       {
/* 302 */         if (this.untagged_tags.size() == 0)
/*     */         {
/* 304 */           setEnabled(tag, false);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void enable()
/*     */   {
/* 313 */     TagType tt = this.tag_manager.getTagType(3);
/*     */     
/* 315 */     tt.addTagTypeListener(this, false);
/*     */     
/* 317 */     for (Tag tag : tt.getTags())
/*     */     {
/* 319 */       tagAdded(tag);
/*     */     }
/*     */     
/* 322 */     List<DownloadManager> existing = this.azureus_core.getGlobalManager().getDownloadManagers();
/*     */     
/* 324 */     addDownloads(existing);
/*     */   }
/*     */   
/*     */ 
/*     */   private void disable()
/*     */   {
/* 330 */     TagType tt = this.tag_manager.getTagType(3);
/*     */     
/* 332 */     tt.removeTagTypeListener(this);
/*     */     
/* 334 */     this.taggable_counts.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setEnabled(Tag current_tag, boolean enabled)
/*     */   {
/* 342 */     if (enabled == this.is_enabled)
/*     */     {
/* 344 */       if (this.is_enabled)
/*     */       {
/* 346 */         if (this.untagged_tags.size() < 2)
/*     */         {
/* 348 */           Debug.out("eh?");
/*     */           
/* 350 */           return;
/*     */         }
/*     */         
/* 353 */         Set<Taggable> existing = current_tag.getTagged();
/*     */         
/* 355 */         for (Taggable t : existing)
/*     */         {
/* 357 */           current_tag.removeTaggable(t);
/*     */         }
/*     */         
/* 360 */         Tag[] temp = (Tag[])this.untagged_tags.toArray(new Tag[this.untagged_tags.size()]);
/*     */         
/* 362 */         Tag copy_from = temp[0] == current_tag ? temp[1] : temp[0];
/*     */         
/* 364 */         for (Taggable t : copy_from.getTagged())
/*     */         {
/* 366 */           current_tag.addTaggable(t);
/*     */         }
/*     */       }
/*     */       
/* 370 */       return;
/*     */     }
/*     */     
/* 373 */     this.is_enabled = enabled;
/*     */     
/* 375 */     if (enabled)
/*     */     {
/* 377 */       if (this.is_initialised)
/*     */       {
/* 379 */         enable();
/*     */       }
/*     */     }
/*     */     else {
/* 383 */       disable();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void handleProperty(TagFeatureProperties.TagProperty property)
/*     */   {
/* 391 */     Tag tag = property.getTag();
/*     */     
/* 393 */     synchronized (this.taggable_counts)
/*     */     {
/* 395 */       Boolean val = property.getBoolean();
/*     */       
/* 397 */       if ((val != null) && (val.booleanValue()))
/*     */       {
/* 399 */         this.untagged_tags.add(tag);
/*     */         
/* 401 */         setEnabled(tag, true);
/*     */       }
/*     */       else
/*     */       {
/* 405 */         boolean was_untagged = this.untagged_tags.remove(tag);
/*     */         
/* 407 */         if (this.untagged_tags.size() == 0)
/*     */         {
/* 409 */           setEnabled(tag, false);
/*     */         }
/*     */         
/* 412 */         if (was_untagged)
/*     */         {
/* 414 */           Set<Taggable> existing = tag.getTagged();
/*     */           
/* 416 */           for (Taggable t : existing)
/*     */           {
/* 418 */             tag.removeTaggable(t);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void addDownloads(List<DownloadManager> dms)
/*     */   {
/*     */     Iterator i$;
/*     */     DownloadManager dm;
/* 429 */     synchronized (this.taggable_counts)
/*     */     {
/* 431 */       if (!this.is_enabled)
/*     */       {
/* 433 */         return;
/*     */       }
/*     */       
/* 436 */       for (i$ = dms.iterator(); i$.hasNext();) { dm = (DownloadManager)i$.next();
/*     */         
/* 438 */         if (dm.isPersistent())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 443 */           if (!this.taggable_counts.containsKey(dm))
/*     */           {
/* 445 */             for (Tag t : this.untagged_tags)
/*     */             {
/* 447 */               t.addTaggable(dm);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected List<Tag> getUntaggedTags()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 239	com/aelitis/azureus/core/tag/impl/TagPropertyUntaggedHandler:taggable_counts	Ljava/util/Map;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: new 129	java/util/ArrayList
/*     */     //   10: dup
/*     */     //   11: aload_0
/*     */     //   12: getfield 240	com/aelitis/azureus/core/tag/impl/TagPropertyUntaggedHandler:untagged_tags	Ljava/util/Set;
/*     */     //   15: invokespecial 254	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: areturn
/*     */     //   21: astore_2
/*     */     //   22: aload_1
/*     */     //   23: monitorexit
/*     */     //   24: aload_2
/*     */     //   25: athrow
/*     */     // Line number table:
/*     */     //   Java source line #457	-> byte code offset #0
/*     */     //   Java source line #459	-> byte code offset #7
/*     */     //   Java source line #460	-> byte code offset #21
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	26	0	this	TagPropertyUntaggedHandler
/*     */     //   5	18	1	Ljava/lang/Object;	Object
/*     */     //   21	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	20	21	finally
/*     */     //   21	24	21	finally
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagPropertyUntaggedHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */