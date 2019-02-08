/*     */ package org.gudy.azureus2.pluginsimpl.local.update;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.InputStream;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateException;
/*     */ import org.gudy.azureus2.plugins.update.UpdateListener;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
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
/*     */ public class UpdateImpl
/*     */   implements Update
/*     */ {
/*     */   private UpdateCheckInstanceImpl instance;
/*     */   private UpdatableComponentImpl component;
/*     */   private String name;
/*     */   private String[] description;
/*  45 */   private String relative_url_base = "";
/*     */   
/*     */   private String old_version;
/*     */   
/*     */   private String new_version;
/*     */   private ResourceDownloader[] downloaders;
/*     */   private boolean mandatory;
/*     */   private int restart_required;
/*     */   private String description_url;
/*     */   private Object user_object;
/*  55 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile boolean cancelled;
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile boolean complete;
/*     */   
/*     */ 
/*     */   private volatile boolean succeeded;
/*     */   
/*     */ 
/*     */ 
/*     */   protected UpdateImpl(UpdateCheckInstanceImpl _instance, UpdatableComponentImpl _component, String _name, String[] _desc, String _old_version, String _new_version, ResourceDownloader[] _downloaders, boolean _mandatory, int _restart_required)
/*     */   {
/*  72 */     this.instance = _instance;
/*  73 */     this.component = _component;
/*  74 */     this.name = _name;
/*  75 */     this.description = _desc;
/*  76 */     this.old_version = _old_version;
/*  77 */     this.new_version = _new_version;
/*  78 */     this.downloaders = _downloaders;
/*  79 */     this.mandatory = _mandatory;
/*  80 */     this.restart_required = _restart_required;
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
/*     */   public UpdateCheckInstance getCheckInstance()
/*     */   {
/* 105 */     return this.instance;
/*     */   }
/*     */   
/*     */ 
/*     */   protected UpdatableComponentImpl getComponent()
/*     */   {
/* 111 */     return this.component;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 117 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getDescription()
/*     */   {
/* 123 */     return this.description;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelativeURLBase()
/*     */   {
/* 129 */     return this.relative_url_base;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRelativeURLBase(String base)
/*     */   {
/* 136 */     this.relative_url_base = base;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDesciptionURL()
/*     */   {
/* 143 */     return this.description_url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDescriptionURL(String url)
/*     */   {
/* 150 */     this.description_url = url;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getOldVersion()
/*     */   {
/* 156 */     return this.old_version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNewVersion()
/*     */   {
/* 162 */     return this.new_version;
/*     */   }
/*     */   
/*     */ 
/*     */   public ResourceDownloader[] getDownloaders()
/*     */   {
/* 168 */     return this.downloaders;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isMandatory()
/*     */   {
/* 174 */     return this.mandatory;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRestartRequired(int _restart_required)
/*     */   {
/* 181 */     this.restart_required = _restart_required;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRestartRequired()
/*     */   {
/* 187 */     return this.restart_required;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUserObject(Object obj)
/*     */   {
/* 194 */     this.user_object = obj;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getUserObject()
/*     */   {
/* 200 */     return this.user_object;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 206 */     this.cancelled = true;
/*     */     
/* 208 */     for (int i = 0; i < this.downloaders.length; i++) {
/*     */       try
/*     */       {
/* 211 */         this.downloaders[i].cancel();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 215 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 219 */     Iterator it = this.listeners.iterator();
/*     */     
/* 221 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 224 */         ((UpdateListener)it.next()).cancelled(this);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 228 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void complete(boolean success)
/*     */   {
/* 237 */     this.complete = true;
/* 238 */     this.succeeded = success;
/*     */     
/* 240 */     Iterator it = this.listeners.iterator();
/*     */     
/* 242 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 245 */         ((UpdateListener)it.next()).complete(this);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 249 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 257 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/* 263 */     return this.complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean wasSuccessful()
/*     */   {
/* 269 */     return this.succeeded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getDecision(int decision_type, String decision_name, String decision_description, Object decision_data)
/*     */   {
/* 279 */     return this.instance.getDecision(this, decision_type, decision_name, decision_description, decision_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream verifyData(InputStream is, boolean force)
/*     */     throws UpdateException
/*     */   {
/* 290 */     return ((UpdateManagerImpl)this.instance.getManager()).verifyData(this, is, force);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UpdateListener l)
/*     */   {
/* 297 */     this.listeners.add(l);
/*     */     
/* 299 */     if (this.cancelled)
/*     */     {
/* 301 */       l.cancelled(this);
/*     */     }
/* 303 */     else if (this.complete)
/*     */     {
/* 305 */       l.complete(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UpdateListener l)
/*     */   {
/* 313 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/update/UpdateImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */