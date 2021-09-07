import static io.restassured.RestAssured.given;

public class UserSteps {
    private CreateUserResponse user;
    public CreateUserResponse createUser(ParamForUserCreation paramForUserCreation){
        user = given().body(paramForUserCreation).post().as(CreateUserResponse.class);
        return user;
    }


}
