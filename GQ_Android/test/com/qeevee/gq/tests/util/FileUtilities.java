package com.qeevee.gq.tests.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtilities {

  private FileUtilities() {}
  
  public static final void copy( File source, File destination ) throws IOException {
    if( source.isDirectory() ) {
      copyDirectory( source, destination );
    } else {
      copyFile( source, destination );
    }
  }
  
  public static final void copyDirectory( File source, File destination ) throws IOException {
    if( !source.isDirectory() ) {
      throw new IllegalArgumentException( "Source (" + source.getPath() + ") must be a directory." );
    }
    
    if( !source.exists() ) {
      throw new IllegalArgumentException( "Source directory (" + source.getPath() + ") doesn't exist." );
    }
    
    if( destination.exists() ) {
      throw new IllegalArgumentException( "Destination (" + destination.getPath() + ") exists." );
    }
    
    destination.mkdirs();
    File[] files = source.listFiles();
    
    for( File file : files ) {
      if( file.isDirectory() ) {
        copyDirectory( file, new File( destination, file.getName() ) );
      } else {
        copyFile( file, new File( destination, file.getName() ) );
      }
    }
  }
  
  public static final void copyFile( File source, File destination ) throws IOException {
    FileChannel sourceChannel = new FileInputStream( source ).getChannel();
    FileChannel targetChannel = new FileOutputStream( destination ).getChannel();
    sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
    sourceChannel.close();
    targetChannel.close();
  }
  
  // Deletes all files and subdirectories under dir.
  // Returns true if all deletions were successful.
  // If a deletion fails, the method stops attempting to delete and returns false.
  public static boolean deleteDirectory(File dir) {
      if (dir.isDirectory()) {
          String[] children = dir.list();
          for (int i=0; i<children.length; i++) {
              boolean success = deleteDirectory(new File(dir, children[i]));
              if (!success) {
                  return false;
              }
          }
      }
  
      // The directory is now empty so delete it
      return dir.delete();
  }
  
}