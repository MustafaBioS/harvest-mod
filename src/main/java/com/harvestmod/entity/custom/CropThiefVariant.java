package com.harvestmod.entity.custom;

import java.util.Arrays;
import java.util.Comparator;

public enum CropThiefVariant {

    NULLSKULLS(0),
    MUSTAFA(1);

    private static final CropThiefVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(CropThiefVariant::getId)).toArray(CropThiefVariant[]::new);

    private final int id;

    CropThiefVariant(int id) {
        this.id = id;
    }

    int getId() {
        return this.id;
    }

    public static CropThiefVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }

}
