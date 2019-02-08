/*    */ package org.gudy.azureus2.core3.tracker.client.impl.bt;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLEncoder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TrackerLoadTester
/*    */ {
/*    */   private static final String trackerUrl = "http://localhost:6969/announce";
/*    */   
/*    */   public TrackerLoadTester(int nbTorrents, int nbClientsPerTorrent)
/*    */   {
/* 37 */     for (int i = 0; i < nbTorrents; i++) {
/* 38 */       byte[] hash = generate20BytesHash(i);
/*    */       
/* 40 */       for (int j = 0; j < nbClientsPerTorrent; j++) {
/* 41 */         byte[] peerId = generate20BytesHash(j);
/* 42 */         announce("http://localhost:6969/announce", hash, peerId, 6881 + j);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public static void main(String[] args) {
/* 48 */     if (args.length < 2) return;
/* 49 */     int nbTorrents = Integer.parseInt(args[0]);
/* 50 */     int nbClientsPerTorrent = Integer.parseInt(args[1]);
/* 51 */     new TrackerLoadTester(nbTorrents, nbClientsPerTorrent);
/*    */   }
/*    */   
/*    */   private void announce(String trackerURL, byte[] hash, byte[] peerId, int port) {
/*    */     try {
/* 56 */       String strUrl = trackerURL + "?info_hash=" + URLEncoder.encode(new String(hash, "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20") + "&peer_id=" + URLEncoder.encode(new String(peerId, "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20") + "&port=" + port + "&uploaded=0&downloaded=0&left=0&numwant=50&no_peer_id=1&compact=1";
/*    */       
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 62 */       URL url = new URL(strUrl);
/* 63 */       URLConnection con = url.openConnection();
/* 64 */       con.connect();
/* 65 */       con.getContent();
/*    */     } catch (Exception e) {
/* 67 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */   private byte[] generate20BytesHash(int iter) {
/* 72 */     byte[] result = new byte[20];
/* 73 */     int pos = 0;
/* 74 */     while (iter > 0) {
/* 75 */       result[(pos++)] = ((byte)(iter % 255));
/* 76 */       iter /= 255;
/*    */     }
/* 78 */     return result;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/bt/TrackerLoadTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */