/*     */ package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.Coordinates;
/*     */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.VivaldiPosition;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
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
/*     */ public class VivaldiPositionImpl
/*     */   implements VivaldiPosition
/*     */ {
/*     */   private static final float cc = 0.25F;
/*     */   private static final float ce = 0.5F;
/*     */   private static final float initial_error = 10.0F;
/*     */   private HeightCoordinatesImpl coordinates;
/*     */   private float error;
/*     */   private int nbUpdates;
/*     */   
/*     */   public VivaldiPositionImpl(HeightCoordinatesImpl coordinates)
/*     */   {
/*  49 */     this.coordinates = coordinates;
/*  50 */     this.error = 10.0F;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getPositionType()
/*     */   {
/*  56 */     return 1;
/*     */   }
/*     */   
/*     */   public Coordinates getCoordinates() {
/*  60 */     return this.coordinates;
/*     */   }
/*     */   
/*     */   public double[] getLocation() {
/*  64 */     Coordinates coords = getCoordinates();
/*     */     
/*  66 */     return coords.getCoordinates();
/*     */   }
/*     */   
/*  69 */   public float getErrorEstimate() { return this.error; }
/*     */   
/*     */   public void setErrorEstimate(float error)
/*     */   {
/*  73 */     this.error = error;
/*     */   }
/*     */   
/*     */ 
/*     */   public void update(float rtt, Coordinates cj, float ej)
/*     */   {
/*  79 */     if ((valid(rtt)) && (valid(ej)) && (cj.isValid()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  84 */       if ((rtt <= 0.0F) || (rtt > 300000.0F)) return;
/*  85 */       if (this.error + ej == 0.0F) { return;
/*     */       }
/*     */       
/*  88 */       float w = this.error / (ej + this.error);
/*     */       
/*     */ 
/*  91 */       float re = rtt - this.coordinates.distance(cj);
/*     */       
/*     */ 
/*  94 */       float es = Math.abs(re) / rtt;
/*     */       
/*     */ 
/*     */ 
/*  98 */       float new_error = es * 0.5F * w + this.error * (1.0F - 0.5F * w);
/*     */       
/*     */ 
/*     */ 
/* 102 */       float delta = 0.25F * w;
/*     */       
/* 104 */       float scale = delta * re;
/*     */       
/* 106 */       HeightCoordinatesImpl random_error = new HeightCoordinatesImpl((float)Math.random() / 10.0F, (float)Math.random() / 10.0F, 0.0F);
/*     */       
/* 108 */       HeightCoordinatesImpl new_coordinates = (HeightCoordinatesImpl)this.coordinates.add(this.coordinates.sub(cj.add(random_error)).unity().scale(scale));
/*     */       
/* 110 */       if ((valid(new_error)) && (new_coordinates.isValid()))
/*     */       {
/* 112 */         this.coordinates = new_coordinates;
/*     */         
/* 114 */         this.error = (new_error > 0.1F ? new_error : 0.1F);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 123 */         this.coordinates = new HeightCoordinatesImpl(0.0F, 0.0F, 0.0F);
/* 124 */         this.error = 10.0F;
/*     */       }
/*     */       
/* 127 */       if (!cj.atOrigin()) {
/* 128 */         this.nbUpdates += 1;
/*     */       }
/* 130 */       if (this.nbUpdates > 5) {
/* 131 */         this.nbUpdates = 0;
/* 132 */         update(10.0F, new HeightCoordinatesImpl(0.0F, 0.0F, 0.0F), 50.0F);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 143 */     return (!Float.isNaN(getErrorEstimate())) && (getCoordinates().isValid());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean valid(float f)
/*     */   {
/* 150 */     return (!Float.isInfinite(f)) && (!Float.isNaN(f));
/*     */   }
/*     */   
/*     */   public void update(float rtt, float[] data)
/*     */   {
/* 155 */     update(rtt, new HeightCoordinatesImpl(data[0], data[1], data[2]), data[3]);
/*     */   }
/*     */   
/*     */   public float estimateRTT(Coordinates coordinates) {
/* 159 */     return this.coordinates.distance(coordinates);
/*     */   }
/*     */   
/*     */   public float[] toFloatArray() {
/* 163 */     return new float[] { this.coordinates.getX(), this.coordinates.getY(), this.coordinates.getH(), this.error };
/*     */   }
/*     */   
/*     */   public void fromFloatArray(float[] data)
/*     */   {
/* 168 */     this.coordinates = new HeightCoordinatesImpl(data[0], data[1], data[2]);
/*     */     
/* 170 */     this.error = data[3];
/*     */   }
/*     */   
/*     */   public String toString() {
/* 174 */     return this.coordinates + " : " + this.error;
/*     */   }
/*     */   
/*     */   public boolean equals(Object arg0) {
/* 178 */     if ((arg0 instanceof VivaldiPositionImpl)) {
/* 179 */       VivaldiPositionImpl other = (VivaldiPositionImpl)arg0;
/* 180 */       if (other.error != this.error) return false;
/* 181 */       if (!other.coordinates.equals(this.coordinates)) return false;
/* 182 */       return true;
/*     */     }
/* 184 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public float estimateRTT(DHTNetworkPosition _other)
/*     */   {
/* 191 */     VivaldiPosition other = (VivaldiPosition)_other;
/*     */     
/* 193 */     Coordinates other_coords = other.getCoordinates();
/*     */     
/* 195 */     if ((this.coordinates.atOrigin()) || (other_coords.atOrigin()))
/*     */     {
/* 197 */       return NaN.0F;
/*     */     }
/*     */     
/* 200 */     return estimateRTT(other_coords);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] _other_id, DHTNetworkPosition _other, float rtt)
/*     */   {
/* 209 */     VivaldiPositionImpl other = (VivaldiPositionImpl)_other;
/*     */     
/* 211 */     Coordinates other_coords = other.getCoordinates();
/*     */     
/* 213 */     update(rtt, other_coords, other.getErrorEstimate());
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSerialisedSize()
/*     */   {
/* 219 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 228 */     float[] data = toFloatArray();
/*     */     
/* 230 */     for (int i = 0; i < data.length; i++)
/*     */     {
/* 232 */       os.writeFloat(data[i]);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/impl/VivaldiPositionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */