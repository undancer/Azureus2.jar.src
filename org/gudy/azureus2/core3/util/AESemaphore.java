/*     */ package org.gudy.azureus2.core3.util;
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
/*     */ public class AESemaphore
/*     */   extends AEMonSem
/*     */ {
/*  30 */   private int dont_wait = 0;
/*     */   
/*  32 */   private int total_reserve = 0;
/*  33 */   private int total_release = 0;
/*     */   
/*  35 */   private boolean released_forever = false;
/*     */   
/*     */ 
/*     */   protected Thread latest_waiter;
/*     */   
/*     */ 
/*     */   public AESemaphore(String _name)
/*     */   {
/*  43 */     this(_name, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AESemaphore(String _name, int count)
/*     */   {
/*  51 */     super(_name, false);
/*     */     
/*  53 */     this.dont_wait = count;
/*  54 */     this.total_release = count;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reserve()
/*     */   {
/*  60 */     if (!reserve(0L))
/*     */     {
/*  62 */       Debug.out("AESemaphore: reserve completed without acquire [" + getString() + "]");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean reserve(long millis)
/*     */   {
/*  70 */     return reserveSupport(millis, 1) == 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean reserveIfAvailable()
/*     */   {
/*  76 */     synchronized (this)
/*     */     {
/*  78 */       if ((this.released_forever) || (this.dont_wait > 0))
/*     */       {
/*  80 */         reserve();
/*     */         
/*  82 */         return true;
/*     */       }
/*     */       
/*     */ 
/*  86 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int reserveSet(int max_to_reserve, long millis)
/*     */   {
/*  96 */     return reserveSupport(millis, max_to_reserve);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int reserveSet(int max_to_reserve)
/*     */   {
/* 103 */     return reserveSupport(0L, max_to_reserve);
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
/*     */   protected int reserveSupport(long millis, int max_to_reserve)
/*     */   {
/* 116 */     synchronized (this)
/*     */     {
/* 118 */       this.entry_count += 1L;
/*     */       
/*     */ 
/*     */ 
/* 122 */       if (this.released_forever)
/*     */       {
/* 124 */         return 1;
/*     */       }
/*     */       
/* 127 */       if (this.dont_wait == 0) {
/*     */         try
/*     */         {
/* 130 */           this.waiting += 1;
/*     */           
/* 132 */           this.latest_waiter = Thread.currentThread();
/*     */           
/* 134 */           if ((this.waiting <= 1) || 
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 139 */             (millis == 0L))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 144 */             spurious_count = 0;
/*     */             
/*     */             do
/*     */             {
/* 148 */               wait();
/*     */               
/* 150 */               if (this.total_reserve != this.total_release)
/*     */                 break;
/* 152 */               spurious_count++;
/*     */             }
/* 154 */             while (spurious_count <= 1024);
/*     */             
/* 156 */             Debug.out("AESemaphore: spurious wakeup limit exceeded");
/*     */             
/* 158 */             throw new Throwable("die die die");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 174 */             wait(millis);
/*     */           }
/*     */           
/* 177 */           if (this.total_reserve == this.total_release)
/*     */           {
/*     */ 
/*     */ 
/* 181 */             this.waiting -= 1;
/*     */             
/* 183 */             spurious_count = 0;
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
/* 200 */             this.latest_waiter = null;return spurious_count;
/*     */           }
/* 186 */           this.total_reserve += 1;
/*     */           
/* 188 */           int spurious_count = 1;
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
/* 200 */           this.latest_waiter = null;return spurious_count;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 192 */           this.waiting -= 1;
/*     */           
/* 194 */           Debug.out("**** semaphore operation interrupted ****");
/*     */           
/* 196 */           throw new RuntimeException("Semaphore: operation interrupted", e);
/*     */         }
/*     */         finally
/*     */         {
/* 200 */           this.latest_waiter = null;
/*     */         }
/*     */       }
/* 203 */       int num_to_get = max_to_reserve > this.dont_wait ? this.dont_wait : max_to_reserve;
/*     */       
/* 205 */       this.dont_wait -= num_to_get;
/*     */       
/* 207 */       this.total_reserve += num_to_get;
/*     */       
/* 209 */       return num_to_get;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void release()
/*     */   {
/*     */     try
/*     */     {
/* 218 */       synchronized (this)
/*     */       {
/*     */ 
/*     */ 
/* 222 */         this.total_release += 1;
/*     */         
/* 224 */         if (this.waiting != 0)
/*     */         {
/* 226 */           this.waiting -= 1;
/*     */           
/* 228 */           notify();
/*     */         }
/*     */         else {
/* 231 */           this.dont_wait += 1;
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
/*     */   public void releaseAllWaiters()
/*     */   {
/* 246 */     synchronized (this)
/*     */     {
/* 248 */       int x = this.waiting;
/*     */       
/* 250 */       for (int i = 0; i < x; i++) {
/* 251 */         release();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void releaseForever()
/*     */   {
/* 259 */     synchronized (this)
/*     */     {
/* 261 */       releaseAllWaiters();
/*     */       
/* 263 */       this.released_forever = true;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isReleasedForever()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 125	org/gudy/azureus2/core3/util/AESemaphore:released_forever	Z
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #270	-> byte code offset #0
/*     */     //   Java source line #272	-> byte code offset #4
/*     */     //   Java source line #273	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	AESemaphore
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getValue()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 120	org/gudy/azureus2/core3/util/AESemaphore:dont_wait	I
/*     */     //   8: aload_0
/*     */     //   9: getfield 123	org/gudy/azureus2/core3/util/AESemaphore:waiting	I
/*     */     //   12: isub
/*     */     //   13: aload_1
/*     */     //   14: monitorexit
/*     */     //   15: ireturn
/*     */     //   16: astore_2
/*     */     //   17: aload_1
/*     */     //   18: monitorexit
/*     */     //   19: aload_2
/*     */     //   20: athrow
/*     */     // Line number table:
/*     */     //   Java source line #279	-> byte code offset #0
/*     */     //   Java source line #281	-> byte code offset #4
/*     */     //   Java source line #282	-> byte code offset #16
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	AESemaphore
/*     */     //   2	16	1	Ljava/lang/Object;	Object
/*     */     //   16	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	16	finally
/*     */     //   16	19	16	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String getString()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: new 76	java/lang/StringBuilder
/*     */     //   7: dup
/*     */     //   8: invokespecial 131	java/lang/StringBuilder:<init>	()V
/*     */     //   11: ldc 10
/*     */     //   13: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   16: aload_0
/*     */     //   17: getfield 120	org/gudy/azureus2/core3/util/AESemaphore:dont_wait	I
/*     */     //   20: invokevirtual 133	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   23: ldc 4
/*     */     //   25: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   28: aload_0
/*     */     //   29: getfield 123	org/gudy/azureus2/core3/util/AESemaphore:waiting	I
/*     */     //   32: invokevirtual 133	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   35: ldc 3
/*     */     //   37: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   40: aload_0
/*     */     //   41: getfield 122	org/gudy/azureus2/core3/util/AESemaphore:total_reserve	I
/*     */     //   44: invokevirtual 133	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   47: ldc 2
/*     */     //   49: invokevirtual 134	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   52: aload_0
/*     */     //   53: getfield 121	org/gudy/azureus2/core3/util/AESemaphore:total_release	I
/*     */     //   56: invokevirtual 133	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   59: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   62: aload_1
/*     */     //   63: monitorexit
/*     */     //   64: areturn
/*     */     //   65: astore_2
/*     */     //   66: aload_1
/*     */     //   67: monitorexit
/*     */     //   68: aload_2
/*     */     //   69: athrow
/*     */     // Line number table:
/*     */     //   Java source line #288	-> byte code offset #0
/*     */     //   Java source line #290	-> byte code offset #4
/*     */     //   Java source line #291	-> byte code offset #65
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	70	0	this	AESemaphore
/*     */     //   2	65	1	Ljava/lang/Object;	Object
/*     */     //   65	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	64	65	finally
/*     */     //   65	68	65	finally
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AESemaphore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */