/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.rss;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
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
/*     */ public class RSSUtils
/*     */ {
/*     */   public static Date parseRSSDate(String date_str)
/*     */   {
/*  47 */     date_str = date_str.trim();
/*     */     
/*  49 */     if (date_str.length() == 0)
/*     */     {
/*  51 */       return null;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*     */       SimpleDateFormat format;
/*     */       
/*     */       SimpleDateFormat format;
/*     */       
/*  60 */       if (!date_str.contains(","))
/*     */       {
/*  62 */         format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
/*     */       }
/*     */       else
/*     */       {
/*  66 */         format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
/*     */       }
/*     */       
/*     */ 
/*  70 */       return format.parse(date_str);
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/*  74 */       String[] fallbacks = { "dd MMM yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE MMM dd HH:mm:ss z yyyy", "EEE MMM dd HH:mm z yyyy", "EEE MMM dd HH z yyyy", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
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
/*  87 */       date_str = date_str.replace(',', ' ');
/*     */       
/*     */ 
/*     */ 
/*  91 */       date_str = date_str.replaceAll("(\\s)+", " ");
/*     */       
/*  93 */       for (int i = 0; i < fallbacks.length; i++) {
/*     */         try
/*     */         {
/*  96 */           return new SimpleDateFormat(fallbacks[i], Locale.US).parse(date_str);
/*     */         }
/*     */         catch (ParseException f) {}
/*     */       }
/*     */       
/*     */ 
/* 102 */       Debug.outNoStack("RSSUtils: failed to parse RSS date: " + date_str);
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Date parseAtomDate(String date_str)
/*     */   {
/* 112 */     date_str = date_str.trim();
/*     */     
/* 114 */     if (date_str.length() == 0)
/*     */     {
/* 116 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 121 */     String[] formats = { "yyyy-MM-dd'T'kk:mm:ss'Z'", "yyyy-MM-dd'T'kk:mm:ssz", "yyyy-MM-dd'T'kk:mm:ssZ", "yyyy-MM-dd'T'kk:mm:ss", "yyyy-MM-dd'T'kk:mm'Z'", "yyyy-MM-dd'T'kk:mmz", "yyyy-MM-dd'T'kk:mmZ", "yyyy-MM-dd'T'kk:mm", "yyyy-MM-dd-hh:mm:ss a" };
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
/* 136 */     for (int i = 0; i < formats.length; i++)
/*     */     {
/*     */       try
/*     */       {
/* 140 */         SimpleDateFormat format = new SimpleDateFormat(formats[i], Locale.US);
/*     */         
/* 142 */         return format.parse(date_str);
/*     */       }
/*     */       catch (ParseException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 150 */     Debug.outNoStack("RSSUtils: failed to parse Atom date: " + date_str);
/*     */     
/* 152 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isRSSFeed(File file)
/*     */   {
/*     */     try
/*     */     {
/* 160 */       String str = FileUtil.readFileAsString(file, 512).toLowerCase();
/*     */       
/* 162 */       str = str.trim().toLowerCase(Locale.US);
/*     */       
/* 164 */       if (str.startsWith("<?xml"))
/*     */       {
/* 166 */         if ((str.contains("<feed")) || (str.contains("<rss")))
/*     */         {
/* 168 */           InputStream is = new BufferedInputStream(new FileInputStream(file));
/*     */           try
/*     */           {
/* 171 */             new RSSFeedImpl(new UtilitiesImpl(null, null), null, is);
/*     */             
/* 173 */             return true;
/*     */           }
/*     */           finally
/*     */           {
/* 177 */             is.close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 184 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 191 */     System.out.println(parseRSSDate("2013-08-11T18:30:00.000Z"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/rss/RSSUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */