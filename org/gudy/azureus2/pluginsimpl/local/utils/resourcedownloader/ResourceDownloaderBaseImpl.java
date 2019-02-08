/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
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
/*     */ public abstract class ResourceDownloaderBaseImpl
/*     */   implements ResourceDownloader
/*     */ {
/*     */   private static final String PR_PROPERTIES_SET = "!!!! properties set !!!!";
/*  44 */   private List listeners = new ArrayList();
/*     */   
/*     */   private boolean result_informed;
/*     */   
/*     */   private Object result_informed_data;
/*     */   private ResourceDownloaderBaseImpl parent;
/*  50 */   private List<ResourceDownloaderBaseImpl> children = new ArrayList();
/*     */   
/*     */   private boolean download_cancelled;
/*     */   
/*  54 */   private Map lc_key_properties = new HashMap();
/*     */   
/*  56 */   protected AEMonitor this_mon = new AEMonitor("ResourceDownloader");
/*     */   
/*     */ 
/*     */ 
/*     */   protected ResourceDownloaderBaseImpl(ResourceDownloaderBaseImpl _parent)
/*     */   {
/*  62 */     this.parent = _parent;
/*     */     
/*  64 */     if (this.parent != null)
/*     */     {
/*  66 */       this.parent.addChild(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ResourceDownloader getClone()
/*     */   {
/*  73 */     return getClone(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl paramResourceDownloaderBaseImpl);
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void setSize(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getBooleanProperty(String key)
/*     */     throws ResourceDownloaderException
/*     */   {
/*  90 */     return getBooleanProperty(key, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getBooleanProperty(String key, boolean maybe_delayed)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 100 */     Object obj = getProperty(key, maybe_delayed);
/*     */     
/* 102 */     if ((obj instanceof Boolean))
/*     */     {
/* 104 */       return ((Boolean)obj).booleanValue();
/*     */     }
/*     */     
/* 107 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLongProperty(String key)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 116 */     Object obj = getProperty(key);
/*     */     
/* 118 */     if ((obj == null) || (!(obj instanceof Number)))
/*     */     {
/* 120 */       return -1L;
/*     */     }
/*     */     
/* 123 */     return ((Number)obj).longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getStringProperty(String key)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 132 */     Object obj = getProperty(key);
/*     */     
/* 134 */     if ((obj == null) || ((obj instanceof String)))
/*     */     {
/* 136 */       return (String)obj;
/*     */     }
/*     */     
/* 139 */     if ((obj instanceof List))
/*     */     {
/* 141 */       List l = (List)obj;
/*     */       
/* 143 */       if (l.size() == 0)
/*     */       {
/* 145 */         return null;
/*     */       }
/*     */       
/* 148 */       obj = l.get(0);
/*     */       
/* 150 */       if ((obj instanceof String))
/*     */       {
/* 152 */         return (String)obj;
/*     */       }
/*     */     }
/*     */     
/* 156 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getProperty(String name)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 165 */     return getProperty(name, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object getProperty(String name, boolean maybe_delayed)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 175 */     Object res = getPropertySupport(name);
/*     */     
/* 177 */     if ((res != null) || (getPropertySupport("!!!! properties set !!!!") != null) || (name.equalsIgnoreCase("URL_Connection")) || (name.equalsIgnoreCase("URL_Connect_Timeout")) || (name.equalsIgnoreCase("URL_Read_Timeout")) || (name.equalsIgnoreCase("URL_Trust_Content_Length")) || (name.equalsIgnoreCase("URL_HTTP_VERB")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 185 */       return res;
/*     */     }
/*     */     
/* 188 */     if (maybe_delayed)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 193 */       getSize();
/*     */       
/* 195 */       return getPropertySupport(name);
/*     */     }
/*     */     
/*     */ 
/* 199 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object getPropertySupport(String name)
/*     */   {
/* 207 */     return this.lc_key_properties.get(name.toLowerCase(MessageText.LOCALE_ENGLISH));
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map getLCKeyProperties()
/*     */   {
/* 213 */     return this.lc_key_properties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getStringPropertySupport(String name)
/*     */   {
/* 220 */     Object obj = this.lc_key_properties.get(name.toLowerCase(MessageText.LOCALE_ENGLISH));
/*     */     
/* 222 */     if ((obj instanceof String))
/*     */     {
/* 224 */       return (String)obj;
/*     */     }
/*     */     
/* 227 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPropertiesSet()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 235 */     setProperty("!!!! properties set !!!!", "true");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setPropertySupport(String name, Object value)
/*     */   {
/* 243 */     boolean already_set = this.lc_key_properties.put(name.toLowerCase(MessageText.LOCALE_ENGLISH), value) == value;
/*     */     
/* 245 */     if ((this.parent != null) && (!already_set)) {
/*     */       try
/*     */       {
/* 248 */         this.parent.setProperty(name, value);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 252 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setProperties(ResourceDownloaderBaseImpl other)
/*     */   {
/* 261 */     Map p = other.lc_key_properties;
/*     */     
/* 263 */     Iterator it = p.keySet().iterator();
/*     */     
/* 265 */     while (it.hasNext())
/*     */     {
/* 267 */       String key = (String)it.next();
/*     */       try
/*     */       {
/* 270 */         setProperty(key, p.get(key));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 274 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setPropertyRecursive(String name, Object value)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 286 */     setProperty(name, value);
/*     */     
/* 288 */     for (ResourceDownloaderBaseImpl kid : getChildren())
/*     */     {
/* 290 */       kid.setPropertyRecursive(name, value);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean isAnonymous()
/*     */   {
/*     */     try
/*     */     {
/* 298 */       return getBooleanProperty("Anonymous", false);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 302 */       Debug.out(e);
/*     */     }
/*     */     
/* 305 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setParent(ResourceDownloader _parent)
/*     */   {
/* 312 */     ResourceDownloaderBaseImpl old_parent = this.parent;
/*     */     
/* 314 */     this.parent = ((ResourceDownloaderBaseImpl)_parent);
/*     */     
/* 316 */     if (old_parent != null)
/*     */     {
/* 318 */       old_parent.removeChild(this);
/*     */     }
/*     */     
/* 321 */     if (this.parent != null)
/*     */     {
/* 323 */       this.parent.addChild(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected ResourceDownloaderBaseImpl getParent()
/*     */   {
/* 330 */     return this.parent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addChild(ResourceDownloaderBaseImpl kid)
/*     */   {
/* 337 */     this.children.add(kid);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeChild(ResourceDownloaderBaseImpl kid)
/*     */   {
/* 344 */     this.children.remove(kid);
/*     */   }
/*     */   
/*     */ 
/*     */   protected List<ResourceDownloaderBaseImpl> getChildren()
/*     */   {
/* 350 */     return this.children;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getLogIndent()
/*     */   {
/* 356 */     String indent = "";
/*     */     
/* 358 */     ResourceDownloaderBaseImpl pos = this.parent;
/*     */     
/* 360 */     while (pos != null)
/*     */     {
/* 362 */       indent = indent + "  ";
/*     */       
/* 364 */       pos = pos.getParent();
/*     */     }
/*     */     
/* 367 */     return indent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addReportListener(ResourceDownloader rd)
/*     */   {
/* 376 */     rd.addListener(new ResourceDownloaderAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void reportActivity(ResourceDownloader downloader, String activity)
/*     */       {
/*     */ 
/*     */ 
/* 384 */         ResourceDownloaderBaseImpl.this.informActivity(activity);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */       {
/* 392 */         ResourceDownloaderBaseImpl.this.informActivity(downloader.getName() + ":" + e.getMessage());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void informPercentDone(int percentage)
/*     */   {
/* 401 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 404 */         ((ResourceDownloaderListener)this.listeners.get(i)).reportPercentComplete(this, percentage);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 408 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void informAmountComplete(long amount)
/*     */   {
/* 417 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 420 */         ((ResourceDownloaderListener)this.listeners.get(i)).reportAmountComplete(this, amount);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (NoSuchMethodError e) {}catch (AbstractMethodError e) {}catch (Throwable e)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 431 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportActivity(String str)
/*     */   {
/* 440 */     informActivity(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void informActivity(String activity)
/*     */   {
/* 447 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 450 */         ((ResourceDownloaderListener)this.listeners.get(i)).reportActivity(this, activity);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 454 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean informComplete(InputStream is)
/*     */   {
/* 463 */     if (!this.result_informed)
/*     */     {
/* 465 */       for (int i = 0; i < this.listeners.size(); i++) {
/*     */         try
/*     */         {
/* 468 */           if (!((ResourceDownloaderListener)this.listeners.get(i)).completed(this, is))
/*     */           {
/* 470 */             return false;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 474 */           Debug.printStackTrace(e);
/*     */           
/* 476 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 480 */       this.result_informed = true;
/*     */       
/* 482 */       this.result_informed_data = is;
/*     */     }
/*     */     
/* 485 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void informFailed(ResourceDownloaderException e)
/*     */   {
/* 492 */     if (!this.result_informed)
/*     */     {
/* 494 */       this.result_informed = true;
/*     */       
/* 496 */       this.result_informed_data = e;
/*     */       
/* 498 */       for (int i = 0; i < this.listeners.size(); i++) {
/*     */         try
/*     */         {
/* 501 */           ((ResourceDownloaderListener)this.listeners.get(i)).failed(this, e);
/*     */         }
/*     */         catch (Throwable f)
/*     */         {
/* 505 */           Debug.printStackTrace(f);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportActivity(ResourceDownloader downloader, String activity)
/*     */   {
/* 516 */     informActivity(activity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */   {
/* 524 */     informPercentDone(percentage);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportAmountComplete(ResourceDownloader downloader, long amount)
/*     */   {
/* 532 */     informAmountComplete(amount);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setCancelled()
/*     */   {
/* 538 */     this.download_cancelled = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 544 */     return this.download_cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(ResourceDownloaderListener l)
/*     */   {
/* 551 */     this.listeners.add(l);
/*     */     
/* 553 */     if (this.result_informed)
/*     */     {
/* 555 */       if ((this.result_informed_data instanceof InputStream))
/*     */       {
/* 557 */         l.completed(this, (InputStream)this.result_informed_data);
/*     */       }
/*     */       else {
/* 560 */         l.failed(this, (ResourceDownloaderException)this.result_informed_data);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(ResourceDownloaderListener l)
/*     */   {
/* 569 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderBaseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */