import React, { Dispatch, SetStateAction } from "react";
import { useHistory, useLocation } from "react-router-dom";
import { axiosDiscordInstance, axiosNolebotInstance } from "./util/AxiosNolebotInstance";
import { AccessToken } from "./entities/AccessToken";
import { GlobalState, useStateMachine } from "little-state-machine";
import { APIUser } from "discord-api-types";

const updateUserToken = (state: GlobalState, payload: AccessToken) => ({
    ...state,
    userToken: {
        ...state.userToken,
        ...payload
    }
});

const updateUserDetails = (state: GlobalState, payload: APIUser) => ({
    ...state,
    userDetails: {
        ...state.userDetails,
        ...payload
    }
})

function OauthRedirect() {
    const location = useLocation();
    const clientCode = new URLSearchParams(location.search).get("code");

    const { actions, state } = useStateMachine({
        updateUserToken,
        updateUserDetails
    });

    const history = useHistory();

    React.useEffect( () => {
        if (clientCode) {
            axiosNolebotInstance.post('/oauth/discord', {
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

                    const tokenResponse: AccessToken = {
                        access_token: data.access_token,
                        expires_in: data.expires_in,
                        refresh_token: data.refresh_token,
                        scope: data.scope[0].split(' '),
                        token_type: data.token_type
                    }

                    actions.updateUserToken(tokenResponse)
                })
        }

    }, [clientCode])

    React.useEffect(() => {
        if (state?.userToken) {
            axiosDiscordInstance.get('/users/@me', {
                headers: {
                    Authorization: `Bearer ${state.userToken.access_token}`
                }
            })
            .then ( response => {
                actions.updateUserDetails(response.data)
            })

            history.push("/");
        }
    }, [state?.userToken])


    return (
        <div>
        </div>
    )
}

export default OauthRedirect;