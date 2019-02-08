/*     */ package com.aelitis.azureus.plugins.clientid;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDGenerator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClientIDPlugin
/*     */ {
/*     */   private static final String CLIENT_NAME = "Vuze 5.7.6.0";
/*     */   private static final String CLIENT_NAME_SM = "Vuze (Swarm Merging) 5.7.6.0";
/*     */   private static boolean send_os;
/*     */   
/*     */   public static void initialize(AzureusCore _core)
/*     */   {
/*  54 */     AzureusCore core = _core;
/*     */     
/*  56 */     String param = "Tracker Client Send OS and Java Version";
/*     */     
/*  58 */     COConfigurationManager.addAndFireParameterListener("Tracker Client Send OS and Java Version", new ParameterListener() {
/*     */       public void parameterChanged(String param) {
/*  60 */         ClientIDPlugin.access$002(COConfigurationManager.getBooleanParameter(param));
/*     */       }
/*     */       
/*  63 */     });
/*  64 */     ClientIDManagerImpl.getSingleton().setGenerator(new ClientIDGenerator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public byte[] generatePeerID(byte[] hash, boolean for_tracker)
/*     */       {
/*     */ 
/*     */ 
/*  72 */         return PeerUtils.createPeerID();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void generateHTTPProperties(byte[] hash, Properties properties)
/*     */       {
/*  80 */         ClientIDPlugin.doHTTPProperties(properties);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public String[] filterHTTP(byte[] hash, String[] lines_in)
/*     */       {
/*  88 */         return lines_in;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public Object getProperty(byte[] hash, String property_name)
/*     */       {
/*  96 */         if (property_name == "Client-Name")
/*     */         {
/*     */           try {
/*  99 */             GlobalManager gm = this.val$core.getGlobalManager();
/*     */             
/* 101 */             DownloadManager dm = gm.getDownloadManager(new HashWrapper(hash));
/*     */             
/* 103 */             if ((dm != null) && (gm.isSwarmMerging(dm) != null))
/*     */             {
/* 105 */               return "Vuze (Swarm Merging) 5.7.6.0";
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/* 110 */           return "Vuze 5.7.6.0";
/*     */         }
/* 112 */         if (property_name == "Messaging-Mode")
/*     */         {
/* 114 */           return Integer.valueOf(2);
/*     */         }
/*     */         
/*     */ 
/* 118 */         return null; } }, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void doHTTPProperties(Properties properties)
/*     */   {
/* 129 */     Boolean raw = (Boolean)properties.get("Raw-Request");
/*     */     
/* 131 */     if ((raw != null) && (raw.booleanValue()))
/*     */     {
/* 133 */       return;
/*     */     }
/*     */     
/* 136 */     String version = "5.7.6.0";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */     int pos = version.indexOf('_');
/*     */     
/* 144 */     if (pos != -1)
/*     */     {
/* 146 */       version = version.substring(0, pos);
/*     */     }
/*     */     
/* 149 */     String agent = "Azureus " + version;
/*     */     
/* 151 */     if (send_os)
/*     */     {
/* 153 */       agent = agent + ";" + Constants.OSName;
/*     */       
/* 155 */       agent = agent + ";Java " + Constants.JAVA_VERSION;
/*     */     }
/*     */     
/* 158 */     properties.put("User-Agent", agent);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/clientid/ClientIDPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */