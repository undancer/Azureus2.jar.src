/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*     */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
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
/*     */ public class SBC_SubscriptionResult
/*     */   implements SearchSubsResultBase
/*     */ {
/*     */   private final Subscription subs;
/*     */   private final String result_id;
/*     */   private final String name;
/*     */   private final byte[] hash;
/*     */   private final int content_type;
/*     */   private final long size;
/*     */   private final String torrent_link;
/*     */   private final String details_link;
/*     */   private final String category;
/*     */   private long time;
/*     */   private long seeds_peers_sort;
/*     */   private String seeds_peers;
/*     */   private long votes_comments_sort;
/*     */   private String votes_comments;
/*     */   private int rank;
/*     */   private LightHashMap<Object, Object> user_data;
/*     */   
/*     */   protected SBC_SubscriptionResult(Subscription _subs, SubscriptionResult _result)
/*     */   {
/*  63 */     this.subs = _subs;
/*  64 */     this.result_id = _result.getID();
/*     */     
/*  66 */     Map<Integer, Object> properties = _result.toPropertyMap();
/*     */     
/*  68 */     this.name = ((String)properties.get(Integer.valueOf(1)));
/*     */     
/*  70 */     this.hash = ((byte[])properties.get(Integer.valueOf(21)));
/*     */     
/*  72 */     String type = (String)properties.get(Integer.valueOf(10));
/*     */     
/*  74 */     if ((type == null) || (type.length() == 0)) {
/*  75 */       this.content_type = 0;
/*     */     } else {
/*  77 */       char c = type.charAt(0);
/*     */       
/*  79 */       if (c == 'v') {
/*  80 */         this.content_type = 1;
/*  81 */       } else if (c == 'a') {
/*  82 */         this.content_type = 2;
/*  83 */       } else if (c == 'g') {
/*  84 */         this.content_type = 3;
/*     */       } else {
/*  86 */         this.content_type = 0;
/*     */       }
/*     */     }
/*     */     
/*  90 */     this.size = ((Long)properties.get(Integer.valueOf(3))).longValue();
/*     */     
/*  92 */     String tl = (String)properties.get(Integer.valueOf(23));
/*     */     
/*  94 */     if (tl == null)
/*     */     {
/*  96 */       tl = (String)properties.get(Integer.valueOf(12));
/*     */     }
/*     */     
/*  99 */     this.torrent_link = tl;
/*     */     
/* 101 */     this.details_link = ((String)properties.get(Integer.valueOf(11)));
/*     */     
/* 103 */     this.category = ((String)properties.get(Integer.valueOf(7)));
/*     */     
/* 105 */     updateMutables(_result, properties);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateMutables(SubscriptionResult _result, Map<Integer, Object> properties)
/*     */   {
/* 113 */     Date pub_date = (Date)properties.get(Integer.valueOf(2));
/*     */     
/* 115 */     if (pub_date == null)
/*     */     {
/* 117 */       this.time = _result.getTimeFound();
/*     */     }
/*     */     else
/*     */     {
/* 121 */       long pt = pub_date.getTime();
/*     */       
/* 123 */       if (pt <= 0L)
/*     */       {
/* 125 */         this.time = _result.getTimeFound();
/*     */       }
/*     */       else
/*     */       {
/* 129 */         this.time = pt;
/*     */       }
/*     */     }
/*     */     
/* 133 */     long seeds = ((Long)properties.get(Integer.valueOf(5))).longValue();
/* 134 */     long leechers = ((Long)properties.get(Integer.valueOf(4))).longValue();
/*     */     
/* 136 */     this.seeds_peers = ((seeds < 0L ? "--" : String.valueOf(seeds)) + "/" + (leechers < 0L ? "--" : String.valueOf(leechers)));
/*     */     
/* 138 */     if (seeds < 0L) {
/* 139 */       seeds = 0L;
/*     */     } else {
/* 141 */       seeds += 1L;
/*     */     }
/*     */     
/* 144 */     if (leechers < 0L) {
/* 145 */       leechers = 0L;
/*     */     } else {
/* 147 */       leechers += 1L;
/*     */     }
/*     */     
/* 150 */     this.seeds_peers_sort = ((seeds & 0x7FFFFFFF) << 32 | leechers & 0xFFFFFFFFFFFFFFFF);
/*     */     
/* 152 */     long votes = ((Long)properties.get(Integer.valueOf(9))).longValue();
/* 153 */     long comments = ((Long)properties.get(Integer.valueOf(8))).longValue();
/*     */     
/* 155 */     if ((votes < 0L) && (comments < 0L))
/*     */     {
/* 157 */       this.votes_comments_sort = 0L;
/* 158 */       this.votes_comments = null;
/*     */     }
/*     */     else
/*     */     {
/* 162 */       this.votes_comments = ((votes < 0L ? "--" : String.valueOf(votes)) + "/" + (comments < 0L ? "--" : String.valueOf(comments)));
/*     */       
/* 164 */       if (votes < 0L) {
/* 165 */         votes = 0L;
/*     */       } else {
/* 167 */         votes += 1L;
/*     */       }
/* 169 */       if (comments < 0L) {
/* 170 */         comments = 0L;
/*     */       } else {
/* 172 */         comments += 1L;
/*     */       }
/*     */       
/* 175 */       this.votes_comments_sort = ((votes & 0x7FFFFFFF) << 32 | comments & 0xFFFFFFFFFFFFFFFF);
/*     */     }
/*     */     
/* 178 */     this.rank = ((Long)properties.get(Integer.valueOf(17))).intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateFrom(SubscriptionResult other)
/*     */   {
/* 185 */     updateMutables(other, other.toPropertyMap());
/*     */   }
/*     */   
/*     */ 
/*     */   public Subscription getSubscription()
/*     */   {
/* 191 */     return this.subs;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/* 197 */     return this.result_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 203 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getHash()
/*     */   {
/* 209 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getContentType()
/*     */   {
/* 215 */     return this.content_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 221 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSeedsPeers()
/*     */   {
/* 227 */     return this.seeds_peers;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSeedsPeersSortValue()
/*     */   {
/* 233 */     return this.seeds_peers_sort;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVotesComments()
/*     */   {
/* 239 */     return this.votes_comments;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getVotesCommentsSortValue()
/*     */   {
/* 245 */     return this.votes_comments_sort;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRank()
/*     */   {
/* 251 */     return this.rank;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTorrentLink()
/*     */   {
/* 257 */     return this.torrent_link;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDetailsLink()
/*     */   {
/* 263 */     return this.details_link;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCategory()
/*     */   {
/* 269 */     return this.category;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTime()
/*     */   {
/* 275 */     return this.time;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getRead()
/*     */   {
/* 281 */     SubscriptionResult result = this.subs.getHistory().getResult(this.result_id);
/*     */     
/* 283 */     if (result != null)
/*     */     {
/* 285 */       return result.getRead();
/*     */     }
/*     */     
/* 288 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRead(boolean read)
/*     */   {
/* 295 */     SubscriptionResult result = this.subs.getHistory().getResult(this.result_id);
/*     */     
/* 297 */     if (result != null)
/*     */     {
/* 299 */       result.setRead(read);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 306 */     SubscriptionResult result = this.subs.getHistory().getResult(this.result_id);
/*     */     
/* 308 */     if (result != null)
/*     */     {
/* 310 */       result.delete();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUserData(Object key, Object data)
/*     */   {
/* 319 */     synchronized (this) {
/* 320 */       if (this.user_data == null) {
/* 321 */         this.user_data = new LightHashMap();
/*     */       }
/* 323 */       this.user_data.put(key, data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getUserData(Object key)
/*     */   {
/* 331 */     synchronized (this) {
/* 332 */       if (this.user_data == null) {
/* 333 */         return null;
/*     */       }
/* 335 */       return this.user_data.get(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SBC_SubscriptionResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */