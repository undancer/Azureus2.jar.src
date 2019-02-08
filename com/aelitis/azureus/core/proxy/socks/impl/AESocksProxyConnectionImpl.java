/*      */ package com.aelitis.azureus.core.proxy.socks.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.proxy.AEProxyConnection;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyConnectionListener;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyException;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyState;
/*      */ import com.aelitis.azureus.core.proxy.socks.AESocksProxy;
/*      */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyConnection;
/*      */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyPlugableConnection;
/*      */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyPlugableConnectionFactory;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolverListener;
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
/*      */ public class AESocksProxyConnectionImpl
/*      */   implements AESocksProxyConnection, AEProxyConnectionListener
/*      */ {
/*   44 */   private static final LogIDs LOGID = LogIDs.NET;
/*      */   
/*      */   public static final boolean TRACE = false;
/*      */   
/*      */   private final AESocksProxyImpl proxy;
/*      */   
/*      */   private final AEProxyConnection connection;
/*      */   
/*      */   private boolean disable_dns_lookups;
/*      */   
/*      */   private String username;
/*      */   
/*      */   private String password;
/*      */   
/*      */   final SocketChannel source_channel;
/*      */   
/*      */   private int socks_version;
/*      */   
/*      */   private AESocksProxyPlugableConnection plugable_connection;
/*      */   
/*      */ 
/*      */   protected AESocksProxyConnectionImpl(AESocksProxyImpl _proxy, AESocksProxyPlugableConnectionFactory _connection_factory, AEProxyConnection _connection)
/*      */     throws IOException
/*      */   {
/*   68 */     this.proxy = _proxy;
/*   69 */     this.connection = _connection;
/*      */     
/*   71 */     this.connection.addListener(this);
/*      */     
/*   73 */     this.source_channel = this.connection.getSourceChannel();
/*      */     try
/*      */     {
/*   76 */       this.plugable_connection = _connection_factory.create(this);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (AEProxyException e)
/*      */     {
/*      */ 
/*   83 */       throw new IOException(e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public AESocksProxy getProxy()
/*      */   {
/*   90 */     return this.proxy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDelegate(AESocksProxyPlugableConnection target)
/*      */   {
/*   97 */     this.plugable_connection = target;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getName()
/*      */   {
/*  103 */     String name = this.connection.getName() + ", ver = " + this.socks_version;
/*      */     
/*  105 */     name = name + this.plugable_connection.getName();
/*      */     
/*  107 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUsername()
/*      */   {
/*  113 */     return this.username;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPassword()
/*      */   {
/*  119 */     return this.password;
/*      */   }
/*      */   
/*      */ 
/*      */   protected AEProxyState getInitialState()
/*      */   {
/*  125 */     return new proxyStateVersion();
/*      */   }
/*      */   
/*      */ 
/*      */   public void connectionClosed(AEProxyConnection con)
/*      */   {
/*      */     try
/*      */     {
/*  133 */       if (this.plugable_connection != null)
/*      */       {
/*  135 */         this.plugable_connection.close();
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  140 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/*  147 */     return this.connection.isClosed();
/*      */   }
/*      */   
/*      */ 
/*      */   public AEProxyConnection getConnection()
/*      */   {
/*  153 */     return this.connection;
/*      */   }
/*      */   
/*      */ 
/*      */   public void disableDNSLookups()
/*      */   {
/*  159 */     this.disable_dns_lookups = true;
/*      */   }
/*      */   
/*      */ 
/*      */   public void enableDNSLookups()
/*      */   {
/*  165 */     this.disable_dns_lookups = false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean areDNSLookupsEnabled()
/*      */   {
/*  171 */     return !this.disable_dns_lookups;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  179 */     new ProxyStateClose(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class proxyStateVersion
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateVersion()
/*      */     {
/*  189 */       super();
/*      */       
/*  191 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  193 */       this.buffer = ByteBuffer.allocate(1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  202 */       int len = sc.read(this.buffer);
/*      */       
/*  204 */       if (len == 0)
/*      */       {
/*  206 */         return false;
/*      */       }
/*  208 */       if (len == -1)
/*      */       {
/*  210 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*  213 */       if (this.buffer.hasRemaining())
/*      */       {
/*  215 */         return true;
/*      */       }
/*      */       
/*  218 */       this.buffer.flip();
/*      */       
/*  220 */       int version = this.buffer.get();
/*      */       
/*  222 */       if (version == 5)
/*      */       {
/*  224 */         new AESocksProxyConnectionImpl.proxyStateV5MethodNumber(AESocksProxyConnectionImpl.this);
/*      */       }
/*  226 */       else if (version == 4)
/*      */       {
/*  228 */         new AESocksProxyConnectionImpl.proxyStateV4Request(AESocksProxyConnectionImpl.this);
/*      */       }
/*      */       else
/*      */       {
/*  232 */         throw new IOException("Unsupported version " + version);
/*      */       }
/*      */       
/*      */ 
/*  236 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class proxyStateV4Request
/*      */     extends AESocksProxyState
/*      */   {
/*      */     boolean got_header;
/*      */     
/*      */     protected int port;
/*      */     
/*      */     protected byte[] address;
/*      */     
/*      */ 
/*      */     protected proxyStateV4Request()
/*      */     {
/*  254 */       super();
/*      */       
/*  256 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  258 */       this.buffer = ByteBuffer.allocate(7);
/*      */     }
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
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  274 */       int len = sc.read(this.buffer);
/*      */       
/*  276 */       if (len == 0)
/*      */       {
/*  278 */         return false;
/*      */       }
/*  280 */       if (len == -1)
/*      */       {
/*  282 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*  285 */       if (this.buffer.hasRemaining())
/*      */       {
/*  287 */         return true;
/*      */       }
/*      */       
/*  290 */       this.buffer.flip();
/*      */       
/*  292 */       if (this.got_header)
/*      */       {
/*  294 */         if (this.buffer.get() == 0)
/*      */         {
/*      */ 
/*      */ 
/*  298 */           if ((this.address[0] == 0) && (this.address[1] == 0) && (this.address[2] == 0) && (this.address[3] != 0))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  305 */             new AESocksProxyConnectionImpl.proxyStateV4aRequest(AESocksProxyConnectionImpl.this, this.port);
/*      */           }
/*      */           else
/*      */           {
/*  309 */             AESocksProxyConnectionImpl.this.socks_version = 4;
/*      */             
/*  311 */             AESocksProxyConnectionImpl.this.plugable_connection.connect(new AESocksProxyAddressImpl("", InetAddress.getByAddress(this.address), this.port));
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  319 */           this.buffer.flip();
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  324 */         this.got_header = true;
/*      */         
/*  326 */         byte command = this.buffer.get();
/*      */         
/*  328 */         if (command != 1)
/*      */         {
/*  330 */           throw new IOException("SocksV4: only CONNECT supported");
/*      */         }
/*      */         
/*  333 */         this.port = (((this.buffer.get() & 0xFF) << 8) + (this.buffer.get() & 0xFF));
/*      */         
/*  335 */         this.address = new byte[4];
/*      */         
/*  337 */         for (int i = 0; i < this.address.length; i++)
/*      */         {
/*  339 */           this.address[i] = this.buffer.get();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  344 */         this.buffer = ByteBuffer.allocate(1);
/*      */       }
/*      */       
/*  347 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class proxyStateV4aRequest
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected String dns_address;
/*      */     
/*      */     protected final int port;
/*      */     
/*      */ 
/*      */     protected proxyStateV4aRequest(int _port)
/*      */     {
/*  362 */       super();
/*      */       
/*  364 */       this.port = _port;
/*  365 */       this.dns_address = "";
/*      */       
/*  367 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  369 */       this.buffer = ByteBuffer.allocate(1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(final SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  380 */       int len = sc.read(this.buffer);
/*      */       
/*  382 */       if (len == 0)
/*      */       {
/*  384 */         return false;
/*      */       }
/*  386 */       if (len == -1)
/*      */       {
/*  388 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  392 */       if (this.buffer.hasRemaining())
/*      */       {
/*  394 */         return true;
/*      */       }
/*      */       
/*  397 */       this.buffer.flip();
/*      */       
/*  399 */       byte data = this.buffer.get();
/*      */       
/*  401 */       if (data == 0)
/*      */       {
/*  403 */         if (AESocksProxyConnectionImpl.this.disable_dns_lookups)
/*      */         {
/*  405 */           AESocksProxyConnectionImpl.this.socks_version = 4;
/*      */           
/*  407 */           AESocksProxyConnectionImpl.this.plugable_connection.connect(new AESocksProxyAddressImpl(this.dns_address, null, this.port));
/*      */         }
/*      */         else {
/*  410 */           final String f_dns_address = this.dns_address;
/*      */           
/*  412 */           AESocksProxyConnectionImpl.this.connection.cancelReadSelect(sc);
/*      */           
/*  414 */           HostNameToIPResolver.addResolverRequest(this.dns_address, new HostNameToIPResolverListener()
/*      */           {
/*      */ 
/*      */             public void hostNameResolutionComplete(InetAddress address)
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/*  423 */                 AESocksProxyConnectionImpl.this.socks_version = 4;
/*      */                 
/*  425 */                 AESocksProxyConnectionImpl.this.plugable_connection.connect(new AESocksProxyAddressImpl(f_dns_address, address, AESocksProxyConnectionImpl.proxyStateV4aRequest.this.port));
/*      */                 
/*      */ 
/*      */ 
/*  429 */                 AESocksProxyConnectionImpl.this.connection.requestReadSelect(sc);
/*      */               }
/*      */               catch (IOException e)
/*      */               {
/*  433 */                 AESocksProxyConnectionImpl.this.connection.failed(e);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       else {
/*  440 */         this.dns_address += (char)data;
/*      */         
/*  442 */         if (this.dns_address.length() > 4096)
/*      */         {
/*  444 */           throw new IOException("DNS name too long");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  449 */         this.buffer.flip();
/*      */       }
/*      */       
/*  452 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class proxyStateV4Reply
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV4Reply()
/*      */       throws IOException
/*      */     {
/*  465 */       super();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  474 */       AESocksProxyConnectionImpl.this.connection.setWriteState(this);
/*      */       
/*  476 */       byte[] addr = AESocksProxyConnectionImpl.this.plugable_connection.getLocalAddress().getAddress();
/*  477 */       int port = AESocksProxyConnectionImpl.this.plugable_connection.getLocalPort();
/*      */       
/*  479 */       this.buffer = ByteBuffer.wrap(new byte[] { 0, 90, (byte)(port >> 8 & 0xFF), (byte)(port & 0xFF), addr[0], addr[1], addr[2], addr[3] });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  485 */       write(AESocksProxyConnectionImpl.this.source_channel);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean writeSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  494 */       int len = sc.write(this.buffer);
/*      */       
/*  496 */       if (this.buffer.hasRemaining())
/*      */       {
/*  498 */         AESocksProxyConnectionImpl.this.connection.requestWriteSelect(sc);
/*      */       }
/*      */       else
/*      */       {
/*  502 */         AESocksProxyConnectionImpl.this.plugable_connection.relayData();
/*      */       }
/*      */       
/*  505 */       return len > 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private class proxyStateV5MethodNumber
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV5MethodNumber()
/*      */     {
/*  519 */       super();
/*      */       
/*  521 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  523 */       this.buffer = ByteBuffer.allocate(1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  532 */       int len = sc.read(this.buffer);
/*      */       
/*  534 */       if (len == 0)
/*      */       {
/*  536 */         return false;
/*      */       }
/*  538 */       if (len == -1)
/*      */       {
/*  540 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  544 */       if (this.buffer.hasRemaining())
/*      */       {
/*  546 */         return true;
/*      */       }
/*      */       
/*  549 */       this.buffer.flip();
/*      */       
/*  551 */       int num_methods = this.buffer.get();
/*      */       
/*  553 */       new AESocksProxyConnectionImpl.proxyStateV5Methods(AESocksProxyConnectionImpl.this, num_methods);
/*      */       
/*  555 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private class proxyStateV5Methods
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV5Methods(int methods)
/*      */     {
/*  568 */       super();
/*      */       
/*  570 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  572 */       this.buffer = ByteBuffer.allocate(methods);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  581 */       int len = sc.read(this.buffer);
/*      */       
/*  583 */       if (len == 0)
/*      */       {
/*  585 */         return false;
/*      */       }
/*  587 */       if (len == -1)
/*      */       {
/*  589 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  593 */       if (this.buffer.hasRemaining())
/*      */       {
/*  595 */         return true;
/*      */       }
/*      */       
/*  598 */       this.buffer.flip();
/*      */       
/*  600 */       byte[] methods = new byte[this.buffer.remaining()];
/*      */       
/*  602 */       this.buffer.get(methods);
/*      */       
/*  604 */       boolean found_no_auth = false;
/*  605 */       boolean found_user_pass = false;
/*      */       
/*  607 */       for (int i = 0; i < methods.length; i++)
/*      */       {
/*  609 */         int method = methods[i] & 0xFF;
/*      */         
/*  611 */         if (method == 0)
/*      */         {
/*  613 */           found_no_auth = true;
/*      */         }
/*  615 */         else if (method == 2)
/*      */         {
/*  617 */           found_user_pass = true;
/*      */         }
/*      */       }
/*      */       
/*  621 */       if (found_no_auth)
/*      */       {
/*  623 */         new AESocksProxyConnectionImpl.proxyStateV5MethodsReply(AESocksProxyConnectionImpl.this, 0);
/*      */         
/*  625 */         return true;
/*      */       }
/*  627 */       if (found_user_pass)
/*      */       {
/*  629 */         new AESocksProxyConnectionImpl.proxyStateV5MethodsReply(AESocksProxyConnectionImpl.this, 2);
/*      */         
/*  631 */         return true;
/*      */       }
/*      */       
/*      */ 
/*  635 */       throw new IOException("V5: No supported methods requested");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private class proxyStateV5MethodsReply
/*      */     extends AESocksProxyConnectionImpl.ProxyStateWriter
/*      */   {
/*      */     protected proxyStateV5MethodsReply(int selected_method)
/*      */       throws IOException
/*      */     {
/*  650 */       super(null);
/*  651 */       if (selected_method == 0)
/*      */       {
/*  653 */         new AESocksProxyConnectionImpl.proxyStateV5Request(AESocksProxyConnectionImpl.this);
/*      */       }
/*      */       else
/*      */       {
/*  657 */         new AESocksProxyConnectionImpl.proxyStateV5Username(AESocksProxyConnectionImpl.this);
/*      */       }
/*      */       
/*  660 */       AESocksProxyConnectionImpl.this.connection.setWriteState(this);
/*      */       
/*  662 */       this.buffer = ByteBuffer.wrap(new byte[] { 5, (byte)selected_method });
/*      */       
/*  664 */       write(AESocksProxyConnectionImpl.this.source_channel);
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
/*      */   private class proxyStateV5Username
/*      */     extends AESocksProxyState
/*      */   {
/*  680 */     boolean got_length = false;
/*      */     
/*      */ 
/*      */     protected proxyStateV5Username()
/*      */     {
/*  685 */       super();
/*      */       
/*  687 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  689 */       this.buffer = ByteBuffer.allocate(2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  698 */       int len = sc.read(this.buffer);
/*      */       
/*  700 */       if (len == 0)
/*      */       {
/*  702 */         return false;
/*      */       }
/*  704 */       if (len == -1)
/*      */       {
/*  706 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  710 */       if (this.buffer.hasRemaining())
/*      */       {
/*  712 */         return true;
/*      */       }
/*      */       
/*  715 */       this.buffer.flip();
/*      */       
/*  717 */       if (!this.got_length)
/*      */       {
/*  719 */         this.buffer.get();
/*      */         
/*  721 */         int length = this.buffer.get() & 0xFF;
/*      */         
/*  723 */         this.buffer = ByteBuffer.allocate(length);
/*      */         
/*  725 */         this.got_length = true;
/*      */       }
/*      */       else
/*      */       {
/*  729 */         String user_name = "";
/*      */         
/*  731 */         while (this.buffer.hasRemaining())
/*      */         {
/*  733 */           user_name = user_name + (char)this.buffer.get();
/*      */         }
/*      */         
/*  736 */         new AESocksProxyConnectionImpl.proxyStateV5Password(AESocksProxyConnectionImpl.this, user_name);
/*      */       }
/*      */       
/*  739 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class proxyStateV5Password
/*      */     extends AESocksProxyState
/*      */   {
/*      */     private final String username;
/*      */     
/*  749 */     boolean got_length = false;
/*      */     
/*      */ 
/*      */ 
/*      */     protected proxyStateV5Password(String _username)
/*      */     {
/*  755 */       super();
/*      */       
/*  757 */       this.username = _username;
/*      */       
/*  759 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  761 */       this.buffer = ByteBuffer.allocate(1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  770 */       int len = sc.read(this.buffer);
/*      */       
/*  772 */       if (len == 0)
/*      */       {
/*  774 */         return false;
/*      */       }
/*  776 */       if (len == -1)
/*      */       {
/*  778 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  782 */       if (this.buffer.hasRemaining())
/*      */       {
/*  784 */         return true;
/*      */       }
/*      */       
/*  787 */       this.buffer.flip();
/*      */       
/*  789 */       if (!this.got_length)
/*      */       {
/*  791 */         int length = this.buffer.get() & 0xFF;
/*      */         
/*  793 */         this.buffer = ByteBuffer.allocate(length);
/*      */         
/*  795 */         this.got_length = true;
/*      */       }
/*      */       else
/*      */       {
/*  799 */         String password = "";
/*      */         
/*  801 */         while (this.buffer.hasRemaining())
/*      */         {
/*  803 */           password = password + (char)this.buffer.get();
/*      */         }
/*      */         
/*  806 */         AESocksProxyConnectionImpl.this.username = this.username;
/*  807 */         AESocksProxyConnectionImpl.this.password = password;
/*      */         
/*  809 */         new AESocksProxyConnectionImpl.proxyStateV5UsernamePasswordReply(AESocksProxyConnectionImpl.this);
/*      */       }
/*      */       
/*  812 */       return true;
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
/*      */   private class proxyStateV5UsernamePasswordReply
/*      */     extends AESocksProxyConnectionImpl.ProxyStateWriter
/*      */   {
/*      */     protected proxyStateV5UsernamePasswordReply()
/*      */       throws IOException
/*      */     {
/*  832 */       super(null);
/*  833 */       new AESocksProxyConnectionImpl.proxyStateV5Request(AESocksProxyConnectionImpl.this);
/*      */       
/*  835 */       AESocksProxyConnectionImpl.this.connection.setWriteState(this);
/*      */       
/*  837 */       this.buffer = ByteBuffer.wrap(new byte[] { 1, 0 });
/*      */       
/*  839 */       write(AESocksProxyConnectionImpl.this.source_channel);
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
/*      */   private class proxyStateV5Request
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV5Request()
/*      */     {
/*  876 */       super();
/*      */       
/*  878 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  880 */       this.buffer = ByteBuffer.allocate(4);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  889 */       int len = sc.read(this.buffer);
/*      */       
/*  891 */       if (len == 0)
/*      */       {
/*  893 */         return false;
/*      */       }
/*  895 */       if (len == -1)
/*      */       {
/*  897 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  901 */       if (this.buffer.hasRemaining())
/*      */       {
/*  903 */         return true;
/*      */       }
/*      */       
/*  906 */       this.buffer.flip();
/*      */       
/*  908 */       this.buffer.get();
/*      */       
/*  910 */       int command = this.buffer.get();
/*      */       
/*  912 */       this.buffer.get();
/*      */       
/*  914 */       int address_type = this.buffer.get();
/*      */       
/*  916 */       if (command == 1)
/*      */       {
/*  918 */         if (address_type == 1)
/*      */         {
/*  920 */           new AESocksProxyConnectionImpl.proxyStateV5RequestIP(AESocksProxyConnectionImpl.this);
/*      */         }
/*  922 */         else if (address_type == 3)
/*      */         {
/*  924 */           new AESocksProxyConnectionImpl.proxyStateV5RequestDNS(AESocksProxyConnectionImpl.this);
/*      */         }
/*  926 */         else if (address_type == 4)
/*      */         {
/*  928 */           new AESocksProxyConnectionImpl.proxyStateV5RequestIPV6(AESocksProxyConnectionImpl.this);
/*      */         }
/*      */         else {
/*  931 */           throw new IOException("V5: Unsupported address type: " + address_type);
/*      */         }
/*  933 */       } else if (command == 3)
/*      */       {
/*  935 */         new AESocksProxyConnectionImpl.proxyStateV5UDPAssociateReply(AESocksProxyConnectionImpl.this);
/*      */       }
/*      */       else
/*      */       {
/*  939 */         throw new IOException("V5: Only connect supported: command=" + command);
/*      */       }
/*      */       
/*  942 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class proxyStateV5RequestIP
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV5RequestIP()
/*      */     {
/*  954 */       super();
/*      */       
/*  956 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/*  958 */       this.buffer = ByteBuffer.allocate(4);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/*  967 */       int len = sc.read(this.buffer);
/*      */       
/*  969 */       if (len == 0)
/*      */       {
/*  971 */         return false;
/*      */       }
/*  973 */       if (len == -1)
/*      */       {
/*  975 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/*  979 */       if (this.buffer.hasRemaining())
/*      */       {
/*  981 */         return true;
/*      */       }
/*      */       
/*  984 */       this.buffer.flip();
/*      */       
/*  986 */       byte[] bytes = new byte[4];
/*      */       
/*  988 */       this.buffer.get(bytes);
/*      */       
/*  990 */       InetAddress inet_address = InetAddress.getByAddress(bytes);
/*      */       
/*  992 */       new AESocksProxyConnectionImpl.proxyStateV5RequestPort(AESocksProxyConnectionImpl.this, "", inet_address);
/*      */       
/*  994 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class proxyStateV5RequestIPV6
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV5RequestIPV6()
/*      */     {
/* 1006 */       super();
/*      */       
/* 1008 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/* 1010 */       this.buffer = ByteBuffer.allocate(16);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/* 1019 */       int len = sc.read(this.buffer);
/*      */       
/* 1021 */       if (len == 0)
/*      */       {
/* 1023 */         return false;
/*      */       }
/* 1025 */       if (len == -1)
/*      */       {
/* 1027 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/* 1031 */       if (this.buffer.hasRemaining())
/*      */       {
/* 1033 */         return true;
/*      */       }
/*      */       
/* 1036 */       this.buffer.flip();
/*      */       
/* 1038 */       byte[] bytes = new byte[16];
/*      */       
/* 1040 */       this.buffer.get(bytes);
/*      */       
/* 1042 */       InetAddress inet_address = InetAddress.getByAddress(bytes);
/*      */       
/* 1044 */       new AESocksProxyConnectionImpl.proxyStateV5RequestPort(AESocksProxyConnectionImpl.this, "", inet_address);
/*      */       
/* 1046 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class proxyStateV5RequestDNS
/*      */     extends AESocksProxyState
/*      */   {
/* 1054 */     boolean got_length = false;
/*      */     
/*      */ 
/*      */     protected proxyStateV5RequestDNS()
/*      */     {
/* 1059 */       super();
/*      */       
/* 1061 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/* 1063 */       this.buffer = ByteBuffer.allocate(1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(final SocketChannel sc)
/*      */       throws IOException
/*      */     {
/* 1072 */       int len = sc.read(this.buffer);
/*      */       
/* 1074 */       if (len == 0)
/*      */       {
/* 1076 */         return false;
/*      */       }
/* 1078 */       if (len == -1)
/*      */       {
/* 1080 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/* 1084 */       if (this.buffer.hasRemaining())
/*      */       {
/* 1086 */         return true;
/*      */       }
/*      */       
/* 1089 */       this.buffer.flip();
/*      */       
/* 1091 */       if (!this.got_length)
/*      */       {
/* 1093 */         int length = this.buffer.get() & 0xFF;
/*      */         
/* 1095 */         this.buffer = ByteBuffer.allocate(length);
/*      */         
/* 1097 */         this.got_length = true;
/*      */       }
/*      */       else
/*      */       {
/* 1101 */         StringBuilder dns_address_b = new StringBuilder(256);
/*      */         
/* 1103 */         while (this.buffer.hasRemaining())
/*      */         {
/* 1105 */           dns_address_b.append((char)this.buffer.get());
/*      */         }
/*      */         
/* 1108 */         String dns_address = dns_address_b.toString();
/*      */         
/* 1110 */         if (AESocksProxyConnectionImpl.this.disable_dns_lookups)
/*      */         {
/* 1112 */           new AESocksProxyConnectionImpl.proxyStateV5RequestPort(AESocksProxyConnectionImpl.this, dns_address, null);
/*      */         }
/*      */         else
/*      */         {
/* 1116 */           final String f_dns_address = dns_address;
/*      */           
/* 1118 */           AESocksProxyConnectionImpl.this.connection.cancelReadSelect(sc);
/*      */           
/* 1120 */           HostNameToIPResolver.addResolverRequest(dns_address, new HostNameToIPResolverListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void hostNameResolutionComplete(InetAddress address)
/*      */             {
/*      */ 
/*      */ 
/* 1128 */               new AESocksProxyConnectionImpl.proxyStateV5RequestPort(AESocksProxyConnectionImpl.this, f_dns_address, address);
/*      */               
/* 1130 */               AESocksProxyConnectionImpl.this.connection.requestReadSelect(sc);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/* 1136 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class proxyStateV5RequestPort
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected final String unresolved_address;
/*      */     
/*      */     protected final InetAddress address;
/*      */     
/*      */ 
/*      */     protected proxyStateV5RequestPort(String _unresolved_address, InetAddress _address)
/*      */     {
/* 1152 */       super();
/*      */       
/* 1154 */       this.unresolved_address = _unresolved_address;
/* 1155 */       this.address = _address;
/*      */       
/* 1157 */       AESocksProxyConnectionImpl.this.connection.setReadState(this);
/*      */       
/* 1159 */       this.buffer = ByteBuffer.allocate(2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean readSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/* 1168 */       int len = sc.read(this.buffer);
/*      */       
/* 1170 */       if (len == 0)
/*      */       {
/* 1172 */         return false;
/*      */       }
/* 1174 */       if (len == -1)
/*      */       {
/* 1176 */         throw new IOException("Connection closed");
/*      */       }
/*      */       
/*      */ 
/* 1180 */       if (this.buffer.hasRemaining())
/*      */       {
/* 1182 */         return true;
/*      */       }
/*      */       
/* 1185 */       this.buffer.flip();
/*      */       
/* 1187 */       int port = ((this.buffer.get() & 0xFF) << 8) + (this.buffer.get() & 0xFF);
/*      */       
/* 1189 */       AESocksProxyConnectionImpl.this.socks_version = 5;
/*      */       
/* 1191 */       AESocksProxyConnectionImpl.this.plugable_connection.connect(new AESocksProxyAddressImpl(this.unresolved_address, this.address, port));
/*      */       
/* 1193 */       return true;
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
/*      */   private class proxyStateV5Reply
/*      */     extends AESocksProxyState
/*      */   {
/*      */     protected proxyStateV5Reply()
/*      */       throws IOException
/*      */     {
/* 1238 */       super();
/*      */       
/* 1240 */       AESocksProxyConnectionImpl.this.connection.setWriteState(this);
/*      */       
/* 1242 */       byte[] addr = AESocksProxyConnectionImpl.this.plugable_connection.getLocalAddress().getAddress();
/*      */       
/* 1244 */       int port = AESocksProxyConnectionImpl.this.plugable_connection.getLocalPort();
/*      */       
/* 1246 */       this.buffer = ByteBuffer.wrap(new byte[] { 5, 0, 0, 1, addr[0], addr[1], addr[2], addr[3], (byte)(port >> 8 & 0xFF), (byte)(port & 0xFF) });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1252 */       write(AESocksProxyConnectionImpl.this.source_channel);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean writeSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/* 1261 */       int len = sc.write(this.buffer);
/*      */       
/* 1263 */       if (this.buffer.hasRemaining())
/*      */       {
/* 1265 */         AESocksProxyConnectionImpl.this.connection.requestWriteSelect(sc);
/*      */       }
/*      */       else
/*      */       {
/* 1269 */         AESocksProxyConnectionImpl.this.plugable_connection.relayData();
/*      */       }
/*      */       
/* 1272 */       return len > 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class proxyStateV5UDPAssociateReply
/*      */     extends AESocksProxyConnectionImpl.ProxyStateWriter
/*      */   {
/*      */     protected proxyStateV5UDPAssociateReply()
/*      */       throws IOException
/*      */     {
/* 1284 */       super(null);
/* 1285 */       AESocksProxyConnectionImpl.this.connection.setWriteState(this);
/*      */       
/* 1287 */       byte[] addr = new byte[4];
/*      */       
/* 1289 */       int port = 0;
/*      */       
/* 1291 */       int reply_state = 69;
/*      */       
/* 1293 */       this.buffer = ByteBuffer.wrap(new byte[] { 5, (byte)reply_state, 0, 1, addr[0], addr[1], addr[2], addr[3], (byte)(port >> 8 & 0xFF), (byte)(port & 0xFF) });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1298 */       write(AESocksProxyConnectionImpl.this.source_channel);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void connected()
/*      */     throws IOException
/*      */   {
/* 1307 */     if (this.socks_version == 4)
/*      */     {
/* 1309 */       new proxyStateV4Reply();
/*      */     }
/*      */     else
/*      */     {
/* 1313 */       new proxyStateV5Reply();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class ProxyStateWriter
/*      */     extends AESocksProxyState
/*      */   {
/*      */     private ProxyStateWriter()
/*      */     {
/* 1324 */       super();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean writeSupport(SocketChannel sc)
/*      */       throws IOException
/*      */     {
/* 1333 */       int len = sc.write(this.buffer);
/*      */       
/* 1335 */       if (this.buffer.hasRemaining())
/*      */       {
/* 1337 */         AESocksProxyConnectionImpl.this.connection.requestWriteSelect(sc);
/*      */       }
/*      */       
/* 1340 */       return len > 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class ProxyStateClose
/*      */     extends AESocksProxyState
/*      */   {
/*      */     private ProxyStateClose()
/*      */       throws IOException
/*      */     {
/* 1353 */       super();
/*      */       
/* 1355 */       AESocksProxyConnectionImpl.this.connection.close();
/*      */       
/* 1357 */       AESocksProxyConnectionImpl.this.connection.setReadState(null);
/* 1358 */       AESocksProxyConnectionImpl.this.connection.setWriteState(null);
/* 1359 */       AESocksProxyConnectionImpl.this.connection.setConnectState(null);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/impl/AESocksProxyConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */