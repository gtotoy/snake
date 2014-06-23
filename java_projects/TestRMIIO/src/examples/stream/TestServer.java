/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package examples.stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
  
  /**
   * Simple example server which can be the target of a streamed file.
   *
   * @author James Ahlborn
   */
  public class TestServer {
  
    public static final int REGISTRY_PORT = Registry.REGISTRY_PORT;
  
    public static class FileServer
      implements RemoteFileServer
    {
      public void sendFile(RemoteInputStream ristream) throws IOException {
        InputStream istream = RemoteInputStreamClient.wrap(ristream);
        FileOutputStream ostream = null;
        try {
          
          File tempFile = File.createTempFile("sentFile_", ".dat");
          ostream = new FileOutputStream(tempFile);
          System.out.println("Writing file " + tempFile);
  
          byte[] buf = new byte[1024];
  
          int bytesRead = 0;
          while((bytesRead = istream.read(buf)) >= 0) {
            ostream.write(buf, 0, bytesRead);
          }
          ostream.flush();
  
          System.out.println("Finished writing file " + tempFile);
          
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
        }
      }
    }
  
    public static void main(String[] args) throws Exception
    {
      
      FileServer server = new FileServer();
      RemoteFileServer stub = (RemoteFileServer)
        UnicastRemoteObject.exportObject(server, 0);
  
      // bind to registry
      Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
      registry.bind("RemoteFileServer", stub);
  
      System.out.println("Server ready");
      
    }    
}
