/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.xml.rss;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSChannel;
/*     */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSFeed;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;
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
/*     */ public class RSSFeedImpl
/*     */   implements RSSFeed
/*     */ {
/*     */   private boolean is_atom;
/*     */   private RSSChannel[] channels;
/*     */   
/*     */   public RSSFeedImpl(Utilities utilities, URL source_url, ResourceDownloader downloader)
/*     */     throws ResourceDownloaderException, SimpleXMLParserDocumentException
/*     */   {
/*  57 */     this(utilities, source_url, downloader.download());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RSSFeedImpl(Utilities utilities, URL source_url, InputStream is)
/*     */     throws SimpleXMLParserDocumentException
/*     */   {
/*     */     try
/*     */     {
/*  69 */       SimpleXMLParserDocument doc = utilities.getSimpleXMLParserDocumentFactory().create(source_url, is);
/*     */       
/*  71 */       String doc_name = doc.getName();
/*     */       
/*  73 */       this.is_atom = ((doc_name != null) && (doc_name.equalsIgnoreCase("feed")));
/*     */       
/*  75 */       List chans = new ArrayList();
/*     */       
/*  77 */       if (this.is_atom)
/*     */       {
/*  79 */         chans.add(new RSSChannelImpl(doc, true));
/*     */       }
/*     */       else
/*     */       {
/*  83 */         SimpleXMLParserDocumentNode[] xml_channels = doc.getChildren();
/*     */         
/*  85 */         for (int i = 0; i < xml_channels.length; i++)
/*     */         {
/*  87 */           SimpleXMLParserDocumentNode xml_channel = xml_channels[i];
/*     */           
/*  89 */           String name = xml_channel.getName().toLowerCase();
/*     */           
/*  91 */           if (name.equals("channel"))
/*     */           {
/*  93 */             chans.add(new RSSChannelImpl(xml_channel, false));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  98 */       this.channels = new RSSChannel[chans.size()];
/*     */       
/* 100 */       chans.toArray(this.channels); return;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 105 */         is.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 109 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAtomFeed()
/*     */   {
/* 117 */     return this.is_atom;
/*     */   }
/*     */   
/*     */ 
/*     */   public RSSChannel[] getChannels()
/*     */   {
/* 123 */     return this.channels;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/xml/rss/RSSFeedImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */