package Utils;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;

public abstract  class RestSpecForGameApi {
    public static final String BASE_URL_GAME = "https://reqres.in/api";
    public static final String BASE_PATH_GAME = "/app/videogames";
    public RequestSpecification REQUEST_SPEC;
        public  Cookies cookies;

    public RestSpecForGameApi(Cookies cookies){
        this.cookies = cookies;
        REQUEST_SPEC = new RequestSpecBuilder()
                .addCookies(cookies)
                .setBaseUri(BASE_URL_GAME)
                .setBasePath(BASE_PATH_GAME)
                .setContentType(ContentType.JSON)
                .setPort(8080)
                .build();
    }

}
