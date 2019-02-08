/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetDecoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BDecoder
/*      */ {
/*      */   public static final int MAX_BYTE_ARRAY_SIZE = 104857600;
/*      */   private static final int MAX_MAP_KEY_SIZE = 65536;
/*      */   private static final boolean TRACE = false;
/*      */   private boolean recovery_mode;
/*      */   private boolean verify_map_order;
/*      */   private static final byte[] PORTABLE_ROOT;
/*      */   
/*      */   static
/*      */   {
/*   55 */     byte[] portable = null;
/*      */     try
/*      */     {
/*   58 */       String root = System.getProperty("azureus.portable.root", "");
/*      */       
/*   60 */       if (root.length() > 0)
/*      */       {
/*   62 */         portable = root.getBytes("UTF-8");
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*   66 */       e.printStackTrace();
/*      */     }
/*      */     
/*   69 */     PORTABLE_ROOT = portable;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, Object> decode(byte[] data)
/*      */     throws IOException
/*      */   {
/*   78 */     return new BDecoder().decodeByteArray(data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, Object> decode(byte[] data, int offset, int length)
/*      */     throws IOException
/*      */   {
/*   89 */     return new BDecoder().decodeByteArray(data, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, Object> decode(BufferedInputStream is)
/*      */     throws IOException
/*      */   {
/*   98 */     return new BDecoder().decodeStream(is);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> decodeByteArray(byte[] data)
/*      */     throws IOException
/*      */   {
/*  113 */     return decode(new BDecoderInputStreamArray(data, null), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> decodeByteArray(byte[] data, int offset, int length)
/*      */     throws IOException
/*      */   {
/*  124 */     return decode(new BDecoderInputStreamArray(data, offset, length, null), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> decodeByteArray(byte[] data, int offset, int length, boolean internKeys)
/*      */     throws IOException
/*      */   {
/*  136 */     return decode(new BDecoderInputStreamArray(data, offset, length, null), internKeys);
/*      */   }
/*      */   
/*      */   public Map<String, Object> decodeByteBuffer(ByteBuffer buffer, boolean internKeys) throws IOException
/*      */   {
/*  141 */     InputStream is = new BDecoderInputStreamArray(buffer);
/*  142 */     Map<String, Object> result = decode(is, internKeys);
/*  143 */     buffer.position(buffer.limit() - is.available());
/*  144 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> decodeStream(BufferedInputStream data)
/*      */     throws IOException
/*      */   {
/*  153 */     return decodeStream(data, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> decodeStream(BufferedInputStream data, boolean internKeys)
/*      */     throws IOException
/*      */   {
/*  163 */     Object res = decodeInputStream(data, "", 0, internKeys);
/*      */     
/*  165 */     if (res == null)
/*      */     {
/*  167 */       throw new BEncodingException("BDecoder: zero length file");
/*      */     }
/*  169 */     if (!(res instanceof Map))
/*      */     {
/*  171 */       throw new BEncodingException("BDecoder: top level isn't a Map");
/*      */     }
/*      */     
/*  174 */     return (Map)res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<String, Object> decode(InputStream data, boolean internKeys)
/*      */     throws IOException
/*      */   {
/*  183 */     Object res = decodeInputStream(data, "", 0, internKeys);
/*      */     
/*  185 */     if (res == null)
/*      */     {
/*  187 */       throw new BEncodingException("BDecoder: zero length file");
/*      */     }
/*  189 */     if (!(res instanceof Map))
/*      */     {
/*  191 */       throw new BEncodingException("BDecoder: top level isn't a Map");
/*      */     }
/*      */     
/*  194 */     return (Map)res;
/*      */   }
/*      */   
/*      */ 
/*  198 */   private ByteBuffer keyBytesBuffer = ByteBuffer.allocate(32);
/*  199 */   private CharBuffer keyCharsBuffer = CharBuffer.allocate(32);
/*  200 */   private final CharsetDecoder keyDecoder = Constants.BYTE_CHARSET.newDecoder();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object decodeInputStream(InputStream dbis, String context, int nesting, boolean internKeys)
/*      */     throws IOException
/*      */   {
/*  211 */     if ((nesting == 0) && (!dbis.markSupported()))
/*      */     {
/*  213 */       throw new IOException("InputStream must support the mark() method");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  218 */     dbis.mark(1);
/*      */     
/*      */ 
/*      */ 
/*  222 */     int tempByte = dbis.read();
/*      */     
/*      */ 
/*      */ 
/*  226 */     switch (tempByte)
/*      */     {
/*      */ 
/*      */     case 100: 
/*  230 */       LightHashMap tempMap = new LightHashMap();
/*      */       try
/*      */       {
/*  233 */         byte[] prev_key = null;
/*      */         
/*      */ 
/*      */ 
/*      */         for (;;)
/*      */         {
/*  239 */           dbis.mark(1);
/*      */           
/*  241 */           tempByte = dbis.read();
/*  242 */           if ((tempByte == 101) || (tempByte == -1)) {
/*      */             break;
/*      */           }
/*  245 */           dbis.reset();
/*      */           
/*      */ 
/*      */ 
/*  249 */           int keyLength = getPositiveNumberFromStream(dbis, ':');
/*      */           
/*  251 */           int skipBytes = 0;
/*      */           
/*  253 */           if (keyLength > 65536) {
/*  254 */             skipBytes = keyLength - 65536;
/*  255 */             keyLength = 65536;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  260 */           if (keyLength < this.keyBytesBuffer.capacity())
/*      */           {
/*  262 */             this.keyBytesBuffer.position(0).limit(keyLength);
/*  263 */             this.keyCharsBuffer.position(0).limit(keyLength);
/*      */           } else {
/*  265 */             this.keyBytesBuffer = ByteBuffer.allocate(keyLength);
/*  266 */             this.keyCharsBuffer = CharBuffer.allocate(keyLength);
/*      */           }
/*      */           
/*  269 */           getByteArrayFromStream(dbis, keyLength, this.keyBytesBuffer.array());
/*      */           
/*  271 */           if (skipBytes > 0) {
/*  272 */             dbis.skip(skipBytes);
/*      */           }
/*      */           
/*  275 */           if (this.verify_map_order)
/*      */           {
/*  277 */             byte[] current_key = new byte[keyLength];
/*      */             
/*  279 */             System.arraycopy(this.keyBytesBuffer.array(), 0, current_key, 0, keyLength);
/*      */             
/*  281 */             if (prev_key != null)
/*      */             {
/*  283 */               int len = Math.min(prev_key.length, keyLength);
/*      */               
/*  285 */               int state = 0;
/*      */               
/*  287 */               for (int i = 0; i < len; i++)
/*      */               {
/*  289 */                 int cb = current_key[i] & 0xFF;
/*  290 */                 int pb = prev_key[i] & 0xFF;
/*      */                 
/*  292 */                 if (cb > pb) {
/*  293 */                   state = 1;
/*  294 */                   break; }
/*  295 */                 if (cb < pb) {
/*  296 */                   state = 2;
/*  297 */                   break;
/*      */                 }
/*      */               }
/*      */               
/*  301 */               if ((state == 0) && 
/*  302 */                 (prev_key.length > keyLength))
/*      */               {
/*  304 */                 state = 2;
/*      */               }
/*      */               
/*      */ 
/*  308 */               if (state == 2)
/*      */               {
/*      */ 
/*      */ 
/*  312 */                 if (!(tempMap instanceof LightHashMapEx))
/*      */                 {
/*  314 */                   LightHashMapEx x = new LightHashMapEx(tempMap);
/*      */                   
/*  316 */                   x.setFlag((byte)1, true);
/*      */                   
/*  318 */                   tempMap = x;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  323 */             prev_key = current_key;
/*      */           }
/*      */           
/*  326 */           this.keyDecoder.reset();
/*  327 */           this.keyDecoder.decode(this.keyBytesBuffer, this.keyCharsBuffer, true);
/*  328 */           this.keyDecoder.flush(this.keyCharsBuffer);
/*  329 */           String key = new String(this.keyCharsBuffer.array(), 0, this.keyCharsBuffer.limit());
/*      */           
/*      */ 
/*  332 */           if (internKeys) {
/*  333 */             key = StringInterner.intern(key);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  338 */           Object value = decodeInputStream(dbis, key, nesting + 1, internKeys);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  354 */           if (value == null)
/*      */           {
/*  356 */             System.err.println("Invalid encoding - value not serialsied for '" + key + "' - ignoring: map so far=" + tempMap + ",loc=" + Debug.getCompressedStackTrace());
/*      */             
/*  358 */             break;
/*      */           }
/*      */           
/*  361 */           if (skipBytes > 0)
/*      */           {
/*  363 */             String msg = "dictionary key is too large - " + (keyLength + skipBytes) + ":, max=" + 65536 + ": skipping key starting with " + new String(key.substring(0, 128));
/*      */             
/*      */ 
/*  366 */             System.err.println(msg);
/*      */ 
/*      */ 
/*      */           }
/*  370 */           else if (tempMap.put(key, value) != null)
/*      */           {
/*  372 */             Debug.out("BDecoder: key '" + key + "' already exists!");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  383 */         dbis.mark(1);
/*  384 */         tempByte = dbis.read();
/*  385 */         dbis.reset();
/*  386 */         if ((nesting > 0) && (tempByte == -1))
/*      */         {
/*  388 */           throw new BEncodingException("BDecoder: invalid input data, 'e' missing from end of dictionary");
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  392 */         if (!this.recovery_mode)
/*      */         {
/*  394 */           if ((e instanceof IOException))
/*      */           {
/*  396 */             throw ((IOException)e);
/*      */           }
/*      */           
/*  399 */           throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */       
/*  403 */       tempMap.compactify(-0.9F);
/*      */       
/*      */ 
/*      */ 
/*  407 */       return tempMap;
/*      */     
/*      */ 
/*      */ 
/*      */     case 108: 
/*  412 */       ArrayList tempList = new ArrayList();
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  417 */         String context2 = context + "[]";
/*      */         
/*  419 */         Object tempElement = null;
/*  420 */         while ((tempElement = decodeInputStream(dbis, context2, nesting + 1, internKeys)) != null)
/*      */         {
/*  422 */           tempList.add(tempElement);
/*      */         }
/*      */         
/*  425 */         tempList.trimToSize();
/*  426 */         dbis.mark(1);
/*  427 */         tempByte = dbis.read();
/*  428 */         dbis.reset();
/*  429 */         if ((nesting > 0) && (tempByte == -1))
/*      */         {
/*  431 */           throw new BEncodingException("BDecoder: invalid input data, 'e' missing from end of list");
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  435 */         if (!this.recovery_mode)
/*      */         {
/*  437 */           if ((e instanceof IOException))
/*      */           {
/*  439 */             throw ((IOException)e);
/*      */           }
/*      */           
/*  442 */           throw new IOException(Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */       
/*  446 */       return tempList;
/*      */     
/*      */     case -1: 
/*      */     case 101: 
/*  450 */       return null;
/*      */     
/*      */     case 105: 
/*  453 */       return Long.valueOf(getNumberFromStream(dbis, 'e'));
/*      */     
/*      */ 
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/*  466 */       dbis.reset();
/*      */       
/*  468 */       return getByteArrayFromStream(dbis, context);
/*      */     }
/*      */     
/*      */     
/*  472 */     int rem_len = dbis.available();
/*      */     
/*  474 */     if (rem_len > 256)
/*      */     {
/*  476 */       rem_len = 256;
/*      */     }
/*      */     
/*  479 */     byte[] rem_data = new byte[rem_len];
/*      */     
/*  481 */     dbis.read(rem_data);
/*      */     
/*  483 */     throw new BEncodingException("BDecoder: unknown command '" + tempByte + ", remainder = " + new String(rem_data));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  519 */   private final char[] numberChars = new char[32];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int getPositiveNumberFromStream(InputStream dbis, char parseChar)
/*      */     throws IOException
/*      */   {
/*  533 */     int tempByte = dbis.read();
/*  534 */     if (tempByte < 0) {
/*  535 */       return -1;
/*      */     }
/*  537 */     if (tempByte != parseChar)
/*      */     {
/*  539 */       int value = tempByte - 48;
/*      */       
/*  541 */       tempByte = dbis.read();
/*      */       
/*  543 */       if (tempByte == parseChar) {
/*  544 */         return value;
/*      */       }
/*  546 */       if (tempByte < 0) {
/*  547 */         return -1;
/*      */       }
/*      */       
/*      */       do
/*      */       {
/*  552 */         value = (value << 3) + (value << 1) + (tempByte - 48);
/*      */         
/*      */ 
/*  555 */         tempByte = dbis.read();
/*  556 */         if (tempByte == parseChar) {
/*  557 */           return value;
/*      */         }
/*  559 */       } while (tempByte >= 0);
/*  560 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*  564 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long getNumberFromStream(InputStream dbis, char parseChar)
/*      */     throws IOException
/*      */   {
/*  577 */     int tempByte = dbis.read();
/*      */     
/*  579 */     int pos = 0;
/*      */     
/*  581 */     while ((tempByte != parseChar) && (tempByte >= 0)) {
/*  582 */       this.numberChars[(pos++)] = ((char)tempByte);
/*  583 */       if (pos == this.numberChars.length) {
/*  584 */         throw new NumberFormatException("Number too large: " + new String(this.numberChars, 0, pos) + "...");
/*      */       }
/*  586 */       tempByte = dbis.read();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  591 */     if (tempByte < 0)
/*      */     {
/*  593 */       return -1L;
/*      */     }
/*  595 */     if (pos == 0)
/*      */     {
/*      */ 
/*  598 */       return 0L;
/*      */     }
/*      */     try
/*      */     {
/*  602 */       return parseLong(this.numberChars, 0, pos);
/*      */     }
/*      */     catch (NumberFormatException e)
/*      */     {
/*  606 */       String temp = new String(this.numberChars, 0, pos);
/*      */       try
/*      */       {
/*  609 */         double d = Double.parseDouble(temp);
/*      */         
/*  611 */         long l = d;
/*      */         
/*  613 */         Debug.out("Invalid number '" + temp + "' - decoding as " + l + " and attempting recovery");
/*      */         
/*  615 */         return l;
/*      */ 
/*      */       }
/*      */       catch (Throwable f)
/*      */       {
/*  620 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static long parseLong(char[] chars, int start, int length)
/*      */   {
/*  632 */     if (length > 0)
/*      */     {
/*      */ 
/*  635 */       if (chars[start] == '0')
/*      */       {
/*  637 */         return 0L;
/*      */       }
/*      */       
/*  640 */       long result = 0L;
/*      */       
/*  642 */       boolean negative = false;
/*      */       
/*  644 */       int i = start;
/*      */       
/*      */       long limit;
/*      */       
/*  648 */       if (chars[i] == '-')
/*      */       {
/*  650 */         negative = true;
/*      */         
/*  652 */         long limit = Long.MIN_VALUE;
/*      */         
/*  654 */         i++;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  660 */         if (length == 1) {
/*  661 */           int digit = chars[i] - '0';
/*      */           
/*  663 */           if ((digit < 0) || (digit > 9))
/*      */           {
/*  665 */             throw new NumberFormatException(new String(chars, start, length));
/*      */           }
/*      */           
/*      */ 
/*  669 */           return digit;
/*      */         }
/*      */         
/*      */ 
/*  673 */         limit = -9223372036854775807L;
/*      */       }
/*      */       
/*  676 */       int max = start + length;
/*      */       
/*  678 */       if (i < max)
/*      */       {
/*  680 */         int digit = chars[(i++)] - '0';
/*      */         
/*  682 */         if ((digit < 0) || (digit > 9))
/*      */         {
/*  684 */           throw new NumberFormatException(new String(chars, start, length));
/*      */         }
/*      */         
/*      */ 
/*  688 */         result = -digit;
/*      */       }
/*      */       
/*      */ 
/*  692 */       long multmin = limit / 10L;
/*      */       
/*  694 */       while (i < max)
/*      */       {
/*      */ 
/*      */ 
/*  698 */         int digit = chars[(i++)] - '0';
/*      */         
/*  700 */         if ((digit < 0) || (digit > 9))
/*      */         {
/*  702 */           throw new NumberFormatException(new String(chars, start, length));
/*      */         }
/*      */         
/*  705 */         if (result < multmin)
/*      */         {
/*  707 */           throw new NumberFormatException(new String(chars, start, length));
/*      */         }
/*      */         
/*  710 */         result *= 10L;
/*      */         
/*  712 */         if (result < limit + digit)
/*      */         {
/*  714 */           throw new NumberFormatException(new String(chars, start, length));
/*      */         }
/*      */         
/*  717 */         result -= digit;
/*      */       }
/*      */       
/*  720 */       if (negative)
/*      */       {
/*  722 */         if (i > start + 1)
/*      */         {
/*  724 */           return result;
/*      */         }
/*      */         
/*      */ 
/*  728 */         throw new NumberFormatException(new String(chars, start, length));
/*      */       }
/*      */       
/*      */ 
/*  732 */       return -result;
/*      */     }
/*      */     
/*      */ 
/*  736 */     throw new NumberFormatException(new String(chars, start, length));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] getByteArrayFromStream(InputStream dbis, String context)
/*      */     throws IOException
/*      */   {
/*  795 */     int length = getPositiveNumberFromStream(dbis, ':');
/*      */     
/*  797 */     if (length < 0) {
/*  798 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  804 */     if (length > 104857600)
/*      */     {
/*  806 */       throw new IOException("Byte array length too large (" + length + ")");
/*      */     }
/*      */     
/*  809 */     byte[] tempArray = new byte[length];
/*      */     
/*  811 */     getByteArrayFromStream(dbis, length, tempArray);
/*      */     
/*  813 */     if ((PORTABLE_ROOT != null) && (length >= PORTABLE_ROOT.length) && (tempArray[1] == 58) && (tempArray[2] == 92) && (context != null))
/*      */     {
/*  815 */       boolean mismatch = false;
/*      */       
/*  817 */       for (int i = 2; i < PORTABLE_ROOT.length; i++)
/*      */       {
/*  819 */         if (tempArray[i] != PORTABLE_ROOT[i])
/*      */         {
/*  821 */           mismatch = true;
/*      */           
/*  823 */           break;
/*      */         }
/*      */       }
/*      */       
/*  827 */       if (!mismatch)
/*      */       {
/*  829 */         context = context.toLowerCase(Locale.US);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  834 */         if ((context.contains("file")) || (context.contains("link")) || (context.contains("dir")) || (context.contains("folder")) || (context.contains("path")) || (context.contains("save")) || (context.contains("torrent")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  842 */           tempArray[0] = PORTABLE_ROOT[0];
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  852 */           String test = new String(tempArray, 0, tempArray.length > 80 ? 80 : tempArray.length);
/*      */           
/*  854 */           System.out.println("Portable: not mapping " + context + "->" + tempArray.length + ": " + test);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  859 */     return tempArray;
/*      */   }
/*      */   
/*      */   private void getByteArrayFromStream(InputStream dbis, int length, byte[] targetArray) throws IOException
/*      */   {
/*  864 */     int count = 0;
/*  865 */     int len = 0;
/*      */     
/*  867 */     while ((count != length) && ((len = dbis.read(targetArray, count, length - count)) > 0)) {
/*  868 */       count += len;
/*      */     }
/*  870 */     if (count != length) {
/*  871 */       throw new IOException("BDecoder::getByteArrayFromStream: truncated");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setVerifyMapOrder(boolean b)
/*      */   {
/*  878 */     this.verify_map_order = b;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRecoveryMode(boolean r)
/*      */   {
/*  885 */     this.recovery_mode = r;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void print(Object obj)
/*      */   {
/*  892 */     StringWriter sw = new StringWriter();
/*      */     
/*  894 */     PrintWriter pw = new PrintWriter(sw);
/*      */     
/*  896 */     print(pw, obj);
/*      */     
/*  898 */     pw.flush();
/*      */     
/*  900 */     System.out.println(sw.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void print(PrintWriter writer, Object obj)
/*      */   {
/*  908 */     print(writer, obj, "", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void print(PrintWriter writer, Object obj, String indent, boolean skip_indent)
/*      */   {
/*  918 */     String use_indent = skip_indent ? "" : indent;
/*      */     
/*  920 */     if ((obj instanceof Long))
/*      */     {
/*  922 */       writer.println(use_indent + obj);
/*      */     }
/*  924 */     else if ((obj instanceof byte[]))
/*      */     {
/*  926 */       byte[] b = (byte[])obj;
/*      */       
/*  928 */       if (b.length == 20) {
/*  929 */         writer.println(use_indent + " { " + ByteFormatter.nicePrint(b) + " }");
/*  930 */       } else if (b.length < 64) {
/*  931 */         writer.println(new String(b) + " [" + ByteFormatter.encodeString(b) + "]");
/*      */       } else {
/*  933 */         writer.println("[byte array length " + b.length);
/*      */       }
/*      */     }
/*  936 */     else if ((obj instanceof String))
/*      */     {
/*  938 */       writer.println(use_indent + obj);
/*      */     }
/*  940 */     else if ((obj instanceof List))
/*      */     {
/*  942 */       List l = (List)obj;
/*      */       
/*  944 */       writer.println(use_indent + "[");
/*      */       
/*  946 */       for (int i = 0; i < l.size(); i++)
/*      */       {
/*  948 */         writer.print(indent + "  (" + i + ") ");
/*      */         
/*  950 */         print(writer, l.get(i), indent + "    ", true);
/*      */       }
/*      */       
/*  953 */       writer.println(indent + "]");
/*      */     }
/*      */     else
/*      */     {
/*  957 */       Map m = (Map)obj;
/*      */       
/*  959 */       Iterator it = m.keySet().iterator();
/*      */       
/*  961 */       while (it.hasNext())
/*      */       {
/*  963 */         String key = (String)it.next();
/*      */         
/*  965 */         if (key.length() > 256) {
/*  966 */           writer.print(indent + key.substring(0, 256) + "... = ");
/*      */         } else {
/*  968 */           writer.print(indent + key + " = ");
/*      */         }
/*      */         
/*  971 */         print(writer, m.get(key), indent + "  ", true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map decodeStrings(Map map)
/*      */   {
/*  988 */     if (map == null)
/*      */     {
/*  990 */       return null;
/*      */     }
/*      */     
/*  993 */     Iterator it = map.entrySet().iterator();
/*      */     
/*  995 */     while (it.hasNext())
/*      */     {
/*  997 */       Map.Entry entry = (Map.Entry)it.next();
/*      */       
/*  999 */       Object value = entry.getValue();
/*      */       
/* 1001 */       if ((value instanceof byte[])) {
/*      */         try
/*      */         {
/* 1004 */           entry.setValue(new String((byte[])value, "UTF-8"));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1008 */           System.err.println(e);
/*      */         }
/* 1010 */       } else if ((value instanceof Map))
/*      */       {
/* 1012 */         decodeStrings((Map)value);
/* 1013 */       } else if ((value instanceof List))
/*      */       {
/* 1015 */         decodeStrings((List)value);
/*      */       }
/*      */     }
/*      */     
/* 1019 */     return map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List decodeStrings(List list)
/*      */   {
/* 1033 */     if (list == null)
/*      */     {
/* 1035 */       return null;
/*      */     }
/*      */     
/* 1038 */     for (int i = 0; i < list.size(); i++)
/*      */     {
/* 1040 */       Object value = list.get(i);
/*      */       
/* 1042 */       if ((value instanceof byte[])) {
/*      */         try
/*      */         {
/* 1045 */           String str = new String((byte[])value, "UTF-8");
/*      */           
/* 1047 */           list.set(i, str);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1051 */           System.err.println(e);
/*      */         }
/* 1053 */       } else if ((value instanceof Map))
/*      */       {
/* 1055 */         decodeStrings((Map)value);
/*      */       }
/* 1057 */       else if ((value instanceof List))
/*      */       {
/* 1059 */         decodeStrings((List)value);
/*      */       }
/*      */     }
/*      */     
/* 1063 */     return list;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void print(File f, File output)
/*      */   {
/*      */     try
/*      */     {
/* 1072 */       BDecoder decoder = new BDecoder();
/*      */       
/* 1074 */       decoder.setRecoveryMode(false);
/*      */       
/* 1076 */       PrintWriter pw = new PrintWriter(new FileWriter(output));
/*      */       
/* 1078 */       print(pw, decoder.decodeStream(new BufferedInputStream(new FileInputStream(f))));
/*      */       
/* 1080 */       pw.flush();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1084 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Object decodeFromJSONGeneric(Object obj)
/*      */   {
/* 1094 */     if (obj == null)
/*      */     {
/* 1096 */       return null;
/*      */     }
/* 1098 */     if ((obj instanceof Map))
/*      */     {
/* 1100 */       return decodeFromJSONObject((Map)obj);
/*      */     }
/* 1102 */     if ((obj instanceof List))
/*      */     {
/* 1104 */       return decodeFromJSONArray((List)obj);
/*      */     }
/* 1106 */     if ((obj instanceof String))
/*      */     {
/* 1108 */       String s = (String)obj;
/*      */       
/*      */       try
/*      */       {
/* 1112 */         int len = s.length();
/*      */         
/* 1114 */         if ((len >= 6) && (s.startsWith("\\x")) && (s.endsWith("\\x")))
/*      */         {
/* 1116 */           byte[] result = new byte[(len - 4) / 2];
/*      */           
/* 1118 */           int pos = 2;
/*      */           
/* 1120 */           for (int i = 0; i < result.length; i++)
/*      */           {
/* 1122 */             result[i] = ((byte)Integer.parseInt(s.substring(pos, pos + 2), 16));
/*      */             
/* 1124 */             pos += 2;
/*      */           }
/*      */           
/* 1127 */           return result;
/*      */         }
/*      */         
/* 1130 */         return s.getBytes("UTF-8");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1134 */         return s.getBytes();
/*      */       }
/*      */     }
/* 1137 */     if ((obj instanceof Long))
/*      */     {
/* 1139 */       return obj;
/*      */     }
/* 1141 */     if ((obj instanceof Boolean))
/*      */     {
/* 1143 */       return new Long(((Boolean)obj).booleanValue() ? 1L : 0L);
/*      */     }
/* 1145 */     if ((obj instanceof Double))
/*      */     {
/* 1147 */       return String.valueOf((Double)obj);
/*      */     }
/*      */     
/*      */ 
/* 1151 */     System.err.println("Unexpected JSON value type: " + obj.getClass());
/*      */     
/* 1153 */     return obj;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List decodeFromJSONArray(List j_list)
/*      */   {
/* 1161 */     List b_list = new ArrayList();
/*      */     
/* 1163 */     for (Object o : j_list)
/*      */     {
/* 1165 */       b_list.add(decodeFromJSONGeneric(o));
/*      */     }
/*      */     
/* 1168 */     return b_list;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map decodeFromJSONObject(Map<Object, Object> j_map)
/*      */   {
/* 1176 */     Map b_map = new HashMap();
/*      */     
/* 1178 */     for (Map.Entry<Object, Object> entry : j_map.entrySet())
/*      */     {
/* 1180 */       Object key = entry.getKey();
/* 1181 */       Object val = entry.getValue();
/*      */       
/* 1183 */       b_map.put((String)key, decodeFromJSONGeneric(val));
/*      */     }
/*      */     
/* 1186 */     return b_map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Map decodeFromJSON(String json)
/*      */   {
/* 1193 */     Map j_map = JSONUtils.decodeJSON(json);
/*      */     
/* 1195 */     return decodeFromJSONObject(j_map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class BDecoderInputStreamArray
/*      */     extends InputStream
/*      */   {
/*      */     private final byte[] bytes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1318 */     private int pos = 0;
/*      */     private int markPos;
/*      */     private final int overPos;
/*      */     
/*      */     public BDecoderInputStreamArray(ByteBuffer buffer)
/*      */     {
/* 1324 */       this.bytes = buffer.array();
/* 1325 */       this.pos = (buffer.arrayOffset() + buffer.position());
/* 1326 */       this.overPos = (this.pos + buffer.remaining());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private BDecoderInputStreamArray(byte[] _buffer)
/*      */     {
/* 1334 */       this.bytes = _buffer;
/* 1335 */       this.overPos = this.bytes.length;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private BDecoderInputStreamArray(byte[] _buffer, int _offset, int _length)
/*      */     {
/* 1344 */       if (_offset == 0) {
/* 1345 */         this.bytes = _buffer;
/* 1346 */         this.overPos = _length;
/*      */       } else {
/* 1348 */         this.bytes = _buffer;
/* 1349 */         this.pos = _offset;
/* 1350 */         this.overPos = Math.min(_offset + _length, this.bytes.length);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int read()
/*      */       throws IOException
/*      */     {
/* 1359 */       if (this.pos < this.overPos) {
/* 1360 */         return this.bytes[(this.pos++)] & 0xFF;
/*      */       }
/* 1362 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int read(byte[] buffer)
/*      */       throws IOException
/*      */     {
/* 1371 */       return read(buffer, 0, buffer.length);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int read(byte[] b, int offset, int length)
/*      */       throws IOException
/*      */     {
/* 1383 */       if (this.pos < this.overPos) {
/* 1384 */         int toRead = Math.min(length, this.overPos - this.pos);
/* 1385 */         System.arraycopy(this.bytes, this.pos, b, offset, toRead);
/* 1386 */         this.pos += toRead;
/* 1387 */         return toRead;
/*      */       }
/* 1389 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int available()
/*      */       throws IOException
/*      */     {
/* 1398 */       return this.overPos - this.pos;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean markSupported()
/*      */     {
/* 1404 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void mark(int limit)
/*      */     {
/* 1411 */       this.markPos = this.pos;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void reset()
/*      */       throws IOException
/*      */     {
/* 1419 */       this.pos = this.markPos;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1428 */     print(new File("C:\\Temp\\tables.config"), new File("C:\\Temp\\tables.txt"));
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/BDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */