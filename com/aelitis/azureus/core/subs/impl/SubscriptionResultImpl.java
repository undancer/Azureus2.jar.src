/*     */ package com.aelitis.azureus.core.subs.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class SubscriptionResultImpl
/*     */   implements SubscriptionResult
/*     */ {
/*     */   private static final long TIME_FOUND_DEFAULT;
/*     */   private final SubscriptionHistoryImpl history;
/*     */   private byte[] key1;
/*     */   private byte[] key2;
/*     */   private boolean read;
/*     */   private boolean deleted;
/*     */   private String result_json;
/*     */   
/*     */   static
/*     */   {
/*  45 */     long tfd = COConfigurationManager.getLongParameter("subscription.result.time.found.default", 0L);
/*     */     
/*  47 */     if (tfd == 0L)
/*     */     {
/*  49 */       tfd = SystemTime.getCurrentTime() / 1000L;
/*     */       
/*  51 */       COConfigurationManager.setParameter("subscription.result.time.found.default", tfd);
/*     */     }
/*     */     
/*  54 */     TIME_FOUND_DEFAULT = tfd * 1000L;
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
/*  66 */   private WeakReference<Map<Integer, Object>> props_ref = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SubscriptionResultImpl(SubscriptionHistoryImpl _history, Result result)
/*     */   {
/*  73 */     this.history = _history;
/*     */     
/*  75 */     Map map = result.toJSONMap();
/*     */     
/*  77 */     this.result_json = JSONUtils.encodeToJSON(map);
/*  78 */     this.read = false;
/*     */     
/*  80 */     String key1_str = result.getEngine().getId() + ":" + result.getName();
/*     */     try
/*     */     {
/*  83 */       byte[] sha1 = new SHA1Simple().calculateHash(key1_str.getBytes("UTF-8"));
/*     */       
/*  85 */       this.key1 = new byte[10];
/*     */       
/*  87 */       System.arraycopy(sha1, 0, this.key1, 0, 10);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  91 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*  94 */     String uid = result.getUID();
/*     */     
/*  96 */     if ((uid != null) && (uid.length() > 0))
/*     */     {
/*  98 */       String key2_str = result.getEngine().getId() + ":" + uid;
/*     */       try
/*     */       {
/* 101 */         byte[] sha1 = new SHA1Simple().calculateHash(key2_str.getBytes("UTF-8"));
/*     */         
/* 103 */         this.key2 = new byte[10];
/*     */         
/* 105 */         System.arraycopy(sha1, 0, this.key2, 0, 10);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 109 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SubscriptionResultImpl(SubscriptionHistoryImpl _history, Map map)
/*     */   {
/* 119 */     this.history = _history;
/*     */     
/* 121 */     this.key1 = ((byte[])map.get("key"));
/* 122 */     this.key2 = ((byte[])map.get("key2"));
/*     */     
/* 124 */     this.read = (((Long)map.get("read")).intValue() == 1);
/*     */     
/* 126 */     Long l_deleted = (Long)map.get("deleted");
/*     */     
/* 128 */     if (l_deleted != null)
/*     */     {
/* 130 */       this.deleted = true;
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 135 */         this.result_json = new String((byte[])map.get("result_json"), "UTF-8");
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 139 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean updateFrom(SubscriptionResultImpl other)
/*     */   {
/* 148 */     if (this.deleted)
/*     */     {
/* 150 */       return false;
/*     */     }
/*     */     
/* 153 */     String my_json_str = getJSON();
/* 154 */     String other_json_str = other.getJSON();
/*     */     
/* 156 */     if (my_json_str.equals(other_json_str))
/*     */     {
/* 158 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 163 */     Map my_json_map = JSONUtils.decodeJSON(my_json_str);
/*     */     
/* 165 */     String my_tf = (String)my_json_map.remove("tf");
/*     */     
/* 167 */     if (my_tf != null)
/*     */     {
/* 169 */       Map other_json_map = JSONUtils.decodeJSON(other_json_str);
/*     */       
/* 171 */       other_json_map.put("tf", my_tf);
/*     */       
/* 173 */       other_json_str = JSONUtils.encodeToJSON(other_json_map);
/*     */     }
/*     */     
/* 176 */     if (my_json_str.equals(other_json_str))
/*     */     {
/* 178 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 182 */     this.key2 = other.getKey2();
/* 183 */     this.result_json = other_json_str;
/*     */     
/* 185 */     synchronized (this)
/*     */     {
/* 187 */       this.props_ref = null;
/*     */     }
/*     */     
/* 190 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getID()
/*     */   {
/* 197 */     return Base32.encode(this.key1);
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getKey1()
/*     */   {
/* 203 */     return this.key1;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getKey2()
/*     */   {
/* 209 */     return this.key2;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getRead()
/*     */   {
/* 215 */     return this.read;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRead(boolean _read)
/*     */   {
/* 222 */     if (this.read != _read)
/*     */     {
/* 224 */       this.read = _read;
/*     */       
/* 226 */       this.history.updateResult(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setReadInternal(boolean _read)
/*     */   {
/* 234 */     this.read = _read;
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 240 */     if (!this.deleted)
/*     */     {
/* 242 */       this.deleted = true;
/*     */       
/* 244 */       this.history.updateResult(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void deleteInternal()
/*     */   {
/* 251 */     this.deleted = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 257 */     return this.deleted;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map toBEncodedMap()
/*     */   {
/* 263 */     Map map = new HashMap();
/*     */     
/* 265 */     map.put("key", this.key1);
/*     */     
/* 267 */     if (this.key2 != null) {
/* 268 */       map.put("key2", this.key2);
/*     */     }
/*     */     
/* 271 */     map.put("read", new Long(this.read ? 1L : 0L));
/*     */     
/* 273 */     if (this.deleted)
/*     */     {
/* 275 */       map.put("deleted", new Long(1L));
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 280 */         map.put("result_json", this.result_json.getBytes("UTF-8"));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 284 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 288 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map toJSONMap()
/*     */   {
/* 294 */     Map map = JSONUtils.decodeJSON(this.result_json);
/*     */     
/* 296 */     map.put("subs_is_read", Boolean.valueOf(this.read));
/* 297 */     map.put("subs_id", getID());
/*     */     
/* 299 */     Result.adjustRelativeTerms(map);
/*     */     
/*     */ 
/*     */ 
/* 303 */     String size = (String)map.get("l");
/*     */     
/* 305 */     if (size != null)
/*     */     {
/* 307 */       size = DisplayFormatters.trimDigits(size, 3);
/*     */       
/* 309 */       map.put("l", size);
/*     */     }
/*     */     
/* 312 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getJSON()
/*     */   {
/* 318 */     return this.result_json;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDownloadLink()
/*     */   {
/* 324 */     Map map = toJSONMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 330 */     String link = (String)map.get("dbl");
/*     */     
/* 332 */     if (link != null)
/*     */     {
/* 334 */       if (link.toLowerCase(Locale.US).startsWith("magnet:"))
/*     */       {
/* 336 */         String dl_link = (String)map.get("dl");
/*     */         
/* 338 */         if (dl_link != null)
/*     */         {
/* 340 */           link = dl_link;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 345 */     if (link == null)
/*     */     {
/* 347 */       link = (String)map.get("dl");
/*     */     }
/*     */     
/* 350 */     return Result.adjustLink(link);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPlayLink()
/*     */   {
/* 356 */     return Result.adjustLink((String)toJSONMap().get("pl"));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAssetHash()
/*     */   {
/* 362 */     return (String)toJSONMap().get("h");
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeFound()
/*     */   {
/* 368 */     String tf_secs_str = (String)toJSONMap().get("tf");
/*     */     
/* 370 */     if (tf_secs_str == null)
/*     */     {
/* 372 */       return TIME_FOUND_DEFAULT;
/*     */     }
/*     */     
/* 375 */     return Long.parseLong(tf_secs_str) * 1000L;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<Integer, Object> toPropertyMap()
/*     */   {
/* 381 */     synchronized (this)
/*     */     {
/* 383 */       if (this.props_ref != null)
/*     */       {
/* 385 */         Map<Integer, Object> cached = (Map)this.props_ref.get();
/*     */         
/* 387 */         if (cached != null)
/*     */         {
/* 389 */           return cached;
/*     */         }
/*     */       }
/*     */       
/* 393 */       Map map = toJSONMap();
/*     */       
/* 395 */       Map<Integer, Object> result = new HashMap();
/*     */       
/* 397 */       String title = (String)map.get("n");
/*     */       
/* 399 */       result.put(Integer.valueOf(20), getID());
/* 400 */       result.put(Integer.valueOf(1), title);
/*     */       
/* 402 */       String pub_date = (String)map.get("ts");
/* 403 */       if (pub_date != null) {
/* 404 */         result.put(Integer.valueOf(2), new Date(Long.parseLong(pub_date)));
/*     */       }
/*     */       
/* 407 */       String size = (String)map.get("lb");
/* 408 */       if (size != null) {
/* 409 */         result.put(Integer.valueOf(3), Long.valueOf(Long.parseLong(size)));
/*     */       }
/*     */       
/* 412 */       String dbl_link = (String)map.get("dbl");
/* 413 */       String dl_link = (String)map.get("dl");
/*     */       
/* 415 */       if (dbl_link == null)
/*     */       {
/* 417 */         dbl_link = dl_link;
/*     */       }
/*     */       
/* 420 */       if (dbl_link != null) {
/* 421 */         result.put(Integer.valueOf(12), Result.adjustLink(dbl_link));
/*     */       }
/* 423 */       if (dl_link != null) {
/* 424 */         result.put(Integer.valueOf(23), Result.adjustLink(dl_link));
/*     */       }
/*     */       
/* 427 */       String cdp_link = (String)map.get("cdp");
/*     */       
/* 429 */       if (cdp_link != null) {
/* 430 */         result.put(Integer.valueOf(11), Result.adjustLink(cdp_link));
/*     */       }
/*     */       
/* 433 */       String hash = (String)map.get("h");
/*     */       
/* 435 */       if (hash != null) {
/* 436 */         result.put(Integer.valueOf(21), Base32.decode(hash));
/*     */       }
/*     */       
/* 439 */       String seeds = (String)map.get("s");
/*     */       
/* 441 */       result.put(Integer.valueOf(5), Long.valueOf(seeds == null ? -1L : Long.parseLong(seeds)));
/*     */       
/* 443 */       String peers = (String)map.get("p");
/*     */       
/* 445 */       result.put(Integer.valueOf(4), Long.valueOf(peers == null ? -1L : Long.parseLong(peers)));
/*     */       
/*     */ 
/* 448 */       String votes = (String)map.get("v");
/*     */       
/* 450 */       result.put(Integer.valueOf(9), Long.valueOf(votes == null ? -1L : Long.parseLong(votes)));
/*     */       
/* 452 */       String comments = (String)map.get("co");
/*     */       
/* 454 */       result.put(Integer.valueOf(8), Long.valueOf(comments == null ? -1L : Long.parseLong(comments)));
/*     */       
/* 456 */       String rank = (String)map.get("r");
/*     */       
/* 458 */       result.put(Integer.valueOf(17), Long.valueOf(rank == null ? -1L : (100.0F * Float.parseFloat(rank))));
/*     */       
/* 460 */       String category = (String)map.get("c");
/*     */       
/* 462 */       if (category != null)
/*     */       {
/* 464 */         result.put(Integer.valueOf(7), category);
/*     */       }
/*     */       
/* 467 */       String contentType = (String)map.get("ct");
/*     */       
/* 469 */       if (contentType != null) {
/* 470 */         result.put(Integer.valueOf(10), contentType);
/*     */       }
/*     */       
/* 473 */       this.props_ref = new WeakReference(result);
/*     */       
/* 475 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionResultImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */