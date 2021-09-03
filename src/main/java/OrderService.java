import Utils.RestSpec;
import io.restassured.http.Cookies;

public class OrderService extends RestSpec {
    @Override
    protected String getBasePath() {
        return "/orders";
    }

    public OrderService(Cookies cookies){super(cookies);}
}
