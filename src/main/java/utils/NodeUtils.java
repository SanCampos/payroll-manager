package main.java.utils;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.List;

/**
 * Created by thedr on 6/30/2017.
 */
public class NodeUtils {
    
    /**
     * Gets all nodes available in a parent. User can choose what instances of nodes are desired
     *
     * Runtime is O(n + (n*i)) where: n = amt of nodes in parent
     *                                i = amt of allowed instances for all nodes
     *
     * @param root The parent node to be used as source of all nodes
     * @param nodes List to add nodes to
     * @param superclasses Superclasses added nodes must be an instance of
     * @return List containing all nodes available in the parent
     */
    public static List<Node> getAllNodesOf(Parent root, List<Node> nodes, String... superclasses) throws ClassNotFoundException {
        for (Node n : root.getChildrenUnmodifiable()) {
            boolean classMatches = false;
            
            for (String s : superclasses) {
                if (Class.forName(s).isInstance(n)) {
                    classMatches = true;
                    break;
                }
            }
            
            if (classMatches)
                nodes.add(n);
            
            //Dives into sub-parents to get their nodes
            if (n instanceof Parent) {
                getAllNodesOf((Parent) n, nodes, superclasses);
                continue;
            }
        }
        return nodes;
    }
    
    public static List<Node> getAllNodesOf(Parent root, List<Node> nodes) throws ClassNotFoundException {
        return getAllNodesOf(root, nodes, null);
    }
}
