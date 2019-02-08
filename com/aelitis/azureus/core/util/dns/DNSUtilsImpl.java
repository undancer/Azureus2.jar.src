/*     */ package com.aelitis.azureus.core.util.dns;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.DNSUtils.DNSDirContext;
/*     */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.naming.NamingEnumeration;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attribute;
/*     */ import javax.naming.directory.Attributes;
/*     */ import javax.naming.directory.DirContext;
/*     */ import javax.naming.directory.InitialDirContext;
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
/*     */ public class DNSUtilsImpl
/*     */   implements DNSUtils.DNSUtilsIntf
/*     */ {
/*     */   private static String getFactory()
/*     */   {
/*  52 */     return System.getProperty("azureus.dns.context.factory", "com.sun.jndi.dns.DnsContextFactory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DNSDirContextImpl getInitialDirContext()
/*     */     throws NamingException
/*     */   {
/*  60 */     Hashtable env = new Hashtable();
/*     */     
/*  62 */     env.put("java.naming.factory.initial", getFactory());
/*     */     
/*  64 */     return new DNSDirContextImpl(new InitialDirContext(env), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DNSDirContextImpl getDirContextForServer(String dns_server_ip)
/*     */     throws NamingException
/*     */   {
/*  74 */     Hashtable env = new Hashtable();
/*     */     
/*  76 */     env.put("java.naming.factory.initial", getFactory());
/*     */     
/*  78 */     env.put("java.naming.provider.url", "dns://" + dns_server_ip + "/");
/*     */     
/*  80 */     return new DNSDirContextImpl(new InitialDirContext(env), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Inet6Address getIPV6ByName(String host)
/*     */     throws UnknownHostException
/*     */   {
/*  89 */     List<Inet6Address> all = getAllIPV6ByName(host);
/*     */     
/*  91 */     return (Inet6Address)all.get(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<Inet6Address> getAllIPV6ByName(String host)
/*     */     throws UnknownHostException
/*     */   {
/* 100 */     List<Inet6Address> result = new ArrayList();
/*     */     try
/*     */     {
/* 103 */       DirContext context = getInitialDirContext().ctx;
/*     */       
/* 105 */       Attributes attrs = context.getAttributes(host, new String[] { "AAAA" });
/*     */       
/* 107 */       if (attrs != null)
/*     */       {
/* 109 */         Attribute attr = attrs.get("aaaa");
/*     */         
/* 111 */         if (attr != null)
/*     */         {
/* 113 */           NamingEnumeration values = attr.getAll();
/*     */           
/* 115 */           while (values.hasMore())
/*     */           {
/* 117 */             Object value = values.next();
/*     */             
/* 119 */             if ((value instanceof String)) {
/*     */               try
/*     */               {
/* 122 */                 result.add((Inet6Address)InetAddress.getByName((String)value));
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 133 */     if (result.size() > 0)
/*     */     {
/* 135 */       return result;
/*     */     }
/*     */     
/* 138 */     throw new UnknownHostException(host);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<InetAddress> getAllByName(String host)
/*     */     throws UnknownHostException
/*     */   {
/*     */     try
/*     */     {
/* 148 */       return getAllByName(getInitialDirContext(), host);
/*     */     }
/*     */     catch (NamingException e)
/*     */     {
/* 152 */       throw new UnknownHostException(host);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<InetAddress> getAllByName(DNSUtils.DNSDirContext context, String host)
/*     */     throws UnknownHostException
/*     */   {
/* 163 */     System.out.println("Lookup for " + host);
/*     */     
/* 165 */     List<InetAddress> result = new ArrayList();
/*     */     try
/*     */     {
/* 168 */       String[] attributes = { "A", "AAAA" };
/*     */       
/* 170 */       Attributes attrs = ((DNSDirContextImpl)context).ctx.getAttributes(host, attributes);
/*     */       
/* 172 */       if (attrs != null)
/*     */       {
/* 174 */         for (String a : attributes)
/*     */         {
/* 176 */           Attribute attr = attrs.get(a);
/*     */           
/* 178 */           if (attr != null)
/*     */           {
/* 180 */             NamingEnumeration values = attr.getAll();
/*     */             
/* 182 */             while (values.hasMore())
/*     */             {
/* 184 */               Object value = values.next();
/*     */               
/* 186 */               if ((value instanceof String)) {
/*     */                 try
/*     */                 {
/* 189 */                   result.add(InetAddress.getByName((String)value));
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 201 */     if (result.size() > 0)
/*     */     {
/* 203 */       return result;
/*     */     }
/*     */     
/* 206 */     throw new UnknownHostException(host);
/*     */   }
/*     */   
/* 209 */   private static final Map<String, String> test_records = new HashMap();
/*     */   
/*     */   static {
/* 212 */     test_records.put("test1.test.null", "BITTORRENT DENY ALL");
/* 213 */     test_records.put("test2.test.null", "BITTORRENT");
/* 214 */     test_records.put("test3.test.null", "BITTORRENT TCP:1 TCP:2 UDP:1 UDP:2");
/* 215 */     test_records.put("test4.test.null", "BITTORRENT TCP:3");
/* 216 */     test_records.put("test5.test.null", "BITTORRENT UDP:4");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> getTXTRecords(String query)
/*     */   {
/* 225 */     result = new ArrayList();
/*     */     
/* 227 */     String test_reply = (String)test_records.get(query);
/*     */     
/* 229 */     if (test_reply != null)
/*     */     {
/* 231 */       result.add(test_reply);
/*     */       
/* 233 */       return result;
/*     */     }
/*     */     
/* 236 */     DirContext context = null;
/*     */     try
/*     */     {
/* 239 */       context = getInitialDirContext().ctx;
/*     */       
/* 241 */       Attributes attrs = context.getAttributes(query, new String[] { "TXT" });
/*     */       
/* 243 */       NamingEnumeration n_enum = attrs.getAll();
/*     */       
/* 245 */       while (n_enum.hasMoreElements())
/*     */       {
/* 247 */         Attribute attr = (Attribute)n_enum.next();
/*     */         
/* 249 */         NamingEnumeration n_enum2 = attr.getAll();
/*     */         
/* 251 */         while (n_enum2.hasMoreElements())
/*     */         {
/* 253 */           String attribute = (String)n_enum2.nextElement();
/*     */           
/* 255 */           if (attribute != null)
/*     */           {
/* 257 */             attribute = attribute.trim();
/*     */             
/* 259 */             if (attribute.startsWith("\""))
/*     */             {
/* 261 */               attribute = attribute.substring(1);
/*     */             }
/*     */             
/* 264 */             if (attribute.endsWith("\""))
/*     */             {
/* 266 */               attribute = attribute.substring(0, attribute.length() - 1);
/*     */             }
/*     */             
/* 269 */             if (attribute.length() > 0)
/*     */             {
/* 271 */               result.add(attribute);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 293 */       return result;
/*     */     }
/*     */     catch (Throwable e) {}finally
/*     */     {
/* 283 */       if (context != null) {
/*     */         try
/*     */         {
/* 286 */           context.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTXTRecord(String query)
/*     */     throws UnknownHostException
/*     */   {
/* 302 */     DirContext context = null;
/*     */     try
/*     */     {
/* 305 */       context = getInitialDirContext().ctx;
/*     */       
/* 307 */       Attributes attrs = context.getAttributes(query, new String[] { "TXT" });
/*     */       
/* 309 */       NamingEnumeration n_enum = attrs.getAll();
/*     */       
/* 311 */       while (n_enum.hasMoreElements())
/*     */       {
/* 313 */         Attribute attr = (Attribute)n_enum.next();
/*     */         
/* 315 */         NamingEnumeration n_enum2 = attr.getAll();
/*     */         
/* 317 */         while (n_enum2.hasMoreElements())
/*     */         {
/* 319 */           String attribute = (String)n_enum2.nextElement();
/*     */           
/* 321 */           if (attribute != null)
/*     */           {
/* 323 */             attribute = attribute.trim();
/*     */             
/* 325 */             if (attribute.startsWith("\""))
/*     */             {
/* 327 */               attribute = attribute.substring(1);
/*     */             }
/*     */             
/* 330 */             if (attribute.endsWith("\""))
/*     */             {
/* 332 */               attribute = attribute.substring(0, attribute.length() - 1);
/*     */             }
/*     */             
/* 335 */             if (attribute.length() > 0)
/*     */             {
/* 337 */               return attribute;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 343 */       throw new UnknownHostException("DNS query returned no results");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 347 */       throw new UnknownHostException("DNS query failed:" + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */     finally
/*     */     {
/* 351 */       if (context != null) {
/*     */         try
/*     */         {
/* 354 */           context.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class DNSDirContextImpl
/*     */     implements DNSUtils.DNSDirContext
/*     */   {
/*     */     final DirContext ctx;
/*     */     
/*     */ 
/*     */ 
/*     */     private DNSDirContextImpl(DirContext _ctx)
/*     */     {
/* 372 */       this.ctx = _ctx;
/*     */     }
/*     */     
/*     */     public String getString()
/*     */     {
/*     */       try
/*     */       {
/* 379 */         return String.valueOf(this.ctx.getEnvironment());
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 383 */         return Debug.getNestedExceptionMessage(e);
/*     */       }
/*     */     }
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
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 403 */       DNSUtilsImpl impl = new DNSUtilsImpl();
/*     */       
/* 405 */       DNSUtils.DNSDirContext ctx = impl.getDirContextForServer("8.8.4.4");
/*     */       
/* 407 */       System.out.println(impl.getAllByName(ctx, "www.google.com"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 411 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/dns/DNSUtilsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */