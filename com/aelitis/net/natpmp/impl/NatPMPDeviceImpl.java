/*     */ package com.aelitis.net.natpmp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.NetUtils;
/*     */ import com.aelitis.net.natpmp.NATPMPDeviceAdapter;
/*     */ import com.aelitis.net.natpmp.NatPMPDevice;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.PortUnreachableException;
/*     */ import java.net.SocketTimeoutException;
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
/*     */ 
/*     */ public class NatPMPDeviceImpl
/*     */   implements NatPMPDevice
/*     */ {
/*     */   static final int NATMAP_VER = 0;
/*     */   static final int NATMAP_PORT = 5351;
/*     */   static final int NATMAP_RESPONSE_MASK = 128;
/*     */   static final int NATMAP_INIT_RETRY = 250;
/*     */   static final int NATMAP_MAX_RETRY = 2250;
/*     */   static final int NATMAP_DEFAULT_LEASE = 86400;
/*     */   static final String NATMAP_LLM = "224.0.0.1";
/*     */   static final byte NATOp_AddrRequest = 0;
/*     */   static final byte NATOp_MapUDP = 1;
/*     */   static final byte NATOp_MapTCP = 2;
/*     */   static final int NATAddrRequest = 2;
/*     */   static final int NATPortMapRequestLen = 12;
/*     */   static final int NATAddrReplyLen = 12;
/*     */   static final int NATPortMapReplyLen = 16;
/*     */   static final int NATResultSuccess = 0;
/*     */   static final int NATResultUnsupportedVer = 1;
/*     */   static final int NATResultNotAuth = 2;
/*     */   static final int NATResultNetFailure = 3;
/*     */   static final int NATResultNoResc = 4;
/*     */   static final int NATResultUnsupportedOp = 5;
/* 104 */   private String current_router_address = "?";
/*     */   
/*     */   private InetAddress hostInet;
/*     */   private InetAddress natPriInet;
/*     */   private InetAddress natPubInet;
/*     */   private NetworkInterface networkInterface;
/* 110 */   private int nat_epoch = 0;
/*     */   
/*     */ 
/*     */   private NATPMPDeviceAdapter adapter;
/*     */   
/*     */   private static NatPMPDeviceImpl NatPMPDeviceSingletonRef;
/*     */   
/*     */ 
/*     */   public static synchronized NatPMPDeviceImpl getSingletonObject(NATPMPDeviceAdapter adapter)
/*     */     throws Exception
/*     */   {
/* 121 */     if (NatPMPDeviceSingletonRef == null)
/* 122 */       NatPMPDeviceSingletonRef = new NatPMPDeviceImpl(adapter);
/* 123 */     return NatPMPDeviceSingletonRef;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private NatPMPDeviceImpl(NATPMPDeviceAdapter _adapter)
/*     */     throws Exception
/*     */   {
/* 132 */     this.adapter = _adapter;
/* 133 */     this.hostInet = NetUtils.getLocalHost();
/*     */     
/* 135 */     checkRouterAddress();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkRouterAddress()
/*     */     throws Exception
/*     */   {
/* 143 */     String natAddr = this.adapter.getRouterAddress().trim();
/*     */     
/* 145 */     if (natAddr.length() == 0)
/*     */     {
/* 147 */       natAddr = convertHost2RouterAddress(this.hostInet);
/*     */     }
/*     */     
/* 150 */     if (natAddr.equals(this.current_router_address))
/*     */     {
/* 152 */       return;
/*     */     }
/*     */     
/* 155 */     this.current_router_address = natAddr;
/*     */     
/* 157 */     log("Using Router IP: " + natAddr);
/*     */     
/* 159 */     this.natPriInet = InetAddress.getByName(natAddr);
/*     */     
/* 161 */     this.networkInterface = NetUtils.getByInetAddress(this.natPriInet);
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
/*     */   public DatagramPacket sendNATMsg(InetAddress dstInet, DatagramPacket dstPkt, byte[] recBuf)
/*     */     throws Exception
/*     */   {
/* 176 */     int retryInterval = 250;
/* 177 */     boolean recRep = false;
/*     */     
/* 179 */     DatagramSocket skt = new DatagramSocket();
/* 180 */     skt.connect(dstInet, 5351);
/* 181 */     skt.setSoTimeout(250);
/* 182 */     skt.send(dstPkt);
/*     */     
/* 184 */     DatagramPacket recPkt = new DatagramPacket(recBuf, recBuf.length);
/*     */     
/*     */ 
/* 187 */     while ((!recRep) && (retryInterval < 2250)) {
/*     */       try {
/* 189 */         skt.receive(recPkt);
/* 190 */         recRep = true;
/*     */ 
/*     */       }
/*     */       catch (SocketTimeoutException ste)
/*     */       {
/*     */ 
/* 196 */         Thread.sleep(retryInterval);
/*     */         
/* 198 */         retryInterval += retryInterval * 2;
/*     */       }
/*     */     }
/*     */     
/* 202 */     if (!recRep)
/*     */     {
/* 204 */       throw new PortUnreachableException();
/*     */     }
/*     */     
/*     */ 
/* 208 */     return recPkt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean connect()
/*     */     throws Exception
/*     */   {
/* 219 */     checkRouterAddress();
/*     */     
/*     */     try
/*     */     {
/* 223 */       byte[] reqBuf = { 0, 0 };
/* 224 */       DatagramPacket dstPkt = new DatagramPacket(reqBuf, reqBuf.length);
/* 225 */       byte[] recBuf = new byte[12];
/* 226 */       sendNATMsg(this.natPriInet, dstPkt, recBuf);
/*     */       
/*     */ 
/*     */ 
/* 230 */       int recErr = unsigned16ByteArrayToInt(recBuf, 2);
/* 231 */       int recEpoch = unsigned32ByteArrayToInt(recBuf, 4);
/* 232 */       String recPubAddr = unsigned8ByteArrayToInt(recBuf, 8) + "." + unsigned8ByteArrayToInt(recBuf, 9) + "." + unsigned8ByteArrayToInt(recBuf, 10) + "." + unsigned8ByteArrayToInt(recBuf, 11);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 238 */       this.natPubInet = InetAddress.getByName(recPubAddr);
/*     */       
/*     */ 
/* 241 */       this.nat_epoch = recEpoch;
/*     */       
/* 243 */       if (recErr != 0) {
/* 244 */         throw new Exception("NAT-PMP connection error: " + recErr);
/*     */       }
/* 246 */       log("Err: " + recErr);
/* 247 */       log("Uptime: " + recEpoch);
/* 248 */       log("Public Address: " + recPubAddr);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 256 */       return true;
/*     */     }
/*     */     catch (PortUnreachableException e) {}
/*     */     
/* 260 */     return false;
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
/*     */   public int addPortMapping(boolean tcp, int publicPort, int privatePort)
/*     */     throws Exception
/*     */   {
/* 277 */     return portMappingProtocol(tcp, publicPort, privatePort, 86400);
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
/*     */   public void deletePortMapping(boolean tcp, int publicPort, int privatePort)
/*     */     throws Exception
/*     */   {
/* 297 */     portMappingProtocol(tcp, publicPort, privatePort, 0);
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
/*     */   public int portMappingProtocol(boolean tcp, int publicPort, int privatePort, int lifetime)
/*     */     throws Exception
/*     */   {
/* 311 */     byte NATOp = tcp ? 2 : 1;
/*     */     
/* 313 */     byte[] pubPort = intToByteArray(publicPort);
/* 314 */     byte[] priPort = intToByteArray(privatePort);
/* 315 */     byte[] portLifeTime = intToByteArray(lifetime);
/*     */     
/*     */ 
/* 318 */     byte[] dstBuf = new byte[12];
/* 319 */     dstBuf[0] = 0;
/* 320 */     dstBuf[1] = NATOp;
/* 321 */     dstBuf[2] = 0;
/* 322 */     dstBuf[3] = 0;
/* 323 */     dstBuf[4] = priPort[2];
/* 324 */     dstBuf[5] = priPort[3];
/* 325 */     dstBuf[6] = pubPort[2];
/* 326 */     dstBuf[7] = pubPort[3];
/* 327 */     System.arraycopy(portLifeTime, 0, dstBuf, 8, 4);
/*     */     
/* 329 */     DatagramPacket dstPkt = new DatagramPacket(dstBuf, dstBuf.length);
/* 330 */     byte[] recBuf = new byte[16];
/* 331 */     sendNATMsg(this.natPriInet, dstPkt, recBuf);
/*     */     
/*     */ 
/*     */ 
/* 335 */     int recOP = unsigned8ByteArrayToInt(recBuf, 1);
/* 336 */     int recCode = unsigned16ByteArrayToInt(recBuf, 2);
/* 337 */     int recEpoch = unsigned32ByteArrayToInt(recBuf, 4);
/*     */     
/* 339 */     int recPubPort = unsigned16ByteArrayToInt(recBuf, 10);
/* 340 */     int recLifetime = unsigned32ByteArrayToInt(recBuf, 12);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 346 */     log("Seconds since Start of Epoch: " + recEpoch);
/* 347 */     log("Returned Mapped Port Lifetime: " + recLifetime);
/*     */     
/* 349 */     if (recCode != 0)
/* 350 */       throw new Exception("An error occured while getting a port mapping: " + recCode);
/* 351 */     if (recOP != NATOp + 128)
/* 352 */       throw new Exception("Received the incorrect port type: " + recOP);
/* 353 */     if (lifetime != recLifetime) {
/* 354 */       log("Received different port life time!");
/*     */     }
/* 356 */     return recPubPort;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/* 362 */     return this.hostInet;
/*     */   }
/*     */   
/*     */ 
/*     */   public NetworkInterface getNetworkInterface()
/*     */   {
/* 368 */     return this.networkInterface;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getExternalIPAddress()
/*     */   {
/* 374 */     return this.natPubInet.getHostAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getEpoch()
/*     */   {
/* 380 */     return this.nat_epoch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 387 */     this.adapter.log(str);
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
/*     */   public static int unsigned32ByteArrayToInt(byte[] b, int offset)
/*     */   {
/* 404 */     int value = 0;
/* 405 */     for (int i = 0; i < 4; i++) {
/* 406 */       int shift = (3 - i) * 8;
/* 407 */       value += ((b[(i + offset)] & 0xFF) << shift);
/*     */     }
/* 409 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int unsigned16ByteArrayToInt(byte[] b, int offset)
/*     */   {
/* 421 */     int value = 0;
/* 422 */     for (int i = 0; i < 2; i++) {
/* 423 */       int shift = (1 - i) * 8;
/* 424 */       value += ((b[(i + offset)] & 0xFF) << shift);
/*     */     }
/* 426 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int unsigned8ByteArrayToInt(byte[] b, int offset)
/*     */   {
/* 438 */     return b[offset] & 0xFF;
/*     */   }
/*     */   
/*     */   public short unsignedByteArrayToShort(byte[] buf) {
/* 442 */     if (buf.length == 2)
/*     */     {
/* 444 */       int i = (buf[0] & 0xFF) << 8 | buf[1] & 0xFF;
/* 445 */       return (short)i;
/*     */     }
/* 447 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] shortToByteArray(short v)
/*     */   {
/* 457 */     byte[] b = new byte[2];
/* 458 */     b[0] = ((byte)(0xFF & v >> 8));
/* 459 */     b[1] = ((byte)(0xFF & v >> 0));
/*     */     
/* 461 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] intToByteArray(int v)
/*     */   {
/* 470 */     byte[] b = new byte[4];
/*     */     
/*     */ 
/* 473 */     int i = 0; for (int shift = 24; i < 4; shift -= 8) {
/* 474 */       b[i] = ((byte)(0xFF & v >> shift));i++;
/*     */     }
/* 476 */     return b;
/*     */   }
/*     */   
/*     */   public String intArrayString(int[] buf) {
/* 480 */     StringBuilder sb = new StringBuilder();
/* 481 */     for (int i = 0; i < buf.length; i++) {
/* 482 */       sb.append(buf[i]).append(" ");
/*     */     }
/* 484 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String byteArrayString(byte[] buf) {
/* 488 */     StringBuilder sb = new StringBuilder();
/* 489 */     for (int i = 0; i < buf.length; i++) {
/* 490 */       sb.append(buf[i]).append(" ");
/*     */     }
/* 492 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String convertHost2RouterAddress(InetAddress inet)
/*     */   {
/* 502 */     byte[] rawIP = inet.getAddress();
/*     */     
/*     */ 
/* 505 */     rawIP[3] = 1;
/*     */     
/* 507 */     String newIP = (rawIP[0] & 0xFF) + "." + (rawIP[1] & 0xFF) + "." + (rawIP[2] & 0xFF) + "." + (rawIP[3] & 0xFF);
/* 508 */     return newIP;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/impl/NatPMPDeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */