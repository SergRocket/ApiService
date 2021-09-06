import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class MainTests {
    private static Autorization api;

    @BeforeClass
    public static void prepeareClient(){
        api = Autorization.loginAs("eve.holt@reques.in", "citysLicka");
    }

    private static final RequestSpecification REQUEST_SPECIFICATION = new RequestSpecBuilder()
            .setBaseUri("https://reqres.in/api")
            .setBasePath("/users")
            .setContentType(ContentType.JSON)
            .build();

    @Test
    public void getUsers(){
        given()
                .spec(REQUEST_SPECIFICATION)
                .when().get()
                .then().statusCode(200);
    }

    @Test
    public void getOneUser(){
        given()
                .spec(REQUEST_SPECIFICATION)
                .when().get()
                .then()
                .statusCode(200)
                .body("data[0].email", equalTo("george.bluth@reqres.in"));
    }

    @Test
    public void printUser(){
        String user = given().spec(REQUEST_SPECIFICATION)
                .when().get()
                .then().statusCode(200)
                .extract().asString();
        System.out.print(user);
    }

    @Test
    public void checkUserName(){
        given().spec(REQUEST_SPECIFICATION)
                .when().get()
                .then().statusCode(200)
                .body("data.find{it.email=='george.bluth@reqres.in'}.first_name", equalTo("George"));
    }

    @Test
    public void getListOfEmail(){
        List<String> emails = given()
                .spec(REQUEST_SPECIFICATION)
                .when().get()
                .then().statusCode(200)
                .extract().jsonPath().getList("data.rmail");
        System.out.print(emails.get(1));
    }

    @Test
    public void checkUserExistent(){
        List<CheckEmailTest> users = given()
                .spec(REQUEST_SPECIFICATION)
                .when().get()
                .then().statusCode(200)
                .extract().jsonPath().getList("data", CheckEmailTest.class);
    }

    @Test
    public void createUser(){
        ParamForUserCreation userCreate = UserGenerator.getSimpleUser();
        CreateUserResponse creationResponce = api.createUser(userCreate);
        assertThat(creationResponce).isNotNull()
        .extracting(CreateUserResponse::getName)
        .isEqualTo(userCreate.getName());

        assertThat(userCreate).isNotNull()
        .extracting(ParamForUserCreation::getName)
        .isEqualTo(userCreate.getName());
    }

}
