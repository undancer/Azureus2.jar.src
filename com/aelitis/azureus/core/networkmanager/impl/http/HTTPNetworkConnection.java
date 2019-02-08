/*      */ package com.aelitis.azureus.core.networkmanager.impl.http;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*      */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*      */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*      */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*      */ import com.aelitis.azureus.core.networkmanager.Transport;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.RawMessageImpl;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTBitfield;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHandshake;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHave;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTInterested;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTPiece;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTRequest;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.HTTPUtils;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class HTTPNetworkConnection
/*      */ {
/*   62 */   protected static final LogIDs LOGID = LogIDs.NWMAN;
/*      */   
/*      */   private static final int MAX_OUTSTANDING_BT_REQUESTS = 16;
/*      */   
/*      */   protected static final String NL = "\r\n";
/*      */   
/*      */   private static final String HDR_SERVER = "Server: Azureus 5.7.6.0\r\n";
/*      */   
/*      */   private static final String HDR_KEEP_ALIVE_TIMEOUT = "Keep-Alive: timeout=30\r\n";
/*      */   private static final String HDR_CACHE_CONTROL = "Cache-Control: public, max-age=86400\r\n";
/*   72 */   private static final String DEFAULT_CONTENT_TYPE = HTTPUtils.guessContentTypeFromFileType(null);
/*      */   private static int max_read_block_size;
/*      */   private static final int TIMEOUT_CHECK_PERIOD = 15000;
/*      */   
/*      */   static
/*      */   {
/*   78 */     ParameterListener param_listener = new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String str)
/*      */       {
/*   83 */         HTTPNetworkConnection.access$002(COConfigurationManager.getIntParameter("BT Request Max Block Size"));
/*      */       }
/*      */       
/*   86 */     };
/*   87 */     COConfigurationManager.addAndFireParameterListener("BT Request Max Block Size", param_listener);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   94 */     http_connection_map = new HashMap();
/*      */     
/*      */ 
/*   97 */     SimpleTimer.addPeriodicEvent("HTTPNetworkConnection:timer", 15000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  106 */         synchronized (HTTPNetworkConnection.http_connection_map)
/*      */         {
/*  108 */           boolean check = true;
/*      */           
/*  110 */           while (check)
/*      */           {
/*  112 */             check = false;
/*      */             
/*  114 */             Iterator<Map.Entry<HTTPNetworkConnection.networkConnectionKey, List<HTTPNetworkConnection>>> it = HTTPNetworkConnection.http_connection_map.entrySet().iterator();
/*      */             
/*  116 */             while (it.hasNext())
/*      */             {
/*  118 */               Map.Entry<HTTPNetworkConnection.networkConnectionKey, List<HTTPNetworkConnection>> entry = (Map.Entry)it.next();
/*      */               
/*  120 */               HTTPNetworkConnection.networkConnectionKey key = (HTTPNetworkConnection.networkConnectionKey)entry.getKey();
/*      */               
/*  122 */               List<HTTPNetworkConnection> connections = (List)entry.getValue();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  137 */               if (HTTPNetworkConnection.checkConnections(connections))
/*      */               {
/*      */ 
/*      */ 
/*  141 */                 if (!HTTPNetworkConnection.http_connection_map.containsKey(key))
/*      */                 {
/*  143 */                   check = true;
/*      */                   
/*  145 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static final int DEAD_CONNECTION_TIMEOUT_PERIOD = 30000;
/*      */   private static final int MAX_CON_PER_ENDPOINT = 5000;
/*      */   static final Map<networkConnectionKey, List<HTTPNetworkConnection>> http_connection_map;
/*      */   protected static boolean checkConnections(List<HTTPNetworkConnection> connections) {
/*  159 */     boolean some_closed = false;
/*      */     
/*  161 */     HTTPNetworkConnection oldest = null;
/*  162 */     long oldest_time = -1L;
/*      */     
/*  164 */     Iterator<HTTPNetworkConnection> it = connections.iterator();
/*      */     
/*  166 */     List<HTTPNetworkConnection> timed_out = new ArrayList();
/*      */     
/*  168 */     while (it.hasNext())
/*      */     {
/*  170 */       HTTPNetworkConnection connection = (HTTPNetworkConnection)it.next();
/*      */       
/*  172 */       long time = connection.getTimeSinceLastActivity();
/*      */       
/*  174 */       if (time > 30000L)
/*      */       {
/*  176 */         if (connection.getRequestCount() == 0)
/*      */         {
/*  178 */           timed_out.add(connection);
/*      */           
/*  180 */           continue;
/*      */         }
/*      */       }
/*      */       
/*  184 */       if ((time > oldest_time) && (!connection.isClosing()))
/*      */       {
/*  186 */         oldest_time = time;
/*      */         
/*  188 */         oldest = connection;
/*      */       }
/*      */     }
/*      */     
/*  192 */     for (int i = 0; i < timed_out.size(); i++)
/*      */     {
/*  194 */       ((HTTPNetworkConnection)timed_out.get(i)).close("Timeout");
/*      */       
/*  196 */       some_closed = true;
/*      */     }
/*      */     
/*  199 */     if (connections.size() - timed_out.size() > 5000)
/*      */     {
/*  201 */       oldest.close("Too many connections from initiator");
/*      */       
/*  203 */       some_closed = true;
/*      */     }
/*      */     
/*  206 */     return some_closed;
/*      */   }
/*      */   
/*      */ 
/*      */   private final HTTPNetworkManager manager;
/*      */   
/*      */   private final NetworkConnection connection;
/*      */   private final PEPeerTransport peer;
/*      */   private final HTTPMessageDecoder decoder;
/*      */   private final HTTPMessageEncoder encoder;
/*  216 */   private boolean sent_handshake = false;
/*      */   
/*  218 */   private final byte[] peer_id = PeerUtils.createWebSeedPeerID();
/*      */   
/*  220 */   private boolean choked = true;
/*      */   
/*  222 */   private final List<httpRequest> http_requests = new ArrayList();
/*  223 */   private final List<BTRequest> choked_requests = new ArrayList();
/*  224 */   private final List<pendingRequest> outstanding_requests = new ArrayList();
/*      */   
/*  226 */   private final BitSet piece_map = new BitSet();
/*      */   
/*      */   private long last_http_activity_time;
/*      */   
/*      */   private final networkConnectionKey network_connection_key;
/*      */   
/*      */   private boolean closing;
/*      */   
/*      */   private boolean destroyed;
/*      */   private final String last_modified_date;
/*  236 */   private String content_type = DEFAULT_CONTENT_TYPE;
/*      */   
/*  238 */   private CopyOnWriteList<requestListener> request_listeners = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected HTTPNetworkConnection(HTTPNetworkManager _manager, NetworkConnection _connection, PEPeerTransport _peer)
/*      */   {
/*  246 */     this.manager = _manager;
/*  247 */     this.connection = _connection;
/*  248 */     this.peer = _peer;
/*      */     
/*  250 */     DiskManager dm = this.peer.getManager().getDiskManager();
/*      */     
/*  252 */     long last_modified = 0L;
/*      */     try
/*      */     {
/*  255 */       last_modified = dm.getFiles()[0].getFile(true).lastModified();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*  260 */     this.last_modified_date = TimeFormatter.getHTTPDate(last_modified);
/*      */     
/*  262 */     this.network_connection_key = new networkConnectionKey();
/*      */     
/*  264 */     this.last_http_activity_time = SystemTime.getCurrentTime();
/*      */     
/*  266 */     this.decoder = ((HTTPMessageDecoder)this.connection.getIncomingMessageQueue().getDecoder());
/*  267 */     this.encoder = ((HTTPMessageEncoder)this.connection.getOutgoingMessageQueue().getEncoder());
/*      */     
/*  269 */     synchronized (http_connection_map)
/*      */     {
/*  271 */       List<HTTPNetworkConnection> connections = (List)http_connection_map.get(this.network_connection_key);
/*      */       
/*  273 */       if (connections == null)
/*      */       {
/*  275 */         connections = new ArrayList();
/*      */         
/*  277 */         http_connection_map.put(this.network_connection_key, connections);
/*      */       }
/*      */       
/*  280 */       connections.add(this);
/*      */       
/*  282 */       if (connections.size() > 5000)
/*      */       {
/*  284 */         checkConnections(connections);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  291 */     this.encoder.setConnection(this);
/*  292 */     this.decoder.setConnection(this);
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isSeed()
/*      */   {
/*  298 */     if ((!this.peer.getControl().isSeeding()) || (this.peer.getControl().getHiddenBytes() > 0L))
/*      */     {
/*  300 */       if (Logger.isEnabled()) {
/*  301 */         Logger.log(new LogEvent(this.peer, LOGID, "Download is not seeding"));
/*      */       }
/*      */       
/*  304 */       sendAndClose(this.manager.getNotFound());
/*      */       
/*  306 */       return false;
/*      */     }
/*      */     
/*  309 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setContentType(String ct)
/*      */   {
/*  316 */     this.content_type = ct;
/*      */   }
/*      */   
/*      */ 
/*      */   protected HTTPNetworkManager getManager()
/*      */   {
/*  322 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected NetworkConnection getConnection()
/*      */   {
/*  328 */     return this.connection;
/*      */   }
/*      */   
/*      */ 
/*      */   protected PEPeerTransport getPeer()
/*      */   {
/*  334 */     return this.peer;
/*      */   }
/*      */   
/*      */   protected PEPeerControl getPeerControl()
/*      */   {
/*  339 */     return this.peer.getControl();
/*      */   }
/*      */   
/*      */ 
/*      */   protected RawMessage encodeChoke()
/*      */   {
/*  345 */     synchronized (this.outstanding_requests)
/*      */     {
/*  347 */       this.choked = true;
/*      */     }
/*      */     
/*  350 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected RawMessage encodeUnchoke()
/*      */   {
/*  356 */     synchronized (this.outstanding_requests)
/*      */     {
/*  358 */       this.choked = false;
/*      */       
/*  360 */       for (int i = 0; i < this.choked_requests.size(); i++)
/*      */       {
/*  362 */         this.decoder.addMessage((BTRequest)this.choked_requests.get(i));
/*      */       }
/*      */       
/*  365 */       this.choked_requests.clear();
/*      */     }
/*      */     
/*  368 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected RawMessage encodeBitField()
/*      */   {
/*  374 */     this.decoder.addMessage(new BTInterested((byte)1));
/*      */     
/*  376 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void readWakeup()
/*      */   {
/*  382 */     this.connection.getTransport().setReadyForRead();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected RawMessage encodeHandShake(Message message)
/*      */   {
/*  389 */     return null;
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
/*      */   protected String encodeHeader(httpRequest request)
/*      */   {
/*  403 */     String current_date = TimeFormatter.getHTTPDate(SystemTime.getCurrentTime());
/*      */     
/*  405 */     StringBuilder res = new StringBuilder(256);
/*      */     
/*  407 */     boolean partial = request.isPartialContent();
/*      */     
/*  409 */     res.append("HTTP/1.1 ");
/*  410 */     res.append(partial ? "206 Partial Content" : "200 OK");
/*  411 */     res.append("\r\n");
/*      */     
/*  413 */     res.append("Content-Type: ");
/*  414 */     res.append(this.content_type);
/*  415 */     res.append("\r\n");
/*      */     
/*  417 */     res.append("Date: ");
/*  418 */     res.append(current_date);
/*  419 */     res.append("\r\n");
/*      */     
/*  421 */     res.append("Last-Modified: ");
/*  422 */     res.append(this.last_modified_date);
/*  423 */     res.append("\r\n");
/*      */     
/*  425 */     res.append("Cache-Control: public, max-age=86400\r\n");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  432 */     res.append("Server: Azureus 5.7.6.0\r\n");
/*      */     
/*  434 */     if (partial)
/*      */     {
/*  436 */       long[] offsets = request.getOriginalOffsets();
/*  437 */       long[] lengths = request.getOriginalLengths();
/*      */       
/*  439 */       long content_length = request.getContentLength();
/*      */       
/*  441 */       if ((offsets.length == 1) && (content_length > 0L))
/*      */       {
/*  443 */         res.append("Content-Range: bytes ").append(offsets[0]).append("-").append(offsets[0] + lengths[0] - 1L).append("/").append(content_length);
/*      */         
/*  445 */         res.append("\r\n");
/*      */       }
/*      */     }
/*  448 */     res.append("Connection: ");
/*  449 */     res.append(request.keepAlive() ? "Keep-Alive" : "Close");
/*  450 */     res.append("\r\n");
/*      */     
/*  452 */     if (request.keepAlive())
/*      */     {
/*  454 */       res.append("Keep-Alive: timeout=30\r\n");
/*      */     }
/*      */     
/*  457 */     res.append("Content-Length: ");
/*  458 */     res.append(request.getTotalLength());
/*  459 */     res.append("\r\n");
/*      */     
/*  461 */     res.append("\r\n");
/*      */     
/*  463 */     return res.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addRequest(httpRequest request)
/*      */     throws IOException
/*      */   {
/*  472 */     this.last_http_activity_time = SystemTime.getCurrentTime();
/*      */     
/*  474 */     PEPeerControl control = getPeerControl();
/*      */     
/*  476 */     if (!this.sent_handshake)
/*      */     {
/*  478 */       this.sent_handshake = true;
/*      */       
/*  480 */       this.decoder.addMessage(new BTHandshake(control.getHash(), this.peer_id, 0, (byte)1));
/*      */       
/*  482 */       byte[] bits = new byte[(control.getPieces().length + 7) / 8];
/*      */       
/*  484 */       DirectByteBuffer buffer = new DirectByteBuffer(ByteBuffer.wrap(bits));
/*      */       
/*  486 */       this.decoder.addMessage(new BTBitfield(buffer, (byte)1));
/*      */     }
/*      */     
/*  489 */     synchronized (this.outstanding_requests)
/*      */     {
/*  491 */       this.http_requests.add(request);
/*      */     }
/*      */     
/*  494 */     submitBTRequests();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void submitBTRequests()
/*      */     throws IOException
/*      */   {
/*  502 */     PEPeerControl control = getPeerControl();
/*      */     
/*  504 */     long piece_size = control.getPieceLength(0);
/*      */     
/*  506 */     synchronized (this.outstanding_requests)
/*      */     {
/*  508 */       while ((this.outstanding_requests.size() < 16) && (this.http_requests.size() > 0))
/*      */       {
/*  510 */         httpRequest http_request = (httpRequest)this.http_requests.get(0);
/*      */         
/*  512 */         long[] offsets = http_request.getModifiableOffsets();
/*  513 */         long[] lengths = http_request.getModifiableLengths();
/*      */         
/*  515 */         int index = http_request.getIndex();
/*      */         
/*  517 */         long offset = offsets[index];
/*  518 */         long length = lengths[index];
/*      */         
/*  520 */         int this_piece_number = (int)(offset / piece_size);
/*  521 */         int this_piece_size = control.getPieceLength(this_piece_number);
/*      */         
/*  523 */         int offset_in_piece = (int)(offset - this_piece_number * piece_size);
/*      */         
/*  525 */         int space_this_piece = this_piece_size - offset_in_piece;
/*      */         
/*  527 */         int request_size = (int)Math.min(length, space_this_piece);
/*      */         
/*  529 */         request_size = Math.min(request_size, max_read_block_size);
/*      */         
/*  531 */         addBTRequest(new BTRequest(this_piece_number, offset_in_piece, request_size, (byte)1), http_request);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  539 */         if (request_size == length)
/*      */         {
/*  541 */           if (index == offsets.length - 1)
/*      */           {
/*  543 */             this.http_requests.remove(0);
/*      */           }
/*      */           else
/*      */           {
/*  547 */             http_request.setIndex(index + 1);
/*      */           }
/*      */         } else {
/*  550 */           offsets[index] += request_size;
/*  551 */           lengths[index] -= request_size;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addBTRequest(BTRequest request, httpRequest http_request)
/*      */     throws IOException
/*      */   {
/*  564 */     synchronized (this.outstanding_requests)
/*      */     {
/*  566 */       if (this.destroyed)
/*      */       {
/*  568 */         throw new IOException("HTTP connection destroyed");
/*      */       }
/*      */       
/*  571 */       this.outstanding_requests.add(new pendingRequest(request, http_request));
/*      */       
/*  573 */       if (this.choked)
/*      */       {
/*  575 */         if (this.choked_requests.size() > 1024)
/*      */         {
/*  577 */           Debug.out("pending request limit exceeded");
/*      */         }
/*      */         else
/*      */         {
/*  581 */           this.choked_requests.add(request);
/*      */         }
/*      */       }
/*      */       else {
/*  585 */         this.decoder.addMessage(request);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected RawMessage[] encodePiece(Message message)
/*      */   {
/*  594 */     this.last_http_activity_time = SystemTime.getCurrentTime();
/*      */     
/*  596 */     BTPiece piece = (BTPiece)message;
/*      */     
/*  598 */     List<pendingRequest> ready_requests = new ArrayList();
/*      */     
/*  600 */     boolean found = false;
/*      */     
/*  602 */     synchronized (this.outstanding_requests)
/*      */     {
/*  604 */       if (this.destroyed)
/*      */       {
/*  606 */         return new RawMessage[] { getEmptyRawMessage(message) };
/*      */       }
/*      */       
/*  609 */       for (int i = 0; i < this.outstanding_requests.size(); i++)
/*      */       {
/*  611 */         pendingRequest req = (pendingRequest)this.outstanding_requests.get(i);
/*      */         
/*  613 */         if ((req.getPieceNumber() == piece.getPieceNumber()) && (req.getStart() == piece.getPieceOffset()) && (req.getLength() == piece.getPieceData().remaining((byte)5)))
/*      */         {
/*      */ 
/*      */ 
/*  617 */           if (req.getBTPiece() == null)
/*      */           {
/*  619 */             req.setBTPiece(piece);
/*      */             
/*  621 */             found = true;
/*      */             
/*  623 */             if (i != 0)
/*      */               break;
/*  625 */             Iterator<pendingRequest> it = this.outstanding_requests.iterator();
/*      */             
/*  627 */             while (it.hasNext())
/*      */             {
/*  629 */               pendingRequest r = (pendingRequest)it.next();
/*      */               
/*  631 */               BTPiece btp = r.getBTPiece();
/*      */               
/*  633 */               if (btp == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*  638 */               it.remove();
/*      */               
/*  640 */               ready_requests.add(r);
/*      */             }
/*  642 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  650 */     if (!found)
/*      */     {
/*  652 */       Debug.out("request not matched");
/*      */       
/*  654 */       return new RawMessage[] { getEmptyRawMessage(message) };
/*      */     }
/*      */     
/*  657 */     if (ready_requests.size() == 0)
/*      */     {
/*  659 */       return new RawMessage[] { getEmptyRawMessage(message) };
/*      */     }
/*      */     try
/*      */     {
/*  663 */       submitBTRequests();
/*      */     }
/*      */     catch (IOException e) {}
/*      */     
/*      */ 
/*      */ 
/*  669 */     pendingRequest req = (pendingRequest)ready_requests.get(0);
/*      */     
/*      */ 
/*      */ 
/*  673 */     httpRequest http_request = req.getHTTPRequest();
/*      */     
/*  675 */     RawMessage[] raw_messages = new RawMessage[ready_requests.size()];
/*      */     
/*  677 */     for (int i = 0; i < raw_messages.length; i++)
/*      */     {
/*  679 */       DirectByteBuffer[] buffers = new DirectByteBuffer[2];
/*      */       
/*  681 */       if (!http_request.hasSentFirstReply())
/*      */       {
/*  683 */         http_request.setSentFirstReply();
/*      */         
/*  685 */         String header = encodeHeader(http_request);
/*      */         
/*  687 */         buffers[0] = new DirectByteBuffer(ByteBuffer.wrap(header.getBytes()));
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  693 */         buffers[0] = new DirectByteBuffer(ByteBuffer.allocate(0));
/*      */       }
/*      */       
/*  696 */       req = (pendingRequest)ready_requests.get(i);
/*      */       
/*  698 */       BTPiece this_piece = req.getBTPiece();
/*      */       
/*  700 */       int piece_number = this_piece.getPieceNumber();
/*      */       
/*  702 */       if (!this.piece_map.get(piece_number))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  707 */         this.piece_map.set(piece_number);
/*      */         
/*  709 */         this.decoder.addMessage(new BTHave(piece_number, (byte)1));
/*      */       }
/*      */       
/*  712 */       buffers[1] = this_piece.getPieceData();
/*      */       
/*  714 */       req.logQueued();
/*      */       
/*  716 */       if (this.request_listeners != null)
/*      */       {
/*  718 */         Iterator<requestListener> it = this.request_listeners.iterator();
/*      */         
/*  720 */         while (it.hasNext())
/*      */         {
/*  722 */           ((requestListener)it.next()).requestComplete(req);
/*      */         }
/*      */       }
/*      */       
/*  726 */       raw_messages[i] = new RawMessageImpl(this_piece, buffers, 2, true, new Message[0]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  735 */     return raw_messages;
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
/*      */   protected boolean isClosing()
/*      */   {
/*  750 */     return this.closing;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void close(String reason)
/*      */   {
/*  757 */     this.closing = true;
/*      */     
/*  759 */     this.peer.getControl().removePeer(this.peer);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  765 */     synchronized (http_connection_map)
/*      */     {
/*  767 */       List<HTTPNetworkConnection> connections = (List)http_connection_map.get(this.network_connection_key);
/*      */       
/*  769 */       if (connections != null)
/*      */       {
/*  771 */         connections.remove(this);
/*      */         
/*  773 */         if (connections.size() == 0)
/*      */         {
/*  775 */           http_connection_map.remove(this.network_connection_key);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  780 */     synchronized (this.outstanding_requests)
/*      */     {
/*  782 */       this.destroyed = true;
/*      */       
/*  784 */       for (int i = 0; i < this.outstanding_requests.size(); i++)
/*      */       {
/*  786 */         pendingRequest req = (pendingRequest)this.outstanding_requests.get(i);
/*      */         
/*  788 */         BTPiece piece = req.getBTPiece();
/*      */         
/*  790 */         if (piece != null)
/*      */         {
/*  792 */           piece.destroy();
/*      */         }
/*      */       }
/*      */       
/*  796 */       this.outstanding_requests.clear();
/*      */       
/*  798 */       for (int i = 0; i < this.choked_requests.size(); i++)
/*      */       {
/*  800 */         BTRequest req = (BTRequest)this.choked_requests.get(i);
/*      */         
/*  802 */         req.destroy();
/*      */       }
/*      */       
/*  805 */       this.choked_requests.clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getTimeSinceLastActivity()
/*      */   {
/*  812 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  814 */     if (now < this.last_http_activity_time)
/*      */     {
/*  816 */       this.last_http_activity_time = now;
/*      */     }
/*      */     
/*  819 */     return now - this.last_http_activity_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/*  826 */     if (Logger.isEnabled()) {
/*  827 */       Logger.log(new LogEvent(getPeer(), LOGID, str));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected RawMessage getEmptyRawMessage(Message message)
/*      */   {
/*  835 */     return new RawMessageImpl(message, new DirectByteBuffer[] { new DirectByteBuffer(ByteBuffer.allocate(0)) }, 2, true, new Message[0]);
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
/*      */   protected void sendAndClose(String data)
/*      */   {
/*  848 */     final Message http_message = new HTTPMessage(data);
/*      */     
/*  850 */     getConnection().getOutgoingMessageQueue().registerQueueListener(new OutgoingMessageQueue.MessageQueueListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public boolean messageAdded(Message message)
/*      */       {
/*      */ 
/*  857 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageQueued(Message message) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageRemoved(Message message) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageSent(Message message)
/*      */       {
/*  876 */         if (message == http_message)
/*      */         {
/*  878 */           HTTPNetworkConnection.this.close("Close after message send complete");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void protocolBytesSent(int byte_count) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dataBytesSent(int byte_count) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void flush() {}
/*  896 */     });
/*  897 */     getConnection().getOutgoingMessageQueue().addMessage(http_message, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void flushRequests(final flushListener l)
/*      */   {
/*  904 */     boolean sync_fire = false;
/*      */     
/*  906 */     synchronized (this.outstanding_requests)
/*      */     {
/*  908 */       final int request_count = this.outstanding_requests.size();
/*      */       
/*  910 */       if (request_count == 0)
/*      */       {
/*  912 */         sync_fire = true;
/*      */       }
/*      */       else
/*      */       {
/*  916 */         if (this.request_listeners == null)
/*      */         {
/*  918 */           this.request_listeners = new CopyOnWriteList();
/*      */         }
/*      */         
/*  921 */         this.request_listeners.add(new requestListener()
/*      */         {
/*      */ 
/*  924 */           int num_to_go = request_count;
/*      */           
/*      */ 
/*      */ 
/*      */           public void requestComplete(HTTPNetworkConnection.pendingRequest r)
/*      */           {
/*  930 */             this.num_to_go -= 1;
/*      */             
/*  932 */             if (this.num_to_go == 0)
/*      */             {
/*  934 */               HTTPNetworkConnection.this.request_listeners.remove(this);
/*      */               
/*  936 */               HTTPNetworkConnection.this.flushRequestsSupport(l);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*  943 */     if (sync_fire)
/*      */     {
/*  945 */       flushRequestsSupport(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void flushRequestsSupport(final flushListener l)
/*      */   {
/*  953 */     OutgoingMessageQueue omq = getConnection().getOutgoingMessageQueue();
/*      */     
/*  955 */     final Message http_message = new HTTPMessage(new byte[0]);
/*      */     
/*  957 */     omq.registerQueueListener(new OutgoingMessageQueue.MessageQueueListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public boolean messageAdded(Message message)
/*      */       {
/*      */ 
/*  964 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageQueued(Message message) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageRemoved(Message message) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageSent(Message message)
/*      */       {
/*  983 */         if (message == http_message)
/*      */         {
/*  985 */           l.flushed();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void protocolBytesSent(int byte_count) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dataBytesSent(int byte_count) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void flush() {}
/* 1003 */     });
/* 1004 */     omq.addMessage(http_message, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1009 */     if (omq.getTotalSize() == 0)
/*      */     {
/* 1011 */       l.flushed();
/*      */     }
/*      */   }
/*      */   
/*      */   protected abstract void decodeHeader(HTTPMessageDecoder paramHTTPMessageDecoder, String paramString)
/*      */     throws IOException;
/*      */   
/*      */   /* Error */
/*      */   protected int getRequestCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 659	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkConnection:outstanding_requests	Ljava/util/List;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 658	com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkConnection:http_requests	Ljava/util/List;
/*      */     //   11: invokeinterface 767 1 0
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #741	-> byte code offset #0
/*      */     //   Java source line #743	-> byte code offset #7
/*      */     //   Java source line #744	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	HTTPNetworkConnection
/*      */     //   5	16	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   protected static class httpRequest
/*      */   {
/*      */     private final long[] orig_offsets;
/*      */     private final long[] orig_lengths;
/*      */     private final long content_length;
/*      */     private final boolean partial_content;
/*      */     private final boolean keep_alive;
/*      */     private final long[] mod_offsets;
/*      */     private final long[] mod_lengths;
/*      */     private int index;
/*      */     private long total_length;
/*      */     private boolean sent_first_reply;
/*      */     
/*      */     protected httpRequest(long[] _offsets, long[] _lengths, long _content_length, boolean _partial_content, boolean _keep_alive)
/*      */     {
/* 1039 */       this.orig_offsets = _offsets;
/* 1040 */       this.orig_lengths = _lengths;
/* 1041 */       this.content_length = _content_length;
/* 1042 */       this.partial_content = _partial_content;
/* 1043 */       this.keep_alive = _keep_alive;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1053 */       this.mod_offsets = ((long[])this.orig_offsets.clone());
/* 1054 */       this.mod_lengths = ((long[])this.orig_lengths.clone());
/*      */       
/* 1056 */       for (int i = 0; i < this.orig_lengths.length; i++)
/*      */       {
/* 1058 */         this.total_length += this.orig_lengths[i];
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isPartialContent()
/*      */     {
/* 1065 */       return this.partial_content;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getContentLength()
/*      */     {
/* 1071 */       return this.content_length;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean hasSentFirstReply()
/*      */     {
/* 1077 */       return this.sent_first_reply;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void setSentFirstReply()
/*      */     {
/* 1083 */       this.sent_first_reply = true;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long[] getOriginalOffsets()
/*      */     {
/* 1089 */       return this.orig_offsets;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long[] getOriginalLengths()
/*      */     {
/* 1095 */       return this.orig_lengths;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long[] getModifiableOffsets()
/*      */     {
/* 1101 */       return this.mod_offsets;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long[] getModifiableLengths()
/*      */     {
/* 1107 */       return this.mod_lengths;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getIndex()
/*      */     {
/* 1113 */       return this.index;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setIndex(int _index)
/*      */     {
/* 1120 */       this.index = _index;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getTotalLength()
/*      */     {
/* 1126 */       return this.total_length;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean keepAlive()
/*      */     {
/* 1132 */       return this.keep_alive;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class pendingRequest
/*      */   {
/*      */     private final int piece;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final int start;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final int length;
/*      */     
/*      */ 
/*      */ 
/*      */     private final HTTPNetworkConnection.httpRequest http_request;
/*      */     
/*      */ 
/*      */ 
/*      */     private BTPiece bt_piece;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected pendingRequest(BTRequest _request, HTTPNetworkConnection.httpRequest _http_request)
/*      */     {
/* 1167 */       this.piece = _request.getPieceNumber();
/* 1168 */       this.start = _request.getPieceOffset();
/* 1169 */       this.length = _request.getLength();
/*      */       
/* 1171 */       this.http_request = _http_request;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int getPieceNumber()
/*      */     {
/* 1190 */       return this.piece;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getStart()
/*      */     {
/* 1196 */       return this.start;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getLength()
/*      */     {
/* 1202 */       return this.length;
/*      */     }
/*      */     
/*      */ 
/*      */     protected HTTPNetworkConnection.httpRequest getHTTPRequest()
/*      */     {
/* 1208 */       return this.http_request;
/*      */     }
/*      */     
/*      */ 
/*      */     protected BTPiece getBTPiece()
/*      */     {
/* 1214 */       return this.bt_piece;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setBTPiece(BTPiece _bt_piece)
/*      */     {
/* 1221 */       this.bt_piece = _bt_piece;
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
/*      */     protected void logQueued() {}
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
/*      */   protected class networkConnectionKey
/*      */   {
/*      */     protected networkConnectionKey() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/* 1261 */       if ((obj instanceof networkConnectionKey))
/*      */       {
/* 1263 */         networkConnectionKey other = (networkConnectionKey)obj;
/*      */         
/* 1265 */         return (Arrays.equals(getAddress(), other.getAddress())) && (Arrays.equals(getHash(), other.getHash()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1270 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected String getName()
/*      */     {
/* 1277 */       return HTTPNetworkConnection.this.peer.getControl().getDisplayName() + ": " + HTTPNetworkConnection.this.connection.getEndpoint().getNotionalAddress().getAddress().getHostAddress();
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte[] getAddress()
/*      */     {
/* 1283 */       return AddressUtils.getAddressBytes(HTTPNetworkConnection.this.connection.getEndpoint().getNotionalAddress());
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte[] getHash()
/*      */     {
/* 1289 */       return HTTPNetworkConnection.this.peer.getControl().getHash();
/*      */     }
/*      */     
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1295 */       return HTTPNetworkConnection.this.peer.getControl().hashCode();
/*      */     }
/*      */   }
/*      */   
/*      */   protected static abstract interface flushListener
/*      */   {
/*      */     public abstract void flushed();
/*      */   }
/*      */   
/*      */   protected static abstract interface requestListener
/*      */   {
/*      */     public abstract void requestComplete(HTTPNetworkConnection.pendingRequest parampendingRequest);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */