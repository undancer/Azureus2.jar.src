/*    */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.plugins.utils.LocaleDecoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocaleDecoderImpl
/*    */   implements LocaleDecoder
/*    */ {
/*    */   LocaleUtilDecoder decoder;
/*    */   
/*    */   protected LocaleDecoderImpl(LocaleUtilDecoder _decoder)
/*    */   {
/* 47 */     this.decoder = _decoder;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 53 */     return this.decoder.getName();
/*    */   }
/*    */   
/*    */ 
/*    */   public String decode(byte[] encoded_bytes)
/*    */   {
/*    */     try
/*    */     {
/* 61 */       return this.decoder.decodeString(encoded_bytes);
/*    */     }
/*    */     catch (UnsupportedEncodingException e)
/*    */     {
/* 65 */       Debug.printStackTrace(e);
/*    */     }
/* 67 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/LocaleDecoderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */