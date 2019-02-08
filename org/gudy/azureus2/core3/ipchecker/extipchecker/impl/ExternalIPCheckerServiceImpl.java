/*     */ package org.gudy.azureus2.core3.ipchecker.extipchecker.impl;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.Vector;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerServiceListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public abstract class ExternalIPCheckerServiceImpl
/*     */   implements ExternalIPCheckerService, Cloneable
/*     */ {
/*     */   private static final int MAX_PAGE_SIZE = 4096;
/*     */   private static final String MSG_KEY_ROOT = "IPChecker.external";
/*     */   private final String name;
/*     */   private final String description;
/*     */   private final String url;
/*     */   private boolean completed;
/*  51 */   private final Vector listeners = new Vector();
/*  52 */   private final AEMonitor this_mon = new AEMonitor("ExtIPCheckServ");
/*     */   
/*  54 */   final AESemaphore timeout_sem = new AESemaphore("ExtIPCheckServ");
/*     */   
/*     */ 
/*     */ 
/*     */   protected ExternalIPCheckerServiceImpl(String name_key)
/*     */   {
/*  60 */     this.name = MessageText.getString(name_key + ".name");
/*  61 */     this.description = MessageText.getString(name_key + ".description");
/*  62 */     this.url = MessageText.getString(name_key + ".url");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initiateCheck(long timeout)
/*     */   {
/*  69 */     _clone().initiateCheckSupport(timeout);
/*     */   }
/*     */   
/*     */   protected ExternalIPCheckerServiceImpl _clone()
/*     */   {
/*     */     try
/*     */     {
/*  76 */       return (ExternalIPCheckerServiceImpl)clone();
/*     */     }
/*     */     catch (CloneNotSupportedException e)
/*     */     {
/*  80 */       Debug.printStackTrace(e);
/*     */     }
/*  82 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void initiateCheckSupport(final long timeout)
/*     */   {
/*  91 */     Thread t = new AEThread("IPChecker")
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*  99 */           ExternalIPCheckerServiceImpl.this.initiateCheckSupport();
/*     */         }
/*     */         finally
/*     */         {
/* 103 */           ExternalIPCheckerServiceImpl.this.setComplete();
/*     */         }
/*     */         
/*     */       }
/* 107 */     };
/* 108 */     t.setDaemon(true);
/*     */     
/* 110 */     t.start();
/*     */     
/* 112 */     if (timeout > 0L)
/*     */     {
/* 114 */       Thread t2 = new AEThread("IPChecker2")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/* 122 */             if (!ExternalIPCheckerServiceImpl.this.timeout_sem.reserve(timeout))
/*     */             {
/* 124 */               if (!ExternalIPCheckerServiceImpl.this.completed)
/*     */               {
/* 126 */                 ExternalIPCheckerServiceImpl.this.informFailure("timeout");
/*     */                 
/* 128 */                 ExternalIPCheckerServiceImpl.this.setComplete();
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 133 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/*     */         }
/* 137 */       };
/* 138 */       t2.setDaemon(true);
/*     */       
/* 140 */       t2.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void initiateCheckSupport();
/*     */   
/*     */ 
/*     */   protected void setComplete()
/*     */   {
/* 151 */     this.completed = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String loadPage(String url_string)
/*     */   {
/*     */     try
/*     */     {
/* 160 */       URL url = new URL(url_string);
/*     */       
/* 162 */       HttpURLConnection connection = null;
/* 163 */       InputStream is = null;
/*     */       try
/*     */       {
/* 166 */         connection = (HttpURLConnection)url.openConnection();
/*     */         
/* 168 */         int response = connection.getResponseCode();
/*     */         String page;
/* 170 */         if ((response == 202) || (response == 200))
/*     */         {
/* 172 */           is = connection.getInputStream();
/*     */           
/* 174 */           page = "";
/*     */           byte[] buffer;
/* 176 */           while (page.length() < 4096)
/*     */           {
/* 178 */             buffer = new byte['ࠀ'];
/*     */             
/* 180 */             int len = is.read(buffer);
/*     */             
/* 182 */             if (len < 0) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 187 */             page = page + new String(buffer, 0, len);
/*     */           }
/*     */           
/* 190 */           return page;
/*     */         }
/*     */         
/*     */ 
/* 194 */         informFailure("httpinvalidresponse", "" + response);
/*     */         
/* 196 */         return null;
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 202 */           if (is != null)
/*     */           {
/* 204 */             is.close();
/*     */           }
/*     */           
/* 207 */           if (connection != null)
/*     */           {
/* 209 */             connection.disconnect();
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 213 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 220 */       return null;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 218 */       informFailure("httploadfail", e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String extractIPAddress(String str)
/*     */   {
/* 228 */     int pos = 0;
/*     */     
/* 230 */     while (pos < str.length())
/*     */     {
/* 232 */       int p1 = str.indexOf('.', pos);
/*     */       
/* 234 */       if (p1 == -1)
/*     */       {
/* 236 */         informFailure("ipnotfound");
/*     */         
/* 238 */         return null;
/*     */       }
/*     */       
/* 241 */       if (p1 > 0)
/*     */       {
/* 243 */         if (Character.isDigit(str.charAt(p1 - 1)))
/*     */         {
/* 245 */           int p2 = p1 - 1;
/*     */           
/* 247 */           while ((p2 >= 0) && (Character.isDigit(str.charAt(p2))))
/*     */           {
/* 249 */             p2--;
/*     */           }
/*     */           
/* 252 */           p2++;
/*     */           
/* 254 */           int p3 = p2 + 1;
/*     */           
/* 256 */           int dots = 0;
/*     */           
/* 258 */           while (p3 < str.length())
/*     */           {
/* 260 */             char c = str.charAt(p3);
/*     */             
/* 262 */             if (c == '.')
/*     */             {
/* 264 */               dots++;
/*     */             } else {
/* 266 */               if (!Character.isDigit(c)) {
/*     */                 break;
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 273 */             p3++;
/*     */           }
/*     */           
/* 276 */           if (dots == 3)
/*     */           {
/* 278 */             return str.substring(p2, p3);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 283 */       pos = p1 + 1;
/*     */     }
/*     */     
/* 286 */     informFailure("ipnotfound");
/*     */     
/* 288 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 294 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 300 */     return this.description;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 306 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void informSuccess(String ip)
/*     */   {
/*     */     try
/*     */     {
/* 314 */       this.this_mon.enter();
/*     */       
/* 316 */       if (!this.completed)
/*     */       {
/* 318 */         this.timeout_sem.releaseForever();
/*     */         
/* 320 */         for (int i = 0; i < this.listeners.size(); i++)
/*     */         {
/* 322 */           ((ExternalIPCheckerServiceListener)this.listeners.elementAt(i)).checkComplete(this, ip);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 327 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void informFailure(String msg_key)
/*     */   {
/*     */     try
/*     */     {
/* 336 */       this.this_mon.enter();
/*     */       
/* 338 */       informFailure(msg_key, null);
/*     */     }
/*     */     finally
/*     */     {
/* 342 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void informFailure(String msg_key, String extra)
/*     */   {
/*     */     try
/*     */     {
/* 352 */       this.this_mon.enter();
/*     */       
/* 354 */       if (!this.completed)
/*     */       {
/* 356 */         this.timeout_sem.releaseForever();
/*     */         
/* 358 */         String message = MessageText.getString("IPChecker.external." + msg_key);
/*     */         
/* 360 */         if (extra != null)
/*     */         {
/* 362 */           message = message + ": " + extra;
/*     */         }
/*     */         
/* 365 */         for (int i = 0; i < this.listeners.size(); i++)
/*     */         {
/* 367 */           ((ExternalIPCheckerServiceListener)this.listeners.elementAt(i)).checkFailed(this, message);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 372 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void reportProgress(String msg_key)
/*     */   {
/*     */     try
/*     */     {
/* 381 */       this.this_mon.enter();
/*     */       
/* 383 */       reportProgress(msg_key, null);
/*     */     }
/*     */     finally
/*     */     {
/* 387 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reportProgress(String msg_key, String extra)
/*     */   {
/*     */     try
/*     */     {
/* 397 */       this.this_mon.enter();
/*     */       
/* 399 */       if (!this.completed)
/*     */       {
/* 401 */         String message = MessageText.getString("IPChecker.external".concat(".").concat(msg_key));
/*     */         
/* 403 */         if (extra != null)
/*     */         {
/* 405 */           message = message.concat(": ").concat(extra);
/*     */         }
/* 407 */         for (int i = 0; i < this.listeners.size(); i++)
/*     */         {
/* 409 */           ((ExternalIPCheckerServiceListener)this.listeners.elementAt(i)).reportProgress(this, message);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 414 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(ExternalIPCheckerServiceListener l)
/*     */   {
/*     */     try
/*     */     {
/* 423 */       this.this_mon.enter();
/*     */       
/* 425 */       this.listeners.addElement(l);
/*     */     }
/*     */     finally
/*     */     {
/* 429 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(ExternalIPCheckerServiceListener l)
/*     */   {
/*     */     try
/*     */     {
/* 438 */       this.this_mon.enter();
/*     */       
/* 440 */       this.listeners.removeElement(l);
/*     */     }
/*     */     finally
/*     */     {
/* 444 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/impl/ExternalIPCheckerServiceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */