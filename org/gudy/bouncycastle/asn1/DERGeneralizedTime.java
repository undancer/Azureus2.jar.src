/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.SimpleTimeZone;
/*     */ import java.util.TimeZone;
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
/*     */ public class DERGeneralizedTime
/*     */   extends ASN1Object
/*     */ {
/*     */   String time;
/*     */   
/*     */   public static DERGeneralizedTime getInstance(Object obj)
/*     */   {
/*  26 */     if ((obj == null) || ((obj instanceof DERGeneralizedTime)))
/*     */     {
/*  28 */       return (DERGeneralizedTime)obj;
/*     */     }
/*     */     
/*  31 */     if ((obj instanceof ASN1OctetString))
/*     */     {
/*  33 */       return new DERGeneralizedTime(((ASN1OctetString)obj).getOctets());
/*     */     }
/*     */     
/*  36 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
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
/*     */   public static DERGeneralizedTime getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  52 */     return getInstance(obj.getObject());
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
/*     */   public DERGeneralizedTime(String time)
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
/*     */   public DERGeneralizedTime(Date time)
/*     */   {
/*  84 */     SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
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
/*     */   DERGeneralizedTime(byte[] bytes)
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
/*     */   public String getTimeString()
/*     */   {
/* 113 */     return this.time;
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
/*     */   public String getTime()
/*     */   {
/* 133 */     if (this.time.charAt(this.time.length() - 1) == 'Z')
/*     */     {
/* 135 */       return this.time.substring(0, this.time.length() - 1) + "GMT+00:00";
/*     */     }
/*     */     
/*     */ 
/* 139 */     int signPos = this.time.length() - 5;
/* 140 */     char sign = this.time.charAt(signPos);
/* 141 */     if ((sign == '-') || (sign == '+'))
/*     */     {
/* 143 */       return this.time.substring(0, signPos) + "GMT" + this.time.substring(signPos, signPos + 3) + ":" + this.time.substring(signPos + 3);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 151 */     signPos = this.time.length() - 3;
/* 152 */     sign = this.time.charAt(signPos);
/* 153 */     if ((sign == '-') || (sign == '+'))
/*     */     {
/* 155 */       return this.time.substring(0, signPos) + "GMT" + this.time.substring(signPos) + ":00";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 162 */     return this.time + calculateGMTOffset();
/*     */   }
/*     */   
/*     */   private String calculateGMTOffset()
/*     */   {
/* 167 */     String sign = "+";
/* 168 */     TimeZone timeZone = TimeZone.getDefault();
/* 169 */     int offset = timeZone.getRawOffset();
/* 170 */     if (offset < 0)
/*     */     {
/* 172 */       sign = "-";
/* 173 */       offset = -offset;
/*     */     }
/* 175 */     int hours = offset / 3600000;
/* 176 */     int minutes = (offset - hours * 60 * 60 * 1000) / 60000;
/*     */     
/*     */     try
/*     */     {
/* 180 */       if ((timeZone.useDaylightTime()) && (timeZone.inDaylightTime(getDate())))
/*     */       {
/* 182 */         hours += (sign.equals("+") ? 1 : -1);
/*     */       }
/*     */     }
/*     */     catch (ParseException e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 190 */     return "GMT" + sign + convert(hours) + ":" + convert(minutes);
/*     */   }
/*     */   
/*     */   private String convert(int time)
/*     */   {
/* 195 */     if (time < 10)
/*     */     {
/* 197 */       return "0" + time;
/*     */     }
/*     */     
/* 200 */     return Integer.toString(time);
/*     */   }
/*     */   
/*     */ 
/*     */   public Date getDate()
/*     */     throws ParseException
/*     */   {
/* 207 */     String d = this.time;
/*     */     SimpleDateFormat dateF;
/* 209 */     if (this.time.endsWith("Z")) { SimpleDateFormat dateF;
/*     */       SimpleDateFormat dateF;
/* 211 */       if (hasFractionalSeconds())
/*     */       {
/* 213 */         dateF = new SimpleDateFormat("yyyyMMddHHmmss.SSSS'Z'");
/*     */       }
/*     */       else
/*     */       {
/* 217 */         dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
/*     */       }
/*     */       
/* 220 */       dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
/*     */     }
/* 222 */     else if ((this.time.indexOf('-') > 0) || (this.time.indexOf('+') > 0))
/*     */     {
/* 224 */       d = getTime();
/* 225 */       SimpleDateFormat dateF; SimpleDateFormat dateF; if (hasFractionalSeconds())
/*     */       {
/* 227 */         dateF = new SimpleDateFormat("yyyyMMddHHmmss.SSSSz");
/*     */       }
/*     */       else
/*     */       {
/* 231 */         dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
/*     */       }
/*     */       
/* 234 */       dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
/*     */     }
/*     */     else {
/*     */       SimpleDateFormat dateF;
/* 238 */       if (hasFractionalSeconds())
/*     */       {
/* 240 */         dateF = new SimpleDateFormat("yyyyMMddHHmmss.SSSS");
/*     */       }
/*     */       else
/*     */       {
/* 244 */         dateF = new SimpleDateFormat("yyyyMMddHHmmss");
/*     */       }
/*     */       
/* 247 */       dateF.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
/*     */     }
/*     */     
/* 250 */     return dateF.parse(d);
/*     */   }
/*     */   
/*     */   private boolean hasFractionalSeconds()
/*     */   {
/* 255 */     return this.time.indexOf('.') == 14;
/*     */   }
/*     */   
/*     */   private byte[] getOctets()
/*     */   {
/* 260 */     char[] cs = this.time.toCharArray();
/* 261 */     byte[] bs = new byte[cs.length];
/*     */     
/* 263 */     for (int i = 0; i != cs.length; i++)
/*     */     {
/* 265 */       bs[i] = ((byte)cs[i]);
/*     */     }
/*     */     
/* 268 */     return bs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void encode(DEROutputStream out)
/*     */     throws IOException
/*     */   {
/* 276 */     out.writeEncoded(24, getOctets());
/*     */   }
/*     */   
/*     */ 
/*     */   boolean asn1Equals(DERObject o)
/*     */   {
/* 282 */     if (!(o instanceof DERGeneralizedTime))
/*     */     {
/* 284 */       return false;
/*     */     }
/*     */     
/* 287 */     return this.time.equals(((DERGeneralizedTime)o).time);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 292 */     return this.time.hashCode();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERGeneralizedTime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */