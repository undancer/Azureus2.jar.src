/*     */ package com.aelitis.azureus.core.peermanager.utils;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*     */ public class ClientIdentifier
/*     */ {
/*     */   public static String identifyBTOnly(String peer_id_client, byte[] handshake_bytes)
/*     */   {
/*  28 */     if ((peer_id_client.equals("Mainline 4.4.0")) && ((handshake_bytes[7] & 0x1) == 0)) {
/*  29 */       return asDiscrepancy("BitThief*", peer_id_client, "fake_client");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  35 */     if (!peer_id_client.startsWith("Azureus ")) { return peer_id_client;
/*     */     }
/*     */     
/*  38 */     String version = peer_id_client.substring(8);
/*  39 */     if ((version.startsWith("1")) || (version.startsWith("2.0")) || (version.startsWith("2.1")) || (version.startsWith("2.2")))
/*     */     {
/*  41 */       return peer_id_client;
/*     */     }
/*     */     
/*     */ 
/*  45 */     return asDiscrepancy(null, peer_id_client, "fake_client");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String identifyAZMP(String peer_id_client_name, String az_msg_client_name, String az_msg_client_version, byte[] peer_id)
/*     */   {
/*  57 */     if (az_msg_client_name.endsWith("BitTyrant")) {
/*  58 */       return "BitTyrant " + az_msg_client_version.replaceAll("BitTyrant", "") + " (Azureus Mod)";
/*     */     }
/*     */     
/*  61 */     String msg_client_name = az_msg_client_name + " " + az_msg_client_version;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  66 */     if (msg_client_name.equals(peer_id_client_name)) { return msg_client_name;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */     String peer_id_client = peer_id_client_name.split(" ", 2)[0];
/*  74 */     String az_client_name = az_msg_client_name.split(" ", 2)[0];
/*  75 */     if (peer_id_client.equals(az_client_name))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */       if ((az_client_name.equals("Azureus")) && (peer_id_client.equals("Azureus")) && (
/*  84 */         (msg_client_name.length() < 15) || (peer_id_client_name.length() < 15) || (!msg_client_name.substring(0, 15).equals(peer_id_client_name.substring(0, 15))))) {
/*  85 */         return asDiscrepancy("Azureus (Hacked)", peer_id_client_name, msg_client_name, "fake_client", "AZMP", peer_id);
/*     */       }
/*     */       
/*  88 */       return msg_client_name;
/*     */     }
/*     */     
/*     */ 
/*  92 */     String res = checkForTransmissionBasedClients(msg_client_name, peer_id_client, peer_id_client_name, msg_client_name, peer_id, "AZMP");
/*  93 */     if (res != null) { return res;
/*     */     }
/*     */     
/*  96 */     String client_displayed_name = null;
/*  97 */     boolean is_peer_id_azureus = peer_id_client_name.startsWith("Azureus ");
/*  98 */     boolean is_msg_client_azureus = az_msg_client_name.equals("Azureus");
/*  99 */     boolean is_fake = false;
/* 100 */     boolean is_mismatch = true;
/* 101 */     boolean is_peer_id_unknown = peer_id_client_name.startsWith(MessageText.getString("PeerSocket.unknown"));
/*     */     
/* 103 */     if (is_peer_id_azureus)
/*     */     {
/*     */ 
/* 106 */       if (is_msg_client_azureus) {
/* 107 */         throw new RuntimeException("logic error in getExtendedClientName - both clients are Azureus");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 112 */       is_fake = true;
/*     */       
/*     */ 
/*     */ 
/* 116 */       if (msg_client_name.equals("Transmission 0.7-svn")) { client_displayed_name = "XTorrent";
/*     */       }
/*     */       
/*     */     }
/* 120 */     else if (is_msg_client_azureus) { is_fake = true;
/* 121 */     } else if (is_peer_id_unknown)
/*     */     {
/*     */ 
/*     */ 
/* 125 */       client_displayed_name = msg_client_name;
/* 126 */       is_mismatch = false;
/*     */       
/*     */ 
/* 129 */       BTPeerIDByteDecoder.logClientDiscrepancy(peer_id_client_name, msg_client_name, "unknown_client", "AZMP", peer_id);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 136 */     else if ((msg_client_name.startsWith("Ares")) && (peer_id_client.equals("ArcticTorrent"))) {
/* 137 */       return msg_client_name;
/*     */     }
/*     */     
/*     */     String discrepancy_type;
/*     */     
/*     */     String discrepancy_type;
/* 143 */     if (is_fake) { discrepancy_type = "fake_client"; } else { String discrepancy_type;
/* 144 */       if (is_mismatch) discrepancy_type = "mismatch_id"; else
/* 145 */         discrepancy_type = null;
/*     */     }
/* 147 */     if (discrepancy_type != null) {
/* 148 */       return asDiscrepancy(null, peer_id_client_name, msg_client_name, discrepancy_type, "AZMP", peer_id);
/*     */     }
/*     */     
/* 151 */     return client_displayed_name;
/*     */   }
/*     */   
/*     */   public static String identifyLTEP(String peer_id_name, String handshake_name, byte[] peer_id) {
/* 155 */     if (handshake_name == null) { return peer_id_name;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 161 */     String handshake_name_to_process = handshake_name;
/* 162 */     if (handshake_name.startsWith("BitTorrent ")) {
/* 163 */       handshake_name_to_process = handshake_name.replaceFirst("BitTorrent", "Mainline");
/*     */     }
/*     */     
/* 166 */     if (peer_id_name.startsWith("µTorrent"))
/*     */     {
/*     */ 
/* 169 */       if (peer_id_name.equals("µTorrent 1.6.0")) {
/* 170 */         return peer_id_name;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 175 */       if ((!handshake_name.startsWith("µTorrent")) && (handshake_name.startsWith("Torrent", 1))) {
/* 176 */         handshake_name_to_process = "µ" + handshake_name.substring(1);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 181 */       if ((peer_id_name.endsWith("Beta")) && (peer_id_name.startsWith(handshake_name_to_process))) {
/* 182 */         return peer_id_name;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 188 */     if ((peer_id_name.startsWith("Mainline 4.")) && (handshake_name.startsWith("Torrent", 1))) {
/* 189 */       return peer_id_name;
/*     */     }
/*     */     
/*     */ 
/* 193 */     if ((peer_id_name.startsWith("Azureus")) && (handshake_name.startsWith("Azureus"))) {
/* 194 */       return asDiscrepancy(null, peer_id_name, handshake_name, "fake_client", "LTEP", peer_id);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 200 */     String client_type_peer = peer_id_name.split(" ", 2)[0];
/* 201 */     String client_type_handshake = handshake_name_to_process.split(" ", 2)[0].split("/", 2)[0];
/*     */     
/*     */ 
/* 204 */     String res = checkForTransmissionBasedClients(handshake_name_to_process, client_type_peer, peer_id_name, handshake_name, peer_id, "LTEP");
/* 205 */     if (res != null) { return res;
/*     */     }
/* 207 */     if (client_type_peer.toLowerCase().equals(client_type_handshake.toLowerCase())) { return handshake_name_to_process;
/*     */     }
/*     */     
/*     */ 
/* 211 */     if (peer_id_name.startsWith(MessageText.getString("PeerSocket.unknown"))) {
/* 212 */       BTPeerIDByteDecoder.logClientDiscrepancy(peer_id_name, handshake_name, "unknown_client", "LTEP", peer_id);
/* 213 */       return handshake_name_to_process;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 221 */     if (peer_id_name.startsWith("libtorrent (Rasterbar)")) {
/* 222 */       if (!handshake_name_to_process.toLowerCase().contains("libtorrent")) {
/* 223 */         handshake_name_to_process = handshake_name_to_process + " (" + peer_id_name + ")";
/*     */       }
/* 225 */       return handshake_name_to_process;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 232 */     if (client_type_handshake.startsWith("libtorrent"))
/*     */     {
/*     */ 
/* 235 */       if ((!client_type_peer.toLowerCase().contains("libtorrent")) && (!client_type_handshake.toLowerCase().contains(client_type_peer.toLowerCase())))
/*     */       {
/*     */ 
/* 238 */         return peer_id_name + " (" + handshake_name_to_process + ")";
/*     */       }
/*     */     }
/*     */     
/* 242 */     if ((client_type_peer.startsWith("迅雷在线")) && (handshake_name_to_process.length() > 0) && (Character.isDigit(handshake_name_to_process.charAt(0))))
/*     */     {
/*     */ 
/* 245 */       return peer_id_name + " (" + handshake_name_to_process + ")";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 251 */     if (peer_id_name.equals(handshake_name))
/*     */     {
/* 253 */       return peer_id_name;
/*     */     }
/*     */     
/*     */ 
/* 257 */     return asDiscrepancy(null, peer_id_name, handshake_name, "mismatch_id", "LTEP", peer_id);
/*     */   }
/*     */   
/*     */ 
/*     */   private static String checkForTransmissionBasedClients(String handshake_name_to_process, String client_type_peer, String peer_id_name, String handshake_name, byte[] peer_id, String protocol)
/*     */   {
/* 263 */     if ((handshake_name_to_process.equals("Transmission 0.7-svn")) && (client_type_peer.equals("Azureus"))) {
/* 264 */       return asDiscrepancy("XTorrent", peer_id_name, handshake_name, "fake_client", protocol, peer_id);
/*     */     }
/*     */     
/*     */ 
/* 268 */     if ((handshake_name_to_process.startsWith("Transmission")) && (client_type_peer.startsWith("XTorrent"))) {
/* 269 */       return asDiscrepancy(client_type_peer, handshake_name_to_process, "fake_client");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 274 */     if ((peer_id_name.equals("Transmission 0.96")) && (handshake_name.equals("Transmission 0.95"))) {
/* 275 */       return peer_id_name;
/*     */     }
/*     */     
/* 278 */     return null;
/*     */   }
/*     */   
/*     */   private static String asDiscrepancy(String client_name, String peer_id_name, String handshake_name, String discrepancy_type, String protocol_type, byte[] peer_id) {
/* 282 */     if (client_name == null) {
/* 283 */       BTPeerIDByteDecoder.logClientDiscrepancy(peer_id_name, handshake_name, discrepancy_type, protocol_type, peer_id);
/*     */     }
/*     */     
/*     */ 
/* 287 */     if (peer_id_name.equals(handshake_name)) return asDiscrepancy(client_name, peer_id_name, discrepancy_type);
/* 288 */     return asDiscrepancy(client_name, peer_id_name + "\" / \"" + handshake_name, discrepancy_type);
/*     */   }
/*     */   
/*     */   private static String asDiscrepancy(String real_client, String dodgy_client, String discrepancy_type) {
/* 292 */     if (real_client == null) {
/* 293 */       real_client = MessageText.getString("PeerSocket.unknown");
/*     */     }
/* 295 */     return real_client + " [" + MessageText.getString(new StringBuilder().append("PeerSocket.").append(discrepancy_type).toString()) + ": \"" + dodgy_client + "\"]";
/*     */   }
/*     */   
/*     */ 
/* 299 */   private static int test_count = 1;
/*     */   
/* 301 */   private static void assertDecode(String client_name, String peer_id, String handshake_name, String handshake_version, byte[] handshake_reserved, String type) throws Exception { byte[] byte_peer_id = BTPeerIDByteDecoder.peerIDStringToBytes(peer_id);
/* 302 */     String peer_id_client = BTPeerIDByteDecoder.decode(byte_peer_id);
/*     */     
/*     */     String decoded_client;
/* 305 */     if (type.equals("AZMP")) { decoded_client = identifyAZMP(peer_id_client, handshake_name, handshake_version, byte_peer_id); } else { String decoded_client;
/* 306 */       if (type.equals("LTEP")) { decoded_client = identifyLTEP(peer_id_client, handshake_name, byte_peer_id); } else { String decoded_client;
/* 307 */         if (type.equals("BT")) decoded_client = identifyBTOnly(peer_id_client, handshake_reserved); else
/* 308 */           throw new RuntimeException("invalid extension type: " + type); } }
/*     */     String decoded_client;
/* 310 */     boolean passed = client_name.equals(decoded_client);
/* 311 */     System.out.println("  Test " + test_count++ + ": \"" + client_name + "\" - " + (passed ? "PASSED" : "FAILED"));
/*     */     
/* 313 */     if (!passed) {
/* 314 */       throw new Exception("\nDecoded      : " + decoded_client + "\n" + "Peer ID name : " + peer_id_client + "\n" + "Extended name: " + handshake_name + "\n");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void assertDecodeAZMP(String client_name, String peer_id, String handshake_name, String handshake_version)
/*     */     throws Exception
/*     */   {
/* 324 */     assertDecode(client_name, peer_id, handshake_name, handshake_version, null, "AZMP");
/*     */   }
/*     */   
/*     */   private static void assertDecodeLTEP(String client_name, String peer_id, String handshake_name) throws Exception {
/* 328 */     assertDecode(client_name, peer_id, handshake_name, null, null, "LTEP");
/*     */   }
/*     */   
/*     */   private static void assertDecodeExtProtocol(String client_name, String peer_id, String handshake_name, String handshake_version) throws Exception {
/* 332 */     assertDecodeAZMP(client_name, peer_id, handshake_name, handshake_version);
/* 333 */     assertDecodeLTEP(client_name, peer_id, handshake_name + " " + handshake_version);
/*     */   }
/*     */   
/*     */   private static void assertDecodeBT(String client_name, String peer_id, String handshake_reserved) throws Exception {
/* 337 */     if (handshake_reserved == null) handshake_reserved = "0000000000000000";
/* 338 */     handshake_reserved = handshake_reserved.replaceAll("[ ]", "");
/* 339 */     byte[] handshake_reserved_bytes = ByteFormatter.decodeString(handshake_reserved);
/* 340 */     if (handshake_reserved_bytes.length != 8) throw new RuntimeException("invalid handshake reserved bytes");
/* 341 */     assertDecode(client_name, peer_id, null, null, handshake_reserved_bytes, "BT");
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 345 */     System.setProperty("transitory.startup", "1");
/*     */     
/* 347 */     BTPeerIDByteDecoder.client_logging_allowed = false;
/*     */     
/* 349 */     System.out.println("Testing simple BT clients:");
/* 350 */     assertDecodeBT("BitThief* [FAKE: \"Mainline 4.4.0\"]", "M4-4-0--9aa757efd5be", "0000000000000000");
/* 351 */     assertDecodeBT("Mainline 4.4.0", "M4-4-0--9aa757efd5be", "0000000000000001");
/* 352 */     assertDecodeBT("Unknown [FAKE: \"Azureus 3.0.3.4\"]", "-AZ3034-6wfG2wk6wWLc", "0000000000000000");
/* 353 */     System.out.println("");
/*     */     
/* 355 */     System.out.println("Testing AZMP clients:");
/* 356 */     assertDecodeAZMP("Azureus 3.0.4.2", "-AZ3042-6ozMq5q6Q3NX", "Azureus", "3.0.4.2");
/* 357 */     assertDecodeAZMP("Azureus 3.0.4.3_B02", "-AZ3043-6ozMq5q6Q3NX", "Azureus", "3.0.4.3_B02");
/* 358 */     assertDecodeAZMP("BitTyrant 2.5.0.0 (Azureus Mod)", "AZ2500BTeyuzyabAfo6U", "AzureusBitTyrant", "2.5.0.0BitTyrant");
/*     */     
/*     */ 
/* 361 */     assertDecodeAZMP("Azureus (Hacked) [FAKE: \"Azureus 2.4.0.2\" / \"Azureus 2.3.0.6\"]", "2D415A32 3430322D 2E414794 2C57D644 4989CA58", "Azureus", "2.3.0.6");
/*     */     
/*     */ 
/*     */ 
/* 365 */     System.out.println("");
/*     */     
/* 367 */     System.out.println("Testing LTEP clients:");
/* 368 */     assertDecodeLTEP("µTorrent 1.7.6", "2D555431 3736302D B39EC7AD F6B94610 AA4ACD4A", "µTorrent 1.7.6");
/* 369 */     assertDecodeLTEP("µTorrent 1.6.1", "2D5554313631302DEA818D43F5E5EC3D67BF8D67", "﷿Torrent 1.6.1");
/* 370 */     assertDecodeLTEP("Unknown [FAKE: \"Azureus 3.0.4.2\"]", "-AZ3042-6ozMq5q6Q3NX", "Azureus 3.0.4.2");
/* 371 */     assertDecodeLTEP("Mainline 6.0", "4D362D30 2D302D2D 8B92860D 05055DF5 B01C2D94", "BitTorrent 6.0");
/*     */     
/* 373 */     assertDecodeLTEP("µTorrent 1.8.0 Beta", "2D555431 3830422D E69C9942 D1A5A6C2 0BE2E4BD", "µTorrent 1.8");
/* 374 */     assertDecodeLTEP("Miro 1.1.0.0 (libtorrent/0.13.0.0)", "-MR1100-00HS~T7*65rm", "libtorrent/0.13.0.0");
/* 375 */     assertDecodeLTEP("linkage/0.1.4 libtorrent/0.12.0.0", "-LK0140-ATIV~nbEQAMr", "linkage/0.1.4 libtorrent/0.12.0.0");
/* 376 */     assertDecodeLTEP("KTorrent 2.2.2", "-KT2210-347143496631", "KTorrent 2.2.2");
/*     */     
/* 378 */     assertDecodeLTEP("Transmission 0.96", "-TR0960-6ep6svaa61r4", "Transmission 0.95");
/* 379 */     assertDecodeLTEP("Opera 9.50", "O100634008270e29150a", "Opera 9.50");
/* 380 */     System.out.println("");
/*     */     
/* 382 */     System.out.println("Testing common clients:");
/*     */     
/* 384 */     assertDecodeExtProtocol("XTorrent [FAKE: \"Azureus 2.5.0.4\" / \"Transmission 0.7-svn\"]", "-AZ2504-192gwethivju", "Transmission", "0.7-svn");
/* 385 */     System.out.println("");
/*     */     
/* 387 */     System.out.println("Done.");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/ClientIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */