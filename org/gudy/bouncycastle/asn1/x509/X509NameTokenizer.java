/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X509NameTokenizer
/*    */ {
/*    */   private String value;
/*    */   
/*    */ 
/*    */   private int index;
/*    */   
/*    */   private char seperator;
/*    */   
/* 14 */   private StringBuffer buf = new StringBuffer();
/*    */   
/*    */ 
/*    */   public X509NameTokenizer(String oid)
/*    */   {
/* 19 */     this(oid, ',');
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public X509NameTokenizer(String oid, char seperator)
/*    */   {
/* 26 */     this.value = oid;
/* 27 */     this.index = -1;
/* 28 */     this.seperator = seperator;
/*    */   }
/*    */   
/*    */   public boolean hasMoreTokens()
/*    */   {
/* 33 */     return this.index != this.value.length();
/*    */   }
/*    */   
/*    */   public String nextToken()
/*    */   {
/* 38 */     if (this.index == this.value.length())
/*    */     {
/* 40 */       return null;
/*    */     }
/*    */     
/* 43 */     int end = this.index + 1;
/* 44 */     boolean quoted = false;
/* 45 */     boolean escaped = false;
/*    */     
/* 47 */     this.buf.setLength(0);
/*    */     
/* 49 */     while (end != this.value.length())
/*    */     {
/* 51 */       char c = this.value.charAt(end);
/*    */       
/* 53 */       if (c == '"')
/*    */       {
/* 55 */         if (!escaped)
/*    */         {
/* 57 */           quoted = !quoted;
/*    */         }
/*    */         else
/*    */         {
/* 61 */           this.buf.append(c);
/*    */         }
/* 63 */         escaped = false;
/*    */ 
/*    */ 
/*    */       }
/* 67 */       else if ((escaped) || (quoted))
/*    */       {
/* 69 */         this.buf.append(c);
/* 70 */         escaped = false;
/*    */       }
/* 72 */       else if (c == '\\')
/*    */       {
/* 74 */         escaped = true;
/*    */       } else {
/* 76 */         if (c == this.seperator) {
/*    */           break;
/*    */         }
/*    */         
/*    */ 
/*    */ 
/* 82 */         this.buf.append(c);
/*    */       }
/*    */       
/* 85 */       end++;
/*    */     }
/*    */     
/* 88 */     this.index = end;
/* 89 */     return this.buf.toString().trim();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509NameTokenizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */