package org.gudy.azureus2.ui.swt.config;

public abstract interface ParameterChangeListener
{
  public abstract void parameterChanged(Parameter paramParameter, boolean paramBoolean);
  
  public abstract void intParameterChanging(Parameter paramParameter, int paramInt);
  
  public abstract void booleanParameterChanging(Parameter paramParameter, boolean paramBoolean);
  
  public abstract void stringParameterChanging(Parameter paramParameter, String paramString);
  
  public abstract void floatParameterChanging(Parameter paramParameter, double paramDouble);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/ParameterChangeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */