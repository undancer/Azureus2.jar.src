/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Vector;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X509ExtensionsGenerator
/*    */ {
/* 20 */   private Hashtable extensions = new Hashtable();
/* 21 */   private Vector extOrdering = new Vector();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void reset()
/*    */   {
/* 28 */     this.extensions = new Hashtable();
/* 29 */     this.extOrdering = new Vector();
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
/*    */ 
/*    */ 
/*    */ 
/*    */   public void addExtension(DERObjectIdentifier oid, boolean critical, DEREncodable value)
/*    */   {
/* 45 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 46 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*    */     
/*    */     try
/*    */     {
/* 50 */       dOut.writeObject(value);
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 54 */       throw new IllegalArgumentException("error encoding value: " + e);
/*    */     }
/*    */     
/* 57 */     addExtension(oid, critical, bOut.toByteArray());
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
/*    */ 
/*    */ 
/*    */ 
/*    */   public void addExtension(DERObjectIdentifier oid, boolean critical, byte[] value)
/*    */   {
/* 73 */     if (this.extensions.containsKey(oid))
/*    */     {
/* 75 */       throw new IllegalArgumentException("extension " + oid + " already added");
/*    */     }
/*    */     
/* 78 */     this.extOrdering.addElement(oid);
/* 79 */     this.extensions.put(oid, new X509Extension(critical, new DEROctetString(value)));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isEmpty()
/*    */   {
/* 89 */     return this.extOrdering.isEmpty();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public X509Extensions generate()
/*    */   {
/* 99 */     return new X509Extensions(this.extOrdering, this.extensions);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509ExtensionsGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */