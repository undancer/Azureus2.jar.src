/*     */ package com.aelitis.azureus.core.tag.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagPropertyListener;
/*     */ import com.aelitis.azureus.core.tag.TagListener;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeAdapter;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.TaggableLifecycleAdapter;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.TrackersUtil;
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
/*     */ public class TagPropertyTrackerTemplateHandler
/*     */   implements TagFeatureProperties.TagPropertyListener, TagListener
/*     */ {
/*     */   final TagManagerImpl tag_manager;
/*     */   
/*     */   protected TagPropertyTrackerTemplateHandler(AzureusCore _core, TagManagerImpl _tm)
/*     */   {
/*  52 */     this.tag_manager = _tm;
/*     */     
/*  54 */     this.tag_manager.addTaggableLifecycleListener(2L, new TaggableLifecycleAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void initialised(List<Taggable> current_taggables)
/*     */       {
/*     */ 
/*     */ 
/*  62 */         TagType tt = TagPropertyTrackerTemplateHandler.this.tag_manager.getTagType(3);
/*     */         
/*  64 */         tt.addTagTypeListener(new TagTypeAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void tagAdded(Tag tag)
/*     */           {
/*     */ 
/*  71 */             TagFeatureProperties tfp = (TagFeatureProperties)tag;
/*     */             
/*  73 */             TagFeatureProperties.TagProperty prop = tfp.getProperty("tracker_templates");
/*     */             
/*  75 */             if (prop != null)
/*     */             {
/*  77 */               prop.addListener(TagPropertyTrackerTemplateHandler.this);
/*     */               
/*  79 */               tag.addTagListener(TagPropertyTrackerTemplateHandler.this, false); } } }, true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String[] getPropertyBits(TagFeatureProperties.TagProperty prop)
/*     */   {
/*  92 */     String[] bits = prop.getStringList();
/*     */     
/*  94 */     if ((bits == null) || (bits.length == 0))
/*     */     {
/*  96 */       return null;
/*     */     }
/*     */     
/*  99 */     return bits;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleStuff(String[] bits, Set<Taggable> taggables)
/*     */   {
/* 107 */     Map<String, List<List<String>>> templates = TrackersUtil.getInstance().getMultiTrackers();
/*     */     List<List<String>> template_trackers;
/* 109 */     String type; for (String bit : bits)
/*     */     {
/* 111 */       String[] temp = bit.split(":");
/*     */       
/* 113 */       String t_name = temp[1];
/*     */       
/* 115 */       template_trackers = (List)templates.get(t_name);
/*     */       
/* 117 */       if (template_trackers == null)
/*     */       {
/* 119 */         Debug.out("Tracker template '" + t_name + "' not found");
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 124 */         type = temp[0];
/*     */         
/* 126 */         for (Taggable t : taggables)
/*     */         {
/* 128 */           DownloadManager dm = (DownloadManager)t;
/*     */           
/* 130 */           TOTorrent torrent = dm.getTorrent();
/*     */           
/* 132 */           if (torrent != null)
/*     */           {
/* 134 */             List<List<String>> trackers = TorrentUtils.announceGroupsToList(torrent);
/*     */             
/* 136 */             if (type.equals("m"))
/*     */             {
/* 138 */               trackers = TorrentUtils.mergeAnnounceURLs(trackers, template_trackers);
/*     */             }
/* 140 */             else if (type.equals("r"))
/*     */             {
/* 142 */               trackers = template_trackers;
/*     */             }
/*     */             else
/*     */             {
/* 146 */               trackers = TorrentUtils.removeAnnounceURLs(trackers, template_trackers, true);
/*     */             }
/*     */             
/* 149 */             TorrentUtils.listToAnnounceGroups(trackers, torrent);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void propertyChanged(TagFeatureProperties.TagProperty property)
/*     */   {
/* 159 */     String[] bits = getPropertyBits(property);
/*     */     
/* 161 */     if (bits == null)
/*     */     {
/* 163 */       return;
/*     */     }
/*     */     
/* 166 */     handleStuff(bits, property.getTag().getTagged());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void propertySync(TagFeatureProperties.TagProperty property)
/*     */   {
/* 173 */     propertyChanged(property);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void taggableAdded(Tag tag, Taggable tagged)
/*     */   {
/* 181 */     TagFeatureProperties tfp = (TagFeatureProperties)tag;
/*     */     
/* 183 */     TagFeatureProperties.TagProperty prop = tfp.getProperty("tracker_templates");
/*     */     
/* 185 */     if (prop != null)
/*     */     {
/* 187 */       String[] bits = getPropertyBits(prop);
/*     */       
/* 189 */       if (bits == null)
/*     */       {
/* 191 */         return;
/*     */       }
/*     */       
/* 194 */       Set<Taggable> taggables = new HashSet();
/*     */       
/* 196 */       taggables.add(tagged);
/*     */       
/* 198 */       handleStuff(bits, taggables);
/*     */     }
/*     */   }
/*     */   
/*     */   public void taggableSync(Tag tag) {}
/*     */   
/*     */   public void taggableRemoved(Tag tag, Taggable tagged) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagPropertyTrackerTemplateHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */