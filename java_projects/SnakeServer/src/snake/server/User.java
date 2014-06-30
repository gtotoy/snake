/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.Serializable;

/**
 *
 * @author Gustavo
 */
public class User implements Serializable {
    public String username;
    public String boxDirectory;
    
    public boolean hasUsername() {
        return username != null;
    }
    
    public boolean hasBoxDirectory() {
        return boxDirectory != null;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", boxDirectory=" + boxDirectory + '}';
    }
}
