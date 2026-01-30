package model;

public class EBook extends Book {
    private double fileSize;
    private String format;
    private String downloadLink;
    private boolean isDRMProtected;

    public EBook(String isbn, String title, String author, int publicationYear,
                 int copies, double fileSize, String format,
                 String downloadLink, boolean isDRMProtected) {
        super(isbn, title, author, publicationYear, copies);
        this.fileSize = fileSize;
        this.format = format;
        this.downloadLink = downloadLink;
        this.isDRMProtected = isDRMProtected;
    }

    public boolean download() {
        System.out.println("Downloading: " + getTitle());
        return downloadLink != null && !downloadLink.isEmpty();
    }

    public String getFileInfo() {
        return String.format("%s format, %.2f MB%s",
                format, fileSize,
                isDRMProtected ? " (DRM Protected)" : "");
    }

    public boolean isCompatibleDevice(String device) {
        String[] compatibleDevices = {"Kindle", "Tablet", "Phone", "Computer", "E-Reader"};
        for (String d : compatibleDevices) {
            if (d.equalsIgnoreCase(device)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return "E-Book";
    }

    @Override
    public String getSpecificDetails() {
        return getFileInfo() + " | Download: " +
                (downloadLink != null ? "Available" : "Not available");
    }

    // Getters and Setters
    public double getFileSize() { return fileSize; }
    public void setFileSize(double fileSize) {
        if (fileSize > 0) this.fileSize = fileSize;
    }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getDownloadLink() { return downloadLink; }
    public void setDownloadLink(String downloadLink) { this.downloadLink = downloadLink; }

    public boolean isDRMProtected() { return isDRMProtected; }
    public void setDRMProtected(boolean DRMProtected) { isDRMProtected = DRMProtected; }
}