package org.gudy.azureus2.plugins.update;

public abstract interface UpdateManagerVerificationListener
{
  public abstract boolean acceptUnVerifiedUpdate(Update paramUpdate);
  
  public abstract void verificationFailed(Update paramUpdate, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/update/UpdateManagerVerificationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */