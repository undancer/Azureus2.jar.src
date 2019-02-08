/*     */ package com.aelitis.azureus.core.networkmanager;
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
/*     */ public abstract class NetworkConnectionHelper
/*     */   implements NetworkConnectionBase
/*     */ {
/*     */   private int upload_limit;
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
/*  29 */   private final LimitedRateGroup upload_limiter = new LimitedRateGroup()
/*     */   {
/*     */     public String getName()
/*     */     {
/*  33 */       return "per_con_up: " + NetworkConnectionHelper.this.getString();
/*     */     }
/*     */     
/*     */     public int getRateLimitBytesPerSecond() {
/*  37 */       return NetworkConnectionHelper.this.upload_limit;
/*     */     }
/*     */     
/*     */     public boolean isDisabled()
/*     */     {
/*  42 */       return NetworkConnectionHelper.this.upload_limit == -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void updateBytesUsed(int used) {}
/*     */   };
/*     */   
/*     */ 
/*     */   private int download_limit;
/*     */   
/*  53 */   private final LimitedRateGroup download_limiter = new LimitedRateGroup()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getName() {
/*  59 */       return "per_con_down: " + NetworkConnectionHelper.this.getString(); }
/*     */     
/*  61 */     public int getRateLimitBytesPerSecond() { return NetworkConnectionHelper.this.download_limit; }
/*     */     
/*     */     public boolean isDisabled()
/*     */     {
/*  65 */       return NetworkConnectionHelper.this.download_limit == -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void updateBytesUsed(int used) {}
/*     */   };
/*     */   
/*     */ 
/*  74 */   private volatile LimitedRateGroup[] upload_limiters = { this.upload_limiter };
/*  75 */   private volatile LimitedRateGroup[] download_limiters = { this.download_limiter };
/*     */   
/*     */ 
/*     */   public int getUploadLimit()
/*     */   {
/*  80 */     return this.upload_limit;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadLimit()
/*     */   {
/*  86 */     return this.download_limit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUploadLimit(int limit)
/*     */   {
/*  93 */     this.upload_limit = limit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadLimit(int limit)
/*     */   {
/* 100 */     this.download_limit = limit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRateLimiter(LimitedRateGroup limiter, boolean upload)
/*     */   {
/* 108 */     synchronized (this)
/*     */     {
/* 110 */       if (upload)
/*     */       {
/* 112 */         for (int i = 0; i < this.upload_limiters.length; i++)
/*     */         {
/* 114 */           if (this.upload_limiters[i] == limiter)
/*     */           {
/* 116 */             return;
/*     */           }
/*     */         }
/*     */         
/* 120 */         LimitedRateGroup[] new_upload_limiters = new LimitedRateGroup[this.upload_limiters.length + 1];
/*     */         
/* 122 */         System.arraycopy(this.upload_limiters, 0, new_upload_limiters, 0, this.upload_limiters.length);
/*     */         
/* 124 */         new_upload_limiters[this.upload_limiters.length] = limiter;
/*     */         
/* 126 */         this.upload_limiters = new_upload_limiters;
/*     */       }
/*     */       else {
/* 129 */         for (int i = 0; i < this.download_limiters.length; i++)
/*     */         {
/* 131 */           if (this.download_limiters[i] == limiter)
/*     */           {
/* 133 */             return;
/*     */           }
/*     */         }
/* 136 */         LimitedRateGroup[] new_download_limiters = new LimitedRateGroup[this.download_limiters.length + 1];
/*     */         
/* 138 */         System.arraycopy(this.download_limiters, 0, new_download_limiters, 0, this.download_limiters.length);
/*     */         
/* 140 */         new_download_limiters[this.download_limiters.length] = limiter;
/*     */         
/* 142 */         this.download_limiters = new_download_limiters;
/*     */       }
/*     */     }
/*     */     
/* 146 */     NetworkManager.getSingleton().addRateLimiter(this, limiter, upload);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRateLimiter(LimitedRateGroup limiter, boolean upload)
/*     */   {
/* 154 */     synchronized (this)
/*     */     {
/* 156 */       if (upload)
/*     */       {
/* 158 */         if (this.upload_limiters.length == 0)
/*     */         {
/* 160 */           return;
/*     */         }
/*     */         
/* 163 */         int pos = 0;
/*     */         
/* 165 */         LimitedRateGroup[] new_upload_limiters = new LimitedRateGroup[this.upload_limiters.length - 1];
/*     */         
/* 167 */         for (int i = 0; i < this.upload_limiters.length; i++)
/*     */         {
/* 169 */           if (this.upload_limiters[i] != limiter)
/*     */           {
/* 171 */             if (pos == new_upload_limiters.length)
/*     */             {
/* 173 */               return;
/*     */             }
/*     */             
/* 176 */             new_upload_limiters[(pos++)] = this.upload_limiters[i];
/*     */           }
/*     */         }
/*     */         
/* 180 */         this.upload_limiters = new_upload_limiters;
/*     */       }
/*     */       else
/*     */       {
/* 184 */         if (this.download_limiters.length == 0)
/*     */         {
/* 186 */           return;
/*     */         }
/*     */         
/* 189 */         int pos = 0;
/*     */         
/* 191 */         LimitedRateGroup[] new_download_limiters = new LimitedRateGroup[this.download_limiters.length - 1];
/*     */         
/* 193 */         for (int i = 0; i < this.download_limiters.length; i++)
/*     */         {
/* 195 */           if (this.download_limiters[i] != limiter)
/*     */           {
/* 197 */             if (pos == new_download_limiters.length)
/*     */             {
/* 199 */               return;
/*     */             }
/*     */             
/* 202 */             new_download_limiters[(pos++)] = this.download_limiters[i];
/*     */           }
/*     */         }
/*     */         
/* 206 */         this.download_limiters = new_download_limiters;
/*     */       }
/*     */     }
/*     */     
/* 210 */     NetworkManager.getSingleton().removeRateLimiter(this, limiter, upload);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LimitedRateGroup[] getRateLimiters(boolean upload)
/*     */   {
/* 217 */     if (upload)
/*     */     {
/* 219 */       return this.upload_limiters;
/*     */     }
/*     */     
/*     */ 
/* 223 */     return this.download_limiters;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/NetworkConnectionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */