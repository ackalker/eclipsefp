/**
 *
 */
package net.sf.eclipsefp.haskell.debug.core.internal.launch.young;

import java.io.File;
import java.util.Random;
import net.sf.eclipsefp.haskell.debug.core.internal.HaskellDebugCore;
import net.sf.eclipsefp.haskell.debug.core.internal.util.CoreTexts;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.junit.model.JUnitModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


/**
 * @author Alejandro Serrano
 *
 */
public class TestSuiteLaunchDelegate extends BaseExecutableLaunchDelegate {
  static final String JUNIT_VIEW = "org.eclipse.jdt.junit.ResultView"; //$NON-NLS-1$

  String filename = null;

  private String getFilename() {
    if( filename == null ) {
      Random r = new Random( System.currentTimeMillis() );
      int n = r.nextInt();
      filename = HaskellDebugCore.getDefault().getStateLocation()
          .append( n + ".xml" ).toOSString(); //$NON-NLS-1$
    }
    return filename;
  }

  /* (non-Javadoc)
   * @see net.sf.eclipsefp.haskell.debug.core.internal.launch.young.AbstractHaskellLaunchDelegate#getUserArguments(org.eclipse.debug.core.ILaunchConfiguration)
   */
  @Override
  protected String getUserArguments( final ILaunchConfiguration config )
      throws CoreException {
    return "--plain --jxml=\"" + getFilename() + "\" --jxml-nested " + super.getUserArguments( config ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  protected void postProcessFinished() {
    // Get file and parse output
    final String fname = getFilename();
    final File file = new File( fname );
    // final TestSuiteAndSession session = TestSuiteAndSession.parseFile( file );

    if (canShowJUnit()) {
      Display.getDefault().syncExec( new Runnable() {

        public void run() {
          try {
            IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
            page.showView( JUNIT_VIEW );
            // JUnitCorePlugin.getModel().addTestRunSession( session );
            JUnitModel.importTestRunSession( file );
          } catch( CoreException e ) {
            // Do nothing
          }
        }
      } );
    } else {
      Display.getCurrent().syncExec( new Runnable() {

        public void run() {
          MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
              CoreTexts.jdt_notFound_title, CoreTexts.jdt_notFound_message );
        }
      } );
    }

    // Always delete the file at the end
    new File( getFilename() ).delete();
  }

  /* (non-Javadoc)
   * @see net.sf.eclipsefp.haskell.debug.core.internal.launch.AbstractHaskellLaunchDelegate#shouldContinue()
   */
  protected boolean canShowJUnit() {
    try {
      // Try to find JDT Core
      this.getClass().getClassLoader().loadClass( "org.eclipse.jdt.core.IJavaProject" ); //$NON-NLS-1$
      // Try to find JUnit model
      this.getClass().getClassLoader().loadClass( "org.eclipse.jdt.internal.junit.model.JUnitModel" ); //$NON-NLS-1$
      // Try to find JUnit view
      this.getClass().getClassLoader().loadClass( "org.eclipse.jdt.internal.junit.ui.TestRunnerViewPart" ); //$NON-NLS-1$
      // Everything is OK
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

}
