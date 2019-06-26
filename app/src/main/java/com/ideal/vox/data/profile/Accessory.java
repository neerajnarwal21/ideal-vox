package com.ideal.vox.data.profile;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

public class Accessory extends ExpandableGroup<AccessoryData> {
    public Accessory(String title, ArrayList<AccessoryData> items) {
        super(title, items);
    }
}