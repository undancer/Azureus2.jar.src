/*     */ package com.aelitis.azureus.core.peermanager.utils;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ public class BTPeerIDByteDecoderDefinitions
/*     */ {
/*  28 */   private static final HashMap az_style_code_map = new HashMap();
/*  29 */   private static final HashMap az_client_version_map = new HashMap();
/*     */   
/*     */ 
/*  32 */   private static final HashMap shadow_style_code_map = new HashMap();
/*  33 */   private static final HashMap shadow_client_version_map = new HashMap();
/*     */   
/*     */ 
/*  36 */   private static final HashMap mainline_style_code_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  42 */   private static final ArrayList custom_style_client_list = new ArrayList();
/*     */   
/*     */   static final String VER_AZ_THREE_DIGITS = "1.2.3";
/*     */   
/*     */   static final String VER_AZ_THREE_DIGITS_PLUS_MNEMONIC = "1.2.3 [4]";
/*     */   
/*     */   static final String VER_AZ_ONE_MAJ_TWO_MIN_PLUS_MNEMONIC = "1.23 [4]";
/*     */   
/*     */   static final String VER_AZ_FOUR_DIGITS = "1.2.3.4";
/*     */   
/*     */   static final String VER_AZ_V_FOUR_DIGITS = "v1.2.3.4";
/*     */   static final String VER_AZ_TWO_MAJ_TWO_MIN = "12.34";
/*     */   static final String VER_AZ_SKIP_FIRST_ONE_MAJ_TWO_MIN = "2.34";
/*     */   static final String VER_AZ_KTORRENT_STYLE = "1.2.3=[RD].4";
/*     */   static final String VER_AZ_TRANSMISSION_STYLE = "transmission";
/*     */   static final String VER_AZ_LAST_THREE_DIGITS = "2.3.4";
/*     */   static final String VER_AZ_THREE_ALPHANUMERIC_DIGITS = "2.33.4";
/*     */   static final String VER_BLOCK = "abcde";
/*     */   static final String VER_DOTTED_BLOCK = "a.b.c.d.e";
/*     */   static final String VER_BYTE_BLOCK_DOTTED_CHAR = "abcde -> a.b.c.d.e";
/*     */   static final String VER_TWOBYTE_BLOCK_DOTTED_CHAR = "abcde -> ab.cd";
/*     */   static final String VER_BITS_ON_WHEELS = "BOW-STYLE";
/*     */   static final String VER_TWO_BYTE_THREE_PART = "ab -> a . b/10 . b%10";
/*     */   static final String NO_VERSION = "NO_VERSION";
/*     */   static final String VER_BYTE_UM_STYLE = "abcd -> a.b.cd";
/*     */   static final String VER_BITLORD = "abcdef -> a.b.c-edf";
/*     */   
/*     */   private static void addAzStyle(String id, String client)
/*     */   {
/*  71 */     addAzStyle(id, client, "1.2.3.4");
/*     */   }
/*     */   
/*     */   private static void addAzStyle(String id, String client, String version_style) {
/*  75 */     if (id.length() != 2) throw new RuntimeException("not two chars long - " + id);
/*  76 */     az_style_code_map.put(id, client);
/*  77 */     az_client_version_map.put(client, version_style);
/*     */   }
/*     */   
/*     */   private static void addShadowStyle(char id, String client) {
/*  81 */     addShadowStyle(id, client, "1.2.3");
/*     */   }
/*     */   
/*     */   private static void addShadowStyle(char id, String client, String version_style) {
/*  85 */     shadow_style_code_map.put("" + id, client);
/*  86 */     shadow_client_version_map.put(client, version_style);
/*     */   }
/*     */   
/*     */   private static void addMainlineStyle(char id, String client) {
/*  90 */     mainline_style_code_map.put("" + id, client);
/*     */   }
/*     */   
/*     */   private static ClientData addSimpleClient(String client_name, String identifier) {
/*  94 */     return addSimpleClient(client_name, identifier, 0);
/*     */   }
/*     */   
/*     */   private static ClientData addSimpleClient(String client_name, String identifier, int position) {
/*  98 */     ClientData result = new ClientData(client_name, identifier, position);
/*  99 */     custom_style_client_list.add(result);
/* 100 */     return result;
/*     */   }
/*     */   
/*     */   private static void addVersionedClient(ClientData client, String version_type, int length) {
/* 104 */     addVersionedClient(client, version_type, length, null);
/*     */   }
/*     */   
/*     */   private static void addVersionedClient(ClientData client, String version_type, int length, String format) {
/* 108 */     addVersionedClient(client, version_type, length, format, client.simple_string.length() + client.simple_string_pos);
/*     */   }
/*     */   
/*     */   private static void addVersionedClient(ClientData client, String version_type, int length, int version_pos) {
/* 112 */     addVersionedClient(client, version_type, length, null, version_pos);
/*     */   }
/*     */   
/*     */   private static void addVersionedClient(ClientData client, String version_type, int length, String format, int version_pos) {
/* 116 */     client.version_data = new VersionNumberData(version_type, length, format, version_pos);
/*     */   }
/*     */   
/*     */   public static String getAzStyleClientName(String peer_id) {
/* 120 */     return (String)az_style_code_map.get(peer_id.substring(1, 3));
/*     */   }
/*     */   
/*     */   public static String getShadowStyleClientName(String peer_id) {
/* 124 */     return (String)shadow_style_code_map.get(peer_id.substring(0, 1));
/*     */   }
/*     */   
/*     */   public static String getMainlineStyleClientName(String peer_id) {
/* 128 */     return (String)mainline_style_code_map.get(peer_id.substring(0, 1));
/*     */   }
/*     */   
/*     */   public static String getAzStyleClientVersion(String client_name, String peer_id) {
/* 132 */     String version_scheme = (String)az_client_version_map.get(client_name);
/* 133 */     if (version_scheme == "NO_VERSION") return null;
/*     */     try {
/* 135 */       return client_name + " " + BTPeerIDByteDecoderUtils.decodeAzStyleVersionNumber(peer_id.substring(3, 7), version_scheme);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 140 */       BTPeerIDByteDecoder.logUnknownClient(peer_id); }
/* 141 */     return null;
/*     */   }
/*     */   
/*     */   public static ClientData getSubstringStyleClient(String peer_id)
/*     */   {
/* 146 */     ClientData cd = null;
/* 147 */     for (int i = 0; i < custom_style_client_list.size(); i++) {
/* 148 */       cd = (ClientData)custom_style_client_list.get(i);
/* 149 */       if (peer_id.startsWith(cd.simple_string, cd.simple_string_pos)) {
/* 150 */         return cd;
/*     */       }
/*     */     }
/* 153 */     return null;
/*     */   }
/*     */   
/*     */   public static String getSubstringStyleClientVersion(ClientData client_data, String peer_id, byte[] peer_id_bytes) {
/* 157 */     VersionNumberData verdata = client_data.version_data;
/* 158 */     if (verdata == null) { return null;
/*     */     }
/* 160 */     String version_scheme = verdata.type;
/* 161 */     String version_string = null;
/*     */     try {
/* 163 */       if (version_scheme == "ab -> a . b/10 . b%10") {
/* 164 */         int start_byte_index = verdata.pos;
/* 165 */         version_string = BTPeerIDByteDecoderUtils.getTwoByteThreePartVersion(peer_id_bytes[start_byte_index], peer_id_bytes[(start_byte_index + 1)]);
/*     */       }
/* 167 */       else if ((version_scheme == "abcde") && (verdata.length == -1)) {
/* 168 */         version_string = BTPeerIDByteDecoderUtils.extractReadableVersionSubstringFromPeerID(peer_id.substring(verdata.pos, peer_id.length()));
/*     */       }
/*     */       else {
/* 171 */         version_string = BTPeerIDByteDecoderUtils.decodeCustomVersionNumber(peer_id.substring(verdata.pos, verdata.pos + verdata.length), version_scheme);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 176 */       if (verdata.fmt == null) return client_data.client_name + " " + version_string;
/* 177 */       return client_data.client_name + verdata.fmt.replaceFirst("%s", version_string);
/*     */     }
/*     */     catch (Exception e) {
/* 180 */       BTPeerIDByteDecoder.logUnknownClient(peer_id); }
/* 181 */     return null;
/*     */   }
/*     */   
/*     */   public static String formatUnknownAzStyleClient(String peer_id)
/*     */   {
/* 186 */     String version_string = peer_id.substring(3, 7);
/*     */     try {
/* 188 */       version_string = BTPeerIDByteDecoderUtils.decodeAzStyleVersionNumber(version_string, "1.2.3.4");
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/* 193 */     return MessageText.getString("PeerSocket.unknown_az_style", new String[] { peer_id.substring(1, 3), version_string });
/*     */   }
/*     */   
/*     */ 
/*     */   public static String formatUnknownShadowStyleClient(String peer_id)
/*     */   {
/* 199 */     String version_string = BTPeerIDByteDecoderUtils.getShadowStyleVersionNumber(peer_id);
/* 200 */     return MessageText.getString("PeerSocket.unknown_shadow_style", new String[] { peer_id.substring(0, 1), version_string });
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
/*     */   static
/*     */   {
/* 231 */     addAzStyle("AZ", "Vuze", "1.2.3.4");
/*     */     
/*     */ 
/* 234 */     addAzStyle("A~", "Ares", "1.2.3");
/* 235 */     addAzStyle("AG", "Ares", "1.2.3");
/* 236 */     addAzStyle("AN", "Ares", "1.2.3.4");
/*     */     
/* 238 */     addAzStyle("AR", "Ares");
/* 239 */     addAzStyle("AV", "Avicora");
/* 240 */     addAzStyle("AX", "BitPump", "12.34");
/* 241 */     addAzStyle("AT", "Artemis");
/* 242 */     addAzStyle("BB", "BitBuddy", "1.234");
/* 243 */     addAzStyle("BC", "BitComet", "2.34");
/* 244 */     addAzStyle("BE", "BitTorrent SDK");
/* 245 */     addAzStyle("BF", "BitFlu", "NO_VERSION");
/* 246 */     addAzStyle("BG", "BTG", "1.2.3.4");
/* 247 */     addAzStyle("bk", "BitKitten (libtorrent)");
/* 248 */     addAzStyle("BR", "BitRocket", "1.2(34)");
/* 249 */     addAzStyle("BS", "BTSlave");
/* 250 */     addAzStyle("BW", "BitWombat");
/* 251 */     addAzStyle("BX", "BittorrentX");
/* 252 */     addAzStyle("CB", "Shareaza Plus");
/* 253 */     addAzStyle("CD", "Enhanced CTorrent", "12.34");
/* 254 */     addAzStyle("CT", "CTorrent", "1.2.34");
/* 255 */     addAzStyle("DP", "Propagate Data Client");
/* 256 */     addAzStyle("DE", "Deluge", "1.2.3.4");
/* 257 */     addAzStyle("EB", "EBit");
/* 258 */     addAzStyle("ES", "Electric Sheep", "1.2.3");
/* 259 */     addAzStyle("eM", "eMule", "NO_VERSION");
/* 260 */     addAzStyle("FC", "FileCroc");
/* 261 */     addAzStyle("FG", "FlashGet", "2.34");
/* 262 */     addAzStyle("FT", "FoxTorrent/RedSwoosh");
/* 263 */     addAzStyle("GR", "GetRight", "1.2");
/* 264 */     addAzStyle("GS", "GSTorrent");
/* 265 */     addAzStyle("HL", "Halite", "1.2.3");
/* 266 */     addAzStyle("IL", "iLivid", "1.2.3");
/* 267 */     addAzStyle("HN", "Hydranode");
/* 268 */     addAzStyle("KG", "KGet");
/* 269 */     addAzStyle("KT", "KTorrent", "1.2.3=[RD].4");
/* 270 */     addAzStyle("LC", "LeechCraft");
/* 271 */     addAzStyle("LH", "LH-ABC");
/* 272 */     addAzStyle("LK", "linkage", "1.2.3");
/* 273 */     addAzStyle("LP", "Lphant", "12.34");
/* 274 */     addAzStyle("LT", "libtorrent (Rasterbar)", "2.33.4");
/* 275 */     addAzStyle("lt", "libTorrent (Rakshasa)", "2.33.4");
/* 276 */     addAzStyle("LW", "LimeWire", "NO_VERSION");
/* 277 */     addAzStyle("MO", "MonoTorrent");
/* 278 */     addAzStyle("MP", "MooPolice", "1.2.3");
/* 279 */     addAzStyle("MR", "Miro");
/* 280 */     addAzStyle("MT", "MoonlightTorrent");
/* 281 */     addAzStyle("NE", "BT Next Evolution", "1.2.3");
/* 282 */     addAzStyle("NX", "Net Transport");
/* 283 */     addAzStyle("OS", "OneSwarm", "1.2.3.4");
/* 284 */     addAzStyle("OT", "OmegaTorrent");
/* 285 */     addAzStyle("PC", "CacheLogic", "12.3-4");
/* 286 */     addAzStyle("PD", "Pando");
/* 287 */     addAzStyle("PE", "PeerProject");
/* 288 */     addAzStyle("pX", "pHoeniX");
/* 289 */     addAzStyle("qB", "qBittorrent", "2.33.4");
/* 290 */     addAzStyle("QD", "qqdownload");
/* 291 */     addAzStyle("RT", "Retriever");
/* 292 */     addAzStyle("RZ", "RezTorrent");
/* 293 */     addAzStyle("S~", "Shareaza alpha/beta");
/* 294 */     addAzStyle("SB", "SwiftBit");
/* 295 */     addAzStyle("SD", "迅雷在线 (Xunlei)");
/* 296 */     addAzStyle("SG", "GS Torrent", "1.2.3.4");
/* 297 */     addAzStyle("SN", "ShareNET");
/* 298 */     addAzStyle("SP", "BitSpirit");
/* 299 */     addAzStyle("SS", "SwarmScope");
/* 300 */     addAzStyle("ST", "SymTorrent", "2.34");
/* 301 */     addAzStyle("st", "SharkTorrent");
/* 302 */     addAzStyle("SZ", "Shareaza");
/* 303 */     addAzStyle("TB", "Torch");
/* 304 */     addAzStyle("TN", "Torrent.NET");
/* 305 */     addAzStyle("TR", "Transmission", "transmission");
/* 306 */     addAzStyle("TS", "TorrentStorm");
/* 307 */     addAzStyle("TT", "TuoTu", "1.2.3");
/* 308 */     addAzStyle("tT", "tTorrent", "v1.2.3.4");
/* 309 */     addAzStyle("TX", "Tixati");
/* 310 */     addAzStyle("UL", "uLeecher!");
/* 311 */     addAzStyle("UT", "µTorrent", "1.2.3 [4]");
/* 312 */     addAzStyle("UM", "µTorrent Mac", "1.2.3 [4]");
/* 313 */     addAzStyle("WT", "Bitlet");
/* 314 */     addAzStyle("WW", "WebTorrent");
/* 315 */     addAzStyle("WY", "FireTorrent");
/* 316 */     addAzStyle("VG", "哇嘎 (Vagaa)", "1.2.3.4");
/* 317 */     addAzStyle("XF", "Xfplay", "1.2.3.4");
/* 318 */     addAzStyle("XL", "迅雷在线 (Xunlei)");
/* 319 */     addAzStyle("XT", "XanTorrent");
/* 320 */     addAzStyle("XX", "XTorrent", "1.2.34");
/* 321 */     addAzStyle("XC", "XTorrent", "1.2.34");
/* 322 */     addAzStyle("ZT", "ZipTorrent");
/* 323 */     addAzStyle("7T", "aTorrent");
/* 324 */     addAzStyle("#@", "Invalid PeerID");
/*     */     
/* 326 */     addShadowStyle('A', "ABC");
/* 327 */     addShadowStyle('O', "Osprey Permaseed");
/* 328 */     addShadowStyle('Q', "BTQueue");
/* 329 */     addShadowStyle('R', "Tribler");
/* 330 */     addShadowStyle('S', "Shad0w");
/* 331 */     addShadowStyle('T', "BitTornado");
/* 332 */     addShadowStyle('U', "UPnP NAT");
/*     */     
/* 334 */     addMainlineStyle('M', "Mainline");
/* 335 */     addMainlineStyle('Q', "Queen Bee");
/*     */     
/*     */ 
/* 338 */     addSimpleClient("µTorrent 1.7.0 RC", "-UT170-");
/* 339 */     addSimpleClient("Azureus 1", "Azureus");
/* 340 */     addSimpleClient("Azureus 2.0.3.2", "Azureus", 5);
/* 341 */     addSimpleClient("Aria 2", "-aria2-");
/* 342 */     addSimpleClient("BitTorrent Plus! II", "PRC.P---");
/* 343 */     addSimpleClient("BitTorrent Plus!", "P87.P---");
/* 344 */     addSimpleClient("BitTorrent Plus!", "S587Plus");
/* 345 */     addSimpleClient("BitTyrant (Azureus Mod)", "AZ2500BT");
/* 346 */     addSimpleClient("Blizzard Downloader", "BLZ");
/* 347 */     addSimpleClient("BTGetit", "BG", 10);
/* 348 */     addSimpleClient("BTugaXP", "btuga");
/* 349 */     addSimpleClient("BTugaXP", "BTuga", 5);
/* 350 */     addSimpleClient("BTugaXP", "oernu");
/* 351 */     addSimpleClient("Deadman Walking", "BTDWV-");
/* 352 */     addSimpleClient("Deadman", "Deadman Walking-");
/* 353 */     addSimpleClient("External Webseed", "Ext");
/* 354 */     addSimpleClient("G3 Torrent", "-G3");
/* 355 */     addSimpleClient("GreedBT 2.7.1", "271-");
/* 356 */     addSimpleClient("Hurricane Electric", "arclight");
/* 357 */     addSimpleClient("HTTP Seed", "-WS");
/* 358 */     addSimpleClient("JVtorrent", "10-------");
/* 359 */     addSimpleClient("Limewire", "LIME");
/* 360 */     addSimpleClient("Martini Man", "martini");
/* 361 */     addSimpleClient("Pando", "Pando");
/* 362 */     addSimpleClient("PeerApp", "PEERAPP");
/* 363 */     addSimpleClient("SimpleBT", "btfans", 4);
/* 364 */     addSimpleClient("Swarmy", "a00---0");
/* 365 */     addSimpleClient("Swarmy", "a02---0");
/* 366 */     addSimpleClient("Teeweety", "T00---0");
/* 367 */     addSimpleClient("TorrentTopia", "346-");
/* 368 */     addSimpleClient("XanTorrent", "DansClient");
/* 369 */     addSimpleClient("MediaGet", "-MG1");
/* 370 */     addSimpleClient("MediaGet2 2.1", "-MG21");
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
/* 381 */     addSimpleClient("Amazon AWS S3", "S3-");
/*     */     
/*     */ 
/* 384 */     ClientData client = null;
/*     */     
/*     */ 
/* 387 */     client = addSimpleClient("BitTorrent DNA", "DNA");
/* 388 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 2, 4);
/*     */     
/*     */ 
/* 391 */     client = addSimpleClient("Opera", "OP");
/* 392 */     addVersionedClient(client, "abcde", 4, " (Build %s)");
/*     */     
/*     */ 
/* 395 */     client = addSimpleClient("Opera", "O");
/* 396 */     addVersionedClient(client, "abcde", 5, " (Build %s)");
/*     */     
/* 398 */     client = addSimpleClient("Burst!", "Mbrst");
/* 399 */     addVersionedClient(client, "a.b.c.d.e", 5);
/*     */     
/* 401 */     client = addSimpleClient("TurboBT", "turbobt");
/* 402 */     addVersionedClient(client, "abcde", 5);
/*     */     
/* 404 */     client = addSimpleClient("BT Protocol Daemon", "btpd");
/* 405 */     addVersionedClient(client, "abcde", 3, 5);
/*     */     
/* 407 */     client = addSimpleClient("Plus!", "Plus");
/* 408 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 3);
/*     */     
/* 410 */     client = addSimpleClient("XBT", "XBT");
/* 411 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 3);
/*     */     
/* 413 */     client = addSimpleClient("BitsOnWheels", "-BOW");
/* 414 */     addVersionedClient(client, "BOW-STYLE", 3);
/*     */     
/* 416 */     client = addSimpleClient("eXeem", "eX");
/* 417 */     addVersionedClient(client, "abcde", 18, " [%s]");
/*     */     
/* 419 */     client = addSimpleClient("MLdonkey", "-ML");
/* 420 */     addVersionedClient(client, "a.b.c.d.e", 5);
/*     */     
/* 422 */     client = addSimpleClient("Bitlet", "BitLet");
/* 423 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 2);
/*     */     
/* 425 */     client = addSimpleClient("AllPeers", "AP");
/* 426 */     addVersionedClient(client, "abcde", -1);
/*     */     
/* 428 */     client = addSimpleClient("BTuga Revolution", "BTM");
/* 429 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 2);
/*     */     
/* 431 */     client = addSimpleClient("Rufus", "RS", 2);
/* 432 */     addVersionedClient(client, "ab -> a . b/10 . b%10", 2, 0);
/*     */     
/*     */ 
/* 435 */     client = addSimpleClient("BitMagnet", "BM", 2);
/* 436 */     addVersionedClient(client, "ab -> a . b/10 . b%10", 2, 0);
/*     */     
/* 438 */     client = addSimpleClient("QVOD", "QVOD");
/* 439 */     addVersionedClient(client, "abcde", 4, " (Build %s)");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 444 */     client = addSimpleClient("Top-BT", "TB");
/* 445 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 3);
/*     */     
/* 447 */     client = addSimpleClient("Tixati", "TIX");
/* 448 */     addVersionedClient(client, "abcde -> ab.cd", 4);
/*     */     
/* 450 */     client = addSimpleClient("folx", "-FL");
/* 451 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 2);
/*     */     
/* 453 */     client = addSimpleClient("µTorrent Mac", "-UM");
/* 454 */     addVersionedClient(client, "abcd -> a.b.cd", 4);
/*     */     
/* 456 */     client = addSimpleClient("µTorrent", "-UT");
/* 457 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 3);
/*     */     
/* 459 */     client = addSimpleClient("BitTorrent", "-BT");
/* 460 */     addVersionedClient(client, "abcde -> a.b.c.d.e", 3);
/*     */     
/* 462 */     client = addSimpleClient("BitLord", "-BL");
/* 463 */     addVersionedClient(client, "abcdef -> a.b.c-edf", 6, 3);
/*     */   }
/*     */   
/*     */   static class ClientData
/*     */   {
/*     */     final String client_name;
/*     */     private final int simple_string_pos;
/*     */     private final String simple_string;
/*     */     private BTPeerIDByteDecoderDefinitions.VersionNumberData version_data;
/*     */     
/*     */     ClientData(String client_name, String simple_string, int simple_string_pos) {
/* 474 */       this.simple_string_pos = simple_string_pos;
/* 475 */       this.simple_string = simple_string;
/* 476 */       this.client_name = client_name;
/* 477 */       this.version_data = null;
/*     */     }
/*     */   }
/*     */   
/*     */   static class VersionNumberData
/*     */   {
/*     */     final String type;
/*     */     final int pos;
/*     */     final String fmt;
/*     */     final int length;
/*     */     
/*     */     VersionNumberData(String type, int length, String formatter, int position)
/*     */     {
/* 490 */       this.type = type;
/* 491 */       this.pos = position;
/* 492 */       this.fmt = formatter;
/* 493 */       this.length = length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/BTPeerIDByteDecoderDefinitions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */