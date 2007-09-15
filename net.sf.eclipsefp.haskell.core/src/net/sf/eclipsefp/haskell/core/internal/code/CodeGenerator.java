// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.core.internal.code;

import net.sf.eclipsefp.haskell.core.code.EHaskellCommentStyle;


/** <p>helping class that generates Haskell source code.</p>
  *
  * @author Leif Frenzel
  */
public class CodeGenerator {

  // TODO honor code template for generating this
  public String createModuleContent( final String[] folderNames,
                                     final String name,
                                     final EHaskellCommentStyle style )
  {
    StringBuffer sb = new StringBuffer();
    sb.append( getLineDelimiter() );
    sb.append(getPrefixFor( style ));
    sb.append( "module " );
    for( int i = 0; i < folderNames.length; i++ ) {
      sb.append( folderNames[ i ] );
      sb.append( "." );
    }
    sb.append( name );
    sb.append( " where" );
    sb.append( getLineDelimiter() );
    sb.append( getSuffixFor(style) );
    return sb.toString();
  }


  private static String getSuffixFor( final EHaskellCommentStyle style ) {
    if (EHaskellCommentStyle.TEX == style) {
      return "\\end{code}";
    }
    
    return "";
  }


  private static String getPrefixFor(final EHaskellCommentStyle style) {
    if (EHaskellCommentStyle.LITERATE == style) {
      return "> ";
    } else if (EHaskellCommentStyle.TEX == style) {
      return "\\begin{code}\n";
    } 

    return "";
  }

  
  // helping methods
  //////////////////
  
  private static String getLineDelimiter() {
    return System.getProperty( "line.separator", "\n" );    
  }
}
