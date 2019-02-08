/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.security;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.plugins.utils.security.SEPublicKey;
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
/*     */ public class SEPublicKeyImpl
/*     */   implements SEPublicKey
/*     */ {
/*     */   private int type;
/*     */   private byte[] encoded;
/*     */   private int hashcode;
/*     */   
/*     */   public static SEPublicKey decode(byte[] encoded)
/*     */   {
/*  35 */     int type = encoded[0] & 0xFF;
/*     */     
/*  37 */     byte[] x = new byte[encoded.length - 1];
/*     */     
/*  39 */     System.arraycopy(encoded, 1, x, 0, x.length);
/*     */     
/*  41 */     return new SEPublicKeyImpl(type, x);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SEPublicKeyImpl(int _type, byte[] _encoded)
/*     */   {
/*  53 */     this.type = _type;
/*  54 */     this.encoded = _encoded;
/*  55 */     this.hashcode = new HashWrapper(this.encoded).hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  61 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] encodePublicKey()
/*     */   {
/*  67 */     byte[] res = new byte[this.encoded.length + 1];
/*     */     
/*  69 */     res[0] = ((byte)this.type);
/*     */     
/*  71 */     System.arraycopy(this.encoded, 0, res, 1, this.encoded.length);
/*     */     
/*  73 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] encodeRawPublicKey()
/*     */   {
/*  79 */     byte[] res = new byte[this.encoded.length];
/*     */     
/*  81 */     System.arraycopy(this.encoded, 0, res, 0, this.encoded.length);
/*     */     
/*  83 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/*  90 */     if ((other instanceof SEPublicKeyImpl))
/*     */     {
/*  92 */       return Arrays.equals(this.encoded, ((SEPublicKeyImpl)other).encoded);
/*     */     }
/*     */     
/*     */ 
/*  96 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 103 */     return this.hashcode;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/security/SEPublicKeyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */