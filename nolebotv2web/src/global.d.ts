import 'little-state-machine';
import { AccessToken } from "./entities/AccessToken";
import { APIUser } from "discord-api-types";

// little state machine state
declare module 'little-state-machine' {
    interface GlobalState {
        userToken?: AccessToken,
        userDetails?: APIUser
    }
}