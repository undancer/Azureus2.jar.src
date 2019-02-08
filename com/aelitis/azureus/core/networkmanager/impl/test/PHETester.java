/*     */ package com.aelitis.azureus.core.networkmanager.impl.test;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector.SelectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelectorFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoder;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoderAdapter;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoderInitial;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPTransportHelper;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
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
/*     */ public class PHETester
/*     */ {
/*  47 */   private final VirtualChannelSelector connect_selector = new VirtualChannelSelector("PHETester", 8, true);
/*     */   
/*  49 */   final byte[] TEST_HEADER = "TestHeader".getBytes();
/*     */   
/*     */   private static final boolean OUTGOING_PLAIN = false;
/*     */   
/*  53 */   private static final byte[] shared_secret = "sdsjdksjdkj".getBytes();
/*     */   
/*     */ 
/*     */   public PHETester()
/*     */   {
/*  58 */     ProtocolDecoder.addSecrets(new byte[][] { shared_secret });
/*     */     
/*     */ 
/*  61 */     VirtualServerChannelSelector accept_server = VirtualServerChannelSelectorFactory.createNonBlocking(new InetSocketAddress(8765), 0, new VirtualServerChannelSelector.SelectListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void newConnectionAccepted(ServerSocketChannel server, SocketChannel channel)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  71 */         PHETester.this.incoming(channel);
/*     */       }
/*     */       
/*  74 */     });
/*  75 */     accept_server.start();
/*     */     
/*  77 */     new Thread()
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/*     */           for (;;) {
/*  84 */             PHETester.this.connect_selector.select(100L);
/*     */           }
/*     */         } catch (Throwable t) {
/*  87 */           Debug.out("connnectSelectLoop() EXCEPTION: ", t);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*  92 */     }.start();
/*  93 */     outgoings();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void incoming(SocketChannel channel)
/*     */   {
/*     */     try
/*     */     {
/* 101 */       TransportHelper helper = new TCPTransportHelper(channel);
/*     */       
/* 103 */       decoder = new ProtocolDecoderInitial(helper, (byte[][])null, false, null, new ProtocolDecoderAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void decodeComplete(ProtocolDecoder decoder, ByteBuffer remaining_initial_data)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */           System.out.println("incoming decode complete: " + decoder.getFilter().getName(false));
/*     */           
/* 118 */           PHETester.this.readStream("incoming", decoder.getFilter());
/*     */           
/* 120 */           PHETester.this.writeStream("ten fat monkies", decoder.getFilter());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void decodeFailed(ProtocolDecoder decoder, Throwable cause)
/*     */         {
/* 128 */           System.out.println("incoming decode failed: " + Debug.getNestedExceptionMessage(cause));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void gotSecret(byte[] session_secret) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public int getMaximumPlainHeaderLength()
/*     */         {
/* 140 */           return PHETester.this.TEST_HEADER.length;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public int matchPlainHeader(ByteBuffer buffer)
/*     */         {
/* 147 */           int pos = buffer.position();
/* 148 */           int lim = buffer.limit();
/*     */           
/* 150 */           buffer.flip();
/*     */           
/* 152 */           boolean match = buffer.compareTo(ByteBuffer.wrap(PHETester.this.TEST_HEADER)) == 0;
/*     */           
/* 154 */           buffer.position(pos);
/* 155 */           buffer.limit(lim);
/*     */           
/* 157 */           System.out.println("Match - " + match);
/*     */           
/* 159 */           return match ? 2 : 1;
/*     */         }
/*     */       });
/*     */     } catch (Throwable e) {
/*     */       ProtocolDecoderInitial decoder;
/* 164 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void outgoings()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 173 */       outgoing();
/*     */       try
/*     */       {
/* 176 */         Thread.sleep(1000000L);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void outgoing()
/*     */   {
/*     */     try
/*     */     {
/* 188 */       final SocketChannel channel = SocketChannel.open();
/*     */       try
/*     */       {
/* 191 */         channel.configureBlocking(false);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 195 */         channel.close();
/*     */         
/* 197 */         throw e;
/*     */       }
/*     */       
/* 200 */       if (channel.connect(new InetSocketAddress("localhost", 8765)))
/*     */       {
/* 202 */         outgoing(channel);
/*     */       }
/*     */       else
/*     */       {
/* 206 */         this.connect_selector.register(channel, new VirtualChannelSelector.VirtualSelectorListener()
/*     */         {
/*     */ 
/*     */           public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/* 215 */               if (channel.finishConnect())
/*     */               {
/* 217 */                 PHETester.this.outgoing(channel);
/*     */                 
/* 219 */                 return true;
/*     */               }
/*     */               
/* 222 */               throw new IOException("finishConnect failed");
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 226 */               e.printStackTrace();
/*     */             }
/* 228 */             return false;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */           public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg) { msg.printStackTrace(); } }, null);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 244 */       e.printStackTrace();
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
/*     */   protected void outgoing(SocketChannel channel)
/*     */   {
/*     */     try
/*     */     {
/* 261 */       TransportHelper helper = new TCPTransportHelper(channel);
/*     */       
/* 263 */       decoder = new ProtocolDecoderInitial(helper, new byte[][] { shared_secret }, true, null, new ProtocolDecoderAdapter()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void decodeComplete(ProtocolDecoder decoder, ByteBuffer remaining_initial_data)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 276 */           System.out.println("outgoing decode complete: " + decoder.getFilter().getName(false));
/*     */           
/* 278 */           PHETester.this.readStream("incoming", decoder.getFilter());
/*     */           
/* 280 */           PHETester.this.writeStream(PHETester.this.TEST_HEADER, decoder.getFilter());
/*     */           
/* 282 */           PHETester.this.writeStream("two jolly porkers", decoder.getFilter());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void decodeFailed(ProtocolDecoder decoder, Throwable cause)
/*     */         {
/* 290 */           System.out.println("outgoing decode failed: " + Debug.getNestedExceptionMessage(cause));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void gotSecret(byte[] session_secret) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public int getMaximumPlainHeaderLength()
/*     */         {
/* 303 */           throw new RuntimeException();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public int matchPlainHeader(ByteBuffer buffer)
/*     */         {
/* 310 */           throw new RuntimeException();
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e) {
/*     */       ProtocolDecoderInitial decoder;
/* 316 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void readStream(final String str, final TransportHelperFilter filter)
/*     */   {
/*     */     try
/*     */     {
/* 326 */       TCPNetworkManager.getSingleton().getReadSelector().register(((TCPTransportHelper)filter.getHelper()).getSocketChannel(), new VirtualChannelSelector.VirtualSelectorListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */         {
/*     */ 
/*     */ 
/* 334 */           ByteBuffer buffer = ByteBuffer.allocate(1024);
/*     */           try
/*     */           {
/* 337 */             long len = filter.read(new ByteBuffer[] { buffer }, 0, 1);
/*     */             
/* 339 */             byte[] data = new byte[buffer.position()];
/*     */             
/* 341 */             buffer.flip();
/*     */             
/* 343 */             buffer.get(data);
/*     */             
/* 345 */             System.out.println(str + ": " + new String(data));
/*     */             
/* 347 */             return len > 0L;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 351 */             e.printStackTrace();
/*     */           }
/* 353 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 361 */         public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg) { msg.printStackTrace(); } }, null);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 368 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeStream(String str, TransportHelperFilter filter)
/*     */   {
/* 377 */     writeStream(str.getBytes(), filter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeStream(byte[] data, TransportHelperFilter filter)
/*     */   {
/*     */     try
/*     */     {
/* 386 */       filter.write(new ByteBuffer[] { ByteBuffer.wrap(data) }, 0, 1);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 390 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void writeStream(byte[] data, SocketChannel channel)
/*     */   {
/*     */     try
/*     */     {
/* 400 */       channel.write(new ByteBuffer[] { ByteBuffer.wrap(data) }, 0, 1);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 404 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 412 */     AEDiagnostics.startup(false);
/*     */     
/*     */ 
/*     */ 
/* 416 */     COConfigurationManager.setParameter("network.transport.encrypted.require", true);
/* 417 */     COConfigurationManager.setParameter("network.transport.encrypted.min_level", "Plain");
/*     */     
/* 419 */     new PHETester();
/*     */     try
/*     */     {
/* 422 */       Thread.sleep(10000000L);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/test/PHETester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */