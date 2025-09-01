package org.apache.chengdb.ui.controller.product;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class ListIcons {
    public static void main(String[] args) {
        for (FontAwesomeSolid icon : FontAwesomeSolid.values()) {
            System.out.println(icon.name() + " -> " + icon.getDescription() + " -> " + icon.getCode());
        }
    }
}
