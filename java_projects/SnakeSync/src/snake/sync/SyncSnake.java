/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.sync;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import snake.server.ISnakeServer;
import snake.server.PathDescriptor;
import snake.server.PathUtils;
import snake.server.Settings;

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
        ArrayList<PathDescriptor> newDescriptors = PathUtils.getRelativePathDescriptors(boxDirectory_.toFile(), false);
        PathDescriptor.updateDescriptors(descriptors_, newDescriptors);
    }
    
    public void pull() {
        ArrayList<PathDescriptor> localDescriptors = descriptors_;
        try {
            synchronized (server_.readLock()) {
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
            }
        } catch(Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void push () {
        Path tempPath = null;
        try {
            tempPath = Files.createTempDirectory(Settings.tempDirectory);
            FileUtils.copyDirectory(boxDirectory_.toFile(), tempPath.toFile(), true);
            ArrayList<PathDescriptor> localDescriptors = new ArrayList<>(descriptors_);
            Path localDirectory = tempPath;
            
            synchronized (server_.writeLock()) {
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
                            new FileInputStream(localDirectory.resolve(localPD.relative_path).toFile()));
                            try {
                                if (serverPD != null) {
                                    if ( serverPD.status == PathDescriptor.Status.Deleted || 
                                            serverPD.lastModified.before(localPD.lastModified))
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
            }
        } catch(Exception e) {
            System.err.println(e.toString());
        } finally {
            try {
                if (tempPath != null) FileUtils.deleteDirectory(tempPath.toFile());
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
    
    ISnakeServer server_;
    String username_;
    Path boxDirectory_;
    ArrayList<PathDescriptor> descriptors_;
}
