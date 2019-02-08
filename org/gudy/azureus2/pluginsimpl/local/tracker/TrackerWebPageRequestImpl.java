/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2.ExternalRequest;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
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
/*     */ 
/*     */ 
/*     */ public class TrackerWebPageRequestImpl
/*     */   implements TrackerWebPageRequest
/*     */ {
/*     */   private Tracker tracker;
/*     */   private TrackerWebContext context;
/*     */   private TRTrackerServerListener2.ExternalRequest request;
/*     */   
/*     */   protected TrackerWebPageRequestImpl(Tracker _tracker, TrackerWebContext _context, TRTrackerServerListener2.ExternalRequest _request)
/*     */   {
/*  59 */     this.tracker = _tracker;
/*  60 */     this.context = _context;
/*  61 */     this.request = _request;
/*     */   }
/*     */   
/*     */ 
/*     */   public Tracker getTracker()
/*     */   {
/*  67 */     return this.tracker;
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerWebContext getContext()
/*     */   {
/*  73 */     return this.context;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getURL()
/*     */   {
/*  79 */     return this.request.getURL();
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getAbsoluteURL()
/*     */   {
/*  85 */     return this.request.getAbsoluteURL();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getClientAddress()
/*     */   {
/*  91 */     return this.request.getClientAddress().getAddress().getHostAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getClientAddress2()
/*     */   {
/*  97 */     return this.request.getClientAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getLocalAddress()
/*     */   {
/* 103 */     return this.request.getLocalAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 109 */     return this.request.getUser();
/*     */   }
/*     */   
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 115 */     return this.request.getInputStream();
/*     */   }
/*     */   
/*     */ 
/*     */   protected OutputStream getOutputStream()
/*     */   {
/* 121 */     return this.request.getOutputStream();
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isActive()
/*     */   {
/* 127 */     return this.request.isActive();
/*     */   }
/*     */   
/*     */ 
/*     */   protected AsyncController getAsyncController()
/*     */   {
/* 133 */     return this.request.getAsyncController();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canKeepAlive()
/*     */   {
/* 139 */     return this.request.canKeepAlive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setKeepAlive(boolean ka)
/*     */   {
/* 146 */     this.request.setKeepAlive(ka);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHeader()
/*     */   {
/* 152 */     return this.request.getHeader();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getHeaders()
/*     */   {
/* 158 */     Map headers = new HashMap();
/*     */     
/* 160 */     String[] header_parts = this.request.getHeader().split("\r\n");
/*     */     
/* 162 */     headers.put("status", header_parts[0].trim());
/*     */     
/* 164 */     for (int i = 1; i < header_parts.length; i++)
/*     */     {
/* 166 */       String[] key_value = header_parts[i].split(":", 2);
/*     */       
/* 168 */       headers.put(key_value[0].trim().toLowerCase(MessageText.LOCALE_ENGLISH), key_value[1].trim());
/*     */     }
/*     */     
/* 171 */     return headers;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerWebPageRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */