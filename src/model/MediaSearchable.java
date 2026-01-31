package model;

/**
 * EXACTLY matches UML diagram for ebook/digital media
 * For UML compliance only - implemented by Ebook class
 */
public interface MediaSearchable {
    // Methods from UML
    String getFileInfo();
    boolean isCompatibleDevice(String device);

    // "Fields" from UML as getters (interfaces can't have fields)
    double getFileSize();
    String getFormat();
    String getDownloadLink();
    boolean isDRMProtected();
    String getDownloadUrl();
}