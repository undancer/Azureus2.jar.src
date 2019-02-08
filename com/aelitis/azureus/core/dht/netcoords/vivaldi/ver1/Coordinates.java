package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1;

public abstract interface Coordinates
{
  public static final float MAX_X = 30000.0F;
  public static final float MAX_Y = 30000.0F;
  public static final float MAX_H = 30000.0F;
  
  public abstract Coordinates add(Coordinates paramCoordinates);
  
  public abstract Coordinates sub(Coordinates paramCoordinates);
  
  public abstract Coordinates scale(float paramFloat);
  
  public abstract float measure();
  
  public abstract float distance(Coordinates paramCoordinates);
  
  public abstract Coordinates unity();
  
  public abstract double[] getCoordinates();
  
  public abstract boolean atOrigin();
  
  public abstract boolean isValid();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/Coordinates.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */