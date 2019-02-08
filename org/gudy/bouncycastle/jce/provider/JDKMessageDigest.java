/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.MD4Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.MD5Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD128Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD256Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD320Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA512Digest;
/*     */ 
/*     */ public class JDKMessageDigest extends java.security.MessageDigest
/*     */ {
/*     */   Digest digest;
/*     */   
/*     */   protected JDKMessageDigest(Digest digest)
/*     */   {
/*  17 */     super(digest.getAlgorithmName());
/*     */     
/*  19 */     this.digest = digest;
/*     */   }
/*     */   
/*     */   public void engineReset()
/*     */   {
/*  24 */     this.digest.reset();
/*     */   }
/*     */   
/*     */ 
/*     */   public void engineUpdate(byte input)
/*     */   {
/*  30 */     this.digest.update(input);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void engineUpdate(byte[] input, int offset, int len)
/*     */   {
/*  38 */     this.digest.update(input, offset, len);
/*     */   }
/*     */   
/*     */   public byte[] engineDigest()
/*     */   {
/*  43 */     byte[] digestBytes = new byte[this.digest.getDigestSize()];
/*     */     
/*  45 */     this.digest.doFinal(digestBytes, 0);
/*     */     
/*  47 */     return digestBytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class SHA1
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public SHA1()
/*     */     {
/*  59 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/*  65 */       SHA1 d = (SHA1)super.clone();
/*  66 */       d.digest = new org.gudy.bouncycastle.crypto.digests.SHA1Digest((org.gudy.bouncycastle.crypto.digests.SHA1Digest)this.digest);
/*     */       
/*  68 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class SHA256
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public SHA256()
/*     */     {
/*  78 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/*  84 */       SHA256 d = (SHA256)super.clone();
/*  85 */       d.digest = new org.gudy.bouncycastle.crypto.digests.SHA256Digest((org.gudy.bouncycastle.crypto.digests.SHA256Digest)this.digest);
/*     */       
/*  87 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class SHA384
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public SHA384()
/*     */     {
/*  97 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 103 */       SHA384 d = (SHA384)super.clone();
/* 104 */       d.digest = new org.gudy.bouncycastle.crypto.digests.SHA384Digest((org.gudy.bouncycastle.crypto.digests.SHA384Digest)this.digest);
/*     */       
/* 106 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class SHA512
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public SHA512()
/*     */     {
/* 116 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 122 */       SHA512 d = (SHA512)super.clone();
/* 123 */       d.digest = new SHA512Digest((SHA512Digest)this.digest);
/*     */       
/* 125 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MD2
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public MD2()
/*     */     {
/* 135 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 141 */       MD2 d = (MD2)super.clone();
/* 142 */       d.digest = new org.gudy.bouncycastle.crypto.digests.MD2Digest((org.gudy.bouncycastle.crypto.digests.MD2Digest)this.digest);
/*     */       
/* 144 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MD4
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public MD4()
/*     */     {
/* 154 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 160 */       MD4 d = (MD4)super.clone();
/* 161 */       d.digest = new MD4Digest((MD4Digest)this.digest);
/*     */       
/* 163 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MD5
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public MD5()
/*     */     {
/* 173 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 179 */       MD5 d = (MD5)super.clone();
/* 180 */       d.digest = new MD5Digest((MD5Digest)this.digest);
/*     */       
/* 182 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD128
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public RIPEMD128()
/*     */     {
/* 192 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 198 */       RIPEMD128 d = (RIPEMD128)super.clone();
/* 199 */       d.digest = new RIPEMD128Digest((RIPEMD128Digest)this.digest);
/*     */       
/* 201 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD160
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public RIPEMD160()
/*     */     {
/* 211 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 217 */       RIPEMD160 d = (RIPEMD160)super.clone();
/* 218 */       d.digest = new org.gudy.bouncycastle.crypto.digests.RIPEMD160Digest((org.gudy.bouncycastle.crypto.digests.RIPEMD160Digest)this.digest);
/*     */       
/* 220 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD256
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public RIPEMD256()
/*     */     {
/* 230 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 236 */       RIPEMD256 d = (RIPEMD256)super.clone();
/* 237 */       d.digest = new RIPEMD256Digest((RIPEMD256Digest)this.digest);
/*     */       
/* 239 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RIPEMD320
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public RIPEMD320()
/*     */     {
/* 249 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 255 */       RIPEMD320 d = (RIPEMD320)super.clone();
/* 256 */       d.digest = new RIPEMD320Digest((RIPEMD320Digest)this.digest);
/*     */       
/* 258 */       return d;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Tiger
/*     */     extends JDKMessageDigest
/*     */     implements Cloneable
/*     */   {
/*     */     public Tiger()
/*     */     {
/* 268 */       super();
/*     */     }
/*     */     
/*     */     public Object clone()
/*     */       throws CloneNotSupportedException
/*     */     {
/* 274 */       Tiger d = (Tiger)super.clone();
/* 275 */       d.digest = new org.gudy.bouncycastle.crypto.digests.TigerDigest((org.gudy.bouncycastle.crypto.digests.TigerDigest)this.digest);
/*     */       
/* 277 */       return d;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKMessageDigest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */