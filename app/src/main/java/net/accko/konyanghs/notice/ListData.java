package net.accko.konyanghs.notice;

public class ListData {
    public String mTitle;
    public String mUrl;
    public String mWriter;
    public String mDate;

    public ListData() {
    }

    public ListData(String mTitle, String mUrl, String mWriter, String mDate) {
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mWriter = mWriter;
        this.mDate = mDate;
    }
}