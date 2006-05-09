// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.core;

import org.eclipse.core.runtime.*;
import org.osgi.framework.BundleContext;

import net.sf.eclipsefp.haskell.core.compiler.CompilerManager;
import net.sf.eclipsefp.haskell.core.halamo.Halamo;
import net.sf.eclipsefp.haskell.core.parser.ParserManager;
import net.sf.eclipsefp.haskell.core.preferences.ICorePreferenceNames;


/** The main plugin class to be used in the desktop.
  * 
  * @author The mighty PDE wizard 
  */
public class HaskellCorePlugin extends Plugin {

  // extension points
  public static final String ID_EXT_POINT_COMPILERS = "haskellCompilers";
  public static final String ID_EXT_POINT_PARSERS   = "haskellParsers";
  public static final String ID_PROBLEM_MARKER = "net.sf.eclipsefp.haskell.core.problem";
  
  private static HaskellCorePlugin plugin;
  
  
  public HaskellCorePlugin() {
    plugin = this;
  }

  public void start( final BundleContext context ) throws Exception {
    super.start( context );
    
    collectCompilerInfo();
    collectParserInfo();
    try {
      // TODO temp (to register as listener)
      Halamo.getInstance().initialize();
    } catch( CoreException ex ) {
      String message =   "Serious problem: could not initialize the Haskell "
                       + "language model.";
      HaskellCorePlugin.log( message, ex );
    }
  }
  
  /** <p>returns the shared instance.</p> */
  public static HaskellCorePlugin getDefault() {
    return plugin;
  }

  public static String getPluginId() {
    return getDefault().getBundle().getSymbolicName();
  }


  // logging and tracing
  //////////////////////
  
  public static void log( final String message, final int severity ) {
    String id = getPluginId();
    Status status = new Status( severity, id, IStatus.OK, message, null );
    getDefault().getLog().log( status );
  }

  public static void log( final String message, final Throwable thr ) {
    String id = getPluginId();
    Status status = new Status( IStatus.ERROR, id, IStatus.OK, message, thr );
    getDefault().getLog().log( status );
  }
  
  public static boolean isTracing( final String optionId ) {
    String option = getPluginId() + "/trace/" + optionId;
    String value = Platform.getDebugOption( option );
    return value != null && value.equals( "true" );
  }
  
  
  // helping methods
  //////////////////
  
  /** reads compiler infos out of the extensions declared in the manifest
    * and registers them with the compiler manager.
    * 
    * All compiler management is delegated to the CompilerManager singleton.
    */ 
  private void collectCompilerInfo() {
    IConfigurationElement[] elements = getExtensions( ID_EXT_POINT_COMPILERS );
    for( int i = 0; i < elements.length; i++ ) {
      String compilerId = elements[ i ].getAttribute( "id" );
      CompilerManager.getInstance().registerCompiler( compilerId, 
                                                      elements[ i ] );
    }
    String pref = "";
    try {
      String name = ICorePreferenceNames.SELECTED_COMPILER;
      pref = getPluginPreferences().getString( name );
      CompilerManager.getInstance().selectCompiler( pref );
    } catch ( Exception ex ) {
      String msg = "Problem when selecting compiler '" + pref + "'.";
      HaskellCorePlugin.log( msg, ex );
    }
  }

  private void collectParserInfo() {
    IConfigurationElement[] elements = getExtensions( ID_EXT_POINT_PARSERS );
    for( int i = 0; i < elements.length; i++ ) {
      try {
        String parserId = elements[ i ].getAttribute( "id" );
        if( parserId == null ) {
          String msg =   "Haskell parser declaration is missing id attribute "
                       + "and will be ignored.";
          log( msg, null ); 
        } else {
          ParserManager.getInstance().registerParser( parserId, elements[ i ] );
        }
      } catch( final CoreException ex ) {
        getLog().log( ex.getStatus() );
      }
    }
  }  
  
  private IConfigurationElement[] getExtensions( final String key ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    return registry.getConfigurationElementsFor( getPluginId(), key );
  }
}