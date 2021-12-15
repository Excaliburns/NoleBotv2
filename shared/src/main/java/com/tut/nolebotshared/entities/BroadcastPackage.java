package com.tut.nolebotshared.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;

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
