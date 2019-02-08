/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagException;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureNotifications;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.TaggableResolver;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.LocalActivityCallback;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public abstract class TagWithState
/*     */   extends TagBase
/*     */ {
/*     */   private static final String TP_KEY = "TagWithState:tp_key";
/*  55 */   private final CopyOnWriteSet<Taggable> objects = new CopyOnWriteSet(true);
/*     */   
/*     */ 
/*     */   private final String TP_KEY_TAG_ADDED_TIME;
/*     */   
/*     */ 
/*     */   private TagFeatureNotifications tag_notifications;
/*     */   
/*     */ 
/*     */   private boolean removed;
/*     */   
/*     */ 
/*     */   public TagWithState(TagTypeBase tt, int tag_id, String name)
/*     */   {
/*  69 */     super(tt, tag_id, name);
/*     */     
/*  71 */     this.TP_KEY_TAG_ADDED_TIME = ("ta:" + getTagUID());
/*     */     
/*  73 */     if (tt.hasTagTypeFeature(256L))
/*     */     {
/*  75 */       this.tag_notifications = ((TagFeatureNotifications)this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TagWithState(TagTypeBase tt, int tag_id, Map map)
/*     */   {
/*  85 */     super(tt, tag_id, MapUtils.getMapString(map, "n", ""));
/*     */     
/*  87 */     this.TP_KEY_TAG_ADDED_TIME = ("ta:" + getTagUID());
/*     */     
/*  89 */     if (tt.hasTagTypeFeature(256L))
/*     */     {
/*  91 */       this.tag_notifications = ((TagFeatureNotifications)this); }
/*     */     List<Map> props;
/*     */     int pos;
/*  94 */     if (map != null)
/*     */     {
/*  96 */       List<byte[]> list = (List)map.get("o");
/*  97 */       props = (List)map.get("p");
/*     */       
/*  99 */       if (list != null)
/*     */       {
/* 101 */         pos = 0;
/*     */         
/* 103 */         for (byte[] b : list)
/*     */         {
/*     */           try {
/* 106 */             String id = new String(b, "UTF-8");
/*     */             
/* 108 */             Taggable taggable = tt.resolveTaggable(id);
/*     */             
/* 110 */             if (taggable != null)
/*     */             {
/* 112 */               if (props != null)
/*     */               {
/* 114 */                 Long time_added = (Long)((Map)props.get(pos)).get("a");
/*     */                 
/* 116 */                 if (time_added != null)
/*     */                 {
/* 118 */                   synchronized ("TagWithState:tp_key")
/*     */                   {
/* 120 */                     Map all_props = (Map)taggable.getTaggableTransientProperty("TagWithState:tp_key");
/*     */                     
/* 122 */                     if (all_props == null)
/*     */                     {
/* 124 */                       all_props = new HashMap();
/*     */                     }
/*     */                     
/* 127 */                     all_props.put(this.TP_KEY_TAG_ADDED_TIME, time_added);
/*     */                     
/* 129 */                     taggable.setTaggableTransientProperty("TagWithState:tp_key", all_props);
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 134 */               this.objects.add(taggable);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 138 */             Debug.out(e);
/*     */           }
/*     */           
/* 141 */           pos++;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exportDetails(Map map, boolean do_contents)
/*     */   {
/* 152 */     MapUtils.setMapString(map, "n", getTagNameRaw());
/*     */     
/* 154 */     if (do_contents)
/*     */     {
/* 156 */       Iterator<Taggable> it = this.objects.iterator();
/*     */       
/* 158 */       List<byte[]> l = new ArrayList(this.objects.size());
/* 159 */       List<Map> p = new ArrayList(this.objects.size());
/*     */       
/* 161 */       while (it.hasNext()) {
/*     */         try
/*     */         {
/* 164 */           Taggable taggable = (Taggable)it.next();
/*     */           
/* 166 */           String id = taggable.getTaggableID();
/*     */           
/* 168 */           if (id != null)
/*     */           {
/* 170 */             l.add(id.getBytes("UTF-8"));
/*     */             
/* 172 */             Map all_props = (Map)taggable.getTaggableTransientProperty("TagWithState:tp_key");
/*     */             
/* 174 */             Map props = new HashMap();
/*     */             
/* 176 */             if (all_props != null)
/*     */             {
/* 178 */               Long time_added = (Long)all_props.get(this.TP_KEY_TAG_ADDED_TIME);
/*     */               
/* 180 */               if (time_added != null)
/*     */               {
/* 182 */                 props.put("a", time_added);
/*     */               }
/*     */             }
/*     */             
/* 186 */             p.add(props);
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/* 195 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */       
/* 199 */       map.put("o", l);
/* 200 */       map.put("p", p);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTagName(String name)
/*     */     throws TagException
/*     */   {
/* 210 */     super.setTagName(name);
/*     */     
/* 212 */     getManager().tagChanged(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getTaggableAddedTime(Taggable taggble)
/*     */   {
/* 220 */     Map all_props = (Map)taggble.getTaggableTransientProperty("TagWithState:tp_key");
/*     */     
/* 222 */     if (all_props != null)
/*     */     {
/* 224 */       Long added_time = (Long)all_props.get(this.TP_KEY_TAG_ADDED_TIME);
/*     */       
/* 226 */       if (added_time != null)
/*     */       {
/* 228 */         return added_time.longValue() * 1000L;
/*     */       }
/*     */     }
/*     */     
/* 232 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addTaggable(Taggable t)
/*     */   {
/* 239 */     if (this.removed)
/*     */     {
/* 241 */       Debug.out("Tag has been removed");
/*     */       
/* 243 */       return;
/*     */     }
/*     */     
/* 246 */     boolean added = this.objects.add(t);
/*     */     
/* 248 */     if (added)
/*     */     {
/* 250 */       if (getTagType().isTagTypePersistent())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 255 */         synchronized ("TagWithState:tp_key")
/*     */         {
/* 257 */           Map all_props = (Map)t.getTaggableTransientProperty("TagWithState:tp_key");
/*     */           
/* 259 */           if (all_props == null)
/*     */           {
/* 261 */             all_props = new HashMap();
/*     */           }
/*     */           
/* 264 */           all_props.put(this.TP_KEY_TAG_ADDED_TIME, Long.valueOf(SystemTime.getCurrentTime() / 1000L));
/*     */           
/* 266 */           t.setTaggableTransientProperty("TagWithState:tp_key", all_props);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 271 */     super.addTaggable(t);
/*     */     
/* 273 */     if (added)
/*     */     {
/* 275 */       getManager().tagContentsChanged(this);
/*     */       
/* 277 */       if (this.tag_notifications != null)
/*     */       {
/* 279 */         checkNotifications(t, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeTaggable(Taggable t)
/*     */   {
/* 288 */     boolean removed = this.objects.remove(t);
/*     */     
/* 290 */     super.removeTaggable(t);
/*     */     
/* 292 */     if (removed)
/*     */     {
/* 294 */       getManager().tagContentsChanged(this);
/*     */       
/* 296 */       if (this.tag_notifications != null)
/*     */       {
/* 298 */         checkNotifications(t, false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void checkNotifications(Taggable taggable, boolean is_add)
/*     */   {
/* 308 */     int flags = getPostingNotifications();
/*     */     
/* 310 */     if (flags != 0)
/*     */     {
/* 312 */       boolean add = (flags & 0x1) != 0;
/* 313 */       boolean rem = (flags & 0x2) != 0;
/*     */       
/* 315 */       if (add != is_add) { if (rem != (!is_add)) {}
/*     */       } else {
/* 317 */         AZ3Functions.provider provider = AZ3Functions.getProvider();
/*     */         
/* 319 */         if (provider != null)
/*     */         {
/*     */ 
/*     */ 
/* 323 */           TaggableResolver resolver = taggable.getTaggableResolver();
/*     */           String name;
/* 325 */           if (resolver != null)
/*     */           {
/* 327 */             name = resolver.getDisplayName(taggable);
/*     */           }
/*     */           else
/*     */           {
/* 331 */             name = taggable.toString();
/*     */           }
/*     */           
/* 334 */           String name = MessageText.getString(is_add ? "tag.notification.added" : "tag.notification.removed", new String[] { name, getTagName(true) });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 341 */           Map<String, String> cb_data = new HashMap();
/*     */           
/* 343 */           cb_data.put("allowReAdd", "true");
/* 344 */           cb_data.put("taguid", String.valueOf(getTagUID()));
/* 345 */           cb_data.put("id", String.valueOf(taggable.getTaggableID()));
/*     */           
/* 347 */           String icon_id = "image.sidebar.tag-green";
/*     */           
/* 349 */           int[] color = getColor();
/*     */           
/* 351 */           if ((color != null) && (color.length == 3))
/*     */           {
/* 353 */             long rgb = color[0] << 16 | color[1] << 8 | color[2];
/*     */             
/* 355 */             String hex = Long.toHexString(rgb);
/*     */             
/* 357 */             while (hex.length() < 6)
/*     */             {
/* 359 */               hex = "0" + hex;
/*     */             }
/*     */             
/* 362 */             icon_id = icon_id + "#" + hex;
/*     */           }
/*     */           
/* 365 */           provider.addLocalActivity(getTagUID() + ":" + taggable.getTaggableID() + ":" + is_add, icon_id, name, new String[] { MessageText.getString("label.view") }, ActivityCallback.class, cb_data);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
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
/*     */   public static class ActivityCallback
/*     */     implements AZ3Functions.provider.LocalActivityCallback
/*     */   {
/*     */     public void actionSelected(String action, Map<String, String> data)
/*     */     {
/* 385 */       String taguid = (String)data.get("taguid");
/*     */       
/* 387 */       final String id = (String)data.get("id");
/*     */       
/* 389 */       if ((taguid != null) && (id != null)) {
/*     */         try
/*     */         {
/* 392 */           Tag tag = TagManagerFactory.getTagManager().lookupTagByUID(Long.parseLong(taguid));
/*     */           
/* 394 */           if (tag != null)
/*     */           {
/* 396 */             TagType tt = tag.getTagType();
/*     */             
/* 398 */             if ((tt instanceof TagTypeWithState))
/*     */             {
/* 400 */               final TaggableResolver resolver = ((TagTypeWithState)tt).getResolver();
/*     */               
/* 402 */               if (resolver != null)
/*     */               {
/* 404 */                 if (!tag.isVisible())
/*     */                 {
/* 406 */                   tag.setVisible(true);
/*     */                 }
/*     */                 
/* 409 */                 tag.requestAttention();
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 414 */                 SimpleTimer.addEvent("async", SystemTime.getOffsetTime(500L), new TimerEventPerformer()
/*     */                 {
/*     */ 
/*     */ 
/*     */                   public void perform(TimerEvent event)
/*     */                   {
/*     */ 
/* 421 */                     resolver.requestAttention(id);
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 430 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeTag()
/*     */   {
/* 440 */     super.removeTag();
/*     */     
/* 442 */     this.removed = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isRemoved()
/*     */   {
/* 448 */     return this.removed;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTaggedCount()
/*     */   {
/* 454 */     return this.objects.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasTaggable(Taggable t)
/*     */   {
/* 461 */     return this.objects.contains(t);
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<Taggable> getTagged()
/*     */   {
/* 467 */     return this.objects.getSet();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagWithState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */