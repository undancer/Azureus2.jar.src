/*     */ package org.gudy.azureus2.pluginsimpl.local.update;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckerListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateException;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateProgressListener;
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
/*     */ public class UpdateCheckerImpl
/*     */   implements UpdateChecker
/*     */ {
/*     */   protected UpdateCheckInstanceImpl check_instance;
/*     */   protected UpdatableComponentImpl component;
/*     */   protected AESemaphore semaphore;
/*     */   protected boolean completed;
/*     */   protected boolean failed;
/*     */   protected boolean cancelled;
/*     */   protected boolean sem_released;
/*  49 */   protected List listeners = new ArrayList();
/*  50 */   protected List progress_listeners = new ArrayList();
/*     */   
/*  52 */   protected AEMonitor this_mon = new AEMonitor("UpdateChecker");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UpdateCheckerImpl(UpdateCheckInstanceImpl _check_instance, UpdatableComponentImpl _component, AESemaphore _sem)
/*     */   {
/*  60 */     this.check_instance = _check_instance;
/*  61 */     this.component = _component;
/*  62 */     this.semaphore = _sem;
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdateCheckInstance getCheckInstance()
/*     */   {
/*  68 */     return this.check_instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Update addUpdate(String name, String[] description, String old_version, String new_version, ResourceDownloader downloader, int restart_required)
/*     */   {
/*  80 */     return addUpdate(name, description, old_version, new_version, new ResourceDownloader[] { downloader }, restart_required);
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
/*     */   public Update addUpdate(String name, String[] description, String old_version, String new_version, ResourceDownloader[] downloaders, int restart_required)
/*     */   {
/*  95 */     reportProgress("Adding update: " + name);
/*     */     
/*  97 */     return this.check_instance.addUpdate(this.component, name, description, old_version, new_version, downloaders, restart_required);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UpdateInstaller createInstaller()
/*     */     throws UpdateException
/*     */   {
/* 107 */     return this.check_instance.createInstaller();
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdatableComponent getComponent()
/*     */   {
/* 113 */     return this.component.getComponent();
/*     */   }
/*     */   
/*     */   public void completed()
/*     */   {
/*     */     try
/*     */     {
/* 120 */       this.this_mon.enter();
/*     */       
/* 122 */       if (!this.sem_released)
/*     */       {
/* 124 */         this.completed = true;
/*     */         
/* 126 */         for (int i = 0; i < this.listeners.size(); i++) {
/*     */           try
/*     */           {
/* 129 */             ((UpdateCheckerListener)this.listeners.get(i)).completed(this);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 133 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         
/* 137 */         this.sem_released = true;
/*     */         
/* 139 */         this.semaphore.release();
/*     */       }
/*     */     }
/*     */     finally {
/* 143 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void failed()
/*     */   {
/*     */     try
/*     */     {
/* 151 */       this.this_mon.enter();
/*     */       
/* 153 */       if (!this.sem_released)
/*     */       {
/* 155 */         this.failed = true;
/*     */         
/* 157 */         for (int i = 0; i < this.listeners.size(); i++) {
/*     */           try
/*     */           {
/* 160 */             ((UpdateCheckerListener)this.listeners.get(i)).failed(this);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 164 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         
/* 168 */         this.sem_released = true;
/*     */         
/* 170 */         this.semaphore.release();
/*     */       }
/*     */     }
/*     */     finally {
/* 174 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getFailed()
/*     */   {
/* 181 */     return this.failed;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancel()
/*     */   {
/* 187 */     this.cancelled = true;
/*     */     
/* 189 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 192 */         ((UpdateCheckerListener)this.listeners.get(i)).cancelled(this);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 196 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(UpdateCheckerListener l)
/*     */   {
/*     */     try
/*     */     {
/* 206 */       this.this_mon.enter();
/*     */       
/* 208 */       this.listeners.add(l);
/*     */       
/* 210 */       if (this.failed)
/*     */       {
/* 212 */         l.failed(this);
/*     */       }
/* 214 */       else if (this.completed)
/*     */       {
/* 216 */         l.completed(this);
/*     */       }
/*     */       
/* 219 */       if (this.cancelled)
/*     */       {
/* 221 */         l.cancelled(this);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 226 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(UpdateCheckerListener l)
/*     */   {
/*     */     try
/*     */     {
/* 235 */       this.this_mon.enter();
/*     */       
/* 237 */       this.listeners.remove(l);
/*     */     }
/*     */     finally
/*     */     {
/* 241 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportProgress(String str)
/*     */   {
/* 249 */     List ref = this.progress_listeners;
/*     */     
/* 251 */     for (int i = 0; i < ref.size(); i++) {
/*     */       try
/*     */       {
/* 254 */         ((UpdateProgressListener)ref.get(i)).reportProgress(str);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 258 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addProgressListener(UpdateProgressListener l)
/*     */   {
/*     */     try
/*     */     {
/* 268 */       this.this_mon.enter();
/*     */       
/* 270 */       List new_l = new ArrayList(this.progress_listeners);
/*     */       
/* 272 */       new_l.add(l);
/*     */       
/* 274 */       this.progress_listeners = new_l;
/*     */     }
/*     */     finally
/*     */     {
/* 278 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeProgressListener(UpdateProgressListener l)
/*     */   {
/*     */     try
/*     */     {
/* 287 */       this.this_mon.enter();
/*     */       
/* 289 */       List new_l = new ArrayList(this.progress_listeners);
/*     */       
/* 291 */       new_l.remove(l);
/*     */       
/* 293 */       this.progress_listeners = new_l;
/*     */     }
/*     */     finally
/*     */     {
/* 297 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/update/UpdateCheckerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */