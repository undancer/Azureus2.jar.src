/*    */ package org.gudy.bouncycastle.asn1.misc;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NetscapeCertType
/*    */   extends DERBitString
/*    */ {
/*    */   public static final int sslClient = 128;
/*    */   public static final int sslServer = 64;
/*    */   public static final int smime = 32;
/*    */   public static final int objectSigning = 16;
/*    */   public static final int reserved = 8;
/*    */   public static final int sslCA = 4;
/*    */   public static final int smimeCA = 2;
/*    */   public static final int objectSigningCA = 1;
/*    */   
/*    */   public NetscapeCertType(int usage)
/*    */   {
/* 41 */     super(getBytes(usage), getPadBits(usage));
/*    */   }
/*    */   
/*    */ 
/*    */   public NetscapeCertType(DERBitString usage)
/*    */   {
/* 47 */     super(usage.getBytes(), usage.getPadBits());
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 52 */     return "NetscapeCertType: 0x" + Integer.toHexString(this.data[0] & 0xFF);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/misc/NetscapeCertType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */