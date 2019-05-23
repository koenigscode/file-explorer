package model;

import javafx.scene.image.ImageView;

import java.io.File;
import java.util.List;

/**
 * @author Koenig Michael
 */
public class FileItem implements Comparable<FileItem> {
    private final File file;
    private final String name;
    private final String size;

    /**
     * Create a new FileItem object
     *
     * @param file the file to encapsulate
     */
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

    /**
     * Get the icon of the file type
     *
     * @return corresponding icon
     */
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

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    /**
     * Compare two FileItems
     *
     * @param o the FileItem to compare with the this object
     * @return int corresponding to the sort order
     */
    @Override
    public int compareTo(FileItem o) {
        if (getFile().isDirectory() && !o.getFile().isDirectory()) return -1;
        if (!getFile().isDirectory() && o.getFile().isDirectory()) return 1;
        return getFile().getName().toLowerCase().compareTo(o.getFile().getName().toLowerCase());
    }

    public boolean hasExtension(String[] exts, boolean showHidden) {
        for (String ext : exts) {
            int i = file.getName().lastIndexOf('.');

            if (!showHidden && file.getName().charAt(0) == '.')
                continue;
            if (ext.isEmpty()) return true;
            if (file.isDirectory()) continue;

            String fileExt = file.getName().substring(i + 1).toLowerCase();
            if (ext.charAt(0) == '.')
                ext = ext.substring(1);

            if (fileExt.contains(ext.toLowerCase())) return true;
        }
        return false;
    }

}
