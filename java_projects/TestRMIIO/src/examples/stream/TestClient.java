/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package examples.stream;

import java.io.FileInputStream;
  import java.rmi.registry.LocateRegistry;
  import java.rmi.registry.Registry;
  
  import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
  
  /**
   * Example client which sends a file (given on the command line) to the
   * example server using a RemoteInputStream.
   *
   * @author James Ahlborn
   */
  public class TestClient {
  
    public static void main(String[] args) throws Exception
    {
      /*if(args.length < 1) {
        System.err.println("Usage: <file>");
        System.exit(1);
      }*/
  
      // grab the file name from the commandline
      //String fileName = args[0];
      String fileName = "D:/snake_test/server/file 0.txt";
      
      // get a handle to the remote service to which we want to send the file
      Registry registry = LocateRegistry.getRegistry(TestServer.REGISTRY_PORT);
      RemoteFileServer stub = (RemoteFileServer)
        registry.lookup("RemoteFileServer");
  
      System.out.println("Sending file " + fileName);
  
      // setup the remote input stream.  note, the client here is actually
      // acting as an RMI server (very confusing, i know).  this code sets up an
      // RMI server in the client, which the RemoteFileServer will then
      // interact with to get the file data.
      SimpleRemoteInputStream istream = new SimpleRemoteInputStream(
          new FileInputStream(fileName));
      try {
        // call the remote method on the server.  the server will actually
        // interact with the RMI "server" we started above to retrieve the
        // file data
        stub.sendFile(istream.export());
      } finally {
        // always make a best attempt to shutdown RemoteInputStream
        istream.close();
      }
  
      System.out.println("Finished sending file " + fileName);    
    }
  
  }

