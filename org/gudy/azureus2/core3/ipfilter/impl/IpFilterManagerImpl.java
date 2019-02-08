/*     */ package org.gudy.azureus2.core3.ipfilter.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.ipfilter.BadIps;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ public class IpFilterManagerImpl
/*     */   implements IpFilterManager, ParameterListener
/*     */ {
/*  44 */   protected static final IpFilterManagerImpl singleton = new IpFilterManagerImpl();
/*     */   
/*  46 */   private RandomAccessFile rafDescriptions = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public IpFilterManagerImpl()
/*     */   {
/*  52 */     COConfigurationManager.addAndFireParameterListener("Ip Filter Enable Description Cache", this);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object addDescription(IpRange range, byte[] description)
/*     */   {
/*  58 */     if (this.rafDescriptions == null) {
/*  59 */       return null;
/*     */     }
/*     */     try
/*     */     {
/*  63 */       if ((description == null) || (description.length == 0)) {
/*  64 */         return null;
/*     */       }
/*     */       
/*     */ 
/*  68 */       int start = (int)this.rafDescriptions.getFilePointer();
/*  69 */       int len = (int)this.rafDescriptions.length();
/*     */       
/*     */ 
/*  72 */       if (len + 61 >= 33554431)
/*     */       {
/*     */ 
/*  75 */         return null;
/*     */       }
/*     */       
/*  78 */       if (start != len) {
/*  79 */         this.rafDescriptions.seek(len);
/*  80 */         start = (int)this.rafDescriptions.getFilePointer();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  86 */       if (description.length <= 61) {
/*  87 */         this.rafDescriptions.write(description);
/*     */       } else {
/*  89 */         this.rafDescriptions.write(description, 0, 61);
/*     */       }
/*  91 */       int end = (int)this.rafDescriptions.getFilePointer();
/*     */       
/*     */ 
/*     */ 
/*  95 */       int info = start + (end - start << 25);
/*     */       
/*  97 */       return new Integer(info);
/*     */     }
/*     */     catch (Exception e) {
/* 100 */       e.printStackTrace();
/*     */     }
/*     */     
/* 103 */     return null;
/*     */   }
/*     */   
/*     */   public byte[] getDescription(Object info)
/*     */   {
/* 108 */     if ((info instanceof Object[])) {
/* 109 */       return (byte[])((Object[])(Object[])info)[0];
/*     */     }
/*     */     
/* 112 */     if ((this.rafDescriptions == null) || (!(info instanceof Integer))) {
/* 113 */       return "".getBytes();
/*     */     }
/*     */     try
/*     */     {
/* 117 */       int posInfo = ((Integer)info).intValue();
/* 118 */       int pos = posInfo & 0x1FFFFFF;
/* 119 */       int len = posInfo >> 25;
/*     */       
/* 121 */       if (len < 0) {
/* 122 */         throw new IllegalArgumentException(getClass().getName() + ": invalid posInfo [" + posInfo + "], pos [" + pos + "], len [" + len + "]");
/*     */       }
/*     */       
/* 125 */       if (this.rafDescriptions.getFilePointer() != pos) {
/* 126 */         this.rafDescriptions.seek(pos);
/*     */       }
/*     */       
/* 129 */       byte[] bytes = new byte[len];
/* 130 */       this.rafDescriptions.read(bytes);
/*     */       
/* 132 */       return bytes;
/*     */     } catch (IOException e) {}
/* 134 */     return "".getBytes();
/*     */   }
/*     */   
/*     */   public void cacheAllDescriptions()
/*     */   {
/* 139 */     IpRange[] ranges = getIPFilter().getRanges();
/* 140 */     for (int i = 0; i < ranges.length; i++) {
/* 141 */       Object info = ((IpRangeImpl)ranges[i]).getDescRef();
/* 142 */       if ((info instanceof Integer)) {
/* 143 */         byte[] desc = getDescription(info);
/* 144 */         Object[] data = { desc, info };
/* 145 */         ((IpRangeImpl)ranges[i]).setDescRef(data);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearDescriptionCache() {
/* 151 */     IpRange[] ranges = getIPFilter().getRanges();
/* 152 */     for (int i = 0; i < ranges.length; i++) {
/* 153 */       Object info = ((IpRangeImpl)ranges[i]).getDescRef();
/* 154 */       if ((info instanceof Object[])) {
/* 155 */         Integer data = (Integer)((Object[])(Object[])info)[1];
/* 156 */         ((IpRangeImpl)ranges[i]).setDescRef(data);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void deleteAllDescriptions() {
/* 162 */     if (this.rafDescriptions != null) {
/*     */       try {
/* 164 */         this.rafDescriptions.close();
/*     */       }
/*     */       catch (IOException e) {}
/* 167 */       this.rafDescriptions = null;
/*     */     }
/*     */     
/* 170 */     parameterChanged(null);
/*     */   }
/*     */   
/*     */ 
/*     */   public static IpFilterManager getSingleton()
/*     */   {
/* 176 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */   public IpFilter getIPFilter()
/*     */   {
/* 182 */     return IpFilterImpl.getInstance();
/*     */   }
/*     */   
/*     */ 
/*     */   public BadIps getBadIps()
/*     */   {
/* 188 */     return BadIpsImpl.getInstance();
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName) {
/* 192 */     boolean enable = COConfigurationManager.getBooleanParameter("Ip Filter Enable Description Cache");
/* 193 */     if ((enable) && (this.rafDescriptions == null)) {
/* 194 */       File fDescriptions = FileUtil.getUserFile("ipfilter.cache");
/*     */       try {
/* 196 */         if (fDescriptions.exists()) {
/* 197 */           fDescriptions.delete();
/*     */         }
/* 199 */         this.rafDescriptions = new RandomAccessFile(fDescriptions, "rw");
/*     */       }
/*     */       catch (FileNotFoundException e) {
/* 202 */         e.printStackTrace();
/*     */       }
/* 204 */     } else if ((!enable) && (this.rafDescriptions != null)) {
/*     */       try {
/* 206 */         this.rafDescriptions.close();
/*     */       }
/*     */       catch (IOException e) {}
/* 209 */       this.rafDescriptions = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/IpFilterManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */