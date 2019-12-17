public class ResultPing {

    private String url;
    private Long ping;


    public ResultPing(String url, Long ping) {
        this.url = url;
        this.ping = ping;
    }

    public String getUrl() {
        return url;
    }

    public Long getPing() {
        return ping;
    }

}
