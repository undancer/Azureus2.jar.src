/*     */ package com.aelitis.azureus.ui.swt.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*     */ import java.util.Date;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ public class SBC_SearchResult
/*     */   implements SearchSubsResultBase, SBC_SearchResultsView.ImageLoadListener
/*     */ {
/*     */   private final SBC_SearchResultsView view;
/*     */   private final Engine engine;
/*     */   private final Result result;
/*     */   private final int content_type;
/*     */   private final String seeds_peers;
/*     */   private final long seeds_peers_sort;
/*     */   private final long votes_comments_sort;
/*     */   private final String votes_comments;
/*     */   private LightHashMap<Object, Object> user_data;
/*     */   
/*     */   public SBC_SearchResult(SBC_SearchResultsView _view, Engine _engine, Result _result)
/*     */   {
/*  59 */     this.view = _view;
/*  60 */     this.engine = _engine;
/*  61 */     this.result = _result;
/*     */     
/*  63 */     String type = this.result.getContentType();
/*     */     
/*  65 */     if ((type == null) || (type.length() == 0)) {
/*  66 */       this.content_type = 0;
/*     */     } else {
/*  68 */       char c = type.charAt(0);
/*     */       
/*  70 */       if (c == 'v') {
/*  71 */         this.content_type = 1;
/*  72 */       } else if (c == 'a') {
/*  73 */         this.content_type = 2;
/*  74 */       } else if (c == 'g') {
/*  75 */         this.content_type = 3;
/*     */       } else {
/*  77 */         this.content_type = 0;
/*     */       }
/*     */     }
/*     */     
/*  81 */     int seeds = this.result.getNbSeeds();
/*  82 */     int leechers = this.result.getNbPeers();
/*  83 */     int super_seeds = this.result.getNbSuperSeeds();
/*     */     
/*  85 */     if (super_seeds > 0) {
/*  86 */       seeds += super_seeds * 10;
/*     */     }
/*  88 */     this.seeds_peers = ((seeds < 0 ? "--" : String.valueOf(seeds)) + "/" + (leechers < 0 ? "--" : String.valueOf(leechers)));
/*     */     
/*  90 */     if (seeds < 0) {
/*  91 */       seeds = 0;
/*     */     } else {
/*  93 */       seeds++;
/*     */     }
/*     */     
/*  96 */     if (leechers < 0) {
/*  97 */       leechers = 0;
/*     */     } else {
/*  99 */       leechers++;
/*     */     }
/*     */     
/* 102 */     this.seeds_peers_sort = ((seeds & 0x7FFFFFFF) << 32 | leechers & 0xFFFFFFFF);
/*     */     
/* 104 */     long votes = this.result.getVotes();
/* 105 */     long comments = this.result.getComments();
/*     */     
/* 107 */     if ((votes < 0L) && (comments < 0L))
/*     */     {
/* 109 */       this.votes_comments_sort = 0L;
/* 110 */       this.votes_comments = null;
/*     */     }
/*     */     else
/*     */     {
/* 114 */       this.votes_comments = ((votes < 0L ? "--" : String.valueOf(votes)) + "/" + (comments < 0L ? "--" : String.valueOf(comments)));
/*     */       
/* 116 */       if (votes < 0L) {
/* 117 */         votes = 0L;
/*     */       } else {
/* 119 */         votes += 1L;
/*     */       }
/* 121 */       if (comments < 0L) {
/* 122 */         comments = 0L;
/*     */       } else {
/* 124 */         comments += 1L;
/*     */       }
/*     */       
/* 127 */       this.votes_comments_sort = ((votes & 0x7FFFFFFF) << 32 | comments & 0xFFFFFFFFFFFFFFFF);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Engine getEngine()
/*     */   {
/* 134 */     return this.engine;
/*     */   }
/*     */   
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 140 */     return this.result.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getHash()
/*     */   {
/* 146 */     String base32_hash = this.result.getHash();
/*     */     
/* 148 */     if (base32_hash != null)
/*     */     {
/* 150 */       return Base32.decode(base32_hash);
/*     */     }
/*     */     
/* 153 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getContentType()
/*     */   {
/* 159 */     return this.content_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 165 */     return this.result.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSeedsPeers()
/*     */   {
/* 171 */     return this.seeds_peers;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSeedsPeersSortValue()
/*     */   {
/* 177 */     return this.seeds_peers_sort;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVotesComments()
/*     */   {
/* 183 */     return this.votes_comments;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getVotesCommentsSortValue()
/*     */   {
/* 189 */     return this.votes_comments_sort;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRank()
/*     */   {
/* 195 */     float rank = this.result.getRank();
/*     */     
/* 197 */     return (int)(rank * 100.0F);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTorrentLink()
/*     */   {
/* 203 */     String r = this.result.getTorrentLink();
/*     */     
/* 205 */     if (r == null)
/*     */     {
/* 207 */       r = this.result.getDownloadLink();
/*     */     }
/*     */     
/* 210 */     return r;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDetailsLink()
/*     */   {
/* 216 */     return this.result.getCDPLink();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCategory()
/*     */   {
/* 222 */     return this.result.getCategory();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTime()
/*     */   {
/* 228 */     Date date = this.result.getPublishedDate();
/*     */     
/* 230 */     if (date != null)
/*     */     {
/* 232 */       return date.getTime();
/*     */     }
/*     */     
/* 235 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public Image getIcon()
/*     */   {
/* 241 */     return this.view.getIcon(this.engine, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getRead()
/*     */   {
/* 247 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRead(boolean read) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void imageLoaded(Image image)
/*     */   {
/* 260 */     this.view.invalidate(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUserData(Object key, Object data)
/*     */   {
/* 268 */     synchronized (this) {
/* 269 */       if (this.user_data == null) {
/* 270 */         this.user_data = new LightHashMap();
/*     */       }
/* 272 */       this.user_data.put(key, data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 280 */     synchronized (this) {
/* 281 */       if (this.user_data == null) {
/* 282 */         return null;
/*     */       }
/* 284 */       return this.user_data.get(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SBC_SearchResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */