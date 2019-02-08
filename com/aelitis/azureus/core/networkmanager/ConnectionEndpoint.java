/*     */ package com.aelitis.azureus.core.networkmanager;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class ConnectionEndpoint
/*     */ {
/*     */   private final InetSocketAddress notional_address;
/*     */   private ProtocolEndpoint[] protocols;
/*     */   private Map<String, Object> properties;
/*     */   
/*     */   public ConnectionEndpoint(InetSocketAddress _notional_address)
/*     */   {
/*  50 */     this.notional_address = _notional_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addProperties(Map<String, Object> p)
/*     */   {
/*  57 */     synchronized (this)
/*     */     {
/*  59 */       if (this.properties == null)
/*     */       {
/*  61 */         this.properties = new HashMap(p);
/*     */       }
/*     */       else
/*     */       {
/*  65 */         this.properties.putAll(p);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getProperty(String name)
/*     */   {
/*  74 */     synchronized (this)
/*     */     {
/*  76 */       if (this.properties != null)
/*     */       {
/*  78 */         return this.properties.get(name);
/*     */       }
/*     */     }
/*     */     
/*  82 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getNotionalAddress()
/*     */   {
/*  88 */     return this.notional_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public ProtocolEndpoint[] getProtocols()
/*     */   {
/*  94 */     if (this.protocols == null)
/*     */     {
/*  96 */       return new ProtocolEndpoint[0];
/*     */     }
/*     */     
/*  99 */     return this.protocols;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addProtocol(ProtocolEndpoint ep)
/*     */   {
/* 106 */     if (this.protocols == null)
/*     */     {
/* 108 */       this.protocols = new ProtocolEndpoint[] { ep };
/*     */     }
/*     */     else
/*     */     {
/* 112 */       for (int i = 0; i < this.protocols.length; i++)
/*     */       {
/* 114 */         if (this.protocols[i] == ep)
/*     */         {
/* 116 */           return;
/*     */         }
/*     */       }
/*     */       
/* 120 */       ProtocolEndpoint[] new_ep = new ProtocolEndpoint[this.protocols.length + 1];
/*     */       
/* 122 */       System.arraycopy(this.protocols, 0, new_ep, 0, this.protocols.length);
/*     */       
/* 124 */       new_ep[this.protocols.length] = ep;
/*     */       
/* 126 */       this.protocols = new_ep;
/*     */     }
/*     */     
/* 129 */     ep.setConnectionEndpoint(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public ConnectionEndpoint getLANAdjustedEndpoint()
/*     */   {
/* 135 */     ConnectionEndpoint result = new ConnectionEndpoint(this.notional_address);
/*     */     
/* 137 */     for (int i = 0; i < this.protocols.length; i++)
/*     */     {
/* 139 */       ProtocolEndpoint ep = this.protocols[i];
/*     */       
/* 141 */       InetSocketAddress address = ep.getAdjustedAddress(true);
/*     */       
/* 143 */       ProtocolEndpointFactory.createEndpoint(ep.getType(), result, address);
/*     */     }
/*     */     
/* 146 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectionAttempt connectOutbound(final boolean connect_with_crypto, final boolean allow_fallback, final byte[][] shared_secrets, ByteBuffer initial_data, final int priority, final Transport.ConnectListener listener)
/*     */   {
/* 158 */     if (this.protocols.length == 1)
/*     */     {
/* 160 */       ProtocolEndpoint protocol = this.protocols[0];
/*     */       
/* 162 */       final Transport transport = protocol.connectOutbound(connect_with_crypto, allow_fallback, shared_secrets, initial_data, priority, listener);
/*     */       
/* 164 */       new ConnectionAttempt()
/*     */       {
/*     */ 
/*     */         public void abandon()
/*     */         {
/*     */ 
/* 170 */           if (transport != null)
/*     */           {
/* 172 */             transport.close("Connection attempt abandoned");
/*     */           }
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/* 179 */     final boolean[] connected = { false };
/* 180 */     final boolean[] abandoned = { false };
/*     */     
/* 182 */     final List<Transport> transports = new ArrayList(this.protocols.length);
/*     */     
/* 184 */     final Transport.ConnectListener listener_delegate = new Transport.ConnectListener()
/*     */     {
/*     */ 
/*     */ 
/* 188 */       private int timeout = Integer.MIN_VALUE;
/*     */       
/*     */       private int fail_count;
/*     */       
/*     */ 
/*     */       public int connectAttemptStarted(int default_connect_timeout)
/*     */       {
/* 195 */         synchronized (connected)
/*     */         {
/* 197 */           if (this.timeout == Integer.MIN_VALUE)
/*     */           {
/*     */ 
/*     */ 
/* 201 */             this.timeout = listener.connectAttemptStarted(default_connect_timeout);
/*     */           }
/*     */           
/* 204 */           return this.timeout;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectSuccess(Transport transport, ByteBuffer remaining_initial_data)
/*     */       {
/*     */         boolean disconnect;
/*     */         
/*     */ 
/* 215 */         synchronized (connected)
/*     */         {
/* 217 */           disconnect = abandoned[0];
/*     */           
/* 219 */           if (!disconnect)
/*     */           {
/* 221 */             if (connected[0] == 0)
/*     */             {
/* 223 */               connected[0] = true;
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 229 */               disconnect = true;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 234 */         if (disconnect)
/*     */         {
/* 236 */           transport.close("Transparent not required");
/*     */         }
/*     */         else
/*     */         {
/* 240 */           listener.connectSuccess(transport, remaining_initial_data);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void connectFailure(Throwable failure_msg)
/*     */       {
/*     */         boolean inform;
/*     */         
/* 250 */         synchronized (connected)
/*     */         {
/* 252 */           this.fail_count += 1;
/*     */           
/* 254 */           inform = this.fail_count == ConnectionEndpoint.this.protocols.length;
/*     */         }
/*     */         
/* 257 */         if (inform)
/*     */         {
/* 259 */           listener.connectFailure(failure_msg);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public Object getConnectionProperty(String property_name)
/*     */       {
/* 267 */         return listener.getConnectionProperty(property_name);
/*     */       }
/*     */       
/* 270 */     };
/* 271 */     boolean ok = true;
/*     */     
/* 273 */     if (this.protocols.length != 2)
/*     */     {
/* 275 */       ok = false;
/*     */     }
/*     */     else {
/* 278 */       ProtocolEndpoint p1 = this.protocols[0];
/* 279 */       ProtocolEndpoint p2 = this.protocols[1];
/*     */       
/* 281 */       if ((p1.getType() != 1) || (p2.getType() != 3))
/*     */       {
/* 283 */         if ((p2.getType() == 1) && (p1.getType() == 3))
/*     */         {
/* 285 */           ProtocolEndpoint temp = p1;
/* 286 */           p1 = p2;
/* 287 */           p2 = temp;
/*     */         }
/*     */         else {
/* 290 */           ok = false;
/*     */         }
/*     */       }
/* 293 */       if (ok)
/*     */       {
/*     */         ByteBuffer initial_data_copy;
/*     */         
/*     */         final ByteBuffer initial_data_copy;
/*     */         
/* 299 */         if (initial_data != null)
/*     */         {
/* 301 */           initial_data_copy = initial_data.duplicate();
/*     */         }
/*     */         else
/*     */         {
/* 305 */           initial_data_copy = null;
/*     */         }
/*     */         
/* 308 */         Transport transport = p2.connectOutbound(connect_with_crypto, allow_fallback, shared_secrets, initial_data, priority, new ConnectListenerEx(listener_delegate, null));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 317 */         transports.add(transport);
/*     */         
/* 319 */         final ProtocolEndpoint tcp_ep = p1;
/*     */         
/* 321 */         SimpleTimer.addEvent("delay:tcp:connect", SystemTime.getCurrentTime() + 750L, false, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 331 */             synchronized (connected)
/*     */             {
/* 333 */               if ((connected[0] != 0) || (abandoned[0] != 0))
/*     */               {
/* 335 */                 return;
/*     */               }
/*     */             }
/*     */             
/* 339 */             Transport transport = tcp_ep.connectOutbound(connect_with_crypto, allow_fallback, shared_secrets, initial_data_copy, priority, new ConnectionEndpoint.ConnectListenerEx(listener_delegate, null));
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 348 */             synchronized (connected)
/*     */             {
/* 350 */               if (abandoned[0] != 0)
/*     */               {
/* 352 */                 transport.close("Connection attempt abandoned");
/*     */               }
/*     */               else
/*     */               {
/* 356 */                 transports.add(transport);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 363 */     if (!ok)
/*     */     {
/* 365 */       Debug.out("No supportified!");
/*     */       
/* 367 */       listener.connectFailure(new Exception("Not Supported"));
/*     */     }
/*     */     
/* 370 */     new ConnectionAttempt()
/*     */     {
/*     */       public void abandon()
/*     */       {
/*     */         List<Transport> to_kill;
/*     */         
/*     */ 
/*     */ 
/* 378 */         synchronized (connected)
/*     */         {
/* 380 */           abandoned[0] = true;
/*     */           
/* 382 */           to_kill = new ArrayList(transports);
/*     */         }
/*     */         
/* 385 */         for (Transport transport : to_kill)
/*     */         {
/* 387 */           transport.close("Connection attempt abandoned");
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 397 */     String str = "[";
/*     */     
/* 399 */     for (int i = 0; i < this.protocols.length; i++)
/*     */     {
/* 401 */       str = str + (i == 0 ? "" : ",") + this.protocols[i].getDescription();
/*     */     }
/*     */     
/* 404 */     return str + "]";
/*     */   }
/*     */   
/*     */ 
/*     */   private static class ConnectListenerEx
/*     */     implements Transport.ConnectListener
/*     */   {
/*     */     private final Transport.ConnectListener listener;
/*     */     
/*     */     private boolean ok;
/*     */     
/*     */     private boolean failed;
/*     */     
/*     */ 
/*     */     private ConnectListenerEx(Transport.ConnectListener _listener)
/*     */     {
/* 420 */       this.listener = _listener;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int connectAttemptStarted(int default_connect_timeout)
/*     */     {
/* 427 */       return this.listener.connectAttemptStarted(default_connect_timeout);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void connectSuccess(Transport transport, ByteBuffer remaining_initial_data)
/*     */     {
/* 435 */       synchronized (this)
/*     */       {
/* 437 */         if ((this.ok) || (this.failed))
/*     */         {
/* 439 */           if (this.ok)
/*     */           {
/* 441 */             Debug.out("Double doo doo");
/*     */           }
/*     */           
/* 444 */           return;
/*     */         }
/*     */         
/* 447 */         this.ok = true;
/*     */       }
/*     */       
/* 450 */       this.listener.connectSuccess(transport, remaining_initial_data);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void connectFailure(Throwable failure_msg)
/*     */     {
/* 457 */       synchronized (this)
/*     */       {
/* 459 */         if ((this.ok) || (this.failed))
/*     */         {
/* 461 */           return;
/*     */         }
/*     */         
/* 464 */         this.failed = true;
/*     */       }
/*     */       
/* 467 */       this.listener.connectFailure(failure_msg);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object getConnectionProperty(String property_name)
/*     */     {
/* 474 */       return this.listener.getConnectionProperty(property_name);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/ConnectionEndpoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */