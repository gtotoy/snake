/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.sync;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import snake.server.ISnakeServer;
import snake.server.PathDescriptor;
import snake.server.PathUtils;

/**
 *
 * @author Gustavo
 */
public class SyncSnake {
    public SyncSnake(ISnakeServer server, String username, Path boxDirectory) {
        server_ = server;
        username_ = username;
        boxDirectory_ = boxDirectory;
        descriptors_ = PathUtils.getRelativePathDescriptors(boxDirectory.toFile(), true);
    }
    
    
    private void receiveFile(PathDescriptor descriptor, RemoteInputStream ristream) throws IOException {
        PathUtils.receiveFile(boxDirectory_, descriptors_, descriptor, ristream);
    }
    
    public void updateDescriptors() {
        System.out.println("Before Update");
        System.out.println(descriptors_.toString());
        ArrayList<PathDescriptor> newDescriptors = PathUtils.getRelativePathDescriptors(boxDirectory_.toFile(), false);
        int currentSize = descriptors_.size();
        for (int i = 0; i < currentSize; ++i) {
            PathDescriptor current = descriptors_.get(i);
            PathDescriptor newDescriptor = PathDescriptor.FindIfEqualRelativePath(newDescriptors, current);
            if (newDescriptor == null) {
                switch(current.status) {
                    case Deleted:
                        break;
                    case Modified:
                        current.status = PathDescriptor.Status.Deleted;
                        current.statusTimePoint = new Date();
                        break;
                }
            } else {
                switch(current.status) {
                    case Deleted:
                        current.lastCreationTime = new Date();
                    case Modified:
                        current.status = PathDescriptor.Status.Modified;
                        current.statusTimePoint = newDescriptor.statusTimePoint;
                        current.lastModified = newDescriptor.lastModified;
                        break;
                }
            }
        }
        
        int newSize = newDescriptors.size();
        for (int i = 0; i < newSize; ++i) {
            PathDescriptor newDescriptor = newDescriptors.get(i);
            PathDescriptor current = PathDescriptor.FindIfEqualRelativePath(descriptors_, newDescriptor);
            if (current == null) {
                newDescriptor.statusTimePoint = new Date();
                descriptors_.add(newDescriptor);
            }
        }
        
        System.out.println("After Update");
        System.out.println(descriptors_.toString());
    }
    
    public void pull() {
        ArrayList<PathDescriptor> localDescriptors = descriptors_;
        try {
            server_.startPull(username_);
            ArrayList<PathDescriptor> serverDescriptors = server_.getRelativeDescriptors(username_);
            for (int i = 0; i < serverDescriptors.size(); ++i) {
                PathDescriptor serverPD = serverDescriptors.get(i);
                PathDescriptor localPD = PathDescriptor.FindIfEqualRelativePath(localDescriptors, serverPD);
                switch (serverPD.status) {
                    case Deleted:
                        if (localPD != null && localPD.status == PathDescriptor.Status.Modified 
                                && localPD.lastModified.before(serverPD.statusTimePoint)
                                && localPD.lastCreationTime.before(serverPD.statusTimePoint))
                            PathUtils.deleteFile(boxDirectory_, localDescriptors, localPD.relative_path);
                        break;
                    case Modified:
                        if (localPD != null) {
                            if (localPD.lastModified.before(serverPD.lastModified)) {
                                receiveFile(serverPD, server_.sendFile(username_, serverPD));
                            }
                        } else {
                          receiveFile(serverPD, server_.sendFile(username_, serverPD));
                        }
                        break;
                }
            }
            server_.endPull(username_);
        } catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void push () {
        ArrayList<PathDescriptor> localDescriptors = descriptors_;
        try {
            server_.startPush(username_);
            ArrayList<PathDescriptor> serverDescriptors = server_.getRelativeDescriptors(username_);
            for (int i = 0; i < localDescriptors.size(); ++i) {
                PathDescriptor localPD = localDescriptors.get(i);
                PathDescriptor serverPD = PathDescriptor.FindIfEqualRelativePath(serverDescriptors, localPD);
                switch (localPD.status) {
                    case Deleted:
                        if (serverPD != null && serverPD.status == PathDescriptor.Status.Modified
                                && serverPD.lastModified.before(localPD.statusTimePoint) )
                            server_.deleteFile(username_, serverPD);
                        break;
                    case Modified:
                        SimpleRemoteInputStream istream = new SimpleRemoteInputStream(
                        new FileInputStream(boxDirectory_.resolve(localPD.relative_path).toFile()));
                        try {
                            if (serverPD != null) {
                                if ( serverPD.status == PathDescriptor.Status.Deleted || serverPD.lastModified.before(localPD.lastModified))
                                    server_.receiveFile(username_, localPD, istream.export());
                            } else {
                              server_.receiveFile(username_, localPD, istream.export());
                            }
                        } finally {
                          istream.close();
                        }
                        break;
                }
            }
            server_.endPush(username_);
        } catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    ISnakeServer server_;
    String username_;
    Path boxDirectory_;
    ArrayList<PathDescriptor> descriptors_;
}
