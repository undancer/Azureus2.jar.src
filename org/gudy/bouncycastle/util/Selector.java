package org.gudy.bouncycastle.util;

public abstract interface Selector
  extends Cloneable
{
  public abstract boolean match(Object paramObject);
  
  public abstract Object clone();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/Selector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */