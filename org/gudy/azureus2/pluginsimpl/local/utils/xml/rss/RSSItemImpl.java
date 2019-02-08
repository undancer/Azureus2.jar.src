/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.rss;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.Date;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSItem;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentAttribute;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
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
/*     */ public class RSSItemImpl
/*     */   implements RSSItem
/*     */ {
/*     */   private boolean is_atom;
/*     */   private SimpleXMLParserDocumentNode node;
/*     */   
/*     */   protected RSSItemImpl(SimpleXMLParserDocumentNode _node, boolean _is_atom)
/*     */   {
/*  47 */     this.is_atom = _is_atom;
/*  48 */     this.node = _node;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  54 */     if (this.node.getChild("title") != null)
/*     */     {
/*  56 */       return this.node.getChild("title").getValue();
/*     */     }
/*     */     
/*  59 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  65 */     if (this.node.getChild("description") != null)
/*     */     {
/*  67 */       return this.node.getChild("description").getValue();
/*     */     }
/*     */     
/*  70 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getLink()
/*     */   {
/*  76 */     SimpleXMLParserDocumentNode link_node = this.node.getChild("link");
/*     */     
/*  78 */     if (link_node != null) {
/*     */       try
/*     */       {
/*  81 */         String value = "";
/*     */         
/*  83 */         if (this.is_atom)
/*     */         {
/*  85 */           SimpleXMLParserDocumentAttribute attr = link_node.getAttribute("href");
/*     */           
/*  87 */           if (attr == null)
/*     */           {
/*  89 */             return null;
/*     */           }
/*     */           
/*  92 */           value = attr.getValue().trim();
/*     */         }
/*     */         else
/*     */         {
/*  96 */           value = link_node.getValue().trim();
/*     */         }
/*     */         
/*  99 */         if (value.length() == 0)
/*     */         {
/* 101 */           return null;
/*     */         }
/*     */         
/* 104 */         if (value.startsWith("//")) {
/* 105 */           value = "http:" + value;
/*     */         }
/*     */         
/* 108 */         return new URL(value);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 112 */         Debug.printStackTrace(e);
/*     */         
/* 114 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 118 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Date getPublicationDate()
/*     */   {
/* 124 */     SimpleXMLParserDocumentNode pd = this.node.getChild(this.is_atom ? "published" : "pubdate");
/*     */     
/* 126 */     if (pd != null)
/*     */     {
/* 128 */       if (this.is_atom)
/*     */       {
/* 130 */         return RSSUtils.parseAtomDate(pd.getValue());
/*     */       }
/*     */       
/*     */ 
/* 134 */       return RSSUtils.parseRSSDate(pd.getValue());
/*     */     }
/*     */     
/*     */ 
/* 138 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUID()
/*     */   {
/* 144 */     SimpleXMLParserDocumentNode uid = this.node.getChild(this.is_atom ? "id" : "guid");
/*     */     
/* 146 */     if (uid != null)
/*     */     {
/* 148 */       String value = uid.getValue().trim();
/*     */       
/* 150 */       if (value.length() > 0)
/*     */       {
/* 152 */         return value;
/*     */       }
/*     */     }
/*     */     
/* 156 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentNode getNode()
/*     */   {
/* 162 */     return this.node;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/rss/RSSItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */