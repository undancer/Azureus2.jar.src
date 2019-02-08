package org.gudy.azureus2.core3.disk;

public abstract interface DiskManagerFileInfoSet
{
  public abstract boolean[] setStorageTypes(boolean[] paramArrayOfBoolean, int paramInt);
  
  public abstract void setPriority(int[] paramArrayOfInt);
  
  public abstract void setSkipped(boolean[] paramArrayOfBoolean, boolean paramBoolean);
  
  public abstract DiskManagerFileInfo[] getFiles();
  
  public abstract int nbFiles();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerFileInfoSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */