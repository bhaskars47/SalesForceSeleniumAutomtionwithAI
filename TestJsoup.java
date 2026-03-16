import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;

public class TestJsoup {
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://login.salesforce.com");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream is = con.getInputStream();
        byte[] bytes = is.readAllBytes();
        String rawHtml = new String(bytes);

        System.out.println("Raw length: " + rawHtml.length());

        Document doc = Jsoup.parse(rawHtml);
        doc.select("script, style, svg, meta, link, noscript, iframe").remove();
        Element body = doc.body();
        if (body != null) {
            String cleaned = body.html().replaceAll("\\s+", " ");
            System.out.println("Cleaned length: " + cleaned.length());
            if (cleaned.length() > 8000) {
                cleaned = cleaned.substring(0, 8000) + "...[TRUNCATED]";
            }
            System.out.println("Contains username input? " + cleaned.contains("username"));
            System.out.println(cleaned);
        }
    }
}
