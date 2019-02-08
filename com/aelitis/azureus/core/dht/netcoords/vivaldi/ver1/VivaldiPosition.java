package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1;

import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;

public abstract interface VivaldiPosition
  extends DHTNetworkPosition
{
  public static final int CONVERGE_EVERY = 5;
  public static final float CONVERGE_FACTOR = 50.0F;
  public static final float ERROR_MIN = 0.1F;
  public static final int FLOAT_ARRAY_SIZE = 4;
  
  public abstract Coordinates getCoordinates();
  
  public abstract float getErrorEstimate();
  
  public abstract void setErrorEstimate(float paramFloat);
  
  public abstract void update(float paramFloat1, Coordinates paramCoordinates, float paramFloat2);
  
  public abstract void update(float paramFloat, float[] paramArrayOfFloat);
  
  public abstract float estimateRTT(Coordinates paramCoordinates);
  
  public abstract float[] toFloatArray();
  
  public abstract void fromFloatArray(float[] paramArrayOfFloat);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/VivaldiPosition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */