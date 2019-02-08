/*     */ package com.aelitis.azureus.core.metasearch.impl.web;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.core.metasearch.impl.DateParser;
/*     */ import java.util.Date;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.commons.lang.Entities;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class WebResult
/*     */   extends Result
/*     */ {
/*     */   String searchQuery;
/*     */   String rootPageURL;
/*     */   String basePageURL;
/*     */   DateParser dateParser;
/*  46 */   String contentType = "";
/*     */   String name;
/*  48 */   String category = "";
/*     */   
/*  50 */   String drmKey = null;
/*     */   
/*     */   Date publishedDate;
/*     */   
/*  54 */   long size = -1L;
/*  55 */   int nbPeers = -1;
/*  56 */   int nbSeeds = -1;
/*  57 */   int nbSuperSeeds = -1;
/*  58 */   int comments = -1;
/*  59 */   int votes = -1;
/*  60 */   int votesDown = -1;
/*  61 */   float rank = -1.0F;
/*     */   
/*     */   boolean privateTorrent;
/*     */   
/*     */   String cdpLink;
/*     */   String torrentLink;
/*     */   String downloadButtonLink;
/*     */   String playLink;
/*     */   String uid;
/*     */   String hash;
/*     */   
/*     */   public WebResult(Engine engine, String rootPageURL, String basePageURL, DateParser dateParser, String searchQuery)
/*     */   {
/*  74 */     super(engine);
/*  75 */     this.rootPageURL = rootPageURL;
/*  76 */     this.basePageURL = basePageURL;
/*  77 */     this.dateParser = dateParser;
/*  78 */     this.searchQuery = searchQuery;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/*  82 */     if (name != null) {
/*  83 */       this.name = name;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNameFromHTML(String name) {
/*  88 */     if (name != null) {
/*  89 */       name = removeHTMLTags(name);
/*  90 */       this.name = Entities.HTML40.unescape(name);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCommentsFromHTML(String comments) {
/*  95 */     if (comments != null) {
/*  96 */       comments = removeHTMLTags(comments);
/*  97 */       comments = Entities.HTML40.unescape(comments);
/*  98 */       comments = comments.replaceAll(",", "");
/*  99 */       comments = comments.replaceAll(" ", "");
/*     */       try {
/* 101 */         this.comments = Integer.parseInt(comments);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCategoryFromHTML(String category) {
/* 108 */     if (category != null) {
/* 109 */       category = removeHTMLTags(category);
/* 110 */       this.category = Entities.HTML40.unescape(category).trim();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */       if ((this.contentType == null) || (this.contentType.length() == 0)) {
/* 118 */         this.contentType = guessContentTypeFromCategory(this.category);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUID(String _uid)
/*     */   {
/* 127 */     this.uid = _uid;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUID()
/*     */   {
/* 133 */     return this.uid;
/*     */   }
/*     */   
/*     */   public void setNbPeersFromHTML(String nbPeers) {
/* 137 */     if (nbPeers != null) {
/* 138 */       nbPeers = removeHTMLTags(nbPeers);
/* 139 */       String nbPeersS = Entities.HTML40.unescape(nbPeers);
/* 140 */       nbPeersS = nbPeersS.replaceAll(",", "");
/* 141 */       nbPeersS = nbPeersS.replaceAll(" ", "");
/*     */       try {
/* 143 */         this.nbPeers = Integer.parseInt(nbPeersS);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNbSeedsFromHTML(String nbSeeds)
/*     */   {
/* 152 */     if (nbSeeds != null) {
/* 153 */       nbSeeds = removeHTMLTags(nbSeeds);
/* 154 */       String nbSeedsS = Entities.HTML40.unescape(nbSeeds);
/* 155 */       nbSeedsS = nbSeedsS.replaceAll(",", "");
/* 156 */       nbSeedsS = nbSeedsS.replaceAll(" ", "");
/*     */       try {
/* 158 */         this.nbSeeds = Integer.parseInt(nbSeedsS);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNbSuperSeedsFromHTML(String nbSuperSeeds)
/*     */   {
/* 167 */     if (nbSuperSeeds != null) {
/* 168 */       nbSuperSeeds = removeHTMLTags(nbSuperSeeds);
/* 169 */       String nbSuperSeedsS = Entities.HTML40.unescape(nbSuperSeeds);
/* 170 */       nbSuperSeedsS = nbSuperSeedsS.replaceAll(",", "");
/* 171 */       nbSuperSeedsS = nbSuperSeedsS.replaceAll(" ", "");
/*     */       try {
/* 173 */         this.nbSuperSeeds = Integer.parseInt(nbSuperSeedsS);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRankFromHTML(String rank_str, float divisor)
/*     */   {
/* 182 */     if (rank_str == null) {
/* 183 */       return;
/*     */     }
/* 185 */     if (rank_str.isEmpty()) {
/* 186 */       this.rank = -2.0F;
/*     */     } else {
/*     */       try {
/* 189 */         float f = Float.parseFloat(rank_str.trim());
/*     */         
/* 191 */         this.rank = (f / divisor);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public void setRankFromHTML(String rank_str) {
/* 198 */     if (rank_str != null) {
/* 199 */       if (rank_str.isEmpty()) {
/* 200 */         this.rank = -2.0F;
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 205 */           float f = Float.parseFloat(rank_str.trim());
/*     */           
/* 207 */           if (!rank_str.contains("."))
/*     */           {
/* 209 */             if ((f >= 0.0F) && (f <= 100.0F))
/*     */             {
/* 211 */               this.rank = (f / 100.0F);
/*     */             }
/*     */             
/*     */           }
/* 215 */           else if ((f >= 0.0F) && (f <= 1.0F))
/*     */           {
/* 217 */             this.rank = f;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public float getRank()
/*     */   {
/* 229 */     if (this.rank != -1.0F)
/*     */     {
/* 231 */       if (this.rank == -2.0F)
/*     */       {
/* 233 */         return -1.0F;
/*     */       }
/*     */       
/*     */ 
/* 237 */       return applyRankBias(this.rank);
/*     */     }
/*     */     
/*     */ 
/* 241 */     return super.getRank();
/*     */   }
/*     */   
/*     */   public void setPublishedDate(Date date) {
/* 245 */     this.publishedDate = date;
/*     */   }
/*     */   
/*     */   public void setPublishedDateFromHTML(String publishedDate) {
/* 249 */     if ((publishedDate != null) && (publishedDate.length() > 0)) {
/* 250 */       publishedDate = removeHTMLTags(publishedDate);
/* 251 */       String publishedDateS = Entities.HTML40.unescape(publishedDate).replace(' ', ' ');
/* 252 */       this.publishedDate = this.dateParser.parseDate(publishedDateS);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSizeFromHTML(String size)
/*     */   {
/* 258 */     if (size != null) {
/* 259 */       size = removeHTMLTags(size);
/* 260 */       String sizeS = Entities.HTML40.unescape(size).replace(' ', ' ');
/* 261 */       sizeS = sizeS.replaceAll("<[^>]+>", " ");
/*     */       
/* 263 */       sizeS = sizeS.replaceFirst("(\\d)([a-zA-Z])", "$1 $2");
/*     */       try {
/* 265 */         StringTokenizer st = new StringTokenizer(sizeS, " ");
/* 266 */         double base = Double.parseDouble(st.nextToken());
/* 267 */         String unit = "b";
/*     */         try {
/* 269 */           unit = st.nextToken().toLowerCase();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/* 273 */         long multiplier = 1L;
/* 274 */         long KB_UNIT = 1024L;
/* 275 */         long KIB_UNIT = 1024L;
/* 276 */         if ("mb".equals(unit)) {
/* 277 */           multiplier = KB_UNIT * KB_UNIT;
/* 278 */         } else if ("mib".equals(unit)) {
/* 279 */           multiplier = KIB_UNIT * KIB_UNIT;
/* 280 */         } else if ("m".equals(unit)) {
/* 281 */           multiplier = KIB_UNIT * KIB_UNIT;
/* 282 */         } else if ("gb".equals(unit)) {
/* 283 */           multiplier = KB_UNIT * KB_UNIT * KB_UNIT;
/* 284 */         } else if ("gib".equals(unit)) {
/* 285 */           multiplier = KIB_UNIT * KIB_UNIT * KIB_UNIT;
/* 286 */         } else if ("g".equals(unit)) {
/* 287 */           multiplier = KIB_UNIT * KIB_UNIT * KIB_UNIT;
/* 288 */         } else if ("kb".equals(unit)) {
/* 289 */           multiplier = KB_UNIT;
/* 290 */         } else if ("kib".equals(unit)) {
/* 291 */           multiplier = KIB_UNIT;
/* 292 */         } else if ("k".equals(unit)) {
/* 293 */           multiplier = KIB_UNIT;
/*     */         }
/*     */         
/* 296 */         this.size = ((base * multiplier));
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public void setVotesFromHTML(String votes_str)
/*     */   {
/* 304 */     if (votes_str != null) {
/* 305 */       votes_str = removeHTMLTags(votes_str);
/* 306 */       votes_str = Entities.HTML40.unescape(votes_str);
/* 307 */       votes_str = votes_str.replaceAll(",", "");
/* 308 */       votes_str = votes_str.replaceAll(" ", "");
/*     */       try {
/* 310 */         this.votes = Integer.parseInt(votes_str);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public void setVotesDownFromHTML(String votes_str)
/*     */   {
/* 318 */     if (votes_str != null) {
/* 319 */       votes_str = removeHTMLTags(votes_str);
/* 320 */       votes_str = Entities.HTML40.unescape(votes_str);
/* 321 */       votes_str = votes_str.replaceAll(",", "");
/* 322 */       votes_str = votes_str.replaceAll(" ", "");
/*     */       try {
/* 324 */         this.votesDown = Integer.parseInt(votes_str);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public void setPrivateFromHTML(String privateTorrent)
/*     */   {
/* 332 */     if ((privateTorrent != null) && (!"".equals(privateTorrent))) {
/* 333 */       this.privateTorrent = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVotes()
/*     */   {
/* 340 */     return this.votes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVotesDown()
/*     */   {
/* 346 */     return this.votesDown;
/*     */   }
/*     */   
/*     */   public void setCDPLink(String cdpLink) {
/* 350 */     this.cdpLink = UrlUtils.unescapeXML(cdpLink);
/*     */   }
/*     */   
/*     */   public void setDownloadButtonLink(String downloadButtonLink) {
/* 354 */     this.downloadButtonLink = UrlUtils.unescapeXML(downloadButtonLink);
/*     */   }
/*     */   
/*     */   public void setTorrentLink(String torrentLink) {
/* 358 */     this.torrentLink = UrlUtils.unescapeXML(torrentLink);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTorrentLinkRaw()
/*     */   {
/* 368 */     return this.torrentLink;
/*     */   }
/*     */   
/*     */   public void setPlayLink(String playLink) {
/* 372 */     this.playLink = playLink;
/*     */   }
/*     */   
/*     */   public String getContentType() {
/* 376 */     return this.contentType;
/*     */   }
/*     */   
/*     */   public String getPlayLink() {
/* 380 */     return reConstructLink(this.playLink);
/*     */   }
/*     */   
/*     */   public void setCategory(String category) {
/* 384 */     this.category = category;
/*     */   }
/*     */   
/*     */   public void setContentType(String contentType)
/*     */   {
/* 389 */     this.contentType = contentType;
/*     */   }
/*     */   
/*     */   public void setDrmKey(String drmKey)
/*     */   {
/* 394 */     this.drmKey = drmKey;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setHash(String _hash)
/*     */   {
/*     */     try
/*     */     {
/* 402 */       this.hash = _hash.trim();
/*     */       
/* 404 */       if (this.hash.length() != 32)
/*     */       {
/*     */ 
/*     */ 
/* 408 */         if (this.hash.length() == 40)
/*     */         {
/*     */ 
/*     */ 
/* 412 */           this.hash = Base32.encode(ByteFormatter.decodeString(this.hash));
/*     */         }
/*     */         else
/*     */         {
/* 416 */           this.hash = null;
/*     */         }
/*     */       }
/*     */     } catch (Throwable e) {
/* 420 */       Debug.printStackTrace(e);
/*     */       
/* 422 */       this.hash = null;
/*     */     }
/*     */     
/* 425 */     if ((this.hash != null) && (this.downloadButtonLink == null)) {
/* 426 */       setDownloadButtonLink(UrlUtils.normaliseMagnetURI(this.hash));
/*     */     }
/* 428 */     if ((this.hash != null) && (this.torrentLink == null)) {
/* 429 */       setTorrentLink(UrlUtils.normaliseMagnetURI(this.hash));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHash()
/*     */   {
/* 436 */     return this.hash;
/*     */   }
/*     */   
/*     */   public String getCDPLink()
/*     */   {
/* 441 */     return reConstructLink(this.cdpLink);
/*     */   }
/*     */   
/*     */   public String getCategory() {
/* 445 */     return this.category;
/*     */   }
/*     */   
/*     */   public String getDownloadLink()
/*     */   {
/* 450 */     return reConstructLink(this.torrentLink);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDownloadButtonLink()
/*     */   {
/* 458 */     if (this.downloadButtonLink != null) {
/* 459 */       return reConstructLink(this.downloadButtonLink);
/*     */     }
/* 461 */     return getDownloadLink();
/*     */   }
/*     */   
/*     */   public String getTorrentLink()
/*     */   {
/* 466 */     return reConstructLink(this.torrentLink);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String reConstructLink(String link)
/*     */   {
/* 473 */     if (link != null)
/*     */     {
/* 475 */       String lc_link = link.toLowerCase();
/*     */       
/* 477 */       if ((lc_link.startsWith("http://")) || (lc_link.startsWith("https://")) || (lc_link.startsWith("tor:http://")) || (lc_link.startsWith("tor:https://")) || (lc_link.startsWith("azplug:")) || (lc_link.startsWith("magnet:")) || (lc_link.startsWith("bc:")) || (lc_link.startsWith("bctp:")) || (lc_link.startsWith("dht:")))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 487 */         return adjustLink(link);
/*     */       }
/*     */       
/* 490 */       if (link.startsWith("/"))
/*     */       {
/* 492 */         return adjustLink((this.rootPageURL == null ? "" : this.rootPageURL) + link);
/*     */       }
/*     */       
/* 495 */       return adjustLink((this.basePageURL == null ? "" : this.basePageURL) + link);
/*     */     }
/*     */     
/* 498 */     return "";
/*     */   }
/*     */   
/*     */   public String getName() {
/* 502 */     return this.name;
/*     */   }
/*     */   
/*     */   public int getNbPeers() {
/* 506 */     return this.nbPeers;
/*     */   }
/*     */   
/*     */   public int getNbSeeds() {
/* 510 */     return this.nbSeeds;
/*     */   }
/*     */   
/*     */   public int getNbSuperSeeds() {
/* 514 */     return this.nbSuperSeeds;
/*     */   }
/*     */   
/*     */   public Date getPublishedDate() {
/* 518 */     return this.publishedDate;
/*     */   }
/*     */   
/*     */   public long getSize() {
/* 522 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getComments()
/*     */   {
/* 528 */     return this.comments;
/*     */   }
/*     */   
/*     */   public String getSearchQuery() {
/* 532 */     return this.searchQuery;
/*     */   }
/*     */   
/*     */   public boolean isPrivate() {
/* 536 */     return this.privateTorrent;
/*     */   }
/*     */   
/*     */   public String getDRMKey() {
/* 540 */     return this.drmKey;
/*     */   }
/*     */   
/*     */   public float getAccuracy() {
/* 544 */     return -1.0F;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/WebResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */