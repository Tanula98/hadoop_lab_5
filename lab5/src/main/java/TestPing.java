public class TestPing {

    private String url;
    private Integer count;

    public TestPing(String url, Integer count) {
        this.url = url;
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public Integer getCount() {
        return count;
    }
}
