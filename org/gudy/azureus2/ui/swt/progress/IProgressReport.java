package org.gudy.azureus2.ui.swt.progress;

import org.eclipse.swt.graphics.Image;

public abstract interface IProgressReport
{
  public abstract IProgressReporter getReporter();
  
  public abstract String getReporterType();
  
  public abstract int getReporterID();
  
  public abstract int getMinimum();
  
  public abstract int getMaximum();
  
  public abstract int getSelection();
  
  public abstract int getPercentage();
  
  public abstract boolean isActive();
  
  public abstract boolean isIndeterminate();
  
  public abstract boolean isDone();
  
  public abstract boolean isPercentageInUse();
  
  public abstract boolean isCancelAllowed();
  
  public abstract boolean isCanceled();
  
  public abstract boolean isRetryAllowed();
  
  public abstract boolean isInErrorState();
  
  public abstract boolean isDisposed();
  
  public abstract String getTitle();
  
  public abstract String getMessage();
  
  public abstract String getDetailMessage();
  
  public abstract String getErrorMessage();
  
  public abstract String getName();
  
  public abstract Image getImage();
  
  public abstract Object getObjectData();
  
  public abstract int getReportType();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/IProgressReport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */