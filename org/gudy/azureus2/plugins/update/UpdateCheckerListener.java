package org.gudy.azureus2.plugins.update;

public abstract interface UpdateCheckerListener
{
  public abstract void completed(UpdateChecker paramUpdateChecker);
  
  public abstract void failed(UpdateChecker paramUpdateChecker);
  
  public abstract void cancelled(UpdateChecker paramUpdateChecker);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateCheckerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */