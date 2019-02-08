/*     */ package com.aelitis.azureus.core.metasearch;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.utils.MomentsAgoDateFormatter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.apache.commons.lang.Entities;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ 
/*     */ public abstract class Result
/*     */ {
/*     */   private static final String HTML_TAGS = "(\\<(/?[^\\>]+)\\>)";
/*     */   private static final String DUPLICATE_SPACES = "\\s{2,}";
/*     */   private final Engine engine;
/*     */   
/*     */   public abstract Date getPublishedDate();
/*     */   
/*     */   public abstract String getCategory();
/*     */   
/*     */   public abstract void setCategory(String paramString);
/*     */   
/*     */   public abstract String getContentType();
/*     */   
/*     */   public abstract void setContentType(String paramString);
/*     */   
/*     */   public abstract String getName();
/*     */   
/*     */   public abstract long getSize();
/*     */   
/*     */   public abstract int getNbPeers();
/*     */   
/*     */   public abstract int getNbSeeds();
/*     */   
/*     */   public abstract int getNbSuperSeeds();
/*     */   
/*     */   public abstract int getComments();
/*     */   
/*     */   public abstract int getVotes();
/*     */   
/*  45 */   private final long time_created_secs = org.gudy.azureus2.core3.util.SystemTime.getCurrentTime() / 1000L;
/*     */   
/*     */ 
/*     */   public abstract int getVotesDown();
/*     */   
/*     */ 
/*     */   public abstract boolean isPrivate();
/*     */   
/*     */ 
/*     */   public abstract String getDRMKey();
/*     */   
/*     */ 
/*     */   public abstract String getDownloadLink();
/*     */   
/*     */ 
/*     */   public abstract String getTorrentLink();
/*     */   
/*     */ 
/*     */   public abstract String getDownloadButtonLink();
/*     */   
/*     */ 
/*     */   public abstract String getCDPLink();
/*     */   
/*     */ 
/*     */   public abstract String getPlayLink();
/*     */   
/*     */ 
/*     */   public abstract float getAccuracy();
/*     */   
/*     */ 
/*     */   public abstract String getSearchQuery();
/*     */   
/*     */ 
/*     */   public abstract String getUID();
/*     */   
/*     */ 
/*     */   public abstract String getHash();
/*     */   
/*     */   protected Result(Engine _engine)
/*     */   {
/*  85 */     this.engine = _engine;
/*     */   }
/*     */   
/*     */ 
/*     */   public Engine getEngine()
/*     */   {
/*  91 */     return this.engine;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  95 */     return getName() + " : " + getNbSeeds() + " s, " + getNbPeers() + "p, ";
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
/*     */   public float getRank()
/*     */   {
/* 124 */     int seeds = getNbSeeds();
/* 125 */     int peers = getNbPeers();
/*     */     
/* 127 */     if (seeds < 0) {
/* 128 */       seeds = 0;
/*     */     }
/*     */     
/* 131 */     if (peers < 0) {
/* 132 */       peers = 0;
/*     */     }
/*     */     
/* 135 */     int totalVirtualPeers = 3 * seeds + peers + 2;
/*     */     
/* 137 */     int superSeeds = getNbSuperSeeds();
/* 138 */     if (superSeeds > 0) {
/* 139 */       totalVirtualPeers += 50 * superSeeds;
/*     */     }
/*     */     
/* 142 */     int votes = getVotes();
/* 143 */     if (votes > 0) {
/* 144 */       if (votes > 50) {
/* 145 */         votes = 50;
/*     */       }
/* 147 */       totalVirtualPeers += 5 * votes;
/*     */     }
/*     */     
/* 150 */     int votesDown = getVotesDown();
/* 151 */     if (votesDown > 0) {
/* 152 */       totalVirtualPeers -= 200 * votesDown;
/*     */     }
/*     */     
/* 155 */     if (totalVirtualPeers < 2) { totalVirtualPeers = 2;
/*     */     }
/* 157 */     float rank = (float)(Math.log(totalVirtualPeers) / Math.log(10.0D)) / 5.0F;
/*     */     
/* 159 */     if (rank > 2.0F) { rank = 2.0F;
/*     */     }
/* 161 */     if (isPrivate()) {
/* 162 */       rank /= 2.0F;
/*     */     }
/*     */     
/* 165 */     String queryString = getSearchQuery();
/* 166 */     String name = getName();
/* 167 */     if ((queryString != null) && (name != null)) {
/* 168 */       name = name.toLowerCase(Locale.ENGLISH);
/*     */       
/* 170 */       String token = "";
/*     */       
/* 172 */       List<String> tokens = new ArrayList();
/*     */       
/* 174 */       char[] chars = queryString.toCharArray();
/*     */       
/* 176 */       for (char c : chars)
/*     */       {
/* 178 */         if (Character.isLetterOrDigit(c))
/*     */         {
/* 180 */           token = token + String.valueOf(c).toLowerCase(Locale.ENGLISH);
/*     */ 
/*     */ 
/*     */         }
/* 184 */         else if (token.length() > 0)
/*     */         {
/* 186 */           tokens.add(token);
/*     */           
/* 188 */           token = "";
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 193 */       if (token.length() > 0)
/*     */       {
/* 195 */         tokens.add(token);
/*     */       }
/*     */       
/* 198 */       for (String s : tokens) {
/* 199 */         if (!name.contains(s)) {
/* 200 */           rank /= 2.0F;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 205 */     rank = applyRankBias(rank);
/*     */     
/* 207 */     return rank;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected float applyRankBias(float _rank)
/*     */   {
/* 214 */     float rank = this.engine.applyRankBias(_rank);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 223 */     return rank;
/*     */   }
/*     */   
/*     */   public Map toJSONMap() {
/* 227 */     Map object = new org.json.simple.JSONObject();
/*     */     
/* 229 */     object.put("tf", "" + this.time_created_secs);
/*     */     
/* 231 */     Date pub_date = getPublishedDate();
/* 232 */     if (pub_date == null) {
/* 233 */       object.put("d", "unknown");
/* 234 */       object.put("ts", "0");
/*     */     } else {
/*     */       try {
/* 237 */         object.put("d", MomentsAgoDateFormatter.getMomentsAgoString(pub_date));
/* 238 */         object.put("ts", "" + pub_date.getTime());
/*     */       } catch (Exception e) {
/* 240 */         object.put("d", "unknown");
/* 241 */         object.put("ts", "0");
/*     */       }
/*     */     }
/*     */     
/* 245 */     object.put("c", getCategory());
/* 246 */     object.put("n", getName());
/*     */     
/* 248 */     int super_seeds = getNbSuperSeeds();
/* 249 */     int seeds = getNbSeeds();
/*     */     
/* 251 */     int seed_total = -1;
/*     */     
/* 253 */     if (super_seeds > 0)
/*     */     {
/* 255 */       seed_total = 10 * super_seeds + new Random().nextInt(10);
/*     */     }
/*     */     
/* 258 */     if (seeds > 0)
/*     */     {
/* 260 */       if (seed_total == -1)
/*     */       {
/* 262 */         seed_total = 0;
/*     */       }
/*     */       
/* 265 */       seed_total += seeds;
/*     */     }
/*     */     
/* 268 */     object.put("s", "" + seed_total);
/*     */     
/* 270 */     if (getNbPeers() >= 0) {
/* 271 */       object.put("p", "" + getNbPeers());
/*     */     } else {
/* 273 */       object.put("p", "-1");
/*     */     }
/*     */     
/* 276 */     int comments = getComments();
/*     */     
/* 278 */     if (comments >= 0)
/*     */     {
/* 280 */       object.put("co", "" + comments);
/*     */     }
/*     */     
/* 283 */     long size = getSize();
/* 284 */     if (size >= 0L)
/*     */     {
/*     */ 
/* 287 */       String size_str = DisplayFormatters.formatByteCountToKiBEtc(size);
/*     */       
/* 289 */       size_str = DisplayFormatters.trimDigits(size_str, 3);
/*     */       
/* 291 */       object.put("l", size_str);
/* 292 */       object.put("lb", "" + size);
/*     */     } else {
/* 294 */       object.put("l", "-1");
/* 295 */       object.put("lb", "0");
/*     */     }
/*     */     
/* 298 */     object.put("r", "" + getRank());
/*     */     
/* 300 */     object.put("ct", getContentType());
/*     */     
/* 302 */     float accuracy = getAccuracy();
/*     */     
/* 304 */     if (accuracy >= 0.0F) {
/* 305 */       if (accuracy > 1.0F) {
/* 306 */         accuracy = 1.0F;
/*     */       }
/* 308 */       object.put("ac", "" + accuracy);
/*     */     }
/*     */     
/* 311 */     if (getCDPLink().length() > 0) {
/* 312 */       object.put("cdp", getCDPLink());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 318 */     if (getDownloadLink().length() > 0) {
/* 319 */       object.put("dl", getDownloadLink());
/*     */     }
/*     */     
/* 322 */     if (getDownloadButtonLink().length() > 0) {
/* 323 */       object.put("dbl", getDownloadButtonLink());
/*     */     }
/*     */     
/* 326 */     if (getPlayLink().length() > 0) {
/* 327 */       object.put("pl", getPlayLink());
/*     */     }
/*     */     
/* 330 */     if (getVotes() >= 0) {
/* 331 */       object.put("v", "" + getVotes());
/*     */     }
/*     */     
/* 334 */     if (getVotesDown() >= 0) {
/* 335 */       object.put("vd", "" + getVotesDown());
/*     */     }
/*     */     
/* 338 */     String drmKey = getDRMKey();
/* 339 */     if (drmKey != null) {
/* 340 */       object.put("dk", drmKey);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 345 */     String uid = getUID();
/* 346 */     if (uid != null) {
/* 347 */       object.put("u", uid);
/*     */     }
/* 349 */     object.put("pr", isPrivate() ? "1" : "0");
/*     */     
/* 351 */     String hash = getHash();
/* 352 */     if (hash != null) {
/* 353 */       object.put("h", hash);
/*     */     }
/*     */     
/* 356 */     return object;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String guessContentTypeFromCategory(String category)
/*     */   {
/* 363 */     if ((category == null) || (category.length() == 0))
/*     */     {
/* 365 */       return "";
/*     */     }
/*     */     
/* 368 */     category = category.toLowerCase(Locale.US);
/*     */     
/* 370 */     if ((category.startsWith("video")) || (category.startsWith("movie")) || (category.startsWith("show")) || (category.startsWith("tv")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 375 */       return "video";
/*     */     }
/* 377 */     if ((category.startsWith("audio")) || (category.startsWith("music")))
/*     */     {
/*     */ 
/* 380 */       return "audio";
/*     */     }
/* 382 */     if (category.startsWith("game"))
/*     */     {
/* 384 */       return "game";
/*     */     }
/*     */     
/* 387 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String adjustLink(String link)
/*     */   {
/* 395 */     if ((link == null) || (link.length() < 5))
/*     */     {
/* 397 */       return link;
/*     */     }
/*     */     
/* 400 */     char c = link.charAt(0);
/*     */     
/* 402 */     if ((c == 'h') || (c == 'H') || (c == 'f') || (c == 'F'))
/*     */     {
/* 404 */       if (MetaSearchManagerFactory.getSingleton().getProxyRequestsEnabled())
/*     */       {
/*     */         try {
/* 407 */           String host = new URL(link).getHost();
/*     */           
/* 409 */           if (org.gudy.azureus2.core3.util.AENetworkClassifier.categoriseAddress(host) != "Public")
/*     */           {
/* 411 */             return link;
/*     */           }
/*     */           
/* 414 */           InetAddress ia = org.gudy.azureus2.core3.util.HostNameToIPResolver.hostAddressToInetAddress(host);
/*     */           
/* 416 */           if (ia != null)
/*     */           {
/* 418 */             if ((ia.isLoopbackAddress()) || (ia.isLinkLocalAddress()) || (ia.isSiteLocalAddress()))
/*     */             {
/*     */ 
/*     */ 
/* 422 */               return link;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/* 429 */         return "tor:" + link;
/*     */       }
/*     */     }
/*     */     
/* 433 */     return link;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void adjustRelativeTerms(Map map)
/*     */   {
/* 440 */     String ts = (String)map.get("ts");
/*     */     
/* 442 */     if (ts != null)
/*     */     {
/* 444 */       long l_ts = Long.parseLong(ts);
/*     */       
/* 446 */       if (l_ts > 0L)
/*     */       {
/* 448 */         map.put("d", MomentsAgoDateFormatter.getMomentsAgoString(new Date(l_ts)));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected static final String removeHTMLTags(String input)
/*     */   {
/* 455 */     if (input == null) {
/* 456 */       return null;
/*     */     }
/* 458 */     String result = input.replaceAll("(\\<(/?[^\\>]+)\\>)", " ");
/* 459 */     return result.replaceAll("\\s{2,}", " ").trim();
/*     */   }
/*     */   
/*     */   protected static final String unescapeEntities(String input)
/*     */   {
/* 464 */     if (input == null) {
/* 465 */       return null;
/*     */     }
/* 467 */     return Entities.HTML40.unescape(input);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/Result.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */