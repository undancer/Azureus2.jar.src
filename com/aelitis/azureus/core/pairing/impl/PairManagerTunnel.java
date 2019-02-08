/*     */ package com.aelitis.azureus.core.pairing.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.pairing.PairedServiceRequestHandler;
/*     */ import java.net.InetAddress;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.URL;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ public class PairManagerTunnel
/*     */ {
/*  49 */   static final ResourceDownloaderFactory rdf = ;
/*     */   
/*     */   final PairingManagerTunnelHandler tunnel_handler;
/*     */   
/*     */   private final String tunnel_key;
/*     */   
/*     */   private final InetAddress originator;
/*     */   private final String sid;
/*     */   private final PairedServiceRequestHandler request_handler;
/*     */   private final SecretKeySpec key;
/*     */   final String tunnel_url;
/*     */   private final String endpoint_url;
/*  61 */   private long last_active = SystemTime.getMonotonousTime();
/*     */   
/*     */   private volatile boolean close_requested;
/*     */   
/*  65 */   private final long create_time = SystemTime.getMonotonousTime();
/*     */   
/*     */   private long last_request_time;
/*     */   
/*     */   private long request_count;
/*     */   
/*     */   private long bytes_in;
/*     */   private long bytes_out;
/*  73 */   private long last_fail_duration_secs = 0L;
/*  74 */   private int consec_fails = 0;
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
/*     */   protected PairManagerTunnel(PairingManagerTunnelHandler _tunnel_handler, String _tunnel_key, InetAddress _originator, String _sid, PairedServiceRequestHandler _request_handler, SecretKeySpec _key, String _tunnel_url, String _endpoint_url)
/*     */   {
/*  87 */     this.tunnel_handler = _tunnel_handler;
/*  88 */     this.tunnel_key = _tunnel_key;
/*  89 */     this.originator = _originator;
/*  90 */     this.sid = _sid;
/*  91 */     this.request_handler = _request_handler;
/*  92 */     this.key = _key;
/*  93 */     this.tunnel_url = _tunnel_url;
/*  94 */     this.endpoint_url = _endpoint_url;
/*     */     
/*  96 */     new AEThread2("PairManagerTunnel:runner")
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 102 */           String current_reply_params = null;
/* 103 */           byte[] current_reply_data = null;
/*     */           label802:
/* 105 */           while (!PairManagerTunnel.this.close_requested)
/*     */           {
/* 107 */             if (PairManagerTunnel.this.consec_fails > 1) {
/*     */               try
/*     */               {
/* 110 */                 Thread.sleep((1 << PairManagerTunnel.this.consec_fails - 1) * 1000);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/*     */ 
/* 116 */             long start_time = SystemTime.getMonotonousTime();
/*     */             
/*     */             try
/*     */             {
/* 120 */               String url_str = PairManagerTunnel.this.tunnel_url + "?server=true" + (current_reply_params == null ? "" : current_reply_params);
/*     */               
/* 122 */               if (PairManagerTunnel.this.last_fail_duration_secs > 0L)
/*     */               {
/* 124 */                 url_str = url_str + "&last_fail=" + PairManagerTunnel.this.last_fail_duration_secs;
/*     */                 
/* 126 */                 PairManagerTunnel.this.last_fail_duration_secs = 0L;
/*     */               }
/*     */               
/* 129 */               byte[] bytes_to_send = current_reply_data == null ? new byte[0] : current_reply_data;
/*     */               
/* 131 */               PairManagerTunnel.access$314(PairManagerTunnel.this, bytes_to_send.length);
/*     */               
/* 133 */               ResourceDownloader rd = PairManagerTunnel.rdf.create(new URL(url_str), bytes_to_send);
/*     */               
/* 135 */               rd.setProperty("URL_Connection", "Keep-Alive");
/* 136 */               rd.setProperty("URL_Read_Timeout", Integer.valueOf(300000));
/*     */               
/* 138 */               byte[] data = FileUtil.readInputStreamAsByteArray(rd.download());
/*     */               
/* 140 */               if (PairManagerTunnel.this.close_requested) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 145 */               PairManagerTunnel.access$414(PairManagerTunnel.this, data.length);
/*     */               
/* 147 */               long now = SystemTime.getMonotonousTime();
/*     */               
/* 149 */               PairManagerTunnel.this.last_active = now;
/*     */               
/* 151 */               current_reply_params = null;
/* 152 */               current_reply_data = null;
/*     */               
/* 154 */               List<String> cookies = (List)rd.getProperty("URL_Set-Cookie");
/*     */               
/* 156 */               boolean cookie_found = false;
/*     */               
/* 158 */               if (cookies != null)
/*     */               {
/* 160 */                 for (String cookie : cookies)
/*     */                 {
/* 162 */                   String name = "vuze_pair_server_reqs=";
/*     */                   
/* 164 */                   if (cookie.startsWith("vuze_pair_server_reqs="))
/*     */                   {
/* 166 */                     cookie_found = true;
/*     */                     
/* 168 */                     String value = cookie.substring("vuze_pair_server_reqs=".length());
/*     */                     
/* 170 */                     int pos = value.indexOf(';');
/*     */                     
/* 172 */                     value = value.substring(0, pos);
/*     */                     
/* 174 */                     String[] bits = value.split("&");
/*     */                     
/* 176 */                     if (bits.length > 0)
/*     */                     {
/* 178 */                       current_reply_params = "";
/*     */                       
/* 180 */                       data_pos = 0;
/*     */                       
/* 182 */                       List<byte[]> replies = new ArrayList();
/* 183 */                       int reply_length = 0;
/*     */                       
/* 185 */                       for (String bit : bits)
/*     */                       {
/* 187 */                         String[] temp = bit.split("=");
/*     */                         
/* 189 */                         if (temp.length == 2)
/*     */                         {
/* 191 */                           String lhs = temp[0].toLowerCase();
/*     */                           
/* 193 */                           if (lhs.startsWith("seq"))
/*     */                           {
/* 195 */                             int seq = Integer.parseInt(lhs.substring(3));
/* 196 */                             int len = Integer.parseInt(temp[1]);
/*     */                             
/* 198 */                             PairManagerTunnel.this.last_request_time = now;
/*     */                             
/* 200 */                             PairManagerTunnel.access$708(PairManagerTunnel.this);
/*     */                             
/* 202 */                             byte[] reply = PairManagerTunnel.this.processRequest(data, data_pos, len);
/*     */                             
/* 204 */                             replies.add(reply);
/*     */                             
/* 206 */                             reply_length += reply.length;
/*     */                             
/* 208 */                             data_pos += len;
/*     */                             
/* 210 */                             current_reply_params = current_reply_params + "&seq" + seq + "=" + reply.length;
/*     */                           }
/* 212 */                           else if (!lhs.equals("keepalive"))
/*     */                           {
/* 214 */                             if (lhs.equals("close"))
/*     */                             {
/* 216 */                               PairManagerTunnel.this.close_requested = true;
/*     */                             }
/*     */                           }
/*     */                         }
/*     */                       }
/* 221 */                       current_reply_data = new byte[reply_length];
/*     */                       
/* 223 */                       data_pos = 0;
/*     */                       
/* 225 */                       for (byte[] reply : replies)
/*     */                       {
/* 227 */                         System.arraycopy(reply, 0, current_reply_data, data_pos, reply.length);
/*     */                         
/* 229 */                         data_pos += reply.length;
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */               int data_pos;
/* 236 */               if (!cookie_found)
/*     */               {
/* 238 */                 throw new Exception("Cookie missing from reply");
/*     */               }
/*     */               
/* 241 */               PairManagerTunnel.this.consec_fails = 0;
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 245 */               long fail_time = SystemTime.getMonotonousTime();
/*     */               
/* 247 */               PairManagerTunnel.this.last_fail_duration_secs = ((fail_time - start_time) / 1000L);
/*     */               
/* 249 */               if ((PairManagerTunnel.this.isTimeout(e)) && (PairManagerTunnel.this.last_fail_duration_secs >= 20L))
/*     */               {
/*     */ 
/*     */ 
/* 253 */                 PairManagerTunnel.this.consec_fails = 0;
/*     */                 
/*     */                 break label802;
/*     */               }
/* 257 */               Debug.out(e);
/*     */               
/* 259 */               PairManagerTunnel.access$108(PairManagerTunnel.this);
/*     */               
/* 261 */               if (PairManagerTunnel.this.consec_fails <= 3) break label802;
/*     */             }
/* 263 */             break;
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 270 */           PairManagerTunnel.this.tunnel_handler.closeTunnel(PairManagerTunnel.this);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isTimeout(Throwable e)
/*     */   {
/* 280 */     if (e == null)
/*     */     {
/* 282 */       return false;
/*     */     }
/*     */     
/* 285 */     if ((e instanceof SocketTimeoutException))
/*     */     {
/* 287 */       return true;
/*     */     }
/*     */     
/* 290 */     String message = e.getMessage();
/*     */     
/* 292 */     if (message != null)
/*     */     {
/* 294 */       message = message.toLowerCase(Locale.US);
/*     */       
/* 296 */       if ((message.contains("timed out")) || (message.contains("timeout")))
/*     */       {
/* 298 */         return true;
/*     */       }
/*     */     }
/* 301 */     return isTimeout(e.getCause());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] processRequest(byte[] request, int offset, int length)
/*     */   {
/*     */     try
/*     */     {
/* 314 */       byte[] IV = new byte[16];
/*     */       
/* 316 */       System.arraycopy(request, offset, IV, 0, IV.length);
/*     */       
/* 318 */       Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*     */       
/* 320 */       decipher.init(2, this.key, new IvParameterSpec(IV));
/*     */       
/* 322 */       byte[] decrypted = decipher.doFinal(request, offset + 16, length - 16);
/*     */       
/*     */ 
/* 325 */       byte[] reply_bytes = this.request_handler.handleRequest(this.originator, this.endpoint_url, decrypted);
/*     */       
/*     */ 
/* 328 */       Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
/*     */       
/* 330 */       encipher.init(1, this.key);
/*     */       
/* 332 */       AlgorithmParameters params = encipher.getParameters();
/*     */       
/* 334 */       byte[] IV = ((IvParameterSpec)params.getParameterSpec(IvParameterSpec.class)).getIV();
/*     */       
/* 336 */       byte[] enc = encipher.doFinal(reply_bytes);
/*     */       
/* 338 */       byte[] rep_bytes = new byte[IV.length + enc.length];
/*     */       
/* 340 */       System.arraycopy(IV, 0, rep_bytes, 0, IV.length);
/* 341 */       System.arraycopy(enc, 0, rep_bytes, IV.length, enc.length);
/*     */       
/* 343 */       return rep_bytes;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 347 */       Debug.out(e);
/*     */     }
/* 349 */     return new byte[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getKey()
/*     */   {
/* 356 */     return this.tunnel_key;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastActive()
/*     */   {
/* 362 */     return this.last_active;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 368 */     this.close_requested = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 374 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 376 */     return "url=" + this.tunnel_url + ", age=" + (now - this.create_time) + ", last_req=" + (this.last_request_time == 0L ? "never" : String.valueOf(now - this.last_request_time)) + ", reqs=" + this.request_count + ", in=" + DisplayFormatters.formatByteCountToKiBEtc(this.bytes_in) + ", out=" + DisplayFormatters.formatByteCountToKiBEtc(this.bytes_out) + ", lf_secs=" + this.last_fail_duration_secs + ", consec_fail=" + this.consec_fails;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/impl/PairManagerTunnel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */