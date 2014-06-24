/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Gustavo
 */
public class PathDescriptor implements Serializable {
    public enum Status { Modified, Deleted }
    public String relative_path;
    public Status status;
    public Date statusTimePoint;

    @Override
    public String toString() {
        return "PathDescriptor{" + "relative_path=" + relative_path + ", status=" + status + ", statusTimePoint=" + statusTimePoint + '}';
    }
}
