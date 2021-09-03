import Utils.RestSpec;
import io.restassured.http.Cookies;

public class UserService extends RestSpec {
    @Override
    protected String getBasePath(){
        return "/users";
    }

    public UserService (Cookies cookies){super(cookies);}

}
