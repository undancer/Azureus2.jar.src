/*     */ package com.aelitis.azureus.core.dht.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHTLogger;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTLog
/*     */ {
/*     */   public static final boolean GLOBAL_BLOOM_TRACE = false;
/*     */   public static final boolean LOCAL_BLOOM_TRACE = false;
/*     */   public static final boolean CONTACT_VERIFY_TRACE = false;
/*     */   public static final boolean TRACE_VERSIONS = false;
/*  62 */   public static boolean logging_on = false;
/*     */   
/*     */ 
/*     */   private static DHTLogger logger;
/*     */   
/*     */ 
/*     */   protected static void setLogging(boolean on)
/*     */   {
/*  70 */     logging_on = on;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isOn()
/*     */   {
/*  76 */     return logging_on;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void log(String str)
/*     */   {
/*  83 */     if (logging_on)
/*     */     {
/*  85 */       if (logger != null)
/*     */       {
/*  87 */         logger.log(str);
/*     */       }
/*     */       else
/*     */       {
/*  91 */         System.out.println(str);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setLogger(DHTLogger l)
/*     */   {
/* 100 */     logger = l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(byte[] b)
/*     */   {
/* 108 */     if (logging_on)
/*     */     {
/* 110 */       return getString2(b);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 115 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString2(byte[] b)
/*     */   {
/* 123 */     String res = ByteFormatter.nicePrint(b);
/*     */     
/* 125 */     if (res.length() > 8)
/*     */     {
/* 127 */       res = res.substring(0, 8) + "...";
/*     */     }
/*     */     
/* 130 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getFullString(byte[] b)
/*     */   {
/* 137 */     return ByteFormatter.nicePrint(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getString(HashWrapper w)
/*     */   {
/* 144 */     if (logging_on)
/*     */     {
/* 146 */       return getString(w.getHash());
/*     */     }
/*     */     
/* 149 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(DHTTransportContact[] contacts)
/*     */   {
/* 157 */     if (logging_on)
/*     */     {
/* 159 */       StringBuilder sb = new StringBuilder(128);
/* 160 */       sb.append("{");
/*     */       
/* 162 */       for (int i = 0; i < contacts.length; i++)
/*     */       {
/* 164 */         if (i > 0) {
/* 165 */           sb.append(",");
/*     */         }
/* 167 */         sb.append(getString(contacts[i].getID()));
/*     */       }
/*     */       
/* 170 */       sb.append("}");
/*     */       
/* 172 */       return sb.toString();
/*     */     }
/* 174 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(DHTTransportContact contact)
/*     */   {
/* 182 */     if (logging_on) {
/* 183 */       return contact.getString();
/*     */     }
/* 185 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(List l)
/*     */   {
/* 193 */     if (logging_on) {
/* 194 */       StringBuilder sb = new StringBuilder(128);
/* 195 */       sb.append("{");
/*     */       
/* 197 */       for (int i = 0; i < l.size(); i++)
/*     */       {
/* 199 */         if (i > 0) {
/* 200 */           sb.append(",");
/*     */         }
/* 202 */         sb.append(getString((DHTTransportContact)l.get(i)));
/*     */       }
/*     */       
/* 205 */       sb.append("}");
/*     */       
/* 207 */       return sb.toString();
/*     */     }
/* 209 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(Set s)
/*     */   {
/* 217 */     if (logging_on) {
/* 218 */       StringBuilder sb = new StringBuilder(128);
/* 219 */       sb.append("{");
/*     */       
/* 221 */       Iterator it = s.iterator();
/*     */       
/* 223 */       while (it.hasNext())
/*     */       {
/* 225 */         if (sb.length() > 1) {
/* 226 */           sb.append(",");
/*     */         }
/* 228 */         sb.append(getString((DHTTransportContact)it.next()));
/*     */       }
/*     */       
/* 231 */       sb.append("}");
/*     */       
/* 233 */       return sb.toString();
/*     */     }
/* 235 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(Map s)
/*     */   {
/* 243 */     if (logging_on) {
/* 244 */       StringBuilder sb = new StringBuilder(128);
/* 245 */       sb.append("{");
/*     */       
/* 247 */       Iterator it = s.keySet().iterator();
/*     */       
/* 249 */       while (it.hasNext())
/*     */       {
/* 251 */         if (sb.length() > 1) {
/* 252 */           sb.append(",");
/*     */         }
/* 254 */         sb.append(getString((HashWrapper)it.next()));
/*     */       }
/*     */       
/* 257 */       sb.append("}");
/*     */       
/* 259 */       return sb.toString();
/*     */     }
/* 261 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(ByteArrayHashMap<?> s)
/*     */   {
/* 269 */     if (logging_on) {
/* 270 */       StringBuilder sb = new StringBuilder(128);
/* 271 */       sb.append("{");
/*     */       
/* 273 */       List<byte[]> keys = s.keys();
/*     */       
/* 275 */       for (byte[] key : keys)
/*     */       {
/* 277 */         if (sb.length() > 1) {
/* 278 */           sb.append(",");
/*     */         }
/* 280 */         sb.append(getString(key));
/*     */       }
/*     */       
/* 283 */       sb.append("}");
/*     */       
/* 285 */       return sb.toString();
/*     */     }
/* 287 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getString(DHTTransportValue[] values)
/*     */   {
/* 295 */     if (logging_on)
/*     */     {
/* 297 */       if (values == null)
/*     */       {
/* 299 */         return "<null>";
/*     */       }
/*     */       
/* 302 */       StringBuilder sb = new StringBuilder(256);
/*     */       
/* 304 */       for (int i = 0; i < values.length; i++)
/*     */       {
/* 306 */         if (i > 0) {
/* 307 */           sb.append(",");
/*     */         }
/* 309 */         getString(sb, values[i]);
/*     */       }
/* 311 */       return sb.toString();
/*     */     }
/* 313 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void getString(StringBuilder sb, DHTTransportValue value)
/*     */   {
/* 322 */     if (logging_on)
/*     */     {
/* 324 */       if (value == null)
/*     */       {
/* 326 */         sb.append("<null>");
/*     */       }
/*     */       else
/*     */       {
/* 330 */         sb.append(getString(value.getValue()));
/* 331 */         sb.append(" <");
/* 332 */         sb.append(value.isLocal() ? "loc" : "rem");
/* 333 */         sb.append(",flag=");
/* 334 */         sb.append(Integer.toHexString(value.getFlags()));
/* 335 */         sb.append(",life=");
/* 336 */         sb.append(value.getLifeTimeHours());
/* 337 */         sb.append(",rep=");
/* 338 */         sb.append(Integer.toHexString(value.getReplicationControl()));
/* 339 */         sb.append(",orig=");
/* 340 */         sb.append(value.getOriginator().getExternalAddress());
/* 341 */         sb.append(">");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getString(DHTTransportValue value)
/*     */   {
/* 350 */     if (logging_on)
/*     */     {
/* 352 */       if (value == null)
/*     */       {
/* 354 */         return "<null>";
/*     */       }
/*     */       
/* 357 */       return getString(value.getValue()) + " <" + (value.isLocal() ? "loc" : "rem") + ",flag=" + Integer.toHexString(value.getFlags()) + ",life=" + value.getLifeTimeHours() + ",rep=" + Integer.toHexString(value.getReplicationControl()) + ",orig=" + value.getOriginator().getExternalAddress() + ">";
/*     */     }
/* 359 */     return "";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/impl/DHTLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */