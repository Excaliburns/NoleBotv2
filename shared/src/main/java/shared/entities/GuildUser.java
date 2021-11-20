package shared.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class GuildUser implements Serializable {
    String id;
    List<String> roleIds;
}
