package org.gudy.azureus2.ui.swt.progress;

import org.eclipse.swt.graphics.Image;

public abstract interface IProgressReporter
  extends Comparable
{
  public abstract void setReporterType(String paramString);
  
  public abstract void dispose();
  
  public abstract IProgressReport getProgressReport();
  
  public abstract void setSelection(int paramInt, String paramString);
  
  public abstract void setPercentage(int paramInt, String paramString);
  
  public abstract void setIndeterminate(boolean paramBoolean);
  
  public abstract void setDone();
  
  public abstract void setMinimum(int paramInt);
  
  public abstract void setMaximum(int paramInt);
  
  public abstract void cancel();
  
  public abstract void retry();
  
  public abstract void setCancelAllowed(boolean paramBoolean);
  
  public abstract void setCancelCloses(boolean paramBoolean);
  
  public abstract boolean getCancelCloses();
  
  public abstract void addListener(IProgressReporterListener paramIProgressReporterListener);
  
  public abstract void removeListener(IProgressReporterListener paramIProgressReporterListener);
  
  public abstract void setName(String paramString);
  
  public abstract void setTitle(String paramString);
  
  public abstract void setImage(Image paramImage);
  
  public abstract void setErrorMessage(String paramString);
  
  public abstract void setMessage(String paramString);
  
  public abstract void appendDetailMessage(String paramString);
  
  public abstract void setRetryAllowed(boolean paramBoolean);
  
  public abstract void setObjectData(Object paramObject);
  
  public abstract IMessage[] getMessageHistory();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/IProgressReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */