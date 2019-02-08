/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.SimpleTimeZone;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Choice;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERUTCTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Time
/*     */   extends ASN1Encodable
/*     */   implements ASN1Choice
/*     */ {
/*     */   DERObject time;
/*     */   
/*     */   public static Time getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  26 */     return getInstance(obj.getObject());
/*     */   }
/*     */   
/*     */ 
/*     */   public Time(DERObject time)
/*     */   {
/*  32 */     if ((!(time instanceof DERUTCTime)) && (!(time instanceof DERGeneralizedTime)))
/*     */     {
/*     */ 
/*  35 */       throw new IllegalArgumentException("unknown object passed to Time");
/*     */     }
/*     */     
/*  38 */     this.time = time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Time(Date date)
/*     */   {
/*  49 */     SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
/*  50 */     SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss");
/*     */     
/*  52 */     dateF.setTimeZone(tz);
/*     */     
/*  54 */     String d = dateF.format(date) + "Z";
/*  55 */     int year = Integer.parseInt(d.substring(0, 4));
/*     */     
/*  57 */     if ((year < 1950) || (year > 2049))
/*     */     {
/*  59 */       this.time = new DERGeneralizedTime(d);
/*     */     }
/*     */     else
/*     */     {
/*  63 */       this.time = new DERUTCTime(d.substring(2));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static Time getInstance(Object obj)
/*     */   {
/*  70 */     if ((obj instanceof Time))
/*     */     {
/*  72 */       return (Time)obj;
/*     */     }
/*  74 */     if ((obj instanceof DERUTCTime))
/*     */     {
/*  76 */       return new Time((DERUTCTime)obj);
/*     */     }
/*  78 */     if ((obj instanceof DERGeneralizedTime))
/*     */     {
/*  80 */       return new Time((DERGeneralizedTime)obj);
/*     */     }
/*     */     
/*  83 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */   public String getTime()
/*     */   {
/*  88 */     if ((this.time instanceof DERUTCTime))
/*     */     {
/*  90 */       return ((DERUTCTime)this.time).getAdjustedTime();
/*     */     }
/*     */     
/*     */ 
/*  94 */     return ((DERGeneralizedTime)this.time).getTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public Date getDate()
/*     */   {
/*     */     try
/*     */     {
/* 102 */       if ((this.time instanceof DERUTCTime))
/*     */       {
/* 104 */         return ((DERUTCTime)this.time).getAdjustedDate();
/*     */       }
/*     */       
/*     */ 
/* 108 */       return ((DERGeneralizedTime)this.time).getDate();
/*     */ 
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/* 113 */       throw new IllegalStateException("invalid date string: " + e.getMessage());
/*     */     }
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 127 */     return this.time;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 132 */     return getTime();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/Time.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */