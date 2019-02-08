/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.gudy.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERPrintableString;
/*     */ import org.gudy.bouncycastle.util.Strings;
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
/*     */ public abstract class X509NameEntryConverter
/*     */ {
/*     */   protected DERObject convertHexEncoded(String str, int off)
/*     */     throws IOException
/*     */   {
/*  64 */     str = Strings.toLowerCase(str);
/*  65 */     byte[] data = new byte[(str.length() - off) / 2];
/*  66 */     for (int index = 0; index != data.length; index++)
/*     */     {
/*  68 */       char left = str.charAt(index * 2 + off);
/*  69 */       char right = str.charAt(index * 2 + off + 1);
/*     */       
/*  71 */       if (left < 'a')
/*     */       {
/*  73 */         data[index] = ((byte)(left - '0' << 4));
/*     */       }
/*     */       else
/*     */       {
/*  77 */         data[index] = ((byte)(left - 'a' + 10 << 4));
/*     */       }
/*  79 */       if (right < 'a')
/*     */       {
/*  81 */         int tmp99_97 = index; byte[] tmp99_96 = data;tmp99_96[tmp99_97] = ((byte)(tmp99_96[tmp99_97] | (byte)(right - '0')));
/*     */       }
/*     */       else
/*     */       {
/*  85 */         int tmp116_114 = index; byte[] tmp116_113 = data;tmp116_113[tmp116_114] = ((byte)(tmp116_113[tmp116_114] | (byte)(right - 'a' + 10)));
/*     */       }
/*     */     }
/*     */     
/*  89 */     ASN1InputStream aIn = new ASN1InputStream(data);
/*     */     
/*  91 */     return aIn.readObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean canBePrintable(String str)
/*     */   {
/* 101 */     return DERPrintableString.isPrintableString(str);
/*     */   }
/*     */   
/*     */   public abstract DERObject getConvertedValue(DERObjectIdentifier paramDERObjectIdentifier, String paramString);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509NameEntryConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */