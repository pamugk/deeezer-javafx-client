package api.objects.utils;

public class Options {
    private boolean streaming;
    private int streaming_duration;
    private boolean offline;
    private boolean hq;
    private boolean ads_display;
    private boolean ads_audio;
    private boolean too_many_devices;
    private boolean can_subscribe;
    private int radio_skips;
    private boolean lossless;
    private boolean preview;
    private boolean radio;

    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    public int getStreaming_duration() {
        return streaming_duration;
    }

    public void setStreaming_duration(int streaming_duration) {
        this.streaming_duration = streaming_duration;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isHq() {
        return hq;
    }

    public void setHq(boolean hq) {
        this.hq = hq;
    }

    public boolean isAds_display() {
        return ads_display;
    }

    public void setAds_display(boolean ads_display) {
        this.ads_display = ads_display;
    }

    public boolean isAds_audio() {
        return ads_audio;
    }

    public void setAds_audio(boolean ads_audio) {
        this.ads_audio = ads_audio;
    }

    public boolean isToo_many_devices() {
        return too_many_devices;
    }

    public void setToo_many_devices(boolean too_many_devices) {
        this.too_many_devices = too_many_devices;
    }

    public boolean isCan_subscribe() {
        return can_subscribe;
    }

    public void setCan_subscribe(boolean can_subscribe) {
        this.can_subscribe = can_subscribe;
    }

    public int getRadio_skips() {
        return radio_skips;
    }

    public void setRadio_skips(int radio_skips) {
        this.radio_skips = radio_skips;
    }

    public boolean isLossless() {
        return lossless;
    }

    public void setLossless(boolean lossless) {
        this.lossless = lossless;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isRadio() {
        return radio;
    }

    public void setRadio(boolean radio) {
        this.radio = radio;
    }
}
