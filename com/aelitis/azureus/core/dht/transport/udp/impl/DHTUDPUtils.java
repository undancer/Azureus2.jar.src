/*      */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*      */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.VivaldiPositionFactory;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeNetwork;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*      */ public class DHTUDPUtils
/*      */ {
/*   61 */   public static final IOException INVALID_PROTOCOL_VERSION_EXCEPTION = new IOException("Invalid DHT protocol version, please update Azureus");
/*      */   
/*      */   protected static final int CT_UDP = 1;
/*      */   
/*   65 */   private static final Map<String, byte[]> node_id_history = new LinkedHashMap(128, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest)
/*      */     {
/*      */ 
/*   72 */       return size() > 128;
/*      */     }
/*      */   };
/*      */   
/*   76 */   private static final SHA1Simple hasher = new SHA1Simple();
/*      */   
/*      */   public static final int INETSOCKETADDRESS_IPV4_SIZE = 7;
/*      */   
/*      */   public static final int INETSOCKETADDRESS_IPV6_SIZE = 19;
/*      */   public static final int DHTTRANSPORTCONTACT_SIZE = 9;
/*      */   public static final int DHTTRANSPORTVALUE_SIZE_WITHOUT_VALUE = 26;
/*      */   
/*      */   protected static byte[] getNodeID(InetSocketAddress address, byte protocol_version)
/*      */     throws DHTTransportException
/*      */   {
/*   87 */     InetAddress ia = address.getAddress();
/*      */     String key;
/*   89 */     if (ia == null) {
/*      */       String key;
/*   91 */       if (address.getHostName().equals("dht6.vuze.com"))
/*      */       {
/*   93 */         key = "IPv6SeedHack";
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*   98 */         throw new DHTTransportException("Address '" + address + "' is unresolved");
/*      */       }
/*      */     }
/*      */     else {
/*      */       String key;
/*  103 */       if (protocol_version >= 50)
/*      */       {
/*  105 */         byte[] bytes = ia.getAddress();
/*      */         String key;
/*  107 */         if (bytes.length == 4)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  115 */           long K2 = 2500L;
/*  116 */           long K3 = 50L;
/*  117 */           long K4 = 5L;
/*      */           
/*  119 */           long result = address.getPort() % 5L;
/*      */           
/*  121 */           result = (bytes[3] << 8 & 0xFF00 | result) % 50L;
/*  122 */           result = (bytes[2] << 16 & 0xFF0000 | result) % 2500L;
/*  123 */           result = bytes[1] << 24 & 0xFF000000 | result;
/*  124 */           result = bytes[0] << 32 & 0xFF00000000 | result;
/*      */           
/*  126 */           key = String.valueOf(result);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  132 */           key = ia.getHostAddress() + ":" + address.getPort() % 8;
/*      */         } } else { String key;
/*  134 */         if (protocol_version >= 33)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  140 */           key = ia.getHostAddress() + ":" + address.getPort() % 8;
/*      */         } else { String key;
/*  142 */           if (protocol_version >= 32)
/*      */           {
/*      */ 
/*      */ 
/*  146 */             key = ia.getHostAddress() + ":" + address.getPort() % 1999;
/*      */           }
/*      */           else
/*      */           {
/*  150 */             key = ia.getHostAddress() + ":" + address.getPort(); }
/*      */         }
/*      */       }
/*      */     }
/*  154 */     synchronized (node_id_history)
/*      */     {
/*  156 */       byte[] res = (byte[])node_id_history.get(key);
/*      */       
/*  158 */       if (res == null)
/*      */       {
/*  160 */         res = hasher.calculateHash(key.getBytes());
/*      */         
/*  162 */         node_id_history.put(key, res);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  167 */       return res;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static byte[] getBogusNodeID()
/*      */   {
/*  174 */     byte[] id = new byte[20];
/*      */     
/*  176 */     RandomUtils.nextBytes(id);
/*      */     
/*  178 */     return id;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseLength(DataOutputStream os, int len, int max_length)
/*      */     throws IOException
/*      */   {
/*  189 */     if (len > max_length)
/*      */     {
/*  191 */       throw new IOException("Invalid DHT data length: max=" + max_length + ",actual=" + len);
/*      */     }
/*      */     
/*  194 */     if (max_length < 256)
/*      */     {
/*  196 */       os.writeByte(len);
/*      */     }
/*  198 */     else if (max_length < 65536)
/*      */     {
/*  200 */       os.writeShort(len);
/*      */     }
/*      */     else
/*      */     {
/*  204 */       os.writeInt(len);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static int deserialiseLength(DataInputStream is, int max_length)
/*      */     throws IOException
/*      */   {
/*      */     int len;
/*      */     
/*      */     int len;
/*      */     
/*  217 */     if (max_length < 256)
/*      */     {
/*  219 */       len = is.readByte() & 0xFF;
/*      */     } else { int len;
/*  221 */       if (max_length < 65536)
/*      */       {
/*  223 */         len = is.readShort() & 0xFFFF;
/*      */       }
/*      */       else
/*      */       {
/*  227 */         len = is.readInt();
/*      */       }
/*      */     }
/*  230 */     if (len > max_length)
/*      */     {
/*  232 */       throw new IOException("Invalid DHT data length: max=" + max_length + ",actual=" + len);
/*      */     }
/*      */     
/*  235 */     return len;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static byte[] deserialiseByteArray(DataInputStream is, int max_length)
/*      */     throws IOException
/*      */   {
/*  245 */     int len = deserialiseLength(is, max_length);
/*      */     
/*  247 */     byte[] data = new byte[len];
/*      */     
/*  249 */     is.read(data);
/*      */     
/*  251 */     return data;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseByteArray(DataOutputStream os, byte[] data, int max_length)
/*      */     throws IOException
/*      */   {
/*  262 */     serialiseByteArray(os, data, 0, data.length, max_length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseByteArray(DataOutputStream os, byte[] data, int start, int length, int max_length)
/*      */     throws IOException
/*      */   {
/*  275 */     serialiseLength(os, length, max_length);
/*      */     
/*  277 */     os.write(data, start, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseByteArrayArray(DataOutputStream os, byte[][] data, int max_length)
/*      */     throws IOException
/*      */   {
/*  288 */     serialiseLength(os, data.length, max_length);
/*      */     
/*  290 */     for (int i = 0; i < data.length; i++)
/*      */     {
/*  292 */       serialiseByteArray(os, data[i], max_length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static byte[][] deserialiseByteArrayArray(DataInputStream is, int max_length)
/*      */     throws IOException
/*      */   {
/*  303 */     int len = deserialiseLength(is, max_length);
/*      */     
/*  305 */     byte[][] data = new byte[len][];
/*      */     
/*  307 */     for (int i = 0; i < data.length; i++)
/*      */     {
/*  309 */       data[i] = deserialiseByteArray(is, max_length);
/*      */     }
/*      */     
/*  312 */     return data;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseAddress(DataOutputStream os, InetSocketAddress address)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  325 */     InetAddress ia = address.getAddress();
/*      */     
/*  327 */     if (ia == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  332 */       serialiseByteArray(os, new byte[4], 16);
/*      */       
/*  334 */       os.writeShort(0);
/*      */     }
/*      */     else
/*      */     {
/*  338 */       serialiseByteArray(os, ia.getAddress(), 16);
/*      */       
/*  340 */       os.writeShort(address.getPort());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static InetSocketAddress deserialiseAddress(DataInputStream is)
/*      */     throws IOException
/*      */   {
/*  350 */     byte[] bytes = deserialiseByteArray(is, 16);
/*      */     
/*  352 */     int port = is.readShort() & 0xFFFF;
/*      */     
/*  354 */     return new InetSocketAddress(InetAddress.getByAddress(bytes), port);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportValue[][] deserialiseTransportValuesArray(DHTUDPPacket packet, DataInputStream is, long skew, int max_length)
/*      */     throws IOException
/*      */   {
/*  366 */     int len = deserialiseLength(is, max_length);
/*      */     
/*  368 */     DHTTransportValue[][] data = new DHTTransportValue[len][];
/*      */     
/*  370 */     for (int i = 0; i < data.length; i++)
/*      */     {
/*  372 */       data[i] = deserialiseTransportValues(packet, is, skew);
/*      */     }
/*      */     
/*  375 */     return data;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseTransportValuesArray(DHTUDPPacket packet, DataOutputStream os, DHTTransportValue[][] values, long skew, int max_length)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  388 */     serialiseLength(os, values.length, max_length);
/*      */     
/*  390 */     for (int i = 0; i < values.length; i++)
/*      */     {
/*  392 */       serialiseTransportValues(packet, os, values[i], skew);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseContact(DataOutputStream os, DHTTransportContact contact)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  405 */     if ((contact.getTransport() instanceof DHTTransportUDP))
/*      */     {
/*  407 */       os.writeByte(1);
/*      */       
/*  409 */       os.writeByte(contact.getProtocolVersion());
/*      */       
/*  411 */       serialiseAddress(os, contact.getExternalAddress());
/*      */     }
/*      */     else
/*      */     {
/*  415 */       throw new IOException("Unsupported contact type:" + contact.getClass().getName());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportUDPContactImpl deserialiseContact(DHTTransportUDPImpl transport, DataInputStream is)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  426 */     byte ct = is.readByte();
/*      */     
/*  428 */     if (ct != 1)
/*      */     {
/*  430 */       throw new IOException("Unsupported contact type:" + ct);
/*      */     }
/*      */     
/*  433 */     byte version = is.readByte();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  438 */     InetSocketAddress external_address = deserialiseAddress(is);
/*      */     
/*  440 */     return new DHTTransportUDPContactImpl(false, transport, external_address, external_address, version, 0, 0L, (byte)0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseAltContact(DataOutputStream os, DHTTransportAlternativeContact contact)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  450 */     os.write((byte)contact.getNetworkType());
/*      */     
/*  452 */     os.write((byte)contact.getVersion());
/*      */     
/*  454 */     os.writeShort(contact.getAge());
/*      */     
/*  456 */     byte[] encoded = BEncoder.encode(contact.getProperties());
/*      */     
/*  458 */     serialiseByteArray(os, encoded, 65535);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportAlternativeContactImpl deserialiseAltContact(DataInputStream is)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  467 */     byte network_type = is.readByte();
/*  468 */     byte version = is.readByte();
/*  469 */     short age = is.readShort();
/*      */     
/*  471 */     byte[] encoded = deserialiseByteArray(is, 65535);
/*      */     
/*  473 */     return new DHTTransportAlternativeContactImpl(network_type, version, age, encoded);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportValue[] deserialiseTransportValues(DHTUDPPacket packet, DataInputStream is, long skew)
/*      */     throws IOException
/*      */   {
/*  484 */     int len = deserialiseLength(is, 65535);
/*      */     
/*  486 */     List l = new ArrayList(len);
/*      */     
/*  488 */     for (int i = 0; i < len; i++)
/*      */     {
/*      */       try
/*      */       {
/*  492 */         l.add(deserialiseTransportValue(packet, is, skew));
/*      */       }
/*      */       catch (DHTTransportException e)
/*      */       {
/*  496 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  500 */     DHTTransportValue[] res = new DHTTransportValue[l.size()];
/*      */     
/*  502 */     l.toArray(res);
/*      */     
/*  504 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseTransportValues(DHTUDPPacket packet, DataOutputStream os, DHTTransportValue[] values, long skew)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  516 */     serialiseLength(os, values.length, 65535);
/*      */     
/*  518 */     for (int i = 0; i < values.length; i++)
/*      */     {
/*  520 */       serialiseTransportValue(packet, os, values[i], skew);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static DHTTransportValue deserialiseTransportValue(DHTUDPPacket packet, DataInputStream is, long skew)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*      */     int version;
/*      */     
/*      */ 
/*      */     final int version;
/*      */     
/*  534 */     if (packet.getProtocolVersion() >= 11)
/*      */     {
/*  536 */       version = is.readInt();
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  542 */       version = -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  549 */     long created = is.readLong() + skew;
/*      */     
/*      */ 
/*      */ 
/*  553 */     byte[] value_bytes = deserialiseByteArray(is, 512);
/*      */     
/*  555 */     final DHTTransportContact originator = deserialiseContact(packet.getTransport(), is);
/*      */     
/*  557 */     final int flags = is.readByte() & 0xFF;
/*      */     
/*      */     int life_hours;
/*      */     final int life_hours;
/*  561 */     if (packet.getProtocolVersion() >= 23)
/*      */     {
/*  563 */       life_hours = is.readByte() & 0xFF;
/*      */     }
/*      */     else
/*      */     {
/*  567 */       life_hours = 0;
/*      */     }
/*      */     
/*      */     byte rep_control;
/*      */     final byte rep_control;
/*  572 */     if (packet.getProtocolVersion() >= 24)
/*      */     {
/*  574 */       rep_control = is.readByte();
/*      */     }
/*      */     else
/*      */     {
/*  578 */       rep_control = -1;
/*      */     }
/*      */     
/*  581 */     DHTTransportValue value = new DHTTransportValue()
/*      */     {
/*      */ 
/*      */       public boolean isLocal()
/*      */       {
/*      */ 
/*  587 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getCreationTime()
/*      */       {
/*  593 */         return this.val$created;
/*      */       }
/*      */       
/*      */ 
/*      */       public byte[] getValue()
/*      */       {
/*  599 */         return version;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getVersion()
/*      */       {
/*  605 */         return originator;
/*      */       }
/*      */       
/*      */ 
/*      */       public DHTTransportContact getOriginator()
/*      */       {
/*  611 */         return flags;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getFlags()
/*      */       {
/*  617 */         return life_hours;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLifeTimeHours()
/*      */       {
/*  623 */         return rep_control;
/*      */       }
/*      */       
/*      */ 
/*      */       public byte getReplicationControl()
/*      */       {
/*  629 */         return this.val$rep_control;
/*      */       }
/*      */       
/*      */ 
/*      */       public byte getReplicationFactor()
/*      */       {
/*  635 */         return this.val$rep_control == -1 ? -1 : (byte)(this.val$rep_control & 0xF);
/*      */       }
/*      */       
/*      */ 
/*      */       public byte getReplicationFrequencyHours()
/*      */       {
/*  641 */         return this.val$rep_control == -1 ? -1 : (byte)(this.val$rep_control >> 4);
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/*  647 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  649 */         return DHTLog.getString(version) + " - " + new String(version) + "{v=" + originator + ",f=" + Integer.toHexString(life_hours) + ",l=" + rep_control + ",r=" + Integer.toHexString(getReplicationControl()) + ",ca=" + (now - this.val$created) + ",or=" + flags.getString() + "}";
/*      */       }
/*      */       
/*      */ 
/*  653 */     };
/*  654 */     return value;
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
/*      */   protected static void serialiseTransportValue(DHTUDPPacket packet, DataOutputStream os, DHTTransportValue value, long skew)
/*      */     throws IOException, DHTTransportException
/*      */   {
/*  668 */     if (packet.getProtocolVersion() >= 11)
/*      */     {
/*  670 */       int version = value.getVersion();
/*      */       
/*      */ 
/*      */ 
/*  674 */       os.writeInt(version);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  679 */       os.writeInt(0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  684 */     os.writeLong(value.getCreationTime() + skew);
/*      */     
/*  686 */     serialiseByteArray(os, value.getValue(), 512);
/*      */     
/*  688 */     serialiseContact(os, value.getOriginator());
/*      */     
/*  690 */     os.writeByte(value.getFlags());
/*      */     
/*  692 */     if (packet.getProtocolVersion() >= 23)
/*      */     {
/*  694 */       os.writeByte(value.getLifeTimeHours());
/*      */     }
/*      */     
/*  697 */     if (packet.getProtocolVersion() >= 24)
/*      */     {
/*  699 */       os.writeByte(value.getReplicationControl());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseContacts(DataOutputStream os, DHTTransportContact[] contacts)
/*      */     throws IOException
/*      */   {
/*  710 */     serialiseLength(os, contacts.length, 65535);
/*      */     
/*  712 */     for (int i = 0; i < contacts.length; i++) {
/*      */       try
/*      */       {
/*  715 */         serialiseContact(os, contacts[i]);
/*      */       }
/*      */       catch (DHTTransportException e)
/*      */       {
/*  719 */         Debug.printStackTrace(e);
/*      */         
/*      */ 
/*      */ 
/*  723 */         throw new IOException(e.getMessage());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportContact[] deserialiseContacts(DHTTransportUDPImpl transport, DataInputStream is)
/*      */     throws IOException
/*      */   {
/*  735 */     int len = deserialiseLength(is, 65535);
/*      */     
/*  737 */     List<DHTTransportContact> l = new ArrayList(len);
/*      */     
/*  739 */     for (int i = 0; i < len; i++)
/*      */     {
/*      */       try
/*      */       {
/*  743 */         DHTTransportContact contact = deserialiseContact(transport, is);
/*      */         
/*  745 */         if (contact.getAddress().getPort() > 0)
/*      */         {
/*  747 */           l.add(contact);
/*      */         }
/*      */       }
/*      */       catch (DHTTransportException e)
/*      */       {
/*  752 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  756 */     DHTTransportContact[] res = new DHTTransportContact[l.size()];
/*      */     
/*  758 */     l.toArray(res);
/*      */     
/*  760 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseAltContacts(DataOutputStream os, DHTTransportAlternativeContact[] contacts)
/*      */     throws IOException
/*      */   {
/*  770 */     if (contacts == null)
/*      */     {
/*  772 */       contacts = new DHTTransportAlternativeContact[0];
/*      */     }
/*      */     
/*  775 */     serialiseLength(os, contacts.length, 64);
/*      */     
/*  777 */     for (int i = 0; i < contacts.length; i++) {
/*      */       try
/*      */       {
/*  780 */         serialiseAltContact(os, contacts[i]);
/*      */       }
/*      */       catch (DHTTransportException e)
/*      */       {
/*  784 */         Debug.printStackTrace(e);
/*      */         
/*      */ 
/*      */ 
/*  788 */         throw new IOException(e.getMessage());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportAlternativeContact[] deserialiseAltContacts(DataInputStream is)
/*      */     throws IOException
/*      */   {
/*  799 */     int len = deserialiseLength(is, 64);
/*      */     
/*  801 */     List<DHTTransportAlternativeContact> l = new ArrayList(len);
/*      */     
/*  803 */     for (int i = 0; i < len; i++)
/*      */     {
/*      */       try
/*      */       {
/*  807 */         DHTTransportAlternativeContact contact = deserialiseAltContact(is);
/*      */         
/*  809 */         l.add(contact);
/*      */       }
/*      */       catch (DHTTransportException e)
/*      */       {
/*  813 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  817 */     DHTTransportAlternativeContact[] res = new DHTTransportAlternativeContact[l.size()];
/*      */     
/*  819 */     l.toArray(res);
/*      */     
/*  821 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseAltContactRequest(DHTUDPPacketRequestPing ping, DataOutputStream os)
/*      */     throws IOException
/*      */   {
/*  831 */     int[] nets = ping.getAltNetworks();
/*  832 */     int[] counts = ping.getAltNetworkCounts();
/*      */     
/*  834 */     int len = (nets == null) || (counts == null) ? 0 : nets.length;
/*      */     
/*  836 */     serialiseLength(os, len, 16);
/*      */     
/*  838 */     for (int i = 0; i < len; i++)
/*      */     {
/*  840 */       os.write(nets[i]);
/*  841 */       os.write(counts[i]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void deserialiseAltContactRequest(DHTUDPPacketRequestPing ping, DataInputStream is)
/*      */     throws IOException
/*      */   {
/*  852 */     int len = deserialiseLength(is, 16);
/*      */     
/*  854 */     int[] nets = new int[len];
/*  855 */     int[] counts = new int[len];
/*      */     
/*  857 */     for (int i = 0; i < len; i++)
/*      */     {
/*  859 */       nets[i] = is.read();
/*  860 */       counts[i] = is.read();
/*      */       
/*  862 */       if ((nets[i] == -1) || (counts[i] == -1))
/*      */       {
/*  864 */         throw new EOFException();
/*      */       }
/*      */     }
/*      */     
/*  868 */     ping.setAltContactRequest(nets, counts);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseVivaldi(DHTUDPPacketReply reply, DataOutputStream os)
/*      */     throws IOException
/*      */   {
/*  878 */     DHTNetworkPosition[] nps = reply.getNetworkPositions();
/*      */     
/*  880 */     if (reply.getProtocolVersion() >= 15)
/*      */     {
/*  882 */       boolean v1_found = false;
/*      */       
/*  884 */       for (int i = 0; i < nps.length; i++)
/*      */       {
/*  886 */         DHTNetworkPosition np = nps[i];
/*      */         
/*  888 */         if (np.getPositionType() == 1)
/*      */         {
/*  890 */           v1_found = true;
/*      */           
/*  892 */           break;
/*      */         }
/*      */       }
/*      */       
/*  896 */       if (!v1_found)
/*      */       {
/*  898 */         if (reply.getProtocolVersion() < 51)
/*      */         {
/*      */ 
/*      */ 
/*  902 */           DHTNetworkPosition np = VivaldiPositionFactory.createPosition(NaN.0F);
/*      */           
/*  904 */           DHTNetworkPosition[] new_nps = new DHTNetworkPosition[nps.length + 1];
/*      */           
/*  906 */           System.arraycopy(nps, 0, new_nps, 0, nps.length);
/*      */           
/*  908 */           new_nps[nps.length] = np;
/*      */           
/*  910 */           nps = new_nps;
/*      */         }
/*      */       }
/*      */       
/*  914 */       os.writeByte((byte)nps.length);
/*      */       
/*  916 */       for (int i = 0; i < nps.length; i++)
/*      */       {
/*  918 */         DHTNetworkPosition np = nps[i];
/*      */         
/*  920 */         os.writeByte(np.getPositionType());
/*  921 */         os.writeByte(np.getSerialisedSize());
/*      */         
/*  923 */         np.serialise(os);
/*      */       }
/*      */       
/*      */     }
/*      */     else
/*      */     {
/*  929 */       for (int i = 0; i < nps.length; i++)
/*      */       {
/*  931 */         if (nps[i].getPositionType() == 1)
/*      */         {
/*  933 */           nps[i].serialise(os);
/*      */           
/*  935 */           return;
/*      */         }
/*      */       }
/*      */       
/*  939 */       Debug.out("Vivaldi V1 missing");
/*      */       
/*  941 */       throw new IOException("Vivaldi V1 missing");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void deserialiseVivaldi(DHTUDPPacketReply reply, DataInputStream is)
/*      */     throws IOException
/*      */   {
/*      */     DHTNetworkPosition[] nps;
/*      */     
/*      */ 
/*  954 */     if (reply.getProtocolVersion() >= 15)
/*      */     {
/*  956 */       int entries = is.readByte() & 0xFF;
/*      */       
/*  958 */       DHTNetworkPosition[] nps = new DHTNetworkPosition[entries];
/*      */       
/*  960 */       int skipped = 0;
/*      */       
/*  962 */       for (int i = 0; i < entries; i++)
/*      */       {
/*  964 */         byte type = is.readByte();
/*  965 */         byte size = is.readByte();
/*      */         
/*  967 */         DHTNetworkPosition np = DHTNetworkPositionManager.deserialise(reply.getAddress().getAddress(), type, is);
/*      */         
/*  969 */         if (np == null)
/*      */         {
/*  971 */           skipped++;
/*      */           
/*  973 */           for (int j = 0; j < size; j++)
/*      */           {
/*  975 */             is.readByte();
/*      */           }
/*      */         }
/*      */         else {
/*  979 */           nps[i] = np;
/*      */         }
/*      */       }
/*      */       
/*  983 */       if (skipped > 0)
/*      */       {
/*  985 */         DHTNetworkPosition[] x = new DHTNetworkPosition[entries - skipped];
/*      */         
/*  987 */         int pos = 0;
/*      */         
/*  989 */         for (int i = 0; i < nps.length; i++)
/*      */         {
/*  991 */           if (nps[i] != null)
/*      */           {
/*  993 */             x[(pos++)] = nps[i];
/*      */           }
/*      */         }
/*      */         
/*  997 */         nps = x;
/*      */       }
/*      */       
/*      */     }
/*      */     else
/*      */     {
/* 1003 */       nps = new DHTNetworkPosition[] { DHTNetworkPositionManager.deserialise(reply.getAddress().getAddress(), 1, is) };
/*      */     }
/*      */     
/* 1006 */     reply.setNetworkPositions(nps);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void serialiseStats(int version, DataOutputStream os, DHTTransportFullStats stats)
/*      */     throws IOException
/*      */   {
/* 1017 */     os.writeLong(stats.getDBValuesStored());
/*      */     
/* 1019 */     os.writeLong(stats.getRouterNodes());
/* 1020 */     os.writeLong(stats.getRouterLeaves());
/* 1021 */     os.writeLong(stats.getRouterContacts());
/*      */     
/* 1023 */     os.writeLong(stats.getTotalBytesReceived());
/* 1024 */     os.writeLong(stats.getTotalBytesSent());
/* 1025 */     os.writeLong(stats.getTotalPacketsReceived());
/* 1026 */     os.writeLong(stats.getTotalPacketsSent());
/* 1027 */     os.writeLong(stats.getTotalPingsReceived());
/* 1028 */     os.writeLong(stats.getTotalFindNodesReceived());
/* 1029 */     os.writeLong(stats.getTotalFindValuesReceived());
/* 1030 */     os.writeLong(stats.getTotalStoresReceived());
/* 1031 */     os.writeLong(stats.getAverageBytesReceived());
/* 1032 */     os.writeLong(stats.getAverageBytesSent());
/* 1033 */     os.writeLong(stats.getAveragePacketsReceived());
/* 1034 */     os.writeLong(stats.getAveragePacketsSent());
/*      */     
/* 1036 */     os.writeLong(stats.getIncomingRequests());
/*      */     
/* 1038 */     String azversion = stats.getVersion() + "[" + version + "]";
/*      */     
/* 1040 */     serialiseByteArray(os, azversion.getBytes(), 64);
/*      */     
/* 1042 */     os.writeLong(stats.getRouterUptime());
/* 1043 */     os.writeInt(stats.getRouterCount());
/*      */     
/* 1045 */     if (version >= 14)
/*      */     {
/* 1047 */       os.writeLong(stats.getDBKeysBlocked());
/* 1048 */       os.writeLong(stats.getTotalKeyBlocksReceived());
/*      */     }
/*      */     
/* 1051 */     if (version >= 20)
/*      */     {
/* 1053 */       os.writeLong(stats.getDBKeyCount());
/* 1054 */       os.writeLong(stats.getDBValueCount());
/* 1055 */       os.writeLong(stats.getDBStoreSize());
/* 1056 */       os.writeLong(stats.getDBKeyDivFreqCount());
/* 1057 */       os.writeLong(stats.getDBKeyDivSizeCount());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DHTTransportFullStats deserialiseStats(int version, DataInputStream is)
/*      */     throws IOException
/*      */   {
/* 1068 */     long db_values_stored = is.readLong();
/*      */     
/* 1070 */     long router_nodes = is.readLong();
/* 1071 */     final long router_leaves = is.readLong();
/* 1072 */     long router_contacts = is.readLong();
/*      */     
/* 1074 */     final long total_bytes_received = is.readLong();
/* 1075 */     long total_bytes_sent = is.readLong();
/* 1076 */     final long total_packets_received = is.readLong();
/* 1077 */     long total_packets_sent = is.readLong();
/* 1078 */     final long total_pings_received = is.readLong();
/* 1079 */     long total_find_nodes_received = is.readLong();
/* 1080 */     final long total_find_values_received = is.readLong();
/* 1081 */     long total_stores_received = is.readLong();
/* 1082 */     long average_bytes_received = is.readLong();
/* 1083 */     final long average_bytes_sent = is.readLong();
/* 1084 */     final long average_packets_received = is.readLong();
/* 1085 */     long average_packets_sent = is.readLong();
/*      */     
/* 1087 */     final long incoming_requests = is.readLong();
/*      */     
/* 1089 */     String az_version = new String(deserialiseByteArray(is, 64));
/*      */     
/* 1091 */     final long router_uptime = is.readLong();
/* 1092 */     int router_count = is.readInt();
/*      */     
/*      */     long total_key_blocks_received;
/*      */     long db_keys_blocked;
/*      */     final long total_key_blocks_received;
/* 1097 */     if (version >= 14)
/*      */     {
/* 1099 */       long db_keys_blocked = is.readLong();
/* 1100 */       total_key_blocks_received = is.readLong();
/*      */     } else {
/* 1102 */       db_keys_blocked = -1L;
/* 1103 */       total_key_blocks_received = -1L;
/*      */     }
/*      */     
/*      */     long db_size_divs;
/*      */     long db_key_count;
/*      */     final long db_value_count;
/*      */     final long db_store_size;
/*      */     long db_freq_divs;
/*      */     final long db_size_divs;
/* 1112 */     if (version >= 20)
/*      */     {
/* 1114 */       long db_key_count = is.readLong();
/* 1115 */       long db_value_count = is.readLong();
/* 1116 */       long db_store_size = is.readLong();
/* 1117 */       long db_freq_divs = is.readLong();
/* 1118 */       db_size_divs = is.readLong();
/*      */     }
/*      */     else
/*      */     {
/* 1122 */       db_key_count = -1L;
/* 1123 */       db_value_count = -1L;
/* 1124 */       db_store_size = -1L;
/* 1125 */       db_freq_divs = -1L;
/* 1126 */       db_size_divs = -1L;
/*      */     }
/*      */     
/* 1129 */     DHTTransportFullStats res = new DHTTransportFullStats()
/*      */     {
/*      */ 
/*      */       public long getDBValuesStored()
/*      */       {
/*      */ 
/* 1135 */         return this.val$db_values_stored;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getDBKeysBlocked()
/*      */       {
/* 1141 */         return db_value_count;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getDBValueCount()
/*      */       {
/* 1147 */         return db_size_divs;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getDBKeyCount()
/*      */       {
/* 1153 */         return db_store_size;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getDBKeyDivSizeCount()
/*      */       {
/* 1159 */         return router_leaves;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getDBKeyDivFreqCount()
/*      */       {
/* 1165 */         return router_uptime;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getDBStoreSize()
/*      */       {
/* 1171 */         return total_bytes_received;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public long getRouterNodes()
/*      */       {
/* 1179 */         return total_packets_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getRouterLeaves()
/*      */       {
/* 1185 */         return total_pings_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getRouterContacts()
/*      */       {
/* 1191 */         return total_find_values_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getRouterUptime()
/*      */       {
/* 1197 */         return total_key_blocks_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getRouterCount()
/*      */       {
/* 1203 */         return average_bytes_sent;
/*      */       }
/*      */       
/*      */       public long getTotalBytesReceived()
/*      */       {
/* 1208 */         return average_packets_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalBytesSent()
/*      */       {
/* 1214 */         return incoming_requests;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalPacketsReceived()
/*      */       {
/* 1220 */         return this.val$total_packets_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalPacketsSent()
/*      */       {
/* 1226 */         return this.val$total_packets_sent;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalPingsReceived()
/*      */       {
/* 1232 */         return this.val$total_pings_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalFindNodesReceived()
/*      */       {
/* 1238 */         return this.val$total_find_nodes_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalFindValuesReceived()
/*      */       {
/* 1244 */         return this.val$total_find_values_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalStoresReceived()
/*      */       {
/* 1250 */         return this.val$total_stores_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalKeyBlocksReceived()
/*      */       {
/* 1256 */         return this.val$total_key_blocks_received;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public long getAverageBytesReceived()
/*      */       {
/* 1264 */         return this.val$average_bytes_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getAverageBytesSent()
/*      */       {
/* 1270 */         return this.val$average_bytes_sent;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getAveragePacketsReceived()
/*      */       {
/* 1276 */         return this.val$average_packets_received;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getAveragePacketsSent()
/*      */       {
/* 1282 */         return this.val$average_packets_sent;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getIncomingRequests()
/*      */       {
/* 1288 */         return this.val$incoming_requests;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getVersion()
/*      */       {
/* 1294 */         return this.val$az_version;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1300 */         return "transport:" + getTotalBytesReceived() + "," + getTotalBytesSent() + "," + getTotalPacketsReceived() + "," + getTotalPacketsSent() + "," + getTotalPingsReceived() + "," + getTotalFindNodesReceived() + "," + getTotalFindValuesReceived() + "," + getTotalStoresReceived() + "," + getTotalKeyBlocksReceived() + "," + getAverageBytesReceived() + "," + getAverageBytesSent() + "," + getAveragePacketsReceived() + "," + getAveragePacketsSent() + "," + getIncomingRequests() + ",router:" + getRouterNodes() + "," + getRouterLeaves() + "," + getRouterContacts() + ",database:" + getDBKeyCount() + "," + getDBValueCount() + "," + getDBValuesStored() + "," + getDBStoreSize() + "," + getDBKeyDivFreqCount() + "," + getDBKeyDivSizeCount() + "," + getDBKeysBlocked() + ",version:" + getVersion() + "," + getRouterUptime() + "," + getRouterCount();
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
/*      */       }
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
/* 1333 */     };
/* 1334 */     return res;
/*      */   }
/*      */   
/* 1337 */   private static final List<DHTTransportUDPImpl> transports = new ArrayList();
/* 1338 */   private static final List<DHTTransportAlternativeNetwork> alt_networks = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */   protected static void registerTransport(DHTTransportUDPImpl transport)
/*      */   {
/* 1344 */     synchronized (transports)
/*      */     {
/* 1346 */       transports.add(transport);
/*      */       
/* 1348 */       for (DHTTransportAlternativeNetwork net : alt_networks)
/*      */       {
/* 1350 */         transport.registerAlternativeNetwork(net);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void registerAlternativeNetwork(DHTTransportAlternativeNetwork net)
/*      */   {
/* 1359 */     synchronized (transports)
/*      */     {
/* 1361 */       alt_networks.add(net);
/*      */       
/* 1363 */       for (DHTTransportUDPImpl transport : transports)
/*      */       {
/* 1365 */         transport.registerAlternativeNetwork(net);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void unregisterAlternativeNetwork(DHTTransportAlternativeNetwork net)
/*      */   {
/* 1374 */     synchronized (transports)
/*      */     {
/* 1376 */       alt_networks.remove(net);
/*      */       
/* 1378 */       for (DHTTransportUDPImpl transport : transports)
/*      */       {
/* 1380 */         transport.unregisterAlternativeNetwork(net);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<DHTTransportAlternativeContact> getAlternativeContacts(int network, int max)
/*      */   {
/* 1390 */     List<DHTTransportAlternativeContact> result_list = new ArrayList(max);
/*      */     
/* 1392 */     if (max > 0)
/*      */     {
/* 1394 */       TreeSet<DHTTransportAlternativeContact> result_set = new TreeSet(new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(DHTTransportAlternativeContact o1, DHTTransportAlternativeContact o2)
/*      */         {
/*      */ 
/*      */ 
/* 1403 */           int res = o1.getAge() - o2.getAge();
/*      */           
/* 1405 */           if (res == 0)
/*      */           {
/* 1407 */             res = o1.getID() - o2.getID();
/*      */           }
/*      */           
/* 1410 */           return res;
/*      */         }
/*      */       });
/*      */       
/* 1414 */       synchronized (transports)
/*      */       {
/*      */ 
/*      */ 
/* 1418 */         for (DHTTransportAlternativeNetwork net : alt_networks)
/*      */         {
/* 1420 */           if (net.getNetworkType() == network)
/*      */           {
/* 1422 */             List<DHTTransportAlternativeContact> temp = net.getContacts(max);
/*      */             
/* 1424 */             if (temp != null)
/*      */             {
/* 1426 */               result_set.addAll(temp);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1433 */         for (DHTTransportUDPImpl transport : transports)
/*      */         {
/* 1435 */           DHTTransportAlternativeNetwork alt = transport.getAlternativeNetwork(network);
/*      */           
/* 1437 */           if (alt != null)
/*      */           {
/* 1439 */             List<DHTTransportAlternativeContact> temp = alt.getContacts(max);
/*      */             
/* 1441 */             if (temp != null)
/*      */             {
/* 1443 */               result_set.addAll(temp);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1449 */       Iterator<DHTTransportAlternativeContact> it = result_set.iterator();
/*      */       
/* 1451 */       while ((it.hasNext()) && (result_list.size() < max))
/*      */       {
/* 1453 */         result_list.add(it.next());
/*      */       }
/*      */     }
/*      */     
/* 1457 */     return result_list;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */