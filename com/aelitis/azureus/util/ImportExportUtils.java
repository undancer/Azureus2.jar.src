/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.json.simple.JSONArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ImportExportUtils
/*     */ {
/*     */   public static final void exportString(Map map, String key, String value)
/*     */   {
/*  42 */     if (value != null) {
/*     */       try
/*     */       {
/*  45 */         map.put(key, value.getBytes("UTF-8"));
/*     */       }
/*     */       catch (UnsupportedEncodingException e)
/*     */       {
/*  49 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportJSONString(Map map, String key, String value)
/*     */   {
/*  60 */     if (value != null)
/*     */     {
/*  62 */       map.put(key, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String importString(Map map, String key, String def)
/*     */   {
/*  72 */     String res = importString(map, key);
/*     */     
/*  74 */     if (res == null)
/*     */     {
/*  76 */       res = def;
/*     */     }
/*     */     
/*  79 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String importString(Map map, String key)
/*     */   {
/*  88 */     if (map == null)
/*     */     {
/*  90 */       return null;
/*     */     }
/*     */     
/*  93 */     Object obj = map.get(key);
/*     */     
/*  95 */     if ((obj instanceof String))
/*     */     {
/*  97 */       return (String)obj;
/*     */     }
/*  99 */     if ((obj instanceof byte[])) {
/*     */       try
/*     */       {
/* 102 */         return new String((byte[])obj, "UTF-8");
/*     */       }
/*     */       catch (UnsupportedEncodingException e)
/*     */       {
/* 106 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 110 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final long importLong(Map map, String key)
/*     */   {
/* 118 */     return importLong(map, key, 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final long importLong(Map map, String key, long def)
/*     */   {
/* 127 */     if (map == null)
/*     */     {
/* 129 */       return def;
/*     */     }
/*     */     
/* 132 */     Object obj = map.get(key);
/*     */     
/* 134 */     if ((obj instanceof Long))
/*     */     {
/* 136 */       return ((Long)obj).longValue();
/*     */     }
/* 138 */     if ((obj instanceof String))
/*     */     {
/* 140 */       return Long.parseLong((String)obj);
/*     */     }
/*     */     
/* 143 */     return def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportLong(Map map, String key, long value)
/*     */   {
/* 152 */     map.put(key, Long.valueOf(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportInt(Map map, String key, int value)
/*     */   {
/* 161 */     map.put(key, new Long(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int importInt(Map map, String key)
/*     */   {
/* 169 */     return (int)importLong(map, key, 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int importInt(Map map, String key, int def)
/*     */   {
/* 179 */     return (int)importLong(map, key, def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportFloat(Map map, String key, float value)
/*     */   {
/* 188 */     exportString(map, key, String.valueOf(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final float importFloat(Map map, String key, float def)
/*     */   {
/* 197 */     String str = importString(map, key);
/*     */     
/* 199 */     if (str == null)
/*     */     {
/* 201 */       return def;
/*     */     }
/*     */     
/* 204 */     return Float.parseFloat(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportBoolean(Map map, String key, boolean value)
/*     */   {
/* 213 */     map.put(key, new Long(value ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean importBoolean(Map map, String key)
/*     */   {
/* 221 */     return importBoolean(map, key, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean importBoolean(Map map, String key, boolean def)
/*     */   {
/* 230 */     if (map == null)
/*     */     {
/* 232 */       return def;
/*     */     }
/*     */     
/* 235 */     Object obj = map.get(key);
/*     */     
/* 237 */     if ((obj instanceof Long))
/*     */     {
/* 239 */       return ((Long)obj).longValue() == 1L;
/*     */     }
/* 241 */     if ((obj instanceof Boolean))
/*     */     {
/* 243 */       return ((Boolean)obj).booleanValue();
/*     */     }
/*     */     
/* 246 */     return def;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportJSONBoolean(Map map, String key, boolean value)
/*     */   {
/* 255 */     map.put(key, Boolean.valueOf(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String importURL(Map map, String key)
/*     */   {
/* 263 */     String url = importString(map, key);
/*     */     
/* 265 */     if (url != null)
/*     */     {
/* 267 */       url = url.trim();
/*     */       
/* 269 */       if (url.length() == 0)
/*     */       {
/* 271 */         url = null;
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 276 */           url = URLDecoder.decode(url, "UTF-8");
/*     */         }
/*     */         catch (UnsupportedEncodingException e)
/*     */         {
/* 280 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 285 */     return url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportURL(Map map, String key, String value)
/*     */   {
/* 294 */     exportString(map, key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportJSONURL(Map map, String key, String value)
/*     */   {
/* 303 */     exportJSONString(map, key, UrlUtils.encode(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String[] importStringArray(Map map, String key)
/*     */   {
/* 311 */     List list = (List)map.get(key);
/*     */     
/* 313 */     if (list == null)
/*     */     {
/* 315 */       return new String[0];
/*     */     }
/*     */     
/* 318 */     String[] res = new String[list.size()];
/*     */     
/* 320 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 322 */       Object obj = list.get(i);
/*     */       
/* 324 */       if ((obj instanceof String))
/*     */       {
/* 326 */         res[i] = ((String)obj);
/*     */       }
/* 328 */       else if ((obj instanceof byte[])) {
/*     */         try
/*     */         {
/* 331 */           res[i] = new String((byte[])(byte[])obj, "UTF-8");
/*     */         }
/*     */         catch (UnsupportedEncodingException e)
/*     */         {
/* 335 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 340 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportStringArray(Map map, String key, String[] data)
/*     */   {
/* 349 */     List l = new ArrayList(data.length);
/*     */     
/* 351 */     map.put(key, l);
/*     */     
/* 353 */     for (int i = 0; i < data.length; i++) {
/*     */       try
/*     */       {
/* 356 */         l.add(data[i].getBytes("UTF-8"));
/*     */       }
/*     */       catch (UnsupportedEncodingException e)
/*     */       {
/* 360 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportJSONStringArray(Map map, String key, String[] data)
/*     */   {
/* 371 */     List l = new JSONArray(data.length);
/*     */     
/* 373 */     map.put(key, l);
/*     */     
/* 375 */     Collections.addAll(l, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void exportIntArray(Map map, String key, int[] values)
/*     */   {
/* 384 */     if (values == null)
/*     */     {
/* 386 */       return;
/*     */     }
/*     */     
/* 389 */     int num = values.length;
/*     */     
/* 391 */     byte[] bytes = new byte[num * 4];
/* 392 */     int pos = 0;
/*     */     
/* 394 */     for (int i = 0; i < num; i++)
/*     */     {
/* 396 */       int v = values[i];
/*     */       
/* 398 */       bytes[(pos++)] = ((byte)(v >>> 24));
/* 399 */       bytes[(pos++)] = ((byte)(v >>> 16));
/* 400 */       bytes[(pos++)] = ((byte)(v >>> 8));
/* 401 */       bytes[(pos++)] = ((byte)v);
/*     */     }
/*     */     
/* 404 */     map.put(key, bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int[] importIntArray(Map map, String key)
/*     */   {
/* 412 */     byte[] bytes = (byte[])map.get(key);
/*     */     
/* 414 */     if (bytes == null)
/*     */     {
/* 416 */       return null;
/*     */     }
/*     */     
/* 419 */     int[] values = new int[bytes.length / 4];
/*     */     
/* 421 */     int pos = 0;
/*     */     
/* 423 */     for (int i = 0; i < values.length; i++)
/*     */     {
/* 425 */       values[i] = (((bytes[(pos++)] & 0xFF) << 24) + ((bytes[(pos++)] & 0xFF) << 16) + ((bytes[(pos++)] & 0xFF) << 8) + (bytes[(pos++)] & 0xFF));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 432 */     return values;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/ImportExportUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */