/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.dnd.ByteArrayTransfer;
/*     */ import org.eclipse.swt.dnd.TransferData;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLTransfer
/*     */   extends ByteArrayTransfer
/*     */ {
/*     */   private boolean bCheckingString;
/*     */   
/*  90 */   public URLTransfer() { this.bCheckingString = false; }
/*     */   
/*  92 */   private static boolean DEBUG = false;
/*     */   
/*  94 */   private static URLTransfer _instance = new URLTransfer();
/*     */   
/*     */ 
/*  97 */   private static final String[] supportedTypes = { "CF_UNICODETEXT", "CF_TEXT", "OEM_TEXT" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */   private static final int[] supportedTypeIds = { 13, 1, 17 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static URLTransfer getInstance()
/*     */   {
/* 110 */     return _instance;
/*     */   }
/*     */   
/*     */   public void javaToNative(Object object, TransferData transferData) {
/* 114 */     if (DEBUG) {
/* 115 */       System.out.println("javaToNative called");
/*     */     }
/* 117 */     if ((object == null) || (!(object instanceof URLType[]))) {
/* 118 */       return;
/*     */     }
/* 120 */     if (isSupportedType(transferData)) {
/* 121 */       URLType[] myTypes = (URLType[])object;
/*     */       try
/*     */       {
/* 124 */         ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 125 */         DataOutputStream writeOut = new DataOutputStream(out);
/* 126 */         int i = 0; for (int length = myTypes.length; i < length; i++) {
/* 127 */           writeOut.writeBytes(myTypes[i].linkURL);
/* 128 */           writeOut.writeBytes("\n");
/* 129 */           writeOut.writeBytes(myTypes[i].linkText);
/*     */         }
/* 131 */         byte[] buffer = out.toByteArray();
/* 132 */         writeOut.close();
/*     */         
/* 134 */         super.javaToNative(buffer, transferData);
/*     */       }
/*     */       catch (IOException e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public Object nativeToJava(TransferData transferData)
/*     */   {
/* 142 */     if (DEBUG) System.out.println("nativeToJava called");
/*     */     try {
/* 144 */       if (isSupportedType(transferData)) {
/* 145 */         byte[] buffer = (byte[])super.nativeToJava(transferData);
/* 146 */         return bytebufferToJava(buffer);
/*     */       }
/*     */     } catch (Exception e) {
/* 149 */       Debug.out(e);
/*     */     }
/*     */     
/* 152 */     return null;
/*     */   }
/*     */   
/*     */   public URLType bytebufferToJava(byte[] buffer)
/*     */   {
/* 157 */     if (buffer == null) {
/* 158 */       if (DEBUG) System.out.println("buffer null");
/* 159 */       return null;
/*     */     }
/*     */     
/* 162 */     URLType myData = null;
/*     */     try { String data;
/*     */       String data;
/* 165 */       if (buffer.length > 1) {
/* 166 */         if (DEBUG) {
/* 167 */           for (int i = 0; i < buffer.length; i++) {
/* 168 */             if (buffer[i] >= 32) {
/* 169 */               System.out.print((char)buffer[i]);
/*     */             } else
/* 171 */               System.out.print("#");
/*     */           }
/* 173 */           System.out.println();
/*     */         }
/* 175 */         boolean bFirst0 = buffer[0] == 0;
/* 176 */         boolean bSecond0 = buffer[1] == 0;
/* 177 */         String data; if ((bFirst0) && (bSecond0))
/*     */         {
/*     */ 
/* 180 */           data = new String(buffer); } else { String data;
/* 181 */           if (bFirst0) {
/* 182 */             data = new String(buffer, "UTF-16BE"); } else { String data;
/* 183 */             if (bSecond0) {
/* 184 */               data = new String(buffer, "UTF-16LE"); } else { String data;
/* 185 */               if ((buffer[0] == -17) && (buffer[1] == -69) && (buffer.length > 3) && (buffer[2] == -65))
/*     */               {
/* 187 */                 data = new String(buffer, 3, buffer.length - 3, "UTF-8"); } else { String data;
/* 188 */                 if ((buffer[0] == -1) || (buffer[0] == -2)) {
/* 189 */                   data = new String(buffer, "UTF-16");
/*     */                 } else
/* 191 */                   data = new String(buffer);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 196 */       } else { byte[] text = new byte[buffer.length];
/* 197 */         int j = 0;
/* 198 */         for (int i = 0; i < buffer.length; i++) {
/* 199 */           if (buffer[i] != 0) {
/* 200 */             text[(j++)] = buffer[i];
/*     */           }
/*     */         }
/* 203 */         data = new String(text, 0, j);
/*     */       }
/*     */       
/* 206 */       int iPos = data.indexOf("\nURL=");
/* 207 */       if (iPos > 0) {
/* 208 */         int iEndPos = data.indexOf("\r", iPos);
/* 209 */         if (iEndPos < 0) {
/* 210 */           iEndPos = data.length();
/*     */         }
/* 212 */         myData = new URLType();
/* 213 */         myData.linkURL = data.substring(iPos + 5, iEndPos);
/* 214 */         myData.linkText = "";
/*     */       } else {
/* 216 */         String[] split = data.split("[\r\n]+", 2);
/*     */         
/* 218 */         myData = new URLType();
/* 219 */         myData.linkURL = (split.length > 0 ? split[0] : "");
/* 220 */         myData.linkText = (split.length > 1 ? split[1] : "");
/*     */       }
/*     */     } catch (Exception ex) {
/* 223 */       ex.printStackTrace();
/*     */     }
/*     */     
/* 226 */     return myData;
/*     */   }
/*     */   
/*     */   protected String[] getTypeNames() {
/* 230 */     return supportedTypes;
/*     */   }
/*     */   
/*     */   protected int[] getTypeIds() {
/* 234 */     return supportedTypeIds;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSupportedType(TransferData transferData)
/*     */   {
/* 243 */     if (this.bCheckingString) {
/* 244 */       return true;
/*     */     }
/* 246 */     if (transferData == null) {
/* 247 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 252 */     URLType url = null;
/*     */     
/* 254 */     if (DEBUG) { System.out.println("Checking if type #" + transferData.type + " is URL");
/*     */     }
/* 256 */     this.bCheckingString = true;
/*     */     try {
/* 258 */       byte[] buffer = (byte[])super.nativeToJava(transferData);
/* 259 */       url = bytebufferToJava(buffer);
/*     */     } catch (Exception e) {
/* 261 */       Debug.out(e);
/*     */     } finally {
/* 263 */       this.bCheckingString = false;
/*     */     }
/*     */     
/* 266 */     if (url == null) {
/* 267 */       if (DEBUG) System.out.println("no, Null URL for type #" + transferData.type);
/* 268 */       return false;
/*     */     }
/*     */     
/* 271 */     if (UrlUtils.isURL(url.linkURL, false)) {
/* 272 */       if (DEBUG) System.out.println("Yes, " + url.linkURL + " of type #" + transferData.type);
/* 273 */       return true;
/*     */     }
/*     */     
/* 276 */     if (DEBUG) System.out.println("no, " + url.linkURL + " not URL for type #" + transferData.type);
/* 277 */     return false;
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
/*     */   public static TransferData pickBestType(TransferData[] dataTypes, TransferData def)
/*     */   {
/* 297 */     for (int i = 0; i < supportedTypeIds.length; i++) {
/* 298 */       int supportedTypeID = supportedTypeIds[i];
/* 299 */       for (int j = 0; j < dataTypes.length; j++) {
/*     */         try {
/* 301 */           TransferData data = dataTypes[j];
/* 302 */           if (supportedTypeID == data.type)
/* 303 */             return data;
/*     */         } catch (Throwable t) {
/* 305 */           Debug.out("Picking Best Type", t);
/*     */         }
/*     */       }
/*     */     }
/* 309 */     return def;
/*     */   }
/*     */   
/*     */   public static class URLType
/*     */   {
/*     */     public String linkURL;
/*     */     public String linkText;
/*     */     
/*     */     public String toString() {
/* 318 */       return this.linkURL + "\n" + this.linkText;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 329 */     Map map = new LinkedHashMap();
/* 330 */     map.put("UTF-8", new byte[] { -17, -69, -65, 72, 105 });
/*     */     
/* 332 */     map.put("UTF-32 BE BOM", new byte[] { 0, 0, -2, -1, 72, 0, 0, 0, 105, 0, 0, 0 });
/*     */     
/* 334 */     map.put("UTF-16 LE BOM", new byte[] { -1, -2, 72, 0, 105, 0 });
/*     */     
/* 336 */     map.put("UTF-16 BE BOM", new byte[] { -2, -1, 0, 72, 0, 105 });
/*     */     
/* 338 */     map.put("UTF-16 LE", new byte[] { 72, 0, 105, 0 });
/* 339 */     map.put("UTF-16 BE", new byte[] { 0, 72, 0, 105 });
/*     */     
/* 341 */     for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
/* 342 */       String element = (String)iterator.next();
/* 343 */       System.out.println(element + ":");
/* 344 */       byte[] buffer = (byte[])map.get(element);
/*     */       
/* 346 */       boolean bFirst0 = buffer[0] == 0;
/* 347 */       boolean bSecond0 = buffer[1] == 0;
/* 348 */       String data = "";
/*     */       try {
/* 350 */         if ((bFirst0) && (bSecond0))
/*     */         {
/*     */ 
/* 353 */           data = new String(buffer);
/* 354 */         } else if (bFirst0) {
/* 355 */           data = new String(buffer, "UTF-16BE");
/* 356 */         } else if (bSecond0) {
/* 357 */           data = new String(buffer, "UTF-16LE");
/* 358 */         } else if ((buffer[0] == -17) && (buffer[1] == -69) && (buffer.length > 3) && (buffer[2] == -65))
/*     */         {
/* 360 */           data = new String(buffer, 3, buffer.length - 3, "UTF-8");
/* 361 */         } else if ((buffer[0] == -1) || (buffer[0] == -2)) {
/* 362 */           data = new String(buffer, "UTF-16");
/*     */         } else {
/* 364 */           data = new String(buffer);
/*     */         }
/*     */       }
/*     */       catch (UnsupportedEncodingException e) {
/* 368 */         e.printStackTrace();
/*     */       }
/*     */       
/* 371 */       System.out.println(data);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/URLTransfer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */