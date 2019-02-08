/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoderPHE;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ public class UDPNetworkManager
/*     */ {
/*     */   public static final boolean MINIMISE_OVERHEADS = true;
/*  38 */   public static final int MIN_INCOMING_INITIAL_PACKET_SIZE = ProtocolDecoderPHE.MIN_INCOMING_INITIAL_PACKET_SIZE;
/*  39 */   public static final int MAX_INCOMING_INITIAL_PACKET_SIZE = ProtocolDecoderPHE.getMaxIncomingInitialPacketSize(true);
/*     */   
/*     */   private static final int MIN_MSS = 128;
/*     */   private static final int MAX_MSS = 8192;
/*     */   private static int udp_mss_size;
/*     */   public static boolean UDP_INCOMING_ENABLED;
/*     */   public static boolean UDP_OUTGOING_ENABLED;
/*     */   private static UDPNetworkManager singleton;
/*     */   
/*     */   static
/*     */   {
/*  50 */     COConfigurationManager.addAndFireParameterListener("UDP.Listen.Port.Enable", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*  58 */         UDPNetworkManager.UDP_INCOMING_ENABLED = UDPNetworkManager.UDP_OUTGOING_ENABLED = COConfigurationManager.getBooleanParameter(name);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*  63 */   public static int getUdpMssSize() { return udp_mss_size; }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void refreshRates(int min_rate)
/*     */   {
/*  69 */     udp_mss_size = COConfigurationManager.getIntParameter("network.udp.mtu.size") - 40;
/*     */     
/*  71 */     if (udp_mss_size > min_rate) { udp_mss_size = min_rate - 1;
/*     */     }
/*  73 */     if (udp_mss_size < 128) { udp_mss_size = 128;
/*     */     }
/*  75 */     if (udp_mss_size > 8192) { udp_mss_size = 8192;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static UDPNetworkManager getSingleton()
/*     */   {
/*  84 */     synchronized (UDPNetworkManager.class)
/*     */     {
/*  86 */       if (singleton == null)
/*     */       {
/*  88 */         singleton = new UDPNetworkManager();
/*     */       }
/*     */     }
/*     */     
/*  92 */     return singleton;
/*     */   }
/*     */   
/*  95 */   private int udp_listen_port = -1;
/*  96 */   private int udp_non_data_listen_port = -1;
/*     */   
/*     */   private UDPConnectionManager _connection_manager;
/*     */   
/*     */ 
/*     */   protected UDPNetworkManager()
/*     */   {
/* 103 */     COConfigurationManager.addAndFireParameterListener("UDP.Listen.Port", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/* 110 */         int port = COConfigurationManager.getIntParameter(name);
/*     */         
/* 112 */         if (port == UDPNetworkManager.this.udp_listen_port)
/*     */         {
/* 114 */           return;
/*     */         }
/*     */         
/* 117 */         if ((port < 0) || (port > 65535) || (port == Constants.INSTANCE_PORT))
/*     */         {
/* 119 */           String msg = "Invalid incoming UDP listen port configured, " + port + ". The port has been reset. Please check your config!";
/*     */           
/* 121 */           Debug.out(msg);
/*     */           
/* 123 */           Logger.log(new LogAlert(false, 3, msg));
/*     */           
/* 125 */           UDPNetworkManager.this.udp_listen_port = RandomUtils.generateRandomNetworkListenPort();
/*     */           
/* 127 */           COConfigurationManager.setParameter(name, UDPNetworkManager.this.udp_listen_port);
/*     */         }
/*     */         else
/*     */         {
/* 131 */           UDPNetworkManager.this.udp_listen_port = port;
/*     */         }
/*     */         
/*     */       }
/* 135 */     });
/* 136 */     COConfigurationManager.addAndFireParameterListener("UDP.NonData.Listen.Port", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/* 143 */         int port = COConfigurationManager.getIntParameter(name);
/*     */         
/* 145 */         if (port == UDPNetworkManager.this.udp_non_data_listen_port)
/*     */         {
/* 147 */           return;
/*     */         }
/*     */         
/* 150 */         if ((port < 0) || (port > 65535) || (port == Constants.INSTANCE_PORT))
/*     */         {
/* 152 */           String msg = "Invalid incoming UDP non-data listen port configured, " + port + ". The port has been reset. Please check your config!";
/*     */           
/* 154 */           Debug.out(msg);
/*     */           
/* 156 */           Logger.log(new LogAlert(false, 3, msg));
/*     */           
/* 158 */           UDPNetworkManager.this.udp_non_data_listen_port = RandomUtils.generateRandomNetworkListenPort();
/*     */           
/* 160 */           COConfigurationManager.setParameter(name, UDPNetworkManager.this.udp_non_data_listen_port);
/*     */         }
/*     */         else
/*     */         {
/* 164 */           UDPNetworkManager.this.udp_non_data_listen_port = port;
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUDPListenerEnabled()
/*     */   {
/* 173 */     return UDP_INCOMING_ENABLED;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPListeningPortNumber()
/*     */   {
/* 179 */     return this.udp_listen_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUDPNonDataListenerEnabled()
/*     */   {
/* 185 */     return UDP_INCOMING_ENABLED;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUDPNonDataListeningPortNumber()
/*     */   {
/* 191 */     return this.udp_non_data_listen_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public UDPConnectionManager getConnectionManager()
/*     */   {
/* 197 */     synchronized (this)
/*     */     {
/* 199 */       if (this._connection_manager == null)
/*     */       {
/* 201 */         this._connection_manager = new UDPConnectionManager();
/*     */       }
/*     */     }
/*     */     
/* 205 */     return this._connection_manager;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPNetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */