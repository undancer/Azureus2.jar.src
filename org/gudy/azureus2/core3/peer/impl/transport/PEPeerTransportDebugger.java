/*     */ package org.gudy.azureus2.core3.peer.impl.transport;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
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
/*     */ public class PEPeerTransportDebugger
/*     */ {
/*     */   protected final int piece_length;
/*     */   protected static final int BT_READING_LENGTH_AND_TYPE = 1234567;
/*     */   
/*     */   protected PEPeerTransportDebugger(PEPeerTransportProtocol transport)
/*     */   {
/*  41 */     this.piece_length = transport.getControl().getPieceLength(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int write(SocketChannel chan, ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/*  51 */     int pos = buffer.position();
/*     */     
/*  53 */     int len = chan.write(buffer);
/*     */     
/*  55 */     if (len > 0)
/*     */     {
/*  57 */       buffer.position(pos);
/*     */       
/*  59 */       analyse(buffer, len);
/*     */     }
/*     */     
/*  62 */     return len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long write(SocketChannel chan, ByteBuffer[] buffers, int array_offset, int array_length)
/*     */     throws IOException
/*     */   {
/*  74 */     int[] pos = new int[buffers.length];
/*     */     
/*  76 */     for (int i = array_offset; i < array_offset + array_length; i++)
/*     */     {
/*  78 */       pos[i] = buffers[i].position();
/*     */     }
/*     */     
/*  81 */     long len = chan.write(buffers, array_offset, array_length);
/*     */     
/*  83 */     for (int i = array_offset; i < array_offset + array_length; i++)
/*     */     {
/*  85 */       ByteBuffer buffer = buffers[i];
/*     */       
/*  87 */       int written = buffer.position() - pos[i];
/*     */       
/*  89 */       if (written > 0)
/*     */       {
/*  91 */         buffer.position(pos[i]);
/*     */         
/*  93 */         analyse(buffer, written);
/*     */       }
/*     */     }
/*     */     
/*  97 */     return len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 102 */   protected int state = -1;
/* 103 */   protected byte[] data_read = new byte[68];
/*     */   
/*     */ 
/*     */   protected int data_read_pos;
/*     */   
/*     */ 
/*     */   protected void analyse(ByteBuffer buffer, int length)
/*     */   {
/* 111 */     byte[] data = new byte[length];
/*     */     
/* 113 */     buffer.get(data);
/*     */     
/* 115 */     for (int i = 0; i < data.length; i++)
/*     */     {
/* 117 */       if (this.data_read_pos == this.data_read.length)
/*     */       {
/* 119 */         if (this.state == 1234567)
/*     */         {
/* 121 */           ByteBuffer bb = ByteBuffer.wrap(this.data_read);
/*     */           
/* 123 */           int len = bb.getInt();
/*     */           
/* 125 */           this.state = bb.get();
/*     */           
/*     */ 
/*     */ 
/* 129 */           if (len == 1)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 135 */             this.state = 1234567;
/*     */             
/* 137 */             this.data_read = new byte[5];
/*     */           }
/*     */           else
/*     */           {
/* 141 */             this.data_read = new byte[len - 1];
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 150 */           if (this.state == 7)
/*     */           {
/* 152 */             ByteBuffer bb = ByteBuffer.wrap(this.data_read);
/*     */             
/* 154 */             int piece_number = bb.getInt();
/* 155 */             int piece_offset = bb.getInt();
/*     */             
/* 157 */             long overall_offset = piece_number * this.piece_length + piece_offset;
/*     */             
/* 159 */             while (bb.hasRemaining())
/*     */             {
/* 161 */               byte v = bb.get();
/*     */               
/* 163 */               if ((byte)(int)overall_offset != v)
/*     */               {
/* 165 */                 System.out.println("piece: write is bad at " + overall_offset + ": expected = " + (byte)(int)overall_offset + ", actual = " + v);
/*     */                 
/*     */ 
/* 168 */                 break;
/*     */               }
/*     */               
/* 171 */               overall_offset += 1L;
/*     */             }
/*     */           }
/*     */           
/* 175 */           this.state = 1234567;
/*     */           
/* 177 */           this.data_read = new byte[5];
/*     */         }
/*     */         
/* 180 */         this.data_read_pos = 0;
/*     */       }
/*     */       
/* 183 */       this.data_read[(this.data_read_pos++)] = data[i];
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/transport/PEPeerTransportDebugger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */