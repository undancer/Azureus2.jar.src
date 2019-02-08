/*     */ package com.aelitis.azureus.core.metasearch.impl.web.json;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*     */ import com.aelitis.azureus.core.metasearch.SearchException;
/*     */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*     */ import com.aelitis.azureus.core.metasearch.impl.EngineImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.MetaSearchImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.FieldMapping;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine.pageDetails;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebResult;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.JSONValue;
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
/*     */ public class JSONEngine
/*     */   extends WebEngine
/*     */ {
/*     */   private static final String variablePattern = "\\$\\{[^}]+\\}";
/*  47 */   private static final Pattern patternVariable = Pattern.compile("\\$\\{[^}]+\\}");
/*     */   
/*     */   private static final boolean DEBUG_MAPPINGS = false;
/*     */   
/*     */   private String resultsEntryPath;
/*     */   private String rankDivisorPath;
/*     */   
/*     */   public static EngineImpl importFromBEncodedMap(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/*  57 */     return new JSONEngine(meta_search, map);
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
/*     */   public static Engine importFromJSONString(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*     */     throws IOException
/*     */   {
/*  71 */     return new JSONEngine(meta_search, id, last_updated, rank_bias, name, map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private float rankDivisor = 1.0F;
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
/*     */   public JSONEngine(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, String searchURLFormat, String timeZone, boolean automaticDateFormat, String userDateFormat, String resultsEntryPath, FieldMapping[] mappings, boolean needs_auth, String auth_method, String login_url, String[] required_cookies)
/*     */   {
/* 100 */     super(meta_search, 2, id, last_updated, rank_bias, name, searchURLFormat, timeZone, automaticDateFormat, userDateFormat, mappings, needs_auth, auth_method, login_url, required_cookies);
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
/* 116 */     this.resultsEntryPath = resultsEntryPath;
/*     */     
/* 118 */     setSource(2);
/*     */     
/* 120 */     setSelectionState(2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JSONEngine(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/* 132 */     super(meta_search, map);
/*     */     
/* 134 */     this.resultsEntryPath = ImportExportUtils.importString(map, "json.path");
/* 135 */     this.rankDivisorPath = ImportExportUtils.importString(map, "rank.divisor.path");
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
/*     */   protected JSONEngine(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*     */     throws IOException
/*     */   {
/* 151 */     super(meta_search, 2, id, last_updated, rank_bias, name, map);
/*     */     
/* 153 */     this.resultsEntryPath = ImportExportUtils.importString(map, "json_result_key");
/* 154 */     this.resultsEntryPath = UrlUtils.decode(this.resultsEntryPath);
/* 155 */     this.rankDivisorPath = ImportExportUtils.importString(map, "rank_divisor_key");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap()
/*     */     throws IOException
/*     */   {
/* 163 */     return exportToBencodedMap(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap(boolean generic)
/*     */     throws IOException
/*     */   {
/* 172 */     Map res = new HashMap();
/*     */     
/* 174 */     ImportExportUtils.exportString(res, "json.path", this.resultsEntryPath);
/*     */     
/* 176 */     ImportExportUtils.exportString(res, "rank.divisor.path", this.rankDivisorPath);
/*     */     
/* 178 */     super.exportToBencodedMap(res, generic);
/*     */     
/* 180 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exportToJSONObject(JSONObject res)
/*     */     throws IOException
/*     */   {
/* 189 */     res.put("json_result_key", this.resultsEntryPath);
/*     */     
/* 191 */     res.put("rank_divisor_key", this.rankDivisorPath);
/*     */     
/* 193 */     super.exportToJSONObject(res);
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
/*     */   protected Result[] searchSupport(SearchParameter[] searchParameters, Map searchContext, int desired_max_matches, int absolute_max_matches, String headers, ResultListener listener)
/*     */     throws SearchException
/*     */   {
/* 207 */     debugStart();
/*     */     
/* 209 */     WebEngine.pageDetails page_details = super.getWebPageContent(searchParameters, searchContext, headers, false);
/*     */     
/* 211 */     String page = page_details.getContent();
/*     */     
/* 213 */     if (listener != null) {
/* 214 */       listener.contentReceived(this, page);
/*     */     }
/*     */     
/*     */ 
/* 218 */     String searchQuery = null;
/*     */     
/* 220 */     for (int i = 0; i < searchParameters.length; i++) {
/* 221 */       if (searchParameters[i].getMatchPattern().equals("s")) {
/* 222 */         searchQuery = searchParameters[i].getValue();
/*     */       }
/*     */     }
/*     */     
/* 226 */     FieldMapping[] mappings = getMappings();
/*     */     try
/*     */     {
/*     */       Object jsonObject;
/*     */       try
/*     */       {
/* 232 */         jsonObject = JSONValue.parse(page);
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/* 238 */         String temp_page = page.replaceAll("\\\\\",", "\",");
/*     */         try
/*     */         {
/* 241 */           jsonObject = JSONValue.parse(temp_page);
/*     */         }
/*     */         catch (Throwable f)
/*     */         {
/* 245 */           throw e;
/*     */         }
/*     */       }
/*     */       
/* 249 */       if (this.rankDivisorPath != null) {
/* 250 */         String[] split = this.rankDivisorPath.split("\\.");
/*     */         try {
/* 252 */           if (split.length > 0) {
/* 253 */             Object jsonRankDivisor = jsonObject;
/* 254 */             for (int i = 0; i < split.length - 1; i++) {
/* 255 */               String key = split[i];
/* 256 */               if (!(jsonRankDivisor instanceof JSONObject))
/*     */                 break;
/* 258 */               jsonRankDivisor = ((JSONObject)jsonRankDivisor).get(key);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 265 */             if ((jsonRankDivisor instanceof Map)) {
/* 266 */               jsonRankDivisor = ((Map)jsonRankDivisor).get(split[(split.length - 1)]);
/*     */             }
/*     */             
/* 269 */             if ((jsonRankDivisor instanceof Number)) {
/* 270 */               this.rankDivisor = ((Number)jsonRankDivisor).floatValue();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */       
/* 277 */       JSONArray resultArray = null;
/*     */       
/* 279 */       if ((this.resultsEntryPath != null) && (this.resultsEntryPath.length() > 0)) {
/* 280 */         String[] split = this.resultsEntryPath.split("\\.");
/* 281 */         if (((jsonObject instanceof JSONArray)) && (split.length > 0) && (!split[0].startsWith("["))) {
/* 282 */           JSONArray array = (JSONArray)jsonObject;
/* 283 */           if (array.size() == 1) {
/* 284 */             jsonObject = array.get(0);
/*     */           }
/*     */         }
/* 287 */         for (String pathEntry : split) {
/* 288 */           if (jsonObject == null) {
/* 289 */             throw new SearchException("Invalid entry path : " + this.resultsEntryPath);
/*     */           }
/*     */           try
/*     */           {
/* 293 */             if ((pathEntry.startsWith("[")) && (pathEntry.endsWith("]"))) {
/* 294 */               int idx = Integer.parseInt(pathEntry.substring(1, pathEntry.length() - 1));
/* 295 */               jsonObject = ((JSONArray)jsonObject).get(idx);
/*     */             } else {
/* 297 */               jsonObject = ((JSONObject)jsonObject).get(pathEntry);
/*     */             }
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 302 */             throw new SearchException("Invalid entry path : " + this.resultsEntryPath, t);
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 308 */         resultArray = (JSONArray)jsonObject;
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 312 */         throw new SearchException("Object is not a result array. Check the JSON service and/or the entry path");
/*     */       }
/*     */       
/*     */ 
/* 316 */       if (resultArray != null)
/*     */       {
/* 318 */         List results = new ArrayList();
/*     */         
/* 320 */         Throwable decode_failure = null;
/*     */         
/* 322 */         for (int i = 0; i < resultArray.size(); i++)
/*     */         {
/* 324 */           Object obj = resultArray.get(i);
/*     */           
/* 326 */           if ((obj instanceof JSONObject)) {
/* 327 */             JSONObject jsonEntry = (JSONObject)obj;
/*     */             
/* 329 */             if (absolute_max_matches >= 0) {
/* 330 */               absolute_max_matches--; if (absolute_max_matches < 0) {
/*     */                 break;
/*     */               }
/*     */             }
/*     */             
/* 335 */             if (listener != null)
/*     */             {
/*     */ 
/*     */ 
/* 339 */               Iterator it = new TreeMap(jsonEntry).entrySet().iterator();
/*     */               
/* 341 */               String[] groups = new String[jsonEntry.size()];
/*     */               
/* 343 */               int pos = 0;
/*     */               
/* 345 */               while (it.hasNext())
/*     */               {
/* 347 */                 Map.Entry entry = (Map.Entry)it.next();
/*     */                 
/* 349 */                 Object key = entry.getKey();
/* 350 */                 Object value = entry.getValue();
/*     */                 
/* 352 */                 if ((key != null) && (value != null))
/*     */                 {
/* 354 */                   groups[(pos++)] = (key.toString() + "=" + UrlUtils.encode(value.toString()));
/*     */                 }
/*     */                 else
/*     */                 {
/* 358 */                   groups[(pos++)] = "";
/*     */                 }
/*     */               }
/*     */               
/* 362 */               listener.matchFound(this, groups);
/*     */             }
/*     */             
/* 365 */             WebResult result = new WebResult(this, getRootPage(), getBasePage(), getDateParser(), searchQuery);
/*     */             try
/*     */             {
/* 368 */               for (int j = 0; j < mappings.length; j++) {
/* 369 */                 String fieldFrom = mappings[j].getName();
/* 370 */                 if (fieldFrom != null)
/*     */                 {
/*     */ 
/*     */ 
/* 374 */                   int fieldTo = mappings[j].getField();
/*     */                   
/* 376 */                   String fieldContent = null;
/* 377 */                   Matcher matcher = patternVariable.matcher(fieldFrom);
/* 378 */                   if (matcher.find()) {
/* 379 */                     fieldContent = fieldFrom;
/*     */                     do {
/* 381 */                       String key = matcher.group();
/* 382 */                       key = key.substring(2, key.length() - 1);
/*     */                       
/* 384 */                       String[] keys = key.split(",", -1);
/*     */                       try {
/* 386 */                         Object replaceWithObject = jsonEntry.get(keys[0]);
/* 387 */                         String replaceWith = replaceWithObject == null ? "" : replaceWithObject.toString();
/*     */                         
/*     */ 
/* 390 */                         if (keys.length > 1) {
/* 391 */                           String[] commands = keys[1].split("\\+");
/* 392 */                           int keyPos = 2;
/* 393 */                           for (String command : commands)
/*     */                           {
/*     */                             try
/*     */                             {
/*     */ 
/* 398 */                               if (command.equals("replace")) {
/* 399 */                                 if (keyPos + 2 > keys.length) {
/*     */                                   break;
/*     */                                 }
/*     */                                 
/*     */ 
/*     */ 
/* 405 */                                 String simpleReplace = keys[keyPos];
/* 406 */                                 keyPos++;
/* 407 */                                 String simpleReplacement = keys[keyPos];
/* 408 */                                 keyPos++;
/*     */                                 
/* 410 */                                 replaceWith = replaceWith.replaceAll(simpleReplace, simpleReplacement);
/* 411 */                               } else if (command.equals("ucase")) {
/* 412 */                                 replaceWith = replaceWith.toUpperCase();
/* 413 */                               } else if (command.equals("lcase")) {
/* 414 */                                 replaceWith = replaceWith.toLowerCase();
/* 415 */                               } else if (command.equals("urldecode")) {
/* 416 */                                 replaceWith = UrlUtils.decode(replaceWith);
/*     */                               }
/*     */                             }
/*     */                             catch (Exception e) {}
/*     */                           }
/*     */                         }
/*     */                         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 429 */                         fieldContent = fieldContent.replaceFirst("\\$\\{[^}]+\\}", replaceWith);
/*     */ 
/*     */ 
/*     */                       }
/*     */                       catch (Exception e) {}
/*     */                     }
/* 435 */                     while (matcher.find());
/*     */                   } else {
/* 437 */                     Object fieldContentObj = jsonEntry.get(fieldFrom);
/* 438 */                     fieldContent = fieldContentObj == null ? "" : fieldContentObj.toString();
/*     */                   }
/*     */                   
/*     */ 
/* 442 */                   if (fieldContent != null)
/*     */                   {
/*     */ 
/*     */ 
/* 446 */                     switch (fieldTo) {
/*     */                     case 1: 
/* 448 */                       result.setNameFromHTML(fieldContent);
/* 449 */                       break;
/*     */                     case 3: 
/* 451 */                       result.setSizeFromHTML(fieldContent);
/* 452 */                       break;
/*     */                     case 4: 
/* 454 */                       result.setNbPeersFromHTML(fieldContent);
/* 455 */                       break;
/*     */                     case 5: 
/* 457 */                       result.setNbSeedsFromHTML(fieldContent);
/* 458 */                       break;
/*     */                     case 6: 
/* 460 */                       result.setCategoryFromHTML(fieldContent);
/* 461 */                       break;
/*     */                     case 2: 
/* 463 */                       result.setPublishedDateFromHTML(fieldContent);
/* 464 */                       break;
/*     */                     case 7: 
/* 466 */                       result.setCommentsFromHTML(fieldContent);
/* 467 */                       break;
/*     */                     case 103: 
/* 469 */                       result.setCDPLink(fieldContent);
/* 470 */                       break;
/*     */                     case 102: 
/* 472 */                       result.setTorrentLink(fieldContent);
/* 473 */                       break;
/*     */                     case 104: 
/* 475 */                       result.setPlayLink(fieldContent);
/* 476 */                       break;
/*     */                     case 105: 
/* 478 */                       result.setDownloadButtonLink(fieldContent);
/* 479 */                       break;
/*     */                     case 10: 
/* 481 */                       result.setVotesFromHTML(fieldContent);
/* 482 */                       break;
/*     */                     case 11: 
/* 484 */                       result.setNbSuperSeedsFromHTML(fieldContent);
/* 485 */                       break;
/*     */                     case 12: 
/* 487 */                       result.setPrivateFromHTML(fieldContent);
/* 488 */                       break;
/*     */                     case 13: 
/* 490 */                       result.setDrmKey(fieldContent);
/* 491 */                       break;
/*     */                     case 14: 
/* 493 */                       result.setVotesDownFromHTML(fieldContent);
/* 494 */                       break;
/*     */                     
/*     */                     case 200: 
/* 497 */                       if (fieldContent.startsWith("magnet:")) {
/* 498 */                         byte[] hash = UrlUtils.getHashFromMagnetURI(fieldContent);
/* 499 */                         if (hash != null) {
/* 500 */                           fieldContent = ByteFormatter.encodeString(hash);
/*     */                         } else {
/* 502 */                           fieldContent = null;
/*     */                         }
/*     */                       }
/* 505 */                       if (fieldContent != null) {
/* 506 */                         result.setHash(fieldContent);
/*     */                       }
/*     */                       break;
/*     */                     case 201: 
/* 510 */                       result.setRankFromHTML(fieldContent, this.rankDivisor);
/*     */                     }
/*     */                     
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/* 518 */               if (result.getHash() == null) {
/* 519 */                 String downloadLink = result.getDownloadLink();
/* 520 */                 String possibleMagnet = UrlUtils.parseTextForMagnets(downloadLink);
/* 521 */                 byte[] hash = UrlUtils.getHashFromMagnetURI(possibleMagnet);
/* 522 */                 if (hash != null) {
/* 523 */                   result.setHash(ByteFormatter.nicePrint(hash, true));
/*     */                 }
/*     */               }
/*     */               
/* 527 */               results.add(result);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 531 */               decode_failure = e;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 536 */         if ((results.size() == 0) && (decode_failure != null))
/*     */         {
/* 538 */           throw decode_failure;
/*     */         }
/*     */         
/* 541 */         Result[] res = (Result[])results.toArray(new Result[results.size()]);
/*     */         
/* 543 */         debugLog("success: found " + res.length + " results");
/*     */         
/* 545 */         return res;
/*     */       }
/*     */       
/*     */ 
/* 549 */       debugLog("success: no result array found so no results");
/*     */       
/* 551 */       return new Result[0];
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 556 */       debugLog("failed: " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       
/* 558 */       if ((e instanceof SearchException))
/*     */       {
/* 560 */         throw ((SearchException)e);
/*     */       }
/*     */       
/* 563 */       String content_str = page;
/*     */       
/* 565 */       if (content_str.length() > 256)
/*     */       {
/* 567 */         content_str = content_str.substring(0, 256) + "...";
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 572 */       throw new SearchException("JSON matching failed for " + getName() + ", content=" + content_str, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/json/JSONEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */