/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
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
/*     */ public class AEMonitor
/*     */   extends AEMonSem
/*     */ {
/*  33 */   private int dont_wait = 1;
/*  34 */   private int nests = 0;
/*  35 */   private int total_reserve = 0;
/*  36 */   private int total_release = 1;
/*     */   
/*     */   protected Thread owner;
/*     */   
/*     */   protected Thread last_waiter;
/*     */   
/*     */ 
/*     */   public AEMonitor(String _name)
/*     */   {
/*  45 */     super(_name, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enter()
/*     */   {
/*  56 */     Thread current_thread = Thread.currentThread();
/*     */     
/*  58 */     synchronized (this)
/*     */     {
/*  60 */       this.entry_count += 1L;
/*     */       
/*  62 */       if (this.owner == current_thread)
/*     */       {
/*  64 */         this.nests += 1;
/*     */       }
/*     */       else
/*     */       {
/*  68 */         if (this.dont_wait == 0)
/*     */         {
/*     */           try {
/*  71 */             this.waiting += 1;
/*     */             
/*  73 */             this.last_waiter = current_thread;
/*     */             
/*  75 */             if (this.waiting > 1) {}
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */             int spurious_count = 0;
/*     */             
/*     */             do
/*     */             {
/*  87 */               wait();
/*     */               
/*  89 */               if (this.total_reserve != this.total_release)
/*     */                 break;
/*  91 */               spurious_count++;
/*     */             }
/*  93 */             while (spurious_count <= 1024);
/*     */             
/*  95 */             this.waiting -= 1;
/*     */             
/*  97 */             Debug.out("AEMonitor: spurious wakeup limit exceeded");
/*     */             
/*  99 */             throw new Throwable("die die die");
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
/* 111 */             this.total_reserve += 1;
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*     */ 
/* 118 */             this.waiting -= 1;
/*     */             
/* 120 */             this.owner = current_thread;
/*     */             
/* 122 */             Debug.out("**** monitor interrupted ****");
/*     */             
/* 124 */             throw new RuntimeException("AEMonitor:interrupted");
/*     */           }
/*     */           finally
/*     */           {
/* 128 */             this.last_waiter = null;
/*     */           }
/*     */         }
/*     */         else {
/* 132 */           this.total_reserve += 1;
/*     */           
/* 134 */           this.dont_wait -= 1;
/*     */         }
/*     */         
/* 137 */         this.owner = current_thread;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean enter(int max_millis)
/*     */   {
/* 156 */     Thread current_thread = Thread.currentThread();
/*     */     
/* 158 */     synchronized (this)
/*     */     {
/* 160 */       this.entry_count += 1L;
/*     */       
/* 162 */       if (this.owner == current_thread)
/*     */       {
/* 164 */         this.nests += 1;
/*     */       }
/*     */       else
/*     */       {
/* 168 */         if (this.dont_wait == 0)
/*     */         {
/*     */           try {
/* 171 */             this.waiting += 1;
/*     */             
/* 173 */             this.last_waiter = current_thread;
/*     */             
/* 175 */             wait(max_millis);
/*     */             
/* 177 */             if (this.total_reserve == this.total_release)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 182 */               this.waiting -= 1;
/*     */               
/* 184 */               boolean bool = false;
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
/* 204 */               this.last_waiter = null;return bool;
/*     */             }
/* 187 */             this.total_reserve += 1;
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*     */ 
/* 194 */             this.waiting -= 1;
/*     */             
/* 196 */             this.owner = current_thread;
/*     */             
/* 198 */             Debug.out("**** monitor interrupted ****");
/*     */             
/* 200 */             throw new RuntimeException("AEMonitor:interrupted");
/*     */           }
/*     */           finally
/*     */           {
/* 204 */             this.last_waiter = null;
/*     */           }
/*     */         }
/*     */         else {
/* 208 */           this.total_reserve += 1;
/*     */           
/* 210 */           this.dont_wait -= 1;
/*     */         }
/*     */         
/* 213 */         this.owner = current_thread;
/*     */       }
/*     */     }
/*     */     
/* 217 */     return true;
/*     */   }
/*     */   
/*     */   public void exit()
/*     */   {
/*     */     try
/*     */     {
/* 224 */       synchronized (this)
/*     */       {
/* 226 */         if (this.nests > 0)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */           this.nests -= 1;
/*     */         }
/*     */         else
/*     */         {
/* 240 */           this.owner = null;
/*     */           
/* 242 */           this.total_release += 1;
/*     */           
/* 244 */           if (this.waiting != 0)
/*     */           {
/* 246 */             this.waiting -= 1;
/*     */             
/* 248 */             notify();
/*     */           }
/*     */           else
/*     */           {
/* 252 */             this.dont_wait += 1;
/*     */             
/* 254 */             if (this.dont_wait > 1)
/*     */             {
/* 256 */               Debug.out("**** AEMonitor '" + this.name + "': multiple exit detected");
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isHeld()
/*     */   {
/* 274 */     synchronized (this)
/*     */     {
/* 276 */       return this.owner == Thread.currentThread();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasWaiters()
/*     */   {
/* 283 */     synchronized (this)
/*     */     {
/* 285 */       return this.waiting > 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Map getSynchronisedMap(Map m)
/*     */   {
/* 293 */     return Collections.synchronizedMap(m);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */