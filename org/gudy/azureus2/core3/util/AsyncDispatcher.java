/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.LinkedList;
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
/*     */ public class AsyncDispatcher
/*     */ {
/*     */   private final String name;
/*     */   private AEThread2 thread;
/*  29 */   private int priority = 5;
/*     */   private AERunnable queue_head;
/*     */   private LinkedList<AERunnable> queue_tail;
/*  32 */   final AESemaphore queue_sem = new AESemaphore("AsyncDispatcher");
/*     */   
/*     */   private int num_priority;
/*     */   
/*     */   final int quiesce_after_millis;
/*     */   
/*     */ 
/*     */   public AsyncDispatcher()
/*     */   {
/*  41 */     this("AsyncDispatcher: " + Debug.getLastCallerShort(), 10000);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AsyncDispatcher(String name)
/*     */   {
/*  48 */     this(name, 10000);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AsyncDispatcher(int quiesce_after_millis)
/*     */   {
/*  55 */     this("AsyncDispatcher: " + Debug.getLastCallerShort(), quiesce_after_millis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AsyncDispatcher(String _name, int _quiesce_after_millis)
/*     */   {
/*  63 */     this.name = _name;
/*  64 */     this.quiesce_after_millis = _quiesce_after_millis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void dispatch(AERunnable target)
/*     */   {
/*  71 */     dispatch(target, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatch(AERunnable target, boolean is_priority)
/*     */   {
/*  79 */     synchronized (this)
/*     */     {
/*  81 */       if (this.queue_head == null)
/*     */       {
/*  83 */         this.queue_head = target;
/*     */         
/*  85 */         if (is_priority)
/*     */         {
/*  87 */           this.num_priority += 1;
/*     */         }
/*     */       }
/*     */       else {
/*  91 */         if (this.queue_tail == null)
/*     */         {
/*  93 */           this.queue_tail = new LinkedList();
/*     */         }
/*     */         
/*  96 */         if (is_priority)
/*     */         {
/*  98 */           if (this.num_priority == 0)
/*     */           {
/* 100 */             this.queue_tail.add(0, this.queue_head);
/*     */             
/* 102 */             this.queue_head = target;
/*     */           }
/*     */           else
/*     */           {
/* 106 */             this.queue_tail.add(this.num_priority - 1, target);
/*     */           }
/*     */           
/* 109 */           this.num_priority += 1;
/*     */         }
/*     */         else
/*     */         {
/* 113 */           this.queue_tail.add(target);
/*     */         }
/*     */       }
/*     */       
/* 117 */       if (this.thread == null)
/*     */       {
/* 119 */         this.thread = new AEThread2(this.name, true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/*     */             for (;;)
/*     */             {
/* 127 */               AsyncDispatcher.this.queue_sem.reserve(AsyncDispatcher.this.quiesce_after_millis);
/*     */               
/* 129 */               AERunnable to_run = null;
/*     */               
/* 131 */               synchronized (AsyncDispatcher.this)
/*     */               {
/* 133 */                 if (AsyncDispatcher.this.queue_head == null)
/*     */                 {
/* 135 */                   AsyncDispatcher.this.queue_tail = null;
/*     */                   
/* 137 */                   AsyncDispatcher.this.thread = null;
/*     */                   
/* 139 */                   break;
/*     */                 }
/*     */                 
/* 142 */                 to_run = AsyncDispatcher.this.queue_head;
/*     */                 
/* 144 */                 if ((AsyncDispatcher.this.queue_tail != null) && (!AsyncDispatcher.this.queue_tail.isEmpty()))
/*     */                 {
/* 146 */                   AsyncDispatcher.this.queue_head = ((AERunnable)AsyncDispatcher.this.queue_tail.removeFirst());
/*     */                 }
/*     */                 else
/*     */                 {
/* 150 */                   AsyncDispatcher.this.queue_head = null;
/*     */                 }
/*     */                 
/* 153 */                 if (AsyncDispatcher.this.num_priority > 0)
/*     */                 {
/* 155 */                   AsyncDispatcher.access$310(AsyncDispatcher.this);
/*     */                 }
/*     */               }
/*     */               try
/*     */               {
/* 160 */                 to_run.runSupport();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 164 */                 Debug.printStackTrace(e);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 169 */         };
/* 170 */         this.thread.setPriority(this.priority);
/*     */         
/* 172 */         this.thread.start();
/*     */       }
/*     */     }
/*     */     
/* 176 */     this.queue_sem.release();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isQuiescent()
/*     */   {
/* 182 */     synchronized (this)
/*     */     {
/* 184 */       return this.thread == null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getQueueSize()
/*     */   {
/* 191 */     synchronized (this)
/*     */     {
/* 193 */       int result = this.queue_head == null ? 0 : 1;
/*     */       
/* 195 */       if (this.queue_tail != null)
/*     */       {
/* 197 */         result += this.queue_tail.size();
/*     */       }
/*     */       
/* 200 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPriority(int p)
/*     */   {
/* 208 */     this.priority = p;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDispatchThread()
/*     */   {
/* 214 */     synchronized (this)
/*     */     {
/* 216 */       return (this.thread != null) && (this.thread.isCurrentThread());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AsyncDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */