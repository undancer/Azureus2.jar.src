/*     */ package org.gudy.azureus2.pluginsimpl.local.update;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateException;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerDecisionListener;
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
/*     */ public class UpdateCheckInstanceImpl
/*     */   implements UpdateCheckInstance
/*     */ {
/*  39 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */ 
/*     */   private static UpdateCheckInstanceImpl active_checker;
/*     */   
/*  44 */   private List<UpdateCheckInstanceListener> listeners = new ArrayList();
/*  45 */   private List<UpdateImpl> updates = new ArrayList();
/*  46 */   private List<UpdateManagerDecisionListener> decision_listeners = new ArrayList();
/*     */   
/*     */ 
/*  49 */   private AESemaphore sem = new AESemaphore("UpdateCheckInstance");
/*     */   
/*     */   private UpdateManager manager;
/*     */   
/*     */   private int check_type;
/*     */   
/*     */   private String name;
/*     */   
/*     */   private UpdatableComponentImpl[] components;
/*     */   private UpdateCheckerImpl[] checkers;
/*     */   private boolean completed;
/*     */   private boolean cancelled;
/*  61 */   private boolean automatic = true;
/*  62 */   private boolean low_noise = false;
/*     */   
/*  64 */   protected AEMonitor this_mon = new AEMonitor("UpdateCheckInstance");
/*     */   
/*  66 */   private Map<Integer, Object> properties = new HashMap();
/*     */   
/*     */   protected UpdateCheckInstanceImpl(UpdateManager _manager, int _check_type, String _name, UpdatableComponentImpl[] _components) {
/*  69 */     this.properties.put(Integer.valueOf(1), Integer.valueOf(1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */     this.manager = _manager;
/*  80 */     this.check_type = _check_type;
/*  81 */     this.name = _name;
/*  82 */     this.components = _components;
/*     */     
/*  84 */     this.checkers = new UpdateCheckerImpl[this.components.length];
/*     */     
/*  86 */     for (int i = 0; i < this.components.length; i++)
/*     */     {
/*  88 */       UpdatableComponentImpl comp = this.components[i];
/*     */       
/*  90 */       this.checkers[i] = new UpdateCheckerImpl(this, comp, this.sem);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  97 */     return this.check_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 103 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addUpdatableComponent(UpdatableComponent component, boolean mandatory)
/*     */   {
/* 113 */     UpdatableComponentImpl comp = new UpdatableComponentImpl(component, mandatory);
/*     */     
/* 115 */     UpdatableComponentImpl[] new_comps = new UpdatableComponentImpl[this.components.length + 1];
/*     */     
/* 117 */     System.arraycopy(this.components, 0, new_comps, 0, this.components.length);
/*     */     
/* 119 */     new_comps[this.components.length] = comp;
/*     */     
/* 121 */     this.components = new_comps;
/*     */     
/*     */ 
/*     */ 
/* 125 */     UpdateCheckerImpl checker = new UpdateCheckerImpl(this, comp, this.sem);
/*     */     
/* 127 */     UpdateCheckerImpl[] new_checkers = new UpdateCheckerImpl[this.checkers.length + 1];
/*     */     
/* 129 */     System.arraycopy(this.checkers, 0, new_checkers, 0, this.checkers.length);
/*     */     
/* 131 */     new_checkers[this.checkers.length] = checker;
/*     */     
/* 133 */     this.checkers = new_checkers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAutomatic(boolean a)
/*     */   {
/* 140 */     this.automatic = a;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAutomatic()
/*     */   {
/* 146 */     return this.automatic;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLowNoise(boolean a)
/*     */   {
/* 153 */     this.low_noise = a;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLowNoise()
/*     */   {
/* 159 */     return this.low_noise;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getProperty(int property_name)
/*     */   {
/* 166 */     return this.properties.get(Integer.valueOf(property_name));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(int property_name, Object value)
/*     */   {
/* 174 */     this.properties.put(Integer.valueOf(property_name), value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/*     */     boolean run_now;
/*     */     
/*     */ 
/* 185 */     synchronized (UpdateCheckInstanceImpl.class)
/*     */     {
/* 187 */       if (active_checker == null)
/*     */       {
/*     */ 
/*     */ 
/* 191 */         active_checker = this;
/*     */         
/* 193 */         boolean run_now = true;
/*     */         
/* 195 */         new AEThread2("UCI:clearer")
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/*     */             for (;;)
/*     */             {
/*     */               try
/*     */               {
/* 205 */                 Thread.sleep(1000L);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */               
/*     */ 
/* 210 */               if (UpdateCheckInstanceImpl.this.isCompleteOrCancelled())
/*     */               {
/* 212 */                 boolean done = true;
/*     */                 
/* 214 */                 if (UpdateCheckInstanceImpl.this.completed)
/*     */                 {
/* 216 */                   Update[] updates = UpdateCheckInstanceImpl.this.getUpdates();
/*     */                   
/* 218 */                   for (Update update : updates)
/*     */                   {
/* 220 */                     if ((!update.isCancelled()) && (!update.isComplete()))
/*     */                     {
/* 222 */                       done = false;
/*     */                       
/* 224 */                       break;
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 
/* 229 */                 if (done)
/*     */                 {
/*     */                   try {
/* 232 */                     Thread.sleep(5000L);
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/* 239 */                   synchronized (UpdateCheckInstanceImpl.class)
/*     */                   {
/* 241 */                     UpdateCheckInstanceImpl.access$102(null);
/*     */                   }
/*     */                   
/* 244 */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/*     */       else
/*     */       {
/* 253 */         run_now = false;
/*     */         
/*     */ 
/*     */ 
/* 257 */         new AEThread2("UCI:waiter")
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/*     */             for (;;)
/*     */             {
/*     */               try
/*     */               {
/* 267 */                 Thread.sleep(1000L);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */               
/*     */ 
/* 272 */               boolean retry = false;
/*     */               
/* 274 */               synchronized (UpdateCheckInstanceImpl.class)
/*     */               {
/* 276 */                 if (UpdateCheckInstanceImpl.active_checker == null)
/*     */                 {
/* 278 */                   retry = true;
/*     */                 }
/*     */               }
/*     */               
/* 282 */               if (retry)
/*     */               {
/* 284 */                 UpdateCheckInstanceImpl.this.start();
/*     */                 
/* 286 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/*     */     }
/*     */     
/* 294 */     if (run_now)
/*     */     {
/* 296 */       startSupport();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void startSupport()
/*     */   {
/* 303 */     for (int i = 0; i < this.components.length; i++)
/*     */     {
/* 305 */       final UpdateCheckerImpl checker = this.checkers[i];
/*     */       
/* 307 */       new AEThread2("UpdatableComponent Checker:" + i, true)
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/* 313 */             checker.getComponent().checkForUpdate(checker);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 317 */             checker.reportProgress("Update check failed: " + Debug.getNestedExceptionMessage(e));
/*     */             
/* 319 */             e.printStackTrace();
/*     */             
/* 321 */             checker.failed();
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     
/* 327 */     new AEThread2("UpdatableComponent Completion Waiter", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 332 */         for (int i = 0; i < UpdateCheckInstanceImpl.this.components.length; i++)
/*     */         {
/* 334 */           UpdateCheckInstanceImpl.this.sem.reserve();
/*     */         }
/*     */         try
/*     */         {
/* 338 */           boolean mandatory_failed = false;
/*     */           
/* 340 */           for (int i = 0; i < UpdateCheckInstanceImpl.this.checkers.length; i++)
/*     */           {
/* 342 */             if ((UpdateCheckInstanceImpl.this.components[i].isMandatory()) && (UpdateCheckInstanceImpl.this.checkers[i].getFailed()))
/*     */             {
/* 344 */               mandatory_failed = true;
/*     */               
/* 346 */               break;
/*     */             }
/*     */           }
/*     */           
/* 350 */           List<UpdateImpl> target_updates = new ArrayList();
/*     */           
/*     */ 
/*     */ 
/* 354 */           if (mandatory_failed)
/*     */           {
/* 356 */             if (Logger.isEnabled()) {
/* 357 */               Logger.log(new LogEvent(UpdateCheckInstanceImpl.LOGID, 3, "Dropping all updates as a mandatory update check failed"));
/*     */             }
/*     */             
/*     */           }
/*     */           else
/*     */           {
/* 363 */             boolean mandatory_only = false;
/*     */             
/* 365 */             for (int i = 0; i < UpdateCheckInstanceImpl.this.updates.size(); i++)
/*     */             {
/* 367 */               UpdateImpl update = (UpdateImpl)UpdateCheckInstanceImpl.this.updates.get(i);
/*     */               
/* 369 */               if (update.isMandatory())
/*     */               {
/* 371 */                 mandatory_only = true;
/*     */                 
/* 373 */                 break;
/*     */               }
/*     */             }
/*     */             
/* 377 */             for (int i = 0; i < UpdateCheckInstanceImpl.this.updates.size(); i++)
/*     */             {
/* 379 */               UpdateImpl update = (UpdateImpl)UpdateCheckInstanceImpl.this.updates.get(i);
/*     */               
/* 381 */               if ((update.isMandatory()) || (!mandatory_only))
/*     */               {
/* 383 */                 target_updates.add(update);
/*     */ 
/*     */               }
/* 386 */               else if (Logger.isEnabled()) {
/* 387 */                 Logger.log(new LogEvent(UpdateCheckInstanceImpl.LOGID, 3, "Dropping update '" + update.getName() + "' as non-mandatory and " + "mandatory updates found"));
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 398 */           Collections.sort(target_updates, new Comparator()
/*     */           {
/*     */ 
/*     */ 
/*     */             public int compare(UpdateImpl o1, UpdateImpl o2)
/*     */             {
/*     */ 
/*     */ 
/* 406 */               int i1 = getIndex(o1);
/* 407 */               int i2 = getIndex(o2);
/*     */               
/* 409 */               return i1 - i2;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             private int getIndex(UpdateImpl update)
/*     */             {
/* 416 */               UpdatableComponentImpl component = update.getComponent();
/*     */               
/* 418 */               for (int i = 0; i < UpdateCheckInstanceImpl.this.components.length; i++)
/*     */               {
/* 420 */                 if (UpdateCheckInstanceImpl.this.components[i] == component)
/*     */                 {
/* 422 */                   return i;
/*     */                 }
/*     */               }
/*     */               
/* 426 */               Debug.out("Missing component!");
/*     */               
/* 428 */               return 0;
/*     */             }
/*     */             
/* 431 */           });
/* 432 */           UpdateCheckInstanceImpl.this.updates = target_updates;
/*     */         }
/*     */         finally
/*     */         {
/*     */           try {
/* 437 */             UpdateCheckInstanceImpl.this.this_mon.enter();
/*     */             
/* 439 */             if (UpdateCheckInstanceImpl.this.cancelled) {
/*     */               return;
/*     */             }
/*     */             
/*     */ 
/* 444 */             UpdateCheckInstanceImpl.this.completed = true;
/*     */           }
/*     */           finally
/*     */           {
/* 448 */             UpdateCheckInstanceImpl.this.this_mon.exit();
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 453 */         for (int i = 0; i < UpdateCheckInstanceImpl.this.listeners.size(); i++) {
/*     */           try
/*     */           {
/* 456 */             ((UpdateCheckInstanceListener)UpdateCheckInstanceImpl.this.listeners.get(i)).complete(UpdateCheckInstanceImpl.this);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 460 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UpdateImpl addUpdate(UpdatableComponentImpl comp, String update_name, String[] desc, String old_version, String new_version, ResourceDownloader[] downloaders, int restart_required)
/*     */   {
/*     */     try
/*     */     {
/* 478 */       this.this_mon.enter();
/*     */       
/* 480 */       UpdateImpl update = new UpdateImpl(this, comp, update_name, desc, old_version, new_version, downloaders, comp.isMandatory(), restart_required);
/*     */       
/*     */ 
/*     */ 
/* 484 */       this.updates.add(update);
/*     */       
/* 486 */       if (this.cancelled)
/*     */       {
/* 488 */         update.cancel();
/*     */       }
/*     */       
/* 491 */       return update;
/*     */     }
/*     */     finally
/*     */     {
/* 495 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public Update[] getUpdates()
/*     */   {
/*     */     try
/*     */     {
/* 503 */       this.this_mon.enter();
/*     */       
/* 505 */       Update[] res = new Update[this.updates.size()];
/*     */       
/* 507 */       this.updates.toArray(res);
/*     */       
/* 509 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 513 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdateChecker[] getCheckers()
/*     */   {
/* 520 */     return this.checkers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UpdateInstaller createInstaller()
/*     */     throws UpdateException
/*     */   {
/* 528 */     return this.manager.createInstaller();
/*     */   }
/*     */   
/*     */   public boolean isCompleteOrCancelled()
/*     */   {
/*     */     try
/*     */     {
/* 535 */       this.this_mon.enter();
/*     */       
/* 537 */       return (this.completed) || (this.cancelled);
/*     */     }
/*     */     finally
/*     */     {
/* 541 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cancel()
/*     */   {
/* 547 */     boolean just_do_updates = false;
/*     */     try
/*     */     {
/* 550 */       this.this_mon.enter();
/*     */       
/* 552 */       if (this.completed)
/*     */       {
/* 554 */         just_do_updates = true;
/*     */       }
/*     */       
/* 557 */       this.cancelled = true;
/*     */     }
/*     */     finally
/*     */     {
/* 561 */       this.this_mon.exit();
/*     */     }
/*     */     
/*     */ 
/* 565 */     for (int i = 0; i < this.updates.size(); i++)
/*     */     {
/* 567 */       ((UpdateImpl)this.updates.get(i)).cancel();
/*     */     }
/*     */     
/* 570 */     if (!just_do_updates)
/*     */     {
/* 572 */       for (int i = 0; i < this.checkers.length; i++)
/*     */       {
/* 574 */         if (this.checkers[i] != null)
/*     */         {
/* 576 */           this.checkers[i].cancel();
/*     */         }
/*     */       }
/*     */       
/* 580 */       for (int i = 0; i < this.listeners.size(); i++) {
/*     */         try
/*     */         {
/* 583 */           ((UpdateCheckInstanceListener)this.listeners.get(i)).cancelled(this);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 587 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 596 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */   public UpdateManager getManager()
/*     */   {
/* 602 */     return this.manager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object getDecision(Update update, int decision_type, String decision_name, String decision_description, Object decision_data)
/*     */   {
/* 613 */     for (int i = 0; i < this.decision_listeners.size(); i++)
/*     */     {
/* 615 */       Object res = ((UpdateManagerDecisionListener)this.decision_listeners.get(i)).decide(update, decision_type, decision_name, decision_description, decision_data);
/*     */       
/*     */ 
/*     */ 
/* 619 */       if (res != null)
/*     */       {
/* 621 */         return res;
/*     */       }
/*     */     }
/*     */     
/* 625 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDecisionListener(UpdateManagerDecisionListener l)
/*     */   {
/* 632 */     this.decision_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDecisionListener(UpdateManagerDecisionListener l)
/*     */   {
/* 639 */     this.decision_listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UpdateCheckInstanceListener l)
/*     */   {
/* 646 */     this.listeners.add(l);
/*     */     
/* 648 */     if (this.completed)
/*     */     {
/* 650 */       l.complete(this);
/*     */     }
/* 652 */     else if (this.cancelled)
/*     */     {
/* 654 */       l.cancelled(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UpdateCheckInstanceListener l)
/*     */   {
/* 662 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/update/UpdateCheckInstanceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */