/*     */ package org.gudy.bouncycastle.jce;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.Principal;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DEROutputStream;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509Principal
/*     */   extends X509Name
/*     */   implements Principal
/*     */ {
/*     */   public X509Principal(byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  26 */     super((ASN1Sequence)new ASN1InputStream(new ByteArrayInputStream(bytes)).readObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Principal(X509Name name)
/*     */   {
/*  35 */     super((ASN1Sequence)name.getDERObject());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Principal(Hashtable attributes)
/*     */   {
/*  46 */     super(attributes);
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
/*     */   public X509Principal(Vector ordering, Hashtable attributes)
/*     */   {
/*  59 */     super(ordering, attributes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Principal(Vector oids, Vector values)
/*     */   {
/*  69 */     super(oids, values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Principal(String dirName)
/*     */   {
/*  79 */     super(dirName);
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
/*     */   public X509Principal(boolean reverse, String dirName)
/*     */   {
/*  93 */     super(reverse, dirName);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Principal(boolean reverse, Hashtable lookUp, String dirName)
/*     */   {
/* 111 */     super(reverse, lookUp, dirName);
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/* 116 */     return toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */   {
/* 124 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 125 */     DEROutputStream dOut = new DEROutputStream(bOut);
/*     */     
/*     */     try
/*     */     {
/* 129 */       dOut.writeObject(this);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 133 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */     
/* 136 */     return bOut.toByteArray();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/X509Principal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */