/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.StringWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URLEncoder;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*      */ import org.json.simple.JSONArray;
/*      */ import org.json.simple.JSONObject;
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
/*      */ public class BEncoder
/*      */ {
/*      */   private static final int BUFFER_DOUBLE_LIMIT = 262144;
/*   44 */   private static final byte[] MINUS_1_BYTES = "-1".getBytes();
/*      */   
/*      */ 
/*      */   private static volatile int non_ascii_logs;
/*      */   
/*      */ 
/*      */ 
/*      */   public static byte[] encode(Map object)
/*      */     throws IOException
/*      */   {
/*   54 */     return encode(object, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] encode(Map object, boolean url_encode)
/*      */     throws IOException
/*      */   {
/*   64 */     BEncoder encoder = new BEncoder(url_encode);
/*      */     
/*   66 */     encoder.encodeObject(object);
/*      */     
/*   68 */     return encoder.toByteArray();
/*      */   }
/*      */   
/*   71 */   private byte[] current_buffer = new byte['Ā'];
/*   72 */   private int current_buffer_pos = 0;
/*      */   
/*      */   private byte[][] old_buffers;
/*   75 */   private final byte[] int_buffer = new byte[12];
/*      */   
/*      */ 
/*      */   private final boolean url_encode;
/*      */   
/*      */ 
/*      */   private BEncoder(boolean _url_encode)
/*      */   {
/*   83 */     this.url_encode = _url_encode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean encodeObject(Object object)
/*      */     throws IOException
/*      */   {
/*   93 */     if ((object instanceof BEncodableObject)) {
/*   94 */       object = ((BEncodableObject)object).toBencodeObject();
/*      */     }
/*      */     
/*   97 */     if (((object instanceof String)) || ((object instanceof Float)) || ((object instanceof Double)))
/*      */     {
/*   99 */       String tempString = (object instanceof String) ? (String)object : String.valueOf(object);
/*      */       
/*      */ 
/*      */ 
/*  103 */       boolean simple = true;
/*      */       
/*  105 */       int char_count = tempString.length();
/*      */       
/*  107 */       byte[] encoded = new byte[char_count];
/*      */       
/*  109 */       for (int i = 0; i < char_count; i++)
/*      */       {
/*  111 */         char c = tempString.charAt(i);
/*      */         
/*  113 */         if (c < '')
/*      */         {
/*  115 */           encoded[i] = ((byte)c);
/*      */         }
/*      */         else
/*      */         {
/*  119 */           simple = false;
/*      */           
/*  121 */           break;
/*      */         }
/*      */       }
/*      */       
/*  125 */       if (simple)
/*      */       {
/*  127 */         writeInt(char_count);
/*      */         
/*  129 */         writeChar(':');
/*      */         
/*  131 */         writeBytes(encoded);
/*      */       }
/*      */       else
/*      */       {
/*  135 */         ByteBuffer bb = Constants.DEFAULT_CHARSET.encode(tempString);
/*      */         
/*  137 */         writeInt(bb.limit());
/*      */         
/*  139 */         writeChar(':');
/*      */         
/*  141 */         writeByteBuffer(bb);
/*      */       }
/*      */     }
/*  144 */     else if ((object instanceof Map))
/*      */     {
/*  146 */       Map tempMap = (Map)object;
/*      */       
/*  148 */       SortedMap tempTree = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  157 */       boolean byte_keys = object instanceof ByteEncodedKeyHashMap;
/*      */       
/*      */ 
/*  160 */       writeChar('d');
/*      */       
/*      */ 
/*  163 */       if ((tempMap instanceof TreeMap))
/*      */       {
/*  165 */         tempTree = (TreeMap)tempMap;
/*      */       }
/*      */       else {
/*  168 */         tempTree = new TreeMap(tempMap);
/*      */       }
/*      */       
/*  171 */       Iterator it = tempTree.entrySet().iterator();
/*      */       
/*  173 */       while (it.hasNext())
/*      */       {
/*  175 */         Map.Entry entry = (Map.Entry)it.next();
/*      */         
/*  177 */         Object o_key = entry.getKey();
/*      */         
/*  179 */         Object value = entry.getValue();
/*      */         
/*  181 */         if (value != null)
/*      */         {
/*  183 */           if ((o_key instanceof byte[]))
/*      */           {
/*  185 */             encodeObject(o_key);
/*  186 */             if (!encodeObject(value))
/*  187 */               encodeObject("");
/*  188 */           } else if ((o_key instanceof String))
/*      */           {
/*  190 */             String key = (String)o_key;
/*  191 */             if (byte_keys)
/*      */             {
/*      */               try
/*      */               {
/*  195 */                 encodeObject(Constants.BYTE_CHARSET.encode(key));
/*  196 */                 if (!encodeObject(value)) {
/*  197 */                   encodeObject("");
/*      */                 }
/*      */               } catch (UnsupportedEncodingException e) {
/*  200 */                 throw new IOException("BEncoder: unsupport encoding: " + e.getMessage());
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  208 */               if (Constants.IS_CVS_VERSION) {
/*  209 */                 char[] chars = key.toCharArray();
/*      */                 
/*  211 */                 for (char c : chars)
/*      */                 {
/*  213 */                   if (c >= '')
/*      */                   {
/*  215 */                     if (non_ascii_logs >= 50)
/*      */                       break;
/*  217 */                     non_ascii_logs += 1;
/*      */                     
/*  219 */                     Debug.out("Non-ASCII key: " + key); break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  226 */               encodeObject(key);
/*  227 */               if (!encodeObject(value))
/*  228 */                 encodeObject("");
/*      */             }
/*      */           } else {
/*  231 */             Debug.out("Attempt to encode an unsupported map key type: " + object.getClass() + ";value=" + object);
/*      */           }
/*      */         }
/*      */       }
/*  235 */       writeChar('e');
/*      */ 
/*      */     }
/*  238 */     else if ((object instanceof List))
/*      */     {
/*  240 */       List tempList = (List)object;
/*      */       
/*      */ 
/*      */ 
/*  244 */       writeChar('l');
/*      */       
/*  246 */       for (int i = 0; i < tempList.size(); i++)
/*      */       {
/*  248 */         encodeObject(tempList.get(i));
/*      */       }
/*      */       
/*  251 */       writeChar('e');
/*      */     }
/*  253 */     else if ((object instanceof Long))
/*      */     {
/*  255 */       Long tempLong = (Long)object;
/*      */       
/*  257 */       writeChar('i');
/*  258 */       writeLong(tempLong.longValue());
/*  259 */       writeChar('e');
/*      */     }
/*  261 */     else if ((object instanceof byte[]))
/*      */     {
/*  263 */       byte[] tempByteArray = (byte[])object;
/*  264 */       writeInt(tempByteArray.length);
/*  265 */       writeChar(':');
/*  266 */       if (this.url_encode) {
/*  267 */         writeBytes(URLEncoder.encode(new String(tempByteArray, "ISO-8859-1"), "ISO-8859-1").getBytes());
/*      */       } else {
/*  269 */         writeBytes(tempByteArray);
/*      */       }
/*      */     }
/*  272 */     else if ((object instanceof Integer))
/*      */     {
/*  274 */       Integer tempInteger = (Integer)object;
/*      */       
/*  276 */       writeChar('i');
/*  277 */       writeInt(tempInteger.intValue());
/*  278 */       writeChar('e');
/*      */     }
/*  280 */     else if ((object instanceof Byte))
/*      */     {
/*  282 */       byte temp = ((Byte)object).byteValue();
/*      */       
/*  284 */       writeChar('i');
/*  285 */       writeInt(temp & 0xFF);
/*  286 */       writeChar('e');
/*      */     }
/*  288 */     else if ((object instanceof ByteBuffer))
/*      */     {
/*  290 */       ByteBuffer bb = (ByteBuffer)object;
/*  291 */       writeInt(bb.limit());
/*  292 */       writeChar(':');
/*  293 */       writeByteBuffer(bb);
/*      */     } else {
/*  295 */       if (object == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  300 */         Debug.out("Attempt to encode a null value: sofar=" + getEncodedSoFar());
/*  301 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  306 */       Debug.out("Attempt to encode an unsupported entry type: " + object.getClass() + ";value=" + object);
/*  307 */       return false;
/*      */     }
/*      */     
/*  310 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void writeChar(char c)
/*      */   {
/*  317 */     int rem = this.current_buffer.length - this.current_buffer_pos;
/*      */     
/*  319 */     if (rem > 0)
/*      */     {
/*  321 */       this.current_buffer[(this.current_buffer_pos++)] = ((byte)c);
/*      */     }
/*      */     else
/*      */     {
/*  325 */       int next_buffer_size = this.current_buffer.length < 262144 ? this.current_buffer.length << 1 : this.current_buffer.length + 262144;
/*      */       
/*  327 */       byte[] new_buffer = new byte[next_buffer_size];
/*      */       
/*  329 */       new_buffer[0] = ((byte)c);
/*      */       
/*  331 */       if (this.old_buffers == null)
/*      */       {
/*  333 */         this.old_buffers = new byte[][] { this.current_buffer };
/*      */       }
/*      */       else
/*      */       {
/*  337 */         byte[][] new_old_buffers = new byte[this.old_buffers.length + 1][];
/*      */         
/*  339 */         System.arraycopy(this.old_buffers, 0, new_old_buffers, 0, this.old_buffers.length);
/*      */         
/*  341 */         new_old_buffers[this.old_buffers.length] = this.current_buffer;
/*      */         
/*  343 */         this.old_buffers = new_old_buffers;
/*      */       }
/*      */       
/*  346 */       this.current_buffer = new_buffer;
/*  347 */       this.current_buffer_pos = 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void writeInt(int i)
/*      */   {
/*  357 */     if (i == -1)
/*      */     {
/*  359 */       writeBytes(MINUS_1_BYTES);
/*      */       
/*  361 */       return;
/*      */     }
/*      */     
/*  364 */     int start = intToBytes(i);
/*      */     
/*  366 */     writeBytes(this.int_buffer, start, 12 - start);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void writeLong(long l)
/*      */   {
/*  373 */     if ((l <= 2147483647L) && (l >= -2147483648L))
/*      */     {
/*  375 */       writeInt((int)l);
/*      */     }
/*      */     else
/*      */     {
/*  379 */       writeBytes(Long.toString(l).getBytes());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void writeBytes(byte[] bytes)
/*      */   {
/*  387 */     writeBytes(bytes, 0, bytes.length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void writeBytes(byte[] bytes, int offset, int length)
/*      */   {
/*  396 */     int rem = this.current_buffer.length - this.current_buffer_pos;
/*      */     
/*  398 */     if (rem >= length)
/*      */     {
/*  400 */       System.arraycopy(bytes, offset, this.current_buffer, this.current_buffer_pos, length);
/*      */       
/*  402 */       this.current_buffer_pos += length;
/*      */     }
/*      */     else
/*      */     {
/*  406 */       if (rem > 0)
/*      */       {
/*  408 */         System.arraycopy(bytes, offset, this.current_buffer, this.current_buffer_pos, rem);
/*      */         
/*  410 */         length -= rem;
/*      */       }
/*      */       
/*  413 */       int next_buffer_size = this.current_buffer.length < 262144 ? this.current_buffer.length << 1 : this.current_buffer.length + 262144;
/*      */       
/*  415 */       byte[] new_buffer = new byte[Math.max(next_buffer_size, length + 512)];
/*      */       
/*  417 */       System.arraycopy(bytes, offset + rem, new_buffer, 0, length);
/*      */       
/*  419 */       if (this.old_buffers == null)
/*      */       {
/*  421 */         this.old_buffers = new byte[][] { this.current_buffer };
/*      */       }
/*      */       else
/*      */       {
/*  425 */         byte[][] new_old_buffers = new byte[this.old_buffers.length + 1][];
/*      */         
/*  427 */         System.arraycopy(this.old_buffers, 0, new_old_buffers, 0, this.old_buffers.length);
/*      */         
/*  429 */         new_old_buffers[this.old_buffers.length] = this.current_buffer;
/*      */         
/*  431 */         this.old_buffers = new_old_buffers;
/*      */       }
/*      */       
/*  434 */       this.current_buffer = new_buffer;
/*  435 */       this.current_buffer_pos = length;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void writeByteBuffer(ByteBuffer bb)
/*      */   {
/*  443 */     writeBytes(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
/*      */   }
/*      */   
/*      */ 
/*      */   private String getEncodedSoFar()
/*      */   {
/*  449 */     return new String(toByteArray());
/*      */   }
/*      */   
/*      */ 
/*      */   private byte[] toByteArray()
/*      */   {
/*  455 */     if (this.old_buffers == null)
/*      */     {
/*  457 */       byte[] res = new byte[this.current_buffer_pos];
/*      */       
/*  459 */       System.arraycopy(this.current_buffer, 0, res, 0, this.current_buffer_pos);
/*      */       
/*      */ 
/*      */ 
/*  463 */       return res;
/*      */     }
/*      */     
/*      */ 
/*  467 */     int total = this.current_buffer_pos;
/*      */     
/*  469 */     for (int i = 0; i < this.old_buffers.length; i++)
/*      */     {
/*  471 */       total += this.old_buffers[i].length;
/*      */     }
/*      */     
/*  474 */     byte[] res = new byte[total];
/*      */     
/*  476 */     int pos = 0;
/*      */     
/*      */ 
/*      */ 
/*  480 */     for (int i = 0; i < this.old_buffers.length; i++)
/*      */     {
/*  482 */       byte[] buffer = this.old_buffers[i];
/*      */       
/*  484 */       int len = buffer.length;
/*      */       
/*  486 */       System.arraycopy(buffer, 0, res, pos, len);
/*      */       
/*  488 */       pos += len;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  493 */     System.arraycopy(this.current_buffer, 0, res, pos, this.current_buffer_pos);
/*      */     
/*      */ 
/*      */ 
/*  497 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Object normaliseObject(Object o)
/*      */   {
/*  506 */     if ((o instanceof Integer)) {
/*  507 */       o = new Long(((Integer)o).longValue());
/*  508 */     } else if ((o instanceof Boolean)) {
/*  509 */       o = new Long(((Boolean)o).booleanValue() ? 1L : 0L);
/*  510 */     } else if ((o instanceof Float)) {
/*  511 */       o = String.valueOf((Float)o);
/*  512 */     } else if ((o instanceof Double)) {
/*  513 */       o = String.valueOf((Double)o);
/*  514 */     } else if ((o instanceof byte[])) {
/*      */       try {
/*  516 */         byte[] b = (byte[])o;
/*      */         
/*  518 */         String s = new String(b, "UTF-8");
/*      */         
/*  520 */         byte[] temp = s.getBytes("UTF-8");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  526 */         if (!Arrays.equals(b, temp))
/*      */         {
/*  528 */           StringBuilder sb = new StringBuilder(b.length * 2 + 4);
/*      */           
/*  530 */           sb.append("\\x");
/*      */           
/*  532 */           for (byte x : b) {
/*  533 */             String ss = Integer.toHexString(x & 0xFF);
/*  534 */             for (int k = 0; k < 2 - ss.length(); k++) {
/*  535 */               sb.append('0');
/*      */             }
/*  537 */             sb.append(ss);
/*      */           }
/*      */           
/*  540 */           sb.append("\\x");
/*      */           
/*  542 */           o = sb.toString();
/*      */         }
/*      */         else
/*      */         {
/*  546 */           o = s;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*  552 */     return o;
/*      */   }
/*      */   
/*      */   public static boolean isEncodable(Object toCheck) {
/*  556 */     if (((toCheck instanceof Integer)) || ((toCheck instanceof Long)) || ((toCheck instanceof Boolean)) || ((toCheck instanceof Float)) || ((toCheck instanceof byte[])) || ((toCheck instanceof String)) || ((toCheck instanceof BEncodableObject)))
/*  557 */       return true;
/*  558 */     if ((toCheck instanceof Map))
/*      */     {
/*  560 */       for (Iterator it = ((Map)toCheck).keySet().iterator(); it.hasNext();)
/*      */       {
/*  562 */         Map.Entry entry = (Map.Entry)it.next();
/*  563 */         Object key = entry.getKey();
/*  564 */         if (((!(key instanceof String)) && (!(key instanceof byte[]))) || (!isEncodable(entry.getValue())))
/*  565 */           return false;
/*      */       }
/*  567 */       return true;
/*      */     }
/*  569 */     if ((toCheck instanceof List))
/*      */     {
/*  571 */       for (Iterator it = ((List)toCheck).iterator(); it.hasNext();)
/*  572 */         if (!isEncodable(it.next()))
/*  573 */           return false;
/*  574 */       return true;
/*      */     }
/*  576 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean objectsAreIdentical(Object o1, Object o2)
/*      */   {
/*  585 */     if ((o1 == null) && (o2 == null))
/*      */     {
/*  587 */       return true;
/*      */     }
/*  589 */     if ((o1 == null) || (o2 == null))
/*      */     {
/*  591 */       return false;
/*      */     }
/*      */     
/*  594 */     if (o1.getClass() != o2.getClass())
/*      */     {
/*  596 */       if (((!(o1 instanceof Map)) || (!(o2 instanceof Map))) && ((!(o1 instanceof List)) || (!(o2 instanceof List))))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  603 */         o1 = normaliseObject(o1);
/*  604 */         o2 = normaliseObject(o2);
/*      */         
/*  606 */         if (o1.getClass() != o2.getClass())
/*      */         {
/*  608 */           Debug.out("Failed to normalise classes " + o1.getClass() + "/" + o2.getClass());
/*      */           
/*  610 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  615 */     if (((o1 instanceof Long)) || ((o1 instanceof String)))
/*      */     {
/*      */ 
/*  618 */       return o1.equals(o2);
/*      */     }
/*  620 */     if ((o1 instanceof byte[]))
/*      */     {
/*  622 */       return Arrays.equals((byte[])o1, (byte[])o2);
/*      */     }
/*  624 */     if ((o1 instanceof List))
/*      */     {
/*  626 */       return listsAreIdentical((List)o1, (List)o2);
/*      */     }
/*  628 */     if ((o1 instanceof Map))
/*      */     {
/*  630 */       return mapsAreIdentical((Map)o1, (Map)o2);
/*      */     }
/*  632 */     if (((o1 instanceof Integer)) || ((o1 instanceof Boolean)) || ((o1 instanceof Float)) || ((o1 instanceof ByteBuffer)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  637 */       return o1.equals(o2);
/*      */     }
/*      */     
/*      */ 
/*  641 */     Debug.out("Invalid type: " + o1);
/*      */     
/*  643 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean listsAreIdentical(List list1, List list2)
/*      */   {
/*  652 */     if ((list1 == null) && (list2 == null))
/*      */     {
/*  654 */       return true;
/*      */     }
/*  656 */     if ((list1 == null) || (list2 == null))
/*      */     {
/*  658 */       return false;
/*      */     }
/*      */     
/*  661 */     if (list1.size() != list2.size())
/*      */     {
/*  663 */       return false;
/*      */     }
/*      */     
/*  666 */     for (int i = 0; i < list1.size(); i++)
/*      */     {
/*  668 */       if (!objectsAreIdentical(list1.get(i), list2.get(i)))
/*      */       {
/*  670 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  674 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean mapsAreIdentical(Map map1, Map map2)
/*      */   {
/*  682 */     if ((map1 == null) && (map2 == null))
/*      */     {
/*  684 */       return true;
/*      */     }
/*  686 */     if ((map1 == null) || (map2 == null))
/*      */     {
/*  688 */       return false;
/*      */     }
/*      */     
/*  691 */     if (map1.size() != map2.size())
/*      */     {
/*  693 */       return false;
/*      */     }
/*      */     
/*  696 */     for (Map.Entry<Object, Object> entry : map1.entrySet())
/*      */     {
/*  698 */       Object key = entry.getKey();
/*      */       
/*  700 */       Object v1 = entry.getValue();
/*  701 */       Object v2 = map2.get(key);
/*      */       
/*  703 */       if (!objectsAreIdentical(v1, v2))
/*      */       {
/*  705 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  709 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Map cloneMap(Map map)
/*      */   {
/*  716 */     if (map == null)
/*      */     {
/*  718 */       return null;
/*      */     }
/*      */     
/*  721 */     Map res = new TreeMap();
/*      */     
/*  723 */     Iterator it = map.entrySet().iterator();
/*      */     
/*  725 */     while (it.hasNext())
/*      */     {
/*  727 */       Map.Entry entry = (Map.Entry)it.next();
/*      */       
/*  729 */       Object key = entry.getKey();
/*  730 */       Object value = entry.getValue();
/*      */       
/*      */ 
/*      */ 
/*  734 */       if ((key instanceof byte[]))
/*      */       {
/*  736 */         key = ((byte[])key).clone();
/*      */       }
/*      */       
/*  739 */       res.put(key, clone(value));
/*      */     }
/*      */     
/*  742 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List cloneList(List list)
/*      */   {
/*  749 */     if (list == null)
/*      */     {
/*  751 */       return null;
/*      */     }
/*      */     
/*  754 */     List res = new ArrayList(list.size());
/*      */     
/*  756 */     Iterator it = list.iterator();
/*      */     
/*  758 */     while (it.hasNext())
/*      */     {
/*  760 */       res.add(clone(it.next()));
/*      */     }
/*      */     
/*  763 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Object clone(Object obj)
/*      */   {
/*  770 */     if ((obj instanceof List))
/*      */     {
/*  772 */       return cloneList((List)obj);
/*      */     }
/*  774 */     if ((obj instanceof Map))
/*      */     {
/*  776 */       return cloneMap((Map)obj);
/*      */     }
/*  778 */     if ((obj instanceof byte[]))
/*      */     {
/*  780 */       return ((byte[])obj).clone();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  785 */     return obj;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static StringBuffer encodeToXML(Map map, boolean simple)
/*      */   {
/*  794 */     XMLEncoder writer = new XMLEncoder();
/*      */     
/*  796 */     return writer.encode(map, simple);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Object encodeToJSONGeneric(Object obj)
/*      */   {
/*  805 */     if ((obj instanceof Map))
/*      */     {
/*  807 */       return encodeToJSONObject((Map)obj);
/*      */     }
/*  809 */     if ((obj instanceof List))
/*      */     {
/*  811 */       return encodeToJSONArray((List)obj);
/*      */     }
/*      */     
/*      */ 
/*  815 */     return normaliseObject(obj);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static JSONArray encodeToJSONArray(List b_list)
/*      */   {
/*  823 */     if (b_list == null)
/*      */     {
/*  825 */       return null;
/*      */     }
/*      */     
/*  828 */     JSONArray j_list = new JSONArray();
/*      */     
/*  830 */     for (Object o : b_list)
/*      */     {
/*  832 */       j_list.add(encodeToJSONGeneric(o));
/*      */     }
/*      */     
/*  835 */     return j_list;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static JSONObject encodeToJSONObject(Map<Object, Object> b_map)
/*      */   {
/*  843 */     if (b_map == null)
/*      */     {
/*  845 */       return null;
/*      */     }
/*      */     
/*  848 */     JSONObject j_map = new JSONObject();
/*      */     
/*  850 */     for (Map.Entry<Object, Object> entry : b_map.entrySet())
/*      */     {
/*  852 */       Object key = entry.getKey();
/*  853 */       Object val = entry.getValue();
/*      */       
/*  855 */       j_map.put((String)key, encodeToJSONGeneric(val));
/*      */     }
/*      */     
/*  858 */     return j_map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String encodeToJSON(Map b_map)
/*      */   {
/*  865 */     if (b_map == null)
/*      */     {
/*  867 */       return null;
/*      */     }
/*      */     
/*  870 */     JSONObject j_map = encodeToJSONObject(b_map);
/*      */     
/*  872 */     return JSONUtils.encodeToJSON(j_map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  878 */   static final byte[] digits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  887 */   static final byte[] DigitTens = { 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57 };
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
/*  900 */   static final byte[] DigitOnes = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 };
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
/*      */   private int intToBytes(int i)
/*      */   {
/*  924 */     int charPos = 12;
/*  925 */     byte sign = 0;
/*      */     
/*  927 */     if (i < 0) {
/*  928 */       sign = 45;
/*  929 */       i = -i;
/*      */     }
/*      */     
/*      */ 
/*  933 */     while (i >= 65536) {
/*  934 */       int q = i / 100;
/*      */       
/*  936 */       int r = i - ((q << 6) + (q << 5) + (q << 2));
/*  937 */       i = q;
/*  938 */       this.int_buffer[(--charPos)] = DigitOnes[r];
/*  939 */       this.int_buffer[(--charPos)] = DigitTens[r];
/*      */     }
/*      */     
/*      */ 
/*      */     for (;;)
/*      */     {
/*  945 */       int q = i * 52429 >>> 19;
/*  946 */       int r = i - ((q << 3) + (q << 1));
/*  947 */       this.int_buffer[(--charPos)] = digits[r];
/*  948 */       i = q;
/*  949 */       if (i == 0) break;
/*      */     }
/*  951 */     if (sign != 0) {
/*  952 */       this.int_buffer[(--charPos)] = sign;
/*      */     }
/*  954 */     return charPos;
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
/*      */   protected static class XMLEncoder
/*      */     extends XUXmlWriter
/*      */   {
/*      */     protected StringBuffer encode(Map map, boolean simple)
/*      */     {
/*  971 */       StringWriter writer = new StringWriter(1024);
/*      */       
/*  973 */       setOutputWriter(writer);
/*      */       
/*  975 */       setGenericSimple(simple);
/*      */       
/*  977 */       writeGeneric(map);
/*      */       
/*  979 */       flushOutputStream();
/*      */       
/*  981 */       return writer.getBuffer();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*  989 */     Map map = new HashMap();
/*      */     
/*  991 */     map.put("a", new Float(1.2D));
/*  992 */     map.put("b", Boolean.valueOf(true));
/*  993 */     map.put("c", "fred".getBytes());
/*      */     
/*  995 */     Map m2 = new HashMap();
/*      */     
/*  997 */     m2.put("boo", "meep");
/*  998 */     map.put("m", m2);
/*      */     
/* 1000 */     List l = new ArrayList();
/*      */     
/* 1002 */     l.add("foo");
/*      */     
/* 1004 */     map.put("l", l);
/*      */     
/* 1006 */     System.out.println(encodeToJSON(map));
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/BEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */