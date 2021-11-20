package shared.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.enums.BroadcastType;
import shared.enums.MessageType;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastPackage implements Serializable {
    MessageType messageType;
    BroadcastType broadcastType;
    Serializable payload;
    UUID correlationId;
}
