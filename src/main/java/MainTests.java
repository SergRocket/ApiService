import Utils.RestSpecForGameApi;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class MainTests {
    private static Autorization api;
    private static UserFullSpec steps;

    @BeforeClass
    public static void prepeareClient(){
        api = Autorization.loginAs("eve.holt@reques.in", "citysLicka");
    }

    private static final RequestSpecification REQUEST_SPECIFICATION = new RequestSpecBuilder()
            .setBaseUri("https://reqres.in/api")
            .setBasePath("/users")
            .setContentType(ContentType.JSON)
            .build();
    private static final RequestSpecification REQUEST_SPEC = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/app/")
            .setPort(8080)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();

    @Test
   public void createNewGame(){
        String gameBody = "{\n" +
                "  \"id\": 19,\n" +
                "  \"name\": \"MyGame3\",\n" +
                "  \"releaseDate\": \"2020-09-15T13:48:28.555Z\",\n" +
                "  \"reviewScore\": 59,\n" +
                "  \"category\": \"Shooters\",\n" +
                "  \"rating\": \"Fine\"\n" +
                "}";
        given().spec(REQUEST_SPEC).body(gameBody)
                .when().post()
                .then().statusCode(200);
    }

    @Test
    public void updateGameInfo(){
        String gameBody = "{\n" +
                "  \"id\": 19,\n" +
                "  \"name\": \"MyGame4\",\n" +
                "  \"releaseDate\": \"2020-09-15T13:48:28.555Z\",\n" +
                "  \"reviewScore\": 61,\n" +
                "  \"category\": \"Shoot\",\n" +
                "  \"rating\": \"Perfect\"\n" +
                "}";
        given().spec(REQUEST_SPEC).body(gameBody)
                .when().put()
                .then().statusCode(200);
    }

    @Test
    public void getGames(){
        String games = given().spec(REQUEST_SPEC)
                .when().get()
                .then()
                .log().body()
                .statusCode(200).extract().asString();
        System.out.print(games);
    }

    @Test
    public void getSpecificVideoGame(){
        given().spec(REQUEST_SPEC).pathParam("videoGameId", 2)
                .when().get(VideoGamesEndpoints.SINGLE_VIDEO_GAME)
        .then().statusCode(200).log().body();
    }

    @Test
    public void getRandomGame(){
        List<String> games = given().spec(REQUEST_SPEC)
                .when().get()
                .then()
                .statusCode(200).extract().xmlPath().getList("videoGames.videoGame.name");
        int randomGame = (int)(Math.random() * games.size());
        System.out.print(games.get(randomGame));
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
                .extract().jsonPath().getList("data.email");
        System.out.print(emails.get(1));
    }

    @Test
    public void checkUserExistent(){
        List<CheckEmailTest> users = given()
                .spec(REQUEST_SPECIFICATION)
                .when().get()
                .then().statusCode(200)
                .extract().jsonPath().getList("data", CheckEmailTest.class);
        System.out.print(users.get(2));
    }

    @Test
    public void createUser(){
        ParamForUserCreation userCreate = UserGenerator.getSimpleUser();
        UserSteps userAPI = new UserSteps();
        CreateUserResponse paramForUser = userAPI.createUser(userCreate);
        assertThat(paramForUser).isNotNull()
        .extracting(CreateUserResponse::getName)
        .isEqualTo(userCreate.getName());
        assertThat(userCreate).isNotNull()
        .extracting(ParamForUserCreation::getName)
        .isEqualTo(userCreate.getName());
    }

    @Test
    public void crUser(){
        ParamForUserCreation create = new ParamForUserCreation();
        create.setName("Jane");
        create.setPosition("testQA-Automation");
        CreateUserResponse rs = given().spec(REQUEST_SPECIFICATION)
                .body(create)
                .when().post()
                .then().extract().as(CreateUserResponse.class);
        assertThat(rs)
                .isNotNull()
                .extracting(CreateUserResponse::getName)
                .isEqualTo(create.getName());
    }

}
