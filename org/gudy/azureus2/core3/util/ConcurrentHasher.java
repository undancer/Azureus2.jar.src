/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class ConcurrentHasher
/*     */ {
/*  37 */   protected static final ConcurrentHasher singleton = new ConcurrentHasher();
/*     */   
/*     */   protected int processor_num;
/*     */   
/*  41 */   protected final List<ConcurrentHasherRequest> requests = new LinkedList();
/*     */   
/*  43 */   protected final List<SHA1Hasher> hashers = new ArrayList();
/*     */   
/*  45 */   protected final AESemaphore request_sem = new AESemaphore("ConcHashReqQ");
/*  46 */   protected final AESemaphore scheduler_sem = new AESemaphore("ConcHashSched");
/*     */   
/*  48 */   protected final AEMonitor requests_mon = new AEMonitor("ConcurrentHasher:R");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  53 */   private static boolean friendly_hashing = COConfigurationManager.getBooleanParameter("diskmanager.friendly.hashchecking");
/*     */   
/*  55 */   static { COConfigurationManager.addParameterListener("diskmanager.friendly.hashchecking", new ParameterListener() {
/*     */       public void parameterChanged(String str) {
/*  57 */         ConcurrentHasher.access$002(COConfigurationManager.getBooleanParameter("diskmanager.friendly.hashchecking"));
/*     */       }
/*     */     }); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ConcurrentHasher getSingleton()
/*     */   {
/*  67 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean concurrentHashingAvailable()
/*     */   {
/*  73 */     return getSingleton().processor_num > 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ConcurrentHasher()
/*     */   {
/*  82 */     this.processor_num = Runtime.getRuntime().availableProcessors();
/*     */     
/*     */ 
/*     */ 
/*  86 */     if (this.processor_num <= 0)
/*     */     {
/*  88 */       this.processor_num = 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  93 */     for (int i = 0; i < this.processor_num + 1; i++)
/*     */     {
/*  95 */       this.scheduler_sem.release();
/*     */     }
/*     */     
/*  98 */     final ThreadPool pool = new ThreadPool("ConcurrentHasher", 64);
/*     */     
/* 100 */     new AEThread2("ConcurrentHasher:scheduler", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/*     */         for (;;)
/*     */         {
/*     */ 
/* 109 */           ConcurrentHasher.this.request_sem.reserve();
/*     */           
/*     */           final ConcurrentHasherRequest req;
/*     */           
/*     */           final SHA1Hasher hasher;
/*     */           
/*     */           try
/*     */           {
/* 117 */             ConcurrentHasher.this.requests_mon.enter();
/*     */             
/* 119 */             req = (ConcurrentHasherRequest)ConcurrentHasher.this.requests.remove(0);
/*     */             SHA1Hasher hasher;
/* 121 */             if (ConcurrentHasher.this.hashers.size() == 0)
/*     */             {
/* 123 */               hasher = new SHA1Hasher();
/*     */             }
/*     */             else
/*     */             {
/* 127 */               hasher = (SHA1Hasher)ConcurrentHasher.this.hashers.remove(ConcurrentHasher.this.hashers.size() - 1);
/*     */             }
/*     */           }
/*     */           finally {
/* 131 */             ConcurrentHasher.this.requests_mon.exit();
/*     */           }
/*     */           
/* 134 */           pool.run(new AERunnable()
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */               try
/*     */               {
/* 141 */                 req.run(hasher);
/*     */               } finally { try { int size;
/*     */                   int max;
/*     */                   int min;
/* 145 */                   ConcurrentHasher.this.requests_mon.enter();
/*     */                   
/* 147 */                   ConcurrentHasher.this.hashers.add(hasher);
/*     */                 }
/*     */                 finally
/*     */                 {
/* 151 */                   ConcurrentHasher.this.requests_mon.exit();
/*     */                 }
/*     */                 
/* 154 */                 if ((ConcurrentHasher.friendly_hashing) && (req.isLowPriority())) {
/*     */                   try
/*     */                   {
/* 157 */                     int size = req.getSize();
/*     */                     
/*     */ 
/*     */ 
/* 161 */                     int max = 250;
/* 162 */                     int min = 50;
/*     */                     
/* 164 */                     size /= 1024;
/*     */                     
/* 166 */                     size /= 8;
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/* 171 */                     size = Math.min(size, 250);
/* 172 */                     size = Math.max(size, 50);
/*     */                     
/* 174 */                     Thread.sleep(size);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 178 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                 }
/*     */                 
/* 182 */                 ConcurrentHasher.this.scheduler_sem.release();
/*     */               }
/*     */             }
/*     */           });
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
/*     */   public ConcurrentHasherRequest addRequest(ByteBuffer buffer)
/*     */   {
/* 200 */     return addRequest(buffer, null, false);
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
/*     */   public ConcurrentHasherRequest addRequest(ByteBuffer buffer, ConcurrentHasherRequestListener listener, boolean low_priorty)
/*     */   {
/* 219 */     ConcurrentHasherRequest req = new ConcurrentHasherRequest(this, buffer, listener, low_priorty);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 228 */     this.scheduler_sem.reserve();
/*     */     try
/*     */     {
/* 231 */       this.requests_mon.enter();
/*     */       
/* 233 */       this.requests.add(req);
/*     */     }
/*     */     finally
/*     */     {
/* 237 */       this.requests_mon.exit();
/*     */     }
/*     */     
/* 240 */     this.request_sem.release();
/*     */     
/* 242 */     return req;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ConcurrentHasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */