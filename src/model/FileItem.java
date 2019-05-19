package model;

import javafx.scene.image.ImageView;

import java.io.File;

public class FileItem implements Comparable<FileItem> {
    private final File file;
    private final String name;
    private final String size;

    public FileItem(File file) {
        if (file == null)
            throw new IllegalArgumentException("file cannot be null");

        this.file = file;
        this.name = file.getName();
        this.size = file.isDirectory() ? "" : humanReadableFileSize(file.length());
    }

    /**
     * Convert file size into human-readable format
     *
     * @param size the file size to convert
     * @return string representing the file size in human-readable format
     */
    private static String humanReadableFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String postfix = ("KMGTPE").charAt(exp - 1) + ("iB");
        return String.format("%.1f %s", size / Math.pow(1024, exp), postfix);
    }

    public ImageView getTypeIcon() {
        String type = this.file.isDirectory() ? "directory" : "file";
        File iconFile = new File("src/res/" + type + ".png");

        if (iconFile.exists()) {
            ImageView imageView = new ImageView(iconFile.toURI().toString());
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(20);
            return imageView;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int compareTo(FileItem o) {
        if (getFile().isDirectory() && !o.getFile().isDirectory()) return -1;
        if (!getFile().isDirectory() && o.getFile().isDirectory()) return 1;
        return getName().compareTo(o.getName());
    }
}
