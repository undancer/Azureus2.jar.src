package org.gudy.azureus2.core3.config;

public abstract interface StringList
{
  public abstract int size();
  
  public abstract String get(int paramInt);
  
  public abstract void add(String paramString);
  
  public abstract void add(int paramInt, String paramString);
  
  public abstract StringIterator iterator();
  
  public abstract int indexOf(String paramString);
  
  public abstract boolean contains(String paramString);
  
  public abstract String remove(int paramInt);
  
  public abstract String[] toArray();
  
  public abstract void clear();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/StringList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */