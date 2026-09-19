package com.harvestmod.entity.custom;

import java.util.Arrays;
import java.util.Comparator;

public enum MustafaEntity {

    DEFAULT(0),
    MUSTAFA(1);

    private static final MustafaEntity[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(MustafaEntity::getId)).toArray(MustafaEntity[]::new);

    private final int id;

    MustafaEntity(int id) {
        this.id = id;
    }

    int getId() {
        return this.id;
    }

    public static MustafaEntity byId(int id) {
        return BY_ID[id % BY_ID.length];
    }

}
