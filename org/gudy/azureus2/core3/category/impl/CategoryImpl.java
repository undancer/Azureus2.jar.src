/*     */ package org.gudy.azureus2.core3.category.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*     */ import com.aelitis.azureus.core.tag.TagDownload;
/*     */ import com.aelitis.azureus.core.tag.Taggable;
/*     */ import com.aelitis.azureus.core.tag.impl.TagBase;
/*     */ import com.aelitis.azureus.core.util.IdentityHashSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CategoryImpl
/*     */   extends TagBase
/*     */   implements Category, Comparable, TagDownload
/*     */ {
/*     */   final String sName;
/*     */   private final int type;
/*  56 */   private final List<DownloadManager> managers = new ArrayList();
/*     */   
/*     */   private int upload_speed;
/*     */   
/*     */   private int download_speed;
/*  61 */   private final Object UPLOAD_PRIORITY_KEY = new Object();
/*     */   
/*     */   private final Map<String, String> attributes;
/*     */   
/*  65 */   private static final AtomicInteger tag_ids = new AtomicInteger();
/*     */   
/*  67 */   private final LimitedRateGroup upload_limiter = new LimitedRateGroup()
/*     */   {
/*     */ 
/*     */     public String getName()
/*     */     {
/*     */ 
/*  73 */       return "cat_up: " + CategoryImpl.this.sName;
/*     */     }
/*     */     
/*     */     public int getRateLimitBytesPerSecond()
/*     */     {
/*  78 */       return CategoryImpl.this.upload_speed;
/*     */     }
/*     */     
/*  81 */     public boolean isDisabled() { return CategoryImpl.this.upload_speed == -1; }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void updateBytesUsed(int used) {}
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*  92 */   private final LimitedRateGroup download_limiter = new LimitedRateGroup()
/*     */   {
/*     */ 
/*     */     public String getName()
/*     */     {
/*     */ 
/*  98 */       return "cat_down: " + CategoryImpl.this.sName;
/*     */     }
/*     */     
/*     */     public int getRateLimitBytesPerSecond()
/*     */     {
/* 103 */       return CategoryImpl.this.download_speed;
/*     */     }
/*     */     
/* 106 */     public boolean isDisabled() { return CategoryImpl.this.download_speed == -1; }
/*     */     
/*     */ 
/*     */ 
/*     */     public void updateBytesUsed(int used) {}
/*     */   };
/*     */   
/*     */ 
/*     */   private boolean destroyed;
/*     */   
/*     */   private static final int LDT_CATEGORY_DMADDED = 1;
/*     */   
/*     */   private static final int LDT_CATEGORY_DMREMOVED = 2;
/*     */   
/* 120 */   private final ListenerManager<CategoryListener> category_listeners = ListenerManager.createManager("CatListenDispatcher", new ListenerManagerDispatcher()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public void dispatch(CategoryListener target, int type, Object value)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 130 */       if (type == 1) {
/* 131 */         target.downloadManagerAdded(CategoryImpl.this, (DownloadManager)value);
/* 132 */       } else if (type == 2) {
/* 133 */         target.downloadManagerRemoved(CategoryImpl.this, (DownloadManager)value);
/*     */       }
/*     */     }
/* 120 */   });
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
/*     */   public CategoryImpl(CategoryManagerImpl manager, String sName, int maxup, int maxdown, Map<String, String> _attributes)
/*     */   {
/* 138 */     super(manager, tag_ids.incrementAndGet(), sName);
/* 139 */     addTag();
/*     */     
/* 141 */     this.sName = sName;
/* 142 */     this.type = 0;
/* 143 */     this.upload_speed = maxup;
/* 144 */     this.download_speed = maxdown;
/* 145 */     this.attributes = _attributes;
/*     */   }
/*     */   
/*     */   public CategoryImpl(CategoryManagerImpl manager, String sName, int type, Map<String, String> _attributes) {
/* 149 */     super(manager, tag_ids.incrementAndGet(), sName);
/* 150 */     addTag();
/*     */     
/* 152 */     this.sName = sName;
/* 153 */     this.type = type;
/* 154 */     this.attributes = _attributes;
/*     */   }
/*     */   
/*     */   public void addCategoryListener(CategoryListener l) {
/* 158 */     if (!this.category_listeners.hasListener(l)) {
/* 159 */       this.category_listeners.addListener(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeCategoryListener(CategoryListener l) {
/* 164 */     this.category_listeners.removeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasCategoryListener(CategoryListener l)
/*     */   {
/* 171 */     return this.category_listeners.hasListener(l);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 175 */     return this.sName;
/*     */   }
/*     */   
/*     */   public int getType() {
/* 179 */     return this.type;
/*     */   }
/*     */   
/*     */   public List<DownloadManager> getDownloadManagers(List<DownloadManager> all_dms) {
/* 183 */     if (this.type == 0)
/* 184 */       return this.managers;
/* 185 */     if ((this.type == 1) || (all_dms == null)) {
/* 186 */       return all_dms;
/*     */     }
/* 188 */     List<DownloadManager> result = new ArrayList();
/* 189 */     for (int i = 0; i < all_dms.size(); i++) {
/* 190 */       DownloadManager dm = (DownloadManager)all_dms.get(i);
/* 191 */       Category cat = dm.getDownloadState().getCategory();
/* 192 */       if ((cat == null) || (cat.getType() == 2)) {
/* 193 */         result.add(dm);
/*     */       }
/*     */     }
/*     */     
/* 197 */     return result;
/*     */   }
/*     */   
/*     */   public void addManager(DownloadManagerState manager_state)
/*     */   {
/* 202 */     Category manager_cat = manager_state.getCategory();
/* 203 */     if (((this.type != 2) && (manager_cat != this)) || ((this.type == 2) && (manager_cat != null)))
/*     */     {
/* 205 */       manager_state.setCategory(this);
/*     */       
/* 207 */       return;
/*     */     }
/*     */     
/* 210 */     DownloadManager manager = manager_state.getDownloadManager();
/*     */     
/*     */ 
/* 213 */     if (manager == null) {
/* 214 */       return;
/*     */     }
/*     */     
/* 217 */     addTaggable(manager);
/*     */     
/* 219 */     if (!this.managers.contains(manager)) {
/* 220 */       if (this.type == 0) {
/* 221 */         this.managers.add(manager);
/*     */       }
/*     */       
/* 224 */       manager.addRateLimiter(this.upload_limiter, true);
/* 225 */       manager.addRateLimiter(this.download_limiter, false);
/*     */       
/* 227 */       int pri = getIntAttribute("at_up_pri", -1);
/*     */       
/* 229 */       if (pri > 0)
/*     */       {
/*     */ 
/*     */ 
/* 233 */         if (manager.getDownloadState() != null)
/*     */         {
/* 235 */           manager.updateAutoUploadPriority(this.UPLOAD_PRIORITY_KEY, true);
/*     */         }
/*     */       }
/*     */       
/* 239 */       this.category_listeners.dispatch(1, manager);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeManager(DownloadManagerState manager_state) {
/* 244 */     if (manager_state.getCategory() == this) {
/* 245 */       manager_state.setCategory(null);
/*     */       
/* 247 */       return;
/*     */     }
/* 249 */     DownloadManager manager = manager_state.getDownloadManager();
/*     */     
/*     */ 
/* 252 */     if (manager == null) {
/* 253 */       return;
/*     */     }
/*     */     
/* 256 */     removeTaggable(manager);
/*     */     
/* 258 */     if ((this.type != 0) || (this.managers.contains(manager))) {
/* 259 */       this.managers.remove(manager);
/*     */       
/* 261 */       manager.removeRateLimiter(this.upload_limiter, true);
/* 262 */       manager.removeRateLimiter(this.download_limiter, false);
/*     */       
/* 264 */       int pri = getIntAttribute("at_up_pri", -1);
/*     */       
/* 266 */       if (pri > 0)
/*     */       {
/*     */ 
/*     */ 
/* 270 */         if (manager.getDownloadState() != null)
/*     */         {
/* 272 */           manager.updateAutoUploadPriority(this.UPLOAD_PRIORITY_KEY, false);
/*     */         }
/*     */       }
/*     */       
/* 276 */       this.category_listeners.dispatch(2, manager);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadSpeed(int speed)
/*     */   {
/* 284 */     if (this.download_speed != speed)
/*     */     {
/* 286 */       this.download_speed = speed;
/*     */       
/* 288 */       CategoryManagerImpl.getInstance().saveCategories(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadSpeed()
/*     */   {
/* 295 */     return this.download_speed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUploadSpeed(int speed)
/*     */   {
/* 302 */     if (this.upload_speed != speed)
/*     */     {
/* 304 */       this.upload_speed = speed;
/*     */       
/* 306 */       CategoryManagerImpl.getInstance().saveCategories(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUploadSpeed()
/*     */   {
/* 313 */     return this.upload_speed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setAttributes(Map<String, String> a)
/*     */   {
/* 320 */     this.attributes.clear();
/* 321 */     this.attributes.putAll(a);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map<String, String> getAttributes()
/*     */   {
/* 327 */     return this.attributes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getStringAttribute(String name)
/*     */   {
/* 334 */     return (String)this.attributes.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStringAttribute(String name, String value)
/*     */   {
/* 342 */     String old = (String)this.attributes.put(name, value);
/*     */     
/* 344 */     if ((old == null) || (!old.equals(value)))
/*     */     {
/* 346 */       CategoryManagerImpl.getInstance().saveCategories(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getIntAttribute(String name)
/*     */   {
/* 355 */     return getIntAttribute(name, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getIntAttribute(String name, int def)
/*     */   {
/* 363 */     String str = getStringAttribute(name);
/*     */     
/* 365 */     if (str == null) {
/* 366 */       return def;
/*     */     }
/* 368 */     return Integer.parseInt(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIntAttribute(String name, int value)
/*     */   {
/* 376 */     String str_val = String.valueOf(value);
/*     */     
/* 378 */     String old = (String)this.attributes.put(name, str_val);
/*     */     
/* 380 */     if ((old == null) || (!old.equals(str_val)))
/*     */     {
/* 382 */       if (name.equals("at_up_pri"))
/*     */       {
/* 384 */         for (DownloadManager dm : this.managers)
/*     */         {
/* 386 */           dm.updateAutoUploadPriority(this.UPLOAD_PRIORITY_KEY, value > 0);
/*     */         }
/*     */       }
/*     */       
/* 390 */       CategoryManagerImpl.getInstance().saveCategories(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getBooleanAttribute(String name)
/*     */   {
/* 398 */     String str = getStringAttribute(name);
/*     */     
/* 400 */     return (str != null) && (str.equals("true"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBooleanAttribute(String name, boolean value)
/*     */   {
/* 408 */     String str_val = value ? "true" : "false";
/*     */     
/* 410 */     String old = (String)this.attributes.put(name, str_val);
/*     */     
/* 412 */     if ((old == null) || (!old.equals(str_val)))
/*     */     {
/* 414 */       CategoryManagerImpl.getInstance().saveCategories(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getTaggableTypes()
/*     */   {
/* 422 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getTagName(boolean localize)
/*     */   {
/* 429 */     if ((localize) && (
/* 430 */       (this.type == 1) || (this.type == 2))) {
/* 431 */       return MessageText.getString(getTagNameRaw());
/*     */     }
/*     */     
/* 434 */     return super.getTagName(localize);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean supportsTagRates()
/*     */   {
/* 440 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean supportsTagUploadLimit()
/*     */   {
/* 446 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean supportsTagDownloadLimit()
/*     */   {
/* 452 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTagUploadLimit()
/*     */   {
/* 458 */     return getUploadSpeed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTagUploadLimit(int bps)
/*     */   {
/* 465 */     setUploadSpeed(bps);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTagCurrentUploadRate()
/*     */   {
/* 471 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTagDownloadLimit()
/*     */   {
/* 477 */     return getDownloadSpeed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTagDownloadLimit(int bps)
/*     */   {
/* 484 */     setDownloadSpeed(bps);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTagCurrentDownloadRate()
/*     */   {
/* 490 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTagUploadPriority()
/*     */   {
/* 496 */     if (this.type == 0)
/*     */     {
/* 498 */       return getIntAttribute("at_up_pri");
/*     */     }
/*     */     
/*     */ 
/* 502 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTagUploadPriority(int priority)
/*     */   {
/* 510 */     setIntAttribute("at_up_pri", priority);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getCanBePublicDefault()
/*     */   {
/* 516 */     return this.type == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean supportsTagTranscode()
/*     */   {
/* 522 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getTagTranscodeTarget()
/*     */   {
/* 528 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTagTranscodeTarget(String uid, String display_name) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<DownloadManager> getTaggedDownloads()
/*     */   {
/* 541 */     AzureusCore core = AzureusCoreFactory.getSingleton();
/*     */     
/* 543 */     if (!core.isStarted())
/*     */     {
/* 545 */       return new IdentityHashSet();
/*     */     }
/* 547 */     return new IdentityHashSet(getDownloadManagers(core.getGlobalManager().getDownloadManagers()));
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<Taggable> getTagged()
/*     */   {
/* 553 */     return (Set)getTaggedDownloads();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTaggedCount()
/*     */   {
/* 559 */     return getTagged().size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasTaggable(Taggable t)
/*     */   {
/* 566 */     if (!(t instanceof DownloadManager))
/*     */     {
/* 568 */       return false;
/*     */     }
/*     */     
/* 571 */     if (this.type == 0)
/*     */     {
/* 573 */       return this.managers.contains(t);
/*     */     }
/* 575 */     if (this.type == 1)
/*     */     {
/* 577 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 581 */     DownloadManager dm = (DownloadManager)t;
/*     */     
/* 583 */     Category cat = dm.getDownloadState().getCategory();
/*     */     
/* 585 */     if ((cat == null) || (cat.getType() == 2))
/*     */     {
/* 587 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 591 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getRunStateCapabilities()
/*     */   {
/* 599 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasRunStateCapability(int capability)
/*     */   {
/* 606 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean[] getPerformableOperations(int[] ops)
/*     */   {
/* 613 */     return new boolean[ops.length];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void performOperation(int op)
/*     */   {
/* 620 */     Debug.out("derp");
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 626 */     if (!this.destroyed)
/*     */     {
/* 628 */       this.destroyed = true;
/*     */       
/* 630 */       removeTag();
/*     */     }
/*     */   }
/*     */   
/*     */   public int compareTo(Object b)
/*     */   {
/* 636 */     boolean aTypeIsUser = this.type == 0;
/* 637 */     boolean bTypeIsUser = ((Category)b).getType() == 0;
/* 638 */     if (aTypeIsUser == bTypeIsUser)
/* 639 */       return this.sName.compareToIgnoreCase(((Category)b).getName());
/* 640 */     if (aTypeIsUser)
/* 641 */       return 1;
/* 642 */     return -1;
/*     */   }
/*     */   
/*     */   public void dump(IndentWriter writer) {
/* 646 */     if (this.upload_speed != 0) {
/* 647 */       writer.println("up=" + this.upload_speed);
/*     */     }
/* 649 */     if (this.download_speed != 0) {
/* 650 */       writer.println("down=" + this.download_speed);
/*     */     }
/* 652 */     if (this.attributes.size() > 0)
/*     */     {
/* 654 */       writer.println("attributes: " + this.attributes);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/category/impl/CategoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */