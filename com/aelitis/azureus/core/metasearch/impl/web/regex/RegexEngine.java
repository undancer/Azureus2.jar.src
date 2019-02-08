/*     */ package com.aelitis.azureus.core.metasearch.impl.web.regex;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*     */ import com.aelitis.azureus.core.metasearch.SearchException;
/*     */ import com.aelitis.azureus.core.metasearch.SearchLoginException;
/*     */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*     */ import com.aelitis.azureus.core.metasearch.impl.EngineImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.MetaSearchImpl;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.FieldMapping;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine.pageDetails;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebResult;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TimeLimitedTask;
/*     */ import org.gudy.azureus2.core3.util.TimeLimitedTask.task;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class RegexEngine
/*     */   extends WebEngine
/*     */ {
/*     */   private static final boolean DEBUG_MAPPINGS = false;
/*     */   private static final String variablePattern = "\\$\\{[^}]+\\}";
/*  51 */   private static final Pattern patternVariable = Pattern.compile("\\$\\{[^}]+\\}");
/*     */   
/*     */ 
/*     */   private String pattern_str;
/*     */   
/*     */ 
/*     */   public static EngineImpl importFromBEncodedMap(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/*  60 */     return new RegexEngine(meta_search, map);
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
/*  74 */     return new RegexEngine(meta_search, id, last_updated, rank_bias, name, map);
/*     */   }
/*     */   
/*     */ 
/*  78 */   private Pattern[] patterns = new Pattern[0];
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
/*     */   public RegexEngine(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, String searchURLFormat, String resultPattern, String timeZone, boolean automaticDateFormat, String userDateFormat, FieldMapping[] mappings, boolean needs_auth, String auth_method, String login_url, String[] required_cookies)
/*     */   {
/* 101 */     super(meta_search, 1, id, last_updated, rank_bias, name, searchURLFormat, timeZone, automaticDateFormat, userDateFormat, mappings, needs_auth, auth_method, login_url, required_cookies);
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
/* 117 */     init(resultPattern);
/*     */     
/* 119 */     setSource(2);
/*     */     
/* 121 */     setSelectionState(2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected RegexEngine(MetaSearchImpl meta_search, Map map)
/*     */     throws IOException
/*     */   {
/* 133 */     super(meta_search, map);
/*     */     
/* 135 */     String resultPattern = ImportExportUtils.importString(map, "regex.pattern");
/*     */     
/* 137 */     init(resultPattern);
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
/*     */   protected RegexEngine(MetaSearchImpl meta_search, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*     */     throws IOException
/*     */   {
/* 153 */     super(meta_search, 1, id, last_updated, rank_bias, name, map);
/*     */     
/* 155 */     String resultPattern = ImportExportUtils.importString(map, "regexp");
/*     */     
/* 157 */     resultPattern = URLDecoder.decode(resultPattern, "UTF-8");
/*     */     
/* 159 */     init(resultPattern);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap()
/*     */     throws IOException
/*     */   {
/* 167 */     return exportToBencodedMap(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map exportToBencodedMap(boolean generic)
/*     */     throws IOException
/*     */   {
/* 176 */     Map res = new HashMap();
/*     */     
/* 178 */     ImportExportUtils.exportString(res, "regex.pattern", this.pattern_str);
/*     */     
/* 180 */     super.exportToBencodedMap(res, generic);
/*     */     
/* 182 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exportToJSONObject(JSONObject res)
/*     */     throws IOException
/*     */   {
/* 191 */     res.put("regexp", UrlUtils.encode(this.pattern_str));
/*     */     
/* 193 */     super.exportToJSONObject(res);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void init(String resultPattern)
/*     */   {
/* 200 */     this.pattern_str = resultPattern.trim();
/* 201 */     if (this.pattern_str.length() == 0) {
/* 202 */       this.patterns = new Pattern[0];
/*     */     } else {
/* 204 */       this.patterns = new Pattern[] { Pattern.compile(this.pattern_str), Pattern.compile(this.pattern_str, 40) };
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
/*     */   protected Result[] searchSupport(final SearchParameter[] searchParameters, Map searchContext, int desired_max_matches, final int o_absolute_max_matches, String headers, final ResultListener listener)
/*     */     throws SearchException
/*     */   {
/* 223 */     debugStart();
/*     */     
/* 225 */     final WebEngine.pageDetails page_details = getWebPageContent(searchParameters, searchContext, headers, false);
/*     */     
/* 227 */     final String page = page_details.getContent();
/*     */     
/* 229 */     if (listener != null)
/*     */     {
/* 231 */       listener.contentReceived(this, page);
/*     */     }
/*     */     
/* 234 */     debugLog("pattern: " + this.pattern_str);
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
/*     */     try
/*     */     {
/* 275 */       TimeLimitedTask task = new TimeLimitedTask("MetaSearch:regexpr", 30000, 4, new TimeLimitedTask.task()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public Object run()
/*     */           throws Exception
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 286 */           int max_matches = o_absolute_max_matches;
/*     */           
/* 288 */           if ((max_matches < 0) || (max_matches > 1024))
/*     */           {
/* 290 */             max_matches = 1024;
/*     */           }
/*     */           
/* 293 */           String searchQuery = null;
/*     */           
/* 295 */           for (int i = 0; i < searchParameters.length; i++) {
/* 296 */             if (searchParameters[i].getMatchPattern().equals("s")) {
/* 297 */               searchQuery = searchParameters[i].getValue();
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 302 */           FieldMapping[] mappings = RegexEngine.this.getMappings();
/*     */           try
/*     */           {
/* 305 */             List<WebResult> results = new ArrayList();
/*     */             
/* 307 */             for (int pat_num = 0; pat_num < RegexEngine.this.patterns.length; pat_num++)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 312 */               if (results.size() > 0) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 317 */               Pattern pattern = RegexEngine.this.patterns[pat_num];
/*     */               
/* 319 */               Matcher m = pattern.matcher(page);
/*     */               
/* 321 */               while (m.find())
/*     */               {
/* 323 */                 if (max_matches >= 0) {
/* 324 */                   max_matches--; if (max_matches < 0) {
/*     */                     break;
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/* 330 */                 String[] groups = new String[m.groupCount()];
/*     */                 
/* 332 */                 for (int i = 0; i < groups.length; i++)
/*     */                 {
/* 334 */                   groups[i] = m.group(i + 1);
/*     */                 }
/*     */                 
/*     */ 
/* 338 */                 if (listener != null)
/*     */                 {
/* 340 */                   listener.matchFound(RegexEngine.this, groups);
/*     */                 }
/*     */                 
/* 343 */                 RegexEngine.this.debugLog("Found match:");
/*     */                 
/* 345 */                 WebResult result = new WebResult(RegexEngine.this, RegexEngine.this.getRootPage(), RegexEngine.this.getBasePage(), RegexEngine.this.getDateParser(), searchQuery);
/*     */                 
/* 347 */                 int fields_matched = 0;
/*     */                 
/* 349 */                 for (int i = 0; i < mappings.length; i++) {
/* 350 */                   String fieldFrom = mappings[i].getName();
/*     */                   
/* 352 */                   String fieldContent = null;
/* 353 */                   Matcher matcher = RegexEngine.patternVariable.matcher(fieldFrom);
/* 354 */                   if (matcher.find()) {
/* 355 */                     fieldContent = fieldFrom;
/*     */                     do {
/* 357 */                       String key = matcher.group();
/* 358 */                       key = key.substring(2, key.length() - 1);
/* 359 */                       String[] keys = key.split(",", -1);
/*     */                       try {
/* 361 */                         int groupNo = Integer.parseInt(keys[0]);
/*     */                         
/*     */ 
/* 364 */                         String replaceWith = groups[(groupNo - 1)];
/*     */                         
/* 366 */                         if (keys.length > 1) {
/* 367 */                           String[] commands = keys[1].split("\\+");
/* 368 */                           int keyPos = 2;
/* 369 */                           for (String command : commands)
/*     */                           {
/*     */                             try
/*     */                             {
/*     */ 
/* 374 */                               if (command.equals("replace")) {
/* 375 */                                 if (keyPos + 2 > keys.length) {
/*     */                                   break;
/*     */                                 }
/*     */                                 
/*     */ 
/*     */ 
/* 381 */                                 String simpleReplace = keys[keyPos];
/* 382 */                                 keyPos++;
/* 383 */                                 String simpleReplacement = keys[keyPos];
/* 384 */                                 keyPos++;
/*     */                                 
/* 386 */                                 replaceWith = replaceWith.replaceAll(simpleReplace, simpleReplacement);
/* 387 */                               } else if (command.equals("ucase")) {
/* 388 */                                 replaceWith = replaceWith.toUpperCase();
/* 389 */                               } else if (command.equals("lcase")) {
/* 390 */                                 replaceWith = replaceWith.toLowerCase();
/* 391 */                               } else if (command.equals("urldecode")) {
/* 392 */                                 replaceWith = UrlUtils.decode(replaceWith);
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
/* 405 */                         fieldContent = fieldContent.replaceFirst("\\$\\{[^}]+\\}", replaceWith);
/*     */ 
/*     */ 
/*     */                       }
/*     */                       catch (Exception e) {}
/*     */                     }
/* 411 */                     while (matcher.find());
/*     */                   } else {
/*     */                     try {
/* 414 */                       int groupNo = Integer.parseInt(fieldFrom);
/* 415 */                       fieldContent = groups[(groupNo - 1)];
/*     */                     }
/*     */                     catch (Exception e) {}
/*     */                   }
/*     */                   
/*     */ 
/* 421 */                   if (fieldContent != null)
/*     */                   {
/* 423 */                     int fieldTo = mappings[i].getField();
/*     */                     
/* 425 */                     RegexEngine.this.debugLog("    " + fieldTo + "=" + fieldContent);
/*     */                     
/* 427 */                     fields_matched++;
/*     */                     
/* 429 */                     switch (fieldTo) {
/*     */                     case 1: 
/* 431 */                       result.setNameFromHTML(fieldContent);
/* 432 */                       break;
/*     */                     case 3: 
/* 434 */                       result.setSizeFromHTML(fieldContent);
/* 435 */                       break;
/*     */                     case 4: 
/* 437 */                       result.setNbPeersFromHTML(fieldContent);
/* 438 */                       break;
/*     */                     case 5: 
/* 440 */                       result.setNbSeedsFromHTML(fieldContent);
/* 441 */                       break;
/*     */                     case 6: 
/* 443 */                       result.setCategoryFromHTML(fieldContent);
/* 444 */                       break;
/*     */                     case 2: 
/* 446 */                       result.setPublishedDateFromHTML(fieldContent);
/* 447 */                       break;
/*     */                     case 103: 
/* 449 */                       result.setCDPLink(fieldContent);
/* 450 */                       break;
/*     */                     case 102: 
/* 452 */                       result.setTorrentLink(fieldContent);
/* 453 */                       break;
/*     */                     case 104: 
/* 455 */                       result.setPlayLink(fieldContent);
/* 456 */                       break;
/*     */                     case 105: 
/* 458 */                       result.setDownloadButtonLink(fieldContent);
/* 459 */                       break;
/*     */                     case 7: 
/* 461 */                       result.setCommentsFromHTML(fieldContent);
/* 462 */                       break;
/*     */                     case 10: 
/* 464 */                       result.setVotesFromHTML(fieldContent);
/* 465 */                       break;
/*     */                     case 11: 
/* 467 */                       result.setNbSuperSeedsFromHTML(fieldContent);
/* 468 */                       break;
/*     */                     case 12: 
/* 470 */                       result.setPrivateFromHTML(fieldContent);
/* 471 */                       break;
/*     */                     case 13: 
/* 473 */                       result.setDrmKey(fieldContent);
/* 474 */                       break;
/*     */                     case 14: 
/* 476 */                       result.setVotesDownFromHTML(fieldContent);
/* 477 */                       break;
/*     */                     case 200: 
/* 479 */                       result.setHash(fieldContent);
/* 480 */                       break;
/*     */                     default: 
/* 482 */                       fields_matched--;
/*     */                     }
/*     */                     
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 490 */                 if (fields_matched > 0)
/*     */                 {
/* 492 */                   if (result.getHash() == null) {
/* 493 */                     String downloadLink = result.getDownloadLink();
/* 494 */                     String possibleMagnet = UrlUtils.parseTextForMagnets(downloadLink);
/* 495 */                     byte[] hash = UrlUtils.getHashFromMagnetURI(possibleMagnet);
/* 496 */                     if (hash != null) {
/* 497 */                       result.setHash(ByteFormatter.nicePrint(hash, true));
/*     */                     }
/*     */                   }
/*     */                   
/* 501 */                   results.add(result);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 509 */             if ((results.size() == 0) && (RegexEngine.this.isNeedsAuth()))
/*     */             {
/* 511 */               if ((page_details.getInitialURL().getProtocol().equalsIgnoreCase("http")) && (page_details.getFinalURL().getProtocol().equalsIgnoreCase("https")))
/*     */               {
/*     */ 
/* 514 */                 throw new SearchLoginException("login possibly required");
/*     */               }
/*     */             }
/*     */             
/* 518 */             return (Result[])results.toArray(new Result[results.size()]);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 522 */             RegexEngine.this.log("Failed process result", e);
/*     */             
/* 524 */             if ((e instanceof SearchException))
/*     */             {
/* 526 */               throw ((SearchException)e);
/*     */             }
/*     */             
/* 529 */             throw new SearchException(e);
/*     */           }
/*     */           
/*     */         }
/* 533 */       });
/* 534 */       Result[] res = (Result[])task.run();
/*     */       
/* 536 */       debugLog("success: found " + res.length + " results");
/*     */       
/* 538 */       return res;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 542 */       debugLog("failed: " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       
/* 544 */       if ((e instanceof SearchException))
/*     */       {
/* 546 */         throw ((SearchException)e);
/*     */       }
/*     */       
/* 549 */       throw new SearchException("Regex matching failed", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/regex/RegexEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */