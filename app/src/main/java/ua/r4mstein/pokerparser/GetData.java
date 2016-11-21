package ua.r4mstein.pokerparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GetData {

    public static void getDataFromPSLinks(String ps_link, ArrayList<MyModel> list) {
        Document document = null;

        try {
            document = Jsoup.connect(ps_link).get();
            Elements div_links = document.select("div.titleBox");

            for (Element link : div_links) {
                Element element = link.select("a").first();
                Element user = link.select("a.forum--board--thread-overview-threadStarter").first();
                MyModel model = new MyModel();
                model.setLinkTitle(element.text());
                model.setLink("https://ru.pokerstrategy.com/forum/" + element.attr("href"));
                if (user != null) {
                    model.setUser(user.text());
                } else {
                    model.setUser("no_user");
                }

                list.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getDataFromGTLinks(String gt_link, ArrayList<MyModel> list) {
        Document document = null;

        try {
            document = Jsoup.connect(gt_link).get();
            Elements tr_elements = document.select("tr.new ");

            for (Element element : tr_elements){
                Element user = element.select("p.post_top_line").select("a").first();
                Element link = element.select("h2").select("a").first();
                MyModel model = new MyModel();
                model.setLinkTitle(link.text());
                model.setLink("http://forum.gipsyteam.ru/" + link.attr("href"));
                model.setUser(user.text());
                list.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
