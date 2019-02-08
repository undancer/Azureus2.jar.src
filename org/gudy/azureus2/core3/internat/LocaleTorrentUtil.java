/*     */ package org.gudy.azureus2.core3.internat;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class LocaleTorrentUtil
/*     */ {
/*  38 */   private static final List listeners = new ArrayList();
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
/*     */   public static LocaleUtilDecoder getTorrentEncodingIfAvailable(TOTorrent torrent)
/*     */     throws TOTorrentException, UnsupportedEncodingException
/*     */   {
/*  56 */     String encoding = torrent.getAdditionalStringProperty("encoding");
/*     */     
/*  58 */     if (encoding == null) {
/*  59 */       return null;
/*     */     }
/*  61 */     if ("utf8 keys".equals(encoding)) {
/*  62 */       encoding = "utf8";
/*     */     }
/*     */     
/*     */ 
/*     */     String canonical_name;
/*     */     
/*     */     try
/*     */     {
/*  70 */       canonical_name = Charset.forName(encoding).name();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  74 */       canonical_name = encoding;
/*     */     }
/*     */     
/*  77 */     LocaleUtilDecoder chosenDecoder = null;
/*  78 */     LocaleUtilDecoder[] all_decoders = LocaleUtil.getSingleton().getDecoders();
/*     */     
/*  80 */     for (int i = 0; i < all_decoders.length; i++) {
/*  81 */       if (all_decoders[i].getName().equals(canonical_name)) {
/*  82 */         chosenDecoder = all_decoders[i];
/*  83 */         break;
/*     */       }
/*     */     }
/*     */     
/*  87 */     return chosenDecoder;
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
/*     */   public static LocaleUtilDecoder getTorrentEncoding(TOTorrent torrent)
/*     */     throws TOTorrentException, UnsupportedEncodingException
/*     */   {
/* 108 */     return getTorrentEncoding(torrent, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public static LocaleUtilDecoder getTorrentEncoding(TOTorrent torrent, boolean saveToFileAllowed)
/*     */     throws TOTorrentException, UnsupportedEncodingException
/*     */   {
/* 115 */     String encoding = torrent.getAdditionalStringProperty("encoding");
/* 116 */     if ("utf8 keys".equals(encoding)) {
/* 117 */       encoding = "utf8";
/*     */     }
/*     */     
/*     */ 
/*     */     boolean bSaveToFile;
/*     */     
/*     */     try
/*     */     {
/* 125 */       TorrentUtils.getTorrentFileName(torrent);
/*     */       
/* 127 */       bSaveToFile = true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 131 */       bSaveToFile = false;
/*     */     }
/*     */     
/* 134 */     if (encoding != null)
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 139 */         LocaleUtilDecoder[] all_decoders = LocaleUtil.getSingleton().getDecoders();
/* 140 */         LocaleUtilDecoder fallback_decoder = LocaleUtil.getSingleton().getFallBackDecoder();
/*     */         
/* 142 */         String canonical_name = encoding.equals(fallback_decoder.getName()) ? encoding : Charset.forName(encoding).name();
/*     */         
/*     */ 
/* 145 */         for (int i = 0; i < all_decoders.length; i++)
/*     */         {
/* 147 */           if (all_decoders[i].getName().equals(canonical_name))
/*     */           {
/* 149 */             return all_decoders[i];
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 154 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 161 */     LocaleUtilDecoderCandidate[] candidates = getTorrentCandidates(torrent);
/*     */     
/* 163 */     boolean system_decoder_is_valid = false;
/*     */     
/* 165 */     LocaleUtil localeUtil = LocaleUtil.getSingleton();
/* 166 */     LocaleUtilDecoder system_decoder = localeUtil.getSystemDecoder();
/* 167 */     for (int i = 0; i < candidates.length; i++) {
/* 168 */       if (candidates[i].getDecoder() == system_decoder) {
/* 169 */         system_decoder_is_valid = true;
/* 170 */         break;
/*     */       }
/*     */     }
/*     */     
/* 174 */     LocaleUtilDecoder selected_decoder = null;
/*     */     
/*     */ 
/*     */ 
/* 178 */     for (int i = 0; i < listeners.size(); i++) {
/*     */       try
/*     */       {
/* 181 */         LocaleUtilDecoderCandidate candidate = ((LocaleUtilListener)listeners.get(i)).selectDecoder(localeUtil, torrent, candidates);
/*     */         
/* 183 */         if (candidate != null)
/*     */         {
/* 185 */           selected_decoder = candidate.getDecoder();
/*     */           
/* 187 */           break;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 193 */     if (selected_decoder == null)
/*     */     {
/*     */ 
/*     */ 
/* 197 */       bSaveToFile = false;
/*     */       
/*     */ 
/*     */ 
/* 201 */       int min_length = Integer.MAX_VALUE;
/* 202 */       int utf8_length = Integer.MAX_VALUE;
/*     */       
/* 204 */       LocaleUtilDecoderCandidate utf8_decoder = null;
/*     */       
/* 206 */       for (LocaleUtilDecoderCandidate candidate : candidates)
/*     */       {
/* 208 */         String val = candidate.getValue();
/*     */         
/* 210 */         if (val != null)
/*     */         {
/* 212 */           int len = val.length();
/*     */           
/* 214 */           if (len < min_length)
/*     */           {
/* 216 */             min_length = len;
/*     */           }
/*     */           
/* 219 */           String name = candidate.getDecoder().getName().toUpperCase(Locale.US);
/*     */           
/* 221 */           if ((name.equals("UTF-8")) || (name.equals("UTF8")))
/*     */           {
/* 223 */             utf8_length = len;
/* 224 */             utf8_decoder = candidate;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 229 */       if ((utf8_decoder != null) && (utf8_length == min_length))
/*     */       {
/* 231 */         selected_decoder = utf8_decoder.getDecoder();
/*     */       }
/* 233 */       else if (system_decoder_is_valid)
/*     */       {
/*     */ 
/*     */ 
/* 237 */         selected_decoder = localeUtil.getSystemDecoder();
/*     */       }
/*     */       else
/*     */       {
/* 241 */         selected_decoder = localeUtil.getFallBackDecoder();
/*     */       }
/*     */     }
/*     */     
/* 245 */     torrent.setAdditionalStringProperty("encoding", selected_decoder.getName());
/*     */     
/* 247 */     if ((bSaveToFile) && (saveToFileAllowed)) {
/* 248 */       TorrentUtils.writeToFile(torrent);
/*     */     }
/*     */     
/* 251 */     return selected_decoder;
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
/*     */   protected static LocaleUtilDecoderCandidate[] getTorrentCandidates(TOTorrent torrent)
/*     */     throws TOTorrentException, UnsupportedEncodingException
/*     */   {
/* 271 */     Set cand_set = new HashSet();
/* 272 */     LocaleUtil localeUtil = LocaleUtil.getSingleton();
/*     */     
/* 274 */     List candidateDecoders = localeUtil.getCandidateDecoders(torrent.getName());
/* 275 */     long lMinCandidates = candidateDecoders.size();
/* 276 */     byte[] minCandidatesArray = torrent.getName();
/*     */     
/* 278 */     cand_set.addAll(candidateDecoders);
/*     */     
/* 280 */     TOTorrentFile[] files = torrent.getFiles();
/*     */     
/* 282 */     for (int i = 0; i < files.length; i++)
/*     */     {
/* 284 */       TOTorrentFile file = files[i];
/*     */       
/* 286 */       byte[][] comps = file.getPathComponents();
/*     */       
/* 288 */       for (int j = 0; j < comps.length; j++) {
/* 289 */         candidateDecoders = localeUtil.getCandidateDecoders(comps[j]);
/* 290 */         if (candidateDecoders.size() < lMinCandidates) {
/* 291 */           lMinCandidates = candidateDecoders.size();
/* 292 */           minCandidatesArray = comps[j];
/*     */         }
/* 294 */         cand_set.retainAll(candidateDecoders);
/*     */       }
/*     */     }
/*     */     
/* 298 */     byte[] comment = torrent.getComment();
/*     */     
/* 300 */     if (comment != null) {
/* 301 */       candidateDecoders = localeUtil.getCandidateDecoders(comment);
/* 302 */       if (candidateDecoders.size() < lMinCandidates) {
/* 303 */         lMinCandidates = candidateDecoders.size();
/* 304 */         minCandidatesArray = comment;
/*     */       }
/* 306 */       cand_set.retainAll(candidateDecoders);
/*     */     }
/*     */     
/* 309 */     byte[] created = torrent.getCreatedBy();
/*     */     
/* 311 */     if (created != null) {
/* 312 */       candidateDecoders = localeUtil.getCandidateDecoders(created);
/* 313 */       if (candidateDecoders.size() < lMinCandidates) {
/* 314 */         lMinCandidates = candidateDecoders.size();
/* 315 */         minCandidatesArray = created;
/*     */       }
/* 317 */       cand_set.retainAll(candidateDecoders);
/*     */     }
/*     */     
/* 320 */     List candidatesList = localeUtil.getCandidatesAsList(minCandidatesArray);
/*     */     
/* 322 */     LocaleUtilDecoderCandidate[] candidates = new LocaleUtilDecoderCandidate[candidatesList.size()];
/* 323 */     candidatesList.toArray(candidates);
/*     */     
/* 325 */     Arrays.sort(candidates, new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 327 */         LocaleUtilDecoderCandidate luc1 = (LocaleUtilDecoderCandidate)o1;
/* 328 */         LocaleUtilDecoderCandidate luc2 = (LocaleUtilDecoderCandidate)o2;
/*     */         
/* 330 */         LocaleUtilDecoder dec1 = luc1.getDecoder();
/* 331 */         LocaleUtilDecoder dec2 = luc2.getDecoder();
/*     */         
/* 333 */         int res = dec1.getIndex() - dec2.getIndex();
/*     */         
/* 335 */         if (res == 0)
/*     */         {
/* 337 */           return 0;
/*     */         }
/*     */         
/* 340 */         String n1 = dec1.getName();
/* 341 */         String n2 = dec2.getName();
/*     */         
/* 343 */         if (n1.equals("UTF-8"))
/*     */         {
/* 345 */           return -1;
/*     */         }
/* 347 */         if (n2.equals("UTF-8"))
/*     */         {
/* 349 */           return 1;
/*     */         }
/*     */         
/*     */ 
/* 353 */         return res;
/*     */       }
/*     */       
/*     */ 
/* 357 */     });
/* 358 */     return candidates;
/*     */   }
/*     */   
/*     */   public static void setTorrentEncoding(TOTorrent torrent, String encoding)
/*     */     throws LocaleUtilEncodingException
/*     */   {
/*     */     try
/*     */     {
/* 366 */       LocaleUtil localeUtil = LocaleUtil.getSingleton();
/* 367 */       LocaleUtilDecoderCandidate[] candidates = getTorrentCandidates(torrent);
/*     */       
/*     */       String canonical_requested_name;
/*     */       
/*     */       String canonical_requested_name;
/*     */       
/* 373 */       if (encoding.equalsIgnoreCase("system"))
/*     */       {
/* 375 */         canonical_requested_name = localeUtil.getSystemEncoding();
/*     */       } else { String canonical_requested_name;
/* 377 */         if (encoding.equalsIgnoreCase("Fallback"))
/*     */         {
/* 379 */           canonical_requested_name = "Fallback";
/*     */         }
/*     */         else
/*     */         {
/* 383 */           CharsetDecoder requested_decoder = Charset.forName(encoding).newDecoder();
/*     */           
/* 385 */           canonical_requested_name = requested_decoder.charset().name();
/*     */         }
/*     */       }
/* 388 */       boolean ok = false;
/*     */       
/* 390 */       for (int i = 0; i < candidates.length; i++)
/*     */       {
/* 392 */         if (candidates[i].getDecoder().getName().equals(canonical_requested_name))
/*     */         {
/*     */ 
/* 395 */           ok = true;
/*     */           
/* 397 */           break;
/*     */         }
/*     */       }
/*     */       
/* 401 */       if (!ok)
/*     */       {
/* 403 */         String[] charsets = new String[candidates.length];
/* 404 */         String[] names = new String[candidates.length];
/*     */         
/* 406 */         for (int i = 0; i < candidates.length; i++)
/*     */         {
/* 408 */           LocaleUtilDecoder decoder = candidates[i].getDecoder();
/*     */           
/* 410 */           charsets[i] = decoder.getName();
/* 411 */           names[i] = decoder.decodeString(torrent.getName());
/*     */         }
/*     */         
/* 414 */         throw new LocaleUtilEncodingException(charsets, names);
/*     */       }
/*     */       
/* 417 */       torrent.setAdditionalStringProperty("encoding", canonical_requested_name);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 421 */       if ((e instanceof LocaleUtilEncodingException))
/*     */       {
/* 423 */         throw ((LocaleUtilEncodingException)e);
/*     */       }
/*     */       
/* 426 */       throw new LocaleUtilEncodingException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setDefaultTorrentEncoding(TOTorrent torrent)
/*     */     throws LocaleUtilEncodingException
/*     */   {
/* 434 */     setTorrentEncoding(torrent, "UTF8");
/*     */   }
/*     */   
/*     */   public static String getCurrentTorrentEncoding(TOTorrent torrent) {
/* 438 */     return torrent.getAdditionalStringProperty("encoding");
/*     */   }
/*     */   
/*     */   public static void addListener(LocaleUtilListener l) {
/* 442 */     listeners.add(l);
/*     */   }
/*     */   
/*     */   public static void removeListener(LocaleUtilListener l) {
/* 446 */     listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleTorrentUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */