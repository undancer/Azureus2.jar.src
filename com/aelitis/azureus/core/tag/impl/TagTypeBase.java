/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagException;
/*     */ import com.aelitis.azureus.core.tag.TagListener;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener;
/*     */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.TaggableResolver;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.ListenerManager;
/*     */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
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
/*     */ public abstract class TagTypeBase
/*     */   implements TagType, TagListener
/*     */ {
/*     */   protected static final String AT_COLOR_ID = "col.rgb";
/*     */   private final int tag_type;
/*     */   private final int tag_type_features;
/*     */   private final String tag_type_name;
/*     */   private static final int TTL_ADD = 1;
/*     */   private static final int TTL_CHANGE = 2;
/*     */   private static final int TTL_REMOVE = 3;
/*     */   private static final int TTL_TYPE_CHANGE = 4;
/*     */   private static final int TTL_ATTENTION_REQUESTED = 5;
/*  52 */   private static final TagManagerImpl manager = ;
/*     */   
/*  54 */   private final ListenerManager<TagTypeListener> tt_listeners = ListenerManager.createManager("TagTypeListeners", new ListenerManagerDispatcher()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void dispatch(TagTypeListener listener, int type, Object value)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  65 */       if (type == 4)
/*     */       {
/*  67 */         listener.tagTypeChanged(TagTypeBase.this);
/*     */       }
/*     */       else
/*     */       {
/*  71 */         final Tag tag = (Tag)value;
/*     */         
/*     */         int event_type;
/*  74 */         if (type == 1)
/*     */         {
/*  76 */           event_type = 0;
/*     */         } else { int event_type;
/*  78 */           if (type == 2)
/*     */           {
/*  80 */             event_type = 1;
/*     */           } else { int event_type;
/*  82 */             if (type == 3)
/*     */             {
/*  84 */               event_type = 2;
/*     */             } else { int event_type;
/*  86 */               if (type == 5)
/*     */               {
/*  88 */                 event_type = 3; } else {
/*     */                 return;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         final int event_type;
/*  95 */         listener.tagEventOccurred(new TagTypeListener.TagEvent()
/*     */         {
/*     */ 
/*     */           public Tag getTag()
/*     */           {
/* 100 */             return tag;
/*     */           }
/*     */           
/*     */           public int getEventType()
/*     */           {
/* 105 */             return event_type;
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*  54 */   });
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
/* 112 */   private final Map<Taggable, List<TagListener>> tag_listeners = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TagTypeBase(int _tag_type, int _tag_features, String _tag_name)
/*     */   {
/* 120 */     this.tag_type = _tag_type;
/* 121 */     this.tag_type_features = _tag_features;
/* 122 */     this.tag_type_name = _tag_name;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addTagType()
/*     */   {
/* 128 */     if (manager.isEnabled())
/*     */     {
/* 130 */       manager.addTagType(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TagManagerImpl getTagManager()
/*     */   {
/* 137 */     return manager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Taggable resolveTaggable(String id)
/*     */   {
/* 144 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeTaggable(TaggableResolver resolver, Taggable taggable)
/*     */   {
/* 152 */     synchronized (this.tag_listeners)
/*     */     {
/* 154 */       this.tag_listeners.remove(taggable);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTagType()
/*     */   {
/* 161 */     return this.tag_type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getTagTypeName(boolean localize)
/*     */   {
/* 168 */     if (localize)
/*     */     {
/* 170 */       if (this.tag_type_name.startsWith("tag."))
/*     */       {
/* 172 */         return MessageText.getString(this.tag_type_name);
/*     */       }
/*     */       
/*     */ 
/* 176 */       return this.tag_type_name;
/*     */     }
/*     */     
/*     */ 
/* 180 */     if (this.tag_type_name.startsWith("tag."))
/*     */     {
/* 182 */       return this.tag_type_name;
/*     */     }
/*     */     
/*     */ 
/* 186 */     return "!" + this.tag_type_name + "!";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTagTypeAuto()
/*     */   {
/* 194 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTagTypePersistent()
/*     */   {
/* 200 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTagTypeFeatures()
/*     */   {
/* 206 */     return this.tag_type_features;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasTagTypeFeature(long feature)
/*     */   {
/* 213 */     return (this.tag_type_features & feature) != 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void fireChanged()
/*     */   {
/* 219 */     this.tt_listeners.dispatch(4, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Tag createTag(String name, boolean auto_add)
/*     */     throws TagException
/*     */   {
/* 229 */     throw new TagException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addTag(Tag t)
/*     */   {
/* 236 */     ((TagBase)t).initialized();
/*     */     
/* 238 */     this.tt_listeners.dispatch(1, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeTag(Tag t)
/*     */   {
/* 245 */     ((TagBase)t).destroy();
/*     */     
/* 247 */     this.tt_listeners.dispatch(3, t);
/*     */     
/* 249 */     manager.removeConfig(t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void requestAttention(Tag t)
/*     */   {
/* 256 */     this.tt_listeners.dispatch(5, t);
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getColorDefault()
/*     */   {
/* 262 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void sync()
/*     */   {
/* 268 */     List<Tag> tags = getTags();
/*     */     
/* 270 */     for (Tag t : tags)
/*     */     {
/* 272 */       ((TagBase)t).sync();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void closing()
/*     */   {
/* 279 */     List<Tag> tags = getTags();
/*     */     
/* 281 */     for (Tag t : tags)
/*     */     {
/* 283 */       ((TagBase)t).closing();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Tag getTag(int tag_id)
/*     */   {
/* 291 */     for (Tag t : getTags())
/*     */     {
/* 293 */       if (t.getTagID() == tag_id)
/*     */       {
/* 295 */         return t;
/*     */       }
/*     */     }
/*     */     
/* 299 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Tag getTag(String tag_name, boolean is_localized)
/*     */   {
/* 307 */     for (Tag t : getTags())
/*     */     {
/* 309 */       if (t.getTagName(is_localized).equals(tag_name))
/*     */       {
/* 311 */         return t;
/*     */       }
/*     */     }
/*     */     
/* 315 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<Tag> getTagsForTaggable(Taggable taggable)
/*     */   {
/* 322 */     List<Tag> result = new ArrayList();
/*     */     
/* 324 */     int taggable_type = taggable.getTaggableType();
/*     */     
/* 326 */     for (Tag t : getTags())
/*     */     {
/* 328 */       if (t.getTaggableTypes() == taggable_type)
/*     */       {
/* 330 */         if (t.hasTaggable(taggable))
/*     */         {
/* 332 */           result.add(t);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 337 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void fireChanged(Tag t)
/*     */   {
/* 344 */     this.tt_listeners.dispatch(2, t);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeTagType()
/*     */   {
/* 350 */     manager.removeTagType(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTagTypeListener(TagTypeListener listener, boolean fire_for_existing)
/*     */   {
/* 358 */     this.tt_listeners.addListener(listener);
/*     */     
/* 360 */     if (fire_for_existing)
/*     */     {
/* 362 */       for (final Tag t : getTags()) {
/*     */         try
/*     */         {
/* 365 */           listener.tagEventOccurred(new TagTypeListener.TagEvent()
/*     */           {
/*     */ 
/*     */             public Tag getTag()
/*     */             {
/* 370 */               return t;
/*     */             }
/*     */             
/*     */             public int getEventType()
/*     */             {
/* 375 */               return 0;
/*     */             }
/*     */           });
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 381 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeTagTypeListener(TagTypeListener listener)
/*     */   {
/* 391 */     this.tt_listeners.removeListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void taggableAdded(Tag tag, Taggable tagged)
/*     */   {
/*     */     List<TagListener> listeners;
/*     */     
/*     */ 
/* 401 */     synchronized (this.tag_listeners)
/*     */     {
/* 403 */       listeners = (List)this.tag_listeners.get(tagged);
/*     */     }
/*     */     
/* 406 */     if (listeners != null)
/*     */     {
/* 408 */       for (TagListener l : listeners) {
/*     */         try
/*     */         {
/* 411 */           l.taggableAdded(tag, tagged);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 415 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 420 */     manager.taggableAdded(this, tag, tagged);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void taggableSync(Tag tag)
/*     */   {
/* 427 */     List<List<TagListener>> all_listeners = new ArrayList();
/*     */     
/* 429 */     synchronized (this.tag_listeners)
/*     */     {
/* 431 */       all_listeners.addAll(this.tag_listeners.values());
/*     */     }
/*     */     
/* 434 */     for (Object listeners : all_listeners)
/*     */     {
/* 436 */       for (TagListener listener : (List)listeners) {
/*     */         try
/*     */         {
/* 439 */           listener.taggableSync(tag);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 443 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void taggableRemoved(Tag tag, Taggable tagged)
/*     */   {
/*     */     List<TagListener> listeners;
/*     */     
/*     */ 
/* 456 */     synchronized (this.tag_listeners)
/*     */     {
/* 458 */       listeners = (List)this.tag_listeners.get(tagged);
/*     */     }
/*     */     
/* 461 */     if (listeners != null)
/*     */     {
/* 463 */       for (TagListener l : listeners) {
/*     */         try
/*     */         {
/* 466 */           l.taggableRemoved(tag, tagged);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 470 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 475 */     manager.taggableRemoved(this, tag, tagged);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTagListener(Taggable taggable, TagListener listener)
/*     */   {
/* 483 */     synchronized (this.tag_listeners)
/*     */     {
/* 485 */       List<TagListener> listeners = (List)this.tag_listeners.get(taggable);
/*     */       
/* 487 */       if (listeners == null)
/*     */       {
/* 489 */         listeners = new ArrayList();
/*     */       }
/*     */       else
/*     */       {
/* 493 */         listeners = new ArrayList(listeners);
/*     */       }
/*     */       
/* 496 */       listeners.add(listener);
/*     */       
/* 498 */       this.tag_listeners.put(taggable, listeners);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeTagListener(Taggable taggable, TagListener listener)
/*     */   {
/* 507 */     synchronized (this.tag_listeners)
/*     */     {
/* 509 */       List<TagListener> listeners = (List)this.tag_listeners.get(taggable);
/*     */       
/* 511 */       if (listeners != null)
/*     */       {
/* 513 */         listeners = new ArrayList(listeners);
/*     */         
/* 515 */         listeners.remove(listener);
/*     */         
/* 517 */         if (listeners.size() == 0)
/*     */         {
/* 519 */           this.tag_listeners.remove(taggable);
/*     */         }
/*     */         else
/*     */         {
/* 523 */           this.tag_listeners.put(taggable, listeners);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Boolean readBooleanAttribute(TagBase tag, String attr, Boolean def)
/*     */   {
/* 535 */     return manager.readBooleanAttribute(this, tag, attr, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean writeBooleanAttribute(TagBase tag, String attr, Boolean value)
/*     */   {
/* 544 */     return manager.writeBooleanAttribute(this, tag, attr, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Long readLongAttribute(TagBase tag, String attr, Long def)
/*     */   {
/* 553 */     return manager.readLongAttribute(this, tag, attr, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean writeLongAttribute(TagBase tag, String attr, Long value)
/*     */   {
/* 562 */     return manager.writeLongAttribute(this, tag, attr, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String readStringAttribute(TagBase tag, String attr, String def)
/*     */   {
/* 571 */     return manager.readStringAttribute(this, tag, attr, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeStringAttribute(TagBase tag, String attr, String value)
/*     */   {
/* 580 */     manager.writeStringAttribute(this, tag, attr, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String[] readStringListAttribute(TagBase tag, String attr, String[] def)
/*     */   {
/* 589 */     return manager.readStringListAttribute(this, tag, attr, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean writeStringListAttribute(TagBase tag, String attr, String[] value)
/*     */   {
/* 598 */     return manager.writeStringListAttribute(this, tag, attr, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 605 */     writer.println(this.tag_type_name);
/*     */     try
/*     */     {
/* 608 */       writer.indent();
/*     */       
/* 610 */       manager.generate(writer, this);
/*     */       
/* 612 */       List<Tag> tags = getTags();
/*     */       
/* 614 */       for (Tag t : tags)
/*     */       {
/* 616 */         ((TagBase)t).generate(writer);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 621 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void generateConfig(IndentWriter writer, TagBase tag)
/*     */   {
/* 630 */     manager.generate(writer, this, tag);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagTypeBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */