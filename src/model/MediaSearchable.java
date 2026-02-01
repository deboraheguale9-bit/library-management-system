package model;

public interface MediaSearchable {
    String getFileInfo();
    boolean isCompatibleDevice(String device);
    double getFileSize();
    String getFormat();
    String getDownloadLink();
    boolean isDRMProtected();
    String getDownloadUrl();
}