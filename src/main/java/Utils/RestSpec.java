package Utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;

public abstract class RestSpec {
    public static final String BASE_URL = "https://reqres.in/api";

    public static final String BASE_URL1 = "https://reqin/";
    public  Cookies cookies;
    protected abstract String getBasePath();
    public RequestSpecification REQUEST_SPECIFICATION;
        public RestSpec(Cookies cookies){
        this.cookies = cookies;
        REQUEST_SPECIFICATION = new RequestSpecBuilder()
                .addCookies(cookies)
                .setBaseUri(BASE_URL)
                .setBasePath(getBasePath())
                .setContentType(ContentType.JSON)
                .build();
    }

}
