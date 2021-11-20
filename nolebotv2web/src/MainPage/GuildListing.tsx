import { useStateMachine } from "little-state-machine";
import { APIGuild, APIPartialGuild } from "discord-api-types";
import React from "react";
import { axiosDiscordInstance, axiosNolebotInstance } from "../util/AxiosNolebotInstance";
import { AxiosResponse } from "axios";


export default function GuildListing() {
    const [isGameAdmin, setIsGameAdmin] = React.useState<boolean>(false);
    const [fsuGuild, setFsuGuild] = React.useState<APIPartialGuild>();
    const [fsuGuildMemberInfo, setFsuGuildMemberInfo] = React.useState<{id: string, roleIds: string[]}>()

    const [guilds, setGuilds] = React.useState<APIPartialGuild[]>([]);
    const { state } = useStateMachine();

    React.useEffect(() => {
        if (state?.userToken) {
            axiosDiscordInstance.get('/users/@me/guilds', {
                headers: {
                    Authorization: `Bearer ${state.userToken.access_token}`
                }
            })
                .then ( response => {
                    setGuilds(response.data)
                    const fsu = response.data.find( (each: APIGuild) => each.id === process.env.REACT_APP_MAIN_SERVER_ID );
                    setFsuGuild(fsu)

                    if (fsu) {
                        axiosNolebotInstance.post(`guild/fsu/${state.userDetails?.id}`)
                            .then((r: AxiosResponse<{id: string, roleIds: string[]}>) => {
                                const gameMasterRole = r.data.roleIds.find( any => {
                                    console.log(any);
                                    console.log(any === '454011610445643806');
                                    return any === '454011610445643806'
                                });
                                setIsGameAdmin(!!gameMasterRole)
                                setFsuGuildMemberInfo(r.data);
                            })
                    }
                })
        }
    }, [state?.userToken])

    return (
        <div>
            {
                isGameAdmin ?
                    <>
                        <p>
                            Welcome Game Manager!
                        </p>
                        Your user data:
                        <pre>
                           {JSON.stringify(state.userDetails, null, 2)}
                        </pre>

                        FSU Guild data:
                        <pre>
                            {JSON.stringify(fsuGuild, null, 2)}
                       </pre>

                        Your FSU user data:
                        <pre>
                            {JSON.stringify(fsuGuildMemberInfo, null, 2)}
                        </pre>
                    </>
                    :
                    <>
                        <p>
                            You're not a GM. :(
                        </p>
                    </>
            }
        </div>
    )
}