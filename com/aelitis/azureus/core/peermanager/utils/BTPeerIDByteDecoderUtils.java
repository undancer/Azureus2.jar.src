/*     */ package com.aelitis.azureus.core.peermanager.utils;
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
/*     */ class BTPeerIDByteDecoderUtils
/*     */ {
/*     */   public static String decodeMnemonic(char c)
/*     */   {
/*  26 */     switch (c) {
/*     */     case 'B': 
/*     */     case 'b': 
/*  29 */       return "Beta";
/*     */     case 'X': 
/*     */     case 'Z': 
/*     */     case 'x': 
/*  33 */       return "(Dev)";
/*     */     }
/*  35 */     return null;
/*     */   }
/*     */   
/*     */   public static String decodeNumericValueOfByte(byte b) {
/*  39 */     return String.valueOf(b & 0xFF);
/*     */   }
/*     */   
/*     */   public static String decodeNumericValueOfByte(byte b, int min_digits) {
/*  43 */     String result = decodeNumericValueOfByte(b);
/*  44 */     while (result.length() < min_digits) result = "0" + result;
/*  45 */     return result;
/*     */   }
/*     */   
/*     */   public static String decodeNumericChar(char c) {
/*  49 */     String result = decodeAlphaNumericChar(c);
/*  50 */     if ((result == null) || (result.length() == 1)) return result;
/*  51 */     return null;
/*     */   }
/*     */   
/*     */   public static String intchar(char c) {
/*  55 */     String result = decodeNumericChar(c);
/*  56 */     if (result == null) throw new IllegalArgumentException("not an integer character: " + c);
/*  57 */     return result;
/*     */   }
/*     */   
/*     */   public static String decodeAlphaNumericChar(char c) {
/*  61 */     if (('0' <= c) && (c <= '9')) {
/*  62 */       return String.valueOf(c);
/*     */     }
/*  64 */     if (('A' <= c) && (c <= 'Z')) {
/*  65 */       return String.valueOf(10 + (c - 'A'));
/*     */     }
/*  67 */     if (('a' <= c) && (c <= 'z')) {
/*  68 */       return String.valueOf(36 + (c - 'A'));
/*     */     }
/*  70 */     if (c == '.') {
/*  71 */       return "62";
/*     */     }
/*  73 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isAzStyle(String peer_id) {
/*  77 */     if (peer_id.charAt(0) != '-') return false;
/*  78 */     if (peer_id.charAt(7) == '-') { return true;
/*     */     }
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
/*  91 */     if (peer_id.substring(1, 3).equals("FG")) return true;
/*  92 */     if (peer_id.substring(1, 3).equals("LH")) return true;
/*  93 */     if (peer_id.substring(1, 3).equals("NE")) return true;
/*  94 */     if (peer_id.substring(1, 3).equals("KT")) return true;
/*  95 */     if (peer_id.substring(1, 3).equals("SP")) return true;
/*  96 */     return false;
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
/*     */ 
/*     */   public static boolean isShadowStyle(String peer_id)
/*     */   {
/* 128 */     if (peer_id.charAt(5) != '-') return false;
/* 129 */     if (!Character.isLetter(peer_id.charAt(0))) return false;
/* 130 */     if ((!Character.isDigit(peer_id.charAt(1))) && (peer_id.charAt(1) != '-')) { return false;
/*     */     }
/*     */     
/* 133 */     for (int last_ver_num_index = 4; 
/* 134 */         last_ver_num_index > 0; last_ver_num_index--) {
/* 135 */       if (peer_id.charAt(last_ver_num_index) != '-') {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/* 140 */     for (int i = 1; i <= last_ver_num_index; i++) {
/* 141 */       char c = peer_id.charAt(i);
/* 142 */       if (c == '-') return false;
/* 143 */       if (decodeAlphaNumericChar(c) == null) { return false;
/*     */       }
/*     */     }
/* 146 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isMainlineStyle(String peer_id)
/*     */   {
/* 155 */     return (peer_id.charAt(2) == '-') && (peer_id.charAt(7) == '-') && ((peer_id.charAt(4) == '-') || (peer_id.charAt(5) == '-'));
/*     */   }
/*     */   
/*     */   public static boolean isPossibleSpoofClient(String peer_id)
/*     */   {
/* 160 */     return (peer_id.endsWith("UDP0")) || (peer_id.endsWith("HTTPBT"));
/*     */   }
/*     */   
/*     */   public static String getMainlineStyleVersionNumber(String peer_id) {
/* 164 */     boolean two_digit_in_middle = peer_id.charAt(5) == '-';
/* 165 */     String middle_part = decodeNumericChar(peer_id.charAt(3));
/* 166 */     if (two_digit_in_middle) {
/* 167 */       middle_part = join(middle_part, decodeNumericChar(peer_id.charAt(4)));
/*     */     }
/* 169 */     return joinAsDotted(decodeNumericChar(peer_id.charAt(1)), middle_part, decodeNumericChar(peer_id.charAt(two_digit_in_middle ? 6 : 5)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getShadowStyleVersionNumber(String peer_id)
/*     */   {
/* 176 */     String ver_number = decodeAlphaNumericChar(peer_id.charAt(1));
/* 177 */     if (ver_number == null) return null;
/* 178 */     for (int i = 2; (i < 6) && (ver_number != null); i++) {
/* 179 */       char c = peer_id.charAt(i);
/* 180 */       if (c == '-') break;
/* 181 */       ver_number = joinAsDotted(ver_number, decodeAlphaNumericChar(peer_id.charAt(i)));
/* 182 */       if (ver_number == null) {
/* 183 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 187 */     while (ver_number.endsWith(".0")) ver_number = ver_number.substring(0, ver_number.length() - 2);
/* 188 */     return ver_number;
/*     */   }
/*     */   
/*     */   public static String decodeAzStyleVersionNumber(String version_data, String version_scheme) {
/* 192 */     char a = version_data.charAt(0);
/* 193 */     char b = version_data.charAt(1);
/* 194 */     char c = version_data.charAt(2);
/* 195 */     char d = version_data.charAt(3);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 201 */     if (version_scheme == "transmission")
/*     */     {
/* 203 */       if (version_data.startsWith("000")) {
/* 204 */         version_scheme = "3.4";
/*     */ 
/*     */ 
/*     */       }
/* 208 */       else if (version_data.startsWith("00")) {
/* 209 */         version_scheme = "2.34";
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 214 */         version_scheme = "1.23 [4]";
/*     */       }
/*     */     }
/*     */     
/* 218 */     if (version_scheme == "1.2.3.4") {
/* 219 */       return decodeAlphaNumericChar(a) + "." + decodeAlphaNumericChar(b) + "." + decodeAlphaNumericChar(c) + "." + decodeAlphaNumericChar(d);
/*     */     }
/*     */     
/* 222 */     if ((version_scheme == "1.2.3") || (version_scheme == "1.2.3 [4]") || (version_scheme == "1.23 [4]"))
/*     */     {
/*     */       String result;
/*     */       
/*     */       String result;
/* 227 */       if (version_scheme == "1.23 [4]") {
/* 228 */         result = intchar(a) + "." + intchar(b) + intchar(c);
/*     */       }
/*     */       else {
/* 231 */         result = intchar(a) + "." + intchar(b) + "." + intchar(c);
/*     */       }
/* 233 */       if ((version_scheme == "1.2.3 [4]") || (version_scheme == "1.23 [4]"))
/*     */       {
/* 235 */         String mnemonic = decodeMnemonic(d);
/* 236 */         if (mnemonic != null) result = result + " " + mnemonic;
/*     */       }
/* 238 */       return result;
/*     */     }
/* 240 */     if (version_scheme == "12.34") {
/* 241 */       return (a == '0' ? "" : intchar(a)) + intchar(b) + "." + intchar(c) + intchar(d);
/*     */     }
/* 243 */     if (version_scheme == "2.3.4") {
/* 244 */       return intchar(b) + "." + intchar(c) + "." + intchar(d);
/*     */     }
/* 246 */     if (version_scheme == "2.33.4") {
/* 247 */       return decodeAlphaNumericChar(a) + "." + decodeAlphaNumericChar(b) + "." + decodeAlphaNumericChar(c);
/*     */     }
/* 249 */     if (version_scheme == "2.34") {
/* 250 */       return intchar(b) + "." + intchar(c) + intchar(d);
/*     */     }
/* 252 */     if (version_scheme == "1.2.3=[RD].4")
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 257 */       switch (c) {
/*     */       case 'R': 
/* 259 */         return intchar(a) + "." + intchar(b) + " RC" + intchar(d);
/*     */       case 'D': 
/* 261 */         return intchar(a) + "." + intchar(b) + " Dev";
/*     */       }
/* 263 */       return intchar(a) + "." + intchar(b);
/*     */     }
/*     */     
/* 266 */     if (version_scheme.equals("1.234")) {
/* 267 */       return intchar(a) + "." + intchar(b) + intchar(c) + intchar(d);
/*     */     }
/* 269 */     if (version_scheme.equals("1.2(34)")) {
/* 270 */       return intchar(a) + "." + intchar(b) + "(" + intchar(c) + intchar(d) + ")";
/*     */     }
/* 272 */     if (version_scheme.equals("1.2.34")) {
/* 273 */       return intchar(a) + "." + intchar(b) + "." + intchar(c) + intchar(d);
/*     */     }
/* 275 */     if (version_scheme.equals("v1234")) {
/* 276 */       return "v" + intchar(a) + intchar(b) + intchar(c) + intchar(d);
/*     */     }
/* 278 */     if (version_scheme.equals("1.2")) {
/* 279 */       return intchar(a) + "." + intchar(b);
/*     */     }
/* 281 */     if (version_scheme.equals("3.4")) {
/* 282 */       return intchar(c) + "." + intchar(d);
/*     */     }
/* 284 */     if (version_scheme.equals("12.3-4")) {
/* 285 */       return decodeAlphaNumericChar(a) + decodeAlphaNumericChar(b) + "." + decodeAlphaNumericChar(c) + "-" + decodeAlphaNumericChar(d);
/*     */     }
/* 287 */     if (version_scheme == "v1.2.3.4") {
/* 288 */       return "v" + decodeAzStyleVersionNumber(version_data, "1.2.3.4");
/*     */     }
/*     */     
/* 291 */     throw new RuntimeException("unknown AZ style version number scheme - " + version_scheme);
/*     */   }
/*     */   
/*     */   public static String getTwoByteThreePartVersion(byte b1, byte b2)
/*     */   {
/* 296 */     String min_part = decodeNumericValueOfByte(b2, 2);
/* 297 */     return joinAsDotted(decodeNumericValueOfByte(b1), min_part.substring(0, 1), min_part.substring(1, 2));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String extractReadableVersionSubstringFromPeerID(String peer_id)
/*     */   {
/* 305 */     for (int i = 0; i < peer_id.length(); i++) {
/* 306 */       char c = peer_id.charAt(i);
/*     */       
/*     */ 
/*     */ 
/* 310 */       if ((!Character.isLetter(c)) && 
/* 311 */         (!Character.isDigit(c)) && 
/* 312 */         (c != '.'))
/*     */       {
/* 314 */         return peer_id.substring(0, i); }
/*     */     }
/* 316 */     return peer_id;
/*     */   }
/*     */   
/*     */   public static String decodeCustomVersionNumber(String version_data, String version_scheme) {
/* 320 */     if (version_scheme == "abcde") {
/* 321 */       return version_data;
/*     */     }
/* 323 */     if ((version_scheme == "a.b.c.d.e") || (version_scheme == "abcde -> a.b.c.d.e"))
/*     */     {
/* 325 */       int inc_size = version_scheme == "a.b.c.d.e" ? 2 : 1;
/* 326 */       String result = version_data.substring(0, 1);
/* 327 */       for (int i = 0 + inc_size; i < version_data.length(); i += inc_size) {
/* 328 */         result = joinAsDotted(result, String.valueOf(version_data.charAt(i)));
/*     */       }
/* 330 */       return result;
/*     */     }
/* 332 */     if (version_scheme == "abcde -> ab.cd") {
/* 333 */       String result = "";
/* 334 */       for (int i = 0; i < version_data.length(); i += 2) {
/* 335 */         String s = version_data.substring(i, i + 2);
/* 336 */         if (i == 0) {
/* 337 */           if (s.charAt(0) == '0') {
/* 338 */             s = s.substring(1);
/*     */           }
/* 340 */           result = s;
/*     */         } else {
/* 342 */           result = joinAsDotted(result, s);
/*     */         }
/*     */       }
/* 345 */       return result;
/*     */     }
/* 347 */     if (version_scheme == "BOW-STYLE") {
/* 348 */       if (version_data.equals("A0C")) return "1.0.6";
/* 349 */       if (version_data.equals("A0B")) return "1.0.5";
/* 350 */       throw new RuntimeException("Unknown BitsOnWheels version number - " + version_data);
/*     */     }
/* 352 */     if (version_scheme == "abcd -> a.b.cd")
/*     */     {
/* 354 */       char[] chars = version_data.toCharArray();
/*     */       
/* 356 */       return chars[0] + "." + chars[1] + "." + chars[2] + chars[3]; }
/* 357 */     if (version_scheme == "abcdef -> a.b.c-edf")
/*     */     {
/* 359 */       char[] chars = version_data.toCharArray();
/*     */       
/* 361 */       return chars[0] + "." + chars[1] + "." + chars[2] + "-" + chars[3] + chars[4] + chars[5];
/*     */     }
/*     */     
/* 364 */     throw new RuntimeException("unknown custom version number scheme - " + version_scheme);
/*     */   }
/*     */   
/*     */   private static String join(String a, String b)
/*     */   {
/* 369 */     if (a == null) return null;
/* 370 */     if (b == null) return null;
/* 371 */     return a + b;
/*     */   }
/*     */   
/*     */   private static String joinAsDotted(String a, String b) {
/* 375 */     if (a == null) return null;
/* 376 */     if (b == null) return null;
/* 377 */     return a + "." + b;
/*     */   }
/*     */   
/*     */   private static String joinAsDotted(String a, String b, String c)
/*     */   {
/* 382 */     if (a == null) return null;
/* 383 */     if (b == null) return null;
/* 384 */     if (c == null) return null;
/* 385 */     return a + "." + b + "." + c;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/BTPeerIDByteDecoderUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */