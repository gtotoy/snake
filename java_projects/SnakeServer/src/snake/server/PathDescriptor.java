/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author Gustavo
 */
public class PathDescriptor implements Serializable {
    public enum Status { Modified, Deleted }
    public String relative_path;
    public Status status;
    public Date statusTimePoint;
    public Date lastModified;
    public Date lastCreationTime;

    public PathDescriptor() {;}
    
    public PathDescriptor( PathDescriptor x ) {
        relative_path = x.relative_path;
        status = x.status;
        statusTimePoint = new Date(x.statusTimePoint.getTime());
        lastModified = new Date(x.lastModified.getTime());
        lastCreationTime = new Date(x.lastCreationTime.getTime());
    }
    
    public static PathDescriptor FindIfEqualRelativePath(ArrayList<PathDescriptor> descriptors, PathDescriptor x) {
        Iterator<PathDescriptor> it = descriptors.iterator();
        while (it.hasNext()) {
            PathDescriptor current = it.next();
            if (current.relative_path.equals(x.relative_path)) {
                return current;
            }
        }
        return null;
    }

    public static void updateDescriptors(ArrayList<PathDescriptor> currentDescriptors, ArrayList<PathDescriptor> newDescriptors) {
        System.out.println("Before Update");
        System.out.println(currentDescriptors.toString());
        int currentSize = currentDescriptors.size();
        for (int i = 0; i < currentSize; ++i) {
            PathDescriptor current = currentDescriptors.get(i);
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
            PathDescriptor current = PathDescriptor.FindIfEqualRelativePath(currentDescriptors, newDescriptor);
            if (current == null) {
                newDescriptor.statusTimePoint = new Date();
                currentDescriptors.add(newDescriptor);
            }
        }
        
        System.out.println("After Update");
        System.out.println(currentDescriptors.toString());
    }
    
    @Override
    public String toString() {
        return "PathDescriptor{" + "relative_path=" + relative_path + ", status=" + status + ", statusTimePoint=" + statusTimePoint + ", lastModified=" + lastModified + ", lastCreationTime=" + lastCreationTime + '}';
    }
    
}
