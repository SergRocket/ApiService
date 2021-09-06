import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckEmailTest {
    public String getEmail(){return email;}
    @JsonProperty("email")
    private String email;
}
