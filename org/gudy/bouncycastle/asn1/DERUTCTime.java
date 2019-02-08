/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.SimpleTimeZone;
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
/*     */ public class DERUTCTime
/*     */   extends ASN1Object
/*     */ {
/*     */   String time;
/*     */   
/*     */   public static DERUTCTime getInstance(Object obj)
/*     */   {
/*  25 */     if ((obj == null) || ((obj instanceof DERUTCTime)))
/*     */     {
/*  27 */       return (DERUTCTime)obj;
/*     */     }
/*     */     
/*  30 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  32 */       return new DERUTCTime(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  35 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public static DERUTCTime getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  51 */     return getInstance(obj.getObject());
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
/*     */   public DERUTCTime(String time)
/*     */   {
/*  67 */     this.time = time;
/*     */     try
/*     */     {
/*  70 */       getDate();
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/*  74 */       throw new IllegalArgumentException("invalid date string: " + e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERUTCTime(Date time)
/*     */   {
/*  84 */     SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss'Z'");
/*     */     
/*  86 */     dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
/*     */     
/*  88 */     this.time = dateF.format(time);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   DERUTCTime(byte[] bytes)
/*     */   {
/*  97 */     char[] dateC = new char[bytes.length];
/*     */     
/*  99 */     for (int i = 0; i != dateC.length; i++)
/*     */     {
/* 101 */       dateC[i] = ((char)(bytes[i] & 0xFF));
/*     */     }
/*     */     
/* 104 */     this.time = new String(dateC);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getDate()
/*     */     throws ParseException
/*     */   {
/* 117 */     SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmssz");
/*     */     
/* 119 */     return dateF.parse(getTime());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getAdjustedDate()
/*     */     throws ParseException
/*     */   {
/* 132 */     SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
/*     */     
/* 134 */     dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
/*     */     
/* 136 */     return dateF.parse(getAdjustedTime());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTime()
/*     */   {
/* 160 */     if ((this.time.indexOf('-') < 0) && (this.time.indexOf('+') < 0))
/*     */     {
/* 162 */       if (this.time.length() == 11)
/*     */       {
/* 164 */         return this.time.substring(0, 10) + "00GMT+00:00";
/*     */       }
/*     */       
/*     */ 
/* 168 */       return this.time.substring(0, 12) + "GMT+00:00";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 173 */     int index = this.time.indexOf('-');
/* 174 */     if (index < 0)
/*     */     {
/* 176 */       index = this.time.indexOf('+');
/*     */     }
/* 178 */     String d = this.time;
/*     */     
/* 180 */     if (index == this.time.length() - 3)
/*     */     {
/* 182 */       d = d + "00";
/*     */     }
/*     */     
/* 185 */     if (index == 10)
/*     */     {
/* 187 */       return d.substring(0, 10) + "00GMT" + d.substring(10, 13) + ":" + d.substring(13, 15);
/*     */     }
/*     */     
/*     */ 
/* 191 */     return d.substring(0, 12) + "GMT" + d.substring(12, 15) + ":" + d.substring(15, 17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAdjustedTime()
/*     */   {
/* 202 */     String d = getTime();
/*     */     
/* 204 */     if (d.charAt(0) < '5')
/*     */     {
/* 206 */       return "20" + d;
/*     */     }
/*     */     
/*     */ 
/* 210 */     return "19" + d;
/*     */   }
/*     */   
/*     */ 
/*     */   private byte[] getOctets()
/*     */   {
/* 216 */     char[] cs = this.time.toCharArray();
/* 217 */     byte[] bs = new byte[cs.length];
/*     */     
/* 219 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/* 221 */       bs[i] = ((byte)cs[i]);
/*     */     }
/*     */     
/* 224 */     return bs;
/*     */   }
/*     */   
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 231 */     out.writeEncoded(23, getOctets());
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 237 */     if (!(o instanceof DERUTCTime))
/*     */     {
/* 239 */       return false;
/*     */     }
/*     */     
/* 242 */     return this.time.equals(((DERUTCTime)o).time);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 247 */     return this.time.hashCode();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 252 */     return this.time;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERUTCTime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */