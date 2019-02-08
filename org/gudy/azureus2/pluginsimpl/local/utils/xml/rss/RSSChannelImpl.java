/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.rss;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSChannel;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSItem;
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
/*     */ 
/*     */ public class RSSChannelImpl
/*     */   implements RSSChannel
/*     */ {
/*     */   private SimpleXMLParserDocumentNode node;
/*     */   private RSSItem[] items;
/*     */   private boolean is_atom;
/*     */   
/*     */   protected RSSChannelImpl(SimpleXMLParserDocumentNode _node, boolean _is_atom)
/*     */   {
/*  52 */     this.node = _node;
/*  53 */     this.is_atom = _is_atom;
/*     */     
/*  55 */     SimpleXMLParserDocumentNode[] xml_items = this.node.getChildren();
/*     */     
/*  57 */     List its = new ArrayList();
/*     */     
/*  59 */     for (int i = 0; i < xml_items.length; i++)
/*     */     {
/*  61 */       SimpleXMLParserDocumentNode xml_item = xml_items[i];
/*     */       
/*  63 */       if (xml_item.getName().equalsIgnoreCase(this.is_atom ? "entry" : "item"))
/*     */       {
/*  65 */         its.add(new RSSItemImpl(xml_item, this.is_atom));
/*     */       }
/*     */     }
/*     */     
/*  69 */     this.items = new RSSItem[its.size()];
/*     */     
/*  71 */     its.toArray(this.items);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  77 */     return this.node.getChild("title").getValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*     */     String[] fields;
/*     */     String[] fields;
/*  85 */     if (this.is_atom)
/*     */     {
/*  87 */       fields = new String[] { "summary", "description" };
/*     */     }
/*     */     else
/*     */     {
/*  91 */       fields = new String[] { "description", "summary" };
/*     */     }
/*     */     
/*  94 */     for (String field : fields)
/*     */     {
/*  96 */       SimpleXMLParserDocumentNode x = this.node.getChild(field);
/*     */       
/*  98 */       if (x != null)
/*     */       {
/* 100 */         return x.getValue();
/*     */       }
/*     */     }
/*     */     
/* 104 */     return null;
/*     */   }
/*     */   
/*     */   public URL getLink()
/*     */   {
/*     */     try
/*     */     {
/* 111 */       return new URL(this.node.getChild("link").getValue());
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 115 */       Debug.printStackTrace(e);
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getPublicationDate()
/*     */   {
/* 126 */     SimpleXMLParserDocumentNode pd = this.node.getChild(this.is_atom ? "updated" : "pubdate");
/*     */     
/* 128 */     if (pd == null)
/*     */     {
/* 130 */       return null;
/*     */     }
/*     */     
/* 133 */     if (this.is_atom)
/*     */     {
/* 135 */       return RSSUtils.parseAtomDate(pd.getValue());
/*     */     }
/*     */     
/*     */ 
/* 139 */     return RSSUtils.parseRSSDate(pd.getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RSSItem[] getItems()
/*     */   {
/* 146 */     return this.items;
/*     */   }
/*     */   
/*     */ 
/*     */   public SimpleXMLParserDocumentNode getNode()
/*     */   {
/* 152 */     return this.node;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/rss/RSSChannelImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */