package com.tutorial.game.type;

public enum MapType {
    MAP_1("map/map.tmx"),
    MAP_2("map/map2.tmx");

    private final String filePatch;

    MapType(String filePatch) {
        this.filePatch = filePatch;
    }

    public String getFilePatch() {
        return filePatch;
    }
}
