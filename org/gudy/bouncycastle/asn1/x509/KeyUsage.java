/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DERBitString;
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
/*    */ public class KeyUsage
/*    */   extends DERBitString
/*    */ {
/*    */   public static final int digitalSignature = 128;
/*    */   public static final int nonRepudiation = 64;
/*    */   public static final int keyEncipherment = 32;
/*    */   public static final int dataEncipherment = 16;
/*    */   public static final int keyAgreement = 8;
/*    */   public static final int keyCertSign = 4;
/*    */   public static final int cRLSign = 2;
/*    */   public static final int encipherOnly = 1;
/*    */   public static final int decipherOnly = 32768;
/*    */   
/*    */   public static DERBitString getInstance(Object obj)
/*    */   {
/* 39 */     if ((obj instanceof KeyUsage))
/*    */     {
/* 41 */       return (KeyUsage)obj;
/*    */     }
/*    */     
/* 44 */     if ((obj instanceof X509Extension))
/*    */     {
/* 46 */       return new KeyUsage(DERBitString.getInstance(X509Extension.convertValueToObject((X509Extension)obj)));
/*    */     }
/*    */     
/* 49 */     return new KeyUsage(DERBitString.getInstance(obj));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public KeyUsage(int usage)
/*    */   {
/* 62 */     super(getBytes(usage), getPadBits(usage));
/*    */   }
/*    */   
/*    */ 
/*    */   public KeyUsage(DERBitString usage)
/*    */   {
/* 68 */     super(usage.getBytes(), usage.getPadBits());
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 73 */     if (this.data.length == 1)
/*    */     {
/* 75 */       return "KeyUsage: 0x" + Integer.toHexString(this.data[0] & 0xFF);
/*    */     }
/* 77 */     return "KeyUsage: 0x" + Integer.toHexString((this.data[1] & 0xFF) << 8 | this.data[0] & 0xFF);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/KeyUsage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */