/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import java.net.InetAddress;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class TCPNetworkManager
/*     */ {
/*  42 */   private static int WRITE_SELECT_LOOP_TIME = 25;
/*  43 */   private static int WRITE_SELECT_MIN_LOOP_TIME = 0;
/*  44 */   private static int READ_SELECT_LOOP_TIME = 25;
/*  45 */   private static int READ_SELECT_MIN_LOOP_TIME = 0;
/*     */   
/*     */   protected static int tcp_mss_size;
/*     */   
/*  49 */   private static final TCPNetworkManager instance = new TCPNetworkManager();
/*     */   
/*  51 */   public static TCPNetworkManager getSingleton() { return instance; }
/*     */   
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  57 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "TCP.Listen.Port.Enable", "network.tcp.connect.outbound.enable" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  68 */         TCPNetworkManager.TCP_INCOMING_ENABLED = TCPNetworkManager.TCP_OUTGOING_ENABLED = COConfigurationManager.getBooleanParameter("TCP.Listen.Port.Enable");
/*     */         
/*  70 */         if (TCPNetworkManager.TCP_OUTGOING_ENABLED)
/*     */         {
/*  72 */           TCPNetworkManager.TCP_OUTGOING_ENABLED = COConfigurationManager.getBooleanParameter("network.tcp.connect.outbound.enable");
/*     */         }
/*     */         
/*     */       }
/*  76 */     });
/*  77 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.tcp.read.select.time", "network.tcp.read.select.min.time", "network.tcp.write.select.time", "network.tcp.write.select.min.time" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */         TCPNetworkManager.access$002(COConfigurationManager.getIntParameter("network.tcp.write.select.time"));
/*  91 */         TCPNetworkManager.access$102(COConfigurationManager.getIntParameter("network.tcp.write.select.min.time"));
/*     */         
/*  93 */         TCPNetworkManager.access$202(COConfigurationManager.getIntParameter("network.tcp.read.select.time"));
/*  94 */         TCPNetworkManager.access$302(COConfigurationManager.getIntParameter("network.tcp.read.select.min.time"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean TCP_INCOMING_ENABLED;
/*     */   
/*     */   public static boolean TCP_OUTGOING_ENABLED;
/*     */   
/*     */   public static int getTcpMssSize()
/*     */   {
/* 106 */     return tcp_mss_size;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void refreshRates(int min_rate)
/*     */   {
/* 112 */     tcp_mss_size = COConfigurationManager.getIntParameter("network.tcp.mtu.size") - 40;
/*     */     
/* 114 */     if (tcp_mss_size > min_rate) { tcp_mss_size = min_rate - 1;
/*     */     }
/* 116 */     if (tcp_mss_size < 512) tcp_mss_size = 512;
/*     */   }
/*     */   
/* 119 */   private final VirtualChannelSelector read_selector = new VirtualChannelSelector("TCP network manager", 1, true);
/*     */   
/* 121 */   private final VirtualChannelSelector write_selector = new VirtualChannelSelector("TCP network manager", 4, true);
/*     */   
/*     */ 
/* 124 */   private final TCPConnectionManager connect_disconnect_manager = new TCPConnectionManager();
/*     */   
/* 126 */   private final IncomingSocketChannelManager incoming_socketchannel_manager = new IncomingSocketChannelManager("TCP.Listen.Port", "TCP.Listen.Port.Enable");
/*     */   
/*     */ 
/*     */   private long read_select_count;
/*     */   
/*     */   private long write_select_count;
/*     */   
/*     */ 
/*     */   protected TCPNetworkManager()
/*     */   {
/* 136 */     Set types = new HashSet();
/*     */     
/* 138 */     types.add("net.tcp.select.read.count");
/* 139 */     types.add("net.tcp.select.write.count");
/*     */     
/* 141 */     AzureusCoreStats.registerProvider(types, new AzureusCoreStatsProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void updateStats(Set types, Map values)
/*     */       {
/*     */ 
/*     */ 
/* 150 */         if (types.contains("net.tcp.select.read.count"))
/*     */         {
/* 152 */           values.put("net.tcp.select.read.count", new Long(TCPNetworkManager.this.read_select_count));
/*     */         }
/* 154 */         if (types.contains("net.tcp.select.write.count"))
/*     */         {
/* 156 */           values.put("net.tcp.select.write.count", new Long(TCPNetworkManager.this.write_select_count));
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 162 */     });
/* 163 */     AEThread2 read_selector_thread = new AEThread2("ReadController:ReadSelector", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/*     */           for (;;)
/*     */           {
/* 172 */             if (TCPNetworkManager.READ_SELECT_MIN_LOOP_TIME > 0)
/*     */             {
/* 174 */               long start = SystemTime.getHighPrecisionCounter();
/*     */               
/* 176 */               TCPNetworkManager.this.read_selector.select(TCPNetworkManager.READ_SELECT_LOOP_TIME);
/*     */               
/* 178 */               long duration = SystemTime.getHighPrecisionCounter() - start;
/*     */               
/* 180 */               duration /= 1000000L;
/*     */               
/* 182 */               long sleep = TCPNetworkManager.READ_SELECT_MIN_LOOP_TIME - duration;
/*     */               
/* 184 */               if (sleep > 0L) {
/*     */                 try
/*     */                 {
/* 187 */                   Thread.sleep(sleep);
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 194 */               TCPNetworkManager.this.read_selector.select(TCPNetworkManager.READ_SELECT_LOOP_TIME);
/*     */             }
/*     */             
/* 197 */             TCPNetworkManager.access$408(TCPNetworkManager.this);
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 203 */           if (!(t instanceof CancelledKeyException))
/*     */           {
/* 205 */             Debug.out("readSelectorLoop() EXCEPTION: ", t);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 211 */     };
/* 212 */     read_selector_thread.setPriority(8);
/* 213 */     read_selector_thread.start();
/*     */     
/*     */ 
/*     */ 
/* 217 */     AEThread2 write_selector_thread = new AEThread2("WriteController:WriteSelector", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/*     */           for (;;)
/*     */           {
/* 226 */             if (TCPNetworkManager.WRITE_SELECT_MIN_LOOP_TIME > 0)
/*     */             {
/* 228 */               long start = SystemTime.getHighPrecisionCounter();
/*     */               
/* 230 */               TCPNetworkManager.this.write_selector.select(TCPNetworkManager.WRITE_SELECT_LOOP_TIME);
/*     */               
/* 232 */               long duration = SystemTime.getHighPrecisionCounter() - start;
/*     */               
/* 234 */               duration /= 1000000L;
/*     */               
/* 236 */               long sleep = TCPNetworkManager.WRITE_SELECT_MIN_LOOP_TIME - duration;
/*     */               
/* 238 */               if (sleep > 0L) {
/*     */                 try
/*     */                 {
/* 241 */                   Thread.sleep(sleep);
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 248 */               TCPNetworkManager.this.write_selector.select(TCPNetworkManager.WRITE_SELECT_LOOP_TIME);
/*     */               
/* 250 */               TCPNetworkManager.access$508(TCPNetworkManager.this);
/*     */             }
/*     */           }
/*     */         } catch (Throwable t) {
/* 254 */           Debug.out("writeSelectorLoop() EXCEPTION: ", t);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 259 */     };
/* 260 */     write_selector_thread.setPriority(8);
/* 261 */     write_selector_thread.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExplicitBindAddress(InetAddress address)
/*     */   {
/* 268 */     this.incoming_socketchannel_manager.setExplicitBindAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearExplicitBindAddress()
/*     */   {
/* 274 */     this.incoming_socketchannel_manager.clearExplicitBindAddress();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isEffectiveBindAddress(InetAddress address)
/*     */   {
/* 281 */     return this.incoming_socketchannel_manager.isEffectiveBindAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TCPConnectionManager getConnectDisconnectManager()
/*     */   {
/* 292 */     return this.connect_disconnect_manager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VirtualChannelSelector getReadSelector()
/*     */   {
/* 299 */     return this.read_selector;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VirtualChannelSelector getWriteSelector()
/*     */   {
/* 306 */     return this.write_selector;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTCPListenerEnabled()
/*     */   {
/* 312 */     return this.incoming_socketchannel_manager.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTCPListeningPortNumber()
/*     */   {
/* 323 */     return this.incoming_socketchannel_manager.getTCPListeningPortNumber();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLastIncomingNonLocalConnectionTime()
/*     */   {
/* 329 */     return this.incoming_socketchannel_manager.getLastNonLocalConnectionTime();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TCPNetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */