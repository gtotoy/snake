/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.sync;

import java.nio.file.Path;
import java.util.ArrayList;
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
        descriptors_ = PathUtils.getRelativePathDescriptors(boxDirectory.toFile());
    }
    
    public void pull () {
        
    }
    
    public void push () {
        
    }
    
    ISnakeServer server_;
    String username_;
    Path boxDirectory_;
    ArrayList<PathDescriptor> descriptors_;
}
