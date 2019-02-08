/*     */ package org.gudy.azureus2.core3.internat;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
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
/*     */ public class LocaleUtilDecoderFallback
/*     */   implements LocaleUtilDecoder
/*     */ {
/*     */   public static final String NAME = "Fallback";
/*  37 */   private static volatile int max_ok_name_length = 64;
/*     */   
/*     */ 
/*     */   private static volatile boolean max_ok_name_length_determined;
/*     */   
/*     */ 
/*     */   private static final String VALID_CHARS = "abcdefghijklmnopqrstuvwxyz1234567890_-.";
/*     */   
/*     */ 
/*     */   private final int index;
/*     */   
/*     */ 
/*     */ 
/*     */   protected LocaleUtilDecoderFallback(int _index)
/*     */   {
/*  52 */     this.index = _index;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  58 */     return "Fallback";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/*  64 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String tryDecode(byte[] bytes, boolean lax)
/*     */   {
/*  72 */     return decode(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String decodeString(byte[] bytes)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  81 */     return decode(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String decode(byte[] data)
/*     */   {
/*  88 */     if (data == null)
/*     */     {
/*  90 */       return null;
/*     */     }
/*     */     
/*  93 */     StringBuffer res = new StringBuffer(data.length * 2);
/*     */     
/*  95 */     for (int i = 0; i < data.length; i++)
/*     */     {
/*  97 */       byte c = data[i];
/*     */       
/*  99 */       if ("abcdefghijklmnopqrstuvwxyz1234567890_-.".indexOf(Character.toLowerCase((char)c)) != -1)
/*     */       {
/* 101 */         res.append((char)c);
/*     */       }
/*     */       else
/*     */       {
/* 105 */         res.append("_");
/* 106 */         res.append(ByteFormatter.nicePrint(c));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 113 */     int len = res.length();
/*     */     
/* 115 */     if (len > max_ok_name_length)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 120 */       if ((!max_ok_name_length_determined) && (fileLengthOK(len)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 125 */         max_ok_name_length = len;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 131 */         if (!max_ok_name_length_determined)
/*     */         {
/* 133 */           for (int i = max_ok_name_length + 16; i < len; i += 16)
/*     */           {
/* 135 */             if (!fileLengthOK(i))
/*     */               break;
/* 137 */             max_ok_name_length = i;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 145 */           max_ok_name_length_determined = true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 150 */         String extension = null;
/*     */         
/* 152 */         int pos = res.lastIndexOf(".");
/*     */         
/* 154 */         if (pos != -1)
/*     */         {
/*     */ 
/*     */ 
/* 158 */           extension = res.substring(pos);
/*     */           
/* 160 */           if ((extension.length() == 1) || (extension.length() > 4))
/*     */           {
/* 162 */             extension = null;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 167 */         byte[] hash = new SHA1Hasher().calculateHash(data);
/*     */         
/* 169 */         String hash_str = ByteFormatter.nicePrint(hash, true);
/*     */         
/* 171 */         res = new StringBuffer(res.substring(0, max_ok_name_length - hash_str.length() - (extension == null ? 0 : extension.length())));
/*     */         
/*     */ 
/*     */ 
/* 175 */         res.append(hash_str);
/*     */         
/* 177 */         if (extension != null)
/*     */         {
/* 179 */           res.append(extension);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 184 */     return res.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean fileLengthOK(int len)
/*     */   {
/* 191 */     StringBuilder n = new StringBuilder(len);
/*     */     
/* 193 */     for (int i = 0; i < len; i++)
/*     */     {
/* 195 */       n.append("A");
/*     */     }
/*     */     try
/*     */     {
/* 199 */       File f = File.createTempFile(n.toString(), "");
/*     */       
/* 201 */       f.delete();
/*     */       
/* 203 */       return true;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 207 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtilDecoderFallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */