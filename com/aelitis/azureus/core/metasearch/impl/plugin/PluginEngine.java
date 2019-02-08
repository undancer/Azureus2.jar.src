/*     */ package com.aelitis.azureus.core.metasearch.impl.plugin;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*     */ import com.aelitis.azureus.core.metasearch.SearchException;
/*     */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*     */ import com.aelitis.azureus.core.metasearch.impl.EngineImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.MetaSearchImpl;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.search.SearchInstance;
/*     */ import org.gudy.azureus2.plugins.utils.search.SearchObserver;
/*     */ import org.gudy.azureus2.plugins.utils.search.SearchProvider;
/*     */ import org.gudy.azureus2.plugins.utils.search.SearchResult;
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
/*     */ public class PluginEngine
/*     */   extends EngineImpl
/*     */ {
/*  40 */   private static int[][] FIELD_MAP = { { 7, 6 }, { 8, 7 }, { 10, 8 }, { 11, 103 }, { 16, 105 }, { 12, 102 }, { 15, 13 }, { 4, 4 }, { 1, 1 }, { 13, 104 }, { 14, 12 }, { 2, 2 }, { 5, 5 }, { 3, 3 }, { 6, 11 }, { 9, 10 }, { 23, 102 }, { 21, 200 } };
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
/*     */   private SearchProvider provider;
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
/*     */   public static EngineImpl importFromBEncodedMap(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/*  70 */     return new PluginEngine(meta_search, map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PluginEngine(MetaSearchImpl _meta_search, long _id, SearchProvider _provider)
/*     */   {
/*  81 */     super(_meta_search, 3, _id, 0L, 1.0F, (String)_provider.getProperty(1));
/*     */     
/*  83 */     this.provider = _provider;
/*     */     
/*  85 */     setSource(2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PluginEngine(MetaSearchImpl _meta_search, Map _map)
/*     */     throws IOException
/*     */   {
/*  95 */     super(_meta_search, _map);
/*     */     
/*     */ 
/*     */ 
/*  99 */     if (getRankBias() == 0.0F)
/*     */     {
/* 101 */       setRankBias(1.0F);
/*     */     }
/*     */     
/* 104 */     setSource(2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap()
/*     */     throws IOException
/*     */   {
/* 112 */     return exportToBencodedMap(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap(boolean generic)
/*     */     throws IOException
/*     */   {
/* 121 */     Map res = new HashMap();
/*     */     
/* 123 */     super.exportToBencodedMap(res, generic);
/*     */     
/* 125 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setProvider(SearchProvider _provider)
/*     */   {
/* 132 */     this.provider = _provider;
/*     */   }
/*     */   
/*     */ 
/*     */   public SearchProvider getProvider()
/*     */   {
/* 138 */     return this.provider;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean useAccuracyForRank()
/*     */   {
/* 144 */     if (this.provider == null)
/*     */     {
/* 146 */       return false;
/*     */     }
/*     */     
/* 149 */     Boolean val = (Boolean)this.provider.getProperty(6);
/*     */     
/* 151 */     if (val == null)
/*     */     {
/* 153 */       return false;
/*     */     }
/*     */     
/* 156 */     return val.booleanValue();
/*     */   }
/*     */   
/*     */   public boolean isActive()
/*     */   {
/* 161 */     return (this.provider != null) && (super.isActive());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNameEx()
/*     */   {
/* 167 */     return super.getName() + ": (plugin)";
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDownloadLinkCSS()
/*     */   {
/* 173 */     if (this.provider == null)
/*     */     {
/* 175 */       return null;
/*     */     }
/*     */     
/* 178 */     return (String)this.provider.getProperty(3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean supportsField(int field)
/*     */   {
/* 185 */     if (this.provider == null)
/*     */     {
/* 187 */       return false;
/*     */     }
/*     */     
/* 190 */     int[] supports = (int[])this.provider.getProperty(5);
/*     */     
/* 192 */     if (supports == null)
/*     */     {
/* 194 */       return true;
/*     */     }
/*     */     
/* 197 */     for (int i = 0; i < FIELD_MAP.length; i++)
/*     */     {
/* 199 */       int[] entry = FIELD_MAP[i];
/*     */       
/* 201 */       if (entry[1] == field)
/*     */       {
/* 203 */         for (int j = 0; j < supports.length; j++)
/*     */         {
/* 205 */           if (supports[j] == entry[0])
/*     */           {
/* 207 */             return true;
/*     */           }
/*     */         }
/*     */         
/* 211 */         break;
/*     */       }
/*     */     }
/*     */     
/* 215 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean supportsContext(String context_key)
/*     */   {
/* 222 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isShareable()
/*     */   {
/* 228 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAnonymous()
/*     */   {
/* 235 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getIcon()
/*     */   {
/* 241 */     if (this.provider == null)
/*     */     {
/* 243 */       return null;
/*     */     }
/*     */     
/* 246 */     return (String)this.provider.getProperty(2);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getReferer()
/*     */   {
/* 252 */     if (this.provider == null)
/*     */     {
/* 254 */       return null;
/*     */     }
/*     */     
/* 257 */     return (String)this.provider.getProperty(4);
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
/*     */   protected Result[] searchSupport(SearchParameter[] params, Map searchContext, final int desired_max_matches, final int absolute_max_matches, String headers, final ResultListener listener)
/*     */     throws SearchException
/*     */   {
/* 271 */     if (this.provider == null)
/*     */     {
/* 273 */       this.provider = getMetaSearch().resolveProvider(this);
/*     */       
/* 275 */       if (this.provider == null)
/*     */       {
/* 277 */         return new Result[0];
/*     */       }
/*     */     }
/*     */     
/* 281 */     Map search_parameters = new HashMap();
/*     */     
/* 283 */     String term = null;
/*     */     
/* 285 */     for (int i = 0; i < params.length; i++)
/*     */     {
/* 287 */       SearchParameter param = params[i];
/*     */       
/* 289 */       String pattern = param.getMatchPattern();
/* 290 */       String value = param.getValue();
/*     */       
/* 292 */       if (pattern.equals("s"))
/*     */       {
/* 294 */         term = value;
/*     */         
/* 296 */         search_parameters.put("s", value);
/*     */       }
/* 298 */       else if (pattern.equals("m"))
/*     */       {
/* 300 */         search_parameters.put("m", Boolean.valueOf(value));
/*     */       }
/* 302 */       else if (pattern.equals("n"))
/*     */       {
/* 304 */         String[] networks = value.split(",");
/*     */         
/* 306 */         search_parameters.put("n", networks);
/*     */       }
/*     */       else
/*     */       {
/* 310 */         Debug.out("Unrecognised search parameter '" + pattern + "=" + value + "' ignored");
/*     */       }
/*     */     }
/*     */     
/* 314 */     final String f_term = term;
/*     */     try
/*     */     {
/* 317 */       final List<PluginResult> results = new ArrayList();
/*     */       
/* 319 */       final AESemaphore sem = new AESemaphore("waiter");
/*     */       
/* 321 */       this.provider.search(search_parameters, new SearchObserver()
/*     */       {
/*     */ 
/*     */ 
/* 325 */         private boolean complete = false;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void resultReceived(SearchInstance search, SearchResult result)
/*     */         {
/* 332 */           PluginResult p_result = new PluginResult(PluginEngine.this, result, f_term);
/*     */           
/* 334 */           synchronized (this)
/*     */           {
/* 336 */             if (this.complete)
/*     */             {
/* 338 */               return;
/*     */             }
/*     */             
/* 341 */             results.add(p_result);
/*     */           }
/*     */           
/* 344 */           if (listener != null)
/*     */           {
/* 346 */             listener.resultsReceived(PluginEngine.this, new Result[] { p_result });
/*     */           }
/*     */           
/* 349 */           synchronized (this)
/*     */           {
/* 351 */             if ((absolute_max_matches >= 0) && (results.size() >= absolute_max_matches))
/*     */             {
/* 353 */               this.complete = true;
/*     */               
/* 355 */               sem.release();
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */         public void cancelled()
/*     */         {
/* 363 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */         public void complete()
/*     */         {
/* 369 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public Object getProperty(int property)
/*     */         {
/* 376 */           if (property == 1)
/*     */           {
/* 378 */             return new Long(desired_max_matches);
/*     */           }
/*     */           
/* 381 */           return null;
/*     */         }
/*     */         
/* 384 */       });
/* 385 */       sem.reserve();
/*     */       
/* 387 */       if (listener != null)
/*     */       {
/* 389 */         listener.resultsComplete(this);
/*     */       }
/*     */       
/* 392 */       return (Result[])results.toArray(new Result[results.size()]);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 396 */       throw new SearchException("Search failed", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/plugin/PluginEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */