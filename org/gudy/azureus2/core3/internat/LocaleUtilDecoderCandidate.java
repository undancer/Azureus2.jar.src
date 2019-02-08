/*    */ package org.gudy.azureus2.core3.internat;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocaleUtilDecoderCandidate
/*    */   implements Comparable
/*    */ {
/*    */   private final int index;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String value;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private LocaleUtilDecoder decoder;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected LocaleUtilDecoderCandidate(int _index)
/*    */   {
/* 35 */     this.index = _index;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 39 */     return this.value;
/*    */   }
/*    */   
/*    */   public LocaleUtilDecoder getDecoder() {
/* 43 */     return this.decoder;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setDetails(LocaleUtilDecoder _decoder, String _value)
/*    */   {
/* 51 */     this.decoder = _decoder;
/* 52 */     this.value = _value;
/*    */   }
/*    */   
/*    */ 
/*    */   public int compareTo(Object o)
/*    */   {
/* 58 */     LocaleUtilDecoderCandidate candidate = (LocaleUtilDecoderCandidate)o;
/*    */     
/*    */     int res;
/*    */     int res;
/* 62 */     if ((this.value == null) && (candidate.value == null))
/*    */     {
/* 64 */       res = 0;
/*    */     } else { int res;
/* 66 */       if (this.value == null)
/*    */       {
/* 68 */         res = 1;
/*    */       } else { int res;
/* 70 */         if (candidate.value == null)
/*    */         {
/* 72 */           res = -1;
/*    */         }
/*    */         else
/*    */         {
/* 76 */           res = this.value.length() - candidate.value.length();
/*    */           
/* 78 */           if (res == 0)
/*    */           {
/* 80 */             res = this.index - candidate.index; }
/*    */         }
/*    */       }
/*    */     }
/* 84 */     if ((this.decoder != null) && (candidate.getDecoder() != null)) {}
/*    */     
/*    */ 
/*    */ 
/* 88 */     return res;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtilDecoderCandidate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */