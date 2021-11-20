import { useStateMachine } from "little-state-machine";
import { APIGuild, APIPartialGuild } from "discord-api-types";
import React from "react";
import { axiosDiscordInstance, axiosNolebotInstance } from "../util/AxiosNolebotInstance";


export default function GuildListing() {
    const [isGameAdmin, _setIsGameAdmin] = React.useState<boolean>(false);
    const [isFsuServerMember, setIsFsuServerMember] = React.useState<boolean>(false);
    const [_fsuGuild, setFsuGuild] = React.useState<APIPartialGuild>();

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

    const onsubmit = (event: React.FormEvent<{postTest: HTMLInputElement}>) => {
        event.preventDefault();
        axiosNolebotInstance.post('/oauth/test', {
            text: event.currentTarget.postTest.value
        })
            .then (resp => {console.log(resp)})
    }

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

            {/* @ts-ignore */}
            <form onSubmit={onsubmit}>
                <input type={"text"} id={"postTest"}/>
                <button type={"submit"}> post </button>
            </form>

        </div>
    )
}