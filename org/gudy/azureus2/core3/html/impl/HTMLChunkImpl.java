/*     */ package org.gudy.azureus2.core3.html.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ public class HTMLChunkImpl
/*     */ {
/*     */   String content;
/*     */   
/*     */   protected HTMLChunkImpl() {}
/*     */   
/*     */   protected HTMLChunkImpl(String _content)
/*     */   {
/*  46 */     this.content = _content;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setContent(String str)
/*     */   {
/*  53 */     this.content = str;
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
/*     */   protected String[] getTags(String tag_name)
/*     */   {
/*  66 */     tag_name = tag_name.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */     
/*  68 */     String lc_content = this.content.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */     
/*  70 */     int pos = 0;
/*     */     
/*  72 */     List res = new ArrayList();
/*     */     
/*     */     for (;;)
/*     */     {
/*  76 */       int p1 = lc_content.indexOf("<" + tag_name, pos);
/*     */       
/*  78 */       if (p1 == -1) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  83 */       int p2 = lc_content.indexOf(">", p1);
/*     */       
/*  85 */       if (p2 == -1) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  90 */       res.add(this.content.substring(p1 + 1, p2));
/*     */       
/*  92 */       pos = p2 + 1;
/*     */     }
/*     */     
/*  95 */     String[] x = new String[res.size()];
/*     */     
/*  97 */     res.toArray(x);
/*     */     
/*  99 */     return x;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getContent()
/*     */   {
/* 105 */     return this.content;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/html/impl/HTMLChunkImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */