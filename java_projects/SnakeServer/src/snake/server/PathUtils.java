/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.File;
import java.nio.file.Path;
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
    
    public static ArrayList<PathDescriptor> getRelativePathDescriptors(File folder) {
        ArrayList<PathDescriptor> descriptors = new ArrayList<PathDescriptor>();
        Iterator<File> filesIter = FileUtils.iterateFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        while ( filesIter.hasNext() ) {
            Path folderPath = folder.toPath();
            File file = filesIter.next();
            Path filePath = file.toPath();
            Path relativePath = folderPath.relativize(filePath);
            
            PathDescriptor descriptor = new PathDescriptor();
            descriptor.relative_path = relativePath.toString();
            descriptor.status = PathDescriptor.Status.Modified;
            descriptor.statusTimePoint = new Date(file.lastModified());
            
            descriptors.add(descriptor);
        }
        return descriptors;
    }
}
