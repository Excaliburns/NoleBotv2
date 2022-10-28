import 'little-state-machine';
import {DiscordUser, GuildUser} from "./entities/JavaGenerated";

// little state machine state
declare module 'little-state-machine' {
    interface GlobalState {
        jwt?: string
        userDetails?: DiscordUser
        guildUserDetails? : GuildUser
    }
}