/*     */ package com.aelitis.azureus.ui.selectedcontent;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ public class DownloadUrlInfo
/*     */ {
/*     */   private String dlURL;
/*     */   private String referer;
/*     */   private Map requestProperties;
/*  40 */   private Map additionalProperties = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DownloadUrlInfo(String url)
/*     */   {
/*  48 */     setDownloadURL(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDownloadURL()
/*     */   {
/*  55 */     return this.dlURL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadURL(String dlURL)
/*     */   {
/*  62 */     this.dlURL = dlURL;
/*     */   }
/*     */   
/*     */   public void setReferer(String referer) {
/*  66 */     this.referer = referer;
/*     */   }
/*     */   
/*     */   public String getReferer() {
/*  70 */     return this.referer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map getRequestProperties()
/*     */   {
/*  79 */     return this.requestProperties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRequestProperties(Map requestProperties)
/*     */   {
/*  86 */     this.requestProperties = requestProperties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAdditionalProperty(String key, Object value)
/*     */   {
/*  93 */     if (this.additionalProperties == null) {
/*  94 */       this.additionalProperties = new LightHashMap(1);
/*     */     }
/*  96 */     this.additionalProperties.put(key, value);
/*     */   }
/*     */   
/*     */   public void setAdditionalProperties(Map mapToCopy) {
/* 100 */     if (this.additionalProperties == null) {
/* 101 */       this.additionalProperties = new LightHashMap(1);
/*     */     }
/* 103 */     this.additionalProperties.putAll(mapToCopy);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map getAdditionalProperties()
/*     */   {
/* 111 */     return this.additionalProperties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean objectEquals(Object o1, Object o2)
/*     */   {
/* 119 */     if (o1 != o2)
/*     */     {
/* 121 */       if ((o1 == null) || (o2 == null) || (!o1.equals(o2)))
/*     */       {
/*     */ 
/*     */ 
/* 125 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 129 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean sameAs(DownloadUrlInfo other)
/*     */   {
/* 136 */     if (other == this)
/*     */     {
/* 138 */       return true;
/*     */     }
/*     */     
/* 141 */     return (objectEquals(this.dlURL, other.dlURL)) && (objectEquals(this.referer, other.referer)) && (objectEquals(this.requestProperties, other.requestProperties)) && (objectEquals(this.additionalProperties, other.additionalProperties));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/selectedcontent/DownloadUrlInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */