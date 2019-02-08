/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import com.aelitis.azureus.core.util.DNSUtils;
/*     */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkAdminASNLookupImpl
/*     */ {
/*     */   private static final String WHOIS_ADDRESS = "whois.cymru.com";
/*     */   private static final int WHOIS_PORT = 43;
/*     */   private static final int TIMEOUT = 30000;
/*     */   private final InetAddress address;
/*     */   
/*     */   protected NetworkAdminASNLookupImpl(InetAddress _address)
/*     */   {
/*  56 */     this.address = _address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetworkAdminASNImpl lookup()
/*     */     throws NetworkAdminException
/*     */   {
/*  66 */     return lookupDNS(this.address);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected NetworkAdminASNImpl lookupTCP(InetAddress address)
/*     */     throws NetworkAdminException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 186	java/net/Socket
/*     */     //   3: dup
/*     */     //   4: invokespecial 327	java/net/Socket:<init>	()V
/*     */     //   7: astore_2
/*     */     //   8: sipush 30000
/*     */     //   11: istore_3
/*     */     //   12: invokestatic 339	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*     */     //   15: lstore 4
/*     */     //   17: aload_2
/*     */     //   18: new 185	java/net/InetSocketAddress
/*     */     //   21: dup
/*     */     //   22: ldc 22
/*     */     //   24: bipush 43
/*     */     //   26: invokespecial 326	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
/*     */     //   29: iload_3
/*     */     //   30: invokevirtual 332	java/net/Socket:connect	(Ljava/net/SocketAddress;I)V
/*     */     //   33: invokestatic 339	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*     */     //   36: lstore 6
/*     */     //   38: iload_3
/*     */     //   39: i2l
/*     */     //   40: lload 6
/*     */     //   42: lload 4
/*     */     //   44: lsub
/*     */     //   45: lsub
/*     */     //   46: l2i
/*     */     //   47: istore_3
/*     */     //   48: iload_3
/*     */     //   49: ifgt +13 -> 62
/*     */     //   52: new 171	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*     */     //   55: dup
/*     */     //   56: ldc 16
/*     */     //   58: invokespecial 292	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException:<init>	(Ljava/lang/String;)V
/*     */     //   61: athrow
/*     */     //   62: iload_3
/*     */     //   63: sipush 30000
/*     */     //   66: if_icmple +7 -> 73
/*     */     //   69: sipush 30000
/*     */     //   72: istore_3
/*     */     //   73: aload_2
/*     */     //   74: iload_3
/*     */     //   75: invokevirtual 329	java/net/Socket:setSoTimeout	(I)V
/*     */     //   78: aload_2
/*     */     //   79: invokevirtual 331	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
/*     */     //   82: astore 8
/*     */     //   84: new 181	java/lang/StringBuilder
/*     */     //   87: dup
/*     */     //   88: invokespecial 317	java/lang/StringBuilder:<init>	()V
/*     */     //   91: ldc 7
/*     */     //   93: invokevirtual 320	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   96: aload_1
/*     */     //   97: invokevirtual 324	java/net/InetAddress:getHostAddress	()Ljava/lang/String;
/*     */     //   100: invokevirtual 320	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   103: ldc 3
/*     */     //   105: invokevirtual 320	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   108: invokevirtual 318	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   111: astore 9
/*     */     //   113: aload 8
/*     */     //   115: aload 9
/*     */     //   117: invokevirtual 310	java/lang/String:getBytes	()[B
/*     */     //   120: invokevirtual 306	java/io/OutputStream:write	([B)V
/*     */     //   123: aload 8
/*     */     //   125: invokevirtual 305	java/io/OutputStream:flush	()V
/*     */     //   128: aload_2
/*     */     //   129: invokevirtual 330	java/net/Socket:getInputStream	()Ljava/io/InputStream;
/*     */     //   132: astore 10
/*     */     //   134: sipush 1024
/*     */     //   137: newarray <illegal type>
/*     */     //   139: astore 11
/*     */     //   141: ldc 1
/*     */     //   143: astore 12
/*     */     //   145: aload 10
/*     */     //   147: aload 11
/*     */     //   149: invokevirtual 304	java/io/InputStream:read	([B)I
/*     */     //   152: istore 13
/*     */     //   154: iload 13
/*     */     //   156: ifgt +6 -> 162
/*     */     //   159: goto +38 -> 197
/*     */     //   162: new 181	java/lang/StringBuilder
/*     */     //   165: dup
/*     */     //   166: invokespecial 317	java/lang/StringBuilder:<init>	()V
/*     */     //   169: aload 12
/*     */     //   171: invokevirtual 320	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   174: new 180	java/lang/String
/*     */     //   177: dup
/*     */     //   178: aload 11
/*     */     //   180: iconst_0
/*     */     //   181: iload 13
/*     */     //   183: invokespecial 313	java/lang/String:<init>	([BII)V
/*     */     //   186: invokevirtual 320	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   189: invokevirtual 318	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   192: astore 12
/*     */     //   194: goto -49 -> 145
/*     */     //   197: aload_0
/*     */     //   198: aload 12
/*     */     //   200: invokevirtual 300	com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminASNLookupImpl:processResult	(Ljava/lang/String;)Lcom/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminASNImpl;
/*     */     //   203: astore 13
/*     */     //   205: aload_2
/*     */     //   206: invokevirtual 328	java/net/Socket:close	()V
/*     */     //   209: aload 13
/*     */     //   211: areturn
/*     */     //   212: astore 14
/*     */     //   214: aload_2
/*     */     //   215: invokevirtual 328	java/net/Socket:close	()V
/*     */     //   218: aload 14
/*     */     //   220: athrow
/*     */     //   221: astore_2
/*     */     //   222: new 171	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException
/*     */     //   225: dup
/*     */     //   226: ldc 21
/*     */     //   228: aload_2
/*     */     //   229: invokespecial 293	com/aelitis/azureus/core/networkmanager/admin/NetworkAdminException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   232: athrow
/*     */     // Line number table:
/*     */     //   Java source line #76	-> byte code offset #0
/*     */     //   Java source line #78	-> byte code offset #8
/*     */     //   Java source line #80	-> byte code offset #12
/*     */     //   Java source line #82	-> byte code offset #17
/*     */     //   Java source line #84	-> byte code offset #33
/*     */     //   Java source line #86	-> byte code offset #38
/*     */     //   Java source line #88	-> byte code offset #48
/*     */     //   Java source line #90	-> byte code offset #52
/*     */     //   Java source line #92	-> byte code offset #62
/*     */     //   Java source line #94	-> byte code offset #69
/*     */     //   Java source line #97	-> byte code offset #73
/*     */     //   Java source line #100	-> byte code offset #78
/*     */     //   Java source line #102	-> byte code offset #84
/*     */     //   Java source line #104	-> byte code offset #113
/*     */     //   Java source line #106	-> byte code offset #123
/*     */     //   Java source line #108	-> byte code offset #128
/*     */     //   Java source line #110	-> byte code offset #134
/*     */     //   Java source line #112	-> byte code offset #141
/*     */     //   Java source line #116	-> byte code offset #145
/*     */     //   Java source line #118	-> byte code offset #154
/*     */     //   Java source line #120	-> byte code offset #159
/*     */     //   Java source line #123	-> byte code offset #162
/*     */     //   Java source line #124	-> byte code offset #194
/*     */     //   Java source line #126	-> byte code offset #197
/*     */     //   Java source line #130	-> byte code offset #205
/*     */     //   Java source line #132	-> byte code offset #221
/*     */     //   Java source line #134	-> byte code offset #222
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	233	0	this	NetworkAdminASNLookupImpl
/*     */     //   0	233	1	address	InetAddress
/*     */     //   7	208	2	socket	java.net.Socket
/*     */     //   221	8	2	e	Throwable
/*     */     //   11	64	3	timeout	int
/*     */     //   15	28	4	start	long
/*     */     //   36	5	6	end	long
/*     */     //   82	42	8	os	java.io.OutputStream
/*     */     //   111	5	9	command	String
/*     */     //   132	14	10	is	java.io.InputStream
/*     */     //   139	40	11	buffer	byte[]
/*     */     //   143	56	12	result	String
/*     */     //   152	58	13	len	int
/*     */     //   212	7	14	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   78	205	212	finally
/*     */     //   212	214	212	finally
/*     */     //   0	209	221	java/lang/Throwable
/*     */     //   212	221	221	java/lang/Throwable
/*     */   }
/*     */   
/*     */   protected NetworkAdminASNImpl lookupDNS(InetAddress address)
/*     */     throws NetworkAdminException
/*     */   {
/* 146 */     byte[] bytes = address.getAddress();
/*     */     
/* 148 */     String ip_query = "origin.asn.cymru.com";
/*     */     
/* 150 */     for (int i = 0; i < 4; i++)
/*     */     {
/* 152 */       ip_query = (bytes[i] & 0xFF) + "." + ip_query;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 157 */     String ip_result = lookupDNS(ip_query);
/*     */     
/* 159 */     NetworkAdminASNImpl result = processResult("AS | BGP Prefix | CC | Reg | Date | AS Name\n" + ip_result + " | n/a");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 164 */     String as = result.getAS();
/*     */     
/* 166 */     if (as.length() > 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */       String asn_query = "AS" + as + ".asn.cymru.com";
/*     */       
/*     */       try
/*     */       {
/* 176 */         String asn_result = lookupDNS(asn_query);
/*     */         
/* 178 */         if (asn_result != null)
/*     */         {
/* 180 */           int pos = asn_result.lastIndexOf('|');
/*     */           
/* 182 */           if (pos != -1)
/*     */           {
/* 184 */             String asn = asn_result.substring(pos + 1).trim();
/*     */             
/* 186 */             result.setASName(asn);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 191 */         Debug.outNoStack("ASN lookup for '" + asn_query + "' failed: " + e.getMessage());
/*     */       }
/*     */     }
/*     */     
/* 195 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String lookupDNS(String query)
/*     */     throws NetworkAdminException
/*     */   {
/* 204 */     DNSUtils.DNSUtilsIntf dns_utils = DNSUtils.getSingleton();
/*     */     
/* 206 */     if (dns_utils == null)
/*     */     {
/* 208 */       throw new NetworkAdminException("DNS lookup unavailable");
/*     */     }
/*     */     try
/*     */     {
/* 212 */       return dns_utils.getTXTRecord(query);
/*     */     }
/*     */     catch (UnknownHostException e)
/*     */     {
/* 216 */       throw new NetworkAdminException("Query failed for '" + query + "'", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetworkAdminASNImpl processResult(String result)
/*     */   {
/* 225 */     StringTokenizer lines = new StringTokenizer(result, "\n");
/*     */     
/* 227 */     int line_number = 0;
/*     */     
/* 229 */     List keywords = new ArrayList();
/*     */     
/* 231 */     Map map = new HashMap();
/*     */     
/* 233 */     while (lines.hasMoreTokens())
/*     */     {
/* 235 */       String line = lines.nextToken().trim();
/*     */       
/* 237 */       line_number++;
/*     */       
/* 239 */       if (line_number > 2) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 244 */       StringTokenizer tok = new StringTokenizer(line, "|");
/*     */       
/* 246 */       int token_number = 0;
/*     */       
/* 248 */       while (tok.hasMoreTokens())
/*     */       {
/* 250 */         String token = tok.nextToken().trim();
/*     */         
/* 252 */         if (line_number == 1)
/*     */         {
/* 254 */           keywords.add(token.toLowerCase(MessageText.LOCALE_ENGLISH));
/*     */         }
/*     */         else
/*     */         {
/* 258 */           if (token_number >= keywords.size()) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 264 */           String kw = (String)keywords.get(token_number);
/*     */           
/* 266 */           map.put(kw, token);
/*     */         }
/*     */         
/*     */ 
/* 270 */         token_number++;
/*     */       }
/*     */     }
/*     */     
/* 274 */     String as = (String)map.get("as");
/* 275 */     String asn = (String)map.get("as name");
/* 276 */     String bgp_prefix = (String)map.get("bgp prefix");
/*     */     
/* 278 */     if (bgp_prefix != null)
/*     */     {
/* 280 */       int pos = bgp_prefix.indexOf(' ');
/*     */       
/* 282 */       if (pos != -1)
/*     */       {
/* 284 */         bgp_prefix = bgp_prefix.substring(pos + 1).trim();
/*     */       }
/*     */       
/* 287 */       if (bgp_prefix.indexOf('/') == -1)
/*     */       {
/* 289 */         bgp_prefix = null;
/*     */       }
/*     */     }
/*     */     
/* 293 */     return new NetworkAdminASNImpl(as, asn, bgp_prefix);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 304 */       NetworkAdminASNLookupImpl lookup = new NetworkAdminASNLookupImpl(InetAddress.getByName("64.71.8.82"));
/*     */       
/* 306 */       System.out.println(lookup.lookup().getString());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 316 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminASNLookupImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */