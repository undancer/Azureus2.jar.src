/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
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
/*    */ public class DERTaggedObject
/*    */   extends ASN1TaggedObject
/*    */ {
/*    */   public DERTaggedObject(int tagNo, DEREncodable obj)
/*    */   {
/* 22 */     super(tagNo, obj);
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
/*    */   public DERTaggedObject(boolean explicit, int tagNo, DEREncodable obj)
/*    */   {
/* 35 */     super(explicit, tagNo, obj);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERTaggedObject(int tagNo)
/*    */   {
/* 45 */     super(false, tagNo, new DERSequence());
/*    */   }
/*    */   
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 52 */     if (!this.empty)
/*    */     {
/* 54 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 55 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*    */       
/* 57 */       dOut.writeObject(this.obj);
/* 58 */       dOut.close();
/*    */       
/* 60 */       byte[] bytes = bOut.toByteArray();
/*    */       
/* 62 */       if (this.explicit)
/*    */       {
/* 64 */         out.writeEncoded(0xA0 | this.tagNo, bytes);
/*    */ 
/*    */ 
/*    */       }
/*    */       else
/*    */       {
/*    */ 
/* 71 */         if ((bytes[0] & 0x20) != 0)
/*    */         {
/* 73 */           bytes[0] = ((byte)(0xA0 | this.tagNo));
/*    */         }
/*    */         else
/*    */         {
/* 77 */           bytes[0] = ((byte)(0x80 | this.tagNo));
/*    */         }
/*    */         
/* 80 */         out.write(bytes);
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 85 */       out.writeEncoded(0xA0 | this.tagNo, new byte[0]);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERTaggedObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */