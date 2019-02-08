/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*     */ public class RandomUtils
/*     */ {
/*  34 */   public static final Random RANDOM = new Random(System.currentTimeMillis());
/*     */   public static final String INSTANCE_ID;
/*     */   
/*     */   static
/*     */   {
/*  39 */     byte[] bytes = new byte[3];
/*     */     
/*  41 */     RANDOM.nextBytes(bytes);
/*     */     
/*  43 */     INSTANCE_ID = Base32.encode(bytes).toLowerCase();
/*     */   }
/*     */   
/*  46 */   public static final SecureRandom SECURE_RANDOM = new SecureRandom();
/*     */   
/*     */   public static final int LISTEN_PORT_MIN = 10000;
/*     */   
/*     */   public static final int LISTEN_PORT_MAX = 65535;
/*     */   
/*     */   public static byte[] generateRandomBytes(int num_to_generate)
/*     */   {
/*  54 */     byte[] id = new byte[num_to_generate];
/*  55 */     RANDOM.nextBytes(id);
/*  56 */     return id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String generateRandomAlphanumerics(int num_to_generate)
/*     */   {
/*  66 */     String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
/*     */     
/*  68 */     StringBuilder buff = new StringBuilder(num_to_generate);
/*     */     
/*  70 */     for (int i = 0; i < num_to_generate; i++) {
/*  71 */       int pos = (int)(RANDOM.nextDouble() * alphabet.length());
/*  72 */       buff.append(alphabet.charAt(pos));
/*     */     }
/*     */     
/*  75 */     return buff.toString();
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
/*     */ 
/*     */ 
/*     */   public static int generateRandomNetworkListenPort()
/*     */   {
/*  92 */     return generateRandomNetworkListenPort(10000, 65535);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int generateRandomNetworkListenPort(int min_port, int max_port)
/*     */   {
/* 100 */     if (min_port > max_port) {
/* 101 */       int temp = min_port;
/* 102 */       min_port = max_port;
/* 103 */       max_port = temp;
/*     */     }
/*     */     
/* 106 */     if (max_port > 65535)
/*     */     {
/* 108 */       max_port = 65535;
/*     */     }
/*     */     
/* 111 */     if (max_port < 1)
/*     */     {
/* 113 */       max_port = 1;
/*     */     }
/*     */     
/* 116 */     if (min_port < 1)
/*     */     {
/* 118 */       min_port = 1;
/*     */     }
/*     */     
/* 121 */     if (min_port > max_port)
/*     */     {
/* 123 */       min_port = max_port;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 128 */     int existing_tcp = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/* 129 */     int existing_udp = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/* 130 */     int existing_udp2 = COConfigurationManager.getIntParameter("UDP.NonData.Listen.Port");
/*     */     
/* 132 */     int port = min_port;
/*     */     
/* 134 */     for (int i = 0; i < 100; i++) {
/* 135 */       int min = min_port;
/* 136 */       port = min + RANDOM.nextInt(max_port + 1 - min);
/*     */       
/*     */ 
/*     */ 
/* 140 */       if ((port < 45100) || (port > 45110))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 145 */         if ((port != existing_tcp) && (port != existing_udp) && (port != existing_udp2))
/*     */         {
/* 147 */           return port;
/*     */         }
/*     */       }
/*     */     }
/* 151 */     return port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int generateRandomPlusMinus1()
/*     */   {
/* 160 */     return RANDOM.nextBoolean() ? -1 : 1;
/*     */   }
/*     */   
/*     */   public static float nextFloat()
/*     */   {
/* 165 */     return RANDOM.nextFloat();
/*     */   }
/*     */   
/*     */   public static void nextBytes(byte[] bytes)
/*     */   {
/* 170 */     RANDOM.nextBytes(bytes);
/*     */   }
/*     */   
/*     */   public static void nextSecureBytes(byte[] bytes)
/*     */   {
/* 175 */     SECURE_RANDOM.nextBytes(bytes);
/*     */   }
/*     */   
/*     */   public static byte[] nextSecureHash()
/*     */   {
/* 180 */     byte[] hash = new byte[20];
/*     */     
/* 182 */     SECURE_RANDOM.nextBytes(hash);
/*     */     
/* 184 */     return hash;
/*     */   }
/*     */   
/*     */   public static byte[] nextHash()
/*     */   {
/* 189 */     byte[] hash = new byte[20];
/*     */     
/* 191 */     RANDOM.nextBytes(hash);
/*     */     
/* 193 */     return hash;
/*     */   }
/*     */   
/*     */   public static int nextInt(int n)
/*     */   {
/* 198 */     return RANDOM.nextInt(n);
/*     */   }
/*     */   
/*     */   public static byte nextByte()
/*     */   {
/* 203 */     return (byte)RANDOM.nextInt();
/*     */   }
/*     */   
/*     */   public static int nextInt()
/*     */   {
/* 208 */     return RANDOM.nextInt();
/*     */   }
/*     */   
/*     */   public static int nextAbsoluteInt()
/*     */   {
/* 213 */     return RANDOM.nextInt() << 1 >>> 1;
/*     */   }
/*     */   
/*     */   public static long nextLong()
/*     */   {
/* 218 */     return RANDOM.nextLong();
/*     */   }
/*     */   
/*     */   public static long nextLong(long n)
/*     */   {
/* 223 */     if (n > 2147483647L)
/*     */     {
/*     */       for (;;)
/*     */       {
/* 227 */         long rand = nextAbsoluteLong();
/*     */         
/* 229 */         long res = rand % n;
/*     */         
/*     */ 
/*     */ 
/* 233 */         if (rand - res + (n - 1L) >= 0L)
/*     */         {
/* 235 */           return res;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 240 */     return RANDOM.nextInt((int)n);
/*     */   }
/*     */   
/*     */ 
/*     */   public static long nextAbsoluteLong()
/*     */   {
/* 246 */     return RANDOM.nextLong() << 1 >>> 1;
/*     */   }
/*     */   
/*     */   public static long nextSecureAbsoluteLong()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 253 */       long val = Math.abs(SECURE_RANDOM.nextLong());
/*     */       
/* 255 */       if (val >= 0L)
/*     */       {
/* 257 */         return val;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int generateRandomIntUpto(int max)
/*     */   {
/* 267 */     return nextInt(max);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int generateRandomIntBetween(int min, int max)
/*     */   {
/* 275 */     return min + generateRandomIntUpto(max + 1 - min);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/RandomUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */