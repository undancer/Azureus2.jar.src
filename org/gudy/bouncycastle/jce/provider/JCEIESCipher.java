/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import javax.crypto.IllegalBlockSizeException;
/*     */ import javax.crypto.NoSuchPaddingException;
/*     */ import javax.crypto.interfaces.DHPrivateKey;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.gudy.bouncycastle.crypto.agreement.ECDHBasicAgreement;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
/*     */ import org.gudy.bouncycastle.crypto.engines.IESEngine;
/*     */ import org.gudy.bouncycastle.crypto.generators.KDF2BytesGenerator;
/*     */ import org.gudy.bouncycastle.crypto.macs.HMac;
/*     */ import org.gudy.bouncycastle.crypto.params.IESParameters;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPrivateKey;
/*     */ import org.gudy.bouncycastle.jce.interfaces.IESKey;
/*     */ import org.gudy.bouncycastle.jce.spec.IESParameterSpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JCEIESCipher
/*     */ {
/*     */   private IESEngine cipher;
/*  38 */   private int state = -1;
/*  39 */   private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/*  40 */   private AlgorithmParameters engineParam = null;
/*  41 */   private IESParameterSpec engineParams = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  46 */   private Class[] availableSpecs = { IESParameterSpec.class };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JCEIESCipher(IESEngine engine)
/*     */   {
/*  54 */     this.cipher = engine;
/*     */   }
/*     */   
/*     */   protected int engineGetBlockSize()
/*     */   {
/*  59 */     return 0;
/*     */   }
/*     */   
/*     */   protected byte[] engineGetIV()
/*     */   {
/*  64 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int engineGetKeySize(Key key)
/*     */   {
/*  70 */     IESKey ieKey = (IESKey)key;
/*     */     
/*  72 */     if ((ieKey.getPrivate() instanceof DHPrivateKey))
/*     */     {
/*  74 */       DHPrivateKey k = (DHPrivateKey)ieKey.getPrivate();
/*     */       
/*  76 */       return k.getX().bitLength();
/*     */     }
/*  78 */     if ((ieKey.getPrivate() instanceof ECPrivateKey))
/*     */     {
/*  80 */       ECPrivateKey k = (ECPrivateKey)ieKey.getPrivate();
/*     */       
/*  82 */       return k.getD().bitLength();
/*     */     }
/*     */     
/*  85 */     throw new IllegalArgumentException("not an IE key!");
/*     */   }
/*     */   
/*     */ 
/*     */   protected int engineGetOutputSize(int inputLen)
/*     */   {
/*  91 */     if ((this.state == 1) || (this.state == 3))
/*     */     {
/*  93 */       return this.buffer.size() + inputLen + 20;
/*     */     }
/*  95 */     if ((this.state == 2) || (this.state == 4))
/*     */     {
/*  97 */       return this.buffer.size() + inputLen - 20;
/*     */     }
/*     */     
/*     */ 
/* 101 */     throw new IllegalStateException("cipher not initialised");
/*     */   }
/*     */   
/*     */ 
/*     */   protected AlgorithmParameters engineGetParameters()
/*     */   {
/* 107 */     if (this.engineParam == null)
/*     */     {
/* 109 */       if (this.engineParams != null)
/*     */       {
/* 111 */         String name = "IES";
/*     */         
/*     */         try
/*     */         {
/* 115 */           this.engineParam = AlgorithmParameters.getInstance(name, BouncyCastleProvider.PROVIDER_NAME);
/* 116 */           this.engineParam.init(this.engineParams);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 120 */           throw new RuntimeException(e.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 125 */     return this.engineParam;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineSetMode(String mode)
/*     */   {
/* 131 */     throw new IllegalArgumentException("can't support mode " + mode);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineSetPadding(String padding)
/*     */     throws NoSuchPaddingException
/*     */   {
/* 138 */     throw new NoSuchPaddingException(padding + " unavailable with RSA.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 148 */     if (!(key instanceof IESKey))
/*     */     {
/* 150 */       throw new InvalidKeyException("must be passed IE key");
/*     */     }
/*     */     
/* 153 */     if ((params == null) && ((opmode == 1) || (opmode == 3)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */       byte[] d = new byte[16];
/* 160 */       byte[] e = new byte[16];
/*     */       
/* 162 */       if (random == null)
/*     */       {
/* 164 */         random = new SecureRandom();
/*     */       }
/*     */       
/* 167 */       random.nextBytes(d);
/* 168 */       random.nextBytes(e);
/*     */       
/* 170 */       params = new IESParameterSpec(d, e, 128);
/*     */     }
/* 172 */     else if (!(params instanceof IESParameterSpec))
/*     */     {
/* 174 */       throw new InvalidAlgorithmParameterException("must be passed IES parameters");
/*     */     }
/*     */     
/* 177 */     IESKey ieKey = (IESKey)key;
/*     */     
/*     */     CipherParameters privKey;
/*     */     CipherParameters pubKey;
/*     */     CipherParameters privKey;
/* 182 */     if ((ieKey.getPublic() instanceof JCEECPublicKey))
/*     */     {
/* 184 */       CipherParameters pubKey = ECUtil.generatePublicKeyParameter(ieKey.getPublic());
/* 185 */       privKey = ECUtil.generatePrivateKeyParameter(ieKey.getPrivate());
/*     */     }
/*     */     else
/*     */     {
/* 189 */       pubKey = DHUtil.generatePublicKeyParameter(ieKey.getPublic());
/* 190 */       privKey = DHUtil.generatePrivateKeyParameter(ieKey.getPrivate());
/*     */     }
/*     */     
/* 193 */     this.engineParams = ((IESParameterSpec)params);
/*     */     
/* 195 */     IESParameters p = new IESParameters(this.engineParams.getDerivationV(), this.engineParams.getEncodingV(), this.engineParams.getMacKeySize());
/*     */     
/* 197 */     this.state = opmode;
/*     */     
/* 199 */     this.buffer.reset();
/*     */     
/* 201 */     switch (opmode)
/*     */     {
/*     */     case 1: 
/*     */     case 3: 
/* 205 */       this.cipher.init(true, privKey, pubKey, p);
/* 206 */       break;
/*     */     case 2: 
/*     */     case 4: 
/* 209 */       this.cipher.init(false, privKey, pubKey, p);
/* 210 */       break;
/*     */     default: 
/* 212 */       System.out.println("eeek!");
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 223 */     AlgorithmParameterSpec paramSpec = null;
/*     */     
/* 225 */     if (params != null)
/*     */     {
/* 227 */       for (int i = 0; i != this.availableSpecs.length; i++)
/*     */       {
/*     */         try
/*     */         {
/* 231 */           paramSpec = params.getParameterSpec(this.availableSpecs[i]);
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 240 */       if (paramSpec == null)
/*     */       {
/* 242 */         throw new InvalidAlgorithmParameterException("can't handle parameter " + params.toString());
/*     */       }
/*     */     }
/*     */     
/* 246 */     this.engineParam = params;
/* 247 */     engineInit(opmode, key, paramSpec, random);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineInit(int opmode, Key key, SecureRandom random)
/*     */     throws InvalidKeyException
/*     */   {
/* 256 */     if ((opmode == 1) || (opmode == 3))
/*     */     {
/*     */       try
/*     */       {
/* 260 */         engineInit(opmode, key, (AlgorithmParameterSpec)null, random);
/* 261 */         return;
/*     */       }
/*     */       catch (InvalidAlgorithmParameterException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 269 */     throw new IllegalArgumentException("can't handle null parameter spec in IES");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] engineUpdate(byte[] input, int inputOffset, int inputLen)
/*     */   {
/* 277 */     this.buffer.write(input, inputOffset, inputLen);
/* 278 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
/*     */   {
/* 288 */     this.buffer.write(input, inputOffset, inputLen);
/* 289 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
/*     */     throws IllegalBlockSizeException, BadPaddingException
/*     */   {
/* 298 */     if (inputLen != 0)
/*     */     {
/* 300 */       this.buffer.write(input, inputOffset, inputLen);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 305 */       byte[] buf = this.buffer.toByteArray();
/*     */       
/* 307 */       this.buffer.reset();
/*     */       
/* 309 */       return this.cipher.processBlock(buf, 0, buf.length);
/*     */     }
/*     */     catch (InvalidCipherTextException e)
/*     */     {
/* 313 */       throw new BadPaddingException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
/*     */     throws IllegalBlockSizeException, BadPaddingException
/*     */   {
/* 325 */     if (inputLen != 0)
/*     */     {
/* 327 */       this.buffer.write(input, inputOffset, inputLen);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 332 */       byte[] buf = this.buffer.toByteArray();
/*     */       
/* 334 */       this.buffer.reset();
/*     */       
/* 336 */       buf = this.cipher.processBlock(buf, 0, buf.length);
/*     */       
/* 338 */       System.arraycopy(buf, 0, output, outputOffset, buf.length);
/*     */       
/* 340 */       return buf.length;
/*     */     }
/*     */     catch (InvalidCipherTextException e)
/*     */     {
/* 344 */       throw new BadPaddingException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class ECIES
/*     */     extends JCEIESCipher
/*     */   {
/*     */     public ECIES()
/*     */     {
/* 356 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JCEIESCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */