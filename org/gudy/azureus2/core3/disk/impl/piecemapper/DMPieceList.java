package org.gudy.azureus2.core3.disk.impl.piecemapper;

public abstract interface DMPieceList
{
  public abstract int size();
  
  public abstract DMPieceMapEntry get(int paramInt);
  
  public abstract int getCumulativeLengthToPiece(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/DMPieceList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */