/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DERGeneralString
/*    */   extends ASN1Object
/*    */   implements DERString
/*    */ {
/*    */   private String string;
/*    */   
/*    */   public static DERGeneralString getInstance(Object obj)
/*    */   {
/* 13 */     if ((obj == null) || ((obj instanceof DERGeneralString)))
/*    */     {
/* 15 */       return (DERGeneralString)obj;
/*    */     }
/* 17 */     if ((obj instanceof ASN1OctetString))
/*    */     {
/* 19 */       return new DERGeneralString(((ASN1OctetString)obj).getOctets());
/*    */     }
/* 21 */     if ((obj instanceof ASN1TaggedObject))
/*    */     {
/* 23 */       return getInstance(((ASN1TaggedObject)obj).getObject());
/*    */     }
/* 25 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static DERGeneralString getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 33 */     return getInstance(obj.getObject());
/*    */   }
/*    */   
/*    */   public DERGeneralString(byte[] string)
/*    */   {
/* 38 */     char[] cs = new char[string.length];
/* 39 */     for (int i = 0; i != cs.length; i++)
/*    */     {
/* 41 */       cs[i] = ((char)(string[i] & 0xFF));
/*    */     }
/* 43 */     this.string = new String(cs);
/*    */   }
/*    */   
/*    */   public DERGeneralString(String string)
/*    */   {
/* 48 */     this.string = string;
/*    */   }
/*    */   
/*    */   public String getString()
/*    */   {
/* 53 */     return this.string;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 58 */     return this.string;
/*    */   }
/*    */   
/*    */   public byte[] getOctets()
/*    */   {
/* 63 */     char[] cs = this.string.toCharArray();
/* 64 */     byte[] bs = new byte[cs.length];
/* 65 */     for (int i = 0; i != cs.length; i++)
/*    */     {
/* 67 */       bs[i] = ((byte)cs[i]);
/*    */     }
/* 69 */     return bs;
/*    */   }
/*    */   
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 75 */     out.writeEncoded(27, getOctets());
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 80 */     return getString().hashCode();
/*    */   }
/*    */   
/*    */   boolean asn1Equals(DERObject o)
/*    */   {
/* 85 */     if (!(o instanceof DERGeneralString))
/*    */     {
/* 87 */       return false;
/*    */     }
/* 89 */     DERGeneralString s = (DERGeneralString)o;
/* 90 */     return getString().equals(s.getString());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERGeneralString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */