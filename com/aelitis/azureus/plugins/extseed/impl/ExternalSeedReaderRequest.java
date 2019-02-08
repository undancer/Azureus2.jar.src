/*     */ package com.aelitis.azureus.plugins.extseed.impl;
/*     */ 
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*     */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderListener;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.plugins.peers.PeerReadRequest;
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
/*     */ public class ExternalSeedReaderRequest
/*     */   implements ExternalSeedHTTPDownloaderListener
/*     */ {
/*     */   private ExternalSeedReaderImpl reader;
/*     */   private List<PeerReadRequest> requests;
/*     */   private int start_piece_number;
/*     */   private int start_piece_offset;
/*     */   private int length;
/*  42 */   private int current_request_index = 0;
/*     */   
/*     */   private PeerReadRequest current_request;
/*     */   
/*     */   private byte[] current_buffer;
/*     */   
/*     */   private int current_position;
/*     */   
/*     */   protected ExternalSeedReaderRequest(ExternalSeedReaderImpl _reader, List<PeerReadRequest> _requests)
/*     */   {
/*  52 */     this.reader = _reader;
/*  53 */     this.requests = _requests;
/*     */     
/*  55 */     for (int i = 0; i < this.requests.size(); i++)
/*     */     {
/*  57 */       PeerReadRequest req = (PeerReadRequest)this.requests.get(i);
/*     */       
/*  59 */       if (i == 0)
/*     */       {
/*  61 */         this.start_piece_number = req.getPieceNumber();
/*  62 */         this.start_piece_offset = req.getOffset();
/*     */       }
/*     */       
/*  65 */       this.length += req.getLength();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStartPieceNumber()
/*     */   {
/*  72 */     return this.start_piece_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStartPieceOffset()
/*     */   {
/*  78 */     return this.start_piece_offset;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  84 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getBuffer()
/*     */     throws ExternalSeedException
/*     */   {
/*  92 */     if (this.current_request_index >= this.requests.size())
/*     */     {
/*  94 */       throw new ExternalSeedException("Insufficient buffers to satisfy request");
/*     */     }
/*     */     
/*  97 */     this.current_request = ((PeerReadRequest)this.requests.get(this.current_request_index++));
/*     */     
/*  99 */     this.current_buffer = new byte[this.current_request.getLength()];
/*     */     
/* 101 */     this.current_position = 0;
/*     */     
/* 103 */     return this.current_buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 109 */     for (int i = 0; i < this.requests.size(); i++)
/*     */     {
/* 111 */       PeerReadRequest req = (PeerReadRequest)this.requests.get(i);
/*     */       
/* 113 */       if (req.isCancelled())
/*     */       {
/* 115 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 119 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void done()
/*     */   {
/* 125 */     this.reader.informComplete(this.current_request, this.current_buffer);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancel()
/*     */   {
/* 131 */     for (int i = 0; i < this.requests.size(); i++)
/*     */     {
/* 133 */       PeerReadRequest req = (PeerReadRequest)this.requests.get(i);
/*     */       
/* 135 */       if (!req.isCancelled())
/*     */       {
/* 137 */         req.cancel();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed()
/*     */   {
/* 145 */     for (int i = this.current_request_index; i < this.requests.size(); i++)
/*     */     {
/* 147 */       PeerReadRequest request = (PeerReadRequest)this.requests.get(i);
/*     */       
/* 149 */       this.reader.informFailed(request);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBufferPosition(int pos)
/*     */   {
/* 157 */     this.current_position = pos;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBufferPosition()
/*     */   {
/* 163 */     return this.current_position;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBufferLength()
/*     */   {
/* 169 */     return this.current_buffer.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDoneOfCurrentIncomingRequest()
/*     */   {
/* 175 */     PeerReadRequest req = this.current_request;
/*     */     
/* 177 */     if (req == null)
/*     */     {
/* 179 */       return 0;
/*     */     }
/*     */     
/* 182 */     return 100 * this.current_position / req.getLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPermittedBytes()
/*     */     throws ExternalSeedException
/*     */   {
/* 191 */     PeerReadRequest req = this.current_request;
/*     */     
/* 193 */     if (req == null)
/*     */     {
/* 195 */       req = (PeerReadRequest)this.requests.get(0);
/*     */     }
/*     */     
/* 198 */     if (req.isCancelled())
/*     */     {
/* 200 */       throw new ExternalSeedException("Request cancelled");
/*     */     }
/*     */     
/* 203 */     return this.reader.getPermittedBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedTime()
/*     */   {
/* 209 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportBytesRead(int num)
/*     */   {
/* 216 */     this.reader.reportBytesRead(num);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/impl/ExternalSeedReaderRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */