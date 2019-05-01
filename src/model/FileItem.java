package model;

import javafx.scene.image.ImageView;

public class FileItem {
    private ImageView type;
    private String name;
    private int size;

    public FileItem(ImageView type, String name, int size) {
        this.type = type;
        this.name = name;
        this.size = size;
    }

    public ImageView getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
