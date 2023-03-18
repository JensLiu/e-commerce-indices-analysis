package dev.jens.user;

public class UserStartRecordDto {
    private String deviceId;
    private String userId;
    private String region;
    private String installSource;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getInstallSource() {
        return installSource;
    }

    public void setInstallSource(String installSource) {
        this.installSource = installSource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserStartRecordDto{" +
                "deviceId='" + deviceId + '\'' +
                ", userId='" + userId + '\'' +
                ", region='" + region + '\'' +
                ", installSource='" + installSource + '\'' +
                ", version='" + version + '\'' +
                ", date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                ", minute='" + minute + '\'' +
                '}';
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    private String version;
    private String date;
    private String hour;
    private String minute;

    public UserStartRecordDto(String deviceId, String userId, String region, String installSource, String version, String date, String hour, String minute) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.region = region;
        this.installSource = installSource;
        this.version = version;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
    }
}
