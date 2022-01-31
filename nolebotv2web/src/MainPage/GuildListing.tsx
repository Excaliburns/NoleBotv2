import { useStateMachine } from "little-state-machine";
import { APIGuild, APIPartialGuild } from "discord-api-types";
import React from "react";
import { axiosDiscordInstance, axiosNolebotInstance } from "../util/AxiosNolebotInstance";
import { AxiosResponse } from "axios";
import { GuildUser } from "../entities/JavaGenerated";


export default function GuildListing() {
    const [isGameAdmin, setIsGameAdmin] = React.useState<boolean>(false);
    const [fsuGuild, setFsuGuild] = React.useState<APIPartialGuild>();
    const [fsuGuildMemberInfo, setFsuGuildMemberInfo] = React.useState<GuildUser>()

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

                    if (fsu && state?.userDetails) {
                        axiosNolebotInstance.post(`guilds/${process.env.REACT_APP_MAIN_SERVER_ID}/${state.userDetails?.id}`, '', {
                            headers: {
                                'X-Username': state.userDetails.username
                            }
                        })
                            .then((r: AxiosResponse<GuildUser>) => {
                                const gameMasterRole = r.data.roles.find( any => {
                                    console.log(any);
                                    console.log(any.id === '454011610445643806');
                                    return any.id === '454011610445643806'
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
                            You're not a GM. :(
                        </p>
                    </>
                    : null
            }
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
        </div>
    )
}