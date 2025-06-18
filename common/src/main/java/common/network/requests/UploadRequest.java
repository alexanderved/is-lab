package common.network.requests;

public class UploadRequest extends NetworkRequest {
    private String dataURL;
    private String filename;

    public UploadRequest() {

    }

    public UploadRequest(String dataURL, String filename) {
        this.dataURL = dataURL;
        this.filename = filename;
    }

    public String getDataURL() {
        return dataURL;
    }

    public String getFilename() {
        return filename;
    }
}
