package cn.origin.cube.settings;

import cn.origin.cube.module.AbstractModule;

import java.awt.*;
import java.util.function.Predicate;
//ToDo
public class ColorSetting extends Setting<Color>{
    boolean isOpen = false;
    boolean isSelected = false;

    public ColorSetting(String name, Color value, AbstractModule module) {
        super(name, value, module);
    }

    public ColorSetting(String name, Color value, AbstractModule module, Predicate<Color> shown) {
        super(name, value, module);
    }

    public Color getColor() {
        return getColor();
    }

    public void setColor(Color value) {
        this.setValue(value);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getColorAsString() {
        return String.valueOf(getValue().getRGB());
    }

}
