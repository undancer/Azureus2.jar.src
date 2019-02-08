/*     */ package org.gudy.azureus2.core3.internat;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.util.Arrays;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocaleUtilDecoderReal
/*     */   implements LocaleUtilDecoder
/*     */ {
/*     */   protected final CharsetDecoder decoder;
/*     */   protected final int index;
/*  42 */   private AEMonitor this_mon = new AEMonitor("LUDR");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LocaleUtilDecoderReal(int _index, CharsetDecoder _decoder)
/*     */   {
/*  49 */     this.index = _index;
/*  50 */     this.decoder = _decoder;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  56 */     return this.decoder.charset().name();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/*  62 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String tryDecode(byte[] array, boolean lax)
/*     */   {
/*     */     try
/*     */     {
/*  71 */       ByteBuffer bb = ByteBuffer.wrap(array);
/*     */       
/*  73 */       CharBuffer cb = CharBuffer.allocate(array.length);
/*     */       
/*     */ 
/*  76 */       this.this_mon.enter();
/*     */       CoderResult cr;
/*  78 */       try { cr = this.decoder.decode(bb, cb, true);
/*     */       } finally {
/*  80 */         this.this_mon.exit();
/*     */       }
/*     */       
/*  83 */       if (!cr.isError())
/*     */       {
/*  85 */         cb.flip();
/*     */         
/*  87 */         String str = cb.toString();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  92 */         if (lax)
/*     */         {
/*  94 */           return str;
/*     */         }
/*     */         
/*  97 */         byte[] b2 = str.getBytes(getName());
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
/* 110 */         if (Arrays.equals(array, b2))
/*     */         {
/* 112 */           return str;
/*     */         }
/*     */       }
/*     */       
/* 116 */       return null;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 123 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String decodeString(byte[] bytes)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 133 */     if (bytes == null)
/*     */     {
/* 135 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 139 */       ByteBuffer bb = ByteBuffer.wrap(bytes);
/*     */       
/* 141 */       CharBuffer cb = CharBuffer.allocate(bytes.length);
/*     */       
/*     */ 
/* 144 */       this.this_mon.enter();
/*     */       CoderResult cr;
/* 146 */       try { cr = this.decoder.decode(bb, cb, true);
/*     */       } finally {
/* 148 */         this.this_mon.exit();
/*     */       }
/*     */       
/* 151 */       if (!cr.isError())
/*     */       {
/* 153 */         cb.flip();
/*     */         
/* 155 */         String str = cb.toString();
/*     */         
/* 157 */         byte[] b2 = str.getBytes(this.decoder.charset().name());
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
/* 170 */         if (Arrays.equals(bytes, b2))
/*     */         {
/* 172 */           return str;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
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
/* 187 */       return new String(bytes, "UTF8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 191 */       Debug.printStackTrace(e);
/*     */     }
/* 193 */     return new String(bytes);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtilDecoderReal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */