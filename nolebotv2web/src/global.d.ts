import 'little-state-machine';
import { DiscordUser } from "./entities/DiscordUser";

// little state machine state
declare module 'little-state-machine' {
    interface GlobalState {
        userDetails?: DiscordUser
    }
}