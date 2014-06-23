/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package examples.stream;

import java.io.IOException;
  import java.rmi.Remote;
  
  import com.healthmarketscience.rmiio.RemoteInputStream;
  
  /**
   * A simple Remote interface for an RMI server which consumes a
   * RemoteInputStream.
   *
   * @author James Ahlborn
   */
  public interface RemoteFileServer extends Remote
  {
  
    public void sendFile(RemoteInputStream ristream) throws IOException;
  
  }

