/*     */ package org.gudy.azureus2.core3.html.impl;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.html.HTMLException;
/*     */ import org.gudy.azureus2.core3.html.HTMLPage;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class HTMLPageImpl
/*     */   extends HTMLChunkImpl
/*     */   implements HTMLPage
/*     */ {
/*     */   public HTMLPageImpl(InputStream is, String charset, boolean close_file)
/*     */     throws HTMLException
/*     */   {
/*  48 */     BufferedReader br = null;
/*     */     
/*  50 */     StringBuilder res = new StringBuilder(1024);
/*     */     
/*     */     try
/*     */     {
/*  54 */       if (charset == null)
/*     */       {
/*  56 */         br = new BufferedReader(new InputStreamReader(is));
/*     */       }
/*     */       else {
/*  59 */         br = new BufferedReader(new InputStreamReader(is, charset));
/*     */       }
/*     */       for (;;)
/*     */       {
/*  63 */         String line = br.readLine();
/*     */         
/*  65 */         if (line == null) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*  70 */         res.append(line);
/*     */       }
/*     */       
/*  73 */       setContent(res.toString()); return;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  77 */       throw new HTMLException("Error reading HTML page", e);
/*     */     }
/*     */     finally
/*     */     {
/*  81 */       if ((br != null) && (close_file))
/*     */       {
/*     */         try
/*     */         {
/*  85 */           br.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*  89 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public URL getMetaRefreshURL()
/*     */   {
/*  97 */     return getMetaRefreshURL(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URL getMetaRefreshURL(URL base_url)
/*     */   {
/* 106 */     String[] tags = getTags("META");
/*     */     
/* 108 */     for (int i = 0; i < tags.length; i++)
/*     */     {
/* 110 */       String tag = tags[i];
/*     */       
/* 112 */       String lc_tag = tag.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */       
/* 114 */       int pos = lc_tag.indexOf("http-equiv=\"refresh\"");
/*     */       
/* 116 */       int url_start = lc_tag.indexOf("url=");
/*     */       
/* 118 */       if ((pos != -1) && (url_start != -1))
/*     */       {
/* 120 */         url_start += 4;
/*     */         
/* 122 */         int e1 = lc_tag.indexOf("\"", url_start);
/*     */         
/* 124 */         if (e1 != -1) {
/*     */           try
/*     */           {
/* 127 */             String mr_url = tag.substring(url_start, e1).trim();
/*     */             
/* 129 */             String lc = mr_url.toLowerCase();
/*     */             
/* 131 */             if ((!lc.startsWith("http:")) && (!lc.startsWith("https:")))
/*     */             {
/* 133 */               if (base_url != null)
/*     */               {
/* 135 */                 String s = base_url.toExternalForm();
/*     */                 
/* 137 */                 int p = s.indexOf('?');
/*     */                 
/* 139 */                 if (p != -1)
/*     */                 {
/* 141 */                   s = s.substring(0, p);
/*     */                 }
/*     */                 
/* 144 */                 if ((s.endsWith("/")) && (mr_url.startsWith("/")))
/*     */                 {
/* 146 */                   mr_url = mr_url.substring(1);
/*     */                 }
/*     */                 
/* 149 */                 mr_url = s + mr_url;
/*     */               }
/*     */             }
/*     */             
/* 153 */             return new URL(mr_url);
/*     */           }
/*     */           catch (MalformedURLException e)
/*     */           {
/* 157 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 163 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/html/impl/HTMLPageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */