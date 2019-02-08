/*     */ package org.apache.commons.lang;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
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
/*     */ public class Entities
/*     */ {
/*  38 */   private static final String[][] BASIC_ARRAY = { { "quot", "34" }, { "amp", "38" }, { "lt", "60" }, { "gt", "62" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  45 */   private static final String[][] APOS_ARRAY = { { "apos", "39" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  50 */   static final String[][] ISO8859_1_ARRAY = { { "nbsp", "160" }, { "iexcl", "161" }, { "cent", "162" }, { "pound", "163" }, { "curren", "164" }, { "yen", "165" }, { "brvbar", "166" }, { "sect", "167" }, { "uml", "168" }, { "copy", "169" }, { "ordf", "170" }, { "laquo", "171" }, { "not", "172" }, { "shy", "173" }, { "reg", "174" }, { "macr", "175" }, { "deg", "176" }, { "plusmn", "177" }, { "sup2", "178" }, { "sup3", "179" }, { "acute", "180" }, { "micro", "181" }, { "para", "182" }, { "middot", "183" }, { "cedil", "184" }, { "sup1", "185" }, { "ordm", "186" }, { "raquo", "187" }, { "frac14", "188" }, { "frac12", "189" }, { "frac34", "190" }, { "iquest", "191" }, { "Agrave", "192" }, { "Aacute", "193" }, { "Acirc", "194" }, { "Atilde", "195" }, { "Auml", "196" }, { "Aring", "197" }, { "AElig", "198" }, { "Ccedil", "199" }, { "Egrave", "200" }, { "Eacute", "201" }, { "Ecirc", "202" }, { "Euml", "203" }, { "Igrave", "204" }, { "Iacute", "205" }, { "Icirc", "206" }, { "Iuml", "207" }, { "ETH", "208" }, { "Ntilde", "209" }, { "Ograve", "210" }, { "Oacute", "211" }, { "Ocirc", "212" }, { "Otilde", "213" }, { "Ouml", "214" }, { "times", "215" }, { "Oslash", "216" }, { "Ugrave", "217" }, { "Uacute", "218" }, { "Ucirc", "219" }, { "Uuml", "220" }, { "Yacute", "221" }, { "THORN", "222" }, { "szlig", "223" }, { "agrave", "224" }, { "aacute", "225" }, { "acirc", "226" }, { "atilde", "227" }, { "auml", "228" }, { "aring", "229" }, { "aelig", "230" }, { "ccedil", "231" }, { "egrave", "232" }, { "eacute", "233" }, { "ecirc", "234" }, { "euml", "235" }, { "igrave", "236" }, { "iacute", "237" }, { "icirc", "238" }, { "iuml", "239" }, { "eth", "240" }, { "ntilde", "241" }, { "ograve", "242" }, { "oacute", "243" }, { "ocirc", "244" }, { "otilde", "245" }, { "ouml", "246" }, { "divide", "247" }, { "oslash", "248" }, { "ugrave", "249" }, { "uacute", "250" }, { "ucirc", "251" }, { "uuml", "252" }, { "yacute", "253" }, { "thorn", "254" }, { "yuml", "255" } };
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
/* 151 */   static final String[][] HTML40_ARRAY = { { "fnof", "402" }, { "Alpha", "913" }, { "Beta", "914" }, { "Gamma", "915" }, { "Delta", "916" }, { "Epsilon", "917" }, { "Zeta", "918" }, { "Eta", "919" }, { "Theta", "920" }, { "Iota", "921" }, { "Kappa", "922" }, { "Lambda", "923" }, { "Mu", "924" }, { "Nu", "925" }, { "Xi", "926" }, { "Omicron", "927" }, { "Pi", "928" }, { "Rho", "929" }, { "Sigma", "931" }, { "Tau", "932" }, { "Upsilon", "933" }, { "Phi", "934" }, { "Chi", "935" }, { "Psi", "936" }, { "Omega", "937" }, { "alpha", "945" }, { "beta", "946" }, { "gamma", "947" }, { "delta", "948" }, { "epsilon", "949" }, { "zeta", "950" }, { "eta", "951" }, { "theta", "952" }, { "iota", "953" }, { "kappa", "954" }, { "lambda", "955" }, { "mu", "956" }, { "nu", "957" }, { "xi", "958" }, { "omicron", "959" }, { "pi", "960" }, { "rho", "961" }, { "sigmaf", "962" }, { "sigma", "963" }, { "tau", "964" }, { "upsilon", "965" }, { "phi", "966" }, { "chi", "967" }, { "psi", "968" }, { "omega", "969" }, { "thetasym", "977" }, { "upsih", "978" }, { "piv", "982" }, { "bull", "8226" }, { "hellip", "8230" }, { "prime", "8242" }, { "Prime", "8243" }, { "oline", "8254" }, { "frasl", "8260" }, { "weierp", "8472" }, { "image", "8465" }, { "real", "8476" }, { "trade", "8482" }, { "alefsym", "8501" }, { "larr", "8592" }, { "uarr", "8593" }, { "rarr", "8594" }, { "darr", "8595" }, { "harr", "8596" }, { "crarr", "8629" }, { "lArr", "8656" }, { "uArr", "8657" }, { "rArr", "8658" }, { "dArr", "8659" }, { "hArr", "8660" }, { "forall", "8704" }, { "part", "8706" }, { "exist", "8707" }, { "empty", "8709" }, { "nabla", "8711" }, { "isin", "8712" }, { "notin", "8713" }, { "ni", "8715" }, { "prod", "8719" }, { "sum", "8721" }, { "minus", "8722" }, { "lowast", "8727" }, { "radic", "8730" }, { "prop", "8733" }, { "infin", "8734" }, { "ang", "8736" }, { "and", "8743" }, { "or", "8744" }, { "cap", "8745" }, { "cup", "8746" }, { "int", "8747" }, { "there4", "8756" }, { "sim", "8764" }, { "cong", "8773" }, { "asymp", "8776" }, { "ne", "8800" }, { "equiv", "8801" }, { "le", "8804" }, { "ge", "8805" }, { "sub", "8834" }, { "sup", "8835" }, { "sube", "8838" }, { "supe", "8839" }, { "oplus", "8853" }, { "otimes", "8855" }, { "perp", "8869" }, { "sdot", "8901" }, { "lceil", "8968" }, { "rceil", "8969" }, { "lfloor", "8970" }, { "rfloor", "8971" }, { "lang", "9001" }, { "rang", "9002" }, { "loz", "9674" }, { "spades", "9824" }, { "clubs", "9827" }, { "hearts", "9829" }, { "diams", "9830" }, { "OElig", "338" }, { "oelig", "339" }, { "Scaron", "352" }, { "scaron", "353" }, { "Yuml", "376" }, { "circ", "710" }, { "tilde", "732" }, { "ensp", "8194" }, { "emsp", "8195" }, { "thinsp", "8201" }, { "zwnj", "8204" }, { "zwj", "8205" }, { "lrm", "8206" }, { "rlm", "8207" }, { "ndash", "8211" }, { "mdash", "8212" }, { "lsquo", "8216" }, { "rsquo", "8217" }, { "sbquo", "8218" }, { "ldquo", "8220" }, { "rdquo", "8221" }, { "bdquo", "8222" }, { "dagger", "8224" }, { "Dagger", "8225" }, { "permil", "8240" }, { "lsaquo", "8249" }, { "rsaquo", "8250" }, { "euro", "8364" } };
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
/* 351 */   public static final Entities XML = new Entities();
/* 352 */   static { XML.addEntities(BASIC_ARRAY);
/* 353 */     XML.addEntities(APOS_ARRAY);
/*     */     
/*     */ 
/*     */ 
/* 357 */     HTML32 = new Entities();
/* 358 */     HTML32.addEntities(BASIC_ARRAY);
/* 359 */     HTML32.addEntities(ISO8859_1_ARRAY);
/*     */     
/*     */ 
/*     */ 
/* 363 */     HTML40 = new Entities();
/* 364 */     fillWithHtml40Entities(HTML40);
/*     */   }
/*     */   
/*     */   static void fillWithHtml40Entities(Entities entities) {
/* 368 */     entities.addEntities(BASIC_ARRAY);
/* 369 */     entities.addEntities(ISO8859_1_ARRAY);
/* 370 */     entities.addEntities(HTML40_ARRAY);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static final Entities HTML32;
/*     */   
/*     */   public static final Entities HTML40;
/*     */   
/*     */   static class PrimitiveEntityMap
/*     */     implements Entities.EntityMap
/*     */   {
/* 382 */     private Map mapNameToValue = new HashMap();
/* 383 */     private IntHashMap mapValueToName = new IntHashMap();
/*     */     
/*     */     public void add(String name, int value) {
/* 386 */       this.mapNameToValue.put(name, new Integer(value));
/* 387 */       this.mapValueToName.put(value, name);
/*     */     }
/*     */     
/*     */     public String name(int value) {
/* 391 */       return (String)this.mapValueToName.get(value);
/*     */     }
/*     */     
/*     */     public int value(String name) {
/* 395 */       Object value = this.mapNameToValue.get(name);
/* 396 */       if (value == null) {
/* 397 */         return -1;
/*     */       }
/* 399 */       return ((Integer)value).intValue();
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract class MapIntMap implements Entities.EntityMap
/*     */   {
/*     */     protected Map mapNameToValue;
/*     */     protected Map mapValueToName;
/*     */     
/*     */     public void add(String name, int value) {
/* 409 */       this.mapNameToValue.put(name, new Integer(value));
/* 410 */       this.mapValueToName.put(new Integer(value), name);
/*     */     }
/*     */     
/*     */     public String name(int value) {
/* 414 */       return (String)this.mapValueToName.get(new Integer(value));
/*     */     }
/*     */     
/*     */     public int value(String name) {
/* 418 */       Object value = this.mapNameToValue.get(name);
/* 419 */       if (value == null) {
/* 420 */         return -1;
/*     */       }
/* 422 */       return ((Integer)value).intValue();
/*     */     }
/*     */   }
/*     */   
/*     */   static class HashEntityMap extends Entities.MapIntMap {
/*     */     public HashEntityMap() {
/* 428 */       this.mapNameToValue = new HashMap();
/* 429 */       this.mapValueToName = new HashMap();
/*     */     }
/*     */   }
/*     */   
/*     */   static class TreeEntityMap extends Entities.MapIntMap {
/*     */     public TreeEntityMap() {
/* 435 */       this.mapNameToValue = new TreeMap();
/* 436 */       this.mapValueToName = new TreeMap();
/*     */     }
/*     */   }
/*     */   
/*     */   static class LookupEntityMap extends Entities.PrimitiveEntityMap {
/*     */     private String[] lookupTable;
/* 442 */     private int LOOKUP_TABLE_SIZE = 256;
/*     */     
/*     */     public String name(int value) {
/* 445 */       if (value < this.LOOKUP_TABLE_SIZE) {
/* 446 */         return lookupTable()[value];
/*     */       }
/* 448 */       return super.name(value);
/*     */     }
/*     */     
/*     */     private String[] lookupTable() {
/* 452 */       if (this.lookupTable == null) {
/* 453 */         createLookupTable();
/*     */       }
/* 455 */       return this.lookupTable;
/*     */     }
/*     */     
/*     */     private void createLookupTable() {
/* 459 */       this.lookupTable = new String[this.LOOKUP_TABLE_SIZE];
/* 460 */       for (int i = 0; i < this.LOOKUP_TABLE_SIZE; i++) {
/* 461 */         this.lookupTable[i] = super.name(i);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static class ArrayEntityMap implements Entities.EntityMap {
/* 467 */     protected int growBy = 100;
/* 468 */     protected int size = 0;
/*     */     protected String[] names;
/*     */     protected int[] values;
/*     */     
/*     */     public ArrayEntityMap() {
/* 473 */       this.names = new String[this.growBy];
/* 474 */       this.values = new int[this.growBy];
/*     */     }
/*     */     
/*     */     public ArrayEntityMap(int growBy) {
/* 478 */       this.growBy = growBy;
/* 479 */       this.names = new String[growBy];
/* 480 */       this.values = new int[growBy];
/*     */     }
/*     */     
/*     */     public void add(String name, int value) {
/* 484 */       ensureCapacity(this.size + 1);
/* 485 */       this.names[this.size] = name;
/* 486 */       this.values[this.size] = value;
/* 487 */       this.size += 1;
/*     */     }
/*     */     
/*     */     protected void ensureCapacity(int capacity) {
/* 491 */       if (capacity > this.names.length) {
/* 492 */         int newSize = Math.max(capacity, this.size + this.growBy);
/* 493 */         String[] newNames = new String[newSize];
/* 494 */         System.arraycopy(this.names, 0, newNames, 0, this.size);
/* 495 */         this.names = newNames;
/* 496 */         int[] newValues = new int[newSize];
/* 497 */         System.arraycopy(this.values, 0, newValues, 0, this.size);
/* 498 */         this.values = newValues;
/*     */       }
/*     */     }
/*     */     
/*     */     public String name(int value) {
/* 503 */       for (int i = 0; i < this.size; i++) {
/* 504 */         if (this.values[i] == value) {
/* 505 */           return this.names[i];
/*     */         }
/*     */       }
/* 508 */       return null;
/*     */     }
/*     */     
/*     */     public int value(String name) {
/* 512 */       for (int i = 0; i < this.size; i++) {
/* 513 */         if (this.names[i].equals(name)) {
/* 514 */           return this.values[i];
/*     */         }
/*     */       }
/* 517 */       return -1;
/*     */     }
/*     */   }
/*     */   
/*     */   static class BinaryEntityMap extends Entities.ArrayEntityMap
/*     */   {
/*     */     public BinaryEntityMap() {}
/*     */     
/*     */     public BinaryEntityMap(int growBy)
/*     */     {
/* 527 */       super();
/*     */     }
/*     */     
/*     */     private int binarySearch(int key)
/*     */     {
/* 532 */       int low = 0;
/* 533 */       int high = this.size - 1;
/*     */       
/* 535 */       while (low <= high) {
/* 536 */         int mid = low + high >> 1;
/* 537 */         int midVal = this.values[mid];
/*     */         
/* 539 */         if (midVal < key) {
/* 540 */           low = mid + 1;
/* 541 */         } else if (midVal > key) {
/* 542 */           high = mid - 1;
/*     */         } else {
/* 544 */           return mid;
/*     */         }
/*     */       }
/* 547 */       return -(low + 1);
/*     */     }
/*     */     
/*     */     public void add(String name, int value) {
/* 551 */       ensureCapacity(this.size + 1);
/* 552 */       int insertAt = binarySearch(value);
/* 553 */       if (insertAt > 0) {
/* 554 */         return;
/*     */       }
/* 556 */       insertAt = -(insertAt + 1);
/* 557 */       System.arraycopy(this.values, insertAt, this.values, insertAt + 1, this.size - insertAt);
/* 558 */       this.values[insertAt] = value;
/* 559 */       System.arraycopy(this.names, insertAt, this.names, insertAt + 1, this.size - insertAt);
/* 560 */       this.names[insertAt] = name;
/* 561 */       this.size += 1;
/*     */     }
/*     */     
/*     */     public String name(int value) {
/* 565 */       int index = binarySearch(value);
/* 566 */       if (index < 0) {
/* 567 */         return null;
/*     */       }
/* 569 */       return this.names[index];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 574 */   EntityMap map = new LookupEntityMap();
/*     */   
/*     */   public void addEntities(String[][] entityArray) {
/* 577 */     for (int i = 0; i < entityArray.length; i++) {
/* 578 */       addEntity(entityArray[i][0], Integer.parseInt(entityArray[i][1]));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addEntity(String name, int value) {
/* 583 */     this.map.add(name, value);
/*     */   }
/*     */   
/*     */   public String entityName(int value) {
/* 587 */     return this.map.name(value);
/*     */   }
/*     */   
/*     */   public int entityValue(String name)
/*     */   {
/* 592 */     return this.map.value(name);
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
/*     */   public String escape(String str)
/*     */   {
/* 606 */     StringBuffer buf = new StringBuffer(str.length() * 2);
/*     */     
/* 608 */     for (int i = 0; i < str.length(); i++) {
/* 609 */       char ch = str.charAt(i);
/* 610 */       String entityName = entityName(ch);
/* 611 */       if (entityName == null) {
/* 612 */         if (ch > '') {
/* 613 */           int intValue = ch;
/* 614 */           buf.append("&#");
/* 615 */           buf.append(intValue);
/* 616 */           buf.append(';');
/*     */         } else {
/* 618 */           buf.append(ch);
/*     */         }
/*     */       } else {
/* 621 */         buf.append('&');
/* 622 */         buf.append(entityName);
/* 623 */         buf.append(';');
/*     */       }
/*     */     }
/* 626 */     return buf.toString();
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
/*     */   public String unescape(String str)
/*     */   {
/* 639 */     StringBuffer buf = new StringBuffer(str.length());
/*     */     
/* 641 */     for (int i = 0; i < str.length(); i++) {
/* 642 */       char ch = str.charAt(i);
/* 643 */       if (ch == '&') {
/* 644 */         int semi = str.indexOf(';', i + 1);
/* 645 */         if (semi == -1) {
/* 646 */           buf.append(ch);
/*     */         }
/*     */         else {
/* 649 */           String entityName = str.substring(i + 1, semi);
/*     */           int entityValue;
/* 651 */           if (entityName.charAt(0) == '#') {
/*     */             try {
/* 653 */               char charAt1 = entityName.charAt(1);
/* 654 */               int entityValue; if ((charAt1 == 'x') || (charAt1 == 'X')) {
/* 655 */                 entityValue = Integer.valueOf(entityName.substring(2), 16).intValue();
/*     */               } else {
/* 657 */                 entityValue = Integer.parseInt(entityName.substring(1));
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 661 */               int entityValue = -1;
/*     */             }
/*     */           } else {
/* 664 */             entityValue = entityValue(entityName);
/*     */           }
/* 666 */           if (entityValue == -1) {
/* 667 */             buf.append('&');
/* 668 */             buf.append(entityName);
/* 669 */             buf.append(';');
/*     */           } else {
/* 671 */             buf.append((char)entityValue);
/*     */           }
/* 673 */           i = semi;
/*     */         }
/* 675 */       } else { buf.append(ch);
/*     */       }
/*     */     }
/* 678 */     return buf.toString();
/*     */   }
/*     */   
/*     */   static abstract interface EntityMap
/*     */   {
/*     */     public abstract void add(String paramString, int paramInt);
/*     */     
/*     */     public abstract String name(int paramInt);
/*     */     
/*     */     public abstract int value(String paramString);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/apache/commons/lang/Entities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */