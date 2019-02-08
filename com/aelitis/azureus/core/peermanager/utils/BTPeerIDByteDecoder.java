/*     */ package com.aelitis.azureus.core.peermanager.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashSet;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class BTPeerIDByteDecoder
/*     */ {
/*     */   static final boolean LOG_UNKNOWN;
/*     */   
/*     */   static
/*     */   {
/*  41 */     String prop = System.getProperty("log.unknown.peerids");
/*  42 */     LOG_UNKNOWN = (prop == null) || (prop.equals("1"));
/*     */   }
/*     */   
/*     */   private static String logUnknownClient0(byte[] peer_id_bytes) throws IOException {
/*  46 */     String text = new String(peer_id_bytes, 0, 20, "ISO-8859-1");
/*  47 */     text = text.replace('\f', ' ');
/*  48 */     text = text.replace('\n', ' ');
/*     */     
/*  50 */     return "[" + text + "] " + ByteFormatter.encodeString(peer_id_bytes) + " ";
/*     */   }
/*     */   
/*     */   private static String asUTF8ByteString(String text) {
/*     */     try {
/*  55 */       byte[] utf_bytes = text.getBytes("UTF8");
/*  56 */       return ByteFormatter.encodeString(utf_bytes);
/*     */     } catch (UnsupportedEncodingException uee) {}
/*  58 */     return "";
/*     */   }
/*     */   
/*  61 */   private static final HashSet logged_discrepancies = new HashSet();
/*     */   
/*  63 */   public static void logClientDiscrepancy(String peer_id_name, String handshake_name, String discrepancy, String protocol, byte[] peer_id) { if (!client_logging_allowed) { return;
/*     */     }
/*     */     
/*  66 */     String line_to_log = discrepancy + " [" + protocol + "]: ";
/*  67 */     line_to_log = line_to_log + "\"" + peer_id_name + "\" / \"" + handshake_name + "\" ";
/*     */     
/*     */ 
/*  70 */     line_to_log = line_to_log + "[" + asUTF8ByteString(handshake_name) + "]";
/*     */     
/*     */ 
/*  73 */     boolean log_to_debug_out = Constants.isCVSVersion();
/*  74 */     if ((log_to_debug_out) || (LOG_UNKNOWN))
/*     */     {
/*  76 */       if (!logged_discrepancies.add(line_to_log)) { return;
/*     */       }
/*     */     }
/*     */     
/*  80 */     if (peer_id != null) {
/*  81 */       line_to_log = line_to_log + ", Peer ID: " + ByteFormatter.encodeString(peer_id);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  86 */     if (log_to_debug_out) {
/*  87 */       Debug.outNoStack("Conflicting peer identification: " + line_to_log);
/*     */     }
/*     */     
/*  90 */     if (!LOG_UNKNOWN) return;
/*  91 */     logClientDiscrepancyToFile(line_to_log);
/*     */   }
/*     */   
/*  94 */   private static AEDiagnosticsLogger logger = null;
/*     */   
/*  96 */   private static synchronized void logClientDiscrepancyToFile(String line_to_log) { if (logger == null) logger = AEDiagnostics.getLogger("clientid");
/*  97 */     try { logger.log(line_to_log);
/*  98 */     } catch (Throwable e) { Debug.printStackTrace(e);
/*     */     } }
/*     */   
/* 101 */   static boolean client_logging_allowed = true;
/*     */   
/*     */ 
/* 104 */   private static final HashSet logged_ids = new HashSet();
/* 105 */   static void logUnknownClient(byte[] peer_id_bytes) { logUnknownClient(peer_id_bytes, true); }
/*     */   
/*     */   static void logUnknownClient(byte[] peer_id_bytes, boolean to_debug_out) {
/* 108 */     if (!client_logging_allowed) { return;
/*     */     }
/*     */     
/* 111 */     boolean log_to_debug_out = (to_debug_out) && (Constants.isCVSVersion());
/* 112 */     if ((log_to_debug_out) || (LOG_UNKNOWN))
/*     */     {
/* 114 */       if (!logged_ids.add(makePeerIDReadableAndUsable(peer_id_bytes))) { return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 119 */     if (log_to_debug_out) {
/* 120 */       Debug.outNoStack("Unable to decode peer correctly - peer ID bytes: " + makePeerIDReadableAndUsable(peer_id_bytes));
/*     */     }
/*     */     
/* 123 */     if (!LOG_UNKNOWN) return;
/* 124 */     try { logClientDiscrepancyToFile(logUnknownClient0(peer_id_bytes));
/* 125 */     } catch (Throwable t) { Debug.printStackTrace(t);
/*     */     }
/*     */   }
/*     */   
/*     */   static void logUnknownClient(String peer_id) {
/* 130 */     try { logUnknownClient(peer_id.getBytes("ISO-8859-1"));
/*     */     }
/*     */     catch (UnsupportedEncodingException uee) {}
/*     */   }
/*     */   
/*     */   public static String decode0(byte[] peer_id_bytes) {
/* 136 */     String peer_id = null;
/* 137 */     try { peer_id = new String(peer_id_bytes, "ISO-8859-1");
/* 138 */     } catch (UnsupportedEncodingException uee) { return "";
/*     */     }
/*     */     
/* 141 */     String client = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */     if (BTPeerIDByteDecoderUtils.isPossibleSpoofClient(peer_id)) {
/* 148 */       client = decodeBitSpiritClient(peer_id, peer_id_bytes);
/* 149 */       if (client != null) return client;
/* 150 */       client = decodeBitCometClient(peer_id, peer_id_bytes);
/* 151 */       if (client != null) return client;
/* 152 */       String BAD_PEER_ID = MessageText.getString("PeerSocket.bad_peer_id");
/* 153 */       return "BitSpirit? (" + BAD_PEER_ID + ")";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     if (BTPeerIDByteDecoderUtils.isAzStyle(peer_id)) {
/* 160 */       client = BTPeerIDByteDecoderDefinitions.getAzStyleClientName(peer_id);
/* 161 */       if (client != null) {
/* 162 */         String client_with_version = BTPeerIDByteDecoderDefinitions.getAzStyleClientVersion(client, peer_id);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 168 */         if ((client.startsWith("ZipTorrent")) && (peer_id.startsWith("bLAde", 8))) {
/* 169 */           String client_name = client_with_version == null ? client : client_with_version;
/* 170 */           String UNKNOWN = MessageText.getString("PeerSocket.unknown");
/* 171 */           String FAKE = MessageText.getString("PeerSocket.fake_client");
/* 172 */           return UNKNOWN + " [" + FAKE + ": " + client_name + "]";
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 178 */         if ("µTorrent 6.0.0 Beta".equals(client_with_version)) {
/* 179 */           return "Mainline 6.0 Beta";
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 185 */         if (client.startsWith("libTorrent (Rakshasa)")) {
/* 186 */           String client_name = client_with_version == null ? client : client_with_version;
/* 187 */           return client_name + " / rTorrent*";
/*     */         }
/*     */         
/* 190 */         if (client_with_version != null) { return client_with_version;
/*     */         }
/* 192 */         return client;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 200 */     if (BTPeerIDByteDecoderUtils.isShadowStyle(peer_id)) {
/* 201 */       client = BTPeerIDByteDecoderDefinitions.getShadowStyleClientName(peer_id);
/* 202 */       if (client != null) {
/* 203 */         String client_ver = BTPeerIDByteDecoderUtils.getShadowStyleVersionNumber(peer_id);
/* 204 */         if (client_ver != null) return client + " " + client_ver;
/* 205 */         return client;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 212 */     client = BTPeerIDByteDecoderDefinitions.getMainlineStyleClientName(peer_id);
/* 213 */     if (client != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 221 */       String client_ver = BTPeerIDByteDecoderUtils.getMainlineStyleVersionNumber(peer_id);
/*     */       
/* 223 */       if (client_ver != null) {
/* 224 */         String result = client + " " + client_ver;
/* 225 */         return result;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 232 */     client = decodeBitSpiritClient(peer_id, peer_id_bytes);
/* 233 */     if (client != null) return client;
/* 234 */     client = decodeBitCometClient(peer_id, peer_id_bytes);
/* 235 */     if (client != null) { return client;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 241 */     BTPeerIDByteDecoderDefinitions.ClientData client_data = BTPeerIDByteDecoderDefinitions.getSubstringStyleClient(peer_id);
/* 242 */     if (client_data != null) {
/* 243 */       client = client_data.client_name;
/* 244 */       String client_with_version = BTPeerIDByteDecoderDefinitions.getSubstringStyleClientVersion(client_data, peer_id, peer_id_bytes);
/* 245 */       if (client_with_version != null) return client_with_version;
/* 246 */       return client;
/*     */     }
/*     */     
/*     */ 
/* 250 */     if ((peer_id_bytes[0] == 45) && (peer_id_bytes[1] == 77)) {
/* 251 */       return "BitTorrent 7.8.2";
/*     */     }
/*     */     
/* 254 */     client = identifyAwkwardClient(peer_id_bytes);
/* 255 */     if (client != null) return client;
/* 256 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String decode(byte[] peer_id)
/*     */   {
/* 265 */     if (peer_id.length > 0)
/*     */     {
/*     */       try {
/* 268 */         String client = decode0(peer_id);
/*     */         
/* 270 */         if (client != null)
/*     */         {
/* 272 */           return client;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 276 */         Debug.out("Failed to decode peer id " + ByteFormatter.encodeString(peer_id) + ": " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       }
/*     */       try
/*     */       {
/* 280 */         String peer_id_as_string = new String(peer_id, "ISO-8859-1");
/*     */         
/* 282 */         boolean is_az_style = BTPeerIDByteDecoderUtils.isAzStyle(peer_id_as_string);
/*     */         
/* 284 */         boolean is_shadow_style = BTPeerIDByteDecoderUtils.isShadowStyle(peer_id_as_string);
/*     */         
/* 286 */         logUnknownClient(peer_id, (!is_az_style) && (!is_shadow_style));
/*     */         
/* 288 */         if (is_az_style) {
/* 289 */           return BTPeerIDByteDecoderDefinitions.formatUnknownAzStyleClient(peer_id_as_string);
/*     */         }
/* 291 */         if (is_shadow_style) {
/* 292 */           return BTPeerIDByteDecoderDefinitions.formatUnknownShadowStyleClient(peer_id_as_string);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 296 */         Debug.out("Failed to decode peer id " + ByteFormatter.encodeString(peer_id) + ": " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       }
/*     */     }
/*     */     
/* 300 */     String sPeerID = getPrintablePeerID(peer_id);
/* 301 */     return MessageText.getString("PeerSocket.unknown") + " [" + sPeerID + "]";
/*     */   }
/*     */   
/*     */   public static String identifyAwkwardClient(byte[] peer_id)
/*     */   {
/* 306 */     int iFirstNonZeroPos = 0;
/*     */     
/* 308 */     iFirstNonZeroPos = 20;
/* 309 */     for (int i = 0; i < 20; i++) {
/* 310 */       if (peer_id[i] != 0) {
/* 311 */         iFirstNonZeroPos = i;
/* 312 */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 317 */     if (iFirstNonZeroPos == 0) {
/* 318 */       boolean bShareaza = true;
/* 319 */       for (int i = 0; i < 16; i++) {
/* 320 */         if (peer_id[i] == 0) {
/* 321 */           bShareaza = false;
/* 322 */           break;
/*     */         }
/*     */       }
/* 325 */       if (bShareaza) {
/* 326 */         for (int i = 16; i < 20; i++) {
/* 327 */           if (peer_id[i] != (peer_id[(i % 16)] ^ peer_id[(15 - i % 16)])) {
/* 328 */             bShareaza = false;
/* 329 */             break;
/*     */           }
/*     */         }
/* 332 */         if (bShareaza) { return "Shareaza";
/*     */         }
/*     */       }
/*     */     }
/* 336 */     byte three = 3;
/* 337 */     if ((iFirstNonZeroPos == 9) && (peer_id[9] == three) && (peer_id[10] == three) && (peer_id[11] == three))
/*     */     {
/*     */ 
/*     */ 
/* 341 */       return "I2PSnark";
/*     */     }
/*     */     
/* 344 */     if ((iFirstNonZeroPos == 12) && (peer_id[12] == 97) && (peer_id[13] == 97)) {
/* 345 */       return "Experimental 3.2.1b2";
/*     */     }
/* 347 */     if ((iFirstNonZeroPos == 12) && (peer_id[12] == 0) && (peer_id[13] == 0)) {
/* 348 */       return "Experimental 3.1";
/*     */     }
/* 350 */     if (iFirstNonZeroPos == 12) { return "Mainline";
/*     */     }
/* 352 */     return null;
/*     */   }
/*     */   
/*     */   private static String decodeBitSpiritClient(String peer_id, byte[] peer_id_bytes)
/*     */   {
/* 357 */     if (!peer_id.substring(2, 4).equals("BS")) return null;
/* 358 */     String version = BTPeerIDByteDecoderUtils.decodeNumericValueOfByte(peer_id_bytes[1]);
/* 359 */     if ("0".equals(version)) version = "1";
/* 360 */     return "BitSpirit v" + version;
/*     */   }
/*     */   
/*     */   private static String decodeBitCometClient(String peer_id, byte[] peer_id_bytes) {
/* 364 */     String mod_name = null;
/* 365 */     if (peer_id.startsWith("exbc")) { mod_name = "";
/* 366 */     } else if (peer_id.startsWith("FUTB")) { mod_name = "(Solidox Mod) ";
/* 367 */     } else if (peer_id.startsWith("xUTB")) mod_name = "(Mod 2) "; else {
/* 368 */       return null;
/*     */     }
/* 370 */     boolean is_bitlord = peer_id.substring(6, 10).equals("LORD");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 376 */     String client_name = is_bitlord ? "BitLord " : "BitComet ";
/* 377 */     String maj_version = BTPeerIDByteDecoderUtils.decodeNumericValueOfByte(peer_id_bytes[4]);
/* 378 */     int min_version_length = (is_bitlord) && (!maj_version.equals("0")) ? 1 : 2;
/*     */     
/* 380 */     return client_name + mod_name + maj_version + "." + BTPeerIDByteDecoderUtils.decodeNumericValueOfByte(peer_id_bytes[5], min_version_length);
/*     */   }
/*     */   
/*     */ 
/*     */   protected static String getPrintablePeerID(byte[] peer_id)
/*     */   {
/* 386 */     return getPrintablePeerID(peer_id, '-');
/*     */   }
/*     */   
/*     */   protected static String getPrintablePeerID(byte[] peer_id, char fallback_char) {
/* 390 */     String sPeerID = "";
/* 391 */     byte[] peerID = new byte[peer_id.length];
/* 392 */     System.arraycopy(peer_id, 0, peerID, 0, peer_id.length);
/*     */     try
/*     */     {
/* 395 */       for (int i = 0; i < peerID.length; i++) {
/* 396 */         int b = 0xFF & peerID[i];
/* 397 */         if ((b < 32) || (b > 127))
/* 398 */           peerID[i] = ((byte)fallback_char);
/*     */       }
/* 400 */       sPeerID = new String(peerID, "ISO-8859-1");
/*     */     }
/*     */     catch (UnsupportedEncodingException ignore) {}catch (Throwable e) {}
/*     */     
/*     */ 
/* 405 */     return sPeerID;
/*     */   }
/*     */   
/*     */   private static String makePeerIDReadableAndUsable(byte[] peer_id) {
/* 409 */     boolean as_ascii = true;
/* 410 */     for (int i = 0; i < peer_id.length; i++) {
/* 411 */       int b = 0xFF & peer_id[i];
/* 412 */       if ((b < 32) || (b > 127) || (b == 10) || (b == 9) || (b == 13)) {
/* 413 */         as_ascii = false;
/* 414 */         break;
/*     */       }
/*     */     }
/* 417 */     if (as_ascii)
/* 418 */       try { return new String(peer_id, "ISO-8859-1");
/* 419 */       } catch (UnsupportedEncodingException uee) { return "";
/*     */       }
/* 421 */     return ByteFormatter.encodeString(peer_id);
/*     */   }
/*     */   
/*     */   static byte[] peerIDStringToBytes(String peer_id) throws Exception {
/* 425 */     if (peer_id.length() > 40) {
/* 426 */       peer_id = peer_id.replaceAll("[ ]", "");
/*     */     }
/*     */     
/* 429 */     byte[] byte_peer_id = null;
/* 430 */     if (peer_id.length() == 40) {
/* 431 */       byte_peer_id = ByteFormatter.decodeString(peer_id);
/* 432 */       String readable_peer_id = makePeerIDReadableAndUsable(byte_peer_id);
/* 433 */       if (!peer_id.equals(readable_peer_id)) {
/* 434 */         throw new RuntimeException("Use alternative format for peer ID - from " + peer_id + " to " + readable_peer_id);
/*     */       }
/*     */     }
/* 437 */     else if (peer_id.length() == 20) {
/* 438 */       byte_peer_id = peer_id.getBytes("ISO-8859-1");
/*     */     }
/*     */     else {
/* 441 */       throw new IllegalArgumentException(peer_id);
/*     */     }
/* 443 */     return byte_peer_id;
/*     */   }
/*     */   
/*     */   private static void assertDecode(String client_result, String peer_id) throws Exception {
/* 447 */     assertDecode(client_result, peerIDStringToBytes(peer_id));
/*     */   }
/*     */   
/*     */   private static void assertDecode(String client_result, byte[] peer_id) throws Exception {
/* 451 */     String peer_id_as_string = getPrintablePeerID(peer_id, '*');
/* 452 */     System.out.println("   Peer ID: " + peer_id_as_string + "   Client: " + client_result);
/*     */     
/*     */ 
/* 455 */     String decoded_result = decode(peer_id);
/* 456 */     if (client_result.equals(decoded_result)) return;
/* 457 */     throw new RuntimeException("assertion failure - expected \"" + client_result + "\", got \"" + decoded_result + "\": " + peer_id_as_string);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 461 */     client_logging_allowed = false;
/*     */     
/* 463 */     String FAKE = MessageText.getString("PeerSocket.fake_client");
/* 464 */     String UNKNOWN = MessageText.getString("PeerSocket.unknown");
/* 465 */     String BAD_PEER_ID = MessageText.getString("PeerSocket.bad_peer_id");
/*     */     
/* 467 */     System.out.println("Testing AZ style clients...");
/* 468 */     assertDecode("Ares 2.0.5.3", "-AG2053-Em6o1EmvwLtD");
/* 469 */     assertDecode("Ares 1.6.7.0", "-AR1670-3Ql6wM3hgtCc");
/* 470 */     assertDecode("Artemis 2.5.2.0", "-AT2520-vEEt0wO6v0cr");
/* 471 */     assertDecode("Vuze 2.2.0.0", "-AZ2200-6wfG2wk6wWLc");
/* 472 */     assertDecode("BT Next Evolution 1.0.9", "-NE1090002IKyMn4g7Ko");
/* 473 */     assertDecode("BitRocket 0.3(32)", "-BR0332-!XVceSn(*KIl");
/* 474 */     assertDecode("Mainline 6.0 Beta", "2D555436 3030422D A78DC290 C3F7BDE0 15EC3CC7");
/* 475 */     assertDecode("FlashGet 1.80", "2D464730 31383075 F8005782 1359D64B B3DFD265");
/* 476 */     assertDecode("GetRight 6.3", "-GR6300-13s3iFKmbArc");
/* 477 */     assertDecode("Halite 0.2.9", "-HL0290-xUO*9ugvENUE");
/* 478 */     assertDecode("KTorrent 1.1 RC1", "-KT11R1-693649213030");
/* 479 */     assertDecode("KTorrent 3.0", "2D4B543330302D006A7139727958377731756A4B");
/* 480 */     assertDecode("libTorrent (Rakshasa) 0.11.2 / rTorrent*", "2D6C74304232302D0D739B93E6BE21FEBB557B20");
/* 481 */     assertDecode("libtorrent (Rasterbar) 0.13.0", "-LT0D00-eZ0PwaDDr-~v");
/* 482 */     assertDecode("linkage 0.1.4", "-LK0140-ATIV~nbEQAMr");
/* 483 */     assertDecode("LimeWire", "2D4C57303030312D31E0B3A0B46F7D4E954F4103");
/* 484 */     assertDecode("Lphant 3.02", "2D4C5030 3330322D 00383336 35363935 37373030");
/* 485 */     assertDecode("Shareaza 2.1.3.2", "2D535A323133322D000000000000000000000000");
/* 486 */     assertDecode("SymTorrent 1.17", "-ST0117-01234567890!");
/* 487 */     assertDecode("Transmission 0.6", "-TR0006-01234567890!");
/* 488 */     assertDecode("Transmission 0.72 (Dev)", "-TR072Z-zihst5yvg22f");
/* 489 */     assertDecode("Transmission 0.72", "-TR0072-8vd6hrmp04an");
/* 490 */     assertDecode("TuoTu 2.1.0", "-TT210w-dq!nWf~Qcext");
/* 491 */     assertDecode("µTorrent 1.7.0 Beta", "2D555431 3730422D 92844644 1DB0A094 A01C01E5");
/* 492 */     assertDecode("哇嘎 (Vagaa) 2.6.4.4", "2D5647323634342D4FD62CDA69E235717E3BB94B");
/*     */     
/* 494 */     assertDecode("FireTorrent 0.3.0.0", "-WY0300-6huHF5Pr7Vde");
/* 495 */     assertDecode("CacheLogic 25.1-26", "-PC251Q-6huHF5Pr7Vde");
/* 496 */     assertDecode("KGet 2.4.5.0", "-KG2450-BDEw8OM14Hk6");
/*     */     
/*     */ 
/* 499 */     System.out.println();
/*     */     
/*     */ 
/* 502 */     System.out.println("Testing Shadow style clients...");
/* 503 */     assertDecode("ABC", "A--------YMyoBPXYy2L");
/* 504 */     assertDecode("ABC 2.6.9", "413236392D2D2D2D345077199FAEC4A673BECA01");
/* 505 */     assertDecode("ABC 3.1", "A310--001v5Gysr4NxNK");
/* 506 */     assertDecode("BitTornado 0.3.12", "T03C-----6tYolxhVUFS");
/* 507 */     assertDecode("BitTornado 0.3.18", "T03I--008gY6iB6Aq27C");
/* 508 */     assertDecode("BitTornado 0.3.9", "T0390----5uL5NvjBe2z");
/* 509 */     assertDecode("Tribler 1", "R100--003hR6s07XWcov");
/* 510 */     assertDecode("Tribler 3.7", "R37---003uApHy851-Pq");
/* 511 */     System.out.println();
/*     */     
/*     */ 
/* 514 */     System.out.println("Testing simple substring clients...");
/* 515 */     assertDecode("Azureus 1", "417A7572 65757300 00000000 000000A0 76F0AEF7");
/* 516 */     assertDecode("Azureus 2.0.3.2", "2D2D2D2D2D417A757265757354694E7A2A6454A7");
/* 517 */     assertDecode("G3 Torrent", "2D473341 6E6F6E79 6D6F7573 70E8D9CB 30250AD4");
/* 518 */     assertDecode("Hurricane Electric", "6172636C696768742E68652EA5860C157A5ADC35");
/* 519 */     assertDecode("Pando", "Pando-6B511B691CAC2E");
/* 520 */     assertDecode("µTorrent 1.7.0 RC", "2D55543137302D00AF8BC5ACCC4631481EB3EB60");
/* 521 */     System.out.println();
/*     */     
/*     */ 
/* 524 */     System.out.println("Testing versioned substring clients...");
/* 525 */     assertDecode("Bitlet 0.1", "4269744C657430319AEA4E02A09E318D70CCF47D");
/* 526 */     assertDecode("BitsOnWheels", "-BOWP05-EPICNZOGQPHP");
/* 527 */     assertDecode("Burst! 1.1.3", "Mbrst1-1-32e3c394b43");
/* 528 */     assertDecode("Opera (Build 7685)", "OP7685f2c1495b1680bf");
/* 529 */     assertDecode("Opera (Build 10063)", "O100634008270e29150a");
/* 530 */     assertDecode("Rufus 0.6.9", "00455253 416E6F6E 796D6F75 7382BE42 75024AE3");
/* 531 */     assertDecode("BitTorrent DNA 1.0", "444E413031303030DD01C9B2DA689E6E02803E91");
/* 532 */     assertDecode("BTuga Revolution 2.1", "BTM21abcdefghijklmno");
/* 533 */     assertDecode("AllPeers 0.70rc30", "4150302E3730726333302D3E3EB87B31F241DBFE");
/* 534 */     assertDecode("External Webseed", "45787420EC7CC30033D7801FEEB713FBB0557AC4");
/* 535 */     assertDecode("QVOD (Build 0054)", "QVOD00541234567890AB");
/* 536 */     assertDecode("Top-BT 1.0.0", "TB100----abcdefghijk");
/* 537 */     System.out.println();
/*     */     
/*     */ 
/* 540 */     System.out.println("Testing BitComet/Lord/Spirit clients...");
/* 541 */     assertDecode("BitComet 0.56", "6578626300387A4463102D6E9AD6723B339F35A9");
/* 542 */     assertDecode("BitLord 0.56", "6578626300384C4F52443200048ECED57BD71028");
/* 543 */     assertDecode("BitSpirit? (" + BAD_PEER_ID + ")", "4D342D302D322D2D6898D9D0CAF25E4555445030");
/* 544 */     assertDecode("BitSpirit v2", "000242539B7ED3E058A8384AA748485454504254");
/* 545 */     assertDecode("BitSpirit v3", "00034253 07248896 44C59530 8A5FF2CA 55445030");
/* 546 */     System.out.println();
/*     */     
/*     */ 
/* 549 */     System.out.println("Testing new mainline style clients...");
/* 550 */     assertDecode("Mainline 5.0.7", "M5-0-7--9aa757efd5be");
/* 551 */     assertDecode("Amazon AWS S3", "S3-1-0-0--0123456789");
/* 552 */     System.out.println();
/*     */     
/*     */ 
/* 555 */     System.out.println("Testing various specialised clients...");
/* 556 */     assertDecode("Mainline", "0000000000000000000000004C53441933104277");
/* 557 */     assertDecode(UNKNOWN + " [" + FAKE + ": ZipTorrent 1.6.0.0]", "-ZT1600-bLAdeY9rdjbe");
/*     */     
/* 559 */     assertDecode("Tixati 1.37", "TIX0137-i6i6f0i5d5b7");
/* 560 */     assertDecode("folx 0.9", "2D464C3039C6F22D5F436863327A6D792E283867");
/*     */     
/*     */ 
/* 563 */     System.out.println();
/*     */     
/*     */ 
/* 566 */     System.out.println("Testing unknown (random byte?) clients...");
/* 567 */     assertDecode(UNKNOWN + " [--------1}-/---A---<]", "0000000000000000317DA32F831FF041A515FE3C");
/* 568 */     assertDecode(UNKNOWN + " [------- --  ------@(]", "000000DF05020020100020200008000000004028");
/* 569 */     assertDecode(UNKNOWN + " [-----------D-y-I--aO]", "0000000000000000F106CE44F179A2498FAC614F");
/* 570 */     assertDecode(UNKNOWN + " [--c--_-5-\\----t-#---]", "E7F163BB0E5FCD35005C09A11BC274C42385A1A0");
/* 571 */     System.out.println();
/*     */     
/*     */ 
/* 574 */     System.out.println("Testing unknown AZ style clients...");
/*     */     
/* 576 */     String unknown_az = MessageText.getString("PeerSocket.unknown_az_style", new String[] { "BD", "0.3.0.0" });
/* 577 */     assertDecode(unknown_az, "-BD0300-1SGiRZ8uWpWH");
/* 578 */     unknown_az = MessageText.getString("PeerSocket.unknown_az_style", new String[] { "wF", "2.2.0.0" });
/* 579 */     assertDecode(unknown_az, "2D7746323230302D9DFF296B56AFC2DF751C609C");
/* 580 */     unknown_az = MessageText.getString("PeerSocket.unknown_az_style", new String[] { "X1", "0.0.6.4" });
/* 581 */     assertDecode(unknown_az, "2D5831303036342D12FB8A5B954153A114267F1F");
/*     */     
/*     */ 
/* 584 */     System.out.println();
/*     */     
/*     */ 
/* 587 */     System.out.println("Testing unknown Shadow style clients...");
/*     */     
/* 589 */     String unknown_shadow = MessageText.getString("PeerSocket.unknown_shadow_style", new String[] { "B", "1.2" });
/* 590 */     assertDecode(unknown_shadow, "B12------xgTofhetSVQ");
/* 591 */     System.out.println();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 600 */     assertDecode("µTorrent Mac 1.5.11", "2D554D3135313130C964BE6F15CA71EF02AF2DD7");
/*     */     
/* 602 */     assertDecode("MediaGet", "2D4D47314372302D3234705F6436000055673362");
/*     */     
/* 604 */     assertDecode("Invalid PeerID 0.0.0.0", "-#@0000-Em6o1EmvwLtD");
/*     */     
/* 606 */     assertDecode("MediaGet2 2.1", "2D4D47323111302D3234705F6436706E55673362");
/*     */     
/* 608 */     assertDecode("Ares 2.1.7.1", "-AN2171-nr17R1h19O7n");
/*     */     
/* 610 */     assertDecode("µTorrent 3.4.0", "2D55543334302D000971FDE48C3688D2023506FC");
/*     */     
/* 612 */     assertDecode("BitTorrent 7.9.1", "2D42543739312D00A5792226709266A467EAD700");
/*     */     
/* 614 */     assertDecode("Tixati 1.1.0.7", "-TX1107-811513660630");
/*     */     
/* 616 */     assertDecode("Torch 6.2.9.2", "-TB6292-jhBrpKfnZ!6e");
/*     */     
/* 618 */     assertDecode("WebTorrent 0.0.6.8", "-WW0068-b9539e1e4f95");
/*     */     
/* 620 */     assertDecode("BitLord 2.4.4-311", "-BL244311-b9539e1e95");
/*     */     
/* 622 */     System.out.println("Done.");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/BTPeerIDByteDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */