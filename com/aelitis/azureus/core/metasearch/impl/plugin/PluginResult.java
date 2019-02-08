/*     */ package com.aelitis.azureus.core.metasearch.impl.plugin;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Result;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PluginResult
/*     */   extends Result
/*     */ {
/*  37 */   private static final Object NULL_OBJECT = PluginResult.class;
/*     */   
/*     */   private SearchResult result;
/*     */   
/*     */   private String search_term;
/*  42 */   private Map property_cache = new LightHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PluginResult(PluginEngine _engine, SearchResult _result, String _search_term)
/*     */   {
/*  50 */     super(_engine);
/*     */     
/*  52 */     this.result = _result;
/*  53 */     this.search_term = _search_term;
/*     */   }
/*     */   
/*     */ 
/*     */   public Date getPublishedDate()
/*     */   {
/*  59 */     return (Date)getResultProperty(2);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCategory()
/*     */   {
/*  65 */     return getStringProperty(7);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCategory(String category) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String getContentType()
/*     */   {
/*  77 */     String ct = getStringProperty(10);
/*     */     
/*  79 */     if ((ct == null) || (ct.length() == 0))
/*     */     {
/*  81 */       ct = guessContentTypeFromCategory(getCategory());
/*     */     }
/*     */     
/*  84 */     return ct;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setContentType(String contentType) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  96 */     return getStringProperty(1);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 102 */     return getLongProperty(3);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNbPeers()
/*     */   {
/* 108 */     return getIntProperty(4);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNbSeeds()
/*     */   {
/* 114 */     return getIntProperty(5);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNbSuperSeeds()
/*     */   {
/* 120 */     return getIntProperty(6);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getComments()
/*     */   {
/* 126 */     return getIntProperty(8);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVotes()
/*     */   {
/* 132 */     return getIntProperty(9);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVotesDown()
/*     */   {
/* 138 */     return getIntProperty(19);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPrivate()
/*     */   {
/* 144 */     return getBooleanProperty(14);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDRMKey()
/*     */   {
/* 151 */     return getStringProperty(15);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDownloadLink()
/*     */   {
/* 157 */     return adjustLink(getStringProperty(12));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDownloadButtonLink()
/*     */   {
/* 163 */     return adjustLink(getStringProperty(16));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCDPLink()
/*     */   {
/* 169 */     return adjustLink(getStringProperty(11));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPlayLink()
/*     */   {
/* 176 */     return adjustLink(getStringProperty(13));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTorrentLink()
/*     */   {
/* 182 */     return adjustLink(getStringProperty(23));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUID()
/*     */   {
/* 188 */     return getStringProperty(20);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHash()
/*     */   {
/* 194 */     byte[] hash = getByteArrayProperty(21);
/*     */     
/* 196 */     if (hash == null)
/*     */     {
/* 198 */       return null;
/*     */     }
/*     */     
/* 201 */     return Base32.encode(hash);
/*     */   }
/*     */   
/*     */ 
/*     */   public float getRank()
/*     */   {
/* 207 */     if (((PluginEngine)getEngine()).useAccuracyForRank())
/*     */     {
/* 209 */       return applyRankBias(getAccuracy());
/*     */     }
/*     */     
/* 212 */     long l_rank = getLongProperty(17);
/*     */     
/*     */ 
/*     */ 
/* 216 */     if ((getLongProperty(5) >= 0L) && (getLongProperty(4) >= 0L))
/*     */     {
/* 218 */       l_rank = Long.MIN_VALUE;
/*     */     }
/*     */     
/* 221 */     if (l_rank == Long.MIN_VALUE)
/*     */     {
/* 223 */       return super.getRank();
/*     */     }
/*     */     
/* 226 */     float rank = (float)l_rank;
/*     */     
/* 228 */     if (rank > 100.0F)
/*     */     {
/* 230 */       rank = 100.0F;
/*     */     }
/* 232 */     else if (rank < 0.0F)
/*     */     {
/* 234 */       rank = 0.0F;
/*     */     }
/*     */     
/* 237 */     return applyRankBias(rank / 100.0F);
/*     */   }
/*     */   
/*     */ 
/*     */   public float getAccuracy()
/*     */   {
/* 243 */     long l_accuracy = getLongProperty(18);
/*     */     
/* 245 */     if (l_accuracy == Long.MIN_VALUE)
/*     */     {
/* 247 */       return -1.0F;
/*     */     }
/*     */     
/* 250 */     float accuracy = (float)l_accuracy;
/*     */     
/* 252 */     if (accuracy > 100.0F)
/*     */     {
/* 254 */       accuracy = 100.0F;
/*     */     }
/* 256 */     else if (accuracy < 0.0F)
/*     */     {
/* 258 */       accuracy = 0.0F;
/*     */     }
/*     */     
/* 261 */     return accuracy / 100.0F;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSearchQuery()
/*     */   {
/* 267 */     return this.search_term;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int getIntProperty(int name)
/*     */   {
/* 274 */     return (int)getLongProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getLongProperty(int name)
/*     */   {
/* 281 */     return getLongProperty(name, Long.MIN_VALUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getLongProperty(int name, long def)
/*     */   {
/*     */     try
/*     */     {
/* 290 */       Long l = (Long)getResultProperty(name);
/*     */       
/* 292 */       if (l == null)
/*     */       {
/* 294 */         return def;
/*     */       }
/*     */       
/* 297 */       return l.longValue();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 301 */       Debug.out("Invalid value returned for Long property " + name);
/*     */     }
/* 303 */     return def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean getBooleanProperty(int name)
/*     */   {
/* 311 */     return getBooleanProperty(name, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean getBooleanProperty(int name, boolean def)
/*     */   {
/*     */     try
/*     */     {
/* 320 */       Boolean b = (Boolean)getResultProperty(name);
/*     */       
/* 322 */       if (b == null)
/*     */       {
/* 324 */         return def;
/*     */       }
/*     */       
/* 327 */       return b.booleanValue();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 331 */       Debug.out("Invalid value returned for Boolean property " + name);
/*     */     }
/* 333 */     return def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getStringProperty(int name)
/*     */   {
/* 341 */     return getStringProperty(name, "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getStringProperty(int name, String def)
/*     */   {
/*     */     try
/*     */     {
/* 350 */       String l = (String)getResultProperty(name);
/*     */       
/* 352 */       if (l == null)
/*     */       {
/* 354 */         return def;
/*     */       }
/*     */       
/* 357 */       return unescapeEntities(removeHTMLTags(l));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 361 */       Debug.out("Invalid value returned for String property " + name);
/*     */     }
/* 363 */     return def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected byte[] getByteArrayProperty(int name)
/*     */   {
/*     */     try
/*     */     {
/* 372 */       return (byte[])getResultProperty(name);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 376 */       Debug.out("Invalid value returned for byte[] property " + name);
/*     */     }
/* 378 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized Object getResultProperty(int prop)
/*     */   {
/* 386 */     Integer i_prop = new Integer(prop);
/*     */     
/* 388 */     Object res = this.property_cache.get(i_prop);
/*     */     
/* 390 */     if (res == null)
/*     */     {
/* 392 */       res = this.result.getProperty(prop);
/*     */       
/* 394 */       if (res == null)
/*     */       {
/* 396 */         res = NULL_OBJECT;
/*     */       }
/*     */       
/* 399 */       this.property_cache.put(i_prop, res);
/*     */     }
/*     */     
/* 402 */     if (res == NULL_OBJECT)
/*     */     {
/* 404 */       return null;
/*     */     }
/*     */     
/* 407 */     return res;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/plugin/PluginResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */