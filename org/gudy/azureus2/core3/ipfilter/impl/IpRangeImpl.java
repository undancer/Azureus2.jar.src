/*     */ package org.gudy.azureus2.core3.ipfilter.impl;
/*     */ 
/*     */ import java.net.UnknownHostException;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
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
/*     */ 
/*     */ public class IpRangeImpl
/*     */   implements IpRange
/*     */ {
/*     */   private static final byte FLAG_SESSION_ONLY = 1;
/*     */   private static final byte FLAG_ADDED_TO_RANGE_LIST = 2;
/*     */   private static final byte FLAG_INVALID_START = 8;
/*     */   private static final byte FLAG_INVALID_END = 16;
/*     */   private static final byte FLAG_INVALID = 24;
/*     */   private int ipStart;
/*     */   private int ipEnd;
/*     */   private byte flags;
/*  54 */   private Object descRef = null;
/*     */   
/*     */   private static final byte FLAG_MERGED = 4;
/*     */   
/*     */   private int merged_end;
/*     */   
/*     */   private IpRange[] my_merged_entries;
/*     */   
/*     */ 
/*     */   public IpRangeImpl(String _description, String _startIp, String _endIp, boolean _sessionOnly)
/*     */   {
/*  65 */     if (_sessionOnly) {
/*  66 */       this.flags = 1;
/*     */     }
/*     */     
/*  69 */     if ((_startIp == null) || (_endIp == null))
/*     */     {
/*  71 */       throw new RuntimeException("Invalid start/end values - null not supported");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  76 */       this.ipStart = PRHelpers.addressToInt(_startIp);
/*     */     } catch (UnknownHostException e) {
/*  78 */       this.flags = ((byte)(this.flags | 0x8));
/*     */     }
/*     */     try {
/*  81 */       this.ipEnd = PRHelpers.addressToInt(_endIp);
/*     */     } catch (UnknownHostException e) {
/*  83 */       this.flags = ((byte)(this.flags | 0x10));
/*     */     }
/*     */     
/*  86 */     if (_description.length() > 0) {
/*  87 */       setDescription(_description);
/*     */     }
/*     */     
/*  90 */     checkValid();
/*     */   }
/*     */   
/*     */   public IpRangeImpl(String _description, int _startIp, int _endIp, boolean _sessionOnly)
/*     */   {
/*  95 */     if (_sessionOnly) {
/*  96 */       this.flags = 1;
/*     */     }
/*     */     
/*  99 */     this.ipStart = _startIp;
/* 100 */     this.ipEnd = _endIp;
/*     */     
/* 102 */     if (_description.length() > 0) {
/* 103 */       setDescription(_description);
/*     */     }
/*     */     
/* 106 */     checkValid();
/*     */   }
/*     */   
/*     */   public void checkValid() {
/* 110 */     ((IpFilterImpl)IpFilterImpl.getInstance()).setValidOrNot(this, isValid());
/*     */   }
/*     */   
/*     */   public boolean isValid() {
/* 114 */     if ((this.flags & 0x18) > 0) {
/* 115 */       return false;
/*     */     }
/*     */     
/* 118 */     long start_address = this.ipStart;
/* 119 */     long end_address = this.ipEnd;
/*     */     
/* 121 */     if (start_address < 0L)
/*     */     {
/* 123 */       start_address += 4294967296L;
/*     */     }
/* 125 */     if (end_address < 0L)
/*     */     {
/* 127 */       end_address += 4294967296L;
/*     */     }
/*     */     
/* 130 */     return end_address >= start_address;
/*     */   }
/*     */   
/*     */   public boolean isInRange(String ipAddress) {
/* 134 */     if (!isValid()) {
/* 135 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 139 */       long int_address = PRHelpers.addressToInt(ipAddress);
/*     */       
/* 141 */       if (int_address < 0L)
/*     */       {
/* 143 */         int_address += 4294967296L;
/*     */       }
/*     */       
/* 146 */       long start_address = this.ipStart;
/* 147 */       long end_address = this.ipEnd;
/*     */       
/* 149 */       if (start_address < 0L)
/*     */       {
/* 151 */         start_address += 4294967296L;
/*     */       }
/* 153 */       if (end_address < 0L)
/*     */       {
/* 155 */         end_address += 4294967296L;
/*     */       }
/*     */       
/* 158 */       return (int_address >= start_address) && (int_address <= end_address);
/*     */     }
/*     */     catch (UnknownHostException e) {}
/*     */     
/* 162 */     return false;
/*     */   }
/*     */   
/*     */   public String getDescription()
/*     */   {
/* 167 */     return new String(IpFilterManagerFactory.getSingleton().getDescription(this.descRef));
/*     */   }
/*     */   
/*     */   public void setDescription(String str)
/*     */   {
/* 172 */     this.descRef = IpFilterManagerFactory.getSingleton().addDescription(this, str.getBytes());
/*     */   }
/*     */   
/*     */   public String getStartIp()
/*     */   {
/* 177 */     return (this.flags & 0x8) > 0 ? "" : PRHelpers.intToAddress(this.ipStart);
/*     */   }
/*     */   
/*     */   public long getStartIpLong()
/*     */   {
/* 182 */     if ((this.flags & 0x8) > 0) {
/* 183 */       return -1L;
/*     */     }
/*     */     
/* 186 */     long val = this.ipStart;
/*     */     
/* 188 */     if (val < 0L)
/*     */     {
/* 190 */       val += 4294967296L;
/*     */     }
/*     */     
/* 193 */     return val;
/*     */   }
/*     */   
/*     */   public void setStartIp(String str) {
/* 197 */     if (str == null) {
/* 198 */       throw new RuntimeException("Invalid start value - null not supported");
/*     */     }
/*     */     
/* 201 */     if (str.equals(getStartIp())) {
/* 202 */       return;
/*     */     }
/*     */     
/* 205 */     this.flags = ((byte)(this.flags & 0xFFFFFFF7));
/*     */     try {
/* 207 */       this.ipStart = PRHelpers.addressToInt(str);
/*     */     } catch (UnknownHostException e) {
/* 209 */       this.flags = ((byte)(this.flags | 0x8));
/*     */     }
/*     */     
/* 212 */     if ((this.flags & 0x18) == 0) {
/* 213 */       checkValid();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getEndIp() {
/* 218 */     return (this.flags & 0x10) > 0 ? "" : PRHelpers.intToAddress(this.ipEnd);
/*     */   }
/*     */   
/*     */   public long getEndIpLong() {
/* 222 */     if ((this.flags & 0x10) > 0) {
/* 223 */       return -1L;
/*     */     }
/*     */     
/* 226 */     long val = this.ipEnd;
/*     */     
/* 228 */     if (val < 0L) {
/* 229 */       val += 4294967296L;
/*     */     }
/*     */     
/* 232 */     return val;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEndIp(String str)
/*     */   {
/* 238 */     if (str == null) {
/* 239 */       throw new RuntimeException("Invalid end value - null not supported");
/*     */     }
/*     */     
/* 242 */     if (str.equals(getEndIp())) {
/* 243 */       return;
/*     */     }
/*     */     
/* 246 */     this.flags = ((byte)(this.flags & 0xFFFFFFEF));
/*     */     try {
/* 248 */       this.ipEnd = PRHelpers.addressToInt(str);
/*     */     } catch (UnknownHostException e) {
/* 250 */       this.flags = ((byte)(this.flags | 0x10));
/*     */     }
/*     */     
/* 253 */     if ((this.flags & 0x18) == 0) {
/* 254 */       checkValid();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 259 */     return getDescription() + " : " + getStartIp() + " - " + getEndIp();
/*     */   }
/*     */   
/*     */   public boolean isSessionOnly() {
/* 263 */     return (this.flags & 0x1) != 0;
/*     */   }
/*     */   
/*     */   public void setSessionOnly(boolean _sessionOnly) {
/* 267 */     if (_sessionOnly) {
/* 268 */       this.flags = ((byte)(this.flags | 0x1));
/*     */     } else {
/* 270 */       this.flags = ((byte)(this.flags & 0xFFFFFFFE));
/*     */     }
/*     */   }
/*     */   
/*     */   public int compareStartIpTo(IpRange other) {
/* 275 */     long l = getStartIpLong() - ((IpRangeImpl)other).getStartIpLong();
/*     */     
/* 277 */     if (l < 0L)
/* 278 */       return -1;
/* 279 */     if (l > 0L) {
/* 280 */       return 1;
/*     */     }
/* 282 */     return 0;
/*     */   }
/*     */   
/*     */   public int compareEndIpTo(IpRange other)
/*     */   {
/* 287 */     long l = getEndIpLong() - ((IpRangeImpl)other).getEndIpLong();
/*     */     
/* 289 */     if (l < 0L)
/* 290 */       return -1;
/* 291 */     if (l > 0L) {
/* 292 */       return 1;
/*     */     }
/* 294 */     return 0;
/*     */   }
/*     */   
/*     */   protected void setAddedToRangeList(boolean b) {
/* 298 */     if (b) {
/* 299 */       this.flags = ((byte)(this.flags | 0x2));
/*     */     } else {
/* 301 */       this.flags = ((byte)(this.flags & 0xFFFFFFFD));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean getAddedToRangeList() {
/* 306 */     return (this.flags & 0x2) != 0;
/*     */   }
/*     */   
/*     */   public int compareDescription(IpRange other) {
/* 310 */     return getDescription().compareTo(other.getDescription());
/*     */   }
/*     */   
/*     */   protected Object getDescRef() {
/* 314 */     return this.descRef;
/*     */   }
/*     */   
/*     */   protected void setDescRef(Object descRef) {
/* 318 */     this.descRef = descRef;
/*     */   }
/*     */   
/*     */   public long getMergedEndLong() {
/* 322 */     return this.merged_end < 0 ? this.merged_end + 4294967296L : this.merged_end;
/*     */   }
/*     */   
/*     */   public IpRange[] getMergedEntries() {
/* 326 */     return this.my_merged_entries;
/*     */   }
/*     */   
/*     */   public void resetMergeInfo() {
/* 330 */     this.flags = ((byte)(this.flags & 0xFFFFFFFB));
/*     */     
/* 332 */     if ((this.flags & 0x10) == 0) {
/* 333 */       this.merged_end = this.ipEnd;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean getMerged() {
/* 338 */     return (this.flags & 0x4) != 0;
/*     */   }
/*     */   
/*     */   public void setMerged() {
/* 342 */     this.flags = ((byte)(this.flags | 0x4));
/*     */   }
/*     */   
/*     */   public void setMergedEnd(long endIpLong) {
/* 346 */     this.merged_end = ((int)(endIpLong >= 4294967296L ? endIpLong - 4294967296L : endIpLong));
/*     */   }
/*     */   
/*     */   public void addMergedEntry(IpRange e2)
/*     */   {
/* 351 */     if (this.my_merged_entries == null)
/*     */     {
/* 353 */       this.my_merged_entries = new IpRange[] { e2 };
/*     */     }
/*     */     else
/*     */     {
/* 357 */       IpRange[] x = new IpRange[this.my_merged_entries.length + 1];
/*     */       
/* 359 */       System.arraycopy(this.my_merged_entries, 0, x, 0, this.my_merged_entries.length);
/*     */       
/* 361 */       x[(x.length - 1)] = e2;
/*     */       
/* 363 */       this.my_merged_entries = x;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/IpRangeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */