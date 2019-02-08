/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
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
/*     */ public class ListenerManager<T>
/*     */ {
/*     */   private static final boolean TIME_LISTENERS = false;
/*     */   private final String name;
/*     */   private final ListenerManagerDispatcher<T> target;
/*     */   private ListenerManagerDispatcherWithException target_with_exception;
/*     */   private final boolean async;
/*     */   private AEThread2 async_thread;
/*     */   
/*     */   public static <T> ListenerManager<T> createManager(String name, ListenerManagerDispatcher<T> target)
/*     */   {
/*  57 */     return new ListenerManager(name, target, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> ListenerManager<T> createAsyncManager(String name, ListenerManagerDispatcher<T> target)
/*     */   {
/*  65 */     return new ListenerManager(name, target, true);
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
/*  77 */   private List<T> listeners = new ArrayList(0);
/*     */   
/*     */ 
/*     */   private List<Object[]> dispatch_queue;
/*     */   
/*     */ 
/*     */   private AESemaphore dispatch_sem;
/*     */   
/*     */   private boolean logged_too_many_listeners;
/*     */   
/*     */ 
/*     */   protected ListenerManager(String _name, ListenerManagerDispatcher<T> _target, boolean _async)
/*     */   {
/*  90 */     this.name = _name;
/*  91 */     this.target = _target;
/*  92 */     this.async = _async;
/*     */     
/*  94 */     if ((this.target instanceof ListenerManagerDispatcherWithException))
/*     */     {
/*  96 */       this.target_with_exception = ((ListenerManagerDispatcherWithException)this.target);
/*     */     }
/*     */     
/*  99 */     if (this.async)
/*     */     {
/* 101 */       this.dispatch_sem = new AESemaphore("ListenerManager::" + this.name);
/* 102 */       this.dispatch_queue = new LinkedList();
/*     */       
/* 104 */       if (this.target_with_exception != null)
/*     */       {
/* 106 */         throw new RuntimeException("Can't have an async manager with exceptions!");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(T listener)
/*     */   {
/* 115 */     if (listener == null)
/*     */     {
/* 117 */       Debug.out("Trying to add null listener to " + this.name);
/* 118 */       return;
/*     */     }
/*     */     
/* 121 */     synchronized (this)
/*     */     {
/* 123 */       ArrayList<T> new_listeners = new ArrayList(this.listeners);
/*     */       
/* 125 */       if (new_listeners.contains(listener)) {
/* 126 */         if (Constants.IS_CVS_VERSION) {
/* 127 */           Debug.out("check this out: listener added twice");
/*     */         }
/* 129 */         Logger.log(new LogEvent(LogIDs.CORE, 1, "addListener called but listener already added for " + this.name + "\n\t" + Debug.getStackTrace(true, false)));
/*     */       }
/*     */       
/*     */ 
/* 133 */       new_listeners.add(listener);
/*     */       
/* 135 */       if (new_listeners.size() > 50) {
/* 136 */         if (Constants.IS_CVS_VERSION) {
/* 137 */           Debug.out("check this out: lots of listeners!");
/* 138 */           if (!this.logged_too_many_listeners) {
/* 139 */             this.logged_too_many_listeners = true;
/* 140 */             Debug.out(String.valueOf(new_listeners));
/*     */           }
/*     */         }
/* 143 */         Logger.log(new LogEvent(LogIDs.CORE, 1, "addListener: over 50 listeners added for " + this.name + "\n\t" + Debug.getStackTrace(true, false)));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 148 */       this.listeners = new_listeners;
/*     */       
/* 150 */       if ((this.async) && (this.async_thread == null))
/*     */       {
/* 152 */         this.async_thread = new AEThread2(this.name, true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/* 157 */             ListenerManager.this.dispatchLoop();
/*     */           }
/*     */           
/* 160 */         };
/* 161 */         this.async_thread.start();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(Object listener)
/*     */   {
/* 170 */     synchronized (this)
/*     */     {
/* 172 */       ArrayList<T> new_listeners = new ArrayList(this.listeners);
/*     */       
/* 174 */       new_listeners.remove(listener);
/*     */       
/* 176 */       this.listeners = new_listeners;
/*     */       
/* 178 */       if ((this.async) && (this.listeners.size() == 0))
/*     */       {
/* 180 */         this.async_thread = null;
/*     */         
/*     */ 
/*     */ 
/* 184 */         this.dispatch_sem.release();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean hasListener(T listener)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 296	org/gudy/azureus2/core3/util/ListenerManager:listeners	Ljava/util/List;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 353 2 0
/*     */     //   14: aload_2
/*     */     //   15: monitorexit
/*     */     //   16: ireturn
/*     */     //   17: astore_3
/*     */     //   18: aload_2
/*     */     //   19: monitorexit
/*     */     //   20: aload_3
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #193	-> byte code offset #0
/*     */     //   Java source line #195	-> byte code offset #4
/*     */     //   Java source line #196	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	ListenerManager<T>
/*     */     //   0	22	1	listener	T
/*     */     //   2	17	2	Ljava/lang/Object;	Object
/*     */     //   17	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 202 */     synchronized (this)
/*     */     {
/* 204 */       this.listeners = new ArrayList();
/*     */       
/* 206 */       if (this.async)
/*     */       {
/* 208 */         this.async_thread = null;
/*     */         
/*     */ 
/*     */ 
/* 212 */         this.dispatch_sem.release();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public List<T> getListenersCopy()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 296	org/gudy/azureus2/core3/util/ListenerManager:listeners	Ljava/util/List;
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: areturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #222	-> byte code offset #0
/*     */     //   Java source line #224	-> byte code offset #4
/*     */     //   Java source line #225	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	ListenerManager<T>
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   public void dispatch(int type, Object value)
/*     */   {
/* 233 */     dispatch(type, value, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatch(int type, Object value, boolean blocking)
/*     */   {
/* 242 */     if (this.async)
/*     */     {
/* 244 */       AESemaphore sem = null;
/*     */       
/* 246 */       if (blocking)
/*     */       {
/* 248 */         sem = new AESemaphore("ListenerManager:blocker");
/*     */       }
/*     */       
/* 251 */       synchronized (this)
/*     */       {
/*     */ 
/*     */ 
/* 255 */         if (this.listeners.size() == 0)
/*     */         {
/* 257 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 263 */         this.dispatch_queue.add(new Object[] { this.listeners, new Integer(type), value, sem });
/*     */         
/* 265 */         if (this.async_thread == null)
/*     */         {
/* 267 */           this.async_thread = new AEThread2(this.name, true)
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/* 272 */               ListenerManager.this.dispatchLoop();
/*     */             }
/*     */             
/* 275 */           };
/* 276 */           this.async_thread.start();
/*     */         }
/*     */       }
/*     */       
/* 280 */       this.dispatch_sem.release();
/*     */       
/* 282 */       if (sem != null)
/*     */       {
/* 284 */         sem.reserve();
/*     */       }
/*     */     }
/*     */     else {
/* 288 */       if (this.target_with_exception != null)
/*     */       {
/* 290 */         throw new RuntimeException("call dispatchWithException, not dispatch");
/*     */       }
/*     */       
/*     */       List<T> listeners_ref;
/*     */       
/* 295 */       synchronized (this)
/*     */       {
/* 297 */         listeners_ref = this.listeners;
/*     */       }
/*     */       try
/*     */       {
/* 301 */         dispatchInternal(listeners_ref, type, value);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 305 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatchWithException(int type, Object value)
/*     */     throws Throwable
/*     */   {
/*     */     List<T> listeners_ref;
/*     */     
/*     */ 
/* 319 */     synchronized (this)
/*     */     {
/* 321 */       listeners_ref = this.listeners;
/*     */     }
/*     */     
/* 324 */     dispatchInternal(listeners_ref, type, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatch(T listener, int type, Object value)
/*     */   {
/* 333 */     dispatch(listener, type, value, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatch(T listener, int type, Object value, boolean blocking)
/*     */   {
/* 343 */     if (this.async)
/*     */     {
/* 345 */       AESemaphore sem = null;
/*     */       
/* 347 */       if (blocking)
/*     */       {
/* 349 */         sem = new AESemaphore("ListenerManager:blocker");
/*     */       }
/*     */       
/* 352 */       synchronized (this)
/*     */       {
/*     */ 
/*     */ 
/* 356 */         this.dispatch_queue.add(new Object[] { listener, new Integer(type), value, sem, null });
/*     */         
/* 358 */         if (this.async_thread == null)
/*     */         {
/* 360 */           this.async_thread = new AEThread2(this.name, true)
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/* 365 */               ListenerManager.this.dispatchLoop();
/*     */             }
/*     */             
/* 368 */           };
/* 369 */           this.async_thread.start();
/*     */         }
/*     */       }
/*     */       
/* 373 */       this.dispatch_sem.release();
/*     */       
/* 375 */       if (sem != null)
/*     */       {
/* 377 */         sem.reserve();
/*     */       }
/*     */     }
/*     */     else {
/* 381 */       if (this.target_with_exception != null)
/*     */       {
/* 383 */         throw new RuntimeException("call dispatchWithException, not dispatch");
/*     */       }
/*     */       
/* 386 */       doDispatch(listener, type, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getListenerName(T listener)
/*     */   {
/* 394 */     Class listener_class = listener.getClass();
/*     */     
/* 396 */     String res = listener_class.getName();
/*     */     try
/*     */     {
/* 399 */       Method getString = listener_class.getMethod("getString", new Class[0]);
/*     */       
/* 401 */       if (getString != null)
/*     */       {
/* 403 */         String s = (String)getString.invoke(listener, new Object[0]);
/*     */         
/* 405 */         res = res + " (" + s + ")";
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 411 */     return res;
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
/*     */   protected void doDispatch(T listener, int type, Object value)
/*     */   {
/*     */     try
/*     */     {
/* 437 */       this.target.dispatch(listener, type, value);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 442 */       Debug.printStackTrace(e);
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
/*     */   protected void doDispatchWithException(T listener, int type, Object value)
/*     */     throws Throwable
/*     */   {
/* 469 */     this.target_with_exception.dispatchWithException(listener, type, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dispatchInternal(List<T> listeners_ref, int type, Object value)
/*     */     throws Throwable
/*     */   {
/* 481 */     for (int i = 0; i < listeners_ref.size(); i++)
/*     */     {
/*     */ 
/* 484 */       if (this.target_with_exception != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */         doDispatchWithException(listeners_ref.get(i), type, value);
/*     */       }
/*     */       else
/*     */       {
/* 495 */         doDispatch(listeners_ref.get(i), type, value);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dispatchInternal(T listener, int type, Object value)
/*     */     throws Throwable
/*     */   {
/* 508 */     if (this.target_with_exception != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 515 */       doDispatchWithException(listener, type, value);
/*     */     }
/*     */     else
/*     */     {
/* 519 */       doDispatch(listener, type, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatchLoop()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 530 */       this.dispatch_sem.reserve();
/*     */       
/* 532 */       Object[] data = null;
/*     */       
/* 534 */       synchronized (this)
/*     */       {
/* 536 */         if ((this.async_thread == null) || (!this.async_thread.isCurrentThread()))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 542 */           this.dispatch_sem.release();
/*     */           
/* 544 */           break;
/*     */         }
/*     */         
/* 547 */         if (this.dispatch_queue.size() > 0)
/*     */         {
/* 549 */           data = (Object[])this.dispatch_queue.remove(0);
/*     */         }
/*     */       }
/*     */       
/* 553 */       if (data != null) {
/*     */         try
/*     */         {
/* 556 */           if (data.length == 4)
/*     */           {
/* 558 */             dispatchInternal((List)data[0], ((Integer)data[1]).intValue(), data[2]);
/*     */           }
/*     */           else
/*     */           {
/* 562 */             dispatchInternal(data[0], ((Integer)data[1]).intValue(), data[2]);
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 567 */           Debug.printStackTrace(e);
/*     */         }
/*     */         finally
/*     */         {
/* 571 */           if (data[3] != null)
/*     */           {
/* 573 */             ((AESemaphore)data[3]).release();
/*     */           }
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
/*     */   public static <T> void dispatchWithTimeout(List<T> _listeners, final ListenerManagerDispatcher<T> _dispatcher, long _timeout)
/*     */   {
/* 588 */     final List<T> listeners = new ArrayList(_listeners);
/*     */     
/* 590 */     final boolean[] completed = new boolean[listeners.size()];
/*     */     
/* 592 */     final AESemaphore timeout_sem = new AESemaphore("ListenerManager:dwt:timeout");
/*     */     
/* 594 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 596 */       final int f_i = i;
/*     */       
/* 598 */       new AEThread2("ListenerManager:dwt:dispatcher", true)
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try {
/* 603 */             _dispatcher.dispatch(listeners.get(f_i), -1, null);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 607 */             Debug.printStackTrace(e);
/*     */           }
/*     */           finally
/*     */           {
/* 611 */             completed[f_i] = true;
/*     */             
/* 613 */             timeout_sem.release();
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     
/* 619 */     boolean timeout_occurred = false;
/*     */     
/* 621 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 623 */       if (_timeout <= 0L)
/*     */       {
/* 625 */         timeout_occurred = true;
/*     */         
/* 627 */         break;
/*     */       }
/*     */       
/* 630 */       long start = SystemTime.getCurrentTime();
/*     */       
/* 632 */       if (!timeout_sem.reserve(_timeout))
/*     */       {
/* 634 */         timeout_occurred = true;
/*     */         
/* 636 */         break;
/*     */       }
/*     */       
/* 639 */       long end = SystemTime.getCurrentTime();
/*     */       
/* 641 */       if (end > start)
/*     */       {
/* 643 */         _timeout -= end - start;
/*     */       }
/*     */     }
/*     */     
/* 647 */     if (timeout_occurred)
/*     */     {
/* 649 */       String str = "";
/*     */       
/* 651 */       for (int i = 0; i < completed.length; i++)
/*     */       {
/* 653 */         if (completed[i] == 0)
/*     */         {
/* 655 */           str = str + (str.length() == 0 ? "" : ",") + listeners.get(i);
/*     */         }
/*     */       }
/*     */       
/* 659 */       if (str.length() > 0)
/*     */       {
/* 661 */         Debug.out("Listener dispatch timeout: failed = " + str);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public long size()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 296	org/gudy/azureus2/core3/util/ListenerManager:listeners	Ljava/util/List;
/*     */     //   8: invokeinterface 349 1 0
/*     */     //   13: i2l
/*     */     //   14: aload_1
/*     */     //   15: monitorexit
/*     */     //   16: lreturn
/*     */     //   17: astore_2
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: aload_2
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #669	-> byte code offset #0
/*     */     //   Java source line #671	-> byte code offset #4
/*     */     //   Java source line #672	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	ListenerManager<T>
/*     */     //   2	17	1	Ljava/lang/Object;	Object
/*     */     //   17	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ListenerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */