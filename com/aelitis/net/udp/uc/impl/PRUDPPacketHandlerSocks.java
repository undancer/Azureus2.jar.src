/*     */ package com.aelitis.net.udp.uc.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelectorFactory;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerStats;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReceiver;
/*     */ import com.aelitis.net.udp.uc.PRUDPPrimordialHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPRequestHandler;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.Proxy;
/*     */ import java.net.Socket;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
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
/*     */ public class PRUDPPacketHandlerSocks
/*     */   implements PRUDPPacketHandler, PRUDPPacketHandlerImpl.PacketTransformer
/*     */ {
/*     */   private static String socks_host;
/*     */   private static int socks_port;
/*     */   private static String socks_user;
/*     */   private static String socks_password;
/*     */   private final InetSocketAddress target;
/*     */   private Socket control_socket;
/*     */   private InetSocketAddress relay;
/*     */   private PRUDPPacketHandler delegate;
/*     */   private byte[] packet_out_header;
/*     */   
/*     */   static
/*     */   {
/*  63 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Proxy.Host", "Proxy.Port", "Proxy.Username", "Proxy.Password" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameter_name)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */         PRUDPPacketHandlerSocks.access$002(COConfigurationManager.getStringParameter("Proxy.Host").trim());
/*  77 */         PRUDPPacketHandlerSocks.access$102(Integer.parseInt(COConfigurationManager.getStringParameter("Proxy.Port").trim()));
/*  78 */         PRUDPPacketHandlerSocks.access$202(COConfigurationManager.getStringParameter("Proxy.Username").trim());
/*  79 */         PRUDPPacketHandlerSocks.access$302(COConfigurationManager.getStringParameter("Proxy.Password").trim());
/*     */         
/*  81 */         if (PRUDPPacketHandlerSocks.socks_user.equalsIgnoreCase("<none>")) {
/*  82 */           PRUDPPacketHandlerSocks.access$202("");
/*     */         }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacketHandlerSocks(InetSocketAddress _target)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 105 */     this.target = _target;
/*     */     
/* 107 */     boolean ok = false;
/*     */     
/* 109 */     AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*     */     
/* 111 */     Proxy proxy = proxy_selector.getSOCKSProxy(socks_host, socks_port, this.target);
/*     */     
/* 113 */     boolean proxy_connected = false;
/* 114 */     Throwable error = null;
/*     */     try
/*     */     {
/* 117 */       this.delegate = new PRUDPPacketHandlerImpl(0, null, this);
/*     */       
/* 119 */       this.control_socket = new Socket(Proxy.NO_PROXY);
/*     */       
/* 121 */       InetSocketAddress proxy_address = (InetSocketAddress)proxy.address();
/*     */       
/* 123 */       this.control_socket.connect(proxy_address);
/*     */       
/* 125 */       proxy_connected = true;
/*     */       
/* 127 */       DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(this.control_socket.getOutputStream(), 256));
/* 128 */       DataInputStream dis = new DataInputStream(this.control_socket.getInputStream());
/*     */       
/* 130 */       dos.writeByte(5);
/* 131 */       dos.writeByte(2);
/* 132 */       dos.writeByte(0);
/* 133 */       dos.writeByte(2);
/*     */       
/* 135 */       dos.flush();
/*     */       
/* 137 */       dis.readByte();
/*     */       
/* 139 */       byte method = dis.readByte();
/*     */       
/* 141 */       if ((method != 0) && (method != 2))
/*     */       {
/* 143 */         throw new IOException("SOCKS 5: no valid method [" + method + "]");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 148 */       if (method == 2)
/*     */       {
/* 150 */         dos.writeByte(1);
/* 151 */         dos.writeByte((byte)socks_user.length());
/* 152 */         dos.write(socks_user.getBytes());
/* 153 */         dos.writeByte((byte)socks_password.length());
/* 154 */         dos.write(socks_password.getBytes());
/*     */         
/* 156 */         dos.flush();
/*     */         
/* 158 */         dis.readByte();
/*     */         
/* 160 */         byte status = dis.readByte();
/*     */         
/* 162 */         if (status != 0)
/*     */         {
/* 164 */           throw new IOException("SOCKS 5: authentication fails [status=" + status + "]");
/*     */         }
/*     */       }
/*     */       
/*     */       String mapped_ip;
/*     */       String mapped_ip;
/* 170 */       if ((this.target.isUnresolved()) || (this.target.getAddress() == null))
/*     */       {
/*     */ 
/*     */ 
/* 174 */         mapped_ip = AEProxyFactory.getAddressMapper().internalise(this.target.getHostName());
/*     */       }
/*     */       else
/*     */       {
/* 178 */         mapped_ip = AddressUtils.getHostNameNoResolve(this.target);
/*     */       }
/*     */       
/* 181 */       dos.writeByte(5);
/* 182 */       dos.writeByte(3);
/* 183 */       dos.writeByte(0);
/*     */       
/* 185 */       dos.writeByte(1);
/* 186 */       dos.write(new byte[4]);
/*     */       
/* 188 */       dos.writeShort((short)this.delegate.getPort());
/*     */       
/* 190 */       dos.flush();
/*     */       
/* 192 */       dis.readByte();
/*     */       
/* 194 */       byte reply = dis.readByte();
/*     */       
/* 196 */       if (reply != 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 201 */         if ((reply == 69) && (proxy_address.getAddress().isLoopbackAddress()))
/*     */         {
/* 203 */           this.control_socket.close();
/*     */           
/* 205 */           this.control_socket = null;
/*     */           
/* 207 */           ok = true; return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */         throw new IOException("SOCKS 5: udp association fails [reply=" + reply + "]");
/*     */       }
/*     */       
/* 217 */       dis.readByte();
/*     */       
/*     */ 
/*     */ 
/* 221 */       byte atype = dis.readByte();
/*     */       InetAddress relay_address;
/* 223 */       InetAddress relay_address; if (atype == 1)
/*     */       {
/* 225 */         byte[] bytes = new byte[4];
/*     */         
/* 227 */         dis.readFully(bytes);
/*     */         
/* 229 */         relay_address = InetAddress.getByAddress(bytes);
/*     */       } else { InetAddress relay_address;
/* 231 */         if (atype == 3)
/*     */         {
/* 233 */           byte len = dis.readByte();
/*     */           
/* 235 */           byte[] bytes = new byte[len & 0xFF];
/*     */           
/* 237 */           dis.readFully(bytes);
/*     */           
/* 239 */           relay_address = InetAddress.getByName(new String(bytes));
/*     */         }
/*     */         else
/*     */         {
/* 243 */           byte[] bytes = new byte[16];
/*     */           
/* 245 */           dis.readFully(bytes);
/*     */           
/* 247 */           relay_address = InetAddress.getByAddress(bytes);
/*     */         }
/*     */       }
/*     */       
/* 251 */       int relay_port = dis.readByte() << 8 & 0xFF00 | dis.readByte() & 0xFF;
/*     */       
/* 253 */       if (relay_address.isAnyLocalAddress())
/*     */       {
/* 255 */         relay_address = this.control_socket.getInetAddress();
/*     */       }
/*     */       
/* 258 */       this.relay = new InetSocketAddress(relay_address, relay_port);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 263 */       ByteArrayOutputStream baos_temp = new ByteArrayOutputStream();
/* 264 */       DataOutputStream dos_temp = new DataOutputStream(baos_temp);
/*     */       
/* 266 */       dos_temp.writeByte(0);
/* 267 */       dos_temp.writeByte(0);
/* 268 */       dos_temp.writeByte(0);
/*     */       try
/*     */       {
/* 271 */         byte[] ip_bytes = HostNameToIPResolver.syncResolve(mapped_ip).getAddress();
/*     */         
/* 273 */         dos_temp.writeByte(ip_bytes.length == 4 ? 1 : 4);
/* 274 */         dos_temp.write(ip_bytes);
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 279 */         dos_temp.writeByte(3);
/* 280 */         dos_temp.writeByte((byte)mapped_ip.length());
/* 281 */         dos_temp.write(mapped_ip.getBytes());
/*     */       }
/*     */       
/*     */ 
/* 285 */       dos_temp.writeShort((short)this.target.getPort());
/*     */       
/* 287 */       dos_temp.flush();
/* 288 */       this.packet_out_header = baos_temp.toByteArray();
/*     */       
/*     */ 
/* 291 */       ok = true;
/*     */       
/* 293 */       Thread.sleep(1000L);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 297 */       error = e;
/*     */       
/* 299 */       throw new PRUDPPacketHandlerException("socks setup failed: " + Debug.getNestedExceptionMessage(e), e);
/*     */     }
/*     */     finally
/*     */     {
/* 303 */       if (!proxy_connected)
/*     */       {
/* 305 */         proxy_selector.connectFailed(proxy, error);
/*     */       }
/*     */       
/* 308 */       if (!ok)
/*     */       {
/*     */         try {
/* 311 */           this.control_socket.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 315 */           Debug.out(e);
/*     */         }
/*     */         finally
/*     */         {
/* 319 */           this.control_socket = null;
/*     */         }
/*     */         
/* 322 */         if (this.delegate != null) {
/*     */           try
/*     */           {
/* 325 */             this.delegate.destroy();
/*     */           }
/*     */           finally
/*     */           {
/* 329 */             this.delegate = null;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void transformSend(DatagramPacket packet)
/*     */   {
/* 340 */     if (this.relay == null)
/*     */     {
/* 342 */       return;
/*     */     }
/*     */     
/* 345 */     byte[] data = packet.getData();
/* 346 */     int data_len = packet.getLength();
/*     */     
/* 348 */     byte[] new_data = new byte[data_len + this.packet_out_header.length];
/*     */     
/* 350 */     System.arraycopy(this.packet_out_header, 0, new_data, 0, this.packet_out_header.length);
/* 351 */     System.arraycopy(data, 0, new_data, this.packet_out_header.length, data_len);
/*     */     
/* 353 */     packet.setData(new_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void transformReceive(DatagramPacket packet)
/*     */   {
/* 360 */     if (this.relay == null)
/*     */     {
/* 362 */       return;
/*     */     }
/*     */     
/* 365 */     byte[] data = packet.getData();
/* 366 */     int data_len = packet.getLength();
/*     */     
/* 368 */     DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data, 0, data_len));
/*     */     try
/*     */     {
/* 371 */       dis.readByte();
/* 372 */       dis.readByte();
/* 373 */       dis.readByte();
/*     */       
/* 375 */       byte atype = dis.readByte();
/*     */       
/* 377 */       int encap_len = 4;
/* 378 */       if (atype == 1)
/*     */       {
/* 380 */         encap_len += 4;
/*     */       }
/* 382 */       else if (atype == 3)
/*     */       {
/* 384 */         encap_len += 1 + (dis.readByte() & 0xFF);
/*     */       }
/*     */       else
/*     */       {
/* 388 */         encap_len += 16;
/*     */       }
/*     */       
/* 391 */       encap_len += 2;
/*     */       
/* 393 */       byte[] new_data = new byte[data_len - encap_len];
/*     */       
/* 395 */       System.arraycopy(data, encap_len, new_data, 0, data_len - encap_len);
/*     */       
/* 397 */       packet.setData(new_data);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 401 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkAddress(InetSocketAddress destination)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 411 */     if (!destination.equals(this.target))
/*     */     {
/* 413 */       throw new PRUDPPacketHandlerException("Destination mismatch");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendAndReceive(PRUDPPacket request_packet, InetSocketAddress destination_address, PRUDPPacketReceiver receiver, long timeout, int priority)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 427 */     checkAddress(destination_address);
/*     */     
/* 429 */     if (this.relay != null)
/*     */     {
/* 431 */       destination_address = this.relay;
/*     */     }
/*     */     
/* 434 */     this.delegate.sendAndReceive(request_packet, destination_address, receiver, timeout, priority);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacket sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 445 */     checkAddress(destination_address);
/*     */     
/* 447 */     if (this.relay != null)
/*     */     {
/* 449 */       destination_address = this.relay;
/*     */     }
/*     */     
/* 452 */     return this.delegate.sendAndReceive(auth, request_packet, destination_address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacket sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address, long timeout_millis)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 464 */     checkAddress(destination_address);
/*     */     
/* 466 */     if (this.relay != null)
/*     */     {
/* 468 */       destination_address = this.relay;
/*     */     }
/*     */     
/* 471 */     return this.delegate.sendAndReceive(auth, request_packet, destination_address, timeout_millis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacket sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address, long timeout_millis, int priority)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 484 */     checkAddress(destination_address);
/*     */     
/* 486 */     if (this.relay != null)
/*     */     {
/* 488 */       destination_address = this.relay;
/*     */     }
/*     */     
/* 491 */     return this.delegate.sendAndReceive(auth, request_packet, destination_address, timeout_millis, priority);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send(PRUDPPacket request_packet, InetSocketAddress destination_address)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 501 */     checkAddress(destination_address);
/*     */     
/* 503 */     if (this.relay != null)
/*     */     {
/* 505 */       destination_address = this.relay;
/*     */     }
/*     */     
/* 508 */     this.delegate.send(request_packet, destination_address);
/*     */   }
/*     */   
/*     */ 
/*     */   public PRUDPRequestHandler getRequestHandler()
/*     */   {
/* 514 */     return this.delegate.getRequestHandler();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRequestHandler(PRUDPRequestHandler request_handler)
/*     */   {
/* 521 */     this.delegate.setRequestHandler(request_handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void primordialSend(byte[] data, InetSocketAddress target)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 531 */     throw new PRUDPPacketHandlerException("not imp");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasPrimordialHandler()
/*     */   {
/* 537 */     return this.delegate.hasPrimordialHandler();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 555 */     return this.delegate.getPort();
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 561 */     return this.delegate.getBindIP();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDelays(int send_delay, int receive_delay, int queued_request_timeout)
/*     */   {
/* 570 */     this.delegate.setDelays(send_delay, receive_delay, queued_request_timeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExplicitBindAddress(InetAddress address)
/*     */   {
/* 577 */     this.delegate.setExplicitBindAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */   public PRUDPPacketHandlerStats getStats()
/*     */   {
/* 583 */     return this.delegate.getStats();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PRUDPPacketHandler openSession(InetSocketAddress target)
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 592 */     throw new PRUDPPacketHandlerException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void closeSession()
/*     */     throws PRUDPPacketHandlerException
/*     */   {
/* 600 */     if (this.control_socket != null) {
/*     */       try
/*     */       {
/* 603 */         this.control_socket.close();
/*     */         
/* 605 */         this.control_socket = null;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 609 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 613 */     if (this.delegate != null)
/*     */     {
/* 615 */       this.delegate.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/*     */     try
/*     */     {
/* 623 */       closeSession();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 627 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPrimordialHandler(PRUDPPrimordialHandler handler) {}
/*     */   
/*     */   public void removePrimordialHandler(PRUDPPrimordialHandler handler) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerSocks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */