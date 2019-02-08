/*     */ package org.gudy.azureus2.core3.internat;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class LocaleUtil
/*     */ {
/*  32 */   private static final String systemEncoding = System.getProperty("file.encoding");
/*     */   
/*  34 */   private static final String[] manual_charset = { systemEncoding, "Big5", "EUC-JP", "EUC-KR", "GB18030", "GB2312", "GBK", "ISO-2022-JP", "ISO-2022-KR", "Shift_JIS", "KOI8-R", "TIS-620", "UTF8", "windows-1251", "ISO-8859-1" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  44 */   protected static final String[] generalCharsets = { "ISO-8859-1", "UTF8", systemEncoding };
/*     */   
/*     */ 
/*     */ 
/*  48 */   private static final LocaleUtil singleton = new LocaleUtil();
/*     */   private final LocaleUtilDecoder[] all_decoders;
/*     */   private final LocaleUtilDecoder[] general_decoders;
/*     */   
/*     */   public static LocaleUtil getSingleton() {
/*  53 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private LocaleUtilDecoder system_decoder;
/*     */   
/*     */ 
/*     */   private final LocaleUtilDecoder fallback_decoder;
/*     */   
/*     */ 
/*     */   private LocaleUtil()
/*     */   {
/*  66 */     List decoders = new ArrayList();
/*  67 */     List decoder_names = new ArrayList();
/*     */     
/*  69 */     for (int i = 0; i < manual_charset.length; i++) {
/*     */       try {
/*  71 */         String name = manual_charset[i];
/*     */         
/*  73 */         CharsetDecoder decoder = Charset.forName(name).newDecoder();
/*     */         
/*  75 */         if (decoder != null)
/*     */         {
/*  77 */           LocaleUtilDecoder lu_decoder = new LocaleUtilDecoderReal(decoders.size(), decoder);
/*     */           
/*  79 */           decoder_names.add(lu_decoder.getName());
/*     */           
/*  81 */           if (i == 0)
/*     */           {
/*  83 */             this.system_decoder = lu_decoder;
/*     */           }
/*     */           
/*  86 */           decoders.add(lu_decoder);
/*     */         }
/*  88 */         else if (i == 0)
/*     */         {
/*  90 */           Debug.out("System decoder failed to be found!!!!");
/*     */         }
/*     */       }
/*     */       catch (Exception ignore) {}
/*     */     }
/*     */     
/*     */ 
/*  97 */     this.general_decoders = new LocaleUtilDecoder[generalCharsets.length];
/*     */     
/*  99 */     for (int i = 0; i < this.general_decoders.length; i++)
/*     */     {
/* 101 */       int gi = decoder_names.indexOf(generalCharsets[i]);
/*     */       
/* 103 */       if (gi != -1)
/*     */       {
/* 105 */         this.general_decoders[i] = ((LocaleUtilDecoder)decoders.get(gi));
/*     */       }
/*     */     }
/*     */     
/* 109 */     boolean show_all = COConfigurationManager.getBooleanParameter("File.Decoder.ShowAll");
/*     */     
/* 111 */     if (show_all)
/*     */     {
/* 113 */       Map m = Charset.availableCharsets();
/*     */       
/* 115 */       Iterator it = m.keySet().iterator();
/*     */       
/* 117 */       while (it.hasNext())
/*     */       {
/* 119 */         String charset_name = (String)it.next();
/*     */         
/* 121 */         if (!decoder_names.contains(charset_name)) {
/*     */           try
/*     */           {
/* 124 */             CharsetDecoder decoder = Charset.forName(charset_name).newDecoder();
/*     */             
/* 126 */             if (decoder != null)
/*     */             {
/* 128 */               LocaleUtilDecoder lu_decoder = new LocaleUtilDecoderReal(decoders.size(), decoder);
/*     */               
/* 130 */               decoders.add(lu_decoder);
/*     */               
/* 132 */               decoder_names.add(lu_decoder.getName());
/*     */             }
/*     */           }
/*     */           catch (Exception ignore) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 141 */     this.fallback_decoder = new LocaleUtilDecoderFallback(decoders.size());
/*     */     
/* 143 */     decoders.add(this.fallback_decoder);
/*     */     
/* 145 */     this.all_decoders = new LocaleUtilDecoder[decoders.size()];
/*     */     
/* 147 */     decoders.toArray(this.all_decoders);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSystemEncoding()
/*     */   {
/* 153 */     return systemEncoding;
/*     */   }
/*     */   
/*     */ 
/*     */   public LocaleUtilDecoder[] getDecoders()
/*     */   {
/* 159 */     return this.all_decoders;
/*     */   }
/*     */   
/*     */ 
/*     */   public LocaleUtilDecoder[] getGeneralDecoders()
/*     */   {
/* 165 */     return this.general_decoders;
/*     */   }
/*     */   
/*     */   public LocaleUtilDecoder getFallBackDecoder() {
/* 169 */     return this.fallback_decoder;
/*     */   }
/*     */   
/*     */ 
/*     */   public LocaleUtilDecoder getSystemDecoder()
/*     */   {
/* 175 */     return this.system_decoder;
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
/*     */   protected LocaleUtilDecoderCandidate[] getCandidates(byte[] array)
/*     */   {
/* 189 */     LocaleUtilDecoderCandidate[] candidates = new LocaleUtilDecoderCandidate[this.all_decoders.length];
/*     */     
/* 191 */     boolean show_less_likely_conversions = COConfigurationManager.getBooleanParameter("File.Decoder.ShowLax");
/*     */     
/* 193 */     for (int i = 0; i < this.all_decoders.length; i++)
/*     */     {
/* 195 */       candidates[i] = new LocaleUtilDecoderCandidate(i);
/*     */       try
/*     */       {
/* 198 */         LocaleUtilDecoder decoder = this.all_decoders[i];
/*     */         
/* 200 */         String str = decoder.tryDecode(array, show_less_likely_conversions);
/*     */         
/* 202 */         if (str != null)
/*     */         {
/* 204 */           candidates[i].setDetails(decoder, str);
/*     */         }
/*     */       }
/*     */       catch (Exception ignore) {}
/*     */     }
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
/* 230 */     return candidates;
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
/*     */   protected List getCandidateDecoders(byte[] array)
/*     */   {
/* 244 */     LocaleUtilDecoderCandidate[] candidates = getCandidates(array);
/*     */     
/* 246 */     List decoders = new ArrayList();
/*     */     
/* 248 */     for (int i = 0; i < candidates.length; i++)
/*     */     {
/* 250 */       LocaleUtilDecoder d = candidates[i].getDecoder();
/*     */       
/* 252 */       if (d != null) {
/* 253 */         decoders.add(d);
/*     */       }
/*     */     }
/* 256 */     return decoders;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List getCandidatesAsList(byte[] array)
/*     */   {
/* 265 */     LocaleUtilDecoderCandidate[] candidates = getCandidates(array);
/*     */     
/* 267 */     List candidatesList = new ArrayList();
/*     */     
/* 269 */     for (int i = 0; i < candidates.length; i++) {
/* 270 */       if (candidates[i].getDecoder() != null) {
/* 271 */         candidatesList.add(candidates[i]);
/*     */       }
/*     */     }
/* 274 */     return candidatesList;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */