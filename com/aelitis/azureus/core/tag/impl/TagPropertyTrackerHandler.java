/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagPropertyListener;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeAdapter;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.TaggableLifecycleAdapter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class TagPropertyTrackerHandler
/*     */   implements TagFeatureProperties.TagPropertyListener
/*     */ {
/*     */   private final AzureusCore azureus_core;
/*     */   final TagManagerImpl tag_manager;
/*  50 */   private final Map<String, List<Tag>> tracker_host_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TagPropertyTrackerHandler(AzureusCore _core, TagManagerImpl _tm)
/*     */   {
/*  57 */     this.azureus_core = _core;
/*  58 */     this.tag_manager = _tm;
/*     */     
/*  60 */     this.tag_manager.addTaggableLifecycleListener(2L, new TaggableLifecycleAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void initialised(List<Taggable> current_taggables)
/*     */       {
/*     */ 
/*     */ 
/*  68 */         TagType tt = TagPropertyTrackerHandler.this.tag_manager.getTagType(3);
/*     */         
/*  70 */         tt.addTagTypeListener(new TagTypeAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void tagAdded(Tag tag)
/*     */           {
/*     */ 
/*  77 */             TagFeatureProperties tfp = (TagFeatureProperties)tag;
/*     */             
/*  79 */             TagFeatureProperties.TagProperty[] props = tfp.getSupportedProperties();
/*     */             
/*  81 */             for (TagFeatureProperties.TagProperty prop : props)
/*     */             {
/*  83 */               if (prop.getName(false).equals("trackers"))
/*     */               {
/*  85 */                 TagPropertyTrackerHandler.this.hookTagProperty(prop);
/*     */                 
/*  87 */                 break; } } } }, true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void taggableCreated(Taggable taggable)
/*     */       {
/*  99 */         TagPropertyTrackerHandler.this.handleDownload((DownloadManager)taggable);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void hookTagProperty(TagFeatureProperties.TagProperty property)
/*     */   {
/* 108 */     property.addListener(this);
/*     */     
/* 110 */     handleProperty(property, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void propertyChanged(TagFeatureProperties.TagProperty property)
/*     */   {
/* 117 */     handleProperty(property, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void propertySync(TagFeatureProperties.TagProperty property) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleProperty(TagFeatureProperties.TagProperty property, boolean start_of_day)
/*     */   {
/* 131 */     String[] trackers = property.getStringList();
/*     */     
/* 133 */     Set<String> tag_hosts = new HashSet(Arrays.asList(trackers));
/*     */     
/* 135 */     Tag tag = property.getTag();
/*     */     
/* 137 */     synchronized (this.tracker_host_map)
/*     */     {
/* 139 */       for (Map.Entry<String, List<Tag>> entry : this.tracker_host_map.entrySet())
/*     */       {
/* 141 */         List<Tag> tags = (List)entry.getValue();
/*     */         
/* 143 */         if (tags.contains(tag))
/*     */         {
/* 145 */           if (!tag_hosts.contains(entry.getKey()))
/*     */           {
/* 147 */             tags.remove(tag);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 152 */       for (String host : tag_hosts)
/*     */       {
/* 154 */         List<Tag> tags = (List)this.tracker_host_map.get(host);
/*     */         
/* 156 */         if (tags == null)
/*     */         {
/* 158 */           tags = new ArrayList();
/*     */           
/* 160 */           this.tracker_host_map.put(host, tags);
/*     */         } else {
/* 162 */           if (tags.contains(tag)) {
/*     */             continue;
/*     */           }
/*     */         }
/*     */         
/* 167 */         tags.add(tag);
/*     */       }
/*     */     }
/*     */     
/* 171 */     if (start_of_day)
/*     */     {
/* 173 */       return;
/*     */     }
/*     */     
/* 176 */     Set<Taggable> tag_dls = tag.getTagged();
/*     */     
/* 178 */     for (Taggable tag_dl : tag_dls)
/*     */     {
/* 180 */       DownloadManager dm = (DownloadManager)tag_dl;
/*     */       
/* 182 */       Object hosts = getAugmentedHosts(dm);
/*     */       
/* 184 */       boolean hit = false;
/*     */       
/* 186 */       for (String host : (Set)hosts)
/*     */       {
/* 188 */         if (tag_hosts.contains(host))
/*     */         {
/* 190 */           hit = true;
/*     */           
/* 192 */           break;
/*     */         }
/*     */       }
/*     */       
/* 196 */       if (!hit)
/*     */       {
/* 198 */         tag.removeTaggable(tag_dl);
/*     */       }
/*     */     }
/*     */     
/* 202 */     List<DownloadManager> managers = this.azureus_core.getGlobalManager().getDownloadManagers();
/*     */     
/* 204 */     for (DownloadManager dm : managers)
/*     */     {
/* 206 */       if ((dm.isPersistent()) && 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 211 */         (!tag.hasTaggable(dm)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 216 */         Object hosts = getAugmentedHosts(dm);
/*     */         
/* 218 */         boolean hit = false;
/*     */         
/* 220 */         for (String host : (Set)hosts)
/*     */         {
/* 222 */           if (tag_hosts.contains(host))
/*     */           {
/* 224 */             hit = true;
/*     */             
/* 226 */             break;
/*     */           }
/*     */         }
/*     */         
/* 230 */         if (hit)
/*     */         {
/* 232 */           tag.addTaggable(dm);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private Set<String> getAugmentedHosts(DownloadManager dm)
/*     */   {
/* 241 */     Set<String> hosts = TorrentUtils.getUniqueTrackerHosts(dm.getTorrent());
/*     */     
/* 243 */     Set<String> result = new HashSet();
/*     */     
/*     */ 
/*     */ 
/* 247 */     for (String host : hosts)
/*     */     {
/* 249 */       result.add(host);
/*     */       
/* 251 */       String[] bits = host.split("\\.");
/*     */       
/* 253 */       String suffix = "";
/*     */       
/* 255 */       for (int i = bits.length - 1; i > 0; i--)
/*     */       {
/* 257 */         String bit = bits[i];
/*     */         
/* 259 */         if (suffix == "")
/*     */         {
/* 261 */           suffix = bit;
/*     */         }
/*     */         else
/*     */         {
/* 265 */           suffix = bit + "." + suffix;
/*     */         }
/*     */         
/* 268 */         result.add(suffix);
/*     */       }
/*     */     }
/*     */     
/* 272 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected List<Tag> getTagsForDownload(DownloadManager dm)
/*     */   {
/* 279 */     List<Tag> result = new ArrayList();
/*     */     
/* 281 */     if (dm.isPersistent())
/*     */     {
/* 283 */       synchronized (this.tracker_host_map)
/*     */       {
/* 285 */         if (this.tracker_host_map.size() > 0)
/*     */         {
/* 287 */           Set<String> hosts = getAugmentedHosts(dm);
/*     */           
/* 289 */           for (String host : hosts)
/*     */           {
/* 291 */             List<Tag> tags = (List)this.tracker_host_map.get(host);
/*     */             
/* 293 */             if (tags != null)
/*     */             {
/* 295 */               result.addAll(tags);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 302 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void handleDownload(DownloadManager dm)
/*     */   {
/* 309 */     List<Tag> applicable_tags = getTagsForDownload(dm);
/*     */     
/* 311 */     for (Tag tag : applicable_tags)
/*     */     {
/* 313 */       if (!tag.hasTaggable(dm))
/*     */       {
/* 315 */         tag.addTaggable(dm);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagPropertyTrackerHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */