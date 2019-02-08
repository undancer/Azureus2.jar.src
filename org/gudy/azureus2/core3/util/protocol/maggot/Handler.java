/*     */ package org.gudy.azureus2.core3.util.protocol.maggot;
/*     */ 
/*     */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLStreamHandler;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.protocol.magnet.MagnetConnection2;
/*     */ import org.gudy.azureus2.core3.util.protocol.magnet.MagnetConnection2.MagnetHandler;
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
/*     */ public class Handler
/*     */   extends URLStreamHandler
/*     */ {
/*     */   public URLConnection openConnection(URL u)
/*     */   {
/*  49 */     new MagnetConnection2(u, new MagnetConnection2.MagnetHandler()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void process(URL maggot, OutputStream os)
/*     */         throws IOException
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */         String maggot_str = maggot.toExternalForm();
/*     */         
/*  66 */         int pos = maggot_str.indexOf('?');
/*     */         
/*     */         String rem;
/*     */         String rem;
/*  70 */         if (pos == -1)
/*     */         {
/*  72 */           rem = "";
/*     */         }
/*     */         else
/*     */         {
/*  76 */           rem = "&" + maggot_str.substring(pos + 1);
/*     */           
/*  78 */           maggot_str = maggot_str.substring(0, pos);
/*     */         }
/*     */         
/*  81 */         pos = maggot_str.lastIndexOf("/");
/*     */         
/*  83 */         maggot_str = maggot_str.substring(pos + 1);
/*     */         
/*  85 */         String[] bits = maggot_str.split(":");
/*     */         
/*  87 */         String btih_str = bits[0];
/*  88 */         String sha1_str = bits[1];
/*     */         
/*  90 */         String magnet_str = "magnet:?xt=urn:btih:" + Base32.encode(ByteFormatter.decodeString(btih_str));
/*     */         
/*  92 */         magnet_str = magnet_str + rem + "&maggot_sha1=" + sha1_str;
/*     */         
/*  94 */         URL magnet = new URL(magnet_str);
/*     */         
/*  96 */         String get = "/download/" + magnet.toString().substring(7) + " HTTP/1.0\r\n\r\n";
/*     */         
/*  98 */         MagnetURIHandler.getSingleton().process(get, new ByteArrayInputStream(new byte[0]), os);
/*     */       }
/*     */     });
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
/*     */   protected void parseURL(URL u, String spec, int start, int limit)
/*     */   {
/* 113 */     spec = spec.substring(start);
/*     */     
/* 115 */     while ((spec.length() > 0) && ("/?".indexOf(spec.charAt(0)) != -1))
/*     */     {
/* 117 */       spec = spec.substring(1);
/*     */     }
/*     */     
/* 120 */     setURL(u, "maggot", spec, -1, spec, null, "", null, null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/maggot/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */