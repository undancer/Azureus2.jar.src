package org.gudy.azureus2.plugins.update;

public abstract interface UpdatableComponent
{
  public abstract String getName();
  
  public abstract int getMaximumCheckTime();
  
  public abstract void checkForUpdate(UpdateChecker paramUpdateChecker);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdatableComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */