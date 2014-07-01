/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testapacheio;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class SimpleTestMonitor {
    // A hardcoded path to a folder you are monitoring .
    public static final String FOLDER =
            "d:/snake_test/client";
    
    public static void main(String[] args) throws Exception {
        // The monitor will perform polling on the folder every 5 seconds
        final long pollingInterval = 5 * 1000;

        File folder = new File(FOLDER);
        
        if (!folder.exists()) {
            // Test to see if monitored folder exists
            throw new RuntimeException("Directory not found: " + FOLDER);
        }
        
        FileAlterationObserver observer = new FileAlterationObserver(folder);
        FileAlterationMonitor monitor =
                new FileAlterationMonitor(pollingInterval);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            // Is triggered when a file is created in the monitored folder
            @Override
            public void onFileCreate(File file) {
                try {
                    // "file" is the reference to the newly created file
                    System.out.println("File created: "
                            + file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }

            @Override
            public void onFileChange(File file) {
                try {
                    // "file" is the reference to the changed file
                    System.out.println("File changed: "
                            + file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }

            
            
            // Is triggered when a file is deleted from the monitored folder
            @Override
            public void onFileDelete(File file) {
                try {
                    // "file" is the reference to the removed file
                    System.out.println("File removed: "
                            + file.getCanonicalPath());
                    // "file" does not exists anymore in the location
                    System.out.println("File still exists in location: "
                            + file.exists());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
        for (;;) {
            Thread.sleep(pollingInterval);
            System.out.println("After Start");
        }
    }
}
