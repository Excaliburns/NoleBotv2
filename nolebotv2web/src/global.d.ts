import 'little-state-machine';
import { APIUser } from "discord-api-types";

// little state machine state
declare module 'little-state-machine' {
    interface GlobalState {
        jwt?: string
        userDetails?: APIUser
    }
}