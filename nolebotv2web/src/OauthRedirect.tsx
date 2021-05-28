import React, { Dispatch, SetStateAction } from "react";
import { useHistory, useLocation } from "react-router-dom";
import axiosInstance from "./util/AxiosInstance";
import { DiscordUser } from "./entities/DiscordUser";

interface OauthRedirectProps {
    readonly setUserData: Dispatch<SetStateAction<DiscordUser | undefined>>
}

function OauthRedirect({setUserData}: OauthRedirectProps) {
    const location = useLocation();
    const history = useHistory();

    React.useEffect( () => {
        const clientCode = new URLSearchParams(location.search).get("code");
        if (clientCode) {
            axiosInstance.post('/oauth/discord', {
                clientCode: clientCode
            })
                .then( response => {
                    const data: {
                        access_token: string,
                        expires_in: number,
                        refresh_token: string,
                        scope: string[],
                        token_type: string
                    } = response.data ;

                    console.log(data);

                    const user: DiscordUser = {
                        access_token: data.access_token,
                        expires_in: data.expires_in,
                        refresh_token: data.refresh_token,
                        scope: data.scope[0].split(' '),
                        token_type: data.token_type
                    }

                    setUserData(user);
                    history.push("/");
                })
        }

    }, [])


    return (
        <div>
        </div>
    )
}

export default OauthRedirect;