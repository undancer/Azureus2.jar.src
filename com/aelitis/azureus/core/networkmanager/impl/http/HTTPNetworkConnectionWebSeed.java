/*     */ package com.aelitis.azureus.core.networkmanager.impl.http;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import java.io.IOException;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
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
/*     */ public class HTTPNetworkConnectionWebSeed
/*     */   extends HTTPNetworkConnection
/*     */ {
/*     */   private boolean switching;
/*     */   
/*     */   protected HTTPNetworkConnectionWebSeed(HTTPNetworkManager _manager, NetworkConnection _connection, PEPeerTransport _peer)
/*     */   {
/*  48 */     super(_manager, _connection, _peer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void decodeHeader(HTTPMessageDecoder decoder, final String header)
/*     */     throws IOException
/*     */   {
/*  58 */     if (this.switching)
/*     */     {
/*  60 */       Debug.out("new header received while paused");
/*     */       
/*  62 */       throw new IOException("Bork");
/*     */     }
/*     */     
/*  65 */     if (!isSeed())
/*     */     {
/*  67 */       return;
/*     */     }
/*     */     
/*  70 */     PEPeerControl control = getPeerControl();
/*     */     try
/*     */     {
/*  73 */       int pos = header.indexOf("\r\n");
/*     */       
/*  75 */       String line = header.substring(4, pos);
/*     */       
/*  77 */       pos = line.lastIndexOf(' ');
/*     */       
/*  79 */       String url = line.substring(0, pos).trim();
/*     */       
/*  81 */       pos = url.indexOf('?');
/*     */       
/*  83 */       if (pos != -1)
/*     */       {
/*  85 */         url = url.substring(pos + 1);
/*     */       }
/*     */       
/*  88 */       StringTokenizer tok = new StringTokenizer(url, "&");
/*     */       
/*  90 */       int piece = -1;
/*  91 */       List<int[]> ranges = new ArrayList();
/*     */       
/*  93 */       while (tok.hasMoreElements())
/*     */       {
/*  95 */         String token = tok.nextToken();
/*     */         
/*  97 */         pos = token.indexOf('=');
/*     */         
/*  99 */         if (pos != -1)
/*     */         {
/* 101 */           String lhs = token.substring(0, pos).toLowerCase(MessageText.LOCALE_ENGLISH);
/* 102 */           String rhs = token.substring(pos + 1);
/*     */           
/* 104 */           if (lhs.equals("info_hash"))
/*     */           {
/* 106 */             final byte[] old_hash = control.getHash();
/*     */             
/* 108 */             final byte[] new_hash = URLDecoder.decode(rhs, "ISO-8859-1").getBytes("ISO-8859-1");
/*     */             
/* 110 */             if (!Arrays.equals(new_hash, old_hash))
/*     */             {
/* 112 */               this.switching = true;
/*     */               
/* 114 */               decoder.pauseInternally();
/*     */               
/* 116 */               flushRequests(new HTTPNetworkConnection.flushListener()
/*     */               {
/*     */                 private boolean triggered;
/*     */                 
/*     */ 
/*     */ 
/*     */                 public void flushed()
/*     */                 {
/* 124 */                   synchronized (this)
/*     */                   {
/* 126 */                     if (this.triggered)
/*     */                     {
/* 128 */                       return;
/*     */                     }
/*     */                     
/* 131 */                     this.triggered = true;
/*     */                   }
/*     */                   
/* 134 */                   HTTPNetworkConnectionWebSeed.this.getManager().reRoute(HTTPNetworkConnectionWebSeed.this, old_hash, new_hash, header);
/*     */ 
/*     */                 }
/*     */                 
/*     */ 
/* 139 */               });
/* 140 */               return;
/*     */             }
/* 142 */           } else if (lhs.equals("piece"))
/*     */           {
/*     */             try {
/* 145 */               piece = Integer.parseInt(rhs);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 149 */               throw new IOException("Invalid piece number '" + rhs + "'");
/*     */             }
/* 151 */           } else if (lhs.equals("ranges"))
/*     */           {
/* 153 */             StringTokenizer range_tok = new StringTokenizer(rhs, ",");
/*     */             
/* 155 */             while (range_tok.hasMoreTokens())
/*     */             {
/* 157 */               String range = range_tok.nextToken();
/*     */               
/* 159 */               int sep = range.indexOf('-');
/*     */               
/* 161 */               if (sep == -1)
/*     */               {
/* 163 */                 throw new IOException("Invalid range specification '" + rhs + "'");
/*     */               }
/*     */               try
/*     */               {
/* 167 */                 ranges.add(new int[] { Integer.parseInt(range.substring(0, sep)), Integer.parseInt(range.substring(sep + 1)) });
/*     */ 
/*     */ 
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/*     */ 
/* 174 */                 throw new IOException("Invalid range specification '" + rhs + "'");
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 181 */       if (piece == -1)
/*     */       {
/* 183 */         throw new IOException("Piece number not specified");
/*     */       }
/*     */       
/* 186 */       boolean keep_alive = header.toLowerCase(MessageText.LOCALE_ENGLISH).contains("keep-alive");
/*     */       
/*     */ 
/* 189 */       int this_piece_size = control.getPieceLength(piece);
/*     */       
/* 191 */       if (ranges.size() == 0)
/*     */       {
/* 193 */         ranges.add(new int[] { 0, this_piece_size - 1 });
/*     */       }
/*     */       
/* 196 */       long[] offsets = new long[ranges.size()];
/* 197 */       long[] lengths = new long[ranges.size()];
/*     */       
/* 199 */       long piece_offset = piece * control.getPieceLength(0);
/*     */       
/* 201 */       for (int i = 0; i < ranges.size(); i++)
/*     */       {
/* 203 */         int[] range = (int[])ranges.get(i);
/*     */         
/* 205 */         int start = range[0];
/* 206 */         int end = range[1];
/*     */         
/* 208 */         if ((start < 0) || (start >= this_piece_size) || (end < 0) || (end >= this_piece_size) || (start > end))
/*     */         {
/*     */ 
/*     */ 
/* 212 */           throw new IOException("Invalid range specification '" + start + "-" + end + "'");
/*     */         }
/*     */         
/* 215 */         offsets[i] = (piece_offset + start);
/* 216 */         lengths[i] = (end - start + 1);
/*     */       }
/*     */       
/* 219 */       addRequest(new HTTPNetworkConnection.httpRequest(offsets, lengths, 0L, false, keep_alive));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 223 */       Debug.outNoStack("Decode of '" + (header.length() > 128 ? header.substring(0, 128) + "..." : header) + "' - " + Debug.getNestedExceptionMessageAndStack(e));
/*     */       
/* 225 */       if ((e instanceof IOException))
/*     */       {
/* 227 */         throw ((IOException)e);
/*     */       }
/*     */       
/*     */ 
/* 231 */       throw new IOException("Decode failed: " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPNetworkConnectionWebSeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */