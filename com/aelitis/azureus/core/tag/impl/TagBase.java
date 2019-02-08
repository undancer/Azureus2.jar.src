/*      */ package com.aelitis.azureus.core.tag.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagException;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureFileLocation;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureLimits;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagPropertyListener;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRSSFeed;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer.TimerTickReceiver;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class TagBase
/*      */   implements Tag, SimpleTimer.TimerTickReceiver
/*      */ {
/*      */   protected static final String AT_RATELIMIT_UP = "rl.up";
/*      */   protected static final String AT_RATELIMIT_DOWN = "rl.down";
/*      */   protected static final String AT_VISIBLE = "vis";
/*      */   protected static final String AT_PUBLIC = "pub";
/*      */   protected static final String AT_GROUP = "gr";
/*      */   protected static final String AT_CAN_BE_PUBLIC = "canpub";
/*      */   protected static final String AT_ORIGINAL_NAME = "oname";
/*      */   protected static final String AT_IMAGE_ID = "img.id";
/*      */   protected static final String AT_COLOR_ID = "col.rgb";
/*      */   protected static final String AT_RSS_ENABLE = "rss.enable";
/*      */   protected static final String AT_RATELIMIT_UP_PRI = "rl.uppri";
/*      */   protected static final String AT_XCODE_TARGET = "xcode.to";
/*      */   protected static final String AT_FL_MOVE_COMP = "fl.comp";
/*      */   protected static final String AT_FL_MOVE_COMP_OPT = "fl.comp.o";
/*      */   protected static final String AT_FL_COPY_COMP = "fl.copy";
/*      */   protected static final String AT_FL_COPY_COMP_OPT = "fl.copy.o";
/*      */   protected static final String AT_FL_INIT_LOC = "fl.init";
/*      */   protected static final String AT_FL_INIT_LOC_OPT = "fl.init.o";
/*      */   protected static final String AT_RATELIMIT_MIN_SR = "rl.minsr";
/*      */   protected static final String AT_RATELIMIT_MAX_SR = "rl.maxsr";
/*      */   protected static final String AT_RATELIMIT_MAX_SR_ACTION = "rl.maxsr.a";
/*      */   protected static final String AT_RATELIMIT_MAX_AGGREGATE_SR = "rl.maxaggsr";
/*      */   protected static final String AT_RATELIMIT_MAX_AGGREGATE_SR_ACTION = "rl.maxaggsr.a";
/*      */   protected static final String AT_RATELIMIT_MAX_AGGREGATE_SR_PRIORITY = "rl.maxaggsr.p";
/*      */   protected static final String AT_PROPERTY_PREFIX = "pp.";
/*      */   protected static final String AT_EOA_PREFIX = "eoa.";
/*      */   protected static final String AT_BYTES_UP = "b.up";
/*      */   protected static final String AT_BYTES_DOWN = "b.down";
/*      */   protected static final String AT_DESCRIPTION = "desc";
/*      */   protected static final String AT_MAX_TAGGABLES = "max.t";
/*      */   protected static final String AT_REMOVAL_STRATEGY = "max.t.r";
/*      */   protected static final String AT_EOS_SCRIPT = "eos.scr";
/*      */   protected static final String AT_NOTIFICATION_POST = "noti.post";
/*      */   protected static final String AT_LIMIT_ORDERING = "max.t.o";
/*   93 */   private static final String[] EMPTY_STRING_LIST = new String[0];
/*      */   
/*      */   final TagTypeBase tag_type;
/*      */   
/*      */   private final int tag_id;
/*      */   
/*      */   private String tag_name;
/*      */   
/*      */   private static final int TL_ADD = 1;
/*      */   private static final int TL_REMOVE = 2;
/*      */   private static final int TL_SYNC = 3;
/*  104 */   private final ListenerManager<com.aelitis.azureus.core.tag.TagListener> t_listeners = ListenerManager.createManager("TagListeners", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(com.aelitis.azureus.core.tag.TagListener listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  115 */       if (type == 1)
/*      */       {
/*  117 */         listener.taggableAdded(TagBase.this, (com.aelitis.azureus.core.tag.Taggable)value);
/*      */       }
/*  119 */       else if (type == 2)
/*      */       {
/*  121 */         listener.taggableRemoved(TagBase.this, (com.aelitis.azureus.core.tag.Taggable)value);
/*      */       }
/*  123 */       else if (type == 3)
/*      */       {
/*  125 */         listener.taggableSync(TagBase.this);
/*      */       }
/*      */     }
/*  104 */   });
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  130 */   private final Map<org.gudy.azureus2.plugins.tag.TagListener, com.aelitis.azureus.core.tag.TagListener> listener_map = new HashMap();
/*      */   
/*      */   private Boolean is_visible;
/*      */   
/*      */   private Boolean is_public;
/*      */   
/*      */   private String group;
/*      */   
/*      */   private int[] colour;
/*      */   
/*      */   private String description;
/*      */   
/*      */   private TagFeatureRateLimit tag_rl;
/*      */   
/*      */   private TagFeatureRSSFeed tag_rss;
/*      */   
/*      */   private TagFeatureFileLocation tag_fl;
/*      */   private TagFeatureLimits tag_limits;
/*      */   private HashMap<String, Object> transient_properties;
/*      */   
/*      */   protected TagBase(TagTypeBase _tag_type, int _tag_id, String _tag_name)
/*      */   {
/*  152 */     this.tag_type = _tag_type;
/*  153 */     this.tag_id = _tag_id;
/*  154 */     this.tag_name = _tag_name;
/*      */     
/*  156 */     if (getManager().isEnabled())
/*      */     {
/*  158 */       this.is_visible = readBooleanAttribute("vis", null);
/*  159 */       this.is_public = readBooleanAttribute("pub", null);
/*  160 */       this.group = readStringAttribute("gr", null);
/*  161 */       this.description = readStringAttribute("desc", null);
/*      */       
/*  163 */       if ((this instanceof TagFeatureRateLimit))
/*      */       {
/*  165 */         this.tag_rl = ((TagFeatureRateLimit)this);
/*      */       }
/*      */       
/*  168 */       if ((this instanceof TagFeatureRSSFeed))
/*      */       {
/*  170 */         this.tag_rss = ((TagFeatureRSSFeed)this);
/*      */         
/*  172 */         if (this.tag_rss.isTagRSSFeedEnabled())
/*      */         {
/*  174 */           getManager().checkRSSFeeds(this, true);
/*      */         }
/*      */       }
/*      */       
/*  178 */       if ((this instanceof TagFeatureFileLocation))
/*      */       {
/*  180 */         this.tag_fl = ((TagFeatureFileLocation)this);
/*      */       }
/*      */       
/*  183 */       if ((this instanceof TagFeatureLimits))
/*      */       {
/*  185 */         this.tag_limits = ((TagFeatureLimits)this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialized()
/*      */   {
/*  193 */     loadPersistentStuff();
/*      */     
/*  195 */     loadTransientStuff();
/*      */   }
/*      */   
/*      */ 
/*      */   public Tag getTag()
/*      */   {
/*  201 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void addTag()
/*      */   {
/*  207 */     if (getManager().isEnabled())
/*      */     {
/*  209 */       this.tag_type.addTag(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected TagManagerImpl getManager()
/*      */   {
/*  216 */     return this.tag_type.getTagManager();
/*      */   }
/*      */   
/*      */ 
/*      */   public TagTypeBase getTagType()
/*      */   {
/*  222 */     return this.tag_type;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagID()
/*      */   {
/*  228 */     return this.tag_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTagUID()
/*      */   {
/*  234 */     return getTagType().getTagType() << 32 | this.tag_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTagName()
/*      */   {
/*  240 */     return getTagName(true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getTagNameRaw()
/*      */   {
/*  246 */     return this.tag_name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getTagName(boolean localize)
/*      */   {
/*  253 */     if (localize)
/*      */     {
/*  255 */       if (this.tag_name.startsWith("tag."))
/*      */       {
/*  257 */         return MessageText.getString(this.tag_name);
/*      */       }
/*      */       
/*      */ 
/*  261 */       return this.tag_name;
/*      */     }
/*      */     
/*      */ 
/*  265 */     if (this.tag_name.startsWith("tag."))
/*      */     {
/*  267 */       return this.tag_name;
/*      */     }
/*      */     
/*      */ 
/*  271 */     String original_name = readStringAttribute("oname", null);
/*      */     
/*  273 */     if ((original_name != null) && (original_name.startsWith("tag.")))
/*      */     {
/*  275 */       return original_name;
/*      */     }
/*      */     
/*  278 */     return "!" + this.tag_name + "!";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTagName(String name)
/*      */     throws TagException
/*      */   {
/*  289 */     if (getTagType().isTagTypeAuto())
/*      */     {
/*  291 */       throw new TagException("Not supported");
/*      */     }
/*      */     
/*  294 */     if (this.tag_name.startsWith("tag."))
/*      */     {
/*  296 */       String original_name = readStringAttribute("oname", null);
/*      */       
/*  298 */       if (original_name == null)
/*      */       {
/*  300 */         writeStringAttribute("oname", this.tag_name);
/*      */       }
/*      */     }
/*      */     
/*  304 */     this.tag_name = name;
/*      */     
/*  306 */     this.tag_type.fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isPublic()
/*      */   {
/*  314 */     boolean pub = this.is_public == null ? getPublicDefault() : this.is_public.booleanValue();
/*      */     
/*  316 */     if (pub)
/*      */     {
/*  318 */       boolean[] autos = isTagAuto();
/*      */       
/*  320 */       if ((autos[0] != 0) || (autos[1] != 0))
/*      */       {
/*  322 */         pub = false;
/*      */       }
/*      */     }
/*      */     
/*  326 */     return pub;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPublic(boolean v)
/*      */   {
/*  333 */     if ((this.is_public == null) || (v != this.is_public.booleanValue()))
/*      */     {
/*  335 */       if ((v) && (!canBePublic()))
/*      */       {
/*  337 */         Debug.out("Invalid attempt to set public");
/*      */         
/*  339 */         return;
/*      */       }
/*      */       
/*  342 */       this.is_public = Boolean.valueOf(v);
/*      */       
/*  344 */       writeBooleanAttribute("pub", Boolean.valueOf(v));
/*      */       
/*  346 */       this.tag_type.fireChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getPublicDefault()
/*      */   {
/*  353 */     if (!getCanBePublicDefault())
/*      */     {
/*  355 */       return false;
/*      */     }
/*      */     
/*  358 */     return this.tag_type.getTagManager().getTagPublicDefault();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCanBePublic(boolean can_be_public)
/*      */   {
/*  365 */     writeBooleanAttribute("canpub", Boolean.valueOf(can_be_public));
/*      */     
/*  367 */     if (!can_be_public)
/*      */     {
/*  369 */       if (isPublic())
/*      */       {
/*  371 */         setPublic(false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canBePublic()
/*      */   {
/*  379 */     return readBooleanAttribute("canpub", Boolean.valueOf(getCanBePublicDefault())).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getCanBePublicDefault()
/*      */   {
/*  385 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean[] isTagAuto()
/*      */   {
/*  391 */     return new boolean[] { false, false };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isVisible()
/*      */   {
/*  399 */     return this.is_visible == null ? getVisibleDefault() : this.is_visible.booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setVisible(boolean v)
/*      */   {
/*  406 */     if ((this.is_visible == null) || (v != this.is_visible.booleanValue()))
/*      */     {
/*  408 */       this.is_visible = Boolean.valueOf(v);
/*      */       
/*  410 */       writeBooleanAttribute("vis", Boolean.valueOf(v));
/*      */       
/*  412 */       this.tag_type.fireChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getGroup()
/*      */   {
/*  419 */     return this.group;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setGroup(String new_group)
/*      */   {
/*  426 */     if ((this.group == null) && (new_group == null))
/*      */     {
/*  428 */       return;
/*      */     }
/*      */     
/*  431 */     if ((this.group == null) || (new_group == null) || (!this.group.equals(new_group)))
/*      */     {
/*  433 */       this.group = new_group;
/*      */       
/*  435 */       writeStringAttribute("gr", new_group);
/*      */       
/*  437 */       this.tag_type.fireChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getVisibleDefault()
/*      */   {
/*  444 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getImageID()
/*      */   {
/*  450 */     return readStringAttribute("img.id", null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setImageID(String id)
/*      */   {
/*  457 */     writeStringAttribute("img.id", id);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int[] decodeRGB(String str)
/*      */   {
/*  464 */     if (str == null)
/*      */     {
/*  466 */       return null;
/*      */     }
/*      */     
/*  469 */     String[] bits = str.split(",");
/*      */     
/*  471 */     if (bits.length != 3)
/*      */     {
/*  473 */       return null;
/*      */     }
/*      */     
/*  476 */     int[] rgb = new int[3];
/*      */     
/*  478 */     for (int i = 0; i < bits.length; i++)
/*      */     {
/*      */       try
/*      */       {
/*  482 */         rgb[i] = Integer.parseInt(bits[i]);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  486 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  490 */     return rgb;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String encodeRGB(int[] rgb)
/*      */   {
/*  497 */     if ((rgb == null) || (rgb.length != 3))
/*      */     {
/*  499 */       return null;
/*      */     }
/*      */     
/*  502 */     return rgb[0] + "," + rgb[1] + "," + rgb[2];
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isColorDefault()
/*      */   {
/*  508 */     return decodeRGB(readStringAttribute("col.rgb", null)) == null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getColor()
/*      */   {
/*  514 */     int[] result = this.colour;
/*      */     
/*  516 */     if (result == null)
/*      */     {
/*  518 */       result = decodeRGB(readStringAttribute("col.rgb", null));
/*      */       
/*  520 */       if (result == null)
/*      */       {
/*  522 */         result = this.tag_type.getColorDefault();
/*      */       }
/*      */       
/*  525 */       this.colour = result;
/*      */     }
/*      */     
/*  528 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setColor(int[] rgb)
/*      */   {
/*  535 */     writeStringAttribute("col.rgb", encodeRGB(rgb));
/*      */     
/*  537 */     this.colour = null;
/*      */     
/*  539 */     this.tag_type.fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTagRSSFeedEnabled()
/*      */   {
/*  545 */     if (this.tag_rss != null)
/*      */     {
/*  547 */       return readBooleanAttribute("rss.enable", Boolean.valueOf(false)).booleanValue();
/*      */     }
/*      */     
/*  550 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagRSSFeedEnabled(boolean enable)
/*      */   {
/*  557 */     if (this.tag_rss != null)
/*      */     {
/*  559 */       if (isTagRSSFeedEnabled() != enable)
/*      */       {
/*  561 */         writeBooleanAttribute("rss.enable", Boolean.valueOf(enable));
/*      */         
/*  563 */         this.tag_type.fireChanged(this);
/*      */         
/*  565 */         this.tag_type.getTagManager().checkRSSFeeds(this, enable);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsTagInitialSaveFolder()
/*      */   {
/*  575 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public File getTagInitialSaveFolder()
/*      */   {
/*  581 */     if (this.tag_fl != null)
/*      */     {
/*  583 */       String str = readStringAttribute("fl.init", null);
/*      */       
/*  585 */       if (str == null)
/*      */       {
/*  587 */         return null;
/*      */       }
/*      */       
/*      */ 
/*  591 */       return new File(str);
/*      */     }
/*      */     
/*      */ 
/*  595 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagInitialSaveFolder(File folder)
/*      */   {
/*  602 */     if (this.tag_fl != null)
/*      */     {
/*  604 */       File existing = getTagInitialSaveFolder();
/*      */       
/*  606 */       if ((existing == null) && (folder == null))
/*      */       {
/*  608 */         return;
/*      */       }
/*  610 */       if ((existing == null) || (folder == null) || (!existing.equals(folder)))
/*      */       {
/*  612 */         writeStringAttribute("fl.init", folder == null ? null : folder.getAbsolutePath());
/*      */         
/*  614 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTagInitialSaveOptions()
/*      */   {
/*  622 */     if (this.tag_fl != null)
/*      */     {
/*  624 */       return readLongAttribute("fl.init.o", Long.valueOf(1L)).longValue();
/*      */     }
/*      */     
/*  627 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagInitialSaveOptions(long options)
/*      */   {
/*  634 */     if (this.tag_fl != null)
/*      */     {
/*  636 */       long existing = getTagInitialSaveOptions();
/*      */       
/*  638 */       if (existing != options)
/*      */       {
/*  640 */         writeLongAttribute("fl.init.o", options);
/*      */         
/*  642 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsTagMoveOnComplete()
/*      */   {
/*  652 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public File getTagMoveOnCompleteFolder()
/*      */   {
/*  658 */     if (this.tag_fl != null)
/*      */     {
/*  660 */       String str = readStringAttribute("fl.comp", null);
/*      */       
/*  662 */       if (str == null)
/*      */       {
/*  664 */         return null;
/*      */       }
/*      */       
/*      */ 
/*  668 */       return new File(str);
/*      */     }
/*      */     
/*      */ 
/*  672 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMoveOnCompleteFolder(File folder)
/*      */   {
/*  679 */     if (this.tag_fl != null)
/*      */     {
/*  681 */       File existing = getTagMoveOnCompleteFolder();
/*      */       
/*  683 */       if ((existing == null) && (folder == null))
/*      */       {
/*  685 */         return;
/*      */       }
/*  687 */       if ((existing == null) || (folder == null) || (!existing.equals(folder)))
/*      */       {
/*  689 */         writeStringAttribute("fl.comp", folder == null ? null : folder.getAbsolutePath());
/*      */         
/*  691 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTagMoveOnCompleteOptions()
/*      */   {
/*  699 */     if (this.tag_fl != null)
/*      */     {
/*  701 */       return readLongAttribute("fl.comp.o", Long.valueOf(1L)).longValue();
/*      */     }
/*      */     
/*  704 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMoveOnCompleteOptions(long options)
/*      */   {
/*  711 */     if (this.tag_fl != null)
/*      */     {
/*  713 */       long existing = getTagMoveOnCompleteOptions();
/*      */       
/*  715 */       if (existing != options)
/*      */       {
/*  717 */         writeLongAttribute("fl.comp.o", options);
/*      */         
/*  719 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsTagCopyOnComplete()
/*      */   {
/*  729 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public File getTagCopyOnCompleteFolder()
/*      */   {
/*  735 */     if (this.tag_fl != null)
/*      */     {
/*  737 */       String str = readStringAttribute("fl.copy", null);
/*      */       
/*  739 */       if (str == null)
/*      */       {
/*  741 */         return null;
/*      */       }
/*      */       
/*      */ 
/*  745 */       return new File(str);
/*      */     }
/*      */     
/*      */ 
/*  749 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagCopyOnCompleteFolder(File folder)
/*      */   {
/*  756 */     if (this.tag_fl != null)
/*      */     {
/*  758 */       File existing = getTagCopyOnCompleteFolder();
/*      */       
/*  760 */       if ((existing == null) && (folder == null))
/*      */       {
/*  762 */         return;
/*      */       }
/*  764 */       if ((existing == null) || (folder == null) || (!existing.equals(folder)))
/*      */       {
/*  766 */         writeStringAttribute("fl.copy", folder == null ? null : folder.getAbsolutePath());
/*      */         
/*  768 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTagCopyOnCompleteOptions()
/*      */   {
/*  776 */     if (this.tag_fl != null)
/*      */     {
/*  778 */       return readLongAttribute("fl.copy.o", Long.valueOf(1L)).longValue();
/*      */     }
/*      */     
/*  781 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagCopyOnCompleteOptions(long options)
/*      */   {
/*  788 */     if (this.tag_fl != null)
/*      */     {
/*  790 */       long existing = getTagCopyOnCompleteOptions();
/*      */       
/*  792 */       if (existing != options)
/*      */       {
/*  794 */         writeLongAttribute("fl.copy.o", options);
/*      */         
/*  796 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getTagMinShareRatio()
/*      */   {
/*  806 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMinShareRatio(int sr)
/*      */   {
/*  813 */     Debug.out("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getTagMaxShareRatio()
/*      */   {
/*  821 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxShareRatio(int sr)
/*      */   {
/*  828 */     Debug.out("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxShareRatioAction()
/*      */   {
/*  834 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxShareRatioAction(int action)
/*      */   {
/*  841 */     Debug.out("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getTagAggregateShareRatio()
/*      */   {
/*  849 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxAggregateShareRatio()
/*      */   {
/*  855 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxAggregateShareRatio(int sr)
/*      */   {
/*  862 */     Debug.out("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxAggregateShareRatioAction()
/*      */   {
/*  868 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxAggregateShareRatioAction(int action)
/*      */   {
/*  875 */     Debug.out("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getTagMaxAggregateShareRatioHasPriority()
/*      */   {
/*  881 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxAggregateShareRatioHasPriority(boolean priority)
/*      */   {
/*  888 */     Debug.out("not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaximumTaggables()
/*      */   {
/*  896 */     if (this.tag_limits != null)
/*      */     {
/*  898 */       return readLongAttribute("max.t", Long.valueOf(0L)).intValue();
/*      */     }
/*      */     
/*  901 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaximumTaggables(int max)
/*      */   {
/*  908 */     if (this.tag_limits != null)
/*      */     {
/*  910 */       if (getMaximumTaggables() != max)
/*      */       {
/*  912 */         writeLongAttribute("max.t", max);
/*      */         
/*  914 */         this.tag_type.fireChanged(this);
/*      */         
/*  916 */         checkMaximumTaggables();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkMaximumTaggables() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public int getRemovalStrategy()
/*      */   {
/*  929 */     if (this.tag_limits != null)
/*      */     {
/*  931 */       return readLongAttribute("max.t.r", Long.valueOf(0L)).intValue();
/*      */     }
/*      */     
/*  934 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRemovalStrategy(int id)
/*      */   {
/*  941 */     if (this.tag_limits != null)
/*      */     {
/*  943 */       if (getRemovalStrategy() != id)
/*      */       {
/*  945 */         writeLongAttribute("max.t.r", id);
/*      */         
/*  947 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOrdering()
/*      */   {
/*  955 */     if (this.tag_limits != null)
/*      */     {
/*  957 */       return readLongAttribute("max.t.o", Long.valueOf(0L)).intValue();
/*      */     }
/*      */     
/*  960 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setOrdering(int id)
/*      */   {
/*  967 */     if (this.tag_limits != null)
/*      */     {
/*  969 */       if (getOrdering() != id)
/*      */       {
/*  971 */         writeLongAttribute("max.t.o", id);
/*      */         
/*  973 */         this.tag_type.fireChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TagFeatureProperties.TagProperty[] getSupportedProperties()
/*      */   {
/*  981 */     return new TagFeatureProperties.TagProperty[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TagFeatureProperties.TagProperty getProperty(String name)
/*      */   {
/*  988 */     TagFeatureProperties.TagProperty[] props = getSupportedProperties();
/*      */     
/*  990 */     for (TagFeatureProperties.TagProperty prop : props)
/*      */     {
/*  992 */       if (prop.getName(false) == name)
/*      */       {
/*  994 */         return prop;
/*      */       }
/*      */     }
/*      */     
/*  998 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TagFeatureProperties.TagProperty createTagProperty(String name, int type)
/*      */   {
/* 1006 */     return new TagPropertyImpl(name, type, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getSupportedActions()
/*      */   {
/* 1014 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsAction(int action)
/*      */   {
/* 1021 */     return (getSupportedActions() & action) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isActionEnabled(int action)
/*      */   {
/* 1028 */     if (!supportsAction(action))
/*      */     {
/* 1030 */       return false;
/*      */     }
/*      */     
/* 1033 */     return readBooleanAttribute("pp." + action, Boolean.valueOf(false)).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setActionEnabled(int action, boolean enabled)
/*      */   {
/* 1041 */     if (!supportsAction(action))
/*      */     {
/* 1043 */       if (enabled)
/*      */       {
/* 1045 */         Debug.out("not supported");
/*      */       }
/*      */       
/* 1048 */       return;
/*      */     }
/*      */     
/* 1051 */     writeBooleanAttribute("pp." + action, Boolean.valueOf(enabled));
/*      */   }
/*      */   
/*      */ 
/*      */   public String getActionScript()
/*      */   {
/* 1057 */     String script = readStringAttribute("eos.scr", "");
/*      */     
/* 1059 */     if (script == null)
/*      */     {
/* 1061 */       script = "";
/*      */     }
/*      */     
/* 1064 */     return script;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setActionScript(String script)
/*      */   {
/* 1071 */     if (script == null)
/*      */     {
/* 1073 */       script = "";
/*      */     }
/*      */     
/* 1076 */     script = script.trim();
/*      */     
/* 1078 */     writeStringAttribute("eos.scr", script);
/*      */     
/* 1080 */     setActionEnabled(32, script.length() > 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPostingNotifications()
/*      */   {
/* 1088 */     return readLongAttribute("noti.post", Long.valueOf(0L)).intValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPostingNotifications(int flags)
/*      */   {
/* 1095 */     writeLongAttribute("noti.post", flags);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addTaggable(com.aelitis.azureus.core.tag.Taggable t)
/*      */   {
/* 1104 */     this.t_listeners.dispatch(1, t);
/*      */     
/* 1106 */     this.tag_type.taggableAdded(this, t);
/*      */     
/* 1108 */     this.tag_type.fireChanged(this);
/*      */     
/* 1110 */     if (this.tag_limits != null)
/*      */     {
/* 1112 */       checkMaximumTaggables();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTaggable(com.aelitis.azureus.core.tag.Taggable t)
/*      */   {
/* 1120 */     this.t_listeners.dispatch(2, t);
/*      */     
/* 1122 */     this.tag_type.taggableRemoved(this, t);
/*      */     
/* 1124 */     this.tag_type.fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sync()
/*      */   {
/* 1131 */     this.t_listeners.dispatch(3, null);
/*      */     
/* 1133 */     this.tag_type.taggableSync(this);
/*      */     
/* 1135 */     savePersistentStuff();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void closing()
/*      */   {
/* 1141 */     savePersistentStuff();
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeTag()
/*      */   {
/* 1147 */     boolean was_rss = isTagRSSFeedEnabled();
/*      */     
/* 1149 */     this.tag_type.removeTag(this);
/*      */     
/* 1151 */     if (was_rss)
/*      */     {
/* 1153 */       this.tag_type.getTagManager().checkRSSFeeds(this, false);
/*      */     }
/*      */     
/* 1156 */     saveTransientStuff();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDescription()
/*      */   {
/* 1162 */     return this.description;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDescription(String str)
/*      */   {
/* 1169 */     String existing = getDescription();
/*      */     
/* 1171 */     if (existing == str)
/*      */     {
/* 1173 */       return;
/*      */     }
/* 1175 */     if ((str != null) && (existing != null))
/*      */     {
/* 1177 */       if (str.equals(existing))
/*      */       {
/* 1179 */         return;
/*      */       }
/*      */     }
/* 1182 */     this.description = str;
/*      */     
/* 1184 */     writeStringAttribute("desc", str);
/*      */     
/* 1186 */     this.tag_type.fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTransientProperty(String property, Object value)
/*      */   {
/* 1194 */     synchronized (this)
/*      */     {
/* 1196 */       if (this.transient_properties == null)
/*      */       {
/* 1198 */         if (value == null)
/*      */         {
/* 1200 */           return;
/*      */         }
/*      */         
/* 1203 */         this.transient_properties = new HashMap();
/*      */       }
/*      */       
/* 1206 */       if (value == null)
/*      */       {
/* 1208 */         this.transient_properties.remove(property);
/*      */       }
/*      */       else
/*      */       {
/* 1212 */         this.transient_properties.put(property, value);
/*      */       }
/*      */       
/* 1215 */       this.tag_type.fireChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getTransientProperty(String property)
/*      */   {
/* 1223 */     synchronized (this)
/*      */     {
/* 1225 */       if (this.transient_properties == null)
/*      */       {
/* 1227 */         return null;
/*      */       }
/*      */       
/* 1230 */       return this.transient_properties.get(property);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addTagListener(com.aelitis.azureus.core.tag.TagListener listener, boolean fire_for_existing)
/*      */   {
/* 1239 */     if (!this.t_listeners.hasListener(listener)) {
/* 1240 */       this.t_listeners.addListener(listener);
/*      */     }
/*      */     
/* 1243 */     if (fire_for_existing)
/*      */     {
/* 1245 */       for (com.aelitis.azureus.core.tag.Taggable t : getTagged())
/*      */       {
/* 1247 */         listener.taggableAdded(this, t);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/* 1255 */     Set<com.aelitis.azureus.core.tag.Taggable> taggables = getTagged();
/*      */     
/* 1257 */     for (com.aelitis.azureus.core.tag.Taggable t : taggables)
/*      */     {
/* 1259 */       this.t_listeners.dispatch(2, t);
/*      */       
/* 1261 */       this.tag_type.taggableRemoved(this, t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTagListener(com.aelitis.azureus.core.tag.TagListener listener)
/*      */   {
/* 1269 */     this.t_listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<org.gudy.azureus2.plugins.tag.Taggable> getTaggables()
/*      */   {
/* 1275 */     Set<com.aelitis.azureus.core.tag.Taggable> taggables = getTagged();
/*      */     
/* 1277 */     List<org.gudy.azureus2.plugins.tag.Taggable> result = new ArrayList(taggables.size());
/*      */     
/* 1279 */     for (com.aelitis.azureus.core.tag.Taggable t : taggables)
/*      */     {
/* 1281 */       if ((t instanceof DownloadManager))
/*      */       {
/* 1283 */         result.add(PluginCoreUtils.wrap((DownloadManager)t));
/*      */       }
/*      */     }
/*      */     
/* 1287 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestAttention()
/*      */   {
/* 1294 */     this.tag_type.requestAttention(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(final org.gudy.azureus2.plugins.tag.TagListener listener)
/*      */   {
/* 1301 */     synchronized (this.listener_map)
/*      */     {
/* 1303 */       com.aelitis.azureus.core.tag.TagListener l = (com.aelitis.azureus.core.tag.TagListener)this.listener_map.get(listener);
/*      */       
/* 1305 */       if (l != null)
/*      */       {
/* 1307 */         Debug.out("listener already added");
/*      */         
/* 1309 */         return;
/*      */       }
/*      */       
/* 1312 */       l = new com.aelitis.azureus.core.tag.TagListener()
/*      */       {
/*      */         public void taggableSync(Tag tag) {
/* 1315 */           listener.taggableSync(tag);
/*      */         }
/*      */         
/*      */         public void taggableRemoved(Tag tag, com.aelitis.azureus.core.tag.Taggable tagged) {
/* 1319 */           listener.taggableRemoved(tag, tagged);
/*      */         }
/*      */         
/*      */         public void taggableAdded(Tag tag, com.aelitis.azureus.core.tag.Taggable tagged) {
/* 1323 */           listener.taggableAdded(tag, tagged);
/*      */         }
/*      */         
/* 1326 */       };
/* 1327 */       this.listener_map.put(listener, l);
/*      */       
/* 1329 */       addTagListener(l, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeListener(org.gudy.azureus2.plugins.tag.TagListener listener)
/*      */   {
/* 1338 */     synchronized (this.listener_map)
/*      */     {
/* 1340 */       com.aelitis.azureus.core.tag.TagListener l = (com.aelitis.azureus.core.tag.TagListener)this.listener_map.remove(listener);
/*      */       
/* 1342 */       if (l == null)
/*      */       {
/* 1344 */         Debug.out("listener not found");
/*      */         
/* 1346 */         return;
/*      */       }
/*      */       
/* 1349 */       removeTagListener(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Boolean readBooleanAttribute(String attr, Boolean def)
/*      */   {
/* 1358 */     return this.tag_type.readBooleanAttribute(this, attr, def);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeBooleanAttribute(String attr, Boolean value)
/*      */   {
/* 1366 */     return this.tag_type.writeBooleanAttribute(this, attr, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Long readLongAttribute(String attr, Long def)
/*      */   {
/* 1374 */     return this.tag_type.readLongAttribute(this, attr, def);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeLongAttribute(String attr, long value)
/*      */   {
/* 1382 */     return this.tag_type.writeLongAttribute(this, attr, Long.valueOf(value));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String readStringAttribute(String attr, String def)
/*      */   {
/* 1390 */     return this.tag_type.readStringAttribute(this, attr, def);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeStringAttribute(String attr, String value)
/*      */   {
/* 1398 */     this.tag_type.writeStringAttribute(this, attr, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String[] readStringListAttribute(String attr, String[] def)
/*      */   {
/* 1406 */     return this.tag_type.readStringListAttribute(this, attr, def);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeStringListAttribute(String attr, String[] value)
/*      */   {
/* 1414 */     return this.tag_type.writeStringListAttribute(this, attr, value);
/*      */   }
/*      */   
/* 1417 */   private static final Map<Long, long[][]> session_cache = new HashMap();
/*      */   
/*      */   private long[] total_up_at_start;
/*      */   private long[] total_down_at_start;
/*      */   private long[] session_up;
/*      */   private long[] session_down;
/*      */   private long[] session_up_reset;
/*      */   private long[] session_down_reset;
/*      */   
/*      */   private void loadTransientStuff()
/*      */   {
/* 1428 */     if ((this.tag_rl != null) && (this.tag_rl.supportsTagRates()))
/*      */     {
/* 1430 */       synchronized (session_cache)
/*      */       {
/* 1432 */         long[][] entry = (long[][])session_cache.get(Long.valueOf(getTagUID()));
/*      */         
/* 1434 */         if (entry != null)
/*      */         {
/* 1436 */           this.total_up_at_start = entry[0];
/* 1437 */           this.total_down_at_start = entry[1];
/* 1438 */           this.session_up = entry[2];
/* 1439 */           this.session_down = entry[3];
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void saveTransientStuff()
/*      */   {
/* 1451 */     if ((this.tag_rl != null) && (this.tag_rl.supportsTagRates()))
/*      */     {
/* 1453 */       long[] session_up = getTagSessionUploadTotalRaw();
/* 1454 */       long[] session_down = getTagSessionDownloadTotalRaw();
/*      */       
/* 1456 */       synchronized (session_cache)
/*      */       {
/* 1458 */         session_cache.put(Long.valueOf(getTagUID()), new long[][] { this.total_up_at_start, this.total_down_at_start, session_up, session_down });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void loadPersistentStuff()
/*      */   {
/* 1466 */     if ((this.tag_rl != null) && (this.tag_rl.supportsTagRates()))
/*      */     {
/* 1468 */       String[] ups = readStringListAttribute("b.up", null);
/*      */       
/* 1470 */       if (ups != null)
/*      */       {
/* 1472 */         this.total_up_at_start = new long[ups.length];
/*      */         
/* 1474 */         for (int i = 0; i < ups.length; i++) {
/*      */           try
/*      */           {
/* 1477 */             this.total_up_at_start[i] = Long.parseLong(ups[i]);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1481 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1486 */       String[] downs = readStringListAttribute("b.down", null);
/*      */       
/* 1488 */       if (downs != null)
/*      */       {
/* 1490 */         this.total_down_at_start = new long[downs.length];
/*      */         
/* 1492 */         for (int i = 0; i < downs.length; i++) {
/*      */           try
/*      */           {
/* 1495 */             this.total_down_at_start[i] = Long.parseLong(downs[i]);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1499 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void savePersistentStuff()
/*      */   {
/* 1509 */     if ((this.tag_rl != null) && (this.tag_rl.supportsTagRates()))
/*      */     {
/* 1511 */       long[] session_up = getTagSessionUploadTotalRaw();
/*      */       
/* 1513 */       if (session_up != null)
/*      */       {
/* 1515 */         String[] ups = new String[session_up.length];
/*      */         
/* 1517 */         for (int i = 0; i < ups.length; i++)
/*      */         {
/* 1519 */           long l = session_up[i];
/*      */           
/* 1521 */           if ((this.total_up_at_start != null) && (this.total_up_at_start.length > i))
/*      */           {
/* 1523 */             l += this.total_up_at_start[i];
/*      */           }
/*      */           
/* 1526 */           ups[i] = String.valueOf(l);
/*      */         }
/*      */         
/* 1529 */         writeStringListAttribute("b.up", ups);
/*      */       }
/*      */       
/* 1532 */       long[] session_down = getTagSessionDownloadTotalRaw();
/*      */       
/* 1534 */       if (session_down != null)
/*      */       {
/* 1536 */         String[] downs = new String[session_down.length];
/*      */         
/* 1538 */         for (int i = 0; i < downs.length; i++)
/*      */         {
/* 1540 */           long l = session_down[i];
/*      */           
/* 1542 */           if ((this.total_down_at_start != null) && (this.total_down_at_start.length > i))
/*      */           {
/* 1544 */             l += this.total_down_at_start[i];
/*      */           }
/*      */           
/* 1547 */           downs[i] = String.valueOf(l);
/*      */         }
/*      */         
/* 1550 */         writeStringListAttribute("b.down", downs);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long[] getTagUploadTotal()
/*      */   {
/* 1558 */     long[] result = getTagSessionUploadTotalRaw();
/*      */     
/* 1560 */     if (result != null)
/*      */     {
/* 1562 */       if ((this.total_up_at_start != null) && (this.total_up_at_start.length == result.length))
/*      */       {
/* 1564 */         for (int i = 0; i < result.length; i++)
/*      */         {
/* 1566 */           result[i] += this.total_up_at_start[i];
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1571 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTagSessionUploadTotal()
/*      */   {
/* 1580 */     long[] result = getTagSessionUploadTotalRaw();
/*      */     
/* 1582 */     if ((result != null) && (this.session_up_reset != null) && (result.length == this.session_up_reset.length))
/*      */     {
/* 1584 */       for (int i = 0; i < result.length; i++)
/*      */       {
/* 1586 */         result[i] -= this.session_up_reset[i];
/*      */       }
/*      */     }
/*      */     
/* 1590 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetTagSessionUploadTotal()
/*      */   {
/* 1596 */     this.session_up_reset = getTagSessionUploadTotalRaw();
/*      */   }
/*      */   
/*      */ 
/*      */   private long[] getTagSessionUploadTotalRaw()
/*      */   {
/* 1602 */     if ((this.tag_rl == null) || (!this.tag_rl.supportsTagRates()))
/*      */     {
/* 1604 */       return null;
/*      */     }
/*      */     
/* 1607 */     long[] result = getTagSessionUploadTotalCurrent();
/*      */     
/* 1609 */     if ((result != null) && (this.session_up != null))
/*      */     {
/* 1611 */       if (result.length == this.session_up.length)
/*      */       {
/* 1613 */         for (int i = 0; i < result.length; i++)
/*      */         {
/* 1615 */           result[i] += this.session_up[i];
/*      */         }
/*      */         
/*      */       } else {
/* 1619 */         Debug.out("derp");
/*      */       }
/*      */     }
/*      */     
/* 1623 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long[] getTagSessionUploadTotalCurrent()
/*      */   {
/* 1629 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public long[] getTagDownloadTotal()
/*      */   {
/* 1635 */     long[] result = getTagSessionDownloadTotalRaw();
/*      */     
/* 1637 */     if (result != null)
/*      */     {
/* 1639 */       if ((this.total_down_at_start != null) && (this.total_down_at_start.length == result.length))
/*      */       {
/* 1641 */         for (int i = 0; i < result.length; i++)
/*      */         {
/* 1643 */           result[i] += this.total_down_at_start[i];
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1648 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public long[] getTagSessionDownloadTotal()
/*      */   {
/* 1654 */     long[] result = getTagSessionDownloadTotalRaw();
/*      */     
/* 1656 */     if ((result != null) && (this.session_down_reset != null) && (result.length == this.session_down_reset.length))
/*      */     {
/* 1658 */       for (int i = 0; i < result.length; i++)
/*      */       {
/* 1660 */         result[i] -= this.session_down_reset[i];
/*      */       }
/*      */     }
/*      */     
/* 1664 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetTagSessionDownloadTotal()
/*      */   {
/* 1670 */     this.session_down_reset = getTagSessionDownloadTotalRaw();
/*      */   }
/*      */   
/*      */ 
/*      */   private long[] getTagSessionDownloadTotalRaw()
/*      */   {
/* 1676 */     if ((this.tag_rl == null) || (!this.tag_rl.supportsTagRates()))
/*      */     {
/* 1678 */       return null;
/*      */     }
/*      */     
/* 1681 */     long[] result = getTagSessionDownloadTotalCurrent();
/*      */     
/* 1683 */     if ((result != null) && (this.session_down != null))
/*      */     {
/* 1685 */       if (result.length == this.session_down.length)
/*      */       {
/* 1687 */         for (int i = 0; i < result.length; i++)
/*      */         {
/* 1689 */           result[i] += this.session_down[i];
/*      */         }
/*      */         
/*      */       } else {
/* 1693 */         Debug.out("derp");
/*      */       }
/*      */     }
/*      */     
/* 1697 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long[] getTagSessionDownloadTotalCurrent()
/*      */   {
/* 1703 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private static final int HISTORY_MAX_SECS = 1800;
/*      */   
/*      */   private volatile boolean history_retention_required;
/*      */   
/*      */   private long[] history;
/*      */   private int history_pos;
/*      */   private boolean history_wrapped;
/*      */   private boolean timer_registered;
/*      */   public void setRecentHistoryRetention(boolean required)
/*      */   {
/* 1717 */     if ((this.tag_rl == null) || (!this.tag_rl.supportsTagRates()))
/*      */     {
/* 1719 */       return;
/*      */     }
/*      */     
/* 1722 */     synchronized (this)
/*      */     {
/* 1724 */       if (required)
/*      */       {
/* 1726 */         if (!this.history_retention_required)
/*      */         {
/* 1728 */           this.history = new long['܈'];
/*      */           
/* 1730 */           this.history_pos = 0;
/*      */           
/* 1732 */           this.history_retention_required = true;
/*      */           
/* 1734 */           if (!this.timer_registered)
/*      */           {
/* 1736 */             SimpleTimer.addTickReceiver(this);
/*      */             
/* 1738 */             this.timer_registered = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1743 */         this.history = null;
/*      */         
/* 1745 */         this.history_retention_required = false;
/*      */         
/* 1747 */         if (this.timer_registered)
/*      */         {
/* 1749 */           SimpleTimer.removeTickReceiver(this);
/*      */           
/* 1751 */           this.timer_registered = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int[][] getRecentHistory()
/*      */   {
/* 1760 */     synchronized (this)
/*      */     {
/* 1762 */       if (this.history == null)
/*      */       {
/* 1764 */         return new int[2][0];
/*      */       }
/*      */       
/*      */ 
/* 1768 */       int entries = this.history_wrapped ? 1800 : this.history_pos;
/* 1769 */       int start = this.history_wrapped ? this.history_pos : 0;
/*      */       
/* 1771 */       int[][] result = new int[2][entries];
/*      */       
/* 1773 */       int pos = start;
/*      */       
/* 1775 */       for (int i = 0; i < entries; i++)
/*      */       {
/* 1777 */         if (pos == 1800)
/*      */         {
/* 1779 */           pos = 0;
/*      */         }
/*      */         
/* 1782 */         long entry = this.history[(pos++)];
/*      */         
/* 1784 */         int send_rate = (int)(entry >> 32 & 0xFFFFFFFF);
/* 1785 */         int recv_rate = (int)(entry & 0xFFFFFFFF);
/*      */         
/* 1787 */         result[0][i] = send_rate;
/* 1788 */         result[1][i] = recv_rate;
/*      */       }
/*      */       
/* 1791 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getTaggableAddedTime(com.aelitis.azureus.core.tag.Taggable taggble)
/*      */   {
/* 1801 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tick(long mono_now, int count)
/*      */   {
/* 1810 */     if (!this.history_retention_required)
/*      */     {
/* 1812 */       return;
/*      */     }
/*      */     
/* 1815 */     long send_rate = this.tag_rl.getTagCurrentUploadRate();
/* 1816 */     long receive_rate = this.tag_rl.getTagCurrentDownloadRate();
/*      */     
/* 1818 */     long entry = send_rate << 32 & 0xFFFFFFFF00000000 | receive_rate & 0xFFFFFFFF;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1823 */     synchronized (this)
/*      */     {
/* 1825 */       if (this.history != null)
/*      */       {
/* 1827 */         this.history[(this.history_pos++)] = entry;
/*      */         
/* 1829 */         if (this.history_pos == 1800)
/*      */         {
/* 1831 */           this.history_pos = 0;
/* 1832 */           this.history_wrapped = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class TagPropertyImpl
/*      */     implements TagFeatureProperties.TagProperty
/*      */   {
/*      */     private final String name;
/*      */     
/*      */     private final int type;
/* 1845 */     private final CopyOnWriteList<TagFeatureProperties.TagPropertyListener> listeners = new CopyOnWriteList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private TagPropertyImpl(String _name, int _type)
/*      */     {
/* 1852 */       this.name = _name;
/* 1853 */       this.type = _type;
/*      */     }
/*      */     
/*      */ 
/*      */     public Tag getTag()
/*      */     {
/* 1859 */       return TagBase.this;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getType()
/*      */     {
/* 1865 */       return this.type;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getName(boolean localize)
/*      */     {
/* 1872 */       if (localize)
/*      */       {
/* 1874 */         return MessageText.getString("tag.property." + this.name);
/*      */       }
/*      */       
/*      */ 
/* 1878 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setStringList(String[] value)
/*      */     {
/* 1886 */       if (TagBase.this.writeStringListAttribute("pp." + this.name, value))
/*      */       {
/* 1888 */         for (TagFeatureProperties.TagPropertyListener l : this.listeners) {
/*      */           try
/*      */           {
/* 1891 */             l.propertyChanged(this);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1895 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 1899 */         TagBase.this.tag_type.fireChanged(TagBase.this);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public String[] getStringList()
/*      */     {
/* 1906 */       return TagBase.this.readStringListAttribute("pp." + this.name, TagBase.EMPTY_STRING_LIST);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setBoolean(Boolean value)
/*      */     {
/* 1913 */       if (TagBase.this.writeBooleanAttribute("pp." + this.name, value))
/*      */       {
/* 1915 */         for (TagFeatureProperties.TagPropertyListener l : this.listeners) {
/*      */           try
/*      */           {
/* 1918 */             l.propertyChanged(this);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1922 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 1926 */         TagBase.this.tag_type.fireChanged(TagBase.this);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public Boolean getBoolean()
/*      */     {
/* 1933 */       return TagBase.this.readBooleanAttribute("pp." + this.name, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setLong(Long value)
/*      */     {
/* 1940 */       if (TagBase.this.writeLongAttribute("pp." + this.name, value.longValue()))
/*      */       {
/* 1942 */         for (TagFeatureProperties.TagPropertyListener l : this.listeners) {
/*      */           try
/*      */           {
/* 1945 */             l.propertyChanged(this);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1949 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 1953 */         TagBase.this.tag_type.fireChanged(TagBase.this);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public Long getLong()
/*      */     {
/* 1960 */       return TagBase.this.readLongAttribute("pp." + this.name, null);
/*      */     }
/*      */     
/*      */ 
/*      */     public String getString()
/*      */     {
/* 1966 */       String value = null;
/*      */       
/* 1968 */       switch (getType()) {
/*      */       case 1: 
/* 1970 */         String[] vals = getStringList();
/*      */         
/* 1972 */         if ((vals != null) && (vals.length > 0)) {
/* 1973 */           value = "";
/*      */           
/* 1975 */           String name = getName(false);
/*      */           
/* 1977 */           if (name.equals("tracker_templates"))
/*      */           {
/* 1979 */             String str_merge = MessageText.getString("label.merge");
/* 1980 */             String str_replace = MessageText.getString("label.replace");
/* 1981 */             String str_remove = MessageText.getString("Button.remove");
/*      */             
/* 1983 */             for (String val : vals) {
/* 1984 */               String[] bits = val.split(":");
/* 1985 */               String type = bits[0];
/* 1986 */               String str = bits[1];
/*      */               
/* 1988 */               if (type.equals("m")) {
/* 1989 */                 str = str + ": " + str_merge;
/* 1990 */               } else if (type.equals("r")) {
/* 1991 */                 str = str + ": " + str_replace;
/*      */               } else {
/* 1993 */                 str = str + ": " + str_remove;
/*      */               }
/* 1995 */               value = value + (value.length() == 0 ? "" : ",") + str;
/*      */             }
/* 1997 */           } else if (name.equals("constraint"))
/*      */           {
/* 1999 */             value = value + vals[0];
/*      */             
/* 2001 */             if (vals.length > 1)
/*      */             {
/* 2003 */               String options = vals[1];
/*      */               
/* 2005 */               boolean auto_add = !options.contains("am=2;");
/* 2006 */               boolean auto_remove = !options.contains("am=1;");
/*      */               
/* 2008 */               if ((!auto_add) || (!auto_remove))
/*      */               {
/* 2010 */                 if ((auto_add) || (auto_remove))
/*      */                 {
/* 2012 */                   value = value + "," + MessageText.getString("label.scope");
/*      */                   
/* 2014 */                   value = value + "=";
/*      */                   
/* 2016 */                   if (auto_add)
/*      */                   {
/* 2018 */                     value = value + MessageText.getString("label.addition.only");
/*      */                   }
/*      */                   else
/*      */                   {
/* 2022 */                     value = value + MessageText.getString("label.removal.only"); }
/*      */                 }
/*      */               }
/*      */             }
/*      */           } else {
/* 2027 */             for (String val : vals)
/* 2028 */               value = value + (value.length() == 0 ? "" : ",") + val;
/*      */           }
/*      */         }
/* 2031 */         break;
/*      */       
/*      */ 
/*      */       case 2: 
/* 2035 */         Boolean val = getBoolean();
/* 2036 */         if (val != null) {
/* 2037 */           value = String.valueOf(val);
/*      */         }
/*      */         
/*      */         break;
/*      */       case 3: 
/* 2042 */         Long val = getLong();
/* 2043 */         if (val != null) {
/* 2044 */           value = String.valueOf(val);
/*      */         }
/*      */         
/*      */         break;
/*      */       default: 
/* 2049 */         value = "Unknown type";
/*      */       }
/*      */       
/*      */       
/* 2053 */       if (value == null)
/*      */       {
/* 2055 */         return "";
/*      */       }
/*      */       
/*      */ 
/* 2059 */       return getName(true) + "=" + value;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addListener(TagFeatureProperties.TagPropertyListener listener)
/*      */     {
/* 2067 */       this.listeners.add(listener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(TagFeatureProperties.TagPropertyListener listener)
/*      */     {
/* 2074 */       this.listeners.remove(listener);
/*      */     }
/*      */     
/*      */ 
/*      */     public void syncListeners()
/*      */     {
/* 2080 */       for (TagFeatureProperties.TagPropertyListener l : this.listeners) {
/*      */         try
/*      */         {
/* 2083 */           l.propertySync(this);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2087 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2097 */     writer.println(this.tag_name);
/*      */     try
/*      */     {
/* 2100 */       writer.indent();
/*      */       
/* 2102 */       this.tag_type.generateConfig(writer, this);
/*      */     }
/*      */     finally
/*      */     {
/* 2106 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */