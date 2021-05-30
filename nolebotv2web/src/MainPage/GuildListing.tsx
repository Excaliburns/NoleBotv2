import { useStateMachine } from "little-state-machine";
import { APIGuild, APIGuildMember, APIPartialGuild } from "discord-api-types";
import React from "react";
import { axiosDiscordInstance } from "../util/AxiosNolebotInstance";


export default function GuildListing() {
    const [isGameAdmin, setIsGameAdmin] = React.useState<boolean>(false);
    const [isFsuServerMember, setIsFsuServerMember] = React.useState<boolean>(false);
    const [fsuGuild, setFsuGuild] = React.useState<APIPartialGuild>();

    const [guilds, setGuilds] = React.useState<APIPartialGuild[]>([]);
    const { state } = useStateMachine();

    React.useEffect(() => {
        if (state?.userToken && !guilds.length) {
            axiosDiscordInstance.get('/users/@me/guilds', {
                headers: {
                    Authorization: `Bearer ${state.userToken.access_token}`
                }
            })
                .then ( response => {
                    setGuilds(response.data)

                    const fsu = response.data.find( (each: APIGuild) => each.id='138481681630887936');
                    const isFsu = !!fsu

                    console.log(isFsu);

                    setIsFsuServerMember(isFsu)
                    setFsuGuild(fsu)
                })
        }
    }, [state?.userToken])

    return (
        <div>
            Are you in the FSU Esports Discord?
            <pre>
                {isFsuServerMember ? 'true' : 'false'}
            </pre>

            Are you a game master / admin?
            <pre>
                {isGameAdmin}
            </pre>

            You are in these guilds:
            <pre>
                {JSON.stringify(guilds, null, 2)}
            </pre>
        </div>
    )
}