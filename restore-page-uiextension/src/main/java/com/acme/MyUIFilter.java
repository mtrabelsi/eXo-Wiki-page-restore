package com.acme;

import java.util.Map;
import javax.jcr.Node;

import org.exoplatform.webui.ext.filter.UIExtensionAbstractFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

public class MyUIFilter extends UIExtensionAbstractFilter {
    /*
        * This method checks if the current node is a file.
    */
    public boolean accept(Map<String, Object> context) throws Exception {
        if (context == null) return true;

            Node currentNode = (Node) context.get(Node.class.getName());
            return currentNode.getParent().isNodeType("wiki:trash");
    }

    /*
        * This is the type of the filter.
    */
    public UIExtensionFilterType getType() {
        return UIExtensionFilterType.MANDATORY;
    }

    /*
        * This is called when the filter has failed.
    */
    public void onDeny(Map<String, Object> context) throws Exception {
        System.out.println("This node is not under the trash");
    }
}