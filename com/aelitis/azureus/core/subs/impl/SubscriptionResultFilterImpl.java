/*     */ package com.aelitis.azureus.core.subs.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionResultFilter;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.json.simple.JSONObject;
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
/*     */ public class SubscriptionResultFilterImpl
/*     */   implements SubscriptionResultFilter
/*     */ {
/*     */   private final SubscriptionImpl subs;
/*     */   private String[] textFilters;
/*     */   private Pattern[] textFilterPatterns;
/*     */   private String[] excludeTextFilters;
/*     */   private Pattern[] excludeTextFilterPatterns;
/*     */   private String regexFilter;
/*  50 */   private long minSeeds = -1L;
/*  51 */   private long minSize = -1L;
/*  52 */   private long maxSize = -1L;
/*  53 */   private String categoryFilter = null;
/*     */   
/*     */   public SubscriptionResultFilterImpl(SubscriptionImpl _subs, Map filters) {
/*  56 */     this.subs = _subs;
/*     */     try
/*     */     {
/*  59 */       this.textFilters = importStrings(filters, "text_filter", " ");
/*     */       
/*  61 */       this.textFilterPatterns = getPatterns(this.textFilters);
/*     */       
/*  63 */       this.excludeTextFilters = importStrings(filters, "text_filter_out", " ");
/*     */       
/*  65 */       this.excludeTextFilterPatterns = getPatterns(this.excludeTextFilters);
/*     */       
/*     */ 
/*  68 */       this.regexFilter = ImportExportUtils.importString(filters, "text_filter_regex");
/*     */       
/*  70 */       this.minSize = ImportExportUtils.importLong(filters, "min_size", -1L);
/*     */       
/*  72 */       this.maxSize = ImportExportUtils.importLong(filters, "max_size", -1L);
/*     */       
/*  74 */       this.minSeeds = ImportExportUtils.importLong(filters, "min_seeds", -1L);
/*     */       
/*  76 */       String rawCategory = ImportExportUtils.importString(filters, "category");
/*  77 */       if (rawCategory != null) {
/*  78 */         this.categoryFilter = rawCategory.toLowerCase();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getMinSze()
/*     */   {
/*  90 */     return this.minSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getMaxSize()
/*     */   {
/*  96 */     return this.maxSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getWithWords()
/*     */   {
/* 102 */     return this.textFilters;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getWithoutWords()
/*     */   {
/* 108 */     return this.excludeTextFilters;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(String[] with_words, String[] without_words, long min_size, long max_size)
/*     */     throws SubscriptionException
/*     */   {
/* 120 */     Map map = JSONUtils.decodeJSON(this.subs.getJSON());
/*     */     
/* 122 */     Map filters = new JSONObject();
/*     */     
/* 124 */     map.put("filters", filters);
/*     */     
/* 126 */     exportStrings(filters, "text_filter", with_words);
/* 127 */     exportStrings(filters, "text_filter_out", without_words);
/*     */     
/* 129 */     if (min_size <= 0L) {
/* 130 */       min_size = -1L;
/*     */     }
/*     */     
/* 133 */     if (max_size <= 0L) {
/* 134 */       max_size = -1L;
/*     */     }
/*     */     
/* 137 */     filters.put("min_size", Long.valueOf(min_size));
/* 138 */     filters.put("max_size", Long.valueOf(max_size));
/*     */     
/* 140 */     this.subs.setDetails(this.subs.getName(false), this.subs.isPublic(), map.toString());
/*     */     
/* 142 */     this.textFilters = with_words;
/*     */     
/* 144 */     this.textFilterPatterns = getPatterns(this.textFilters);
/*     */     
/* 146 */     this.excludeTextFilters = without_words;
/*     */     
/* 148 */     this.excludeTextFilterPatterns = getPatterns(this.excludeTextFilters);
/*     */     
/* 150 */     this.minSize = min_size;
/* 151 */     this.maxSize = max_size;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 157 */     String res = addString("", "+", getString(this.textFilters));
/*     */     
/* 159 */     res = addString(res, "-", getString(this.excludeTextFilters));
/*     */     
/* 161 */     res = addString(res, "regex=", this.regexFilter);
/*     */     
/* 163 */     res = addString(res, "cat=", this.categoryFilter);
/*     */     
/* 165 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String addString(String existing, String key, String rest)
/*     */   {
/* 174 */     if ((rest == null) || (rest.length() == 0))
/*     */     {
/* 176 */       return existing;
/*     */     }
/*     */     
/* 179 */     String str = key + rest;
/*     */     
/* 181 */     if ((existing == null) || (existing.length() == 0))
/*     */     {
/* 183 */       return str;
/*     */     }
/*     */     
/* 186 */     return existing + "," + str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getString(String[] strs)
/*     */   {
/* 193 */     String res = "";
/*     */     
/* 195 */     for (int i = 0; i < strs.length; i++) {
/* 196 */       res = res + (i == 0 ? "" : "&") + strs[i];
/*     */     }
/*     */     
/* 199 */     return res;
/*     */   }
/*     */   
/*     */ 
/* 203 */   private static Pattern[] NO_PATTERNS = new Pattern[0];
/*     */   
/*     */ 
/*     */ 
/*     */   private Pattern[] getPatterns(String[] strs)
/*     */   {
/* 209 */     if (strs.length == 0)
/*     */     {
/* 211 */       return NO_PATTERNS;
/*     */     }
/*     */     
/* 214 */     Pattern[] pats = new Pattern[strs.length];
/*     */     
/* 216 */     for (int i = 0; i < strs.length; i++) {
/*     */       try
/*     */       {
/* 219 */         pats[i] = Pattern.compile(strs[i].trim());
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 223 */         System.out.println("Failed to compile pattern '" + strs[i]);
/*     */       }
/*     */     }
/*     */     
/* 227 */     return pats;
/*     */   }
/*     */   
/*     */   private String[] importStrings(Map filters, String key, String separator) throws IOException {
/* 231 */     String rawStringFilter = ImportExportUtils.importString(filters, key);
/* 232 */     if (rawStringFilter != null) {
/* 233 */       StringTokenizer st = new StringTokenizer(rawStringFilter, separator);
/* 234 */       String[] stringFilter = new String[st.countTokens()];
/* 235 */       for (int i = 0; i < stringFilter.length; i++) {
/* 236 */         stringFilter[i] = st.nextToken().toLowerCase();
/*     */       }
/* 238 */       return stringFilter;
/*     */     }
/* 240 */     return new String[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void exportStrings(Map map, String key, String[] values)
/*     */   {
/* 249 */     if ((values == null) || (values.length == 0))
/*     */     {
/* 251 */       return;
/*     */     }
/*     */     
/* 254 */     String encoded = "";
/*     */     
/* 256 */     for (String value : values)
/*     */     {
/* 258 */       encoded = encoded + (encoded == "" ? "" : " ") + value;
/*     */     }
/*     */     
/* 261 */     map.put(key, encoded);
/*     */   }
/*     */   
/*     */   public Result[] filter(Result[] results) {
/* 265 */     List<Result> filteredResults = new ArrayList(results.length);
/* 266 */     for (int i = 0; i < results.length; i++) {
/* 267 */       Result result = results[i];
/*     */       
/* 269 */       String name = result.getName();
/*     */       
/* 271 */       if (name != null)
/*     */       {
/*     */ 
/* 274 */         name = name.toLowerCase();
/*     */         
/* 276 */         boolean valid = true;
/* 277 */         for (int j = 0; j < this.textFilters.length; j++)
/*     */         {
/*     */ 
/*     */ 
/* 281 */           if (!name.contains(this.textFilters[j]))
/*     */           {
/*     */ 
/*     */ 
/* 285 */             Pattern p = this.textFilterPatterns[j];
/*     */             
/* 287 */             if ((p == null) || (!p.matcher(name).find()))
/*     */             {
/* 289 */               valid = false;
/*     */               
/* 291 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 297 */         if (valid)
/*     */         {
/*     */ 
/*     */ 
/* 301 */           for (int j = 0; j < this.excludeTextFilters.length; j++)
/*     */           {
/*     */ 
/*     */ 
/* 305 */             if (name.contains(this.excludeTextFilters[j])) {
/* 306 */               valid = false;
/* 307 */               break;
/*     */             }
/* 309 */             Pattern p = this.excludeTextFilterPatterns[j];
/*     */             
/* 311 */             if ((p != null) && (p.matcher(name).find())) {
/* 312 */               valid = false;
/* 313 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 319 */           if (valid)
/*     */           {
/*     */ 
/*     */ 
/* 323 */             long size = result.getSize();
/*     */             
/* 325 */             if ((this.minSize <= -1L) || 
/* 326 */               (this.minSize <= size))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 331 */               if ((this.maxSize <= -1L) || 
/* 332 */                 (this.maxSize >= size))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 337 */                 if ((this.minSeeds <= -1L) || 
/* 338 */                   (this.minSeeds >= result.getNbSeeds()))
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 343 */                   if (this.categoryFilter != null) {
/* 344 */                     String category = result.getCategory();
/* 345 */                     if ((category == null) || (!category.equalsIgnoreCase(this.categoryFilter))) {}
/*     */ 
/*     */ 
/*     */                   }
/*     */                   else
/*     */                   {
/*     */ 
/* 352 */                     filteredResults.add(result);
/*     */                   } } } }
/*     */           }
/*     */         } } }
/* 356 */     Result[] fResults = (Result[])filteredResults.toArray(new Result[filteredResults.size()]);
/*     */     
/* 358 */     return fResults;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionResultFilterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */