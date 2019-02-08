/*     */ package com.aelitis.azureus.core.subs.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.metasearch.SearchLoginException;
/*     */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionScheduler;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class SubscriptionDownloader
/*     */ {
/*     */   private SubscriptionManagerImpl manager;
/*     */   private SubscriptionImpl subs;
/*     */   
/*     */   protected SubscriptionDownloader(SubscriptionManagerImpl _manager, SubscriptionImpl _subs)
/*     */     throws SubscriptionException
/*     */   {
/*  47 */     this.manager = _manager;
/*  48 */     this.subs = _subs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void download()
/*     */     throws SubscriptionException
/*     */   {
/*  56 */     log("Downloading");
/*     */     
/*  58 */     Map map = JSONUtils.decodeJSON(this.subs.getJSON());
/*     */     
/*  60 */     Long engine_id = (Long)map.get("engine_id");
/*  61 */     String search_term = (String)map.get("search_term");
/*  62 */     String networks = (String)map.get("networks");
/*  63 */     Map filters = (Map)map.get("filters");
/*     */     
/*  65 */     Engine engine = this.manager.getEngine(this.subs, map, false);
/*     */     
/*  67 */     if (engine == null)
/*     */     {
/*  69 */       throw new SubscriptionException("Download failed, search engine " + engine_id + " not found");
/*     */     }
/*     */     
/*  72 */     List sps = new ArrayList();
/*     */     
/*  74 */     if (search_term != null)
/*     */     {
/*  76 */       sps.add(new SearchParameter("s", search_term));
/*     */       
/*  78 */       log("    Using search term '" + search_term + "' for engine " + engine.getString());
/*     */     }
/*     */     
/*  81 */     if ((networks != null) && (networks.length() > 0))
/*     */     {
/*  83 */       sps.add(new SearchParameter("n", networks));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  93 */     SearchParameter[] parameters = (SearchParameter[])sps.toArray(new SearchParameter[sps.size()]);
/*     */     
/*     */ 
/*  96 */     SubscriptionHistoryImpl history = (SubscriptionHistoryImpl)this.subs.getHistory();
/*     */     try
/*     */     {
/*  99 */       Map context = new HashMap();
/*     */       
/* 101 */       context.put("azsrc", "subscription");
/*     */       
/* 103 */       Result[] results = engine.search(parameters, context, -1, -1, null, null);
/*     */       
/* 105 */       log("    Got " + results.length + " results");
/*     */       
/* 107 */       SubscriptionResultFilterImpl result_filter = new SubscriptionResultFilterImpl(this.subs, filters);
/*     */       
/* 109 */       results = result_filter.filter(results);
/*     */       
/* 111 */       log("    Post-filter: " + results.length + " results");
/*     */       
/* 113 */       SubscriptionResultImpl[] s_results = new SubscriptionResultImpl[results.length];
/*     */       
/* 115 */       for (int i = 0; i < results.length; i++)
/*     */       {
/* 117 */         SubscriptionResultImpl s_result = new SubscriptionResultImpl(history, results[i]);
/*     */         
/* 119 */         s_results[i] = s_result;
/*     */       }
/*     */       
/* 122 */       SubscriptionResultImpl[] all_results = history.reconcileResults(engine, s_results);
/*     */       
/* 124 */       checkAutoDownload(all_results);
/*     */       
/* 126 */       history.setLastError(null, false);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 130 */       log("    Download failed", e);
/*     */       
/* 132 */       history.setLastError(Debug.getNestedExceptionMessage(e), e instanceof SearchLoginException);
/*     */       
/* 134 */       throw new SubscriptionException("Search failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkAutoDownload(SubscriptionResultImpl[] results)
/*     */   {
/* 142 */     if (!this.subs.getHistory().isAutoDownload())
/*     */     {
/* 144 */       return;
/*     */     }
/*     */     
/* 147 */     for (int i = 0; i < results.length; i++)
/*     */     {
/* 149 */       SubscriptionResultImpl result = results[i];
/*     */       
/* 151 */       if ((!result.isDeleted()) && (!result.getRead()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 156 */         this.manager.getScheduler().download(this.subs, result);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 164 */     this.manager.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, Throwable e)
/*     */   {
/* 172 */     this.manager.log(str, e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */