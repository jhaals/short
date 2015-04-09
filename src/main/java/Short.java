import org.apache.commons.validator.routines.UrlValidator;

import java.net.URL;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

public class Short {

    public static void main(String[] args) {

        Database db = new Database();

        if(!db.setup()) {
            System.out.println("failed to setup database!");
            System.exit(1);
        }

        post("/v1/save", (req, res) -> {
            String url = req.queryParams("url");
            UrlValidator urlValidator = new UrlValidator();

            if (!urlValidator.isValid(url)) {
                halt(500, "invalid url");
            }

            int result = db.save(url);
            // TODO: Throw something instead
            if(result == 0) {
               halt(500, "could not store url in database");
            }
            return "http://127.0.0.1:4567/" + UrlShortener.encode(result);
        });

        get("/:key", (req, res) -> {
            // Remove dirt from request
            String key = req.params(":key").replaceAll("[^A-Za-z0-9]", "");
            int id = UrlShortener.decode(key);

            // Negative id, not allowed so nothing will return.
            if(id < 0) {
                halt(404, "Not found");
            }

            String result = db.get(id);
            if(result == null) {
                halt(404, "Not found");
            }
            res.redirect(result);
            return result;
        });
    }
}
