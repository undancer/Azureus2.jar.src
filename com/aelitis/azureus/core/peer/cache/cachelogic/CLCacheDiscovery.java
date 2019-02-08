/*     */ package com.aelitis.azureus.core.peer.cache.cachelogic;
/*     */ 
/*     */ import com.aelitis.azureus.core.peer.cache.CacheDiscoverer;
/*     */ import com.aelitis.azureus.core.peer.cache.CacheDiscovery.CachePeerImpl;
/*     */ import com.aelitis.azureus.core.peer.cache.CachePeer;
/*     */ import com.aelitis.azureus.core.peermanager.utils.PeerClassifier;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
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
/*     */ public class CLCacheDiscovery
/*     */   implements CacheDiscoverer
/*     */ {
/*     */   public static final String CDPDomainName = ".find-cache.com";
/*     */   public static final String CDPServerName = "cls.find-cache.com";
/*     */   public static final int CDPPort = 19523;
/*     */   public static final int CDPVersion = 0;
/*     */   public static final int CDPTimeout = 5000;
/*     */   static CDPResponse Response;
/*     */   
/*     */   private String byteArrayToHex(byte[] Bytes, int Max)
/*     */   {
/*  64 */     int Length = Bytes.length;
/*  65 */     if (Length > Max) {
/*  66 */       Length = Max;
/*     */     }
/*  68 */     String Result = new String();
/*  69 */     for (int Index = 0; Index < Length; Index++) {
/*  70 */       int Value = Bytes[Index] & 0xFF;
/*  71 */       if (Value < 16)
/*  72 */         Result = Result + "0";
/*  73 */       Result = Result + Integer.toHexString(Value);
/*     */     }
/*  75 */     return Result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String lookupFarm()
/*     */   {
/*  83 */     if (Response != null)
/*     */     {
/*  85 */       if (Response.isStillValid())
/*     */       {
/*  87 */         return Response.getFarmID();
/*     */       }
/*     */       
/*  90 */       Response = null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 101 */       InetAddress CDPServer = InetAddress.getByName("cls.find-cache.com");
/*     */       
/*     */ 
/* 104 */       DatagramSocket Socket = new DatagramSocket();
/* 105 */       Socket.setSoTimeout(5000);
/*     */       
/*     */ 
/* 108 */       CDPQuery Query = new CDPQuery("Azureus 5.7.6.0");
/* 109 */       byte[] Buffer = Query.getBytes();
/* 110 */       DatagramPacket Packet = new DatagramPacket(Buffer, Buffer.length, CDPServer, 19523);
/*     */       
/*     */ 
/* 113 */       Socket.send(Packet);
/*     */       
/*     */ 
/* 116 */       Buffer = new byte['Ć'];
/* 117 */       Packet.setData(Buffer);
/* 118 */       Socket.receive(Packet);
/* 119 */       if ((Packet.getAddress() != CDPServer) || (Packet.getPort() != 19523)) {
/* 120 */         throw new Exception("CDP server address mismatch on response");
/*     */       }
/*     */       
/* 123 */       Response = new CDPResponse(Packet.getData());
/*     */       
/*     */ 
/* 126 */       return Response.getFarmID();
/*     */     }
/*     */     catch (Throwable Excpt) {
/* 129 */       if (!(Excpt instanceof UnknownHostException))
/*     */       {
/*     */ 
/*     */ 
/* 133 */         Excpt.printStackTrace();
/*     */       }
/*     */     }
/* 136 */     return "default";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String hashAnnounceURL(URL announce_url)
/*     */   {
/* 148 */     byte[] Digest = new SHA1Hasher().calculateHash(announce_url.getHost().getBytes());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 155 */     return byteArrayToHex(Digest, 16);
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
/*     */ 
/*     */ 
/*     */   public InetAddress[] findCache(URL announce_url, String hex_hash)
/*     */   {
/* 176 */     String Hostname = "bt-" + hex_hash.substring(0, 4) + ".bt-" + hashAnnounceURL(announce_url) + "-" + lookupFarm() + ".find-cache.com";
/*     */     
/*     */     InetAddress[] Caches;
/*     */     try
/*     */     {
/* 181 */       Caches = InetAddress.getAllByName(Hostname);
/*     */     } catch (UnknownHostException NoCache) {
/* 183 */       Caches = new InetAddress[0];
/*     */     }
/* 185 */     return Caches;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InetAddress[] findCache(URL announce_url, byte[] hash)
/*     */   {
/* 195 */     return findCache(announce_url, byteArrayToHex(hash, 4));
/*     */   }
/*     */   
/*     */ 
/*     */   public CachePeer[] lookup(TOTorrent torrent)
/*     */   {
/*     */     try
/*     */     {
/* 203 */       InetAddress[] addresses = findCache(torrent.getAnnounceURL(), torrent.getHash());
/*     */       
/* 205 */       CachePeer[] result = new CachePeer[addresses.length];
/*     */       
/* 207 */       for (int i = 0; i < addresses.length; i++)
/*     */       {
/* 209 */         result[i] = new CacheDiscovery.CachePeerImpl(2, addresses[i], 6881);
/*     */       }
/*     */       
/* 212 */       return result;
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 216 */       Debug.printStackTrace(e);
/*     */     }
/* 218 */     return new CachePeer[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CachePeer lookup(byte[] peer_id, InetAddress ip, int port)
/*     */   {
/* 228 */     if (PeerClassifier.getClientDescription(peer_id).startsWith("CacheLogic"))
/*     */     {
/* 230 */       return new CacheDiscovery.CachePeerImpl(2, ip, port);
/*     */     }
/*     */     
/* 233 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   static class CDPQuery
/*     */   {
/*     */     private String Client;
/*     */     
/*     */ 
/*     */     public CDPQuery(String _Client)
/*     */     {
/* 244 */       this.Client = _Client;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     byte[] getBytes()
/*     */     {
/* 253 */       String Temp = "@@@" + this.Client;
/* 254 */       byte[] Bytes = Temp.getBytes();
/* 255 */       Bytes[0] = 0;
/* 256 */       Bytes[1] = 0;
/* 257 */       Bytes[2] = ((byte)this.Client.length());
/*     */       
/* 259 */       return Bytes;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class CDPResponse
/*     */   {
/*     */     public static final int MinSize = 7;
/*     */     
/*     */ 
/*     */ 
/*     */     public static final int MaxSize = 262;
/*     */     
/*     */ 
/*     */ 
/*     */     String farmID;
/*     */     
/*     */ 
/*     */ 
/*     */     long validUntil;
/*     */     
/*     */ 
/*     */ 
/*     */     public CDPResponse(byte[] Bytes)
/*     */       throws Exception
/*     */     {
/* 287 */       if ((Bytes.length < 7) || (7 + Bytes[6] > Bytes.length)) {
/* 288 */         throw new Exception("CDP response too short");
/*     */       }
/* 290 */       if (Bytes[0] != 0) {
/* 291 */         throw new Exception("Unsupported CDP version");
/*     */       }
/* 293 */       this.farmID = new String();
/* 294 */       for (int Index = 0; Index < Bytes[6]; Index++) {
/* 295 */         this.farmID += (char)Bytes[(7 + Index)];
/*     */       }
/* 297 */       long Timeout = 0L;
/* 298 */       for (Index = 2; Index < 6; Index++)
/* 299 */         Timeout = (Timeout << 8) + (Bytes[Index] & 0xFF);
/* 300 */       this.validUntil = (System.currentTimeMillis() + Timeout * 1000L);
/*     */     }
/*     */     
/*     */ 
/*     */     public String getFarmID()
/*     */     {
/* 306 */       return this.farmID;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isStillValid()
/*     */     {
/* 312 */       return System.currentTimeMillis() < this.validUntil;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 321 */       TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedFile(new File("C:\\temp\\test.torrent"));
/*     */       
/* 323 */       CachePeer[] peers = new CLCacheDiscovery().lookup(torrent);
/*     */       
/* 325 */       System.out.println("peers=" + peers.length);
/*     */       
/* 327 */       for (int i = 0; i < peers.length; i++)
/*     */       {
/* 329 */         System.out.println("    cache: " + peers[i].getAddress() + ":" + peers[i].getPort());
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 333 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peer/cache/cachelogic/CLCacheDiscovery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */