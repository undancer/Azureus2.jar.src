/*     */ package com.aelitis.azureus.core.peermanager.piecepicker.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.HashCodeUtils;
/*     */ import java.util.Arrays;
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
/*     */ public class BitFlags
/*     */   implements Cloneable
/*     */ {
/*     */   public int start;
/*     */   public int end;
/*     */   public int nbSet;
/*     */   public final boolean[] flags;
/*     */   
/*     */   public BitFlags(int count)
/*     */   {
/*  49 */     this.start = count;
/*  50 */     this.end = 0;
/*  51 */     this.nbSet = 0;
/*  52 */     this.flags = new boolean[count];
/*     */   }
/*     */   
/*     */   public BitFlags(boolean[] _flags)
/*     */   {
/*  57 */     this.start = _flags.length;
/*  58 */     this.flags = _flags;
/*  59 */     for (int i = 0; i < this.flags.length; i++) {
/*  60 */       if (this.flags[i] != 0) {
/*  61 */         this.nbSet += 1;
/*  62 */         if (i < this.start) {
/*  63 */           this.start = i;
/*     */         }
/*  65 */         this.end = i;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public BitFlags(BitFlags other)
/*     */   {
/*  73 */     this.start = other.start;
/*  74 */     this.end = other.end;
/*  75 */     this.nbSet = other.nbSet;
/*  76 */     this.flags = ((boolean[])other.flags.clone());
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/*  81 */     return new BitFlags(this);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/*  86 */     int result = HashCodeUtils.hashMore(0, this.flags);
/*  87 */     result = HashCodeUtils.hashMore(result, this.nbSet);
/*  88 */     result = HashCodeUtils.hashMore(result, this.end);
/*  89 */     return HashCodeUtils.hashMore(result, this.start);
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/*  94 */     if ((o == null) || (!(o instanceof BitFlags)))
/*  95 */       return false;
/*  96 */     BitFlags other = (BitFlags)o;
/*  97 */     if (this.start != other.start)
/*  98 */       return false;
/*  99 */     if (this.end != other.end)
/* 100 */       return false;
/* 101 */     if (this.nbSet != other.nbSet)
/* 102 */       return false;
/* 103 */     if ((this.flags == null) && (other.flags == null))
/* 104 */       return true;
/* 105 */     if ((this.flags == null) || (other.flags == null))
/* 106 */       return false;
/* 107 */     if (this.flags.length != other.flags.length)
/* 108 */       return false;
/* 109 */     for (int i = 0; i < this.flags.length; i++)
/*     */     {
/* 111 */       if ((this.flags[i] ^ other.flags[i]) != 0) {
/* 112 */         return false;
/*     */       }
/*     */     }
/* 115 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 123 */     return this.flags.length;
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 128 */     Arrays.fill(this.flags, false);
/* 129 */     this.start = this.flags.length;
/* 130 */     this.end = 0;
/* 131 */     this.nbSet = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setStart(int i)
/*     */   {
/* 137 */     this.flags[i] = true;
/* 138 */     this.nbSet += 1;
/* 139 */     this.start = i;
/*     */   }
/*     */   
/*     */ 
/*     */   public void set(int i)
/*     */   {
/* 145 */     if (this.flags[i] == 0)
/*     */     {
/* 147 */       this.flags[i] = true;
/* 148 */       this.nbSet += 1;
/* 149 */       if (this.start > i)
/* 150 */         this.start = i;
/* 151 */       if (this.end < i) {
/* 152 */         this.end = i;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setEnd(int i)
/*     */   {
/* 159 */     this.flags[i] = true;
/* 160 */     this.nbSet += 1;
/* 161 */     this.end = i;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setOnly(int i)
/*     */   {
/* 167 */     if (this.start < this.flags.length)
/* 168 */       Arrays.fill(this.flags, this.start, this.end, false);
/* 169 */     this.nbSet = 1;
/* 170 */     this.start = i;
/* 171 */     this.end = i;
/* 172 */     this.flags[i] = true;
/*     */   }
/*     */   
/*     */   public void setAll()
/*     */   {
/* 177 */     this.start = 0;
/* 178 */     this.end = (this.flags.length - 1);
/* 179 */     Arrays.fill(this.flags, true);
/* 180 */     this.nbSet = this.flags.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BitFlags and(BitFlags other)
/*     */   {
/* 191 */     BitFlags result = new BitFlags(this.flags.length);
/* 192 */     if ((this.nbSet > 0) && (other.nbSet > 0))
/*     */     {
/*     */ 
/* 195 */       int i = this.start > other.start ? this.start : other.start;
/* 196 */       int endI = this.end < other.end ? this.end : other.end;
/* 198 */       for (; 
/* 198 */           i <= endI; i++)
/*     */       {
/* 200 */         if ((this.flags[i] != 0) && (other.flags[i] != 0))
/*     */         {
/* 202 */           result.flags[i] = true;
/* 203 */           result.nbSet += 1;
/* 204 */           result.start = i;
/* 205 */           break;
/*     */         }
/*     */       }
/* 209 */       for (; 
/* 209 */           i <= endI; i++)
/*     */       {
/* 211 */         if ((this.flags[i] != 0) && (other.flags[i] != 0))
/*     */         {
/* 213 */           result.flags[i] = true;
/* 214 */           result.nbSet += 1;
/* 215 */           result.end = i;
/*     */         }
/*     */       }
/* 218 */       if (result.end < result.start)
/* 219 */         result.end = result.start;
/*     */     }
/* 221 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/util/BitFlags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */