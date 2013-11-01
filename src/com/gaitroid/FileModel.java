package com.gaitroid;

import java.util.ArrayList;

public class FileModel {

    public static ArrayList<Item> Items;

    public static void LoadModel(String[] fileNames) {
        Items = new ArrayList<Item>();

        for(int i =0; i < fileNames.length; i++) {
            Items.add(new Item(i+1, "ic_dataFile.png", fileNames[i]));
        }
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