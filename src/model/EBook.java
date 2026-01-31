package model;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Gets file size in human-readable format
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return String.format("%.1f MB", fileSize);
        } else {
            return String.format("%.1f GB", fileSize / 1024);
        }
    }

    // ====================
    // MediaSearchable Interface (UML Compliance)
    // ====================
    @Override
    public String getFileInfo() {
        return String.format("%s format, %s%s",
                format, getFormattedFileSize(),
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

            case "azw":
                // AZW is Amazon Kindle format
                return deviceLower.contains("kindle");

            case "txt":
                // Plain text works everywhere
                return true;

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
    // SQLITE-COMPATIBLE getProfile()
    // ====================

    /**
     * Returns eBook data as Map for SQLite database storage
     * Overrides parent method to add eBook-specific fields
     * Column names match database schema
     */
    @Override
    public Map<String, Object> getProfile() {
        Map<String, Object> profile = super.getProfile(); // Get base Book fields

        // Add eBook-specific fields for SQLite
        profile.put("file_size_mb", fileSize);
        profile.put("format", format);
        profile.put("download_link", downloadLink);
        profile.put("drm_protected", isDRMProtected ? 1 : 0); // Boolean to int for SQLite
        profile.put("download_url", getDownloadUrl());

        // Set PrintedBook-specific fields to null (since this is an eBook)
        profile.put("shelf_location", null);
        profile.put("condition", null);
        profile.put("edition", null);
        profile.put("is_reserved", null);

        return profile;
    }

    /**
     * Static factory method to create EBook from database Map
     * Used by BookRepositorySQLite when loading from database
     */
    public static EBook fromMap(Map<String, Object> data) {
        // Extract base Book fields
        String isbn = (String) data.get("isbn");
        String title = (String) data.get("title");
        String author = (String) data.get("author");
        int publicationYear = (int) data.get("publication_year");
        int copies = (int) data.get("copies");

        // Extract eBook-specific fields
        double fileSize = (double) data.get("file_size_mb");
        String format = (String) data.get("format");
        String downloadLink = (String) data.get("download_link");
        boolean drmProtected = (int) data.get("drm_protected") == 1;

        return new EBook(isbn, title, author, publicationYear, copies,
                fileSize, format, downloadLink, drmProtected);
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
        // Validate format
        String formatLower = format.toLowerCase();
        if (!formatLower.matches("pdf|epub|mobi|azw|txt")) {
            throw new IllegalArgumentException("Invalid format: " + format +
                    ". Valid formats: PDF, EPUB, MOBI, AZW, TXT");
        }
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

    // Additional getters for convenience
    public String getFormattedSize() {
        return getFormattedFileSize();
    }

    // ====================
    // OVERRIDDEN METHODS
    // ====================
    @Override
    public String toString() {
        return super.toString() + " [" + getFileInfo() + "]";
    }

    /**
     * Enhanced toString for detailed display
     */
    public String toDetailedString() {
        return String.format("EBook: %s\n" +
                        "Author: %s\n" +
                        "Format: %s, Size: %s\n" +
                        "DRM: %s\n" +
                        "Download: %s",
                getTitle(), getAuthor(), format, getFormattedFileSize(),
                isDRMProtected ? "Protected" : "Free",
                downloadLink != null ? "Available" : "Not Available");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EBook)) return false;
        if (!super.equals(obj)) return false;

        EBook eBook = (EBook) obj;
        return Double.compare(eBook.fileSize, fileSize) == 0 &&
                isDRMProtected == eBook.isDRMProtected &&
                format.equals(eBook.format) &&
                downloadLink.equals(eBook.downloadLink);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Double.hashCode(fileSize);
        result = 31 * result + format.hashCode();
        result = 31 * result + downloadLink.hashCode();
        result = 31 * result + (isDRMProtected ? 1 : 0);
        return result;
    }
}