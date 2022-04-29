import 'little-state-machine';
import {DiscordUser} from "./entities/JavaGenerated";

// little state machine state
declare module 'little-state-machine' {
    interface GlobalState {
        jwt?: string
        accessToken?: string,
        userDetails?: DiscordUser
    }
}