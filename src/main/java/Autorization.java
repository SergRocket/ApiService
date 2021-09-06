import Utils.UserLogin;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Autorization {
    public static final String BASE_URL = "https://reqres.in/api";
    public static final String BASE_PATH = "/login";
    private static  RequestSpecification REQUEST_SPECIFICATION;
    public Cookies cookies;
    public UserService user;
    public OrderService order;

    private Autorization(Cookies cookies){
        this.cookies = cookies;
        user = new UserService(cookies);
        order = new OrderService(cookies);
    }

    public static Autorization loginAs(String login, String password){
        Cookies cookies = given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .body(new UserLogin(login, password))
                .post()
                .detailedCookies();
        return new Autorization(cookies);

    }

    public CreateUserResponse createUser(ParamForUserCreation paramForUserCreation){
        return given().body(paramForUserCreation).post().as(CreateUserResponse.class);
    }


}
