/*     */ package com.aelitis.azureus.core.clientmessageservice.secure.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.clientmessageservice.ClientMessageService;
/*     */ import com.aelitis.azureus.core.clientmessageservice.ClientMessageServiceClient;
/*     */ import java.io.IOException;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.KeyGenerator;
/*     */ import javax.crypto.SecretKey;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.plugins.utils.Formatters;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.encodings.PKCS1Encoding;
/*     */ import org.gudy.bouncycastle.crypto.engines.RSAEngine;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ import org.gudy.bouncycastle.jce.provider.RSAUtil;
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
/*     */ public class SecureMessageServiceClientHelper
/*     */   implements ClientMessageService
/*     */ {
/*     */   private ClientMessageService delegate;
/*     */   private SecretKey session_key;
/*     */   private byte[] encryped_session_key;
/*     */   
/*     */   public static ClientMessageService getServerService(String server_address, int server_port, int timeout_secs, String msg_type_id, RSAPublicKey public_key)
/*     */     throws IOException
/*     */   {
/*  58 */     return new SecureMessageServiceClientHelper(server_address, server_port, timeout_secs, msg_type_id, public_key);
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
/*     */   protected SecureMessageServiceClientHelper(String server_address, int server_port, int timeout_secs, String msg_type_id, RSAPublicKey public_key)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  76 */       KeyGenerator secret_key_gen = KeyGenerator.getInstance("DESede");
/*     */       
/*  78 */       this.session_key = secret_key_gen.generateKey();
/*     */       
/*  80 */       byte[] secret_bytes = this.session_key.getEncoded();
/*     */       try
/*     */       {
/*  83 */         Cipher rsa_cipher = Cipher.getInstance("RSA");
/*     */         
/*  85 */         rsa_cipher.init(1, public_key);
/*     */         
/*  87 */         this.encryped_session_key = rsa_cipher.doFinal(secret_bytes);
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/*  93 */         RSAEngine eng = new RSAEngine();
/*     */         
/*  95 */         PKCS1Encoding padded_eng = new PKCS1Encoding(eng);
/*     */         
/*  97 */         CipherParameters param = RSAUtil.generatePublicKeyParameter(public_key);
/*     */         
/*  99 */         param = new ParametersWithRandom(param, RandomUtils.SECURE_RANDOM);
/*     */         
/* 101 */         padded_eng.init(true, param);
/*     */         
/* 103 */         this.encryped_session_key = padded_eng.processBlock(secret_bytes, 0, secret_bytes.length);
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 108 */       e.printStackTrace();
/*     */       
/* 110 */       throw new IOException("Secure client message service initialisation fails - " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */     
/* 113 */     this.delegate = ClientMessageServiceClient.getServerService(server_address, server_port, msg_type_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendMessage(Map plain_payload)
/*     */     throws IOException
/*     */   {
/* 122 */     Map secure_payload = new HashMap();
/*     */     try
/*     */     {
/* 125 */       byte[] message_bytes = StaticUtilities.getFormatters().bEncode(plain_payload);
/*     */       
/* 127 */       Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
/*     */       
/* 129 */       cipher.init(1, this.session_key);
/*     */       
/* 131 */       byte[] encrypted_message = cipher.doFinal(message_bytes);
/*     */       
/* 133 */       secure_payload.put("ver", "1");
/* 134 */       secure_payload.put("alg", "DESede");
/* 135 */       secure_payload.put("key", this.encryped_session_key);
/* 136 */       secure_payload.put("content", encrypted_message);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 140 */       throw new IOException("send message failed - " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */     
/* 143 */     this.delegate.sendMessage(secure_payload);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map receiveMessage()
/*     */     throws IOException
/*     */   {
/* 151 */     Map secure_payload = this.delegate.receiveMessage();
/*     */     
/* 153 */     byte[] encrypted_message = (byte[])secure_payload.get("content");
/*     */     try
/*     */     {
/* 156 */       Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
/*     */       
/* 158 */       cipher.init(2, this.session_key);
/*     */       
/* 160 */       byte[] message_bytes = cipher.doFinal(encrypted_message);
/*     */       
/* 162 */       return StaticUtilities.getFormatters().bDecode(message_bytes);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 168 */       throw new IOException("send message failed - " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 175 */     this.delegate.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setMaximumMessageSize(int max_bytes)
/*     */   {
/* 181 */     this.delegate.setMaximumMessageSize(max_bytes);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/impl/SecureMessageServiceClientHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */