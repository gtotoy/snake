/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author Gustavo
 */
public class PathUtils {
    
    public static ArrayList<PathDescriptor> getRelativePathDescriptors(File folder, boolean useSystemCreationTime) {
        ArrayList<PathDescriptor> descriptors = new ArrayList<PathDescriptor>();
        Iterator<File> filesIter = FileUtils.iterateFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        while ( filesIter.hasNext() ) {
            Path folderPath = folder.toPath();
            File file = filesIter.next();
            Path filePath = file.toPath().toAbsolutePath();
            Path relativePath = folderPath.relativize(filePath);
            
            PathDescriptor descriptor = new PathDescriptor();
            descriptor.relative_path = relativePath.toString();
            descriptor.status = PathDescriptor.Status.Modified;
            descriptor.statusTimePoint = new Date();
            descriptor.lastModified = new Date(file.lastModified());
            if (useSystemCreationTime) {
                BasicFileAttributes attributes = null;
		try {
                    attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
                    descriptor.lastCreationTime = new Date(attributes.creationTime().toMillis());
		} catch(Exception e) {
                    descriptor.lastCreationTime = descriptor.statusTimePoint;
		}
            } else {
                descriptor.lastCreationTime = descriptor.statusTimePoint;
            }
            descriptors.add(descriptor);
        }
        return descriptors;
    }
    
    public static void deleteFile(Path localDirectory, ArrayList<PathDescriptor> localDescriptors, String localRelativePath) {
        localDirectory.resolve(localRelativePath).toFile().delete();
        PathDescriptor inPD = new PathDescriptor();
        inPD.relative_path = localRelativePath;
        PathDescriptor localPD = PathDescriptor.FindIfEqualRelativePath(localDescriptors, inPD);
        localPD.status = PathDescriptor.Status.Deleted;
        localPD.statusTimePoint = new Date();
    }
    
    public static void receiveFile(Path localDirectory, ArrayList<PathDescriptor> localDescriptors, PathDescriptor inDescriptor, RemoteInputStream ristream) throws IOException {
        InputStream istream = RemoteInputStreamClient.wrap(ristream);
        FileOutputStream ostream = null;
        Path filePath = localDirectory.resolve(inDescriptor.relative_path).toAbsolutePath();
        File file = filePath.toFile();
        try {
          file.getParentFile().mkdirs();
          file.createNewFile();
          ostream = new FileOutputStream(file);
          byte[] buffer = new byte[1024];
          int bytesRead = 0;
          while((bytesRead = istream.read(buffer)) >= 0) {
            ostream.write(buffer, 0, bytesRead);
          }
          ostream.flush();
        } finally {
          try {
            if(istream != null) {
              istream.close();
            }
          } finally {
            if(ostream != null) {
              ostream.close();
            }
          }
          file.setLastModified(inDescriptor.lastModified.getTime());
          PathDescriptor localPD = PathDescriptor.FindIfEqualRelativePath(localDescriptors, inDescriptor);
          if (localPD != null) {
              localPD.status = PathDescriptor.Status.Modified;
              localPD.statusTimePoint = new Date();
              localPD.lastModified = inDescriptor.lastModified;
              localPD.lastCreationTime = inDescriptor.lastCreationTime;
          } else {
              localPD = new PathDescriptor();
              localPD.relative_path = inDescriptor.relative_path;
              localPD.status = PathDescriptor.Status.Modified;
              localPD.statusTimePoint = new Date();
              localPD.lastModified = inDescriptor.lastModified;
              localPD.lastCreationTime = inDescriptor.lastCreationTime;
              localDescriptors.add(localPD);
          }
        }
    }
}
