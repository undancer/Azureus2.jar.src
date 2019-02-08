/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MapUtils
/*     */ {
/*     */   public static int getMapInt(Map map, String key, int def)
/*     */   {
/*  34 */     if (map == null) {
/*  35 */       return def;
/*     */     }
/*     */     try {
/*  38 */       Number n = (Number)map.get(key);
/*     */       
/*  40 */       if (n == null)
/*     */       {
/*  42 */         return def;
/*     */       }
/*     */       
/*  45 */       return n.intValue();
/*     */     } catch (Throwable e) {
/*  47 */       Debug.out(e); }
/*  48 */     return def;
/*     */   }
/*     */   
/*     */   public static long getMapLong(Map map, String key, long def)
/*     */   {
/*  53 */     if (map == null) {
/*  54 */       return def;
/*     */     }
/*     */     try {
/*  57 */       Number n = (Number)map.get(key);
/*     */       
/*  59 */       if (n == null)
/*     */       {
/*  61 */         return def;
/*     */       }
/*     */       
/*  64 */       return n.longValue();
/*     */     } catch (Throwable e) {
/*  66 */       Debug.out(e); }
/*  67 */     return def;
/*     */   }
/*     */   
/*     */   public static String getMapString(Map map, String key, String def)
/*     */   {
/*  72 */     if (map == null) {
/*  73 */       return def;
/*     */     }
/*     */     try {
/*  76 */       Object o = map.get(key);
/*  77 */       if ((o == null) && (!map.containsKey(key))) {
/*  78 */         return def;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  84 */       if ((o instanceof String)) {
/*  85 */         return (String)o;
/*     */       }
/*  87 */       if ((o instanceof byte[])) {
/*  88 */         return new String((byte[])o, "utf-8");
/*     */       }
/*  90 */       return def;
/*     */     } catch (Throwable t) {
/*  92 */       Debug.out(t); }
/*  93 */     return def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] getMapStringArray(Map map, String key, String[] def)
/*     */   {
/* 103 */     Object o = map.get(key);
/* 104 */     if (!(o instanceof List)) {
/* 105 */       return def;
/*     */     }
/* 107 */     List list = (List)o;
/* 108 */     String[] result = new String[list.size()];
/* 109 */     for (int i = 0; i < result.length; i++) {
/* 110 */       result[i] = getString(list.get(i));
/*     */     }
/* 112 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getString(Object obj)
/*     */   {
/* 119 */     if ((obj instanceof String))
/* 120 */       return (String)obj;
/* 121 */     if ((obj instanceof byte[])) {
/*     */       try
/*     */       {
/* 124 */         return new String((byte[])obj, "UTF-8");
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 129 */     return null;
/*     */   }
/*     */   
/*     */   public static void setMapString(Map map, String key, String val) {
/* 133 */     if (map == null) {
/* 134 */       Debug.out("Map is null!");
/* 135 */       return;
/*     */     }
/*     */     try {
/* 138 */       if (val == null) {
/* 139 */         map.remove(key);
/*     */       } else {
/* 141 */         map.put(key, val.getBytes("utf-8"));
/*     */       }
/*     */     } catch (Throwable e) {
/* 144 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static byte[] getMapByteArray(Map map, String key, byte[] def) {
/* 149 */     if (map == null) {
/* 150 */       return def;
/*     */     }
/*     */     try {
/* 153 */       Object o = map.get(key);
/* 154 */       if ((o instanceof byte[])) {
/* 155 */         return (byte[])o;
/*     */       }
/*     */       
/* 158 */       String b64Key = key + ".B64";
/* 159 */       if (map.containsKey(b64Key)) {
/* 160 */         o = map.get(b64Key);
/* 161 */         if ((o instanceof String)) {
/* 162 */           return Base64.decode((String)o);
/*     */         }
/*     */       }
/*     */       
/* 166 */       String b32Key = key + ".B32";
/* 167 */       if (map.containsKey(b32Key)) {
/* 168 */         o = map.get(b32Key);
/* 169 */         if ((o instanceof String)) {
/* 170 */           return Base32.decode((String)o);
/*     */         }
/*     */       }
/*     */       
/* 174 */       return def;
/*     */     } catch (Throwable t) {
/* 176 */       Debug.out(t); }
/* 177 */     return def;
/*     */   }
/*     */   
/*     */   public static Object getMapObject(Map map, String key, Object def, Class cla)
/*     */   {
/* 182 */     if (map == null) {
/* 183 */       return def;
/*     */     }
/*     */     try {
/* 186 */       Object o = map.get(key);
/* 187 */       if (cla.isInstance(o)) {
/* 188 */         return o;
/*     */       }
/* 190 */       return def;
/*     */     }
/*     */     catch (Throwable t) {
/* 193 */       Debug.out(t); }
/* 194 */     return def;
/*     */   }
/*     */   
/*     */   public static boolean getMapBoolean(Map map, String key, boolean def)
/*     */   {
/* 199 */     if (map == null) {
/* 200 */       return def;
/*     */     }
/*     */     try {
/* 203 */       Object o = map.get(key);
/* 204 */       if ((o instanceof Boolean)) {
/* 205 */         return ((Boolean)o).booleanValue();
/*     */       }
/*     */       
/* 208 */       if ((o instanceof Long)) {
/* 209 */         return ((Long)o).longValue() == 1L;
/*     */       }
/*     */       
/* 212 */       return def;
/*     */     } catch (Throwable e) {
/* 214 */       Debug.out(e); }
/* 215 */     return def;
/*     */   }
/*     */   
/*     */   public static List getMapList(Map map, String key, List def)
/*     */   {
/* 220 */     if (map == null) {
/* 221 */       return def;
/*     */     }
/*     */     try {
/* 224 */       List list = (List)map.get(key);
/* 225 */       if ((list == null) && (!map.containsKey(key))) {
/* 226 */         return def;
/*     */       }
/* 228 */       return list;
/*     */     } catch (Throwable t) {
/* 230 */       Debug.out(t); }
/* 231 */     return def;
/*     */   }
/*     */   
/*     */   public static Map getMapMap(Map map, String key, Map def)
/*     */   {
/* 236 */     if (map == null) {
/* 237 */       return def;
/*     */     }
/*     */     try {
/* 240 */       Map valMap = (Map)map.get(key);
/* 241 */       if ((valMap == null) && (!map.containsKey(key))) {
/* 242 */         return def;
/*     */       }
/* 244 */       return valMap;
/*     */     } catch (Throwable t) {
/* 246 */       Debug.out(t); }
/* 247 */     return def;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/MapUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */