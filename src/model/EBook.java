package model;

/**
 * EBook class - represents digital books
 * Implements MediaSearchable for UML compliance
 */
public class EBook extends Book implements MediaSearchable {
    private double fileSize;          // in MB
    private String format;            // PDF, EPUB, MOBI, etc.
    private String downloadLink;
    private boolean isDRMProtected;
    private String downloadUrl;       // Added for UML compliance (optional)

    public EBook(String isbn, String title, String author, int publicationYear,
                 int copies, double fileSize, String format,
                 String downloadLink, boolean isDRMProtected) {
        super(isbn, title, author, publicationYear, copies);
        setFileSize(fileSize);
        setFormat(format);
        setDownloadLink(downloadLink);
        setDRMProtected(isDRMProtected);
        this.downloadUrl = downloadLink; // Same as downloadLink for simplicity
    }

    // ====================
    // Book Abstract Methods
    // ====================
    @Override
    public String getType() {
        return "E-Book";
    }

    @Override
    public String getSpecificDetails() {
        return getFileInfo() + " | Download: " +
                (downloadLink != null ? "Available" : "Not available");
    }

    // ====================
    // EBook-Specific Methods
    // ====================
    public boolean download() {
        if (downloadLink == null || downloadLink.isEmpty()) {
            System.out.println("Cannot download: No download link available for " + getTitle());
            return false;
        }
        System.out.println("Downloading: " + getTitle() + " (" + format + ", " + fileSize + "MB)");
        // TODO: Actual download logic
        return true;
    }

    // ====================
    // MediaSearchable Interface (UML Compliance)
    // ====================
    @Override
    public String getFileInfo() {
        return String.format("%s format, %.2f MB%s",
                format, fileSize,
                isDRMProtected ? " (DRM Protected)" : "");
    }

    @Override
    public boolean isCompatibleDevice(String device) {
        if (device == null || device.trim().isEmpty()) {
            return false;
        }

        String deviceLower = device.toLowerCase().trim();

        // Check format compatibility with device
        switch (format.toLowerCase()) {
            case "pdf":
                return deviceLower.contains("tablet") ||
                        deviceLower.contains("computer") ||
                        deviceLower.contains("phone") ||
                        deviceLower.contains("ipad") ||
                        deviceLower.contains("android");

            case "epub":
                // EPUB works on most devices except basic Kindle
                if (deviceLower.contains("kindle") && !deviceLower.contains("fire")) {
                    return false; // Basic Kindle doesn't support EPUB natively
                }
                return true;

            case "mobi":
                // MOBI is Kindle format
                return deviceLower.contains("kindle");

            default:
                return deviceLower.contains("computer"); // Unknown format, assume PC works
        }
    }

    // ====================
    // MediaSearchable Getters (UML "fields")
    // ====================
    @Override
    public double getFileSize() {
        return fileSize;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getDownloadLink() {
        return downloadLink;
    }

    @Override
    public boolean isDRMProtected() {
        return isDRMProtected;
    }

    @Override
    public String getDownloadUrl() {
        // UML shows both downloadLink and downloadUrl - treat as same
        return downloadUrl != null ? downloadUrl : downloadLink;
    }

    // ====================
    // GETTERS AND SETTERS WITH VALIDATION
    // ====================
    public void setFileSize(double fileSize) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("File size must be positive: " + fileSize);
        }
        this.fileSize = fileSize;
    }

    public void setFormat(String format) {
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Format cannot be null or empty");
        }
        String validFormats = "PDF, EPUB, MOBI, AZW, TXT";
        this.format = format.trim();
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
        // Also update downloadUrl for UML compliance
        this.downloadUrl = downloadLink;
    }

    public void setDRMProtected(boolean DRMProtected) {
        isDRMProtected = DRMProtected;
    }

    // Additional setter for downloadUrl (UML compliance)
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    // ====================
    // OVERRIDDEN METHODS
    // ====================
    @Override
    public String toString() {
        return super.toString() + " [" + getFileInfo() + "]";
    }

    @Override
    public java.util.Map<String, Object> getProfile() {
        java.util.Map<String, Object> profile = super.getProfile();
        profile.put("fileSizeMB", fileSize);
        profile.put("format", format);
        profile.put("drmProtected", isDRMProtected);
        profile.put("downloadAvailable", downloadLink != null);
        return profile;
    }
}