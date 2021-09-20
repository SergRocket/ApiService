import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ParamForGameCreation {
    private int id;
    private String releaseDate;
    private String name;
    private int reviewScore;
    private String category;
    private String rating;
}
