/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageGenerator;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
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
/*     */ public class LocalResourceHTTPServer
/*     */   implements TrackerWebPageGenerator
/*     */ {
/*     */   private static final String my_ip = "127.0.0.1";
/*     */   private int my_port;
/*     */   private LoggerChannel logger;
/*     */   private int resource_id_next;
/*  46 */   private Map published_resources = new HashMap();
/*     */   
/*     */ 
/*     */   public LocalResourceHTTPServer(PluginInterface _plugin_interface, LoggerChannel _logger)
/*     */     throws Exception
/*     */   {
/*  52 */     this.logger = _logger;
/*     */     
/*  54 */     this.resource_id_next = new Random().nextInt(1073741823);
/*     */     
/*  56 */     InetAddress bind_address = InetAddress.getByName("127.0.0.1");
/*     */     
/*  58 */     TrackerWebContext context = _plugin_interface.getTracker().createWebContext("Director:localResource", 0, 1, bind_address);
/*     */     
/*     */ 
/*  61 */     this.my_port = context.getURLs()[0].getPort();
/*     */     
/*  63 */     if (this.logger != null) {
/*  64 */       this.logger.log("Local resource publisher running on 127.0.0.1:" + this.my_port);
/*     */     }
/*     */     
/*  67 */     context.addPageGenerator(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*     */     throws IOException
/*     */   {
/*  74 */     String path = request.getURL();
/*     */     
/*     */     File resource;
/*     */     
/*  78 */     synchronized (this)
/*     */     {
/*  80 */       resource = (File)this.published_resources.get(path);
/*     */     }
/*     */     
/*  83 */     if (resource == null)
/*     */     {
/*  85 */       return false;
/*     */     }
/*     */     
/*  88 */     return response.useFile(resource.getParent(), "/" + resource.getName());
/*     */   }
/*     */   
/*     */   public URL publishResource(File resource)
/*     */     throws Exception
/*     */   {
/*  94 */     synchronized (this)
/*     */     {
/*  96 */       resource = resource.getCanonicalFile();
/*     */       
/*  98 */       URL result = new URL("http://127.0.0.1:" + this.my_port + "/" + this.resource_id_next++ + "/" + resource.getName());
/*     */       
/*     */ 
/* 101 */       this.published_resources.put(result.getPath(), resource);
/*     */       
/* 103 */       if (this.logger != null) {
/* 104 */         this.logger.log("Local resource added: " + resource + " -> " + result);
/*     */       }
/*     */       
/* 107 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/LocalResourceHTTPServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */