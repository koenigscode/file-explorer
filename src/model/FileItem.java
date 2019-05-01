package model;

import java.io.File;

public class FileItem {
    private String type;
    private String name;
    private String size;

    public FileItem(File file) {
        if (file == null)
            throw new IllegalArgumentException("file cannot be null");

        this.type = file.isDirectory() ? "directory" : "file";
        this.name = file.getName();
        this.size = "file".equals(type) ? humanReadableFileSize(file.length()) : "";
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

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }
}
