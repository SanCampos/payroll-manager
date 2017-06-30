package main.java.utils;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.List;

/**
 * Created by thedr on 6/30/2017.
 */
public class NodeUtils {
    
    /**
     * Gets all nodes available in a parent
     *
     * @param root The parent node to be used as source of all nodes
     * @param nodes List to add nodes to
     * @return List containing all nodes available in the parent
     */
    public static List<Node> getAllNodesOf(Parent root, List<Node> nodes) {
        for (Node n : root.getChildrenUnmodifiable()) {
            nodes.add(n);
            
            if (n instanceof Parent) //Dives into sub-parents to get their nodes
                getAllNodesOf((Parent) n, nodes);
        }
        return nodes;
    }
}
