package com.gaitroid;

import java.util.ArrayList;

public class Model {

    public static ArrayList<Item> Items;

    public static void LoadModel() {

        Items = new ArrayList<Item>();
        Items.add(new Item(1, "ic_left.png", "Left"));
        Items.add(new Item(2, "ic_right.png", "Right"));
        Items.add(new Item(3, "ic_all.png", "All Devices"));

    }

    public static Item GetbyId(int id){

        for(Item item : Items) {
            if (item.Id == id) {
                return item;
            }
        }
        return null;
    }

}