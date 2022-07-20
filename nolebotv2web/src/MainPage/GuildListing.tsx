import {useStateMachine} from "little-state-machine";
import {APIGuild, APIPartialGuild} from "discord-api-types";
import React from "react";
import {AxiosResponse} from "axios";
import {useAxios} from "../util/AxiosProvider";
import {DiscordUser, GuildUser} from "../entities/JavaGenerated";


export default function GuildListing() {
    const [isGameAdmin, setIsGameAdmin] = React.useState<boolean>(false);
    const [fsuGuild, setFsuGuild] = React.useState<APIPartialGuild>();
    const [fsuGuildMemberInfo, setFsuGuildMemberInfo] = React.useState<GuildUser>()

    const [guilds, setGuilds] = React.useState<APIPartialGuild[]>([]);
    const [userDetails, setUserDetails] = React.useState<DiscordUser>();
    const { state } = useStateMachine();
    const axios = useAxios();

    React.useEffect(() => {
        axios.get('/user/guilds')
            .then( (response) => {
                setGuilds(response.data)
                const fsu = response.data.find( (each: APIGuild) => each.id === process.env.REACT_APP_MAIN_SERVER_ID );
                setFsuGuild(fsu)

                if (fsu) {
                    axios.post(`guilds/${process.env.REACT_APP_MAIN_SERVER_ID}/me`)
                        .then((r: AxiosResponse<GuildUser>) => {
                            const gameMasterRole = r.data.roles.find( any => {
                                console.log(any.id === '454011610445643806');
                                return any.id === '454011610445643806'
                            });
                            setIsGameAdmin(!!gameMasterRole)
                            setFsuGuildMemberInfo(r.data);
                        })
                }
            })
    }, [state.jwt])

    React.useEffect(() => {
        axios.get('/user/info')
            .then((response) => {
                console.log(response.data)
                setUserDetails(response.data)
            })
    }, [state.jwt])

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
                           {JSON.stringify(userDetails, null, 2)}
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