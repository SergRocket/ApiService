import Utils.RestSpecForGameApi;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

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
    private static final RequestSpecification FOOTBALL_SPEC = new RequestSpecBuilder()
            .setBaseUri("http://api.football-data.org")
            .setBasePath("/v2/")
            .addHeader("X-Auth-Token", "af7f10f6b76f4518b0347b88aa539f12")
            .addHeader("X-Response-Control", "minifield")
            .build();

    @Test
   public void createNewGame(){
        String gameBody = "{\n" +
                "  \"id\": 11,\n" +
                "  \"name\": \"MyGame5\",\n" +
                "  \"releaseDate\": \"2020-09-15T13:48:28.555Z\",\n" +
                "  \"reviewScore\": 59,\n" +
                "  \"category\": \"Shooters\",\n" +
                "  \"rating\": \"Fine\"\n" +
                "}";
        given().spec(REQUEST_SPEC).body(gameBody)
                .when().post(VideoGamesEndpoints.ALL_VIDEO_GAMES)
                .then().statusCode(200);
    }

    @Test
    public void extractMapOfElements(){
        Map<String, ?> allDataAboutTeam = given().spec(FOOTBALL_SPEC)
                .pathParam("compId", 2021)
                .when().get(FootBallEndpoints.COMPETITONS_TEAMS)
                .then().statusCode(200).extract().jsonPath()
                .getMap("teams.find { it.name == 'Aston Villa FC'}");
        System.out.print("Vap of the team`s data is " + allDataAboutTeam);
    }

    @Test
    public void extractSingleValue(){
        String certainPlayer = given().spec(FOOTBALL_SPEC).pathParam("teamId", 57)
                .when().get(FootBallEndpoints.TEAMS_BY_ID)
                .then().statusCode(200).extract().jsonPath().getString("squad.find { it.shirtNumber == 48 }.name");
        System.out.print("Name of the player is " + certainPlayer);
    }

    @Test
    public void extractListOfValues(){
        List<String> playerNames = given().spec(FOOTBALL_SPEC).pathParam("teamId", 57)
                .when().get(FootBallEndpoints.TEAMS_BY_ID)
                .then().statusCode(200).extract().jsonPath().getList("squad.findAll { it.id >= 1200 }.name");
        System.out.print("List of players contains " + playerNames);
    }

    @Test
    public void extractSingleHighestValue(){
        String playerName = given().spec(FOOTBALL_SPEC).pathParam("teamId", 57)
                .when().get(FootBallEndpoints.TEAMS_BY_ID)
                .then().statusCode(200).extract().jsonPath().getString("squad.max { it.shirtNumber }.name");
        System.out.print("Player with highest Id is " + playerName);
    }

    @Test
    public void extractAndAddValues(){
        int sumOfAllIds = given().spec(FOOTBALL_SPEC).pathParam("teamId", 57)
                .when().get(FootBallEndpoints.TEAMS_BY_ID)
                .then().statusCode(200).extract().jsonPath().getInt("squad.collect { it.id }.sum()");
        System.out.print("Sum of all ids " + sumOfAllIds);

    }

    @Test
    public void extractMapOfObjectWithFind(){
        Map<String, ?> playerOfCertainPosition = given().spec(FOOTBALL_SPEC).pathParam("teamId", 57)
                .when().get(FootBallEndpoints.TEAMS_BY_ID)
                .then().statusCode(200).extract().jsonPath()
                .getMap("squad.findAll { it.position == 'Goalkeeper'}.find { it.nationality == 'Germany' }");
        System.out.print("Player as Goalkeeper and form Greece is " + playerOfCertainPosition);
    }


    @Test
    public void createGame() throws InterruptedException {
        ParamForGameCreation  paramForGame = new ParamForGameCreation();
        paramForGame.setId(48);
        paramForGame.setName("GTA-14");
        paramForGame.setReleaseDate("2020-09-20T11:56:33.236Z");
        paramForGame.setReviewScore(90);
        paramForGame.setCategory("Fight");
        paramForGame.setRating("Finish");
        CreateGameResponse cgr = given().spec(REQUEST_SPEC).body(paramForGame)
                .when().post(VideoGamesEndpoints.ALL_VIDEO_GAMES)
                .then().statusCode(200).time(lessThan(880L))
                .extract().as(CreateGameResponse.class);
        assertThat(cgr).extracting(CreateGameResponse :: getStatus)
                .isEqualTo("Record Added Successfully");
        Thread.sleep(2000);
        given().spec(REQUEST_SPEC)
                .pathParam("videoGameId", paramForGame.getId())
                .when().get(VideoGamesEndpoints.SINGLE_VIDEO_GAME)
                .then().statusCode(200).time(lessThan(780L))
                .body("name", equalTo(paramForGame.getName())).body("reviewScore",
                equalTo(paramForGame.getReviewScore()));

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
        given().spec(REQUEST_SPEC).pathParam("videoGameId", 2)
                .body(gameBody)
                .when().put(VideoGamesEndpoints.SINGLE_VIDEO_GAME)
                .then().statusCode(200);
    }

    @Test
    public void deleteSpecificGame(){
        given().spec(REQUEST_SPEC).pathParam("videoGameId", 2)
                .when().delete(VideoGamesEndpoints.SINGLE_VIDEO_GAME)
                .then().statusCode(200);
    }

    @Test
    public void getGames(){
        String games = given().spec(REQUEST_SPEC)
                .when().get(VideoGamesEndpoints.ALL_VIDEO_GAMES)
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
                .when().get(VideoGamesEndpoints.ALL_VIDEO_GAMES)
                .then()
                .statusCode(200).time(lessThan(580L))
                .extract().xmlPath().getList("videoGames.videoGame.name");
        int randomGame = (int)(Math.random() * games.size());
        System.out.print(games.get(randomGame));
    }

    @Test
    public void getArea(){
        given().spec(FOOTBALL_SPEC)
                .when().get(FootBallEndpoints.ALL_AREAS)
                .then().statusCode(200);
    }

    @Test
    public void mesureResponseTime(){
        long responceTime = given().spec(FOOTBALL_SPEC)
                .when().get(FootBallEndpoints.ALL_TEAMS).time();
        System.out.print("Responce time is " + responceTime);
    }

    @Test
    public void checkResponceTime(){
        given().spec(FOOTBALL_SPEC)
                .when().get(FootBallEndpoints.ALL_AREAS)
                .then().statusCode(200).time(lessThan(580L));
    }

    @Test
    public void getAreas(){
       List<String> areas = given().spec(FOOTBALL_SPEC)
                .when().get(FootBallEndpoints.ALL_AREAS)
                .then().statusCode(200).time(lessThan(780L)).extract().jsonPath().getList("areas.name");
       System.out.print(areas);
    }

    @Test
    public void getHeaders(){
        Response response = given()
                .given().spec(FOOTBALL_SPEC).pathParam("areaId", 2012)
                .when().get(FootBallEndpoints.AREAS_BY_ID).then().statusCode(200)
                .time(lessThan(780L))
                .extract().response();
        //Headers headers = response.getHeaders();
        String contentType = response.getHeader("Content-Type");
        System.out.print(contentType);
    }

    @Test
    public void getSpecificArea(){
        String area = given().spec(FOOTBALL_SPEC).pathParam("areaId", 2000)
                .when().get(FootBallEndpoints.AREAS_BY_ID)
                .then().statusCode(200).extract().asString();
        System.out.print(area);
    }

    @Test
    public void checkNameValueOfSpecificArea(){
        given().spec(FOOTBALL_SPEC).pathParam("areaId", 2015)
                .when().get(FootBallEndpoints.AREAS_BY_ID)
                .then().statusCode(200).body("id", equalTo(2015)).body("name",
                equalTo("Australia"));
    }

    @Test
    public void printAreaNames(){
        List<String> teams = given().spec(FOOTBALL_SPEC)
                .when().get(FootBallEndpoints.ALL_AREAS)
                .then().statusCode(200).extract().jsonPath().getList("areas.name");
        for (String teamName : teams){
            System.out.println(teamName);
        }
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
        create.setName("Janis");
        create.setPosition("testQAA");
        CreateUserResponse rs = given().spec(REQUEST_SPECIFICATION)
                .body(create)
                .when().post()
                .then().time(lessThan(780L)).extract().as(CreateUserResponse.class);
        assertThat(rs)
                .isNotNull()
                .extracting(CreateUserResponse::getName)
                .isEqualTo(create.getName());
    }
}
