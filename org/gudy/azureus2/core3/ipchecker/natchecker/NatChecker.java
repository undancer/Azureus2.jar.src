/*     */ package org.gudy.azureus2.core3.ipchecker.natchecker;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPMapping;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPluginService;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*     */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
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
/*     */ public class NatChecker
/*     */ {
/*  59 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   public static final int NAT_OK = 1;
/*     */   
/*     */   public static final int NAT_KO = 2;
/*     */   
/*     */   public static final int NAT_UNABLE = 3;
/*     */   private int result;
/*  67 */   private String additional_info = "";
/*     */   
/*     */ 
/*     */ 
/*     */   private InetAddress ip_address;
/*     */   
/*     */ 
/*     */ 
/*     */   public NatChecker(AzureusCore azureus_core, InetAddress bind_ip, int port, boolean http_test)
/*     */   {
/*  77 */     String check = "azureus_rand_" + String.valueOf(RandomUtils.nextInt(100000));
/*     */     
/*  79 */     if ((port < 0) || (port > 65535) || (port == Constants.INSTANCE_PORT))
/*     */     {
/*  81 */       this.result = 3;
/*     */       
/*  83 */       this.additional_info = "Invalid port"; return;
/*     */     }
/*     */     
/*     */ 
/*     */     NatCheckerServer server;
/*     */     
/*     */     try
/*     */     {
/*  91 */       server = new NatCheckerServer(bind_ip, port, check, http_test);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  95 */       this.result = 3;
/*     */       
/*  97 */       this.additional_info = ("Can't initialise server: " + Debug.getNestedExceptionMessage(e));
/*     */       
/*  99 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */     PluginInterface pi_upnp = azureus_core.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*     */     
/* 108 */     UPnPMapping new_mapping = null;
/*     */     
/* 110 */     String upnp_str = null;
/*     */     
/* 112 */     if (pi_upnp != null)
/*     */     {
/* 114 */       UPnPPlugin upnp = (UPnPPlugin)pi_upnp.getPlugin();
/*     */       
/* 116 */       UPnPMapping mapping = upnp.getMapping(true, port);
/*     */       
/* 118 */       if (mapping == null)
/*     */       {
/* 120 */         new_mapping = mapping = upnp.addMapping("NAT Tester", true, port, true);
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 125 */           Thread.sleep(500L);
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 130 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 134 */       UPnPPluginService[] services = upnp.getServices();
/*     */       
/* 136 */       if (services.length > 0)
/*     */       {
/* 138 */         upnp_str = "";
/*     */         
/* 140 */         for (int i = 0; i < services.length; i++)
/*     */         {
/* 142 */           UPnPPluginService service = services[i];
/*     */           
/* 144 */           upnp_str = upnp_str + (i == 0 ? "" : ",") + service.getInfo();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 151 */       server.start();
/*     */       
/* 153 */       String urlStr = "http://nettest.vuze.com/" + (http_test ? "httptest" : "nattest") + "?port=" + String.valueOf(port) + "&check=" + check;
/*     */       
/* 155 */       if (upnp_str != null)
/*     */       {
/* 157 */         urlStr = urlStr + "&upnp=" + URLEncoder.encode(upnp_str, "UTF8");
/*     */       }
/*     */       
/* 160 */       NetworkAdminASN net_asn = NetworkAdmin.getSingleton().getCurrentASN();
/*     */       
/* 162 */       String as = net_asn.getAS();
/* 163 */       String asn = net_asn.getASName();
/*     */       
/* 165 */       if (as.length() > 0)
/*     */       {
/* 167 */         urlStr = urlStr + "&as=" + URLEncoder.encode(as, "UTF8");
/* 168 */         urlStr = urlStr + "&asn=" + URLEncoder.encode(asn, "UTF8");
/*     */       }
/*     */       
/* 171 */       urlStr = urlStr + "&locale=" + MessageText.getCurrentLocale().toString();
/*     */       
/* 173 */       String ip_override = TRTrackerUtils.getPublicIPOverride();
/*     */       
/* 175 */       if (ip_override != null)
/*     */       {
/* 177 */         urlStr = urlStr + "&ip=" + ip_override;
/*     */       }
/*     */       
/* 180 */       URL url = new URL(urlStr);
/*     */       
/* 182 */       Properties http_properties = new Properties();
/*     */       
/* 184 */       http_properties.put("URL", url);
/* 185 */       http_properties.put("Raw-Request", Boolean.valueOf(true));
/*     */       try
/*     */       {
/* 188 */         ClientIDManagerImpl.getSingleton().generateHTTPProperties(null, http_properties);
/*     */       }
/*     */       catch (ClientIDException e)
/*     */       {
/* 192 */         throw new IOException(e.getMessage());
/*     */       }
/*     */       
/* 195 */       url = (URL)http_properties.get("URL");
/*     */       
/*     */ 
/* 198 */       HttpURLConnection con = (HttpURLConnection)url.openConnection();
/*     */       
/* 200 */       con.connect();
/*     */       
/* 202 */       ByteArrayOutputStream message = new ByteArrayOutputStream();
/* 203 */       InputStream is = con.getInputStream();
/*     */       
/* 205 */       byte[] data = new byte['Ѐ'];
/*     */       
/* 207 */       int expected_length = -1;
/*     */       
/*     */       for (;;)
/*     */       {
/* 211 */         int len = is.read(data);
/*     */         
/* 213 */         if (len <= 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 218 */         message.write(data, 0, len);
/*     */         
/* 220 */         if ((expected_length == -1) && (message.size() >= 4))
/*     */         {
/* 222 */           byte[] bytes = message.toByteArray();
/*     */           
/* 224 */           ByteBuffer bb = ByteBuffer.wrap(bytes);
/*     */           
/* 226 */           expected_length = bb.getInt();
/*     */           
/* 228 */           message = new ByteArrayOutputStream();
/*     */           
/* 230 */           if (bytes.length > 4)
/*     */           {
/* 232 */             message.write(bytes, 4, bytes.length - 4);
/*     */           }
/*     */         }
/*     */         
/* 236 */         if ((expected_length != -1) && (message.size() == expected_length)) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 242 */       Map map = BDecoder.decode(message.toByteArray());
/* 243 */       int reply_result = ((Long)map.get("result")).intValue();
/*     */       
/* 245 */       switch (reply_result) {
/*     */       case 0: 
/* 247 */         byte[] reason = (byte[])map.get("reason");
/* 248 */         if (reason != null) {
/* 249 */           Logger.log(new LogEvent(LOGID, 3, "NAT CHECK FAILED: " + new String(reason)));
/*     */         }
/*     */         
/* 252 */         this.result = 2;
/* 253 */         this.additional_info = (reason == null ? "Unknown" : new String(reason, "UTF8"));
/* 254 */         break;
/*     */       
/*     */       case 1: 
/* 257 */         this.result = 1;
/* 258 */         byte[] reply = (byte[])map.get("reply");
/* 259 */         if (reply != null) {
/* 260 */           this.additional_info = new String(reply, "UTF8");
/*     */         }
/*     */         
/*     */         break;
/*     */       default: 
/* 265 */         this.result = 3;
/* 266 */         this.additional_info = "Invalid response";
/*     */       }
/*     */       
/*     */       
/*     */ 
/* 271 */       byte[] ip_bytes = (byte[])map.get("ip_address");
/*     */       
/* 273 */       if (ip_bytes != null) {
/*     */         try
/*     */         {
/* 276 */           this.ip_address = InetAddress.getByAddress(ip_bytes);
/*     */ 
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 284 */       this.result = 3;
/* 285 */       this.additional_info = ("Error: " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */     finally
/*     */     {
/* 289 */       server.stopIt();
/*     */       
/* 291 */       if (new_mapping != null)
/*     */       {
/* 293 */         new_mapping.destroy();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getResult()
/*     */   {
/* 302 */     return this.result;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getExternalAddress()
/*     */   {
/* 308 */     return this.ip_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAdditionalInfo()
/*     */   {
/* 314 */     return this.additional_info;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/natchecker/NatChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */