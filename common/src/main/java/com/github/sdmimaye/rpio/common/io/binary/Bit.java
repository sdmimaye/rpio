package com.github.sdmimaye.rpio.common.io.binary;

public class Bit {
    public Bit() {

    }

    public Bit(int index, boolean selected) {
        this.index = index;
        this.selected = selected;
    }

    private int index;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
