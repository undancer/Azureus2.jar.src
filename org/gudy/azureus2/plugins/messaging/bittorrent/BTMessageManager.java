/*     */ package org.gudy.azureus2.plugins.messaging.bittorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTCancel;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessage;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTPiece;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTRequest;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BTMessageManager
/*     */ {
/*     */   public static final String ID_BTMESSAGE_REQUEST = "BT_REQUEST";
/*     */   public static final String ID_BTMESSAGE_CANCEL = "BT_CANCEL";
/*     */   public static final String ID_BTMESSAGE_PIECE = "BT_PIECE";
/*     */   public static final String ID_BTMESSAGE_UNCHOKE = "BT_UNCHOKE";
/*     */   
/*     */   public static BTMessageRequest createCoreBTRequestAdaptation(org.gudy.azureus2.plugins.messaging.Message core_made_message)
/*     */   {
/*  53 */     com.aelitis.azureus.core.peermanager.messaging.Message core_msg = ((MessageAdapter)core_made_message).getCoreMessage();
/*     */     
/*  55 */     if (core_msg.getID().equals("BT_REQUEST")) {
/*  56 */       return new BTMessageRequest(core_msg);
/*     */     }
/*     */     
/*  59 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BTMessageCancel createCoreBTCancelAdaptation(org.gudy.azureus2.plugins.messaging.Message core_made_message)
/*     */   {
/*  70 */     com.aelitis.azureus.core.peermanager.messaging.Message core_msg = ((MessageAdapter)core_made_message).getCoreMessage();
/*     */     
/*  72 */     if (core_msg.getID().equals("BT_CANCEL")) {
/*  73 */       return new BTMessageCancel(core_msg);
/*     */     }
/*     */     
/*  76 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BTMessagePiece createCoreBTPieceAdaptation(org.gudy.azureus2.plugins.messaging.Message core_made_message)
/*     */   {
/*  87 */     com.aelitis.azureus.core.peermanager.messaging.Message core_msg = ((MessageAdapter)core_made_message).getCoreMessage();
/*     */     
/*  89 */     if (core_msg.getID().equals("BT_PIECE")) {
/*  90 */       return new BTMessagePiece(core_msg);
/*     */     }
/*     */     
/*  93 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static MessageAdapter wrapCoreMessage(BTMessage core_msg)
/*     */   {
/* 100 */     String id = core_msg.getID();
/*     */     
/* 102 */     if (id.equals("BT_REQUEST"))
/*     */     {
/* 104 */       return new BTMessageRequest(core_msg);
/*     */     }
/* 106 */     if (id.equals("BT_CANCEL"))
/*     */     {
/* 108 */       return new BTMessageCancel(core_msg);
/*     */     }
/* 110 */     if (id.equals("BT_PIECE"))
/*     */     {
/* 112 */       return new BTMessagePiece(core_msg);
/*     */     }
/*     */     
/*     */ 
/* 116 */     return new MessageAdapter(core_msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.plugins.messaging.Message createCoreBTRequest(int piece_number, int piece_offset, int length)
/*     */   {
/* 128 */     return new MessageAdapter(new BTRequest(piece_number, piece_offset, length, (byte)1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.plugins.messaging.Message createCoreBTCancel(int piece_number, int piece_offset, int length)
/*     */   {
/* 140 */     return new MessageAdapter(new BTCancel(piece_number, piece_offset, length, (byte)1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.plugins.messaging.Message createCoreBTPiece(int piece_number, int piece_offset, ByteBuffer data)
/*     */   {
/* 152 */     return new MessageAdapter(new BTPiece(piece_number, piece_offset, new DirectByteBuffer(data), (byte)1));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/bittorrent/BTMessageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */