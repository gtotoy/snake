package snake.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import snake.server.ISnakeServer;
import snake.server.Settings;

public class ClientMain {
    public static String invalidArguments = "Invalid Arguments, check documentation of commands";
    public static final String cuser = "-cuser";
    public static final String user = "-user";
    public static final String setboxdir = "-setboxdir";
    public static final String syncbox = "-syncbox";
    public static final String autosyncbox = "-autosyncbox";
    
    public static void main(String[] args) {
        try {
            if ( args.length < 1 ) {
                System.out.println(invalidArguments);
                return;
            }
            String ip = "localhost";
            String url;
            ISnakeServer server;
            String username;
            String directory;
            switch (args[0]) {
                case cuser:
                    if (args.length >= 3) {
                        ip = args[2];
                    } 
                    if (args.length >= 2) {
                        url = "rmi://" + ip + "/Server";
                        server = (ISnakeServer) Naming.lookup(url);
                        System.out.println("Server: " + ip);
                        username = args[1];
                        SnakeClient.createUser(server, username);
                    } else {
                        System.out.println(invalidArguments);
                    }
                    break;
                case user:
                    if (args.length >= 3) {
                        switch (args[2]) {
                            case setboxdir:
                                if (args.length >= 5) {
                                    ip = args[4];
                                }
                                if (args.length >= 4) {
                                    url = "rmi://" + ip + "/Server";
                                    server = (ISnakeServer) Naming.lookup(url);
                                    System.out.println("Server: " + ip);
                                    username = args[1];
                                    directory = args[3];
                                    SnakeClient.setBoxDirectory(server, username, directory, ip);
                                } else {
                                    System.out.println(invalidArguments);
                                }
                                break;
                            case syncbox:
                                if (args.length >= 4) {
                                    ip = args[3];
                                }
                                url = "rmi://" + ip + "/Server";
                                server = (ISnakeServer) Naming.lookup(url);
                                System.out.println("Server: " + ip);
                                username = args[1];
                                SnakeClient.syncBox(server, username, ip);
                                break;
                            case autosyncbox:
                                if (args.length >= 4) {
                                    ip = args[3];
                                }
                                url = "rmi://" + ip + "/Server";
                                server = (ISnakeServer) Naming.lookup(url);
                                System.out.println("Server: " + ip);
                                username = args[1];
                                SnakeClient.autosyncBox(server, username, ip);
                                break;
                            default:
                                System.out.println(invalidArguments);
                                break;
                        }
                    }
                    break;
                default:
                    System.out.println(invalidArguments);
                    System.out.println(String.format("Check %s or %s", cuser, user));
                    break;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
