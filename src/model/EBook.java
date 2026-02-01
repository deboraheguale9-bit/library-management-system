package model;


public class EBook extends Book implements MediaSearchable {
    private double fileSize;
    private String format;
    private String downloadLink;
    private boolean isDRMProtected;
    private String downloadUrl;

    public EBook(String isbn, String title, String author, int publicationYear,
                 int copies, double fileSize, String format,
                 String downloadLink, boolean isDRMProtected) {
        super(isbn, title, author, publicationYear, copies);
        setFileSize(fileSize);
        setFormat(format);
        setDownloadLink(downloadLink);
        setDRMProtected(isDRMProtected);
        this.downloadUrl = downloadLink;
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


    public boolean download() {
        if (downloadLink == null || downloadLink.isEmpty()) {
            System.out.println("Cannot download: No download link available for " + getTitle());
            return false;
        }
        System.out.println("Downloading: " + getTitle() + " (" + format + ", " + fileSize + "MB)");
        // TODO: Actual download logic
        return true;
    }


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


        switch (format.toLowerCase()) {
            case "pdf":
                return deviceLower.contains("tablet") ||
                        deviceLower.contains("computer") ||
                        deviceLower.contains("phone") ||
                        deviceLower.contains("ipad") ||
                        deviceLower.contains("android");

            case "epub":

                if (deviceLower.contains("kindle") && !deviceLower.contains("fire")) {
                    return false;
                }
                return true;

            case "mobi":

                return deviceLower.contains("kindle");

            default:
                return deviceLower.contains("computer");
        }
    }


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
        return downloadUrl != null ? downloadUrl : downloadLink;
    }


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
        this.downloadUrl = downloadLink;
    }

    public void setDRMProtected(boolean DRMProtected) {
        isDRMProtected = DRMProtected;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

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