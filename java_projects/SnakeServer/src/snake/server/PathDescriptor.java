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

    @Override
    public String toString() {
        return "PathDescriptor{" + "relative_path=" + relative_path + ", status=" + status + ", statusTimePoint=" + statusTimePoint + ", lastModified=" + lastModified + ", lastCreationTime=" + lastCreationTime + '}';
    }
    
}
