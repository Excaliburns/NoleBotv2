import {useStateMachine} from "little-state-machine";
import {APIGuild, APIPartialGuild} from "discord-api-types";
import React from "react";
import {AxiosResponse} from "axios";
import {useAxios} from "../../util/AxiosProvider";
import {DiscordUser, GuildUser} from "../../entities/JavaGenerated";
import NavBar from "../../shared/components/NavBar";


export default function GuildListing() {
    const [isGameAdmin, setIsGameAdmin] = React.useState<boolean | undefined>(false);
    const [fsuGuild, setFsuGuild] = React.useState<APIPartialGuild>();
    const { state } = useStateMachine();
    const axios = useAxios();

    React.useEffect(() => {
        axios.get('/user/guilds')
            .then( (response) => {
                const fsu = response.data.find( (each: APIGuild) => each.id === process.env.REACT_APP_MAIN_SERVER_ID );
                setFsuGuild(fsu)
                setIsGameAdmin(state?.guildUserDetails?.isGameManager)
            })
    }, [state.jwt])

    return (
        <div>
            {
                isGameAdmin ?
                    <p>
                        Welcome Game Manager!
                    </p>
                    :
                    <p>
                        You're not a GM : (
                    </p>
            }
            <>
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
                            {JSON.stringify(state.guildUserDetails, null, 2)}
                        </pre>
            </>
        </div>
    )
}