/*    */ package org.gudy.bouncycastle.jce;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.x509.KeyUsage;
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
/*    */ public class X509KeyUsage
/*    */   implements DEREncodable
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
/* 38 */   private int usage = 0;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public X509KeyUsage(int usage)
/*    */   {
/* 50 */     this.usage = usage;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 55 */     return new KeyUsage(this.usage);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/X509KeyUsage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */