/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.security.AlgorithmParameterGeneratorSpi;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.security.spec.DSAParameterSpec;
/*     */ import javax.crypto.spec.DHGenParameterSpec;
/*     */ import javax.crypto.spec.DHParameterSpec;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.RC2ParameterSpec;
/*     */ import org.gudy.bouncycastle.crypto.generators.DHParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.generators.DSAParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.generators.ElGamalParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.params.DHParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ElGamalParameters;
/*     */ import org.gudy.bouncycastle.jce.spec.ElGamalParameterSpec;
/*     */ 
/*     */ public abstract class JDKAlgorithmParameterGenerator
/*     */   extends AlgorithmParameterGeneratorSpi
/*     */ {
/*     */   protected SecureRandom random;
/*     */   protected int strength;
/*     */   
/*     */   public JDKAlgorithmParameterGenerator()
/*     */   {
/*  29 */     this.strength = 1024;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void engineInit(int strength, SecureRandom random)
/*     */   {
/*  35 */     this.strength = strength;
/*  36 */     this.random = random;
/*     */   }
/*     */   
/*     */   public static class DH
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*  42 */     private int l = 0;
/*     */     
/*     */ 
/*     */ 
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/*  49 */       if (!(genParamSpec instanceof DHGenParameterSpec))
/*     */       {
/*  51 */         throw new InvalidAlgorithmParameterException("DH parameter generator requires a DHGenParameterSpec for initialisation");
/*     */       }
/*  53 */       DHGenParameterSpec spec = (DHGenParameterSpec)genParamSpec;
/*     */       
/*  55 */       this.strength = spec.getPrimeSize();
/*  56 */       this.l = spec.getExponentSize();
/*  57 */       this.random = random;
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/*  62 */       DHParametersGenerator pGen = new DHParametersGenerator();
/*     */       
/*  64 */       if (this.random != null)
/*     */       {
/*  66 */         pGen.init(this.strength, 20, this.random);
/*     */       }
/*     */       else
/*     */       {
/*  70 */         pGen.init(this.strength, 20, new SecureRandom());
/*     */       }
/*     */       
/*  73 */       DHParameters p = pGen.generateParameters();
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/*  79 */         params = AlgorithmParameters.getInstance("DH", BouncyCastleProvider.PROVIDER_NAME);
/*  80 */         params.init(new DHParameterSpec(p.getP(), p.getG(), this.l));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*  84 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/*  87 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class DSA
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/*  99 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSA parameter generation.");
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/* 104 */       DSAParametersGenerator pGen = new DSAParametersGenerator();
/*     */       
/* 106 */       if (this.random != null)
/*     */       {
/* 108 */         pGen.init(this.strength, 20, this.random);
/*     */       }
/*     */       else
/*     */       {
/* 112 */         pGen.init(this.strength, 20, new SecureRandom());
/*     */       }
/*     */       
/* 115 */       DSAParameters p = pGen.generateParameters();
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/* 121 */         params = AlgorithmParameters.getInstance("DSA", BouncyCastleProvider.PROVIDER_NAME);
/* 122 */         params.init(new DSAParameterSpec(p.getP(), p.getQ(), p.getG()));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 126 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/* 129 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class ElGamal
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 141 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ElGamal parameter generation.");
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/* 146 */       ElGamalParametersGenerator pGen = new ElGamalParametersGenerator();
/*     */       
/* 148 */       if (this.random != null)
/*     */       {
/* 150 */         pGen.init(this.strength, 20, this.random);
/*     */       }
/*     */       else
/*     */       {
/* 154 */         pGen.init(this.strength, 20, new SecureRandom());
/*     */       }
/*     */       
/* 157 */       ElGamalParameters p = pGen.generateParameters();
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/* 163 */         params = AlgorithmParameters.getInstance("ElGamal", BouncyCastleProvider.PROVIDER_NAME);
/* 164 */         params.init(new ElGamalParameterSpec(p.getP(), p.getG()));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 168 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/* 171 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class DES
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 183 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DES parameter generation.");
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/* 188 */       byte[] iv = new byte[8];
/*     */       
/* 190 */       if (this.random == null)
/*     */       {
/* 192 */         this.random = new SecureRandom();
/*     */       }
/*     */       
/* 195 */       this.random.nextBytes(iv);
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/* 201 */         params = AlgorithmParameters.getInstance("DES", BouncyCastleProvider.PROVIDER_NAME);
/* 202 */         params.init(new IvParameterSpec(iv));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 206 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/* 209 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class RC2
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/* 216 */     RC2ParameterSpec spec = null;
/*     */     
/*     */ 
/*     */ 
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 223 */       if ((genParamSpec instanceof RC2ParameterSpec))
/*     */       {
/* 225 */         this.spec = ((RC2ParameterSpec)genParamSpec);
/* 226 */         return;
/*     */       }
/*     */       
/* 229 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for RC2 parameter generation.");
/*     */     }
/*     */     
/*     */ 
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/*     */       AlgorithmParameters params;
/* 236 */       if (this.spec == null)
/*     */       {
/* 238 */         byte[] iv = new byte[8];
/*     */         
/* 240 */         if (this.random == null)
/*     */         {
/* 242 */           this.random = new SecureRandom();
/*     */         }
/*     */         
/* 245 */         this.random.nextBytes(iv);
/*     */         
/*     */         try
/*     */         {
/* 249 */           AlgorithmParameters params = AlgorithmParameters.getInstance("RC2", BouncyCastleProvider.PROVIDER_NAME);
/* 250 */           params.init(new IvParameterSpec(iv));
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 254 */           throw new RuntimeException(e.getMessage());
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/* 261 */           params = AlgorithmParameters.getInstance("RC2", BouncyCastleProvider.PROVIDER_NAME);
/* 262 */           params.init(this.spec);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 266 */           throw new RuntimeException(e.getMessage());
/*     */         }
/*     */       }
/*     */       
/* 270 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class AES
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 282 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/* 287 */       byte[] iv = new byte[16];
/*     */       
/* 289 */       if (this.random == null)
/*     */       {
/* 291 */         this.random = new SecureRandom();
/*     */       }
/*     */       
/* 294 */       this.random.nextBytes(iv);
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/* 300 */         params = AlgorithmParameters.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
/* 301 */         params.init(new IvParameterSpec(iv));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 305 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/* 308 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class IDEA
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 320 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for IDEA parameter generation.");
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/* 325 */       byte[] iv = new byte[8];
/*     */       
/* 327 */       if (this.random == null)
/*     */       {
/* 329 */         this.random = new SecureRandom();
/*     */       }
/*     */       
/* 332 */       this.random.nextBytes(iv);
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/* 338 */         params = AlgorithmParameters.getInstance("IDEA", BouncyCastleProvider.PROVIDER_NAME);
/* 339 */         params.init(new IvParameterSpec(iv));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 343 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/* 346 */       return params;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class CAST5
/*     */     extends JDKAlgorithmParameterGenerator
/*     */   {
/*     */     protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 358 */       throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for CAST5 parameter generation.");
/*     */     }
/*     */     
/*     */     protected AlgorithmParameters engineGenerateParameters()
/*     */     {
/* 363 */       byte[] iv = new byte[8];
/*     */       
/* 365 */       if (this.random == null)
/*     */       {
/* 367 */         this.random = new SecureRandom();
/*     */       }
/*     */       
/* 370 */       this.random.nextBytes(iv);
/*     */       
/*     */       AlgorithmParameters params;
/*     */       
/*     */       try
/*     */       {
/* 376 */         params = AlgorithmParameters.getInstance("CAST5", BouncyCastleProvider.PROVIDER_NAME);
/* 377 */         params.init(new IvParameterSpec(iv));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 381 */         throw new RuntimeException(e.getMessage());
/*     */       }
/*     */       
/* 384 */       return params;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKAlgorithmParameterGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */